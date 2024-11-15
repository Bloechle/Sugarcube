package sugarcube.common.data.xml;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.IntArray;
import sugarcube.common.data.collections.Map3;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.data.collections.Unicodes;

public class CharRef
{
  
//6400 - 1024 (S3 offset) free codes until uF8FF
  public static final int PRIVATE_USE_OFFSET = 0xE000 + 1024;
  private static StringMap<Integer> NAME_TO_UNICODE = new StringMap<>();
  private static Map3<Integer, String> UNICODE_TO_NAME = new Map3<>();

  static
  {
    populateMaps();
  }

  public static int ensureUnicode(int c)
  {
 // Zen.LOG.debug(CharRef.class, ".ensureUnicodes - ensuring: "+c);
    return IsValid(c) ? c : (c & 0x00000FFF) + PRIVATE_USE_OFFSET; 
  }

  public static int[] ensureUnicodes(int... c)
  {
    int[] chars = new int[c.length];
    for (int i = 0; i < chars.length; i++)
      chars[i] = ensureUnicode(c[i]);
    return chars;
  }

  public static boolean IsPrivateUseArea(int c)
  {
    return Character.UnicodeBlock.PRIVATE_USE_AREA.equals(Character.UnicodeBlock.of(c));
  }

  public static boolean IsValid(int c)
  {
    return !(c < 0x0020 || c > 0xD7FF && c < 0xE000 || c > 0xFFFD && c < 0x10000 || c > 0x10FFFF);
  }

  public static boolean IsCtrlOrInvalid(int c)
  {
    return c < 0x0020  || c > 0x007E && c < 0x00A1 || c == 0x00AD|| c > 0xD7FF && c < 0xE000 || c > 0xFFFD && c < 0x10000 || c > 0x10FFFF;
  }

  public static boolean isCtrl(int c)
  {
    return c < 0x0020 || c > 0x007E && c < 0x00A1 || c == 0x00AD;
  }

  public static boolean isValid(CharSequence seq)
  {
    for (int i = 0; i < seq.length(); i++)
      if (!IsValid(seq.charAt(i)))
        return false;
    return true;
  }

