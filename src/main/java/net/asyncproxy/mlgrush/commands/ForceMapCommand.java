package net.asyncproxy.mlgrush.commands;

import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.game.GameHandler;
import net.asyncproxy.mlgrush.modules.game.GameState;
import net.asyncproxy.mlgrush.modules.map.MapHandler;
import net.asyncproxy.mlgrush.modules.vote.VoteHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.Force;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ForceMapCommand implements CommandExecutor, TabCompleter {

    private final String prefix;

    private final String color;

    private final MiniMessage mm;

    private final GameHandler gameHandler;

    private final MapHandler mapHandler;

    private final VoteHandler voteHandler;

    public ForceMapCommand() {
        this.prefix = MLGRush.getInstance().getPrefix();
        this.color = MLGRush.getInstance().getColor();
        this.mm = MiniMessage.miniMessage();
        this.gameHandler = MLGRush.getInstance().getGameHandler();
        this.mapHandler = MLGRush.getInstance().getMapHandler();
        this.voteHandler = MLGRush.getInstance().getVoteHandler();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Du musst ein Spieler sein um diesen Befehl ausführen zu können.");
            return true;
        }

        if (!player.hasPermission("mlgrush.forcemap")) {
            player.sendMessage(this.mm.deserialize(this.prefix + "<red>Du hast keine Rechte auf diesen Befehl."));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(this.mm.deserialize(this.prefix + "Verwendung: " + this.color + "/forcemap <Map-Name></gradient>"));
            return true;
        }

        if (this.gameHandler.getGameState() != GameState.LOBBY) {
            player.sendMessage(this.mm.deserialize(this.prefix + "<red>Das Spiel läuft bereits."));
            return true;
        }

        String mapName = args[0];

        if (!this.mapHandler.getAllMapWithNames().contains(mapName)) {
            player.sendMessage(this.mm.deserialize(this.prefix + "<red>Es existiert keine Map mit dem Namen " + mapName));
            return true;
        }

        this.voteHandler.forceMap(mapName);
        player.sendMessage(this.mm.deserialize(this.prefix + "Es wird jetzt auf der Map " + this.color + mapName + "</gradient> gespielt."));
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return this.mapHandler.getAllMapWithNames();
        } else {
            return null;
        }
    }
}
