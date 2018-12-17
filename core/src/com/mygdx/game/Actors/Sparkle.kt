package com.mygdx.game.Actors

import com.badlogic.gdx.scenes.scene2d.Stage
import com.mygdx.game.Utils.Assets.sparklelAnimation

class Sparkle(x: Float, y: Float, s: Stage) : BaseActor(x, y, s, sparklelAnimation) {

    override fun act(delta: Float) {
        super.act(delta)
        if (isAnimationFinished()) remove()
    }
}