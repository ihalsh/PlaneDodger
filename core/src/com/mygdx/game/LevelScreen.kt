package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys.SPACE
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.mygdx.game.Actors.*
import com.mygdx.game.Actors.BaseActor.Companion.setWorldBounds
import com.mygdx.game.Utils.Assets.assetManager
import com.mygdx.game.Utils.Assets.labelStyle
import com.mygdx.game.Utils.Constants.Companion.STAR
import com.mygdx.game.Utils.Constants.Companion.WORLD_HEIGHT
import com.mygdx.game.Utils.Constants.Companion.WORLD_WIDTH
import ktx.app.KtxInputAdapter
import ktx.app.KtxScreen
import ktx.app.clearScreen


class LevelScreen(
        private val mainStage: Stage = Stage(),
        private val uiStage: Stage = Stage(),
        private val uiTable: Table = Table().apply {
            setFillParent(true)
            uiStage.addActor(this)
        }) : KtxScreen, KtxInputAdapter {

    //Adds plane
    private val plane: Plane by lazy { Plane(100f, 500f, mainStage) }

    private var starTimer = 0f
    private var starSpawnInterval = 4f
    private var score: Int = 0
    private val scoreLabel: Label = Label("$score", labelStyle)

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == SPACE) plane.boost()
        return true
    }

    override fun show() {

        //Handle input from everywhere
        val im = Gdx.input.inputProcessor as InputMultiplexer
        im.addProcessor(this)
        im.addProcessor(uiStage)
        im.addProcessor(mainStage)

        //world bounds
        setWorldBounds(WORLD_WIDTH, WORLD_HEIGHT)

        //Adds seamless sky, ground and plane
        Sky(0f, 0f, mainStage)
        Sky(800f, 0f, mainStage)
        Ground(0f, 0f, mainStage)
        Ground(800f, 0f, mainStage)
        plane

        //Position elements
        with(uiTable) {
            pad(10f)
            add(scoreLabel)
            row()
            add().expandY()
        }
    }

    private fun update(delta: Float) {
        // update all actors
        mainStage.act(delta)
        uiStage.act(delta)

        //spawn and stars and check for collision
        starTimer += delta
        if (starTimer > starSpawnInterval) {
            Star(800f, random(100f, 500f), mainStage)
            starTimer = 0f
        }
        BaseActor.getList(mainStage, STAR).forEach { star ->
            if (plane.overlaps(star)) {
                star.remove()
                score++
                scoreLabel.setText("$score")
            }
        }
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

