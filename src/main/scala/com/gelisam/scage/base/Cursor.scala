package com.gelisam.scage.base

import com.gelisam.scage.base.Settings._

import com.gelisam.scage.Sound

import com.github.dunnololda.scage.handlers.Renderer
import com.github.dunnololda.scage.handlers.controller2.ScageController
import com.github.dunnololda.scage.ScageLib._
import org.streum.configrity.Configuration

import java.awt.event.KeyEvent

class Cursor(
  scage: Renderer,
  name: String
) {
  private val name_x = s"${name}.x"
  private val name_y = s"${name}.y"
  def loadCursor: Vec =
    if (Cursor.config.contains(name_x) && Cursor.config.contains(name_y))
      Vec(Cursor.config[Double](name_x), Cursor.config[Double](name_y))
    else
      Vec(0, 0)
  def saveCursor = {
    Cursor.config = Cursor.config.set(name_x, cursorPos.x)
    Cursor.config = Cursor.config.set(name_y, cursorPos.y)
    Cursor.config.save(Cursor.filename)
  }
  
  var cursorPos = loadCursor
  def moveCursor(newPos: Vec) = {
    cursorPos = newPos - scage.center
  }
  
  def render = {
    var pos = scage.center + cursorPos
    drawLine(pos - Vec(10, 0), pos + Vec(10, 0), ScageColor.WHITE)
    drawLine(pos - Vec(0, 10), pos + Vec(0, 10), ScageColor.WHITE)
  }
}

object Cursor {
  var cursorPicker: Cursor = null
  var cursorA: Cursor = null
  var cursorB: Cursor = null
  
  sealed trait Mode
  case class Disabled() extends Mode
  case class PickCursor() extends Mode
  case class PickValue(cursor: Cursor) extends Mode
  
  var mode: Cursor.Mode = Cursor.Disabled()
  def toggle = mode = mode match {
    case Cursor.Disabled() => Cursor.PickCursor()
    case Cursor.PickCursor() => Cursor.PickValue(
      if (cursorPicker.cursorPos.x <= 0) cursorA
      else cursorB
    )
    case Cursor.PickValue(cursor) => {
      cursor.saveCursor
      Cursor.Disabled()
    }
  }
  
  private val filename = "constants.conf"
  private var config = Configuration.load(filename)
  
  def init(scage: ScageController with Renderer) = {
    cursorPicker = new Cursor(scage, "cursorPicker")
    cursorA = new Cursor(scage, "cursorA")
    cursorB = new Cursor(scage, "cursorB")
    
    scage.key(KEY_GRAVE, onKeyDown = toggle)
    scage.leftMouse(
      onBtnDown = moveCursor(_),
      onBtnUp = moveCursor(_)
    )
    scage.leftMouseDrag(
      onDrag = moveCursor(_)
    )
  }
  
  def moveCursor(newPos: Vec) = mode match {
    case Cursor.Disabled() => {}
    case Cursor.PickCursor() => cursorPicker.moveCursor(newPos)
    case Cursor.PickValue(cursor) => cursor.moveCursor(newPos)
  }
  
  def render(scage: Renderer) = {
    mode match {
      case Cursor.Disabled() => {}
      case Cursor.PickCursor() => {
        print(s"Pick a side", scage.windowCenter, DARK_GRAY, align = "center")
        cursorPicker.render
      }
      case Cursor.PickValue(cursor) => {
        print(s"${cursor.cursorPos}", scage.windowCenter, DARK_GRAY, align = "center")
        cursor.render
      }
    }
  }
}
