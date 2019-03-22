package com.symbol.game.effects.particle;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import com.symbol.game.util.Resources;

import java.util.Random;

public class Particle implements Pool.Poolable {

    public static final float DEFAULT_LIFETIME = 0.5f;
    public static final int DEFAULT_INTESITY = 7;

    public static final float DEFAULT_INITIAL_Z = 2f;
    public static final float DEFAULT_VX_SCALING = 0.5f;
    public static final float DEFAULT_VY_SCALING = 0.5f;
    public static final float DEFAULT_VZ_SCALING = 0.7f;
    public static final float DEFAULT_Z_NEG_VX_SCALING = 0.6f;
    public static final float DEFAULT_Z_NEG_VY_SCALING = 0.6f;
    public static final float DEFAULT_Z_NEG_VZ_SCALING = -0.5f;
    public static final float DEFAULT_Z_POS_VZ_SCALING = 0.15f;

    private Vector3 position = new Vector3();
    private Vector3 velocity = new Vector3();

    private float zNegVxScale = 0f;
    private float zNegVyScale = 0f;
    private float zNegVzScale = 0f;
    private float zPosVzScale = 0f;

    private TextureRegion texture;

    private boolean shouldRemove = false;
    private float stateTime = 0f;
    private float lifetime = 0f;

    private Random rand;

    public Particle(Random rand) {
        this.rand = rand;
    }

    public void set(Resources res, String hex, float lifetime) {
        texture = res.getTexture(hex);
        this.lifetime = lifetime;
    }

    public void initVectors(float originX, float originY, float zi) {
        position.set(originX, originY, zi);

        float vx = (float) rand.nextGaussian() * DEFAULT_VX_SCALING;
        float vy = (float) rand.nextGaussian() * DEFAULT_VY_SCALING;
        float vz = rand.nextFloat() * DEFAULT_VZ_SCALING + zi;
        velocity.set(vx, vy, vz);

        this.zNegVxScale = DEFAULT_Z_NEG_VX_SCALING;
        this.zNegVyScale = DEFAULT_Z_NEG_VY_SCALING;
        this.zNegVzScale = DEFAULT_Z_NEG_VZ_SCALING;
        this.zPosVzScale = DEFAULT_Z_POS_VZ_SCALING;
    }

    public void update(float dt) {
        stateTime += dt;
        if (stateTime >= lifetime) {
            shouldRemove = true;
        }

        if (!shouldRemove) {
            position.x += velocity.x;
            position.y += velocity.y;
            position.z += velocity.z;

            if (position.z > 0) {
                position.z = 0f;
                velocity.z *= zNegVzScale;
                velocity.x *= zNegVxScale;
                velocity.y *= zNegVyScale;
            }
            velocity.z += zPosVzScale;
        }
    }

    public void render(Batch batch) {
        if (!shouldRemove) {
            batch.draw(texture, position.x, position.y - position.z);
        }
    }

    public boolean shouldRemove() {
        return shouldRemove;
    }

    @Override
    public void reset() {
        texture = null;
        position.set(0f, 0f, 0f);
        velocity.set(0f, 0f, 0f);
        shouldRemove = false;
        stateTime = 0f;
        lifetime = 0f;
        zNegVyScale = 0f;
        zNegVxScale = 0f;
        zNegVzScale = 0f;
        zPosVzScale = 0f;
    }

}