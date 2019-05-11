package com.symbol.game.scene.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.symbol.game.Config;
import com.symbol.game.data.MapData;
import com.symbol.game.screen.MapSelectScreen;
import com.symbol.game.util.Resources;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class MapDialog extends Table {

    private static final float POSITION_X_LEFT = 55f;
    private static final float POSITION_X_RIGHT = 41f;
    private static final float POSITION_Y = 16f;

    private final Resources res;
    private MapSelectScreen mapSelectScreen;

    private TextureRegionDrawable bgLeft;
    private TextureRegionDrawable bgRight;

    private Image emptyBackground;
    private boolean displayed = false;

    private TextButton enterButton;

    public MapDialog(Resources res, MapSelectScreen mapSelectScreen) {
        this.res = res;
        this.mapSelectScreen = mapSelectScreen;

        emptyBackground = new Image();
        emptyBackground.setSize(Config.V_WIDTH, Config.V_HEIGHT);
        emptyBackground.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mapSelectScreen.hideMapDialog();
            }
        });

        bgLeft = new TextureRegionDrawable(res.getTexture("map_dialog_bg_left"));
        bgRight = new TextureRegionDrawable(res.getTexture("map_dialog_bg_right"));

        setTouchable(Touchable.enabled);

        createEnterButton();
    }

    private void createEnterButton() {
        enterButton = new TextButton(res.getString("mapEnterButton"),
                res.getTextButtonStyle("enter_map", Color.WHITE));
        enterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mapSelectScreen.navigateToGameScreen();
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

    public void setOrientation(boolean right) {
        setBackground(right ? bgRight : bgLeft);
        setPosition(right ? POSITION_X_RIGHT : POSITION_X_LEFT, POSITION_Y);

        if (right) add(enterButton).expandY().bottom().padRight(5f).padBottom(3f);
        else add(enterButton).expandY().bottom().padLeft(4f).padBottom(3f);
    }

    public void setData(MapData data) {

    }

    public boolean isDisplayed() {
        return displayed;
    }

}
