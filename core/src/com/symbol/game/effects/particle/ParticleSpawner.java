package com.symbol.game.effects.particle;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.symbol.game.util.Resources;

import java.util.Random;

public class ParticleSpawner {

    private static Array<Particle> particles = new Array<Particle>();
    private static Pool<Particle> particlePool;

    public static void init() {
        final Random rand = new Random();
        particlePool = new Pool<Particle>() {
            @Override
            protected Particle newObject() {
                return new Particle(rand);
            }
        };
    }

    public static void spawn(Resources res, String hex, float lifetime, int intensity, float x, float y) {
        for (int i = 0; i < intensity; i++) {
            Particle particle = particlePool.obtain();
            particle.set(res, hex, lifetime);
            particle.initVectors(x, y, Particle.DEFAULT_INITIAL_Z);
            particles.add(particle);
        }
    }

    public static void update(float dt) {
        for (int i = particles.size - 1; i >= 0; i--) {
            Particle particle = particles.get(i);
            particle.update(dt);
            if (particle.shouldRemove()) {
                particles.removeIndex(i);
                particlePool.free(particle);
            }
        }
    }

    public static void render(Batch batch) {
        for (Particle particle : particles) {
            particle.render(batch);
        }
    }

}