package com.symbol.ecs

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.symbol.ecs.component.*
import com.symbol.ecs.component.EnemyComponent
import com.symbol.ecs.component.PlayerComponent
import com.symbol.ecs.component.ProjectileComponent
import com.symbol.ecs.entity.EnemyAttackType
import com.symbol.ecs.entity.EnemyMovementType
import com.symbol.ecs.entity.EnemyType
import com.symbol.ecs.system.GRAVITY
import com.symbol.ecs.system.TERMINAL_VELOCITY

class EntityBuilder(private val engine: PooledEngine) {

    private var playerComponent: PlayerComponent? = null
    private var enemyComponent: EnemyComponent? = null
    private var projectileComponent: ProjectileComponent? = null

    private var boundingBoxComponent: BoundingBoxComponent? = null
    private var directionComponent: DirectionComponent? = null
    private var gravityComponent: GravityComponent? = null
    private var healthComponent: HealthComponent? = null
    private var knockbackComponent: KnockbackComponent? = null
    private var positionComponent: PositionComponent? = null
    private var removeComponent: RemoveComponent? = null
    private var textureComponent: TextureComponent? = null
    private var velocityComponent: VelocityComponent? = null

    companion object {
        fun instance(engine: PooledEngine) : EntityBuilder = EntityBuilder(engine)
    }

    fun player(canDoubleJump: Boolean = false, canShoot: Boolean = false) : EntityBuilder {
        playerComponent = engine.createComponent(PlayerComponent::class.java)
        playerComponent?.canDoubleJump = canDoubleJump
        playerComponent?.canShoot = canShoot
        return this
    }

    fun enemy(type: EnemyType,
              movementType: EnemyMovementType = EnemyMovementType.None,
              attackType: EnemyAttackType = EnemyAttackType.None,
              damage: Int = 0,
              jumpImpulse: Float = 0f,
              activationRange: Float = -1f,
              attackRate: Float = 0f,
              attackTexture: String? = null,
              projectileSpeed: Float = 0f,
              attackDetonateTime: Float = 0f,
              explodeOnDeath: Boolean = false) : EntityBuilder {
        enemyComponent = engine.createComponent(EnemyComponent::class.java)
        enemyComponent?.type = type
        enemyComponent?.movementType = movementType
        enemyComponent?.attackType = attackType
        enemyComponent?.jumpImpulse = jumpImpulse
        enemyComponent?.damage = damage
        enemyComponent?.activationRange = activationRange
        enemyComponent?.attackRate = attackRate
        enemyComponent?.attackTexture = attackTexture
        enemyComponent?.projectileSpeed = projectileSpeed
        enemyComponent?.attackDetonateTime = attackDetonateTime
        enemyComponent?.explodeOnDeath = explodeOnDeath
        return this
    }

    fun projectile(unstoppable: Boolean = false,
                   textureStr: String? = null,
                   enemy: Boolean = false,
                   damage: Int = 0,
                   knockback: Float = 0f,
                   detonateTime: Float = 0f) : EntityBuilder {
        projectileComponent = engine.createComponent(ProjectileComponent::class.java)
        projectileComponent?.unstoppable = unstoppable
        projectileComponent?.textureStr = textureStr
        projectileComponent?.enemy = enemy
        projectileComponent?.damage = damage
        projectileComponent?.knockback = knockback
        projectileComponent?.detonateTime = detonateTime
        return this
    }

    fun boundingBox(bx: Float, by: Float) : EntityBuilder {
        boundingBoxComponent = engine.createComponent(BoundingBoxComponent::class.java)
        boundingBoxComponent?.rect?.setSize(bx, by)
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

        engine.addEntity(entity)
        return entity
    }

}