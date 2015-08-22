package com.gelisam.scage.ludumdare33

import com.gelisam.scage.ludumdare33._

import com.gelisam.scage.adjust._
import com.gelisam.scage.adjust.Predef._
import com.gelisam.scage.Sprite

import com.github.dunnololda.scage.handlers.controller2.ScageController
import com.github.dunnololda.scage.ScageLib._

class Menu(scage: ScageController) {
  val sprite = Sprite("menu.png")
  val pos = Adjustable[Vec]("menuPos")
  
  val cmdPos = Adjustable[Vec]("menuCmdPos")
  val cmdGap = Adjustable[Double]("menuCmdGap")
  val cmdArray = Array[String]("Fight", "", "Magic", "Item")
  
  var pointer = 0
  val pointerSprite = Sprite("cursor.png")
  val pointerPos = Adjustable[Vec]("menuPointerPos")
  
  def incr {
    pointer += 1
    if (pointer >= cmdArray.length) pointer = 0
    if (cmdArray(pointer) == "") incr
  }
  def decr {
    pointer -= 1
    if (pointer < 0) pointer = cmdArray.length - 1
    if (cmdArray(pointer) == "") decr
  }
  def select {
    val cmd = cmdArray(pointer)
    println(cmd)
  }
  
  scage.key(KEY_DOWN, onKeyDown = incr)
  scage.key(KEY_UP, onKeyDown = decr)
  scage.key(KEY_RETURN, onKeyDown = select)
  scage.key(KEY_Z, onKeyDown = select)
  
  def render {
    openglLocalTransform {
      openglMove(Vec(-234, -215) + pos)
      sprite.render()
      
      for (i <- 0 until cmdArray.length) {
        val cmd = cmdArray(i)
        print(cmd, Vec(-23, 90) + cmdPos + Vec(0, 3*cmdGap) * i, BLACK)
      }
      pointerSprite.render(Vec(-23, 90) + pointerPos + Vec(0, 3*cmdGap) * pointer)
    }
  }
}
