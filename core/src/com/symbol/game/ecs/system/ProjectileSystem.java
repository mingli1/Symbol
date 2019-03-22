package com.symbol.game.ecs.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.symbol.game.ecs.EntityBuilder;
import com.symbol.game.ecs.Mapper;
import com.symbol.game.ecs.component.BoundingBoxComponent;
import com.symbol.game.ecs.component.ColorComponent;
import com.symbol.game.ecs.component.GravityComponent;
import com.symbol.game.ecs.component.HealthComponent;
import com.symbol.game.ecs.component.KnockbackComponent;
import com.symbol.game.ecs.component.PlayerComponent;
import com.symbol.game.ecs.component.PositionComponent;
import com.symbol.game.ecs.component.ProjectileComponent;
import com.symbol.game.ecs.component.RemoveComponent;
import com.symbol.game.ecs.component.TextureComponent;
import com.symbol.game.ecs.component.VelocityComponent;
import com.symbol.game.ecs.component.enemy.AttackComponent;
import com.symbol.game.ecs.component.enemy.CorporealComponent;
import com.symbol.game.ecs.component.enemy.TrapComponent;
import com.symbol.game.ecs.component.map.MapEntityComponent;
import com.symbol.game.ecs.component.map.SquareSwitchComponent;
import com.symbol.game.ecs.component.map.ToggleTileComponent;
import com.symbol.game.ecs.entity.MapEntityType;
import com.symbol.game.ecs.entity.Player;
import com.symbol.game.ecs.entity.ProjectileMovementType;
import com.symbol.game.effects.particle.Particle;
import com.symbol.game.effects.particle.ParticleSpawner;
import com.symbol.game.map.MapObject;
import com.symbol.game.util.Direction;
import com.symbol.game.util.Resources;

import java.util.HashMap;
import java.util.Map;

public class ProjectileSystem extends IteratingSystem {

    public static final float DIAGONAL_PROJECTILE_SCALING = 0.75f;
    private static final float KNOCKBACK_TIME = 0.1f;

    private Array<MapObject> mapObjects = new Array<MapObject>();

    private ImmutableArray<Entity> allEntities;
    private ImmutableArray<Entity> mapEntities;
    private ImmutableArray<Entity> toggleTiles;

    private Map<Entity, Float> knockbackTimes = new HashMap<Entity, Float>();
    private Map<Entity, Float> prevVelocities = new HashMap<Entity, Float>();
    private Map<Entity, Boolean> startKnockback = new HashMap<Entity, Boolean>();

    private Map<Entity, Float> waveTimers = new HashMap<Entity, Float>();

    private Player player;
    private Resources res;

    public ProjectileSystem(Player player, Resources res) {
        super(Family.all(ProjectileComponent.class).get());
        this.player = player;
        this.res = res;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        allEntities = engine.getEntitiesFor(Family.all(HealthComponent.class).get());
        mapEntities = engine.getEntitiesFor(Family.all(MapEntityComponent.class).get());
        toggleTiles = engine.getEntitiesFor(Family.all(ToggleTileComponent.class).get());
    }

    public void setMapData(Array<MapObject> mapObjects) {
        this.mapObjects.clear();
        this.mapObjects.addAll(mapObjects);

        knockbackTimes.clear();
        prevVelocities.clear();
        startKnockback.clear();
        waveTimers.clear();
        for (Entity entity : allEntities) {
            knockbackTimes.put(entity, 0f);
            prevVelocities.put(entity, 0f);
            startKnockback.put(entity, false);
        }
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        for (Entity e : allEntities) {
            VelocityComponent vel = Mapper.VEL_MAPPER.get(e);
            if (startKnockback.get(e)) {
                KnockbackComponent knockback = Mapper.KNOCKBACK_MAPPER.get(e);
                knockbackTimes.put(e, knockbackTimes.get(e) + dt);
                if (knockbackTimes.get(e) > KNOCKBACK_TIME) {
                    vel.dx = prevVelocities.get(e);
                    knockbackTimes.put(e, 0f);
                    startKnockback.put(e, false);
                    knockback.knockingBack = false;
                }
            }
        }
    }

