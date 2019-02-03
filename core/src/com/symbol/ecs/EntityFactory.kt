package com.symbol.ecs

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.symbol.ecs.component.*
import com.symbol.ecs.component.projectile.ProjectileComponent
import com.symbol.ecs.entity.PLAYER_BOUNDS_HEIGHT
import com.symbol.ecs.entity.PLAYER_BOUNDS_WIDTH
import com.symbol.ecs.entity.PLAYER_SPEED
import com.symbol.ecs.entity.Player
import com.symbol.util.Resources

object EntityFactory {

    fun createPlayer(engine: PooledEngine, res: Resources) : Player {
        val player = Player()
        engine.addEntity(player)

        val bounds = Mapper.BOUNDING_BOX_MAPPER.get(player)
        val texture = Mapper.TEXTURE_MAPPER.get(player)
        val speed = Mapper.SPEED_MAPPER.get(player)

        bounds.rect.setSize(PLAYER_BOUNDS_WIDTH, PLAYER_BOUNDS_HEIGHT)
        texture.texture = res.getSingleTexture("player")
        speed.speed = PLAYER_SPEED

        return player
    }

    fun createProjectile(engine: PooledEngine, unstoppable: Boolean,
                         x: Float, y: Float, dx: Float, dy: Float, bw: Float, bh: Float,
                         texture: TextureRegion) : Entity {
        val projectileComponent = engine.createComponent(ProjectileComponent::class.java)
        val positionComponent = engine.createComponent(PositionComponent::class.java)
        val boundingBoxComponent = engine.createComponent(BoundingBoxComponent::class.java)
        val textureComponent = engine.createComponent(TextureComponent::class.java)
        val velocityComponent = engine.createComponent(VelocityComponent::class.java)
        val directionComponent = engine.createComponent(DirectionComponent::class.java)
        val removeComponent = engine.createComponent(RemoveComponent::class.java)

        projectileComponent.unstoppable = unstoppable
        positionComponent.set(x, y)
        velocityComponent.set(dx, dy)
        boundingBoxComponent.rect.setSize(bw, bh)
        textureComponent.texture = texture

        val projectile = engine.createEntity()
        projectile.add(projectileComponent)
        projectile.add(positionComponent)
        projectile.add(boundingBoxComponent)
        projectile.add(textureComponent)
        projectile.add(velocityComponent)
        projectile.add(directionComponent)
        projectile.add(removeComponent)

        engine.addEntity(projectile)
        return projectile
    }

}