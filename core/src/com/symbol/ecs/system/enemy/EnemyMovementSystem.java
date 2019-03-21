package com.symbol.ecs.system.enemy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.symbol.ecs.Mapper;
import com.symbol.ecs.component.BoundingBoxComponent;
import com.symbol.ecs.component.DirectionComponent;
import com.symbol.ecs.component.GravityComponent;
import com.symbol.ecs.component.JumpComponent;
import com.symbol.ecs.component.OrbitComponent;
import com.symbol.ecs.component.PositionComponent;
import com.symbol.ecs.component.RemoveComponent;
import com.symbol.ecs.component.TextureComponent;
import com.symbol.ecs.component.VelocityComponent;
import com.symbol.ecs.component.enemy.ActivationComponent;
import com.symbol.ecs.component.enemy.CorporealComponent;
import com.symbol.ecs.component.enemy.EnemyComponent;
import com.symbol.ecs.entity.EnemyMovementType;
import com.symbol.ecs.entity.Player;
import com.symbol.util.Resources;

import java.util.HashMap;
import java.util.Map;

public class EnemyMovementSystem extends IteratingSystem {

    private static final float MOVEMENT_FREQUENCY = 0.7f;
    private static final float JUMP_FREQUENCY = 1.2f;

    private Map<Entity, Float> movementTimers = new HashMap<Entity, Float>();
    private Map<Entity, Float> jumpTimers = new HashMap<Entity, Float>();
    private Map<Entity, Float> corporealTimers = new HashMap<Entity, Float>();

    private Player player;
    private Resources res;

    public EnemyMovementSystem(Player player, Resources res) {
        super(Family.all(EnemyComponent.class).get());
        this.player = player;
        this.res = res;
    }

    public void reset() {
        movementTimers.clear();
        jumpTimers.clear();
        corporealTimers.clear();
        for (Entity entity : getEntities()) {
            movementTimers.put(entity, 0f);
            jumpTimers.put(entity, 0f);
            corporealTimers.put(entity, 0f);
        }
    }

    @Override
    public void processEntity(Entity entity, float dt) {
        EnemyComponent enemyComponent = Mapper.ENEMY_MAPPER.get(entity);
        ActivationComponent activation = Mapper.ACTIVATION_MAPPER.get(entity);
        CorporealComponent corp = Mapper.CORPOREAL_MAPPER.get(entity);
        DirectionComponent dirComponent = Mapper.DIR_MAPPER.get(entity);
        PositionComponent position = Mapper.POS_MAPPER.get(entity);
        VelocityComponent velocity = Mapper.VEL_MAPPER.get(entity);
        GravityComponent gravity = Mapper.GRAVITY_MAPPER.get(entity);
        JumpComponent jump = Mapper.JUMP_MAPPER.get(entity);

        if (corp != null && corp.incorporealTime != 0f) {
            corporealTimers.put(entity, corporealTimers.get(entity) + dt);
            if (corporealTimers.get(entity) >= corp.incorporealTime) {
                corp.corporeal = !corp.corporeal;

                if (!corp.corporeal) {
                    TextureComponent texture = Mapper.TEXTURE_MAPPER.get(entity);
                    texture.texture = res.getTexture(texture.textureStr + Resources.INCORPOREAL) == null ?
                            texture.texture : res.getTexture(texture.textureStr + Resources.INCORPOREAL);
                }

                corporealTimers.put(entity, 0f);
            }
        }

        if (activation.active) {
            if (gravity != null) {
                if (gravity.onGround && jump != null
                        && enemyComponent.movementType != EnemyMovementType.RandomWithJump) {
                    velocity.dy = jump.impulse;
                }
            }
            switch (enemyComponent.movementType) {
                case None: return;
                case BackAndForth: backAndForth(entity, position, velocity, dirComponent, gravity); break;
                case Charge: charge(position, velocity); break;
                case Random: random(entity, dt, position, velocity, gravity); break;
                case RandomWithJump: randomWithJump(entity, dt, position, velocity, gravity); break;
                case Orbit: orbit(entity, enemyComponent); break;
            }
        }
    }

    private void backAndForth(Entity entity, PositionComponent p, VelocityComponent v, DirectionComponent dir, GravityComponent g) {
        BoundingBoxComponent bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity);

        if (v.dx == 0f) v.dx = dir.facingRight ? v.speed : -v.speed;
        if (p.x < g.platform.x) {
            v.dx = v.speed;
        }
        else if (p.x > g.platform.x + g.platform.width - bounds.rect.width) {
            v.dx = -v.speed;
        }
    }

    private void charge(PositionComponent p, VelocityComponent v) {
        PositionComponent playerPosition = Mapper.POS_MAPPER.get(player);
        if (v.dx == 0f) v.dx = p.x < playerPosition.x ? v.speed : -v.speed;
    }

    private void random(Entity entity, float dt, PositionComponent p, VelocityComponent v, GravityComponent g) {
        BoundingBoxComponent bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity);
        if (p.x < g.platform.x) {
            v.dx = v.speed;
            return;
        }
        else if (p.x > g.platform.x + g.platform.width - bounds.rect.width) {
            v.dx = -v.speed;
            return;
        }

        movementTimers.put(entity, movementTimers.get(entity) + dt);
        if (movementTimers.get(entity) >= MOVEMENT_FREQUENCY) {
            int action = MathUtils.random(2);
            switch (action) {
                case 1: v.dx = -v.speed; break;
                case 2: v.dx = v.speed; break;
                default: v.dx = 0f;
            }
            movementTimers.put(entity, 0f);
        }
    }

    private void randomWithJump(Entity entity, float dt, PositionComponent p,
                               VelocityComponent v, GravityComponent g) {
        random(entity, dt, p, v, g);
        JumpComponent jump = Mapper.JUMP_MAPPER.get(entity);
        jumpTimers.put(entity, jumpTimers.get(entity) + dt);
        if (jumpTimers.get(entity) >= JUMP_FREQUENCY) {
            if (jump.impulse != 0f && MathUtils.randomBoolean()) {
                v.dy = jump.impulse;
            }
            jumpTimers.put(entity, 0f);
        }
    }

    private void orbit(Entity entity, EnemyComponent enemyComponent) {
        OrbitComponent orbit = Mapper.ORBIT_MAPPER.get(entity);
        RemoveComponent remove = Mapper.REMOVE_MAPPER.get(entity);
        RemoveComponent parentRemove = Mapper.REMOVE_MAPPER.get(enemyComponent.parent);

        if (parentRemove != null && !parentRemove.shouldRemove) {
            BoundingBoxComponent bounds = Mapper.BOUNDING_BOX_MAPPER.get(enemyComponent.parent);
            float originX = bounds.rect.x + bounds.rect.width / 2;
            float originY = bounds.rect.y + bounds.rect.height / 2;

            if (orbit != null) orbit.setOrigin(originX, originY);
        }
        else {
            remove.shouldRemove = true;
        }
    }

}