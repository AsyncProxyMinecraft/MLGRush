package net.asyncproxy.mlgrush.modules.game;

import lombok.Getter;
import org.bukkit.entity.Player;

public class GameHandler {

    @Getter
    private GameState gameState;

    public GameHandler() {
        this.gameState = GameState.LOBBY;
    }

    public void startGame() {
        this.gameState = GameState.RUNNING;
    }

    public void endGame() {
        this.gameState = GameState.RUNNING;
    }
}
