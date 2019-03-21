package com.symbol.ecs.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.symbol.ecs.Mapper;
import com.symbol.ecs.component.*;
import com.symbol.ecs.component.map.MapEntityComponent;
import com.symbol.ecs.component.map.MovingPlatformComponent;
import com.symbol.effects.particle.Particle;
import com.symbol.effects.particle.ParticleSpawner;
import com.symbol.map.MapObject;
import com.symbol.map.MapObjectType;
import com.symbol.util.Resources;

import java.util.HashMap;
import java.util.Map;

public class MapCollisionSystem extends IteratingSystem {

    private static final int NUM_SUB_STEPS = 30;
    private static final float MAP_OBJECT_DAMAGE_RATE = 1f;
    private static final float MAP_OBJECT_SLOW_PERCENTAGE = 0.4f;
    private static final float MAP_OBJECT_PUSH = 45f;

    public static final float MAP_OBJECT_JUMP_BOOST_PERCENTAGE = 1.5f;

    private Array<MapObject> mapObjects = new Array<MapObject>();
    private int mapWidth = 0;
    private int mapHeight = 0;

    private float stepX = 0f;
    private float stepY = 0f;

    private ImmutableArray<Entity> removableEntities;
    private ImmutableArray<Entity> movingPlatforms;
    private ImmutableArray<Entity> mapEntities;

    private Map<Entity, Float> damageTimes = new HashMap<Entity, Float>();
    private Map<Entity, Boolean> startDamage = new HashMap<Entity, Boolean>();

    private Resources res;

    public MapCollisionSystem(Resources res) {
        super(Family.all(BoundingBoxComponent.class, GravityComponent.class).get());
        this.res = res;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        removableEntities = engine.getEntitiesFor(Family.all(RemoveComponent.class).get());
        movingPlatforms = engine.getEntitiesFor(Family.all(MovingPlatformComponent.class).get());
        mapEntities = engine.getEntitiesFor(Family.all(MapEntityComponent.class).exclude(MovingPlatformComponent.class).get());
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        for (Entity entity : removableEntities) {
            PositionComponent position = Mapper.POS_MAPPER.get(entity);
            float width = Mapper.TEXTURE_MAPPER.get(entity).texture.getRegionWidth();
            float height = Mapper.TEXTURE_MAPPER.get(entity).texture.getRegionHeight();
            RemoveComponent remove = Mapper.REMOVE_MAPPER.get(entity);
            if (position.x < -mapWidth - width || position.x > mapWidth * 2 ||
                    position.y < -mapHeight - height || position.y > mapHeight * 2) {
                remove.shouldRemove = true;
            }
        }
    }

