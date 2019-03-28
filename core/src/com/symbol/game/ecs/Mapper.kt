package com.symbol.game.ecs

import com.badlogic.ashley.core.ComponentMapper
import com.symbol.game.ecs.component.*
import com.symbol.game.ecs.component.enemy.*
import com.symbol.game.ecs.component.map.*

object Mapper {

    val POS_MAPPER: ComponentMapper<PositionComponent> = ComponentMapper.getFor(PositionComponent::class.java)
    val VEL_MAPPER: ComponentMapper<VelocityComponent> = ComponentMapper.getFor(VelocityComponent::class.java)
    val TEXTURE_MAPPER: ComponentMapper<TextureComponent> = ComponentMapper.getFor(TextureComponent::class.java)
    val BOUNDING_BOX_MAPPER: ComponentMapper<BoundingBoxComponent> = ComponentMapper.getFor(BoundingBoxComponent::class.java)
    val BOUNDING_CIRCLE_MAPPER: ComponentMapper<BoundingCircleComponent> = ComponentMapper.getFor(BoundingCircleComponent::class.java)
    val GRAVITY_MAPPER: ComponentMapper<GravityComponent> = ComponentMapper.getFor(GravityComponent::class.java)
    val JUMP_MAPPER: ComponentMapper<JumpComponent> = ComponentMapper.getFor(JumpComponent::class.java)
    val DIR_MAPPER: ComponentMapper<DirectionComponent> = ComponentMapper.getFor(DirectionComponent::class.java)
    val PLAYER_MAPPER: ComponentMapper<PlayerComponent> = ComponentMapper.getFor(PlayerComponent::class.java)
    val REMOVE_MAPPER: ComponentMapper<RemoveComponent> = ComponentMapper.getFor(RemoveComponent::class.java)
    val PROJ_MAPPER: ComponentMapper<ProjectileComponent> = ComponentMapper.getFor(ProjectileComponent::class.java)
    val HEALTH_MAPPER: ComponentMapper<HealthComponent> = ComponentMapper.getFor(HealthComponent::class.java)
    val KNOCKBACK_MAPPER: ComponentMapper<KnockbackComponent> = ComponentMapper.getFor(KnockbackComponent::class.java)
    val ORBIT_MAPPER: ComponentMapper<OrbitComponent> = ComponentMapper.getFor(OrbitComponent::class.java)
    val STATUS_EFFECT_MAPPER: ComponentMapper<StatusEffectComponent> = ComponentMapper.getFor(StatusEffectComponent::class.java)

    val ENEMY_MAPPER: ComponentMapper<EnemyComponent> = ComponentMapper.getFor(EnemyComponent::class.java)
    val ACTIVATION_MAPPER: ComponentMapper<ActivationComponent> = ComponentMapper.getFor(ActivationComponent::class.java)
    val CORPOREAL_MAPPER: ComponentMapper<CorporealComponent> = ComponentMapper.getFor(CorporealComponent::class.java)
    val ATTACK_MAPPER: ComponentMapper<AttackComponent> = ComponentMapper.getFor(AttackComponent::class.java)
    val EXPLODE_MAPPER: ComponentMapper<ExplodeComponent> = ComponentMapper.getFor(ExplodeComponent::class.java)
    val TELEPORT_MAPPER: ComponentMapper<TeleportComponent> = ComponentMapper.getFor(TeleportComponent::class.java)
    val LAST_STAND_MAPPER: ComponentMapper<LastStandComponent> = ComponentMapper.getFor(LastStandComponent::class.java)
    val TRAP_MAPPER: ComponentMapper<TrapComponent> = ComponentMapper.getFor(TrapComponent::class.java)

    val MAP_ENTITY_MAPPER: ComponentMapper<MapEntityComponent> = ComponentMapper.getFor(MapEntityComponent::class.java)
    val MOVING_PLATFORM_MAPPER: ComponentMapper<MovingPlatformComponent> = ComponentMapper.getFor(MovingPlatformComponent::class.java)
    val PORTAL_MAPPER: ComponentMapper<PortalComponent> = ComponentMapper.getFor(PortalComponent::class.java)
    val CLAMP_MAPPER: ComponentMapper<ClampComponent> = ComponentMapper.getFor(ClampComponent::class.java)
    val HEALTH_PACK_MAPPER: ComponentMapper<HealthPackComponent> = ComponentMapper.getFor(HealthPackComponent::class.java)
    val COLOR_MAPPER: ComponentMapper<ColorComponent> = ComponentMapper.getFor(ColorComponent::class.java)
    val SQUARE_SWITCH_MAPPER: ComponentMapper<SquareSwitchComponent> = ComponentMapper.getFor(SquareSwitchComponent::class.java)
    val TOGGLE_TILE_MAPPER: ComponentMapper<ToggleTileComponent> = ComponentMapper.getFor(ToggleTileComponent::class.java)
    val FORCE_FIELD_MAPPER: ComponentMapper<ForceFieldComponent> = ComponentMapper.getFor(ForceFieldComponent::class.java)

}