// Place your PNG files in the src/main/resources/resources/images folder.
package com.gelisam.scage

import com.github.dunnololda.scage.ScageLib._
import org.lwjgl.opengl.GL11

class Sprite(spriteId: Int) {
  def render(offset: Vec = Vec(0, 0)) = {
    // make pixels crisp
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    
    drawDisplayList(spriteId, offset)
  }
}

object Sprite {
  def init(scage: Object) = {
  }
  
  def apply(filename: String, scale: Int = 1): Sprite = {
    val fullFilename = s"resources/images/${filename}"
    val texture = getTexture(fullFilename)
		val width: Float = texture.getTextureWidth
		val height: Float = texture.getTextureHeight
    val spriteId = image(filename, width*scale, height*scale, 0, 0, width, height)
    new Sprite(spriteId)
  }
}
