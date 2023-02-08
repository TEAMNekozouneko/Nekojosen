package com.nekozouneko.nekojosen;

import com.nekozouneko.nekojosen.game.KojosenGame;
import com.nekozouneko.nekojosen.listener.InventoryClickListener;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class Nekojosen extends JavaPlugin {

    private static Nekojosen instance;
    private KojosenGame game = new KojosenGame(String.format("%07x", new Random().nextInt(Integer.MAX_VALUE)+1));

    public static Nekojosen getInstance() {
        return instance;
    }

    public NamespacedKey newKey(String name) {
        return new NamespacedKey(this, name);
    }

    public KojosenGame getGame() {
        return game;
    }

    @Override
    public void onEnable() {
        instance = this;

        getCommand("nekojosen").setExecutor(new NekojosenCommand());

        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
