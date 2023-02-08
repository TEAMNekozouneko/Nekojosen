package com.nekozouneko.nekojosen.gui;

import com.nekozouneko.nekojosen.Nekojosen;
import com.nekozouneko.nekojosen.util.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public final class MainDashboard {

    public static void open(Player p) {
        Nekojosen plugin = Nekojosen.getInstance();
        Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, "攻城戦ダッシュボード [DASHBOARD]");

        ItemStack map = new ItemStackBuilder(Material.FILLED_MAP)
                .name("§7マップ")
                .lore("§7マップの管理を行います。")
                .bukkitData(plugin.newKey("action"), PersistentDataType.STRING, "open_gui")
                .bukkitData(plugin.newKey("value"), PersistentDataType.STRING, "map")
                .build();
        ItemStack settings = new ItemStackBuilder(Material.TRIPWIRE_HOOK)
                .name("§7設定")
                .lore("§7ゲームの詳しい設定を変更、確認します。")
                .bukkitData(plugin.newKey("action"), PersistentDataType.STRING, "open_gui")
                .bukkitData(plugin.newKey("value"), PersistentDataType.STRING, "settings")
                .build();
        ItemStack game = new ItemStackBuilder(Material.DROPPER)
                .name("§7ゲーム")
                .lore("§7ゲームの開始やチームの振り分けを行います。")
                .bukkitData(plugin.newKey("action"), PersistentDataType.STRING, "open_gui")
                .bukkitData(plugin.newKey("value"), PersistentDataType.STRING, "game")
                .build();

        inv.setItem(1, game);
        inv.setItem(2, settings);
        inv.setItem(3, map);
        p.openInventory(inv);
    }

    public static boolean isHandleable(InventoryClickEvent e) {
        Bukkit.broadcastMessage(e.getView().getTitle());
        return (e.getView().getTitle().matches("^.+ \\[DASHBOARD]$"));
    }

    public static void handle(InventoryClickEvent e) {
        Player p = ((Player) e.getWhoClicked());
        ItemStack item = e.getCurrentItem();

        e.setCancelled(true);

        if (item == null || item.getType().isAir() || e.getRawSlot() > e.getInventory().getSize()) return;

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();

        NamespacedKey act = Nekojosen.getInstance().newKey("action");
        NamespacedKey val = Nekojosen.getInstance().newKey("value");

        if (pdc.has(act, PersistentDataType.STRING) && pdc.has(val, PersistentDataType.STRING)) {
            String av = pdc.get(act, PersistentDataType.STRING);
            String vv = pdc.get(val, PersistentDataType.STRING);
            if (av != null && vv != null && av.equals("open_gui")) {
                switch (vv) {
                    case "game":
                        break;
                    case "settings":
                        GameSettings.open(p);
                        break;
                    case "map":
                        break;
                }
            }
        }
    }
}
