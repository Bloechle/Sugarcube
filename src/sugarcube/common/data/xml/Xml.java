package sugarcube.common.data.xml;

import org.xml.sax.InputSource;
import sugarcube.common.system.reflection.Annot._Xml;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.FloatArray;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.collections.StringList;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.interfaces.Visitor;
import sugarcube.common.system.io.IO;
import sugarcube.common.system.reflection.ClassField;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;

public class Xml
{
  public static final Collection<? extends XmlINode> SELF_CLOSE = Collections.emptyList();
  public static final boolean DEFAULT_TAG_SELF_CLOSING = true;
  public static final String DEFAULT_DATA_SEPARATOR = " ";
  public static final String TAG_ID = "id";
  public static final String ENCODING = "UTF-8";
  public static final String EXT = ".xml";
  public static final String FILE_EXTENSION = EXT;
  public static String UTF8_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
  public static String UTF8_HEADER_STANDALONE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
  private StringSet duplicates = new StringSet();
  private StringList inlineTags = new StringList();
  private Appendable data;
  private String indentation = " ";
  private String currentOffset = "";
  private boolean tagSelfClosing;
  private boolean hasCData = false;
  private int skipReturn = 0;
  private XmlDecimalFormat decimalFormat;
  private int decimalFactor;
  private String dataSeparator = DEFAULT_DATA_SEPARATOR;
  private String lineReturn = "\n";
  private char lastChar = ' ';
  private boolean standalone = false;
  private boolean isHTML = false;

  public Xml()
  {
    this(2, DEFAULT_DATA_SEPARATOR);
  }

  public Xml(String separator)
  {
    this(2, separator);
  }

  public Xml(int indentation, String separator)
  {
    this(new StringBuilder(), indentation, DEFAULT_TAG_SELF_CLOSING, separator);
  }

  public Xml(Appendable stream)
  {
    this(stream, 2, DEFAULT_TAG_SELF_CLOSING, DEFAULT_DATA_SEPARATOR);
  }

  public Xml(Appendable stream, int indentation)
  {
    this(stream, indentation, DEFAULT_TAG_SELF_CLOSING, DEFAULT_DATA_SEPARATOR);
  }

  public Xml(Appendable stream, int indentation, boolean tagSelfClosing)
  {
    this(stream, indentation, tagSelfClosing, DEFAULT_DATA_SEPARATOR);
  }

  public Xml(Appendable stream, int indentation, boolean tagSelfClosing, String separator)
  {
    this.data = stream;
    this.indentation = "";
    this.dataSeparator = separator;
    this.tagSelfClosing = tagSelfClosing;
    if (indentation < 0)
      indentation = 2;
    for (int i = 0; i < indentation; i++)
      this.indentation += " ";
    // less than 3 decimals may lead to rounding side effects, especially with
    // transform matrices
    this.setNumberOfDecimals(4);
  }

  public Xml html(boolean isHTML)
  {
    this.isHTML = isHTML;
    return this;
  }

  public boolean isHTML()
  {
    return this.isHTML;
  }

  public Xml standalone(boolean bool)
  {
    this.standalone = bool;
    return this;
  }

  public Xml skipReturn(int nb)
  {
    this.skipReturn = nb;
    return this;
  }

  public Xml setSeparator(String separator)
  {
    this.dataSeparator = separator;
    return this;
  }

  public final Xml setNumberOfDecimals(int nb)
  {
    this.decimalFactor = 1;
    for (int i = 0; i <= nb; i++)
      this.decimalFactor *= 10;
    this.decimalFormat = decimalFormat(nb);
    return this;
  }

  // public Properties3 properties()
  // {
  // return this.props;
  // }
  public XmlDecimalFormat numberFormat()
  {
    return decimalFormat;
  }

  public XmlDecimalFormat nf()
  {
    return numberFormat();
  }

  public String indentation()
  {
    return this.indentation;
  }

  public String currentOffset()
  {
    return this.currentOffset;
  }

