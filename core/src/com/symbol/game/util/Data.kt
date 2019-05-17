package com.symbol.game.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter
import com.symbol.game.data.*
import com.symbol.game.map.TILE_SIZE
import com.symbol.game.scene.page.HelpPage

private const val PREFERENCES_TAG = "SYMBOL_GAME_SAVE"
private const val SAVE_KEY = "SAVE_KEY"

class Data(private val res: Resources) {

    private val jsonReader = JsonReader()
    private val maps: JsonValue
    private val strings: JsonValue
    private val colors: JsonValue
    private val entityDetails: JsonValue
    private val technicalDetails: JsonValue
    private val playerData: JsonValue

    val mapDatas = mutableListOf<MapData>()
    private val helpPages: MutableMap<String, HelpPage> = HashMap()

    private val preferences = Gdx.app.getPreferences(PREFERENCES_TAG)
    private val json = Json().apply {
        setOutputType(JsonWriter.OutputType.json)
        setUsePrototypes(false)
    }
    lateinit var saveData: SaveData private set

    init {
        with (jsonReader) {
            maps = parse(Gdx.files.internal("data/maps.json"))
            strings = parse(Gdx.files.internal("data/strings.json"))
            colors = parse(Gdx.files.internal("data/colors.json"))
            entityDetails = parse(Gdx.files.internal("data/entity_details.json"))
            technicalDetails = parse(Gdx.files.internal("data/technical_details.json"))
            playerData = parse(Gdx.files.internal("data/player.json"))
        }

        loadMapDatas()
        loadHelpPages()
        loadSaveData()
    }

    fun save() {
        val saveValue = json.toJson(saveData)
        preferences.putString(SAVE_KEY, saveValue)
        preferences.flush()
    }

    private fun loadSaveData() {
        saveData = json.fromJson(SaveData::class.java, preferences.getString(SAVE_KEY)) ?:
                SaveData()

        for (i in 0 until saveData.mapsCompleted) {
            mapDatas[i].completed = true
        }
    }

    fun getString(key: String) : String? = strings.getString(key)

    fun getColor(key: String) : String? = colors.getString(key)

    fun getColorFromHexKey(key: String) = Color(Color.valueOf(getColor(key)))

    fun getHelpPage(key: String) : HelpPage? = helpPages[key]

    fun getPlayerData(key: String) = playerData[key]

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
                    res.getTexture("tileset")!!.split(TILE_SIZE, TILE_SIZE)[rc[0].toInt()][rc[1].toInt()]
                } else {
                    res.getTexture(imageStr)
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

                helpPages[id] = HelpPage(res, entityDetails)
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
                    val wrapper = ImageWrapper(res.getTexture(it.getString("image")),
                            ImageAlign.valueOf(it.getString("align")))
                    technicalDetails.images.add(wrapper)
                }
                helpPages[id] = HelpPage(res, technicalDetails)
            }
        }
    }

}