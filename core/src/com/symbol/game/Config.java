package com.symbol.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class Config {

    public static final boolean DEBUG = true;

    public static final int V_WIDTH = 200;
    public static final int V_HEIGHT = V_WIDTH * 3 / 5;
    public static final int SCALE = 6;

    public static final int S_WIDTH = V_WIDTH * SCALE;
    public static final int S_HEIGHT = V_HEIGHT * SCALE;

    public static final String TITLE = "Symbol";

    public static final int BG_FPS = 10;
    public static final int FG_FPS = 60;

    public static final boolean V_SYNC = false;

    public static final boolean RESIZABLE = false;

    public static boolean onAndroid() {
        return Gdx.app.getType() == Application.ApplicationType.Android;
    }

}