package com.mygdx.game.Actors

import com.badlogic.gdx.scenes.scene2d.Stage
import com.mygdx.game.Utils.Assets.skyAnimation

class Sky(x: Float, y: Float, s: Stage) : BaseActor(x, y, s, animation = skyAnimation) {

    init {
        setSpeed(25f)
        setMotionAngle(180f)
    }

    override fun act(delta: Float) {
        super.act(delta)
        applyPhysics(delta)
        // if moved completely past left edge of screen:
        // shift right, past other instance.
        if (x + width < 0) moveBy(2 * width, 0f)
    }
}