package com.gelisam.scage.ludumdare33

import com.gelisam.scage.ludumdare33._

class Battle(fighters: Array[Damageable with Attacker]) {
  var currentIndex = 0
  
  def currentFighter: Damageable with Attacker =
    fighters(currentIndex)
  
  def begin =
    currentFighter.takeTurn(() => nextFighter)
  
  def nextFighter {
    currentIndex += 1
    if (currentIndex >= fighters.length) {
      currentIndex = 0
    }
    if (currentFighter.alive) {
      currentFighter.takeTurn(() => nextFighter)
    } else if (Main.monster.alive) {
      anihilated
    } else {
      hero_wins
    }
  }
  
  def anihilated {
    println("anihilated")
  }
  
  def hero_wins {
    println("hero wins")
  }
}

object Battle {
  def apply(fighters: Damageable with Attacker*) =
    new Battle(fighters.toArray)
}