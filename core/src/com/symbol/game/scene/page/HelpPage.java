package com.symbol.game.scene.page;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.symbol.game.data.EntityDetails;
import com.symbol.game.data.ImageWrapper;
import com.symbol.game.data.TechnicalDetails;
import com.symbol.game.util.Resources;

public class HelpPage extends Table implements Page {

    private static final float ENTITY_DETAILS_IMAGE_SIZE = 32f;
    private static final float SCROLL_PANE_WIDTH = 106f;
    private static final float SCROLL_PANE_HEIGHT = 64;
    private static final float LAYOUT_WIDTH = 96f;

    private static final Color ENTITY_TYPE_ENEMY = new Color(220 / 255.f, 0f, 0f, 1f);
    private static final Color ENTITY_TYPE_TERRAIN = new Color(132 / 255.f, 196 / 255.f, 125 / 255.f, 1);
    private static final Color ENTITY_TYPE_MAP_OBJECT = new Color(114 / 255.f, 184 / 255.f, 1, 1);
    private static final Color TECHNICAL_DETAILS_TITLE = new Color(65 / 255.f, 65 / 255.f, 130 / 255.f, 1);

    private Resources res;

    private Table container;
    private ScrollPane scrollPane;

    private boolean seen = false;

    private HelpPage(Resources res) {
        this.res = res;
        setBackground(new TextureRegionDrawable(res.getTexture("help_page_bg")));

        container = new Table();
        container.pack();
        container.setTransform(false);

        scrollPane = new ScrollPane(container, res.getSkin());
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.layout();
        scrollPane.setFlingTime(0f);
        scrollPane.setOverscroll(false, false);

        add(scrollPane).size(SCROLL_PANE_WIDTH, SCROLL_PANE_HEIGHT).fill();
    }

    public HelpPage(Resources res, EntityDetails details) {
        this(res);
        createEntityDetailsLayout(details);
    }

    public HelpPage(Resources res, TechnicalDetails details) {
        this(res);
        createLayoutForTechnicalDetails(details);
    }

    @Override
    public void reset() {
        scrollPane.setScrollY(0f);
    }

    private void createEntityDetailsLayout(EntityDetails details) {
        Label entityTypeLabel = new Label(details.getName(),
                res.getLabelStyle(getColorForEntityType(details.getEntityType())));
        container.add(entityTypeLabel).left().row();

        addDivider(3f, 3f);

        Table middleTable = new Table();

        Image entityImage = new Image(details.getImage());
        entityImage.setScaling(Scaling.fit);
        middleTable.add(entityImage)
                .size(ENTITY_DETAILS_IMAGE_SIZE, ENTITY_DETAILS_IMAGE_SIZE)
                .fill()
                .padRight(4f)
                .top();

        Label descriptionLabel = new Label(details.getDescription(),
                res.getLabelStyle(Color.BLACK));
        descriptionLabel.setWrap(true);
        descriptionLabel.setAlignment(Align.topLeft);
        middleTable.add(descriptionLabel)
                .width(64f)
                .fill()
                .padLeft(2f);

        container.add(middleTable).expand().top().row();

        if (!details.getAdditionalInfo().equals("")) {
            addDivider(4f, 3f);
            Label tipLabel = new Label("tip: " + details.getAdditionalInfo(),
                    res.getLabelStyle(new Color(80 / 255.f, 127 / 255.f, 175 / 255.f, 1)));
            tipLabel.setWrap(true);
            tipLabel.setAlignment(Align.topLeft);
            container.add(tipLabel).width(LAYOUT_WIDTH).fill().left();
        }
    }

    private Color getColorForEntityType(String entityType) {
        switch (entityType) {
            case "enemy":
                return ENTITY_TYPE_ENEMY;
            case "terrain":
                return ENTITY_TYPE_TERRAIN;
            default:
                return ENTITY_TYPE_MAP_OBJECT;
        }
    }

    private void createLayoutForTechnicalDetails(TechnicalDetails details) {
        Label title = new Label(details.getTitle(), res.getLabelStyle(TECHNICAL_DETAILS_TITLE));
        container.add(title).left().row();

        addDivider(3f, 3f);

        Label.LabelStyle textStyle = res.getLabelStyle(Color.BLACK);

        int diff = details.getTexts().size - details.getImages().size;
        if (diff != 0) {
            for (int i = 0; i < diff; i++) {
                Label text = new Label(details.getTexts().get(i), textStyle);
                text.setWrap(true);
                text.setAlignment(Align.topLeft);
                container.add(text).width(LAYOUT_WIDTH + 10).fill().left().row();
                addDivider(3f, 3f);
            }
        }

        for (int i = diff; i < details.getTexts().size; i++) {
            Table table = new Table();
            ImageWrapper wrapper = details.getImages().get(i - diff);

            Image image = new Image(wrapper.getImage());
            image.setScaling(Scaling.fit);
            Label text = new Label(details.getTexts().get(i), textStyle);
            text.setWrap(true);
            text.setAlignment(Align.topLeft);

            switch (wrapper.getAlignment()) {
                case Left:
                    table.add(image).size(details.getImageSize(), details.getImageSize())
                            .fill().padRight(4f);
                    table.add(text).width(LAYOUT_WIDTH - details.getImageSize()).fill().padLeft(2f);
                    break;
                case Right:
                    table.add(text).width(LAYOUT_WIDTH - details.getImageSize()).fill().padRight(2f);
                    table.add(image).size(details.getImageSize(), details.getImageSize())
                            .fill().padRight(4f);
                    break;
                case Top:
                    table.add(image).size(details.getImageSize(), details.getImageSize())
                            .fill().padBottom(4f).row();
                    table.add(text).width(LAYOUT_WIDTH).fill();
                    break;
            }
            container.add(table).expand().top().row();

            if (i != details.getTexts().size - 1) addDivider(3f, 3f);
        }
    }

    private void addDivider(float spaceTop, float spaceBottom) {
        container.add(new Image(res.getTexture("page_divider")))
                .spaceTop(spaceTop)
                .spaceBottom(spaceBottom)
                .left()
                .row();
    }

    @Override
    public void notifySeen() {
        seen = true;
    }

    @Override
    public boolean hasSeen() {
        return seen;
    }

    @Override
    public Actor getActor() { return this; }

}