package com.nekozouneko.nekojosen.listener;

import com.google.common.base.Preconditions;
import com.nekozouneko.nekojosen.Nekojosen;
import com.nekozouneko.nekojosen.Util;
import com.nekozouneko.nekojosen.game.KojosenGame;
import com.nekozouneko.nekojosen.game.KojosenSettings;
import com.nekozouneko.nekojosen.gui.GameSettings;
import com.nekozouneko.nekojosen.listener.action.BlockAction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.BlockVector;

public final class BlockBreakListener implements Listener {

    private final Nekojosen plugin = Nekojosen.getInstance();

    @EventHandler
    public void onEvent(BlockBreakEvent e) {
        if (plugin.getGame() != null && plugin.getGame().isJoined(e.getPlayer().getUniqueId())) {
            final KojosenGame game = plugin.getGame();
            final KojosenSettings sts = game.getSettings();

            if (!e.getPlayer().getWorld().equals(game.getCopy())) return;

            if (sts.getMap().getBeacons().containsValue(Util.asVector(e.getBlock()))) {
                KojosenGame.GTeam tar = null;
                for (String k : sts.getMap().getBeacons().keySet()) {
                    try {
                        KojosenGame.GTeam t = KojosenGame.GTeam.valueOf(k);
                        BlockVector v = sts.getMap().getBeacon(t);
                        if (v.equals(Util.asVector(e.getBlock()))) tar = t;
                    } catch (IllegalArgumentException e1) {
                        e1.printStackTrace();
                    }
                }

                if (tar == null) return; // 勘違いだったのか...

                KojosenGame.GTeam gt = game.getPlayers().get(e.getPlayer().getUniqueId());

                if (gt != tar) {
                    // 勝利処理
                }
                else {
                    e.getPlayer().sendMessage("§c自陣のビーコンは破壊できません。");
                    e.setCancelled(true);
                }
            }
        }

        if (BlockAction.actions.containsKey(e.getPlayer())) {
            BlockAction act = BlockAction.actions.get(e.getPlayer());
            if (act.getType() == BlockAction.Type.BREAK) {
                act.onAction(e.getPlayer(), e);
                BlockAction.actions.remove(e.getPlayer());
            }
        }

    }

}
