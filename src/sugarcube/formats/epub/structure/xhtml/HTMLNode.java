package sugarcube.formats.epub.structure.xhtml;

import sugarcube.common.data.collections.*;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.common.data.xml.css.CSS;
import sugarcube.formats.epub.structure.js.JS;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;
import java.util.Iterator;

public class HTMLNode extends OCDNode implements Iterable<OCDNode>
{
  protected List3<OCDNode> nodes = new List3<>();

  public HTMLNode(String tag, OCDNode parent)
  {
    super(tag, parent);
  }

  public HTMLNode(String tag, String... props)
  {
    super(tag, null);
    this.addAttributes(props);
  }

  public HTMLNode htmlParent()
  {
    return parent != null && parent instanceof HTMLNode ? (HTMLNode) parent : null;
  }

  public HTMLNode escape(boolean attributes, boolean cdata)
  {
    this.props.escape(attributes, cdata);
    return this;
  }

  public HTMLNode get(String id)
  {
    if (Str.Equals(id, id()))
      return this;
    for (OCDNode node : this)
      if (node instanceof HTMLNode)
      {
        HTMLNode htmlNode = ((HTMLNode) node).get(id);
        if (htmlNode != null)
          return htmlNode;
      }
    return null;
  }

  public HTMLNode idClass(String id, String classname)
  {
    return id(id).classname(classname);
  }

  public HTMLNode id(String id)
  {
    return setID(id);
  }

  @Override
  public HTMLNode setID(String id)
  {
    super.setID(id);
    this.props.put("id", id);
    return this;
  }

  @Override
  public Collection<? extends OCDNode> children()
  {
    return nodes;
  }

  public List3<HTMLNode> htmlChildren()
  {
    List3<HTMLNode> html = new List3<>();
    for (OCDNode node : nodes)
      if (node instanceof HTMLNode)
        html.add((HTMLNode) node);
    return html;
  }

  // since meta is often used in Xml descriptions
  public HTMLNode addMeta(String name, String content)
  {
    return this.addChild("meta", "name", name, "content", content);
  }

  public HTMLAnchor addAnchor(String href, String... props)
  {
    HTMLAnchor anchor = new HTMLAnchor(href, props);
    this.addChild(anchor);
    return anchor;
  }

  public HTMLLinkBox addLinkBox(String href, Rectangle3 box)
  {
    HTMLLinkBox link = new HTMLLinkBox(href, box);
    this.addChild(link);
    return link;
  }

  public HTMLImage addImage(String src, String... props)
  {
    HTMLImage image = new HTMLImage(src, props);
    this.addChild(image);
    return image;
  }

  public HTMLDiv addDiv(String... props)
  {
    HTMLDiv div = new HTMLDiv(props);
    this.addChild(div);
    return div;
  }

  public HTMLSpan addSpan(String... props)
  {
    HTMLSpan span = new HTMLSpan(props);
    this.addChild(span);
    return span;
  }

  public HTMLBox addBox(double x, double y)
  {
    HTMLBox box = new HTMLBox(x, y);
    this.addChild(box);
    return box;
  }

  public HTMLBox addBox(String id, double x, double y)
  {
    HTMLBox box = new HTMLBox(id, x, y);
    this.addChild(box);
    return box;
  }

  public HTMLBox addBox(String id, double x, double y, double width, double height)
  {
    return addBox(id, new Rectangle3(x, y, width, height));
  }

  public HTMLBox addBox(String id, double x, double y, double width, double height, double scale)
  {
    return addBox(id, new Rectangle3(x * scale, y * scale, width * scale, height * scale));
  }

  public HTMLBox addBox(String id, Rectangle3 bounds)
  {
    HTMLBox box = new HTMLBox(id, bounds);
    this.addChild(box);
    return box;
  }

  public HTMLScript addScript()
  {
    HTMLScript script = new HTMLScript();
    this.addChild(script);
    return script;
  }

  public HTMLScript addScript(JS js)
  {
    HTMLScript script = new HTMLScript(js);
    this.addChild(script);
    return script;
  }

  public HTMLNode addNode(String tag, String... props)
  {
    HTMLNode node = new HTMLNode(tag, props);
    this.addChild(node);
    return node;
  }

  public HTMLNode add(String tag, String... props)
  {
    return this.addChild(new HTMLNode(tag, props));
  }

