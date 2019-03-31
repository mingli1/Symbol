package com.symbol.game.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Disposable
import com.symbol.game.ecs.entity.EntityColor

const val TOP = "_t"
const val TOP_RIGHT ="_tr"
const val ORBIT = "_orbit"

const val INCORPOREAL = "_ic"
const val STATUS_EFFECT = "se_"

const val TOGGLE_ON = "_on"
const val TOGGLE_OFF = "_off"

const val BRACKET_LEFT = "_left"
const val BRACKET_RIGHT = "_right"

private const val BUTTON = "button_"
private const val BUTTON_UP = "_up"
private const val BUTTON_DOWN = "_down"

class Resources : Disposable {

    private val assetManager = AssetManager()
    private val atlas: TextureAtlas
    private val textures: MutableMap<String, TextureRegion> = HashMap()

    val font: BitmapFont

    init {
        assetManager.load("textures/textures.atlas", TextureAtlas::class.java)
        assetManager.finishLoading()

        atlas = assetManager.get("textures/textures.atlas", TextureAtlas::class.java)

        font = BitmapFont(Gdx.files.internal("font/font.fnt"), atlas.findRegion("font"), false)
        font.setUseIntegerPositions(false)

        load("background")

        loadPlayerAndEnemies()
        loadMapEntities()
        loadProjectiles()
        loadStatusEffects()
        loadBrackets()
        loadToggles()
        loadColors()
        loadButtons()
        loadImages()
    }

    private fun loadPlayerAndEnemies() {
        load("player")
        load("e_e")
        load("e_sqrt")
        load("e_exists")
        load("e_sum")
        load("e_big_pi")
        load("e_in")
        load("e_theta")
        load("e_big_omega")
        load("e_njoin")
        load("e_big_phi")
        load("e_percent")
        load("e_percent_orbit")
        load("e_nabla")
        load("e_cintegral")
        for (i in 0..3) load("e_because$i")
    }

    private fun loadMapEntities() {
        for (i in 1..3) load("mplatform$i")
        load("approx")
        load("curly_brace_portal")
        load("health_pack")
        load("between")
        load("toggle_square")
        for (i in 1..3) load("forcefield$i")
        load("damage_boost")
    }

    private fun loadProjectiles() {
        loadProjectile("p_dot")
        for (i in 2..4) loadProjectile("p_dot$i")
        loadProjectile("p_dot_xor")
        loadProjectile("p_angle_bracket")
        loadProjectile("p_xor")
        loadProjectile("p_arrow")
        loadProjectile("p_cup")
        loadProjectile("p_implies")
        loadProjectile("p_ldots")
        loadProjectile("p_large_triangle")
        loadProjectile("p_big_ll")
        loadProjectile("p_ltimes")
        loadProjectile("p_alpha")
        loadProjectile("p_succ")
        loadProjectile("p_because")
    }

    private fun loadStatusEffects() {
        load(STATUS_EFFECT + "stun")
        load(STATUS_EFFECT + "snare")
        load(STATUS_EFFECT + "slow")
        load(STATUS_EFFECT + "grounded")
        load(STATUS_EFFECT + "speed_boost_right")
        load(STATUS_EFFECT + "speed_boost_left")
        load(STATUS_EFFECT + "jump_boost")
        load(STATUS_EFFECT + "last_stand")
        load(STATUS_EFFECT + "damage_boost")
    }

    private fun loadBrackets() {
        loadBracket("square_bracket")
    }

    private fun loadToggles() {
        loadToggle("updownarrow")
        loadToggle("square_switch")
    }

    private fun loadColors() {
        load("black")
        load("hp_bar_color")
        load("hp_bar_bg_color")
        load("hp_bar_green")
        load("hp_bar_yellow")
        load("hp_bar_orange")

        load(EntityColor.PLAYER_COLOR)
        load(EntityColor.BETWEEN_COLOR)
        load(EntityColor.SUM_COLOR)
        load(EntityColor.LDOTS_COLOR)
        load(EntityColor.PORTAL_COLOR)
        load(EntityColor.SQUARE_BRACKET_COLOR)
        load(EntityColor.XOR_COLOR)
        load(EntityColor.NATURAL_JOIN_COLOR)
        load(EntityColor.NABLA_COLOR)
        load(EntityColor.SQRT_COLOR)
        load(EntityColor.CUP_COLOR)
        load(EntityColor.ALPHA_COLOR)
        load(EntityColor.BIG_OMEGA_COLOR)
        load(EntityColor.EXISTS_COLOR)
        load(EntityColor.IMPLIES_COLOR)
        load(EntityColor.LARGE_TRIANGLE_COLOR)
        load(EntityColor.IN_COLOR)
        load(EntityColor.LTIMES_COLOR)
        load(EntityColor.PERCENT_COLOR)
        load(EntityColor.ARROW_COLOR)
        load(EntityColor.THETA_COLOR)
        load(EntityColor.ANGLE_BRACKET_COLOR)
        load(EntityColor.BIG_PHI_COLOR)
        load(EntityColor.DOT_COLOR)
        load(EntityColor.CINTEGRAL_COLOR)
        load(EntityColor.SUCC_COLOR)
        load(EntityColor.BECAUSE_COLOR)
        load(EntityColor.BECAUSE_PROJ_COLOR)
        load(EntityColor.DOT2_COLOR)
        load(EntityColor.DOT3_COLOR)
        load(EntityColor.DOT4_COLOR)
    }

    private fun loadButtons() {
        loadButton("settings")
        loadButton("left")
        loadButton("right")
        loadButton("jump")
        loadButton("shoot")
    }

    private fun loadImages() {
        load("player_hp_icon")
        load("player_hp_icon_yellow")
        load("player_hp_icon_orange")
        load("player_hp_icon_red")
        load("charge_bar_icon")
        for (i in 2..4) load("charge_bar_icon$i")
    }

    fun getTexture(key: String): TextureRegion? {
        return textures[key]
    }

    fun getSubProjectileTextureFor(key: String) : TextureRegion? {
        if (key == "p_xor") return getTexture("p_dot_xor")
        if (key == "p_dot4") return getTexture("p_dot")
        return null
    }

    fun getButtonStyle(key: String) : ImageButton.ImageButtonStyle {
        val style = ImageButton.ImageButtonStyle()
        style.imageUp = TextureRegionDrawable(getTexture(BUTTON + key + BUTTON_UP))
        style.imageDown = TextureRegionDrawable(getTexture(BUTTON + key + BUTTON_DOWN))
        return style
    }

    private fun load(key: String) {
        textures[key] = atlas.findRegion(key)
    }

    private fun loadProjectile(key: String) {
        load(key)

        val top = atlas.findRegion(key + TOP)
        val topRight = atlas.findRegion(key + TOP_RIGHT)

        if (top != null) textures[key + TOP] = top
        if (topRight != null) textures[key + TOP_RIGHT] = topRight
    }

    private fun loadButton(key: String) {
        load(BUTTON + key + BUTTON_UP)
        load(BUTTON + key + BUTTON_DOWN)
    }

    private fun loadBracket(key: String) {
        load(key + BRACKET_LEFT)
        load(key + BRACKET_RIGHT)
    }

    private fun loadToggle(key: String) {
        load(key + TOGGLE_OFF)
        load(key + TOGGLE_ON)
    }

    override fun dispose() {
        assetManager.dispose()
        atlas.dispose()
    }

}