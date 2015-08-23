package com.gelisam.scage.ludumdare33

import com.gelisam.dylemma.frp._
import com.gelisam.scage.adjust._
import com.gelisam.scage.adjust.Predef._
import com.gelisam.scage.Sprite

import com.github.dunnololda.scage.ScageLib._
import io.dylemma.frp._

class Monster(
  timeE: EventStream[Double],
  timeB: Behavior[Double]
)(
  implicit observer: Observer
) extends Damageable(3000, timeE, timeB) with Attacker {
  def takeTurn(cc: () => Unit) {
    attack(BattleScreen.hero, cc)
  }
  
  val sprite = Sprite("monster.png", 5)
  val attackingSprite = Sprite("monster-hi.png", 5)
  val pos = Adjustable[Vec]("monsterPos")
  
  val attackPower = 80
  val attackE = EventSource[Unit]
  val attackHiDuration = Adjustable[Double]("monsterAttackHiDuration")
  val attackLoDuration = Adjustable[Double]("monsterAttackLoDuration")
  val attackDelay = Adjustable[Double]("attackDelay")
  val attackAnimation: Animation[Sprite] =
    Animation.unit(sprite).during(attackDelay) ++
    Animation.unit(attackingSprite).during(attackHiDuration) ++
    Animation.unit(sprite).during(attackLoDuration) ++
    Animation.unit(attackingSprite).during(attackHiDuration) ++
    Animation.unit(sprite).during(attackDelay)
  val animatedSprite =
    Animated.unit(sprite) ||
    attackAnimation.runAnimation(attackE, timeE, timeB)
  
  var afterAttacking: () => Unit = null
  animatedSprite.stopE.foreach{_ =>
    afterAttacking()
  }
  
  def attack(target: Damageable, cc: () => Unit) {
    afterAttacking = {() =>
      target.takeDamage(attackPower, cc)
    }
    attackE fire ()
  }
  
  val damagePos = Adjustable[Vec]("monsterDamagePos")
  
  def render {
    openglLocalTransform {
      openglMove(pos)
      if (!recoil.animatedBlink) animatedSprite.render()
      damageDisplay.render(damagePos)
    }
  }
}
