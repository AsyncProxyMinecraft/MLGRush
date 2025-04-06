package net.asyncproxy.mlgrush.modules.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface IMySQLHandler {

    void connect() throws SQLException;

    void disconnect() throws SQLException;

    void update(String query, Object... params) throws SQLException;

    CompletableFuture<Void> updateAsync(String query, Object... params);

    <T> List<T> query(String query, Function<ResultSet, T> resultHandler, Object... params) throws SQLException;

    <T> CompletableFuture<List<T>> queryAsync(String query, Function<ResultSet, T> resultHandler, Object... params);

}
