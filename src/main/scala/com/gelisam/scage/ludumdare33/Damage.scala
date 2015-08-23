package com.gelisam.scage.ludumdare33

import com.gelisam.dylemma.frp._
import com.gelisam.scage.adjust._
import com.gelisam.scage.adjust.Predef._
import com.gelisam.scage.Sprite

import com.github.dunnololda.scage.ScageLib._
import io.dylemma.frp._

class Damage(
  timeE: EventStream[Double],
  timeB: Behavior[Double]
)(
  implicit observer: Observer
) {
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
  
  val startDamageE = EventSource[Unit]
  val damageOffset =
    damageAnimation.runAnimation(startDamageE, timeE, timeB)
  
  var afterAnimating: () => Unit = null
  damageOffset.stopE.foreach{_ =>
    afterAnimating()
  }
  
  var damageText = ""
  def apply(text: String, cc: () => Unit) {
    damageText = text
    afterAnimating = cc
    startDamageE fire ()
  }
  
  def render(pos: Vec) = if (damageOffset.activeB) {
    print(damageText, pos + Vec(0, damageOffset), WHITE, align = "center")
  }
}
