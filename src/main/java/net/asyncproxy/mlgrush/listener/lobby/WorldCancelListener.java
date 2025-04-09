package net.asyncproxy.mlgrush.listener.lobby;

import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class WorldCancelListener implements Listener {

    private boolean isInLobby(Player player) {
        return MLGRush.getInstance().getGameHandler().getGameState() == GameState.LOBBY;
    }

    private boolean isInLobby() {
        return MLGRush.getInstance().getGameHandler().getGameState() == GameState.LOBBY;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler 
    public void onBlockDamage(BlockDamageEvent event) {
        if (isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler 
    public void onBlockFertilize(BlockFertilizeEvent event) {
        if (event.getPlayer() != null && isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler 
    public void onSignChange(SignChangeEvent event) {
        if (isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler 
    public void onEntityDamage(EntityDamageEvent event) {
        if (isInLobby()) {
            if (event.getEntity() instanceof Player && isInLobby((Player) event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (isInLobby()) {
            if (event.getDamager() instanceof Player && isInLobby((Player) event.getDamager())) {
                event.setCancelled(true);
            }
            if (event.getEntity() instanceof Player && isInLobby((Player) event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler 
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (isInLobby()) {
            if (event.getEntity() instanceof Player && isInLobby((Player) event.getEntity())) {
                event.setCancelled(true);
                ((Player) event.getEntity()).setFoodLevel(20);
                ((Player) event.getEntity()).setSaturation(20);
            }
        }
    }

    @EventHandler 
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler 
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (isInLobby()) {
            if (event.getEntity() instanceof Player && isInLobby((Player) event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler 
    public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent event) {
        if (isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler 
    public void onInventoryClick(InventoryClickEvent event) {
        if (isInLobby()) {
            if (event.getWhoClicked() instanceof Player && isInLobby((Player) event.getWhoClicked())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler 
    public void onInventoryDrag(InventoryDragEvent event) {
        if (isInLobby()) {
            if (event.getWhoClicked() instanceof Player && isInLobby((Player) event.getWhoClicked())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler 
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (isInLobby(event.getPlayer())) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR ||
                    event.getAction() == Action.RIGHT_CLICK_BLOCK ||
                    event.getAction() == Action.LEFT_CLICK_AIR ||
                    event.getAction() == Action.LEFT_CLICK_BLOCK) {

                event.setCancelled(true);
            }
        }
    }

    @EventHandler 
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler 
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler 
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    @EventHandler 
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler 
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler 
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        if (isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler 
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler 
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        if (isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler 
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player &&
                isInLobby((Player) event.getEntity().getShooter())) {
            event.setCancelled(true);
        }
    }

    @EventHandler 
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        if (isInLobby(event.getPlayer())) {
            event.setAmount(0);
        }
    }

    @EventHandler 
    public void onVehicleDamage(VehicleDamageEvent event) {
        if (event.getAttacker() instanceof Player && isInLobby((Player) event.getAttacker())) {
            event.setCancelled(true);
        }
    }

    @EventHandler 
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player && isInLobby((Player) event.getEntered())) {
            event.setCancelled(true);
        }
    }

    @EventHandler 
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Player && isInLobby((Player) event.getRemover())) {
            event.setCancelled(true);
        }
    }

    @EventHandler 
    public void onPvP(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();

            if (isInLobby(damaged) || isInLobby(damager)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler 
    public void onPlayerHarvestBlock(PlayerHarvestBlockEvent event) {
        if (isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler 
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player && isInLobby((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler 
    public void onEntityDropItem(EntityDropItemEvent event) {
        if (event.getEntity() instanceof Player && isInLobby((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }
}
