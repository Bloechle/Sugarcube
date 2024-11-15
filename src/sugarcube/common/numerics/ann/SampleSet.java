package sugarcube.common.numerics.ann;

import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.Occurrences;
import sugarcube.common.data.collections.Pair;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.numerics.Matrix;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.common.data.xml.Xmlizer;

import java.util.*;

public class SampleSet implements Iterable<Sample>, XmlINode, Xmlizer
{
  private String name = "noname";
  private Norm norm = Norm.IDENTITY;
  private StringMap<Sample> samples = new StringMap<>();
  private StringMap<Integer> classes = new StringMap<>();

  protected SampleSet()
  {
  }

  public SampleSet(String name, String... classes)
  {
    this.name = name;
    this.setClasses(classes);
  }
  
  public int classIndex(String className)
  {
    return this.classes.get(className, -1);
  }

  public void setClasses(String... classes)
  {
    for (int i = 0; i < classes.length; i++)
      this.classes.put(classes[i], i);
  }

  public Norm norm()
  {
    return norm;
  }

  public String name()
  {
    return name;
  }

  public boolean contains(String name)
  {
    return this.samples.containsKey(name);
  }

  public Sample sample(String name)
  {
    return this.samples.get(name);
  }

  public Sample get(String name)
  {
    return this.samples.get(name);
  }

  public Occurrences<String> occurrence()
  {
    Occurrences<String> occurrence = new Occurrences<>();
    for (Sample sample : this)
      occurrence.inc(sample.label());
    return occurrence;
  }

  public float[] mean(String label)
  {
    float[] mean = new float[nbOfFeatures()];
    int counter = 0;
    for (Sample sample : this)
      if (sample.isLabel(label))
      {
        Zen.Array.AddTo(mean, sample.values());
        counter++;
      }
    return counter == 0 ? mean : Zen.Array.DivideSelf(mean, counter);
  }

  public Sample first()
  {
    return this.iterator().hasNext() ? this.iterator().next() : null;
  }

  public int nbOfFeatures()
  {
    return samples.size() == 0 ? 0 : first().dimension();
  }

  public void redimension(int newSize)
  {
    this.redimension(newSize, 0);
    ;
  }

  public void redimension(int newSize, int value)
  {
    int dimension = newSize;
    for (Sample sample : this)
      if (sample.dimension() < dimension)
      {
        float[] values = sample.values();
        float[] newValues = new float[dimension];
        System.arraycopy(values, 0, newValues, 0, values.length);
        for (int i = values.length; i < newValues.length; i++)
          newValues[i] = value;
        sample.setValues(newValues);
      }
  }

  public SampleSet shuffle()
  {
    SampleSet shuffled = new SampleSet(name());
    List<Sample> list = list();
    Collections.shuffle(list);
    shuffled.addAll(list);
    return shuffled;
  }

  public Pair<SampleSet> split(String label)
  {
    SampleSet labelSet = new SampleSet(label);
    SampleSet otherSet = new SampleSet("¬" + label);
    labelSet.norm = this.norm;
    otherSet.norm = this.norm;
    for (Sample sample : this)
      if (!label.equals(Label.UNDEF))
        if (sample.isLabel(label))
          labelSet.add(sample);
        else
          otherSet.add(sample);
    return new Pair<>(labelSet, otherSet);
  }

  public Sample[] samples()
  {
    return samples.values().toArray(new Sample[0]);
  }

  public List<Sample> list()
  {
    return new LinkedList<>(samples.values());
  }

  @Override
  public Iterator<Sample> iterator()
  {
    return this.samples.values().iterator();
  }

  public Matrix matrix()
  {
    double[][] matrix = new double[nbOfFeatures()][size()];
    int col = 0;
    for (Sample sample : this)
    {
      float[] values = sample.values();
      for (int row = 0; row < values.length; row++)
        matrix[row][col] = values[row];
      col++;
    }
    return new Matrix(matrix);
  }

  public SampleSet clear()
  {
    this.samples.clear();
    this.norm = Norm.IDENTITY;
    return this;
  }

  // public SampleSet remove(Label label)
  // {
  // return this.remove(label.name());
  // }
  //
  // public SampleSet remove(String label)
  // {
  // Iterator<Map.Entry<String, Sample>> iterator =
  // this.samples.entrySet().iterator();
  // while (iterator.hasNext())
  // {
  // Map.Entry<String, Sample> entry = iterator.next();
  // if (entry.getValue().label().equals(label))
  // iterator.remove();
  // }
  // return this;
  // }

  public Sample remove(String sampleID)
  {
    return samples.remove(sampleID);
  }

  public Sample remove(Sample sample)
  {
    return remove(sample.id());
  }

  public void add(Sample sample)
  {
    if (sample != null && !sample.label().equals(Label.UNDEF))
      samples.put(sample.id(), sample);
  }
  
  public Sample addSample(String id, String label, float... values)
  {
    Sample sample = new Sample(id,label,values);
    this.add(sample);
    return sample;
  }

  public void addAll(Iterable<Sample> samples)
  {
    for (Sample sample : samples)
      this.add(sample);
  }

  public int size()
  {
    return samples.size();
  }

  public boolean isEmpty()
  {
    return samples.isEmpty();
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("SampleSet[").append(size()).append("]");
    for (Sample sample : samples.values())
      sb.append("\n").append(sample);
    return sb.toString();
  }

  @Override
  public Object clone()
  {
    return copy();
  }

  public SampleSet copy()
  {
    return copy(name);
  }

  public SampleSet copy(String name)
  {
    SampleSet set = new SampleSet(name, classes.values().toArray(new String[0]));
    set.norm = this.norm;
    set.addAll(this);
    return set;
  }

  public SampleSet normalize(Norm norm)
  {
    SampleSet set = new SampleSet(name, classes.values().toArray(new String[0]));
    set.norm = norm;
    for (Sample sample : this)
      set.add(sample.normalize(norm));
    return set;
  }

  public SampleSet gaussianNormalize()
  {
    return normalize(new Norm.Gaussian(this));
  }

  public SampleSet extremaNormalize(double factor, double shift)
  {
    return normalize(new Norm.Extrema(this, factor, shift));
  }

  @Override
  public Collection<? extends XmlINode> children()
  {
    return samples.values();
  }

  @Override
  public XmlINode parent()
  {
    return null;
  }

  @Override
  public void setParent(XmlINode parent)
  {
  }

  @Override
  public String tag()
  {
    return "samples";
  }

  @Override
  public Xmlizer xmlizer()
  {
    return this;
  }

  @Override
  public Collection<? extends XmlINode> writeAttributes(Xml xml)
  {
    xml.write("size", this.size());
    xml.write("name", this.name);
    xml.write("norm", this.norm.name());
    xml.write("classes", this.classes.values().toArray(new String[0]));
    // writeAttributes.add("features", this.features.toArray(new String[0]));
    // writeAttributes.add("labels", this.labels.toArray(new String[0]));
    return this.children();
  }

  @Override
  public void readAttributes(DomNode dom)
  {
    this.name = dom.value("name");
    this.classes.clear();
    this.setClasses(dom.values("classes"));
    // this.features.addAll(e.values("features"));
    // this.labels.addAll(e.values("labels"));
  }

  @Override
  public XmlINode newChild(DomNode child)
  {
    return new Sample();
  }

  @Override
  public void endChild(XmlINode child)
  {
    this.add((Sample) child);
  }

  @Override
  public String sticker()
  {
    return "SampleSet[" + name + "]";
  }
}
