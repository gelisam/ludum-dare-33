// Place your sound files in the src/main/resources/resources/sounds folder.
package com.gelisam.scage

import org.newdawn.slick.util.ResourceLoader
import org.newdawn.slick.openal.{SoundStore, Audio, AudioLoader}

import com.github.dunnololda.scage.Scage

class Sound(audio: Audio) {
  def play = if (Sound.enabled) {
    audio.playAsSoundEffect(1, 1, false) 
  }
  
  def stop = if (Sound.enabled) {
    audio.stop
  }
}

class Music(audio: Audio) {
  def play = if (Sound.enabled) {
    audio.playAsMusic(1, 1, true) 
  }
}

object Sound {
  var enabled = true
  
  def init(scage: Scage) = {
    scage.action {
      SoundStore.get().poll(0)
    }
  }
  
  def apply(filename: String): Sound = new Sound({
    try {
      val fullFilename = s"resources/sounds/${filename}"
      val url = ResourceLoader.getResource(fullFilename)
      AudioLoader.getAudio("OGG", url.openStream())
    } catch {
      case e: java.lang.RuntimeException => {
        println("sound files not found, disabling sounds.")
        Sound.enabled = false
        null
      }
    }
  })
}

object Music {
  def apply(filename: String): Sound = new Sound({
    try {
      val fullFilename = s"resources/sounds/${filename}"
      val url = ResourceLoader.getResource(fullFilename)
      AudioLoader.getStreamingAudio("OGG", url)
    } catch {
      case e: java.lang.RuntimeException => {
        println("sound files not found, disabling sounds.")
        Sound.enabled = false
        null
      }
    }
  })
}
