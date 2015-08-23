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
  val attackE = EventSource[Unit]
  val attackDelay = Adjustable[Double]("attackDelay")
  val lurchForwardDistance = Adjustable[Double]("heroLurchForwardDistance")
  val lurchForwardDuration = Adjustable[Double]("heroLurchForwardDuration")
  var lurchForwardAnimation = Animation.math(t =>
    t / lurchForwardDuration * lurchForwardDistance
  ).during(lurchForwardDuration)
  val lurchBackAnimation = Animation.math(t =>
    (1 - t / lurchForwardDuration) * lurchForwardDistance
  ).during(lurchForwardDuration)
  val attackAnimation =
    lurchForwardAnimation ++
    lurchBackAnimation ++
    lurchForwardAnimation ++
    lurchBackAnimation ++
    Animation.unit(0.0).during(attackDelay)
  val animatedOffset =
    Animated.unit(0.0) ||
    attackAnimation.runAnimation(attackE, timeE, timeB)
  
  var target: Damageable = null
  animatedOffset.stopE.foreach{_ =>
    target.takeDamage(attackPower)
  }
  
  def attack(target: Damageable) {
    this.target = target
    attackE fire ()
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
      if (!recoil.animatedBlink) sprite.render(Vec(-animatedOffset, 0))
      damageDisplay.render(damagePos)
    }
  }
}
