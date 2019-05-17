package com.symbol.game.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.*

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
private const val BUTTON_DISABLED = "_disabled"

class Resources : Disposable {

    private val assetManager = AssetManager()
    private val atlas: TextureAtlas

    val skin: Skin
    val invertShader: ShaderProgram
    private val font: BitmapFont

    init {
        assetManager.load("textures/textures.atlas", TextureAtlas::class.java)
        assetManager.finishLoading()

        atlas = assetManager.get("textures/textures.atlas", TextureAtlas::class.java)

        font = BitmapFont(Gdx.files.internal("font/font.fnt"), atlas.findRegion("font"), false).apply {
            setUseIntegerPositions(false)
        }

        skin = Skin(atlas).apply {
            add("default-font", font)
            load(Gdx.files.internal("textures/skin.json"))
        }

        ShaderProgram.pedantic = false
        invertShader = ShaderProgram(Gdx.files.internal("shader/invert.vsh"),
                Gdx.files.internal("shader/invert.fsh"))
    }

    fun getTexture(key: String) : TextureRegion? = atlas.findRegion(key)

    fun getNinePatch(key: String) : NinePatch? = atlas.createPatch(key)

    fun getSubProjectileTextureFor(key: String) : TextureRegion? =
            when (key) {
                "p_xor" -> getTexture("p_dot_xor")
                "p_dot4" -> getTexture("p_dot")
                else -> null
            }

    fun getImageButtonStyle(key: String) : ImageButton.ImageButtonStyle {
        val style = ImageButton.ImageButtonStyle().apply {
            imageUp = TextureRegionDrawable(getTexture(BUTTON + key + BUTTON_UP))
            imageDown = TextureRegionDrawable(getTexture(BUTTON + key + BUTTON_DOWN))
            imageOver = TextureRegionDrawable(getTexture(BUTTON + key + BUTTON_DOWN))
        }

        getTexture(BUTTON + key + BUTTON_DISABLED)?.let {
            style.imageDisabled = TextureRegionDrawable(it)
        }

        return style
    }

    fun getTextButtonStyle(key: String, color: Color = Color.WHITE) : TextButton.TextButtonStyle {
        return TextButton.TextButtonStyle().apply {
            up = TextureRegionDrawable(getTexture(BUTTON + key + BUTTON_UP))
            over = TextureRegionDrawable(getTexture(BUTTON + key + BUTTON_DOWN))
            font = this@Resources.font
            fontColor = color
        }
    }

    fun getLabelStyle(color: Color = Color.WHITE) = Label.LabelStyle(font, color)

    override fun dispose() {
        assetManager.dispose()
        atlas.dispose()
        skin.dispose()
        font.dispose()
        invertShader.dispose()
    }

}