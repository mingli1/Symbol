package com.symbol.game.ecs.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.symbol.game.ecs.EntityBuilder;
import com.symbol.game.ecs.system.GravitySystem;
import com.symbol.game.util.Resources;

public class EntityFactory {

    public static void createEnemy(PooledEngine engine, Resources res, EnemyType type, Rectangle rect, boolean facingRight) {
        String textureStr = type == EnemyType.Because ? "e_" + type.typeStr + "0" : "e_" + type.typeStr;
        TextureRegion texture = res.getTexture(textureStr);
        switch (type) {
            case EConstant:
                EntityBuilder.instance(engine)
                        .enemy(EnemyMovementType.BackAndForth, EnemyAttackType.None, null)
                        .activation(150f)
                        .attack(2, 0f, null, 0f, 0f, false, 0f)
                        .color(EntityColor.E_COLOR)
                        .health(2)
                        .boundingBox(7f, 7f, 0f, 0f)
                        .position(rect.x, rect.y)
                        .velocity(0f, 0f, 25f)
                        .direction(facingRight, false)
                        .texture(texture, textureStr)
                        .knockback().gravity(false, GravitySystem.GRAVITY, GravitySystem.TERMINAL_VELOCITY, true).remove().build();
                break;
            case SquareRoot:
                EntityBuilder.instance(engine)
                        .enemy(EnemyMovementType.Charge, EnemyAttackType.None, null)
                        .activation(75f)
                        .attack(3, 0f, null, 0f, 0f, false, 0f)
                        .color(EntityColor.SQRT_COLOR)
                        .health(3)
                        .boundingBox(10f, 8f, 0f, 0f)
                        .position(rect.x, rect.y)
                        .velocity(0f, 0f, 60f)
                        .direction(facingRight, false)
                        .texture(texture, textureStr)
                        .gravity(false, GravitySystem.GRAVITY, GravitySystem.TERMINAL_VELOCITY, true).remove().build();
                break;
            case Exists:
                EntityBuilder.instance(engine)
                        .enemy(EnemyMovementType.Charge, EnemyAttackType.None, null)
                        .activation(90f)
                        .attack(Player.PLAYER_HP, 0f, null, 0f, 0f, false, 0f)
                        .color(EntityColor.EXISTS_COLOR)
                        .health(2)
                        .boundingBox(9f, 13f, 0f, 0f)
                        .position(rect.x, rect.y)
                        .velocity(0f, 0f, 75f)
                        .direction(facingRight, false)
                        .texture(texture, textureStr)
                        .gravity(false, GravitySystem.GRAVITY, GravitySystem.TERMINAL_VELOCITY, true).remove().build();
                break;
            case Summation:
                EntityBuilder.instance(engine)
                        .enemy(EnemyMovementType.None, EnemyAttackType.ShootOne, null)
                        .activation(120f)
                        .attack(2, 1.4f, "p_large_triangle", 45f, 0f, false, 0f)
                        .color(EntityColor.SUM_COLOR)
                        .health(2)
                        .boundingBox(10f, 13f, 0f, 0f)
                        .position(rect.x, rect.y)
                        .velocity(0f, 0f, 0f)
                        .direction(facingRight, false)
                        .texture(texture, textureStr)
                        .gravity(false, GravitySystem.GRAVITY, GravitySystem.TERMINAL_VELOCITY, true).remove().build();
                break;
            case BigPi:
                EntityBuilder.instance(engine)
                        .enemy(EnemyMovementType.None, EnemyAttackType.ShootOne, null)
                        .activation(120f)
                        .attack(4, 1.4f, "p_big_ll", 45f, 0f, false, 0f)
                        .color(EntityColor.BIG_PI_COLOR)
                        .health(4)
                        .boundingBox(11f, 13f, 0f, 0f)
                        .position(rect.x, rect.y)
                        .velocity(0f, 0f, 0f)
                        .direction(facingRight, false)
                        .texture(texture, textureStr)
                        .gravity(false, GravitySystem.GRAVITY, GravitySystem.TERMINAL_VELOCITY, true).remove().build();
                break;
            case In:
                EntityBuilder.instance(engine)
                        .enemy(EnemyMovementType.None, EnemyAttackType.ShootOne, null)
                        .activation(100f)
                        .attack(1, 2f, "p_xor", 45f, 0f, false, 2f)
                        .color(EntityColor.IN_COLOR)
                        .health(3)
                        .boundingBox(11f, 11f, 0f, 0f)
                        .position(rect.x, rect.y)
                        .velocity(0f, 0f, 0f)
                        .direction(facingRight, false)
                        .texture(texture, textureStr)
                        .gravity(false, GravitySystem.GRAVITY, GravitySystem.TERMINAL_VELOCITY, true).remove().build();
                break;
            case BigOmega:
                EntityBuilder.instance(engine)
                        .enemy(EnemyMovementType.None, EnemyAttackType.SprayThree, null)
                        .activation(150f)
                        .attack(2, 2.5f, "p_cup", 200f, 0f, false, 0f)
                        .color(EntityColor.BIG_OMEGA_COLOR)
                        .health(3)
                        .boundingBox(12f, 13f, 0f, 0f)
                        .position(rect.x, rect.y)
                        .velocity(0f, 0f, 0f)
                        .texture(texture, textureStr)
                        .gravity(false, GravitySystem.GRAVITY, GravitySystem.TERMINAL_VELOCITY, true).remove().build();
                break;
            case NaturalJoin:
                EntityBuilder.instance(engine)
                        .enemy(EnemyMovementType.BackAndForth, EnemyAttackType.TwoHorizontalWave, null)
                        .activation(100f)
                        .attack(2, 1.5f, "p_ltimes", 45f, 60f, false, 0f)
                        .explode()
                        .color(EntityColor.NATURAL_JOIN_COLOR)
                        .health(4)
                        .boundingBox(9f, 7f, 0f, 0f)
                        .position(rect.x, rect.y)
                        .velocity(0f, 0f, 30f)
                        .texture(texture, textureStr)
                        .direction(facingRight, false)
                        .knockback().gravity(false, GravitySystem.GRAVITY, GravitySystem.TERMINAL_VELOCITY, true).remove().build();
                break;
            case BigPhi:
                EntityBuilder.instance(engine)
                        .enemy(EnemyMovementType.None, EnemyAttackType.ShootAndQuake, null)
                        .activation(200f)
                        .attack(4, 1.5f, "p_alpha", 60f, 0f, false, 0f)
                        .explode()
                        .color(EntityColor.BIG_PHI_COLOR)
                        .health(10)
                        .boundingBox(14f, 16f, 0f, 0f)
                        .position(rect.x, rect.y)
                        .velocity(0f, 0f, 0f)
                        .jump(150f)
                        .texture(texture, textureStr)
                        .direction(facingRight, false)
                        .gravity(false, GravitySystem.GRAVITY, GravitySystem.TERMINAL_VELOCITY, true).remove().build();
                break;
            case Percent:
                Entity parent = EntityBuilder.instance(engine)
                        .enemy(EnemyMovementType.BackAndForth, EnemyAttackType.None, null)
                        .activation(120f)
                        .attack(1, 0f, null, 0f, 0f, false, 0f)
                        .color(EntityColor.PERCENT_COLOR)
                        .health(2)
                        .boundingBox(10f, 10f, 0f, 0f)
                        .position(rect.x, rect.y)
                        .velocity(0f, 0f, 20f)
                        .jump(120f)
                        .texture(texture, textureStr)
                        .direction(facingRight, false)
                        .gravity(false, -480f, -240f, true).knockback().remove().build();

                float[] angles = new float[] {
                        MathUtils.PI2 / 5f, MathUtils.PI2 * 2f / 5f, MathUtils.PI2 * 3 / 5f, MathUtils.PI2 * 4 / 5f, 0f
                };
                for (float angle : angles) {
                    EntityBuilder.instance(engine)
                            .enemy(EnemyMovementType.Orbit, EnemyAttackType.None, parent)
                            .activation(150f)
                            .attack(1, 0f, null, 0f, 0f, false, 0f)
                            .color(EntityColor.PERCENT_ORBIT_COLOR)
                            .health(1)
                            .boundingBox(6f, 6f, 0f, 0f)
                            .position(rect.x, rect.y)
                            .velocity(0f, 0f, 0f)
                            .texture(res.getTexture("e_" + type.typeStr + Resources.ORBIT), "e_" + type.typeStr + Resources.ORBIT)
                            .orbit(true, 0f, 0f, angle, 2f, 15f)
                            .remove().build();
                }
                break;
            case Nabla:
                EntityBuilder.instance(engine)
                        .enemy(EnemyMovementType.None, EnemyAttackType.None, null)
                        .activation(140f)
                        .attack(4, 0f, null, 0f, 0f, false, 0f)
                        .color(EntityColor.NABLA_COLOR)
                        .health(1)
                        .gravity(false, -1200f, -160f, false)
                        .boundingBox(texture.getRegionWidth() - 4, texture.getRegionHeight(), 0f, 0f)
                        .position(rect.x, rect.y)
                        .velocity(0f, 0f, 0f)
                        .texture(texture, textureStr)
                        .remove().build();
                break;
            case CIntegral:
                EntityBuilder.instance(engine)
                        .enemy(EnemyMovementType.None, EnemyAttackType.ArcTwo, null)
                        .activation(120f)
                        .attack(4, 2f, "p_succ", 80f, 80f, false, 0f)
                        .corporeal(true, 2f)
                        .color(EntityColor.CINTEGRAL_COLOR)
                        .health(5)
                        .boundingBox(8f, 16f, 0f, 0f)
                        .position(rect.x, rect.y)
                        .direction(facingRight, false)
                        .velocity(0f, 0f, 0f)
                        .texture(texture, textureStr)
                        .gravity(false, GravitySystem.GRAVITY, GravitySystem.TERMINAL_VELOCITY, true).remove().build();
                break;
            case Because:
                EntityBuilder.instance(engine)
                        .enemy(EnemyMovementType.BackAndForth, EnemyAttackType.None, null)
                        .activation(120f)
                        .attack(2, 0f, "p_because", 60f, 0f, false, 0f)
                        .trap()
                        .color(EntityColor.BECAUSE_COLOR)
                        .health(100)
                        .boundingBox(14f, 12f, 0f, 0f)
                        .position(rect.x, rect.y)
                        .direction(facingRight, false)
                        .velocity(0f, 0f, 0f)
                        .texture(texture, "e_because")
                        .gravity(false, GravitySystem.GRAVITY, GravitySystem.TERMINAL_VELOCITY, true).remove().build();
                break;
        }
    }

