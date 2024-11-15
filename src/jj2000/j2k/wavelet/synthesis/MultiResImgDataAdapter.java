/* 
 * CVS identifier:
 * 
 * $Id: MultiResImgDataAdapter.java,v 1.10 2002/07/25 15:11:55 grosbois Exp $
 * 
 * Class:                   MultiResImgDataAdapter
 * 
 * Description:             A default implementation of the MultiResImgData
 *                          interface that has and MultiResImgData source
 *                          and just returns the values of the source.
 * 
 * 
 * 
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.wavelet.synthesis;

import jj2000.j2k.image.Coord;

/**
 * This class provides a default implementation for the methods of the 'MultiResImgData' interface. The default implementation consists just in returning the
 * value of the source, where the source is another 'MultiResImgData' object.
 *
 * <p>This abstract class can be used to facilitate the development of other classes that implement 'MultiResImgData'. For example a dequantizer can inherit
 * from this class and all the trivial methods do not have to be reimplemented.</p>
 *
 * <p>If the default implementation of a method provided in this class does not suit a particular implementation of the 'MultiResImgData' interface, the method
 * can be overriden to implement the proper behaviour.</p>
 *
 * @see MultiResImgData
 *
 */
public abstract class MultiResImgDataAdapter implements MultiResImgData
{
  /**
   * Index of the current tile
   */
  protected int tIdx = 0;
  /**
   * The MultiResImgData source
   */
  protected MultiResImgData mressrc;

  /**
   * Instantiates the MultiResImgDataAdapter object specifying the MultiResImgData source.
   *
   * @param src From where to obrtain the MultiResImgData values.
     *
   */
  protected MultiResImgDataAdapter(MultiResImgData src)
  {
    mressrc = src;
  }

  /**
   * Returns the overall width of the current tile in pixels, for the given resolution level. This is the tile's width without accounting for any component
   * subsampling.
   *
   * <p>This default implementation returns the value of the source.</p>
   *
   * @param rl The resolution level, from 0 to L.
   *
   * @return The total current tile's width in pixels.
     *
   */
  public int getTileWidth(int rl)
  {
    return mressrc.getTileWidth(rl);
  }

  /**
   * Returns the overall height of the current tile in pixels, for the given resolution level. This is the tile's height without accounting for any component
   * subsampling.
   *
   * <p>This default implementation returns the value of the source.</p>
   *
   * @param rl The resolution level, from 0 to L.
   *
   * @return The total current tile's height in pixels.
     *
   */
  public int getTileHeight(int rl)
  {
    return mressrc.getTileHeight(rl);
  }

  /**
   * Returns the nominal tiles width
   */
  public int getNomTileWidth()
  {
    return mressrc.getNomTileWidth();
  }

  /**
   * Returns the nominal tiles height
   */
  public int getNomTileHeight()
  {
    return mressrc.getNomTileHeight();
  }

  /**
   * Returns the overall width of the image in pixels, for the given resolution level. This is the image's width without accounting for any component
   * subsampling or tiling.
   *
   * <p>This default implementation returns the value of the source.</p>
   *
   * @param rl The resolution level, from 0 to L.
   *
   * @return The total image's width in pixels.
     *
   */
  public int getImgWidth(int rl)
  {
    return mressrc.getImgWidth(rl);
  }

  /**
   * Returns the overall height of the image in pixels, for the given resolution level. This is the image's height without accounting for any component
   * subsampling or tiling.
   *
   * <p>This default implementation returns the value of the source.</p>
   *
   * @param rl The resolution level, from 0 to L.
   *
   * @return The total image's height in pixels.
     *
   */
  public int getImgHeight(int rl)
  {
    return mressrc.getImgHeight(rl);
  }

  /**
   * Returns the number of components in the image.
   *
   * <p>This default implementation returns the value of the source.</p>
   *
   * @return The number of components in the image.
     *
   */
  public int getNumComps()
  {
    return mressrc.getNumComps();
  }

  /**
   * Returns the component subsampling factor in the horizontal direction, for the specified component. This is, approximately, the ratio of dimensions between
   * the reference grid and the component itself, see the 'ImgData' interface desription for details.
   *
   * <p>This default implementation returns the value of the source.</p>
   *
   * @param c The index of the component (between 0 and N-1)
   *
   * @return The horizontal subsampling factor of component 'c'
   *
   * @see jj2000.j2k.image.ImgData
     *
   */
  public int getCompSubsX(int c)
  {
    return mressrc.getCompSubsX(c);
  }

  /**
   * Returns the component subsampling factor in the vertical direction, for the specified component. This is, approximately, the ratio of dimensions between
   * the reference grid and the component itself, see the 'ImgData' interface desription for details.
   *
   * <p>This default implementation returns the value of the source.</p>
   *
   * @param c The index of the component (between 0 and N-1)
   *
   * @return The vertical subsampling factor of component 'c'
   *
   * @see jj2000.j2k.image.ImgData
     *
   */
  public int getCompSubsY(int c)
  {
    return mressrc.getCompSubsY(c);
  }

  /**
   * Returns the width in pixels of the specified tile-component for the given resolution level.
   *
   * <p>This default implementation returns the value of the source.</p>
   *
   * @param t Tile index.
   *
   * @param c The index of the component, from 0 to N-1.
   *
   * @param rl The resolution level, from 0 to L.
   *
   * @return The width in pixels of component <tt>c</tt> in tile <tt>t</tt> for resolution level <tt>rl</tt>.
     *
   */
  public int getTileCompWidth(int t, int c, int rl)
  {
    return mressrc.getTileCompWidth(t, c, rl);
  }

