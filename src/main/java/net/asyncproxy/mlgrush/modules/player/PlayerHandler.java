package net.asyncproxy.mlgrush.modules.player;

import lombok.Getter;
import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.database.IMySQLHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCreatePortalEvent;

import java.sql.ClientInfoStatus;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerHandler {

    @Getter
    private final ConcurrentHashMap<UUID, PlayerData> loadedPlayers;

    private final IMySQLHandler mySQLHandler;

    public PlayerHandler() {
        this.mySQLHandler = MLGRush.getInstance().getMySQLHandler();
        this.loadedPlayers = new ConcurrentHashMap<>();
    }

    public PlayerData getPlayer(UUID uuid) {
        return this.loadedPlayers.get(uuid);
    }

    public CompletableFuture<Boolean> doesPlayerExist(UUID uuid) {
        final String query = "SELECT uuid FROM mlgrush_players WHERE uuid = ?";

        return this.mySQLHandler.queryAsync(query, resultSet -> {
            try {
                return resultSet.getString("uuid");
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        }, uuid.toString()).thenApply(result -> {
            if (!result.isEmpty()) {
                String dbUUID = result.getFirst();
                return dbUUID.equals(uuid.toString());
            }
            return false;
        });
    }

    public void createNewPlayer(UUID uuid) {
        String insertPlayer = "INSERT INTO mlgrush_players (uuid, kills, deaths, wins, losses) VALUES (?, ?, ?, ?, ?)";

        this.mySQLHandler.updateAsync(insertPlayer, uuid.toString(), 0, 0, 0, 0);
        this.getLoadedPlayers().put(uuid, new PlayerData(uuid, 0, 0, 0, 0));
    }

    public void loadAllPlayers() {
        String query = "SELECT * FROM mlgrush_players";

        this.mySQLHandler.queryAsync(query, resultSet -> {
            try {
                if (resultSet.next()) {
                    this.loadedPlayers.put(UUID.fromString(resultSet.getString("uuid")), new PlayerData(
                            UUID.fromString(resultSet.getString("uuid")),
                            resultSet.getInt("kills"),
                            resultSet.getInt("deaths"),
                            resultSet.getInt("wins"),
                            resultSet.getInt("losses")
                    ));
                }
            } catch (SQLException exception) {
                throw new RuntimeException("Error loading all Players: ", exception);
            }
            return null;
        });
    }

    public void loadPlayer(Player player) {
        if (this.loadedPlayers.containsKey(player.getUniqueId())) return;

        String query = "SELECT * FROM mlgrush_players WHERE uuid = ?";

        this.mySQLHandler.queryAsync(query, resultSet -> {
            try {
                this.loadedPlayers.put(player.getUniqueId(), new PlayerData(
                        UUID.fromString(resultSet.getString("uuid")),
                        resultSet.getInt("kills"),
                        resultSet.getInt("deaths"),
                        resultSet.getInt("wins"),
                        resultSet.getInt("losses")
                ));
            } catch (SQLException exception) {
                throw new RuntimeException("Error while loading Player " + player.getName() + ": ", exception);
            }
            return null;
        }, player.getUniqueId().toString());
    }

    public void savePlayer(Player player) {
        if (!this.loadedPlayers.containsKey(player.getUniqueId())) return;

        UUID uuid = player.getUniqueId();

        PlayerData playerData = this.loadedPlayers.get(uuid);

        String update = "UPDATE mlgrush_players SET kills = ?, deaths = ?, wins = ?, losses = ? WHERE uuid = ?";

        this.mySQLHandler.updateAsync(update, playerData.getKills(), playerData.getDeaths(), playerData.getWins(), playerData.getLosses(), uuid.toString());
    }

    public void saveAndRemovePlayer(Player player) {
        if (!this.loadedPlayers.containsKey(player.getUniqueId())) return;

        UUID uuid = player.getUniqueId();

        PlayerData playerData = this.loadedPlayers.get(uuid);

        String update = "UPDATE mlgrush_players SET kills = ?, deaths = ?, wins = ?, losses = ? WHERE uuid = ?";

        this.mySQLHandler.updateAsync(update, playerData.getKills(), playerData.getDeaths(), playerData.getWins(), playerData.getLosses(), uuid.toString());
        this.loadedPlayers.remove(uuid);
    }
}
