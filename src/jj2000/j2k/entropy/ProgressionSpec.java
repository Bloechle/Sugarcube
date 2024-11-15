/*
 * CVS identifier:
 *
 * $Id: ProgressionSpec.java,v 1.19 2001/05/02 14:08:42 grosbois Exp $
 *
 * Class:                   ProgressionSpec
 *
 * Description:             Specification of the progression(s) type(s) and
 *                          changes of progression.
 *
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.entropy;

import jj2000.j2k.IntegerSpec;
import jj2000.j2k.ModuleSpec;
import jj2000.j2k.codestream.ProgressionType;
import jj2000.j2k.util.ParameterList;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * This class extends ModuleSpec class for progression type(s) and progression order changes holding purposes.
 *
 * <p>It stores the progression type(s) used in the codestream. There can be several progression type(s) if progression order changes are used (POC
 * markers).</p>
 *
 */
public class ProgressionSpec extends ModuleSpec
{
  /**
   * Creates a new ProgressionSpec object for the specified number of tiles and components.
   *
   * @param nt The number of tiles
   *
   * @param nc The number of components
   *
   * @param type the type of the specification module i.e. tile specific, component specific or both. The ProgressionSpec class should only be used only with
   * the type ModuleSpec.SPEC_TYPE_TILE.
     *
   */
  public ProgressionSpec(int nt, int nc, byte type)
  {
    super(nt, nc, type);
    if (type != ModuleSpec.SPEC_TYPE_TILE)
      throw new Error("Illegal use of class ProgressionSpec !");
  }