  public static String Escape(String text)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < text.length(); i++)
      sb.append(Escape(text.charAt(i)));
    return sb.toString();
  }

  public static boolean IsEscaped(CharSequence seq)
  {
    for (int i = 0; i < seq.length(); i++)
      if (CharRef.IsEscaped(seq.charAt(i)))
        return true;
    return false;
  }

  public static String Escape(char c)
  {
    switch (c)
    {
    case '&':
      return "&amp;";
    case '<':
      return "&lt;";
    case '>':
      return "&gt;";
    case '"':
      return "&quot;";
    case '\'':
      return "&apos;";
    case '“':
      return "&ldquo;";
    case '”':
      return "&rdquo;";
    case '’':
      return "&rsquo;";
    case '‘':
      return "&lsquo;";
    case '‚':
      return "&sbquo;";
    case '„':
      return "&bdquo;";
    default:
      return String.valueOf(c);
    }
  }

  public static boolean IsEscaped(int unicode)
  {
    switch (unicode)
    {
    case 34:// "
    case 38:// &
    case 39:// '
    case 60:// <
    case 62:// >
      return true;
    default:
      return false;
    }
  }

  public static String Html(String text)
  {
    return Html(true, text);
  }

  public static String Html(char... text)
  {
    return Html(true, text);
  }

  public static String Html(int... text)
  {
    return Html(true, text);
  }

  public static String Html(boolean doEscape, String text)
  {
    StringBuilder sb = new StringBuilder(text.length() * 2);

    for (int c : text.codePoints().toArray())
    {  
      if (IsValid(c) || c == Unicodes.ASCII_LF || c == Unicodes.ASCII_CR)
        sb.append(c >= 32 && c <= 126 && (!doEscape || !IsEscaped(c)) ? (char) c : ("&#" + c + ";"));
      else if (!(c == Unicodes.ASCII_LF || c == Unicodes.ASCII_CR))
        Log.warn(CharRef.class, ".Html - non valid unicode: " + c);
    }
    return sb.toString();
  }

  public static String Html(boolean doEscape, char... unicodes)
  {
    StringBuilder sb = new StringBuilder(unicodes.length * 2);
    for (int c : unicodes)
      if (IsValid(c) || c == Unicodes.ASCII_LF || c == Unicodes.ASCII_CR)
        sb.append(c >= 32 && c <= 126 && (!doEscape || !IsEscaped(c)) ? (char) c : ("&#" + c + ";"));
      else if (!(c == Unicodes.ASCII_LF || c == Unicodes.ASCII_CR))
        Log.warn(CharRef.class, ".Html - non valid unicode: " + c);
    return sb.toString();
  }

  public static String Html(boolean doEscape, int... unicodes)
  {
    StringBuilder sb = new StringBuilder(unicodes.length * 2);
    for (int c : unicodes)
      if (IsValid(c) || c == Unicodes.ASCII_LF || c == Unicodes.ASCII_CR)
        sb.append(c >= 32 && c <= 126 && (!doEscape || !IsEscaped(c)) ? (char) c : ("&#" + c + ";"));
      else if (!(c == Unicodes.ASCII_LF || c == Unicodes.ASCII_CR))
        Log.warn(CharRef.class, ".Html - non valid unicode: " + c);
    return sb.toString();
  }

  public static String UnEscape(String data)
  {
    if (data == null)
      return null;
    else if (data.isEmpty())
      return "";
    StringBuilder sb = new StringBuilder(data.length());
    int i = -1;
    int j;// index of ";"
    int k;// index of second &
    int c;// char at i
    int uni = -1;
    while (++i < data.length())
      if ((c = data.charAt(i)) == '&' && (j = data.indexOf(';', i)) > i && ((k = data.indexOf('&', i + 1)) < 0 || k > j))
      {
        if (data.charAt(i + 1) == '#')// numeric character reference
          if (data.charAt(i + 2) == 'x')// hexadecimal character reference
            sb.appendCodePoint(Integer.parseInt(data.substring(i + 3, j), 16));
          else
            sb.appendCodePoint(Integer.parseInt(data.substring(i + 2, j)));
        else if ((uni = NAME_TO_UNICODE.get(data.substring(i + 1, j), -1)) > 31)
        {
          sb.appendCodePoint(uni);
        } else
          sb.append(data.substring(i, j + 1));
        i = j;
      } else
        sb.appendCodePoint(c);
    return sb.toString();
  }

  public static int[] UnHtml(String data)
  {
    if (data == null)
      return null;
    else if (data.isEmpty())
      return new int[0];
    IntArray unicodes = new IntArray(data.length() / 2);
    int i = -1;
    int j;// index of ";"
    int c;// char at i
    while (++i < data.length())
      if ((c = data.charAt(i)) == '&' && (j = data.indexOf(';', i)) > i)
      {
        if (data.charAt(i + 1) == '#')// numeric character reference
          if (data.charAt(i + 2) == 'x')// hexadecimal character reference
            unicodes.add(Integer.parseInt(data.substring(i + 3, j), 16));
          else
            unicodes.add(Integer.parseInt(data.substring(i + 2, j)));
        else
          unicodes.add(entityUnicode(data.substring(i + 1, j)));
        i = j;
      } else
        unicodes.add(c);
    return unicodes.array();
  }

  public static String entityName(int unicode)
  {
    return UNICODE_TO_NAME.get(unicode, null);
  }

  public static int entityUnicode(String name)
  {
    if (name.startsWith("&"))
      name = name.substring(1);
    if (name.endsWith(";"))
      name = name.substring(0, name.length() - 1);
    return NAME_TO_UNICODE.get(name, -1);
  }

  private static void add(String name, int unicode)
  {
    // has to be added before put to avoid overriding real unicode to name html
    // mapping
    // Acirc => Acircumflex
    if (name.endsWith("circ"))
      add(name + "umflex", unicode);
    // Ccedil => Ccedilla
    if (name.endsWith("cedil"))
      add(name + "la", unicode);
    // aum => aumlaut
    if (name.endsWith("uml"))
      add(name + "aut", unicode);
    // aelit => aeligature
    if (name.endsWith("lig"))
      add(name + "ature", unicode);

    NAME_TO_UNICODE.put(name, unicode);
    UNICODE_TO_NAME.put(unicode, name);
  }

  private static void populateMaps()
  {
    // supplementary adds, but put before to avoid breaking html unicode to name
    // original mappling
    add("question", '?');
    add("hyphen", '-');
    add("period", '.');
    add("comma", ',');

    // "http://www.w3.org/TR/xhtml1/DTD/xhtml-lat1.ent" in XHTML 1.0;
    add("nbsp", 160);// <!-- no-break space = non-breaking space, U+00A0 ISOnum
                     // -->
    add("iexcl", 161);// <!-- inverted exclamation mark, U+00A1 ISOnum -->
    add("cent", 162);// <!-- cent sign, U+00A2 ISOnum -->
    add("pound", 163);// <!-- pound sign, U+00A3 ISOnum -->
    add("curren", 164);// <!-- currency sign, U+00A4 ISOnum -->
    add("yen", 165);// <!-- yen sign = yuan sign, U+00A5 ISOnum -->
    add("brvbar", 166);// <!-- broken bar = broken vertical bar, U+00A6 ISOnum
                       // -->
    add("sect", 167);// <!-- section sign, U+00A7 ISOnum -->
    add("uml", 168);// <!-- diaeresis = spacing diaeresis, U+00A8 ISOdia -->
    add("copy", 169);// <!-- copyright sign, U+00A9 ISOnum -->
    add("ordf", 170);// <!-- feminine ordinal indicator, U+00AA ISOnum -->
    add("laquo", 171);// <!-- left-pointing double angle quotation mark = left
                      // pointing guillemet, U+00AB ISOnum -->
    add("not", 172);// <!-- not sign = angled dash, U+00AC ISOnum -->
    add("shy", 173);// <!-- soft hyphen = discretionary hyphen, U+00AD ISOnum
                    // -->
    add("reg", 174);// <!-- registered sign = registered trade mark sign, U+00AE
                    // ISOnum -->
    add("macr", 175);// <!-- macron = spacing macron = overline = APL overbar,
                     // U+00AF ISOdia -->
    add("deg", 176);// <!-- degree sign, U+00B0 ISOnum -->
    add("plusmn", 177);// <!-- plus-minus sign = plus-or-minus sign, U+00B1
                       // ISOnum -->
    add("sup2", 178);// <!-- superscript two = superscript digit two = squared,
                     // U+00B2 ISOnum -->
    add("sup3", 179);// <!-- superscript three = superscript digit three =
                     // cubed, U+00B3 ISOnum -->
    add("acute", 180);// <!-- acute accent = spacing acute, U+00B4 ISOdia -->
    add("micro", 181);// <!-- micro sign, U+00B5 ISOnum -->
    add("para", 182);// <!-- pilcrow sign = paragraph sign, U+00B6 ISOnum -->
    add("middot", 183);// <!-- middle dot = Georgian comma = Greek middle dot,
                       // U+00B7 ISOnum -->
    add("cedil", 184);// <!-- cedilla = spacing cedilla, U+00B8 ISOdia -->
    add("sup1", 185);// <!-- superscript one = superscript digit one, U+00B9
                     // ISOnum -->
    add("ordm", 186);// <!-- masculine ordinal indicator, U+00BA ISOnum -->
    add("raquo", 187);// <!-- right-pointing double angle quotation mark = right
                      // pointing guillemet, U+00BB ISOnum -->
    add("frac14", 188);// <!-- vulgar fraction one quarter = fraction one
                       // quarter, U+00BC ISOnum -->
    add("frac12", 189);// <!-- vulgar fraction one half = fraction one half,
                       // U+00BD ISOnum --> = fraction three quarters, U+00BE
                       // ISOnum -->
    add("iquest", 191);// <!-- inverted question mark = turned question mark,
                       // U+00BF ISOnum -->
    add("Agrave", 192);// <!-- latin capital letter A with grave = latin capital
                       // letter A grave, U+00C0 ISOlat1 -->
    add("Aacute", 193);// <!-- latin capital letter A with acute, U+00C1 ISOlat1
                       // -->
    add("Acirc", 194);// <!-- latin capital letter A with circumflex, U+00C2
                      // ISOlat1 -->
    add("Atilde", 195);// <!-- latin capital letter A with tilde, U+00C3 ISOlat1
                       // -->
    add("Auml", 196);// <!-- latin capital letter A with diaeresis, U+00C4
                     // ISOlat1 -->
    add("Aring", 197);// <!-- latin capital letter A with ring above = latin
                      // capital letter A ring, U+00C5 ISOlat1 -->
    add("AElig", 198);// <!-- latin capital letter AE = latin capital ligature
                      // AE, U+00C6 ISOlat1 -->
    add("Ccedil", 199);// <!-- latin capital letter C with cedilla, U+00C7
                       // ISOlat1 -->
    add("Egrave", 200);// <!-- latin capital letter E with grave, U+00C8 ISOlat1
                       // -->
    add("Eacute", 201);// <!-- latin capital letter E with acute, U+00C9 ISOlat1
                       // -->
    add("Ecirc", 202);// <!-- latin capital letter E with circumflex, U+00CA
                      // ISOlat1 -->
    add("Euml", 203);// <!-- latin capital letter E with diaeresis, U+00CB
                     // ISOlat1 -->
    add("Igrave", 204);// <!-- latin capital letter I with grave, U+00CC ISOlat1
                       // -->
    add("Iacute", 205);// <!-- latin capital letter I with acute, U+00CD ISOlat1
                       // -->
    add("Icirc", 206);// <!-- latin capital letter I with circumflex, U+00CE
                      // ISOlat1 -->
    add("Iuml", 207);// <!-- latin capital letter I with diaeresis, U+00CF
                     // ISOlat1 -->
    add("ETH", 208);// <!-- latin capital letter ETH, U+00D0 ISOlat1 -->
    add("Ntilde", 209);// <!-- latin capital letter N with tilde, U+00D1 ISOlat1
                       // -->
    add("Ograve", 210);// <!-- latin capital letter O with grave, U+00D2 ISOlat1
                       // -->
    add("Oacute", 211);// <!-- latin capital letter O with acute, U+00D3 ISOlat1
                       // -->
    add("Ocirc", 212);// <!-- latin capital letter O with circumflex, U+00D4
                      // ISOlat1 -->
    add("Otilde", 213);// <!-- latin capital letter O with tilde, U+00D5 ISOlat1
                       // -->
    add("Ouml", 214);// <!-- latin capital letter O with diaeresis, U+00D6
                     // ISOlat1 -->
    add("times", 215);// <!-- multiplication sign, U+00D7 ISOnum -->
    add("Oslash", 216);// <!-- latin capital letter O with stroke = latin
                       // capital letter O slash, U+00D8 ISOlat1 -->
    add("Ugrave", 217);// <!-- latin capital letter U with grave, U+00D9 ISOlat1
                       // -->
    add("Uacute", 218);// <!-- latin capital letter U with acute, U+00DA ISOlat1
                       // -->
    add("Ucirc", 219);// <!-- latin capital letter U with circumflex, U+00DB
                      // ISOlat1 -->
    add("Uuml", 220);// <!-- latin capital letter U with diaeresis, U+00DC
                     // ISOlat1 -->
    add("Yacute", 221);// <!-- latin capital letter Y with acute, U+00DD ISOlat1
                       // -->
    add("THORN", 222);// <!-- latin capital letter THORN, U+00DE ISOlat1 -->
    add("szlig", 223);// <!-- latin small letter sharp s = ess-zed, U+00DF
                      // ISOlat1 -->
    add("agrave", 224);// <!-- latin small letter a with grave = latin small
                       // letter a grave, U+00E0 ISOlat1 -->
    add("aacute", 225);// <!-- latin small letter a with acute, U+00E1 ISOlat1
                       // -->
    add("acirc", 226);// <!-- latin small letter a with circumflex, U+00E2
                      // ISOlat1 -->
    add("atilde", 227);// <!-- latin small letter a with tilde, U+00E3 ISOlat1
                       // -->
    add("auml", 228);// <!-- latin small letter a with diaeresis, U+00E4 ISOlat1
                     // -->
    add("aring", 229);// <!-- latin small letter a with ring above = latin small
                      // letter a ring, U+00E5 ISOlat1 -->
    add("aelig", 230);// <!-- latin small letter ae = latin small ligature ae,
                      // U+00E6 ISOlat1 -->
    add("ccedil", 231);// <!-- latin small letter c with cedilla, U+00E7 ISOlat1
                       // -->
    add("egrave", 232);// <!-- latin small letter e with grave, U+00E8 ISOlat1
                       // -->
    add("eacute", 233);// <!-- latin small letter e with acute, U+00E9 ISOlat1
                       // -->
    add("ecirc", 234);// <!-- latin small letter e with circumflex, U+00EA
                      // ISOlat1 -->
    add("euml", 235);// <!-- latin small letter e with diaeresis, U+00EB ISOlat1
                     // -->
    add("igrave", 236);// <!-- latin small letter i with grave, U+00EC ISOlat1
                       // -->
    add("iacute", 237);// <!-- latin small letter i with acute, U+00ED ISOlat1
                       // -->
    add("icirc", 238);// <!-- latin small letter i with circumflex, U+00EE
                      // ISOlat1 -->
    add("iuml", 239);// <!-- latin small letter i with diaeresis, U+00EF ISOlat1
                     // -->
    add("eth", 240);// <!-- latin small letter eth, U+00F0 ISOlat1 -->
    add("ntilde", 241);// <!-- latin small letter n with tilde, U+00F1 ISOlat1
                       // -->
    add("ograve", 242);// <!-- latin small letter o with grave, U+00F2 ISOlat1
                       // -->
    add("oacute", 243);// <!-- latin small letter o with acute, U+00F3 ISOlat1
                       // -->
    add("ocirc", 244);// <!-- latin small letter o with circumflex, U+00F4
                      // ISOlat1 -->
    add("otilde", 245);// <!-- latin small letter o with tilde, U+00F5 ISOlat1
                       // -->
    add("ouml", 246);// <!-- latin small letter o with diaeresis, U+00F6 ISOlat1
                     // -->
    add("divide", 247);// <!-- division sign, U+00F7 ISOnum -->
    add("oslash", 248);// <!-- latin small letter o with stroke, = latin small
                       // letter o slash, U+00F8 ISOlat1 -->
    add("ugrave", 249);// <!-- latin small letter u with grave, U+00F9 ISOlat1
                       // -->
    add("uacute", 250);// <!-- latin small letter u with acute, U+00FA ISOlat1
                       // -->
    add("ucirc", 251);// <!-- latin small letter u with circumflex, U+00FB
                      // ISOlat1 -->
    add("uuml", 252);// <!-- latin small letter u with diaeresis, U+00FC ISOlat1
                     // -->
    add("yacute", 253);// <!-- latin small letter y with acute, U+00FD ISOlat1
                       // -->
    add("thorn", 254);// <!-- latin small letter thorn, U+00FE ISOlat1 -->
    add("yuml", 255);// <!-- latin small letter y with diaeresis, U+00FF ISOlat1
                     // -->

    // <!-- Mathematical, Greek and Symbolic characters for XHTML -->
    // <!-- Latin Extended-B -->
    add("fnof", 402);// <!-- latin small letter f with hook = function = florin,
                     // U+0192 ISOtech -->

    // <!-- Greek -->
    add("Alpha", 913);// <!-- greek capital letter alpha, U+0391 -->
    add("Beta", 914);// <!-- greek capital letter beta, U+0392 -->
    add("Gamma", 915);// <!-- greek capital letter gamma, U+0393 ISOgrk3 -->
    add("Delta", 916);// <!-- greek capital letter delta, U+0394 ISOgrk3 -->
    add("Epsilon", 917);// <!-- greek capital letter epsilon, U+0395 -->
    add("Zeta", 918);// <!-- greek capital letter zeta, U+0396 -->
    add("Eta", 919);// <!-- greek capital letter eta, U+0397 -->
    add("Theta", 920);// <!-- greek capital letter theta, U+0398 ISOgrk3 -->
    add("Iota", 921);// <!-- greek capital letter iota, U+0399 -->
    add("Kappa", 922);// <!-- greek capital letter kappa, U+039A -->
    add("Lambda", 923);// <!-- greek capital letter lamda, U+039B ISOgrk3 -->
    add("Mu", 924);// <!-- greek capital letter mu, U+039C -->
    add("Nu", 925);// <!-- greek capital letter nu, U+039D -->
    add("Xi", 926);// <!-- greek capital letter xi, U+039E ISOgrk3 -->
    add("Omicron", 927);// <!-- greek capital letter omicron, U+039F -->
    add("Pi", 928);// <!-- greek capital letter pi, U+03A0 ISOgrk3 -->
    add("Rho", 929);// <!-- greek capital letter rho, U+03A1 -->
    // <!-- there is no Sigmaf, and no U+03A2 character either -->
    add("Sigma", 931);// <!-- greek capital letter sigma, U+03A3 ISOgrk3 -->
    add("Tau", 932);// <!-- greek capital letter tau, U+03A4 -->
    add("Upsilon", 933);// <!-- greek capital letter upsilon, U+03A5 ISOgrk3 -->
    add("Phi", 934);// <!-- greek capital letter phi, U+03A6 ISOgrk3 -->
    add("Chi", 935);// <!-- greek capital letter chi, U+03A7 -->
    add("Psi", 936);// <!-- greek capital letter psi, U+03A8 ISOgrk3 -->
    add("Omega", 937);// <!-- greek capital letter omega, U+03A9 ISOgrk3 -->

    add("alpha", 945);// <!-- greek small letter alpha, U+03B1 ISOgrk3 -->
    add("beta", 946);// <!-- greek small letter beta, U+03B2 ISOgrk3 -->
    add("gamma", 947);// <!-- greek small letter gamma, U+03B3 ISOgrk3 -->
    add("delta", 948);// <!-- greek small letter delta, U+03B4 ISOgrk3 -->
    add("epsilon", 949);// <!-- greek small letter epsilon, U+03B5 ISOgrk3 -->
    add("zeta", 950);// <!-- greek small letter zeta, U+03B6 ISOgrk3 -->
    add("eta", 951);// <!-- greek small letter eta, U+03B7 ISOgrk3 -->
    add("theta", 952);// <!-- greek small letter theta, U+03B8 ISOgrk3 -->
    add("iota", 953);// <!-- greek small letter iota, U+03B9 ISOgrk3 -->
    add("kappa", 954);// <!-- greek small letter kappa, U+03BA ISOgrk3 -->
    add("lambda", 955);// <!-- greek small letter lamda, U+03BB ISOgrk3 -->
    add("mu", 956);// <!-- greek small letter mu, U+03BC ISOgrk3 -->
    add("nu", 957);// <!-- greek small letter nu, U+03BD ISOgrk3 -->
    add("xi", 958);// <!-- greek small letter xi, U+03BE ISOgrk3 -->
    add("omicron", 959);// <!-- greek small letter omicron, U+03BF NEW -->
    add("pi", 960);// <!-- greek small letter pi, U+03C0 ISOgrk3 -->
    add("rho", 961);// <!-- greek small letter rho, U+03C1 ISOgrk3 -->
    add("sigmaf", 962);// <!-- greek small letter final sigma, U+03C2 ISOgrk3
                       // -->
    add("sigma", 963);// <!-- greek small letter sigma, U+03C3 ISOgrk3 -->
    add("tau", 964);// <!-- greek small letter tau, U+03C4 ISOgrk3 -->
    add("upsilon", 965);// <!-- greek small letter upsilon, U+03C5 ISOgrk3 -->
    add("phi", 966);// <!-- greek small letter phi, U+03C6 ISOgrk3 -->
    add("chi", 967);// <!-- greek small letter chi, U+03C7 ISOgrk3 -->
    add("psi", 968);// <!-- greek small letter psi, U+03C8 ISOgrk3 -->
    add("omega", 969);// <!-- greek small letter omega, U+03C9 ISOgrk3 -->
    add("thetasym", 977);// <!-- greek theta symbol, U+03D1 NEW -->
    add("upsih", 978);// <!-- greek upsilon with hook symbol, U+03D2 NEW -->
    add("piv", 982);// <!-- greek pi symbol, U+03D6 ISOgrk3 -->

    // <!-- General Punctuation -->
    add("bull", 8226);// <!-- bullet = black small circle, U+2022 ISOpub -->
    // <!-- bullet is NOT the same as bullet operator, U+2219 -->
    add("hellip", 8230);// <!-- horizontal ellipsis = three dot leader, U+2026
                        // ISOpub -->
    add("prime", 8242);// <!-- prime = minutes = feet, U+2032 ISOtech -->
    add("Prime", 8243);// <!-- double prime = seconds = inches, U+2033 ISOtech
                       // -->
    add("oline", 8254);// <!-- overline = spacing overscore, U+203E NEW -->
    add("frasl", 8260);// <!-- fraction slash, U+2044 NEW -->

    // <!-- Letterlike Symbols -->
    add("weierp", 8472);// <!-- script capital P = power set = Weierstrass p,
                        // U+2118 ISOamso -->
    add("image", 8465);// <!-- black-letter capital I = imaginary part, U+2111
                       // ISOamso -->
    add("real", 8476);// <!-- black-letter capital R = real part symbol, U+211C
                      // ISOamso -->
    add("trade", 8482);// <!-- trade mark sign, U+2122 ISOnum -->
    add("alefsym", 8501);// <!-- alef symbol = first transfinite cardinal,
                         // U+2135 NEW -->
    // <!-- alef symbol is NOT the same as hebrew letter alef, U+05D0 although
    // the same glyph could be used to depict both characters -->

    // <!-- Arrows -->
    add("larr", 8592);// <!-- leftwards arrow, U+2190 ISOnum -->
    add("uarr", 8593);// <!-- upwards arrow, U+2191 ISOnum-->
    add("rarr", 8594);// <!-- rightwards arrow, U+2192 ISOnum -->
    add("darr", 8595);// <!-- downwards arrow, U+2193 ISOnum -->
    add("harr", 8596);// <!-- left right arrow, U+2194 ISOamsa -->
    add("crarr", 8629);// <!-- downwards arrow with corner leftwards = carriage
                       // return, U+21B5 NEW -->
    add("lArr", 8656);// <!-- leftwards double arrow, U+21D0 ISOtech -->
    // <!-- Unicode does not say that lArr is the same as the 'is implied by'
    // arrow but also does not have any other character for that function. So
    // lArr can be used for 'is implied by' as ISOtech suggests -->
    add("uArr", 8657);// <!-- upwards double arrow, U+21D1 ISOamsa -->
    add("rArr", 8658);// <!-- rightwards double arrow, U+21D2 ISOtech -->
    // <!-- Unicode does not say this is the 'implies' character but does not
    // have another character with this function so rArr can be used for
    // 'implies' as ISOtech suggests -->
    add("dArr", 8659);// <!-- downwards double arrow, U+21D3 ISOamsa -->
    add("hArr", 8660);// <!-- left right double arrow, U+21D4 ISOamsa -->

    // <!-- Mathematical Operators -->
    add("forall", 8704);// <!-- for all, U+2200 ISOtech -->
    add("part", 8706);// <!-- partial differential, U+2202 ISOtech -->
    add("exist", 8707);// <!-- there exists, U+2203 ISOtech -->
    add("empty", 8709);// <!-- empty set = null set, U+2205 ISOamso -->
    add("nabla", 8711);// <!-- nabla = backward difference, U+2207 ISOtech -->
    add("isin", 8712);// <!-- element of, U+2208 ISOtech -->
    add("notin", 8713);// <!-- not an element of, U+2209 ISOtech -->
    add("ni", 8715);// <!-- contains as member, U+220B ISOtech -->
    add("prod", 8719);// <!-- n-ary product = product sign, U+220F ISOamsb -->
    // <!-- prod is NOT the same character as U+03A0 'greek capital letter pi'
    // though the same glyph might be used for both -->
    add("sum", 8721);// <!-- n-ary summation, U+2211 ISOamsb -->
    // <!-- sum is NOT the same character as U+03A3 'greek capital letter sigma'
    // though the same glyph might be used for both -->
    add("minus", 8722);// <!-- minus sign, U+2212 ISOtech -->
    add("lowast", 8727);// <!-- asterisk operator, U+2217 ISOtech -->
    add("radic", 8730);// <!-- square root = radical sign, U+221A ISOtech -->
    add("prop", 8733);// <!-- proportional to, U+221D ISOtech -->
    add("infin", 8734);// <!-- infinity, U+221E ISOtech -->
    add("ang", 8736);// <!-- angle, U+2220 ISOamso -->
    add("and", 8743);// <!-- logical and = wedge, U+2227 ISOtech -->
    add("or", 8744);// <!-- logical or = vee, U+2228 ISOtech -->
    add("cap", 8745);// <!-- intersection = cap, U+2229 ISOtech -->
    add("cup", 8746);// <!-- union = cup, U+222A ISOtech -->
    add("int", 8747);// <!-- integral, U+222B ISOtech -->
    add("there4", 8756);// <!-- therefore, U+2234 ISOtech -->
    add("sim", 8764);// <!-- tilde operator = varies with = similar to, U+223C
                     // ISOtech -->
    // <!-- tilde operator is NOT the same character as the tilde, U+007E,
    // although the same glyph might be used to represent both -->
    add("cong", 8773);// <!-- approximately equal to, U+2245 ISOtech -->
    add("asymp", 8776);// <!-- almost equal to = asymptotic to, U+2248 ISOamsr
                       // -->
    add("ne", 8800);// <!-- not equal to, U+2260 ISOtech -->
    add("equiv", 8801);// <!-- identical to, U+2261 ISOtech -->
    add("le", 8804);// <!-- less-than or equal to, U+2264 ISOtech -->
    add("ge", 8805);// <!-- greater-than or equal to, U+2265 ISOtech -->
    add("sub", 8834);// <!-- subset of, U+2282 ISOtech -->
    add("sup", 8835);// <!-- superset of, U+2283 ISOtech -->
    add("nsub", 8836);// <!-- not a subset of, U+2284 ISOamsn -->
    add("sube", 8838);// <!-- subset of or equal to, U+2286 ISOtech -->
    add("supe", 8839);// <!-- superset of or equal to, U+2287 ISOtech -->
    add("oplus", 8853);// <!-- circled plus = direct sum, U+2295 ISOamsb -->
    add("otimes", 8855);// <!-- circled times = vector product, U+2297 ISOamsb
                        // -->
    add("perp", 8869);// <!-- up tack = orthogonal to = perpendicular, U+22A5
                      // ISOtech -->
    add("sdot", 8901);// <!-- dot operator, U+22C5 ISOamsb -->
    // <!-- dot operator is NOT the same character as U+00B7 middle dot -->

    // <!-- Miscellaneous Technical -->
    add("lceil", 8968);// <!-- left ceiling = APL upstile, +2308 ISOamsc -->
    add("rceil", 8969);// <!-- right ceiling, U+2309 ISOamsc -->
    add("lfloor", 8970);// <!-- left floor = APL downstile, U+230A ISOamsc -->
    add("rfloor", 8971);// <!-- right floor, U+230B ISOamsc -->
    add("lang", 9001);// <!-- left-pointing angle bracket = bra, U+2329 ISOtech
                      // -->
    // <!-- lang is NOT the same character as U+003C 'less than sign' or U+2039
    // 'single left-pointing angle quotation mark' -->
    add("rang", 9002);// <!-- right-pointing angle bracket = ket, U+232A ISOtech
                      // -->
    // <!-- rang is NOT the same character as U+003E 'greater than sign' or
    // U+203A 'single right-pointing angle quotation mark' -->

    // <!-- Geometric Shapes -->
    add("loz", 9674);// <!-- lozenge, U+25CA ISOpub -->

    // <!-- Miscellaneous Symbols -->
    add("spades", 9824);// <!-- black spade suit, U+2660 ISOpub -->
    // <!-- black here seems to mean filled as opposed to hollow -->
    add("clubs", 9827);// <!-- black club suit = shamrock, U+2663 ISOpub -->
    add("hearts", 9829);// <!-- black heart suit = valentine, U+2665 ISOpub -->
    add("diams", 9830);// <!-- black diamond suit, U+2666 ISOpub -->

    // <!-- Special characters for XHTML -->
    // <!-- C0 Controls and Basic Latin -->
    add("quot", 34);// <!-- quotation mark, U+0022 ISOnum -->
    add("amp", 38);// <!-- ampersand, U+0026 ISOnum -->
    add("lt", 60);// <!-- less-than sign, U+003C ISOnum -->
    add("gt", 62);// <!-- greater-than sign, U+003E ISOnum -->
    add("apos	", 39);// <!-- apostrophe = APL quote, U+0027 ISOnum -->

    // <!-- Latin Extended-A -->
    add("OElig", 338);// <!-- latin capital ligature OE, U+0152 ISOlat2 -->
    add("oelig", 339);// <!-- latin small ligature oe, U+0153 ISOlat2 -->
    // <!-- ligature is a misnomer, this is a separate character in some
    // languages -->
    add("Scaron", 352);// <!-- latin capital letter S with caron, U+0160 ISOlat2
                       // -->
    add("scaron", 353);// <!-- latin small letter s with caron, U+0161 ISOlat2
                       // -->
    add("Yuml", 376);// <!-- latin capital letter Y with diaeresis, U+0178
                     // ISOlat2 -->

    // <!-- Spacing Modifier Letters -->
    add("circ", 710);// <!-- modifier letter circumflex accent, U+02C6 ISOpub
                     // -->
    add("tilde", 732);// <!-- small tilde, U+02DC ISOdia -->

    // <!-- General Punctuation -->
    add("ensp", 8194);// <!-- en space, U+2002 ISOpub -->
    add("emsp", 8195);// <!-- em space, U+2003 ISOpub -->
    add("thinsp", 8201);// <!-- thin space, U+2009 ISOpub -->
    add("zwnj", 8204);// <!-- zero width non-joiner, U+200C NEW RFC 2070 -->
    add("zwj", 8205);// <!-- zero width joiner, U+200D NEW RFC 2070 -->
    add("lrm", 8206);// <!-- left-to-right mark, U+200E NEW RFC 2070 -->
    add("rlm", 8207);// <!-- right-to-left mark, U+200F NEW RFC 2070 -->
    add("ndash", 8211);// <!-- en dash, U+2013 ISOpub -->
    add("mdash", 8212);// <!-- em dash, U+2014 ISOpub -->
    // has to be added before lsquo and rsquo to avoid overriding real unicode
    // to name html mapping
    add("quoteleft", 8216);
    add("quoteright", 8217);
    add("lsquo", 8216);// <!-- left single quotation mark, U+2018 ISOnum -->
    add("rsquo", 8217);// <!-- right single quotation mark, U+2019 ISOnum -->
    add("sbquo", 8218);// <!-- single low-9 quotation mark, U+201A NEW -->
    add("ldquo", 8220);// <!-- left double quotation mark, U+201C ISOnum -->
    add("rdquo", 8221);// <!-- right double quotation mark, U+201D ISOnum -->
    add("bdquo", 8222);// <!-- double low-9 quotation mark, U+201E NEW -->
    add("dagger", 8224);// <!-- dagger, U+2020 ISOpub -->
    add("Dagger", 8225);// <!-- double dagger, U+2021 ISOpub -->
    add("permil", 8240);// <!-- per mille sign, U+2030 ISOtech -->
    add("lsaquo", 8249);// <!-- single left-pointing angle quotation mark,
                        // U+2039 ISO proposed -->
    // <!-- lsaquo is proposed but not yet ISO standardized -->
    add("rsaquo", 8250);// <!-- single right-pointing angle quotation mark,
                        // U+203A ISO proposed -->
    // <!-- rsaquo is proposed but not yet ISO standardized -->
    // <!-- Currency Symbols -->
    add("euro", 8364);// <!-- euro sign, U+20AC NEW -->
  }

  public static int smallCapital(int c)
  {
    c = Character.toUpperCase(c);
    switch (c)
    {
    case 'A':
      return '\u1D00';
    case 'B':
      return '\u0299';
    case 'C':
      return '\u1D04';
    case 'D':
      return '\u1D05';
    case 'E':
      return '\u1D07';
    case 'F':
      return '\uA730';
    case 'G':
      return '\u0262';
    case 'H':
      return '\u029C';
    case 'I':
      return '\u026A';
    case 'J':
      return '\u1D0A';
    case 'K':
      return '\u1D0B';
    case 'L':
      return '\u029F';
    case 'M':
      return '\u1D0D';
    case 'N':
      return '\u0274';
    case 'O':
      return '\u1D0F';
    case 'P':
      return '\u1D18';
    case 'Q':
      return 'a';//
    case 'R':
      return '\u0280';
    case 'S':
      return '\uA731';
    case 'T':
      return '\u1D1B';
    case 'U':
      return '\u1D1C';
    case 'V':
      return '\u1D20';
    case 'W':
      return '\u1D21';
    case 'X':
      return 'd';//
    case 'Y':
      return '\u028F';
    case 'Z':
      return '\u1D22';
    case 'Æ':
      return '\u1D01';
    case 'Œ':
      return '\u0276';
    }
    return c;
  }
}
