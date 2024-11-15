package sugarcube.common.system.process;

import sugarcube.common.system.log.Logger;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.data.Bool;
import sugarcube.common.interfaces.Progressable;

import javax.swing.*;

public class Progression extends Progressor
{
    public interface Listener
    {
        void progress(Progressable progression);
    }


    // PERMIL is more precise than PERCENT
    public static final int PERMIL = 1000;
    // <0 = canceled, 0 = not yet processing, 1..nbOfSteps = processing,
    // >nbOfSteps = complete
    private int lastStepDone;
    private final Set3<Listener> listeners = new Set3<>();
    private final Logger log = new Logger(this.getClass().getName());
    private Bool doCancel = new Bool();
    private JProgressBar progressBar = null;

    public Progression(Listener... listeners)
    {
        this("Processing...", PERMIL, listeners);
    }

    public Progression(String name, Listener... listeners)
    {
        this(name, name, PERMIL, listeners);
    }

    public Progression(String name, String desc, Listener... listeners)
    {
        this(name, desc, PERMIL, listeners);
    }

    public Progression(String desc, int nbOfSteps, Listener... listeners)
    {
        this("", desc, nbOfSteps, listeners);
    }

    public Progression(String name, String desc, int nbOfSteps, Listener... listeners)
    {
        this.name = name;
        this.desc = desc;
        this.steps = nbOfSteps;
        this.listeners.addAll(listeners);
    }

    public Progression reset(String name, int nbOfSteps)
    {
        this.name = name;
        this.steps = nbOfSteps;
        reset();
        return this;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isName(String name)
    {
        return name.equals(name);
    }

    public Logger log()
    {
        return log;
    }

    public String description()
    {
        return desc;
    }

    public void setDescription(String description)
    {
        desc = description;
    }

    public Progression listen(Listener... listeners)
    {
        this.listeners.addAll(listeners);
        return this;
    }

    public Progression addListeners(Listener... listeners)
    {
        this.listeners.addAll(listeners);
        return this;
    }

    public int numberOfSteps()
    {
        return steps;
    }

    public long lastStepDone()
    {
        return lastStepDone;
    }

    @Override
    public float progress()
    {
        return isCompleted() ? 1f : lastStepDone / (float) steps;
    }

    public int level(int maxLevel)
    {
        return isCompleted() ? maxLevel : (int) (0.5 + maxLevel * progress());
    }

    public int permil()
    {
        return isCompleted() ? 1000 : (int) (0.5 + 1000 * progress());
    }

    public int percent()
    {
        return isCompleted() ? 100 : (int) (0.5 + 100 * progress());
    }

    public void setNbOfSteps(int steps)
    {
        this.steps = steps;
    }

    public void start()
    {
        reset();
        stepAchieved(0);
    }

    public void start(String description)
    {
        reset();
        setDescription(description);
        stepAchieved(0);
    }

    // IMPORTANT : call it when the step is finished
    public void stepAchieved()
    {
        stepAchieved(lastStepDone + 1);
    }

    public Progression stepAchieved(String description)
    {
        setDescription(description);
        stepAchieved(lastStepDone + 1);
        return this;
    }

    public void stepAchieved(int currentStep)
    {
        lastStepDone = currentStep;
        progress = progress();
        notifyListeners();
    }

    public void stepAchieved(int currentStep, String description)
    {
        stepAchieved(currentStep);
        setDescription(description);
    }

    public void setProgress(double value)
    {
        stepAchieved(value < 0 ? -1 : (int) ((value > 1 ? 1 : value) * steps));
    }

    public final String setDescription(Object description)
    {
        return desc = (description.getClass().equals(String.class) ? (String) description
                : description.getClass().equals(Class.class) ? ((Class) description).getName() : description.getClass().getName());
    }

//  public void setProgress(double value, Object description)
//  {
//    this.setProgress(value);
//    this.setDescription(description);
//  }

    public Progression update(double progress, Object desc)
    {
        setProgress(progress);
        setDescription(desc);
        return this;
    }

    @Override
    public Progression update(float progress, String desc)
    {
        setProgress(progress);
        setDescription(desc);
        return this;
    }

    public void setProgressBar(JProgressBar bar)
    {
        progressBar = bar;
        progressBar.setMinimum(0);
        progressBar.setMaximum(steps);
    }

    private void notifyListeners()
    {
        if (progressBar != null)
        {
            progressBar.setValue(lastStepDone);
            progressBar.setString(desc);
        }
        Progression copy = copy();
        for (Listener listener : listeners)
            listener.progress(copy);
    }

    public void initialize(Object description)
    {
        setDescription(description);
        setProgress(0.0);
    }

    public void reset()
    {
        lastStepDone = 0;
    }

    @Override
    public Progression complete(String description)
    {
        desc = description;
        complete();
        return this;
    }

    public Progression complete()
    {
        state = STATE_COMPLETED;
        stepAchieved(steps);
        return this;
    }

    public boolean isCompleted()
    {
        return state == STATE_COMPLETED;
    }

    public boolean isProcessing()
    {
        return lastStepDone >= 0 && lastStepDone < steps;
    }

    public void cancel()
    {
        doCancel.setBool(true);
    }

    public void setBoolCancel(Bool doCancel)
    {
        this.doCancel = doCancel;
    }

    public boolean canceled()
    {
        return doCancel.isTrue();
    }


    @Override
    public String toString()
    {
        return name + " " + lastStepDone + "/" + steps;
    }

    public Progression copy()
    {
        Progression copy = new Progression();
        copy.name = name;
        copy.desc = desc;
        copy.progress = progress;
        copy.state = state;
        copy.steps = steps;
        copy.lastStepDone = lastStepDone;
        copy.doCancel = doCancel;
        return copy;
    }
}
