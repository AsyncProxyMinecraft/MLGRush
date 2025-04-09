package net.asyncproxy.mlgrush.listener.game;

import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.game.GameHandler;
import net.asyncproxy.mlgrush.modules.game.GameState;
import net.asyncproxy.mlgrush.modules.team.TeamHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BedBreakListener implements Listener {

    private final GameHandler gameHandler;

    private final GameState gameState;

    private final TeamHandler teamHandler;

    private final String prefix;

    private final MiniMessage mm;

    public BedBreakListener() {
        this.gameHandler = MLGRush.getInstance().getGameHandler();
        this.gameState = this.gameHandler.getGameState();
        this.teamHandler = MLGRush.getInstance().getTeamHandler();
        this.prefix = MLGRush.getInstance().getPrefix();
        this.mm = MiniMessage.miniMessage();
    }

    private boolean isInGame() {
        return this.gameState == GameState.RUNNING;
    }

    @EventHandler
    public void onBedBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.getBlock().getType().name().endsWith("_BED")) event.setCancelled(true);

        if (this.teamHandler.isPlayerBed(player, event.getBlock(), this.gameHandler.mapPlaying)) {
            player.sendMessage(this.mm.deserialize(this.prefix + "<red>Du kannst dein eigenes Bed nicht zerstören!"));
        } else {
            this.gameHandler.destroyBed(player);
        }
    }
}
