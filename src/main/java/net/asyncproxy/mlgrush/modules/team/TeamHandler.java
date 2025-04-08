package net.asyncproxy.mlgrush.modules.team;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class TeamHandler {

    private final Map<String, TeamData> teams;
    private final Scoreboard scoreboard;

    /**
     * Creates a new TeamBuilder
     * @param scoreboardManager
     */
    public TeamHandler(ScoreboardManager scoreboardManager) {
        this.teams = new HashMap<>();
        this.scoreboard = scoreboardManager.getNewScoreboard();
    }

    /**
     * Creates a new Team
     * @param name name for the Team
     * @param displayName Displayname for the Team
     * @param color chat color for the team
     * @param maxPlayers maximumPlayers for the team
     * @return the created team
     */
    public TeamData createTeam(String name, Component displayName, NamedTextColor color, int maxPlayers) {
        String teamId = name.length() > 16 ? name.substring(0, 16) : name;

        if (this.scoreboard.getTeam(teamId) != null) {
            scoreboard.getTeam(teamId).unregister();
        }

        Team scoreboardTeamData = this.scoreboard.registerNewTeam(teamId);
        scoreboardTeamData.displayName(displayName);
        scoreboardTeamData.color(color);
        scoreboardTeamData.setAllowFriendlyFire(false);
        scoreboardTeamData.setCanSeeFriendlyInvisibles(true);

        TeamData team = new TeamData(name, displayName, color, maxPlayers, scoreboardTeamData, this.scoreboard);
        teams.put(teamId, team);

        return team;
    }

    /**
     * Adds a player to a team
     * @param teamName Name from the team
     * @param player player who is getting added to the team
     * @return true if successful, false if team doesn't exist
     */
    public boolean addPlayerToTeam(String teamName, Player player) {
        TeamData team = this.teams.get(teamName);
        if (team == null) {
            return false;
        }

        return team.addPlayer(player);
    }

    /**
     * removes a player his current team
     * @param player the player who is getting removed
     * @return true if successfully removed from the team, false if the player wasn't in a team
     */
    public boolean removePlayerFromTeam(Player player) {
        for (TeamData team : this.teams.values()) {
            if (team.removePlayer(player)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the Team from the Player
     * @param player player who you want the team from
     * @return returns the TeamData, null if the player isn't in a team
     */
    public TeamData getPlayerTeam(Player player) {
        for (TeamData team : this.teams.values()) {
            if (team.containsPlayer(player)) {
                return team;
            }
        }
        return null;
    }

    /**
     * Get the TeamData from the Name
     * @param teamName Team name
     * @return returns the TeamData
     */
    public TeamData getTeam(String teamName) {
        return this.teams.get(teamName);
    }

    /**
     * List of all Teams created in the TeamBuilder
     * @return returns a list of all Teams you've created
     */
    public List<TeamData> getAllTeams() {
        return new ArrayList<>(this.teams.values());
    }

    /**
     * Setting players even in teams
     * @param players List of Players, who should be going fairly in random Teams
     */
    public void distributePlayers(List<Player> players) {
        for (Player player : players) {
            removePlayerFromTeam(player);
        }

        List<TeamData> teamList = getAllTeams();
        if (teamList.isEmpty()) {
            return;
        }

        int teamIndex = 0;
        Random random = new Random();

        List<Player> shuffledPlayers = new ArrayList<>(players);
        for (int i = shuffledPlayers.size() - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            Player temp = shuffledPlayers.get(index);
            shuffledPlayers.set(index, shuffledPlayers.get(i));
            shuffledPlayers.set(i, temp);
        }

        for (Player player : shuffledPlayers) {
            while (teamIndex < teamList.size()) {
                TeamData team = teamList.get(teamIndex);
                if (team.addPlayer(player)) {
                    break;
                }
                teamIndex++;
            }

            if (teamIndex >= teamList.size()) {
                teamIndex = 0;
            }
        }
    }

    /**
     * updates the Scoreboard for a player
     * @param player player who should get the scoreboard updated
     */
    public void setScoreboard(Player player) {
        player.setScoreboard(this.scoreboard);
    }

    /**
     * Updates the scoreboard for all Players who are in a Team
     */
    public void updateScoreboardForAllPlayers() {
        for (TeamData team : this.teams.values()) {
            for (Player player : team.getPlayers()) {
                player.setScoreboard(this.scoreboard);
            }
        }
    }

    /**
     * Deletes a Team from the TeamList
     * @param teamName the name of the team
     * @return true if successful, false if team doesn't exist
     */
    public boolean deleteTeam(String teamName) {
        TeamData team = this.teams.get(teamName);
        if (team == null) {
            return false;
        }

        for (Player player : team.getPlayers()) {
            team.removePlayer(player);
        }

        team.getScoreboardTeam().unregister();

        teams.remove(teamName);

        return true;
    }

    /**
     * Deletes all registered teams
     */
    public void deleteAllTeams() {
        for (String teamName : new ArrayList<>(this.teams.keySet())) {
            deleteTeam(teamName);
        }
    }

}