  public HTMLNode addChild(String tag, String... props)
  {
    return this.addChild(new HTMLNode(tag, props));
  }

  public HTMLNode add(OCDNode node)
  {
    return this.addChild(node);
  }

  public HTMLNode addChild(OCDNode node)
  {
    // Log.debug(this,".addChild - this="+this.tag+", child="+node.tag);
    node.setParent(this);
    this.nodes.add(node);
    return this;
  }

  public HTMLNode addChild(OCDNode node, OCDNode anchor)
  {
    node.setParent(this);
    this.nodes.addAfter(node, anchor);
    return this;
  }

  public HTMLNode removeChild(OCDNode node)
  {
    this.nodes.remove(node);
    return this;
  }

  public HTMLNode addChildren(Collection<? extends OCDNode> children)
  {
    for (OCDNode child : children)
      this.addChild(child);
    return this;
  }

  public HTMLNode addChildren(OCDNode... children)
  {
    for (OCDNode child : children)
      this.addChild(child);
    return this;
  }

  public HTMLNode styleSize(int width, int height)
  {
    return style("w:" + width + "px; h:" + height + "px;");
  }

  public HTMLNode style(String style)
  {
    if (Str.IsVoid(style))
      return this;
    return this.addAttribute("style", CSS.Style(props.get("style", ""), style));
  }

  public HTMLNode classnameIf(boolean cond, String... classes)
  {
    if (cond)
      classname(classes);
    return this;
  }

  public HTMLNode classname(String... classes)
  {
    StringSet set = new StringSet(Tokens.Split(props.get("class", "")).strings()).addAll3(classes);
    Stringer sc = new Stringer();
    for (String cls : set)
      sc.span(cls).sp();
    return this.addAttribute("class", sc.trimEnd(" ").toString());
  }

  public HTMLNode pointer()
  {
    this.classname("sc-pointer");
    return this;
  }

  public String style()
  {
    return attribute("style", "").trim();
  }

  public String classes()
  {
    return attribute("class", "").trim();
  }

  public String attribute(String key, String def)
  {
    return this.props.get(key, def);
  }

  public HTMLNode onload(String js)
  {
    return addAttribute("onload", js);
  }

  public HTMLNode onclick(String js)
  {
    return addAttribute("onclick", js);
  }

  public HTMLNode addAttribute(String key, String value)
  {
    return this.addAttributes(key, value);
  }

  public HTMLNode addAttributes(String... keyValues)
  {
    this.props.putAll(keyValues);
    return this;
  }

  public HTMLNode props(String... keyValues)
  {
    this.props.putAll(keyValues);
    return this;
  }

  public HTMLNode addCData(String cdata, boolean escaped)
  {
    this.props.escapeCData(escaped);
    this.props.putEmptyValue(cdata);
    return this;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    props.writeAttributes(xml);
    return this.children();
  }

  @Override
  public void readAttributes(DomNode dom)
  {
    props.readAttributes(dom, true);
  }

  @Override
  public XmlINode newChild(DomNode child)
  {
    return new HTMLNode(child.tag(), this);
  }

  @Override
  public void endChild(XmlINode child)
  {
    if (child != null)
      this.addChild((OCDNode) child);
  }

  public final HTMLNode setCData(String cdata, boolean doEscape)
  {
    this.setCData(cdata);
    this.props.escapeCData(doEscape);
    return this;
  }

  public void appendCData(String cdata)
  {
    this.props.setEmptyValue(this.props.emptyValue() + "" + cdata);
  }

  public void setCData(String cdata)
  {
    this.props.setEmptyValue(cdata);
  }

  public String cdata()
  {
    return this.props.emptyValue();
  }

  public boolean hasCData()
  {
    return this.props.containsEmptyKey();
  }

  public boolean hasCDataChars()
  {
    String cdata = cdata();
    return cdata != null && !cdata.trim().isEmpty();
  }

  public static String R(double d)
  {
    return "" + (Math.round(d * 100) / 100.0);
  }

  public static String S(double d)
  {
    return "" + Math.round(d);
  }

  public static int I(double d)
  {
    return (int) Math.round(d);
  }

  @Override
  public Iterator<OCDNode> iterator()
  {
    return nodes.iterator();
  }

  @Override
  public String toString()
  {
    return xmlString();
  }

  @Override
  public String xmlString()
  {
    return Xml.toString(this, true);
  }
}
