package com.symbol.game.map

enum class MapObjectType(val typeStr: String, val solid: Boolean) {

    Ground("ground", true),
    Lethal("lethal", false),
    Damage("damage", false),
    Grounded("grounded", true),
    Slow("slow", true),
    PushRight("push_right", true),
    PushLeft("push_left", true),
    JumpBoost("jump_boost", true);

    companion object {
        fun getType(typeStr: String) : MapObjectType? = MapObjectType.values().find { it.typeStr == typeStr }
    }

}