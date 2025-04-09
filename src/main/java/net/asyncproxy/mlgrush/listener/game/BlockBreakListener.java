package net.asyncproxy.mlgrush.listener.game;

import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.game.GameHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    private final GameHandler gameHandler;

    public BlockBreakListener() {
        this.gameHandler = MLGRush.getInstance().getGameHandler();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!this.gameHandler.getPlacedBlocks().contains(event.getBlock())) {
            event.setCancelled(true);
        }

        this.gameHandler.getPlacedBlocks().remove(event.getBlock());
    }
}
