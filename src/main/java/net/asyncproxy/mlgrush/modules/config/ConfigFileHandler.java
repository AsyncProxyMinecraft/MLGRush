package net.asyncproxy.mlgrush.modules.config;

import com.google.gson.*;
import net.asyncproxy.mlgrush.MLGRush;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigFileHandler {
    private JsonObject config;
    private final File directory = new File("plugins/MLGRush");
    private final File file = new File(directory, "config.json");

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    public ConfigFileHandler() {
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
                throw new RuntimeException("Could not create Config File: ", exception);
            }

            this.config.addProperty("prefix", "<gradient:#ffaa00:#ffff55>MLGRush</gradient> <dark_gray>| <gray>");
            this.config.addProperty("color", "<gradient:#ffaa00:#ffff55>");

            MLGRush.getInstance().setPrefix(this.config.get("prefix").getAsString());
            MLGRush.getInstance().setColor(this.config.get("color").getAsString());

            saveConfig();
        }
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

            MLGRush.getInstance().setPrefix(this.config.get("prefix").getAsString());
            MLGRush.getInstance().setColor(this.config.get("color").getAsString());
        } catch (IOException | JsonSyntaxException exception) {
            MLGRush.getInstance().getLogger().warning("Could not load config file");
        }
    }
}