  private Xml raw(char... chars)
  {
    if (chars.length > 0)
      try
      {
        for (char c : chars)
          this.data.append(c);
        this.lastChar = chars[chars.length - 1];
      } catch (IOException ex)
      {
        Log.error(this, ".raw - IOException: " + ex.getMessage());
      }
    return this;
  }

  private Xml raw(String seq)
  {
    if (!seq.isEmpty())
      try
      {
        this.data.append(seq);
        this.lastChar = seq.charAt(seq.length() - 1);
      } catch (IOException ex)
      {
        Log.error(this, ".raw - IOException: " + ex.getMessage());
      }
    return this;
  }

//  private Xml esc(char... chars)
//  {
//    if (chars.length > 0)
//      try
//      {
//        for (char c : chars)
//          if (CharRef.isEscaped(c))
//            this.data.append(CharRef.escape(c));
//          else
//            this.data.append(c);
//        this.lastChar = chars[chars.length - 1];
//      } catch (IOException ex)
//      {
//        Log.error(this, ".esc - IOException: " + ex.getMessage());
//      }
//    return this;
//  }

  private Xml esc(String seq)
  {
    if (!seq.isEmpty())
      try
      {
        // if (!CharRef.isEscaped(seq) || seq.charAt(0) == '&' &&
        // seq.charAt(seq.length() - 1) == ';')
        // this.data.append(seq);
        // else
        // this.data.append(CharRef.escape(seq));
        this.data.append(CharRef.Html(seq));
        this.lastChar = seq.charAt(seq.length() - 1);
      } catch (Exception ex)
      {
        Log.error(this, ".esc - Exception: " + ex.getMessage());
      }
    return this;
  }

  public Xml startCDataInlineTagging()
  {
    this.hasCData = true;
    return this.closeTag();
  }

  public Xml openInlineTag(String tag, Object... a)
  {
    raw('<').esc(tag);
    for (int i = 0; i + 1 < a.length; i += 2)
      raw(' ').raw(a[i].toString()).raw('=', '"').esc(a[i + 1].toString()).raw('"');
    raw('>');
    if (a.length % 2 != 0)
      inlineCData(a[a.length - 1].toString());
    this.inlineTags.add(tag);
    return this;
  }

  public Xml inlineCData(String cdata)
  {
    return esc(cdata);
  }

  public Xml closeInlineTag()
  {
    return raw('<', '/').esc(inlineTags.removeLast()).raw('>');
  }

  public Xml closeInlineTags()
  {
    while (!inlineTags.isEmpty())
      raw('<', '/').esc(inlineTags.removeLast()).raw('>');
    return this;
  }

  public Xml inlineTag(String tag, Object... a)
  {
    raw('<').esc(tag);
    for (int i = 0; i + 1 < a.length; i += 2)
      raw(' ').raw(a[i].toString()).raw('=', '"').esc(a[i + 1].toString()).raw('"');
    if (a.length % 2 != 0)
    {
      raw('>');
      inlineCData(a[a.length - 1].toString());
      raw('<', '/').esc(tag).raw('>');
    } else if (this.tagSelfClosing)
      raw('/', '>');
    else
      raw('<', '/').esc(tag).raw('>');
    return this;
  }

  public boolean badName(String name)
  {
    return name == null || name.isEmpty() || duplicates.yet(name);
  }

  public boolean isCData(String name)
  {
    return hasCData = name.equals(XmlINode.CDATA);
  }

  public final void writeXML(XmlINode node, String offset)
  {
    this.currentOffset = offset;
    String tag = node.tag();
    // ensures that each attribute is written only one time for each elements
    // !!!
    this.duplicates.clear();
    // null or empty tag used when XmlINode is XmlCData node
    if ((tag == null || tag.isEmpty()))
    {
      node.xmlizer().writeAttributes(this);
    } else
    {
      // allowing cdata tags to be contiguous (avoiding white spaces add)
      raw(skipReturn <= 0 ? lineReturn + offset : "").raw('<').esc(node.tag());

      if (skipReturn > 0)
        skipReturn--;

      if (hasCData)
        hasCData = false;
      Collection<? extends XmlINode> children = node.xmlizer().writeAttributes(this);
      boolean addCR = !hasCData;
      // do not use node.children() since xml writing may hide children
      if (writeChildren(offset, children))
        raw(addCR ? lineReturn + offset : "").raw('<', '/').esc(node.tag()).raw('>');
      else if (this.tagSelfClosing || children == null)
        raw('/', '>');// HTML5 does not support self closing tags !!!
      else
        raw('>', '<', '/').esc(node.tag()).raw('>');
    }
  }

