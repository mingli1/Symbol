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
import com.symbol.game.util.Orientation

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
    private var accelerationGateComponent: AccelerationGateComponent? = null

    companion object {
        fun instance(engine: PooledEngine) : EntityBuilder = EntityBuilder(engine)
    }

    fun player(canDoubleJump: Boolean = false, canShoot: Boolean = false) : EntityBuilder {
        playerComponent = engine.createComponent(PlayerComponent::class.java).apply {
            this.canDoubleJump = canDoubleJump
            this.canShoot = canShoot
        }
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
        projectileComponent = engine.createComponent(ProjectileComponent::class.java).apply {
            this.movementType = movementType
            this.parentFacingRight = parentFacingRight
            this.sub = sub
            this.collidesWithTerrain = collidesWithTerrain
            this.collidesWithProjectiles = collidesWithProjectiles
            this.textureStr = textureStr
            this.damage = damage
            this.knockback = knockback
            this.playerType = playerType
            this.detonateTime = detonateTime
            this.acceleration = acceleration
        }
        return this
    }

    fun color(hex: String) : EntityBuilder {
        colorComponent = engine.createComponent(ColorComponent::class.java).apply {
            this.hex = hex
        }
        return this
    }

    fun boundingBox(bx: Float, by: Float, x: Float = 0f, y: Float = 0f) : EntityBuilder {
        boundingBoxComponent = engine.createComponent(BoundingBoxComponent::class.java).apply {
            rect.set(x, y, bx, by)
        }
        return this
    }

    fun boundingCircle(x: Float = 0f, y: Float = 0f, radius: Float = 0f) : EntityBuilder {
        boundingCircleComponent = engine.createComponent(BoundingCircleComponent::class.java).apply {
            circle.set(x, y, radius)
        }
        return this
    }

    fun direction(facingRight: Boolean = true, yFlip: Boolean = false) : EntityBuilder {
        directionComponent = engine.createComponent(DirectionComponent::class.java).apply {
            this.facingRight = facingRight
            this.yFlip = yFlip
        }
        return this
    }

    fun gravity(onGround: Boolean = false, gravity: Float = GRAVITY,
                terminalVelocity: Float = TERMINAL_VELOCITY, collidable: Boolean = true) : EntityBuilder {
        gravityComponent = engine.createComponent(GravityComponent::class.java).apply {
            this.onGround = onGround
            this.gravity = gravity
            this.terminalVelocity = terminalVelocity
            this.collidable = collidable
        }
        return this
    }

    fun jump(impulse: Float = 0f) : EntityBuilder {
        jumpComponent = engine.createComponent(JumpComponent::class.java).apply {
            this.impulse = impulse
        }
        return this
    }

    fun health(hp: Int) : EntityBuilder {
        healthComponent = engine.createComponent(HealthComponent::class.java).apply {
            this.hp = hp
            maxHp = hp
        }
        return this
    }

    fun knockback() : EntityBuilder {
        knockbackComponent = engine.createComponent(KnockbackComponent::class.java)
        return this
    }

    fun position(x: Float, y: Float) : EntityBuilder {
        positionComponent = engine.createComponent(PositionComponent::class.java).apply {
            this.x = x
            this.y = y
            originX = x
            originY = y
        }
        return this
    }

    fun remove() : EntityBuilder {
        removeComponent = engine.createComponent(RemoveComponent::class.java)
        return this
    }

    fun texture(texture: TextureRegion, textureStr: String? = null) : EntityBuilder {
        textureComponent = engine.createComponent(TextureComponent::class.java).apply {
            this.texture = texture
            this.textureStr = textureStr
        }
        return this
    }

    fun velocity(dx: Float = 0f, dy: Float = 0f, speed: Float = 0f) : EntityBuilder {
        velocityComponent = engine.createComponent(VelocityComponent::class.java).apply {
            this.dx = dx
            this.dy = dy
            this.speed = speed
        }
        return this
    }

    fun orbit(clockwise: Boolean = true, angle: Float = 0f,
              speed: Float = 0f, radius: Float = 0f) : EntityBuilder {
        orbitComponent = engine.createComponent(OrbitComponent::class.java).apply {
            this.clockwise = clockwise
            this.angle = angle
            this.speed = speed
            this.radius = radius
        }
        return this
    }

    fun statusEffect(type: StatusEffect = StatusEffect.None, apply: StatusEffect = StatusEffect.None,
                     duration: Float = 0f, value: Float = 0f) : EntityBuilder {
        statusEffectComponent = engine.createComponent(StatusEffectComponent::class.java).apply {
            this.type = type
            this.apply = apply
            this.duration = duration
            this.value = value
        }
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
        enemyComponent = engine.createComponent(EnemyComponent::class.java).apply {
            this.enemyType = enemyType
            this.movementType = movementType
            this.attackType = attackType
            this.parent = parent
            this.visible = visible
        }
        return this
    }

    fun activation(activationRange: Float = -1f) : EntityBuilder {
        activationComponent = engine.createComponent(ActivationComponent::class.java).apply {
            this.activationRange = activationRange
        }
        return this
    }

    fun corporeal(corporeal: Boolean = true, incorporealTime: Float = 0f) : EntityBuilder {
        corporalComponent = engine.createComponent(CorporealComponent::class.java).apply {
            this.corporeal = corporeal
            this.incorporealTime = incorporealTime
        }
        return this
    }

    fun attack(damage: Int = 0, attackRate: Float = 0f, attackTexture: String? = null,
               projectileSpeed: Float = 0f, projectileAcceleration: Float = 0f,
               projectileDestroyable: Boolean = false,
               attackDetonateTime: Float = 0f) : EntityBuilder {
        attackComponent = engine.createComponent(AttackComponent::class.java).apply {
            this.damage = damage
            this.attackRate = attackRate
            this.attackTexture = attackTexture
            this.projectileSpeed = projectileSpeed
            this.projectileAcceleration = projectileAcceleration
            this.projectileDestroyable = projectileDestroyable
            this.attackDetonateTime = attackDetonateTime
        }
        return this
    }

    fun explode() : EntityBuilder {
        explodeComponent = engine.createComponent(ExplodeComponent::class.java)
        return this
    }

    fun teleport(range: Float = 0f, freq: Float = 0f) : EntityBuilder {
        teleportComponent = engine.createComponent(TeleportComponent::class.java).apply {
            this.range = range
            this.freq = freq
        }
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
        mapEntityComponent = engine.createComponent(MapEntityComponent::class.java).apply {
            mapEntityType = type
            this.mapCollidable = mapCollidable
            this.projectileCollidable = projectileCollidable
        }
        return this
    }

    fun movingPlatform() : EntityBuilder {
        movingPlatformComponent = engine.createComponent(MovingPlatformComponent::class.java)
        return this
    }

    fun portal(id: Int, target: Int) : EntityBuilder {
        portalComponent = engine.createComponent(PortalComponent::class.java).apply {
            this.id = id
            this.target = target
        }
        return this
    }

    fun clamp(right: Boolean, rect: Rectangle, acceleration: Float = 0f, backVelocity: Float = 0f) : EntityBuilder {
        clampComponent = engine.createComponent(ClampComponent::class.java).apply {
            this.right = right
            this.rect = rect
            this.acceleration = acceleration
            this.backVelocity = backVelocity
        }
        return this
    }

    fun healthPack(regen: Int = 0, regenTime: Float = 0f) : EntityBuilder {
        healthPackComponent = engine.createComponent(HealthPackComponent::class.java).apply {
            this.regen = regen
            this.regenTime = regenTime
        }
        return this
    }

    fun squareSwitch(targetId: Int) : EntityBuilder {
        squareSwitchComponent = engine.createComponent(SquareSwitchComponent::class.java).apply {
            this.targetId = targetId
        }
        return this
    }

    fun toggleTile(id: Int, lx: Float = 0f, ly: Float = 0f, lw: Float = 0f, lh: Float = 0f) : EntityBuilder {
        toggleTileComponent = engine.createComponent(ToggleTileComponent::class.java).apply {
            this.id = id
            lethalRect.set(lx, ly, lw, lh)
        }
        return this
    }

    fun forceField(duration: Float = 0f) : EntityBuilder {
        forceFieldComponent = engine.createComponent(ForceFieldComponent::class.java).apply {
            this.duration = duration
        }
        return this
    }

    fun damageBoost(damageBoost: Int = 0, duration: Float = 0f) : EntityBuilder {
        damageBoostComponent = engine.createComponent(DamageBoostComponent::class.java).apply {
            this.damageBoost = damageBoost
            this.duration = duration
        }
        return this
    }

    fun mirror(orientation: String) : EntityBuilder {
        mirrorComponent = engine.createComponent(MirrorComponent::class.java).apply {
            this.orientation = Orientation.getType(orientation)!!
        }
        return this
    }

    fun invertSwitch() : EntityBuilder {
        invertSwitchComponent = engine.createComponent(InvertSwitchComponent::class.java)
        return this
    }

    fun backAndForth(dist: Float = 0f, positive: Boolean = true) : EntityBuilder {
        backAndForthComponent = engine.createComponent(BackAndForthComponent::class.java).apply {
            this.dist = dist
            this.positive = positive
        }
        return this
    }

    fun accelerationGate(boost: Float = 0f) : EntityBuilder {
        accelerationGateComponent = engine.createComponent(AccelerationGateComponent::class.java).apply {
            this.boost = boost
        }
        return this
    }

    fun build() : Entity {
        val entity = engine.createEntity()

        playerComponent?.let { entity.add(it) }
        projectileComponent?.let { entity.add(it) }

        colorComponent?.let { entity.add(it) }
        boundingBoxComponent?.let { entity.add(it) }
        boundingCircleComponent?.let { entity.add(it) }
        directionComponent?.let { entity.add(it) }
        gravityComponent?.let { entity.add(it) }
        jumpComponent?.let { entity.add(it) }
        healthComponent?.let { entity.add(it) }
        knockbackComponent?.let { entity.add(it) }
        positionComponent?.let { entity.add(it) }
        removeComponent?.let { entity.add(it) }
        textureComponent?.let { entity.add(it) }
        velocityComponent?.let { entity.add(it) }
        orbitComponent?.let { entity.add(it) }
        statusEffectComponent?.let { entity.add(it) }
        affectAllComponent?.let { entity.add(it) }

        enemyComponent?.let { entity.add(it) }
        activationComponent?.let { entity.add(it) }
        corporalComponent?.let { entity.add(it) }
        attackComponent?.let { entity.add(it) }
        explodeComponent?.let { entity.add(it) }
        teleportComponent?.let { entity.add(it) }
        lastStandComponent?.let { entity.add(it) }
        trapComponent?.let { entity.add(it) }
        blockComponent?.let { entity.add(it) }

        mapEntityComponent?.let { entity.add(it) }
        movingPlatformComponent?.let { entity.add(it) }
        portalComponent?.let { entity.add(it) }
        clampComponent?.let { entity.add(it) }
        healthPackComponent?.let { entity.add(it) }
        squareSwitchComponent?.let { entity.add(it) }
        toggleTileComponent?.let { entity.add(it) }
        forceFieldComponent?.let { entity.add(it) }
        damageBoostComponent?.let { entity.add(it) }
        mirrorComponent?.let { entity.add(it) }
        invertSwitchComponent?.let { entity.add(it) }
        backAndForthComponent?.let { entity.add(it) }
        accelerationGateComponent?.let { entity.add(it) }

        engine.addEntity(entity)
        return entity
    }

}