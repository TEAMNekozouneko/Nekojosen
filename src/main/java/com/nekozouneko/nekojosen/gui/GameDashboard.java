package com.nekozouneko.nekojosen.gui;

import com.nekozouneko.nekojosen.Nekojosen;
import com.nekozouneko.nekojosen.util.ItemStackBuilder;

import org.bukkit.Bukkit;
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
import org.bukkit.persistence.PersistentDataType;

public final class GameDashboard implements InventoryHolder, Listener {

    private final Inventory inv = Bukkit.createInventory(this, 9, "ゲームダッシュボード");
    private final Player player;
    private final Nekojosen plugin;

    public GameDashboard(Nekojosen plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public Player getPlayer() {
        return player;
    }

    public void update() {
        inv.clear();

        ItemStack start;
        switch (plugin.getGame().getStatus()) {
            case WAITING:
                start = new ItemStackBuilder(Material.LIME_WOOL)
                        .name("§aゲームを開始")
                        .bukkitData(plugin.newKey("action"), PersistentDataType.STRING, "start")
                        .build();
                break;
            case COUNTDOWN:
                start = new ItemStackBuilder(Material.YELLOW_WOOL)
                        .name("§cゲームの開始をキャンセル")
                        .bukkitData(plugin.newKey("action"), PersistentDataType.STRING, "cancel")
                        .build();
                break;
            default:
                start = new ItemStackBuilder(Material.RED_WOOL)
                        .name("§cゲームを強制終了")
                        .bukkitData(plugin.newKey("action"), PersistentDataType.STRING, "end")
                        .build();
                break;
        }

        ItemStack random = new ItemStackBuilder(Material.LEATHER_CHESTPLATE)
                .name("§fチームを振り分け")
                .bukkitData(plugin.newKey("action"), PersistentDataType.STRING, "randomize")
                .build();

        ItemStack change = new ItemStackBuilder(Material.PISTON)
                .name("§7チームを変更、参加")
                .bukkitData(plugin.newKey("action"), PersistentDataType.STRING, "change")
                .build();

        ItemStack equipments = new ItemStackBuilder(Material.CHEST)
                .name("§2キット設定")
                .bukkitData(plugin.newKey("action"), PersistentDataType.STRING, "equipments")
                .build();

        inv.setItem(1, equipments);
        inv.setItem(3, random);
        inv.setItem(4, change);
        inv.setItem(7, start);
    }

    public void open() {
        player.openInventory(inv);
        update();
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() != this) return;

        e.setCancelled(true);

        final ItemStack item = e.getCurrentItem();
        final Player p = ((Player) e.getWhoClicked());

        if (item == null || item.getType().isAir()) {
            update();
            return;
        }

        final String act = item.getItemMeta().getPersistentDataContainer()
                        .getOrDefault(
                                plugin.newKey("action"),
                                PersistentDataType.STRING, "none"
                        );

        switch (act) {
            case "start":
                plugin.getGame().startCountdown();
                break;
            case "cancel":
                plugin.getGame().cancelCountdown();
                break;
            case "end":
                plugin.getGame().end();
                p.sendMessage("§cゲームを強制終了しました。");
                break;
            case "randomize":
                plugin.getGame().getPlayers().clear();

                Bukkit.getOnlinePlayers().forEach((p1) -> plugin.getGame().join(p1.getUniqueId()));
                break;
            case "change":
            case "equipments":
                break;
        }

        update();
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() != this) return;

        HandlerList.unregisterAll(this);
    }
}