  protected boolean writeChildren(String offset, Collection<? extends XmlINode> nodes)
  {
    this.currentOffset = offset;
    if (nodes != null && !nodes.isEmpty())
    {
      if (!hasCData)
        this.closeTag();
      for (XmlINode node : nodes)
        writeXML(node, offset + indentation);
      return true;
    } else
      return hasCData;
  }

  private Xml openAt(String name)
  {
    raw(' ').esc(name).raw('=', '\"');
    return this;
  }

  private Xml closeAt()
  {
    if (!hasCData)
      raw('\"');
    return this;
  }

  private Xml closeTag()
  {
    if (lastChar != '>')
      raw('>');
    return this;
  }

  public Xml writeField(Field field, XmlINode node)
  {
    String key = field.getName();
    if (field.isAnnotationPresent(_Xml.class))
    {
      _Xml annot = (_Xml) field.getAnnotation(_Xml.class);
      if (annot.key() != null && !annot.key().trim().isEmpty())
        key = annot.key();
      if (annot.ns() != null && !annot.ns().trim().isEmpty())
        key = annot.ns() + ":" + key;
    }
    try
    {
      ClassField f3 = ClassField.Get(field, node);
      if (f3.hasValue())
      {
        if (f3.isString())
          this.write(key, f3.value());
        else if (f3.isReal())
          this.write(key, f3.real());
        else if (f3.isInteger())
          this.write(key, f3.integer());
        else if (f3.isBool())
          this.write(key, f3.bool());
      }
    } catch (Exception e)
    {
      e.printStackTrace();
      return this;
    }
    return this;
  }

  public Xml writeID(String id)
  {
    return id == null ? this : this.write(TAG_ID, id);
  }

  public Xml writeCData(String cdata, boolean doEscape)
  {
    if (cdata != null)
    {
      this.hasCData = true;
      return doEscape ? closeTag().esc(cdata) : closeTag().raw(cdata);
    }
    return this;
  }

  public Xml writeCData(String cdata)
  {
    return writeCData(cdata, true);
  }

  public Xml write(boolean[] data)
  {
    if (data != null && data.length != 0)
    {
      this.hasCData = true;
      closeTag().writeBools(data);
    }
    return this;
  }

  public Xml write(int[] data)
  {
    if (data != null && data.length != 0)
    {
      this.hasCData = true;
      closeTag().writeInts(data);
    }
    return this;
  }

  public Xml write(float[] data)
  {
    if (data != null && data.length != 0)
    {
      this.hasCData = true;
      closeTag().writeFloats(data);
    }
    return this;
  }

  public Xml write(String[] data)
  {
    if (data != null && data.length != 0)
    {
      this.hasCData = true;
      closeTag().writeStrings(data);
    }
    return this;
  }

  public Xml writeAlone(String name)
  {
    return badName(name) ? this : raw(' ').esc(name);
  }

  public Xml write(String name, String data)
  {
    return badName(name) || data == null || data.isEmpty() ? this
        : isCData(name) ? closeTag().writeString(data) : openAt(name).writeString(data).closeAt();
  }

  public Xml write(String name, String data, boolean doEscape)
  {
    return badName(name) || data == null || data.isEmpty() ? this
        : isCData(name) ? closeTag().writeString(data, doEscape) : openAt(name).writeString(data, doEscape).closeAt();
  }

