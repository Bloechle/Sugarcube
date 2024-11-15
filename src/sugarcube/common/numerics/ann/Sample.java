package sugarcube.common.numerics.ann;

import sugarcube.common.data.Zen;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.common.data.xml.Xmlizer;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public class Sample implements XmlINode, Xmlizer
{
  private XmlINode parent;
  private long timestamp;
  private String id;
  private String label;
  private float[] values; 
  
  protected Sample()
  {
  }

  public Sample(String id, float... values)
  {
    this(id, Label.UNDEF, values);
  }

  public Sample(String id, String label, float... values)
  {
    this.id = id;
    this.label = label;
    this.values = values;
    this.timestamp = System.currentTimeMillis();
  }

  public Sample addFeature(float... values)
  {
    float[] tmp = this.values;
    this.values = new float[tmp.length + values.length];
    System.arraycopy(tmp, 0, this.values, 0, tmp.length);
    System.arraycopy(values, 0, this.values, tmp.length, values.length);
    return this;
  }

  public boolean isLabel(String label)
  {
    return this.label.equals(label);
  }

  public boolean isLabel(Label label)
  {
    return isLabel(label.name());
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || this.getClass() != o.getClass())
      return false;
    return Arrays.equals(this.values(), ((Sample) o).values());
  }

  @Override
  public int hashCode()
  {
    return Arrays.hashCode(values());
  }

  @Override
  public Object clone()
  {
    return copy();
  }

  public Sample copy()
  {
    return new Sample(id, label, Zen.Array.copy(values));
  }
  
  public long timestamp()
  {
    return this.timestamp;
  }

  public String id()
  {
    return id;
  }
  

  public String label()
  {
    return label;
  }
  
  public Sample label(String label)
  {
    this.label = label;
    return this;
  }

  public Sample setLabel(String label)
  {
    this.label = label;
    return this;
  }

  public int dimension()
  {
    return values.length;
  }

  public void setValues(float... values)
  {
    this.values = values;
  }

  public float[] values()
  {
    return values;
  }

  public double[] doubles()
  {
    return Zen.Array.toDoubles(values);
  }

  public Sample normalize(Norm norm)
  {
    return norm == null ? this : new Sample(id, label, norm.normalize(this.values));
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("Sample[").append(id).append("]");
    sb.append("\n").append(label);
    sb.append("\n").append(Zen.Array.String(values));
    sb.append("\n");
    return sb.toString();
  }

  @Override
  public Collection<? extends XmlINode> children()
  {
    return new LinkedList<XmlINode>();
  }

  @Override
  public XmlINode parent()
  {
    return this.parent;
  }
  
  @Override
  public void setParent(XmlINode parent)
  {
    this.parent = parent;
  }

  @Override
  public String tag()
  {
    return "smp";
  }
  
  @Override
  public Xmlizer xmlizer()
  {
    return this;
  }

  @Override
  public Collection<? extends XmlINode> writeAttributes(Xml xml)
  {
    xml.write("label",label);    
    xml.write("timestamp",""+timestamp);
    xml.write("id",id);
    xml.write(values);    
    return this.children();
  }

  @Override
  public void readAttributes(DomNode e)
  {
    this.label = e.value("label");    
    this.timestamp = Long.parseLong(e.value("timestamp"));
    this.id = e.value("id");
    this.values = e.reals(new float[0]);    
  }

  @Override
  public XmlINode newChild(DomNode child)
  {
    return null;
  }

  @Override
  public void endChild(XmlINode child)
  {
  }

  @Override
  public String sticker()
  {
    return label+"["+id+"]";
  }
}
