/* 
 * CVS identifier:
 * 
 * $Id: AnWTFilterSpec.java,v 1.27 2001/05/08 16:11:37 grosbois Exp $
 * 
 * Class:                   AnWTFilterSpec
 * 
 * Description:             Analysis filters specification
 * 
 * 
 * 
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.wavelet.analysis;

import jj2000.j2k.ModuleSpec;
import jj2000.j2k.quantization.QuantTypeSpec;
import jj2000.j2k.util.ParameterList;

import java.util.StringTokenizer;

/**
 * This class extends ModuleSpec class for analysis filters specification holding purpose.
 *
 * @see ModuleSpec
 *
 */
public class AnWTFilterSpec extends ModuleSpec
{
  /**
   * The reversible default filter
   */
  private final static String REV_FILTER_STR = "w5x3";
  /**
   * The non-reversible default filter
   */
  private final static String NON_REV_FILTER_STR = "w9x7";

  /**
   * Constructs a new 'AnWTFilterSpec' for the specified number of components and tiles.
   *
   * @param nt The number of tiles
   *
   * @param nc The number of components
   *
   * @param type the type of the specification module i.e. tile specific, component specific or both.
   *
   * @param qts Quantization specifications
   *
   * @param pl The ParameterList
     *
   */
  public AnWTFilterSpec(int nt, int nc, byte type,
    QuantTypeSpec qts, ParameterList pl)
  {
    super(nt, nc, type);

    // Check parameters
    pl.checkList(AnWTFilter.OPT_PREFIX,
      pl.toNameArray(AnWTFilter.getParameterInfo()));

    String param = pl.value("Ffilters");
    boolean isFilterSpecified = true;

    // No parameter specified
    if (param == null)
    {
      isFilterSpecified = false;

      // If lossless compression, uses the reversible filters in each
      // tile-components 
      if (pl.bool("lossless"))
      {
        setDefault(parseFilters(REV_FILTER_STR));
        return;
      }

      // If no filter is specified through the command-line, use
      // REV_FILTER_STR or NON_REV_FILTER_STR according to the
      // quantization type
      for (int t = nt - 1; t >= 0; t--)
        for (int c = nc - 1; c >= 0; c--)
          switch (qts.getSpecValType(t, c))
          {
            case SPEC_DEF:
              if (getDefault() == null)
              {
                if (pl.bool("lossless"))
                  setDefault(parseFilters(REV_FILTER_STR));
                if (((String) qts.getDefault()).
                  equals("reversible"))
                  setDefault(parseFilters(REV_FILTER_STR));
                else
                  setDefault(parseFilters(NON_REV_FILTER_STR));
              }
              specValType[t][c] = SPEC_DEF;
              break;
            case SPEC_COMP_DEF:
              if (!isCompSpecified(c))
                if (((String) qts.getCompDef(c)).
                  equals("reversible"))
                  setCompDef(c, parseFilters(REV_FILTER_STR));
                else
                  setCompDef(c, parseFilters(NON_REV_FILTER_STR));
              specValType[t][c] = SPEC_COMP_DEF;
              break;
            case SPEC_TILE_DEF:
              if (!isTileSpecified(t))
                if (((String) qts.getTileDef(t)).
                  equals("reversible"))
                  setTileDef(t, parseFilters(REV_FILTER_STR));
                else
                  setTileDef(t, parseFilters(NON_REV_FILTER_STR));
              specValType[t][c] = SPEC_TILE_DEF;
              break;
            case SPEC_TILE_COMP:
              if (!isTileCompSpecified(t, c))
                if (((String) qts.getTileCompVal(t, c)).
                  equals("reversible"))
                  setTileCompVal(t, c,
                    parseFilters(REV_FILTER_STR));
                else
                  setTileCompVal(t, c,
                    parseFilters(NON_REV_FILTER_STR));
              specValType[t][c] = SPEC_TILE_COMP;
              break;
            default:
              throw new IllegalArgumentException("Unsupported "
                + "specification "
                + "type");
          }
      return;
    }

    // Parse argument
    StringTokenizer stk = new StringTokenizer(param);
    String word; // current word
    byte curSpecType = SPEC_DEF; // Specification type of the
    // current parameter
    boolean[] tileSpec = null; // Tiles concerned by the specification
    boolean[] compSpec = null; // Components concerned by the specification
    AnWTFilter[][] filter;

    while (stk.hasMoreTokens())
    {
      word = stk.nextToken();

      switch (word.charAt(0))
      {
        case 't': // Tiles specification
        case 'T': // Tiles specification
          tileSpec = parseIdx(word, nTiles);
          if (curSpecType == SPEC_COMP_DEF)
            curSpecType = SPEC_TILE_COMP;
          else
            curSpecType = SPEC_TILE_DEF;
          break;
        case 'c': // Components specification
        case 'C': // Components specification
          compSpec = parseIdx(word, nComp);
          if (curSpecType == SPEC_TILE_DEF)
            curSpecType = SPEC_TILE_COMP;
          else
            curSpecType = SPEC_COMP_DEF;
          break;
        case 'w': // WT filters specification
        case 'W': // WT filters specification
          if (pl.bool("lossless")
            && word.equalsIgnoreCase("w9x7"))
            throw new IllegalArgumentException("Cannot use non "
              + "reversible "
              + "wavelet transform with"
              + " '-lossless' option");

          filter = parseFilters(word);
          if (curSpecType == SPEC_DEF)
            setDefault(filter);
          else if (curSpecType == SPEC_TILE_DEF)
          {
            for (int i = tileSpec.length - 1; i >= 0; i--)
              if (tileSpec[i])
                setTileDef(i, filter);
          }
          else if (curSpecType == SPEC_COMP_DEF)
          {
            for (int i = compSpec.length - 1; i >= 0; i--)
              if (compSpec[i])
                setCompDef(i, filter);
          }
          else
            for (int i = tileSpec.length - 1; i >= 0; i--)
              for (int j = compSpec.length - 1; j >= 0; j--)
                if (tileSpec[i] && compSpec[j])
                  setTileCompVal(i, j, filter);

          // Re-initialize
          curSpecType = SPEC_DEF;
          tileSpec = null;
          compSpec = null;
          break;

        default:
          throw new IllegalArgumentException("Bad construction for "
            + "parameter: " + word);
      }
    }

    // Check that default value has been specified
    if (getDefault() == null)
    {
      int ndefspec = 0;
      for (int t = nt - 1; t >= 0; t--)
        for (int c = nc - 1; c >= 0; c--)
          if (specValType[t][c] == SPEC_DEF)
            ndefspec++;

      // If some tile-component have received no specification, it takes
      // the default value defined in ParameterList
      if (ndefspec != 0)
        if (((String) qts.getDefault()).equals("reversible"))
          setDefault(parseFilters(REV_FILTER_STR));
        else
          setDefault(parseFilters(NON_REV_FILTER_STR));
      else
      {
        // All tile-component have been specified, takes the first
        // tile-component value as default.
        setDefault(getTileCompVal(0, 0));
        switch (specValType[0][0])
        {
          case SPEC_TILE_DEF:
            for (int c = nc - 1; c >= 0; c--)
              if (specValType[0][c] == SPEC_TILE_DEF)
                specValType[0][c] = SPEC_DEF;
            tileDef[0] = null;
            break;
          case SPEC_COMP_DEF:
            for (int t = nt - 1; t >= 0; t--)
              if (specValType[t][0] == SPEC_COMP_DEF)
                specValType[t][0] = SPEC_DEF;
            compDef[0] = null;
            break;
          case SPEC_TILE_COMP:
            specValType[0][0] = SPEC_DEF;
            tileCompVal.put("t0c0", null);
            break;
        }
      }
    }

    // Check consistency between filter and quantization type
    // specification
    for (int t = nt - 1; t >= 0; t--)
      for (int c = nc - 1; c >= 0; c--)
        // Reversible quantization
        if (((String) qts.getTileCompVal(t, c)).equals("reversible"))
        {
          // If filter is reversible, it is OK
          if (isReversible(t, c))
            continue;

          // If no filter has been defined, use reversible filter
          if (!isFilterSpecified)
            setTileCompVal(t, c, parseFilters(REV_FILTER_STR));
          else
            // Non reversible filter specified -> Error
            throw new IllegalArgumentException("Filter of "
              + "tile-component"
              + " (" + t + "," + c + ") does"
              + " not allow "
              + "reversible "
              + "quantization. "
              + "Specify '-Qtype "
              + "expounded' or "
              + "'-Qtype derived'"
              + "in "
              + "the command line.");
        }
        else
        { // No reversible quantization
          // No reversible filter -> OK
          if (!isReversible(t, c))
            continue;

          // If no filter has been specified, use non-reversible
          // filter
          if (!isFilterSpecified)
            setTileCompVal(t, c, parseFilters(NON_REV_FILTER_STR));
          else
            // Reversible filter specified -> Error
            throw new IllegalArgumentException("Filter of "
              + "tile-component"
              + " (" + t + "," + c + ") does"
              + " not allow "
              + "non-reversible "
              + "quantization. "
              + "Specify '-Qtype "
              + "reversible' in "
              + "the command line");
        }
  }

