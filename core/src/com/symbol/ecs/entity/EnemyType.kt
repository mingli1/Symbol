package com.symbol.ecs.entity

enum class EnemyType(val typeStr: String) {

    None(""),
    EConstant("e"),
    SquareRoot("sqrt"),
    Exists("exists"),
    Summation("sum"),
    BigPi("big_pi"),
    In("in"),
    Theta("theta"),
    BigOmega("big_omega"),
    NaturalJoin("njoin"),
    BigPhi("big_phi"),
    Percent("percent"),
    PercentOrbit("percent_orbit");

    companion object {
        fun getType(typeStr: String) : EnemyType? {
            for (type in EnemyType.values()) {
                if (type.typeStr == typeStr) return type
            }
            return null
        }
    }

}

enum class EnemyAttackType {

    None,
    ShootOne,
    ShootTwoHorizontal,
    ShootTwoVertical,
    ShootFour,
    ShootFourDiagonal,
    ShootEight,
    ShootAtPlayer,
    SprayThree,
    ShootAndQuake

}

enum class EnemyMovementType {

    None,
    BackAndForth,
    Charge,
    Random,
    Orbit

}