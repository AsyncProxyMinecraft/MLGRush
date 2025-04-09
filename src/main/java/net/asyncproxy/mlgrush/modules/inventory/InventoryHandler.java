package net.asyncproxy.mlgrush.modules.inventory;

import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.item.ItemBuilder;
import net.asyncproxy.mlgrush.modules.map.MapHandler;
import net.asyncproxy.mlgrush.modules.team.TeamData;
import net.asyncproxy.mlgrush.modules.team.TeamHandler;
import net.asyncproxy.mlgrush.modules.vote.VoteHandler;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InventoryHandler {

    private final MiniMessage mm;
    private final TeamHandler teamHandler;
    private final MapHandler mapHandler;
    private final String color;
    private final VoteHandler voteHandler;

    public InventoryHandler() {
        this.mm = MiniMessage.miniMessage();
        this.teamHandler = MLGRush.getInstance().getTeamHandler();
        this.mapHandler = MLGRush.getInstance().getMapHandler();
        this.color = MLGRush.getInstance().getColor();
        this.voteHandler = MLGRush.getInstance().getVoteHandler();
    }

    public void openTeamSelectionInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, this.mm.deserialize("<red>Teamauswahl"));

        if (teamHandler.getAllTeams().isEmpty()) {
            inventory.setItem(0, new ItemBuilder(Material.BARRIER).setName("<red>Es wurden Keine Teams erstellt.").build());
        } else {
            for (int i = 0; i < teamHandler.getAllTeams().size(); i++) {
                TeamData team = teamHandler.getAllTeams().get(i);
                ItemBuilder itemBuilder = new ItemBuilder(Material.RED_BED);
                if (team.getName().equalsIgnoreCase("blue")) {
                    itemBuilder.setName("<blue>Blau <dark_gray>» <gray>"  + team.getPlayerCount() + "/" + team.getMaxPlayers());
                } else {
                    itemBuilder.setName("<red>Rot <dark_gray>» <gray>"  + team.getPlayerCount() + "/" + team.getMaxPlayers());
                }

                itemBuilder.setPersistentData("mlgrush", "team", team.getName());

                if (team.getPlayerCount() == 0) {
                    inventory.setItem(i, itemBuilder.build());
                } else {
                    team.getPlayers().forEach(p -> {
                        if (team.getName().equalsIgnoreCase("blue")) {
                            itemBuilder.addLore("§8» §9" + p.getName());
                        } else {
                            itemBuilder.addLore("§8» §c" + p.getName());
                        }
                    });

                    inventory.setItem(i, itemBuilder.build());
                }
            }
        }

        player.openInventory(inventory);
    }

    public void openMapVotingInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, this.mm.deserialize("<green>Map-Voting"));

        if (this.mapHandler.getAllMapWithNames().isEmpty()) {
            inventory.setItem(0, new ItemBuilder(Material.BARRIER).setName("<red>Es wurden Keine Maps erstellt.").build());
        } else {
            for (int i = 0; i < this.mapHandler.getAllMapWithNames().size(); i++) {
                String mapName = this.mapHandler.getAllMapWithNames().get(i);
                ItemBuilder itemBuilder = new ItemBuilder(Material.PAPER);
                itemBuilder.setName(this.color + mapName + " <dark_gray>» " + this.color + this.voteHandler.getVotesFromMap(mapName));
                itemBuilder.setPersistentData("mlgrush", "map", mapName);
                inventory.setItem(i, itemBuilder.build());
            }
        }

        player.openInventory(inventory);
    }
}
