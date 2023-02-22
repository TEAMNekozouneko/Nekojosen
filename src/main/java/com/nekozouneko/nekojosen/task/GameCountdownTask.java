package com.nekozouneko.nekojosen.task;

import com.nekozouneko.nekojosen.game.KojosenGame;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class GameCountdownTask extends BukkitRunnable {

    private final KojosenGame game;
    private int second;
    private boolean first = true;

    public GameCountdownTask(KojosenGame game, int time) {
        this.game = game;
        this.second = time;
    }

    @Override
    public void run() {
        if (first) first = false;
        else second--;

        if (second < 0) {
            game.start();
            cancel();
            return;
        }

        if (second <= 5 || (second % 10) == 0) {
            game.getPlayers().keySet().forEach((uuid) -> {
                Player p = Bukkit.getPlayer(uuid);
                if (p == null) return;

                p.sendTitle(Objects.toString(second), "", 5, 20, 5);
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
            });
        }
    }

}
