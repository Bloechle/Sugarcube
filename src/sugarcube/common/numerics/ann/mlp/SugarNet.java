package sugarcube.common.numerics.ann.mlp;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.numerics.ann.Label;
import sugarcube.common.numerics.ann.Norm;
import sugarcube.common.numerics.ann.Sample;
import sugarcube.common.numerics.ann.SampleSet;
import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.ui.gui.Paint3;

import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;

/**
 * This class implements a multilayer perceptron neural network. Implementation follows the Duda/Hart/Stork "Pattern
 * Classification" book hints. Learning rule is backpropagation with momentum. Default activation function is the
 * centered odd sigmoid: tanh. The network contains one input layer, one or more hidden layers, and one output layer.
 * Consecutive layers are fully connected between them. A bias neuron whose activation is a constant equal to one is
 * added at each layer excepted the output one.
 */
public class SugarNet implements NetStrings
{
  protected transient Panel panel = null;
  protected String[] classes = new String[0];
  protected String[] features = new String[0];
  protected SampleSet trainset;
  protected Norm norm = Norm.IDENTITY;
  protected Subnet[] subnets = new Subnet[0];
  //input and hidden size are declared without bias, i.e., with bias => size  + 1
  protected Neuron[] inputs = new Neuron[0];
  protected Neuron[] outputs = new Neuron[0];
  protected double momentum = 0.9; //momentum, typically between 0.8 and 0.95
  protected double maxLearning = 0.1;
  protected double minLearning = 0.001; //learning rate, typically between 0.001 and 0.1
  protected double maxDecay = 0.001;
  protected double minDecay = 0.0001; //weight decay, typically between 0.001 and 0.1   
  protected double[] relax = new double[0];//relax values precomputed

  public SugarNet()
  {
  }

  public Subnet[] subnets()
  {
    return this.subnets;
  }

  public void setRelaxCycles(int relaxCycles)
  {
    this.relax = new double[relaxCycles];
    for (int i = 0; i < relaxCycles; i++)
      this.relax[i] = 1.0 - Math.tanh(i / (double) relaxCycles);
  }

  public double relax(int iteration)
  {
    //goes from 1.0 to 0.0 when iteration -> relaxCycles    
    return iteration < relax.length ? relax[iteration] : 0;
  }

  public double learning(int iteration)
  {
    return (this.maxLearning - this.minLearning) * relax(iteration) + this.minLearning;
  }

  public Norm norm()
  {
    return norm;
  }

  public int nbOfClasses()
  {
    return this.outputs().length / 2;
  }

  public double activation(int classIndex)
  {
    return (this.outputs[classIndex * 2].activation() - this.outputs[classIndex * 2 + 1].activation()) / 2.0;
  }

  public Neuron[] outputs()
  {
    return this.outputs;
  }

  public Neuron[] inputs()
  {
    return this.inputs;
  }

  protected int[] hiddenSizes()
  {
    return Zen.Array.Ints(5, 5);
  }

  public SampleSet trainingSet()
  {
    return this.trainset;
  }

  //get existing neuron by name if existing, create new otherwise
  protected Neuron neuron(int index, String name, Neuron.Type type)
  {
    Neuron[] neurons = type.equals(Neuron.Type.INPUT) ? inputs : outputs;
    if (index < neurons.length && neurons[index].isName(name))
      return neurons[index];
    for (Neuron neuron : neurons)
      if (neuron.isName(name))
        return neuron;
    return new Neuron(type, name);
  }

  //get existing subnet by name if existing, create new otherwise
  protected Subnet subnet(int index, String name)
  {
    if (index < subnets.length && subnets[index].isName(name))
      return subnets[index];
    for (Subnet subnet : subnets)
      if (subnet.isName(name))
        return subnet;
    return new Subnet(this, index, name);
  }

  public synchronized int[] liveUpdate(SampleSet trainingSet, String[] classes, String[] features, int fullRecos, int minCycles, int maxCycles)
  {
    this.trainset = trainingSet;
    this.norm = Norm.IDENTITY;
    this.features = features;
    this.classes = classes;
    this.setRelaxCycles(minCycles);
    Neuron[] ins = new Neuron[features.length + 1]; //+ bias
    for (int i = 0; i < features.length; i++)
      ins[i] = neuron(i, features[i], Neuron.Type.INPUT);
    ins[features.length] = new Neuron(Neuron.Type.BIAS, INPUT_BIAS);
    Neuron[] outs = new Neuron[classes.length * 2];
    for (int i = 0; i < classes.length; i++)
    {
      outs[2 * i] = this.neuron(2 * i, classes[i], Neuron.Type.OUTPUT);
      outs[2 * i + 1] = this.neuron(2 * i + 1, NOT_PREFIX + classes[i], Neuron.Type.OUTPUT);
    }
    this.inputs = ins;
    this.outputs = outs;
    Subnet[] subs = new Subnet[classes.length];
    for (int i = 0; i < classes.length; i++)
      subs[i] = this.subnet(i, classes[i]);
    this.subnets = subs;
//    this.iteration = 0;
    for (Subnet subnet : this.subnets)
      subnet.liveUpdate();
    return this.train(fullRecos, minCycles, maxCycles);
  }

