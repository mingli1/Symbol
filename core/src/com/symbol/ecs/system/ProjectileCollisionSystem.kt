package com.symbol.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.utils.Array
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.enemy.EnemyComponent
import com.symbol.ecs.component.projectile.ProjectileComponent
import com.symbol.map.MapObject

class ProjectileCollisionSystem : IteratingSystem(Family.all(ProjectileComponent::class.java).get()) {

    private var mapObjects: Array<MapObject> = Array()
    private var mapWidth: Int = 0
    private var mapHeight: Int = 0

    private lateinit var enemies: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        enemies = engine!!.getEntitiesFor(Family.all(EnemyComponent::class.java).get())
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val pj = Mapper.PROJ_MAPPER.get(entity)
        val bb = Mapper.BOUNDING_BOX_MAPPER.get(entity)
        val position = Mapper.POS_MAPPER.get(entity)
        val width = Mapper.TEXTURE_MAPPER.get(entity).texture!!.regionWidth
        val height = Mapper.TEXTURE_MAPPER.get(entity).texture!!.regionHeight
        val remove = Mapper.REMOVE_MAPPER.get(entity)
        bb.rect.setPosition(position.x + (width - bb.rect.width) / 2, position.y + (height - bb.rect.height) / 2)

        if (position.x < -mapWidth - width || position.x > mapWidth * 2 ||
                position.y < -mapHeight - height || position.y > mapHeight * 2) {
            remove.shouldRemove = true
        }
        else if (!pj.unstoppable) {
            for (mapObject in mapObjects) {
                if (bb.rect.overlaps(mapObject.bounds)) {
                    remove.shouldRemove = true
                    break
                }
            }
        }

        if (!pj.enemy) {
            for (enemy in enemies) {
                val ebb = Mapper.BOUNDING_BOX_MAPPER.get(enemy)
                if (bb.rect.overlaps(ebb.rect)) {
                    engine.removeEntity(enemy)
                    remove.shouldRemove = true
                    break
                }
            }
        }
    }

    fun setMapData(mapObjects: Array<MapObject>, mapWidth: Int, mapHeight: Int) {
        this.mapObjects.clear()
        this.mapObjects.addAll(mapObjects)
        this.mapWidth = mapWidth
        this.mapHeight = mapHeight
    }

}