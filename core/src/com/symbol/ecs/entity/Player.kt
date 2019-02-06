package com.symbol.ecs.entity

import com.badlogic.ashley.core.Entity
import com.symbol.ecs.component.*
import com.symbol.ecs.component.player.PlayerComponent

const val PLAYER_WIDTH = 8f
const val PLAYER_HEIGHT = 8f
const val PLAYER_BOUNDS_WIDTH = 7f
const val PLAYER_BOUNDS_HEIGHT = 7f

const val PLAYER_SPEED = 35f
const val PLAYER_JUMP_IMPULSE = 160f

const val PLAYER_PROJECTILE_SHOOT_DELAY = 0.1f
const val PLAYER_PROJECTILE_SPEED = 80f
const val PLAYER_PROJECTILE_BOUNDS_WIDTH = 4f
const val PLAYER_PROJECTILE_BOUNDS_HEIGHT = 4f
const val PLAYER_PROJECTILE_RES_KEY = "p_dot"

const val PLAYER_HP = 8
const val PLAYER_DAMAGE = 1

class Player : Entity() {

    init {
        add(PlayerComponent())
        add(PositionComponent())
        add(GravityComponent())
        add(BoundingBoxComponent())
        add(TextureComponent())
        add(VelocityComponent())
        add(DirectionComponent())
        add(HealthComponent())
    }

}