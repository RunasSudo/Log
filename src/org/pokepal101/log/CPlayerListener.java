package org.pokepal101.log;

import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.*;

/**
 * 
 * @author pokepal101
 * @author cedeel
 *
 */
public class CPlayerListener extends PlayerListener {
    private final LogPlugin plugin;

    public CPlayerListener(LogPlugin instance) {
        plugin = instance;
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        Block b = null;
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getMaterial().equals(plugin.stickMat)
                    && event.getPlayer().hasPermission("log.stick")) {
                b = event.getClickedBlock();
            } else if (event.getMaterial().equals(plugin.boneMat)
                    && event.getPlayer().hasPermission("log.bone")) {
                Block bb = event.getClickedBlock();
                BlockFace bf = event.getBlockFace();
                b = bb.getRelative(bf);
            }
        }

        if (b != null) {
            event.getPlayer().sendMessage("Block changes:");
            Position p = new Position(b.getWorld().getName() ,b.getX(), b.getY(), b.getZ());
            ArrayList<ChangeSet> changeList = plugin.ds.getByPos(p);
            if (changeList.size() == 0)
                event.getPlayer().sendMessage("- No hits.");
            else {
                for (int i = 0; i < changeList.size(); i++) {
                    Modification m = changeList.get(i).getMod();
                    String s = Material.getMaterial(m.getBlockID()).toString()
                            + " ";
                    s += "[" + m.getBlockID() + "] ";
                    if (m.getPlaced())
                        s += ChatColor.GREEN + "placed " + ChatColor.WHITE;
                    else
                        s += ChatColor.RED + "destroyed " + ChatColor.WHITE;
                    s += "by " + m.getWho();
                    s += " at " + m.getDate() + ".";
                    event.getPlayer().sendMessage("- " + s);
                }
            }
            event.setCancelled(true);
        }
    }
}
