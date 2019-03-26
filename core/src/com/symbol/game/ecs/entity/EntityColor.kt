package com.symbol.game.ecs.entity

object EntityColor {

    const val PLAYER_COLOR = "ffd700ff"
    const val E_COLOR = PLAYER_COLOR
    const val BETWEEN_COLOR = "b2bdffff"
    const val SUM_COLOR = "a477ffff"
    const val BIG_PI_COLOR = SUM_COLOR
    const val LDOTS_COLOR = "40aa00ff"
    const val PORTAL_COLOR = "b8ccefff"
    const val SQUARE_BRACKET_COLOR = "ff5400ff"
    const val XOR_COLOR = "c1ff56ff"
    const val DOT_XOR_COLOR = XOR_COLOR
    const val NATURAL_JOIN_COLOR = "5fd0d8ff"
    const val NABLA_COLOR = "fff050ff"
    const val SQRT_COLOR = "8ace8bff"
    const val CUP_COLOR = "ff75acff"
    const val ALPHA_COLOR = "bc49ffff"
    const val BIG_OMEGA_COLOR = "ff4f7dff"
    const val EXISTS_COLOR = "ff9151ff"
    const val IMPLIES_COLOR = "ffbd26ff"
    const val LARGE_TRIANGLE_COLOR = "dcc9ffff"
    const val BIG_LL_COLOR = LARGE_TRIANGLE_COLOR
    const val IN_COLOR = "b5ef51ff"
    const val LTIMES_COLOR = "69dbe5ff"
    const val PERCENT_COLOR = "ff954fff"
    const val PERCENT_ORBIT_COLOR = PERCENT_COLOR
    const val ARROW_COLOR = "3d40ffff"
    const val THETA_COLOR = "77cee5ff"
    const val ANGLE_BRACKET_COLOR = "a0e5ffff"
    const val BIG_PHI_COLOR = "aa0fffff"
    const val DOT_COLOR = "ff8795ff"
    const val CINTEGRAL_COLOR = "9bffd5ff"
    const val SUCC_COLOR = "b5ffdeff"
    const val BECAUSE_COLOR = "59d177ff"
    const val BECAUSE_PROJ_COLOR = "b1d1b9ff"
    const val DOT2_COLOR = "ef73d2ff"
    const val DOT3_COLOR = "ce59e0ff"
    const val DOT4_COLOR = "9361ddff"

    fun getProjectileColor(key: String?) : String? {
        return when (key) {
            "p_dot_xor" -> DOT_XOR_COLOR
            "p_angle_bracket" -> ANGLE_BRACKET_COLOR
            "p_xor" -> XOR_COLOR
            "p_arrow" -> ARROW_COLOR
            "p_cup" -> CUP_COLOR
            "p_implies" -> IMPLIES_COLOR
            "p_ldots" -> LDOTS_COLOR
            "p_large_triangle" -> LARGE_TRIANGLE_COLOR
            "p_big_ll" -> BIG_LL_COLOR
            "p_ltimes" -> LTIMES_COLOR
            "p_alpha" -> ALPHA_COLOR
            "p_succ" -> SUCC_COLOR
            "p_because" -> BECAUSE_PROJ_COLOR
            "p_dot" -> DOT_COLOR
            "p_dot2" -> DOT2_COLOR
            "p_dot3" -> DOT3_COLOR
            "p_dot4" -> DOT4_COLOR
            else -> null
        }
    }

}