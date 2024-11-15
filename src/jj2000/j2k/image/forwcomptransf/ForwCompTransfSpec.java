/* 
 * CVS identifier:
 * 
 * $Id: ForwCompTransfSpec.java,v 1.7 2001/05/08 16:10:18 grosbois Exp $
 * 
 * Class:                   ForwCompTransfSpec
 * 
 * Description:             Component Transformation specification for encoder
 * 
 * 
 * 
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.image.forwcomptransf;

import jj2000.j2k.image.CompTransfSpec;
import jj2000.j2k.util.ParameterList;
import jj2000.j2k.wavelet.FilterTypes;
import jj2000.j2k.wavelet.analysis.AnWTFilter;
import jj2000.j2k.wavelet.analysis.AnWTFilterSpec;

import java.util.StringTokenizer;

/**
 * This class extends CompTransfSpec class in order to hold encoder specific aspects of CompTransfSpec.
 *
 * @see CompTransfSpec
 *
 */
public class ForwCompTransfSpec extends CompTransfSpec implements FilterTypes
{
  /**
   * Constructs a new 'ForwCompTransfSpec' for the specified number of components and tiles, the wavelet filters type and the parameter of the option 'Mct'.
   * This constructor is called by the encoder. It also checks that the arguments belong to the recognized arguments list.
   *
   * <p>This constructor chose the component transformation type depending on the wavelet filters : RCT with w5x3 filter and ICT with w9x7 filter. Note: All
   * filters must use the same data type.</p>
   *
   * @param nt The number of tiles
   *
   * @param nc The number of components
   *
   * @param type the type of the specification module i.e. tile specific, component specific or both.
   *
   * @param wfs The wavelet filter specifications
   *
   * @param pl The ParameterList
     *
   */
  public ForwCompTransfSpec(int nt, int nc, byte type, AnWTFilterSpec wfs,
    ParameterList pl)
  {
    super(nt, nc, type);

    String param = pl.value("Mct");

    if (param == null)
    { // The option has not been specified

      // If less than three component, do not use any component
      // transformation 
      if (nc < 3)
      {
        setDefault("none");
        return;
      }
      // If the compression is lossless, uses RCT
      else if (pl.bool("lossless"))
      {
        setDefault("rct");
        return;
      }
      else
      {
        AnWTFilter[][] anfilt;
        int[] filtType = new int[nComp];
        for (int c = 0; c < 3; c++)
        {
          anfilt = (AnWTFilter[][]) wfs.getCompDef(c);
          filtType[c] = anfilt[0][0].getFilterType();
        }

        // Check that the three first components use the same filters
        boolean reject = false;
        for (int c = 1; c < 3; c++)
          if (filtType[c] != filtType[0])
            reject = true;

        if (reject)
          setDefault("none");
        else
        {
          anfilt = (AnWTFilter[][]) wfs.getCompDef(0);
          if (anfilt[0][0].getFilterType() == W9X7)
            setDefault("ict");
          else
            setDefault("rct");
        }
      }

      // Each tile receives a component transform specification
      // according the type of wavelet filters that are used by the
      // three first components
      for (int t = 0; t < nt; t++)
      {
        AnWTFilter[][] anfilt;
        int[] filtType = new int[nComp];
        for (int c = 0; c < 3; c++)
        {
          anfilt = (AnWTFilter[][]) wfs.getTileCompVal(t, c);
          filtType[c] = anfilt[0][0].getFilterType();
        }

        // Check that the three components use the same filters
        boolean reject = false;
        for (int c = 1; c < nComp; c++)
          if (filtType[c] != filtType[0])
            reject = true;

        if (reject)
          setTileDef(t, "none");
        else
        {
          anfilt = (AnWTFilter[][]) wfs.getTileCompVal(t, 0);
          if (anfilt[0][0].getFilterType() == W9X7)
            setTileDef(t, "ict");
          else
            setTileDef(t, "rct");
        }
      }
      return;
    }

    // Parse argument
    StringTokenizer stk = new StringTokenizer(param);
    String word; // current word
    byte curSpecType = SPEC_DEF; // Specification type of the
    // current parameter
    boolean[] tileSpec = null; // Tiles concerned by the
    // specification
    Boolean value;

    while (stk.hasMoreTokens())
    {
      word = stk.nextToken();

      switch (word.charAt(0))
      {
        case 't': // Tiles specification
          tileSpec = parseIdx(word, nTiles);
          if (curSpecType == SPEC_COMP_DEF)
            curSpecType = SPEC_TILE_COMP;
          else
            curSpecType = SPEC_TILE_DEF;
          break;
        case 'c': // Components specification
          throw new IllegalArgumentException("Component specific "
            + " parameters"
            + " not allowed with "
            + "'-Mct' option");
        default:
          if (word.equals("off"))
          {
            if (curSpecType == SPEC_DEF)
              setDefault("none");
            else if (curSpecType == SPEC_TILE_DEF)
              for (int i = tileSpec.length - 1; i >= 0; i--)
                if (tileSpec[i])
                  setTileDef(i, "none");
          }
          else if (word.equals("on"))
          {
            if (nc < 3)
              throw new IllegalArgumentException("Cannot use component"
                + " transformation on a "
                + "image with less than "
                + "three components");

            if (curSpecType == SPEC_DEF) // Set arbitrarily the default
              // value to RCT (later will be found the suitable
              // component transform for each tile)
              setDefault("rct");
            else if (curSpecType == SPEC_TILE_DEF)
              for (int i = tileSpec.length - 1; i >= 0; i--)
                if (tileSpec[i])
                  if (getFilterType(i, wfs) == W5X3)
                    setTileDef(i, "rct");
                  else
                    setTileDef(i, "ict");
          }
          else
            throw new IllegalArgumentException("Default parameter of "
              + "option Mct not"
              + " recognized: " + param);

          // Re-initialize
          curSpecType = SPEC_DEF;
          tileSpec = null;
          break;
      }
    }

    // Check that default value has been specified
    if (getDefault() == null)
    {
      // If not, set arbitrarily the default value to 'none' but
      // specifies explicitely a default value for each tile depending
      // on the wavelet transform that is used
      setDefault("none");

      for (int t = 0; t < nt; t++)
      {
        if (isTileSpecified(t))
          continue;

        AnWTFilter[][] anfilt;
        int[] filtType = new int[nComp];
        for (int c = 0; c < 3; c++)
        {
          anfilt = (AnWTFilter[][]) wfs.getTileCompVal(t, c);
          filtType[c] = anfilt[0][0].getFilterType();
        }

        // Check that the three components use the same filters
        boolean reject = false;
        for (int c = 1; c < nComp; c++)
          if (filtType[c] != filtType[0])
            reject = true;

        if (reject)
          setTileDef(t, "none");
        else
        {
          anfilt = (AnWTFilter[][]) wfs.getTileCompVal(t, 0);
          if (anfilt[0][0].getFilterType() == W9X7)
            setTileDef(t, "ict");
          else
            setTileDef(t, "rct");
        }
      }
    }

    // Check validity of component transformation of each tile compared to
    // the filter used.
    for (int t = nt - 1; t >= 0; t--)
      if (((String) getTileDef(t)).equals("none"))
        // No comp. transf is used. No check is needed
        continue;
      else if (((String) getTileDef(t)).equals("rct"))
      {
        // Tile is using Reversible component transform
        int filterType = getFilterType(t, wfs);
        switch (filterType)
        {
          case FilterTypes.W5X3: // OK
            break;
          case FilterTypes.W9X7: // Must use ICT
            if (isTileSpecified(t))
              // User has requested RCT -> Error
              throw new IllegalArgumentException("Cannot use RCT "
                + "with 9x7 filter "
                + "in tile " + t);
            else // Specify ICT for this tile
              setTileDef(t, "ict");
            break;
          default:
            throw new IllegalArgumentException("Default filter is "
              + "not JPEG 2000 part"
              + " I compliant");
        }
      }
      else
      { // ICT
        int filterType = getFilterType(t, wfs);
        switch (filterType)
        {
          case FilterTypes.W5X3: // Must use RCT
            if (isTileSpecified(t))
              // User has requested ICT -> Error
              throw new IllegalArgumentException("Cannot use ICT "
                + "with filter 5x3 "
                + "in tile " + t);
            else
              setTileDef(t, "rct");
            break;
          case FilterTypes.W9X7: // OK
            break;
          default:
            throw new IllegalArgumentException("Default filter is "
              + "not JPEG 2000 part"
              + " I compliant");

        }
      }
  }

  /**
   * Get the filter type common to all component of a given tile. If the tile index is -1, it searches common filter type of default specifications.
   *
   * @param t The tile index
   *
   * @param wfs The analysis filters specifications
   *
   * @return The filter type common to all the components 
     *
   */
  private int getFilterType(int t, AnWTFilterSpec wfs)
  {
    AnWTFilter[][] anfilt;
    int[] filtType = new int[nComp];
    for (int c = 0; c < nComp; c++)
    {
      if (t == -1)
        anfilt = (AnWTFilter[][]) wfs.getCompDef(c);
      else
        anfilt = (AnWTFilter[][]) wfs.getTileCompVal(t, c);
      filtType[c] = anfilt[0][0].getFilterType();
    }

    // Check that all filters are the same one
    boolean reject = false;
    for (int c = 1; c < nComp; c++)
      if (filtType[c] != filtType[0])
        reject = true;
    if (reject)
      throw new IllegalArgumentException("Can not use component"
        + " transformation when "
        + "components do not use "
        + "the same filters");
    return filtType[0];
  }
}
