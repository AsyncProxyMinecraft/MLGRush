package net.asyncproxy.mlgrush.modules.countdown;

import lombok.Setter;
import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.game.GameHandler;
import net.asyncproxy.mlgrush.modules.vote.VoteHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicInteger;

public class CountdownHandler {

    private BukkitTask countdownTask;

    private final GameHandler gameHandler;

    private AtomicInteger countdown;

    private final String prefix;

    private final String color;

    private final MiniMessage mm;

    private final VoteHandler voteHandler;

    @Setter
    private String votedMap;

    public CountdownHandler() {
        this.countdownTask = null;
        this.gameHandler = MLGRush.getInstance().getGameHandler();
        this.countdown = new AtomicInteger(30);
        this.prefix = MLGRush.getInstance().getPrefix();
        this.color = MLGRush.getInstance().getColor();
        this.mm = MiniMessage.miniMessage();
        this.voteHandler = MLGRush.getInstance().getVoteHandler();
        this.votedMap = null;
    }

    public boolean isCountdownRunning() {
        return this.countdownTask != null;
    }

    public void startCountdown() {
        if (this.countdownTask != null) {
            this.countdownTask.cancel();
            this.countdownTask = null;
        }

        this.countdownTask = Bukkit.getScheduler().runTaskTimer(MLGRush.getInstance(), () -> {
            Bukkit.getOnlinePlayers().forEach(players -> {
                players.setLevel(this.countdown.get());
            });
            int countdown = this.countdown.getAndDecrement();

            if (countdown == 0) {
                Bukkit.broadcast(this.mm.deserialize(this.prefix + "Das Spiel startet jetzt!"));
                Bukkit.getOnlinePlayers().forEach(player -> {
                   player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.3F, 1.0F);
                });
                this.gameHandler.startGame(this.votedMap);
                this.countdownTask.cancel();
                this.setVotedMap(null);
                return;
            }

            if (countdown == 1) {
                Bukkit.broadcast(this.mm.deserialize(this.prefix + "Das Spiel startet in " + this.color + "einer <gray>Sekunde."));
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.3F, 1.0F);
                });
            }

            if (countdown == 2 || countdown == 3 || countdown == 4 || countdown == 5 || countdown == 10 || countdown == 20 || countdown == 25) {
                if (countdown == 25) {
                    Bukkit.broadcast(this.mm.deserialize(this.prefix + "Die Voting-Phase endet in " + this.color + "10 <gray>Sekunden"));
                }
                if (countdown == 20) {
                    Bukkit.broadcast(this.mm.deserialize(this.prefix + "Die Voting-Phase endet in " + this.color + "5 <gray>Sekunden"));
                }
                Bukkit.broadcast(this.mm.deserialize(this.prefix + "Das Spiel startet in " + this.color + countdown + " <gray>Sekunden."));
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.3F, 1.0F);
                });
            }

            if (countdown == 15) {
                Bukkit.broadcast(this.mm.deserialize(this.prefix + "Die Voting-Phase wurde beendet!"));
                this.setVotedMap(this.voteHandler.getVotedMap());
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.getInventory().clear();
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.3F, 1.0F);
                    player.showTitle(Title.title(this.mm.deserialize("<dark_gray>» <gray>Es wird auf der Map gespielt "), this.mm.deserialize("<dark_gray>» " + this.color + this.votedMap + "</gradient>")));
                });
            }
        }, 20L, 20L);
    }

    public void cancelCountdown() {
        if (this.countdownTask != null) {
            this.countdownTask.cancel();
            this.countdownTask = null;
            this.countdown = new AtomicInteger(30);
            this.voteHandler.clearAllVotes();
            this.setVotedMap(null);
        }
    }
}