    public static void createMapEntity(PooledEngine engine, Resources res, MapProperties props, MapEntityType type, Rectangle rect) {
        switch (type) {
            case MovingPlatform:
                float dist = props.get("dist") == null ? 0f : props.get("dist", Float.class);
                float dx = props.get("dx") == null ? 0f : props.get("dx", Float.class);
                String textureStr = type.typeStr + MathUtils.ceil(rect.width / 8);
                TextureRegion texture = res.getTexture(textureStr);

                EntityBuilder.instance(engine)
                        .mapEntity(type, false, true)
                        .movingPlatform(dist, rect.x, rect.y, dx > 0)
                        .boundingBox(texture.getRegionWidth(), texture.getRegionHeight(), 0f, 0f)
                        .position(rect.x, rect.y)
                        .velocity(dx, 0f, 0f)
                        .texture(texture, textureStr)
                        .build();
                break;
            case TemporaryPlatform:
                TextureRegion tptexture = res.getTexture("approx");
                EntityBuilder.instance(engine)
                        .mapEntity(type, false, false)
                        .boundingBox(tptexture.getRegionWidth(), tptexture.getRegionHeight(), rect.x, rect.y)
                        .position(rect.x, rect.y)
                        .texture(tptexture, "approx")
                        .remove().build();
                break;
            case Portal:
                TextureRegion ptexture = res.getTexture("curly_brace_portal");
                float bw = ptexture.getRegionWidth() - 4f;
                float bh = ptexture.getRegionHeight() - 4f;
                int id = props.get("id", Integer.class);
                int target = props.get("target", Integer.class);

                EntityBuilder.instance(engine)
                        .mapEntity(type, false, true)
                        .portal(id, target)
                        .boundingBox(bw, bh, 0f, 0f)
                        .position(rect.x, rect.y)
                        .velocity(0f, 0f, 0f)
                        .texture(ptexture, "curly_brace_portal")
                        .build();
                break;
            case Clamp:
                String textureKey = props.get("texture") == null ? "square_bracket" : props.get("texture", String.class);
                float acceleration = props.get("accel") == null ? 144f : props.get("accel", Float.class);
                float backVelocity = props.get("backVel") == null ? 10f : props.get("backVel", Float.class);
                TextureRegion ctextureLeft = res.getTexture(textureKey + Resources.BRACKET_LEFT);
                TextureRegion ctextureRight = res.getTexture(textureKey + Resources.BRACKET_RIGHT);

                EntityBuilder.instance(engine)
                        .mapEntity(type, false, true)
                        .clamp(false, rect, acceleration, backVelocity)
                        .boundingBox(ctextureLeft.getRegionWidth(), ctextureLeft.getRegionHeight(), 0f, 0f)
                        .position(rect.x, rect.y)
                        .velocity(0f, 0f, 0f)
                        .texture(ctextureLeft, null)
                        .build();

                EntityBuilder.instance(engine)
                        .mapEntity(type, false, true)
                        .clamp(true, rect, acceleration, backVelocity)
                        .boundingBox(ctextureRight.getRegionWidth(), ctextureRight.getRegionHeight(), 0f, 0f)
                        .position(rect.x + rect.width - ctextureRight.getRegionWidth(), rect.y)
                        .velocity(0f, 0f, 0f)
                        .texture(ctextureRight, null)
                        .build();
                break;
            case HealthPack:
                TextureRegion hptexture = res.getTexture("health_pack");
                int regen = props.get("regen") == null ? 0 : props.get("regen", Integer.class);

                EntityBuilder.instance(engine)
                        .mapEntity(type, false, false)
                        .healthPack(regen)
                        .boundingBox(hptexture.getRegionWidth(), hptexture.getRegionHeight(), 0f, 0f)
                        .position(rect.x, rect.y)
                        .velocity(0f, 0f, 0f)
                        .texture(hptexture, "health_pack")
                        .remove().build();
                break;
            case Mirror:
                TextureRegion mtexture = res.getTexture("between");

                EntityBuilder.instance(engine)
                        .mapEntity(type, false, false)
                        .boundingBox(mtexture.getRegionWidth(), mtexture.getRegionHeight(), 0f, 0f)
                        .position(rect.x, rect.y)
                        .velocity(0f, 0f, 0f)
                        .texture(mtexture, "between")
                        .build();
                break;
            case GravitySwitch:
                String gstextureStr = "updownarrow";
                TextureRegion gstexture = res.getTexture(gstextureStr + Resources.TOGGLE_OFF);

                EntityBuilder.instance(engine)
                        .mapEntity(type, false, true)
                        .boundingBox(gstexture.getRegionWidth(), gstexture.getRegionHeight(), 0f, 0f)
                        .position(rect.x, rect.y)
                        .velocity(0f, 0f, 0f)
                        .texture(gstexture, gstextureStr)
                        .build();
                break;
            case SquareSwitch:
                String sstextureStr = "square_switch";
                TextureRegion sstexture = res.getTexture(sstextureStr + Resources.TOGGLE_ON);
                int targetId = props.get("targetId", Integer.class);

                EntityBuilder.instance(engine)
                        .mapEntity(type, true, true)
                        .squareSwitch(targetId)
                        .boundingBox(sstexture.getRegionWidth(), sstexture.getRegionHeight(), 0f, 0f)
                        .position(rect.x, rect.y)
                        .velocity(0f, 0f, 0f)
                        .texture(sstexture, sstextureStr)
                        .build();
                break;
            case ToggleTile:
                TextureRegion tttexture = res.getTexture("toggle_square");
                int ttid = props.get("id", Integer.class);

                EntityBuilder.instance(engine)
                        .mapEntity(type, true, true)
                        .toggleTile(ttid)
                        .boundingBox(tttexture.getRegionWidth(), tttexture.getRegionHeight(), 0f, 0f)
                        .position(rect.x, rect.y)
                        .velocity(0f, 0f, 0f)
                        .texture(tttexture, "toggle_square")
                        .build();
                break;
        }
    }

}