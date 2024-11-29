package sugarcube.common.system.util;

import sugarcube.common.system.log.Log;

import java.util.regex.Pattern;

public class Regex3
{
  public static final String URL = new StringBuilder()
  .append("((?:(ftp|ftps|Ftp|Ftps|http|https|Http|Https|rtsp|Rtsp):")
  .append("\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)")
    .append("\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_")
    .append("\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?")
    .append("((?:(?:[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}\\.)+")   // named host
    .append("(?:")   // plus top level domain
    .append("(?:aero|arpa|asia|a[cdefgivlmnoqrstuwxz])")
    .append("|(?:biz|b[abdefghijmnorstvwyz])")
    .append("|(?:cat|com|coop|c[acdfghiklmnoruvxyz])")
    .append("|d[ejkmoz]")
    .append("|(?:edu|e[cegrstu])")
    .append("|f[ijkmor]")
    .append("|(?:gov|g[abdefghilmnpqrstuwy])")
    .append("|h[kmnrtu]")
    .append("|(?:info|int|i[delmnoqrst])")
    .append("|(?:jobs|j[emop])")
    .append("|k[eghimnrwyz]")
    .append("|l[abcikrstuvy]")
    .append("|(?:mil|mobi|museum|m[acdghklmnopqrstuvwxyz])")
    .append("|(?:name|net|n[acefgilopruz])")
    .append("|(?:org|om)")
    .append("|(?:pro|p[aefghklmnrstwy])")
    .append("|qa")
    .append("|r[eouw]")
    .append("|s[abcdeghijklmnortuvyz]")
    .append("|(?:tel|travel|t[cdfghjklmnoprtvwz])")
    .append("|u[agkmsyz]")
    .append("|v[aceginu]")
    .append("|w[fs]")
    .append("|y[etu]")
    .append("|z[amw]))")
    .append("|(?:(?:25[0-5]|2[0-4]") // or ip address
    .append("[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(?:25[0-5]|2[0-4][0-9]")
    .append("|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1]")
    .append("[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}")
    .append("|[1-9][0-9]|[0-9])))")
    .append("(?:\\:\\d{1,5})?)") // plus option port number
    .append("(\\/(?:(?:[a-zA-Z0-9\\;\\/\\?\\:\\@\\&\\=\\#\\~")  // plus option query params
    .append("\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?")
    .append("(?:\\b|$)").toString();  
  
  public static final String EMAIL = "[a-zA-Z0-9\\.!#\\$%&'*+-/=?\\^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*";  
  
  public static void main(String... args)
  {
    Pattern pattern = Pattern.compile(Regex3.URL);
    
    String[] tests = new String[]{"http://www.bloechle.ch", "www.groupe-e.ch"};
    
    for(String test: tests)
    {
      Log.debug(Regex3.class, " - "+test+" : "+pattern.matcher(test).find());
    }
    
  }
}
