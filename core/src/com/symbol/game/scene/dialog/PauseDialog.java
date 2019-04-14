package com.symbol.game.scene.dialog;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.symbol.game.Symbol;

public class PauseDialog extends BaseModalDialog {

    private static final String TITLE = "PAUSED";
    private static final String RESUME_TAG = "Resume";
    private static final String SETTINGS_TAG = "Settings";
    private static final String EXIT_TAG = "Exit";

    private static final float WINDOW_MIN_HEIGHT = 90f;

    private static final float TOP_BOTTOM_PADDING = 10f;
    private static final float BUTTON_WIDTH = 60f;
    private static final float BUTTON_HEIGHT = 15f;
    private static final float BUTTON_PADDING = 5f;

    private final Symbol game;

    public PauseDialog(final Symbol game) {
        super(TITLE, game.getRes().getSkin(), game);
        this.game = game;

        getBackground().setMinHeight(WINDOW_MIN_HEIGHT);

        getButtonTable().defaults().width(BUTTON_WIDTH).padLeft(BUTTON_PADDING);
        getButtonTable().defaults().height(BUTTON_HEIGHT).padRight(BUTTON_PADDING);

        TextButton resumeButton = new TextButton(RESUME_TAG, getSkin());
        button(resumeButton, RESUME_TAG);

        getButtonTable().row();

        TextButton settingsButton = new TextButton(SETTINGS_TAG, getSkin());
        button(settingsButton, SETTINGS_TAG);

        getButtonTable().padBottom(TOP_BOTTOM_PADDING).row();

        TextButton exitButton = new TextButton(EXIT_TAG, getSkin());
        button(exitButton, EXIT_TAG);
    }

    @Override
    protected void result(Object object) {
        if (object.equals(RESUME_TAG) || object.equals(SETTINGS_TAG)) {
            game.getGameScreen().notifyResume();
        }
        else if (object.equals(EXIT_TAG)) {
            hide(null);
            game.setScreen(game.getMenuScreen());
        }
    }

}
