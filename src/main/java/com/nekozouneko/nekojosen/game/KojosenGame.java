package com.nekozouneko.nekojosen.game;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.nekozouneko.nekojosen.Nekojosen;
import com.nekozouneko.nekojosen.Util;
import com.nekozouneko.nekojosen.game.map.KojosenMap;
import com.nekozouneko.nekojosen.task.GameCountdownTask;
import com.nekozouneko.nekojosen.util.WorldCopyVisitor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.BiConsumer;

public class KojosenGame {

    public enum GTeam {
        NOT_HAS_TEAM, RED, BLUE;
    }

    private final String id;
    private final Nekojosen plugin;
    private final Map<GTeam, Team> teams = new HashMap<>();
    private final Map<UUID, GTeam> players = new HashMap<>();
    private final KojosenSettings settings = new KojosenSettings();

    private KojosenState status = KojosenState.WAITING;
    private BukkitRunnable countdownTask = null;

    private final Map<GTeam, UUID> general = new HashMap<>(); // お前が大将 (大将戦のみ使用)
    private final Map<GTeam, Integer> tickets = new HashMap<>(); // チケット
    private KojosenMap map = null;
    private World copy = null;

    public KojosenGame(Nekojosen plugin, String id) {
        this.id = id;
        this.plugin = plugin;

        Team red = plugin.getSb().registerNewTeam("red");
        Team blue = plugin.getSb().registerNewTeam("blue");

        red.setColor(ChatColor.RED);
        red.setAllowFriendlyFire(false);
        red.setCanSeeFriendlyInvisibles(true);

        blue.setColor(ChatColor.BLUE);
        blue.setAllowFriendlyFire(false);
        blue.setCanSeeFriendlyInvisibles(true);

        teams.put(GTeam.BLUE, blue);
        teams.put(GTeam.RED, red);
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public KojosenSettings getSettings() {
        return settings;
    }

    // ----

    public void join(UUID player) {
        if (!players.containsKey(player)) {
            GTeam gt = Util.randomize(players);
            players.put(player, gt);
            Player p = Bukkit.getPlayer(player);
            if (p != null) {
                p.setScoreboard(plugin.getSb());
                teams.get(gt).addEntry(p.getName());
            }
        }
    }

    public void leave(UUID player) {
        players.remove(player);
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            teams.values().forEach((t) -> t.removeEntry(p.getName()));
            p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    public void setTeam(UUID player, GTeam team) {
        players.put(player, team);

        Team t = teams.get(team);
        Player p = Bukkit.getPlayer(player);

        if (t != null && p != null) {
            t.addEntry(p.getName());
        }
        else if (p != null) Util.leaveTeam(p);
    }

    @NotNull
    public Map<UUID, GTeam> getPlayers() {
        return players;
    }

    public Set<UUID> getPlayers(GTeam team) {
        Set<UUID> set = new HashSet<>();

        players.forEach((k, v) -> {
            if (team == v) set.add(k);
        });

        return set;
    }

    public void forEachPlayers(BiConsumer<Player, GTeam> func) {
        players.forEach((id, team) -> {
            Player p = Bukkit.getPlayer(id);
            if (p == null) return;

            func.accept(p, team);
        });
    }

    public boolean isJoined(UUID player) {
        return players.containsKey(player);
    }

    // game

    public void startCountdown() {
        if (status == KojosenState.WAITING) {
            countdownTask = new GameCountdownTask(this, 5);
            countdownTask.runTaskTimer(plugin, 0, 20);
            setStatus(KojosenState.COUNTDOWN);
        }
    }

    public void cancelCountdown() {
        if (countdownTask != null && !countdownTask.isCancelled()) {
            countdownTask.cancel();
            countdownTask = null;
            setStatus(KojosenState.WAITING);
        }
    }

    public boolean canStart() {
        if (!(players.size() >= 2)) return false;
        if (settings.getMap() == null && plugin.getMapManager().getMaps().isEmpty()) return false;
        if (!status.isWaiting()) return false;

        return true;
    }

    public void start() {
        if (canStart()) {
            cancelCountdown();
            setStatus(KojosenState.PREP_TIME);

            try {
                // マップの選択
                if (settings.getMap() == null) {
                    map = Util.randomFromCollection(plugin.getMapManager().getMaps().values());
                } else map = settings.getMap();

                Preconditions.checkState(map != null);
                Preconditions.checkState(map.getWorld() != null || map.getWorld().isEmpty());

                World w = Bukkit.getWorld(map.getWorld());

                Preconditions.checkState(w != null);

                if (!Util.containsKeys(map.getSpawns(),
                        GTeam.RED.name(), GTeam.BLUE.name(), GTeam.NOT_HAS_TEAM.name())
                ) {
                    throw new IllegalStateException("マップのスポーン設定が不足しています。");
                }

                // マップのコピー
                final String wn = "./" + id + "_" + map.getId();
                Files.walkFileTree(w.getWorldFolder().toPath(), new WorldCopyVisitor(w, Paths.get(wn)));

                WorldCreator.name(wn).createWorld();

                // 設定確認する
                if (settings.isEnabledTickets()) {
                    tickets.put(GTeam.RED, settings.getTickets());
                    tickets.put(GTeam.BLUE, settings.getTickets());
                }
                else {
                    tickets.put(GTeam.RED, null);
                    tickets.put(GTeam.BLUE, null);
                }

                // モードごとの設定
                if (settings.getMode() == KojosenMode.DESTROY_BEACON) {
                    if (!Util.containsKeys(map.getBeacons(), GTeam.RED.name(), GTeam.BLUE.name())) {
                        throw new IllegalStateException("マップのビーコン設定が不十分です。");
                    }
                    else {
                        map.getBeacons().forEach((t1, b) -> {
                            try {
                                GTeam.valueOf(t1);
                                Util.asLoc(b, copy).getBlock().setType(Material.BEACON);
                            }
                            catch (IllegalArgumentException e) { return; }
                        });
                    }
                }
                else if (settings.getMode() == KojosenMode.GENERAL_BATTLE) {
                    Random r = new Random();
                    List<UUID> rps = Lists.newArrayList(getPlayers(GTeam.RED));
                    List<UUID> bps = Lists.newArrayList(getPlayers(GTeam.BLUE));

                    general.put(GTeam.RED, rps.get(r.nextInt(rps.size())));
                    general.put(GTeam.BLUE, bps.get(r.nextInt(bps.size())));
                }

                forEachPlayers((p, t) -> {
                    Util.heal(p);

                    Location sp = Util.asLoc(map.getSpawnOrWorldSpawn(t), copy);
                    p.teleport(sp);
                });
            }
            catch (Throwable t) /* エラーで跳ね返す おまえ（nekozouneko）の部屋 */ {
                players.keySet().forEach((id) -> {
                    Player p = Bukkit.getPlayer(id);
                    if (p == null) return;

                    p.sendMessage("エラーによりゲームを開始できませんでした。");
                    if (p.isOp()) p.sendMessage(t.getMessage());
                    end();
                });
            }
        }
        else end();
    }

    public void end() {
        // 一旦全員を退出扱いにする
        teams.values().forEach((t) -> {
            t.getEntries().forEach(t::removeEntry);
        });
        players.clear();

        // 再度チームをランダムに振り分ける
        Bukkit.getOnlinePlayers().forEach((p) -> join(p.getUniqueId()));

        // 大将とチケットなどをリセット
        tickets.clear();
        general.clear();

        // マップをリセット
        map = null;
        if (copy != null) {
            Bukkit.unloadWorld(copy, false);

            try {
                Util.safeDeleteIfExists(copy.getWorldFolder().toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            copy = null;
        }

        // 後始末完了
        status = KojosenState.WAITING;
    }

    // map

    public World getCopy() {
        return copy;
    }

    private void copyMap() {
        
    }

    // other

    public void setStatus(KojosenState status) {
        this.status = status;
    }

    public KojosenState getStatus() {
        return status;
    }

    // util

    public Team toSbTeam(GTeam team) {
        return teams.get(team);
    }

}
