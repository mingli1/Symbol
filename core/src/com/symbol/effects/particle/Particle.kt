package com.symbol.effects.particle

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool
import com.symbol.util.Resources
import java.util.Random

const val DEFAULT_LIFETIME = 0.5f
const val DEFAULT_INTESITY = 7

const val DEFAULT_INITIAL_Z = 2f
const val DEFAULT_VX_SCALING = 0.5f
const val DEFAULT_VY_SCALING = 0.5f
const val DEFAULT_VZ_SCALING = 0.7f
const val DEFAULT_Z_NEG_VX_SCALING = 0.6f
const val DEFAULT_Z_NEG_VY_SCALING = 0.6f
const val DEFAULT_Z_NEG_VZ_SCALING = -0.5f
const val DEFAULT_Z_POS_VZ_SCALING = 0.15f

class Particle(private val rand: Random) : Pool.Poolable {

    private val position = Vector3()
    private val velocity = Vector3()

    private var zNegVxScale: Float = 0f
    private var zNegVyScale: Float = 0f
    private var zNegVzScale: Float = 0f
    private var zPosVzScale: Float = 0f

    private var texture: TextureRegion? = null

    var shouldRemove: Boolean = false
    private var stateTime: Float = 0f
    private var lifetime: Float = 0f

    fun set(res: Resources, hex: String, lifetime: Float) {
        texture = res.getTexture(hex)!!
        this.lifetime = lifetime
    }

    fun initVectors(originX: Float, originY: Float, zi: Float = DEFAULT_INITIAL_Z,
                    vxScale: Float = DEFAULT_VX_SCALING,
                    vyScale: Float = DEFAULT_VY_SCALING,
                    vzScale: Float = DEFAULT_VZ_SCALING,
                    zNegVxScale: Float = DEFAULT_Z_NEG_VX_SCALING,
                    zNegVyScale: Float = DEFAULT_Z_NEG_VY_SCALING,
                    zNegVzScale: Float = DEFAULT_Z_NEG_VZ_SCALING,
                    zPosVzScale: Float = DEFAULT_Z_POS_VZ_SCALING) {
        position.set(originX, originY, zi)

        val vx = rand.nextGaussian().toFloat() * vxScale
        val vy = rand.nextGaussian().toFloat() * vyScale
        val vz = rand.nextFloat() * vzScale + zi
        velocity.set(vx, vy, vz)

        this.zNegVxScale = zNegVxScale
        this.zNegVyScale = zNegVyScale
        this.zNegVzScale = zNegVzScale
        this.zPosVzScale = zPosVzScale
    }

    fun update(dt: Float) {
        stateTime += dt
        if (stateTime >= lifetime) {
            shouldRemove = true
        }

        if (!shouldRemove) {
            position.x += velocity.x
            position.y += velocity.y
            position.z += velocity.z

            if (position.z > 0) {
                position.z = 0f
                velocity.z *= zNegVzScale
                velocity.x *= zNegVxScale
                velocity.y *= zNegVyScale
            }
            velocity.z += zPosVzScale
        }
    }

    fun render(batch: Batch) {
        if (!shouldRemove) {
            batch.draw(texture, position.x, position.y - position.z)
        }
    }

    override fun reset() {
        texture = null
        position.set(0f, 0f, 0f)
        velocity.set(0f, 0f, 0f)
        shouldRemove = false
        stateTime = 0f
        lifetime = 0f
        zNegVyScale = 0f
        zNegVxScale = 0f
        zNegVzScale = 0f
        zPosVzScale = 0f
    }

}