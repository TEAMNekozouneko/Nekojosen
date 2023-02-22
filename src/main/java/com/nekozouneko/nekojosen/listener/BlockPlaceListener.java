package com.nekozouneko.nekojosen.listener;

import com.nekozouneko.nekojosen.listener.action.BlockAction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public final class BlockPlaceListener implements Listener {

    @EventHandler
    public void onEvent(BlockPlaceEvent e) {
        if (BlockAction.actions.containsKey(e.getPlayer())) {
            BlockAction act = BlockAction.actions.get(e.getPlayer());
            if (act.getType() == BlockAction.Type.PLACE) {
                act.onAction(e.getPlayer(), e);
                BlockAction.actions.remove(e.getPlayer());
            }
        }
    }

}
