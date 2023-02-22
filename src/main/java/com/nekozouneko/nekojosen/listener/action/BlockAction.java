package com.nekozouneko.nekojosen.listener.action;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class BlockAction {

    public enum Type {
        PLACE, BREAK
    }

    public static final Map<Player, BlockAction> actions = new HashMap<>();

    private final BiConsumer<Player, Location> close;
    private final Type type;

    public BlockAction(BiConsumer<Player, Location> close, Type type) {
        this.close = close;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void onAction(Player p, BlockEvent e) {
        close.accept(p, e.getBlock().getLocation());
    }

}
