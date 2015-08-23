package com.gelisam.scage.ludumdare33

import com.gelisam.scage.ludumdare33._

trait Attacker {
  def takeTurn(cc: () => Unit)
  def attack(target: Damageable, cc: () => Unit)
}
