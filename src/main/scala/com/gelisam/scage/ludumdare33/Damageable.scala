package com.gelisam.scage.ludumdare33

import com.gelisam.dylemma.frp._

import io.dylemma.frp._

class Damageable(
  var totalHp: Int,
  timeE: EventStream[Double],
  timeB: Behavior[Double]
)(
  implicit observer: Observer
) {
  var hp = totalHp
  
  def reset {
    hp = totalHp
    recoil.reset
  }
  
  var recoil = new Recoil(timeE, timeB)
  val damageDisplay = new Damage(timeE, timeB)
  
  def takeDamage(damage: Int, cc: () => Unit) {
    hp -= damage
    if (hp < 0) {
      hp = 0
    }
    recoil(hp == 0)
    damageDisplay(s"${damage}", cc)
  }
  
  def alive: Boolean = hp > 0
}
