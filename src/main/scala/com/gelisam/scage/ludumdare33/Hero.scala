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
) extends Attacker with Damageable {
  val sprite = Sprite("hero.png", 3)
  val pos = Adjustable[Vec]("heroPos")
  
  val attackPower = 10
  
  def attack(target: Damageable) {
    target.takeDamage(attackPower)
  }
  
  var totalHp = 100
  var hp = totalHp
  
  val damagePos = Adjustable[Vec]("heroDamagePos")
  val damageDisplay = new Damage(timeE, timeB)
  
  var recoil = new Recoil(timeE, timeB)
  
  def takeDamage(damage: Int) {
    recoil()
    damageDisplay(s"${damage}")
  }
  
  def render {
    openglLocalTransform {
      openglMove(pos)
      if (!recoil.animatedBlink) sprite.render()
      damageDisplay.render(damagePos)
    }
  }
}
