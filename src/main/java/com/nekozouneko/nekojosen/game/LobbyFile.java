package com.nekozouneko.nekojosen.game;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.nekozouneko.nekojosen.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class LobbyFile {

    public static LobbyFile load(File path) throws IOException {
        Gson gson = new Gson();

        if (path.exists()) {
            try (
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            new FileInputStream(path), StandardCharsets.UTF_8
                    ))
            ) {
                return gson.fromJson(reader, LobbyFile.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new LobbyFile((String) null, null);
    }

    public static void save(LobbyFile data, File path) throws IOException {
        Gson gson = new Gson();

        try (
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(path), StandardCharsets.UTF_8
                ))
        ) {
            gson.toJson(data, LobbyFile.class, writer);

            writer.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String world;
    private Vector location;

    public LobbyFile(String world, Vector location) {
        this.world = world;
        this.location = location;
    }

    public LobbyFile(World world, Location location) {
        this(world.getName(), Util.asVector(location));
    }

    public void setLocation(Vector location) {
        this.location = location;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getWorld() {
        return world;
    }

    public Vector getLocation() {
        return location;
    }

    public void teleport(Player p) {
        Preconditions.checkState(location != null && world != null);

        p.teleport(Util.asLoc(location, Bukkit.getWorld(world)));
    }
}
