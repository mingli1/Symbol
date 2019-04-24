package com.symbol.game.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.symbol.game.data.EntityDetails
import com.symbol.game.data.ImageAlign
import com.symbol.game.data.ImageWrapper
import com.symbol.game.data.TechnicalDetails
import com.symbol.game.map.TILE_SIZE
import com.symbol.game.scene.HelpPage

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
    private val jsonReader = JsonReader()

    private val atlas: TextureAtlas
    private val strings: JsonValue
    private val colors: JsonValue
    private val entityDetails: JsonValue
    private val technicalDetails: JsonValue

    private val helpPages: MutableMap<String, HelpPage> = HashMap()

    val skin: Skin
    val font: BitmapFont
    val invertShader: ShaderProgram

    init {
        assetManager.load("textures/textures.atlas", TextureAtlas::class.java)
        assetManager.finishLoading()

        atlas = assetManager.get("textures/textures.atlas", TextureAtlas::class.java)

        strings = jsonReader.parse(Gdx.files.internal("data/strings.json"))
        colors = jsonReader.parse(Gdx.files.internal("data/colors.json"))
        entityDetails = jsonReader.parse(Gdx.files.internal("data/entity_details.json"))
        technicalDetails = jsonReader.parse(Gdx.files.internal("data/technical_details.json"))

        font = BitmapFont(Gdx.files.internal("font/font.fnt"), atlas.findRegion("font"), false)
        font.setUseIntegerPositions(false)

        skin = Skin(atlas)
        skin.add("default-font", font)
        skin.load(Gdx.files.internal("textures/skin.json"))

        ShaderProgram.pedantic = false
        invertShader = ShaderProgram(Gdx.files.internal("shader/invert.vsh"),
                Gdx.files.internal("shader/invert.fsh"))

        loadHelpPages()
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

        val disabled = getTexture(BUTTON + key + BUTTON_DISABLED)
        if (disabled != null) style.imageDisabled = TextureRegionDrawable(disabled)

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

    fun getHelpPage(key: String) : HelpPage? {
        return helpPages[key]
    }

    private fun loadHelpPages() {
        val entityDetailsRoot = entityDetails.get("details")
        for (entityDetail in entityDetailsRoot) {
            val id = entityDetail.getString("id")
            val imageStr = entityDetail.getString("image")!!
            var image: TextureRegion?

            image = if (imageStr.contains("tileset", true)) {
                val rc = imageStr.substring(7, imageStr.length).split("_")
                getTexture("tileset")!!.split(TILE_SIZE, TILE_SIZE)[rc[0].toInt()][rc[1].toInt()]
            } else {
                getTexture(imageStr)
            }
            image?.flip(entityDetail.getBoolean("flip"), false)

            val entityDetails = EntityDetails(
                    id = id,
                    name = entityDetail.getString("name"),
                    entityType = entityDetail.getString("entityType"),
                    image = image,
                    description = entityDetail.getString("description"),
                    additionalInfo = entityDetail.getString("additionalInfo")
            )

            helpPages[id] = HelpPage(this, entityDetails)
        }

        val technicalDetailsRoot = technicalDetails.get("details")
        for (technicalDetail in technicalDetailsRoot) {
            val id = technicalDetail.getString("id")
            val technicalDetails = TechnicalDetails(
                    id = id,
                    title = technicalDetail.getString("title"),
                    imageSize = technicalDetail.getInt("imageSize")
            )

            val texts = technicalDetail.get("texts")
            for (text in texts) {
                technicalDetails.texts.add(text.asString())
            }

            val images = technicalDetail.get("images")
            for (image in images) {
                val wrapper = ImageWrapper(getTexture(image.getString("image")),
                        ImageAlign.valueOf(image.getString("align")))
                technicalDetails.images.add(wrapper)
            }

            helpPages[id] = HelpPage(this, technicalDetails)
        }
    }

    override fun dispose() {
        assetManager.dispose()
        atlas.dispose()
    }

}