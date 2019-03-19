package com.symbol.ecs.system

import com.badlogic.ashley.core.EntitySystem
import com.symbol.ecs.Mapper
import com.symbol.ecs.entity.PLAYER_PROJECTILE_SHOOT_DELAY
import com.symbol.ecs.entity.Player

class PlayerSystem(private val player: Player) : EntitySystem() {

    private var stateTime = 0f

    override fun update(dt: Float) {
        val playerComp = Mapper.PLAYER_MAPPER.get(player)

        if (!playerComp.canShoot) {
            stateTime += dt
            if (stateTime >= PLAYER_PROJECTILE_SHOOT_DELAY) {
                playerComp.canShoot = true
                stateTime = 0f
            }
        }
    }

}