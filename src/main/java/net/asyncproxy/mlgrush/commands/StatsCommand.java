package net.asyncproxy.mlgrush.commands;

import com.google.common.math.Stats;
import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.player.PlayerData;
import net.asyncproxy.mlgrush.modules.player.PlayerHandler;
import net.asyncproxy.mlgrush.modules.player.UUIDFetcher;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class StatsCommand implements CommandExecutor {

    private final String prefix;

    private final String color;

    private final MiniMessage mm;

    private final PlayerHandler playerHandler;

    public StatsCommand() {
        this.prefix = MLGRush.getInstance().getPrefix();
        this.color = MLGRush.getInstance().getColor();
        this.mm = MiniMessage.miniMessage();
        this.playerHandler = MLGRush.getInstance().getPlayerHandler();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Es können nur Spieler diesen Befehl ausführen.");
            return true;
        }

        if (args.length == 0) {
            PlayerData playerData = this.playerHandler.getPlayer(player.getUniqueId());
            player.sendMessage(this.mm.deserialize(this.prefix + " "));
            player.sendMessage(this.mm.deserialize(this.prefix + "Platzierung <dark_gray>»</dark_gray> " + this.color + "#COMING soon"));
            player.sendMessage(this.mm.deserialize(this.prefix + "Kills <dark_gray>»</dark_gray> " + this.color + playerData.getKills()));
            player.sendMessage(this.mm.deserialize(this.prefix + "Tode <dark_gray>»</dark_gray> " + this.color + playerData.getDeaths()));
            player.sendMessage(this.mm.deserialize(this.prefix + "Gewonnen <dark_gray>»</dark_gray> " + this.color + playerData.getWins()));
            player.sendMessage(this.mm.deserialize(this.prefix + "Verloren <dark_gray>»</dark_gray> " + this.color + playerData.getLosses()));
            player.sendMessage(this.mm.deserialize(this.prefix + "Spiele <dark_gray>»</dark_gray> " + this.color + (playerData.getWins() + playerData.getLosses())));
            player.sendMessage(this.mm.deserialize(this.prefix + " "));
        } else {
            if (args.length != 1) {
                player.sendMessage(this.mm.deserialize(this.prefix + "Verwendung: " + this.color + "/stats <Spieler>"));
                return true;
            }

            String target = args[0];

            UUID uuid;
            try {
                uuid = UUIDFetcher.getUUID(target);
            } catch (Exception exception) {
                player.sendMessage(this.mm.deserialize(this.prefix + "<red>Es existiert kein Spieler mit dem Namen " + target));
                return true;
            }

            boolean exists = this.playerHandler.doesPlayerExist(uuid).join();
            if (!exists) {
                player.sendMessage(this.mm.deserialize(this.prefix + "<red>Der Spieler " + target + " hat bisher keine Runde gespielt."));
                return true;
            }

            PlayerData playerData = this.playerHandler.getPlayer(uuid);
            player.sendMessage(this.mm.deserialize(this.prefix + "Stats von dem Spieler <dark_gray>»</dark_gray> " + this.color + target));
            player.sendMessage(this.mm.deserialize(this.prefix + " "));
            player.sendMessage(this.mm.deserialize(this.prefix + "Platzierung <dark_gray>»</dark_gray> " + this.color + "#COMING soon"));
            player.sendMessage(this.mm.deserialize(this.prefix + "Kills <dark_gray>»</dark_gray> " + this.color + playerData.getKills()));
            player.sendMessage(this.mm.deserialize(this.prefix + "Tode <dark_gray>»</dark_gray> " + this.color + playerData.getDeaths()));
            player.sendMessage(this.mm.deserialize(this.prefix + "Gewonnen <dark_gray>»</dark_gray> " + this.color + playerData.getWins()));
            player.sendMessage(this.mm.deserialize(this.prefix + "Verloren <dark_gray>»</dark_gray> " + this.color + playerData.getLosses()));
            player.sendMessage(this.mm.deserialize(this.prefix + "Spiele <dark_gray>»</dark_gray> " + this.color + (playerData.getWins() + playerData.getLosses())));
            player.sendMessage(this.mm.deserialize(this.prefix + " "));
        }
        return false;
    }
}
