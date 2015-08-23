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

  action {
    tickE fire msecsFromInitWithoutPause
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
  
  val startWalkingE = EventSource[Unit]
  
  val heroWalkSprites = Array(Sprite("hero-walk1.png", 4), Sprite("hero-walk2.png", 4))
  val heroWalkCycle = 0.1
  val walkCycleAnimation: Animation[Sprite] = Animation.math {t =>
    val period = heroWalkSprites.length * heroWalkCycle
    val index = ((t % period) / heroWalkCycle).toInt
    val adjustedIndex = Adjust.limit(0, index, heroWalkSprites.length - 1)
    heroWalkSprites(adjustedIndex)
  }
  val animatedWalkSprite: Animated[Sprite] =
    walkCycleAnimation.runAnimation(startWalkingE, timeE, timeB)
  
  val heroWalkSpeed = Vec(-128, 0)
  val heroWalkDuration = 2.0
  val heroStandPos = heroWalkDuration * heroWalkSpeed
  val walkAnimation: Animation[Vec] =
    Animation.math(t => t * heroWalkSpeed).during(heroWalkDuration)
  val animatedWalkingHeroPos: Animated[Vec] =
    Animated.unit(Vec(0,0)) ||
    walkAnimation.runAnimation(startWalkingE, timeE, timeB)
  
  val heroSprite = Sprite("hero.png", 4)
  val heroPos = Adjustable[Vec]("caveHeroPos")
  val standingAnimation: Animation[Vec] =
    Animation.unit(heroStandPos).during(0.5)
  val animatedStandingHeroPos: Animated[Vec] =
    standingAnimation.runAnimation(animatedWalkingHeroPos.stopE, timeE, timeB)
  
  val closedBoxSprite = Sprite("closed-box.png", 2)
  val openBoxSprite = Sprite("open-box.png", 2)
  val boxPos = Adjustable[Vec]("treasureChestPos")
  val animatedBoxSprite: Animated[Sprite] =
    Animated.unit(closedBoxSprite) ||
    Animation.unit(openBoxSprite).runAnimation(animatedStandingHeroPos.stopE, timeE, timeB)
  
  animatedStandingHeroPos.stopE.foreach {_ =>
    messageBox.display("Monster-in-a-box!") {() =>
      BattleScreen.run
    }
  }
  
  var playingIntro = false
  def playIntro {
    playingIntro = true
    startWalkingE fire ()
  }
  
  render {
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
    
    Adjust.render(this)
    
    if (onPause) {
      darkerSprite.render(darkerPos)
      print(s"PAUSED", Vec(0,0), DARK_GRAY, align = "center")
    }
  }
}
