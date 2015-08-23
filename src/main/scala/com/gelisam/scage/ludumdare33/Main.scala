package com.gelisam.scage.ludumdare33

import com.gelisam.scage.ludumdare33._
import com.gelisam.scage.ludumdare33.Settings._

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
  key(KEY_RETURN, onKeyDown = BattleScreen.run)
  key(KEY_E, onKeyDown = BattleScreen.run)
  key(KEY_Z, onKeyDown = BattleScreen.run)

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
  
  val heroSprite = Sprite("hero.png", 4)
  val heroPos = Adjustable[Vec]("caveHeroPos")
  
  val closedBoxSprite = Sprite("closed-box.png", 2)
  val openBoxSprite = Sprite("open-box.png", 2)
  val boxPos = Adjustable[Vec]("treasureChestPos")
  
  render {
    caveBackgroundSprite.render(Vec(192, -16))
    closedBoxSprite.render(boxPos)
    heroSprite.render(heroPos)
    caveForegroundSprite.render(Vec(192, -16))
    darkerSprite.render(darkerPos)
    titleSprite.render(Vec(192, 0) + titlePos)
    messageBox.render
    
    Adjust.render(this)
    
    if (onPause) {
      print(s"PAUSED",        windowCenter, DARK_GRAY, align = "center")
    }
  }
}
