package com.nekozouneko.nekojosen.listener;

import com.nekozouneko.nekojosen.Nekojosen;
import com.nekozouneko.nekojosen.game.KojosenGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onEvent(PlayerJoinEvent e) {
        KojosenGame game = Nekojosen.getInstance().getGame();
        if (game != null && game.getStatus().isWaiting()) {
            game.join(e.getPlayer().getUniqueId());
        }
    }

}
