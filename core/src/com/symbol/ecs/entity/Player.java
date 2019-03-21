package com.symbol.ecs.entity;

import com.badlogic.ashley.core.Entity;
import com.symbol.ecs.component.*;
import com.symbol.util.Resources;

public class Player extends Entity {

    public static final float PLAYER_WIDTH = 8f;
    public static final float PLAYER_HEIGHT = 8f;

    public static final float PLAYER_PROJECTILE_SHOOT_DELAY = 0.1f;
    public static final float PLAYER_PROJECTILE_SPEED = 80f;
    public static final float PLAYER_PROJECTILE_BOUNDS_WIDTH = 4f;
    public static final float PLAYER_PROJECTILE_BOUNDS_HEIGHT = 4f;
    public static final String PLAYER_PROJECTILE_RES_KEY = "p_dot";
    public static final float PLAYER_PROJECTILE_KNOCKBACK = 75f;

    public static final int PLAYER_HP = 8;
    public static final int PLAYER_DAMAGE = 1;

    private static final float PLAYER_SPEED = 35f;
    private static final float PLAYER_JUMP_IMPULSE = 160f;
    private static final float PLAYER_BOUNDS_WIDTH = 7f;
    private static final float PLAYER_BOUNDS_HEIGHT = 7f;

    public Player(Resources res) {
        ColorComponent color = new ColorComponent();
        BoundingBoxComponent bounds = new BoundingBoxComponent();
        TextureComponent texture = new TextureComponent();
        VelocityComponent velocity = new VelocityComponent();
        HealthComponent health = new HealthComponent();
        JumpComponent jump = new JumpComponent();

        color.hex = EntityColor.PLAYER_COLOR;
        bounds.rect.setSize(PLAYER_BOUNDS_WIDTH, PLAYER_BOUNDS_HEIGHT);
        texture.texture = res.getTexture("player");
        velocity.speed = PLAYER_SPEED;
        health.hp = PLAYER_HP;
        health.maxHp = PLAYER_HP;
        jump.impulse = PLAYER_JUMP_IMPULSE;

        add(new PlayerComponent());
        add(new PositionComponent());
        add(new GravityComponent());
        add(new DirectionComponent());
        add(color);
        add(bounds);
        add(texture);
        add(velocity);
        add(health);
        add(jump);
    }

}