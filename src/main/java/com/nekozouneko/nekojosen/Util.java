package com.nekozouneko.nekojosen;

import com.google.common.collect.Lists;
import com.nekozouneko.nekojosen.game.KojosenGame;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public final class Util {

    public static String toTimer(long s) {
        long minutes = s / 60;
        long seconds = s - minutes * 60;
        return (String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
    }

    public static Material toBoolMaterial(boolean bool) {
        return (bool ? Material.LIME_WOOL : Material.RED_WOOL);
    }

    public static boolean isNeg(int numb) {
        return numb <= 0;
    }

    public static Location asLoc(Vector vec, World w) {
        return new Location(w, vec.getX(), vec.getY(), vec.getZ());
    }

    public static BlockVector asVector(Block block) {
        return new BlockVector(block.getX(), block.getY(), block.getZ());
    }

    public static Vector asVector(Location loc) {
        return new Vector(loc.getX(), loc.getY(), loc.getZ());
    }

    public static KojosenGame.GTeam randomize(Map<UUID, KojosenGame.GTeam> players) {
        if (players.size() == 0) {
            int i = new Random().nextInt(2);
            switch (i) {
                case 0:
                    return KojosenGame.GTeam.BLUE;
                case 1:
                    return KojosenGame.GTeam.RED;
            }
            return null;
        }
        Map<KojosenGame.GTeam, Integer> c = new HashMap<>();

        players.forEach((k, v) -> {
            if (v != KojosenGame.GTeam.NOT_HAS_TEAM) {
                c.put(v, c.getOrDefault(v, 0)+1);
            }
        });

        KojosenGame.GTeam minKey = null;

        for (Map.Entry<KojosenGame.GTeam, Integer> e : c.entrySet()) {
            if (minKey == null || c.get(minKey) > e.getValue()) minKey = e.getKey();
        }

        return minKey;
    }

    public static <T> T orElse(T obj, T def) {
        if (obj == null) return def;
        return obj;
    }

    public static <T> T randomFromCollection(Collection<T> collec) {
        T[] arr = (T[]) collec.toArray();
        return arr[new Random().nextInt(arr.length)];
    }

    public static void safeDeleteIfExists(Path path) throws IOException {
        if (!path.toFile().exists()) return;

        if (!path.toFile().isDirectory()) Files.deleteIfExists(path);
        else {
            try (Stream<Path> ps = Files.list(path)) {
                for (Path p : streamToList(ps)) safeDeleteIfExists(p);
            }
        }
    }

    public static <T> List<T> streamToList(Stream<T> stream) {
        List<T> l = Lists.newArrayList();
        stream.forEach(l::add);

        return l;
    }

    public static void leaveTeam(Player p) {
        Team t = p.getScoreboard().getEntryTeam(p.getName());
        if (t != null) t.removeEntry(p.getName());
    }

    public static void heal(Player p) {
        AttributeInstance ai = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        p.setHealth(ai != null ? ai.getValue() : 20.0);
        p.setSaturation(20);
        p.setFoodLevel(20);
    }

    @SafeVarargs
    public static <K, V> boolean containsKeys(Map<K, V> map, K... objs) {
        if (map.isEmpty() || objs.length == 0) return false;
        boolean b = true;

        for (K obj : objs) b = b && map.containsKey(obj);

        return b;
    }

    public static List<String> toPlayerNames(Collection<? extends OfflinePlayer> players) {
        List<String> res = new ArrayList<>();
        players.forEach((p) -> res.add(p.getName()));

        return res;
    }

}
