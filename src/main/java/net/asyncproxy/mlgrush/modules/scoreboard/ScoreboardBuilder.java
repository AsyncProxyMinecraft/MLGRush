package net.asyncproxy.mlgrush.modules.scoreboard;

import org.bukkit.entity.Player;

public class ScoreboardBuilder {

    public void buildLobbyScoreboard(Player player) {
        ScoreboardHandler scoreboard = new ScoreboardHandler(player, "");

        scoreboard.setBlankLine(0)
                .setLine(0, "")
                .setLine(1, "")
                .setLine(2, "")
                .setLine(3, "")
                .setLine(4, "")
                .setLine(5, "")
                .setLine(6, "")

        scoreboard.show();
    }
}
