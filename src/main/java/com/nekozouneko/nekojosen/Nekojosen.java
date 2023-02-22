package com.nekozouneko.nekojosen;

import com.nekozouneko.nekojosen.command.JoinCommand;
import com.nekozouneko.nekojosen.command.LeaveCommand;
import com.nekozouneko.nekojosen.command.NekojosenCommand;
import com.nekozouneko.nekojosen.game.KitFile;
import com.nekozouneko.nekojosen.game.KojosenGame;
import com.nekozouneko.nekojosen.game.LobbyFile;
import com.nekozouneko.nekojosen.game.map.MapManager;
import com.nekozouneko.nekojosen.listener.*;
import com.nekozouneko.nekojosen.listener.action.BlockAction;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public final class Nekojosen extends JavaPlugin {

    private static Nekojosen instance;

    private Scoreboard sb;
    private KojosenGame game;
    private File mapDir;

    private MapManager mapMan;
    private LobbyFile lobby;
    private File lobbyPath;
    private KitFile kit;
    private File kitPath;

    public static Nekojosen getInstance() {
        return instance;
    }

    public NamespacedKey newKey(String name) {
        return new NamespacedKey(this, name);
    }

    public KojosenGame getGame() {
        return game;
    }

    public Scoreboard getSb() {
        return sb;
    }

    public File getMapDir() {
        return mapDir;
    }

    public MapManager getMapManager() {
        return mapMan;
    }

    public LobbyFile getLobby() {
        return lobby;
    }

    public File getLobbyPath() {
        return lobbyPath;
    }

    public KitFile getKit() {
        return kit;
    }

    public File getKitPath() {
        return kitPath;
    }

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) getDataFolder().mkdir();

        mapDir = new File(getDataFolder(), "maps");
        if (!mapDir.exists()) mapDir.mkdir();

        try {
            lobbyPath = new File(getDataFolder(), "lobby.json");
            kitPath = new File(getDataFolder(), "kit.json");
            lobby = LobbyFile.load(lobbyPath);
            kit = KitFile.load(kitPath);
        }
        catch (IOException e) { e.printStackTrace(); }

        instance = this;
        sb = Bukkit.getScoreboardManager().getNewScoreboard();
        game = new KojosenGame(this, String.format("%07x", new Random().nextInt(Integer.MAX_VALUE)+1));
        mapMan = new MapManager(this);

        getCommand("nekojosen").setExecutor(new NekojosenCommand());
        getCommand("join").setExecutor(new JoinCommand());
        getCommand("leave").setExecutor(new LeaveCommand());

        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);

        BlockAction.actions.clear();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
