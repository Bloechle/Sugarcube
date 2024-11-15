package sugarcube.common.numerics.ann.mlp;

import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.ui.gui.Font3;

public class Link extends Neural
{
  protected static Font3 FONT_PLAIN = Font3.GUI_FONT;
  protected static Font3 FONT_BOLD = FONT_PLAIN.bold();
  protected static double THRESHOLD = 0.00001;
  protected Neuron from = null;
  protected Neuron to = null;
  private double weight = 0.0;
  private double delta = 0.0;

  protected Link()
  {
    super(false);
  }

  public Link(Neuron from, Neuron to)
  {
    this(from.name + " - " + to.name, from, to);
  }

  public Link(String name, Neuron from, Neuron to)
  {
    super(false);
    this.name = name;
    this.from = from;
    this.to = to;
    this.from.addOutputLink(this);
    this.to.addInputLink(this);
  }

  public void relaxDecay(Subnet subnet)
  {
    if (!isEnabled)
      return;

    double relax = subnet.relax();
    //add random noise (avoid local minima)    
    if (relax > 0)
      this.weight += relax * (2.0 * Math.random() - 1.0) / Math.sqrt(this.to.inputSize());
    //weight decay (avoid unuseful big weights)
    double decay = (subnet.network.maxDecay - subnet.network.minDecay) * relax + subnet.network.minDecay;
    weight = weight > decay ? weight - decay : weight < -decay ? weight + decay : 0.0;
    weight -= decay * weight;
  }

  public double delta()
  {
    return delta;
  }

  public void update(double delta)
  {
    if (isEnabled)
    {
      this.delta = delta;
      this.weight += delta;
      if (Math.abs(this.weight) < THRESHOLD)
        this.weight = 0.0;
    }
  }

  public void reset()
  {
    if (isEnabled)
      this.weight = (2.0 * Math.random() - 1.0) / Math.sqrt(this.to.inputSize());
  }

  public void setWeight(double weight)
  {
    if (isEnabled)
      this.weight = weight;
  }

  public double weight()
  {
    return isEnabled ? this.weight : 0.0;
  }

  public static Color3 color(double w)
  {
    float v = 0.6f;
    float s = (float) (w > 1 ? 1 : w < -1 ? -1 : w);
    return s >= 0 ? Color3.hsl(0.35f, s, v) : Color3.hsl(0, -s, v);
  }

  @Override
  public void paint(Graphics3 g)
  {
//    if (this.from.isBias())
//      return;
    if (from.x < 0 || to.x < 0)
      return;


    double max = -1;
    for (Link link : this.to.inputs)
      if (Math.abs(link.weight) > max)
        max = Math.abs(link.weight);
    if (max < 0)
      max = 1;

    double strength = Math.abs(this.weight) / max;

    g.draw(new Line3(from.x, from.y, to.x, to.y), color(weight).alpha(strength * 0.9 + 0.1), 1.5);
  }
}
