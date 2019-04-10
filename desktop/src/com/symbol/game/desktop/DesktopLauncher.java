package com.symbol.game.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.symbol.game.Config;
import com.symbol.game.Symbol;

public class DesktopLauncher {

    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.title = Config.TITLE;
        config.width = Config.S_WIDTH;
        config.height = Config.S_HEIGHT;
        config.backgroundFPS = Config.BG_FPS;
        config.foregroundFPS = Config.FG_FPS;
        config.vSyncEnabled = Config.V_SYNC;
        config.resizable = Config.RESIZABLE;
        config.addIcon("textures/desktop_icon128.png", Files.FileType.Internal);
        config.addIcon("textures/desktop_icon32.png", Files.FileType.Internal);
        config.addIcon("textures/desktop_icon16.png", Files.FileType.Internal);

        new LwjglApplication(new Symbol(), config);
    }

}
