package com.symbol.game.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.symbol.game.ecs.EntityDetails

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
    private val jsonReader = JsonReader()

    private val atlas: TextureAtlas
    private val strings: JsonValue
    private val colors: JsonValue
    private val entityDetails: JsonValue

    private val entityDetailsMap: MutableMap<String, EntityDetails> = HashMap()

    val skin: Skin
    val font: BitmapFont

    init {
        assetManager.load("textures/textures.atlas", TextureAtlas::class.java)
        assetManager.finishLoading()

        atlas = assetManager.get("textures/textures.atlas", TextureAtlas::class.java)

        strings = jsonReader.parse(Gdx.files.internal("data/strings.json"))
        colors = jsonReader.parse(Gdx.files.internal("data/colors.json"))
        entityDetails = jsonReader.parse(Gdx.files.internal("data/entity_details.json"))
        loadEntityDetails()

        font = BitmapFont(Gdx.files.internal("font/font.fnt"), atlas.findRegion("font"), false)
        font.setUseIntegerPositions(false)

        skin = Skin(atlas)
        skin.add("default-font", font)
        skin.load(Gdx.files.internal("textures/skin.json"))
    }

    fun getTexture(key: String) : TextureRegion? {
        return atlas.findRegion(key)
    }

    fun getString(key: String) : String? {
        return strings.getString(key)
    }

    fun getColor(key: String) : String? {
        return colors.getString(key)
    }

    fun getEntityDetail(id: String) : EntityDetails {
        return entityDetailsMap[id]!!
    }

    fun getSubProjectileTextureFor(key: String) : TextureRegion? {
        if (key == "p_xor") return getTexture("p_dot_xor")
        if (key == "p_dot4") return getTexture("p_dot")
        return null
    }

    fun getImageButtonStyle(key: String) : ImageButton.ImageButtonStyle {
        val style = ImageButton.ImageButtonStyle()
        style.imageUp = TextureRegionDrawable(getTexture(BUTTON + key + BUTTON_UP))
        style.imageDown = TextureRegionDrawable(getTexture(BUTTON + key + BUTTON_DOWN))
        style.imageOver = TextureRegionDrawable(getTexture(BUTTON + key + BUTTON_DOWN))
        return style
    }

    fun getTextButtonStyle(key: String, color: Color = Color.WHITE) : TextButton.TextButtonStyle {
        val style = TextButton.TextButtonStyle()
        style.up = TextureRegionDrawable(getTexture(BUTTON + key + BUTTON_UP))
        style.over = TextureRegionDrawable(getTexture(BUTTON + key + BUTTON_DOWN))
        style.font = font
        style.fontColor = color
        return style
    }

    fun getLabelStyle(color: Color = Color.WHITE) : Label.LabelStyle {
        return Label.LabelStyle(font, color)
    }

    fun getColorFromHexKey(key: String) : Color {
        return Color(Color.valueOf(getColor(key)))
    }

    private fun loadEntityDetails() {
        val root = entityDetails.get("details")
        for (entityDetail in root) {
            val id = entityDetail.getString("id")
            val image = getTexture(entityDetail.getString("image")!!)
            image?.flip(entityDetail.getBoolean("flip"), false)

            entityDetailsMap[id] = EntityDetails(
                    id = id,
                    name = entityDetail.getString("name"),
                    entityType = entityDetail.getString("entityType"),
                    image = image,
                    description = entityDetail.getString("description"),
                    additionalInfo = entityDetail.getString("additionalInfo")
            )
        }
    }

    override fun dispose() {
        assetManager.dispose()
        atlas.dispose()
    }

}