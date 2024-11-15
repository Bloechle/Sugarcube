/* 
 * CVS identifier:
 * 
 * $Id: GuardBitsSpec.java,v 1.13 2000/09/19 14:11:01 grosbois Exp $
 * 
 * Class:                   GuardBitsSpec
 * 
 * Description:             Guard bits specifications
 * 
 * COPYRIGHT:
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * 
 * 
 * 
 */
package jj2000.j2k.quantization;

import jj2000.j2k.ModuleSpec;
import jj2000.j2k.util.ParameterList;

import java.util.StringTokenizer;

/**
 * This class extends ModuleSpec class in order to hold specifications about number of guard bits in each tile-component.
 *
 * @see ModuleSpec
 *
 */
public class GuardBitsSpec extends ModuleSpec
{
  /**
   * Constructs an empty 'GuardBitsSpec' with specified number of tile and components. This constructor is called by the decoder.
   *
   * @param nt Number of tiles
   *
   * @param nc Number of components
   *
   * @param type the type of the specification module i.e. tile specific, component specific or both.
     *
   */
  public GuardBitsSpec(int nt, int nc, byte type)
  {
    super(nt, nc, type);
  }

  /**
   * Constructs a new 'GuardBitsSpec' for the specified number of components and tiles and the arguments of "-Qguard_bits" option.
   *
   * @param nt The number of tiles
   *
   * @param nc The number of components
   *
   * @param type the type of the specification module i.e. tile specific, component specific or both.
   *
   * @param pl The ParameterList
     *
   */
  public GuardBitsSpec(int nt, int nc, byte type, ParameterList pl)
  {
    super(nt, nc, type);

    String param = pl.value("Qguard_bits");
    if (param == null)
      throw new IllegalArgumentException("Qguard_bits option not "
        + "specified");

    // Parse argument
    StringTokenizer stk = new StringTokenizer(param);
    String word; // current word
    byte curSpecType = SPEC_DEF; // Specification type of the
    // current parameter
    boolean[] tileSpec = null; // Tiles concerned by the specification
    boolean[] compSpec = null; // Components concerned by the specification
    Integer value; // value of the guard bits

    while (stk.hasMoreTokens())
    {
      word = stk.nextToken().toLowerCase();

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
          compSpec = parseIdx(word, nComp);
          if (curSpecType == SPEC_TILE_DEF)
            curSpecType = SPEC_TILE_COMP;
          else
            curSpecType = SPEC_COMP_DEF;
          break;
        default: // Step size value
          try
          {
            value = new Integer(word);
          }
          catch (NumberFormatException e)
          {
            throw new IllegalArgumentException("Bad parameter for "
              + "-Qguard_bits option"
              + " : " + word);
          }

          if (value.floatValue() <= 0.0f)
            throw new IllegalArgumentException("Guard bits value "
              + "must be positive : "
              + value);


          if (curSpecType == SPEC_DEF)
            setDefault(value);
          else if (curSpecType == SPEC_TILE_DEF)
          {
            for (int i = tileSpec.length - 1; i >= 0; i--)
              if (tileSpec[i])
                setTileDef(i, value);
          }
          else if (curSpecType == SPEC_COMP_DEF)
          {
            for (int i = compSpec.length - 1; i >= 0; i--)
              if (compSpec[i])
                setCompDef(i, value);
          }
          else
            for (int i = tileSpec.length - 1; i >= 0; i--)
              for (int j = compSpec.length - 1; j >= 0; j--)
                if (tileSpec[i] && compSpec[j])
                  setTileCompVal(i, j, value);

          // Re-initialize
          curSpecType = SPEC_DEF;
          tileSpec = null;
          compSpec = null;
          break;
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
        setDefault(new Integer(pl.getDefaultParameterList().value("Qguard_bits")));
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
  }
}
