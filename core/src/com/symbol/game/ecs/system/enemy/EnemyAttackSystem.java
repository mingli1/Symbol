package com.symbol.game.ecs.system.enemy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.symbol.game.ecs.EntityBuilder;
import com.symbol.game.ecs.Mapper;
import com.symbol.game.ecs.component.ColorComponent;
import com.symbol.game.ecs.component.DirectionComponent;
import com.symbol.game.ecs.component.GravityComponent;
import com.symbol.game.ecs.component.HealthComponent;
import com.symbol.game.ecs.component.ProjectileComponent;
import com.symbol.game.ecs.component.RemoveComponent;
import com.symbol.game.ecs.component.enemy.ActivationComponent;
import com.symbol.game.ecs.component.enemy.AttackComponent;
import com.symbol.game.ecs.component.enemy.EnemyComponent;
import com.symbol.game.ecs.component.enemy.TrapComponent;
import com.symbol.game.ecs.entity.EnemyAttackType;
import com.symbol.game.ecs.entity.EntityColor;
import com.symbol.game.ecs.entity.Player;
import com.symbol.game.ecs.entity.ProjectileMovementType;
import com.symbol.game.ecs.system.GravitySystem;
import com.symbol.game.ecs.system.ProjectileSystem;
import com.symbol.game.effects.particle.Particle;
import com.symbol.game.effects.particle.ParticleSpawner;
import com.symbol.game.map.camera.CameraShake;
import com.symbol.game.util.Direction;
import com.symbol.game.util.Resources;

import java.util.HashMap;
import java.util.Map;

public class EnemyAttackSystem extends IteratingSystem {

    private static final float CAMERA_SHAKE_POWER = 3f;
    private static final float CAMERA_SHAKE_DURATION = 0.7f;

    private static final float TRAP_EXPLODE_TIME = 2f;

    private Map<Entity, Float> attackTimers = new HashMap<Entity, Float>();

    private Player player;
    private Resources res;

    public EnemyAttackSystem(Player player, Resources res) {
        super(Family.all(EnemyComponent.class).get());
        this.player = player;
        this.res = res;
    }

    public void reset() {
        attackTimers.clear();
        for (Entity entity : getEntities()) {
            attackTimers.put(entity, 0f);
        }
    }

    public void processEntity(Entity entity, float dt) {
        EnemyComponent enemyComponent = Mapper.ENEMY_MAPPER.get(entity);
        ActivationComponent activation = Mapper.ACTIVATION_MAPPER.get(entity);
        AttackComponent attack = Mapper.ATTACK_MAPPER.get(entity);
        RemoveComponent remove = Mapper.REMOVE_MAPPER.get(entity);
        Rectangle bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity).rect;
        Rectangle playerBounds = Mapper.BOUNDING_BOX_MAPPER.get(player).rect;
        DirectionComponent dir = Mapper.DIR_MAPPER.get(entity);

