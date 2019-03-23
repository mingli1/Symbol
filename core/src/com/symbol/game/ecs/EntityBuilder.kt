package com.symbol.game.ecs

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.symbol.game.ecs.component.*
import com.symbol.game.ecs.component.enemy.*
import com.symbol.game.ecs.component.map.*
import com.symbol.game.ecs.entity.EnemyAttackType
import com.symbol.game.ecs.entity.EnemyMovementType
import com.symbol.game.ecs.entity.MapEntityType
import com.symbol.game.ecs.system.GRAVITY
import com.symbol.game.ecs.system.TERMINAL_VELOCITY

class EntityBuilder(private val engine: PooledEngine) {

    private var playerComponent: PlayerComponent? = null
    private var projectileComponent: ProjectileComponent? = null

    private var boundingBoxComponent: BoundingBoxComponent? = null
    private var directionComponent: DirectionComponent? = null
    private var gravityComponent: GravityComponent? = null
    private var jumpComponent: JumpComponent? = null
    private var healthComponent: HealthComponent? = null
    private var knockbackComponent: KnockbackComponent? = null
    private var positionComponent: PositionComponent? = null
    private var removeComponent: RemoveComponent? = null
    private var textureComponent: TextureComponent? = null
    private var velocityComponent: VelocityComponent? = null
    private var orbitComponent: OrbitComponent? = null
    private var colorComponent: ColorComponent? = null

    private var enemyComponent: EnemyComponent? = null
    private var activationComponent: ActivationComponent? = null
    private var corporalComponent: CorporealComponent? = null
    private var attackComponent: AttackComponent? = null
    private var explodeComponent: ExplodeComponent? = null
    private var teleportComponent: TeleportComponent? = null
    private var lastStandComponent: LastStandComponent? = null
    private var trapComponent: TrapComponent? = null

    private var mapEntityComponent: MapEntityComponent? = null
    private var movingPlatformComponent: MovingPlatformComponent? = null
    private var portalComponent: PortalComponent? = null
    private var clampComponent: ClampComponent? = null
    private var healthPackComponent: HealthPackComponent? = null
    private var squareSwitchComponent: SquareSwitchComponent? = null
    private var toggleTileComponent: ToggleTileComponent? = null

    companion object {
        fun instance(engine: PooledEngine) : EntityBuilder = EntityBuilder(engine)
    }

    fun player(canDoubleJump: Boolean = false, canShoot: Boolean = false) : EntityBuilder {
        playerComponent = engine.createComponent(PlayerComponent::class.java)
        playerComponent?.canDoubleJump = canDoubleJump
        playerComponent?.canShoot = canShoot
        return this
    }

    fun projectile(movementType: ProjectileMovementType = ProjectileMovementType.Normal,
                   parentFacingRight: Boolean = false,
                   collidesWithTerrain: Boolean = true,
                   collidesWithProjectiles: Boolean = false,
                   textureStr: String? = null,
                   enemy: Boolean = false,
                   damage: Int = 0,
                   knockback: Float = 0f,
                   detonateTime: Float = 0f,
                   acceleration: Float = 0f) : EntityBuilder {
        projectileComponent = engine.createComponent(ProjectileComponent::class.java)
        projectileComponent?.movementType = movementType
        projectileComponent?.parentFacingRight = parentFacingRight
        projectileComponent?.collidesWithTerrain = collidesWithTerrain
        projectileComponent?.collidesWithProjectiles = collidesWithProjectiles
        projectileComponent?.textureStr = textureStr
        projectileComponent?.enemy = enemy
        projectileComponent?.damage = damage
        projectileComponent?.knockback = knockback
        projectileComponent?.detonateTime = detonateTime
        projectileComponent?.acceleration = acceleration
        return this
    }

    fun color(hex: String) : EntityBuilder {
        colorComponent = engine.createComponent(ColorComponent::class.java)
        colorComponent?.hex = hex
        return this
    }

    fun boundingBox(bx: Float, by: Float, x: Float = 0f, y: Float = 0f) : EntityBuilder {
        boundingBoxComponent = engine.createComponent(BoundingBoxComponent::class.java)
        boundingBoxComponent?.rect?.set(x, y, bx, by)
        return this
    }

    fun direction(facingRight: Boolean = true, yFlip: Boolean = false) : EntityBuilder {
        directionComponent = engine.createComponent(DirectionComponent::class.java)
        directionComponent?.facingRight = facingRight
        directionComponent?.yFlip = yFlip
        return this
    }

