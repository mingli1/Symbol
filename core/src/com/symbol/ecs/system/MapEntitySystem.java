package com.symbol.ecs.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.symbol.ecs.Mapper;
import com.symbol.ecs.component.BoundingBoxComponent;
import com.symbol.ecs.component.ColorComponent;
import com.symbol.ecs.component.HealthComponent;
import com.symbol.ecs.component.PlayerComponent;
import com.symbol.ecs.component.PositionComponent;
import com.symbol.ecs.component.RemoveComponent;
import com.symbol.ecs.component.VelocityComponent;
import com.symbol.ecs.component.map.ClampComponent;
import com.symbol.ecs.component.map.HealthPackComponent;
import com.symbol.ecs.component.map.MapEntityComponent;
import com.symbol.ecs.component.map.MovingPlatformComponent;
import com.symbol.ecs.component.map.PortalComponent;
import com.symbol.ecs.entity.Player;
import com.symbol.effects.particle.Particle;
import com.symbol.effects.particle.ParticleSpawner;
import com.symbol.util.Resources;

public class MapEntitySystem extends IteratingSystem {

    private Player player;
    private Resources res;

    private ImmutableArray<Entity> portals;

    public MapEntitySystem(Player player, Resources res) {
        super(Family.all(MapEntityComponent.class).get());
        this.player = player;
        this.res = res;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        portals = engine.getEntitiesFor(Family.all(PortalComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float dt) {
        MapEntityComponent mapEntityComponent = Mapper.MAP_ENTITY_MAPPER.get(entity);
        switch (mapEntityComponent.mapEntityType) {
            case MovingPlatform: handleMovingPlatform(entity); break;
            case TemporaryPlatform: handleTempPlatform(entity); break;
            case Portal: handlePortal(entity); break;
            case Clamp: handleClamp(entity, dt); break;
            case HealthPack: handleHealthPack(entity); break;
        }
    }

    private void handleMovingPlatform(Entity entity) {
        MovingPlatformComponent mp = Mapper.MOVING_PLATFORM_MAPPER.get(entity);
        BoundingBoxComponent bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity);
        PositionComponent position = Mapper.POS_MAPPER.get(entity);
        VelocityComponent velocity = Mapper.VEL_MAPPER.get(entity);

        if (velocity.dx != 0f) {
            if (mp.positive) {
                float trueX = position.x + bounds.rect.width;
                if ((velocity.dx > 0 && trueX - mp.originX >= mp.distance) ||
                        (velocity.dx < 0 && position.x <= mp.originX)) {
                    velocity.dx = -velocity.dx;
                }
            }
            else {
                if ((velocity.dx < 0 && mp.originX - position.x >= mp.distance) ||
                        (velocity.dx > 0 && position.x >= mp.originX)) {
                    velocity.dx = -velocity.dx;
                }
            }
        }
    }

    private void handleTempPlatform(Entity entity) {
        BoundingBoxComponent bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity);
        RemoveComponent remove = Mapper.REMOVE_MAPPER.get(entity);
        BoundingBoxComponent playerBounds = Mapper.BOUNDING_BOX_MAPPER.get(player);
        VelocityComponent playerVel = Mapper.VEL_MAPPER.get(player);
        PlayerComponent playerComp = Mapper.PLAYER_MAPPER.get(player);

        if (playerBounds.rect.overlaps(bounds.rect)) {
            remove.shouldRemove = true;
            playerComp.canDoubleJump = true;
            playerVel.dy = 0f;
        }
    }

    private void handlePortal(Entity entity) {
        BoundingBoxComponent bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity);
        BoundingBoxComponent playerBounds = Mapper.BOUNDING_BOX_MAPPER.get(player);
        PortalComponent portalSource = Mapper.PORTAL_MAPPER.get(entity);
        float width = Mapper.TEXTURE_MAPPER.get(player).texture.getRegionWidth();
        float height = Mapper.TEXTURE_MAPPER.get(player).texture.getRegionHeight();

        if (portalSource.teleported && !playerBounds.rect.overlaps(bounds.rect)) {
            portalSource.teleported = false;
        }

        if (playerBounds.rect.overlaps(bounds.rect)) {
            if (!portalSource.teleported) {
                for (Entity portal : portals) {
                    PortalComponent portalTarget = Mapper.PORTAL_MAPPER.get(portal);
                    if (portalTarget.id == portalSource.target) {
                        BoundingBoxComponent targetPos = Mapper.BOUNDING_BOX_MAPPER.get(portal);
                        PositionComponent playerPos = Mapper.POS_MAPPER.get(player);

                        playerPos.set(targetPos.rect.x, targetPos.rect.y);
                        playerBounds.rect.setPosition(playerPos.x + (width - playerBounds.rect.width) / 2,
                                playerPos.y + (height - playerBounds.rect.height) / 2);
                        portalTarget.teleported = true;
                        break;
                    }
                }
            }
        }
    }

    private void handleClamp(Entity entity, float dt) {
        ClampComponent clamp = Mapper.CLAMP_MAPPER.get(entity);
        PositionComponent pos = Mapper.POS_MAPPER.get(entity);
        BoundingBoxComponent bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity);
        VelocityComponent vel = Mapper.VEL_MAPPER.get(entity);
        BoundingBoxComponent playerBounds = Mapper.BOUNDING_BOX_MAPPER.get(player);

        if (!clamp.right) {
            if (clamp.clamping) {
                if (pos.x < clamp.rect.x + (clamp.rect.width / 2) - bounds.rect.width) {
                    vel.dx += clamp.acceleration * dt;
                } else {
                    vel.dx = -clamp.backVelocity;
                    clamp.clamping = false;
                }
            } else if (pos.x <= clamp.rect.x) clamp.clamping = true;
        }
        else {
            if (clamp.clamping) {
                if (pos.x > clamp.rect.x + clamp.rect.width / 2) {
                    vel.dx -= clamp.acceleration * dt;
                } else {
                    vel.dx = clamp.backVelocity;
                    clamp.clamping = false;
                }
            } else if (pos.x >= clamp.rect.x + clamp.rect.width - bounds.rect.width) clamp.clamping = true;
        }

        if (playerBounds.rect.overlaps(bounds.rect)) {
            HealthComponent playerHealth = Mapper.HEALTH_MAPPER.get(player);
            playerHealth.hp = 0;

            ColorComponent color = Mapper.COLOR_MAPPER.get(player);
            ParticleSpawner.spawn(res, color.hex, Particle.DEFAULT_LIFETIME, Particle.DEFAULT_INTESITY + playerHealth.maxHp,
                    playerBounds.rect.x + playerBounds.rect.width / 2,
                    playerBounds.rect.y + playerBounds.rect.height / 2);
        }
    }

    private void handleHealthPack(Entity entity) {
        HealthPackComponent healthPack = Mapper.HEALTH_PACK_MAPPER.get(entity);
        BoundingBoxComponent bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity);
        BoundingBoxComponent playerBounds = Mapper.BOUNDING_BOX_MAPPER.get(player);
        RemoveComponent remove = Mapper.REMOVE_MAPPER.get(entity);

        if (playerBounds.rect.overlaps(bounds.rect)) {
            HealthComponent playerHealth = Mapper.HEALTH_MAPPER.get(player);
            playerHealth.hp += healthPack.regen;
            remove.shouldRemove = true;
        }
    }

}