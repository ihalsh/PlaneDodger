package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys.SPACE
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.mygdx.game.Actors.*
import com.mygdx.game.Actors.BaseActor.Companion.setWorldBounds
import com.mygdx.game.Utils.Assets.assetManager
import com.mygdx.game.Utils.Assets.backgroundMusic
import com.mygdx.game.Utils.Assets.explosionSound
import com.mygdx.game.Utils.Assets.gameOverAnimation
import com.mygdx.game.Utils.Assets.labelStyle
import com.mygdx.game.Utils.Assets.restartButtonStyle
import com.mygdx.game.Utils.Assets.sparkleSound
import com.mygdx.game.Utils.Constants.Companion.ENEMY
import com.mygdx.game.Utils.Constants.Companion.GAME_OVER_DELAY
import com.mygdx.game.Utils.Constants.Companion.STAR
import com.mygdx.game.Utils.Constants.Companion.WORLD_HEIGHT
import com.mygdx.game.Utils.Constants.Companion.WORLD_WIDTH
import ktx.app.KtxInputAdapter
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.log.info

class LevelScreen(
        private val mainStage: Stage = Stage(ScalingViewport(Scaling.fit, WORLD_WIDTH, WORLD_HEIGHT)),
        private val uiStage: Stage = Stage(ScalingViewport(Scaling.fit, WORLD_WIDTH, WORLD_HEIGHT)),
        private val uiTable: Table = Table().apply {
            setFillParent(true)
            uiStage.addActor(this)
        },
        private var endTimer: Float = 0f) : KtxScreen, KtxInputAdapter {

    //Adds plane
    private val plane: Plane by lazy { Plane(100f, 500f, mainStage) }

    private val restartButton = Button(restartButtonStyle).apply {
        color = Color.SKY
        setOrigin(width/2, height/2)
    }

    //Star variables
    private var starTimer = 0f
    private var starSpawnInterval = 4f
    private var score: Int = 0
    private val scoreLabel: Label = Label("$score", labelStyle)

    //Enemy variables
    private var enemyTimer = 0f
    private var enemySpawnInterval = 3f
    private var enemySpeed = 100f
    private var gameOver = false
    private var gameOverMessage = BaseActor(0f, 0f, uiStage, gameOverAnimation)
            .apply { isVisible = false }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == SPACE) plane.boost()
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val x = restartButton.x + restartButton.originX
        val y = restartButton.y + restartButton.originY

        val clickWorldCoordinates =
                mainStage.viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))

//        info { "$x, $y" }
//        info { "${clickWorldCoordinates.x}, ${clickWorldCoordinates.y}" }

        val isRestartClicked =
                clickWorldCoordinates.dst(x, y) <= restartButton.width / 2

        if (isRestartClicked) restartLevel()
        plane.boost()
        return true
    }

    override fun show() {

        //Handle input from everywhere
        val im = Gdx.input.inputProcessor as InputMultiplexer
        im.addProcessor(this)
        im.addProcessor(uiStage)
        im.addProcessor(mainStage)

        //world bounds
//        setWorldBounds(mainStage.viewport.worldWidth, mainStage.viewport.worldHeight)

        Sky(0f, 0f, mainStage)
        Sky(800f, 0f, mainStage)
        Ground(0f, 0f, mainStage)
        Ground(800f, 0f, mainStage)
        plane

        //Position elements
        with(uiTable) {
            pad(10f)
            add(scoreLabel).expandX()
            add(restartButton).right()
            row().colspan(2)
            add(gameOverMessage).expandY()
        }

        //Play music
        with(backgroundMusic) {
            isLooping = true
            volume = 0.75f
            play()
        }
    }

    private fun restartLevel() {
        //Star variables
        starTimer = 0f
        starSpawnInterval = 4f
        score = 0
        scoreLabel.setText("$score")

        //Enemy variables
        enemyTimer = 0f
        enemySpawnInterval = 3f
        enemySpeed = 100f
        gameOver = false
        gameOverMessage.isVisible = false

        endTimer = 0f

        //Adds seamless sky, ground and plane

        with(mainStage) {
            clear()
            Sky(0f, 0f, this)
            Sky(800f, 0f, this)
            Ground(0f, 0f, this)
            Ground(800f, 0f, this)
            addActor(plane.apply { setPosition(100f, 500f) })
        }

        //Play music
        with(backgroundMusic) {
            stop()
            play()
        }
    }

    private fun update(delta: Float) {
        //Stops the game
        if (gameOver) {
            endTimer += delta
            if (endTimer > GAME_OVER_DELAY) return
        }

        // update all actors
        uiStage.act(delta)
        mainStage.act(delta)

        //spawn and stars and check for collision
        starTimer += delta
        if (starTimer > starSpawnInterval) {
            Star(800f, random(100f, 500f), mainStage)
            starTimer = 0f
        }
        BaseActor.getList(mainStage, STAR).forEach { star ->
            if (plane.overlaps(star)) {
                Sparkle(0f, 0f, mainStage).apply { centerAtActor(star) }
                sparkleSound.play()
                star.remove()
                score++
                scoreLabel.setText("$score")
            }
        }

        //spawn enemies
        enemyTimer += delta
        if (enemyTimer > enemySpawnInterval) {
            Enemy(800f, random(100f, mainStage.viewport.worldHeight - 100f), mainStage)
                    .apply { setSpeed(enemySpeed) }
            enemyTimer = 0f
            enemySpawnInterval -= 0.10f
            enemySpeed += 10
            if (enemySpawnInterval < 0.5f) enemySpawnInterval = 0.5f
            if (enemySpeed > 400) enemySpeed = 400f
        }
        BaseActor.getList(mainStage, ENEMY).forEach { enemy ->
            if (plane.overlaps(enemy) && !gameOver) {
                Explosion(0f, 0f, mainStage).apply {
                    centerAtActor(enemy)
                    setScale(3f)
                }
                explosionSound.play()
                backgroundMusic.stop()
                with(plane) {
                    setPosition(-100f, -100f)
                    remove()
                }
                gameOver = true
                gameOverMessage.isVisible = true
            }
            if (enemy.x + enemy.width < 0) {
                score++
                scoreLabel.setText("$score")
                enemy.remove()
            }
        }
    }

    override fun render(delta: Float) {
        clearScreen(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b)
        update(delta)
        mainStage.draw()
        uiStage.draw()
    }

    override fun resize(width: Int, height: Int) {
        mainStage.viewport.update(width, height)
        uiStage.viewport.update(width, height)
    }

    override fun dispose() {
        mainStage.clear()
        uiStage.clear()
        assetManager.dispose()
    }
}