  public Xml write(String name, String[] data)
  {
    return badName(name) || data == null || data.length == 0 ? this
        : isCData(name) ? closeTag().writeStrings(data) : openAt(name).writeStrings(data).closeAt();
  }

  public Xml write(String name, boolean data)
  {
    return write(name, data ? "true" : "false");
  }

  public Xml write(String name, boolean[] data)
  {
    return badName(name) || data == null || data.length == 0 ? this
        : isCData(name) ? closeTag().writeBools(data) : openAt(name).writeBools(data).closeAt();
  }

  public Xml write(String name, int data)
  {
    return badName(name) ? this : isCData(name) ? closeTag().writeInts(data) : openAt(name).writeInts(data).closeAt();
  }

  public Xml write(String name, int[] data)
  {
    return badName(name) || data == null || data.length == 0 ? this
        : isCData(name) ? closeTag().writeInts(data) : openAt(name).writeInts(data).closeAt();
  }

  public Xml write(String name, double data)
  {
    return this.write(name, (float) data);
  }

  public Xml write(String name, float data)
  {
    return badName(name) || Float.isNaN(data) ? this : isCData(name) ? closeTag().writeFloats(data) : openAt(name).writeFloats(data).closeAt();
  }

  public Xml write(String name, float[] data)
  {
    return badName(name) || data == null || data.length == 0 ? this
        : isCData(name) ? closeTag().writeFloats(data) : openAt(name).writeFloats(data).closeAt();
  }

  private Xml writeString(String data)
  {
    this.esc(data);
    return this;
  }

  private Xml writeString(String data, boolean doEscape)
  {
    if (doEscape)
      this.esc(data);
    else
      this.raw(data);
    return this;
  }

  private Xml writeStrings(String[] data)
  {
    data = escapeWhitespaces(data);
    for (int i = 0; i < data.length; i++)
      this.esc(data[i] + (i < data.length - 1 ? dataSeparator : ""));
    return this;
  }

  private Xml writeBools(boolean... data)
  {
    int[] ints = new int[data.length];
    for (int i = 0; i < ints.length; i++)
      ints[i] = data[i] ? 1 : 0;
    return writeInts(ints);
  }

  private Xml writeInts(int... data)
  {
    for (int i = 0; i < data.length; i++)
      this.raw(data[i] + (i < data.length - 1 ? dataSeparator : ""));
    return this;
  }

  private Xml writeFloats(float... data)
  {
    for (int i = 0; i < data.length; i++)
      this.raw(toString(data[i]) + (i < data.length - 1 ? dataSeparator : ""));
    return this;
  }

  public String toString(double d)
  {
    return toString(d, decimalFormat);
  }

