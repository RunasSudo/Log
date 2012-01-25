package org.pokepal101.log;

import java.util.*;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
import org.bukkit.event.block.*;

import be.darnell.mc.log.ChangeSet;
import be.darnell.mc.log.LogPlugin;
import be.darnell.mc.log.Modification;
import be.darnell.mc.log.Position;

/**
 * Sample block listener
 * @author pokepal101
 * @author cedeel
 */
public class CBlockListener extends BlockListener {
    private final LogPlugin plugin;

    public CBlockListener(final LogPlugin plugin) {
        this.plugin = plugin;
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        Block b = event.getBlockPlaced();
        Position p = new Position(b.getWorld().getName() ,b.getX(), b.getY(), b.getZ());

        Modification m = new Modification(event.getPlayer().getName(), true,
                b.getTypeId(), formatDate());
        plugin.ds.writeSingle(new ChangeSet(m, p));
    }

    public void onBlockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        Position p = new Position(b.getWorld().getName() ,b.getX(), b.getY(), b.getZ());

        Modification m = new Modification(event.getPlayer().getName(), false,
                b.getTypeId(), formatDate());
        plugin.ds.writeSingle(new ChangeSet(m, p));
    }

    private String formatDate() {
        // format: 31/12/2008 14:30:33
        return String.format("%1$td/%1$tm/%1$tY %1$tT", Calendar.getInstance());
    }
}
