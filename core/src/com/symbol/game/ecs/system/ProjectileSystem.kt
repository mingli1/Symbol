package com.symbol.game.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import com.symbol.game.ecs.EntityBuilder
import com.symbol.game.ecs.Mapper
import com.symbol.game.ecs.component.*
import com.symbol.game.ecs.component.enemy.EnemyComponent
import com.symbol.game.ecs.component.map.MapEntityComponent
import com.symbol.game.ecs.component.map.ToggleTileComponent
import com.symbol.game.ecs.entity.MapEntityType
import com.symbol.game.ecs.entity.Player
import com.symbol.game.effects.particle.DEFAULT_INTESITY
import com.symbol.game.effects.particle.DEFAULT_LIFETIME
import com.symbol.game.effects.particle.ParticleSpawner
import com.symbol.game.map.MapObject
import com.symbol.game.map.camera.CameraRotation
import com.symbol.game.screen.GameScreen
import com.symbol.game.util.*
import kotlin.math.abs

const val DIAGONAL_PROJECTILE_SCALING = 0.75f
private const val KNOCKBACK_TIME = 0.1f
private const val GRAVITY_FLIP_TIME = 0.75f

class ProjectileSystem(private val player: Player,
                       private val res: Resources,
                       private val data: Data,
                       private val gameScreen: GameScreen)
    : IteratingSystem(Family.all(ProjectileComponent::class.java).get()) {

    private var mapObjects: Array<MapObject> = Array()

    private lateinit var allEntities: ImmutableArray<Entity>
    private lateinit var mapEntities: ImmutableArray<Entity>
    private lateinit var toggleTiles: ImmutableArray<Entity>
    private lateinit var enemies: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        allEntities = engine!!.getEntitiesFor(Family.all(HealthComponent::class.java).get())
        mapEntities = engine.getEntitiesFor(Family.all(MapEntityComponent::class.java).get())
        toggleTiles = engine.getEntitiesFor(Family.all(ToggleTileComponent::class.java).get())
        enemies = engine.getEntitiesFor(Family.all(EnemyComponent::class.java).get())
    }

    fun setMapData(mapObjects: Array<MapObject>) {
        this.mapObjects.clear()
        this.mapObjects.addAll(mapObjects)
    }

    override fun update(dt: Float) {
        super.update(dt)

        allEntities.forEach {
            Mapper.KNOCKBACK_MAPPER[it]?.run {
                if (knockingBack) {
                    timer += dt
                    if (timer > KNOCKBACK_TIME) {
                        Mapper.VEL_MAPPER[it].run { dx = prevVel }
                        timer = 0f
                        knockingBack = false
                    }
                }
            }
        }
    }

    override fun processEntity(entity: Entity?, dt: Float) {
        val pj = Mapper.PROJ_MAPPER[entity]
        val color = Mapper.COLOR_MAPPER[entity]
        val bb = Mapper.BOUNDING_BOX_MAPPER[entity]
        val position = Mapper.POS_MAPPER[entity]
        val velocity = Mapper.VEL_MAPPER[entity]
        val width = Mapper.TEXTURE_MAPPER[entity].texture!!.regionWidth
        val height = Mapper.TEXTURE_MAPPER[entity].texture!!.regionHeight
        val remove = Mapper.REMOVE_MAPPER[entity]
        bb.rect.setPosition(position.x + (width - bb.rect.width) / 2, position.y + (height - bb.rect.height) / 2)

        pj.lifeTime += dt

        if (pj.acceleration != 0f) {
            when (pj.movementType) {
                ProjectileMovementType.Normal -> applyAcceleration(pj, velocity, dt)
                ProjectileMovementType.Boomerang -> {
                    if (!pj.half) {
                        applyAcceleration(pj, velocity, dt)

                        val dirChangeX = (pj.acceleration < 0 && velocity.dx < 0) || (pj.acceleration > 0 && velocity.dx > 0)
                        val dirChangeY = (pj.acceleration < 0 && velocity.dy < 0) || (pj.acceleration > 0 && velocity.dy > 0)
                        pj.half = dirChangeX || dirChangeY
                    }
                    else {
                        if (velocity.dx != 0f) velocity.dx += pj.acceleration * dt
                        if (velocity.dy != 0f) velocity.dy += pj.acceleration * dt
                    }

                    val passOriginX = (pj.acceleration < 0 && bb.rect.x < position.originX) ||
                            (pj.acceleration > 0 && bb.rect.x > position.originX)
                    val passOriginY = (pj.acceleration < 0 && bb.rect.y < position.originY) ||
                            (pj.acceleration > 0 && bb.rect.y > position.originY)

                    if (passOriginX || passOriginY) {
                        remove.shouldRemove = true
                    }
                }
                else -> {}
            }
        }

        if (pj.collidesWithTerrain) {
            for (mapObject in mapObjects) {
                if (bb.rect.overlaps(mapObject.bounds)) {
                    if (pj.playerType != 0) handlePlayerProjectile(entity, pj, bb.rect)
                    removeAndSpawnParticles(color, pj, position, width, height, remove)
                    break
                }
            }
        }

        handleMapEntityCollisions(entity)

        for (projectile in entities) {
            val projectileComp = Mapper.PROJ_MAPPER[projectile]
            if (entity!! != projectile && projectileComp.collidesWithProjectiles) {
                val bounds = Mapper.BOUNDING_BOX_MAPPER[projectile]
                if (bb.rect.overlaps(bounds.rect)) {
                    if (pj.playerType != 0) handlePlayerProjectile(entity, pj, bb.rect)
                    remove.shouldRemove = true
                    val projectileRemove = Mapper.REMOVE_MAPPER[projectile]
                    projectileRemove.shouldRemove = true
                    break
                }
            }
        }

        for (e in allEntities) {
            val ebb = Mapper.BOUNDING_BOX_MAPPER[e]
            val ev = Mapper.VEL_MAPPER[e]

            if (bb.rect.overlaps(ebb.rect)) {
                val knockback = Mapper.KNOCKBACK_MAPPER[e]
                val player = Mapper.PLAYER_MAPPER[e]

                if (Mapper.AFFECT_ALL_MAPPER[entity] != null ||
                        (Mapper.PLAYER_MAPPER[entity] == null && player != null) ||
                        (Mapper.PLAYER_MAPPER[entity] != null && player == null)) {
                    val corp = Mapper.CORPOREAL_MAPPER[e]
                    if (corp != null && !corp.corporeal) break

                    val trap = Mapper.TRAP_MAPPER[e]
                    if (trap != null) handleTrapEnemy(e)

                    if (knockback != null) {
                        ev.prevVel = ev.dx
                        ev.dx = if (bb.rect.x < ebb.rect.x + ebb.rect.width / 2) pj.knockback else -pj.knockback
                        knockback.knockingBack = true
                    }
                    charge(entity, pj)
                    hit(e, pj.damage)

                    val se = Mapper.STATUS_EFFECT_MAPPER[entity]
                    val target = Mapper.STATUS_EFFECT_MAPPER[e]
                    if (se != null && target != null) target.apply(se.apply, se.duration, se.value)

                    if (pj.playerType != 0) handlePlayerProjectile(entity, pj, bb.rect)

                    remove.shouldRemove = true
                    break
                }
            }
        }

        when (pj.movementType) {
            ProjectileMovementType.Arc -> handleArcMovement(entity, dt, pj)
            ProjectileMovementType.Wave -> handleWaveMovement(entity, dt, pj)
            ProjectileMovementType.Homing -> handleHomingMovement(entity)
            else -> {}
        }

        handleDetonation(entity, pj, bb.rect, remove)
    }

    private fun removeAndSpawnParticles(color: ColorComponent, pj: ProjectileComponent,
                                        position: PositionComponent, width: Int, height: Int,
                                        remove: RemoveComponent) {
        ParticleSpawner.spawn(res, color.hex!!,
                DEFAULT_LIFETIME, DEFAULT_INTESITY + pj.damage,
                position.x + width / 2,
                position.y + height / 2)
        remove.shouldRemove = true
    }

    private fun applyAcceleration(pj: ProjectileComponent, vel: VelocityComponent, dt: Float) {
        if (vel.dx != 0f) vel.dx += if (vel.dx > 0f) pj.acceleration * dt else -pj.acceleration * dt
        if (vel.dy != 0f) vel.dy += if (vel.dy > 0f) pj.acceleration * dt else -pj.acceleration * dt
    }

    private fun hit(entity: Entity, damage: Int) {
        val ebb = Mapper.BOUNDING_BOX_MAPPER[entity]
        val health = Mapper.HEALTH_MAPPER[entity]
        var intensity = DEFAULT_INTESITY + damage

        health.hit(damage)
        if (health.hp <= 0) intensity *= 2

        val entityColor = Mapper.COLOR_MAPPER[entity]
        ParticleSpawner.spawn(res, entityColor.hex!!,
                DEFAULT_LIFETIME, intensity,
                ebb.rect.x + ebb.rect.width / 2,
                ebb.rect.y + ebb.rect.height / 2)

        handleTeleportation(entity)
        handleLastStand(entity)
    }

    private fun charge(entity: Entity?, pj: ProjectileComponent) {
        Mapper.PLAYER_MAPPER[entity]?.run {
            Mapper.CHARGE_MAPPER[player].run {
                if (pj.playerType != 1 && !pj.sub) {
                    charge += data.getPlayerData("chargeGain").asInt()
                    val maxCharge = data.getPlayerData("maxCharge").asInt()
                    if (charge > maxCharge) charge = maxCharge
                }
            }
        }
    }

    private fun handleMapEntityCollisions(entity: Entity?) {
        val pj = Mapper.PROJ_MAPPER[entity]
        val color = Mapper.COLOR_MAPPER[entity]
        val bb = Mapper.BOUNDING_BOX_MAPPER[entity]
        val position = Mapper.POS_MAPPER[entity]
        val width = Mapper.TEXTURE_MAPPER[entity].texture!!.regionWidth
        val height = Mapper.TEXTURE_MAPPER[entity].texture!!.regionHeight
        val remove = Mapper.REMOVE_MAPPER[entity]

        for (mapEntity in mapEntities) {
            val me = Mapper.MAP_ENTITY_MAPPER[mapEntity]
            val bounds = Mapper.BOUNDING_BOX_MAPPER[mapEntity]
            val boundsCircle = Mapper.BOUNDING_CIRCLE_MAPPER[mapEntity]

            val overlap = if (boundsCircle == null) bb.rect.overlaps(bounds.rect) else
                Intersector.overlaps(boundsCircle.circle, bb.rect)
            val affectAllOrFromPlayer = Mapper.AFFECT_ALL_MAPPER[entity] != null ||
                    Mapper.PLAYER_MAPPER[entity] != null

            if (!pj.sub && overlap) {
                when (me.mapEntityType) {
                    MapEntityType.Mirror -> handleMirror(entity, mapEntity, pj)
                    MapEntityType.AccelerationGate -> handleAccelerationGate(entity, mapEntity)
                    else -> {}
                }
            }

            if (affectAllOrFromPlayer && !pj.sub && overlap) {
                when (me.mapEntityType) {
                    MapEntityType.GravitySwitch -> handleGravitySwitch()
                    MapEntityType.SquareSwitch -> handleSquareSwitch(mapEntity)
                    MapEntityType.ForceField -> handleForceField(entity, mapEntity, boundsCircle)
                    MapEntityType.InvertSwitch -> handleInvertSwitch(mapEntity)
                    else -> {}
                }
            }

            if (boundsCircle != null && affectAllOrFromPlayer && !pj.sub && !overlap) {
                if (boundsCircle.circle.contains(position.originX, position.originY)) {
                    removeAndSpawnParticles(color, pj, position, width, height, remove)
                }
            }

            if (me.projectileCollidable) {
                if (overlap && affectAllOrFromPlayer) {
                    if (pj.playerType != 0) handlePlayerProjectile(entity, pj, bb.rect)
                    removeAndSpawnParticles(color, pj, position, width, height, remove)
                    break
                }
            }
        }

        val lastEntity = Mapper.LAST_ENTITY_MAPPER[entity]
        if (lastEntity?.entity != null) {
            val leBounds = Mapper.BOUNDING_BOX_MAPPER[lastEntity.entity]
            if (!bb.rect.overlaps(leBounds.rect)) {
                pj.withinMirror = false
                entity?.remove(LastEntityComponent::class.java)
            }
        }
    }

    private fun handleDetonation(entity: Entity?, pj: ProjectileComponent, bounds: Rectangle, remove: RemoveComponent) {
        if (Mapper.PLAYER_MAPPER[entity] == null && !pj.collidesWithTerrain && pj.detonateTime != 0f) {
            if (pj.lifeTime >= pj.detonateTime) {
                val vel = Mapper.VEL_MAPPER[entity]
                val speed = if (vel.dx != 0f) Math.abs(vel.dx) else Math.abs(vel.dy)
                val texture = res.getSubProjectileTextureFor(pj.textureStr!!)!!

                createSubProjectile(pj.damage, bounds, speed, 0f, texture)
                createSubProjectile(pj.damage, bounds, speed * DIAGONAL_PROJECTILE_SCALING, -speed * DIAGONAL_PROJECTILE_SCALING, texture)
                createSubProjectile(pj.damage, bounds, 0f, -speed, texture)
                createSubProjectile(pj.damage, bounds, -speed * DIAGONAL_PROJECTILE_SCALING, -speed * DIAGONAL_PROJECTILE_SCALING, texture)
                createSubProjectile(pj.damage, bounds, -speed, 0f, texture)
                createSubProjectile(pj.damage, bounds, -speed * DIAGONAL_PROJECTILE_SCALING, speed * DIAGONAL_PROJECTILE_SCALING, texture)
                createSubProjectile(pj.damage, bounds, 0f, speed, texture)
                createSubProjectile(pj.damage, bounds, speed * DIAGONAL_PROJECTILE_SCALING, speed * DIAGONAL_PROJECTILE_SCALING, texture)

                remove.shouldRemove = true
            }
        }
    }

    private fun handlePlayerProjectile(entity: Entity?, pj: ProjectileComponent, bounds: Rectangle) {
        if (pj.playerType == 4) {
            val vel = Mapper.VEL_MAPPER[entity]
            val speed = if (vel.dx != 0f) Math.abs(vel.dx) else Math.abs(vel.dy)
            val texture = res.getSubProjectileTextureFor(pj.textureStr!!)!!

            createSubProjectile(1, bounds, speed, 0f, texture, false, true, data.getColor("p_dot"))
            createSubProjectile(1, bounds, 0f, -speed, texture, false, true, data.getColor("p_dot"))
            createSubProjectile(1, bounds, -speed, 0f, texture, false, true, data.getColor("p_dot"))
            createSubProjectile(1, bounds, 0f, speed, texture, false, true, data.getColor("p_dot"))
            createSubProjectile(1, bounds, speed * DIAGONAL_PROJECTILE_SCALING,
                    -speed * DIAGONAL_PROJECTILE_SCALING, texture, false, true, data.getColor("p_dot"))
            createSubProjectile(1, bounds, -speed * DIAGONAL_PROJECTILE_SCALING,
                    -speed * DIAGONAL_PROJECTILE_SCALING, texture, false, true, data.getColor("p_dot"))
            createSubProjectile(1, bounds, -speed * DIAGONAL_PROJECTILE_SCALING,
                    speed * DIAGONAL_PROJECTILE_SCALING, texture, false, true, data.getColor("p_dot"))
            createSubProjectile(1, bounds, speed * DIAGONAL_PROJECTILE_SCALING,
                    speed * DIAGONAL_PROJECTILE_SCALING, texture, false, true, data.getColor("p_dot"))
        }
    }

    private fun handleArcMovement(entity: Entity?, dt: Float, pj: ProjectileComponent) {
        val vel = Mapper.VEL_MAPPER[entity]
        val ay = (pj.acceleration / 1.5f) * dt
        vel.dx += if (pj.parentFacingRight) pj.acceleration * dt else -pj.acceleration * dt

        if (!pj.half) {
            if (vel.dy > 0) {
                vel.dy -= ay
                if (vel.dy < 0) pj.half = true
            } else if (vel.dy < 0) {
                vel.dy += ay
                if (vel.dy > 0) pj.half = true
            }
        }
        else {
            if (vel.dy < 0) vel.dy -= ay
            else if (vel.dy > 0) vel.dy += ay
        }
    }

    private fun handleWaveMovement(entity: Entity?, dt: Float, pj: ProjectileComponent) {
        pj.waveTimer += dt * (pj.acceleration / 10f)
        if (pj.waveTimer >= MathUtils.PI2) pj.waveTimer = 0f
        val offset = MathUtils.sin(pj.waveTimer) * pj.acceleration

        val velocity = Mapper.VEL_MAPPER[entity]

        when (pj.waveDir) {
            Direction.Left, Direction.Right -> velocity.dy = offset
            Direction.Up, Direction.Down -> velocity.dx = offset
        }
    }

    private fun handleHomingMovement(entity: Entity?) {
        val velocity = Mapper.VEL_MAPPER[entity]
        val bounds = Mapper.BOUNDING_BOX_MAPPER[entity]
        val playerBounds = Mapper.BOUNDING_BOX_MAPPER[player]

        val x = bounds.rect.x + bounds.rect.width / 2
        val y = bounds.rect.y + bounds.rect.height / 2
        val px = playerBounds.rect.x + playerBounds.rect.width / 2
        val py = playerBounds.rect.y + playerBounds.rect.height / 2

        velocity.dx = if (x > px) -velocity.speed else velocity.speed
        velocity.dy = if (y > py) -velocity.speed else velocity.speed

        if (abs(x - px) <= 1f) velocity.dx = 0f
        if (abs(y - py) <= 1f) velocity.dy = 0f
    }

    private fun createSubProjectile(damage: Int, bounds: Rectangle,
                                    dx: Float = 0f, dy: Float = 0f, texture: TextureRegion,
                                    enemy: Boolean = true,
                                    collidesWithTerrain: Boolean = false,
                                    hex: String? = null) {
        val bw = texture.regionWidth - 1
        val bh = texture.regionHeight - 1
        val builder = EntityBuilder.instance(engine as PooledEngine)
                .projectile(sub = true, collidesWithTerrain = collidesWithTerrain, damage = damage)
                .position(bounds.x + (bounds.width / 2) - (bw / 2), bounds.y + (bounds.height / 2) - (bh / 2))
                .velocity(dx = dx, dy = dy)
                .boundingBox(bw.toFloat(), bh.toFloat())
                .texture(texture)
                .direction(yFlip = true).remove()
        if (!enemy) builder.player()
        if (hex != null) builder.color(hex)
        builder.build()
    }

    private fun handleTeleportation(entity: Entity?) {
        Mapper.TELEPORT_MAPPER[entity]?.run {
            if (range == 0f) {
                val bounds = Mapper.BOUNDING_BOX_MAPPER[entity]
                val position = Mapper.POS_MAPPER[entity]
                val velocity = Mapper.VEL_MAPPER[entity]

                val platform = mapObjects.random().bounds
                val randX = MathUtils.random(platform.x, platform.x + platform.width - bounds.rect.width)
                val newY = platform.y + platform.height + bounds.rect.height / 2

                position.set(randX, newY)
                velocity.dx = 0f
            }
        }
    }

    private fun handleLastStand(entity: Entity?) {
        Mapper.ATTACK_MAPPER[entity]?.let { attackComp ->
            Mapper.LAST_STAND_MAPPER[entity]?.let {
                val health = Mapper.HEALTH_MAPPER[entity]
                val scale = 1f / health.maxHp
                attackComp.attackRate -= attackComp.attackRate * scale

                val se = Mapper.STATUS_EFFECT_MAPPER[entity]
                if (se != null) {
                    if (se.type != StatusEffect.LastStand && health.hp.toFloat() / health.maxHp.toFloat() <= 0.5f) {
                        se.apply(StatusEffect.LastStand)
                        se.entityApplied = true
                    }
                }
            }
        }
    }

    private fun handleTrapEnemy(entity: Entity?) {
        Mapper.TRAP_MAPPER[entity].run {
            if (!countdown) countdown = true
            hits++
            if (hits <= 3) Mapper.TEXTURE_MAPPER[entity].run { texture = res.getTexture(textureStr + hits) }
        }
    }

    private fun handleMirror(entity: Entity?, mapEntity: Entity?, pj: ProjectileComponent) {
        val mirror = Mapper.MIRROR_MAPPER[mapEntity]
        val pBounds = Mapper.BOUNDING_BOX_MAPPER[entity]
        val mBounds = Mapper.BOUNDING_BOX_MAPPER[mapEntity]
        val velocity = Mapper.VEL_MAPPER[entity]
        entity?.add((engine as PooledEngine).createComponent(AffectAllComponent::class.java))

        val pRight = velocity.dx > 0 && velocity.dy == 0f
        val pLeft = velocity.dx < 0 && velocity.dy == 0f
        val pUp = velocity.dy > 0 && velocity.dx == 0f
        val pDown = velocity.dy < 0 && velocity.dx == 0f
        val pUpLeft = velocity.dx < 0 && velocity.dy > 0
        val pUpRight = velocity.dx > 0 && velocity.dy > 0
        val pDownLeft = velocity.dx < 0 && velocity.dy < 0
        val pDownRight = velocity.dx > 0 && velocity.dy < 0

        val px = pBounds.rect.x + pBounds.rect.width / 2
        val py = pBounds.rect.y + pBounds.rect.height / 2

        if (!pj.withinMirror) {
            when (mirror.orientation) {
                Orientation.Vertical -> {
                    val xHalf = mBounds.rect.x + mBounds.rect.width / 2
                    if ((pRight && px >= xHalf) || (pLeft && px <= xHalf) ||
                            (pDownRight && px >= xHalf) || (pDownLeft && px <= xHalf) ||
                            (pUpRight && px >= xHalf) || (pUpLeft && px <= xHalf)) {
                        velocity.dx = -velocity.dx
                        applyMirror(entity, mapEntity, pj)
                    }
                }
                Orientation.Horizontal -> {
                    val yHalf = mBounds.rect.y + mBounds.rect.height / 2
                    if ((pUp && py >= yHalf) || (pDown && py <= yHalf) ||
                            (pDownRight && py <= yHalf) || (pDownLeft && py <= yHalf) ||
                            (pUpRight && py >= yHalf) || (pUpLeft && py <= yHalf)) {
                        velocity.dy = -velocity.dy
                        applyMirror(entity, mapEntity, pj)
                    }
                }
                Orientation.LeftDiagonal -> {
                    if ((pRight && px >= mBounds.rect.x + mBounds.rect.width - py + mBounds.rect.y) ||
                            (pLeft && px <= mBounds.rect.x + mBounds.rect.width - py + mBounds.rect.y)) {
                        velocity.dy = -velocity.dx
                        velocity.dx = 0f
                        applyMirror(entity, mapEntity, pj)
                    }
                    else if ((pDown && py <= mBounds.rect.y + mBounds.rect.height - px + mBounds.rect.x) ||
                            (pUp && py >= mBounds.rect.y + mBounds.rect.height - px + mBounds.rect.x)) {
                        velocity.dx = -velocity.dy
                        velocity.dy = 0f
                        applyMirror(entity, mapEntity, pj)
                    }
                    else if ((pDownLeft && Intersector.isPointInTriangle(px, py,
                                    mBounds.rect.x, mBounds.rect.y,
                                    mBounds.rect.x + mBounds.rect.width, mBounds.rect.y,
                                    mBounds.rect.x, mBounds.rect.y + mBounds.rect.height)) ||
                            (pUpRight && Intersector.isPointInTriangle(px, py,
                                    mBounds.rect.x + mBounds.rect.width, mBounds.rect.y + mBounds.rect.height,
                                    mBounds.rect.x + mBounds.rect.width, mBounds.rect.y,
                                    mBounds.rect.x, mBounds.rect.y + mBounds.rect.height))) {
                        velocity.dx = -velocity.dx
                        velocity.dy = -velocity.dy
                        applyMirror(entity, mapEntity, pj)
                    }
                }
                Orientation.RightDiagonal -> {
                    if ((pRight && px >= mBounds.rect.x + py - mBounds.rect.y) ||
                            (pLeft && px <= mBounds.rect.x + py - mBounds.rect.y)) {
                        velocity.dy = velocity.dx
                        velocity.dx = 0f
                        applyMirror(entity, mapEntity, pj)
                    }
                    else if ((pDown && py <= mBounds.rect.y + px - mBounds.rect.x) ||
                            (pUp && py >= mBounds.rect.y + px - mBounds.rect.x)) {
                        velocity.dx = velocity.dy
                        velocity.dy = 0f
                        applyMirror(entity, mapEntity, pj)
                    }
                    else if ((pDownRight && Intersector.isPointInTriangle(px, py,
                                    mBounds.rect.x, mBounds.rect.y,
                                    mBounds.rect.x + mBounds.rect.width, mBounds.rect.y,
                                    mBounds.rect.x + mBounds.rect.width, mBounds.rect.y + mBounds.rect.height)) ||
                            (pUpLeft && Intersector.isPointInTriangle(px, py,
                                    mBounds.rect.x, mBounds.rect.y,
                                    mBounds.rect.x, mBounds.rect.y + mBounds.rect.height,
                                    mBounds.rect.x + mBounds.rect.width, mBounds.rect.y + mBounds.rect.height))) {
                        velocity.dx = -velocity.dx
                        velocity.dy = -velocity.dy
                        applyMirror(entity, mapEntity, pj)
                    }
                }
            }
        }
    }

    private fun applyMirror(entity: Entity?, mapEntity: Entity?, pj: ProjectileComponent) {
        pj.withinMirror = true
        val lastEntity = Mapper.LAST_ENTITY_MAPPER[entity]
        if (lastEntity == null) {
            entity?.add((engine as PooledEngine).createComponent(LastEntityComponent::class.java))
            Mapper.LAST_ENTITY_MAPPER[entity].entity = mapEntity
        } else {
            lastEntity.entity = mapEntity
        }
    }

    private fun handleGravitySwitch() {
        if (CameraRotation.isEnded()) {
            val gravity = Mapper.GRAVITY_MAPPER[player]

            gravity.reverse = !gravity.reverse
            CameraRotation.start(180f, GRAVITY_FLIP_TIME)

            mapEntities.forEach {
                val gme = Mapper.MAP_ENTITY_MAPPER[it]
                if (gme.mapEntityType == MapEntityType.GravitySwitch) {
                    val meTexture = Mapper.TEXTURE_MAPPER[it]
                    meTexture.texture = res.getTexture(meTexture.textureStr +
                            if (gravity.reverse) TOGGLE_ON else TOGGLE_OFF)
                }
            }
        }
    }

    private fun handleSquareSwitch(mapEntity: Entity?) {
        val switch = Mapper.SQUARE_SWITCH_MAPPER[mapEntity]
        val switchTexture = Mapper.TEXTURE_MAPPER[mapEntity]

        switch.toggle = !switch.toggle
        switchTexture.texture = res.getTexture(switchTexture.textureStr +
                if (switch.toggle) TOGGLE_ON else TOGGLE_OFF)

        toggleTiles.forEach {
            val tme = Mapper.MAP_ENTITY_MAPPER[it]
            val toggleComp = Mapper.TOGGLE_TILE_MAPPER[it]
            val toggleTexture = Mapper.TEXTURE_MAPPER[it]

            if (switch.targetId == toggleComp.id) {
                toggleComp.toggle = !toggleComp.toggle
                toggleTexture.texture = if (toggleComp.toggle) res.getTexture(toggleTexture.textureStr!!) else null
                tme.mapCollidable = toggleComp.toggle
                tme.projectileCollidable = toggleComp.toggle
            }
        }
    }

    private fun handleForceField(entity: Entity?, mapEntity: Entity?, boundsCircle: BoundingCircleComponent) {
        val color = Mapper.COLOR_MAPPER[entity]
        val pj = Mapper.PROJ_MAPPER[entity]
        val position = Mapper.POS_MAPPER[entity]
        val remove = Mapper.REMOVE_MAPPER[entity]
        val width = Mapper.TEXTURE_MAPPER[entity].texture!!.regionWidth
        val height = Mapper.TEXTURE_MAPPER[entity].texture!!.regionHeight
        val ff = Mapper.FORCE_FIELD_MAPPER[mapEntity]
        val playerBounds = Mapper.BOUNDING_BOX_MAPPER[player]

        val circleContainsPlayer = boundsCircle.circle.contains(playerBounds.rect.x, playerBounds.rect.y) &&
                boundsCircle.circle.contains(playerBounds.rect.x + playerBounds.rect.width,
                        playerBounds.rect.y) &&
                boundsCircle.circle.contains(playerBounds.rect.x,
                        playerBounds.rect.y + playerBounds.rect.height) &&
                boundsCircle.circle.contains(playerBounds.rect.x + playerBounds.rect.width,
                        playerBounds.rect.y + playerBounds.rect.height)

        if (!circleContainsPlayer && ff.activated)
            removeAndSpawnParticles(color, pj, position, width, height, remove)
    }

    private fun handleInvertSwitch(entity: Entity?) {
        Mapper.INVERT_SWITCH_MAPPER[entity].run {
            toggle = !toggle
            gameScreen.mapInverted = toggle
        }
        enemies.forEach {
            Mapper.ENEMY_MAPPER[it].run { visible = !visible }
        }
    }

    private fun handleAccelerationGate(entity: Entity?, mapEntity: Entity?) {
        val velocity = Mapper.VEL_MAPPER[entity]
        val agate = Mapper.ACCEL_GATE_MAPPER[mapEntity]
        if (velocity.dx > 0 && velocity.dx != velocity.speed + agate.boost) {
            velocity.dx = velocity.speed + agate.boost
        }
        else if (velocity.dx < 0 && velocity.dx != -velocity.speed - agate.boost) {
            velocity.dx = -velocity.speed - agate.boost
        }
        else if (velocity.dy > 0 && velocity.dy != velocity.speed + agate.boost) {
            velocity.dy = velocity.speed + agate.boost
        }
        else if (velocity.dy < 0 && velocity.dy != -velocity.speed - agate.boost) {
            velocity.dy = -velocity.speed - agate.boost
        }
    }

}