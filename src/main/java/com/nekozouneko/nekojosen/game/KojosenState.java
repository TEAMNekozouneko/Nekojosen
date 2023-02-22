package com.nekozouneko.nekojosen.game;

public enum KojosenState {
    WAITING(true),
    COUNTDOWN(true),

    PREP_TIME(false),
    PLAYING(false);

    private final boolean waiting;

    KojosenState(boolean waiting) {
        this.waiting = waiting;
    }

    public boolean isWaiting() {
        return waiting;
    }

    public boolean isPlaying() {
        return !waiting;
    }
}
