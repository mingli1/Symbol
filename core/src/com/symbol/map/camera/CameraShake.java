package com.symbol.map.camera;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class CameraShake {

    public static float time = 0f;
    public static boolean toggle = false;

    private static float currentTime = 0f;
    private static float power = 0f;
    private static float currentPower = 0f;

    public static Vector3 position = new Vector3();

    public static void shake(float power) {
        CameraShake.power = power;
        toggle = true;
    }

    public static void shakeFor(float power, float duration) {
        CameraShake.power = power;
        time = duration;
    }

    public static void stop() {
        toggle = false;
    }

    public static void update(float dt) {
        if (toggle) {
            currentPower = power;
            applyShake();
        }
        else {
            if (currentTime < time) {
                currentPower = power * ((time - currentTime) / time);
                applyShake();
                currentTime += dt;
            }
            else {
                time = 0f;
                currentTime = 0f;
            }
        }
    }

    private static void applyShake() {
        position.x = (MathUtils.random() - 0.5f) * 2 * currentPower;
        position.y = (MathUtils.random() - 0.5f) * 2 * currentPower;
    }

}