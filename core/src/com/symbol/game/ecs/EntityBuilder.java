package com.symbol.game.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.symbol.game.ecs.component.*;
import com.symbol.game.ecs.component.enemy.*;
import com.symbol.game.ecs.component.map.*;
import com.symbol.game.ecs.entity.EnemyAttackType;
import com.symbol.game.ecs.entity.EnemyMovementType;
import com.symbol.game.ecs.entity.MapEntityType;
import com.symbol.game.ecs.entity.ProjectileMovementType;

public class EntityBuilder {

    private PooledEngine engine;

    private PlayerComponent playerComponent;
    private ProjectileComponent projectileComponent;

    private BoundingBoxComponent boundingBoxComponent;
    private DirectionComponent directionComponent;
    private GravityComponent gravityComponent;
    private JumpComponent jumpComponent;
    private HealthComponent healthComponent;
    private KnockbackComponent knockbackComponent;
    private PositionComponent positionComponent;
    private RemoveComponent removeComponent;
    private TextureComponent textureComponent;
    private VelocityComponent velocityComponent;
    private OrbitComponent orbitComponent;
    private ColorComponent colorComponent;

    private EnemyComponent enemyComponent;
    private ActivationComponent activationComponent;
    private CorporealComponent corporalComponent;
    private AttackComponent attackComponent;
    private ExplodeComponent explodeComponent;
    private TeleportComponent teleportComponent;
    private LastStandComponent lastStandComponent;
    private TrapComponent trapComponent;

    private MapEntityComponent mapEntityComponent;
    private MovingPlatformComponent movingPlatformComponent;
    private PortalComponent portalComponent;
    private ClampComponent clampComponent;
    private HealthPackComponent healthPackComponent;
    private SquareSwitchComponent squareSwitchComponent;
    private ToggleTileComponent toggleTileComponent;

    public EntityBuilder(PooledEngine engine) {
        this.engine = engine;
    }

    public static EntityBuilder instance(PooledEngine engine) {
        return new EntityBuilder(engine);
    }

    public EntityBuilder player(boolean canDoubleJump, boolean canShoot) {
        playerComponent = engine.createComponent(PlayerComponent.class);
        playerComponent.canDoubleJump = canDoubleJump;
        playerComponent.canShoot = canShoot;
        return this;
    }

    public EntityBuilder projectile(ProjectileMovementType movementType,
                                    boolean parentFacingRight,
                                    boolean collidesWithTerrain,
                                    boolean collidesWithProjectiles,
                                    String textureStr,
                                    boolean enemy,
                                    int damage,
                                    float knockback,
                                    float detonateTime,
                                    float acceleration) {
        projectileComponent = engine.createComponent(ProjectileComponent.class);
        projectileComponent.movementType = movementType;
        projectileComponent.parentFacingRight = parentFacingRight;
        projectileComponent.collidesWithTerrain = collidesWithTerrain;
        projectileComponent.collidesWithProjectiles = collidesWithProjectiles;
        projectileComponent.textureStr = textureStr;
        projectileComponent.enemy = enemy;
        projectileComponent.damage = damage;
        projectileComponent.knockback = knockback;
        projectileComponent.detonateTime = detonateTime;
        projectileComponent.acceleration = acceleration;
        return this;
    }

    public EntityBuilder color(String hex) {
        colorComponent = engine.createComponent(ColorComponent.class);
        colorComponent.hex = hex;
        return this;
    }

    public EntityBuilder boundingBox(float bx, float by, float x, float y) {
        boundingBoxComponent = engine.createComponent(BoundingBoxComponent.class);
        boundingBoxComponent.rect.set(x, y, bx, by);
        return this;
    }

    public EntityBuilder direction(boolean facingRight, boolean yFlip) {
        directionComponent = engine.createComponent(DirectionComponent.class);
        directionComponent.facingRight = facingRight;
        directionComponent.yFlip = yFlip;
        return this;
    }