  public synchronized void initialize(SampleSet trainingSet, String[] classNames, String[] featureNames)
  {
    this.trainset = trainingSet;
    this.norm = trainingSet.norm();
    this.features = featureNames;
    this.classes = classNames;
    this.reset();
  }

  public synchronized void reset()
  {
    Neuron[] ins = new Neuron[features.length + 1]; //+ bias
    for (int i = 0; i < features.length; i++)
      ins[i] = neuron(i, features[i], Neuron.Type.INPUT);
    ins[features.length] = new Neuron(Neuron.Type.BIAS, "input-bias");

    Neuron[] outs = new Neuron[classes.length * 2];
    for (int i = 0; i < classes.length; i++)
    {
      outs[2 * i] = this.neuron(2 * i, classes[i], Neuron.Type.OUTPUT);
      outs[2 * i + 1] = this.neuron(2 * i + 1, "¬" + classes[i], Neuron.Type.OUTPUT);
    }

    this.inputs = ins;
    this.outputs = outs;

    Subnet[] subs = new Subnet[classes.length];
    for (int i = 0; i < classes.length; i++)
      subs[i] = this.subnet(i, classes[i]);

    this.subnets = subs;
//    this.iteration = 0;
    for (Subnet subnet : this.subnets)
      subnet.reset();
  }

  public void setInputValues(float[] inputValues)
  {
    for (int j = 0; j < inputValues.length; j++)
      this.inputs[j].setActivation(inputValues[j]);
  }

  public synchronized int feedForward(float[] inputValues)
  {
    if (inputValues.length < this.inputs.length)
    {
      this.setInputValues(inputValues);
      for (int i = 0; i < subnets.length; i++)
        this.subnets[i].feedForward();
      return this.winningClass();
    }
    else
      return -1;
  }

  public int winningClass()
  {
    if (outputs.length == 0)
      return -1;
    else
    {
      int indexMax = 0;
      double max = outputs[0].activation();
      for (int i = 2; i < outputs.length; i += 2)
        if (outputs[i].activation() > max)
          max = outputs[indexMax = i].activation();
      return indexMax / 2;
    }
  }

  public synchronized int[] train(int fullRecos, int minCycles, int maxCycles)
  {
    for (int i = 0; i < maxCycles; i++)
    {
      SampleSet shuffleSet = this.trainset.shuffle();
      for (Subnet subnet : subnets)
        if (subnet.fullRecos < fullRecos || i < minCycles)
          subnet.backPropagation(shuffleSet);
    }
    this.refreshPanel();
    int[] cycles = new int[subnets.length];
    for (int i = 0; i < subnets.length; i++)
      cycles[i] = subnets[i].iteration;
    return cycles;
  }

  public synchronized double[] train(int cycles)
  {
    double[] rates = new double[cycles];
    for (int i = 0; i < cycles; i++)
    {
      SampleSet shuffleSet = this.trainset.shuffle();
      double rate = 0.0;
      //weights decay before correction in order to avoid increase in train error      
      for (Subnet subnet : subnets)
        subnet.applyWeightDecay();

      int size = 0;
      for (Sample sample : shuffleSet)
        if (!sample.isLabel(Label.UNDEF))
        {
          size++;
          this.setInputValues(sample.values());
          for (int j = 0; j < subnets.length; j++)
            subnets[j].backPropagation(sample.label().equals(this.classes[j]) ? 1f : -1f);

          int winningClass = this.winningClass();
          if (winningClass >= 0 && sample.label().equals(this.classes[winningClass]))
            rate++;
        }
      for (Subnet subnet : subnets)
        subnet.cycleElapsed();

      rates[i] = rate / (size > 0 ? size : 1);
    }
    this.refreshPanel();
    System.out.println("rates=" + Arrays.toString(rates));
    return rates;
  }

  public synchronized Label classify(float... features)
  {
    if (features.length != this.features.length)
    {
      Log.warn(this, ".classify - features.length!=featureNames.length: " + features.length + "!=" + this.features.length);
      return Label.UNDEF_LABEL;
    }
    int classIndex = feedForward(norm == null || norm == Norm.IDENTITY ? features : norm.normalize(features));
    if (classIndex < 0)
      return Label.UNDEF_LABEL;
    double difference = 0;
    double confidence = 2;
    for (int i = 0; i < outputs.length; i += 2)
      if (i != classIndex * 2 && (difference = outputs[classIndex * 2].activation() - outputs[i].activation()) < confidence)
        confidence = difference;
    confidence = confidence > 1 ? 1 : confidence;
    return new Label(this.classes[classIndex], confidence > 1f ? 1f : confidence < 0f ? 0f : (float) confidence);
  }

