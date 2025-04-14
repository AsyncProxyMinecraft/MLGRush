package net.asyncproxy.mlgrush.listener.game;

import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.game.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropListener implements Listener {

    private boolean isInGame() {
        return MLGRush.getInstance().getGameHandler().getGameState() == GameState.RUNNING;
    }

    @EventHandler
    public void onPlayerItemDrop(PlayerDropItemEvent event) {
        if (isInGame()) {
            event.setCancelled(true);
        }
    }
}
