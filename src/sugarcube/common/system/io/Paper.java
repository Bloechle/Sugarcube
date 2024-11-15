package sugarcube.common.system.io;

import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.Str;
import sugarcube.common.graphics.geom.Dimension3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Metric;
import sugarcube.common.data.xml.Nb;

public class Paper extends Dimension3
{
  public static final Paper VOID = new Paper(0, 0, "Void");
  // all sizes are defined in millimeters
  public static final Paper A0 = new Paper(841, 1189, "A0");// 1 square meter
  public static final Paper B0 = new Paper(1000, 1414, "B0");// sqrt(0.5) square
                                                             // meter (between
                                                             // A0 and A1)
  public static final Paper C0 = new Paper(917, 1297, "C0");// (A0 + B0)/2
  public static final Paper LETTER = new Paper(215.9, 279.4, "Letter");
  public static final Paper GOV_LETTER = new Paper(203.2, 266.7, "Gov Letter");
  public static final Paper LEGAL = new Paper(215.9, 355.6, "Legal");
  public static final Paper TABLOID = new Paper(279, 432, "Tabloid");
  public static final Paper CREDIT_CARD = new Paper(85.6, 53.98, "Credit Card");
  public static final Paper BUSINESS_CARD_ISO = new Paper(74, 52, "Business Card");

  public String name;

  public Paper(double w, double h, String name)
  {
    super(w, h);
    this.name = name;
  }

  public Paper(Dimension3 dim, String name)
  {
    this(dim.width(), dim.height(), name);
  }

  public Rectangle3 box()
  {
    return this.asRectangle();
  }

  public Rectangle3 ocdBox()
  {
    return new Rectangle3(0, 0, Metric.mm2ocd(width()), Metric.mm2ocd(height()));
  }

  public Dimension3 dim()
  {
    return new Dimension3(width(), height());
  }

  public Dimension3 ocdDim()
  {
    return new Dimension3(Metric.mm2ocd(width()), Metric.mm2ocd(height()));
  }

  public Paper portrait(boolean portrait)
  {
    return portrait == isPortrait() ? this : swap();
  }

  @Override
  public Paper swap()
  {
    return new Paper(height(), width(), name);
  }

  public boolean isPortrait()
  {
    return height() > width();
  }

  public static Paper A(int index)
  {
    return ISO(A0, index);
  }

  public static Paper B(int index)
  {
    return ISO(B0, index);
  }

  public static Paper C(int index)
  {
    return ISO(C0, index);
  }

  public static Paper ISO(Paper p0, int index)
  {
    return new Paper(dim(p0, index), p0.name.charAt(0) + "" + index);
  }

  public static Dimension3 dim(Dimension3 d0, int i)
  {
    return i == 0 ? d0 : i > 0 ? dim(new Dimension3(d0.height() / 2, d0.width()), i - 1) : dim(new Dimension3(d0.width() * 2, d0.height()), i + 1);
  }

  public static Paper get(String name, Paper def)
  {
    return dimension(name, def);
  }

  public static Paper dimension(String name, Paper def)
  {
    if(Str.IsVoid(name))
      return def;
    name = name.trim().toLowerCase().replace(' ', '-').replace('_', '-');
    if (name.length() < 4)
    {
      int i = Nb.Int(name.substring(1), -1000);
      if (i > -10 && i < 20)
        switch (name.charAt(0))
        {
        case 'a':
          return A(i);
        case 'b':
          return B(i);
        case 'c':
          return C(i);
        }
    }
    switch (name)
    {
    case "custom":
      return def;
    case "letter":
      return LETTER;
    case "government-letter":
    case "gov-letter":
      return GOV_LETTER;
    case "legal":
      return LEGAL;
    case "tabloid":
      return TABLOID;
    case "credit-card":
      return CREDIT_CARD;
    case "business-card":
    case "iso-business-card":
      return BUSINESS_CARD_ISO;
    }
    return def;
  }

  public double distance(Paper paper)
  {
    return Math.abs(this.width() - paper.width()) + Math.abs(this.height() - paper.height());
  }

  public Paper copy()
  {
    return new Paper(width(), height(), name);
  }

  @Override
  public String toString()
  {
    return name + " - " + Nb.String(width(), 2) + "x" + Nb.String(height(), 2) + "mm";
  }
  
  public static Paper ocdSelect(String abc, double w, double h)
  {
    return select(abc, Metric.ocd2mm(w), Metric.ocd2mm(h));
  }
  
  public static Paper select(String abc, double w, double h)
  {
    boolean portrait = h > w;
    if (!portrait)
    {
      double tmp = w;
      w = h;
      h = tmp;
    }

    abc = Zen.avoid(abc, "abc").toLowerCase().trim().replace(" ", "").replace(",", "");
    Paper paper = new Paper(w, h, abc);
    Paper best = Paper.VOID.copy();
    double min = paper.width() + paper.height();

    for (char c : abc.toCharArray())
      for (int i = 0; i < 10; i++)
      {
        Paper p = c == 'b' ? B(i) : c == 'c' ? C(i) : A(i);
        double d = paper.distance(p);
        if (d < min)
        {
          min = d;
          best = p;
        }
      }
    return best.name.equals(VOID.name) ? paper : best.portrait(portrait);
  }  

  public static void main(String... args)
  {
    Paper.A(4);
  }

}
