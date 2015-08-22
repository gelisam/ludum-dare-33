package com.gelisam.scage.ludumdare33

import com.gelisam.scage.ludumdare33.Settings._

import com.gelisam.dylemma.frp.Behavior
import com.gelisam.scage.adjust._
import com.gelisam.scage.adjust.Predef._
import com.gelisam.scage.Sound

import com.github.dunnololda.scage.handlers.Renderer
import com.github.dunnololda.scage.ScageLib._
import io.dylemma.frp._

class Ball(
  scage: Renderer,
  timeE: EventStream[Double],
  timeB: Behavior[Double]
)(implicit observer: Observer) {
  lazy val timeAtStartB: Behavior[Double] = Behavior.stepper(0.0, bounceE.map(_ => timeB))
  lazy val timeSinceStartE: EventStream[Double] = timeE.map(_ - timeAtStartB)
  
  lazy val startPosB: Behavior[Vec] = Behavior.stepper(scage.center, bounceE.map(ball => ball))
  lazy val ballPosE: EventStream[Vec] = timeSinceStartE.map(t =>
    startPosB.value + ballDirB.value * t * pixelsPerSecond
  )
  lazy val ballPosB: Behavior[Vec] = Behavior.stepper(scage.center, ballPosE)
  
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
  
  val pixelsPerSecond = Adjustable[Double]("pixelsPerSecond")
  val ballColor = Adjustable[ScageColor]("ballColor")
  def render() {
    drawFilledCircle(ballPosB, ballRadius, ballColor)
  }
  
  val bop = Sound("media/bop.wav")
  bounceE.foreach(_ => bop.play)
}
