package com.symbol.game.scene.dialog;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.symbol.game.Symbol;

public class PauseDialog extends BaseModalDialog {

    private static final float WINDOW_MIN_HEIGHT = 90f;

    private static final float TOP_BOTTOM_PADDING = 10f;
    private static final float BUTTON_WIDTH = 60f;
    private static final float BUTTON_HEIGHT = 15f;
    private static final float BUTTON_PADDING = 5f;

    private final Symbol game;

    private ConfirmDialog confirmDialog;

    public PauseDialog(final Symbol game) {
        super(game.getRes().getString("pauseDialogTitle"), game.getRes().getSkin(), game);
        this.game = game;

        getBackground().setMinHeight(WINDOW_MIN_HEIGHT);

        getButtonTable().defaults().width(BUTTON_WIDTH).padLeft(BUTTON_PADDING);
        getButtonTable().defaults().height(BUTTON_HEIGHT).padRight(BUTTON_PADDING);

        TextButton resumeButton = new TextButton(game.getRes().getString("resumeButton"), getSkin());
        button(resumeButton, game.getRes().getString("resumeButton"));

        getButtonTable().row();

        TextButton settingsButton = new TextButton(game.getRes().getString("settingsButton"), getSkin());
        button(settingsButton, game.getRes().getString("settingsButton"));

        getButtonTable().padBottom(TOP_BOTTOM_PADDING).row();

        TextButton exitButton = new TextButton(game.getRes().getString("exitButton"), getSkin());
        button(exitButton, game.getRes().getString("exitButton"));

        confirmDialog = new ConfirmDialog(game,
                game.getRes().getString("exitConfirmTitle"),
                game.getRes().getString("exitConfirmMessage"),
                this::exit,
                () -> {
                    confirmDialog.hide();
                    show(stage);
                });
    }

    @Override
    protected void result(Object object) {
        if (object.equals(game.getRes().getString("resumeButton")) ||
                object.equals(game.getRes().getString("settingsButton"))) {
            game.getGameScreen().notifyResume();
        }
        else if (object.equals(game.getRes().getString("exitButton"))) {
            confirmDialog.show(stage);
        }
    }

    private void exit() {
        hide(null);
        confirmDialog.hide(null);
        game.setScreen(game.getMenuScreen());
    }

}
