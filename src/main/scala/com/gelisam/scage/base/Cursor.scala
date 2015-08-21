package com.gelisam.scage.base

import com.gelisam.scage.base.Settings._

import com.gelisam.dylemma.frp.Behavior
import com.gelisam.scage.Sound

import com.github.dunnololda.scage.handlers.Renderer
import com.github.dunnololda.scage.handlers.controller2.ScageController
import com.github.dunnololda.scage.ScageLib._
import io.dylemma.frp._

class Cursor(
  scage: ScageController with Renderer
)(implicit observer: Observer) {
  def render() {
    drawLine(scage.center - Vec(10, 0), scage.center + Vec(10, 0), ScageColor.WHITE)
    drawLine(scage.center - Vec(0, 10), scage.center + Vec(0, 10), ScageColor.WHITE)
  }
}
