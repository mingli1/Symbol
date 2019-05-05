package com.symbol.game.scene.page;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.symbol.game.util.Resources;

public class MapPage extends Table implements Page {

    private Resources res;
    private PagedScrollPane parent;

    private int pageIndex;

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

    public MapPage(Resources res, MapPageType type, PagedScrollPane parent) {
        this.res = res;
        this.parent = parent;
        setBackground(new TextureRegionDrawable(res.getTexture(type.key)));

        createMapButton(type);
    }

    private void createMapButton(MapPageType type) {
        ImageButton.ImageButtonStyle mapIncompletedStyle = res.getImageButtonStyle("map_incomplete");
        ImageButton mapButton = new ImageButton(mapIncompletedStyle);

        switch (type) {
            case Start:
                add(mapButton).expandX().left();
                break;
            case Left:
            case EndLeft:
                add(mapButton).expandX().left().padBottom(1f);
                break;
            case Right:
            case EndRight:
                add(mapButton).expandX().right().padBottom(1f);
                break;
        }

        mapButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parent.scrollToIndex(pageIndex);
            }
        });
    }

    @Override
    public void reset() {

    }

    @Override
    public void notifySeen() {

    }

    @Override
    public boolean hasSeen() {
        return false;
    }

    @Override
    public Actor getPageActor() {
        return this;
    }

    @Override
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

}
