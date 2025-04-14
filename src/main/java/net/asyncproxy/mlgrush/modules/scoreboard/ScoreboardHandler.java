package net.asyncproxy.mlgrush.modules.scoreboard;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ScoreboardHandler {
    private static final Map<UUID, ScoreboardHandler> playerBoards = new HashMap<>();
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    private final Player player;
    private final Scoreboard scoreboard;
    private final Objective objective;
    private final Map<Integer, TeamLine> lines = new HashMap<>();
    private final AtomicInteger teamCounter = new AtomicInteger(0);

    public ScoreboardHandler(Player player, String title) {
        this.player = player;

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        this.scoreboard = manager.getNewScoreboard();

        Component titleComponent = miniMessage.deserialize(title);
        this.objective = scoreboard.registerNewObjective("sidebar", Criteria.DUMMY, titleComponent);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        playerBoards.put(player.getUniqueId(), this);
    }

    public ScoreboardHandler setLine(int line, String text) {
        if (line < 1 || line > 15) {
            throw new IllegalArgumentException("Zeile muss zwischen 1 und 15 liegen");
        }

        int score = 16 - line;

        TeamLine oldTeamLine = lines.get(line);
        if (oldTeamLine != null) {
            scoreboard.resetScores(oldTeamLine.getEntry());
            Team oldTeam = scoreboard.getTeam(oldTeamLine.getTeamName());
            if (oldTeam != null) {
                oldTeam.unregister();
            }
        }

        TeamLine teamLine = createTeamLine(text, score);
        lines.put(line, teamLine);

        return this;
    }

    private TeamLine createTeamLine(String text, int score) {
        String teamName = "sb_" + teamCounter.getAndIncrement();

        String entry = getUniqueString();

        Team team = scoreboard.registerNewTeam(teamName);

        Component component = miniMessage.deserialize(text);

        team.prefix(component);

        team.addEntry(entry);

        objective.getScore(entry).setScore(score);

        return new TeamLine(teamName, entry);
    }

    public ScoreboardHandler updateLine(int line, String text) {
        if (line < 1 || line > 15) {
            throw new IllegalArgumentException("Zeile muss zwischen 1 und 15 liegen");
        }

        TeamLine teamLine = lines.get(line);
        if (teamLine != null) {
            Team team = scoreboard.getTeam(teamLine.getTeamName());
            if (team != null) {
                Component component = miniMessage.deserialize(text);
                team.prefix(component);
                return this;
            }
        }

        return setLine(line, text);
    }

    public ScoreboardHandler removeLine(int line) {
        if (line < 1 || line > 15) {
            throw new IllegalArgumentException("Zeile muss zwischen 1 und 15 liegen");
        }

        TeamLine teamLine = lines.remove(line);
        if (teamLine != null) {
            scoreboard.resetScores(teamLine.getEntry());
            Team team = scoreboard.getTeam(teamLine.getTeamName());
            if (team != null) {
                team.unregister();
            }
        }

        return this;
    }

    public ScoreboardHandler setBlankLine(int line) {
        return setLine(line, "<dark_gray>·</dark_gray>");
    }

    public void show() {
        player.setScoreboard(scoreboard);
    }

    public void remove() {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        playerBoards.remove(player.getUniqueId());

        for (TeamLine teamLine : lines.values()) {
            Team team = scoreboard.getTeam(teamLine.getTeamName());
            if (team != null) {
                team.unregister();
            }
        }
        lines.clear();
    }

    public ScoreboardHandler clearLines() {
        for (TeamLine teamLine : lines.values()) {
            scoreboard.resetScores(teamLine.getEntry());
            Team team = scoreboard.getTeam(teamLine.getTeamName());
            if (team != null) {
                team.unregister();
            }
        }
        lines.clear();
        return this;
    }

    public ScoreboardHandler updateTitle(String title) {
        Component titleComponent = miniMessage.deserialize(title);
        objective.displayName(titleComponent);
        return this;
    }

    private String getUniqueString() {
        return "§" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static ScoreboardHandler getPlayerScoreboard(Player player) {
        return playerBoards.get(player.getUniqueId());
    }

    public static boolean hasScoreboard(Player player) {
        return playerBoards.containsKey(player.getUniqueId());
    }
}
