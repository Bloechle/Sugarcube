/*
 * CVS identifier:
 *
 * $Id: ROI.java,v 1.3 2001/01/03 15:08:15 qtxjoas Exp $
 *
 * Class:                   ROI
 *
 * Description:             This class describes a single ROI
 *
 *
 *
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 */
package jj2000.j2k.roi.encoder;

import jj2000.j2k.image.input.ImgReaderPGM;

/**
 * This class contains the shape of a single ROI. In the current implementation only rectangles and circles are supported.
 *
 * @see ROIMaskGenerator
 */
public class ROI
{
  /**
   * ImgReaderPGM object with the arbrtrary ROI
   */
  public ImgReaderPGM maskPGM = null;
  /**
   * Where or not the ROI shape is arbitrary
   */
  public boolean arbShape;
  /**
   * Flag indicating whether the ROI is rectangular or not
   */
  public boolean rect;
  /**
   * The components for which the ROI is relevant
   */
  public int comp;
  /**
   * x coordinate of upper left corner of rectangular ROI
   */
  public int ulx;
  /**
   * y coordinate of upper left corner of rectangular ROI
   */
  public int uly;
  /**
   * width of rectangular ROI
   */
  public int w;
  /**
   * height of rectangular ROI
   */
  public int h;
  /**
   * x coordinate of center of circular ROI
   */
  public int x;
  /**
   * y coordinate of center of circular ROI
   */
  public int y;
  /**
   * radius of circular ROI
   */
  public int r;

  /**
   * Constructor for ROI with arbitrary shape
   *
   * @param comp The component the ROI belongs to
   *
   * @param maskPGM ImgReaderPGM containing the ROI
   */
  public ROI(int comp, ImgReaderPGM maskPGM)
  {
    arbShape = true;
    rect = false;
    this.comp = comp;
    this.maskPGM = maskPGM;
  }

  /**
   * Constructor for rectangular ROIs
   *
   * @param comp The component the ROI belongs to
   *
   * @param x x-coordinate of upper left corner of ROI
   *
   * @param y y-coordinate of upper left corner of ROI
   *
   * @param w width of ROI
   *
   * @param h height of ROI
   */
  public ROI(int comp, int ulx, int uly, int w, int h)
  {
    arbShape = false;
    this.comp = comp;
    this.ulx = ulx;
    this.uly = uly;
    this.w = w;
    this.h = h;
    rect = true;
  }

  /**
   * Constructor for circular ROIs
   *
   * @param comp The component the ROI belongs to
   *
   * @param x x-coordinate of center of ROI
   *
   * @param y y-coordinate of center of ROI
   *
   * @param w radius of ROI
   */
  public ROI(int comp, int x, int y, int rad)
  {
    arbShape = false;
    this.comp = comp;
    this.x = x;
    this.y = y;
    this.r = rad;
  }

  /**
   * This function prints all relevant data for the ROI
   */
  public String toString()
  {
    if (arbShape)
      return "ROI with arbitrary shape, PGM file= " + maskPGM;
    else if (rect)
      return "Rectangular ROI, comp=" + comp + " ulx=" + ulx + " uly=" + uly
        + " w=" + w + " h=" + h;
    else
      return "Circular ROI,  comp=" + comp + " x=" + x + " y=" + y
        + " radius=" + r;

  }
}
