package com.nekozouneko.nekojosen.listener;

import com.nekozouneko.nekojosen.gui.GameSettings;
import com.nekozouneko.nekojosen.gui.MainDashboard;
import com.nekozouneko.nekojosen.gui.MapDashboard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onEvent(InventoryClickEvent e) {
        if (MainDashboard.isHandleable(e)) {
            MainDashboard.handle(e);
        }
        else if (GameSettings.isHandleable(e)) {
            GameSettings.handle(e);
        }
        else if (MapDashboard.isHandleable(e)) {
            MapDashboard.handle(e);
        }
    }

}
