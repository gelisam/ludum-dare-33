package com.gelisam.scage.ludumdare33

import com.gelisam.scage.ludumdare33._

import com.gelisam.scage.adjust._
import com.gelisam.scage.adjust.Predef._
import com.gelisam.scage.Sprite

import com.github.dunnololda.scage.ScageLib._

class MessageBox {
  val sprite = Sprite("message-box.png")
  val pos = Adjustable[Vec]("messageBoxPos")
  
  def render {
    openglLocalTransform {
      openglMove(Vec(200, 165) + pos)
      sprite.render()
    }
  }
}
