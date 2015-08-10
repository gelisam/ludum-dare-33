package com.gelisam.scage.base

import com.gelisam.dylemma.frp.Behavior

import com.github.dunnololda.scage.ScageLib._
import com.github.dunnololda.scage.ScageScreenApp
import com.github.dunnololda.scage.support.tracer3.{CoordTracer, DefaultTrace}
import com.github.dunnololda.scage.support.{ScageColor, Vec}
import io.dylemma.frp._

import scala.collection.mutable.ArrayBuffer

object Settings {
  val worldSize = Vec(640, 480)
  val pixelsPerSecond = 100.0
  val ballRadius = 10
}

import Settings._

object BouncingBall
  extends ScageScreenApp("Bounding Ball", worldSize.ix, worldSize.iy)
  with Observer
{
  val tickE = EventSource[Long]  // in milliseconds
  val timeB = Behavior.stepper(0.0, tickE.map(_ / 1000.0))  // in seconds
  
  lazy val timeAtStartB: Behavior[Double] = Behavior.stepper(0.0, bounceE.map(_ => timeB))
  lazy val timeSinceStartB: Behavior[Double] = (timeB map2 timeAtStartB)(_-_)
  
  lazy val startPosB: Behavior[Vec] = Behavior.stepper(center, bounceE.map(_ => ballPosB))
  lazy val ballPosB: Behavior[Vec] = Behavior.stepper(center, timeSinceStartB.changeE.map(t =>
    startPosB.value + ballDirB.value * t * pixelsPerSecond
  ))
  
  lazy val ballDxB = Behavior.stepper(1,
    bounceRightE.map(_ => 1) ||
    bounceLeftE.map(_ => -1)
  )
  lazy val ballDyB = Behavior.stepper(1,
    bounceUpE.map(_ => 1) ||
    bounceDownE.map(_ => -1)
  )
  lazy val ballDirB = (ballDxB map2 ballDyB)(Vec(_,_))
  
  lazy val bounceRightE = tickE.filter(_ => ballPosB.x - ballRadius < 0)
  lazy val bounceUpE    = tickE.filter(_ => ballPosB.y - ballRadius < 0)
  lazy val bounceLeftE  = tickE.filter(_ => ballPosB.x + ballRadius > worldSize.x)
  lazy val bounceDownE  = tickE.filter(_ => ballPosB.y + ballRadius > worldSize.y)
  lazy val bounceE = bounceRightE ||
                     bounceUpE    ||
                     bounceLeftE  ||
                     bounceDownE
  
  keyIgnorePause(KEY_ESCAPE, onKeyDown = {
    stopApp()
  })
  keyIgnorePause(KEY_SPACE, onKeyDown = {
    switchPause()
  })

  action {
    tickE fire msecsFromInitWithoutPause
    Behavior.updateBehaviors
  }
  render {
    drawFilledCircle(ballPosB, ballRadius, ScageColor.RED)
    if (onPause) {
      print(s"PAUSED",        windowCenter, DARK_GRAY, align = "center")
    }
  }
}
