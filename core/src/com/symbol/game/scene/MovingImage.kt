package com.symbol.game.scene

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image

class MovingImage(drawable: TextureRegion, val origin: Vector2,
                  val target: Vector2, val speed: Float) : Image(drawable) {

    private var horizontal = false
    private var shouldStart = false

    init {
        horizontal = origin.y == target.y
    }

    fun start() {
        shouldStart = true
    }

    fun ended() : Boolean = origin == target

    fun update(dt: Float) {
        if (shouldStart) {
            if (horizontal) {
                if (origin.x < target.x) {
                    if (x < target.x && x + speed * dt < target.x) {
                        val next = x + speed * dt
                        setPosition(next, y)
                    } else {
                        setPosition(target.x, target.y)
                        shouldStart = false
                    }
                }
                else {
                    if (x > target.x && x - speed * dt > target.x) {
                        val next = x - speed * dt
                        setPosition(next, y)
                    } else {
                        setPosition(target.x, target.y)
                        shouldStart = false
                    }
                }
            } else {
                // moving up
                if (origin.y < target.y && y + speed * dt < target.y) {
                    if (y < target.y) {
                        val next = y + speed * dt
                        setPosition(x, next)
                    } else {
                        setPosition(target.x, target.y)
                        shouldStart = false
                    }
                }
                // moving down
                else {
                    if (y > target.y && y - speed * dt > target.y) {
                        val next = y - speed * dt
                        setPosition(x, next)
                    } else {
                        setPosition(target.x, target.y)
                        shouldStart = false
                    }
                }
            }
        }
    }

}