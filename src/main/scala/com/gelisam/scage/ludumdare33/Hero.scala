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
) {
  val sprite = Sprite("hero.png", 3)
  val pos = Adjustable[Vec]("heroPos")
  
  var totalHp = 100
  var hp = totalHp
  
  val damageBounceHeight = Adjustable[Double]("damageBounceHeight")
  val damageBounceSpeed = Adjustable[Double]("damageBounceSpeed")
  val damageBounceDuration = Adjustable[Double]("damageBounceDuration")
  val damageBounce = Animation.math {t =>
    val x = (t/damageBounceDuration - damageBounceSpeed) / (1 - damageBounceSpeed)
    damageBounceHeight.value * (1 - Math.pow(x, 2))
  }.during(damageBounceDuration)
  
  val damageStayDuration = Adjustable[Double]("damageStayDuration")
  val damageStay = Animation.unit(0.0).during(damageStayDuration)
  
  val damageAnimation = damageBounce ++ damageStay
  
  val damagePos = Adjustable[Vec]("heroDamagePos")
  val startDamageE = EventSource[Unit]
  val damageOffset =
    damageAnimation.runAnimation(startDamageE, timeE, timeB)
  
  def takeDamage() {
    startDamageE fire ()
  }
  
  def render {
    openglLocalTransform {
      openglMove(pos)
      sprite.render()
      
      if (damageOffset.activeB) {
        print("10", damagePos.value + Vec(0, damageOffset), WHITE, align = "center")
      }
    }
  }
}
