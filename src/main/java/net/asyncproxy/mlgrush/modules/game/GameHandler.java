package net.asyncproxy.mlgrush.modules.game;

import lombok.Getter;
import lombok.Setter;
import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.config.SpawnFileHandler;
import net.asyncproxy.mlgrush.modules.countdown.CountdownHandler;
import net.asyncproxy.mlgrush.modules.item.ItemBuilder;
import net.asyncproxy.mlgrush.modules.map.LocationSerializer;
import net.asyncproxy.mlgrush.modules.map.MapData;
import net.asyncproxy.mlgrush.modules.map.MapHandler;
import net.asyncproxy.mlgrush.modules.player.PlayerHandler;
import net.asyncproxy.mlgrush.modules.team.TeamData;
import net.asyncproxy.mlgrush.modules.team.TeamHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GameHandler {

    @Getter
    private GameState gameState;

    private final TeamHandler teamHandler;

    private final MapHandler mapHandler;

    private final MiniMessage mm;

    @Setter
    public String mapPlaying;

    @Getter
    private final List<Block> placedBlocks;

    private BukkitTask task;

    private final SpawnFileHandler spawnFileHandler;

    private final PlayerHandler playerHandler;

    @Getter
    private final ConcurrentHashMap<Player, Player> damagedMap;

    private BukkitTask countdownTask;

    private final String prefix;

    private final String color;

    public GameHandler() {
        this.gameState = GameState.LOBBY;
        this.teamHandler = MLGRush.getInstance().getTeamHandler();
        this.mapHandler = MLGRush.getInstance().getMapHandler();
        this.mm = MiniMessage.miniMessage();
        this.mapPlaying = null;
        this.placedBlocks = new ArrayList<>();
        this.task = null;
        this.spawnFileHandler = MLGRush.getInstance().getSpawnFileHandler();
        this.playerHandler = MLGRush.getInstance().getPlayerHandler();
        this.damagedMap = new ConcurrentHashMap<>();
        this.countdownTask = null;
        this.prefix = MLGRush.getInstance().getPrefix();
        this.color = MLGRush.getInstance().getColor();
    }

    public void startGame(String mapName) {
        this.gameState = GameState.RUNNING;
        this.setMapPlaying(mapName);

        this.teamHandler.getAllTeams().forEach(team -> {
            teleportPlayersToMap(team.getPlayers());
            givePlayerItems(team.getPlayers());
        });

        sendActionBar();
    }

    public void endGame(TeamData winningTeam) {
        this.gameState = GameState.FINISHED;
        this.cancelActionBar();
        Location spawnLocation = this.spawnFileHandler.getSpawn();
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.getInventory().clear();
            player.teleport(spawnLocation);
        });

        if (winningTeam.getName().equalsIgnoreCase("red")) {
            TeamData blue = this.teamHandler.getTeam("blue");

            Player redPlayer = winningTeam.getPlayers().getFirst();
            Player bluePlayer = blue.getPlayers().getFirst();

            this.playerHandler.getPlayer(redPlayer.getUniqueId()).addWins(1);
            this.playerHandler.getPlayer(bluePlayer.getUniqueId()).addLosses(1);

            Bukkit.getOnlinePlayers().forEach(players -> {
                players.showTitle(Title.title(this.mm.deserialize("<dark_gray><b>»</b <gray>Team <red>Rot <dark_gray><b>«</b>"), this.mm.deserialize("<dark_gray><b>»</b> <gray>hat das Spiel gewonnen. <dark_gray><b>«</b>")));
            });

            this.playerHandler.savePlayer(redPlayer);
            this.playerHandler.savePlayer(bluePlayer);
        } else {
            TeamData red = this.teamHandler.getTeam("red");

            Player bluePlayer = winningTeam.getPlayers().getFirst();
            Player redPlayer = red.getPlayers().getFirst();

            this.playerHandler.getPlayer(redPlayer.getUniqueId()).addWins(1);
            this.playerHandler.getPlayer(bluePlayer.getUniqueId()).addLosses(1);

            Bukkit.getOnlinePlayers().forEach(players -> {
                players.showTitle(Title.title(this.mm.deserialize("<dark_gray><b>»</b> <gray>Team <blue>Blau <dark_gray><b>«</b>"), this.mm.deserialize("<dark_gray><b>»</b> <gray>hat das Spiel gewonnen. <dark_gray><b>«</b>")));
            });

            this.playerHandler.savePlayer(redPlayer);
            this.playerHandler.savePlayer(bluePlayer);
        }

        this.startEndCountdown();
    }

    public void destroyBed(Player player) {
        TeamData teamData = this.teamHandler.getPlayerTeam(player);
        if (teamData == null) return;

        teamData.addScore(1);
        sendActionBar();

        resetAllBlocks();
        this.getDamagedMap().clear();
        if (teamData.getScore() == 10) {
            endGame(teamData);
            return;
        }

        this.teamHandler.getAllTeams().forEach(team -> {
            this.teleportPlayersToMap(team.getPlayers());
            this.givePlayerItems(team.getPlayers());
        });

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.3F, 1.0F);
    }

    public void teleportPlayersToMap(List<Player> players) {
        MapData mapData = this.mapHandler.getMap(this.mapPlaying);
        players.forEach(player -> {
            TeamData teamData = this.teamHandler.getPlayerTeam(player);
            if (teamData.getName().equalsIgnoreCase("red")) {
                player.teleport(LocationSerializer.locFromString(mapData.getRedSpawn()));
            } else {
                player.teleport(LocationSerializer.locFromString(mapData.getBlueSpawn()));
            }
        });
    }

    public void teleportPlayer(Player player) {
        MapData mapData = this.mapHandler.getMap(this.mapPlaying);
        TeamData teamData = this.teamHandler.getPlayerTeam(player);

        if (teamData.getName().equalsIgnoreCase("red")) {
            player.teleport(LocationSerializer.locFromString(mapData.getRedSpawn()));
        } else {
            player.teleport(LocationSerializer.locFromString(mapData.getBlueSpawn()));
        }

        resetBlockAmount(player);
    }

    public void givePlayerItems(List<Player> players) {
        final ItemStack stick = new ItemBuilder(Material.STICK)
                .setName("<dark_gray>» <red>Knüppel")
                .setUnbreakable(true)
                .addEnchantment(Enchantment.KNOCKBACK, 2).build();

        final ItemStack blocks = new ItemBuilder(Material.SANDSTONE).setAmount(64).build();

        final ItemStack pickaxe = new ItemBuilder(Material.WOODEN_PICKAXE)
                .setName("<dark_gray>» <red>Spitzhacke")
                .setUnbreakable(true)
                .addEnchantment(Enchantment.EFFICIENCY, 1).build();


        players.forEach(player -> {
            player.getInventory().clear();

            player.getInventory().setItem(0, stick);
            player.getInventory().setItem(1, blocks);
            player.getInventory().setItem(2, pickaxe);
        });
    }

    private void resetBlockAmount(Player player) {
        final ItemStack blocks = new ItemBuilder(Material.SANDSTONE).setAmount(64).build();
        player.getInventory().setItem(1, blocks);
    }

    private void resetBlockAmount(List<Player> players) {
        final ItemStack blocks = new ItemBuilder(Material.SANDSTONE).setAmount(64).build();
        players.forEach(player -> {
            player.getInventory().setItem(1, blocks);
        });
    }

    private void resetAllBlocks() {
        this.placedBlocks.forEach(block -> {
            block.setType(Material.AIR);
        });
        this.placedBlocks.clear();
    }

    private void sendActionBar() {
        TeamData redTeam = this.teamHandler.getTeam("red");
        TeamData blueTeam = this.teamHandler.getTeam("blue");

        String redPlayerName = redTeam.getPlayers().getFirst().getName();
        String bluePlayerName = blueTeam.getPlayers().getFirst().getName();

        if (this.task != null) {
            this.task.cancel();
        }

        this.task = Bukkit.getScheduler().runTaskTimer(MLGRush.getInstance(), () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.sendActionBar(this.mm.deserialize("<red>" + redPlayerName + "</red> " +
                        "<gray>" + redTeam.getScore() + "</gray> <dark_gray>| " +
                        "<blue>" + bluePlayerName + "</blue> <gray>" + blueTeam.getScore() + "</gray>")
                );
            });
        }, 10L, 10L);
    }

    public void startEndCountdown() {
        if (this.countdownTask != null) {
            this.countdownTask.cancel();
            this.countdownTask = null;
        }

        AtomicInteger atomicInteger = new AtomicInteger(10);
        this.countdownTask = Bukkit.getScheduler().runTaskTimer(MLGRush.getInstance(), () -> {
            int countdown = atomicInteger.getAndDecrement();

            if (countdown == 0) {
                Bukkit.getServer().shutdown();
                this.countdownTask.cancel();
                this.countdownTask = null;
                return;
            }

            if (countdown == 2 || countdown == 3 || countdown == 4 || countdown == 5 || countdown == 10) {
                Bukkit.broadcast(this.mm.deserialize(this.prefix + "Das Spiel endet in " + this.color + countdown + " <gray>Sekunden."));
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.3F, 1.0F);
                });
            }

            if (countdown == 1) {
                Bukkit.broadcast(this.mm.deserialize(this.prefix + "Das Spiel endet in " + this.color + "einer <gray>Sekunden."));
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.3F, 1.0F);
                });
            }
        }, 20L, 20L);
    }

    private void cancelActionBar() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }
}
