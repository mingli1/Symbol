package com.symbol.game.scene.dialog;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.symbol.game.Symbol;

public class PauseDialog extends BaseModalDialog {

    private static final float WINDOW_MIN_HEIGHT = 90f;

    private static final float TOP_BOTTOM_PADDING = 10f;
    private static final float BUTTON_WIDTH = 60f;
    private static final float BUTTON_HEIGHT = 15f;
    private static final float BUTTON_PADDING = 5f;

    private final Symbol context;

    private ConfirmDialog confirmDialog;

    public PauseDialog(final Symbol context) {
        super(context.getData().getString("pauseDialogTitle"), context.getRes().getSkin(), context);
        this.context = context;

        getBackground().setMinHeight(WINDOW_MIN_HEIGHT);

        getButtonTable().defaults().width(BUTTON_WIDTH).padLeft(BUTTON_PADDING);
        getButtonTable().defaults().height(BUTTON_HEIGHT).padRight(BUTTON_PADDING);

        TextButton resumeButton = new TextButton(data.getString("resumeButton"), getSkin());
        addSound(resumeButton);
        button(resumeButton, data.getString("resumeButton"));

        getButtonTable().row();

        TextButton settingsButton = new TextButton(data.getString("settingsButton"), getSkin());
        addSound(settingsButton);
        button(settingsButton, data.getString("settingsButton"));

        getButtonTable().padBottom(TOP_BOTTOM_PADDING).row();

        TextButton exitButton = new TextButton(data.getString("exitButton"), getSkin());
        addSound(exitButton);
        button(exitButton, data.getString("exitButton"));

        confirmDialog = new ConfirmDialog(context,
                data.getString("exitConfirmTitle"),
                data.getString("exitConfirmMessage"),
                this::exit,
                () -> {
                    confirmDialog.hide();
                    show(stage);
                });
    }

    @Override
    protected void result(Object object) {
        res.playSound("std_button_click", 1f);
        if (object.equals(data.getString("resumeButton")) ||
                object.equals(data.getString("settingsButton"))) {
            context.getGameScreen().notifyResume();
        }
        else if (object.equals(data.getString("exitButton"))) {
            confirmDialog.show(stage);
        }
    }

    private void exit() {
        hide(null);
        confirmDialog.hide(null);
        context.setScreen(context.getMenuScreen());
    }

}
