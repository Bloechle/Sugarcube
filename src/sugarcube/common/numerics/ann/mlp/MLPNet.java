package sugarcube.common.numerics.ann.mlp;

import sugarcube.common.data.Zen;
import sugarcube.common.numerics.ann.Label;
import sugarcube.common.numerics.ann.Norm;
import sugarcube.common.numerics.ann.Sample;
import sugarcube.common.numerics.ann.SampleSet;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.ui.gui.Box3;
import sugarcube.common.ui.gui.Paint3;
import sugarcube.common.ui.gui.Spinner3;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Arrays;

/**
 * This class implements a multilayer perceptron neural network.
 * Implementation follows the Duda/Hart/Stork "Pattern Classification" book hints.
 * Learning rule is backpropagation with momentum.
 * Default activation function is the centered odd sigmoid: tanh.
 * The network contains one input layer, one or more hidden layers, and one output layer.
 * Consecutive layers are fully connected between them.
 * A bias neuron whose activation is a constant equal to one is added at each layer excepted the output one.
 */
public class MLPNet
{
  protected transient Panel panel = null;
  protected String[] classNames = new String[0];
  protected String[] featureNames = new String[0];
  protected SampleSet trainingSet;
  protected Norm norm = null;
  private Neuron[][] neurons;
  private Link[][][] links;
  private double momentum; //momentum, typically between 0.8 and 0.95
  private double learning; //learning rate, typically between 0.001 and 0.1
//  private double decay; //weight decay, typically between 0.001 and 0.1

  public MLPNet()
  {
  }

  public Norm norm()
  {
    return norm;
  }

  public int classOutputSize()
  {
    return this.outputs().length;
  }

  public double outputActivation(int classIndex)
  {
    return this.outputs()[classIndex].activation();
  }

  public SampleSet trainingSet()
  {
    return this.trainingSet;
  }

  public void reset()
  {
    this.momentum = 0.90;
    this.learning = 0.01;
//    this.decay = 0.01;
    this.randomizeWeights();
    this.resetNeurons();
  }

  public synchronized void initialize(SampleSet trainingSet, String[] classNames, String[] featureNames)
  {
    this.trainingSet = trainingSet;
    this.norm = trainingSet.norm();
    this.classNames = classNames;
    this.featureNames = featureNames;
    this.setLayers(hiddenSize());
  }

  public void randomizeWeights()
  {
    if (links != null)
      for (int i = 0; i < links.length; i++)
        for (int j = 0; j < links[i].length; j++)
          for (int k = 0; k < links[i][j].length; k++)
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
    if (links != null)
      for (int i = 0; i < links.length; i++)
        for (int j = 0; j < links[i].length; j++)
          for (int k = 0; k < links[i][j].length; k++)
            links[i][j][k].cycleElapsed();
    if (neurons != null)
      for (int i = 0; i < neurons.length; i++)
        for (int j = 0; j < neurons[i].length; j++)
          neurons[i][j].cycleElapsed();
  }

  public int layers()
  {
    return neurons == null ? 0 : neurons.length;
  }

  public void setLayers(int... hiddens)
  {
    this.setLayers(this.featureNames.length, this.classNames.length, hiddens);
  }

  public void setLayers(int inputs, int outputs, int[] hiddens)
  {
    int index;
    for (index = 0; index < hiddens.length; index++)
      if (hiddens[index] == 0)
        break;
    hiddens = Zen.Array.trim(hiddens, 0, index);

    if (!(Zen.Array.equals(hiddens, hiddenSize()) && inputs == inputSize() && outputs == outputSize()))
    {
      this.neurons = new Neuron[hiddens.length + 2][];//+2 for input and output layers
      this.neurons[0] = new Neuron[inputs + 1];//+1 for bias in input layer
      this.neurons[neurons.length - 1] = new Neuron[outputs];//+0... no bias in output layer
      for (int i = 1; i < neurons.length - 1; i++)
        this.neurons[i] = new Neuron[hiddens[i - 1] + 1];//+1 for bias in hidden layers
      this.links = new Link[neurons.length - 1][][];//-1 because n neuron layers and n-1 weight layers
      for (int i = 0; i < this.links.length; i++)
      {
        if (i == this.links.length - 1) //output layer has no bias neuron...
          this.links[i] = new Link[neurons[i + 1].length][];
        else
          this.links[i] = new Link[neurons[i + 1].length - 1][];
        for (int j = 0; j < this.links[i].length; j++)
          this.links[i][j] = new Link[neurons[i].length];
      }


      for (int i = 0; i < neurons.length; i++)
        for (int j = 0; j < neurons[i].length; j++)
        {
          Neuron.Type type = i != neurons.length - 1 && j == neurons[i].length - 1 ? Neuron.Type.BIAS : i == 0 ? Neuron.Type.INPUT : i == neurons.length - 1 ? Neuron.Type.OUTPUT : Neuron.Type.HIDDEN;
          String id = type.isBias() ? "bias" : i == 0 ? featureNames[j] : i == neurons.length - 1 ? classNames[j] : "hidden";
          this.neurons[i][j] = new Neuron(type, id);
        }



      for (int i = 0; i < links.length; i++)
        for (int j = 0; j < links[i].length; j++)
          for (int k = 0; k < links[i][j].length; k++)
            this.links[i][j][k] = new Link(neurons[i][k], neurons[i + 1][j]);

      this.reset();
    }
  }

