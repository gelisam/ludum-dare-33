package com.gelisam.scage.ludumdare33

class Damageable(var totalHp: Int) {
  var hp = totalHp
  
  def takeDamage(damage: Int, cc: () => Unit) {
    hp -= damage
    if (hp < 0) {
      hp = 0
    }
  }
  
  def alive: Boolean = hp > 0
}
