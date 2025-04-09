package net.asyncproxy.mlgrush.modules.game;

import lombok.Getter;
import lombok.Setter;
import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.countdown.CountdownHandler;
import net.asyncproxy.mlgrush.modules.item.ItemBuilder;
import net.asyncproxy.mlgrush.modules.map.LocationSerializer;
import net.asyncproxy.mlgrush.modules.map.MapData;
import net.asyncproxy.mlgrush.modules.map.MapHandler;
import net.asyncproxy.mlgrush.modules.team.TeamData;
import net.asyncproxy.mlgrush.modules.team.TeamHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

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

    public GameHandler() {
        this.gameState = GameState.LOBBY;
        this.teamHandler = MLGRush.getInstance().getTeamHandler();
        this.mapHandler = MLGRush.getInstance().getMapHandler();
        this.mm = MiniMessage.miniMessage();
        this.mapPlaying = null;
        this.placedBlocks = new ArrayList<>();
        this.task = null;
    }

    public void startGame(String mapName) {
        this.gameState = GameState.RUNNING;
        this.setMapPlaying(mapName);

        List<Player> distributePlayers = new ArrayList<>();

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (this.teamHandler.getPlayerTeam(player) == null) {
                distributePlayers.add(player);
            }
        });

        this.teamHandler.distributePlayers(distributePlayers);

        this.teamHandler.getAllTeams().forEach(team -> {
            teleportPlayersToMap(team.getPlayers());
            givePlayerItems(team.getPlayers());
        });

        sendActionBar();
    }

    public void endGame(Player winner) {
        this.gameState = GameState.FINISHED;

    }

    public void destroyBed(Player player) {
        TeamData teamData = this.teamHandler.getPlayerTeam(player);
        if (teamData == null) return;

        teamData.setScore(teamData.getScore() + 1);

        resetAllBlocks();
        if (teamData.getScore() == 10) {
            endGame(player);
            return;
        }

        this.teamHandler.getAllTeams().forEach(team -> {
           this.teleportPlayersToMap(team.getPlayers());
           this.resetBlockAmount(team.getPlayers());
        });

        sendActionBar();
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
        }, 20L, 20L);
    }

    private void cancelActionBar() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }
}
