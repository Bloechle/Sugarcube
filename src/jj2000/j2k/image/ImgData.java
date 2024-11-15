/*
 * CVS Identifier:
 *
 * $Id: ImgData.java,v 1.10 2001/09/14 09:17:46 grosbois Exp $
 *
 * Interface:           ImgData
 *
 * Description:         The interface for classes that provide image
 *                      data.
 *
 *
 *
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.image;

/**
 * This interface defines methods to access image attributes (width, height, number of components, etc.). The image can be tiled or not (i.e. if the image is
 * not tiled then there is only 1 tile). It should be implemented by all classes that provide image data, such as image file readers, color transforms, wavelet
 * transforms, etc. This interface, however, does not define methods to transfer image data (i.e. pixel data), that is defined by other interfaces, such as
 * 'BlkImgDataSrc'.
 *
 * @see BlkImgDataSrc
 *
 */
public interface ImgData
{
  /**
   * Returns the overall width of the current tile in pixels. This is the tile's width without accounting for any component subsampling. This is also referred
   * as the reference grid width in the current tile.
   *
   * @return The total current tile's width in pixels.
     *
   */
  public int getTileWidth();

  /**
   * Returns the overall height of the current tile in pixels. This is the tile's height without accounting for any component subsampling. This is also referred
   * as the reference grid height in the current tile.
   *
   * @return The total current tile's height in pixels.
     *
   */
  public int getTileHeight();

  /**
   * Returns the nominal tiles width
   */
  public int getNomTileWidth();

  /**
   * Returns the nominal tiles height
   */
  public int getNomTileHeight();

  /**
   * Returns the overall width of the image in pixels. This is the image's width without accounting for any component subsampling or tiling.
   *
   * @return The total image's width in pixels.
     *
   */
  public int getImgWidth();

  /**
   * Returns the overall height of the image in pixels. This is the image's height without accounting for any component subsampling or tiling.
   *
   * @return The total image's height in pixels.
     *
   */
  public int getImgHeight();

  /**
   * Returns the number of components in the image.
   *
   * @return The number of components in the image.
     *
   */
  public int getNumComps();

  /**
   * Returns the component subsampling factor in the horizontal direction, for the specified component. This is, approximately, the ratio of dimensions between
   * the reference grid and the component itself, see the 'ImgData' interface desription for details.
   *
   * @param c The index of the component (between 0 and N-1)
   *
   * @return The horizontal subsampling factor of component 'c'
   *
   * @see ImgData
     *
   */
  public int getCompSubsX(int c);

  /**
   * Returns the component subsampling factor in the vertical direction, for the specified component. This is, approximately, the ratio of dimensions between
   * the reference grid and the component itself, see the 'ImgData' interface desription for details.
   *
   * @param c The index of the component (between 0 and N-1)
   *
   * @return The vertical subsampling factor of component 'c'
   *
   * @see ImgData
     *
   */
  public int getCompSubsY(int c);

  /**
   * Returns the width in pixels of the specified tile-component
   *
   * @param t Tile index
   *
   * @param c The index of the component, from 0 to N-1.
   *
   * @return The width in pixels of component <tt>c</tt> in tile<tt>t</tt>.
     *
   */
  public int getTileCompWidth(int t, int c);

  /**
   * Returns the height in pixels of the specified tile-component.
   *
   * @param t The tile index.
   *
   * @param c The index of the component, from 0 to N-1.
   *
   * @return The height in pixels of component <tt>c</tt> in tile <tt>t</tt>.
     *
   */
  public int getTileCompHeight(int t, int c);

  /**
   * Returns the width in pixels of the specified component in the overall image.
   *
   * @param c The index of the component, from 0 to N-1.
   *
   * @return The width in pixels of component <tt>c</tt> in the overall image.
     *
   */
  public int getCompImgWidth(int c);

  /**
   * Returns the height in pixels of the specified component in the overall image.
   *
   * @param c The index of the component, from 0 to N-1.
   *
   * @return The height in pixels of component <tt>n</tt> in the overall image.
     *
   */
  public int getCompImgHeight(int c);

  /**
   * Returns the number of bits, referred to as the "range bits", corresponding to the nominal range of the image data in the specified component. If this
   * number is <i>n</b> then for unsigned data the nominal range is between 0 and 2^b-1, and for signed data it is between -2^(b-1) and 2^(b-1)-1. In the case
   * of transformed data which is not in the image domain (e.g., wavelet coefficients), this method returns the "range bits" of the image data that generated
   * the coefficients.
   *
   * @param c The index of the component.
   *
   * @return The number of bits corresponding to the nominal range of the image data (in the image domain).
     *
   */
  public int getNomRangeBits(int c);

  /**
   * Changes the current tile, given the new indices. An IllegalArgumentException is thrown if the coordinates do not correspond to a valid tile.
   *
   * @param x The horizontal index of the tile.
   *
   * @param y The vertical index of the new tile.
     *
   */
  public void setTile(int x, int y);

  /**
   * Advances to the next tile, in standard scan-line order (by rows then columns). An NoNextElementException is thrown if the current tile is the last one
   * (i.e. there is no next tile).
     *
   */
  public void nextTile();

  /**
   * Returns the indixes of the current tile. These are the horizontal and vertical indexes of the current tile.
   *
   * @param co If not null this object is used to return the information. If null a new one is created and returned.
   *
   * @return The current tile's indices (vertical and horizontal indexes).
     *
   */
  public Coord getTile(Coord co);

  /**
   * Returns the index of the current tile, relative to a standard scan-line order.
   *
   * @return The current tile's index (starts at 0).
     *
   */
  public int getTileIdx();

  /**
   * Returns the horizontal tile partition offset in the reference grid
   */
  public int getTilePartULX();

  /**
   * Returns the vertical tile partition offset in the reference grid
   */
  public int getTilePartULY();

  /**
   * Returns the horizontal coordinate of the upper-left corner of the specified component in the current tile.
   *
   * @param c The index of the component.
     *
   */
  public int getCompULX(int c);

  /**
   * Returns the vertical coordinate of the upper-left corner of the specified component in the current tile.
   *
   * @param c The index of the component.
     *
   */
  public int getCompULY(int c);

  /**
   * Returns the horizontal coordinate of the image origin, the top-left corner, in the canvas system, on the reference grid.
   *
   * @return The horizontal coordinate of the image origin in the canvas system, on the reference grid.
     *
   */
  public int getImgULX();

  /**
   * Returns the vertical coordinate of the image origin, the top-left corner, in the canvas system, on the reference grid.
   *
   * @return The vertical coordinate of the image origin in the canvas system, on the reference grid.
     *
   */
  public int getImgULY();

  /**
   * Returns the number of tiles in the horizontal and vertical directions.
   *
   * @param co If not null this object is used to return the information. If null a new one is created and returned.
   *
   * @return The number of tiles in the horizontal (Coord.x) and vertical (Coord.y) directions.
     *
   */
  public Coord getNumTiles(Coord co);

  /**
   * Returns the total number of tiles in the image.
   *
   * @return The total number of tiles in the image.
     *
   */
  public int getNumTiles();
}
