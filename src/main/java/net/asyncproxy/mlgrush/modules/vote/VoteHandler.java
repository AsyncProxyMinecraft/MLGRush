package net.asyncproxy.mlgrush.modules.vote;

import lombok.Getter;
import lombok.Setter;
import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.map.MapHandler;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class VoteHandler {

    private final MapHandler mapHandler;
    private final ConcurrentHashMap<Player, String> mapVotes;

    @Getter
    @Setter
    private boolean isForceMap;

    @Getter
    @Setter
    private String forceMapName;

    public VoteHandler() {
        this.mapHandler = MLGRush.getInstance().getMapHandler();
        this.mapVotes = new ConcurrentHashMap<>();
        this.isForceMap = false;
        this.forceMapName = null;
    }

    public void forceMap(String forceMapName) {
        this.setForceMapName(forceMapName);
        if (!this.isForceMap) {
            this.setForceMap(true);
        }
    }

    public void addVote(Player player, String mapName) {
        if (this.mapHandler.getMap(mapName) != null) {
            if (!this.mapVotes.containsKey(player)) {
                this.mapVotes.put(player, mapName);
            }
        }
    }

    public void removeVote(Player player) {
        this.mapVotes.remove(player);
    }

    public void clearAllVotes() {
        this.mapVotes.clear();
    }

    public boolean hasVoted(Player player) {
        return this.mapVotes.containsKey(player);
    }

    public boolean hasVotedForMap(Player player, String mapName) {
        for (Map.Entry<Player, String> entry : this.mapVotes.entrySet()) {
            if (entry.getKey().equals(player) && entry.getValue().equals(mapName)) {
                return true;
            }
        }
        return false;
    }

    public int getVotesFromMap(String mapName) {
        int votes = 0;
        if (this.mapHandler.getMap(mapName) != null) {
            for (Map.Entry<Player, String> entry : this.mapVotes.entrySet()) {
                if (entry.getValue().equals(mapName)) {
                    votes++;
                }
            }
        }
        return votes;
    }

    public String getVotedMap() {
        if (this.mapHandler.getAllMapWithNames().isEmpty()) {
            return "NO MAP";
        } else {
            if (!this.isForceMap) {
                if (this.mapVotes.isEmpty()) {
                    if (this.mapHandler.getAllMapWithNames().size() == 1) {
                        return this.mapHandler.getAllMapWithNames().getFirst();
                    }

                    int randomNumber = new Random().nextInt(0, this.mapHandler.getAllMapWithNames().size());
                    return this.mapHandler.getAllMapWithNames().get(randomNumber);
                } else {
                    Map<String, Integer> voteCount = new HashMap<>();

                    for (String mapName : this.mapVotes.values()) {
                        voteCount.put(mapName, voteCount.getOrDefault(mapName, 0) + 1);
                    }

                    String mostVotedMap = null;
                    int maxVotes = 0;

                    for (Map.Entry<String, Integer> entry : voteCount.entrySet()) {
                        if (entry.getValue() > maxVotes) {
                            maxVotes = entry.getValue();
                            mostVotedMap = entry.getKey();
                        }
                    }

                    return mostVotedMap;
                }
            } else {
                return this.getForceMapName();
            }
        }
    }
}
