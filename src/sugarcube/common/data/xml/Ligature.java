package sugarcube.common.data.xml;

import sugarcube.common.data.collections.Map3;

import java.util.Arrays;
import java.util.Map;

public class Ligature
{
  public static Map3<Integer, int[]> LATIN = new Map3<>();

  static
  {
    add('\u0026', 'e', 't');
    add('\u00DF', 'ſ', 's');
    add('\u00DF', 'ſ', 'z');
    add('\u00C6', 'A', 'E');
    add('\u00E6', 'a', 'e');
    add('\u0152', 'O', 'E');
    add('\u0153', 'o', 'e');
    add('\u0132', 'I', 'J');
    add('\u0133', 'i', 'j');
    add('\u1D6B', 'u', 'e');
    add('\uA728', 'T', 'Z');
    add('\uA729', 't', 'z');
    add('\uA732', 'A', 'A');
    add('\uA733', 'a', 'a');
    add('\uA734', 'A', 'O');
    add('\uA735', 'a', 'o');
    add('\uA736', 'A', 'U');
    add('\uA737', 'a', 'u');
    add('\uA738', 'A', 'V');
    add('\uA739', 'a', 'v');
    add('\uA73C', 'A', 'Y');
    add('\uA73D', 'a', 'y');
    add('\uA74E', 'O', 'O');
    add('\uA74F', 'o', 'o');
    add('\uFB00', 'f', 'f');
    add('\uFB01', 'f', 'i');
    add('\uFB02', 'f', 'l');
    add('\uFB03', 'f', 'f', 'i');
    add('\uFB04', 'f', 'f', 'l');
    add('\uFB05', 'ſ', 't');
    add('\uFB06', 's', 't');
  }

  public static int ligature(int... codes)
  {
    int key = -1;
    if (codes != null && codes.length > 0)
      for (Map.Entry<Integer, int[]> e : LATIN.entrySet())
      {
        key = e.getKey();
        if (codes.length == 1 && codes[0] == key)
          return key;
        else if (Arrays.equals(codes, e.getValue()))
          return key;
      }
    return -1;
  }

  public static void add(int lig, int... codes)
  {
    LATIN.put(lig, codes);
  }
}