    @Override
    public void processEntity(Entity entity, float dt) {
        BoundingBoxComponent bb = Mapper.BOUNDING_BOX_MAPPER.get(entity);
        PositionComponent position = Mapper.POS_MAPPER.get(entity);
        VelocityComponent velocity = Mapper.VEL_MAPPER.get(entity);
        float width = Mapper.TEXTURE_MAPPER.get(entity).texture.getRegionWidth();
        float height = Mapper.TEXTURE_MAPPER.get(entity).texture.getRegionHeight();
        GravityComponent gravity = Mapper.GRAVITY_MAPPER.get(entity);
        PlayerComponent player = Mapper.PLAYER_MAPPER.get(entity);

        stepX = (gravity.onMovingPlatform ? velocity.platformDx : velocity.dx) * dt / NUM_SUB_STEPS;
        for (int i = 0; i < NUM_SUB_STEPS; i++) {
            savePreviousPosition(position);
            position.x += stepX;
            bb.rect.setPosition(position.x + (width - bb.rect.width) / 2, position.y + (height - bb.rect.height) / 2);

            if (gravity.collidable) {
                for (MapObject mapObject : mapObjects) {
                    if (mapObject.getType().solid && bb.rect.overlaps(mapObject.getBounds())) {
                        revertCurrentPosition(position);
                    }
                }
                for (Entity mapEntity : mapEntities) {
                    MapEntityComponent comp = Mapper.MAP_ENTITY_MAPPER.get(mapEntity);
                    BoundingBoxComponent bounds = Mapper.BOUNDING_BOX_MAPPER.get(mapEntity);
                    if (comp.mapCollidable && bb.rect.overlaps(bounds.rect)) {
                        revertCurrentPosition(position);
                    }
                }
                for (Entity mplatform : movingPlatforms) {
                    BoundingBoxComponent bounds = Mapper.BOUNDING_BOX_MAPPER.get(mplatform);
                    VelocityComponent vel = Mapper.VEL_MAPPER.get(mplatform);
                    if (bb.rect.overlaps(bounds.rect)) {
                        boolean collisionLeft = (velocity.dx >= 0 && vel.dx < 0) || (velocity.dx > 0 && vel.dx > 0);
                        boolean collisionRight = (velocity.dx <= 0 && vel.dx > 0) || (velocity.dx < 0 && vel.dx < 0);

                        if (bb.rect.x < bounds.rect.x && collisionLeft)
                            position.x = bounds.rect.x - bb.rect.width - 1;
                        else if (bb.rect.x + bb.rect.width > bounds.rect.x + bounds.rect.width && collisionRight)
                            position.x = bounds.rect.x + bounds.rect.width + 1;
                    }
                }
            }
        }

        stepY = velocity.dy * dt / NUM_SUB_STEPS;
        for (int i = 0; i < NUM_SUB_STEPS; i++) {
            savePreviousPosition(position);
            position.y += stepY;
            bb.rect.setPosition(position.x + (width - bb.rect.width) / 2, position.y + (height - bb.rect.height) / 2);

            if (gravity.collidable) {
                for (MapObject mapObject : mapObjects) {
                    if (mapObject.getType().solid && bb.rect.overlaps(mapObject.getBounds())) {
                        revertCurrentPosition(position);
                        if (velocity.dy < 0 || (gravity.reverse && velocity.dy > 0 )) {
                            gravity.onGround = true;
                            gravity.platform.set(mapObject.getBounds());

                            handleGroundedMapObject(mapObject, player);
                            handleSlowMapObject(mapObject, velocity);
                            handlePushRightMapObject(mapObject, velocity);
                            handlePushLeftMapObject(mapObject, velocity);
                            handleJumpBoostMapObject(mapObject, player);
                        }
                        velocity.dy = 0f;
                    }
                }
                for (Entity mapEntity : mapEntities) {
                    MapEntityComponent comp = Mapper.MAP_ENTITY_MAPPER.get(mapEntity);
                    BoundingBoxComponent bounds = Mapper.BOUNDING_BOX_MAPPER.get(mapEntity);
                    if (comp.mapCollidable && bb.rect.overlaps(bounds.rect)) {
                        revertCurrentPosition(position);
                        if (velocity.dy < 0 || (gravity.reverse && velocity.dy > 0 )) {
                            gravity.onGround = true;
                            gravity.platform.set(bounds.rect);
                            if (player != null) player.canJump = true;
                        }
                        velocity.dy = 0f;
                    }
                }
                for (Entity mplatform : movingPlatforms) {
                    BoundingBoxComponent bounds = Mapper.BOUNDING_BOX_MAPPER.get(mplatform);
                    if (bb.rect.overlaps(bounds.rect)) {
                        revertCurrentPosition(position);
                        if ((velocity.dy < 0 || (gravity.reverse && velocity.dy > 0)) &&
                                bb.rect.x + bb.rect.width > bounds.rect.x &&
                                bb.rect.x < bounds.rect.x + bounds.rect.width) {
                            gravity.onGround = true;
                            gravity.onMovingPlatform = true;
                            gravity.platform.set(bounds.rect);
                            if (player != null) player.canJump = true;
                        }
                        velocity.dy = 0f;

                        VelocityComponent vel = Mapper.VEL_MAPPER.get(mplatform);
                        if (gravity.onMovingPlatform) {
                            if ((velocity.dx < 0 && vel.dx > 0) || (velocity.dx > 0 && vel.dx < 0))
                                velocity.platformDx = velocity.dx / 2 - vel.dx;
                            else
                                velocity.platformDx = velocity.dx != 0f ? vel.dx + velocity.dx / 2 : vel.dx + velocity.dx;
                        }
                    }
                }
            }
        }
        if (velocity.dy != 0f) {
            gravity.onGround = false;
            gravity.onMovingPlatform = false;
            velocity.platformDx = 0f;
        }

        if (Mapper.PROJ_MAPPER.get(entity) != null) return;

        for (MapObject mapObject : mapObjects) {
            if (bb.rect.overlaps(mapObject.getBounds())) {
                if (mapObject.getType() == MapObjectType.Lethal) handleLethalMapObject(entity);
                if (mapObject.getType() == MapObjectType.Damage) handleDamageMapObject(mapObject, entity);
            }
        }

        if (startDamage.get(entity)) {
            damageTimes.put(entity, damageTimes.get(entity) + dt);
            if (damageTimes.get(entity) >= MAP_OBJECT_DAMAGE_RATE) {
                damageTimes.put(entity, 0f);
                startDamage.put(entity, false);
            }
        }
    }

