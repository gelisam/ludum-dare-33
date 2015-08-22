package com.gelisam.scage.ludumdare33

import com.gelisam.scage.adjust._
import com.gelisam.scage.adjust.Predef._
import com.gelisam.scage.Sprite

import com.github.dunnololda.scage.ScageLib._

class Monster {
  val sprite = Sprite("monster.png", 5)
  val pos = Adjustable[Vec]("monsterPos")
  
  var totalHp = 3000
  var hp = totalHp
  
  def render {
    sprite.render(pos)
  }
}
