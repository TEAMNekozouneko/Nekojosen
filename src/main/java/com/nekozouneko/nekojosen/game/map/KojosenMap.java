package com.nekozouneko.nekojosen.game.map;

import com.google.common.base.Preconditions;
import com.nekozouneko.nekojosen.Util;
import com.nekozouneko.nekojosen.game.KojosenGame;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class KojosenMap {

    private final String world;
    private final String id;
    private String name;
    private final Map<String, BlockVector> beacons = new HashMap<>();
    private final Map<String, Vector> spawns = new HashMap<>();

    public KojosenMap(String world, String name) {
        Preconditions.checkArgument(world != null, "World cannot be null.");
        Preconditions.checkArgument(name != null, "Map name cannot be null.");
        Preconditions.checkArgument(Bukkit.getWorld(world) != null, "World is not exists.");

        this.world = world;
        this.id = String.format("%07x", new Random().nextInt(Integer.MAX_VALUE)+1).toUpperCase();
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getWorld() {
        return world;
    }

    public Map<String, BlockVector> getBeacons() {
        return beacons;
    }

    public BlockVector getBeacon(@NotNull KojosenGame.GTeam team) {
        return beacons.get(team.name());
    }

    public Map<String, Vector> getSpawns() {
        return spawns;
    }

    public Vector getSpawn(KojosenGame.GTeam team) {
        return spawns.get(team.name());
    }

    public Vector getSpawnOrWorldSpawn(KojosenGame.GTeam team) {
        World w = Bukkit.getWorld(world);
        return spawns.getOrDefault(team.name(), Util.asVector(w.getSpawnLocation()));
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
