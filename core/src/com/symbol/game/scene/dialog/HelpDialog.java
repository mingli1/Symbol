package com.symbol.game.scene.dialog;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.symbol.game.Symbol;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class HelpDialog extends Window {

    private static final Vector2 POSITION = new Vector2(16f, 8f);
    private Image shadow;
    private boolean displayed;

    public HelpDialog(final Symbol game, WindowStyle windowStyle) {
        super("", windowStyle);
        shadow = new Image(game.getRes().getTexture("shadow"));
        shadow.setVisible(false);
        setMovable(false);
    }

    public void show(Stage stage) {
        displayed = true;
        shadow.setVisible(true);

        clearActions();
        pack();
        stage.addActor(shadow);
        stage.addActor(this);
        addAction(sequence(Actions.alpha(0), Actions.fadeIn(0.4f, Interpolation.fade)));

        setPosition(POSITION.x, POSITION.y);
    }

    public void hide() {
        displayed = false;
        shadow.setVisible(false);
        addAction(sequence(fadeOut(0.4f, Interpolation.fade), Actions.removeActor()));
    }

    public boolean isDisplayed() {
        return displayed;
    }

}
