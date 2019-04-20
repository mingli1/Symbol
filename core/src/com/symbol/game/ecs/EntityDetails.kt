package com.symbol.game.ecs

import com.badlogic.gdx.graphics.g2d.TextureRegion

data class EntityDetails(
        val id: String? = null,
        val entityType: String? = null,
        val image: TextureRegion? = null,
        val description: String? = null,
        val additionalInfo: String? = null,
        var seen: Boolean = false
)