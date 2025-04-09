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

import java.util.ArrayList;
import java.util.List;

public class GameHandler {

    @Getter
    private GameState gameState;

    private final TeamHandler teamHandler;

    private final MapHandler mapHandler;

    private final MiniMessage mm;

    @Setter
    private String mapPlaying;

    @Getter
    private final List<Block> placedBlocks;

    public GameHandler() {
        this.gameState = GameState.LOBBY;
        this.teamHandler = MLGRush.getInstance().getTeamHandler();
        this.mapHandler = MLGRush.getInstance().getMapHandler();
        this.mm = MiniMessage.miniMessage();
        this.mapPlaying = null;
        this.placedBlocks = new ArrayList<>();
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
    }

    public void endGame() {
        this.gameState = GameState.FINISHED;

    }

    public void destroyBed(Block block) {

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

    public void givePlayerItems(List<Player> players) {
        ItemStack stick = new ItemBuilder(Material.STICK)
                .setName("<dark_gray>» <red>Knüppel")
                .setUnbreakable(true)
                .addEnchantment(Enchantment.KNOCKBACK, 2).build();

        ItemStack blocks = new ItemBuilder(Material.SANDSTONE).setAmount(64).build();

        ItemStack pickaxe = new ItemBuilder(Material.WOODEN_PICKAXE)
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


}
