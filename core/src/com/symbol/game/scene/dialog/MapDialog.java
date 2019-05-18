package com.symbol.game.scene.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.symbol.game.Config;
import com.symbol.game.data.MapData;
import com.symbol.game.screen.MapSelectScreen;
import com.symbol.game.util.Data;
import com.symbol.game.util.Resources;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class MapDialog extends Table {

    private static final float CONTENT_WIDTH = 94f;
    private static final float POSITION_X_LEFT = 55f;
    private static final float POSITION_X_RIGHT = 41f;
    private static final float POSITION_Y = 16f;

    private final Resources res;
    private final Data data;
    private MapSelectScreen mapSelectScreen;
    private int mapIndex;

    private TextureRegionDrawable bgLeft;
    private TextureRegionDrawable bgRight;

    private Image emptyBackground;
    private boolean displayed = false;

    private Label mapName;
    private TextButton enterButton;

    public MapDialog(Resources res, Data data, MapSelectScreen mapSelectScreen) {
        this.res = res;
        this.data = data;
        this.mapSelectScreen = mapSelectScreen;

        emptyBackground = new Image();
        emptyBackground.setSize(Config.V_WIDTH, Config.V_HEIGHT);
        emptyBackground.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mapSelectScreen.hideMapDialog();
            }
        });

        mapName = new Label("", res.getLabelStyle(data.getColorFromHexKey("player")));
        mapName.setWrap(true);
        mapName.setAlignment(Align.center);

        bgLeft = new TextureRegionDrawable(res.getTexture("map_dialog_bg_left"));
        bgRight = new TextureRegionDrawable(res.getTexture("map_dialog_bg_right"));

        setTouchable(Touchable.enabled);

        createEnterButton();
    }

    private void createEnterButton() {
        enterButton = new TextButton(data.getString("mapEnterButton"),
                res.getTextButtonStyle("enter_map", Color.WHITE));
        enterButton.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!Config.INSTANCE.onAndroid() && pointer == -1) res.playSound("std_button_hover", 1f);
            }
        });
        enterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mapSelectScreen.navigateToGameScreen(mapIndex);
            }
        });
    }

    public void show(Stage stage) {
        displayed = true;

        clearActions();
        pack();
        stage.addActor(emptyBackground);
        stage.addActor(this);

        addAction(sequence(Actions.alpha(0), Actions.fadeIn(0.4f, Interpolation.fade)));
    }

    public void hide() {
        displayed = false;
        clearChildren();
        emptyBackground.remove();
        addAction(sequence(fadeOut(0.4f, Interpolation.fade), Actions.removeActor()));
    }

    public void setData(boolean right, MapData data) {
        mapIndex = data.getId();

        setBackground(right ? bgRight : bgLeft);
        setPosition(right ? POSITION_X_RIGHT : POSITION_X_LEFT, POSITION_Y);

        mapName.setText(data.getName());
        add(mapName).width(CONTENT_WIDTH).padTop(4f).row();

        if (right) {
            add(new Image(res.getTexture("page_divider")))
                    .spaceTop(3f)
                    .spaceBottom(3f)
                    .width(CONTENT_WIDTH)
                    .left()
                    .row();
        } else {
            add(new Image(res.getTexture("page_divider")))
                    .spaceTop(3f)
                    .spaceBottom(3f)
                    .width(CONTENT_WIDTH)
                    .right()
                    .row();
        }

        if (right) add(enterButton).expandY().bottom().padRight(5f).padBottom(3f);
        else add(enterButton).expandY().bottom().padLeft(4f).padBottom(3f);
    }

    public boolean isDisplayed() {
        return displayed;
    }

}
