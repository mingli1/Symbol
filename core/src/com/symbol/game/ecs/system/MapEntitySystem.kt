package com.symbol.game.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.symbol.game.ecs.Mapper
import com.symbol.game.ecs.component.StatusEffect
import com.symbol.game.ecs.component.map.MapEntityComponent
import com.symbol.game.ecs.component.map.PortalComponent
import com.symbol.game.ecs.entity.MapEntityType
import com.symbol.game.ecs.entity.Player
import com.symbol.game.effects.particle.DEFAULT_INTESITY
import com.symbol.game.effects.particle.DEFAULT_LIFETIME
import com.symbol.game.effects.particle.ParticleSpawner
import com.symbol.game.util.Resources

class MapEntitySystem(private val player: Player, private val res: Resources) :
        IteratingSystem(Family.all(MapEntityComponent::class.java).get()) {

    private lateinit var portals: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        portals = engine!!.getEntitiesFor(Family.all(PortalComponent::class.java).get())
    }

    override fun processEntity(entity: Entity?, dt: Float) {
        val mapEntityComponent = Mapper.MAP_ENTITY_MAPPER.get(entity)
        when (mapEntityComponent.mapEntityType) {
            MapEntityType.MovingPlatform -> handleMovingPlatform(entity)
            MapEntityType.TemporaryPlatform -> handleTempPlatform(entity)
            MapEntityType.Portal -> handlePortal(entity)
            MapEntityType.Clamp -> handleClamp(entity, dt)
            MapEntityType.HealthPack -> handleHealthPack(entity)
            MapEntityType.ForceField -> handleForceField(entity, dt)
            MapEntityType.DamageBoost -> handleDamageBoost(entity)
            else -> {}
        }
    }

    private fun handleMovingPlatform(entity: Entity?) {
        val mp = Mapper.MOVING_PLATFORM_MAPPER.get(entity)
        val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity)
        val position = Mapper.POS_MAPPER.get(entity)
        val velocity = Mapper.VEL_MAPPER.get(entity)

        if (velocity.dx != 0f) {
            if (mp.positive) {
                val trueX = position.x + bounds.rect.width
                if ((velocity.dx > 0 && trueX - mp.originX >= mp.distance) ||
                        (velocity.dx < 0 && position.x <= mp.originX)) {
                    velocity.dx = -velocity.dx
                }
            }
            else {
                if ((velocity.dx < 0 && mp.originX - position.x >= mp.distance) ||
                        (velocity.dx > 0 && position.x >= mp.originX)) {
                    velocity.dx = -velocity.dx
                }
            }
        }
    }

    private fun handleTempPlatform(entity: Entity?) {
        val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity)
        val remove = Mapper.REMOVE_MAPPER.get(entity)
        val playerBounds = Mapper.BOUNDING_BOX_MAPPER.get(player)
        val playerVel = Mapper.VEL_MAPPER.get(player)
        val playerComp = Mapper.PLAYER_MAPPER.get(player)

        if (playerBounds.rect.overlaps(bounds.rect)) {
            remove.shouldRemove = true
            playerComp.canDoubleJump = true
            playerVel.dy = 0f
        }
    }

    private fun handlePortal(entity: Entity?) {
        val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity)
        val playerBounds = Mapper.BOUNDING_BOX_MAPPER.get(player)
        val portalSource = Mapper.PORTAL_MAPPER.get(entity)
        val width = Mapper.TEXTURE_MAPPER.get(player).texture!!.regionWidth
        val height = Mapper.TEXTURE_MAPPER.get(player).texture!!.regionHeight

        if (portalSource.teleported && !playerBounds.rect.overlaps(bounds.rect)) {
            portalSource.teleported = false
        }

        if (playerBounds.rect.overlaps(bounds.rect)) {
            if (!portalSource.teleported) {
                for (portal in portals) {
                    val portalTarget = Mapper.PORTAL_MAPPER.get(portal)
                    if (portalTarget.id == portalSource.target) {
                        val targetPos = Mapper.BOUNDING_BOX_MAPPER.get(portal)
                        val playerPos = Mapper.POS_MAPPER.get(player)

                        playerPos.set(targetPos.rect.x, targetPos.rect.y)
                        playerBounds.rect.setPosition(playerPos.x + (width - playerBounds.rect.width) / 2,
                                playerPos.y + (height - playerBounds.rect.height) / 2)
                        portalTarget.teleported = true
                        break
                    }
                }
            }
        }
    }

    private fun handleClamp(entity: Entity?, dt: Float) {
        val clamp = Mapper.CLAMP_MAPPER.get(entity)
        val pos = Mapper.POS_MAPPER.get(entity)
        val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity)
        val vel = Mapper.VEL_MAPPER.get(entity)
        val playerBounds = Mapper.BOUNDING_BOX_MAPPER.get(player)

        if (!clamp.right) {
            if (clamp.clamping) {
                if (pos.x < clamp.rect.x + (clamp.rect.width / 2) - bounds.rect.width) {
                    vel.dx += clamp.acceleration * dt
                } else {
                    vel.dx = -clamp.backVelocity
                    clamp.clamping = false
                }
            } else if (pos.x <= clamp.rect.x) clamp.clamping = true
        }
        else {
            if (clamp.clamping) {
                if (pos.x > clamp.rect.x + clamp.rect.width / 2) {
                    vel.dx -= clamp.acceleration * dt
                } else {
                    vel.dx = clamp.backVelocity
                    clamp.clamping = false
                }
            } else if (pos.x >= clamp.rect.x + clamp.rect.width - bounds.rect.width) clamp.clamping = true
        }

        if (playerBounds.rect.overlaps(bounds.rect)) {
            val playerHealth = Mapper.HEALTH_MAPPER.get(player)
            playerHealth.hp = 0

            val color = Mapper.COLOR_MAPPER.get(player)
            ParticleSpawner.spawn(res, color.hex!!, DEFAULT_LIFETIME,
                    (DEFAULT_INTESITY + playerHealth.maxHp) * 2,
                    playerBounds.rect.x + playerBounds.rect.width / 2,
                    playerBounds.rect.y + playerBounds.rect.height / 2)
        }
    }

    private fun handleHealthPack(entity: Entity?) {
        val healthPack = Mapper.HEALTH_PACK_MAPPER.get(entity)
        val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity)
        val playerBounds = Mapper.BOUNDING_BOX_MAPPER.get(player)
        val remove = Mapper.REMOVE_MAPPER.get(entity)

        if (playerBounds.rect.overlaps(bounds.rect)) {
            val playerComp = Mapper.PLAYER_MAPPER.get(player)
            val se = Mapper.STATUS_EFFECT_MAPPER.get(player)

            playerComp.startHealing = true
            playerComp.healing = healthPack.regen
            playerComp.healTime = healthPack.regenTime
            se.apply(StatusEffect.Healing, healthPack.regenTime)

            remove.shouldRemove = true
        }
    }

    private fun handleForceField(entity: Entity?, dt: Float) {
        val ff = Mapper.FORCE_FIELD_MAPPER.get(entity)
        val texture = Mapper.TEXTURE_MAPPER.get(entity)

        if (ff.duration != 0f) {
            ff.timer += dt
            if (ff.timer >= ff.duration) {
                ff.activated = !ff.activated
                texture.texture = if (ff.activated) res.getTexture(texture.textureStr!!) else null
                ff.timer = 0f
            }
        }
    }

    private fun handleDamageBoost(entity: Entity?) {
        val boost = Mapper.DAMAGE_BOOST_MAPPER.get(entity)
        val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity)
        val playerBounds = Mapper.BOUNDING_BOX_MAPPER.get(player)
        val remove = Mapper.REMOVE_MAPPER.get(entity)

        if (playerBounds.rect.overlaps(bounds.rect)) {
            val playerComp = Mapper.PLAYER_MAPPER.get(player)
            val se = Mapper.STATUS_EFFECT_MAPPER.get(player)

            playerComp.damageBoost = boost.damageBoost
            se.apply(StatusEffect.DamageBoost, boost.duration)

            remove.shouldRemove = true
        }
    }

}