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
) extends Damageable {
  val sprite = Sprite("monster.png", 5)
  val attackingSprite = Sprite("monster-hi.png", 5)
  val pos = Adjustable[Vec]("monsterPos")
  
  val attackE = EventSource[Unit]
  val attackHiDuration = Adjustable[Double]("monsterAttackHiDuration")
  val attackLoDuration = Adjustable[Double]("monsterAttackLoDuration")
  val attackDelay = Adjustable[Double]("monsterAttackDelay")
  val attackAnimation: Animation[Sprite] =
    Animation.unit(sprite).during(attackDelay) ++
    Animation.unit(attackingSprite).during(attackHiDuration) ++
    Animation.unit(sprite).during(attackLoDuration) ++
    Animation.unit(attackingSprite).during(attackHiDuration) ++
    Animation.unit(sprite).during(attackDelay)
  val animatedSprite =
    Animated.unit(sprite) ||
    attackAnimation.runAnimation(attackE, timeE, timeB)
  
  var totalHp = 3000
  var hp = totalHp
  
  val damagePos = Adjustable[Vec]("monsterDamagePos")
  val damageDisplay = new Damage(timeE, timeB)
  
  var recoilE = EventSource[Unit]
  var recoilAnimation: Animation[Boolean] =
    Animation.unit(false).during(0.08) ++
    Animation.unit(true).during(0.08) ++
    Animation.unit(false).during(0.08) ++
    Animation.unit(true).during(0.08) ++
    Animation.unit(false).during(0.08) ++
    Animation.unit(true).during(0.08) ++
    Animation.unit(false).during(0.08)
  var animatedBlink =
    Animated.unit(false) ||
    recoilAnimation.runAnimation(recoilE, timeE, timeB)
  
  def takeDamage(damage: Int) {
    recoilE fire ()
    damageDisplay(s"${damage}")
  }
  
  def render {
    openglLocalTransform {
      openglMove(pos)
      if (!animatedBlink) animatedSprite.render()
      damageDisplay.render(damagePos)
    }
  }
}