  public int inputSize()
  {
    return neurons == null || neurons.length == 0 ? 0 : neurons[0].length == 0 ? 0 : neurons[0].length - 1;//without bias!!!
  }

  public int outputSize()
  {
    return neurons == null || neurons.length == 0 ? 0 : neurons[neurons.length - 1].length;
  }

  public int[] hiddenSize()
  {
    if (neurons != null && neurons.length > 2)
    {
      int[] hidden = new int[neurons.length - 2];
      for (int i = 0; i < hidden.length; i++)
        hidden[i] = neurons[i + 1].length - 1;//without bias
      return hidden;
    }
    else
      return new int[0];
  }

//  public void applyWeightsDecay()
//  {
//    if (links != null)
//      for (int i = 0; i < links.length; i++)
//        for (int j = 0; j < links[i].length; j++)
//          for (int k = 0; k < links[i][j].length; k++)
//            links[i][j][k].decay(decay);
//  }
  public double rate(SampleSet set)
  {
    int counter = 0;
    int size = 0;
    for (Sample sample : set)
      if (!sample.isLabel(Label.UNDEF))
      {
        size++;
        feedForward(sample.values());
        if (maxOutput().equals(sample.label()))
          counter++;
      }
    return counter / (double) (size > 0 ? size : 1);
  }

  public synchronized Label classify(Sample sample)
  {
    if (neurons != null)
    {
      feedForward(sample.normalize(norm).values());
      double[] output = outputValues();
      Arrays.sort(output);
      float confidence = 0f;
      if (output.length > 1)
        confidence = (float) ((output[output.length - 1] - output[output.length - 2]) / 2.0);
      return new Label(maxOutput(), confidence > 1f ? 1f : confidence < 0f ? 0f : confidence);
    }
    else
      return Label.UNDEF_LABEL;
  }

  public SampleSet classify(SampleSet set)
  {
    for (Sample sample : set)
      classifyIt(sample);
    return set;
  }

  public Label classifyIt(Sample sample)
  {
    Label label = classify(sample);
    sample.setLabel(label.name());
    return label;
  }

  public String classify(float... features)
  {
    return this.classify(new Sample("" + Arrays.hashCode(features), features)).name();
  }

  public void refreshPanel()
  {
    if (panel != null)
      this.panel.refresh();
  }

  public Neuron[] outputs()
  {
    return neurons == null || neurons.length == 0 ? new Neuron[0] : neurons[neurons.length - 1];
  }

  public double[] outputValues()
  {
    Neuron[] output = outputs();
    double[] values = new double[output.length];
    for (int i = 0; i < values.length; i++)
      values[i] = output[i].activation();
    return values;
  }

  public synchronized int feedForward(float... input)
  {
    if (input.length < this.neurons[0].length)
    {
      if (input != null && input.length > 0)
        for (int j = 0; j < input.length; j++)
          this.neurons[0][j].evaluate(input[j]);

      for (int i = 0; i < links.length; i++)
      {
        Neuron[] y = neurons[i + 1];
        Neuron[] x = neurons[i];
        Link[][] w = links[i];
        for (int j = 0; j < w.length; j++)
        {
          double net = 0.0;
          for (int k = 0; k < w[j].length; k++)
            net += x[k].activation() * w[j][k].weight();
          y[j].evaluate(net);
        }
      }
      return winningClass();
    }
    else
      return -1;
  }

  public int winningClass()
  {
    int indexMax = 0;
    Neuron[] outputs = this.outputs();
    double max = outputs[0].activation();
    for (int i = 1; i < outputs.length; i++)
      if (outputs[i].activation() > max)
        max = outputs[indexMax = i].activation();
    return indexMax;
  }

