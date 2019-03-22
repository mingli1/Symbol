package com.symbol.game.map;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.symbol.game.ecs.entity.EnemyType;
import com.symbol.game.ecs.entity.EntityFactory;
import com.symbol.game.ecs.entity.MapEntityType;
import com.symbol.game.util.Resources;

public class MapManager implements Disposable {

    private static final String DIR = "map/";

    private static final String PLAYER_SPAWN_LAYER = "player";
    private static final String TILE_LAYER = "tile";
    private static final String COLLISION_LAYER = "collision";
    private static final String ENEMY_LAYER = "enemy";
    private static final String MAP_ENTITY_LAYER = "map";

    private static final String MAP_OBJECT_TYPE = "type";
    private static final String MAP_OBJECT_DAMAGE = "damage";

    private static final String TYPE = "type";
    private static final String ENEMY_FACING_RIGHT = "facingRight";

    private TmxMapLoader mapLoader = new TmxMapLoader();
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera cam;
    private PooledEngine engine;
    private Resources res;

    private TiledMapTileLayer tileLayer;
    private MapLayer collisionLayer;
    private MapLayer enemyLayer = null;
    private MapLayer mapEntityLayer = null;

    private int tileSize;
    private int mapWidth;
    private int mapHeight;

    private Vector2 playerSpawnPosition = new Vector2();

    private Array<MapObject> mapObjects = new Array<MapObject>();

    public MapManager(Batch batch, OrthographicCamera cam, PooledEngine engine, Resources res) {
        this.cam = cam;
        this.engine = engine;
        this.res = res;

        renderer = new OrthogonalTiledMapRenderer(null, 1f, batch);
    }

    public void load(String mapName) {
        tiledMap = mapLoader.load(DIR + mapName + ".tmx");

        tileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(TILE_LAYER);
        collisionLayer = tiledMap.getLayers().get(COLLISION_LAYER);
        MapLayer playerSpawnLayer = tiledMap.getLayers().get(PLAYER_SPAWN_LAYER);
        enemyLayer = tiledMap.getLayers().get(ENEMY_LAYER);
        mapEntityLayer = tiledMap.getLayers().get(MAP_ENTITY_LAYER);

        tileSize = (int) tileLayer.getTileWidth();
        mapWidth = tileLayer.getWidth();
        mapHeight = tileLayer.getHeight();

        Rectangle spawn = playerSpawnLayer.getObjects().getByType(RectangleMapObject.class).get(0).getRectangle();
        playerSpawnPosition.set(spawn.x, spawn.y);

        loadMapObjects();

        if (enemyLayer != null) loadEnemies();
        if (mapEntityLayer != null) loadMapEntities();

        renderer.setMap(tiledMap);
    }

    private void loadMapObjects() {
        mapObjects.clear();
        MapObjects objects = collisionLayer.getObjects();
        for (RectangleMapObject rectangleMapObject : objects.getByType(RectangleMapObject.class)) {
            Rectangle mapObjectRect = rectangleMapObject.getRectangle();
            String typeProp = rectangleMapObject.getProperties().get(MAP_OBJECT_TYPE, String.class);
            Object damageProp = rectangleMapObject.getProperties().get(MAP_OBJECT_DAMAGE);

            MapObjectType mapObjectType = typeProp == null ? MapObjectType.Ground : MapObjectType.getType(typeProp);
            int mapObjectDamage = damageProp == null ? 0 : (Integer) damageProp;

            mapObjects.add(new MapObject(mapObjectRect, mapObjectType, mapObjectDamage));
        }
    }

    private void loadEnemies() {
        MapObjects enemyObjects = enemyLayer.getObjects();
        for (RectangleMapObject enemyMapObject : enemyObjects.getByType(RectangleMapObject.class)) {
            Rectangle enemyObjectRect = enemyMapObject.getRectangle();
            String typeProp = enemyMapObject.getProperties().get(TYPE, String.class);
            Object facingRightProp = enemyMapObject.getProperties().get(ENEMY_FACING_RIGHT);

            EnemyType enemyObjectType = typeProp == null ? EnemyType.None : EnemyType.getType(typeProp);
            boolean facingRight = facingRightProp == null ? true : (Boolean) facingRightProp;

            EntityFactory.createEnemy(engine, res, enemyObjectType, enemyObjectRect, facingRight);
        }
    }

    private void loadMapEntities() {
        MapObjects mapEntityObjects = mapEntityLayer.getObjects();
        for (RectangleMapObject mapEntityObject : mapEntityObjects.getByType(RectangleMapObject.class)) {
            Rectangle mapEntityRect = mapEntityObject.getRectangle();
            String typeProp = mapEntityObject.getProperties().get(TYPE, String.class);
            MapEntityType mapEntityType = typeProp == null ? MapEntityType.None : MapEntityType.getType(typeProp);

            EntityFactory.createMapEntity(engine, res, mapEntityObject.getProperties(), mapEntityType, mapEntityRect);
        }
    }

    public void update() {
        renderer.setView(cam);
    }

    public void render() {
        renderer.renderTileLayer(tileLayer);
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public Array<MapObject> getMapObjects() {
        return mapObjects;
    }

    public Vector2 getPlayerSpawnPosition() {
        return playerSpawnPosition;
    }

    @Override
    public void dispose() {
        renderer.dispose();
        tiledMap.dispose();
    }

}