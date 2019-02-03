package com.symbol.ecs.component.projectile

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ProjectileComponent : Component, Pool.Poolable {
    override fun reset() {}
}