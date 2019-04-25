package com.symbol.game.ecs.entity

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
    PercentOrbit("percent_orbit"),
    Nabla("nabla"),
    CIntegral("cintegral"),
    Because("because"),
    Block("block");

    companion object {
        fun getType(typeStr: String) : EnemyType? = values().find { it.typeStr == typeStr }
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
    ShootAndQuake,
    Random,
    ArcTwo,
    HorizontalWave,
    VerticalWave,
    TwoHorizontalWave,
    TwoVerticalWave,
    FourWave,
    ShootBoomerang,
    ShootHoming

}

enum class EnemyMovementType {

    None,
    BackAndForth,
    Charge,
    Random,
    RandomWithJump,
    Orbit,
    TeleportTriangle,
    TeleportSquare

}