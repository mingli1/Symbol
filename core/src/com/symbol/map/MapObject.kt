package com.symbol.map

import com.badlogic.gdx.math.Rectangle

data class MapObject(val bounds: Rectangle, val type: MapObjectType = MapObjectType.Ground)