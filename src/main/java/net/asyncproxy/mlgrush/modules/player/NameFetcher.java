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

public class NameFetcher {
    private static final String MINECRAFT_API_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final String XBOX_API_URL = "https://mcprofile.io/api/v1/bedrock/fuid/";
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static final ConcurrentHashMap<UUID, String> UUID_NAME_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, Boolean> BEDROCK_PLAYER_CACHE = new ConcurrentHashMap<>();
    private static final Gson GSON = new GsonBuilder().create();

    /**
     * Holt den Namen eines Spielers anhand seiner UUID.
     *
     * @param uuid Die UUID des Spielers
     * @return Der Spielername (mit vorangestelltem Punkt für Bedrock-Spieler)
     * @throws Exception Wenn ein Fehler auftritt
     */
    public static String getName(UUID uuid) throws Exception {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID darf nicht null sein");
        }

        // Prüfen, ob Name bereits im Cache ist
        if (UUID_NAME_CACHE.containsKey(uuid)) {
            String name = UUID_NAME_CACHE.get(uuid);
            boolean isBedrock = BEDROCK_PLAYER_CACHE.getOrDefault(uuid, false);
            return isBedrock ? "." + name : name;
        }

        // Versuchen, den Namen über die Java-API zu holen
        try {
            return fetchJavaName(uuid);
        } catch (Exception e) {
            // Wenn das fehlschlägt, versuchen wir es mit der Bedrock-API
            return fetchBedrockName(uuid);
        }
    }

    /**
     * Ruft asynchron den Namen eines Spielers anhand seiner UUID ab.
     *
     * @param uuid Die UUID des Spielers
     * @return Ein CompletableFuture mit dem Spielernamen
     */
    public static CompletableFuture<String> getNameAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getName(uuid);
            } catch (Exception e) {
                throw new RuntimeException("Fehler beim Abrufen des Namens für UUID " + uuid, e);
            }
        }, EXECUTOR);
    }

    private static String fetchJavaName(UUID uuid) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(MINECRAFT_API_URL + uuid.toString().replace("-", "")).openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
                String name = response.get("name").getAsString();

                UUID_NAME_CACHE.put(uuid, name);
                BEDROCK_PLAYER_CACHE.put(uuid, false);

                return name;
            }
        } else {
            throw new Exception("Konnte Namen für Java-Spieler nicht abrufen: HTTP " + connection.getResponseCode());
        }
    }

    private static String fetchBedrockName(UUID uuid) throws Exception {
        // Hier würde die eigentliche Implementierung für die Bedrock-API kommen
        // Dies ist ein Beispiel, wie es aussehen könnte:
        HttpURLConnection connection = (HttpURLConnection) new URL(XBOX_API_URL + uuid.toString()).openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
                String name = response.get("gamertag").getAsString();

                UUID_NAME_CACHE.put(uuid, name);
                BEDROCK_PLAYER_CACHE.put(uuid, true);

                return "." + name; // Wir geben den Namen mit einem Punkt zurück, um Bedrock-Spieler zu kennzeichnen
            }
        } else {
            throw new Exception("Konnte Namen für Bedrock-Spieler nicht abrufen: HTTP " + connection.getResponseCode());
        }
    }
}
