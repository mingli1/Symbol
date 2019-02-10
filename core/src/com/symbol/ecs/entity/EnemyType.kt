package com.symbol.ecs.entity

enum class EnemyType(val typeStr: String) {

    None(""),
    EConstant("e"),
    SquareRoot("sqrt");

    companion object {
        fun getType(typeStr: String) : EnemyType? {
            for (type in EnemyType.values()) {
                if (type.typeStr == typeStr) return type
            }
            return null
        }
    }

}