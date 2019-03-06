package com.symbol.map

enum class MapObjectType(val typeStr: String, val solid: Boolean) {

    Ground("ground", true),
    Lethal("lethal", false),
    Damage("damage", false);

    companion object {
        fun getType(typeStr: String) : MapObjectType? {
            for (type in MapObjectType.values()) {
                if (type.typeStr == typeStr) return type
            }
            return null
        }
    }

}