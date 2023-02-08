package com.nekozouneko.nekojosen;

import com.nekozouneko.nekojosen.game.KojosenGame;
import org.bukkit.Location;
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

    public KojosenMap(World world, String name) {
        this.world = world.getName();
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

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
