package com.nekozouneko.nekojosen.gui.map;

import com.nekozouneko.nekojosen.Nekojosen;
import com.nekozouneko.nekojosen.game.map.KojosenMap;
import com.nekozouneko.nekojosen.game.map.MapManager;
import com.nekozouneko.nekojosen.util.ItemStackBuilder;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
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

import java.util.Collections;

public final class WorldList implements InventoryHolder, Listener {
    
    public enum CloseAction {
        CREATE_MAP
    }
    
    private final Nekojosen plugin;
    private final Player target;
    private final Inventory inv = Bukkit.createInventory(this, 54, "ワールドを選択");
    private final CloseAction act;

    private int page;

    public WorldList(Nekojosen plugin, Player target, int page, CloseAction act) {
        this.plugin = plugin;
        this.target = target;
        this.act = act;
        this.page = page;

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
        boolean canNext = Bukkit.getWorlds().size() >= (45 * page);

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

        for (int i = 0; i < 45; i++) {
            if ( Bukkit.getWorlds().size() <= (i + (45 * (page-1))) ) break;
            Material m;
            World w = Bukkit.getWorlds().get(i + (45 * (page-1)));

            switch (w.getEnvironment()) {
                case NORMAL:
                    m = Material.GRASS_BLOCK;
                    break;
                case NETHER:
                    m = Material.NETHERRACK;
                    break;
                case THE_END:
                    m = Material.END_STONE;
                    break;
                default:
                    m = Material.IRON_PICKAXE;
                    break;
            }

            ItemStack wi = new ItemStackBuilder(m)
                    .name("§f" + w.getName())
                    .bukkitData(plugin.newKey("action"), PersistentDataType.STRING, "select-world")
                    .bukkitData(plugin.newKey("value"), PersistentDataType.STRING, w.getName())
                    .build();

            inv.setItem(i, wi);
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
                case "previous_page":
                    page--;
                    break;
                case "next_page":
                    page++;
                    break;
                case "select-world":
                    if (act == CloseAction.CREATE_MAP) {
                        new AnvilGUI.Builder()
                                .plugin(plugin)
                                .title("マップ名")
                                .text("")
                                .onComplete((c) -> {
                                    if (c.getText() == null || c.getText().isEmpty()) {
                                        c.getPlayer().sendMessage("§c作成に失敗しました。マップ名が空白か存在しません。");
                                        return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(""));
                                    }

                                    String value = pdc.get(plugin.newKey("value"), PersistentDataType.STRING);
                                    if (value != null) {
                                        KojosenMap map = new KojosenMap(value, c.getText());
                                        plugin.getMapManager().save(map);
                                        plugin.getMapManager().load(map);
                                        c.getPlayer().sendMessage("§aマップは正常に作成され、ロードされました。");
                                    }
                                    else {
                                        c.getPlayer().sendMessage("§c作成に失敗しました。ワールド名が設定されていませんでした。");
                                    }

                                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                                })
                                .open(p);
                    }
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
