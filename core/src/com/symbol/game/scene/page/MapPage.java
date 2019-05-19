package com.symbol.game.scene.page;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.symbol.game.Config;
import com.symbol.game.data.MapData;
import com.symbol.game.screen.MapSelectScreen;
import com.symbol.game.util.Data;
import com.symbol.game.util.Resources;

import java.util.List;

public class MapPage extends Table implements Page {

    private static final float MAP_BUTTON_WIDTH = 21f;

    private Resources res;
    private Data data;
    private MapSelectScreen parent;
    private MapPageType type;

    private int pageIndex;
    private Label mapIconLabel;
    private MapData mapData;
    private boolean right;

    private ImageButton mapButton;
    private ImageButtonStyle mapCompleteStyle;
    private ImageButtonStyle mapIncompleteStyle;
    private ImageButtonStyle mapDisabledStyle;

    private TextureRegionDrawable incompleteBg;
    private TextureRegionDrawable complete1Bg;
    private TextureRegionDrawable complete2Bg;

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

    public MapPage(Resources res, Data data, MapData mapData, MapPageType type, MapSelectScreen parent) {
        this.res = res;
        this.data = data;
        this.parent = parent;
        this.mapData = mapData;
        this.type = type;

        mapCompleteStyle = res.getImageButtonStyle("map_complete");
        mapIncompleteStyle = res.getImageButtonStyle("map_incomplete");
        mapDisabledStyle = new ImageButtonStyle();
        mapDisabledStyle.imageUp = mapDisabledStyle.imageDown
                = mapDisabledStyle.imageDisabled = new TextureRegionDrawable(res.getTexture("button_map_disabled"));

        incompleteBg = new TextureRegionDrawable(res.getTexture(type.key));
        if (type == MapPageType.Start || type == MapPageType.EndLeft || type == MapPageType.EndRight) {
            complete1Bg = new TextureRegionDrawable(res.getTexture(type.key + "_c"));
        } else {
            complete1Bg = new TextureRegionDrawable(res.getTexture(type.key + "_c1"));
            complete2Bg = new TextureRegionDrawable(res.getTexture(type.key + "_c2"));
        }

        setBackground(incompleteBg);

        createMapButton(type);
    }

    private void createMapButton(MapPageType type) {
        mapButton = new ImageButton(mapDisabledStyle);

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

        mapButton.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!Config.INSTANCE.onAndroid() && pointer == -1) {
                    res.playSound("map_button_hover", 1f);
                }
            }

        });
        mapButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                res.playSound("map_button_click", 1f);
                parent.pagedScrollPane.scrollToIndex(pageIndex);
                onMapButtonClicked();
            }
        });
    }

    private void onMapButtonClicked() {
        parent.showMapDialog(right, mapData);
    }

    @Override
    public void reset() {
        List<MapData> mapDatas = data.getMapDatas();
        int prevIndex = mapData.getId() - 1;

        mapButton.setTouchable(Touchable.enabled);
        if (!mapData.getCompleted()) {
            if (prevIndex < 0 || mapDatas.get(prevIndex).getCompleted()) {
                mapButton.setStyle(mapIncompleteStyle);

                if (type == MapPageType.Left || type == MapPageType.Right) {
                    setBackground(complete1Bg);
                }
            } else {
                mapButton.setTouchable(Touchable.disabled);
                mapButton.setStyle(mapDisabledStyle);
            }
        }
        else {
            mapButton.setStyle(mapCompleteStyle);

            if (type == MapPageType.Start || type == MapPageType.EndLeft || type == MapPageType.EndRight) {
                setBackground(complete1Bg);
            } else {
                setBackground(complete2Bg);
            }
        }
    }

    @Override
    public void notifySeen() {}

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
