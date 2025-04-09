package net.asyncproxy.mlgrush.commands;

import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.countdown.CountdownHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StartCommand implements CommandExecutor {

    private final CountdownHandler countdownHandler;

    private final String prefix;

    private final String color;

    private final MiniMessage mm;

    public StartCommand() {
        this.countdownHandler = MLGRush.getInstance().getCountdownHandler();
        this.prefix = MLGRush.getInstance().getPrefix();
        this.color = MLGRush.getInstance().getColor();
        this.mm = MiniMessage.miniMessage();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Du kannst diesen Befehl nur als Spieler ausführen!");
            return true;
        }

        if (!player.hasPermission("mlgrush.start")) {
            player.sendMessage(this.mm.deserialize(this.prefix + "<red>Du hast keine Rechte auf diesen Befehl."));
            return true;
        }

        if (!this.countdownHandler.isCountdownRunning()) {
            player.sendMessage(this.mm.deserialize(this.prefix + "<red>Der Countdown läuft derzeit nicht."));
            return true;
        }

        if (this.countdownHandler.getCountdown().get() <= 15) {
            player.sendMessage(this.mm.deserialize(this.prefix + "<red>Das Spiel kann nicht mehr verkürzt werden!"));
            return true;
        }

        this.countdownHandler.getCountdown().set(15);
        player.sendMessage(this.mm.deserialize(this.prefix + "Du hast das Spiel verkürzt!"));
        return false;
    }
}
