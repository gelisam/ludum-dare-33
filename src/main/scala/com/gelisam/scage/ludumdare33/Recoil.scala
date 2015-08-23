package com.gelisam.scage.ludumdare33

import com.gelisam.dylemma.frp._
import com.gelisam.scage.adjust._
import com.gelisam.scage.adjust.Predef._

import com.github.dunnololda.scage.ScageLib._
import io.dylemma.frp._

class Recoil(
  timeE: EventStream[Double],
  timeB: Behavior[Double]
)(
  implicit observer: Observer
) {
  val resetE = EventSource[Unit]
  def reset {
    resetE fire ()
  }
  
  val recoilE = EventSource[Unit]
  val deathE = EventSource[Unit]
  val recoilAnimation: Animation[Boolean] =
    Animation.unit(false).during(0.08) ++
    Animation.unit(true).during(0.08) ++
    Animation.unit(false).during(0.08) ++
    Animation.unit(true).during(0.08) ++
    Animation.unit(false).during(0.08) ++
    Animation.unit(true).during(0.08) ++
    Animation.unit(false).during(0.08)
  val deathAnimation: Animation[Boolean] =
    Animation.unit(false).during(1.5) ++
    Animation.unit(true).during(0.08) ++
    Animation.unit(false).during(0.08) ++
    Animation.unit(true).during(0.08) ++
    Animation.unit(false).during(0.08) ++
    Animation.unit(true).during(0.08) ++
    Animation.unit(false).during(0.08) ++
    Animation.unit(true).during(0.08) ++
    Animation.unit(false).during(0.08) ++
    Animation.unit(true).during(0.08) ++
    Animation.unit(false).during(0.08) ++
    Animation.unit(true)
  val animatedRecoilBlink =
    Animation.unit(false).runAnimation(resetE, timeE, timeB) ||
    recoilAnimation.runAnimation(recoilE, timeE, timeB)
  val animatedDeathBlink =
    Animation.unit(false).runAnimation(resetE, timeE, timeB) ||
    deathAnimation.runAnimation(deathE, timeE, timeB)
  
  def animatedBlink: Boolean =
    animatedRecoilBlink.valueB.value ||
    animatedDeathBlink.valueB.value
  
  def apply(deadly: Boolean) {
    if (deadly) deathE fire ()
    else recoilE fire ()
  }
}
