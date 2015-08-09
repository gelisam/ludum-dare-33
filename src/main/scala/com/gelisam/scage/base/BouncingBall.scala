package com.gelisam.scage.base

import com.github.dunnololda.scage.ScageLib._
import com.github.dunnololda.scage.ScageScreenApp
import com.github.dunnololda.scage.support.tracer3.{CoordTracer, DefaultTrace}
import com.github.dunnololda.scage.support.{ScageColor, Vec}
import io.dylemma.frp._

import scala.collection.mutable.ArrayBuffer

object Settings {
  val worldSize = Vec(640, 480)
  val pixelsPerSecond = 500.0
  val ballRadius = 10
}

object BouncingBall extends ScageScreenApp("Bounding Ball", Settings.worldSize.ix, Settings.worldSize.iy) {
  var ballPos = Settings.worldSize / 2
  var ballDir = Vec(1,1)
  
  keyIgnorePause(KEY_ESCAPE, onKeyDown = {
    stopApp()
  })
  keyIgnorePause(KEY_SPACE, onKeyDown = {
    switchPause()
  })

  var lastTimestamp = 0L
  init {
    lastTimestamp = System.currentTimeMillis
  }
  action {
    val now = System.currentTimeMillis
    val seconds = (now - lastTimestamp).toDouble / 1000
    lastTimestamp = now
    
    val pixels = Settings.pixelsPerSecond * seconds
    ballPos += pixels * ballDir
    
    if (ballPos.x - Settings.ballRadius < 0)
      ballDir = ballDir.copy(x = 1)
    if (ballPos.y - Settings.ballRadius < 0)
      ballDir = ballDir.copy(y = 1)
    if (ballPos.x + Settings.ballRadius > Settings.worldSize.x)
      ballDir = ballDir.copy(x = -1)
    if (ballPos.y + Settings.ballRadius > Settings.worldSize.y)
      ballDir = ballDir.copy(y = -1)
  }
  render {
    drawFilledCircle(ballPos, Settings.ballRadius, ScageColor.RED)
    if (onPause) {
      print(s"PAUSED",        windowCenter, DARK_GRAY, align = "center")
    }
  }
}
