package com.symbol.game.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.symbol.game.ecs.Mapper
import com.symbol.game.ecs.component.LastEntityComponent
import com.symbol.game.ecs.component.player.PlayerComponent
import com.symbol.game.ecs.component.ProjectileComponent
import com.symbol.game.ecs.component.StatusEffect
import com.symbol.game.ecs.component.enemy.EnemyComponent
import com.symbol.game.ecs.component.map.BackAndForthComponent
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
    private lateinit var portalAffectedEntities: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        portals = engine!!.getEntitiesFor(Family.all(PortalComponent::class.java).get())
        portalAffectedEntities = engine.getEntitiesFor(Family.one(PlayerComponent::class.java,
                EnemyComponent::class.java, ProjectileComponent::class.java).get())
    }

    override fun processEntity(entity: Entity?, dt: Float) {
        val mapEntityComponent = Mapper.MAP_ENTITY_MAPPER[entity]
        val backAndForthComponent = Mapper.BACK_AND_FORTH_MAPPER[entity]

        if (backAndForthComponent != null) handleMovement(entity, backAndForthComponent)

        when (mapEntityComponent.mapEntityType) {
            MapEntityType.TemporaryPlatform -> handleTempPlatform(entity)
            MapEntityType.Portal -> handlePortal(entity)
            MapEntityType.Clamp -> handleClamp(entity, dt)
            MapEntityType.HealthPack -> handleHealthPack(entity)
            MapEntityType.ForceField -> handleForceField(entity, dt)
            MapEntityType.DamageBoost -> handleDamageBoost(entity)
            else -> {}
        }
    }

    private fun handleMovement(entity: Entity?, bf: BackAndForthComponent) {
        val bounds = Mapper.BOUNDING_BOX_MAPPER[entity]
        val position = Mapper.POS_MAPPER[entity]
        val velocity = Mapper.VEL_MAPPER[entity]

        if (velocity.dx != 0f) {
            if (bf.positive) {
                val trueX = position.x + bounds.rect.width
                if ((velocity.dx > 0 && trueX - position.originX >= bf.dist) ||
                        (velocity.dx < 0 && position.x <= position.originX)) {
                    velocity.dx = -velocity.dx
                }
            }
            else {
                if ((velocity.dx < 0 && position.originX - position.x >= bf.dist) ||
                        (velocity.dx > 0 && position.x >= position.originX)) {
                    velocity.dx = -velocity.dx
                }
            }
        }
        else if (velocity.dy != 0f) {
            if (bf.positive) {
                val trueY = position.y + bounds.rect.height
                if ((velocity.dy > 0 && trueY - position.originY >= bf.dist) ||
                        (velocity.dy < 0 && position.y <= position.originY)) {
                    velocity.dy = -velocity.dy
                }
            }
            else {
                if ((velocity.dy < 0 && position.originY - position.y >= bf.dist) ||
                        (velocity.dy > 0 && position.y >= position.originY)) {
                    velocity.dy = -velocity.dy
                }
            }
        }
    }

    private fun handleTempPlatform(entity: Entity?) {
        val bounds = Mapper.BOUNDING_BOX_MAPPER[entity]
        val remove = Mapper.REMOVE_MAPPER[entity]
        val playerBounds = Mapper.BOUNDING_BOX_MAPPER[player]
        val playerVel = Mapper.VEL_MAPPER[player]
        val playerComp = Mapper.PLAYER_MAPPER[player]

        if (playerBounds.rect.overlaps(bounds.rect)) {
            remove.shouldRemove = true
            playerComp.canDoubleJump = true
            playerVel.dy = 0f
        }
    }

    private fun handlePortal(entity: Entity?) {
        val bounds = Mapper.BOUNDING_BOX_MAPPER[entity]
        val portalSource = Mapper.PORTAL_MAPPER[entity]

        for (pEntity in portalAffectedEntities) {
            val pEntityBounds = Mapper.BOUNDING_BOX_MAPPER[pEntity]
            val width = Mapper.TEXTURE_MAPPER[pEntity].texture!!.regionWidth
            val height = Mapper.TEXTURE_MAPPER[pEntity].texture!!.regionHeight

            val lastPortal = Mapper.LAST_ENTITY_MAPPER[pEntity]
            if (lastPortal?.entity != null && lastPortal.entity!! == entity) {
                val leBounds = Mapper.BOUNDING_BOX_MAPPER[lastPortal.entity]
                if (portalSource.teleported && !pEntityBounds.rect.overlaps(leBounds.rect)) {
                    portalSource.teleported = false
                    pEntity.remove(LastEntityComponent::class.java)
                }
            }

            if (pEntityBounds.rect.overlaps(bounds.rect)) {
                if (!portalSource.teleported) {
                    for (portal in portals) {
                        val portalTarget = Mapper.PORTAL_MAPPER[portal]
                        if (portalTarget.id == portalSource.target) {
                            val targetPos = Mapper.BOUNDING_BOX_MAPPER[portal]
                            val entityPos = Mapper.POS_MAPPER[pEntity]

                            entityPos.set(targetPos.rect.x, targetPos.rect.y)
                            pEntityBounds.rect.setPosition(entityPos.x + (width - pEntityBounds.rect.width) / 2,
                                    entityPos.y + (height - pEntityBounds.rect.height) / 2)
                            portalTarget.teleported = true

                            val lastEntity = Mapper.LAST_ENTITY_MAPPER[pEntity]
                            if (lastEntity == null) {
                                pEntity?.add((engine as PooledEngine).createComponent(LastEntityComponent::class.java))
                                Mapper.LAST_ENTITY_MAPPER[pEntity].entity = portal
                            } else {
                                lastEntity.entity = portal
                            }

                            break
                        }
                    }
                }
            }
        }
    }

    private fun handleClamp(entity: Entity?, dt: Float) {
        val clamp = Mapper.CLAMP_MAPPER[entity]
        val pos = Mapper.POS_MAPPER[entity]
        val bounds = Mapper.BOUNDING_BOX_MAPPER[entity]
        val vel = Mapper.VEL_MAPPER[entity]
        val playerBounds = Mapper.BOUNDING_BOX_MAPPER[player]

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
            val playerHealth = Mapper.HEALTH_MAPPER[player]
            playerHealth.hp = 0

            val color = Mapper.COLOR_MAPPER[player]
            ParticleSpawner.spawn(res, color.hex!!, DEFAULT_LIFETIME,
                    (DEFAULT_INTESITY + playerHealth.maxHp) * 2,
                    playerBounds.rect.x + playerBounds.rect.width / 2,
                    playerBounds.rect.y + playerBounds.rect.height / 2)
        }
    }

    private fun handleHealthPack(entity: Entity?) {
        val healthPack = Mapper.HEALTH_PACK_MAPPER[entity]
        val bounds = Mapper.BOUNDING_BOX_MAPPER[entity]
        val playerBounds = Mapper.BOUNDING_BOX_MAPPER[player]
        val remove = Mapper.REMOVE_MAPPER[entity]

        if (playerBounds.rect.overlaps(bounds.rect)) {
            val playerComp = Mapper.PLAYER_MAPPER[player]
            val se = Mapper.STATUS_EFFECT_MAPPER[player]

            playerComp.startHealing = true
            playerComp.healing = healthPack.regen
            playerComp.healTime = healthPack.regenTime
            se.apply(StatusEffect.Healing, healthPack.regenTime)

            remove.shouldRemove = true
        }
    }

    private fun handleForceField(entity: Entity?, dt: Float) {
        Mapper.FORCE_FIELD_MAPPER[entity].run {
            if (duration != 0f) {
                timer += dt
                if (timer >= duration) {
                    activated = !activated
                    Mapper.TEXTURE_MAPPER[entity].run {
                        texture = if (activated) res.getTexture(textureStr!!) else null
                    }
                    timer = 0f
                }
            }
        }
    }

    private fun handleDamageBoost(entity: Entity?) {
        val boost = Mapper.DAMAGE_BOOST_MAPPER[entity]
        val bounds = Mapper.BOUNDING_BOX_MAPPER[entity]
        val playerBounds = Mapper.BOUNDING_BOX_MAPPER[player]
        val remove = Mapper.REMOVE_MAPPER[entity]

        if (playerBounds.rect.overlaps(bounds.rect)) {
            val playerComp = Mapper.PLAYER_MAPPER[player]
            val se = Mapper.STATUS_EFFECT_MAPPER[player]

            playerComp.damageBoost = boost.damageBoost
            se.apply(StatusEffect.DamageBoost, boost.duration)

            remove.shouldRemove = true
        }
    }

}