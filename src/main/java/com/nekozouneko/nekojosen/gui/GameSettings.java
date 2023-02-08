package com.nekozouneko.nekojosen.gui;

import com.nekozouneko.nekojosen.Nekojosen;
import com.nekozouneko.nekojosen.Util;
import com.nekozouneko.nekojosen.game.KojosenMode;
import com.nekozouneko.nekojosen.game.KojosenSettings;
import com.nekozouneko.nekojosen.util.ItemStackBuilder;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Collections;

public final class GameSettings {

    public static void open(Player p) {
        KojosenSettings s = Nekojosen.getInstance().getGame().getSettings();
        Inventory inv = Bukkit.createInventory(null, 27, "ゲーム設定 [SETTINGS]");
        update(inv, s);

        p.openInventory(inv);
    }

    public static void update(Inventory inv, KojosenSettings s) {
        Nekojosen pl = Nekojosen.getInstance();
        inv.clear();
        ItemStack mode = new ItemStackBuilder(Material.BOOK)
                .name("§7ゲームモード")
                .lore(
                        "§7ゲームのモードを設定します。",
                        "§7DESTROY_BEACON: ビーコンを破壊すると勝利",
                        "§7GENERAL_BATTLE: 大将戦",
                        "",
                        "§7現在の値: §f" + s.getMode().name()
                )
                .bukkitData(pl.newKey("variable"), PersistentDataType.STRING, "mode")
                .build();
        ItemStack map = new ItemStackBuilder(Material.FILLED_MAP)
                .name("§7マップ")
                .lore(
                        "§7ゲームのマップを設定します。", "",
                        "§7現在の値: §f" + (s.getMap() != null ? s.getMap().getName() + " ("+s.getMap().getId()+")" : "ランダム")
                )
                .bukkitData(pl.newKey("variable"), PersistentDataType.STRING, "map")
                .build();
        ItemStack et = new ItemStackBuilder(Util.toBoolMaterial(s.isEnabledTickets()))
                .name("§7チケットを有効化")
                .lore("§7チケットを有効化するかどうか", "", "§7現在の値: §f" + s.isEnabledTickets())
                .bukkitData(pl.newKey("variable"), PersistentDataType.STRING, "enable_ticket")
                .build();

        ItemStack pre = new ItemStackBuilder(Material.CHEST)
                .name("§7準備時間")
                .lore("§7準備時間を設定します。", "", "§7現在の値: §f" + String.format("%,d", s.getPreparation()) + "秒")
                .bukkitData(pl.newKey("variable"), PersistentDataType.STRING, "preparation")
                .build();
        ItemStack limit = new ItemStackBuilder(Material.CHEST)
                .name("§7制限時間")
                .lore("§7制限時間を設定します。", "§70以下にすると無効になります。", "",
                        "§7現在の値: §f" + (!Util.isNeg(s.getLimit()) ? String.format("%,d", s.getLimit()) + "秒" : "無効")
                )
                .bukkitData(pl.newKey("variable"), PersistentDataType.STRING, "limit")
                .build();
        ItemStack tickets = new ItemStackBuilder(Material.PAPER)
                .name("§7チケット数")
                .lore("§7チケット数を設定します。", "§7なおチケットを有効化しないと使用されません。", "",
                        "§7現在の値: §f" + s.getTickets()
                )
                .bukkitData(pl.newKey("variable"), PersistentDataType.STRING, "tickets")
                .build();

        inv.setItem(2, mode);
        inv.setItem(11, map);
        inv.setItem(20, et);

        inv.setItem(5, pre);
        inv.setItem(14, limit);
        inv.setItem(23, tickets);
    }

    public static boolean isHandleable(InventoryClickEvent e) {
        return (e.getView().getTitle().matches(".+ \\[SETTINGS]$") && e.getInventory().getType() == InventoryType.CHEST);
    }

    public static void handle(InventoryClickEvent e) {
        Player p = ((Player) e.getWhoClicked());
        ItemStack item = e.getCurrentItem();
        KojosenSettings s = Nekojosen.getInstance().getGame().getSettings();

        e.setCancelled(true);
        update(e.getInventory(), s);

        if (e.getRawSlot() > e.getInventory().getSize() || item == null || item.getType().isAir()) return;

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if (pdc.has(Nekojosen.getInstance().newKey("variable"), PersistentDataType.STRING)) {
            switch (pdc.getOrDefault(Nekojosen.getInstance().newKey("variable"), PersistentDataType.STRING, "")) {
                case "mode":
                    s.setMode(s.getMode().next());
                    update(e.getInventory(), s);
                    break;
                case "enable_ticket":
                    s.setEnableTickets(!s.isEnabledTickets());
                    update(e.getInventory(), s);
                    break;
                case "preparation":
                    new AnvilGUI.Builder()
                            .plugin(Nekojosen.getInstance())
                            .title("準備時間の設定")
                            .text(s.getPreparation() + "")
                            .onComplete((c) -> {
                                try {
                                    int i = Integer.parseInt(c.getText());
                                    s.setPreparation(i);
                                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                                }
                                catch (NumberFormatException e1) {
                                    c.getPlayer().sendMessage("§c数字として変換できません。再度お試しください。");
                                    return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(s.getPreparation() + ""));
                                }
                            })
                            .onClose((p1) -> {
                                Bukkit.getScheduler().runTaskLater(Nekojosen.getInstance(), () -> open(p1), 1);
                            })
                            .open(p);
                    break;
                case "limit":
                    new AnvilGUI.Builder()
                            .plugin(Nekojosen.getInstance())
                            .title("制限時間の設定")
                            .text(s.getLimit() + "")
                            .onComplete((c) -> {
                                try {
                                    int i = Integer.parseInt(c.getText());
                                    s.setLimit(i);
                                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                                }
                                catch (NumberFormatException e1) {
                                    c.getPlayer().sendMessage("§c数字として変換できません。再度お試しください。");
                                    return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(s.getLimit() + ""));
                                }
                            })
                            .onClose((p1) -> {
                                Bukkit.getScheduler().runTaskLater(Nekojosen.getInstance(), () -> open(p1), 1);
                            })
                            .open(p);
                    break;
                case "tickets":
                    new AnvilGUI.Builder()
                            .plugin(Nekojosen.getInstance())
                            .title("チケット数の設定")
                            .text(s.getTickets() + "")
                            .onComplete((c) -> {
                                try {
                                    int i = Integer.parseInt(c.getText());
                                    s.setTickets(i);
                                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                                }
                                catch (NumberFormatException e1) {
                                    c.getPlayer().sendMessage("§c数字として変換できません。再度お試しください。");
                                    return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(s.getTickets() + ""));
                                }
                            })
                            .onClose((p1) -> {
                                Bukkit.getScheduler().runTaskLater(Nekojosen.getInstance(), () -> open(p1), 1);
                            })
                            .open(p);
                    break;
            }
        }
    }

}
