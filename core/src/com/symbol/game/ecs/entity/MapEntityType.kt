package com.symbol.game.ecs.entity

enum class MapEntityType(val typeStr: String) {

    None("none"),
    MovingPlatform("mplatform"),
    TemporaryPlatform("tplatform"),
    Portal("portal"),
    Clamp("clamp"),
    HealthPack("hp_pack"),
    Mirror("mirror"),
    GravitySwitch("gswitch"),
    SquareSwitch("sswitch"),
    ToggleTile("toggle"),
    ForceField("force_field"),
    DamageBoost("damage_boost"),
    InvertSwitch("iswitch");

    companion object {
        fun getType(typeStr: String) : MapEntityType? = values().find { it.typeStr == typeStr }
    }

}