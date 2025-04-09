package net.asyncproxy.mlgrush.commands;

import net.asyncproxy.mlgrush.MLGRush;
import net.asyncproxy.mlgrush.modules.map.LocationSerializer;
import net.asyncproxy.mlgrush.modules.map.MapData;
import net.asyncproxy.mlgrush.modules.map.MapHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MapCommand implements CommandExecutor {

    private final String prefix;

    private final String color;

    private final MiniMessage mm;

    private final MapHandler mapHandler;

    public MapCommand() {
        this.prefix = MLGRush.getInstance().getPrefix();
        this.color = MLGRush.getInstance().getColor();
        this.mm = MiniMessage.miniMessage();
        this.mapHandler = MLGRush.getInstance().getMapHandler();
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Du kannst diesen Befehl nur als Spieler ausführen.");
            return true;
        }

        if (!player.hasPermission("mlgrush.setup")) {
            player.sendMessage(this.mm.deserialize(this.prefix + "<red>Du hast keine Rechte auf diesen Befehl."));
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> {
                if (args.length != 2) {
                    player.sendMessage(this.mm.deserialize(this.prefix + "Verwendung: " + this.color + "/map create <Map-Name></gradient>"));
                    return true;
                }

                String mapName = args[1];
                if (this.mapHandler.getMap(mapName) != null) {
                    player.sendMessage(this.mm.deserialize(this.prefix + "<red>Es gibt bereits eine Map mit dem Namen " + mapName + "."));
                    return true;
                }

                this.mapHandler.addMap(mapName, new MapData(mapName));
                player.sendMessage(this.mm.deserialize(this.prefix + "Du hast die Map " + this.color + mapName + "</gradient> erfolgreich erstellt."));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.3F, 1.0F);
            }

            case "setspawn" -> {
                if (args.length != 3) {
                    player.sendMessage(this.mm.deserialize(this.prefix + "Verwendung: " + this.color + "/map setspawn <Map-Name> <Rot, Blau></gradient>"));
                    return true;
                }

                String mapName = args[1];
                String team = args[2];
                if (!team.equalsIgnoreCase("rot") && !team.equalsIgnoreCase("blau")) {
                    player.sendMessage(this.prefix + "<red>Das Team " + team + " existiert nicht.");
                    return true;
                }

                if (this.mapHandler.getMap(mapName) == null) {
                    player.sendMessage(this.mm.deserialize(this.prefix + "<red>Es existiert keine Map mit dem Namen " + mapName + "</gradient>."));
                    return true;
                }

                MapData map = this.mapHandler.getMap(mapName);
                if (team.equalsIgnoreCase("rot")) {
                    map.setRedSpawn(LocationSerializer.toString(player.getLocation()));
                } else {
                    map.setBlueSpawn(LocationSerializer.toString(player.getLocation()));
                }

                player.sendMessage(this.mm.deserialize(this.prefix + "Du hast den Spawn für das Team " + this.color + team + "</gradient> erfolgreich gesetzt."));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.3F, 1.0F);
            }

            case "setdeathheight" -> {
                if (args.length != 2) {
                    player.sendMessage(this.mm.deserialize(this.prefix + "Verwendung: " + this.color + "/map setdeathheight <Map-Name></gradient>"));
                    return true;
                }

                String mapName = args[1];
                if (this.mapHandler.getMap(mapName) == null) {
                    player.sendMessage(this.mm.deserialize(this.prefix + "<red>Es existiert keine Map mit dem Namen " + mapName + "</gradient>."));
                    return true;
                }

                MapData map = this.mapHandler.getMap(mapName);
                map.setDeathHeight(LocationSerializer.toString(player.getLocation()));
                player.sendMessage(this.mm.deserialize(this.prefix + "Du hast die Todeshöhe erfolgreich gesetzt."));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.3F, 1.0F);
            }

            case "setbed" -> {
                if (args.length != 3) {
                    player.sendMessage(this.mm.deserialize(this.prefix + "Verwendung: " + this.color + "/map setbed <Map-Name> <Rot, Blau></gradient>"));
                    return true;
                }

                String mapName = args[1];
                if (this.mapHandler.getMap(mapName) == null) {
                    player.sendMessage(this.mm.deserialize(this.prefix + "<red>Es existiert keine Map mit dem Namen " + mapName + "</gradient>."));
                    return true;
                }

                String team = args[2];
                if (!team.equalsIgnoreCase("rot") && !team.equalsIgnoreCase("blau")) {
                    player.sendMessage(this.prefix + "<red>Das Team " + team + " existiert nicht.");
                    return true;
                }

                Block block = player.getTargetBlockExact(20);
                if (block == null) {
                    player.sendMessage(this.prefix + "<red>Du musst auf das Bed schauen von dem Jeweiligen Team.");
                    return true;
                }

                if (!block.getType().toString().endsWith("_BED")) {
                    player.sendMessage(this.prefix + "<red>Du musst auf das Bed schauen von dem Jeweiligen Team.");
                    return true;
                }


                BlockData blockData = block.getBlockData();

                MapData map = this.mapHandler.getMap(mapName);
                if (blockData instanceof Bed) {
                    Bed bed = (Bed) blockData;
                    BlockFace facing = bed.getFacing();
                    boolean isHead = bed.getPart() == Bed.Part.HEAD;

                    if (team.equalsIgnoreCase("rot")) {
                        if (isHead) {
                            map.setRedBedTop(LocationSerializer.toString(block.getLocation()));
                        } else {
                            map.setRedBedBottom(LocationSerializer.toString(block.getLocation()));
                        }

                        BlockFace direction = isHead ? facing.getOppositeFace() : facing;
                        Block otherPart = block.getRelative(direction);

                        if (isHead) {
                            map.setRedBedBottom(LocationSerializer.toString(otherPart.getLocation()));
                        } else {
                            map.setRedBedTop(LocationSerializer.toString(otherPart.getLocation()));
                        }
                    } else {
                        if (isHead) {
                            map.setBlueBedTop(LocationSerializer.toString(block.getLocation()));
                        } else {
                            map.setBlueBedBottom(LocationSerializer.toString(block.getLocation()));
                        }

                        BlockFace direction = isHead ? facing.getOppositeFace() : facing;
                        Block otherPart = block.getRelative(direction);

                        if (isHead) {
                            map.setBlueBedBottom(LocationSerializer.toString(otherPart.getLocation()));
                        } else {
                            map.setBlueBedTop(LocationSerializer.toString(otherPart.getLocation()));
                        }
                    }

                    player.sendMessage(this.mm.deserialize(this.prefix + "Du hast das Bed erfolgreich für das Team " + this.color + team + "</gradient> gesetzt."));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.3F, 1.0F);
                }
            }

            case "remove" -> {
                if (args.length != 2) {
                    player.sendMessage(this.mm.deserialize(this.prefix + "Verwendung: " + this.color + "/map remove <Map-Name></gradient>"));
                    return true;
                }

                String mapName = args[1];
                if (this.mapHandler.getMap(mapName) == null) {
                    player.sendMessage(this.mm.deserialize(this.prefix + "<red>Es existiert keine Map mit dem Namen " + mapName + "."));
                    return true;
                }

                this.mapHandler.removeMap(mapName);
                player.sendMessage(this.mm.deserialize(this.prefix + "Du hast die Map " + this.color + mapName + "</gradient> erfolgreich gelöscht."));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.3F, 1.0F);
            }

            case "save" -> {
                if (args.length != 2) {
                    player.sendMessage(this.mm.deserialize(this.prefix + "Verwendung: " + this.color + "/map save <Map-Name></gradient>"));
                    return true;
                }

                String mapName = args[1];
                MapData mapData = this.mapHandler.getMap(mapName);
                if (mapData == null) {
                    player.sendMessage(this.mm.deserialize(this.prefix + "<red>Es existiert keine Map mit dem Namen " + mapName + "."));
                    return true;
                }

                if (mapData.getRedSpawn() == null) {
                    player.sendMessage(this.mm.deserialize(this.prefix + "<red>Die Map kann nicht gespeichert werden da der Spawn für das Rote Team noch nicht gesetzt worden ist!"));
                    return true;
                }

                if (mapData.getBlueSpawn() == null) {
                    player.sendMessage(this.mm.deserialize(this.prefix + "<red>Die Map kann nicht gespeichert werden da der Spawn für das Blaue Team noch nicht gesetzt worden ist!"));
                    return true;
                }

                if (mapData.getDeathHeight() == null) {
                    player.sendMessage(this.mm.deserialize(this.prefix + "<red>Die Map kann nicht gespeichert werden da die Todeshöhe noch nicht gesetzt worden ist!"));
                    return true;
                }

                if (mapData.getRedBedTop() == null && mapData.getRedBedBottom() == null) {
                    player.sendMessage(this.mm.deserialize(this.prefix + "<red>Die Map kann nicht gespeichert werden da das Bed für das Rote Team noch nicht gesetzt worden ist!"));
                    return true;
                }

                if (mapData.getBlueBedTop() == null && mapData.getBlueBedBottom() == null) {
                    player.sendMessage(this.mm.deserialize(this.prefix + "<red>Die Map kann nicht gespeichert werden da das Bed für das Blaue Team noch nicht gesetzt worden ist!"));
                    return true;
                }

                this.mapHandler.storeMap(mapName, mapData);
                player.sendMessage(this.mm.deserialize(this.prefix + "Du hast die Map " + this.color + mapName + "</gradient> erfolgreich gespeichert."));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.3F, 1.0F);
            }

            default -> sendHelp(player);
        }
        return false;
    }

    private void sendHelp(Player player) {
        player.sendMessage(this.mm.deserialize(this.prefix + "Verwendung: " + this.color + "/map create <Map-Name></gradient>"));
        player.sendMessage(this.mm.deserialize(this.prefix + "Verwendung: " + this.color + "/map setspawn <Map-Name> <Rot, Blau></gradient>"));
        player.sendMessage(this.mm.deserialize(this.prefix + "Verwendung: " + this.color + "/map setdeathheight <Map-Name></gradient>"));
        player.sendMessage(this.mm.deserialize(this.prefix + "Verwendung: " + this.color + "/map setbed <Map-Name> <Rot, Blau></gradient>"));
        player.sendMessage(this.mm.deserialize(this.prefix + "Verwendung: " + this.color + "/map save <Map-Name></gradient>"));
        player.sendMessage(this.mm.deserialize(this.prefix + "Verwendung: " + this.color + "/map remove <Map-Name></gradient>"));
    }
}
