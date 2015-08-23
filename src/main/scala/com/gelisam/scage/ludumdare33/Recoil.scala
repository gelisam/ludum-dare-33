package com.gelisam.scage.ludumdare33

import com.gelisam.dylemma.frp._

import io.dylemma.frp._

class Recoil(
  timeE: EventStream[Double],
  timeB: Behavior[Double]
)(
  implicit observer: Observer
) {
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
  
  def apply() {
    recoilE fire ()
  }
}
