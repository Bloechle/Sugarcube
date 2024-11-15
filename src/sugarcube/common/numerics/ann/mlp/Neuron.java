package sugarcube.common.numerics.ann.mlp;

import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.Map3;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.graphics.geom.Circle3;
import sugarcube.common.graphics.geom.Square3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.numerics.Evaluable;

import java.awt.*;
import java.awt.geom.Point2D;

public class Neuron extends Neural implements Comparable<Neuron>
{
  public static enum Type
  {
    INPUT, HIDDEN, OUTPUT, BIAS, UNDEF;

    public boolean isInput()
    {
      return this.equals(INPUT);
    }

    public boolean isHidden()
    {
      return this.equals(HIDDEN);
    }

    public boolean isOutput()
    {
      return this.equals(OUTPUT);
    }

    public boolean isBias()
    {
      return this.equals(BIAS);
    }

    public boolean isUndef()
    {
      return this.equals(UNDEF);
    }
  }
  protected Evaluable function = Evaluable.SIGMOID;
  protected Evaluable derivative = Evaluable.DSIGMOID;
  private double activation;//activation=f(net)
  private double slope;//slope=f'(net)
  private double derror;//derror=slope*error
  private double serror;//serror+=error*error
  private double error;//serror/counter
  private double counter;//counts the number of samples during an epoch
  private double theta;//current theta, used to compute eta
  protected Type type = Type.UNDEF;
  protected Map3<Neuron,Link> inputs = new Map3<Neuron,Link>();
  protected Map3<Neuron,Link> outputs = new Map3<Neuron,Link>();
  //positions on a graphical context, if used in a graphical environment
  protected transient double x = -1.0;
  protected transient double y = -1.0;
  protected transient double radius = 6.0;
  protected transient double relevance = 1.0;
  protected transient double mean = 0.0; //mean activation

  protected Neuron()
  {
    super(true);
  }

  public Neuron(Type type, String name)
  {
    super(true);
    this.type = type;
    this.name = name;
    this.activation = this.isBias() ? 1.0 : 0.0;
  } 
  
  public void addInputLink(Link link)
  {
    this.inputs.put(link.from,link);
  }

  public void addOutputLink(Link link)
  {
    this.outputs.put(link.to, link);
  }

  public Type type()
  {
    return this.type;
  }

  public void reset()
  {
    this.activation = this.isBias() ? 1.0 : 0.0;
    this.slope = 0.0;
    this.derror = 0.0;
    this.counter = 0;
    this.serror = 0.0;
    this.error = 0.0;
    this.theta = 0.0;
    this.relevance = 1.0;
    this.mean = 0.0;
  }

  public double theta()
  {
    return this.theta;
  }

  public double relevance()
  {
    return this.relevance;
  }

  @Override
  public void cycleElapsed()
  {
    if (isEnabled)
    {
      this.theta = this.serror / this.counter;
      this.error = Math.sqrt(serror) / (counter == 0 ? 1 : counter);
      this.serror = 0;
      this.counter = 0;
    }
  }

  public double error()
  {
    return error;
  }

  public double activation()
  {
    return isEnabled ? this.activation : 0.0;
  }

  public double derror()
  {
    return isEnabled ? this.derror : 0.0;
  }

  public void derror(double error)
  {
    if (isEnabled)
    {
      this.derror = this.slope * error;
      this.counter++;
      this.serror += error * error;
    }
  }

  public void setActivation(double activation)
  {
    this.activation = activation;
  }

  public void evaluate(double net)
  {
    if (isEnabled)
      this.activation = this.function.eval(net);
  }

  public void derivate(double net)
  {
    if (isEnabled)
      this.slope = this.derivative.eval(net);
  }

  public void setCoords(double x, double y)
  {
    this.x = x;
    this.y = y;
  }

  public boolean select(Point2D p)
  {
    if (y >= p.getY() - radius && y <= p.getY() + radius && x >= p.getX() - 10 * radius && x <= p.getX() + 10 * radius)
      this.select();
    else
      this.unselect();
    return this.isSelected();
  }

  public boolean isInput()
  {
    return type.isInput();
  }

  public boolean isOutput()
  {
    return type.isOutput();
  }

  public boolean isHidden()
  {
    return type.isHidden();
  }

  public boolean isBias()
  {
    return type.isBias();
  }

  public boolean isUndef()
  {
    return type.isUndef();
  }

  public Set3<Link> inputs()
  {
    Set3<Link> set = new Set3<Link>();
    for (Link link : this.inputs)
      if (link.isEnabled)
        set.add(link);
    return set;
  }

  public Set3<Link> outputs()
  {
    Set3<Link> set = new Set3<Link>();
    for (Link link : this.outputs)
      if (link.isEnabled)
        set.add(link);
    return set;
  }

  public Link firstOutput()
  {
    for (Link link : outputs)
      if (link.isEnabled)
        return link;
    return null;
  }

  public int inputSize()
  {
    int size = 0;
    for (Link link : this.inputs)
      if (link.isEnabled)
        size++;
    return size == 0 ? 1 : size;
  }

  public int outputSize()
  {
    int size = 0;
    for (Link link : this.outputs)
      if (link.isEnabled)
        size++;
    return size == 0 ? 1 : size;
  }

  public Shape shape()
  {
    if (this.isEnabled())
      return isBias() ? new Square3(x - radius, y - radius, 2 * radius) : new Circle3(x, y, radius);
    else
      return new Circle3(x, y, radius+10);
  }

  @Override
  public void paint(Graphics3 g)
  {
//    if (isBias())
//      return;

    Color3 color = Link.color(activation);//we wanna paint the mean neuron activation
    g.fill(shape(), color);

    if (isInput())
      g.drawTo(name + " " + stract(), x - 2 * radius, y + radius / 2, Link.FONT_PLAIN, Color3.BLACK);
    else if (isOutput())
      if (isSelected())
        g.draw(name + " " + stract(), x + 2 * radius, y + radius / 2, Link.FONT_BOLD, Color3.BLACK);
      else
        g.draw(name + " " + stract(), x + 2 * radius, y + radius / 2, Link.FONT_PLAIN, Color3.BLACK);


    if (isSelected())
    {
      g.draw(shape(), Color3.BLACK, 1);
    }
  }

  private String stract()
  {
    return this.activation > 0 ? "+" + Zen.toString(activation, 2) : this.activation < 0 ? Zen.toString(activation, 2) : " 0.00";
  }

  private String strerror()
  {
    return Zen.toString(error, 2);
  }

  private String strrelevance()
  {
    return Zen.toString(relevance, 2);
  }

  @Override
  public int compareTo(Neuron n)
  {
    return relevance > n.relevance ? -1 : relevance < n.relevance ? 1 : 0;
  }
}
