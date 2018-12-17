package com.mygdx.game.Utils

import com.mygdx.game.Actors.Ground
import com.mygdx.game.Actors.Star

class Constants {

    companion object {
        //General
        const val WORLD_WIDTH = 800f
        const val WORLD_HEIGHT = 600f

        val GROUND = Ground::class.qualifiedName.toString()
        val STAR = Star::class.qualifiedName.toString()
    }
}