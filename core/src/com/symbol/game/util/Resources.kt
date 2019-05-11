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
import com.symbol.game.data.*
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
    private val atlas: TextureAtlas

    val skin: Skin
    val invertShader: ShaderProgram
    private val font: BitmapFont

    private val jsonReader = JsonReader()
    private val maps: JsonValue
    private val strings: JsonValue
    private val colors: JsonValue
    private val entityDetails: JsonValue
    private val technicalDetails: JsonValue

    val mapDatas = mutableListOf<MapData>()
    private val helpPages: MutableMap<String, HelpPage> = HashMap()

    init {
        assetManager.load("textures/textures.atlas", TextureAtlas::class.java)
        assetManager.finishLoading()

        atlas = assetManager.get("textures/textures.atlas", TextureAtlas::class.java)

        with (jsonReader) {
            maps = parse(Gdx.files.internal("data/maps.json"))
            strings = parse(Gdx.files.internal("data/strings.json"))
            colors = parse(Gdx.files.internal("data/colors.json"))
            entityDetails = parse(Gdx.files.internal("data/entity_details.json"))
            technicalDetails = parse(Gdx.files.internal("data/technical_details.json"))
        }

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

        loadMapDatas()
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

    private fun loadMapDatas() {
        maps["maps"].forEachIndexed { index, data ->
            val mapData = MapData(id = index, name = data.getString("name"))
            mapDatas.add(mapData)
        }
    }

    private fun loadHelpPages() {
        entityDetails["details"].forEach {
            it.run {
                val id = getString("id")
                val imageStr = getString("image")!!

                val image = if (imageStr.contains("tileset", true)) {
                    val rc = imageStr.substring(7, imageStr.length).split("_")
                    getTexture("tileset")!!.split(TILE_SIZE, TILE_SIZE)[rc[0].toInt()][rc[1].toInt()]
                } else {
                    getTexture(imageStr)
                }
                image?.flip(getBoolean("flip"), false)

                val entityDetails = EntityDetails(
                        id = id,
                        name = getString("name"),
                        entityType = getString("entityType"),
                        image = image,
                        description = getString("description"),
                        additionalInfo = getString("additionalInfo")
                )

                helpPages[id] = HelpPage(this@Resources, entityDetails)
            }
        }

        technicalDetails["details"].forEach { detail ->
            detail.run {
                val id = getString("id")
                val technicalDetails = TechnicalDetails(
                        id = id,
                        title = getString("title"),
                        imageSize = getInt("imageSize")
                )

                this["texts"].forEach { technicalDetails.texts.add(it.asString()) }
                this["images"].forEach {
                    val wrapper = ImageWrapper(getTexture(it.getString("image")),
                            ImageAlign.valueOf(it.getString("align")))
                    technicalDetails.images.add(wrapper)
                }

                helpPages[id] = HelpPage(this@Resources, technicalDetails)
            }
        }
    }

    override fun dispose() {
        assetManager.dispose()
        atlas.dispose()
    }

}