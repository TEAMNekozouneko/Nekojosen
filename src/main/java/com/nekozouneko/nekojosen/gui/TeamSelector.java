package com.nekozouneko.nekojosen.gui;

import com.nekozouneko.nekojosen.Nekojosen;
import com.nekozouneko.nekojosen.game.KojosenGame;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class TeamSelector implements InventoryHolder, Listener {

    private final Nekojosen plugin;
    private final Player p;
    private final Inventory inv = Bukkit.createInventory(this, 9, "チームを選択...");
    private final boolean enableLeave;
    private final Consumer<KojosenGame.GTeam> onSuccess;

    public TeamSelector(Nekojosen plugin, Player player, boolean enableLeave, Consumer<KojosenGame.GTeam> onSuccess) {
        this.plugin = plugin;
        this.p = player;
        this.enableLeave = enableLeave;
        this.onSuccess = onSuccess;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open() {
        p.openInventory(inv);
        update();
    }

    public void update() {
        inv.clear();

        ItemStack red = new ItemStackBuilder(Material.RED_WOOL)
                .name("§c赤チーム")
                .bukkitData(plugin.newKey("team"), PersistentDataType.STRING, "red")
                .build();

        ItemStack blue = new ItemStackBuilder(Material.BLUE_WOOL)
                .name("§9青チーム")
                .bukkitData(plugin.newKey("team"), PersistentDataType.STRING, "blue")
                .build();

        ItemStack leave = new ItemStackBuilder(Material.LIGHT_GRAY_WOOL)
                .name("§7退出")
                .bukkitData(plugin.newKey("team"), PersistentDataType.STRING, "leave")
                .build();

        ItemStack back = new ItemStackBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .name(" ")
                .build();

        if (enableLeave) {
            inv.setItem(8, leave);
        }
        else inv.setItem(8, back);

        inv.setItem(0, red);
        inv.setItem(1, blue);
        inv.setItem(2, back);
        inv.setItem(3, back);
        inv.setItem(4, back);
        inv.setItem(5, back);
        inv.setItem(6, back);
        inv.setItem(7, back);
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

        if (item == null || item.getType().isAir() || !(e.getRawSlot() < 54)) return;

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        String team = pdc.getOrDefault(plugin.newKey("team"), PersistentDataType.STRING, "none");

        switch (team) {
            case "red":
                if (onSuccess != null) {
                    p.closeInventory();
                    onSuccess.accept(KojosenGame.GTeam.RED);
                }
                break;
            case "blue":
                if (onSuccess != null) {
                    p.closeInventory();
                    onSuccess.accept(KojosenGame.GTeam.BLUE);
                }
                break;
            case "leave":
                if (onSuccess != null) {
                    p.closeInventory();
                    onSuccess.accept(null);
                }
                break;
            default:
                update();
                break;
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() != this) return;

        HandlerList.unregisterAll(this);
    }
}
