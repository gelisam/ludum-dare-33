package com.gelisam.scage

import java.io._
import javax.sound.sampled._

// I can't believe it! The API really requires us to read from the disk every time??
case class Sound(private val file: File) {
  def play = {
    val clip = Sound.loadClip(file)
    clip.start
  }
}

object Sound {
  // Need to mess with the classloader, see http://stackoverflow.com/a/25083123/3286185
  def loadClip(file: File): Clip = {
    val cl = classOf[javax.sound.sampled.AudioSystem].getClassLoader
    val old = Thread.currentThread.getContextClassLoader
    try {
      Thread.currentThread.setContextClassLoader(cl)
      val clip: Clip = AudioSystem.getClip
      clip.open(AudioSystem.getAudioInputStream(file))
      clip
    } finally Thread.currentThread.setContextClassLoader(old)
  }
  
  def apply(filename: String): Sound =
    Sound(new File(filename))
}
