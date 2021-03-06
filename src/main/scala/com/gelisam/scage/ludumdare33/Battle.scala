package com.gelisam.scage.ludumdare33

import com.gelisam.scage.ludumdare33._

class Battle(fighters: Array[Damageable with Attacker]) {
  var currentIndex = 0
  
  def currentFighter: Damageable with Attacker =
    fighters(currentIndex)
  
  def begin {
    fighters.foreach(_.reset)
    currentFighter.takeTurn(() => nextFighter)
  }
  
  def nextFighter {
    currentIndex += 1
    if (currentIndex >= fighters.length) {
      currentIndex = 0
    }
    if (currentFighter.alive) {
      currentFighter.takeTurn(() => nextFighter)
    } else if (BattleScreen.monster.alive) {
      anihilated
    } else {
      hero_wins
    }
  }
  
  def anihilated {
    BattleScreen.messageBox.display("anihilated") {() =>
      Main.playIntermission
      BattleScreen.stop
    }
  }
  
  def hero_wins {
    BattleScreen.messageBox.display("gained 1000 XP") {() =>
      BattleScreen.messageBox.display("found 0 GP") {() =>
        BattleScreen.messageBox.display("obtained X-Potion") {() =>
          println("hero wins")
        }
      }
    }
  }
}

object Battle {
  def apply(fighters: Damageable with Attacker*) =
    new Battle(fighters.toArray)
}
