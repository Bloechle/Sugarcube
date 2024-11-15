package sugarcube.common.ui.fx.beans;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import sugarcube.common.data.collections.IntSet;
import sugarcube.common.data.collections.Str;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.ui.fx.base.Fx;

public class PString extends SimpleStringProperty
{
  public PString()
  {
  }

  public PString(String text)
  {
    super(text);
  }

  public String trim()
  {
    return get().trim();
  }

  public boolean is(String text)
  {
    return this.get().equals(text);
  }

  public void bindBidi(StringProperty friend)
  {
    Bindings.bindBidirectional(this, friend);
  }

  public Rectangle3 box()
  {
    return isVoid() ? null : Rectangle3.scan(this.get());
  }

  public IntSet ranges()
  {
    return Fx.ParseRanges(get());
  }

  public boolean hasData()
  {
    return Str.HasData(get());
  }

  public boolean hasChar()
  {
    return Str.HasChar(get());
  }

  public boolean isVoid()
  {
    return Str.IsVoid(this.get());
  }

  public boolean isTrimVoid()
  {
    return Str.IsVoid(this.get().trim());
  }

  public String[] split(String regex)
  {
    return this.get().trim().split(regex);
  }

  public String[] splitComma()
  {
    return split("\\s*,\\s*");
  }

  public String[] splitSpace(String... clean)
  {
    String v = get();
    for (String c : clean)
      v = v.replace(c, " ");
    return Str.Split(v.trim());
  }

  public PString copy()
  {
    return new PString(get());
  }

  public static PString New()
  {
    return new PString("");
  }

  public static PString New(String string)
  {
    return new PString(string);
  }
}
