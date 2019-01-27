package com.symbol.ecs

import com.badlogic.ashley.core.ComponentMapper
import com.symbol.ecs.component.*

object Mapper {

    val POS_MAPPER: ComponentMapper<PositionComponent> = ComponentMapper.getFor(PositionComponent::class.java)

    val PREV_POS_MAPPER: ComponentMapper<PreviousPositionComponent> = ComponentMapper.getFor(PreviousPositionComponent::class.java)

    val VEL_MAPPER: ComponentMapper<VelocityComponent> = ComponentMapper.getFor(VelocityComponent::class.java)

    val TEXTURE_MAPPER: ComponentMapper<TextureComponent> = ComponentMapper.getFor(TextureComponent::class.java)

    val BOUNDING_BOX_MAPPER: ComponentMapper<BoundingBoxComponent> = ComponentMapper.getFor(BoundingBoxComponent::class.java)

    val SPEED_MAPPER: ComponentMapper<SpeedComponent> = ComponentMapper.getFor(SpeedComponent::class.java)

}