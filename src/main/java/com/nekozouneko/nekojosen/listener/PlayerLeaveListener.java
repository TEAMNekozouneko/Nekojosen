package com.nekozouneko.nekojosen.listener;

import com.nekozouneko.nekojosen.Nekojosen;
import com.nekozouneko.nekojosen.game.KojosenGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    @EventHandler
    public void onEvent(PlayerQuitEvent e) {
        KojosenGame game = Nekojosen.getInstance().getGame();
        if (game != null && game.getStatus().isWaiting()) {
            game.leave(e.getPlayer().getUniqueId());
        }
    }

}
