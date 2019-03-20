package com.symbol.ecs.entity;

public enum EnemyType {

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
    Because("because");

    public String typeStr;

    EnemyType(String typeStr) {
        this.typeStr = typeStr;
    }

    public static EnemyType getType(String typeStr) {
        for (EnemyType type : EnemyType.values()) {
            if (type.typeStr.equals(typeStr)) return type;
        }
        return null;
    }

}