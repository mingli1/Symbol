package com.symbol.game.scene.dialog;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.symbol.game.Symbol;
import com.symbol.game.util.Resources;

public class BaseModalDialog extends Dialog {

    protected final Resources res;

    private static final float TITLE_PADDING = 20f;

    private Image image;
    boolean dismissable = false;

    Stage stage;

    BaseModalDialog(String title, Skin skin, final Symbol context) {
        super(title, skin);
        res = context.getRes();

        getTitleLabel().setAlignment(Align.center);
        getTitleLabel().setFontScale(1.5f);
        getTitleLabel().setColor(res.getColorFromHexKey("player"));
        getTitleTable().padTop(TITLE_PADDING);

        image = new Image(res.getTexture("shadow"));

        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (dismissable) hide();
            }
        });
    }

    @Override
    public Dialog show(Stage stage) {
        this.stage = stage;
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
