package com.gelisam.scage.ludumdare33

import com.gelisam.scage.ludumdare33._

import com.gelisam.scage.adjust._
import com.gelisam.scage.adjust.Predef._
import com.gelisam.scage.Sprite

import com.github.dunnololda.scage.ScageLib._

class HpBar(monster: Monster, hero: Hero) {
  val sprite = Sprite("hp-bar.png")
  val pos = Adjustable[Vec]("hpBarPos")
  
  def render {
    sprite.render(Vec(194, -189) + pos)
  }
}
