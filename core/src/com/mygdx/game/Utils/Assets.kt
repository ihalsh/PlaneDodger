package com.mygdx.game.Utils

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetErrorListener
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color.*
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.TimeUtils.millis
import com.badlogic.gdx.utils.TimeUtils.timeSinceMillis
import ktx.log.info

object Assets : Disposable, AssetErrorListener {

    val assetManager: AssetManager = AssetManager().apply {
        setErrorListener(Assets)
        setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(InternalFileHandleResolver()))
    }

    init {
        val startTime = millis()
        with(assetManager) {
            //            logger = com.badlogic.gdx.utils.Logger("AssetManager", com.badlogic.gdx.Application.LOG_INFO)
            load("button.png", Texture::class.java)
            load("dialog.png", Texture::class.java)
            load("game-over.png", Texture::class.java)
            load("ground.png", Texture::class.java)
            load("key-blank.png", Texture::class.java)
            load("sky.png", Texture::class.java)
            load("star.png", Texture::class.java)
            load("planeGreen0.png", Texture::class.java)
            load("planeGreen1.png", Texture::class.java)
            load("planeGreen2.png", Texture::class.java)
            load("planeRed0.png", Texture::class.java)
            load("planeRed1.png", Texture::class.java)
            load("planeRed2.png", Texture::class.java)
            load("explosion.png", Pixmap::class.java)
            load("sparkle.png", Pixmap::class.java)
            load("OpenSans.ttf", FreeTypeFontGenerator::class.java)
            load("explosion.wav", Sound::class.java)
            load("sparkle.mp3", Sound::class.java)
            load("Prelude-and-Action.mp3", Music::class.java)
            finishLoading()
        }
        info { "Assets loading time: ${timeSinceMillis(startTime)} milliseconds" }
    }

    private val ktxLogger = ktx.log.logger<Assets>()

    //Music and sound
    val explosionSound: Sound by lazy { assetManager.get<Sound>("explosion.wav") }
    val sparkleSound: Sound by lazy { assetManager.get<Sound>("sparkle.mp3") }
    val actionTheme: Music by lazy { assetManager.get<Music>("Prelude-and-Action.mp3") }

    //Label style
    private val customFont = assetManager.get<FreeTypeFontGenerator>("OpenSans.ttf")
            .generateFont(FreeTypeFontParameter().apply {
                size = 40
                color = WHITE
                borderWidth = 2f
                borderColor = BLACK
                borderStraight = true
                minFilter = Linear
                magFilter = Linear
            })
    val labelStyle: LabelStyle = LabelStyle().apply {
        font = customFont
    }

    //Image button style
//    val restartButtonStyle = ButtonStyle()
//            .apply { up = TextureRegionDrawable(TextureRegion(assetManager.get<Texture>("undo.png"))) }
//
//    val muteButtonStyle = ButtonStyle()
//            .apply { up = TextureRegionDrawable(TextureRegion(assetManager.get<Texture>("audio.png"))) }

    //Text button style
//    val textButtonStyle = TextButtonStyle().apply {
//        up = NinePatchDrawable(NinePatch(assetManager.get<Texture>("button.png"),
//                24,
//                24,
//                24,
//                24
//        ))
//        font = customFont
//        fontColor = GRAY
//    }

    //Single Texture Animation
    val skyAnimation: Animation<TextureRegion> by lazy {
        Animation(1f, TextureRegion(assetManager.get<Texture>("sky.png")))
                .apply { playMode = LOOP }
    }

    val groundAnimation: Animation<TextureRegion> by lazy {
        Animation(1f, TextureRegion(assetManager.get<Texture>("ground.png")))
                .apply { playMode = LOOP }
    }

    val starAnimation: Animation<TextureRegion> by lazy {
        Animation(1f, TextureRegion(assetManager.get<Texture>("star.png")))
                .apply { playMode = LOOP }
    }

    val gameOverAnimation: Animation<TextureRegion> by lazy {
        Animation(1f, TextureRegion(assetManager.get<Texture>("game-over.png")))
                .apply { playMode = LOOP }
    }

    //Load Animation From Files
    val planeGreenAnimation: Animation<TextureRegion> by lazy {
        val planeGreenTextures = Array<TextureRegion>().also {
            it.size = 3
            for (i in 0..2) it[i] = TextureRegion(assetManager.get<Texture>("planeGreen$i.png"))
        }
        Animation(0.1f, planeGreenTextures, LOOP)
    }

    val planeRedAnimation: Animation<TextureRegion> by lazy {
        val planeRedTextures = Array<TextureRegion>().also {
            it.size = 3
            for (i in 0..2) it[i] = TextureRegion(assetManager.get<Texture>("planeRed$i.png"))
        }
        Animation(0.1f, planeRedTextures, LOOP)
    }

    //Load Animation From Sheet
//    val whirlpoolAnimation: Animation<TextureRegion> by lazy {
//        val texture = Texture(assetManager.get<Pixmap>("whirlpool.png"), true)
//                .apply { setFilter(Linear, Linear) }
//
//        val tempArray = TextureRegion.split(texture,
//                texture.width / 5 /*cols*/,
//                texture.height / 2 /*rows*/)
//
//        val textureArray = Array<TextureRegion>()
//        for (r in 0 until 2)
//            for (c in 0 until 5)
//                textureArray.add(tempArray[r][c])
//
//        Animation(0.1f, textureArray, NORMAL)
//    }

    override fun dispose() {
        info { "Assets disposed...Ok" }
        assetManager.dispose()
    }

    override fun error(asset: AssetDescriptor<*>, throwable: Throwable) {
        ktxLogger.error(throwable) { "Couldn't load asset: ${asset.fileName}" }
    }
}
