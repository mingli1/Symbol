package com.symbol.game.input

interface KeyInputHandler {

    fun move(right: Boolean)

    fun stop(right: Boolean)

    fun jump()

    fun startCharge()

    fun endCharge()

}