package com.symbol.input;

import com.badlogic.ashley.core.*;
import com.symbol.ecs.EntityBuilder;
import com.symbol.ecs.Mapper;
import com.symbol.ecs.component.DirectionComponent;
import com.symbol.ecs.component.GravityComponent;
import com.symbol.ecs.component.JumpComponent;
import com.symbol.ecs.component.PlayerComponent;
import com.symbol.ecs.component.PositionComponent;
import com.symbol.ecs.component.VelocityComponent;
import com.symbol.ecs.entity.*;
import com.symbol.ecs.system.MapCollisionSystem;
import com.symbol.util.Resources;

public class KeyInputSystem extends EntitySystem implements KeyInputHandler {

    private Resources res;

    private Entity player;
    private PlayerComponent playerComp;
    private VelocityComponent vel;

    public KeyInputSystem(Resources res) {
        this.res = res;
    }

    @Override
    public void addedToEngine(Engine engine) {
        player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).get(0);
        playerComp = Mapper.PLAYER_MAPPER.get(player);
        vel = Mapper.VEL_MAPPER.get(player);
    }

    @Override
    public void move(boolean right) {
        vel.move(right);
    }

    @Override
    public void stop(boolean right) {
        if (right) { if (vel.dx > 0) vel.dx = 0f; }
        else { if (vel.dx < 0) vel.dx = 0f; }
    }

    @Override
    public void jump() {
        GravityComponent gravity = Mapper.GRAVITY_MAPPER.get(player);
        JumpComponent jump = Mapper.JUMP_MAPPER.get(player);

        if (gravity.onGround && playerComp.canJump) {
            if (gravity.reverse)
                vel.dy = playerComp.hasJumpBoost ? -jump.impulse * MapCollisionSystem.MAP_OBJECT_JUMP_BOOST_PERCENTAGE : -jump.impulse;
            else
                vel.dy = playerComp.hasJumpBoost ? jump.impulse * MapCollisionSystem.MAP_OBJECT_JUMP_BOOST_PERCENTAGE : jump.impulse;
            playerComp.canJump = false;
            playerComp.canDoubleJump = true;
        }
        else if (playerComp.canDoubleJump) {
            vel.dy = gravity.reverse ? -jump.impulse : jump.impulse;
            playerComp.canDoubleJump = false;
        }
    }

    @Override
    public void shoot() {
        if (playerComp.canShoot) {
            PositionComponent playerPos = Mapper.POS_MAPPER.get(player);
            DirectionComponent dir = Mapper.DIR_MAPPER.get(player);

            EntityBuilder.Companion.instance((PooledEngine) getEngine())
                    .projectile(ProjectileMovementType.Normal,
                            false, true, false, null, false,
                            Player.PLAYER_DAMAGE, Player.PLAYER_PROJECTILE_KNOCKBACK, 0f, 0f)
                    .color(EntityColor.DOT_COLOR)
                    .position(playerPos.x + (Player.PLAYER_WIDTH / 2) - (Player.PLAYER_PROJECTILE_BOUNDS_WIDTH / 2),
                            playerPos.y + (Player.PLAYER_HEIGHT / 2) - (Player.PLAYER_PROJECTILE_BOUNDS_HEIGHT / 2))
                    .velocity(dir.facingRight ? Player.PLAYER_PROJECTILE_SPEED : -Player.PLAYER_PROJECTILE_SPEED, 0f, 0f)
                    .boundingBox(Player.PLAYER_PROJECTILE_BOUNDS_WIDTH, Player.PLAYER_PROJECTILE_BOUNDS_HEIGHT, 0f, 0f)
                    .texture(res.getTexture(Player.PLAYER_PROJECTILE_RES_KEY), null)
                    .direction(false, false).remove().build();

            playerComp.canShoot = false;
        }
    }

}