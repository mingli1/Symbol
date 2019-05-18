package com.symbol.game.scene.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.symbol.game.Symbol;

public class ConfirmDialog extends BaseModalDialog {

    private static final String AFFIRMATIVE_TAG = "AFFIRMATIVE";
    private static final String DISMISSIVE_TAG = "DISMISSIVE";

    private static final float BUTTON_WIDTH = 50f;
    private static final float BUTTON_HEIGHT = 16f;
    private static final float WIDTH = 100f;

    private Runnable affirmative;
    private Runnable dismissive;

    public ConfirmDialog(final Symbol context,
                         String title, String message,
                         Runnable affirmative, Runnable dismissive) {
        super(title, context.getRes().getSkin(), context);
        this.affirmative = affirmative;
        this.dismissive = dismissive;

        getButtonTable().defaults().width(BUTTON_WIDTH);
        getButtonTable().defaults().height(BUTTON_HEIGHT);

        Label messageLabel = new Label(message, res.getLabelStyle(Color.WHITE));
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.center);
        getContentTable().add(messageLabel).width(WIDTH).fill().space(4f);

        TextButton affirmButton = new TextButton(data.getString("affirmativeText"), getSkin());
        addSound(affirmButton);
        TextButton dismissButton = new TextButton(data.getString("dismissiveText"), getSkin());
        addSound(dismissButton);

        button(affirmButton, AFFIRMATIVE_TAG);
        button(dismissButton, DISMISSIVE_TAG);

        getButtonTable().pad(-16f, 4f, 4f, 4f);
    }

    @Override
    protected void result(Object object) {
        res.playSound("std_button_click", 1f);
        if (object.equals(AFFIRMATIVE_TAG)) affirmative.run();
        else if (object.equals(DISMISSIVE_TAG)) dismissive.run();
    }

}
