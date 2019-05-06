package com.symbol.game.scene.page;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.symbol.game.screen.MapSelectScreen;
import com.symbol.game.util.Resources;

public class MapPage extends Table implements Page {

    private static final float MAP_BUTTON_WIDTH = 21f;

    private Resources res;
    private MapSelectScreen parent;

    private int pageIndex;
    private Label mapIconLabel;
    private boolean right;

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

    public MapPage(Resources res, MapPageType type, MapSelectScreen parent) {
        this.res = res;
        this.parent = parent;
        setBackground(new TextureRegionDrawable(res.getTexture(type.key)));

        createMapButton(type);
    }

    private void createMapButton(MapPageType type) {
        ImageButton.ImageButtonStyle mapIncompletedStyle = res.getImageButtonStyle("map_incomplete");
        ImageButton mapButton = new ImageButton(mapIncompletedStyle);

        Label.LabelStyle black = res.getLabelStyle(Color.BLACK);
        mapIconLabel = new Label("", black);
        Container<Label> wrapper = new Container<>();
        wrapper.setActor(mapIconLabel);

        switch (type) {
            case Start:
            case Left:
            case EndLeft:
                add(mapButton).expandX().left().padTop(5f).row();
                add(wrapper).width(MAP_BUTTON_WIDTH).expandX().left();
                right = false;
                break;
            case Right:
            case EndRight:
                add(mapButton).expandX().right().padTop(5f).row();
                add(wrapper).width(MAP_BUTTON_WIDTH).expandX().right();
                right = true;
                break;
        }

        mapButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parent.pagedScrollPane.scrollToIndex(pageIndex);
                onMapButtonClicked();
            }
        });
    }

    private void onMapButtonClicked() {
        parent.showMapDialog(right);
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
        mapIconLabel.setText(String.valueOf(pageIndex + 1));
    }

}
