package com.symbol.game.scene.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.symbol.game.Symbol;

public class AboutDialog extends BaseModalDialog {

    private static final float MIN_HEIGHT = 65f;

    public AboutDialog(final Symbol game) {
        super(game.getRes().getString("aboutDialogTitle"), game.getRes().getSkin(), game);

        dismissable = true;

        getContentTable().left().bottom().padLeft(5f);
        text(game.getRes().getString("aboutDialogContent"));

        ImageButton githubButton = new ImageButton(game.getRes().getImageButtonStyle("github"));
        githubButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.net.openURI(game.getRes().getString("githubUrl"));
            }
        });
        button(githubButton);
        getButtonTable().add(new Label(game.getRes().getString("sourceCodeLabel"), getSkin()));
        getButtonTable().pad(0f, 5f, 5f, 5f);
    }

    @Override
    public float getPrefHeight() {
        return MIN_HEIGHT;
    }

}
