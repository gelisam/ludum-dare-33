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
  var cursorPos = scage.center
  def moveCursor(newPos: Vec) {
    cursorPos = newPos
  }
  
  scage.leftMouse(
    onBtnDown = moveCursor(_),
    onBtnUp = moveCursor(_)
  )
  scage.leftMouseDrag(
    onDrag = moveCursor(_)
  )
  
  def render() {
    print(s"${cursorPos}", scage.windowCenter, DARK_GRAY, align = "center")
    
    drawLine(cursorPos - Vec(10, 0), cursorPos + Vec(10, 0), ScageColor.WHITE)
    drawLine(cursorPos - Vec(0, 10), cursorPos + Vec(0, 10), ScageColor.WHITE)
  }
}
