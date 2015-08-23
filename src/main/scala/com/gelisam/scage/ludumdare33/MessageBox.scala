package com.gelisam.scage.ludumdare33

import com.gelisam.scage.ludumdare33._

import com.gelisam.dylemma.frp._
import com.gelisam.scage.adjust._
import com.gelisam.scage.adjust.Predef._
import com.gelisam.scage.Sprite

import com.github.dunnololda.scage.ScageLib._
import io.dylemma.frp._

class MessageBox(
  timeE: EventStream[Double],
  timeB: Behavior[Double]
)(
  implicit observer: Observer
) {
  val sprite = Sprite("message-box.png")
  val pos = Adjustable[Vec]("messageBoxPos")
  val textPos = Adjustable[Vec]("messageBoxTextPos")
  
  val messageE = EventSource[Unit]
  val messageDuration = Adjustable[Double]("messageDuration")
  val messageDelay = Adjustable[Double]("messageDelay")
  val messageAnimation: Animation[Boolean] =
    Animation.unit(false).during(messageDelay) ++
    Animation.unit(true).during(messageDuration) ++
    Animation.unit(false).during(messageDelay)
  val animatedVisibility =
    Animated.unit(false) ||
    messageAnimation.runAnimation(messageE, timeE, timeB)
  
  var afterMessage: () => Unit = null
  animatedVisibility.stopE.foreach{_ =>
    afterMessage()
  }
  
  var message = ""
  def display(message: String) (cc: () => Unit) {
    this.message = message
    afterMessage = cc
    messageE fire ()
  }
  
  def render {
    if (animatedVisibility) {
      openglLocalTransform {
        openglMove(Vec(200, 165) + pos)
        sprite.render()
        print(message, textPos, BLACK)
      }
    }
  }
}
