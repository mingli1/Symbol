package com.symbol.game.ecs.entity;

public enum MapEntityType {

    None("none"),
    MovingPlatform("mplatform"),
    TemporaryPlatform("tplatform"),
    Portal("portal"),
    Clamp("clamp"),
    HealthPack("hp_pack"),
    Mirror("mirror"),
    GravitySwitch("gswitch"),
    SquareSwitch("sswitch"),
    ToggleTile("toggle");

    public String typeStr;

    MapEntityType(String typeStr) {
        this.typeStr = typeStr;
    }

    public static MapEntityType getType(String typeStr) {
        for (MapEntityType type : MapEntityType.values()) {
            if (type.typeStr.equals(typeStr)) return type;
        }
        return null;
    }

}