  /**
   * Parse filters from the given word
   *
   * @param word String to parse
   *
   * @return Analysis wavelet filter (first dimension: by direction, second dimension: by decomposition levels)
   */
  private AnWTFilter[][] parseFilters(String word)
  {
    AnWTFilter[][] filt = new AnWTFilter[2][1];
    if (word.equalsIgnoreCase("w5x3"))
    {
      filt[0][0] = new AnWTFilterIntLift5x3();
      filt[1][0] = new AnWTFilterIntLift5x3();
      return filt;
    }
    else if (word.equalsIgnoreCase("w9x7"))
    {
      filt[0][0] = new AnWTFilterFloatLift9x7();
      filt[1][0] = new AnWTFilterFloatLift9x7();
      return filt;
    }
    else
      throw new IllegalArgumentException("Non JPEG 2000 part I filter: "
        + word);
  }

  /**
   * Returns the data type used by the filters in this object, as defined in the 'DataBlk' interface for specified tile-component.
   *
   * @param t Tile index
   *
   * @param c Component index
   *
   * @return The data type of the filters in this object
   *
   * @see jj2000.j2k.image.DataBlk
     *
   */
  public int getWTDataType(int t, int c)
  {
    AnWTFilter[][] an = (AnWTFilter[][]) getSpec(t, c);
    return an[0][0].getDataType();
  }

