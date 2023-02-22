package com.nekozouneko.nekojosen.game;

import com.nekozouneko.nekojosen.game.map.KojosenMap;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class KojosenSettings {

    private KojosenMode mode = KojosenMode.DESTROY_BEACON; // ゲームモード
    private KojosenMap map = null; // nullならランダム、でなければそのマップ固定

    private boolean enable_tickets = false; // チケットを有効化するかどうか

    private int tickets = 100; // 開始時のデフォルトチケット
    private int preparation = 30; // 準備時間
    private int limit = 300; // 制限時間

    public KojosenSettings() {
    }

    public void setTickets(int i) {
        tickets = i;
    }

    public int getTickets() {
        return tickets;
    }

    public void setPreparation(int i) {
        preparation = i;
    }

    public int getPreparation() {
        return preparation;
    }

    public void setLimit(int i) {
        limit = i;
    }

    public int getLimit() {
        return limit;
    }

    public void setEnableTickets(boolean b) {
        enable_tickets = b;
    }

    public boolean isEnabledTickets() {
        return enable_tickets;
    }

    public void setMode(@NotNull KojosenMode mode) {
        this.mode = Objects.requireNonNull(mode);
    }

    @NotNull
    public KojosenMode getMode() {
        return mode;
    }

    public void setMap(KojosenMap map) {
        this.map = map;
    }

    public KojosenMap getMap() {
        return map;
    }
}
