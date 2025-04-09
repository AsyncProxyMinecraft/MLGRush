package net.asyncproxy.mlgrush.listener.game;

import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.game.GameHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    private final GameHandler gameHandler;

    public BlockPlaceListener() {
        this.gameHandler = MLGRush.getInstance().getGameHandler();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        this.gameHandler.getPlacedBlocks().add(event.getBlock());
    }
}
