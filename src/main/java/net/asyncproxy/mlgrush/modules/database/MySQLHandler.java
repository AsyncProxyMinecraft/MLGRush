package net.asyncproxy.mlgrush.modules.database;

import net.asyncproxy.mlgrush.MLGRush;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Logger;

public class MySQLHandler implements IMySQLHandler {

    private Connection connection = null;

    private final Logger logger;

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    private final String url;

    public MySQLHandler(String host, int port, String username, String password, String database) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;

        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database;

        this.logger = MLGRush.getInstance().getLogger();
    }

    @Override
    public void connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            this.connection = DriverManager.getConnection(this.url, this.username, this.password);
            this.logger.info("Connected to MySQL!");
        } catch (ClassNotFoundException exception) {
            throw new SQLException("MySQL-Driver not found", exception);
        }
    }

    @Override
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            this.logger.info("Connection to MySQL has been closed!");
        }
    }

    @Override
    public void update(String query, Object... params) throws SQLException {
        ensureConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setParameters(preparedStatement, params);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public CompletableFuture<Void> updateAsync(String query, Object... params) {
        return CompletableFuture.runAsync(() -> {
           try {
               update(query, params);
           } catch (SQLException exception) {
               throw new RuntimeException("Error while updating MySQL", exception);
           }
        });
    }

    @Override
    public <T> List<T> query(String query, Function<ResultSet, T> resultHandler, Object... params) throws SQLException {
        ensureConnection();

        List<T> results = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setParameters(preparedStatement, params);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    results.add(resultHandler.apply(resultSet));
                }
            }
        }

        return results;
    }

    @Override
    public <T> CompletableFuture<List<T>> queryAsync(String query, Function<ResultSet, T> resultHandler, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
           try {
               return query(query, resultHandler, params);
           } catch (SQLException exception) {
               throw new RuntimeException("Error while querying MySQL", exception);
           }
        });
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            if (this.url != null && this.username != null && this.password != null) {
                this.connection = DriverManager.getConnection(this.url, this.username, this.password);
            } else {
                throw new SQLException("MySQL-Connection has not been initialized!");
            }
        }
    }

    private void setParameters(PreparedStatement preparedStatement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
    }
}
