package com.gelisam.scage.base

import com.github.dunnololda.scage.ScageLib._
import com.github.dunnololda.scage.ScageScreenApp
import com.github.dunnololda.scage.support.tracer3.{CoordTracer, DefaultTrace}
import com.github.dunnololda.scage.support.{ScageColor, Vec}

import scala.collection.mutable.ArrayBuffer

object BouncingBall extends ScageScreenApp("Bounding Ball", 640, 480) {
  init {
  }
  action(10) {
  }
  keyIgnorePause(KEY_SPACE, onKeyDown = {
    switchPause()
  })

  render(-10) {
    if (onPause) {
      print("PAUSE. PRESS SPACE",        windowCenter, DARK_GRAY, align = "center")
    }
  }
}
