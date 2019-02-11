package com.symbol.ecs.system.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Rectangle
import com.symbol.ecs.EntityBuilder
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.EnemyComponent
import com.symbol.ecs.entity.EnemyAttackType
import com.symbol.ecs.entity.Player
import com.symbol.util.Resources

class EnemyAttackSystem(private val player: Player, private val res: Resources) :
        IteratingSystem(Family.all(EnemyComponent::class.java).get()) {

    private var attackTimers: MutableMap<Entity, Float> = HashMap()

    fun reset() {
        attackTimers.clear()
        for (entity in entities) {
            attackTimers[entity] = 0f
        }
    }

    override fun processEntity(entity: Entity?, dt: Float) {
        val enemyComponent = Mapper.ENEMY_MAPPER.get(entity)
        val remove = Mapper.REMOVE_MAPPER.get(entity)
        val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity).rect
        val playerBounds = Mapper.BOUNDING_BOX_MAPPER.get(player).rect
        val facingRight = Mapper.DIR_MAPPER.get(entity).facingRight

        if (bounds.overlaps(playerBounds)) {
            val playerHealth = Mapper.HEALTH_MAPPER.get(player)
            playerHealth.hp -= enemyComponent.damage
            remove.shouldRemove = true
            return
        }

        if (enemyComponent.active && enemyComponent.canAttack) {
            when (enemyComponent.attackType) {
                EnemyAttackType.None -> return
                EnemyAttackType.ShootOne -> shootOne(enemyComponent, bounds, facingRight)
            }
        }

        if (!enemyComponent.canAttack) {
            attackTimers[entity!!] = attackTimers[entity]?.plus(dt)!!
            if (attackTimers[entity]!! >= enemyComponent.attackRate) {
                attackTimers[entity] = 0f
                enemyComponent.canAttack = true
            }
        }
    }

    private fun shootOne(enemyComp: EnemyComponent, bounds: Rectangle, facingRight: Boolean) {
        val texture = res.getTexture(enemyComp.attackTexture!!)
        val bw = texture!!.regionWidth - 1
        val bh = texture.regionHeight - 1
        EntityBuilder.instance(engine as PooledEngine)
                .projectile(unstoppable = true, enemy = true, damage = enemyComp.damage)
                .position(bounds.x + (bounds.width / 2) - (bw / 2), bounds.y + (bounds.height / 2) - (bh / 2))
                .velocity(dx = if (facingRight) enemyComp.projectileSpeed else -enemyComp.projectileSpeed)
                .boundingBox(bw.toFloat(), bh.toFloat())
                .texture(texture)
                .direction().remove().build()
        enemyComp.canAttack = false
    }

}