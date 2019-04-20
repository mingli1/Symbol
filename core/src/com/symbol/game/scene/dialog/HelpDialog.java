package com.symbol.game.scene.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.symbol.game.Symbol;
import com.symbol.game.scene.Page;
import com.symbol.game.scene.PagedScrollPane;
import com.symbol.game.util.Resources;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class HelpDialog extends Table {

    private static final float TOP_ARROW_OFFSET = 5f;

    private Resources res;

    private static final Vector2 POSITION = new Vector2(19f, 8f);
    private Image shadow;
    private boolean displayed;

    public HelpDialog(final Symbol game) {
        res = game.getRes();
        shadow = new Image(game.getRes().getTexture("shadow"));
        shadow.setVisible(false);

        setBackground(new TextureRegionDrawable(res.getTexture("help_dialog_bg")));
        padTop(TOP_ARROW_OFFSET);
        //setDebug(true, true);

        createLayout();
    }

    private void createLayout() {
        ImageButton.ImageButtonStyle leftStyle = res.getImageButtonStyle("help_dialog_left");
        ImageButton leftButton = new ImageButton(leftStyle);
        add(leftButton).padLeft(3f);

        add(getInnerTable()).expandX();

        ImageButton.ImageButtonStyle rightStyle = res.getImageButtonStyle("help_dialog_right");
        ImageButton rightButton = new ImageButton(rightStyle);
        add(rightButton).padRight(3f);
    }

    private Table getInnerTable() {
        Table table = new Table();

        Label.LabelStyle titleStyle = new Label.LabelStyle(res.getFont(),
                new Color(Color.valueOf(res.getColor("player"))));
        Label titleLabel = new Label(res.getString("helpDialogTitle"), titleStyle);
        table.add(titleLabel).left().padBottom(2f).row();

        Drawable none = new TextureRegionDrawable(res.getTexture("default-rect"));
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.background = scrollPaneStyle.corner = scrollPaneStyle.hScroll =
                scrollPaneStyle.hScrollKnob = scrollPaneStyle.vScroll = scrollPaneStyle.vScrollKnob = none;

        PagedScrollPane pagedScrollPane = new PagedScrollPane(scrollPaneStyle, 15f);
        pagedScrollPane.setFlingTime(0.1f);
        for (int i = 0; i < 15; i++) {
            TextureRegionDrawable drawable = new TextureRegionDrawable(res.getTexture("help_page_bg"));
            Page page = new Page(drawable, res, i);
            pagedScrollPane.addPage(page);
        }
        table.add(pagedScrollPane).padBottom(1f).size(116f, 72f).fill();

        return table;
    }

    public void show(Stage stage) {
        displayed = true;
        shadow.setVisible(true);

        clearActions();
        pack();
        stage.addActor(shadow);
        stage.addActor(this);
        addAction(sequence(Actions.alpha(0), Actions.fadeIn(0.4f, Interpolation.fade)));

        setPosition(POSITION.x, POSITION.y);
    }

    public void hide() {
        displayed = false;
        shadow.setVisible(false);
        addAction(sequence(fadeOut(0.4f, Interpolation.fade), Actions.removeActor()));
    }

    public boolean isDisplayed() {
        return displayed;
    }

}