    public EntityBuilder gravity(boolean onGround, float gravity, float terminalVelocity, boolean collidable) {
        gravityComponent = engine.createComponent(GravityComponent.class);
        gravityComponent.onGround = onGround;
        gravityComponent.gravity = gravity;
        gravityComponent.terminalVelocity = terminalVelocity;
        gravityComponent.collidable = collidable;
        return this;
    }

    public EntityBuilder jump(float impulse) {
        jumpComponent = engine.createComponent(JumpComponent.class);
        jumpComponent.impulse = impulse;
        return this;
    }

    public EntityBuilder health(int hp) {
        healthComponent = engine.createComponent(HealthComponent.class);
        healthComponent.hp = hp;
        healthComponent.maxHp = hp;
        return this;
    }

    public EntityBuilder knockback() {
        knockbackComponent = engine.createComponent(KnockbackComponent.class);
        return this;
    }

    public EntityBuilder position(float x, float y) {
        positionComponent = engine.createComponent(PositionComponent.class);
        positionComponent.x = x;
        positionComponent.y = y;
        return this;
    }

    public EntityBuilder remove() {
        removeComponent = engine.createComponent(RemoveComponent.class);
        return this;
    }

    public EntityBuilder texture(TextureRegion texture, String textureStr) {
        textureComponent = engine.createComponent(TextureComponent.class);
        textureComponent.texture = texture;
        textureComponent.textureStr = textureStr;
        return this;
    }

    public EntityBuilder velocity(float dx, float dy, float speed) {
        velocityComponent = engine.createComponent(VelocityComponent.class);
        velocityComponent.dx = dx;
        velocityComponent.dy = dy;
        velocityComponent.speed = speed;
        return this;
    }

    public EntityBuilder orbit(boolean clockwise, float originX, float originY,
                               float angle, float speed, float radius) {
        orbitComponent = engine.createComponent(OrbitComponent.class);
        orbitComponent.clockwise = clockwise;
        orbitComponent.originX = originX;
        orbitComponent.originY = originY;
        orbitComponent.angle = angle;
        orbitComponent.speed = speed;
        orbitComponent.radius = radius;
        return this;
    }

    public EntityBuilder enemy(EnemyMovementType movementType, EnemyAttackType attackType, Entity parent) {
        enemyComponent = engine.createComponent(EnemyComponent.class);
        enemyComponent.movementType = movementType;
        enemyComponent.attackType = attackType;
        enemyComponent.parent = parent;
        return this;
    }

    public EntityBuilder activation(float activationRange) {
        activationComponent = engine.createComponent(ActivationComponent.class);
        activationComponent.activationRange = activationRange;
        return this;
    }

    public EntityBuilder corporeal(boolean corporeal, float incorporealTime) {
        corporalComponent = engine.createComponent(CorporealComponent.class);
        corporalComponent.corporeal = corporeal;
        corporalComponent.incorporealTime = incorporealTime;
        return this;
    }

    public EntityBuilder attack(int damage, float attackRate, String attackTexture,
               float projectileSpeed, float projectileAcceleration,
               boolean projectileDestroyable,
               float attackDetonateTime) {
        attackComponent = engine.createComponent(AttackComponent.class);
        attackComponent.damage = damage;
        attackComponent.attackRate = attackRate;
        attackComponent.attackTexture = attackTexture;
        attackComponent.projectileSpeed = projectileSpeed;
        attackComponent.projectileAcceleration = projectileAcceleration;
        attackComponent.projectileDestroyable = projectileDestroyable;
        attackComponent.attackDetonateTime = attackDetonateTime;
        return this;
    }

    public EntityBuilder explode() {
        explodeComponent = engine.createComponent(ExplodeComponent.class);
        return this;
    }

    public EntityBuilder teleport() {
        teleportComponent = engine.createComponent(TeleportComponent.class);
        return this;
    }

    public EntityBuilder lastStand() {
        lastStandComponent = engine.createComponent(LastStandComponent.class);
        return this;
    }

