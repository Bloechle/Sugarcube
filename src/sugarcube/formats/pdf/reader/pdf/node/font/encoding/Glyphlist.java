package sugarcube.formats.pdf.reader.pdf.node.font.encoding;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Unicodes;
import sugarcube.formats.pdf.reader.pdf.util.Mapper;
import sugarcube.formats.pdf.resources.pdf.RS_PDF;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Glyphlist
{
  public static final String NOTDEF = ".notdef";
  public static final int UNDEF = -1;
  public static final Unicodes MISSING_UNICODE = new Unicodes((int) '*');
  public static final Mapper<String, Unicodes> NAME_TO_UNICODE = new Mapper<>("NameToUnicode", MISSING_UNICODE);
  public static final Mapper<Unicodes, String> UNICODE_TO_NAME = new Mapper<>("UnicodeToName", "*");

  protected Glyphlist()
  {
  }

  static
  {
    BufferedReader input = null;
    try
    {
      input = new BufferedReader(new InputStreamReader(RS_PDF.stream("encoding/glyphlist.txt")));
      String line;
      while ((line = input.readLine()) != null)
        if (!line.trim().isEmpty() && !line.trim().startsWith("#"))
        {
          String[] tokens = line.split(";");
          ADD_HEXA_FROM_GLYPHLIST(tokens[0].trim(), tokens[1].split("\\s+"));
        }
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      try
      {
        if (input != null)
          input.close();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }

    //zapfdingbats list
    ADD("a100", 0x275E);
    ADD("a101", 0x2761);
    ADD("a102", 0x2762);
    ADD("a103", 0x2763);
    ADD("a104", 0x2764);
    ADD("a105", 0x2710);
    ADD("a106", 0x2765);
    ADD("a107", 0x2766);
    ADD("a108", 0x2767);
    ADD("a109", 0x2660);
    ADD("a10", 0x2721);
    ADD("a110", 0x2665);
    ADD("a111", 0x2666);
    ADD("a112", 0x2663);
    ADD("a117", 0x2709);
    ADD("a118", 0x2708);
    ADD("a119", 0x2707);
    ADD("a11", 0x261B);
    ADD("a120", 0x2460);
    ADD("a121", 0x2461);
    ADD("a122", 0x2462);
    ADD("a123", 0x2463);
    ADD("a124", 0x2464);
    ADD("a125", 0x2465);
    ADD("a126", 0x2466);
    ADD("a127", 0x2467);
    ADD("a128", 0x2468);
    ADD("a129", 0x2469);
    ADD("a12", 0x261E);
    ADD("a130", 0x2776);
    ADD("a131", 0x2777);
    ADD("a132", 0x2778);
    ADD("a133", 0x2779);
    ADD("a134", 0x277A);
    ADD("a135", 0x277B);
    ADD("a136", 0x277C);
    ADD("a137", 0x277D);
    ADD("a138", 0x277E);
    ADD("a139", 0x277F);
    ADD("a13", 0x270C);
    ADD("a140", 0x2780);
    ADD("a141", 0x2781);
    ADD("a142", 0x2782);
    ADD("a143", 0x2783);
    ADD("a144", 0x2784);
    ADD("a145", 0x2785);
    ADD("a146", 0x2786);
    ADD("a147", 0x2787);
    ADD("a148", 0x2788);
    ADD("a149", 0x2789);
    ADD("a14", 0x270D);
    ADD("a150", 0x278A);
    ADD("a151", 0x278B);
    ADD("a152", 0x278C);
    ADD("a153", 0x278D);
    ADD("a154", 0x278E);
    ADD("a155", 0x278F);
    ADD("a156", 0x2790);
    ADD("a157", 0x2791);
    ADD("a158", 0x2792);
    ADD("a159", 0x2793);
    ADD("a15", 0x270E);
    ADD("a160", 0x2794);
    ADD("a161", 0x2192);
    ADD("a162", 0x27A3);
    ADD("a163", 0x2194);
    ADD("a164", 0x2195);
    ADD("a165", 0x2799);
    ADD("a166", 0x279B);
    ADD("a167", 0x279C);
    ADD("a168", 0x279D);
    ADD("a169", 0x279E);
    ADD("a16", 0x270F);
    ADD("a170", 0x279F);
    ADD("a171", 0x27A0);
    ADD("a172", 0x27A1);
    ADD("a173", 0x27A2);
    ADD("a174", 0x27A4);
    ADD("a175", 0x27A5);
    ADD("a176", 0x27A6);
    ADD("a177", 0x27A7);
    ADD("a178", 0x27A8);
    ADD("a179", 0x27A9);
    ADD("a17", 0x2711);
    ADD("a180", 0x27AB);
    ADD("a181", 0x27AD);
    ADD("a182", 0x27AF);
    ADD("a183", 0x27B2);
    ADD("a184", 0x27B3);
    ADD("a185", 0x27B5);
    ADD("a186", 0x27B8);
    ADD("a187", 0x27BA);
    ADD("a188", 0x27BB);
    ADD("a189", 0x27BC);
    ADD("a18", 0x2712);
    ADD("a190", 0x27BD);
    ADD("a191", 0x27BE);
    ADD("a192", 0x279A);
    ADD("a193", 0x27AA);
    ADD("a194", 0x27B6);
    ADD("a195", 0x27B9);
    ADD("a196", 0x2798);
    ADD("a197", 0x27B4);
    ADD("a198", 0x27B7);
    ADD("a199", 0x27AC);
    ADD("a19", 0x2713);
    ADD("a1", 0x2701);
    ADD("a200", 0x27AE);
    ADD("a201", 0x27B1);
    ADD("a202", 0x2703);
    ADD("a203", 0x2750);
    ADD("a204", 0x2752);
    ADD("a205", 0x276E);
    ADD("a206", 0x2770);
    ADD("a20", 0x2714);
    ADD("a21", 0x2715);
    ADD("a22", 0x2716);
    ADD("a23", 0x2717);
    ADD("a24", 0x2718);
    ADD("a25", 0x2719);
    ADD("a26", 0x271A);
    ADD("a27", 0x271B);
    ADD("a28", 0x271C);
    ADD("a29", 0x2722);
    ADD("a2", 0x2702);
    ADD("a30", 0x2723);
    ADD("a31", 0x2724);
    ADD("a32", 0x2725);
    ADD("a33", 0x2726);
    ADD("a34", 0x2727);
    ADD("a35", 0x2605);
    ADD("a36", 0x2729);
    ADD("a37", 0x272A);
    ADD("a38", 0x272B);
    ADD("a39", 0x272C);
    ADD("a3", 0x2704);
    ADD("a40", 0x272D);
    ADD("a41", 0x272E);
    ADD("a42", 0x272F);
    ADD("a43", 0x2730);
    ADD("a44", 0x2731);
    ADD("a45", 0x2732);
    ADD("a46", 0x2733);
    ADD("a47", 0x2734);
    ADD("a48", 0x2735);
    ADD("a49", 0x2736);
    ADD("a4", 0x260E);
    ADD("a50", 0x2737);
    ADD("a51", 0x2738);
    ADD("a52", 0x2739);
    ADD("a53", 0x273A);
    ADD("a54", 0x273B);
    ADD("a55", 0x273C);
    ADD("a56", 0x273D);
    ADD("a57", 0x273E);
    ADD("a58", 0x273F);
    ADD("a59", 0x2740);
    ADD("a5", 0x2706);
    ADD("a60", 0x2741);
    ADD("a61", 0x2742);
    ADD("a62", 0x2743);
    ADD("a63", 0x2744);
    ADD("a64", 0x2745);
    ADD("a65", 0x2746);
    ADD("a66", 0x2747);
    ADD("a67", 0x2748);
    ADD("a68", 0x2749);
    ADD("a69", 0x274A);
    ADD("a6", 0x271D);
    ADD("a70", 0x274B);
    ADD("a71", 0x25CF);
    ADD("a72", 0x274D);
    ADD("a73", 0x25A0);
    ADD("a74", 0x274F);
    ADD("a75", 0x2751);
    ADD("a76", 0x25B2);
    ADD("a77", 0x25BC);
    ADD("a78", 0x25C6);
    ADD("a79", 0x2756);
    ADD("a7", 0x271E);
    ADD("a81", 0x25D7);
    ADD("a82", 0x2758);
    ADD("a83", 0x2759);
    ADD("a84", 0x275A);
    ADD("a85", 0x276F);
    ADD("a86", 0x2771);
    ADD("a87", 0x2772);
    ADD("a88", 0x2773);
    ADD("a89", 0x2768);
    ADD("a8", 0x271F);
    ADD("a90", 0x2769);
    ADD("a91", 0x276C);
    ADD("a92", 0x276D);
    ADD("a93", 0x276A);
    ADD("a94", 0x276B);
    ADD("a95", 0x2774);
    ADD("a96", 0x2775);
    ADD("a97", 0x275B);
    ADD("a98", 0x275C);
    ADD("a99", 0x275D);
    ADD("a9", 0x2720);

    //ligatures
    ADD("f_f", "ff");
    ADD("f_f_i", "ffi");
    ADD("f_i", "fi");
    ADD("f_l", "fl");
    ADD("T_h", "Th");
  }

  public static String unistring(int unicode)
  {
    try
    {
      return new String(Character.toChars(unicode));
    }
    catch (Exception e)
    {
      Log.warn(Glyphlist.class, ".unistring - exception with unicode " + unicode + ": " + e);
      return "*";
    }
  }

  public static String NAME(Unicodes character)
  {
    return NAME(character, NOTDEF);
  }

  public static String NAME(Unicodes character, String def)
  {
    return UNICODE_TO_NAME.containsKey(character) ? UNICODE_TO_NAME.get(character) : def;
  }

  public static Unicodes UNICODE(String name)
  {
    return NAME_TO_UNICODE.get(name);
  }

  private static void ADD_HEXA_FROM_GLYPHLIST(String name, String... hexaChars)
  {
    int[] unicodes = new int[hexaChars.length];
    for (int i = 0; i < unicodes.length; i++)
      unicodes[i] = Integer.parseInt(hexaChars[i].trim(), 16);
    ADD(name, unicodes);
  }

  protected static void ADD(String name, String characters)
  {
    Unicodes unicodes = new Unicodes(characters);
    NAME_TO_UNICODE.put(name, unicodes);
    if (!UNICODE_TO_NAME.containsKey(unicodes))
      UNICODE_TO_NAME.put(unicodes, name);
  }

  protected static void ADD(String name, int... character)
  {
    Unicodes unicodes = new Unicodes(character);
    NAME_TO_UNICODE.put(name, unicodes);
    if (!UNICODE_TO_NAME.containsKey(unicodes))
      UNICODE_TO_NAME.put(unicodes, name);
  }
//  protected static boolean CONTAINS(String name)
//  {
//    return NAME_TO_UNICODE.containsKey(name);
//  }
}
