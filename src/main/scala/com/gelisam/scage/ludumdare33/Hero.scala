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
) extends Damageable(100, timeE, timeB) with Attacker {
  def takeTurn(cc: () => Unit) {
    attack(BattleScreen.monster, cc)
  }
  
  val sprite = Sprite("hero.png", 3)
  val pos = Adjustable[Vec]("heroPos")
  
  val attackPower = 1000
  val attackE = EventSource[Unit]
  val attackDelay = Adjustable[Double]("attackDelay")
  val lurchForwardDistance = Adjustable[Double]("heroLurchForwardDistance")
  val lurchForwardDuration = Adjustable[Double]("heroLurchForwardDuration")
  val lurchForwardAnimation = Animation.math(t0 =>
    if (t0 < lurchForwardDuration) {
      val t = t0
      t / lurchForwardDuration * lurchForwardDistance
    } else if (t0 < 2*lurchForwardDuration) {
      val t = t0 - lurchForwardDuration
      (1 - t / lurchForwardDuration) * lurchForwardDistance
    } else if (t0 < 3*lurchForwardDuration) {
      val t = t0 - 2*lurchForwardDuration
      t / lurchForwardDuration * lurchForwardDistance
    } else {
      val t = t0 - 3*lurchForwardDuration
      (1 - t / lurchForwardDuration) * lurchForwardDistance
    }
  ).during(4*lurchForwardDuration)
  val attackAnimation =
    lurchForwardAnimation ++
    Animation.unit(0.0).during(attackDelay)
  val animatedOffset =
    Animated.unit(0.0) ||
    attackAnimation.runAnimation(attackE, timeE, timeB)
  
  var afterAttacking: () => Unit = null
  animatedOffset.stopE.foreach{_ =>
    afterAttacking()
  }
  
  def attack(target: Damageable, cc: () => Unit) {
    afterAttacking = {() =>
      target.takeDamage(attackPower, cc)
    }
    attackE fire ()
  }
  
  val damagePos = Adjustable[Vec]("heroDamagePos")
  
  def render {
    openglLocalTransform {
      openglMove(pos)
      if (!recoil.animatedBlink) {
        sprite.render(Vec(-animatedOffset, 0))
      }
      damageDisplay.render(damagePos)
    }
  }
}
