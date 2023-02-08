package com.nekozouneko.nekojosen;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

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

}
