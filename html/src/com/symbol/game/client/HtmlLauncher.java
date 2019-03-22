package com.symbol.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.symbol.game.Config;
import com.symbol.game.Symbol;

public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig() {
        return new GwtApplicationConfiguration(Config.V_WIDTH * 5, Config.V_HEIGHT * 5);
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new Symbol();
    }

}