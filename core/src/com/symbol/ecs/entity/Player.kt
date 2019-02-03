package com.symbol.ecs.entity

import com.badlogic.ashley.core.Entity
import com.symbol.ecs.component.*
import com.symbol.ecs.component.player.PlayerComponent

const val PLAYER_BOUNDS_WIDTH = 7f
const val PLAYER_BOUNDS_HEIGHT = 7f
const val PLAYER_SPEED = 35f
const val PLAYER_JUMP_IMPULSE = 160f

class Player : Entity() {

    init {
        add(PlayerComponent())
        add(PositionComponent())
        add(PreviousPositionComponent())
        add(GravityComponent())
        add(BoundingBoxComponent())
        add(TextureComponent())
        add(VelocityComponent())
        add(SpeedComponent())
        add(DirectionComponent())
    }

}