    fun gravity(onGround: Boolean = false, gravity: Float = GRAVITY,
                terminalVelocity: Float = TERMINAL_VELOCITY, collidable: Boolean = true) : EntityBuilder {
        gravityComponent = engine.createComponent(GravityComponent::class.java)
        gravityComponent?.onGround = onGround
        gravityComponent?.gravity = gravity
        gravityComponent?.terminalVelocity = terminalVelocity
        gravityComponent?.collidable = collidable
        return this
    }

    fun jump(impulse: Float = 0f) : EntityBuilder {
        jumpComponent = engine.createComponent(JumpComponent::class.java)
        jumpComponent?.impulse = impulse
        return this
    }

    fun health(hp: Int) : EntityBuilder {
        healthComponent = engine.createComponent(HealthComponent::class.java)
        healthComponent?.hp = hp
        healthComponent?.maxHp = hp
        return this
    }

    fun knockback() : EntityBuilder {
        knockbackComponent = engine.createComponent(KnockbackComponent::class.java)
        return this
    }

    fun position(x: Float, y: Float) : EntityBuilder {
        positionComponent = engine.createComponent(PositionComponent::class.java)
        positionComponent?.x = x
        positionComponent?.y = y
        return this
    }

    fun remove() : EntityBuilder {
        removeComponent = engine.createComponent(RemoveComponent::class.java)
        return this
    }

    fun texture(texture: TextureRegion, textureStr: String? = null) : EntityBuilder {
        textureComponent = engine.createComponent(TextureComponent::class.java)
        textureComponent?.texture = texture
        textureComponent?.textureStr = textureStr
        return this
    }

    fun velocity(dx: Float = 0f, dy: Float = 0f, speed: Float = 0f) : EntityBuilder {
        velocityComponent = engine.createComponent(VelocityComponent::class.java)
        velocityComponent?.dx = dx
        velocityComponent?.dy = dy
        velocityComponent?.speed = speed
        return this
    }

    fun orbit(clockwise: Boolean = true, originX: Float = 0f, originY: Float = 0f,
              angle: Float = 0f, speed: Float = 0f, radius: Float = 0f) : EntityBuilder {
        orbitComponent = engine.createComponent(OrbitComponent::class.java)
        orbitComponent?.clockwise = clockwise
        orbitComponent?.originX = originX
        orbitComponent?.originY = originY
        orbitComponent?.angle = angle
        orbitComponent?.speed = speed
        orbitComponent?.radius = radius
        return this
    }

    fun enemy(movementType: EnemyMovementType = EnemyMovementType.None,
              attackType: EnemyAttackType = EnemyAttackType.None,
              parent: Entity? = null) : EntityBuilder {
        enemyComponent = engine.createComponent(EnemyComponent::class.java)
        enemyComponent?.movementType = movementType
        enemyComponent?.attackType = attackType
        enemyComponent?.parent = parent
        return this
    }

    fun activation(activationRange: Float = -1f) : EntityBuilder {
        activationComponent = engine.createComponent(ActivationComponent::class.java)
        activationComponent?.activationRange = activationRange
        return this
    }

    fun corporeal(corporeal: Boolean = true, incorporealTime: Float = 0f) : EntityBuilder {
        corporalComponent = engine.createComponent(CorporealComponent::class.java)
        corporalComponent?.corporeal = corporeal
        corporalComponent?.incorporealTime = incorporealTime
        return this
    }

    fun attack(damage: Int = 0, attackRate: Float = 0f, attackTexture: String? = null,
               projectileSpeed: Float = 0f, projectileAcceleration: Float = 0f,
               projectileDestroyable: Boolean = false,
               attackDetonateTime: Float = 0f) : EntityBuilder {
        attackComponent = engine.createComponent(AttackComponent::class.java)
        attackComponent?.damage = damage
        attackComponent?.attackRate = attackRate
        attackComponent?.attackTexture = attackTexture
        attackComponent?.projectileSpeed = projectileSpeed
        attackComponent?.projectileAcceleration = projectileAcceleration
        attackComponent?.projectileDestroyable = projectileDestroyable
        attackComponent?.attackDetonateTime = attackDetonateTime
        return this
    }

    fun explode() : EntityBuilder {
        explodeComponent = engine.createComponent(ExplodeComponent::class.java)
        return this
    }

