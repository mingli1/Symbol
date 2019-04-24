package com.symbol.game.scene;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.symbol.game.Symbol;
import com.symbol.game.ecs.Mapper;
import com.symbol.game.ecs.component.HealthComponent;
import com.symbol.game.ecs.component.PlayerComponent;
import com.symbol.game.scene.dialog.HelpDialog;
import com.symbol.game.scene.dialog.PauseDialog;

import static com.symbol.game.ecs.entity.PlayerKt.PLAYER_TIER_ONE_ATTACK_TIME;
import static com.symbol.game.ecs.entity.PlayerKt.PLAYER_TIER_THREE_ATTACK_TIME;
import static com.symbol.game.ecs.entity.PlayerKt.PLAYER_TIER_TWO_ATTACK_TIME;

public class Hud extends Scene {

    private static final Vector2 HP_BAR_POSITION = new Vector2(16, 107f);
    private static final float HP_BAR_WIDTH = 44f;
    private static final float HP_BAR_HEIGHT = 4;

    private static final Vector2 CHARGE_BAR_POSITION = new Vector2(16, 97.5f);
    private static final float CHARGE_BAR_WIDTH = HP_BAR_WIDTH;
    private static final float CHARGE_BAR_HEIGHT = 2;

    private static final float HP_BAR_DECAY_RATE = 18.f;
    private static final float CHARGE_BAR_ACTIVATION_TIME = PLAYER_TIER_ONE_ATTACK_TIME;
    private static final float MAX_CHARGE = PLAYER_TIER_THREE_ATTACK_TIME;
    private static final float BAR_ONE_OFFSET = CHARGE_BAR_WIDTH * (PLAYER_TIER_ONE_ATTACK_TIME / MAX_CHARGE);
    private static final float BAR_TWO_OFFSET = CHARGE_BAR_WIDTH * (PLAYER_TIER_TWO_ATTACK_TIME / MAX_CHARGE);

    private static final float HP_BAR_YELLOW_THRESHOLD = 0.5f;
    private static final float HP_BAR_ORANGE_THRESHOLD = 0.3f;
    private static final float HP_BAR_RED_THRESHOLD = 0.15f;

    private static final Vector2 HELP_BUTTON_POSITION = new Vector2(161f, 104f);

    private Entity player;

    private Table root;

    private float hpBarWidth;
    private float decayingHpBarWidth;
    private boolean startHpBarDecay = false;
    private boolean damaged = true;
    private TextureRegion hpBarColor;
    private Image hpBarIcon;
    private TextureRegionDrawable hpBarIconGreen;
    private TextureRegionDrawable hpBarIconYellow;
    private TextureRegionDrawable hpBarIconOrange;
    private TextureRegionDrawable hpBarIconRed;

    private float chargeBarWidth;
    private Image chargeBarIcon;
    private TextureRegionDrawable chargeBarTiers[] = new TextureRegionDrawable[4];

    private HelpDialog helpDialog;
    private PauseDialog pauseDialog;

    private ImageButton helpButton;
    private Image helpButtonAlert;

    public Hud(final Symbol game, Entity player, Stage stage, Viewport viewport) {
        super(game, stage, viewport);
        this.player = player;

        root = new Table();
        root.setFillParent(true);
        root.setRound(false);
        root.top();
        stage.addActor(root);

        hpBarColor = game.getRes().getTexture("hp_bar_green");

        pauseDialog = new PauseDialog(game);

        createHealthBar();
        createHelpButton();
        createHelpDialog();
        createSettingsButton();
        createChargeBar();
        createHelpButtonAlert();

        stage.addActor(game.fps);
    }

    private void createHealthBar() {
        hpBarIconGreen = new TextureRegionDrawable(game.getRes().getTexture("player_hp_icon"));
        hpBarIconYellow = new TextureRegionDrawable(game.getRes().getTexture("player_hp_icon_yellow"));
        hpBarIconOrange = new TextureRegionDrawable(game.getRes().getTexture("player_hp_icon_orange"));
        hpBarIconRed = new TextureRegionDrawable(game.getRes().getTexture("player_hp_icon_red"));

        hpBarIcon = new Image(hpBarIconGreen);
        root.add(hpBarIcon).pad(6f, 4f, 3f, 4f);
    }