  /**
   * Returns the horizontal analysis filters to be used in component 'n' and tile 't'.
   *
   * <P>The horizontal analysis filters are returned in an array of AnWTFilter. Each element contains the horizontal filter for each resolution level starting
   * with resolution level 1 (i.e. the analysis filter to go from resolution level 1 to resolution level 0). If there are less elements than the maximum
   * resolution level, then the last element is assumed to be repeated.
   *
   * @param t The tile index, in raster scan order
   *
   * @param c The component index.
   *
   * @return The array of horizontal analysis filters for component 'n' and tile 't'.
     *
   */
  public AnWTFilter[] getHFilters(int t, int c)
  {
    AnWTFilter[][] an = (AnWTFilter[][]) getSpec(t, c);
    return an[0];
  }

  /**
   * Returns the vertical analysis filters to be used in component 'n' and tile 't'.
   *
   * <P>The vertical analysis filters are returned in an array of AnWTFilter. Each element contains the vertical filter for each resolution level starting with
   * resolution level 1 (i.e. the analysis filter to go from resolution level 1 to resolution level 0). If there are less elements than the maximum resolution
   * level, then the last element is assumed to be repeated.
   *
   * @param t The tile index, in raster scan order
   *
   * @param c The component index.
   *
   * @return The array of horizontal analysis filters for component 'n' and tile 't'.
     *
   */
  public AnWTFilter[] getVFilters(int t, int c)
  {
    AnWTFilter[][] an = (AnWTFilter[][]) getSpec(t, c);
    return an[1];
  }

  /**
   * Debugging method
   */
  public String toString()
  {
    String str = "";
    AnWTFilter[][] an;

    str += "nTiles=" + nTiles + "\nnComp=" + nComp + "\n\n";

    for (int t = 0; t < nTiles; t++)
      for (int c = 0; c < nComp; c++)
      {
        an = (AnWTFilter[][]) getSpec(t, c);

        str += "(t:" + t + ",c:" + c + ")\n";

        // Horizontal filters
        str += "\tH:";
        for (int i = 0; i < an[0].length; i++)
          str += " " + an[0][i];
        // Horizontal filters
        str += "\n\tV:";
        for (int i = 0; i < an[1].length; i++)
          str += " " + an[1][i];
        str += "\n";
      }

    return str;
  }

  /**
   * Check the reversibility of filters contained is the given tile-component.
   *
   * @param t The index of the tile
   *
   * @param c The index of the component
     *
   */
  public boolean isReversible(int t, int c)
  {
    // Note: no need to buffer the result since this method is
    // normally called once per tile-component.
    AnWTFilter[] hfilter = getHFilters(t, c),
      vfilter = getVFilters(t, c);

    // As soon as a filter is not reversible, false can be returned
    for (int i = hfilter.length - 1; i >= 0; i--)
      if (!hfilter[i].isReversible() || !vfilter[i].isReversible())
        return false;
    return true;
  }
}
