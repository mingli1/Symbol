package com.symbol.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.symbol.ecs.entity.EntityColor;

import java.util.HashMap;
import java.util.Map;

public class Resources implements Disposable {

    public static final String TOP = "_t";
    public static final String TOP_RIGHT ="_tr";
    public static final String ORBIT = "_orbit";

    public static final String INCORPOREAL = "_ic";

    public static final String TOGGLE_ON = "_on";
    public static final String TOGGLE_OFF = "_off";

    public static final String BRACKET_LEFT = "_left";
    public static final String BRACKET_RIGHT = "_right";

    private static final String BUTTON = "button_";
    private static final String BUTTON_UP = "_up";
    private static final String BUTTON_DOWN = "_down";

    private AssetManager assetManager = new AssetManager();
    private TextureAtlas atlas;
    private Map<String, TextureRegion> textures = new HashMap<String, TextureRegion>();

    private BitmapFont font;

    public Resources() {
        assetManager.load("textures/textures.atlas", TextureAtlas.class);
        assetManager.finishLoading();

        atlas = assetManager.get("textures/textures.atlas", TextureAtlas.class);

        font = new BitmapFont(Gdx.files.internal("font/font.fnt"), atlas.findRegion("font"), false);
        font.setUseIntegerPositions(false);

        load("background");

        loadPlayerAndEnemies();
        loadMapEntities();
        loadProjectiles();
        loadBrackets();
        loadToggles();
        loadColors();
        loadButtons();
    }

    private void loadPlayerAndEnemies() {
        load("player");
        load("e_e");
        load("e_sqrt");
        load("e_exists");
        load("e_sum");
        load("e_big_pi");
        load("e_in");
        load("e_theta");
        load("e_big_omega");
        load("e_njoin");
        load("e_big_phi");
        load("e_percent");
        load("e_percent_orbit");
        load("e_nabla");
        load("e_cintegral");
        for (int i = 0; i < 4; i++) load("e_because" + String.valueOf(i));
    }

    private void loadMapEntities() {
        for (int i = 1; i < 4; i++) load("mplatform" + String.valueOf(i));
        load("approx");
        load("curly_brace_portal");
        load("health_pack");
        load("between");
        load("toggle_square");
    }

    private void loadProjectiles() {
        loadProjectile("p_dot");
        loadProjectile("p_dot_xor");
        loadProjectile("p_angle_bracket");
        loadProjectile("p_xor");
        loadProjectile("p_arrow");
        loadProjectile("p_cup");
        loadProjectile("p_implies");
        loadProjectile("p_ldots");
        loadProjectile("p_large_triangle");
        loadProjectile("p_big_ll");
        loadProjectile("p_ltimes");
        loadProjectile("p_alpha");
        loadProjectile("p_succ");
        loadProjectile("p_because");
    }

    private void loadBrackets() {
        loadBracket("square_bracket");
    }

    private void loadToggles() {
        loadToggle("updownarrow");
        loadToggle("square_switch");
    }

    private void loadColors() {
        load("black");
        load("hp_bar_color");
        load("hp_bar_bg_color");
        load("hp_bar_green");

        load(EntityColor.PLAYER_COLOR);
        load(EntityColor.BETWEEN_COLOR);
        load(EntityColor.SUM_COLOR);
        load(EntityColor.LDOTS_COLOR);
        load(EntityColor.PORTAL_COLOR);
        load(EntityColor.SQUARE_BRACKET_COLOR);
        load(EntityColor.XOR_COLOR);
        load(EntityColor.NATURAL_JOIN_COLOR);
        load(EntityColor.NABLA_COLOR);
        load(EntityColor.SQRT_COLOR);
        load(EntityColor.CUP_COLOR);
        load(EntityColor.ALPHA_COLOR);
        load(EntityColor.BIG_OMEGA_COLOR);
        load(EntityColor.EXISTS_COLOR);
        load(EntityColor.IMPLIES_COLOR);
        load(EntityColor.LARGE_TRIANGLE_COLOR);
        load(EntityColor.IN_COLOR);
        load(EntityColor.LTIMES_COLOR);
        load(EntityColor.PERCENT_COLOR);
        load(EntityColor.ARROW_COLOR);
        load(EntityColor.THETA_COLOR);
        load(EntityColor.ANGLE_BRACKET_COLOR);
        load(EntityColor.BIG_PHI_COLOR);
        load(EntityColor.DOT_COLOR);
        load(EntityColor.CINTEGRAL_COLOR);
        load(EntityColor.SUCC_COLOR);
        load(EntityColor.BECAUSE_COLOR);
        load(EntityColor.BECAUSE_PROJ_COLOR);
    }

    private void loadButtons() {
        loadButton("settings");
        loadButton("left");
        loadButton("right");
        loadButton("jump");
        loadButton("shoot");
    }

    public TextureRegion getTexture(String key) {
        return textures.get(key);
    }

    public TextureRegion getSubProjectileTextureFor(String key) {
        if (key.equals("p_xor")) return getTexture("p_dot_xor");
        return null;
    }

    public BitmapFont getFont() {
        return font;
    }

    public ImageButton.ImageButtonStyle getButtonStyle(String key) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(getTexture(BUTTON + key + BUTTON_UP));
        style.imageDown = new TextureRegionDrawable(getTexture(BUTTON + key + BUTTON_DOWN));
        return style;
    }

    private void load(String key) {
        textures.put(key, atlas.findRegion(key));
    }

    private void loadProjectile(String key) {
        load(key);

        TextureRegion top = atlas.findRegion(key + TOP);
        TextureRegion topRight = atlas.findRegion(key + TOP_RIGHT);

        if (top != null) textures.put(key + TOP, top);
        if (topRight != null) textures.put(key + TOP_RIGHT, topRight);
    }

    private void loadButton(String key) {
        load(BUTTON + key + BUTTON_UP);
        load(BUTTON + key + BUTTON_DOWN);
    }

    private void loadBracket(String key) {
        load(key + BRACKET_LEFT);
        load(key + BRACKET_RIGHT);
    }

    private void loadToggle(String key) {
        load(key + TOGGLE_OFF);
        load(key + TOGGLE_ON);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        atlas.dispose();
    }

}