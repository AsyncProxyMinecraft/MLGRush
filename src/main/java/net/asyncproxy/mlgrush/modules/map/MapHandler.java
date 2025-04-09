package net.asyncproxy.mlgrush.modules.map;

import lombok.Getter;
import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.database.IMySQLHandler;
import org.bukkit.block.Block;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MapHandler {

    @Getter
    private final ConcurrentHashMap<String, MapData> loadedMaps;

    private final IMySQLHandler mySQLHandler;

    public MapHandler() {
        this.loadedMaps = new ConcurrentHashMap<>();
        this.mySQLHandler = MLGRush.getInstance().getMySQLHandler();

        this.loadAllMaps();
    }

    public void addMap(String mapName, MapData mapData) {
        this.loadedMaps.put(mapName, mapData);
    }

    public void removeMap(String mapName) {
        this.loadedMaps.remove(mapName);

        String query = "DELETE FROM mlgrush_maps WHERE mapName = ?";
        this.mySQLHandler.updateAsync(query, mapName);
    }

    public void storeMap(String mapName, MapData mapData) {
        String query = "INSERT INTO mlgrush_maps (mapName, deathHeight, redSpawn, blueSpawn, redBedTop, redBedBottom, blueBedTop, blueBedBottom) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        this.mySQLHandler.updateAsync(query, mapName, mapData.getDeathHeight(), mapData.getRedSpawn(), mapData.getBlueSpawn(), mapData.getRedBedTop(), mapData.getRedBedBottom(), mapData.getBlueBedTop(), mapData.getBlueBedBottom());
    }

    public MapData getMap(String mapName) {
        return this.loadedMaps.get(mapName);
    }

    public List<String> getAllMapWithNames() {
        List<String> maps = new ArrayList<>();
        this.loadedMaps.forEach((mapName, mapData) -> {
            maps.add(mapName);
        });
        return maps;
    }

    private void loadAllMaps() {
        String query = "SELECT * FROM mlgrush_maps";

        this.mySQLHandler.queryAsync(query, resultSet -> {
            try {
                MapData mapData = new MapData(resultSet.getString("mapName"));
                mapData.setDeathHeight(resultSet.getString("deathHeight"));
                mapData.setRedSpawn(resultSet.getString("redSpawn"));
                mapData.setBlueSpawn(resultSet.getString("blueSpawn"));
                mapData.setRedBedTop(resultSet.getString("redBedTop"));
                mapData.setRedBedBottom(resultSet.getString("redBedBottom"));
                mapData.setBlueBedTop(resultSet.getString("blueBedTop"));
                mapData.setBlueBedBottom(resultSet.getString("blueBedBottom"));

                this.loadedMaps.put(mapData.getMapName(), mapData);
            } catch (SQLException exception) {
                throw new RuntimeException("There was an error loading all Maps.", exception);
            }
            return null;
        });
    }
}
