package com.gelisam.scage.ludumdare33

import com.gelisam.scage.adjust._
import com.gelisam.scage.adjust.Predef._
import com.gelisam.scage.Sprite

import com.github.dunnololda.scage.ScageLib._

class Hero {
  val sprite = Sprite("hero.png", 3)
  val pos = Adjustable[Vec]("heroPos")
  
  var hp = 100
  
  def render {
    sprite.render(pos)
  }
}
