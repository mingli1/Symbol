package com.symbol.game.map

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.symbol.game.ecs.EntityFactory
import com.symbol.game.ecs.Mapper
import com.symbol.game.ecs.entity.EnemyType
import com.symbol.game.ecs.entity.MapEntityType
import com.symbol.game.map.camera.CameraUtil
import com.symbol.game.scene.HelpPage
import com.symbol.game.util.Resources

const val TILE_SIZE = 8

private const val DIR = "map/"

private const val PLAYER_SPAWN_LAYER = "player"
private const val TILE_LAYER = "tile"
private const val COLLISION_LAYER = "collision"
private const val ENEMY_LAYER = "enemy"
private const val MAP_ENTITY_LAYER = "map"

private const val MAP_OBJECT_TYPE = "type"
private const val MAP_OBJECT_DAMAGE = "damage"

private const val TYPE = "type"
private const val ENEMY_FACING_RIGHT = "facingRight"

class MapManager(private val engine: PooledEngine, private val res: Resources) : Disposable {

    private val mapLoader = TmxMapLoader()
    private var tiledMap: TiledMap? = null

    private val textureMap = Array<Array<TextureRegion>>()
    private lateinit var tileLayer: TiledMapTileLayer
    private lateinit var collisionLayer: MapLayer
    private lateinit var playerSpawnLayer: MapLayer
    private var enemyLayer: MapLayer? = null
    private var mapEntityLayer: MapLayer? = null

    var mapWidth = 0
        private set
    var mapHeight = 0
        private set

    var playerSpawnPosition = Vector2()

    val mapObjects: Array<MapObject> = Array()
    val helpPages: Array<HelpPage> = Array()

    private val newEntities = Array<Entity>()
    private val newMapObjects = Array<MapObject>()
    private val oldEntities = Array<Entity>()
    private val oldMapObjects = Array<MapObject>()

    private val entityComparator: (Entity, Entity) -> Int = { e1, e2 ->
        val pos1 = Mapper.POS_MAPPER.get(e1)
        val pos2 = Mapper.POS_MAPPER.get(e2)

        val dist1 = Vector2.dst2(playerSpawnPosition.x, playerSpawnPosition.y, pos1.x, pos1.y)
        val dist2 = Vector2.dst2(playerSpawnPosition.x, playerSpawnPosition.y, pos2.x, pos2.y)

        dist1.toInt() - dist2.toInt()
    }

    private val mapObjectComparator: (MapObject, MapObject) -> Int = { m1, m2 ->
        val dist1 = Vector2.dst2(playerSpawnPosition.x, playerSpawnPosition.y, m1.bounds.x, m1.bounds.y)
        val dist2 = Vector2.dst2(playerSpawnPosition.x, playerSpawnPosition.y, m2.bounds.x, m2.bounds.y)
        dist1.toInt() - dist2.toInt()
    }

    fun load(mapName: String) {
        tiledMap = mapLoader.load("$DIR$mapName.tmx")

        tileLayer = tiledMap!!.layers.get(TILE_LAYER) as TiledMapTileLayer
        collisionLayer = tiledMap!!.layers.get(COLLISION_LAYER)
        playerSpawnLayer = tiledMap!!.layers.get(PLAYER_SPAWN_LAYER)
        enemyLayer = tiledMap!!.layers.get(ENEMY_LAYER)
        mapEntityLayer = tiledMap!!.layers.get(MAP_ENTITY_LAYER)

        mapWidth = tileLayer.width
        mapHeight = tileLayer.height

        val spawn = playerSpawnLayer.objects.getByType(RectangleMapObject::class.java)[0].rectangle
        playerSpawnPosition.set(spawn.x, spawn.y)

        loadMapObjects()

        if (enemyLayer != null) loadEnemies()
        if (mapEntityLayer != null) loadMapEntities()

        val tileset = res.getTexture("tileset")!!.split(TILE_SIZE, TILE_SIZE)

        for (row in 0 until mapHeight) {
            val inner = Array<TextureRegion>()
            for (col in 0 until mapWidth) {
                val cell = tileLayer.getCell(col, row)

                if (cell != null) {
                    val id = cell.tile.id - 1

                    val x = id % tileset[0].size
                    val y = id / tileset[0].size

                    inner.add(tileset[y][x])
                }
                else {
                    inner.add(null)
                }
            }
            textureMap.add(inner)
        }

        loadHelpPages()
    }

    private fun loadMapObjects() {
        mapObjects.clear()
        val objects = collisionLayer.objects
        for (rectangleMapObject in objects.getByType(RectangleMapObject::class.java)) {
            val mapObjectRect = rectangleMapObject.rectangle
            val typeProp = rectangleMapObject.properties[MAP_OBJECT_TYPE]
            val damageProp = rectangleMapObject.properties[MAP_OBJECT_DAMAGE]

            val mapObjectType = if (typeProp == null) MapObjectType.Ground else MapObjectType.getType(typeProp.toString())!!
            val mapObjectDamage = if (damageProp == null) 0 else damageProp as Int

            mapObjects.add(MapObject(mapObjectRect, mapObjectType, mapObjectDamage))
        }
    }

