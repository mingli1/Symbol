package com.symbol.input

interface KeyInputHandler {

    fun move(right: Boolean)

    fun stop(right: Boolean)

    fun jump()

    fun shoot(keyDown: Boolean)

}