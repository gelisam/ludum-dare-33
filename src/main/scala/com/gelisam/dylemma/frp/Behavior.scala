package com.gelisam.dylemma.frp

import io.dylemma.frp._

case class Behavior[A](var value: A, private var changeE: EventStream[A])(implicit obs: Observer) {
  {
    var nextValue = value
    changeE.foreach(nextValue = _)
    Behavior.updateBehaviorsE.foreach(_ => value = nextValue)
  }
  
  def map[B](f: A => B): Behavior[B] =
    Behavior(f(value), changeE.map(f))
  def map2[B,C](that: Behavior[B])(f: (A,B) => C): Behavior[C] =
    Behavior(f(value, that.value), changeE.either(that.changeE).map {
      case Left(a) => f(a, that.value)
      case Right(b) => f(value, b)
    })
}

object Behavior {
  implicit def getValue[A]: Behavior[A] => A = _.value
  
  val updateBehaviorsE = EventSource[Unit]
  def updateBehaviors = updateBehaviorsE fire Unit
  
  def unit[A](value: A)(implicit obs: Observer): Behavior[A] = {
    val neverE = EventSource[A]
    Behavior(value, neverE)
  }
  def stepper[A](value: A, changeE: EventStream[A])(implicit obs: Observer): Behavior[A] =
    Behavior(value, changeE)
  def accum[A](value: A, changeE: EventStream[A => A])(implicit obs: Observer): Behavior[A] = {
    var acc = value
    Behavior(value, changeE.map {f =>
      acc = f(acc)
      acc
    })
  }
}
