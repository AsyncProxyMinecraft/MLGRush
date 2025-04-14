package net.asyncproxy.mlgrush.listener.game;

import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.game.GameHandler;
import net.asyncproxy.mlgrush.modules.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityListener implements Listener {

    private final GameHandler gameHandler;

    public EntityDamageByEntityListener() {
        this.gameHandler = MLGRush.getInstance().getGameHandler();
    }

    public boolean isInGame() {
        return this.gameHandler.getGameState() == GameState.RUNNING;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (isInGame()) {
            if (event.getDamager() instanceof Player damagerPlayer && event.getEntity() instanceof Player attackerPlayer) {
                this.gameHandler.getDamagedMap().put(damagerPlayer, attackerPlayer);
            }
        }
    }
}
