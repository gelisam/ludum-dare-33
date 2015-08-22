package com.gelisam.scage.adjust

import com.github.dunnololda.scage.support._

import io.dylemma.frp._

trait Adjuster[A] {
  def valueAt(pos: Vec): A
}

object Predef {
  implicit val BooleanAdjuster = new Adjuster[Boolean] {
    def valueAt(pos: Vec): Boolean = pos.x > 0
  }
  
  implicit val IntAdjuster = new Adjuster[Int] {
    def valueAt(pos: Vec): Int = (Math.pow(2, pos.y / 32) * pos.x / 32).toInt
  }
  
  implicit val FloatAdjuster = new Adjuster[Float] {
    def valueAt(pos: Vec): Float = (Math.pow(2, pos.y / 32) * pos.x / 32).toFloat
  }
  
  implicit val DoubleAdjuster = new Adjuster[Double] {
    def valueAt(pos: Vec): Double = Math.pow(2, pos.y / 32) * pos.x / 32
  }
  
  implicit val VecAdjuster = new Adjuster[Vec] {
    def valueAt(pos: Vec): Vec = {
      val sx = Math.signum(pos.x)
      val sy = Math.signum(pos.y)
      val rx = Math.abs(pos.x) - 96
      val ry = Math.abs(pos.y) - 96
      val dx = sx * Math.pow(2, rx / 16)
      val dy = sy * Math.pow(2, ry / 16)
      Vec(dx, dy)
    }
  }
  
  implicit val ColorAdjusted = new Adjuster[ScageColor] {
    def valueAt(pos: Vec): ScageColor = {
      val theta = Math.atan2(pos.y, pos.x)
      val radius = Math.sqrt(pos.x*pos.x + pos.y*pos.y)
      val (r1,g1,b1): (Double, Double, Double) =
        if      (theta < -2*Math.PI/3) {val t = (theta + 3*Math.PI/3)/(Math.PI/3); (1  ,  t,0  )} // (1,0,0)..(1,1,0) ~ -3pi/3..-2pi/3
        else if (theta < -1*Math.PI/3) {val t = (theta + 2*Math.PI/3)/(Math.PI/3); (1  ,1-t,1  )} // (1,1,0)..(0,1,0) ~ -2pi/3..-1pi/3
        else if (theta <  0*Math.PI/3) {val t = (theta + 1*Math.PI/3)/(Math.PI/3); (0  ,1  ,  t)} // (0,1,0)..(0,1,1) ~ -1pi/3.. 0pi/3
        else if (theta <  1*Math.PI/3) {val t = (theta + 0*Math.PI/3)/(Math.PI/3); (0  ,1-t,1  )} // (0,1,1)..(0,0,1) ~  0pi/3..1pi/3
        else if (theta <  2*Math.PI/3) {val t = (theta - 1*Math.PI/3)/(Math.PI/3); (  t,0  ,1  )} // (0,0,1)..(1,0,1) ~  1pi/3..2pi/3
        else                           {val t = (theta - 2*Math.PI/3)/(Math.PI/3); (1  ,0  ,t  )} // (1,0,1)..(1,0,0) ~  2pi/3..3pi/3
      
      val (r2,g2,b2): (Double, Double, Double) =
        if (radius < 16) (1,1,1)                                                                  // (1,1,1)..(1,1,1)
        else if (radius < 116) {val s = (radius -  16)/100; (s*r1+(1-s), s*g1+(1-s), s*b1+(1-s))} // (1,1,1)..(1,t,0)
        else if (radius < 132) (r1,g1,b1)                                                         // (1,t,0)..(1,t,0)
        else if (radius < 232) {val s = (radius - 132)/100; ((1-s)*r1, (1-s)*g1, (1-s)*b1)}       // (1,t,0)..(0,0,0)
        else if (radius < 248) (0,0,0)                                                            // (0,0,0)..(0,0,0)
        else                   {val s = (radius - 248)/100; (s, s, s)}                            // (0,0,0)..(1,1,1)
      
      ScageColor(r2.toFloat,g2.toFloat,b2.toFloat)
    }
  }
}
