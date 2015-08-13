package com.gelisam.scage.base

import com.gelisam.dylemma.frp.Behavior

import com.github.dunnololda.scage.ScageLib._
import io.dylemma.frp._

object Settings {
  val worldSize = Vec(640, 480)
  val pixelsPerSecond = 100.0
  val ballRadius = 10
}

import Settings._

object BouncingBall
  extends ScageScreenApp("Bouncing Ball", worldSize.ix, worldSize.iy)
  with Observer
{
  val tickE = EventSource[Long]      // in milliseconds
  val timeE = tickE.map(_ / 1000.0)  // in seconds
  val timeB = Behavior.stepper(0.0, timeE)
  
  lazy val timeAtStartB: Behavior[Double] = Behavior.stepper(0.0, bounceE.map(_ => timeB))
  lazy val timeSinceStartE: EventStream[Double] = timeE.map(_ - timeAtStartB)
  
  lazy val startPosB: Behavior[Vec] = Behavior.stepper(center, bounceE.map(ball => ball))
  lazy val ballPosE: EventStream[Vec] = timeSinceStartE.map(t =>
    startPosB.value + ballDirB.value * t * pixelsPerSecond
  )
  lazy val ballPosB: Behavior[Vec] = Behavior.stepper(center, ballPosE)
  
  lazy val ballDxB: Behavior[Int] = Behavior.stepper(1,
    bounceRightE.map(_ => 1) ||
    bounceLeftE.map(_ => -1)
  )
  lazy val ballDyB: Behavior[Int] = Behavior.stepper(1,
    bounceUpE.map(_ => 1) ||
    bounceDownE.map(_ => -1)
  )
  lazy val ballDirB = (ballDxB map2 ballDyB)(Vec(_,_))
  
  lazy val bounceRightE = ballPosE.filter(ball => ball.x - ballRadius < 0           && ballDxB.value == -1)
  lazy val bounceUpE    = ballPosE.filter(ball => ball.y - ballRadius < 0           && ballDyB.value == -1)
  lazy val bounceLeftE  = ballPosE.filter(ball => ball.x + ballRadius > worldSize.x && ballDxB.value ==  1)
  lazy val bounceDownE  = ballPosE.filter(ball => ball.y + ballRadius > worldSize.y && ballDyB.value ==  1)
  
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
