package com.symbol.game.scene.page;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.symbol.game.util.Resources;

public class MapPage extends Table implements Page {

    private Resources res;

    public enum MapPageType {
        Start("map_page_start"),
        Left("map_page_left"),
        Right("map_page_right"),
        EndLeft("map_page_end_left"),
        EndRight("map_page_end_right");

        private String key;

        MapPageType(String key) {
            this.key = key;
        }
    }

    public MapPage(Resources res, MapPageType type) {
        this.res = res;
        setBackground(new TextureRegionDrawable(res.getTexture(type.key)));
    }

    @Override
    public void notifySeen() {

    }

    @Override
    public boolean hasSeen() {
        return false;
    }

    @Override
    public Actor getActor() {
        return this;
    }

}
