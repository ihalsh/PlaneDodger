package com.mygdx.game.Actors

import com.badlogic.gdx.scenes.scene2d.Stage
import com.mygdx.game.Utils.Assets.planeRedAnimation

class Enemy(
        x: Float,
        y: Float,
        s: Stage) : BaseActor(x, y, s,
        planeRedAnimation,
        numSides = 8) {

    init {
        setSpeed(100f)
        setMotionAngle(180f)
    }

    override fun act(delta: Float) {
        super.act(delta)
        applyPhysics(delta)
    }
}