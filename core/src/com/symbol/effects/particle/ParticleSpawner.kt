package com.symbol.effects.particle

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import com.symbol.util.Resources

object ParticleSpawner {

    private val particles = Array<Particle>()
    private val particlePool: Pool<Particle>

    init {
        particlePool = object : Pool<Particle>() {
            override fun newObject(): Particle = Particle()
        }
    }

    fun spawn(res: Resources, hex: String, lifetime: Float, intensity: Int, x: Float, y: Float,
              zi: Float = DEFAULT_INITIAL_Z,
              vxScale: Float = DEFAULT_VX_SCALING,
              vyScale: Float = DEFAULT_VY_SCALING,
              vzScale: Float = DEFAULT_VZ_SCALING,
              zNegVxScale: Float = DEFAULT_Z_NEG_VX_SCALING,
              zNegVyScale: Float = DEFAULT_Z_NEG_VY_SCALING,
              zNegVzScale: Float = DEFAULT_Z_NEG_VZ_SCALING,
              zPosVzScale: Float = DEFAULT_Z_POS_VZ_SCALING) {
        for (i in 1..intensity) {
            val particle = particlePool.obtain()
            particle.set(res, hex, lifetime)
            particle.initVectors(x, y, zi, vxScale, vyScale, vzScale,
                    zNegVxScale, zNegVyScale, zNegVzScale, zPosVzScale)
            particles.add(particle)
        }
    }

    fun update(dt: Float) {
        for (i in particles.size - 1 downTo 0) {
            val particle = particles[i]
            particle.update(dt)
            if (particle.shouldRemove) {
                particles.removeIndex(i)
                particlePool.free(particle)
            }
        }
    }

    fun render(batch: Batch) {
        for (particle in particles) {
            particle.render(batch)
        }
    }

}