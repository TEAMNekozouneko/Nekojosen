package com.nekozouneko.nekojosen.game.map;

import com.google.common.base.Preconditions;
import com.google.common.reflect.Reflection;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nekozouneko.nekojosen.Nekojosen;
import com.nekozouneko.nekojosen.Util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapManager {

    private final Map<String, KojosenMap> maps = new HashMap<>();
    private final Nekojosen plugin;

    public MapManager(Nekojosen plugin) {
        plugin.getLogger().info("New instance called.");
        this.plugin = plugin;

        try { loadAll(plugin.getMapDir()); }
        catch (IOException e) { e.printStackTrace(); }
    }

    public void load(File f) {
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)
                )
        ) {
            Gson gson = new Gson();

            KojosenMap m = gson.fromJson(reader, KojosenMap.class);
            maps.put(m.getId(), m);
            plugin.getLogger().info("Map loaded from " + f + ". : " + m);
        }
        catch (JsonSyntaxException e) {
            plugin.getLogger().warning(f.getName() + " is not valid file.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(KojosenMap map) {
        maps.put(map.getId(), map);
        plugin.getLogger().info("Map loaded from <memory> : " + map);
    }

    public void loadAll(File d) throws IOException {
        Preconditions.checkArgument(d != null);
        Preconditions.checkState(d.isDirectory(), "Argument 'd' is not directory.");

        for (File f : Util.orElse(d.listFiles(), new File[0])) {
            if (f.getName().endsWith(".json")) load(f);
        }
    }

    public Map<String, KojosenMap> getMaps() {
        return Collections.unmodifiableMap(maps);
    }

    public KojosenMap getMap(String id) {
        return maps.get(id);
    }

    public void unload(String id) {
        maps.remove(id);
    }

    public void delete(KojosenMap map) throws IOException {
        Preconditions.checkArgument(map != null);
        unload(map.getId());
        File f = new File(plugin.getMapDir(), map.getId() + ".json");
        Files.deleteIfExists(f.toPath());
    }

    public void save(KojosenMap map) {
        try (
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(new File(plugin.getMapDir(), map.getId() + ".json")),
                        StandardCharsets.UTF_8
                ))
        ) {
            Gson gson = new Gson();
            gson.toJson(map, KojosenMap.class, writer);

            writer.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