    fun teleport() : EntityBuilder {
        teleportComponent = engine.createComponent(TeleportComponent::class.java)
        return this
    }

    fun lastStand() : EntityBuilder {
        lastStandComponent = engine.createComponent(LastStandComponent::class.java)
        return this
    }

    fun trap() : EntityBuilder {
        trapComponent = engine.createComponent(TrapComponent::class.java)
        return this
    }

    fun mapEntity(type: MapEntityType = MapEntityType.None,
                  mapCollidable: Boolean = false,
                  projectileCollidable: Boolean = false) : EntityBuilder {
        mapEntityComponent = engine.createComponent(MapEntityComponent::class.java)
        mapEntityComponent?.mapEntityType = type
        mapEntityComponent?.mapCollidable = mapCollidable
        mapEntityComponent?.projectileCollidable = projectileCollidable
        return this
    }

    fun movingPlatform(distance: Float = 0f, originX: Float = 0f, originY: Float = 0f, positive: Boolean = true) : EntityBuilder {
        movingPlatformComponent = engine.createComponent(MovingPlatformComponent::class.java)
        movingPlatformComponent?.distance = distance
        movingPlatformComponent?.originX = originX
        movingPlatformComponent?.originY = originY
        movingPlatformComponent?.positive = positive
        return this
    }

    fun portal(id: Int, target: Int) : EntityBuilder {
        portalComponent = engine.createComponent(PortalComponent::class.java)
        portalComponent?.id = id
        portalComponent?.target = target
        return this
    }

    fun clamp(right: Boolean, rect: Rectangle, acceleration: Float = 0f, backVelocity: Float = 0f) : EntityBuilder {
        clampComponent = engine.createComponent(ClampComponent::class.java)
        clampComponent?.right = right
        clampComponent?.rect = rect
        clampComponent?.acceleration = acceleration
        clampComponent?.backVelocity = backVelocity
        return this
    }

    fun healthPack(regen: Int = 0) : EntityBuilder {
        healthPackComponent = engine.createComponent(HealthPackComponent::class.java)
        healthPackComponent?.regen = regen
        return this
    }

    fun squareSwitch(targetId: Int) : EntityBuilder {
        squareSwitchComponent = engine.createComponent(SquareSwitchComponent::class.java)
        squareSwitchComponent?.targetId = targetId
        return this
    }

    fun toggleTile(id: Int) : EntityBuilder {
        toggleTileComponent = engine.createComponent(ToggleTileComponent::class.java)
        toggleTileComponent?.id = id
        return this
    }

    fun build() : Entity {
        val entity = engine.createEntity()

        if (playerComponent != null) entity.add(playerComponent)
        if (projectileComponent != null) entity.add(projectileComponent)

        if (colorComponent != null) entity.add(colorComponent)
        if (boundingBoxComponent != null) entity.add(boundingBoxComponent)
        if (directionComponent != null) entity.add(directionComponent)
        if (gravityComponent != null) entity.add(gravityComponent)
        if (jumpComponent != null) entity.add(jumpComponent)
        if (healthComponent != null) entity.add(healthComponent)
        if (knockbackComponent != null) entity.add(knockbackComponent)
        if (positionComponent != null) entity.add(positionComponent)
        if (removeComponent != null) entity.add(removeComponent)
        if (textureComponent != null) entity.add(textureComponent)
        if (velocityComponent != null) entity.add(velocityComponent)
        if (orbitComponent != null) entity.add(orbitComponent)

        if (enemyComponent != null) entity.add(enemyComponent)
        if (activationComponent != null) entity.add(activationComponent)
        if (corporalComponent != null) entity.add(corporalComponent)
        if (attackComponent != null) entity.add(attackComponent)
        if (explodeComponent != null) entity.add(explodeComponent)
        if (teleportComponent != null) entity.add(teleportComponent)
        if (lastStandComponent != null) entity.add(lastStandComponent)
        if (trapComponent != null) entity.add(trapComponent)

        if (mapEntityComponent != null) entity.add(mapEntityComponent)
        if (movingPlatformComponent != null) entity.add(movingPlatformComponent)
        if (portalComponent != null) entity.add(portalComponent)
        if (clampComponent != null) entity.add(clampComponent)
        if (healthPackComponent != null) entity.add(healthPackComponent)
        if (squareSwitchComponent != null) entity.add(squareSwitchComponent)
        if (toggleTileComponent != null) entity.add(toggleTileComponent)

        engine.addEntity(entity)
        return entity
    }

}