        if (bounds.overlaps(playerBounds)) {
            HealthComponent playerHealth = Mapper.HEALTH_MAPPER.get(player);
            playerHealth.hit(attack.damage);
            remove.shouldRemove = true;

            ColorComponent color = Mapper.COLOR_MAPPER.get(entity);
            ParticleSpawner.spawn(res, color.hex, Particle.DEFAULT_LIFETIME, Particle.DEFAULT_INTESITY + attack.damage,
                    bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
            return;
        }

        if (activation.active) {
            if (enemyComponent.attackType == EnemyAttackType.ShootAndQuake) {
                GravityComponent gravity = Mapper.GRAVITY_MAPPER.get(entity);
                if (gravity.onGround) {
                    CameraShake.shakeFor(CAMERA_SHAKE_POWER, CAMERA_SHAKE_DURATION);
                }
            }
            if (attack.canAttack) {
                switch (enemyComponent.attackType) {
                    case None: break;
                    case ShootOne: shootOne(attack, dir, bounds); break;
                    case ShootTwoHorizontal: shootTwoHorizontal(attack, dir, bounds); break;
                    case ShootTwoVertical: shootTwoVertical(attack, dir, bounds); break;
                    case ShootFour: shootFour(attack, dir, bounds); break;
                    case ShootFourDiagonal: shootFourDiagonal(attack, dir, bounds); break;
                    case ShootEight: shootEight(attack, dir, bounds); break;
                    case ShootAtPlayer: shootAtPlayer(attack, dir, bounds, playerBounds); break;
                    case SprayThree: sprayThree(attack, bounds); break;
                    case ShootAndQuake: shootAtPlayer(attack, dir, bounds, playerBounds); break;
                    case Random: random(attack, bounds, dir); break;
                    case ArcTwo: arcTwo(attack, bounds, dir); break;
                    case HorizontalWave: horizontalWave(attack, bounds, dir); break;
                    case VerticalWave: verticalWave(attack, bounds, dir); break;
                    case TwoHorizontalWave: twoHorizontalWave(attack, bounds, dir); break;
                    case TwoVerticalWave: twoVerticalWave(attack, bounds, dir); break;
                    case FourWave: fourWave(attack, bounds, dir); break;
                }
                attack.canAttack = false;
            }
        }

        if (Mapper.EXPLODE_MAPPER.get(entity) != null) {
            explodeOnDeath(entity, attack, dir, bounds);
        }

        TrapComponent trap = Mapper.TRAP_MAPPER.get(entity);
        if (trap != null) {
            if (trap.countdown) {
                trap.timer += dt;
                if (trap.timer >= TRAP_EXPLODE_TIME) {
                    remove.shouldRemove = true;
                    if (trap.hits != 3) explodeOnDeath(entity, attack, dir, bounds);
                }
            }
        }

        if (!attack.canAttack) {
            attackTimers.put(entity, attackTimers.get(entity) + dt);
            if (attackTimers.get(entity) >= attack.attackRate) {
                attackTimers.put(entity, 0f);
                attack.canAttack = true;
            }
        }
    }

    private void shootOne(AttackComponent attackComp, DirectionComponent dir, Rectangle bounds) {
        TextureRegion texture = res.getTexture(attackComp.attackTexture);
        createProjectile(attackComp, dir, bounds,
                dir.facingRight ? attackComp.projectileSpeed : -attackComp.projectileSpeed, 0f, texture, ProjectileMovementType.Normal);
    }

    private void shootTwoHorizontal(AttackComponent attackComp, DirectionComponent dir, Rectangle bounds) {
        TextureRegion texture = res.getTexture(attackComp.attackTexture);
        createProjectile(attackComp, dir, bounds, attackComp.projectileSpeed, 0f, texture, ProjectileMovementType.Normal);
        createProjectile(attackComp, dir, bounds, -attackComp.projectileSpeed, 0f, texture, ProjectileMovementType.Normal);
    }

    private void shootTwoVertical(AttackComponent attackComp, DirectionComponent dir, Rectangle bounds) {
        TextureRegion topTexture = res.getTexture(attackComp.attackTexture + Resources.TOP) == null ?
                res.getTexture(attackComp.attackTexture) : res.getTexture(attackComp.attackTexture + Resources.TOP);
        createProjectile(attackComp, dir, bounds, 0f, attackComp.projectileSpeed, topTexture, ProjectileMovementType.Normal);
        createProjectile(attackComp, dir, bounds, 0f, -attackComp.projectileSpeed, topTexture, ProjectileMovementType.Normal);
    }

    private void shootFour(AttackComponent attackComp, DirectionComponent dir, Rectangle bounds) {
        shootTwoHorizontal(attackComp, dir, bounds);
        shootTwoVertical(attackComp, dir, bounds);
    }

