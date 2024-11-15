package sugarcube.insight.ribbon.actions.search;

import sugarcube.common.data.collections.Str;

public class SearchNorm
{
  public boolean caseSensitive = true;
  public boolean spaceSensitive = true;
  public boolean accentSensitive = false;

  public SearchNorm(boolean cs, boolean sp, boolean accent)
  {
    this.caseSensitive = cs;
    this.spaceSensitive = sp;
    this.accentSensitive = accent;
  }
  
  public String apply(String text)
  {
    if (!caseSensitive)
      text = text.toLowerCase();
    if (!spaceSensitive)
      text = text.replace(" ", "");
    if (!accentSensitive)
      text = Str.UnAccent(text);
    return text;
  }
}
