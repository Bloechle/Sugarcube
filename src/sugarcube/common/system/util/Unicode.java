package sugarcube.common.system.util;

public class Unicode
{
  public static String expand(int uni)
  {
    switch (uni)
    {
      case 0x0132:
        return "ij";
      case 0x0133:
        return "ij";
      case 0xFB00:
        return "ff";
      case 0xFB01:
        return "fi";
      case 0xFB02:
        return "fl";
      case 0xFB03:
        return "ffi";
      case 0xFB04:
        return "ffl";
      default:
        return new String(new int[]
          {
            uni
          }, 0, 1);
    }
  }
  
  public static String expand(char[] data)
  {
    return expand(new String(data));
  }  

  public static String expand(String data)
  {
    StringBuilder sb = null;
    for (int i = 0; i < data.length(); i++)
    {
      String lig = expand(data.codePointAt(i));
      if (sb != null)
        sb.append(lig);
      else if (lig.length() > 1)
      {
        sb = new StringBuilder(data.length());
        sb.append(data.substring(0, i)).append(lig);
      }
    }
    return sb == null ? data : sb.toString();
  }
}