  /**
   * Creates a new ProgressionSpec object for the specified number of tiles, components and the ParameterList instance.
   *
   * @param nt The number of tiles
   *
   * @param nc The number of components
   *
   * @param nl The number of layer
   *
   * @param dls The number of decomposition levels specifications
   *
   * @param type the type of the specification module. The ProgressionSpec class should only be used only with the type ModuleSpec.SPEC_TYPE_TILE.
   *
   * @param pl The ParameterList instance
     *
   */
  public ProgressionSpec(int nt, int nc, int nl, IntegerSpec dls, byte type,
    ParameterList pl)
  {
    super(nt, nc, type);

    String param = pl.value("Aptype");
    Progression[] prog;
    int mode = -1;

    if (param == null)
    { // No parameter specified
      if (pl.value("Rroi") == null)
        mode = checkProgMode("res");
      else
        mode = checkProgMode("layer");

      if (mode == -1)
      {
        String errMsg = "Unknown progression type : '" + param + "'";
        throw new IllegalArgumentException(errMsg);
      }
      prog = new Progression[1];
      prog[0] = new Progression(mode, 0, nc, 0, dls.getMax() + 1, nl);
      setDefault(prog);
      return;
    }

    StringTokenizer stk = new StringTokenizer(param);
    byte curSpecType = SPEC_DEF; // Specification type of the
    // current parameter
    boolean[] tileSpec = null; // Tiles concerned by the specification
    String word = null; // current word
    String errMsg = null; // Error message
    boolean needInteger = false; // True if an integer value is expected
    int intType = 0; // Type of read integer value (0=index of first
    // resolution level, 1= index of first component, 2=index of first  
    // layer not included, 3= index of first resolution level not
    // included, 4= index of  first component not included
    Vector progression = new Vector();
    int tmp = 0;
    Progression curProg = null;

    while (stk.hasMoreTokens())
    {
      word = stk.nextToken();

      switch (word.charAt(0))
      {
        case 't':
          // If progression were previously found, store them
          if (progression.size() > 0)
          {
            // Ensure that all information has been taken
            curProg.ce = nc;
            curProg.lye = nl;
            curProg.re = dls.getMax() + 1;
            prog = new Progression[progression.size()];
            progression.copyInto(prog);
            if (curSpecType == SPEC_DEF)
              setDefault(prog);
            else if (curSpecType == SPEC_TILE_DEF)
              for (int i = tileSpec.length - 1; i >= 0; i--)
                if (tileSpec[i])
                  setTileDef(i, prog);
          }
          progression.removeAllElements();
          intType = -1;
          needInteger = false;

          // Tiles specification
          tileSpec = parseIdx(word, nTiles);
          curSpecType = SPEC_TILE_DEF;
          break;
        default:
          // Here, words is either a Integer (progression bound index)
          // or a String (progression order type). This is determined by
          // the value of needInteger.
          if (needInteger)
          { // Progression bound info
            try
            {
              tmp = (new Integer(word)).intValue();
            }
            catch (NumberFormatException e)
            {
              // Progression has missing parameters
              throw new IllegalArgumentException("Progression "
                + "order"
                + " specification "
                + "has missing "
                + "parameters: "
                + param);
            }

            switch (intType)
            {
              case 0: // cs
                if (tmp < 0
                  || tmp > (dls.getMax() + 1))
                  throw new IllegalArgumentException("Invalid res_start "
                    + "in '-Aptype'"
                    + " option: " + tmp);
                curProg.rs = tmp;
                break;
              case 1: // rs
                if (tmp < 0 || tmp > nc)
                  throw new IllegalArgumentException("Invalid comp_start "
                    + "in '-Aptype' "
                    + "option: " + tmp);
                curProg.cs = tmp;
                break;
              case 2: // lye
                if (tmp < 0)
                  throw new IllegalArgumentException("Invalid layer_end "
                    + "in '-Aptype'"
                    + " option: " + tmp);
                if (tmp > nl)
                  tmp = nl;
                curProg.lye = tmp;
                break;
              case 3: // ce
                if (tmp < 0)
                  throw new IllegalArgumentException("Invalid res_end "
                    + "in '-Aptype'"
                    + " option: " + tmp);
                if (tmp > (dls.getMax() + 1))
                  tmp = dls.getMax() + 1;
                curProg.re = tmp;
                break;
              case 4: // re
                if (tmp < 0)
                  throw new IllegalArgumentException("Invalid comp_end "
                    + "in '-Aptype'"
                    + " option: " + tmp);
                if (tmp > nc)
                  tmp = nc;
                curProg.ce = tmp;
                break;
            }

            if (intType < 4)
            {
              intType++;
              needInteger = true;
              break;
            }
            else if (intType == 4)
            {
              intType = 0;
              needInteger = false;
              break;
            }
            else
              throw new Error("Error in usage of 'Aptype' "
                + "option: " + param);
          }

          if (!needInteger)
          { // Progression type info
            mode = checkProgMode(word);
            if (mode == -1)
            {
              errMsg = "Unknown progression type : '" + word + "'";
              throw new IllegalArgumentException(errMsg);
            }
            needInteger = true;
            intType = 0;
            if (progression.size() == 0)
              curProg = new Progression(mode, 0, nc, 0, dls.getMax() + 1,
                nl);
            else
              curProg = new Progression(mode, 0, nc, 0, dls.getMax() + 1,
                nl);
            progression.addElement(curProg);
          }
      } // switch
    } // while 

    if (progression.size() == 0)
    { // No progression defined
      if (pl.value("Rroi") == null)
        mode = checkProgMode("res");
      else
        mode = checkProgMode("layer");
      if (mode == -1)
      {
        errMsg = "Unknown progression type : '" + param + "'";
        throw new IllegalArgumentException(errMsg);
      }
      prog = new Progression[1];
      prog[0] = new Progression(mode, 0, nc, 0, dls.getMax() + 1, nl);
      setDefault(prog);
      return;
    }

    // Ensure that all information has been taken
    curProg.ce = nc;
    curProg.lye = nl;
    curProg.re = dls.getMax() + 1;

    // Store found progression
    prog = new Progression[progression.size()];
    progression.copyInto(prog);

    if (curSpecType == SPEC_DEF)
      setDefault(prog);
    else if (curSpecType == SPEC_TILE_DEF)
      for (int i = tileSpec.length - 1; i >= 0; i--)
        if (tileSpec[i])
          setTileDef(i, prog);

    // Check that default value has been specified
    if (getDefault() == null)
    {
      int ndefspec = 0;
      for (int t = nt - 1; t >= 0; t--)
        for (int c = nc - 1; c >= 0; c--)
          if (specValType[t][c] == SPEC_DEF)
            ndefspec++;

      // If some tile-component have received no specification, they
      // receive the default progressiveness.
      if (ndefspec != 0)
      {
        if (pl.value("Rroi") == null)
          mode = checkProgMode("res");
        else
          mode = checkProgMode("layer");
        if (mode == -1)
        {
          errMsg = "Unknown progression type : '" + param + "'";
          throw new IllegalArgumentException(errMsg);
        }
        prog = new Progression[1];
        prog[0] = new Progression(mode, 0, nc, 0, dls.getMax() + 1, nl);
        setDefault(prog);
      }
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

  /**
   * Check if the progression mode exists and if so, return its integer value. It returns -1 otherwise.
   *
   * @param mode The progression mode stored in a string
   *
   * @return The integer value of the progression mode or -1 if the progression mode does not exist.
   *
   * @see ProgressionType
     *
   */
  private int checkProgMode(String mode)
  {
    if (mode.equals("res"))
      return ProgressionType.RES_LY_COMP_POS_PROG;
    else if (mode.equals("layer"))
      return ProgressionType.LY_RES_COMP_POS_PROG;
    else if (mode.equals("pos-comp"))
      return ProgressionType.POS_COMP_RES_LY_PROG;
    else if (mode.equals("comp-pos"))
      return ProgressionType.COMP_POS_RES_LY_PROG;
    else if (mode.equals("res-pos"))
      return ProgressionType.RES_POS_COMP_LY_PROG;
    else
      // No corresponding progression mode, we return -1.
      return -1;
  }
}
