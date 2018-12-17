package com.mygdx.game.Actors

import com.badlogic.gdx.scenes.scene2d.Stage
import com.mygdx.game.Utils.Assets.explosionAnimation

class Explosion(x: Float, y: Float, s: Stage) : BaseActor(x, y, s, explosionAnimation) {

    override fun act(delta: Float) {
        super.act(delta)
        if (isAnimationFinished()) remove()
    }
}