    public void processEntity(Entity entity, float dt) {
        ProjectileComponent pj = Mapper.PROJ_MAPPER.get(entity);
        ColorComponent color = Mapper.COLOR_MAPPER.get(entity);
        BoundingBoxComponent bb = Mapper.BOUNDING_BOX_MAPPER.get(entity);
        PositionComponent position = Mapper.POS_MAPPER.get(entity);
        VelocityComponent velocity = Mapper.VEL_MAPPER.get(entity);
        float width = Mapper.TEXTURE_MAPPER.get(entity).texture.getRegionWidth();
        float height = Mapper.TEXTURE_MAPPER.get(entity).texture.getRegionHeight();
        RemoveComponent remove = Mapper.REMOVE_MAPPER.get(entity);
        bb.rect.setPosition(position.x + (width - bb.rect.width) / 2, position.y + (height - bb.rect.height) / 2);

        pj.lifeTime += dt;

        if (pj.acceleration != 0f && pj.movementType == ProjectileMovementType.Normal) {
            if (velocity.dx != 0f) velocity.dx += velocity.dx > 0f ? pj.acceleration * dt : -pj.acceleration * dt;
            if (velocity.dy != 0f) velocity.dy += velocity.dy > 0f ? pj.acceleration * dt : -pj.acceleration * dt;
        }

        if (pj.collidesWithTerrain) {
            for (MapObject mapObject : mapObjects) {
                if (bb.rect.overlaps(mapObject.getBounds())) {
                    remove.shouldRemove = true;
                    ParticleSpawner.spawn(res, color.hex,
                            Particle.DEFAULT_LIFETIME, Particle.DEFAULT_INTESITY + pj.damage,
                            position.x + width / 2,
                            position.y + height / 2);
                    break;
                }
            }
            handleMapEntityCollisions(entity);
        }

        for (Entity projectile : getEntities()) {
            ProjectileComponent projectileComp = Mapper.PROJ_MAPPER.get(projectile);
            if (!entity.equals(projectile) && projectileComp.collidesWithProjectiles) {
                BoundingBoxComponent bounds = Mapper.BOUNDING_BOX_MAPPER.get(projectile);
                if (bb.rect.overlaps(bounds.rect)) {
                    remove.shouldRemove = true;
                    RemoveComponent projectileRemove = Mapper.REMOVE_MAPPER.get(projectile);
                    projectileRemove.shouldRemove = true;
                    break;
                }
            }
        }

        for (Entity e : allEntities) {
            BoundingBoxComponent ebb = Mapper.BOUNDING_BOX_MAPPER.get(e);
            VelocityComponent ev = Mapper.VEL_MAPPER.get(e);

            if (bb.rect.overlaps(ebb.rect)) {
                KnockbackComponent knockback = Mapper.KNOCKBACK_MAPPER.get(e);
                PlayerComponent player = Mapper.PLAYER_MAPPER.get(e);

                if ((pj.enemy && player != null) || (!pj.enemy && player == null)) {
                    CorporealComponent corp = Mapper.CORPOREAL_MAPPER.get(e);
                    if (corp != null && !corp.corporeal) break;

                    TrapComponent trap = Mapper.TRAP_MAPPER.get(e);
                    if (trap != null) handleTrapEnemy(e);

                    if (knockback != null) {
                        prevVelocities.put(e, ev.dx);
                        ev.dx = bb.rect.x < ebb.rect.x + ebb.rect.width / 2 ? pj.knockback : -pj.knockback;
                        startKnockback.put(e, true);
                        knockback.knockingBack = true;
                    }
                    hit(e, pj.damage);

                    ColorComponent entityColor = Mapper.COLOR_MAPPER.get(e);
                    ParticleSpawner.spawn(res, entityColor.hex,
                            Particle.DEFAULT_LIFETIME, Particle.DEFAULT_INTESITY + pj.damage,
                            ebb.rect.x + ebb.rect.width / 2,
                            ebb.rect.y + ebb.rect.height / 2);

                    remove.shouldRemove = true;
                    break;
                }
            }
        }

        switch (pj.movementType) {
            case Arc: handleArcMovement(entity, dt, pj); break;
            case Wave: handleWaveMovement(entity, dt, pj); break;
        }

        handleDetonation(entity, pj, bb.rect, remove);
    }

    private void hit(Entity entity, int damage) {
        HealthComponent health = Mapper.HEALTH_MAPPER.get(entity);
        health.hit(damage);

        handleTeleportation(entity);
        handleLastStand(entity);
    }

