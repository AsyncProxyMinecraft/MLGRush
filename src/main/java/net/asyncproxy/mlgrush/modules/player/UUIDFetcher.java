package net.asyncproxy.mlgrush.modules.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class UUIDFetcher {
    private static final String MINECRAFT_API_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String XBOX_API_URL = "https://mcprofile.io/api/v1/bedrock/gamertag/";
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static final ConcurrentHashMap<String, UUID> NAME_UUID_CACHE = new ConcurrentHashMap<>();
    private static final Pattern BEDROCK_USERNAME_PATTERN = Pattern.compile("^\\.(.+)$");
    private static final Gson GSON = new GsonBuilder().create();

    /**
     * Holt die UUID eines Spielers, unterstützt sowohl Java- als auch Bedrock-Spieler.
     * Für Bedrock-Spieler muss der Name mit einem Punkt beginnen (.Spielername).
     *
     * @param name Der Spielername (mit Punkt für Bedrock-Spieler)
     * @return Die UUID des Spielers
     * @throws Exception Wenn ein Fehler auftritt
     */
    public static UUID getUUID(String name) throws Exception {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name darf nicht null oder leer sein");
        }

        // Prüfen, ob UUID bereits im Cache ist
        if (NAME_UUID_CACHE.containsKey(name)) {
            return NAME_UUID_CACHE.get(name);
        }

        // Bestimmen, ob es sich um einen Bedrock-Spieler handelt
        boolean isBedrock = name.startsWith(".");
        String actualName = isBedrock ? name.substring(1) : name;

        // API-Anfrage je nach Spielertyp
        if (isBedrock) {
            return fetchBedrockUUID(actualName);
        } else {
            return fetchJavaUUID(actualName);
        }
    }

    /**
     * Ruft asynchron die UUID eines Spielers ab.
     *
     * @param name Der Spielername (mit Punkt für Bedrock-Spieler)
     * @return Ein CompletableFuture mit der UUID des Spielers
     */
    public static CompletableFuture<UUID> getUUIDAsync(String name) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getUUID(name);
            } catch (Exception e) {
                throw new RuntimeException("Fehler beim Abrufen der UUID für " + name, e);
            }
        }, EXECUTOR);
    }

    private static UUID fetchJavaUUID(String name) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(MINECRAFT_API_URL + name).openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
                String id = response.get("id").getAsString();

                // Mojang API liefert UUIDs ohne Bindestriche
                UUID uuid = UUID.fromString(id.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
                NAME_UUID_CACHE.put(name, uuid);
                NAME_UUID_CACHE.put("." + name, uuid); // Cache auch für Bedrock-Format

                return uuid;
            }
        } else {
            throw new Exception("Konnte UUID für Java-Spieler nicht abrufen: HTTP " + connection.getResponseCode());
        }
    }

    private static UUID fetchBedrockUUID(String name) throws Exception {
        // Hier würde die eigentliche Implementierung für die Bedrock-API kommen
        // Dies ist ein Beispiel, wie es aussehen könnte:
        HttpURLConnection connection = (HttpURLConnection) new URL(XBOX_API_URL + name).openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
                String id = response.get("floodgateuid").getAsString();

                // Konvertieren der Bedrock-ID ins UUID-Format (dies ist ein Beispiel)
                // In der Praxis müsste dies an die tatsächliche API angepasst werden
                UUID uuid = UUID.fromString(id);
                NAME_UUID_CACHE.put("." + name, uuid);

                return uuid;
            }
        } else {
            throw new Exception("Konnte UUID für Bedrock-Spieler nicht abrufen: HTTP " + connection.getResponseCode());
        }
    }
}