  private String maxOutput()
  {
    double max = Double.NEGATIVE_INFINITY;
    Neuron out = null;
    for (Neuron neuron : outputs())
      if (neuron.activation() > max)
        max = (out = neuron).activation();
    return out == null ? null : out.name();
  }

  public synchronized double train(int cycles)
  {
    double rate = 0.0;
    for (int i = 0; i < cycles; i++)
      rate = this.backPropagation(this.trainingSet.shuffle());
    this.refreshPanel();
    return rate;
  }

  private synchronized double backPropagation(SampleSet set)
  {
    double rate = 0.0;
    int size = 0;

    for (Sample sample : set)
      if (!sample.isLabel(Label.UNDEF))
      {
        size++;

        float[] features = sample.values();
        for (int i = 0; i < features.length; i++)
          this.neurons[0][i].evaluate(features[i]);

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
              net += x[k].activation() * w[j][k].weight();
            y[j].evaluate(net);//f(net)
            y[j].derivate(net);//f'(net)
          }
        }

        //error backpropagation
        for (int i = links.length; i > 0; i--)
          if (i == links.length)//i.e., output layer
          {
            double max = Double.NEGATIVE_INFINITY;
            Neuron out = null;
            String label = sample.label();
            for (Neuron neuron : outputs())
            {
              if (neuron.activation() > max)
                max = (out = neuron).activation();

              double e = label.equals(neuron.name()) ? 1.0 - neuron.activation() : -1.0 - neuron.activation();
              neuron.derror(e);
            }

            if (out != null && label.equals(out.name()))
              rate++;
          }
          else
          {
            Neuron[] x = neurons[i];
            Neuron[] y = neurons[i + 1];
            Link[][] w = links[i];

            for (int k = 0; k < x.length; k++)
            {
              double e = 0.0;
              for (int j = 0; j < w.length; j++)
                e += w[j][k].weight() * y[j].derror();

              x[k].derror(e);
            }

          }

        //weights correction computation and update
        for (int i = links.length; i > 0; i--)
        {
          Neuron[] y = neurons[i];
          Neuron[] x = neurons[i - 1];
          Link[][] w = links[i - 1];

          for (int j = 0; j < w.length; j++)
            for (int k = 0; k < w[j].length; k++)
              w[j][k].update(learning * (1.0 - momentum) * y[j].derror() * x[k].activation() + momentum * w[j][k].delta());
        }
      }
    this.cycleElapsed();
    return rate / (size > 0 ? size : 1);
  }

  public Panel panel()
  {
    return panel == null ? panel = new Panel() : panel;
  }

  public class Panel extends Paint3 implements ChangeListener
  {
    private Spinner3 h1Spinner = new Spinner3("h1", 0, 100, 5, this);
    private Spinner3 h2Spinner = new Spinner3("h2", 0, 100, 0, this);
    private Spinner3 nuSpinner = new Spinner3("\u03B7", 0, 1, learning, 0.001, this);
    private Spinner3 muSpinner = new Spinner3("\u03B1", 0, 1, momentum, 0.01, this);

    public Panel()
    {
      super("MLP");
      this.add(Box3.vertical(Box3.horizontal(h1Spinner, h2Spinner, nuSpinner, muSpinner)), BorderLayout.SOUTH);
    }

    @Override
    public void stateChanged(ChangeEvent e)
    {
      this.refresh();
    }

    public SampleSet society()
    {
      return trainingSet;
    }

    @Override
    public synchronized void refresh()
    {
      MLPNet.this.setLayers(h1Spinner.intValue(), h2Spinner.intValue());
      MLPNet.this.learning = nuSpinner.value();
      MLPNet.this.momentum = muSpinner.value();

      h1Spinner.enable();
      h2Spinner.enable();
      nuSpinner.enable();
      muSpinner.enable();

      super.refresh();
    }

    @Override
    protected void paintGraphics(Graphics3 g)
    {
      synchronized (MLPNet.this)
      {
        g.clearWhite();

        if (neurons != null)
        {
          for (int i = 0; i < neurons.length; i++)
            for (int j = 0; j < neurons[i].length; j++)
              neurons[i][j].setCoords(g.spaceWidth() * (i + 1) / (neurons.length + 1), g.spaceHeight() * (j + 1) / (neurons[i].length + 1));

          for (Link[][] layer : links)
            for (Link[] from : layer)
              for (Link link : from)
                link.paint(g);
          for (Neuron[] layer : neurons)
            for (Neuron neuron : layer)
              neuron.paint(g);
        }
      }
    }
  }
}
