package net.asyncproxy.mlgrush.modules.player;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PlayerData {

    private final UUID uuid;

    private int kills;

    private int deaths;

    private int wins;

    private int losses;

    public PlayerData(UUID uuid, int kills, int deaths, int wins, int losses) {
        this.uuid = uuid;
        this.kills = kills;
        this.deaths = deaths;
        this.wins = wins;
        this.losses = losses;
    }

    public void addKills(int kills) {
        this.kills += kills;
    }

    public void addDeaths(int deaths) {
        this.deaths += deaths;
    }

    public void addWins(int wins) {
        this.wins += wins;
    }

    public void addLosses(int losses) {
        this.losses += losses;
    }

}
