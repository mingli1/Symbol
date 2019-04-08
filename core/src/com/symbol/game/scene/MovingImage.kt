package com.symbol.game.scene

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image

class MovingImage(drawable: TextureRegion, private val origin: Vector2,
                  val target: Vector2, var speed: Float) : Image(drawable) {

    private var shouldStart = false
    private var nextImage: MovingImage? = null

    fun start() {
        shouldStart = true
    }

    fun link(image: MovingImage) {
        nextImage = image
    }

    override fun setOrigin(x: Float, y: Float) {
        origin.set(x, y)
        setPosition(x, y)
    }

    fun update(dt: Float) {
        if (shouldStart) {
            if (origin.y == target.y) {
                if (origin.x < target.x) {
                    if (x < target.x && x + speed * dt < target.x) {
                        val next = x + speed * dt
                        setPosition(next, y)
                    } else end()
                }
                else {
                    if (x > target.x && x - speed * dt > target.x) {
                        val next = x - speed * dt
                        setPosition(next, y)
                    } else end()
                }
            } else {
                if (origin.y < target.y && y + speed * dt < target.y) {
                    if (y < target.y) {
                        val next = y + speed * dt
                        setPosition(x, next)
                    } else end()
                }
                else {
                    if (y > target.y && y - speed * dt > target.y) {
                        val next = y - speed * dt
                        setPosition(x, next)
                    } else end()
                }
            }
        }
    }

    private fun end() {
        setPosition(target.x, target.y)
        shouldStart = false
        nextImage?.start()
    }

}