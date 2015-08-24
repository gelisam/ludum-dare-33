package com.gelisam.scage.ludumdare33

import com.gelisam.scage.ludumdare33._
import com.gelisam.scage.ludumdare33.Settings._

import com.gelisam.dylemma.frp._
import com.gelisam.scage.adjust._
import com.gelisam.scage.adjust.Predef._
import com.gelisam.scage.Sprite

import com.gelisam.dylemma.frp.Behavior

import com.github.dunnololda.scage.ScageLib._
import io.dylemma.frp._

object Main
  extends ScageScreenApp("Treasure Chest Life - Ludum Dare 33", worldSize.ix, worldSize.iy)
  with Observer
{
  init {
    Adjust.init(this)
    Sprite.init(this)
  }
  
  val resetE = EventSource[Unit]
  def reset {
    playingIntro = false
    resetE fire ()
  }
  
  val tickE = EventSource[Long]      // in milliseconds
  val timeE = tickE.map(_ / 1000.0)  // in seconds
  val timeB = Behavior.stepper(0.0, timeE)
  
  keyIgnorePause(KEY_ESCAPE, onKeyDown = {
    stop()
  })
  keyIgnorePause(KEY_SPACE, onKeyDown = {
    switchPause()
  })
  key(KEY_RETURN, onKeyDown = playIntro)
  key(KEY_E, onKeyDown = playIntro)
  key(KEY_Z, onKeyDown = playIntro)
  
  def timeMultiplier: Long =
    if (keyPressed(KEY_N)) 4
    else 1

  action {
    tickE fire (msecsFromInitWithoutPause * timeMultiplier)
    Behavior.updateBehaviors
    
    // Check for memory leaks. Enabling this will make the game super slow, but if there are
    // no leaks the amount of allocated memory should stay relatively constant.
    //System.gc()
    //val runtime = Runtime.getRuntime()
    //println(runtime.totalMemory() - runtime.freeMemory())
  }
  
  val caveBackgroundSprite = Sprite("cave.png", 4)
  val caveForegroundSprite = Sprite("cave-fg.png", 4)
  
  val messageBox = new MessageBox(timeE, timeB)
  
  val darkerSprite = Sprite("darker.png", 32)
  val darkerPos = Adjustable[Vec]("darkerPos")
  
  val titleSprite = Sprite("title.png", 3)
  val titlePos = Adjustable[Vec]("titlePos")
  
  val playIntroE = EventSource[Unit]
  
  val heroWalkSprites = Array(Sprite("hero-walk1.png", 4), Sprite("hero-walk2.png", 4))
  val heroWalkCycle = 0.1
  val walkCycleAnimation: Animation[Sprite] = Animation.math {t =>
    val period = heroWalkSprites.length * heroWalkCycle
    val index = ((t % period) / heroWalkCycle).toInt
    val adjustedIndex = Adjust.limit(0, index, heroWalkSprites.length - 1)
    heroWalkSprites(adjustedIndex)
  }
  val animatedWalkSprite: Animated[Sprite] =
    walkCycleAnimation.runAnimation(playIntroE, timeE, timeB)
  
  val heroWalkSpeed = Vec(-128, 0)
  val heroWalkDuration = 2.0
  val heroStandPos = heroWalkDuration * heroWalkSpeed
  val walkAnimation: Animation[Vec] =
    Animation.math(t => t * heroWalkSpeed).during(heroWalkDuration)
  val animatedWalkingHeroPos: Animated[Vec] =
    Animation.unit(Vec(0,0)).runAnimation(resetE, timeE, timeB) ||
    walkAnimation.runAnimation(playIntroE, timeE, timeB)
  
  val heroSprite = Sprite("hero.png", 4)
  val heroPos = Adjustable[Vec]("caveHeroPos")
  val standingAnimation: Animation[Vec] =
    Animation.unit(heroStandPos).during(0.5)
  val animatedStandingHeroPos: Animated[Vec] =
    standingAnimation.runAnimation(animatedWalkingHeroPos.stopE, timeE, timeB)
  
  val closedBoxSprite = Sprite("closed-box.png", 2)
  val openBoxSprite = Sprite("open-box.png", 2)
  val boxPos = Adjustable[Vec]("treasureChestPos")
  lazy val animatedBoxSprite: Animated[Sprite] =
    Animation.unit(closedBoxSprite).runAnimation(resetE || playIntermissionE, timeE, timeB) ||
    Animation.unit(openBoxSprite).runAnimation(animatedStandingHeroPos.stopE, timeE, timeB)
  
  animatedStandingHeroPos.stopE.foreach {_ =>
    messageBox.display("Monster-in-a-box!") {() => ()}
  }
  
  val zoomEffectAmount = 1.0
  val zoomEffectDelay = 1.0
  val zoomEffectDuration = 0.15
  val zoomEffectAnimation = Animation.math(t0 =>
    if (t0 < zoomEffectDelay) {
      0.0
    } else {
      val t1 = t0 - zoomEffectDelay
      if (t1 < zoomEffectDuration) {
        val t = t1
        t / zoomEffectDuration * zoomEffectAmount
      } else if (t1 < 2*zoomEffectDuration) {
        val t = t1 - zoomEffectDuration
        (1 - t / zoomEffectDuration) * zoomEffectAmount
      } else {
        val t = t1 - 2*zoomEffectDuration
        t / zoomEffectDuration * zoomEffectAmount
      }
    }
  ).during(zoomEffectDelay + 3*zoomEffectDuration)
  val animatedZoom =
    Animation.unit(0.0).runAnimation(resetE, timeE, timeB) ||
    zoomEffectAnimation.runAnimation(animatedStandingHeroPos.stopE, timeE, timeB)
  
  val introBlackScreenDuration = 0.25
  val animatedIntroBlackScreen: Animated[Boolean] =
    Animation.unit(false).runAnimation(resetE, timeE, timeB) ||
    Animation.unit(true).during(introBlackScreenDuration).runAnimation(animatedZoom.stopE, timeE, timeB)
  
  animatedIntroBlackScreen.stopE.foreach {_ =>
    pause
    BattleScreen.run
  }
  
  var playingIntro = false
  def playIntro {
    playingIntro = true
    playIntroE fire ()
  }
  
  
  val playIntermissionE = EventSource[Unit]
  
  val intermissionBlackScreenDuration = 0.5
  val intermissionBlackScrenAnimation: Animation[Boolean] =
    Animation.unit(true).during(intermissionBlackScreenDuration)
  val animatedIntermissionBlackScreen: Animated[Boolean] =
    Animation.unit(false).runAnimation(resetE, timeE, timeB) ||
    intermissionBlackScrenAnimation.runAnimation(playIntermissionE, timeE, timeB)
  
  animatedIntermissionBlackScreen.stopE.foreach {_ =>
    messageBox.display("Hero: \"I guess I need to grind a few more levels...\"") {() =>
      // ...
    }
  }
  
  def playIntermission {
    pauseOff
    playIntermissionE fire ()
  }
  
  def animatedBlackScreen: Boolean =
    animatedIntroBlackScreen.activeB ||
    animatedIntermissionBlackScreen.activeB
  
  render {
    if (!animatedBlackScreen) {
      if (animatedZoom.activeB) {
        globalScale = 1 + animatedZoom.toFloat
      } else {
        globalScale = 1
      }
      caveBackgroundSprite.render(Vec(192, -16))
      animatedBoxSprite.render(boxPos)
      if (playingIntro) {
        if (animatedWalkingHeroPos.activeB) {
          animatedWalkSprite.render(heroPos.value + animatedWalkingHeroPos.valueB.value)
        } else {
          heroSprite.render(heroPos.value + animatedStandingHeroPos.valueB.value)
        }
      }
      caveForegroundSprite.render(Vec(192, -16))
      if (!playingIntro) {
        darkerSprite.render(darkerPos)
        titleSprite.render(Vec(192, 0) + titlePos)
      }
      messageBox.render
    }
    
    Adjust.render(this)
    
    if (onPause) {
      darkerSprite.render(darkerPos)
      print(s"PAUSED", Vec(0,0), DARK_GRAY, align = "center")
    }
  }
}
