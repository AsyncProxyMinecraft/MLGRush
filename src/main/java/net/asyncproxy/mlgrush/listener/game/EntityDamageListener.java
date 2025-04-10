package net.asyncproxy.mlgrush.listener.game;

import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.game.GameHandler;
import net.asyncproxy.mlgrush.modules.game.GameState;
import org.bukkit.damage.DamageSource;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    private final GameHandler gameHandler;

    public EntityDamageListener() {
        this.gameHandler = MLGRush.getInstance().getGameHandler();
    }

    private boolean isInGame() {
        return this.gameHandler.getGameState() == GameState.RUNNING;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (isInGame()) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) event.setCancelled(true);

            event.setDamage(0);
        }
    }
}
