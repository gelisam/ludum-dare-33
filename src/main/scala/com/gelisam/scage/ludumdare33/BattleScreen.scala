package com.gelisam.scage.ludumdare33

import com.gelisam.scage.ludumdare33._
import com.gelisam.scage.ludumdare33.Settings._

import com.gelisam.scage.adjust._
import com.gelisam.scage.adjust.Predef._
import com.gelisam.scage.Sprite

import com.gelisam.dylemma.frp.Behavior

import com.github.dunnololda.scage.ScageLib._
import io.dylemma.frp._

object BattleScreen
  extends ScageScreen("Battle screen")
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

  action {
    tickE fire msecsFromInitWithoutPause
    Behavior.updateBehaviors
    
    // Check for memory leaks. Enabling this will make the game super slow, but if there are
    // no leaks the amount of allocated memory should stay relatively constant.
    //System.gc()
    //val runtime = Runtime.getRuntime()
    //println(runtime.totalMemory() - runtime.freeMemory())
  }
  
  val fightBackgroundSprite = Sprite("fight-background.png", 4)
  val fightBackgroundPos = Adjustable[Vec]("fightBackgroundPos")
  
  val monster = new Monster(timeE, timeB)
  val hero = new Hero(timeE, timeB)
  
  var battle = Battle(monster, hero)
  val hpBar = new HpBar(monster, hero)
  val menu = new Menu(this, monster, hero)
  val messageBox = new MessageBox(timeE, timeB)
  
  render {
    fightBackgroundSprite.render(Vec(192, -16) + fightBackgroundPos)
    monster.render
    hero.render
    hpBar.render
    menu.render
    messageBox.render
    
    Adjust.render(this)
    
    if (onPause) {
      print(s"PAUSED",        windowCenter, DARK_GRAY, align = "center")
    }
  }
}
