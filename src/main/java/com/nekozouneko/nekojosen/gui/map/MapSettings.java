package com.nekozouneko.nekojosen.gui.map;

import com.google.common.base.Preconditions;
import com.nekozouneko.nekojosen.Nekojosen;
import com.nekozouneko.nekojosen.Util;
import com.nekozouneko.nekojosen.game.map.KojosenMap;
import com.nekozouneko.nekojosen.gui.TeamSelector;
import com.nekozouneko.nekojosen.listener.action.BlockAction;
import com.nekozouneko.nekojosen.util.ItemStackBuilder;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BlockVector;

import java.lang.reflect.Type;
import java.util.Collections;

public final class MapSettings implements InventoryHolder, Listener {

    private final Nekojosen plugin;
    private final Player player;
    private final Inventory inv;
    private final KojosenMap map;

    public MapSettings(Nekojosen plugin, Player player, KojosenMap map) {
        Preconditions.checkArgument(plugin != null);
        Preconditions.checkArgument(player != null);
        Preconditions.checkArgument(map != null);

        this.plugin = plugin;
        this.player = player;
        this.inv = Bukkit.createInventory(this, InventoryType.HOPPER, "マップ設定 - " + map.getName());
        this.map = map;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open() {
        player.openInventory(inv);
        update();
    }

    public void update() {
        inv.clear();

        ItemStack name = new ItemStackBuilder(Material.NAME_TAG)
                .name("§fマップ名を変更")
                .bukkitData(plugin.newKey("action"), PersistentDataType.STRING, "rename")
                .build();

        ItemStack beacon = new ItemStackBuilder(Material.BEACON)
                .name("§bビーコンの位置を設定")
                .bukkitData(plugin.newKey("action"), PersistentDataType.STRING, "set-beacon")
                .build();

        ItemStack spawn = new ItemStackBuilder(Material.RED_BED)
                .name("§aスポーンする位置を設定")
                .bukkitData(plugin.newKey("action"), PersistentDataType.STRING, "set-spawn")
                .build();

        inv.setItem(0, name);
        inv.setItem(3, beacon);
        inv.setItem(4, spawn);
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() != this) return;

        final ItemStack item = e.getCurrentItem();
        final Player p = ((Player) e.getWhoClicked());

        if (item == null || item.getType().isAir()) return;
        
        e.setCancelled(true);

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        String key = pdc.getOrDefault(plugin.newKey("action"), PersistentDataType.STRING, "none");

        switch (key) {
            case "rename": {
                new AnvilGUI.Builder()
                        .plugin(plugin)
                        .title("マップ名を変更")
                        .text(map.getName())
                        .onComplete((c) -> {
                            if (c.getText().isEmpty())
                                return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(map.getName()));

                            map.setName(c.getText());
                            plugin.getMapManager().save(map);
                            return Collections.singletonList(AnvilGUI.ResponseAction.openInventory(inv));
                        })
                        .open(p);
                break;
            }
            case "set-beacon": {
                new TeamSelector(plugin, p, false, (t) -> {
                    BlockAction.actions.put(p, new BlockAction((pl, l) -> {
                        map.getBeacons().put(t.name(), new BlockVector(Util.asVector(l)));
                        plugin.getMapManager().save(map);
                    }, BlockAction.Type.BREAK));
                }).open();
                break;
            }
            case "set-spawn": {
                new TeamSelector(plugin, p, false, (t) -> {
                    BlockAction.actions.put(p, new BlockAction((pl, l) -> {
                        map.getSpawns().put(t.name(), Util.asVector(l));
                    }, BlockAction.Type.PLACE));
                    plugin.getMapManager().save(map);
                }).open();
                break;
            }
        }

        update();
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() != this) return;

        HandlerList.unregisterAll(this);
    }
}