    private fun loadEnemies() {
        val enemyObjects = enemyLayer!!.objects
        for (enemyMapObject in enemyObjects.getByType(RectangleMapObject::class.java)) {
            val enemyObjectRect = enemyMapObject.rectangle
            val typeProp = enemyMapObject.properties[TYPE]
            val facingRightProp = enemyMapObject.properties[ENEMY_FACING_RIGHT]

            val enemyObjectType = if (typeProp == null) EnemyType.None else EnemyType.getType(typeProp.toString())!!
            val facingRight = if (facingRightProp == null) true else facingRightProp as Boolean

            EntityFactory.createEnemy(engine, res, enemyObjectType, enemyObjectRect, facingRight)
        }
    }

    private fun loadMapEntities() {
        val mapEntityObjects = mapEntityLayer!!.objects
        for (mapEntityObject in mapEntityObjects.getByType(RectangleMapObject::class.java)) {
            val mapEntityRect = mapEntityObject.rectangle
            val typeProp = mapEntityObject.properties[TYPE]
            val mapEntityType = if (typeProp == null) MapEntityType.None else MapEntityType.getType(typeProp.toString())!!

            EntityFactory.createMapEntity(engine, res, mapEntityObject.properties, mapEntityType, mapEntityRect)
        }
    }

    private fun loadHelpPages() {
        helpPages.clear()

        newEntities.clear()
        oldEntities.clear()
        newMapObjects.clear()
        oldMapObjects.clear()

        for (entity in engine.entities) {
            val enemy = Mapper.ENEMY_MAPPER.get(entity)
            val mapEntity = Mapper.MAP_ENTITY_MAPPER.get(entity)
            var page: HelpPage? = null

            if (enemy != null) page = res.getHelpPage(enemy.enemyType.typeStr)
            else if (mapEntity != null) page = res.getHelpPage(mapEntity.mapEntityType.typeStr)

            if (page != null) {
                if (page.hasSeen() && !containsEntityType(oldEntities, entity)) oldEntities.add(entity)
                else if (!containsEntityType(newEntities, entity)) newEntities.add(entity)
            }
        }

        for (mapObject in mapObjects) {
            val page = res.getHelpPage(mapObject.type.typeStr)
            if (page != null) {
                if (page.hasSeen() && !containsMapObjectType(oldMapObjects, mapObject)) oldMapObjects.add(mapObject)
                else if (!containsMapObjectType(newMapObjects, mapObject)) newMapObjects.add(mapObject)
            }
        }

        newEntities.sort(entityComparator)
        oldEntities.sort(entityComparator)
        newMapObjects.sort(mapObjectComparator)
        oldMapObjects.sort(mapObjectComparator)

        for (mapObject in newMapObjects) addMapObjectHelpPage(mapObject)
        for (entity in newEntities) addEntityHelpPage(entity)
        for (entity in oldEntities) addEntityHelpPage(entity)
        for (mapObject in oldMapObjects) addMapObjectHelpPage(mapObject)
    }

    private fun containsEntityType(entities: Array<Entity>, entity: Entity) : Boolean {
        val enemy = Mapper.ENEMY_MAPPER.get(entity)
        val mapEntity = Mapper.MAP_ENTITY_MAPPER.get(entity)
        for (e in entities) {
            val enemy2 = Mapper.ENEMY_MAPPER.get(e)
            val mapEntity2 = Mapper.MAP_ENTITY_MAPPER.get(e)
            if (enemy != null && enemy2 != null) {
                if (enemy.enemyType == enemy2.enemyType) return true
            }
            else if (mapEntity != null && mapEntity2 != null) {
                if (mapEntity.mapEntityType == mapEntity2.mapEntityType) return true
            }
        }
        return false
    }

    private fun containsMapObjectType(mapObjects: Array<MapObject>, mapObject: MapObject) : Boolean {
        for (me in mapObjects) {
            if (mapObject.type == me.type) return true
        }
        return false
    }

    private fun addEntityHelpPage(entity: Entity) {
        val enemy = Mapper.ENEMY_MAPPER.get(entity)
        val mapEntity = Mapper.MAP_ENTITY_MAPPER.get(entity)
        if (enemy != null) helpPages.add(res.getHelpPage(enemy.enemyType.typeStr))
        else if (mapEntity != null) helpPages.add(res.getHelpPage(mapEntity.mapEntityType.typeStr))
    }

    private fun addMapObjectHelpPage(mapObject: MapObject) =
            helpPages.add(res.getHelpPage(mapObject.type.typeStr))

    fun render(batch: Batch, cam: OrthographicCamera) {
        for (row in 0 until mapHeight) {
            for (col in 0 until mapWidth) {
                val texture = textureMap[row][col]
                val x = col * TILE_SIZE
                val y = row * TILE_SIZE

                if (CameraUtil.withinCamera(x.toFloat(), y.toFloat(), cam)) {
                    if (texture != null) {
                        batch.draw(texture, x.toFloat(), y.toFloat())
                    }
                }
            }
        }
    }

    override fun dispose() {
        tiledMap?.dispose()
    }

}