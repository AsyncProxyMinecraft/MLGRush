package net.asyncproxy.mlgrush.listener.lobby;

import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.game.GameHandler;
import net.asyncproxy.mlgrush.modules.game.GameState;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final GameHandler gameHandler;

    private final MiniMessage mm;

    private final String prefix;

    private final String color;

    public PlayerJoinListener() {
        this.gameHandler = MLGRush.getInstance().getGameHandler();
        this.mm = MiniMessage.miniMessage();
        this.prefix = MLGRush.getInstance().getPrefix();
        this.color = MLGRush.getInstance().getColor();
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(null);
        Player player = event.getPlayer();

        if (this.gameHandler.getGameState() == GameState.LOBBY) {
            Bukkit.broadcast(this.mm.deserialize(this.prefix + "Der Spieler " + this.color + player.getName() + " <gray>hat das Spiel betreten."));
        } else {
            
        }
    }
}
