package net.asyncproxy.mlgrush.listener.lobby;

import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.countdown.CountdownHandler;
import net.asyncproxy.mlgrush.modules.game.GameHandler;
import net.asyncproxy.mlgrush.modules.game.GameState;
import net.asyncproxy.mlgrush.modules.item.ItemBuilder;
import net.asyncproxy.mlgrush.modules.team.TeamData;
import net.asyncproxy.mlgrush.modules.team.TeamHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final GameHandler gameHandler;

    private final CountdownHandler countdownHandler;

    private final String prefix;

    private final String color;

    private final MiniMessage mm;

    private final TeamHandler teamHandler;

    public PlayerQuitListener() {
        this.gameHandler = MLGRush.getInstance().getGameHandler();
        this.countdownHandler = MLGRush.getInstance().getCountdownHandler();
        this.prefix = MLGRush.getInstance().getPrefix();
        this.color = MLGRush.getInstance().getColor();
        this.mm = MiniMessage.miniMessage();
        this.teamHandler = MLGRush.getInstance().getTeamHandler();
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.quitMessage(null);
        Player player = event.getPlayer();

        if (this.gameHandler.getGameState() == GameState.LOBBY) {
            Bukkit.broadcast(this.mm.deserialize(this.prefix + "Der Spieler " + this.color + player.getName() + "</gradient> <gray>hat das Spiel verlassen."));
            if (this.countdownHandler.isCountdownRunning()) {
                if (this.teamHandler.getPlayerTeam(player) != null) {
                    this.teamHandler.getPlayerTeam(player).removePlayer(player);
                }
                this.countdownHandler.cancelCountdown();
                Bukkit.getOnlinePlayers().forEach(players -> {
                    players.sendMessage(this.mm.deserialize(this.prefix + "<red>Der Countdown wurde abgebrochen da ein Spieler das Spiel verlassen hat."));
                    players.playSound(players.getLocation(), Sound.BLOCK_ANVIL_BREAK, 0.3F, 1.0F);
                    players.setLevel(0);
                    givePlayerVotingItems(players);
                });
            }
        } else if (this.gameHandler.getGameState() == GameState.RUNNING) {
            TeamData leavedTeam = this.teamHandler.getPlayerTeam(player);
            if (leavedTeam != null) {
                if (leavedTeam.getName().equalsIgnoreCase("red")) {
                    this.gameHandler.endGame(this.teamHandler.getTeam("blue"));
                } else {
                    this.gameHandler.endGame(this.teamHandler.getTeam("red"));
                }
            }
        }
    }

    private void givePlayerVotingItems(Player player) {
        player.getInventory().clear();

        player.getInventory().setItem(0, new ItemBuilder(Material.RED_BED).setName("<dark_gray>» <red>Teamauswahl").build());
        player.getInventory().setItem(4, new ItemBuilder(Material.PAPER).setName("<dark_gray>» <green>Map-Voting").build());
    }
}
