package com.nekozouneko.nekojosen.gui.map;

import com.nekozouneko.nekojosen.Nekojosen;
import com.nekozouneko.nekojosen.game.map.KojosenMap;
import com.nekozouneko.nekojosen.util.ItemStackBuilder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class MapList implements InventoryHolder, Listener {

    public enum CloseAction {
        DELETE_MAP,
        EDIT_MAP
    }

    private final Nekojosen plugin;
    private final Player target;
    private final Inventory inv = Bukkit.createInventory(this, 54, "マップを選択");
    private final CloseAction act;

    private int page;

    public MapList(Nekojosen plugin, Player target, int page, CloseAction act) {
        this.plugin = plugin;
        this.target = target;
        this.page = page;
        this.act = act;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    public void open() {
        target.openInventory(inv);
        update();
    }

    public void update() {
        inv.clear();

        boolean canPrev = page > 1;
        boolean canNext = plugin.getMapManager().getMaps().size() >= (45 * page);

        ItemStack prev = new ItemStackBuilder(canPrev ? Material.ARROW : Material.STICK)
                .name(ChatColor.GREEN + "前のページに戻る")
                .bukkitData(plugin.newKey("action"), PersistentDataType.STRING, canPrev ? "previous_page" : "none")
                .bukkitData(plugin.newKey("value"), PersistentDataType.INTEGER, page - 1)
                .build();

        ItemStack next = new ItemStackBuilder(canNext ? Material.ARROW : Material.STICK)
                .name(ChatColor.GREEN + "次のページに戻る")
                .bukkitData(plugin.newKey("action"), PersistentDataType.STRING, canNext ? "next_page" : "none")
                .bukkitData(plugin.newKey("value"), PersistentDataType.INTEGER, page + 1)
                .build();

        ItemStack back = new ItemStackBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .name(" ")
                .bukkitData(plugin.newKey("action"), PersistentDataType.STRING, "none")
                .build();

        inv.setItem(45, prev);
        inv.setItem(46, back);
        inv.setItem(47, back);
        inv.setItem(48, back);
        inv.setItem(49, back);
        inv.setItem(50, back);
        inv.setItem(51, back);
        inv.setItem(52, back);
        inv.setItem(53, next);

        List<String> keys = new ArrayList<>(plugin.getMapManager().getMaps().keySet());
        for (int i = 0; i < 45; i++) {
            if ( keys.size() <= (i + (45 * (page-1))) ) break;
            KojosenMap map = plugin.getMapManager().getMap(keys.get(i + (45 * (page-1))));

            ItemStack mi = new ItemStackBuilder(i % 2 == 0 ? Material.MAP : Material.FILLED_MAP)
                    .name("§f" + map.getName())
                    .lore("§7ID: " + map.getId())
                    .bukkitData(plugin.newKey("action"), PersistentDataType.STRING, "select-map")
                    .bukkitData(plugin.newKey("value"), PersistentDataType.STRING, map.getId())
                    .build();

            inv.setItem(i, mi);
        }
    }

    public void setPage(int page) {
        this.page = page;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() != this) return;

        e.setCancelled(true);

        final ItemStack item = e.getCurrentItem();
        final Player p = ((Player) e.getWhoClicked());

        if (item == null || item.getType().isAir() || !(e.getRawSlot() < 54)) return;

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();

        if (pdc.has(plugin.newKey("action"), PersistentDataType.STRING)) {
            switch (pdc.getOrDefault(plugin.newKey("action"), PersistentDataType.STRING, "none")) {
                case "previous_page": {
                    page--;
                    break;
                }
                case "next_page": {
                    page++;
                    break;
                }
                case "select-map": {
                    if (act == null) break;
                    final String val = pdc.get(plugin.newKey("value"), PersistentDataType.STRING);

                    switch (act) {
                        case DELETE_MAP: {
                            if (val != null) {
                                try {
                                    plugin.getMapManager().delete(plugin.getMapManager().getMap(val));
                                    p.sendMessage("§a削除に成功しました。");
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                    p.sendMessage("§c削除に失敗しました: ファイルが存在しないかサーバーを実行したユーザーの権限が不足しています。");
                                }
                                break;
                            }
                        }
                        case EDIT_MAP:
                            if (val != null) {
                                new MapSettings(plugin, p, plugin.getMapManager().getMap(val)).open();
                            }
                            break;
                    }
                    break;
                }
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
