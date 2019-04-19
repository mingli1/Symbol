package com.symbol.game.scene.dialog;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.symbol.game.Symbol;
import com.symbol.game.util.Resources;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class HelpDialog extends Table {

    private static final float TOP_ARROW_OFFSET = 5f;

    private Resources res;

    private static final Vector2 POSITION = new Vector2(16f, 8f);
    private Image shadow;
    private boolean displayed;

    public HelpDialog(final Symbol game) {
        res = game.getRes();
        shadow = new Image(game.getRes().getTexture("shadow"));
        shadow.setVisible(false);

        setBackground(new TextureRegionDrawable(res.getTexture("help_dialog_bg")));
        padTop(TOP_ARROW_OFFSET);
        setDebug(true, true);

        createNavigationButtons();
    }

    private void createNavigationButtons() {
        ImageButton.ImageButtonStyle leftStyle = res.getImageButtonStyle("help_dialog_left");
        ImageButton leftButton = new ImageButton(leftStyle);
        add(leftButton).left().padLeft(3f).expandX();

        ImageButton.ImageButtonStyle rightStyle = res.getImageButtonStyle("help_dialog_right");
        ImageButton rightButton = new ImageButton(rightStyle);
        add(rightButton).right().padRight(3f);
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
