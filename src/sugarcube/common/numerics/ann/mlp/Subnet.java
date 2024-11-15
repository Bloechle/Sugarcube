package sugarcube.common.numerics.ann.mlp;

import sugarcube.common.numerics.ann.Label;
import sugarcube.common.numerics.ann.Sample;
import sugarcube.common.numerics.ann.SampleSet;
import sugarcube.common.data.collections.Pair;
import sugarcube.common.data.Base;

import java.util.Iterator;

/**
 * Learning rule is backpropagation with momentum. Default activation function is the centered odd sigmoid: tanh.
 *
 * n inputs h hiddens (may have more than one hidden layer) 1 ouptut 1 bias per layer (except output layer) o = neuron x
 * = bias (neuron having a constant 1 activation)
 *
 * o
 * o o
 * o o o
 * o x
 * x
 *
 */
public class Subnet implements NetStrings
{
  protected SugarNet network;
  protected Neuron[][] neurons;
  protected Link[][][] links;
  protected int totalIteration = 0;
  protected int iteration = 0; //current iteration
  protected int fullRecos = 0;
  protected int subnetIndex; //0 to n-1
  protected String name;

  public Subnet(SugarNet network, int subnetIndex, String name)
  {
    this.network = network;
    this.subnetIndex = subnetIndex;
    this.name = name;
    this.reset();
  }

  public String name()
  {
    return name;
  }

  public boolean isName(String name)
  {
    return this.name.equals(name);
  }

  public int fullRecos()
  {
    return this.fullRecos;
  }

  public final Subnet reset()
  {
    this.iteration = 0;
    this.fullRecos = 0;
    this.resetLayers();
    this.resetWeights();
    this.resetNeurons();
    return this;
  }

  public void liveUpdate()
  {
    this.iteration = 0;
    this.fullRecos = 0;
    this.neurons[0] = network.inputs;
    Link[][][] lnks = new Link[links.length][][];//-1 because n neuron layers and n-1 link weight layers
    for (int i = 0; i < lnks.length; i++)
    {
      lnks[i] = new Link[neurons[i + 1].length - (i == lnks.length - 1 ? 0 : 1)][]; //output layer has no bias neuron...
      for (int j = 0; j < lnks[i].length; j++)
        lnks[i][j] = new Link[neurons[i].length];
    }
    for (int i = 0; i < lnks.length; i++)
      for (int j = 0; j < lnks[i].length; j++)
        for (int k = 0; k < lnks[i][j].length; k++)
        {
          lnks[i][j][k] = neurons[i + 1][j].inputs.get(neurons[i][k]);
          if (lnks[i][j][k] == null)
            lnks[i][j][k] = new Link(neurons[i][k], neurons[i + 1][j]);
        }

    int i = lnks.length - 1;
    for (int j = 0; j < lnks[i].length; j++)
      for (int k = 0; k < lnks[i][j].length; k++)
        if (k != lnks[i][j].length - 1 && (j == 0 && k >= lnks[i][j].length / 2 || j == 1 && k < lnks[i][j].length / 2))
          lnks[i][j][k].setEnabled(false);
    this.links = lnks;
  }

  public void resetLayers()
  {
    int[] hiddens = network.hiddenSizes();
    this.neurons = new Neuron[hiddens.length + 2][];//+2 for input and output layers
    this.neurons[0] = network.inputs;
    for (int i = 1; i < neurons.length - 1; i++)
      this.neurons[i] = new Neuron[hiddens[i - 1] + 1];//+1 for bias in hidden layers

    this.neurons[neurons.length - 1] = new Neuron[2];//output neurons
    this.neurons[neurons.length - 1][0] = network.neuron(subnetIndex * 2, name, Neuron.Type.OUTPUT);
    this.neurons[neurons.length - 1][1] = network.neuron(subnetIndex * 2 + 1, NOT_PREFIX + name, Neuron.Type.OUTPUT);

    this.links = new Link[neurons.length - 1][][];//-1 because n neuron layers and n-1 link weight layers
    for (int i = 0; i < this.links.length; i++)
    {
      this.links[i] = new Link[neurons[i + 1].length - (i == this.links.length - 1 ? 0 : 1)][]; //output layer has no bias neuron...
      for (int j = 0; j < this.links[i].length; j++)
        this.links[i][j] = new Link[neurons[i].length];
    }

    for (int i = 1; i < neurons.length - 1; i++)
      for (int j = 0; j < neurons[i].length; j++)
        this.neurons[i][j] = new Neuron(j == neurons[i].length - 1 ? Neuron.Type.BIAS : Neuron.Type.HIDDEN, Base.x32.random8());

    for (int i = 0; i < links.length; i++)
      for (int j = 0; j < links[i].length; j++)
        for (int k = 0; k < links[i][j].length; k++)
          this.links[i][j][k] = new Link(neurons[i][k], neurons[i + 1][j]);

    int i = this.links.length - 1;
    for (int j = 0; j < links[i].length; j++)
      for (int k = 0; k < links[i][j].length; k++)
        if (k != links[i][j].length - 1 && (j == 0 && k >= links[i][j].length / 2 || j == 1 && k < links[i][j].length / 2))
          this.links[i][j][k].setEnabled(false);
  }