  public void refreshPanel()
  {
    if (panel != null)
      this.panel.refresh();
  }

  public Panel panel()
  {
    return panel(true);
  }

  public Panel panel(boolean simpleView)
  {
    return panel == null ? panel = new Panel(simpleView) : panel;
  }

  public class Panel extends Paint3 implements MouseMotionListener, MouseListener, ActionListener
  {
    private boolean simpleView = true;
    private Neuron selected = null;
    private int nbOfInputs = 10;

    public Panel()
    {
      this(true);
    }

    public Panel(boolean simpleView)
    {
      super("SugarNet");
      this.simpleView = simpleView;
      this.paintPanel.setFocusable(true);
      this.paintPanel.addMouseListener(this);
      this.paintPanel.addMouseMotionListener(this);
      this.paintPanel.registerKeyboardAction(this, KeyStroke.getKeyStroke('t'), WHEN_IN_FOCUSED_WINDOW);
    }

    public void setSimpleView(boolean simpleView)
    {
      this.simpleView = simpleView;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
      this.simpleView = !this.simpleView;
      this.refresh();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
      nbOfInputs -= e.getWheelRotation();
      nbOfInputs = nbOfInputs < 1 ? 1 : nbOfInputs > SugarNet.this.inputs.length - 1 ? SugarNet.this.inputs.length - 1 : nbOfInputs;
      this.refresh();
    }

    public SampleSet sampleSet()
    {
      return trainset;
    }

    @Override
    public synchronized void refresh()
    {
      super.refresh();
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
      if (selected != null)
        selected.isEnabledSwap();
      this.refresh();
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
      Neuron oldSelected = selected;

      selected = null;
      //TODO add inner neurons
      for (Neuron neuron : inputs)
        if (neuron.select(e.getPoint()))
          this.selected = neuron;
      for (Neuron neuron : outputs)
        if (neuron.select(e.getPoint()))
          this.selected = neuron;

      if (oldSelected != selected)
        this.paintPanel.repaint();
    }

    @Override
    protected void paintGraphics(Graphics3 g)
    {
      boolean simpleView = this.simpleView;
      synchronized (SugarNet.this)
      {
        int width = g.spaceWidth();
        int height = g.spaceHeight();
        int margin = width / 3;

        for (Neuron neuron : SugarNet.this.inputs)
          neuron.relevance = 0.0;

        for (Subnet subnet : subnets)
          if (selected == null || !selected.isOutput() || subnet.output() == selected)
            subnet.backpropagateRelevance();


        Neuron[] inputs = SugarNet.this.inputs;
        if (inputs.length > 0)
        {
          inputs = Arrays.copyOf(inputs, inputs.length - 1); //we do not include the bias
          Arrays.sort(inputs);
        }


        for (int i = nbOfInputs; i < inputs.length; i++)
          inputs[i].setCoords(-1, -1);
        inputs = Arrays.copyOf(inputs, inputs.length > nbOfInputs ? nbOfInputs : inputs.length);

        for (int i = 0; i < inputs.length; i++)
          inputs[i].setCoords(margin, (i + 1) * height / (inputs.length + 1));
        for (int i = 0; i < outputs.length; i += 2)
        {
          int x = width - margin;
          int y = (i / 2 + 1) * height / (outputs.length / 2 + 1);

          outputs[i].setCoords(x, y);
          outputs[i + 1].setCoords(x, y + height / (2 * outputs.length + 1));
        }
        for (int i = 0; i < subnets.length; i++)
          subnets[i].setNeuronCoords(margin, i * height / subnets.length, width - 2 * margin, height / subnets.length);

        if (selected != null)
          feedForward(trainset.mean(selected.name()));

        g.clearWhite();

        for (Subnet subnet : subnets)
        {
          Neuron output = subnet.output();

          for (Neuron neuron : SugarNet.this.inputs)
            neuron.relevance = 0.0;
          if (selected == null || !selected.isOutput() || output == selected)
            subnet.backpropagateRelevance();

          if (simpleView)
          {
            double maxRelevance = 0.001;
            for (Neuron input : inputs)
              if (input.relevance > maxRelevance)
                maxRelevance = input.relevance;
            for (Neuron input : inputs)
            {
              double r = input.relevance / maxRelevance;
              g.draw(new Line3(input.x, input.y, output.x, output.y), Color3.BLUE_PIGMENT.alpha((r * 1.1) - 0.1), Math.abs(1.5 * r));
            }
            output.paint(g);
          }
          else
          {
            for (Link[][] layer : subnet.links)
              for (Link[] links : layer)
                for (Link link : links)
                  if (link.isEnabled)
                    link.paint(g);

            for (int i = 1; i < subnet.neurons.length; i++)
              for (int j = 0; j < subnet.neurons[i].length; j++)
                subnet.neurons[i][j].paint(g);
          }
        }

        for (Neuron neuron : inputs)
          neuron.paint(g);
      }
    }
  }
}