    private void handleMapEntityCollisions(Entity entity) {
        ProjectileComponent pj = Mapper.PROJ_MAPPER.get(entity);
        ColorComponent color = Mapper.COLOR_MAPPER.get(entity);
        BoundingBoxComponent bb = Mapper.BOUNDING_BOX_MAPPER.get(entity);
        PositionComponent position = Mapper.POS_MAPPER.get(entity);
        VelocityComponent velocity = Mapper.VEL_MAPPER.get(entity);
        float width = Mapper.TEXTURE_MAPPER.get(entity).texture.getRegionWidth();
        float height = Mapper.TEXTURE_MAPPER.get(entity).texture.getRegionHeight();
        RemoveComponent remove = Mapper.REMOVE_MAPPER.get(entity);

        for (Entity mapEntity : mapEntities) {
            MapEntityComponent me = Mapper.MAP_ENTITY_MAPPER.get(mapEntity);
            BoundingBoxComponent bounds = Mapper.BOUNDING_BOX_MAPPER.get(mapEntity);

            if (!pj.enemy && bb.rect.overlaps(bounds.rect)) {
                switch (me.mapEntityType) {
                    case Mirror:
                        pj.enemy = true;
                        velocity.dx = -velocity.dx;
                        break;
                    case GravitySwitch:
                        GravityComponent gravity = Mapper.GRAVITY_MAPPER.get(player);

                        gravity.reverse = !gravity.reverse;
                        for (Entity gravitySwitch : mapEntities) {
                            MapEntityComponent gs = Mapper.MAP_ENTITY_MAPPER.get(gravitySwitch);
                            if (gs.mapEntityType == MapEntityType.GravitySwitch) {
                                TextureComponent gsTexture = Mapper.TEXTURE_MAPPER.get(gravitySwitch);
                                gsTexture.texture = res.getTexture(gsTexture.textureStr +
                                        (gravity.reverse ? Resources.TOGGLE_ON : Resources.TOGGLE_OFF));
                            }
                        }
                        break;
                    case SquareSwitch:
                        SquareSwitchComponent sw = Mapper.SQUARE_SWITCH_MAPPER.get(mapEntity);
                        TextureComponent switchTexture = Mapper.TEXTURE_MAPPER.get(mapEntity);

                        sw.toggle = !sw.toggle;
                        switchTexture.texture = res.getTexture(switchTexture.textureStr +
                                (sw.toggle ? Resources.TOGGLE_ON : Resources.TOGGLE_OFF));

                        for (Entity tt : toggleTiles) {
                            MapEntityComponent tme = Mapper.MAP_ENTITY_MAPPER.get(tt);
                            ToggleTileComponent toggleComp = Mapper.TOGGLE_TILE_MAPPER.get(tt);
                            TextureComponent toggleTexture = Mapper.TEXTURE_MAPPER.get(tt);

                            if (sw.targetId == toggleComp.id) {
                                toggleComp.toggle = !toggleComp.toggle;
                                toggleTexture.texture = toggleComp.toggle ? res.getTexture(toggleTexture.textureStr) : null;
                                tme.mapCollidable = toggleComp.toggle;
                                tme.projectileCollidable = toggleComp.toggle;
                            }
                        }
                        break;
                }
            }

            if (me.projectileCollidable) {
                if (bb.rect.overlaps(bounds.rect)) {
                    ParticleSpawner.spawn(res, color.hex,
                            Particle.DEFAULT_LIFETIME, Particle.DEFAULT_INTESITY + pj.damage,
                            position.x + width / 2,
                            position.y + height / 2);
                    remove.shouldRemove = true;
                    break;
                }
            }
        }
    }

    private void handleDetonation(Entity entity, ProjectileComponent pj, Rectangle bounds, RemoveComponent remove) {
        if (pj.enemy && !pj.collidesWithTerrain && pj.detonateTime != 0f) {
            if (pj.lifeTime >= pj.detonateTime) {
                VelocityComponent vel = Mapper.VEL_MAPPER.get(entity);
                float speed = vel.dx != 0f ? Math.abs(vel.dx) : Math.abs(vel.dy);
                TextureRegion texture = res.getSubProjectileTextureFor(pj.textureStr);

                createSubProjectile(pj, bounds, speed, 0f, texture);
                createSubProjectile(pj, bounds, speed * DIAGONAL_PROJECTILE_SCALING, -speed * DIAGONAL_PROJECTILE_SCALING, texture);
                createSubProjectile(pj, bounds, 0f, -speed, texture);
                createSubProjectile(pj, bounds, -speed * DIAGONAL_PROJECTILE_SCALING, -speed * DIAGONAL_PROJECTILE_SCALING, texture);
                createSubProjectile(pj, bounds, -speed, 0f, texture);
                createSubProjectile(pj, bounds, -speed * DIAGONAL_PROJECTILE_SCALING, speed * DIAGONAL_PROJECTILE_SCALING, texture);
                createSubProjectile(pj, bounds, 0f, speed, texture);
                createSubProjectile(pj, bounds, speed * DIAGONAL_PROJECTILE_SCALING, speed * DIAGONAL_PROJECTILE_SCALING, texture);

                remove.shouldRemove = true;
            }
        }
    }

