package net.asyncproxy.mlgrush.listener.game;

import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.game.GameHandler;
import net.asyncproxy.mlgrush.modules.game.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BedBreakListener implements Listener {

    private final GameHandler gameHandler;

    private final GameState gameState;

    public BedBreakListener() {
        this.gameHandler = MLGRush.getInstance().getGameHandler();
        this.gameState = this.gameHandler.getGameState();
    }

    private boolean isInGame() {
        return this.gameState == GameState.RUNNING;
    }

    @EventHandler
    public void onBedBreak(BlockBreakEvent event) {
        if (event.getBlock().getType().name().endsWith("_BED")) event.setCancelled(true);


    }
}
