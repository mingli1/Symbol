package com.symbol.game.util

enum class Orientation(val typeStr: String) {
    Horizontal("h"),
    Vertical("v"),
    LeftDiagonal("ld"),
    RightDiagonal("rd");

    companion object {
        fun getType(typeStr: String) : Orientation? = Orientation.values().find { it.typeStr == typeStr }
    }

}