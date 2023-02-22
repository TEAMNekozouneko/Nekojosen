package com.nekozouneko.nekojosen.gui;

import com.nekozouneko.nekojosen.Nekojosen;
import com.nekozouneko.nekojosen.Util;
import com.nekozouneko.nekojosen.gui.map.MapList;
import com.nekozouneko.nekojosen.gui.map.WorldList;
import com.nekozouneko.nekojosen.util.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class MapDashboard {

    public static void open(Player p) {
        final Nekojosen plugin = Nekojosen.getInstance();
        Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, "マップダッシュボード [MDASHBOARD]");

        ItemStack add = new ItemStackBuilder(Material.LIME_WOOL)
                .name("§a作成")
                .lore("§7マップを作成します。")
                .bukkitData(plugin.newKey("map_action"), PersistentDataType.STRING, "create")
                .build();

        ItemStack delete = new ItemStackBuilder(Material.RED_WOOL)
                .name("§c削除")
                .lore("§7マップを削除します。")
                .bukkitData(plugin.newKey("map_action"), PersistentDataType.STRING, "delete")
                .build();

        ItemStack edit = new ItemStackBuilder(Material.WOODEN_AXE)
                .name("§6編集")
                .bukkitData(plugin.newKey("map_action"), PersistentDataType.STRING, "edit")
                .build();

        int ms = plugin.getMapManager().getMaps().size();
        ms = ms == 0 ? 1 : ms;
        ItemStack list = new ItemStackBuilder(Material.BARREL)
                .name("§7マップ一覧")
                .lore("§7マップの一覧を見ます。")
                .lore("§7現在、§f" + plugin.getMapManager().getMaps().size() + "§7個のマップがロードされています。")
                .amount(ms)
                .bukkitData(plugin.newKey("map_action"), PersistentDataType.STRING, "list")
                .build();

        inv.setItem(0, add);
        inv.setItem(1, delete);
        inv.setItem(2, edit);
        inv.setItem(4, list);

        p.openInventory(inv);
    }

    public static boolean isHandleable(InventoryClickEvent e) {
        return e.getView().getTitle().matches("^.+ \\[MDASHBOARD]$") && e.getInventory().getType() == InventoryType.HOPPER;
    }

    public static void handle(InventoryClickEvent e) {
        e.setCancelled(true);

        final ItemStack item = e.getCurrentItem();
        final Player p = ((Player) e.getWhoClicked());
        final Nekojosen plugin = Nekojosen.getInstance();

        if (item == null || item.getType().isAir()) return;


        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if (pdc.has(plugin.newKey("map_action"), PersistentDataType.STRING)) {
            switch (Util.orElse(pdc.get(plugin.newKey("map_action"), PersistentDataType.STRING), "")) {
                case "create":
                    new WorldList(Nekojosen.getInstance(), p, 1, WorldList.CloseAction.CREATE_MAP).open();
                    break;
                case "delete":
                    new MapList(plugin, p, 1, MapList.CloseAction.DELETE_MAP).open();
                    break;
                case "edit":
                    new MapList(plugin, p, 1, MapList.CloseAction.EDIT_MAP).open();
                    break;
                case "list":
                    new MapList(plugin, p, 1, null).open();
                    break;
            }
        }
    }


}
