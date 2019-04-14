package com.symbol.game.scene

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image

class DynamicImage(drawable: TextureRegion) : Image(drawable) {

    private var nextImage: DynamicImage? = null

    private var startLinear = false
    private val origin = Vector2()
    private val target = Vector2()
    private var speed = 0f

    private var startJump = false
    private var dy = 0f
    private var gravity = 0f

    fun startLinearMovement() {
        startLinear = true
    }

    fun applyLinearMovement(origin: Vector2, target: Vector2, speed: Float) {
        startLinear = false
        setPosition(origin.x, origin.y)
        this.origin.set(origin)
        this.target.set(target)
        this.speed = speed
    }

    fun applyJump(gravity: Float, impulse: Float) {
        if (!startJump && !startLinear) {
            this.gravity = gravity
            origin.y = y
            dy = impulse
            startJump = true
        }
    }

    fun moving() : Boolean = startLinear || startJump

    fun link(image: DynamicImage) {
        nextImage = image
    }

    fun update(dt: Float) {
        if (startLinear) {
            if (origin.y == target.y) {
                if (origin.x < target.x) {
                    if (x < target.x && x + speed * dt < target.x) {
                        val next = x + speed * dt
                        setPosition(next, y)
                    } else endLinearMovement()
                }
                else {
                    if (x > target.x && x - speed * dt > target.x) {
                        val next = x - speed * dt
                        setPosition(next, y)
                    } else endLinearMovement()
                }
            } else {
                if (origin.y < target.y && y + speed * dt < target.y) {
                    if (y < target.y) {
                        val next = y + speed * dt
                        setPosition(x, next)
                    } else endLinearMovement()
                }
                else {
                    if (y > target.y && y - speed * dt > target.y) {
                        val next = y - speed * dt
                        setPosition(x, next)
                    } else endLinearMovement()
                }
            }
        }
        else if (startJump) {
            dy -= gravity * dt
            y += dy * dt
            if (y < origin.y) endJump()
        }
    }

    private fun endLinearMovement() {
        setPosition(target.x, target.y)
        startLinear = false
        nextImage?.startLinearMovement()
    }

    private fun endJump() {
        y = origin.y
        startJump = false
    }

}