  public String toString(float[] data)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < data.length; i++)
      sb.append(toString(data[i])).append((i < data.length - 1 ? dataSeparator : ""));
    return sb.toString();
  }

  public static String toString(double d, XmlDecimalFormat df)
  {
    return df == null ? Long.toString(Math.round(d)) : df.format(d);
  }

  public static XmlDecimalFormat decimalFormat(int nbOfDecimals)
  {
    return new XmlDecimalFormat(nbOfDecimals, 2);
  }

  public boolean isZero(float a)
  {
    return Math.abs(a) < 1f / (decimalFactor * 10);
  }

  public boolean isOne(float a)
  {
    return isZero(a - 1);
  }

  public boolean isZero(float[] a)
  {
    for (int i = 0; i < a.length; i++)
      if (!isZero(a[i]))
        return false;
    return true;
  }

  public boolean sameValues(float[] a)
  {
    if (a.length == 0)
      return false;
    float first = a[0];
    for (int i = 1; i < a.length; i++)
      if (!isZero(a[i] - first))
        return false;
    return true;
  }

  public float[] trimSameValues(float[] a)
  {
    if (a.length == 0)
      return a;
    float last = a[a.length - 1];
    for (int i = a.length - 2; i >= 0; i--)
      if (!isZero(last - a[i]))
      {
        if (i == a.length - 2)
          return a;
        float[] trimmed = new float[i + 2];
        System.arraycopy(a, 0, trimmed, 0, trimmed.length);
        return trimmed;
      }
    return Zen.Array.Floats(last);
  }

  public String compressFloats(float[] floats)
  {
    StringBuilder sb = new StringBuilder(floats.length * 2);
    int repeat = 0;
    for (int i = 1; i < floats.length; i++)
    {
      repeat++;
      if (!equals(floats[i], floats[i - 1]))
      {
        sb.append(sb.length() == 0 ? "" : " ").append(repeat == 1 ? "" : repeat + "#").append(toString(floats[i - 1]));
        repeat = 0;
      }
    }
    sb.append(sb.length() == 0 ? "" : " ").append(repeat == 0 ? "" : repeat + "#").append(toString(floats[floats.length - 1]));
    return sb.toString();
  }

  public static float[] uncompressFloats(String string)
  {
    FloatArray floats = new FloatArray(string.length() * 4);
    for (String token : Str.Split(string.trim()))
    {
      int i = token.indexOf("#");
      if (i > 0)
      {
        int repeat = Nb.Int(token.substring(0, i), 1);
        float value = Nb.Float(token.substring(i + 1));
        floats.add(Zen.Array.instance(repeat, value));
      } else
        floats.add(Nb.Float(token, 0));
    }
    return floats.array();
  }

  public boolean equals(Object a, Object b)
  {
    return a == null || b == null ? false : a.equals(b);
  }

  public boolean equals(double a, double b)
  {
    return Double.isNaN(a) && Double.isNaN(b) || Math.abs(a - b) < 1.0 / decimalFactor;
  }

  public boolean equals(float a, float b)
  {
    return Float.isNaN(a) && Float.isNaN(b) || Math.abs(a - b) < 1f / decimalFactor;
  }

  public boolean equals(float[] a, float... b)
  {
    if (a == null && b == null)
      return true;
    else if (a == null || b == null || a.length != b.length)
      return false;
    else
      for (int i = 0; i < a.length; i++)
        if (!equals(a[i], b[i]))
          return false;
    return true;
  }

  @Override
  public String toString()
  {
    return data.toString();
  }

  public static String toString(XmlINode node)
  {
    return toString(node, false);
  }

  public static String toString(XmlINode node, boolean isHTML)
  {
    Xml xml = new Xml().html(isHTML);
    xml.write(node);
    return xml.toString();
  }

  public Xml writeHeader(String... headerLines)
  {
    this.raw(standalone ? UTF8_HEADER_STANDALONE : UTF8_HEADER);
    for (String headerLine : headerLines)
      if (headerLine != null && !headerLine.isEmpty())
        this.raw(lineReturn + headerLine);
    return this;
  }

  public Xml close()
  {
    this.raw(lineReturn);
    this.inlineTags.clear();
    this.data = null;
    return this;
  }

  public Xml write(XmlINode node)
  {
    writeXML(node, "");
    return this;
  }

  public static boolean Store(XmlINode node, File file)
  {
    if (file.isDirectory())
      Log.error(Xml.class, ".Store - not a valid file name:" + file);
    try
    {
      if (file.getParentFile() != null && !file.getParentFile().exists())
        file.getParentFile().mkdirs();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), ENCODING));
      writer.write(UTF8_HEADER);
      Xml xml = new Xml(writer);
      xml.write(node);
      writer.flush();
      writer.close();
      Log.debug(Xml.class, ".Store - write successful: " + file.getAbsolutePath());
      return true;
    } catch (IOException e)
    {
      Log.warn(Xml.class, ".Store - write error: " + e);
      e.printStackTrace();
      return false;
    }
  }

  public static DomNode Parse(File file)
  {
    try
    {
      return Parse(new FileInputStream(file));
    } catch (Exception ex)
    {
      Log.error(Xml.class, ".Parse - " + ex.getMessage());
      ex.printStackTrace();
    }
    return null;
  }

  public static DomNode Parse(InputStream stream)
  {
    DomNode element = null;
    try
    {
      stream = stream instanceof BufferedInputStream ? stream : new BufferedInputStream(stream);
      element = new DomNode(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream).getDocumentElement());
    } catch (Exception ex)
    {
      Log.error(Xml.class, ".Parse - " + ex.getMessage());
      ex.printStackTrace();
    }
    IO.Close(stream);
    return element;
  }

  public static DomNode Parse(String xml)
  {
    DomNode element = null;
    try
    {
      InputSource source = new InputSource(new ByteArrayInputStream(xml.getBytes("UTF-8")));
      element = new DomNode(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source).getDocumentElement());
    } catch (Exception ex)
    {
      Log.error(Xml.class, ".Parse - " + ex.getMessage());
      ex.printStackTrace();
    }
    return element;
  }

  public static InputStream Load(XmlINode node, InputStream stream)
  {
    node = Load(node, DomNode.Load(stream));
    return node == null ? null : stream;
  }

  public static XmlINode Load(XmlINode node, String xml)
  {
    return Load(node, Parse(xml));
  }

  public static XmlINode Load(XmlINode node, File file)
  {
    return Load(node, Parse(file));
  }

  public static XmlINode Load(XmlINode node, DomNode element)
  {
    if (element == null)
      return null;
    try
    {
      node.xmlizer().readAttributes(element);
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    try
    {
      for (DomNode childElement : element)
      {
        XmlINode childNode = node.xmlizer().newChild(childElement);
        if (childNode != null)
          node.xmlizer().endChild(Load(childNode, childElement));
      }
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    node.xmlizer().endChild(null);
    return node;
  }

  public static boolean VisitTree(XmlINode node, Visitor<XmlINode> visitor)
  {
    for (XmlINode child : node.children())
    {
      if (visitor.visit(child))
        return true;
      if (VisitTree(child, visitor))
        return true;
    }
    return false;
  }

  public static String[] escapeWhitespaces(String[] a)
  {
    String[] escaped = new String[a.length];
    for (int i = 0; i < a.length; i++)
      escaped[i] = a[i].contains(" ") ? a[i].replaceAll(" ", "|") : a[i];
    return escaped;
  }

  public static String[] unescapeWhitespaces(String[] a)
  {
    for (int i = 0; i < a.length; i++)
      if (a[i].contains("|"))
        a[i] = a[i].replaceAll("|", " ");
    return a;
  }

  public static boolean write(XmlINode node, Writer writer, int indent, boolean header)
  {
    try
    {
      writer = writer instanceof BufferedWriter ? writer : new BufferedWriter(writer); // "UTF-8"
      Xml xml = new Xml(writer, indent);
      if (header)
        xml.writeHeader();
      xml.write(node);
      xml = null;
      writer.flush();
      // write.close(); //do not uncomment this since zip outputstream needs to
      // stay open for further entries
      writer = null;
      return true;
    } catch (Exception e)
    {
      Log.warn(Xml.class, ".write - Xml outputstream writing exception: " + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }

  public static boolean write(XmlINode node, OutputStream stream, int indent, boolean header)
  {
    try
    {
      return write(node, new BufferedWriter(new OutputStreamWriter(stream, Xml.ENCODING)), indent, header);
    } catch (UnsupportedEncodingException e)
    {
      Log.warn(Xml.class, ".write - Xml outputstream writing exception: " + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }

  public static boolean write(XmlINode node, File file)
  {
    try
    {
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Xml.ENCODING)); // "UTF-8"
      Xml xml = new Xml(writer, 2);
      xml.writeHeader();
      xml.write(node);
      xml = null;
      writer.flush();
      writer.close();
      writer = null;
      return true;
    } catch (Exception e)
    {
      Log.warn(Xml.class, ".write - Xml outputstream writing exception: " + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }

  public static boolean IsValid(InputStream stream)
  {
    boolean isValid = DomNode.Load(stream) != null;
    IO.Close(stream);
    return isValid;
  }

  public static boolean IsValid(File file)
  {
    return DomNode.Load(file) != null;
  }
}
