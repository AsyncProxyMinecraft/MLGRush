package net.asyncproxy.mlgrush.listener.lobby;

import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.config.SpawnFileHandler;
import net.asyncproxy.mlgrush.modules.countdown.CountdownHandler;
import net.asyncproxy.mlgrush.modules.game.GameHandler;
import net.asyncproxy.mlgrush.modules.game.GameState;
import net.asyncproxy.mlgrush.modules.item.ItemBuilder;
import net.asyncproxy.mlgrush.modules.player.PlayerHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.util.Locale;

public class PlayerJoinListener implements Listener {

    private final GameHandler gameHandler;

    private final MiniMessage mm;

    private final String prefix;

    private final String color;

    private final PlayerHandler playerHandler;

    private final CountdownHandler countdownHandler;

    private final SpawnFileHandler spawnFileHandler;

    public PlayerJoinListener() {
        this.gameHandler = MLGRush.getInstance().getGameHandler();
        this.mm = MiniMessage.miniMessage();
        this.prefix = MLGRush.getInstance().getPrefix();
        this.color = MLGRush.getInstance().getColor();
        this.playerHandler = MLGRush.getInstance().getPlayerHandler();
        this.countdownHandler = MLGRush.getInstance().getCountdownHandler();
        this.spawnFileHandler = MLGRush.getInstance().getSpawnFileHandler();
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(null);
        Player player = event.getPlayer();
        this.playerHandler.doesPlayerExist(player.getUniqueId()).thenAccept(exists -> {
            if (!exists) {
                this.playerHandler.createNewPlayer(player.getUniqueId());
            } else {
                this.playerHandler.loadPlayer(player);
            }
        });

        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);

        if (this.gameHandler.getGameState() == GameState.LOBBY) {
            Location location = this.spawnFileHandler.getSpawn();
            if (location == null) {
                player.sendMessage(this.mm.deserialize(this.prefix + "<red>Der Spawn wurde noch nicht gesetzt!"));
            } else {
                player.teleport(location);
            }

            Bukkit.broadcast(this.mm.deserialize(this.prefix + "Der Spieler " + this.color + player.getName() + "</gradient> <gray>hat das Spiel betreten."));
            givePlayerVotingItems(player);

            if (Bukkit.getOnlinePlayers().size() < 2) {
                Bukkit.broadcast(this.mm.deserialize(this.prefix + "Es wird noch ein weiterer Spieler benötigt damit das Spiel starten kann."));
            }

            if (Bukkit.getOnlinePlayers().size() == 2) {
                Bukkit.broadcast(this.mm.deserialize(this.prefix + "Der Countdown startet jetzt."));
                this.countdownHandler.startCountdown();
            }
        } else {
            
        }
    }

    private void givePlayerVotingItems(Player player) {
        player.getInventory().clear();

        player.getInventory().setItem(0, new ItemBuilder(Material.RED_BED).setName("<dark_gray>» <red>Teamauswahl").build());
        player.getInventory().setItem(4, new ItemBuilder(Material.PAPER).setName("<dark_gray>» <green>Map-Voting").build());
    }
}
