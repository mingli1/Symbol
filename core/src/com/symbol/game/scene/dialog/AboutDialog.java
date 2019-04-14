package com.symbol.game.scene.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.symbol.game.Symbol;

public class AboutDialog extends BaseModalDialog {

    private static final String TITLE = "About";
    private static final String CONTENT = "Created by\nMing Li in 2019";
    private static final String BUTTON_LABEL = "Source Code";
    private static final float MIN_HEIGHT = 65f;

    private static final String URL = "https://github.com/mingli1/Symbol";

    public AboutDialog(final Symbol game) {
        super(TITLE, game.getRes().getSkin(), game);

        dismissable = true;

        getContentTable().left().bottom().padLeft(5f);
        text(CONTENT);

        ImageButton githubButton = new ImageButton(game.getRes().getImageButtonStyle("github"));
        githubButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.net.openURI(URL);
            }
        });
        button(githubButton);
        getButtonTable().add(new Label(BUTTON_LABEL, getSkin()));
        getButtonTable().pad(0f, 5f, 5f, 5f);
    }

    @Override
    public float getPrefHeight() {
        return MIN_HEIGHT;
    }

}
