package com.gelisam.scage.base

import com.gelisam.scage.adjust._
import com.gelisam.scage.base._
import com.gelisam.scage.base.Settings._

import com.gelisam.dylemma.frp.Behavior

import com.github.dunnololda.scage.ScageLib._
import io.dylemma.frp._

object BouncingBall
  extends ScageScreenApp("Bouncing Ball", worldSize.ix, worldSize.iy)
  with Observer
{
  val tickE = EventSource[Long]      // in milliseconds
  val timeE = tickE.map(_ / 1000.0)  // in seconds
  val timeB = Behavior.stepper(0.0, timeE)
  
  val ball = new Ball(this, timeE, timeB)
  Adjust.init(this)
  
  keyIgnorePause(KEY_ESCAPE, onKeyDown = {
    stopApp()
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
  
  render {
    ball.render()
    Adjust.render(this)
    
    if (onPause) {
      print(s"PAUSED",        windowCenter, DARK_GRAY, align = "center")
    }
  }
}
