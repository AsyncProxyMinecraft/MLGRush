package net.asyncproxy.mlgrush.modules.config;

import net.asyncproxy.mlgrush.MLGRush;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.*;

public class SpawnFileHandler {

    private JsonDocument jsonDocument;

    public void setSpawn(Location spawnLocation, Player player) {
        try {
            File file = new File(MLGRush.getInstance().getDataFolder(), "spawn.json");
            this.jsonDocument = JsonDocument.loadDocument(file);

            if (this.jsonDocument == null) {
                this.jsonDocument = new JsonDocument();
            }

            this.jsonDocument.append("spawn-world", spawnLocation.getWorld().getName());
            this.jsonDocument.append("spawn-x", String.valueOf(spawnLocation.getX()));
            this.jsonDocument.append("spawn-y", String.valueOf(spawnLocation.getY()));
            this.jsonDocument.append("spawn-z", String.valueOf(spawnLocation.getZ()));
            this.jsonDocument.append("spawn-yaw", String.valueOf(spawnLocation.getYaw()));
            this.jsonDocument.append("spawn-pitch", String.valueOf(spawnLocation.getPitch()));
            this.jsonDocument.save(file);

            player.sendMessage(MiniMessage.miniMessage().deserialize(MLGRush.getInstance().getPrefix() + "<green>Du hast den Spawn gesetzt."));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.3F, 1.0F);
        } catch (Exception e) {
            MLGRush.getInstance().getLogger().severe("Fehler beim Laden der Konfiguration: " + e.getMessage());
            player.sendMessage(MiniMessage.miniMessage().deserialize(MLGRush.getInstance().getPrefix() + "<red>Es ist ein Fehler aufgetreten den Spawn zu setzen!"));
        }
    }

    public Location getSpawn() {
        File file = new File(MLGRush.getInstance().getDataFolder(), "spawn.json");
        this.jsonDocument = JsonDocument.loadDocument(file);

        if (this.jsonDocument != null) {
            World world = Bukkit.getWorld(this.jsonDocument.getString("spawn-world"));
            double x = Double.parseDouble(this.jsonDocument.getString("spawn-x"));
            double y = Double.parseDouble(this.jsonDocument.getString("spawn-y"));
            double z = Double.parseDouble(this.jsonDocument.getString("spawn-z"));
            float yaw = Float.parseFloat(this.jsonDocument.getString("spawn-yaw"));
            float pitch = Float.parseFloat(this.jsonDocument.getString("spawn-pitch"));

            return new Location(world, x, y, z, yaw, pitch);
        }
        return null;
    }
}
