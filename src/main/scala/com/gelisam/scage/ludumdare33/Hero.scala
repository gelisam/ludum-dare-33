package com.gelisam.scage.ludumdare33

import com.gelisam.dylemma.frp._
import com.gelisam.scage.adjust._
import com.gelisam.scage.adjust.Predef._
import com.gelisam.scage.Sprite

import com.github.dunnololda.scage.ScageLib._
import io.dylemma.frp._

class Hero(
  timeE: EventStream[Double],
  timeB: Behavior[Double]
)(
  implicit observer: Observer
) extends Damageable {
  val sprite = Sprite("hero.png", 3)
  val pos = Adjustable[Vec]("heroPos")
  
  var totalHp = 100
  var hp = totalHp
  
  val damagePos = Adjustable[Vec]("heroDamagePos")
  val damageDisplay = new Damage(timeE, timeB)
  
  def takeDamage(damage: Int) {
    damageDisplay(s"${damage}")
  }
  
  def render {
    openglLocalTransform {
      openglMove(pos)
      sprite.render()
      damageDisplay.render(damagePos)
    }
  }
}
