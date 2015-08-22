package com.gelisam.scage.adjust

import com.gelisam.scage.base.Settings._

import com.gelisam.scage.Sound

import com.github.dunnololda.scage.handlers.Renderer
import com.github.dunnololda.scage.handlers.controller2.ScageController
import com.github.dunnololda.scage.ScageLib._
import org.streum.configrity.Configuration

import scala.collection.mutable.Map

import java.awt.event.KeyEvent

class Cursor(val name: String, private var _pos: Vec) {
  def pos = _pos
  def pos_=(newPos: Vec) =
    _pos = newPos
}

object CursorConfig {
  private val filename = "constants.conf"
  private var config = Configuration.load(filename)
  
  def load(name: String): Vec = {
    val name_x = s"${name}.x"
    val name_y = s"${name}.y"
    if (config.contains(name_x) && config.contains(name_y))
      Vec(config[Double](name_x), config[Double](name_y))
    else
      Vec(0, 0)
  }
  
  def save(cursor: Cursor) = {
    val name_x = s"${cursor.name}.x"
    val name_y = s"${cursor.name}.y"
    config = config.set(name_x, cursor.pos.x)
    config = config.set(name_y, cursor.pos.y)
    config.save(filename)
  }
}

object Cursor {
  val config = Configuration.load("constants.conf")
  val cursors = Map[String,Cursor]()
  
  def apply(
    name: String,
    mkCursor: (String, Vec) => Cursor = new Cursor(_,_)
  ): Cursor = cursors.getOrElseUpdate(name, {
    val pos = CursorConfig.load(name)
    mkCursor(name, pos)
  })
}

object Adjust {
  var cursorPicker: Cursor = null
  var cursorA: Cursor = null
  var cursorB: Cursor = null
  
  sealed trait Mode
  case class Disabled() extends Mode
  case class PickCursor() extends Mode
  case class PickValue(cursor: Cursor) extends Mode
  
  var mode: Adjust.Mode = Adjust.Disabled()
  def toggle = mode = mode match {
    case Adjust.Disabled() => Adjust.PickCursor()
    case Adjust.PickCursor() => Adjust.PickValue(
      if (cursorPicker.pos.x <= 0) cursorA
      else cursorB
    )
    case Adjust.PickValue(cursor) => {
      CursorConfig.save(cursor)
      Adjust.Disabled()
    }
  }
  
  def init(scage: ScageController with Renderer) = {
    def moveCursor(newPos: Vec) = mode match {
      case Adjust.Disabled() => {}
      case Adjust.PickCursor() => cursorPicker.pos = newPos - scage.center
      case Adjust.PickValue(cursor) => cursor.pos = newPos - scage.center
    }
    
    cursorPicker = Cursor("cursorPicker")
    cursorA = Cursor("cursorA")
    cursorB = Cursor("cursorB")
    
    scage.key(KEY_GRAVE, onKeyDown = toggle)
    scage.leftMouse(
      onBtnDown = moveCursor(_),
      onBtnUp = moveCursor(_)
    )
    scage.leftMouseDrag(
      onDrag = moveCursor(_)
    )
  }
  
  def render(scage: Renderer) = {
    def renderCursor(cursor: Cursor) = {
      var pos = scage.center + cursor.pos
      drawLine(pos - Vec(10, 0), pos + Vec(10, 0), ScageColor.WHITE)
      drawLine(pos - Vec(0, 10), pos + Vec(0, 10), ScageColor.WHITE)
    }
    
    mode match {
      case Adjust.Disabled() => {}
      case Adjust.PickCursor() => {
        print(s"Pick a side", scage.windowCenter, DARK_GRAY, align = "center")
        renderCursor(cursorPicker)
      }
      case Adjust.PickValue(cursor) => {
        print(s"${cursor.pos}", scage.windowCenter, DARK_GRAY, align = "center")
        renderCursor(cursor)
      }
    }
  }
}
