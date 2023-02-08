package com.nekozouneko.nekojosen.game;

import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KojosenGame {

    public enum GTeam {
        NOT_HAS_TEAM, RED, BLUE;
    }

    private final String id;
    private final Map<UUID, GTeam> players = new HashMap<>();
    private final KojosenSettings settings = new KojosenSettings();

    private Map<GTeam, UUID> general = new HashMap<>(); // お前が大将 (大将戦のみ使用)
    private Map<GTeam, Integer> tickets = new HashMap<>(); // チケット

    public KojosenGame(String id) {
        this.id = id;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public KojosenSettings getSettings() {
        return settings;
    }

    public void join(UUID player) {

    }

    public void leave(UUID player) {

    }

    @NotNull
    public Map<UUID, GTeam> getPlayers() {
        return Collections.unmodifiableMap(players);
    }

}