    private void shootFourDiagonal(AttackComponent attackComp, DirectionComponent dir, Rectangle bounds) {
        TextureRegion trTexture = res.getTexture(attackComp.attackTexture + Resources.TOP_RIGHT) == null ?
                res.getTexture(attackComp.attackTexture) : res.getTexture(attackComp.attackTexture + Resources.TOP_RIGHT);
        createProjectile(attackComp, dir, bounds, -attackComp.projectileSpeed * ProjectileSystem.DIAGONAL_PROJECTILE_SCALING,
                attackComp.projectileSpeed * ProjectileSystem.DIAGONAL_PROJECTILE_SCALING, trTexture, ProjectileMovementType.Normal);
        createProjectile(attackComp, dir, bounds, attackComp.projectileSpeed * ProjectileSystem.DIAGONAL_PROJECTILE_SCALING,
                attackComp.projectileSpeed * ProjectileSystem.DIAGONAL_PROJECTILE_SCALING, trTexture, ProjectileMovementType.Normal);
        createProjectile(attackComp, dir, bounds, -attackComp.projectileSpeed * ProjectileSystem.DIAGONAL_PROJECTILE_SCALING,
                -attackComp.projectileSpeed * ProjectileSystem.DIAGONAL_PROJECTILE_SCALING, trTexture, ProjectileMovementType.Normal);
        createProjectile(attackComp, dir, bounds, attackComp.projectileSpeed * ProjectileSystem.DIAGONAL_PROJECTILE_SCALING,
                -attackComp.projectileSpeed * ProjectileSystem.DIAGONAL_PROJECTILE_SCALING, trTexture, ProjectileMovementType.Normal);
    }

    private void shootEight(AttackComponent attackComp, DirectionComponent dir, Rectangle bounds) {
        shootFour(attackComp, dir, bounds);
        shootFourDiagonal(attackComp, dir, bounds);
    }

    private void shootAtPlayer(AttackComponent attackComp, DirectionComponent dir, Rectangle bounds, Rectangle playerBounds) {
        TextureRegion topTexture = res.getTexture(attackComp.attackTexture + Resources.TOP) == null ?
                res.getTexture(attackComp.attackTexture) : res.getTexture(attackComp.attackTexture + Resources.TOP);
        TextureRegion texture = res.getTexture(attackComp.attackTexture);

        boolean xBiased = Math.abs(bounds.x - playerBounds.x) > Math.abs(bounds.y - playerBounds.y);
        float xCenter = playerBounds.x + playerBounds.width / 2;
        float yCenter = playerBounds.y + playerBounds.height / 2;

        dir.facingRight = bounds.x < xCenter;

        if (bounds.x < xCenter && xBiased)
            createProjectile(attackComp, dir, bounds, attackComp.projectileSpeed, 0f, texture, ProjectileMovementType.Normal);
        if (bounds.x >= xCenter && xBiased)
            createProjectile(attackComp, dir, bounds, -attackComp.projectileSpeed, 0f, texture, ProjectileMovementType.Normal);
        if (bounds.y < yCenter && !xBiased)
            createProjectile(attackComp, dir, bounds, 0f, attackComp.projectileSpeed, topTexture, ProjectileMovementType.Normal);
        if (bounds.y >= yCenter && !xBiased)
            createProjectile(attackComp, dir, bounds, 0f, -attackComp.projectileSpeed, topTexture, ProjectileMovementType.Normal);
    }

    private void sprayThree(AttackComponent attackComp, Rectangle bounds) {
        TextureRegion topTexture = res.getTexture(attackComp.attackTexture + Resources.TOP) == null ?
                res.getTexture(attackComp.attackTexture) : res.getTexture(attackComp.attackTexture + Resources.TOP);
        TextureRegion side = res.getTexture(attackComp.attackTexture + Resources.TOP_RIGHT) == null ?
                res.getTexture(attackComp.attackTexture) : res.getTexture(attackComp.attackTexture + Resources.TOP_RIGHT);
        createGravityProjectile(attackComp, bounds, 0f, attackComp.projectileSpeed, topTexture);
        createGravityProjectile(attackComp, bounds, -attackComp.projectileSpeed / 4, attackComp.projectileSpeed, side);
        createGravityProjectile(attackComp, bounds, attackComp.projectileSpeed / 4, attackComp.projectileSpeed, side);
    }

