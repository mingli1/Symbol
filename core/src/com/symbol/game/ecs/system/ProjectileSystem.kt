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
import com.symbol.game.ecs.component.map.MapEntityComponent
import com.symbol.game.ecs.component.map.ToggleTileComponent
import com.symbol.game.ecs.entity.EntityColor
import com.symbol.game.ecs.entity.MapEntityType
import com.symbol.game.ecs.entity.Player
import com.symbol.game.effects.particle.DEFAULT_INTESITY
import com.symbol.game.effects.particle.DEFAULT_LIFETIME
import com.symbol.game.effects.particle.ParticleSpawner
import com.symbol.game.map.MapObject
import com.symbol.game.util.Resources
import com.symbol.game.util.TOGGLE_OFF
import com.symbol.game.util.TOGGLE_ON

const val DIAGONAL_PROJECTILE_SCALING = 0.75f
private const val KNOCKBACK_TIME = 0.1f

class ProjectileSystem(private val player: Player, private val res: Resources)
    : IteratingSystem(Family.all(ProjectileComponent::class.java).get()) {

    private var mapObjects: Array<MapObject> = Array()

    private lateinit var allEntities: ImmutableArray<Entity>
    private lateinit var mapEntities: ImmutableArray<Entity>
    private lateinit var toggleTiles: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        allEntities = engine!!.getEntitiesFor(Family.all(HealthComponent::class.java).get())
        mapEntities = engine.getEntitiesFor(Family.all(MapEntityComponent::class.java).get())
        toggleTiles = engine.getEntitiesFor(Family.all(ToggleTileComponent::class.java).get())
    }

    fun setMapData(mapObjects: Array<MapObject>) {
        this.mapObjects.clear()
        this.mapObjects.addAll(mapObjects)
    }

    override fun update(dt: Float) {
        super.update(dt)

        for (e in allEntities) {
            val vel = Mapper.VEL_MAPPER.get(e)
            val knockback = Mapper.KNOCKBACK_MAPPER.get(e)
            if (knockback != null && knockback.knockingBack) {
                knockback.timer += dt
                if (knockback.timer > KNOCKBACK_TIME) {
                    vel.dx = vel.prevVel
                    knockback.timer = 0f
                    knockback.knockingBack = false
                }
            }
        }
    }

    override fun processEntity(entity: Entity?, dt: Float) {
        val pj = Mapper.PROJ_MAPPER.get(entity)
        val color = Mapper.COLOR_MAPPER.get(entity)
        val bb = Mapper.BOUNDING_BOX_MAPPER.get(entity)
        val position = Mapper.POS_MAPPER.get(entity)
        val velocity = Mapper.VEL_MAPPER.get(entity)
        val width = Mapper.TEXTURE_MAPPER.get(entity).texture!!.regionWidth
        val height = Mapper.TEXTURE_MAPPER.get(entity).texture!!.regionHeight
        val remove = Mapper.REMOVE_MAPPER.get(entity)
        bb.rect.setPosition(position.x + (width - bb.rect.width) / 2, position.y + (height - bb.rect.height) / 2)

        pj.lifeTime += dt

        if (pj.acceleration != 0f && pj.movementType == ProjectileMovementType.Normal) {
            if (velocity.dx != 0f) velocity.dx += if (velocity.dx > 0f) pj.acceleration * dt else -pj.acceleration * dt
            if (velocity.dy != 0f) velocity.dy += if (velocity.dy > 0f) pj.acceleration * dt else -pj.acceleration * dt
        }

        if (pj.collidesWithTerrain) {
            for (mapObject in mapObjects) {
                if (bb.rect.overlaps(mapObject.bounds)) {
                    if (pj.playerType != 0) handlePlayerProjectile(entity, pj, bb.rect)
                    removeAndSpawnParticles(color, pj, position, width, height, remove)
                    break
                }
            }
            handleMapEntityCollisions(entity)
        }

        for (projectile in entities) {
            val projectileComp = Mapper.PROJ_MAPPER.get(projectile)
            if (entity!! != projectile && projectileComp.collidesWithProjectiles) {
                val bounds = Mapper.BOUNDING_BOX_MAPPER.get(projectile)
                if (bb.rect.overlaps(bounds.rect)) {
                    if (pj.playerType != 0) handlePlayerProjectile(entity, pj, bb.rect)
                    remove.shouldRemove = true
                    val projectileRemove = Mapper.REMOVE_MAPPER.get(projectile)
                    projectileRemove.shouldRemove = true
                    break
                }
            }
        }

        for (e in allEntities) {
            val ebb = Mapper.BOUNDING_BOX_MAPPER.get(e)
            val ev = Mapper.VEL_MAPPER.get(e)

            if (bb.rect.overlaps(ebb.rect)) {
                val knockback = Mapper.KNOCKBACK_MAPPER.get(e)
                val player = Mapper.PLAYER_MAPPER.get(e)

                if ((pj.enemy && player != null) || (!pj.enemy && player == null)) {
                    val corp = Mapper.CORPOREAL_MAPPER.get(e)
                    if (corp != null && !corp.corporeal) break

                    val trap = Mapper.TRAP_MAPPER.get(e)
                    if (trap != null) handleTrapEnemy(e)

                    if (knockback != null) {
                        ev.prevVel = ev.dx
                        ev.dx = if (bb.rect.x < ebb.rect.x + ebb.rect.width / 2) pj.knockback else -pj.knockback
                        knockback.knockingBack = true
                    }
                    hit(e, pj.damage)

                    val se = Mapper.STATUS_EFFECT_MAPPER.get(entity)
                    val target = Mapper.STATUS_EFFECT_MAPPER.get(e)
                    if (se != null && target != null) target.apply(se.apply, se.duration, se.value)

                    if (pj.playerType != 0) handlePlayerProjectile(entity, pj, bb.rect)

                    val entityColor = Mapper.COLOR_MAPPER.get(e)
                    ParticleSpawner.spawn(res, entityColor.hex!!,
                            DEFAULT_LIFETIME, DEFAULT_INTESITY + pj.damage,
                            ebb.rect.x + ebb.rect.width / 2,
                            ebb.rect.y + ebb.rect.height / 2)

                    remove.shouldRemove = true
                    break
                }
            }
        }

        when (pj.movementType) {
            ProjectileMovementType.Normal -> {}
            ProjectileMovementType.Arc -> handleArcMovement(entity, dt, pj)
            ProjectileMovementType.Wave -> handleWaveMovement(entity, dt, pj)
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

    private fun hit(entity: Entity, damage: Int) {
        val health = Mapper.HEALTH_MAPPER.get(entity)
        health.hit(damage)

        handleTeleportation(entity)
        handleLastStand(entity)
    }

    private fun handleMapEntityCollisions(entity: Entity?) {
        val pj = Mapper.PROJ_MAPPER.get(entity)
        val color = Mapper.COLOR_MAPPER.get(entity)
        val bb = Mapper.BOUNDING_BOX_MAPPER.get(entity)
        val position = Mapper.POS_MAPPER.get(entity)
        val velocity = Mapper.VEL_MAPPER.get(entity)
        val width = Mapper.TEXTURE_MAPPER.get(entity).texture!!.regionWidth
        val height = Mapper.TEXTURE_MAPPER.get(entity).texture!!.regionHeight
        val remove = Mapper.REMOVE_MAPPER.get(entity)

        for (mapEntity in mapEntities) {
            val me = Mapper.MAP_ENTITY_MAPPER.get(mapEntity)
            val bounds = Mapper.BOUNDING_BOX_MAPPER.get(mapEntity)
            val boundsCircle = Mapper.BOUNDING_CIRCLE_MAPPER.get(mapEntity)

            val overlap = if (boundsCircle == null) bb.rect.overlaps(bounds.rect) else
                Intersector.overlaps(boundsCircle.circle, bb.rect)

            if (!pj.enemy && !pj.sub && overlap) {
                when (me.mapEntityType) {
                    MapEntityType.Mirror -> {
                        pj.enemy = true
                        velocity.dx = -velocity.dx
                    }
                    MapEntityType.GravitySwitch -> {
                        val gravity = Mapper.GRAVITY_MAPPER.get(player)

                        gravity.reverse = !gravity.reverse
                        for (gravitySwitch in mapEntities) {
                            val gme = Mapper.MAP_ENTITY_MAPPER.get(gravitySwitch)
                            if (gme.mapEntityType == MapEntityType.GravitySwitch) {
                                val meTexture = Mapper.TEXTURE_MAPPER.get(gravitySwitch)
                                meTexture.texture = res.getTexture(meTexture.textureStr +
                                        if (gravity.reverse) TOGGLE_ON else TOGGLE_OFF)
                            }
                        }
                    }
                    MapEntityType.SquareSwitch -> {
                        val switch = Mapper.SQUARE_SWITCH_MAPPER.get(mapEntity)
                        val switchTexture = Mapper.TEXTURE_MAPPER.get(mapEntity)

                        switch.toggle = !switch.toggle
                        switchTexture.texture = res.getTexture(switchTexture.textureStr +
                                if (switch.toggle) TOGGLE_ON else TOGGLE_OFF)

                        for (tt in toggleTiles) {
                            val tme = Mapper.MAP_ENTITY_MAPPER.get(tt)
                            val toggleComp = Mapper.TOGGLE_TILE_MAPPER.get(tt)
                            val toggleTexture = Mapper.TEXTURE_MAPPER.get(tt)

                            if (switch.targetId == toggleComp.id) {
                                toggleComp.toggle = !toggleComp.toggle
                                toggleTexture.texture = if (toggleComp.toggle) res.getTexture(toggleTexture.textureStr!!) else null
                                tme.mapCollidable = toggleComp.toggle
                                tme.projectileCollidable = toggleComp.toggle
                            }
                        }
                    }
                    MapEntityType.ForceField -> {
                        val ff = Mapper.FORCE_FIELD_MAPPER.get(mapEntity)
                        val playerBounds = Mapper.BOUNDING_BOX_MAPPER.get(player)
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
                    else -> {}
                }
            }

            if (boundsCircle != null && !pj.enemy && !pj.sub && !overlap) {
                if (boundsCircle.circle.contains(pj.originX, pj.originY)) {
                    removeAndSpawnParticles(color, pj, position, width, height, remove)
                }
            }

            if (me.projectileCollidable) {
                if (overlap) {
                    if (pj.playerType != 0) handlePlayerProjectile(entity, pj, bb.rect)
                    removeAndSpawnParticles(color, pj, position, width, height, remove)
                    break
                }
            }
        }
    }

    private fun handleDetonation(entity: Entity?, pj: ProjectileComponent, bounds: Rectangle, remove: RemoveComponent) {
        if (pj.enemy && !pj.collidesWithTerrain && pj.detonateTime != 0f) {
            if (pj.lifeTime >= pj.detonateTime) {
                val vel = Mapper.VEL_MAPPER.get(entity)
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
            val vel = Mapper.VEL_MAPPER.get(entity)
            val speed = if (vel.dx != 0f) Math.abs(vel.dx) else Math.abs(vel.dy)
            val texture = res.getSubProjectileTextureFor(pj.textureStr!!)!!

            createSubProjectile(1, bounds, speed, 0f, texture, false, true, EntityColor.DOT_COLOR)
            createSubProjectile(1, bounds, 0f, -speed, texture, false, true, EntityColor.DOT_COLOR)
            createSubProjectile(1, bounds, -speed, 0f, texture, false, true, EntityColor.DOT_COLOR)
            createSubProjectile(1, bounds, 0f, speed, texture, false, true, EntityColor.DOT_COLOR)
            createSubProjectile(1, bounds, speed * DIAGONAL_PROJECTILE_SCALING,
                    -speed * DIAGONAL_PROJECTILE_SCALING, texture, false, true, EntityColor.DOT_COLOR)
            createSubProjectile(1, bounds, -speed * DIAGONAL_PROJECTILE_SCALING,
                    -speed * DIAGONAL_PROJECTILE_SCALING, texture, false, true, EntityColor.DOT_COLOR)
            createSubProjectile(1, bounds, -speed * DIAGONAL_PROJECTILE_SCALING,
                    speed * DIAGONAL_PROJECTILE_SCALING, texture, false, true, EntityColor.DOT_COLOR)
            createSubProjectile(1, bounds, speed * DIAGONAL_PROJECTILE_SCALING,
                    speed * DIAGONAL_PROJECTILE_SCALING, texture, false, true, EntityColor.DOT_COLOR)
        }
    }

    private fun handleArcMovement(entity: Entity?, dt: Float, pj: ProjectileComponent) {
        val vel = Mapper.VEL_MAPPER.get(entity)
        val ay = (pj.acceleration / 1.5f) * dt
        vel.dx += if (pj.parentFacingRight) pj.acceleration * dt else -pj.acceleration * dt

        if (!pj.arcHalf) {
            if (vel.dy > 0) {
                vel.dy -= ay
                if (vel.dy < 0) pj.arcHalf = true
            } else if (vel.dy < 0) {
                vel.dy += ay
                if (vel.dy > 0) pj.arcHalf = true
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

        val velocity = Mapper.VEL_MAPPER.get(entity)

        when (pj.waveDir) {
            Direction.Left, Direction.Right -> velocity.dy = offset
            Direction.Up, Direction.Down -> velocity.dx = offset
        }
    }

    private fun createSubProjectile(damage: Int, bounds: Rectangle,
                                    dx: Float = 0f, dy: Float = 0f, texture: TextureRegion,
                                    enemy: Boolean = true,
                                    collidesWithTerrain: Boolean = false,
                                    hex: String? = null) {
        val bw = texture.regionWidth - 1
        val bh = texture.regionHeight - 1
        val builder = EntityBuilder.instance(engine as PooledEngine)
                .projectile(sub = true, collidesWithTerrain = collidesWithTerrain, enemy = enemy, damage = damage)
                .position(bounds.x + (bounds.width / 2) - (bw / 2), bounds.y + (bounds.height / 2) - (bh / 2))
                .velocity(dx = dx, dy = dy)
                .boundingBox(bw.toFloat(), bh.toFloat())
                .texture(texture)
                .direction(yFlip = true).remove()
        if (hex != null) builder.color(hex)
        builder.build()
    }

    private fun handleTeleportation(entity: Entity?) {
        if (Mapper.TELEPORT_MAPPER.get(entity) != null) {
            val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity)
            val position = Mapper.POS_MAPPER.get(entity)
            val velocity = Mapper.VEL_MAPPER.get(entity)

            val platform = mapObjects.random().bounds
            val randX = MathUtils.random(platform.x, platform.x + platform.width - bounds.rect.width)
            val newY = platform.y + platform.height + bounds.rect.height / 2

            position.set(randX, newY)
            velocity.dx = 0f
        }
    }

    private fun handleLastStand(entity: Entity?) {
        val attackComp = Mapper.ATTACK_MAPPER.get(entity)
        if (attackComp != null) {
            if (Mapper.LAST_STAND_MAPPER.get(entity) != null) {
                val health = Mapper.HEALTH_MAPPER.get(entity)
                val scale = 1f / health.maxHp
                attackComp.attackRate -= attackComp.attackRate * scale

                /* @TODO Add last stand icon
                val se = Mapper.STATUS_EFFECT_MAPPER.get(entity)
                se?.apply(StatusEffect.LastStand)
                */
            }
        }
    }

    private fun handleTrapEnemy(entity: Entity?) {
        val trapComp = Mapper.TRAP_MAPPER.get(entity)
        val texture = Mapper.TEXTURE_MAPPER.get(entity)

        if (!trapComp.countdown) trapComp.countdown = true
        trapComp.hits++
        if (trapComp.hits <= 3) texture.texture = res.getTexture(texture.textureStr + trapComp.hits)
    }

}