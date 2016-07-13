/*
 * Amadeus Confidential Information:
 * Unauthorized use and disclosure strictly forbidden.
 * @1998-2015 - Amadeus s.a.s - All Rights Reserved.
 */
package com.amadeus.fun;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

/**
 * 
 * 
 * @author bmallick
 */
public class ResizeImage {

  // Initialize log4j
  public static final Logger logger = Logger.getLogger(ResizeImage.class);

  // Resize image
  public void resizeImage(String image, int width, int height) {
    try {
      BufferedImage originalImage = ImageIO.read(new File(image));
      if (originalImage.getWidth() != width || originalImage.getHeight() != height) {
        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
        BufferedImage resizeImageHintJpg = resizeImageWithHint(originalImage, type, width, height);
        ImageIO.write(resizeImageHintJpg, "jpg", new File(image));
      }
    }
    catch (IOException e) {
      logger.error("Error cropping image : " + e.getMessage());
    }
  }

  // Resize image with hint
  private BufferedImage resizeImageWithHint(BufferedImage originalImage, int type, int width, int height) {
    BufferedImage resizedImage = new BufferedImage(width, height, type);
    Graphics2D g = resizedImage.createGraphics();
    g.drawImage(originalImage, 0, 0, width, height, null);
    g.dispose();
    g.setComposite(AlphaComposite.Src);
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    return resizedImage;
  }
}