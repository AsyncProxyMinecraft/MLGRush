package net.asyncproxy.mlgrush.listener.game;

import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.game.GameHandler;
import net.asyncproxy.mlgrush.modules.map.LocationSerializer;
import net.asyncproxy.mlgrush.modules.map.MapData;
import net.asyncproxy.mlgrush.modules.map.MapHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private final MapHandler mapHandler;

    private final GameHandler gameHandler;

    public PlayerMoveListener() {
        this.mapHandler = MLGRush.getInstance().getMapHandler();
        this.gameHandler = MLGRush.getInstance().getGameHandler();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        final MapData map = this.mapHandler.getMap(this.gameHandler.mapPlaying);
        final Location deathHeight = LocationSerializer.locFromString(map.getDeathHeight());

        if (event.getPlayer().getY() <= deathHeight.getY()) {
            this.gameHandler.teleportPlayer(player);
        }
    }
}
