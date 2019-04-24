package com.symbol.game.ecs

import com.badlogic.ashley.core.ComponentMapper
import com.symbol.game.ecs.component.*
import com.symbol.game.ecs.component.enemy.*
import com.symbol.game.ecs.component.map.*

object Mapper {

    val POS_MAPPER = ComponentMapper.getFor(PositionComponent::class.java)!!
    val VEL_MAPPER = ComponentMapper.getFor(VelocityComponent::class.java)!!
    val TEXTURE_MAPPER = ComponentMapper.getFor(TextureComponent::class.java)!!
    val BOUNDING_BOX_MAPPER = ComponentMapper.getFor(BoundingBoxComponent::class.java)!!
    val BOUNDING_CIRCLE_MAPPER = ComponentMapper.getFor(BoundingCircleComponent::class.java)!!
    val GRAVITY_MAPPER = ComponentMapper.getFor(GravityComponent::class.java)!!
    val JUMP_MAPPER = ComponentMapper.getFor(JumpComponent::class.java)!!
    val DIR_MAPPER = ComponentMapper.getFor(DirectionComponent::class.java)!!
    val PLAYER_MAPPER = ComponentMapper.getFor(PlayerComponent::class.java)!!
    val REMOVE_MAPPER = ComponentMapper.getFor(RemoveComponent::class.java)!!
    val PROJ_MAPPER = ComponentMapper.getFor(ProjectileComponent::class.java)!!
    val HEALTH_MAPPER = ComponentMapper.getFor(HealthComponent::class.java)!!
    val KNOCKBACK_MAPPER = ComponentMapper.getFor(KnockbackComponent::class.java)!!
    val ORBIT_MAPPER = ComponentMapper.getFor(OrbitComponent::class.java)!!
    val STATUS_EFFECT_MAPPER = ComponentMapper.getFor(StatusEffectComponent::class.java)!!
    val AFFECT_ALL_MAPPER = ComponentMapper.getFor(AffectAllComponent::class.java)!!
    val LAST_ENTITY_MAPPER = ComponentMapper.getFor(LastEntityComponent::class.java)!!

    val ENEMY_MAPPER = ComponentMapper.getFor(EnemyComponent::class.java)!!
    val ACTIVATION_MAPPER = ComponentMapper.getFor(ActivationComponent::class.java)!!
    val CORPOREAL_MAPPER = ComponentMapper.getFor(CorporealComponent::class.java)!!
    val ATTACK_MAPPER = ComponentMapper.getFor(AttackComponent::class.java)!!
    val EXPLODE_MAPPER = ComponentMapper.getFor(ExplodeComponent::class.java)!!
    val TELEPORT_MAPPER = ComponentMapper.getFor(TeleportComponent::class.java)!!
    val LAST_STAND_MAPPER = ComponentMapper.getFor(LastStandComponent::class.java)!!
    val TRAP_MAPPER = ComponentMapper.getFor(TrapComponent::class.java)!!
    val BLOCK_MAPPER = ComponentMapper.getFor(BlockComponent::class.java)!!

    val MAP_ENTITY_MAPPER = ComponentMapper.getFor(MapEntityComponent::class.java)!!
    val MOVING_PLATFORM_MAPPER = ComponentMapper.getFor(MovingPlatformComponent::class.java)!!
    val PORTAL_MAPPER = ComponentMapper.getFor(PortalComponent::class.java)!!
    val CLAMP_MAPPER = ComponentMapper.getFor(ClampComponent::class.java)!!
    val HEALTH_PACK_MAPPER = ComponentMapper.getFor(HealthPackComponent::class.java)!!
    val COLOR_MAPPER = ComponentMapper.getFor(ColorComponent::class.java)!!
    val SQUARE_SWITCH_MAPPER = ComponentMapper.getFor(SquareSwitchComponent::class.java)!!
    val TOGGLE_TILE_MAPPER = ComponentMapper.getFor(ToggleTileComponent::class.java)!!
    val FORCE_FIELD_MAPPER = ComponentMapper.getFor(ForceFieldComponent::class.java)!!
    val DAMAGE_BOOST_MAPPER = ComponentMapper.getFor(DamageBoostComponent::class.java)!!
    val MIRROR_MAPPER = ComponentMapper.getFor(MirrorComponent::class.java)!!
    val INVERT_SWITCH_MAPPER = ComponentMapper.getFor(InvertSwitchComponent::class.java)!!

}