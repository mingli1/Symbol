package com.symbol.game.scene.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.symbol.game.Symbol;

public class BaseModalDialog extends Dialog {

    private Image image;

    public BaseModalDialog(String title, Skin skin, final Symbol game) {
        super(title, skin);
        image = new Image(game.getRes().getTexture("shadow"));
    }

    @Override
    public Dialog show(Stage stage) {
        stage.addActor(image);
        super.show(stage);
        return this;
    }

    @Override
    public void hide() {
        super.hide();
        image.remove();
    }

}
