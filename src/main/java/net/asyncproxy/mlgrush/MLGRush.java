package net.asyncproxy.mlgrush;

import lombok.Getter;
import lombok.Setter;
import net.asyncproxy.mlgrush.commands.*;
import net.asyncproxy.mlgrush.listener.game.BedBreakListener;
import net.asyncproxy.mlgrush.listener.game.BlockBreakListener;
import net.asyncproxy.mlgrush.listener.game.BlockPlaceListener;
import net.asyncproxy.mlgrush.listener.game.PlayerMoveListener;
import net.asyncproxy.mlgrush.listener.lobby.*;
import net.asyncproxy.mlgrush.modules.config.ConfigFileHandler;
import net.asyncproxy.mlgrush.modules.config.DatabaseFileHandler;
import net.asyncproxy.mlgrush.modules.config.SpawnFileHandler;
import net.asyncproxy.mlgrush.modules.countdown.CountdownHandler;
import net.asyncproxy.mlgrush.modules.database.IMySQLHandler;
import net.asyncproxy.mlgrush.modules.database.MySQLHandler;
import net.asyncproxy.mlgrush.modules.game.GameHandler;
import net.asyncproxy.mlgrush.modules.inventory.InventoryHandler;
import net.asyncproxy.mlgrush.modules.map.MapHandler;
import net.asyncproxy.mlgrush.modules.player.PlayerHandler;
import net.asyncproxy.mlgrush.modules.team.TeamHandler;
import net.asyncproxy.mlgrush.modules.vote.VoteHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.File;
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

    private final File databaseFile = new File("plugins/MLGRush", "database.json");

    private DatabaseFileHandler databaseFileHandler;

    private ConfigFileHandler configFileHandler;

    private SpawnFileHandler spawnFileHandler;

    private MapHandler mapHandler;

    private TeamHandler teamHandler;

    private PlayerHandler playerHandler;

    private CountdownHandler countdownHandler;

    private InventoryHandler inventoryHandler;

    private VoteHandler voteHandler;

    @Override
    public void onEnable() {
        instance = this;

        if (!this.databaseFile.exists()) {
            this.getLogger().warning("MySQL database file not found! Creating new one...");
            this.getLogger().warning("Please insert your credentials before running the plugin!");
            this.databaseFileHandler = new DatabaseFileHandler();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.databaseFileHandler = new DatabaseFileHandler();
        this.mySQLHandler = new MySQLHandler(this.databaseFileHandler.getHost(),
                this.databaseFileHandler.getPort(),
                this.databaseFileHandler.getUsername(),
                this.databaseFileHandler.getPassword(),
                this.databaseFileHandler.getDatabase());

        try {
            this.mySQLHandler.connect();
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't establish MySQL-Connection!", e);
        }

        createSQLTables();

        this.configFileHandler = new ConfigFileHandler();

        this.spawnFileHandler = new SpawnFileHandler();

        this.mapHandler = new MapHandler();

        this.teamHandler = new TeamHandler(Bukkit.getScoreboardManager());
        createTeams();

        this.playerHandler = new PlayerHandler();

        this.gameHandler = new GameHandler();

        this.voteHandler = new VoteHandler();

        this.countdownHandler = new CountdownHandler();

        this.inventoryHandler = new InventoryHandler();

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldCancelListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);

        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new BedBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), this);

        this.getCommand("stats").setExecutor(new StatsCommand());
        this.getCommand("forcemap").setExecutor(new ForceMapCommand());
        this.getCommand("setspawn").setExecutor(new SetSpawnCommand());
        this.getCommand("map").setExecutor(new MapCommand());
        this.getCommand("start").setExecutor(new StartCommand());

        this.mapHandler.getLoadedMaps().forEach((mapName, mapData) -> {
            String map = mapData.getRedSpawn().split(";")[0];
            setupWorldRules(map);
        });

        this.getLogger().info("MLGRush started successfully.");
        this.getLogger().info("Running on Version > v" + this.getDescription().getVersion());
        this.getLogger().info("Author > AsyncProxy");
        this.getLogger().info("Loaded " + this.mapHandler.getAllMapWithNames().size() + " maps.");
        this.getLogger().info("Created " + this.teamHandler.getAllTeams().size() + " teams.");
    }

    @Override
    public void onDisable() {
        if (this.mySQLHandler != null) {
            try {
                this.mySQLHandler.disconnect();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if (this.teamHandler != null) {
            this.teamHandler.deleteAllTeams();
        }
    }

    private void createTeams() {
        this.teamHandler.createTeam("red", Component.text("Red"), NamedTextColor.RED, 1);
        this.teamHandler.createTeam("blue", Component.text("Blue"), NamedTextColor.BLUE, 1);
    }

    private void createSQLTables() {
        String createPlayerTable = "CREATE TABLE IF NOT EXISTS mlgrush_players(" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "kills INT NOT NULL, " +
                "deaths INT NOT NULL, " +
                "wins INT NOT NULL, " +
                "losses INT NOT NULL)";

        this.mySQLHandler.updateAsync(createPlayerTable);

        String createMapTable = "CREATE TABLE IF NOT EXISTS mlgrush_maps(" +
                "mapName TEXT NOT NULL," +
                "deathHeight TEXT, " +
                "redSpawn TEXT, " +
                "blueSpawn TEXT, " +
                "redBedTop TEXT, " +
                "redBedBottom TEXT, " +
                "blueBedTop TEXT, " +
                "blueBedBottom TEXT)";

        this.mySQLHandler.updateAsync(createMapTable);
    }

    private void setupWorldRules(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

        this.getLogger().info("Setting up world rules for " + worldName);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_TILE_DROPS, false);
        world.setGameRule(GameRule.DO_MOB_LOOT, false);
        world.setStorm(false);
        world.setFullTime(1200);
        world.setThundering(false);
        world.setClearWeatherDuration(-1);
        world.save();

    }
}
