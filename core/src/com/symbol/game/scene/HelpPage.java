package com.symbol.game.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.symbol.game.ecs.EntityDetails;
import com.symbol.game.util.Resources;

public class HelpPage extends Table {

    private Resources res;

    private Table container;

    private HelpPage(Resources res) {
        this.res = res;
        setBackground(new TextureRegionDrawable(res.getTexture("help_page_bg")));

        container = new Table();
        container.pack();
        container.setTransform(false);

        ScrollPane scrollPane = new ScrollPane(container, res.getSkin());
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.layout();

        add(scrollPane).size(116f, 72f).fill();
    }

    public HelpPage(Resources res, EntityDetails details) {
        this(res);
        createEntityDetailsLayout(details);
    }

    private void createEntityDetailsLayout(EntityDetails details) {
        Label entityTypeLabel = new Label(details.getEntityType(),
                res.getLabelStyle(getColorForEntityType(details.getEntityType())));
        container.add(entityTypeLabel);
    }

    private Color getColorForEntityType(String entityType) {
        if (entityType.equals("enemy")) return Color.RED;
        else return new Color(114 / 255.f, 184 / 255.f, 1, 1);
    }

}