package com.symbol.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Disposable

const val TOP = "_t"
const val TOP_RIGHT ="_tr"
const val ORBIT = "_orbit"

private const val BUTTON = "button_"
private const val BUTTON_UP = "_up"
private const val BUTTON_DOWN = "_down"

class Resources : Disposable {

    private val assetManager: AssetManager = AssetManager()
    private val atlas: TextureAtlas
    private val textures: MutableMap<String, TextureRegion> = HashMap()

    val font: BitmapFont

    init {
        assetManager.load("textures/textures.atlas", TextureAtlas::class.java)
        assetManager.finishLoading()

        atlas = assetManager.get("textures/textures.atlas", TextureAtlas::class.java)

        font = BitmapFont(Gdx.files.internal("font/font.fnt"), atlas.findRegion("font"), false)
        font.setUseIntegerPositions(false)

        textures["background"] = atlas.findRegion("background")

        textures["player"] = atlas.findRegion("player")
        textures["e_e"] = atlas.findRegion("e_e")
        textures["e_sqrt"] = atlas.findRegion("e_sqrt")
        textures["e_exists"] = atlas.findRegion("e_exists")
        textures["e_sum"] = atlas.findRegion("e_sum")
        textures["e_big_pi"] = atlas.findRegion("e_big_pi")
        textures["e_in"] = atlas.findRegion("e_in")
        textures["e_theta"] = atlas.findRegion("e_theta")
        textures["e_big_omega"] = atlas.findRegion("e_big_omega")
        textures["e_njoin"] = atlas.findRegion("e_njoin")
        textures["e_big_phi"] = atlas.findRegion("e_big_phi")
        textures["e_percent"] = atlas.findRegion("e_percent")
        textures["e_percent_orbit"] = atlas.findRegion("e_percent_orbit")

        for (i in 1..3) textures["mplatform$i"] = atlas.findRegion("mplatform$i")
        textures["approx"] = atlas.findRegion("approx")
        textures["curly_brace_portal"] = atlas.findRegion("curly_brace_portal")

        loadProjectile("p_dot")
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

        textures["black"] = atlas.findRegion("black")
        textures["hp_bar_color"] = atlas.findRegion("hp_bar_color")
        textures["hp_bar_bg_color"] = atlas.findRegion("hp_bar_bg_color")
        textures["hp_bar_green"] = atlas.findRegion("hp_bar_green")

        loadButton("settings")
    }

    fun getTexture(key: String): TextureRegion? {
        return textures[key]
    }

    fun getSubProjectileTextureFor(key: String) : TextureRegion? {
        if (key == "p_xor") return getTexture("p_dot_xor")
        return null
    }

    private fun loadProjectile(key: String) {
        textures[key] = atlas.findRegion(key)

        val top = atlas.findRegion(key + TOP)
        val topRight = atlas.findRegion(key + TOP_RIGHT)

        if (top != null) textures[key + TOP] = top
        if (topRight != null) textures[key + TOP_RIGHT] = topRight
    }

    private fun loadButton(key: String) {
        textures[BUTTON + key + BUTTON_UP] = atlas.findRegion(BUTTON + key + BUTTON_UP)
        textures[BUTTON + key + BUTTON_DOWN] = atlas.findRegion(BUTTON + key + BUTTON_DOWN)
    }

    fun getButtonStyle(key: String) : ImageButton.ImageButtonStyle {
        val style = ImageButton.ImageButtonStyle()
        style.imageUp = TextureRegionDrawable(getTexture(BUTTON + key + BUTTON_UP))
        style.imageDown = TextureRegionDrawable(getTexture(BUTTON + key + BUTTON_DOWN))
        return style
    }

    override fun dispose() {
        assetManager.dispose()
        atlas.dispose()
    }

}