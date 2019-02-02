package com.symbol.ecs

import com.badlogic.ashley.core.PooledEngine
import com.symbol.ecs.entity.PLAYER_BOUNDS_HEIGHT
import com.symbol.ecs.entity.PLAYER_BOUNDS_WIDTH
import com.symbol.ecs.entity.PLAYER_SPEED
import com.symbol.ecs.entity.Player
import com.symbol.util.Resources

object EntityFactory {

    fun createPlayer(engine: PooledEngine, res: Resources) : Player {
        val player = Player()
        engine.addEntity(player)

        val bounds = Mapper.BOUNDING_BOX_MAPPER.get(player)
        val texture = Mapper.TEXTURE_MAPPER.get(player)
        val speed = Mapper.SPEED_MAPPER.get(player)

        bounds.rect.setSize(PLAYER_BOUNDS_WIDTH, PLAYER_BOUNDS_HEIGHT)
        texture.texture = res.getSingleTexture("player")
        speed.speed = PLAYER_SPEED

        return player
    }

}