    public EntityBuilder trap() {
        trapComponent = engine.createComponent(TrapComponent.class);
        return this;
    }

    public EntityBuilder mapEntity(MapEntityType type, boolean mapCollidable, boolean projectileCollidable) {
        mapEntityComponent = engine.createComponent(MapEntityComponent.class);
        mapEntityComponent.mapEntityType = type;
        mapEntityComponent.mapCollidable = mapCollidable;
        mapEntityComponent.projectileCollidable = projectileCollidable;
        return this;
    }

    public EntityBuilder movingPlatform(float distance, float originX, float originY, boolean positive) {
        movingPlatformComponent = engine.createComponent(MovingPlatformComponent.class);
        movingPlatformComponent.distance = distance;
        movingPlatformComponent.originX = originX;
        movingPlatformComponent.originY = originY;
        movingPlatformComponent.positive = positive;
        return this;
    }

    public EntityBuilder portal(int id, int target) {
        portalComponent = engine.createComponent(PortalComponent.class);
        portalComponent.id = id;
        portalComponent.target = target;
        return this;
    }

    public EntityBuilder clamp(boolean right, Rectangle rect, float acceleration, float backVelocity) {
        clampComponent = engine.createComponent(ClampComponent.class);
        clampComponent.right = right;
        clampComponent.rect = rect;
        clampComponent.acceleration = acceleration;
        clampComponent.backVelocity = backVelocity;
        return this;
    }

    public EntityBuilder healthPack(int regen) {
        healthPackComponent = engine.createComponent(HealthPackComponent.class);
        healthPackComponent.regen = regen;
        return this;
    }

    public EntityBuilder squareSwitch(int targetId) {
        squareSwitchComponent = engine.createComponent(SquareSwitchComponent.class);
        squareSwitchComponent.targetId = targetId;
        return this;
    }

    public EntityBuilder toggleTile(int id) {
        toggleTileComponent = engine.createComponent(ToggleTileComponent.class);
        toggleTileComponent.id = id;
        return this;
    }

    public Entity build() {
        Entity entity = engine.createEntity();

        if (playerComponent != null) entity.add(playerComponent);
        if (projectileComponent != null) entity.add(projectileComponent);

        if (colorComponent != null) entity.add(colorComponent);
        if (boundingBoxComponent != null) entity.add(boundingBoxComponent);
        if (directionComponent != null) entity.add(directionComponent);
        if (gravityComponent != null) entity.add(gravityComponent);
        if (jumpComponent != null) entity.add(jumpComponent);
        if (healthComponent != null) entity.add(healthComponent);
        if (knockbackComponent != null) entity.add(knockbackComponent);
        if (positionComponent != null) entity.add(positionComponent);
        if (removeComponent != null) entity.add(removeComponent);
        if (textureComponent != null) entity.add(textureComponent);
        if (velocityComponent != null) entity.add(velocityComponent);
        if (orbitComponent != null) entity.add(orbitComponent);

        if (enemyComponent != null) entity.add(enemyComponent);
        if (activationComponent != null) entity.add(activationComponent);
        if (corporalComponent != null) entity.add(corporalComponent);
        if (attackComponent != null) entity.add(attackComponent);
        if (explodeComponent != null) entity.add(explodeComponent);
        if (teleportComponent != null) entity.add(teleportComponent);
        if (lastStandComponent != null) entity.add(lastStandComponent);
        if (trapComponent != null) entity.add(trapComponent);

        if (mapEntityComponent != null) entity.add(mapEntityComponent);
        if (movingPlatformComponent != null) entity.add(movingPlatformComponent);
        if (portalComponent != null) entity.add(portalComponent);
        if (clampComponent != null) entity.add(clampComponent);
        if (healthPackComponent != null) entity.add(healthPackComponent);
        if (squareSwitchComponent != null) entity.add(squareSwitchComponent);
        if (toggleTileComponent != null) entity.add(toggleTileComponent);

        engine.addEntity(entity);
        return entity;
    }

}