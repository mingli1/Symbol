package com.symbol.map

enum class MapObjectType(val typeStr: String) {

    Ground("ground"),
    Lethal("lethal"),
    KnockbackDamage("kbd");

    companion object {
        fun getType(typeStr: String) : MapObjectType? {
            for (type in MapObjectType.values()) {
                if (type.typeStr == typeStr) return type
            }
            return null
        }
    }

}