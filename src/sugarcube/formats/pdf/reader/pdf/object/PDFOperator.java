package sugarcube.formats.pdf.reader.pdf.object;

import java.util.LinkedHashMap;
import java.util.Map;

public class PDFOperator extends PDFObject
{
  public static enum OP
  {
    b("b"), B("B"), b_("b*"), B_("B*"), BDC("BDC"), BI("BI"), BMC("BMC"),
    BT("BT"), BX("BX"), c("c"), cm("cm"), CS("CS"), cs("cs"), d("d"), d0("d0"),
    d1("d1"), Do("Do"), DP("DP"), EI("EI"), EMC("EMC"), ET("ET"), EX("EX"),
    f("f"), F("F"), f_("f*"), G("G"), g("g"), gs("gs"), h("h"), i("i"), ID("ID"),
    j("j"), J("J"), K("K"), k("k"), l("l"), m("m"), M("M"), MP("MP"), n("n"),
    q("q"), Q("Q"), re("re"), RG("RG"), rg("rg"), ri("ri"), s("s"), S("S"),
    SC("SC"), sc("sc"), SCN("SCN"), scn("scn"), sh("sh"), T_("T*"), Tc("Tc"),
    Td("Td"), TD("TD"), Tf("Tf"), Tj("Tj"), TJ("TJ"), TL("TL"), Tm("Tm"), Tr("Tr"),
    Ts("Ts"), Tw("Tw"), Tz("Tz"), v("v"), w("w"), W("W"), W_("W*"), y("y"), apos("'"), guil("\"");
    private static final Map<String, OP> OPS = new LinkedHashMap<String, OP>();

    static
    {
      for (OP op : OP.values())
        OPS.put(op.name, op);
    }
    public final String name;

    private OP(String name)
    {
      this.name = name;
    }

    public static boolean contains(String name)
    {
      return OPS.containsKey(name);
    }

    public static OP getOP(String name)
    {
      return OPS.get(name);
    }
  }
  public static final PDFOperator NULL_PDFOPERATOR = new PDFOperator();
  private static final String NULL_OPERATOR = "NULL_OPERATOR";
  private final String operator;

  private PDFOperator()
  {
    super(Type.Operator);
    this.operator = NULL_OPERATOR;
  }

  public PDFOperator(PDFObject pdfObject, String operator, StreamReader reader)
  {
    super(Type.Operator, pdfObject);
    this.operator = operator;
    this.streamLocator = reader.streamLocator();
  }

  public static boolean isOperator(String token)
  {
    return OP.contains(token);
  }

  public OP op()
  {
    return OP.getOP(operator);
  }

  @Override
  public String stringValue()
  {
    return operator;
  }

  @Override
  public String toString()
  {
    return stringValue();
  }

  @Override
  public String sticker()
  {
    return nodeNamePrefix() + "Operator[" + operator + "]";
  }
}
