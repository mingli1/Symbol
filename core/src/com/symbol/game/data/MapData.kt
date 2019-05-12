package com.symbol.game.data

data class MapData(
        val id: Int,
        val name: String? = null,
        val stats: MapStatistics = MapStatistics(),
        var completed: Boolean = false
)

data class MapStatistics(
        var numDeaths: Int = 0
)