package com.symbol.game.scene.dialog;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.symbol.game.Symbol;
import com.symbol.game.scene.PagedScrollPane;
import com.symbol.game.util.Resources;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class HelpDialog extends Table {

    private static final float TOP_ARROW_OFFSET = 5f;
    private static final float PAGE_SPACING = 15f;
    private static final float PAGE_FLING_TIME = 0.1f;
    private static final float SCROLL_PANE_WIDTH = 116f;
    private static final float SCROLL_PANE_HEIGHT = 72f;

    private Resources res;

    private static final Vector2 POSITION = new Vector2(19f, 8f);
    private Image shadow;
    private boolean displayed;

    private PagedScrollPane pagedScrollPane;
    private ImageButton leftButton;
    private ImageButton rightButton;

    public HelpDialog(final Symbol game) {
        res = game.getRes();
        shadow = new Image(game.getRes().getTexture("shadow"));
        shadow.setVisible(false);

        setBackground(new TextureRegionDrawable(res.getTexture("help_dialog_bg")));
        padTop(TOP_ARROW_OFFSET);

        createLayout();
    }

    private void createLayout() {
        ImageButton.ImageButtonStyle leftStyle = res.getImageButtonStyle("help_dialog_left");
        leftButton = new ImageButton(leftStyle);
        add(leftButton).padLeft(3f);

        add(getInnerTable()).expandX();

        ImageButton.ImageButtonStyle rightStyle = res.getImageButtonStyle("help_dialog_right");
        rightButton = new ImageButton(rightStyle);
        add(rightButton).padRight(3f);

        leftButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) { pagedScrollPane.scrollToLeft(); }
        });

        rightButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) { pagedScrollPane.scrollToRight(); }
        });
    }

    private Table getInnerTable() {
        Table table = new Table();

        Label.LabelStyle titleStyle = res.getLabelStyle(res.getColorFromHexKey("player"));
        Label titleLabel = new Label(res.getString("helpDialogTitle"), titleStyle);
        table.add(titleLabel).left().padBottom(2f).row();

        Drawable none = new TextureRegionDrawable(res.getTexture("default-rect"));
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.background = scrollPaneStyle.corner = scrollPaneStyle.hScroll =
                scrollPaneStyle.hScrollKnob = scrollPaneStyle.vScroll = scrollPaneStyle.vScrollKnob = none;

        pagedScrollPane = new PagedScrollPane(scrollPaneStyle, PAGE_SPACING);
        pagedScrollPane.setFlingTime(PAGE_FLING_TIME);
        pagedScrollPane.addPage(res.getHelpPage("e"));
        pagedScrollPane.addPage(res.getHelpPage("sqrt"));
        pagedScrollPane.addPage(res.getHelpPage("portal"));
        pagedScrollPane.addPage(res.getHelpPage("mirror"));
        pagedScrollPane.addPage(res.getHelpPage("lethal"));
        table.add(pagedScrollPane).padBottom(1f).size(SCROLL_PANE_WIDTH, SCROLL_PANE_HEIGHT).fill();

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
        pagedScrollPane.resetCurrentPage();
        addAction(sequence(fadeOut(0.4f, Interpolation.fade), Actions.removeActor()));
    }

    public void update(float dt) {
        leftButton.setDisabled(pagedScrollPane.getScrollX() <= 0);
        rightButton.setDisabled(pagedScrollPane.getScrollX() >= pagedScrollPane.getMaxX());
    }

    public boolean isDisplayed() {
        return displayed;
    }

}
