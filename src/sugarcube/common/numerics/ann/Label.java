package sugarcube.common.numerics.ann;

import sugarcube.common.data.Zen;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.common.data.xml.Xmlizer;

import java.util.Collection;
import java.util.LinkedList;

public class Label implements XmlINode, Xmlizer
{
  public static final String UNDEF = "undef";
  public static final Label UNDEF_LABEL = new Label(UNDEF, 1f); //because groundtruth == -1  
  private transient XmlINode parent;
  private String name = UNDEF;
  private float confidence;//confidence is between [0..1], if confidence<0, then this is a certitude

  public Label()
  {
    this(UNDEF, 1f);
  }

  public Label(String name)
  {
    this(name, -1f);
  }

  public Label(String name, float confidence)
  {
    this.name = name == null || name.equals(UNDEF) ? UNDEF : name;
    this.confidence = this.name.equals(UNDEF) ? 1f : confidence;
  }

  public String name()
  {
    return this.name;
  }

  public boolean hasConfidence()
  {
    return confidence >= 0f;
  }

  public float confidence()
  {
    return confidence;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || this.getClass() != o.getClass())
      return false;
    return this.name == null || ((Label) o).name == null ? false : this.name.equals(((Label) o).name);
  }

  @Override
  public int hashCode()
  {
    return name.hashCode();
  }

  public boolean isUndefined()
  {
    return name == null || name.equals(UNDEF);
  }

  public boolean isGroundTruth()
  {
    return !name.equals(UNDEF) && confidence < 0.0;
  }

  public Label asGroundTruth()
  {
    return new Label(name, -1f);
  }

  public boolean isName(String label)
  {
    return this.name.equals(label);
  }

  @Override
  public Object clone()
  {
    return copy();
  }

  public Label copy()
  {
    return new Label(name, confidence);
  }

  @Override
  public String toString()
  {
    return name;
  }

  public String stringReport()
  {
    return "Label[" + name + "]";
  }

  @Override
  public Collection<? extends XmlINode> children()
  {
    return new LinkedList<XmlINode>();
  }

  @Override
  public XmlINode parent()
  {
    return parent;
  }

  public void setParent(XmlINode parent)
  {
    this.parent = parent;
  }
  
  @Override
  public Xmlizer xmlizer()
  {
    return this;
  }  

  @Override
  public Collection<? extends XmlINode> writeAttributes(Xml x)
  {
    x.write("name", name);
    if (confidence >= 0.0)
      x.write("confidence", Zen.toString(confidence,2));
    return this.children();
  }

  @Override
  public void readAttributes(DomNode e)
  {
    this.name = e.value("name", UNDEF);
    this.confidence = (float) e.real("confidence", -1f);    
  }

  @Override
  public XmlINode newChild(DomNode child)
  {
    return new Label();
  }

  @Override
  public void endChild(XmlINode child)
  {
  }

  @Override
  public String tag()
  {
    return "label";
  }

  @Override
  public String sticker()
  {
    return "Label[" + name + "]";
  }
}
