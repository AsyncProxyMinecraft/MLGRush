package net.asyncproxy.mlgrush.listener.game;

import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.game.GameHandler;
import net.asyncproxy.mlgrush.modules.game.GameState;
import net.asyncproxy.mlgrush.modules.map.LocationSerializer;
import net.asyncproxy.mlgrush.modules.map.MapData;
import net.asyncproxy.mlgrush.modules.map.MapHandler;
import net.asyncproxy.mlgrush.modules.player.PlayerHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private final MapHandler mapHandler;

    private final GameHandler gameHandler;

    private final PlayerHandler playerHandler;

    private final String prefix;

    private final String color;

    private final MiniMessage mm;

    public PlayerMoveListener() {
        this.mapHandler = MLGRush.getInstance().getMapHandler();
        this.gameHandler = MLGRush.getInstance().getGameHandler();
        this.playerHandler = MLGRush.getInstance().getPlayerHandler();
        this.prefix = MLGRush.getInstance().getPrefix();
        this.color = MLGRush.getInstance().getColor();
        this.mm = MiniMessage.miniMessage();
    }

    private boolean isInGame() {
        return this.gameHandler.getGameState() == GameState.RUNNING;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (isInGame()) {
            Player player = event.getPlayer();
            final MapData map = this.mapHandler.getMap(this.gameHandler.mapPlaying);
            final Location deathHeight = LocationSerializer.locFromString(map.getDeathHeight());

            if (event.getPlayer().getY() <= deathHeight.getY()) {
                if (this.gameHandler.getDamagedMap().containsKey(player)) {
                    Player attacker = this.gameHandler.getDamagedMap().get(player);
                    this.gameHandler.getDamagedMap().clear();
                    player.sendMessage(this.mm.deserialize(this.prefix + "Du wurdest von " + this.color + attacker.getName() + "</gradient> <gray>getötet."));
                    attacker.sendMessage(this.mm.deserialize(this.prefix + "Du hast " + this.color + player.getName() + "</gradient> <gray>getötet."));
                    this.playerHandler.getPlayer(attacker.getUniqueId()).addKills(1);
                    this.playerHandler.getPlayer(player.getUniqueId()).addDeaths(1);
                    this.gameHandler.teleportPlayer(player);
                    return;
                }

                this.playerHandler.getPlayer(player.getUniqueId()).addDeaths(1);
                this.gameHandler.teleportPlayer(player);
                player.sendMessage(this.mm.deserialize(this.prefix + "Du bist gestorben."));
            }
        }
    }
}