    private void handleArcMovement(Entity entity, float dt, ProjectileComponent pj) {
        VelocityComponent vel = Mapper.VEL_MAPPER.get(entity);
        float ay = (pj.acceleration / 1.5f) * dt;
        vel.dx += pj.parentFacingRight ? pj.acceleration * dt : -pj.acceleration * dt;

        if (!pj.arcHalf) {
            if (vel.dy > 0) {
                vel.dy -= ay;
                if (vel.dy < 0) pj.arcHalf = true;
            } else if (vel.dy < 0) {
                vel.dy += ay;
                if (vel.dy > 0) pj.arcHalf = true;
            }
        }
        else {
            if (vel.dy < 0) vel.dy -= ay;
            else if (vel.dy > 0) vel.dy += ay;
        }
    }

    private void handleWaveMovement(Entity entity, float dt, ProjectileComponent pj) {
        if (waveTimers.get(entity) == null) waveTimers.put(entity, 0f);
        waveTimers.put(entity, waveTimers.get(entity) + dt * (pj.acceleration / 10f));
        if (waveTimers.get(entity) >= MathUtils.PI2) waveTimers.put(entity, 0f);
        float offset = MathUtils.sin(waveTimers.get(entity)) * pj.acceleration;

        VelocityComponent velocity = Mapper.VEL_MAPPER.get(entity);

        if (pj.waveDir == Direction.Left || pj.waveDir == Direction.Right) velocity.dy = offset;
        if (pj.waveDir == Direction.Up || pj.waveDir == Direction.Down) velocity.dx = offset;
    }

    private void createSubProjectile(ProjectileComponent pj, Rectangle bounds,
                                 float dx, float dy, TextureRegion texture) {
        float bw = texture.getRegionWidth() - 1;
        float bh = texture.getRegionHeight() - 1;
        EntityBuilder.instance((PooledEngine) getEngine())
                .projectile(ProjectileMovementType.Normal, false, false, false, null, true, pj.damage, 0f, 0f, 0f)
                .position(bounds.x + (bounds.width / 2) - (bw / 2), bounds.y + (bounds.height / 2) - (bh / 2))
                .velocity(dx, dy, 0f)
                .boundingBox(bw, bh, 0f, 0f)
                .texture(texture, null)
                .direction(true, true).remove().build();
    }

    private void handleTeleportation(Entity entity) {
        if (Mapper.TELEPORT_MAPPER.get(entity) != null) {
            BoundingBoxComponent bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity);
            PositionComponent position = Mapper.POS_MAPPER.get(entity);
            VelocityComponent velocity = Mapper.VEL_MAPPER.get(entity);

            Rectangle platform = mapObjects.random().getBounds();
            float randX = MathUtils.random(platform.x, platform.x + platform.width - bounds.rect.width);
            float newY = platform.y + platform.height + bounds.rect.height / 2;

            position.set(randX, newY);
            velocity.dx = 0f;
        }
    }

    private void handleLastStand(Entity entity) {
        AttackComponent attackComp = Mapper.ATTACK_MAPPER.get(entity);
        if (attackComp != null) {
            if (Mapper.LAST_STAND_MAPPER.get(entity) != null) {
                HealthComponent health = Mapper.HEALTH_MAPPER.get(entity);
                float scale = 1f / health.maxHp;
                attackComp.attackRate -= attackComp.attackRate * scale;
            }
        }
    }

    private void handleTrapEnemy(Entity entity) {
        TrapComponent trapComp = Mapper.TRAP_MAPPER.get(entity);
        TextureComponent texture = Mapper.TEXTURE_MAPPER.get(entity);

        if (!trapComp.countdown) trapComp.countdown = true;
        trapComp.hits++;
        if (trapComp.hits <= 3) texture.texture = res.getTexture(texture.textureStr + trapComp.hits);
    }

}