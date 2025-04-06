package net.asyncproxy.mlgrush;

import lombok.Getter;
import net.asyncproxy.mlgrush.modules.database.IMySQLHandler;
import net.asyncproxy.mlgrush.modules.database.MySQLHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class MLGRush extends JavaPlugin {

    @Getter
    private static MLGRush instance;

    @Getter
    private IMySQLHandler mySQLHandler;

    @Override
    public void onEnable() {
        instance = this;

        this.mySQLHandler = new MySQLHandler("", 3306, "", "", "");
        try {
            this.mySQLHandler.connect();
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't establish MySQL-Connection!", e);
        }


    }

    @Override
    public void onDisable() {

    }
}
