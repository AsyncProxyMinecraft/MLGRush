package net.asyncproxy.mlgrush.modules.map;

import lombok.Getter;
import lombok.Setter;

@Getter
public class MapData {

    private final String mapName;

    @Setter
    private String deathHeight;

    @Setter
    private String redSpawn;

    @Setter
    private String blueSpawn;

    @Setter
    private String redBedTop;

    @Setter
    private String redBedBottom;

    @Setter
    private String blueBedTop;

    @Setter
    private String blueBedBottom;

    public MapData(String mapName) {
        this.mapName = mapName;
        this.deathHeight = null;
        this.redSpawn = null;
        this.blueSpawn = null;
        this.redBedTop = null;
        this.redBedBottom = null;
        this.blueBedTop = null;
        this.blueBedBottom = null;
    }
}
