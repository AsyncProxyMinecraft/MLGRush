package net.asyncproxy.mlgrush.listener.lobby;

import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.inventory.InventoryHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    private final InventoryHandler inventoryHandler;

    public PlayerInteractListener() {
        this.inventoryHandler = MLGRush.getInstance().getInventoryHandler();
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() == null) return;

        if (event.getItem().getType() == Material.RED_BED) {
            this.inventoryHandler.openTeamSelectionInventory(player);
        }

        if (event.getItem().getType() == Material.PAPER) {
            this.inventoryHandler.openMapVotingInventory(player);
        }
    }
}
