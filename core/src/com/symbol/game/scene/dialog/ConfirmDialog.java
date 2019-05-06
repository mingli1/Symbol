package com.symbol.game.scene.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.symbol.game.Symbol;

public class ConfirmDialog extends BaseModalDialog {

    private static final String AFFIRMATIVE_TAG = "AFFIRMATIVE";
    private static final String DISMISSIVE_TAG = "DISMISSIVE";

    private static final float BUTTON_WIDTH = 50f;
    private static final float BUTTON_HEIGHT = 16f;
    private static final float WIDTH = 100f;

    private Runnable affirmative;
    private Runnable dismissive;

    public ConfirmDialog(final Symbol game,
                         String title, String message,
                         Runnable affirmative, Runnable dismissive) {
        super(title, game.getRes().getSkin(), game);
        this.affirmative = affirmative;
        this.dismissive = dismissive;

        getButtonTable().defaults().width(BUTTON_WIDTH);
        getButtonTable().defaults().height(BUTTON_HEIGHT);

        Label messageLabel = new Label(message, game.getRes().getLabelStyle(Color.WHITE));
        messageLabel.setWrap(true);
        getContentTable().add(messageLabel).width(WIDTH).fill().space(4f);

        button(game.getRes().getString("affirmativeText"), AFFIRMATIVE_TAG);
        button(game.getRes().getString("dismissiveText"), DISMISSIVE_TAG);

        getButtonTable().pad(-16f, 4f, 4f, 4f);
    }

    @Override
    protected void result(Object object) {
        if (object.equals(AFFIRMATIVE_TAG)) affirmative.run();
        else if (object.equals(DISMISSIVE_TAG)) dismissive.run();
    }

}