package com.symbol.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.TextureComponent

class RenderSystem(private val batch: Batch) : IteratingSystem(
    Family.all(TextureComponent::class.java).get()
) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val texture = Mapper.TEXTURE_MAPPER.get(entity)
        val position = Mapper.POS_MAPPER.get(entity)

        batch.draw(texture.texture, position.x, position.y)
    }

}