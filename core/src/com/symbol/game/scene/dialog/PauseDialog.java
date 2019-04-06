package com.symbol.game.scene.dialog;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.symbol.game.Symbol;

public class PauseDialog extends Dialog {

    private static final String TITLE = "PAUSED";
    private static final String RESUME_TAG = "Resume";
    private static final String SETTINGS_TAG = "Settings";
    private static final String EXIT_TAG = "Exit";

    private final Symbol game;

    public PauseDialog(final Symbol game) {
        super(TITLE, game.getRes().getSkin());
        this.game = game;

        getButtonTable().defaults().width(50);
        getButtonTable().defaults().height(15);

        TextButton resumeButton = new TextButton(RESUME_TAG, getSkin());
        button(resumeButton, RESUME_TAG);

        getButtonTable().row();

        TextButton settingsButton = new TextButton(SETTINGS_TAG, getSkin());
        button(settingsButton, SETTINGS_TAG);

        getButtonTable().row();

        TextButton exitButton = new TextButton(EXIT_TAG, getSkin());
        button(exitButton, EXIT_TAG);
    }

    @Override
    protected void result(Object object) {
        game.gameScreen.notifyResume();
    }

}
