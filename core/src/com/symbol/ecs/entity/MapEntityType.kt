package com.symbol.ecs.entity

enum class MapEntityType(val typeStr: String) {

    None("none"),
    MovingPlatform("mplatform"),
    TemporaryPlatform("tplatform"),
    Portal("portal");

    companion object {
        fun getType(typeStr: String) : MapEntityType? {
            for (type in MapEntityType.values()) {
                if (type.typeStr == typeStr) return type
            }
            return null
        }
    }

}