  /**
   * Returns the height in pixels of the specified tile-component for the given resolution level.
   *
   * <p>This default implementation returns the value of the source.</p>
   *
   * @param t The tile index.
   *
   * @param c The index of the component, from 0 to N-1.
   *
   * @param rl The resolution level, from 0 to L.
   *
   * @return The height in pixels of component <tt>c</tt> in tile <tt>t</tt>. 
     *
   */
  public int getTileCompHeight(int t, int c, int rl)
  {
    return mressrc.getTileCompHeight(t, c, rl);
  }

  /**
   * Returns the width in pixels of the specified component in the overall image, for the given resolution level.
   *
   * <p>This default implementation returns the value of the source.</p>
   *
   * @param c The index of the component, from 0 to N-1.
   *
   * @param rl The resolution level, from 0 to L.
   *
   * @return The width in pixels of component <tt>c</tt> in the overall image.
     *
   */
  public int getCompImgWidth(int c, int rl)
  {
    return mressrc.getCompImgWidth(c, rl);
  }

  /**
   * Returns the height in pixels of the specified component in the overall image, for the given resolution level.
   *
   * <P>This default implementation returns the value of the source.
   *
   * @param c The index of the component, from 0 to N-1.
   *
   * @param rl The resolution level, from 0 to L.
   *
   * @return The height in pixels of component <tt>c</tt> in the overall image.
     *
   */
  public int getCompImgHeight(int c, int rl)
  {
    return mressrc.getCompImgHeight(c, rl);
  }

  /**
   * Changes the current tile, given the new indexes. An IllegalArgumentException is thrown if the indexes do not correspond to a valid tile.
   *
   * <p>This default implementation just changes the tile in the source.</p>
   *
   * @param x The horizontal indexes the tile.
   *
   * @param y The vertical indexes of the new tile.
     *
   */
  public void setTile(int x, int y)
  {
    mressrc.setTile(x, y);
    tIdx = getTileIdx();
  }

  /**
   * Advances to the next tile, in standard scan-line order (by rows then columns). An NoNextElementException is thrown if the current tile is the last one
   * (i.e. there is no next tile).
   *
   * <p>This default implementation just changes the tile in the source.</p>
     *
   */
  public void nextTile()
  {
    mressrc.nextTile();
    tIdx = getTileIdx();
  }

  /**
   * Returns the indexes of the current tile. These are the horizontal and vertical indexes of the current tile.
   *
   * <p>This default implementation returns the value of the source.</p>
   *
   * @param co If not null this object is used to return the information. If null a new one is created and returned.
   *
   * @return The current tile's indexes (vertical and horizontal indexes).
     *
   */
  public Coord getTile(Coord co)
  {
    return mressrc.getTile(co);
  }

  /**
   * Returns the index of the current tile, relative to a standard scan-line order.
   *
   * <p>This default implementation returns the value of the source.</p>
   *
   * @return The current tile's index (starts at 0).
     *
   */
  public int getTileIdx()
  {
    return mressrc.getTileIdx();
  }

  /**
   * Returns the horizontal coordinate of the upper-left corner of the specified resolution level in the given component of the current tile.
   *
   * <p>This default implementation returns the value of the source.</p>
   *
   * @param c The component index.
   *
   * @param rl The resolution level index.
     *
   */
  public int getResULX(int c, int rl)
  {
    return mressrc.getResULX(c, rl);
  }

  /**
   * Returns the vertical coordinate of the upper-left corner of the specified resolution in the given component of the current tile.
   *
   * <p>This default implementation returns the value of the source.</p>
   *
   * @param c The component index.
   *
   * @param rl The resolution level index.
     *
   */
  public int getResULY(int c, int rl)
  {
    return mressrc.getResULY(c, rl);
  }

  /**
   * Returns the horizontal tile partition offset in the reference grid
   */
  public int getTilePartULX()
  {
    return mressrc.getTilePartULX();
  }

  /**
   * Returns the vertical tile partition offset in the reference grid
   */
  public int getTilePartULY()
  {
    return mressrc.getTilePartULY();
  }

  /**
   * Returns the horizontal coordinate of the image origin, the top-left corner, in the canvas system, on the reference grid at the specified resolution level.
   *
   * <p>This default implementation returns the value of the source.</p>
   *
   * @param rl The resolution level, from 0 to L.
   *
   * @return The horizontal coordinate of the image origin in the canvas system, on the reference grid.
     *
   */
  public int getImgULX(int rl)
  {
    return mressrc.getImgULX(rl);
  }

  /**
   * Returns the vertical coordinate of the image origin, the top-left corner, in the canvas system, on the reference grid at the specified resolution level.
   *
   * <p>This default implementation returns the value of the source.</p>
   *
   * @param rl The resolution level, from 0 to L.
   *
   * @return The vertical coordinate of the image origin in the canvas system, on the reference grid.
     *
   */
  public int getImgULY(int rl)
  {
    return mressrc.getImgULY(rl);
  }

  /**
   * Returns the number of tiles in the horizontal and vertical directions.
   *
   * <p>This default implementation returns the value of the source.</p>
   *
   * @param co If not null this object is used to return the information. If null a new one is created and returned.
   *
   * @return The number of tiles in the horizontal (Coord.x) and vertical (Coord.y) directions.
     *
   */
  public Coord getNumTiles(Coord co)
  {
    return mressrc.getNumTiles(co);
  }

  /**
   * Returns the total number of tiles in the image.
   *
   * <p>This default implementation returns the value of the source.</p>
   *
   * @return The total number of tiles in the image.
     *
   */
  public int getNumTiles()
  {
    return mressrc.getNumTiles();
  }
}
