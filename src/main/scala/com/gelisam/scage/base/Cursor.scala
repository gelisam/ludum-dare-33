package com.gelisam.scage.base

import com.gelisam.scage.base.Settings._

import com.gelisam.dylemma.frp.Behavior
import com.gelisam.scage.Sound

import com.github.dunnololda.scage.handlers.Renderer
import com.github.dunnololda.scage.handlers.controller2.ScageController
import com.github.dunnololda.scage.ScageLib._
import io.dylemma.frp._
import org.streum.configrity.Configuration

import java.awt.event.KeyEvent

class Cursor(
  scage: ScageController with Renderer
)(implicit observer: Observer) {
  var enabled = false
  def toggle = {
    if (enabled) {
      saveCursor
    }
    enabled = !enabled
  }
  
  val filename = "constants.conf"
  var config = Configuration.load(filename)
  val name = "cursor"
  val name_x = s"${name}.x"
  val name_y = s"${name}.y"
  def loadCursor: Vec =
    if (config.contains(name_x) && config.contains(name_y))
      Vec(config[Double](name_x), config[Double](name_y))
    else
      Vec(0, 0)
  def saveCursor = {
    config = config.set(name_x, cursorPos.x)
    config = config.set(name_y, cursorPos.y)
    config.save(filename)
  }
  
  var cursorPos = loadCursor
  def moveCursor(newPos: Vec) = if (enabled) {
    cursorPos = newPos - scage.center
  }
  
  scage.key(KEY_GRAVE, onKeyDown = toggle)
  scage.leftMouse(
    onBtnDown = moveCursor(_),
    onBtnUp = moveCursor(_)
  )
  scage.leftMouseDrag(
    onDrag = moveCursor(_)
  )
  
  def render() = if (enabled) {
    print(s"${cursorPos}", scage.windowCenter, DARK_GRAY, align = "center")
    
    var pos = scage.center + cursorPos
    drawLine(pos - Vec(10, 0), pos + Vec(10, 0), ScageColor.WHITE)
    drawLine(pos - Vec(0, 10), pos + Vec(0, 10), ScageColor.WHITE)
  }
}
