package com.gelisam.dylemma.frp

import com.gelisam.dylemma.frp._

import io.dylemma.frp._

// an animated value does something interesting for a possibly-zero amound of time,
// then "stops", which really means "switches to an idle animation".
case class Animated[A](
  startE: EventStream[Unit],
  valueB: Behavior[A],
  stopE: EventStream[Unit]
)(
  implicit observer: Observer
) {
  val activeB = Behavior.stepper(false, startE.map(_ => true)
                                     || stopE.map(_ => false))
  
  def map[B](f: A => B): Animated[B] =
    Animated(startE, valueB.map(f), stopE)
  def map2[B,C](that: Animated[B])(f: (A,B) => C): Animated[C] = {
    val eitherStartE = (startE || that.startE).filter(_ => !activeB && !that.activeB)
    
    val idleChangeE: EventStream[(Boolean, Boolean) => (Boolean, Boolean)] =
      startE.map    (_ => (    _:Boolean,     _:Boolean) => (false, false)) ||
      stopE.map     (_ => (    _:Boolean, idleB:Boolean) => (true, idleB)) ||
      that.stopE.map(_ => (idleA:Boolean,     _:Boolean) => (idleA, true))
    val idleChangesToE: EventStream[(Boolean, Boolean)] =
      idleChangeE.foldLeft((false, false)) {
        case ((idleA,idleB), f) => f(idleA,idleB)
      }
    val bothIdleE: EventStream[Unit] =
      idleChangesToE.filter(_ == (true, true)).map(_ => ())
  
    Animated(eitherStartE, (valueB map2 that.valueB)(f), bothIdleE)
  }
  
  // play whichever animation started last, starting with this one.
  def ||(that: Animated[A]): Animated[A] = {
    val eitherStartE = (startE || that.startE).filter(_ => !activeB && !that.activeB)
    
    // false => this
    // true => that
    val whichB: Behavior[Boolean] =
      Behavior.stepper(false, startE.map(_ => false)
                      || that.startE.map(_ => true))
    val realValueB: Behavior[A] = (valueB map2 that.valueB) {(a,b) =>
      whichB.map {
        case false => a
        case true => b
      }
    }
    val reallyStopsE = stopE.filter(_ => whichB.value == false) ||
                  that.stopE.filter(_ => whichB.value == true)
    
    Animated(startE, realValueB, reallyStopsE)
  }
}

object Animated {
  implicit def getBehavior[A]: Animated[A] => Behavior[A] = _.valueB
  implicit def getValue[A]: Animated[A] => A = _.valueB.value
  
  val neverE = EventSource[Unit]
  
  def unit[A](value: A)(implicit observer: Observer): Animated[A] =
    Animated(neverE, Behavior.unit(value), neverE)
}

trait Animation[A] {
  def runAnimation(
    startE: EventStream[Unit],
    timeE: EventStream[Double],
    timeB: Behavior[Double]
  )(
    implicit observer: Observer
  ): Animated[A]

  def map[B](f: A => B): Animation[B] = Animation((startE, timeE, timeB) => implicit observer =>
    runAnimation(startE, timeE, timeB).map(f)
  )
  def map2[B,C](that: Animation[B])(f: (A,B) => C): Animation[C] =
    Animation((startE, timeE, timeB) => implicit observer => {
      val animationA = runAnimation(startE, timeE, timeB)
      val animationB = that.runAnimation(startE, timeE, timeB)
      (animationA map2 animationB)(f)
    })
  
  // ignore the animation's stop events,
  // pretend the animation stops after delay seconds instead
  def during(delay: Double): Animation[A] =
    Animation((startE, timeE, timeB) => implicit observer => {
      val startTimeB: Behavior[Double] = Behavior.stepper(0.0, startE.map(_ => timeB))
      lazy val activeB: Behavior[Boolean] = Behavior.stepper(false, startE.map(_ => true)
                                                                 || stopE.map(_ => false))
      lazy val stopE = timeE.filter(t => activeB && t >= startTimeB.value + delay).map(_ => ())
      
      Animated(startE, runAnimation(startE, timeE, timeB), stopE)
    })
  
  // play this animation until it stops, then play that other animation afterwards
  def ++(that: Animation[A]): Animation[A] =
    Animation((startE, timeE, timeB) => implicit observer => {
      val animatedA = runAnimation(startE, timeE, timeB)
      val animatedB = that.runAnimation(animatedA.stopE, timeE, timeB)
      
      // false => this
      // true => that
      val whichB: Behavior[Boolean] =
        Behavior.stepper(false, startE.map(_ => false)
                             || animatedA.stopE.map(_ => true))
      val realValueB: Behavior[A] = (animatedA.valueB map2 animatedB.valueB) {(a,b) =>
        whichB.value match {
          case false => a
          case true => b
        }
      }
      val reallyStopsE =
        animatedB.stopE.filter(_ => whichB.value == true)
      
      Animated(startE, realValueB, reallyStopsE)
    })
}

object Animation {
  def apply[A](
    f: (EventStream[Unit], EventStream[Double], Behavior[Double]) => (Observer) => Animated[A]
  ): Animation[A] = new Animation[A] {
    def runAnimation(startE: EventStream[Unit], timeE: EventStream[Double], timeB: Behavior[Double])
                    (implicit observer: Observer): Animated[A]
                    =
      f(startE, timeE, timeB)(observer)
  }
  
  def unit[A](value: A): Animation[A] = Animation((startE, timeE, timeB) => implicit observer =>
    Animated(startE, Behavior.unit(value), startE)
  )
  
  def math[A](f: Double => A): Animation[A] = Animation((startE, timeE, timeB) => implicit observer => {
    val startTimeB: Behavior[Double] = Behavior.stepper(0.0, startE.map(_ => timeB))
    Animated(startE, timeB.map(t => f(t - startTimeB)), startE)
  })
}
