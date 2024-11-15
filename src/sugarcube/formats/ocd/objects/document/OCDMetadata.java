package sugarcube.formats.ocd.objects.document;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.collections.Stringer;
import sugarcube.common.data.Base;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.system.time.Date3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDEntry;
import sugarcube.formats.ocd.objects.OCDNode;
import sugarcube.formats.ocd.objects.metadata.dc.DC;
import sugarcube.formats.ocd.objects.metadata.dc.DCElement;
import sugarcube.formats.ocd.objects.metadata.dc.OPF;
import sugarcube.formats.ocd.objects.metadata.dc.OPF.Role;
import sugarcube.formats.ocd.objects.metadata.powerswitch.PSMeta;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OCDMetadata extends OCDEntry implements Iterable<DCElement>
{
  public static final String TAG = "metadata";
  public static final String XMLNS_DC = "http://purl.org/dc/elements/1.1/";
  public static final String XMLNS_OPF = "http://www.idpf.org/2007/opf/";
  protected List3<DCElement> elements = new List3<DCElement>();

  public OCDMetadata(OCDNode parent)
  {
    super(TAG, parent, "meta.xml");
  }

  public boolean isEmpty()
  {
    return this.elements.isEmpty();
  }

  public List3<DCElement> elements()
  {
    return this.elements;
  }

  public void populateMap(Map<String, String> map, String prefix, boolean override)
  {
    for (DCElement element : elements)
    {
      String key = prefix + DC.name(element.tag());
      String val = element.cdata();
      if ((Str.IsVoid(map.get(key)) || override) && !Str.IsVoid(val))
        map.put(key, val);
    }
  }

  public void populateMeta(Map<String, String> map, String prefix, boolean override)
  {

    for (Map.Entry<String, String> entry : map.entrySet())
    {
      String key = entry.getKey();
      boolean ok = false;
      if (prefix == null || prefix.isEmpty())
        ok = true;
      else if (key.startsWith(prefix))
      {
        ok = true;
        key = key.substring(prefix.length());
      }
      if (ok && DC.isDC(key))
      {
        key = DC.nsName(key);
        if (override)
          this.remove(key);
        this.add(key, entry.getValue());
      }
    }

  }

  public OCDMetadata reset(OCDMetadata meta)
  {
    this.clear();
    for (DCElement element : meta)
      this.add(element.copy());
    return this;
  }

  public boolean load(File file)
  {
    try
    {
      return load(new FileInputStream(file));
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return false;
  }

  public boolean load(InputStream stream)
  {
    DomNode node = Xml.Parse(stream);
    if (node != null)
    {
      String root = node.tag().toLowerCase();
      if (root.equals("metadata") || node.has("xmlns:dc"))
      {
        this.clear();
        Xml.Load(this, node);
        this.complete();
        return true;
      } else if (root.equals("valuedescription") || node.value("Type", "").equals("metadatafields"))
      {
        this.clear();
        PSMeta meta = new PSMeta();
        Xml.Load(meta, node);
        meta.populate(this);
        this.complete();
        return true;
      }
    }
    return false;
  }

  public void complete()
  {
    String identifier = this.value(DC.identifier, null);
    if (identifier == null || identifier.trim().isEmpty())
      this.add(DC.identifier, Base.x32.random16());
    String date = this.value(DC.date, null);
    if (date == null || date.trim().isEmpty())
      this.add(DC.date, Date3.UTC());
    String contributor = this.value(DC.contributor, null);
    if (contributor == null || contributor.trim().isEmpty())
      this.add(DC.contributor, "sugarcubeIT");
    String format = this.value(DC.format, null);
    if (format == null || format.trim().isEmpty())
      this.add(DC.format, "ebook");
    String type = this.value(DC.type, null);
    if (type == null || type.trim().isEmpty())
      this.add(DC.type, "text");
    String rights = this.value(DC.rights, null);
    if (rights == null || rights.trim().isEmpty())
      this.add(DC.rights, "All Rights Reserved");
  }

  @Override
  public OCDMetadata clear()
  {
    this.elements.clear();
    return this;
  }

  public String dc(String key, String def)
  {
    return dc(DC.get(key), def);
  }

  public String dc(DC dc, String def)
  {
    DCElement el = dc == null ? null : element(dc);
    return el == null ? def : el.cdata();
  }

  public DCElement element(DC tag, String... keyValues)
  {
    if (tag != null)
      for (DCElement element : elements)
        if (tag.nsName.equals(element.tag()) && element.props().hasPairs(keyValues))
          return element;
    return null;
  }

  public boolean hasElement(DC tag, String... keyValues)
  {
    return element(tag, keyValues) != null;
  }

  public String[] values(DC tag, String... def)
  {
    List3<String> list = new List3<String>();
    for (DCElement element : elements)
      if (element.is(tag.nsName))
        list.add(element.cdata());
    return list.isEmpty() ? def : list.toArray(new String[0]);
  }

  public String value(DC tag, String def)
  {
    Stringer sc = new Stringer();
    for (DCElement element : elements)
      if (element.is(tag.nsName))
      {
        String cdata = element.cdata();
        if (cdata != null)
          sc.append(cdata).append(DCElement.SEPARATOR);
      }
    sc.trimEnd(DCElement.SEPARATOR);
    return sc.isEmpty() ? def : sc.toString();
  }

  public String creator(Role role, String def)
  {
    return dcCreator(role.toString(), def);
  }

  public String dcCreator(String role, String def)
  {
    Stringer sc = new Stringer();
    for (DCElement element : elements)
      if (element.is(DC.creator.nsName) && element.props().has(OPF.Role.KEY, role))
      {
        String cdata = element.cdata();
        if (cdata != null)
          sc.append(cdata).append(DCElement.SEPARATOR);
      }
    sc.trimEnd(DCElement.SEPARATOR);
    return sc.isEmpty() ? def : sc.toString();
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    xml.write("xmlns:dc", XMLNS_DC);
    xml.write("xmlns:opf", XMLNS_OPF);
    return new List3<DCElement>(this.elements);
  }

  @Override
  public void readAttributes(DomNode e)
  {
  }

  @Override
  public XmlINode newChild(DomNode child)
  {
    // Log.debug(this, ".newChild - "+child+": "+DCTag.contains(child.tag()));
    if (DC.contains(child.tag()))
      return new DCElement(child.tag(), this);
    return null;
  }

  @Override
  public void endChild(XmlINode child)
  {
    if (child == null)
      return;
    // Log.debug(this, ".endChild - "+child);
    this.add((DCElement) child);
  }

  public void set(DC tag, String... cdata)
  {
    this.set(tag.nsName(), cdata);
  }

  public void set(String tag, String... cdata)
  {
    Iterator<DCElement> it = this.iterator();
    while (it.hasNext())
      if (it.next().is(tag))
        it.remove();
    this.add(tag, cdata);
  }

  public void replace(DC tag, String... cdata)
  {
    this.replace(tag.nsName(), cdata);
  }

  public void replace(String tag, String... cdata)
  {
    this.remove(tag);
    this.add(tag, cdata);
  }

  public void remove(DC tag)
  {
    this.remove(tag.nsName());
  }

  public void remove(String tag)
  {
    Iterator<DCElement> it = this.iterator();
    while (it.hasNext())
      if (it.next().is(tag))
        it.remove();
  }

  public void add(DC tag, String... cdata)
  {
    // Log.debug(this, ".add - " + tag + ": " + cdata[0]);
    this.add(tag.nsName(), cdata);
  }

  public void add(String tag, String... cdata)
  {
    this.add(new DCElement(tag, this, cdata));
  }

  public void add(DCElement element)
  {
    // Log.debug(this, ".add - " + element.tag + ": " + element.cdata());
    element.setParent(this);
    this.elements.add(element);
  }

  public void remove(DCElement element)
  {
    this.elements.remove(element);
    element.setParent(null);
  }

  @Override
  public List<? extends DCElement> children()
  {
    // used by treezable to visit tree
    return this.elements;
  }

  @Override
  public Iterator<DCElement> iterator()
  {
    return this.elements.iterator();
  }

  public void write(StringBuilder sb)
  {
    Xml xml = new Xml(sb);
    xml.write(this);
    return;
  }

  @Override
  public String sticker()
  {
    return tag();
  }

  @Override
  public void paint(Graphics3 g, OCD.ViewProps props)
  {
  }

  @Override
  public OCDMetadata copy()
  {
    OCDMetadata meta = new OCDMetadata(parent());
    for (DCElement element : this)
      meta.add(element.copy());
    return meta;
  }

  @Override
  public String toString()
  {
    return Xml.toString(this);
  }
}
