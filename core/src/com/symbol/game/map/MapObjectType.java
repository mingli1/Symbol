package com.symbol.game.map;

public enum MapObjectType {

    Ground("ground", true),
    Lethal("lethal", false),
    Damage("damage", false),
    Grounded("grounded", true),
    Slow("slow", true),
    PushRight("push_right", true),
    PushLeft("push_left", true),
    JumpBoost("jump_boost", true);

    public String typeStr;
    public boolean solid;

    MapObjectType(String typeStr, boolean solid) {
        this.typeStr = typeStr;
        this.solid = solid;
    }

    public static MapObjectType getType(String typeStr) {
        for (MapObjectType type : MapObjectType.values()) {
            if (type.typeStr.equals(typeStr)) return type;
        }
        return null;
    }

}