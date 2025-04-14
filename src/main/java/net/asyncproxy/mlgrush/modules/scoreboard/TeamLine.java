package net.asyncproxy.mlgrush.modules.scoreboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamLine {
    private final String teamName;
    private final String entry;

    public TeamLine(String teamName, String entry) {
        this.teamName = teamName;
        this.entry = entry;
    }
}
