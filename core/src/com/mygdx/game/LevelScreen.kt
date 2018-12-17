package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.mygdx.game.Utils.Assets.assetManager
import ktx.app.KtxInputAdapter
import ktx.app.KtxScreen
import ktx.app.clearScreen
import com.mygdx.game.Actors.Ground
import com.mygdx.game.Actors.Sky

class LevelScreen(
        private val mainStage: Stage = Stage(),
        private val uiStage: Stage = Stage(),
        private val uiTable: Table = Table().apply {
            setFillParent(true)
            uiStage.addActor(this)
        }) : KtxScreen, KtxInputAdapter {


    override fun show() {

        //Handle input from everywhere
        val im = Gdx.input.inputProcessor as InputMultiplexer
        im.addProcessor(this)
        im.addProcessor(uiStage)
        im.addProcessor(mainStage)

        //Adds seamless sky and ground
        Sky(0f, 0f, mainStage)
        Sky(800f, 0f, mainStage)
        Ground(0f, 0f, mainStage)
        Ground(800f, 0f, mainStage)

        //world bounds
//        setWorldBounds(baseActor = ocean)

    }

    private fun update(delta: Float) {
        // update all actors
        mainStage.act(delta)
        uiStage.act(delta)
    }

    override fun render(delta: Float) {
        clearScreen(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b)
        update(delta)
        mainStage.draw()
        uiStage.draw()
    }

    override fun dispose() {
        mainStage.clear()
        uiStage.clear()
        assetManager.dispose()
    }
}