    private void createHelpButton() {
        ImageButton.ImageButtonStyle style = game.getRes().getImageButtonStyle("help");
        helpButton = new ImageButton(style);
        helpButton.setPosition(HELP_BUTTON_POSITION.x, HELP_BUTTON_POSITION.y);
        stage.addActor(helpButton);

        helpButton.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (pointer == 0) {
                    if (helpDialog.isDisplayed()) hideHelpDialog();
                    else showHelpDialog();
                }
            }
        });
    }

    private void createHelpButtonAlert() {
        helpButtonAlert = new Image(game.getRes().getTexture("button_alert"));
        helpButtonAlert.setTouchable(Touchable.disabled);
        helpButtonAlert.setPosition(171f, 112f);
        stage.addActor(helpButtonAlert);
    }

    private void createHelpDialog() {
        helpDialog = new HelpDialog(game, this);
    }

    private void createSettingsButton() {
        ImageButton.ImageButtonStyle style = game.getRes().getImageButtonStyle("settings");
        ImageButton settingsButton = new ImageButton(style);
        root.add(settingsButton).expandX().right().padRight(4f).padTop(3f);

        settingsButton.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (pointer == 0) {
                    pauseDialog.show(stage);
                    game.getGameScreen().notifyPause();
                }
            }
        });
    }

    private void createChargeBar() {
        chargeBarTiers[0] = new TextureRegionDrawable(game.getRes().getTexture("charge_bar_icon"));
        for (int i = 2; i <= 4; i++) {
            chargeBarTiers[i - 1] = new TextureRegionDrawable(game.getRes().getTexture("charge_bar_icon" + i));
        }

        chargeBarIcon = new Image(chargeBarTiers[0]);
        chargeBarIcon.setVisible(false);
        root.row();
        root.add(chargeBarIcon);
    }

    @Override
    public void update(float dt) {
        updateStatusBars(dt);
    }

    @Override
    public void render(float dt) {
        if (helpDialog.isDisplayed()) helpDialog.update(dt);
        renderHpBar();
        renderChargeBar();
    }

    private void updateStatusBars(float dt) {
        HealthComponent health = Mapper.INSTANCE.getHEALTH_MAPPER().get(player);
        float hpPercentage = (float) health.getHp() / health.getMaxHp();
        hpBarWidth = HP_BAR_WIDTH * hpPercentage;
        if (health.getHpChange()) {
            decayingHpBarWidth = HP_BAR_WIDTH * ((float) Math.abs(health.getHpDelta()) / health.getMaxHp());
            startHpBarDecay = true;
            damaged = health.getHpDelta() < 0;
            health.setHpChange(false);
        }
        if (startHpBarDecay) {
            decayingHpBarWidth -= HP_BAR_DECAY_RATE * dt;
            if (decayingHpBarWidth <= 0) {
                decayingHpBarWidth = 0;
                startHpBarDecay = false;
            }
        }

        if (hpPercentage <= HP_BAR_RED_THRESHOLD) {
            hpBarColor = game.getRes().getTexture("hp_bar_color");
            hpBarIcon.setDrawable(hpBarIconRed);
        }
        else if (hpPercentage <= HP_BAR_ORANGE_THRESHOLD) {
            hpBarColor = game.getRes().getTexture("hp_bar_orange");
            hpBarIcon.setDrawable(hpBarIconOrange);
        }
        else if (hpPercentage <= HP_BAR_YELLOW_THRESHOLD) {
            hpBarColor = game.getRes().getTexture("hp_bar_yellow");
            hpBarIcon.setDrawable(hpBarIconYellow);
        }
        else {
            hpBarColor = game.getRes().getTexture("hp_bar_green");
            hpBarIcon.setDrawable(hpBarIconGreen);
        }

        PlayerComponent playerComp = Mapper.INSTANCE.getPLAYER_MAPPER().get(player);
        if (playerComp.getChargeTime() >= CHARGE_BAR_ACTIVATION_TIME) {
            if (!chargeBarIcon.isVisible()) chargeBarIcon.setVisible(true);
            float currentCharge = playerComp.getChargeTime() - CHARGE_BAR_ACTIVATION_TIME;
            chargeBarWidth = CHARGE_BAR_WIDTH * (currentCharge / MAX_CHARGE);

            if (chargeBarWidth > CHARGE_BAR_WIDTH) chargeBarWidth = CHARGE_BAR_WIDTH;
            chargeBarIcon.setDrawable(chargeBarTiers[playerComp.getChargeIndex() - 1]);
        }
        else {
            if (chargeBarIcon.isVisible()) chargeBarIcon.setVisible(false);
            if (chargeBarWidth != 0f) chargeBarWidth = 0f;
        }
    }

    private void renderHpBar() {
        game.getBatch().draw(game.getRes().getTexture("black"), HP_BAR_POSITION.x, HP_BAR_POSITION.y,
                HP_BAR_WIDTH + 2, HP_BAR_HEIGHT + 2);
        game.getBatch().draw(game.getRes().getTexture("hp_bar_bg_color"), HP_BAR_POSITION.x + 1, HP_BAR_POSITION.y + 1,
                HP_BAR_WIDTH, HP_BAR_HEIGHT);
        game.getBatch().draw(hpBarColor, HP_BAR_POSITION.x + 1, HP_BAR_POSITION.y + 1,
                hpBarWidth, HP_BAR_HEIGHT);

        if (startHpBarDecay) {
            if (damaged) {
                game.getBatch().draw(game.getRes().getTexture("hp_bar_color"),
                        HP_BAR_POSITION.x + 1 + hpBarWidth, HP_BAR_POSITION.y + 1,
                        decayingHpBarWidth, HP_BAR_HEIGHT);
            }
            else {
                game.getBatch().draw(game.getRes().getTexture("hp_bar_heal_color"),
                        HP_BAR_POSITION.x + 1 + hpBarWidth - decayingHpBarWidth,
                        HP_BAR_POSITION.y + 1, decayingHpBarWidth, HP_BAR_HEIGHT);
            }
        }
    }

    private void renderChargeBar() {
        PlayerComponent playerComp = Mapper.INSTANCE.getPLAYER_MAPPER().get(player);
        if (playerComp.getChargeTime() >= CHARGE_BAR_ACTIVATION_TIME) {
            game.getBatch().draw(game.getRes().getTexture("black"), CHARGE_BAR_POSITION.x, CHARGE_BAR_POSITION.y,
                    CHARGE_BAR_WIDTH + 2, CHARGE_BAR_HEIGHT + 2);
            game.getBatch().draw(game.getRes().getTexture("hp_bar_bg_color"), CHARGE_BAR_POSITION.x + 1, CHARGE_BAR_POSITION.y + 1,
                    CHARGE_BAR_WIDTH, CHARGE_BAR_HEIGHT);

            String hex = playerComp.getChargeIndex() == 1 ? game.getRes().getColor("p_dot")
                    : game.getRes().getColor("p_dot" + playerComp.getChargeIndex());
            game.getBatch().draw(game.getRes().getTexture(hex), CHARGE_BAR_POSITION.x + 1, CHARGE_BAR_POSITION.y + 1,
                    chargeBarWidth, CHARGE_BAR_HEIGHT);

            game.getBatch().draw(game.getRes().getTexture("black"),
                    CHARGE_BAR_POSITION.x + 1 + BAR_ONE_OFFSET, CHARGE_BAR_POSITION.y + 1, 1, CHARGE_BAR_HEIGHT);
            game.getBatch().draw(game.getRes().getTexture("black"),
                    CHARGE_BAR_POSITION.x + 1 + BAR_TWO_OFFSET, CHARGE_BAR_POSITION.y + 1, 1, CHARGE_BAR_HEIGHT);
        }
    }

    public Dialog getPauseDialog() {
        return pauseDialog;
    }

    public Table getHelpDialog() {
        return helpDialog;
    }

    public void toggleHelpButtonAlert(boolean toggle) {
        helpButtonAlert.setVisible(toggle);
    }

    public void showHelpDialog() {
        helpDialog.show(stage);
        helpButton.setZIndex(stage.getActors().size + 1);
        helpButtonAlert.setZIndex(stage.getActors().size + 1);
        game.getGameScreen().notifyPause();
    }

    public void hideHelpDialog() {
        helpDialog.hide();
        helpButton.setZIndex(0);
        helpButtonAlert.setZIndex(1);
        game.getGameScreen().notifyResume();
    }

    public boolean hasHelpPageNotSeen() {
        return helpDialog.hasPageNotSeen();
    }

    public void setHelpPages(Array<Page> pages) {
        helpDialog.setHelpPages(pages);
        toggleHelpButtonAlert(helpDialog.hasPageNotSeen());
    }

    @Override
    public void dispose() {}

}
