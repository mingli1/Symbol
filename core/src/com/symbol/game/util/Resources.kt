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
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.symbol.game.data.EntityDetails
import com.symbol.game.data.ImageAlign
import com.symbol.game.data.ImageWrapper
import com.symbol.game.data.TechnicalDetails
import com.symbol.game.map.TILE_SIZE
import com.symbol.game.scene.page.HelpPage

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
    val invertShader: ShaderProgram
    private val font: BitmapFont

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

    fun getTexture(key: String) : TextureRegion? = atlas.findRegion(key)

    fun getNinePatch(key: String) : NinePatch? = atlas.createPatch(key)

    fun getString(key: String) : String? = strings.getString(key)

    fun getColor(key: String) : String? = colors.getString(key)

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

    fun getColorFromHexKey(key: String) = Color(Color.valueOf(getColor(key)))

    fun getHelpPage(key: String) : HelpPage? = helpPages[key]

    private fun loadHelpPages() {
        val entityDetailsRoot = entityDetails["details"]
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

        val technicalDetailsRoot = technicalDetails["details"]
        for (technicalDetail in technicalDetailsRoot) {
            val id = technicalDetail.getString("id")
            val technicalDetails = TechnicalDetails(
                    id = id,
                    title = technicalDetail.getString("title"),
                    imageSize = technicalDetail.getInt("imageSize")
            )

            val texts = technicalDetail["texts"]
            for (text in texts) {
                technicalDetails.texts.add(text.asString())
            }

            val images = technicalDetail["images"]
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