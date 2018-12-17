package com.mygdx.game.Actors

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.mygdx.game.Utils.Assets.starAnimation

class Star(
        x: Float,
        y: Float,
        s: Stage) : BaseActor(x, y, s,
        starAnimation,
        numSides = 5) {

    init {
        val pulse = sequence(
                scaleTo(1.2f, 1.2f, 0.5f),
                scaleTo(1.0f, 1.0f, 0.5f))
        addAction(forever(pulse))
        setSpeed(100f)
        setMotionAngle(180f)

        // set collision polygon
        val vertices = FloatArray(2 * numSides)
        for (i in 0 until numSides) {
            val angle = i * 6.28f / numSides - 0.95f
            // x-coordinate
            vertices[2 * i] = width / 2 * MathUtils.cos(angle) + width / 2
            // y-coordinate
            vertices[2 * i + 1] = height / 2 * MathUtils.sin(angle) + height / 2
        }
        boundaryPolygon.apply {
            setVertices(vertices)
            setPosition(x, y)
        }
    }

    override fun act(delta: Float) {
        super.act(delta)
        applyPhysics(delta)
        // remove after moving past left edge of screen
        if (x + width < 0)
            remove()
    }
}