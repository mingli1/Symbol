package com.symbol.ecs

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.symbol.ecs.component.*
import com.symbol.ecs.component.enemy.EnemyComponent
import com.symbol.ecs.component.projectile.ProjectileComponent
import com.symbol.ecs.entity.*
import com.symbol.util.Resources

object EntityFactory {

    fun createPlayer(engine: PooledEngine, res: Resources) : Player {
        val player = Player()
        engine.addEntity(player)

        val bounds = Mapper.BOUNDING_BOX_MAPPER.get(player)
        val texture = Mapper.TEXTURE_MAPPER.get(player)
        val velocity = Mapper.VEL_MAPPER.get(player)
        val health = Mapper.HEALTH_MAPPER.get(player)

        bounds.rect.setSize(PLAYER_BOUNDS_WIDTH, PLAYER_BOUNDS_HEIGHT)
        texture.texture = res.getSingleTexture("player")
        velocity.speed = PLAYER_SPEED
        health.hp = PLAYER_HP
        health.maxHp = PLAYER_HP

        return player
    }

    fun createProjectile(engine: PooledEngine, unstoppable: Boolean, enemy: Boolean,
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
        projectileComponent.enemy = enemy
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

    fun createEnemy(engine: PooledEngine, res: Resources, type: EnemyType, rect: Rectangle, facingRight: Boolean) : Entity? {
        return when (type) {
            EnemyType.EConstant -> createBasicEnemy(engine, type, EnemyMovementType.BackAndForth, 2, rect, 25f,
                    facingRight, res.getSingleTexture("e_${type.typeStr}")!!)
            else -> null
        }
    }

    private fun createBasicEnemy(engine: PooledEngine, type: EnemyType, movementType: EnemyMovementType, hp: Int,
                                 rect: Rectangle, speed: Float, facingRight: Boolean, texture: TextureRegion) : Entity {
        val enemyComponent = engine.createComponent(EnemyComponent::class.java)
        val positionComponent = engine.createComponent(PositionComponent::class.java)
        val boundingBoxComponent = engine.createComponent(BoundingBoxComponent::class.java)
        val textureComponent = engine.createComponent(TextureComponent::class.java)
        val velocityComponent = engine.createComponent(VelocityComponent::class.java)
        val gravityComponent = engine.createComponent(GravityComponent::class.java)
        val directionComponent = engine.createComponent(DirectionComponent::class.java)
        val healthComponent = engine.createComponent(HealthComponent::class.java)
        val removeComponent = engine.createComponent(RemoveComponent::class.java)

        enemyComponent.type = type
        enemyComponent.movementType = movementType
        positionComponent.set(rect.x, rect.y)
        boundingBoxComponent.rect.setSize(rect.width, rect.height)
        textureComponent.texture = texture
        velocityComponent.speed = speed
        healthComponent.hp = hp
        healthComponent.maxHp = hp
        directionComponent.facingRight = facingRight

        val enemy = engine.createEntity()
        enemy.add(enemyComponent)
        enemy.add(positionComponent)
        enemy.add(boundingBoxComponent)
        enemy.add(textureComponent)
        enemy.add(velocityComponent)
        enemy.add(gravityComponent)
        enemy.add(directionComponent)
        enemy.add(healthComponent)
        enemy.add(removeComponent)

        engine.addEntity(enemy)
        return enemy
    }

}