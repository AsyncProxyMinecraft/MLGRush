package net.asyncproxy.mlgrush.commands;

import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.config.ConfigFileHandler;
import net.asyncproxy.mlgrush.modules.config.SpawnFileHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetSpawnCommand implements CommandExecutor {

    private final String prefix;

    private final String color;

    private final MiniMessage mm;

    private final SpawnFileHandler spawnFileHandler;

    public SetSpawnCommand() {
        this.prefix = MLGRush.getInstance().getPrefix();
        this.color = MLGRush.getInstance().getColor();
        this.mm = MiniMessage.miniMessage();
        this.spawnFileHandler = MLGRush.getInstance().getSpawnFileHandler();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Du kannst diesen Befehl nur als Spieler ausführen");
            return true;
        }

        if (!player.hasPermission("mlgrush.setup")) {
            player.sendMessage(this.mm.deserialize(this.prefix + "<red>Du hast keine Rechte auf diesen Befehl."));
            return true;
        }

        if (args.length != 0) {
            player.sendMessage(this.mm.deserialize(this.prefix + "Verwendung: " + this.color + "/setspawn</gradient>"));
            return true;
        }

        this.spawnFileHandler.setSpawn(player.getLocation(), player);
        return false;
    }
}
