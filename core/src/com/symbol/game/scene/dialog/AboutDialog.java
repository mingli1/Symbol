package com.symbol.game.scene.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.symbol.game.Symbol;

public class AboutDialog extends BaseModalDialog {

    private static final float MIN_HEIGHT = 65f;

    public AboutDialog(final Symbol context) {
        super(context.getData().getString("aboutDialogTitle"), context.getRes().getSkin(), context);

        dismissable = true;

        getContentTable().left().bottom().padLeft(5f);
        text(data.getString("aboutDialogContent"));

        ImageButton githubButton = new ImageButton(res.getImageButtonStyle("github"));
        githubButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.net.openURI(data.getString("githubUrl"));
            }
        });
        button(githubButton);
        getButtonTable().add(new Label(data.getString("sourceCodeLabel"), getSkin()));
        getButtonTable().pad(0f, 5f, 5f, 5f);
    }

    @Override
    public float getPrefHeight() {
        return MIN_HEIGHT;
    }

}
