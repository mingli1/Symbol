package com.symbol.game.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.symbol.game.ecs.component.*;
import com.symbol.game.ecs.component.enemy.*;
import com.symbol.game.ecs.component.map.*;

public class Mapper {

    public static final ComponentMapper<PositionComponent> POS_MAPPER = ComponentMapper.getFor(PositionComponent.class);
    public static final ComponentMapper<VelocityComponent> VEL_MAPPER = ComponentMapper.getFor(VelocityComponent.class);
    public static final ComponentMapper<TextureComponent> TEXTURE_MAPPER = ComponentMapper.getFor(TextureComponent.class);
    public static final ComponentMapper<BoundingBoxComponent> BOUNDING_BOX_MAPPER = ComponentMapper.getFor(BoundingBoxComponent.class);
    public static final ComponentMapper<GravityComponent> GRAVITY_MAPPER = ComponentMapper.getFor(GravityComponent.class);
    public static final ComponentMapper<JumpComponent> JUMP_MAPPER = ComponentMapper.getFor(JumpComponent.class);
    public static final ComponentMapper<DirectionComponent> DIR_MAPPER = ComponentMapper.getFor(DirectionComponent.class);
    public static final ComponentMapper<PlayerComponent> PLAYER_MAPPER = ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<RemoveComponent> REMOVE_MAPPER = ComponentMapper.getFor(RemoveComponent.class);
    public static final ComponentMapper<ProjectileComponent> PROJ_MAPPER = ComponentMapper.getFor(ProjectileComponent.class);
    public static final ComponentMapper<HealthComponent> HEALTH_MAPPER = ComponentMapper.getFor(HealthComponent.class);
    public static final ComponentMapper<KnockbackComponent> KNOCKBACK_MAPPER = ComponentMapper.getFor(KnockbackComponent.class);
    public static final ComponentMapper<OrbitComponent> ORBIT_MAPPER = ComponentMapper.getFor(OrbitComponent.class);

    public static final ComponentMapper<EnemyComponent> ENEMY_MAPPER = ComponentMapper.getFor(EnemyComponent.class);
    public static final ComponentMapper<ActivationComponent> ACTIVATION_MAPPER = ComponentMapper.getFor(ActivationComponent.class);
    public static final ComponentMapper<CorporealComponent> CORPOREAL_MAPPER = ComponentMapper.getFor(CorporealComponent.class);
    public static final ComponentMapper<AttackComponent> ATTACK_MAPPER = ComponentMapper.getFor(AttackComponent.class);
    public static final ComponentMapper<ExplodeComponent> EXPLODE_MAPPER = ComponentMapper.getFor(ExplodeComponent.class);
    public static final ComponentMapper<TeleportComponent> TELEPORT_MAPPER = ComponentMapper.getFor(TeleportComponent.class);
    public static final ComponentMapper<LastStandComponent> LAST_STAND_MAPPER = ComponentMapper.getFor(LastStandComponent.class);
    public static final ComponentMapper<TrapComponent> TRAP_MAPPER = ComponentMapper.getFor(TrapComponent.class);

    public static final ComponentMapper<MapEntityComponent> MAP_ENTITY_MAPPER = ComponentMapper.getFor(MapEntityComponent.class);
    public static final ComponentMapper<MovingPlatformComponent> MOVING_PLATFORM_MAPPER = ComponentMapper.getFor(MovingPlatformComponent.class);
    public static final ComponentMapper<PortalComponent> PORTAL_MAPPER = ComponentMapper.getFor(PortalComponent.class);
    public static final ComponentMapper<ClampComponent> CLAMP_MAPPER = ComponentMapper.getFor(ClampComponent.class);
    public static final ComponentMapper<HealthPackComponent> HEALTH_PACK_MAPPER = ComponentMapper.getFor(HealthPackComponent.class);
    public static final ComponentMapper<ColorComponent> COLOR_MAPPER = ComponentMapper.getFor(ColorComponent.class);
    public static final ComponentMapper<SquareSwitchComponent> SQUARE_SWITCH_MAPPER = ComponentMapper.getFor(SquareSwitchComponent.class);
    public static final ComponentMapper<ToggleTileComponent> TOGGLE_TILE_MAPPER = ComponentMapper.getFor(ToggleTileComponent.class);

}