    public void setMapData(Array<MapObject> mapObjects, int mapWidth, int mapHeight) {
        this.mapObjects.clear();
        this.mapObjects.addAll(mapObjects);
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        damageTimes.clear();
        startDamage.clear();
        for (Entity entity : getEntities()) {
            damageTimes.put(entity, 0f);
            startDamage.put(entity, false);
        }
    }

    private void handleLethalMapObject(Entity entity) {
        HealthComponent health = Mapper.HEALTH_MAPPER.get(entity);
        if (health != null) health.hp = 0;

        BoundingBoxComponent bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity);
        ColorComponent color = Mapper.COLOR_MAPPER.get(entity);
        ParticleSpawner.spawn(res, color.hex, Particle.DEFAULT_LIFETIME, Particle.DEFAULT_INTESITY + health.maxHp,
                bounds.rect.x + bounds.rect.width / 2, bounds.rect.y + bounds.rect.height / 2);
    }

    private void handleDamageMapObject(MapObject mapObject, Entity entity) {
        if (damageTimes.get(entity) == 0f) {
            HealthComponent health = Mapper.HEALTH_MAPPER.get(entity);
            if (health != null) health.hit(mapObject.getDamage());
            startDamage.put(entity, true);

            BoundingBoxComponent bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity);
            ColorComponent color = Mapper.COLOR_MAPPER.get(entity);
            ParticleSpawner.spawn(res, color.hex, Particle.DEFAULT_LIFETIME, Particle.DEFAULT_INTESITY + mapObject.getDamage(),
                    bounds.rect.x + bounds.rect.width / 2, bounds.rect.y + bounds.rect.height / 2);
        }
    }

    private void handleGroundedMapObject(MapObject mapObject, PlayerComponent player) {
        boolean grounded = mapObject.getType() == MapObjectType.Grounded;
        if (player != null) player.canJump = !grounded;
        if (grounded) if (player != null) player.canDoubleJump = false;
    }

    private void handleSlowMapObject(MapObject mapObject, VelocityComponent velocity) {
        if (mapObject.getType() == MapObjectType.Slow) {
            if (velocity.dx > 0) velocity.dx = velocity.speed * MAP_OBJECT_SLOW_PERCENTAGE;
            else if (velocity.dx < 0) velocity.dx = -velocity.speed * MAP_OBJECT_SLOW_PERCENTAGE;
        }
        else {
            if (velocity.dx != 0f && Math.abs(velocity.dx) == velocity.speed * MAP_OBJECT_SLOW_PERCENTAGE) {
                if (velocity.dx > 0) velocity.dx = velocity.speed;
                else if (velocity.dx < 0) velocity.dx = -velocity.speed;
            }
        }
    }

    private void handlePushRightMapObject(MapObject mapObject, VelocityComponent velocity) {
        if (mapObject.getType() == MapObjectType.PushRight) {
            if (velocity.dx > 0 && velocity.dx == velocity.speed) velocity.dx += MAP_OBJECT_PUSH;
        }
        else if (velocity.dx > 0 && velocity.dx == velocity.speed + MAP_OBJECT_PUSH) velocity.dx = velocity.speed;
    }

    private void handlePushLeftMapObject(MapObject mapObject, VelocityComponent velocity) {
        if (mapObject.getType() == MapObjectType.PushLeft) {
            if (velocity.dx < 0 && velocity.dx == -velocity.speed) velocity.dx -= MAP_OBJECT_PUSH;
        }
        else if (velocity.dx < 0 && velocity.dx == -velocity.speed - MAP_OBJECT_PUSH) velocity.dx = -velocity.speed;
    }

    private void handleJumpBoostMapObject(MapObject mapObject, PlayerComponent player) {
        if (player != null) player.hasJumpBoost = mapObject.getType() == MapObjectType.JumpBoost;
    }

    private void savePreviousPosition(PositionComponent position) {
        position.setPrev(position.x, position.y);
    }

    private void revertCurrentPosition(PositionComponent position) {
        position.set(position.prevX, position.prevY);
    }

}