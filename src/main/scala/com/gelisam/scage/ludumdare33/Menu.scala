package com.gelisam.scage.ludumdare33

import com.gelisam.scage.ludumdare33._

import com.gelisam.scage.adjust._
import com.gelisam.scage.adjust.Predef._
import com.gelisam.scage.Sprite

import com.github.dunnololda.scage.ScageLib._

class Menu {
  val sprite = Sprite("menu.png")
  val pos = Adjustable[Vec]("menuPos")
  
  def render {
    openglLocalTransform {
      openglMove(Vec(-234, -215) + pos)
      sprite.render()
    }
  }
}
