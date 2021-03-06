package com.symbol.game.data

import com.badlogic.gdx.graphics.g2d.TextureRegion

data class EntityDetails(
        val id: String? = null,
        val name: String? = null,
        val entityType: String? = null,
        val image: TextureRegion? = null,
        val description: String? = null,
        val additionalInfo: String? = null
)