  public void resetWeights()
  {
    if (links != null)
      for (int i = 0; i < links.length; i++)
        for (int j = 0; j < links[i].length; j++)
          for (int k = 0; k < links[i][j].length; k++)
            if (links[i][j][k].isEnabled)
              links[i][j][k].reset();
  }

  public void resetNeurons()
  {
    if (neurons != null)
      for (int i = 0; i < neurons.length; i++)
        for (int j = 0; j < neurons[i].length; j++)
          neurons[i][j].reset();
  }

  public void cycleElapsed()
  {
    this.iteration++;
    this.totalIteration++;
    //since links do not use cycleElapsed
//    if (links != null)
//      for (int i = 0; i < links.length; i++)
//        for (int j = 0; j < links[i].length; j++)
//          for (int k = 0; k < links[i][j].length; k++)
//            links[i][j][k].cycleElapsed();
    if (neurons != null)
      for (int i = 0; i < neurons.length; i++)
        for (int j = 0; j < neurons[i].length; j++)
          neurons[i][j].cycleElapsed();
  }

  public Neuron output()
  {
    return this.neurons[this.neurons.length - 1][0];
  }

  public Neuron output_()
  {
    return this.neurons[this.neurons.length - 1][1];
  }

  public double relax()
  {
    return this.network.relax(iteration);
  }

  public double learning()
  {
    return this.network.learning(iteration);
  }

  protected double feedForward()
  {
    for (int i = 0; i < links.length; i++)
    {
      Neuron[] y = neurons[i + 1];
      Neuron[] x = neurons[i];
      Link[][] w = links[i];
      for (int j = 0; j < w.length; j++)
      {
        double net = 0.0;
        for (int k = 0; k < w[j].length; k++)
          if (w[j][k].isEnabled && x[k].isEnabled)
            net += x[k].activation() * w[j][k].weight();
        y[j].evaluate(net);
      }
    }
    return output().activation();
  }

  protected void applyWeightDecay()
  {
    //weights decay should be done only one time per epoch and before applying weight correction
    if (links != null)
      for (int i = 0; i < links.length; i++)
        for (int j = 0; j < links[i].length; j++)
          for (int k = 0; k < links[i][j].length; k++)
            links[i][j][k].relaxDecay(this);
  }

  public synchronized double backPropagation(SampleSet set)
  {
    double rate = 0.0;
    //weights decay before correction in order to avoid increase in train error
    //this.applyWeightRelaxDecay();
    int size = 0;
    for (Sample sample : set)
      if (!sample.isLabel(Label.UNDEF))
      {
        size++;
        this.network.setInputValues(sample.values());

        boolean isClassSample = sample.label().equals(network.classes[this.subnetIndex]);
        this.backPropagation(isClassSample ? 1f : -1f);

        double out = this.output().activation();
        double out_ = this.output_().activation();
        if (isClassSample ? out > 0 && out_ < 0 : out < 0 && out_ > 0)
          rate++;
      }
    this.cycleElapsed();

    rate = rate / (size > 0 ? size : 1);
    this.fullRecos = rate == 1f ? fullRecos + 1 : 0;
    return rate;
  }

