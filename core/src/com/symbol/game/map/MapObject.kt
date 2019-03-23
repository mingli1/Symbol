package com.symbol.game.map

import com.badlogic.gdx.math.Rectangle

data class MapObject(val bounds: Rectangle, val type: MapObjectType = MapObjectType.Ground, val damage: Int = 0)