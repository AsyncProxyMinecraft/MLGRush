package net.asyncproxy.mlgrush.modules.config;

import com.google.gson.*;
import net.asyncproxy.mlgrush.MLGRush;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class DatabaseFileHandler {
    private JsonObject config;
    private final File directory = new File("plugins/MLGRush");
    private final File file = new File("plugins/MLGRush", "database.json");

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    public DatabaseFileHandler() {
        this.config = new JsonObject();
        loadConfig();
    }

    public void createDefaultFile() {
        if (!directory.exists()) {
            directory.mkdir();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException exception) {
                MLGRush.getInstance().getLogger().warning("Could not create config file");
                return;
            }

            this.config.addProperty("host", "localhost");
            this.config.addProperty("port", 3306);
            this.config.addProperty("username", "root");
            this.config.addProperty("password", "password");
            this.config.addProperty("database", "database");

            saveConfig();
        }
    }

    public String getHost() {
        return this.config.get("host").getAsString();
    }

    public int getPort() {
        return this.config.get("port").getAsInt();
    }

    public String getUsername() {
        return this.config.get("username").getAsString();
    }

    public String getPassword() {
        return this.config.get("password").getAsString();
    }

    public String getDatabase() {
        return this.config.get("database").getAsString();
    }

    public void saveConfig() {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            gson.toJson(config, writer);
        } catch (IOException exception) {
            MLGRush.getInstance().getLogger().warning("Could not save config file");
        }
    }

    public void loadConfig() {
        if (!file.exists()) {
            createDefaultFile();
            return;
        }

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            this.config = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException | JsonSyntaxException exception) {
            MLGRush.getInstance().getLogger().warning("Could not load config file");
        }
    }
}
