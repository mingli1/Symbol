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
import com.symbol.game.ecs.entity.EnemyType
import com.symbol.game.ecs.entity.MapEntityType
import com.symbol.game.ecs.system.GRAVITY
import com.symbol.game.ecs.system.TERMINAL_VELOCITY

class EntityBuilder(private val engine: PooledEngine) {

    private var playerComponent: PlayerComponent? = null
    private var projectileComponent: ProjectileComponent? = null

    private var boundingBoxComponent: BoundingBoxComponent? = null
    private var boundingCircleComponent: BoundingCircleComponent? = null
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
    private var statusEffectComponent: StatusEffectComponent? = null
    private var affectAllComponent: AffectAllComponent? = null

    private var enemyComponent: EnemyComponent? = null
    private var activationComponent: ActivationComponent? = null
    private var corporalComponent: CorporealComponent? = null
    private var attackComponent: AttackComponent? = null
    private var explodeComponent: ExplodeComponent? = null
    private var teleportComponent: TeleportComponent? = null
    private var lastStandComponent: LastStandComponent? = null
    private var trapComponent: TrapComponent? = null
    private var blockComponent: BlockComponent? = null

    private var mapEntityComponent: MapEntityComponent? = null
    private var movingPlatformComponent: MovingPlatformComponent? = null
    private var portalComponent: PortalComponent? = null
    private var clampComponent: ClampComponent? = null
    private var healthPackComponent: HealthPackComponent? = null
    private var squareSwitchComponent: SquareSwitchComponent? = null
    private var toggleTileComponent: ToggleTileComponent? = null
    private var forceFieldComponent: ForceFieldComponent? = null
    private var damageBoostComponent: DamageBoostComponent? = null
    private var mirrorComponent: MirrorComponent? = null
    private var invertSwitchComponent: InvertSwitchComponent? = null
    private var backAndForthComponent: BackAndForthComponent? = null

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
                   sub: Boolean = false,
                   collidesWithTerrain: Boolean = true,
                   collidesWithProjectiles: Boolean = false,
                   textureStr: String? = null,
                   damage: Int = 0,
                   knockback: Float = 0f,
                   playerType: Int = 0,
                   detonateTime: Float = 0f,
                   acceleration: Float = 0f) : EntityBuilder {
        projectileComponent = engine.createComponent(ProjectileComponent::class.java)
        projectileComponent?.movementType = movementType
        projectileComponent?.parentFacingRight = parentFacingRight
        projectileComponent?.sub = sub
        projectileComponent?.collidesWithTerrain = collidesWithTerrain
        projectileComponent?.collidesWithProjectiles = collidesWithProjectiles
        projectileComponent?.textureStr = textureStr
        projectileComponent?.damage = damage
        projectileComponent?.knockback = knockback
        projectileComponent?.playerType = playerType
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

    fun boundingCircle(x: Float = 0f, y: Float = 0f, radius: Float = 0f) : EntityBuilder {
        boundingCircleComponent = engine.createComponent(BoundingCircleComponent::class.java)
        boundingCircleComponent?.circle?.set(x, y, radius)
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
        positionComponent?.originX = x
        positionComponent?.originY = y
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

    fun orbit(clockwise: Boolean = true, angle: Float = 0f,
              speed: Float = 0f, radius: Float = 0f) : EntityBuilder {
        orbitComponent = engine.createComponent(OrbitComponent::class.java)
        orbitComponent?.clockwise = clockwise
        orbitComponent?.angle = angle
        orbitComponent?.speed = speed
        orbitComponent?.radius = radius
        return this
    }

    fun statusEffect(type: StatusEffect = StatusEffect.None, apply: StatusEffect = StatusEffect.None,
                     duration: Float = 0f, value: Float = 0f) : EntityBuilder {
        statusEffectComponent = engine.createComponent(StatusEffectComponent::class.java)
        statusEffectComponent?.type = type
        statusEffectComponent?.apply = apply
        statusEffectComponent?.duration = duration
        statusEffectComponent?.value = value
        return this
    }

    fun affectAll() : EntityBuilder {
        affectAllComponent = engine.createComponent(AffectAllComponent::class.java)
        return this
    }

    fun enemy(enemyType: EnemyType = EnemyType.None,
              movementType: EnemyMovementType = EnemyMovementType.None,
              attackType: EnemyAttackType = EnemyAttackType.None,
              parent: Entity? = null,
              visible: Boolean = true) : EntityBuilder {
        enemyComponent = engine.createComponent(EnemyComponent::class.java)
        enemyComponent?.enemyType = enemyType
        enemyComponent?.movementType = movementType
        enemyComponent?.attackType = attackType
        enemyComponent?.parent = parent
        enemyComponent?.visible = visible
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

    fun teleport(range: Float = 0f, freq: Float = 0f) : EntityBuilder {
        teleportComponent = engine.createComponent(TeleportComponent::class.java)
        teleportComponent?.range = range
        teleportComponent?.freq = freq
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

    fun block() : EntityBuilder {
        blockComponent = engine.createComponent(BlockComponent::class.java)
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

    fun movingPlatform() : EntityBuilder {
        movingPlatformComponent = engine.createComponent(MovingPlatformComponent::class.java)
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

    fun healthPack(regen: Int = 0, regenTime: Float = 0f) : EntityBuilder {
        healthPackComponent = engine.createComponent(HealthPackComponent::class.java)
        healthPackComponent?.regen = regen
        healthPackComponent?.regenTime = regenTime
        return this
    }

    fun squareSwitch(targetId: Int) : EntityBuilder {
        squareSwitchComponent = engine.createComponent(SquareSwitchComponent::class.java)
        squareSwitchComponent?.targetId = targetId
        return this
    }

    fun toggleTile(id: Int, lx: Float = 0f, ly: Float = 0f, lw: Float = 0f, lh: Float = 0f) : EntityBuilder {
        toggleTileComponent = engine.createComponent(ToggleTileComponent::class.java)
        toggleTileComponent?.id = id
        toggleTileComponent?.lethalRect?.set(lx, ly, lw, lh)
        return this
    }

    fun forceField(duration: Float = 0f) : EntityBuilder {
        forceFieldComponent = engine.createComponent(ForceFieldComponent::class.java)
        forceFieldComponent?.duration = duration
        return this
    }

    fun damageBoost(damageBoost: Int = 0, duration: Float = 0f) : EntityBuilder {
        damageBoostComponent = engine.createComponent(DamageBoostComponent::class.java)
        damageBoostComponent?.damageBoost = damageBoost
        damageBoostComponent?.duration = duration
        return this
    }

    fun mirror(orientation: String) : EntityBuilder {
        mirrorComponent = engine.createComponent(MirrorComponent::class.java)
        mirrorComponent?.orientation = MirrorComponent.Orientation.getType(orientation)!!
        return this
    }

    fun invertSwitch() : EntityBuilder {
        invertSwitchComponent = engine.createComponent(InvertSwitchComponent::class.java)
        return this
    }

    fun backAndForth(dist: Float = 0f, positive: Boolean = true) : EntityBuilder {
        backAndForthComponent = engine.createComponent(BackAndForthComponent::class.java)
        backAndForthComponent?.dist = dist
        backAndForthComponent?.positive = positive
        return this
    }

    fun build() : Entity {
        val entity = engine.createEntity()

        if (playerComponent != null) entity.add(playerComponent)
        if (projectileComponent != null) entity.add(projectileComponent)

        if (colorComponent != null) entity.add(colorComponent)
        if (boundingBoxComponent != null) entity.add(boundingBoxComponent)
        if (boundingCircleComponent != null) entity.add(boundingCircleComponent)
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
        if (statusEffectComponent != null) entity.add(statusEffectComponent)
        if (affectAllComponent != null) entity.add(affectAllComponent)

        if (enemyComponent != null) entity.add(enemyComponent)
        if (activationComponent != null) entity.add(activationComponent)
        if (corporalComponent != null) entity.add(corporalComponent)
        if (attackComponent != null) entity.add(attackComponent)
        if (explodeComponent != null) entity.add(explodeComponent)
        if (teleportComponent != null) entity.add(teleportComponent)
        if (lastStandComponent != null) entity.add(lastStandComponent)
        if (trapComponent != null) entity.add(trapComponent)
        if (blockComponent != null) entity.add(blockComponent)

        if (mapEntityComponent != null) entity.add(mapEntityComponent)
        if (movingPlatformComponent != null) entity.add(movingPlatformComponent)
        if (portalComponent != null) entity.add(portalComponent)
        if (clampComponent != null) entity.add(clampComponent)
        if (healthPackComponent != null) entity.add(healthPackComponent)
        if (squareSwitchComponent != null) entity.add(squareSwitchComponent)
        if (toggleTileComponent != null) entity.add(toggleTileComponent)
        if (forceFieldComponent != null) entity.add(forceFieldComponent)
        if (damageBoostComponent != null) entity.add(damageBoostComponent)
        if (mirrorComponent != null) entity.add(mirrorComponent)
        if (invertSwitchComponent != null) entity.add(invertSwitchComponent)
        if (backAndForthComponent != null) entity.add(backAndForthComponent)

        engine.addEntity(entity)
        return entity
    }

}