  public synchronized double bofBackPropagation(SampleSet set)
  {
    double rate = 0.0;
    //weights decay before correction in order to avoid increase in train error
    this.applyWeightDecay();
    int size = 0;

    Pair<SampleSet> pair = set.split(network.classes[this.subnetIndex]);

    SampleSet classSet = pair.first().shuffle();
    SampleSet otherSet = pair.second().shuffle();

    int maxLength = Math.max(classSet.size(), otherSet.size());
    Iterator<Sample> classSetIterator = classSet.iterator();
    Iterator<Sample> otherSetIterator = otherSet.iterator();
    for (int i = 0; i < maxLength; i++)
    {
      if (classSetIterator.hasNext())
      {
        Sample sample = classSetIterator.next();
        if (!sample.isLabel(Label.UNDEF))
        {
          size++;
          this.network.setInputValues(sample.values());
          this.backPropagation(1f);
          if (this.output().activation() > 0 && this.output_().activation() < 0)
            rate++;
        }
      }
      else
        classSetIterator = classSet.iterator();

      if (otherSetIterator.hasNext())
      {
        Sample sample = otherSetIterator.next();
        if (!sample.isLabel(Label.UNDEF))
        {
          size++;
          this.network.setInputValues(sample.values());
          this.backPropagation(-1f);
          if (this.output().activation() < 0 && this.output_().activation() > 0)
            rate++;
        }

      }
      else
        otherSetIterator = otherSet.iterator();
    }
    this.cycleElapsed();

    rate = rate / (size > 0 ? size : 1);
    this.fullRecos = rate == 1f ? fullRecos + 1 : 0;
    return rate;
  }

  protected synchronized void backPropagation(float output)
  {
    //feedforward: activation & derivate computation
    for (int i = 0; i < links.length; i++)
    {
      Neuron[] y = neurons[i + 1];
      Neuron[] x = neurons[i];
      Link[][] w = links[i];

      for (int j = 0; j < w.length; j++)
      {
        double net = 0.0;
        for (int k = 0; k < w[j].length; k++)
          if (w[j][k].isEnabled && x[k].isEnabled)
            net += x[k].activation() * w[j][k].weight();
        y[j].evaluate(net);//f(net)
        y[j].derivate(net);//f'(net)
      }
    }

    //error backpropagation
    this.output().derror(output - output().activation());
    this.output_().derror(-output - output_().activation());

    for (int i = links.length - 1; i > 0; i--)
    {
      Neuron[] x = neurons[i];
      Neuron[] y = neurons[i + 1];
      Link[][] w = links[i];

      for (int k = 0; k < x.length; k++)
      {
        double e = 0.0;
        for (int j = 0; j < w.length; j++)
          if (w[j][k].isEnabled && x[k].isEnabled)
            e += w[j][k].weight() * y[j].derror();

        if (x[k].isEnabled)
          x[k].derror(e);
      }
    }

    double learning = learning();
    //weights correction computation and update
    for (int i = links.length; i > 0; i--)
    {
      Neuron[] y = neurons[i];
      Neuron[] x = neurons[i - 1];
      Link[][] w = links[i - 1];

      for (int j = 0; j < w.length; j++)
        for (int k = 0; k < w[j].length; k++)
          if (w[j][k].isEnabled)
            w[j][k].update(learning * (1.0 - network.momentum) * y[j].derror() * x[k].activation() + network.momentum * w[j][k].delta());
    }
  }

  public synchronized void backpropagateRelevance()
  {
    this.output().relevance = 1.0;
    this.output_().relevance = 1.0;
    for (int i = 1; i < neurons.length - 1; i++)
      for (int j = 0; j < neurons[i].length; j++)
        neurons[i][j].relevance = 0.0;

    for (int i = links.length - 1; i >= 0; i--)
      for (int j = 0; j < links[i].length; j++)
        for (int k = 0; k < links[i][j].length; k++)
          if (links[i][j][k].isEnabled) // && !neurons[i][k].isBias())
            neurons[i][k].relevance += Math.abs(links[i][j][k].weight()) * neurons[i + 1][j].relevance;
  }

  public void setNeuronCoords(int x, int y, int width, int height)
  {
    int dx = width / (neurons.length - 1);
    for (int i = 1; i < neurons.length - 1; i++)
    {
      int dy = height / (neurons[i].length + 1);
      for (int j = 0; j < neurons[i].length; j++)
        neurons[i][j].setCoords(x + i * dx, y + (j + 1) * dy);
    }
  }
}
