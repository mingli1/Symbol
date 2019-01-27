package com.symbol.ecs

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.symbol.ecs.component.*
import com.symbol.ecs.component.player.PlayerComponent

object EntityFactory {

    fun createPlayer(engine: PooledEngine, position: Vector2, bounds: Rectangle,
                     texture: TextureRegion, speed: Float) : Entity {
        val playerComponent = engine.createComponent(PlayerComponent::class.java)
        val positionComponent = engine.createComponent(PositionComponent::class.java)
        val prevPositionComponent = engine.createComponent(PreviousPositionComponent::class.java)
        val gravityComponent = engine.createComponent(GravityComponent::class.java)
        val boundingBoxComponent = engine.createComponent(BoundingBoxComponent::class.java)
        val textureComponent = engine.createComponent(TextureComponent::class.java)
        val velocityComponent = engine.createComponent(VelocityComponent::class.java)
        val speedComponent = engine.createComponent(SpeedComponent::class.java)

        positionComponent.x = position.x
        positionComponent.y = position.y
        prevPositionComponent.x = position.x
        prevPositionComponent.y = position.y
        boundingBoxComponent.rect = bounds
        textureComponent.texture = texture
        speedComponent.speed = speed

        val entity = engine.createEntity()

        entity.add(playerComponent)
        entity.add(positionComponent)
        entity.add(prevPositionComponent)
        entity.add(gravityComponent)
        entity.add(boundingBoxComponent)
        entity.add(textureComponent)
        entity.add(velocityComponent)
        entity.add(speedComponent)

        engine.addEntity(entity)
        return entity
    }

}