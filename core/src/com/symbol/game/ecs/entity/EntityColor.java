package com.symbol.game.ecs.entity;

public class EntityColor {

    public static final String PLAYER_COLOR = "ffd700ff";
    public static final String E_COLOR = PLAYER_COLOR;
    public static final String BETWEEN_COLOR = "b2bdffff";
    public static final String SUM_COLOR = "a477ffff";
    public static final String BIG_PI_COLOR = SUM_COLOR;
    public static final String LDOTS_COLOR = "40aa00ff";
    public static final String PORTAL_COLOR = "b8ccefff";
    public static final String SQUARE_BRACKET_COLOR = "ff5400ff";
    public static final String XOR_COLOR = "c1ff56ff";
    public static final String DOT_XOR_COLOR = XOR_COLOR;
    public static final String NATURAL_JOIN_COLOR = "5fd0d8ff";
    public static final String NABLA_COLOR = "fff050ff";
    public static final String SQRT_COLOR = "8ace8bff";
    public static final String CUP_COLOR = "ff75acff";
    public static final String ALPHA_COLOR = "bc49ffff";
    public static final String BIG_OMEGA_COLOR = "ff4f7dff";
    public static final String EXISTS_COLOR = "ff9151ff";
    public static final String IMPLIES_COLOR = "ffbd26ff";
    public static final String LARGE_TRIANGLE_COLOR = "dcc9ffff";
    public static final String BIG_LL_COLOR = LARGE_TRIANGLE_COLOR;
    public static final String IN_COLOR = "b5ef51ff";
    public static final String LTIMES_COLOR = "69dbe5ff";
    public static final String PERCENT_COLOR = "ff954fff";
    public static final String PERCENT_ORBIT_COLOR = PERCENT_COLOR;
    public static final String ARROW_COLOR = "3d40ffff";
    public static final String THETA_COLOR = "77cee5ff";
    public static final String ANGLE_BRACKET_COLOR = "a0e5ffff";
    public static final String BIG_PHI_COLOR = "aa0fffff";
    public static final String DOT_COLOR = "ff8795ff";
    public static final String CINTEGRAL_COLOR = "9bffd5ff";
    public static final String SUCC_COLOR = "b5ffdeff";
    public static final String BECAUSE_COLOR = "59d177ff";
    public static final String BECAUSE_PROJ_COLOR = "b1d1b9ff";

    public static String getProjectileColor(String key) {
        if (key.equals("p_dot_xor")) return DOT_XOR_COLOR;
        if (key.equals("p_angle_bracket")) return ANGLE_BRACKET_COLOR;
        if (key.equals("p_xor")) return XOR_COLOR;
        if (key.equals("p_arrow")) return ARROW_COLOR;
        if (key.equals("p_cup")) return CUP_COLOR;
        if (key.equals("p_implies")) return IMPLIES_COLOR;
        if (key.equals("p_ldots")) return LDOTS_COLOR;
        if (key.equals("p_large_triangle")) return LARGE_TRIANGLE_COLOR;
        if (key.equals("p_big_ll")) return BIG_LL_COLOR;
        if (key.equals("p_ltimes")) return LTIMES_COLOR;
        if (key.equals("p_alpha")) return ALPHA_COLOR;
        if (key.equals("p_succ")) return SUCC_COLOR;
        if (key.equals("p_because")) return BECAUSE_PROJ_COLOR;
        return null;
    }

}