    private void random(AttackComponent attackComp, Rectangle bounds, DirectionComponent dir) {
        int action = MathUtils.random(3);
        TextureRegion texture = res.getTexture(attackComp.attackTexture);
        TextureRegion topTexture = res.getTexture(attackComp.attackTexture + Resources.TOP) == null ?
                res.getTexture(attackComp.attackTexture) : res.getTexture(attackComp.attackTexture + Resources.TOP);
        switch (action) {
            case 0:
                dir.facingRight = true;
                createProjectile(attackComp, dir, bounds, attackComp.projectileSpeed, 0f, texture, ProjectileMovementType.Normal);
                break;
            case 1:
                dir.facingRight = false;
                createProjectile(attackComp, dir, bounds, -attackComp.projectileSpeed, 0f, texture, ProjectileMovementType.Normal);
                break;
            case 2:
                createProjectile(attackComp, dir, bounds, 0f, attackComp.projectileSpeed, topTexture, ProjectileMovementType.Normal);
                break;
            case 3:
                createProjectile(attackComp, dir, bounds, 0f, -attackComp.projectileSpeed, topTexture, ProjectileMovementType.Normal);
                break;
        }
    }

    private void arcTwo(AttackComponent attackComp, Rectangle bounds, DirectionComponent dir) {
        TextureRegion texture = res.getTexture(attackComp.attackTexture + Resources.TOP_RIGHT) == null ?
                res.getTexture(attackComp.attackTexture) : res.getTexture(attackComp.attackTexture + Resources.TOP_RIGHT);
        float initialDx = dir.facingRight ? -attackComp.projectileSpeed * ProjectileSystem.DIAGONAL_PROJECTILE_SCALING
                            : attackComp.projectileSpeed * ProjectileSystem.DIAGONAL_PROJECTILE_SCALING;
        createProjectile(attackComp, dir, bounds, initialDx,
                attackComp.projectileSpeed * ProjectileSystem.DIAGONAL_PROJECTILE_SCALING, texture, ProjectileMovementType.Arc);
        createProjectile(attackComp, dir, bounds, initialDx,
                -attackComp.projectileSpeed * ProjectileSystem.DIAGONAL_PROJECTILE_SCALING, texture, ProjectileMovementType.Arc);
    }

    private void explodeOnDeath(Entity entity, AttackComponent attackComp, DirectionComponent dir, Rectangle bounds) {
        RemoveComponent remove = Mapper.REMOVE_MAPPER.get(entity);
        if (remove.shouldRemove) {
            shootEight(attackComp, dir, bounds);
        }
    }

    private void horizontalWave(AttackComponent attackComp, Rectangle bounds, DirectionComponent dir) {
        TextureRegion texture = res.getTexture(attackComp.attackTexture + Resources.TOP_RIGHT) == null ?
                res.getTexture(attackComp.attackTexture) : res.getTexture(attackComp.attackTexture + Resources.TOP_RIGHT);
        Entity proj = createProjectile(attackComp, dir, bounds, dir.facingRight ? attackComp.projectileSpeed : -attackComp.projectileSpeed,
                0f, texture, ProjectileMovementType.Wave);
        ProjectileComponent projComp = Mapper.PROJ_MAPPER.get(proj);
        projComp.waveDir = Direction.Right;
    }

    private void verticalWave(AttackComponent attackComp, Rectangle bounds, DirectionComponent dir) {
        TextureRegion texture = res.getTexture(attackComp.attackTexture + Resources.TOP_RIGHT) == null ?
                res.getTexture(attackComp.attackTexture) : res.getTexture(attackComp.attackTexture + Resources.TOP_RIGHT);
        Entity proj = createProjectile(attackComp, dir, bounds, 0f,
                MathUtils.randomBoolean() ? attackComp.projectileSpeed : -attackComp.projectileSpeed,
                texture, ProjectileMovementType.Wave);
        ProjectileComponent projComp = Mapper.PROJ_MAPPER.get(proj);
        projComp.waveDir = Direction.Up;
    }

