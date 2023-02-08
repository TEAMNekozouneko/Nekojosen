package com.nekozouneko.nekojosen.game;

public enum KojosenMode {

    DESTROY_BEACON,
    GENERAL_BATTLE;

    public KojosenMode next() {
        switch (this) {
            case DESTROY_BEACON:
                return GENERAL_BATTLE;
            case GENERAL_BATTLE:
                return DESTROY_BEACON;
            default: throw new RuntimeException();
        }
    }

}
