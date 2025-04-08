package net.asyncproxy.mlgrush;

import lombok.Getter;
import lombok.Setter;
import net.asyncproxy.mlgrush.modules.database.IMySQLHandler;
import net.asyncproxy.mlgrush.modules.database.MySQLHandler;
import net.asyncproxy.mlgrush.modules.game.GameHandler;
import net.asyncproxy.mlgrush.modules.team.TeamHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

import java.sql.SQLException;

@Getter
public final class MLGRush extends JavaPlugin {

    @Getter
    private static MLGRush instance;

    private IMySQLHandler mySQLHandler;

    @Setter
    private String prefix;

    @Setter
    private String color;

    private GameHandler gameHandler;

    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        instance = this;

        this.mySQLHandler = new MySQLHandler("", 3306, "", "", "");
        try {
            this.mySQLHandler.connect();
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't establish MySQL-Connection!", e);
        }

        this.gameHandler = new GameHandler();

    }

    @Override
    public void onDisable() {

    }

    private void createTeams() {
        TeamHandler redTeam = new TeamHandler(this.scoreboardManager);
        TeamHandler blueTeam = new TeamHandler(this.scoreboardManager);

        redTeam.createTeam("red", Component.text("Rot"), NamedTextColor.RED, 1);
        blueTeam.createTeam("blue", Component.text("Blau"), NamedTextColor.BLUE, 1);
    }

    private void createSQLTables() {

    }
}
