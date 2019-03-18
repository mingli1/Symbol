package com.symbol.ecs

import com.badlogic.ashley.core.ComponentMapper
import com.symbol.ecs.component.*
import com.symbol.ecs.component.enemy.EnemyComponent
import com.symbol.ecs.component.map.*

object Mapper {

    val POS_MAPPER: ComponentMapper<PositionComponent> = ComponentMapper.getFor(PositionComponent::class.java)

    val VEL_MAPPER: ComponentMapper<VelocityComponent> = ComponentMapper.getFor(VelocityComponent::class.java)

    val TEXTURE_MAPPER: ComponentMapper<TextureComponent> = ComponentMapper.getFor(TextureComponent::class.java)

    val BOUNDING_BOX_MAPPER: ComponentMapper<BoundingBoxComponent> = ComponentMapper.getFor(BoundingBoxComponent::class.java)

    val GRAVITY_MAPPER: ComponentMapper<GravityComponent> = ComponentMapper.getFor(GravityComponent::class.java)

    val JUMP_MAPPER: ComponentMapper<JumpComponent> = ComponentMapper.getFor(JumpComponent::class.java)

    val DIR_MAPPER: ComponentMapper<DirectionComponent> = ComponentMapper.getFor(DirectionComponent::class.java)

    val PLAYER_MAPPER: ComponentMapper<PlayerComponent> = ComponentMapper.getFor(PlayerComponent::class.java)

    val REMOVE_MAPPER: ComponentMapper<RemoveComponent> = ComponentMapper.getFor(RemoveComponent::class.java)

    val PROJ_MAPPER: ComponentMapper<ProjectileComponent> = ComponentMapper.getFor(ProjectileComponent::class.java)

    val HEALTH_MAPPER: ComponentMapper<HealthComponent> = ComponentMapper.getFor(HealthComponent::class.java)

    val KNOCKBACK_MAPPER: ComponentMapper<KnockbackComponent> = ComponentMapper.getFor(KnockbackComponent::class.java)

    val ORBIT_MAPPER: ComponentMapper<OrbitComponent> = ComponentMapper.getFor(OrbitComponent::class.java)


    val ENEMY_MAPPER: ComponentMapper<EnemyComponent> = ComponentMapper.getFor(EnemyComponent::class.java)


    val MAP_ENTITY_MAPPER: ComponentMapper<MapEntityComponent> = ComponentMapper.getFor(MapEntityComponent::class.java)

    val MOVING_PLATFORM_MAPPER: ComponentMapper<MovingPlatformComponent> = ComponentMapper.getFor(MovingPlatformComponent::class.java)

    val PORTAL_MAPPER: ComponentMapper<PortalComponent> = ComponentMapper.getFor(PortalComponent::class.java)

    val CLAMP_MAPPER: ComponentMapper<ClampComponent> = ComponentMapper.getFor(ClampComponent::class.java)

    val HEALTH_PACK_MAPPER: ComponentMapper<HealthPackComponent> = ComponentMapper.getFor(HealthPackComponent::class.java)

    val COLOR_MAPPER: ComponentMapper<ColorComponent> = ComponentMapper.getFor(ColorComponent::class.java)

    val SQUARE_SWITCH_MAPPER: ComponentMapper<SquareSwitchComponent> = ComponentMapper.getFor(SquareSwitchComponent::class.java)

    val TOGGLE_TILE_MAPPER: ComponentMapper<ToggleTileComponent> = ComponentMapper.getFor(ToggleTileComponent::class.java)

}