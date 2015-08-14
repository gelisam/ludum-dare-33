package com.gelisam.scage

import java.io._
import javax.sound.sampled._

// I can't believe it! The API really requires us to read from the disk every time??
case class Sound(private val file: File) {
  def play = {
    val clip: Clip = AudioSystem.getClip
    clip.open(AudioSystem.getAudioInputStream(file))
    clip.start
  }
}

object Sound {
  def apply(filename: String): Sound =
    Sound(new File(filename))
}