    private void twoHorizontalWave(AttackComponent attackComp, Rectangle bounds, DirectionComponent dir) {
        TextureRegion texture = res.getTexture(attackComp.attackTexture + Resources.TOP_RIGHT) == null?
                res.getTexture(attackComp.attackTexture) : res.getTexture(attackComp.attackTexture + Resources.TOP_RIGHT);
        Entity projLeft = createProjectile(attackComp, dir, bounds, -attackComp.projectileSpeed,
                0f, texture, ProjectileMovementType.Wave);
        Entity projRight = createProjectile(attackComp, dir, bounds, attackComp.projectileSpeed,
                0f, texture, ProjectileMovementType.Wave);
        ProjectileComponent pl = Mapper.PROJ_MAPPER.get(projLeft);
        ProjectileComponent pr = Mapper.PROJ_MAPPER.get(projRight);
        pl.waveDir = Direction.Left;
        pr.waveDir = Direction.Right;
    }

    private void twoVerticalWave(AttackComponent attackComp, Rectangle bounds, DirectionComponent dir) {
        TextureRegion texture = res.getTexture(attackComp.attackTexture + Resources.TOP_RIGHT) == null ?
                res.getTexture(attackComp.attackTexture) : res.getTexture(attackComp.attackTexture + Resources.TOP_RIGHT);
        Entity projTop = createProjectile(attackComp, dir, bounds, 0f, attackComp.projectileSpeed,
                texture, ProjectileMovementType.Wave);
        Entity projBot = createProjectile(attackComp, dir, bounds, 0f, -attackComp.projectileSpeed,
                texture, ProjectileMovementType.Wave);
        ProjectileComponent pt = Mapper.PROJ_MAPPER.get(projTop);
        ProjectileComponent pb = Mapper.PROJ_MAPPER.get(projBot);
        pt.waveDir = Direction.Up;
        pb.waveDir = Direction.Down;
    }

    private void fourWave(AttackComponent attackComp, Rectangle bounds, DirectionComponent dir) {
        twoHorizontalWave(attackComp, bounds, dir);
        twoVerticalWave(attackComp, bounds, dir);
    }

    private Entity createProjectile(AttackComponent attackComp, DirectionComponent dir, Rectangle bounds,
                                 float dx, float dy, TextureRegion texture,
                                 ProjectileMovementType movementType) {
        float bw = texture.getRegionWidth() - 1;
        float bh = texture.getRegionHeight() - 1;
        return EntityBuilder.instance((PooledEngine) getEngine())
                .projectile(movementType, dir.facingRight, false, attackComp.projectileDestroyable,
                        attackComp.attackTexture, true,
                        attackComp.damage, 0f, attackComp.attackDetonateTime, attackComp.projectileAcceleration)
                .color(EntityColor.getProjectileColor(attackComp.attackTexture))
                .position(bounds.x + (bounds.width / 2) - (bw / 2), bounds.y + (bounds.height / 2) - (bh / 2))
                .velocity(dx, dy, 0f)
                .boundingBox(bw, bh, 0f, 0f)
                .texture(texture, null)
                .direction(true, true).remove().build();
    }

    private Entity createGravityProjectile(AttackComponent attackComp, Rectangle bounds,
                                        float dx, float dy, TextureRegion texture) {
        float bw = texture.getRegionWidth() - 1;
        float bh = texture.getRegionHeight() - 1;
        return EntityBuilder.instance((PooledEngine) getEngine())
                .projectile(ProjectileMovementType.Normal, false, false, attackComp.projectileDestroyable,
                        attackComp.attackTexture, true, attackComp.damage, 0f, 0f, 0f)
                .color(EntityColor.getProjectileColor(attackComp.attackTexture))
                .position(bounds.x + (bounds.width / 2) - (bw / 2), bounds.y + (bounds.height / 2) - (bh / 2))
                .velocity(dx, dy, 0f)
                .boundingBox(bw, bh, 0f, 0f)
                .texture(texture, null)
                .direction(true, true).gravity(false, GravitySystem.GRAVITY, GravitySystem.TERMINAL_VELOCITY, false).remove().build();
    }

}