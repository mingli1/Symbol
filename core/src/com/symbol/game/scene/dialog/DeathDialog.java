package com.symbol.game.scene.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.symbol.game.Symbol;
import com.symbol.game.util.Data;
import com.symbol.game.util.Resources;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class DeathDialog extends Table {

    private static final float FADE_IN_TIME = 3f;
    private static final float BUTTON_WIDTH = 100f;

    private final Symbol context;
    private Resources res;
    private Data data;
    private boolean displayed;

    public DeathDialog(final Symbol context) {
        this.context = context;
        res = context.getRes();
        data = context.getData();
        setBackground(new TextureRegionDrawable(res.getTexture("death_shadow")));
        setFillParent(true);
        setTouchable(Touchable.enabled);
        createLayout();
    }

    private void createLayout() {
        Label message = new Label(data.getString("deathMessage"), res.getLabelStyle(Color.WHITE));
        message.setFontScale(4f);
        add(message).row();

        TextButton respawnButton = new TextButton(data.getString("respawnButton"),
                res.getTextButtonStyle("menu", Color.WHITE));
        add(respawnButton).width(BUTTON_WIDTH).height(16f).space(8f).padTop(8f).row();

        TextButton quitButton = new TextButton(data.getString("quitButton"), res.getSkin());
        add(quitButton).width(BUTTON_WIDTH).height(16f);

        respawnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) { onRespawnClicked(); }
        });
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onQuitClicked();
            }
        });
    }

    public void show(Stage stage) {
        displayed = true;
        clearActions();
        pack();
        stage.addActor(this);
        addAction(sequence(alpha(0), fadeIn(FADE_IN_TIME, Interpolation.fade)));
    }

    public void hide() {
        displayed = false;
        remove();
    }

    private void onRespawnClicked() {
        hide();
        context.getGameScreen().loadMap(true);
    }

    private void onQuitClicked() {
        hide();
        context.setScreen(context.getMenuScreen());
    }

    public boolean isDisplayed() {
        return displayed;
    }

}
