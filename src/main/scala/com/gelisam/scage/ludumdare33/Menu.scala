package com.gelisam.scage.ludumdare33

import com.gelisam.scage.ludumdare33._

import com.gelisam.scage.adjust._
import com.gelisam.scage.adjust.Predef._
import com.gelisam.scage.Sprite

import com.github.dunnololda.scage.ScageLib._

class Menu {
  val sprite = Sprite("menu.png")
  val pos = Adjustable[Vec]("menuPos")
  
  val menuCmdPos = Adjustable[Vec]("menuCmdPos")
  val menuCmdGap = Adjustable[Double]("menuCmdGap")
  val cmdArray = Array[String]("Fight", "", "Magic", "Item")
  
  def render {
    openglLocalTransform {
      openglMove(Vec(-234, -215) + pos)
      sprite.render()
      
      for (i <- 0 until cmdArray.length) {
        val cmd = cmdArray(i)
        print(cmd, Vec(-23, 90) + menuCmdPos + Vec(0, 3*menuCmdGap) * i, BLACK)
      }
    }
  }
}
