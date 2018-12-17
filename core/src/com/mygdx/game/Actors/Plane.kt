package com.mygdx.game.Actors

import com.badlogic.gdx.scenes.scene2d.Stage
import com.mygdx.game.Utils.Assets.planeGreenAnimation
import com.mygdx.game.Utils.Constants.Companion.GROUND

class Plane(
        x: Float,
        y: Float,
        s: Stage) : BaseActor(x, y, s,
                    planeGreenAnimation,
                    maxSpeed = 800f,
                    acceleration = 800f, // simulate force of gravity
                    numSides = 8) {

    override fun act(delta: Float) {
        super.act(delta)
        accelerateAtAngle(270f)
        applyPhysics(delta)

        // stop plane from passing through the ground
        BaseActor.getList(stage, GROUND).forEach { g ->
            if (overlaps(g)) {
                setSpeed(0f)
                preventOverlap(g)
            }
        }

        // stop plane from moving past top of screen
        if (y + height > worldBounds.height) {
            setSpeed(0f)
            boundToWorld()
        }
    }

    fun boost() {
        setSpeed(300f)
        setMotionAngle(90f)
    }
}