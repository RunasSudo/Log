package org.pokepal101.log;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.material.*;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import org.pokepal101.log.data.DataStore;
import org.pokepal101.log.data.FileDataStore;

//import com.nijiko.permissions.PermissionHandler;
//import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * Log plugin for Bukkit
 *
 * @author pokepal101
 * @author cedeel
 */
public class LogPlugin extends JavaPlugin {
    private final CPlayerListener playerListener = new CPlayerListener(this);
    private final CBlockListener blockListener = new CBlockListener(this);
    private static File LOGFILE;
    private static File CONFIG_FILE;
    protected DataStore ds;
    protected Material stickMat = Material.STICK;
    protected Material boneMat = Material.BONE;
    private Properties props = new Properties();
    public final Logger logger = Logger.getLogger("Minecraft");

    // public static PermissionHandler Permissions;

    public void onDisable() {
        PluginDescriptionFile pdf = this.getDescription();
        logger.info(pdf.getName() + " shutting down.");

        try {
            ds.persist();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info(pdf.getName() + " " + pdf.getVersion()
                + " successfully unloaded.");
    }

    public void onEnable() {
        // initialize configuration folder and paths.
        if (!getDataFolder().exists()) {
            try {
                getDataFolder().mkdirs();
                logger.info("Creating config dir: "
                        + getDataFolder().getAbsolutePath());
            } catch (Exception e) {
                logger.warning("Config dir creation failed: ");
                e.printStackTrace();
            }
        }
        LOGFILE = new File(getDataFolder(), "logdata.db2");
        CONFIG_FILE = new File(getDataFolder(), "config.properties");
        ds = new FileDataStore(LOGFILE);

        PluginDescriptionFile pdf = this.getDescription();
        logger.info(pdf.getName() + " " + pdf.getVersion()
                + " successfully loaded.");
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener,
                Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener,
                Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener,
                Priority.Normal, this);
        logger.info(pdf.getName() + ": events initialized.");

        try {
            if (!LOGFILE.exists()) {
                LOGFILE.createNewFile();
            }
            logger.info(pdf.getName() + " data loaded successfully.");

            if (!CONFIG_FILE.exists()) {
                if (!getDataFolder().exists()) {
                    getDataFolder().mkdirs();
                }
                CONFIG_FILE.createNewFile();
                props.put("stickItem", "280");
                props.put("boneItem", "352");
                FileOutputStream fos = new FileOutputStream(
                        CONFIG_FILE.getPath());
                props.store(fos, "");
                fos.close();
                logger.info(pdf.getName() + ": Created new properties file.");
            } else {
                FileInputStream is = new FileInputStream(CONFIG_FILE.getPath());
                props.load(is);
                is.close();
                logger.info(pdf.getName() + ": Loaded properties file.");
            }
            stickMat = new MaterialData(Integer.parseInt(props.getProperty(
                    "stickItem", "280"))).getItemType();
            boneMat = new MaterialData(Integer.parseInt(props.getProperty(
                    "boneItem", "352"))).getItemType();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setupPermissions();
    }

    private void setupPermissions() {
    }

    public boolean onCommand(CommandSender sender, Command command,
            String commandLabel, String[] args) {
        if (command.getName().equalsIgnoreCase("log")) {
            if (args.length == 0) {
                sendHelp(sender);

            } else if (args[0].equalsIgnoreCase("purge")
                    && hasPerms(sender, "log.admin.purge")) {
                if (ds.purge())
                    sender.sendMessage("purge successful.");
                else
                    sender.sendMessage("purge failed.");
            }

            else if (args[0].equalsIgnoreCase("clean")
                    && hasPerms(sender, "log.admin.clean")) {
                if (args.length == 2)
                {
                    cleanLog(sender, args);
                    sender.sendMessage(ChatColor.GOLD + "Log cleaned");
                }
            }

            else if (args[0].equalsIgnoreCase("rollback")
                    && hasPerms(sender, "log.admin.rollback")) {
                if (args.length == 1)
                    sender.sendMessage(ChatColor.YELLOW
                            + "Usage: rollback <username>");
                else
                    rollback(args);
            }

            return true;
        }
        return false;
    }

    private void rollback(String[] args) {
        String username = args[1];

        ArrayList<ChangeSet> ch = ds.getByName(username);

        for (int iter = ch.size(); iter > 0; iter--) {
            ChangeSet cs = ch.get(iter - 1);

            Position p = cs.getPos();

            Block b = getServer().getWorld(p.getWorld())
                    .getBlockAt(p.getX(),p.getY(), p.getZ());
            Material blocktype = Material.AIR;
            if (!cs.getMod().getPlaced())
                blocktype = Material.getMaterial(cs.getMod().getBlockID());
            logger.info("Undoing change from " + cs.getMod().getDate());
            b.setType(blocktype);
        }
        ds.clearPlayer(username);

    }

    private void cleanLog(CommandSender sender, String[] args) {
        try {
            long seconds = Long.parseLong(args[1].substring(0,
                    args[1].length() - 1));

            switch (args[1].charAt(args[1].length() - 1)) {
            case 'd':
                seconds = seconds * 24;
            case 'h':
                seconds = seconds * 60;
            case 'm':
                seconds = seconds * 60;
                break;
            default:
                sender.sendMessage("Invalid unit of time.");
                break;
            }
            ds.clear(seconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendHelp(CommandSender sender) {
        ChatColor color = ChatColor.GREEN;
        sender.sendMessage(color + "Log Help:");
        sender.sendMessage(color + "/log purge : Erases the log database");
        sender.sendMessage(color
                + "/log clean <time> : Erases entries older than <time>");
        sender.sendMessage(color + "     <time> should end in 'm', 'h', or 'd'");
        sender.sendMessage(color
                + "     <time> cannot contain a combination of time units (ie: '6h 13m' is not valid)");
        sender.sendMessage(color
                + "/log rollback <player> : Rolls back the changes a player has made");
    }

    private boolean hasPerms(CommandSender sender, String perm) {
        return ((sender instanceof Player && sender.hasPermission(perm)) || sender instanceof ConsoleCommandSender);
    }
}
