package com.gelisam.scage.ludumdare33

import com.gelisam.scage.ludumdare33._

import com.gelisam.scage.adjust._
import com.gelisam.scage.adjust.Predef._
import com.gelisam.scage.Sprite

import com.github.dunnololda.scage.ScageLib._

class HpBar(monster: Monster, hero: Hero) {
  val sprite = Sprite("hp-bar.png")
  val pos = Adjustable[Vec]("hpBarPos")
  
  val monsterHpPos = Adjustable[Vec]("monsterHpPos")
  val heroHpPos = Adjustable[Vec]("heroHpPos")
  
  def render {
    openglLocalTransform {
      openglMove(Vec(194, -189) + pos)
      sprite.render()
      
      print(s"Monster ${monster.hp} / ${monster.totalHp} HP", Vec(-230, 32) + monsterHpPos, BLACK, align = "center-right")
      print(s"Hero ${hero.hp} / ${hero.totalHp} HP", Vec(106, 32) + heroHpPos, BLACK, align = "center-right")
    }
  }
}
