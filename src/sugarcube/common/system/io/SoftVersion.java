package sugarcube.common.system.io;

import sugarcube.common.data.Zen;
import sugarcube.common.data.xml.Nb;

public class SoftVersion
{
  //major.minor.revision(.build)
  public final String software;
  public final int major;
  public final int minor;
  public final int revision;
  public final int build;
  public final String date;

  public SoftVersion(String software, String date, int... version)
  {
    this.software = software;
    int[] v4 = version4(version);
    major = v4[0];
    minor = v4[1];
    revision = v4[2];
    build = v4[3];
    this.date = date;
  }

  public SoftVersion(String software, String date, String version)
  {
    this(software, date, parseVersion(version));
  }

  public static int[] parseVersion(String version)
  {
    int[] v4 = new int[4];
    String[] token = version.trim().split("\\.");
    v4[0] = token.length > 0 ? nb(token[0]) : 1;
    v4[1] = token.length > 1 ? nb(token[1]) : 0;
    v4[2] = token.length > 2 ? nb(token[2]) : 0;
    v4[3] = token.length > 3 ? nb(token[3]) : 0;
    return v4;
  }

  public boolean isAtLeast(String v)
  {
    return isAtLeast(parseVersion(v));
  }

  public boolean isAtLeast(SoftVersion v)
  {
    return isAtLeast(v.arrayValue());
  }

  public boolean isAtLeast(int... v)
  {
    //release and build are not taken into account
    int[] v4 = version4(v);
    if (major > v4[0])
      return true;
    if (major < v4[0])
      return false;
    if (minor > v4[1])
      return true;
    if (minor < v4[1])
      return false;
    return true;
  }

  public boolean isLessThan(String v)
  {
    return !isAtLeast(v);
  }

  public boolean isLessThan(SoftVersion v)
  {
    return !isAtLeast(v);
  }

  public boolean isLessThan(int... v)
  {
    return !isAtLeast(v);
  }

  public int[] arrayValue()
  {
    return Zen.Array.Ints(major, minor, revision, build);
  }

  private int[] version4(int... version)
  {
    if (version.length == 4)
      return version;
    int[] v4 = new int[4];
    v4[0] = version.length > 0 ? version[0] : 1;
    v4[1] = version.length > 1 ? version[1] : 0;
    v4[2] = version.length > 2 ? version[2] : 0;
    v4[3] = version.length > 3 ? version[3] : 0;
    return v4;
  }

  private static int nb(String s)
  {
    if (s == null)
      return 0;
    s = s.trim();
    while (!s.isEmpty() && !Character.isDigit(s.charAt(0)))
      s = s.substring(1, s.length());
    while (!s.isEmpty() && !Character.isDigit(s.charAt(s.length() - 1)))
      s = s.substring(0, s.length() - 1);
    return Nb.Int(s, 0);
  }

  public String versionValue()
  {
    return major + "." + minor + "." + revision + (build > 0 ? "." + build : "");
  }

  @Override
  public String toString()
  {
    return software + " v" + versionValue();
  }
  
  public SoftVersion derive(String software)
  {
    return new SoftVersion(software, date, major, minor, revision, build);
  }
}
