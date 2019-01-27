package com.symbol.map

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable

private const val DIR = "map/"
private const val TILE_LAYER = "tile"
private const val COLLISION_LAYER = "collision"

class TileMapManager(batch: Batch, private val cam: OrthographicCamera) : Disposable {

    private val mapLoader: TmxMapLoader = TmxMapLoader()
    private lateinit var tiledMap: TiledMap
    private val renderer: OrthogonalTiledMapRenderer = OrthogonalTiledMapRenderer(null, 1f, batch)

    private lateinit var tileLayer: TiledMapTileLayer
    private lateinit var collisionLayer: MapLayer

    var tileSize: Int = 0
        private set

    var mapWidth: Int = 0
        private set
    var mapHeight: Int = 0
        private set

    val collisionBoxes: Array<Rectangle> = Array()

    fun load(mapName: String) {
        tiledMap = mapLoader.load("$DIR$mapName.tmx")

        tileSize = tiledMap.properties.get("tileSize", Int::class.java)
        mapWidth = tiledMap.properties.get("mapWidth", Int::class.java)
        mapHeight = tiledMap.properties.get("mapHeight", Int::class.java)

        tileLayer = tiledMap.layers.get(TILE_LAYER) as TiledMapTileLayer
        collisionLayer = tiledMap.layers.get(COLLISION_LAYER)

        collisionBoxes.clear()
        val objects = collisionLayer.objects
        for (rectangleMapObject in objects.getByType(RectangleMapObject::class.java)) {
            val collisionBox = rectangleMapObject.rectangle
            collisionBoxes.add(collisionBox)
        }

        renderer.map = tiledMap
    }

    fun update() {
        renderer.setView(cam)
    }

    fun render() {
        renderer.render()
    }

    override fun dispose() {
        renderer.dispose()
        tiledMap.dispose()
    }

}