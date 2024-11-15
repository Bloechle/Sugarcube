/*
 * CVS identifier:
 *
 * $Id: SubbandROIMask.java,v 1.2 2001/02/28 15:12:44 grosbois Exp $
 *
 * Class:                   ROI
 *
 * Description:             This class describes the ROI mask for a subband
 *
 *
 *
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 *  */
package jj2000.j2k.roi.encoder;

/**
 * This abstract class describes the ROI mask for a single subband. Each object of the class contains the mask for a particular subband and also has references
 * to the masks of the children subbands of the subband corresponding to this mask.
 */
public abstract class SubbandROIMask
{
  /**
   * The subband masks of the child LL
   */
  protected SubbandROIMask ll;
  /**
   * The subband masks of the child LH
   */
  protected SubbandROIMask lh;
  /**
   * The subband masks of the child HL
   */
  protected SubbandROIMask hl;
  /**
   * The subband masks of the child HH
   */
  protected SubbandROIMask hh;
  /**
   * Flag indicating whether this subband mask is a node or not
   */
  protected boolean isNode;
  /**
   * Horizontal uper-left coordinate of the subband mask
   */
  public int ulx;
  /**
   * Vertical uper-left coordinate of the subband mask
   */
  public int uly;
  /**
   * Width of the subband mask
   */
  public int w;
  /**
   * Height of the subband mask
   */
  public int h;

  /**
   * The constructor of the SubbandROIMask takes the dimensions of the subband as parameters
   *
   * @param ulx The upper left x coordinate of corresponding subband
   *
   * @param uly The upper left y coordinate of corresponding subband
   *
   * @param w The width of corresponding subband
   *
   * @param h The height of corresponding subband
     *
   */
  public SubbandROIMask(int ulx, int uly, int w, int h)
  {
    this.ulx = ulx;
    this.uly = uly;
    this.w = w;
    this.h = h;
  }

  /**
   * Returns a reference to the Subband mask element to which the specified point belongs. The specified point must be inside this (i.e. the one defined by this
   * object) subband mask. This method searches through the tree.
   *
   * @param x horizontal coordinate of the specified point.
   *
   * @param y horizontal coordinate of the specified point.
     *
   */
  public SubbandROIMask getSubbandRectROIMask(int x, int y)
  {
    SubbandROIMask cur, hhs;

    // Check that we are inside this subband
    if (x < ulx || y < uly || x >= ulx + w || y >= uly + h)
      throw new IllegalArgumentException();

    cur = this;
    while (cur.isNode)
    {
      hhs = cur.hh;
      // While we are still at a node -> continue
      if (x < hhs.ulx)
        // Is the result of horizontal low-pass
        if (y < hhs.uly)
          // Vertical low-pass
          cur = cur.ll;
        else
          // Vertical high-pass
          cur = cur.lh;
      else
        // Is the result of horizontal high-pass
        if (y < hhs.uly)
          // Vertical low-pass
          cur = cur.hl;
        else
          // Vertical high-pass
          cur = cur.hh;
    }
    return cur;
  }
}
