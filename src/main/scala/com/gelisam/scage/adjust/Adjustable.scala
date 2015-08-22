package com.gelisam.scage.adjust

import com.github.dunnololda.scage.support.Vec

class Adjustable[A](
  name: String,
  initialPos: Vec
)(
  implicit adjuster: Adjuster[A]
) extends Cursor(name, initialPos) {
  var value: A = adjuster.valueAt(initialPos)
  
  override def pos_=(newPos: Vec) = {
    super[Cursor].pos = newPos
    value = adjuster.valueAt(newPos)
  }
  
  override def toString() = s"${value}"
}

object Adjustable {
  implicit def getValue[A]: Adjustable[A] => A = _.value
  
  def apply[A](name: String)(implicit adjuster: Adjuster[A]): Adjustable[A] = {
    Cursor(name, (name, vec) => new Adjustable[A](name, vec)).asInstanceOf[Adjustable[A]]
  }
}
