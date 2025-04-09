package net.asyncproxy.mlgrush.listener.lobby;

import io.papermc.paper.persistence.PersistentDataContainerView;
import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.team.TeamHandler;
import net.asyncproxy.mlgrush.modules.vote.VoteHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

public class InventoryClickListener implements Listener {

    private final TeamHandler teamHandler;

    private final VoteHandler voteHandler;

    private final String prefix;

    private final String color;

    private final MiniMessage mm;

    public InventoryClickListener() {
        this.teamHandler = MLGRush.getInstance().getTeamHandler();
        this.voteHandler = MLGRush.getInstance().getVoteHandler();
        this.prefix = MLGRush.getInstance().getPrefix();
        this.color = MLGRush.getInstance().getColor();
        this.mm = MiniMessage.miniMessage();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null) return;

        if (event.getCurrentItem().getItemMeta() == null) return;

        if (event.getView().title().equals(this.mm.deserialize("<red>Teamauswahl"))) {
            event.setCancelled(true);

            if (event.getCurrentItem().getType() == Material.RED_BED) {
                NamespacedKey key = new NamespacedKey("mlgrush", "team");
                PersistentDataContainerView container = event.getCurrentItem().getPersistentDataContainer();
                if (container.has(key, PersistentDataType.STRING)) {
                    String teamName = container.get(key, PersistentDataType.STRING);
                    if (teamName == null) return;
                    if (this.teamHandler.getTeam(teamName).getPlayerCount() == this.teamHandler.getTeam(teamName).getMaxPlayers()) {
                        event.getView().close();
                        player.sendMessage(this.mm.deserialize(this.prefix + "<red>Dieses Team ist bereits voll."));
                        return;
                    }

                    if (this.teamHandler.getPlayerTeam(player) == null) {
                        this.teamHandler.addPlayerToTeam(teamName, player);
                        event.getView().close();
                        if (teamName.equalsIgnoreCase("red")) {
                            player.sendMessage(this.mm.deserialize(this.prefix + "Du bist jetzt im Team " + this.color + "Rot.</gradient>"));
                        } else {
                            player.sendMessage(this.mm.deserialize(this.prefix + "Du bist jetzt im Team " + this.color + "Blau.</gradient>"));
                        }
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.3F, 1.0F);
                        return;
                    }

                    if (!this.teamHandler.getPlayerTeam(player).getName().equalsIgnoreCase(teamName)) {
                        this.teamHandler.removePlayerFromTeam(player);
                        this.teamHandler.addPlayerToTeam(teamName, player);
                        event.getView().close();
                        if (teamName.equalsIgnoreCase("red")) {
                            player.sendMessage(this.mm.deserialize(this.prefix + "Du bist jetzt im Team " + this.color + "Rot.</gradient>"));
                        } else {
                            player.sendMessage(this.mm.deserialize(this.prefix + "Du bist jetzt im Team " + this.color + "Blau.</gradient>"));
                        }
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.3F, 1.0F);
                    } else {
                        event.getView().close();
                        player.sendMessage(this.mm.deserialize(this.prefix + "<red>Du bist bereits in diesem Team."));
                    }
                }
            }
        }

        if (event.getView().title().equals(this.mm.deserialize("<green>Map-Voting"))) {
            event.setCancelled(true);

            if (event.getCurrentItem().getType() == Material.PAPER) {
                NamespacedKey key = new NamespacedKey("mlgrush", "map");
                PersistentDataContainerView container = event.getCurrentItem().getPersistentDataContainer();
                if (container.has(key, PersistentDataType.STRING)) {
                    String mapName = container.get(key, PersistentDataType.STRING);
                    if (this.voteHandler.hasVotedForMap(player, mapName)) {
                        event.getView().close();
                        player.sendMessage(this.mm.deserialize(this.prefix + "<red>Du votest bereits für diese Map."));
                        return;
                    }

                    if (this.voteHandler.hasVoted(player)) {
                        this.voteHandler.removeVote(player);
                    }

                    this.voteHandler.addVote(player, mapName);
                    event.getView().close();
                    player.sendMessage(this.mm.deserialize(this.prefix + "Du votest jetzt für die Map " + this.color + mapName + "</gradient>."));
                }
            }
        }
    }
}
