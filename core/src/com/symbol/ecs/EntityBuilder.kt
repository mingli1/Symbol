package com.symbol.ecs

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.symbol.ecs.component.*
import com.symbol.ecs.component.EnemyComponent
import com.symbol.ecs.component.PlayerComponent
import com.symbol.ecs.component.ProjectileComponent
import com.symbol.ecs.component.map.*
import com.symbol.ecs.entity.EnemyAttackType
import com.symbol.ecs.entity.EnemyMovementType
import com.symbol.ecs.entity.MapEntityType
import com.symbol.ecs.system.GRAVITY
import com.symbol.ecs.system.TERMINAL_VELOCITY

class EntityBuilder(private val engine: PooledEngine) {

    private var playerComponent: PlayerComponent? = null
    private var enemyComponent: EnemyComponent? = null
    private var projectileComponent: ProjectileComponent? = null

    private var mapEntityComponent: MapEntityComponent? = null
    private var movingPlatformComponent: MovingPlatformComponent? = null
    private var portalComponent: PortalComponent? = null
    private var clampComponent: ClampComponent? = null
    private var healthPackComponent: HealthPackComponent? = null

    private var boundingBoxComponent: BoundingBoxComponent? = null
    private var directionComponent: DirectionComponent? = null
    private var gravityComponent: GravityComponent? = null
    private var healthComponent: HealthComponent? = null
    private var knockbackComponent: KnockbackComponent? = null
    private var positionComponent: PositionComponent? = null
    private var removeComponent: RemoveComponent? = null
    private var textureComponent: TextureComponent? = null
    private var velocityComponent: VelocityComponent? = null
    private var orbitComponent: OrbitComponent? = null

    companion object {
        fun instance(engine: PooledEngine) : EntityBuilder = EntityBuilder(engine)
    }

    fun player(canDoubleJump: Boolean = false, canShoot: Boolean = false) : EntityBuilder {
        playerComponent = engine.createComponent(PlayerComponent::class.java)
        playerComponent?.canDoubleJump = canDoubleJump
        playerComponent?.canShoot = canShoot
        return this
    }

    fun enemy(movementType: EnemyMovementType = EnemyMovementType.None,
              attackType: EnemyAttackType = EnemyAttackType.None,
              damage: Int = 0,
              jumpImpulse: Float = 0f,
              activationRange: Float = -1f,
              attackRate: Float = 0f,
              attackTexture: String? = null,
              projectileSpeed: Float = 0f,
              projectileAcceleration: Float = 0f,
              attackDetonateTime: Float = 0f,
              explodeOnDeath: Boolean = false,
              teleportOnHit: Boolean = false,
              lastStand: Boolean = false,
              parent: Entity? = null) : EntityBuilder {
        enemyComponent = engine.createComponent(EnemyComponent::class.java)
        enemyComponent?.movementType = movementType
        enemyComponent?.attackType = attackType
        enemyComponent?.jumpImpulse = jumpImpulse
        enemyComponent?.damage = damage
        enemyComponent?.activationRange = activationRange
        enemyComponent?.attackRate = attackRate
        enemyComponent?.attackTexture = attackTexture
        enemyComponent?.projectileSpeed = projectileSpeed
        enemyComponent?.projectileAcceleration = projectileAcceleration
        enemyComponent?.attackDetonateTime = attackDetonateTime
        enemyComponent?.explodeOnDeath = explodeOnDeath
        enemyComponent?.teleportOnHit = teleportOnHit
        enemyComponent?.lastStand = lastStand
        enemyComponent?.parent = parent
        return this
    }

    fun projectile(unstoppable: Boolean = false,
                   textureStr: String? = null,
                   enemy: Boolean = false,
                   damage: Int = 0,
                   knockback: Float = 0f,
                   detonateTime: Float = 0f,
                   acceleration: Float = 0f) : EntityBuilder {
        projectileComponent = engine.createComponent(ProjectileComponent::class.java)
        projectileComponent?.unstoppable = unstoppable
        projectileComponent?.textureStr = textureStr
        projectileComponent?.enemy = enemy
        projectileComponent?.damage = damage
        projectileComponent?.knockback = knockback
        projectileComponent?.detonateTime = detonateTime
        projectileComponent?.acceleration = acceleration
        return this
    }

    fun mapEntity(type: MapEntityType = MapEntityType.None, projectileCollidable: Boolean = false) : EntityBuilder {
        mapEntityComponent = engine.createComponent(MapEntityComponent::class.java)
        mapEntityComponent?.mapEntityType = type
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

    fun texture(texture: TextureRegion) : EntityBuilder {
        textureComponent = engine.createComponent(TextureComponent::class.java)
        textureComponent?.texture = texture
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

    fun build() : Entity {
        val entity = engine.createEntity()

        if (enemyComponent != null) entity.add(enemyComponent)
        if (playerComponent != null) entity.add(playerComponent)
        if (projectileComponent != null) entity.add(projectileComponent)
        if (boundingBoxComponent != null) entity.add(boundingBoxComponent)
        if (directionComponent != null) entity.add(directionComponent)
        if (gravityComponent != null) entity.add(gravityComponent)
        if (healthComponent != null) entity.add(healthComponent)
        if (knockbackComponent != null) entity.add(knockbackComponent)
        if (positionComponent != null) entity.add(positionComponent)
        if (removeComponent != null) entity.add(removeComponent)
        if (textureComponent != null) entity.add(textureComponent)
        if (velocityComponent != null) entity.add(velocityComponent)
        if (orbitComponent != null) entity.add(orbitComponent)
        if (mapEntityComponent != null) entity.add(mapEntityComponent)
        if (movingPlatformComponent != null) entity.add(movingPlatformComponent)
        if (portalComponent != null) entity.add(portalComponent)
        if (clampComponent != null) entity.add(clampComponent)
        if (healthPackComponent != null) entity.add(healthPackComponent)

        engine.addEntity(entity)
        return entity
    }

}