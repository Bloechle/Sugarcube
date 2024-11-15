package sugarcube.common.ui.fx.controls;

import javafx.event.EventHandler;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import sugarcube.common.numerics.Math3;

public class FxProgressBar extends StackPane
{
  public ProgressBar bar;
  public Text text = new Text();
  public int min = 1;
  public int max = 100;

  public FxProgressBar()
  {
    this.bar = new ProgressBar();
    this.getChildren().addAll(bar, text);
    text.setMouseTransparent(true);
    // FxCSS.style(bar, ".sc-progress-bar");
  }
  
  public FxProgressBar show()
  {
    this.setVisible(true);
    return this;
  }

  public FxProgressBar handleMouse(EventHandler<MouseEvent> handler)
  {
    bar.setOnMousePressed(m -> updateBar(m,null));
    bar.setOnMouseDragged(m -> updateBar(m,null));    
    bar.setOnMouseReleased(m ->  updateBar(m,handler));
    return this;
  }
  
  private void updateBar(MouseEvent m, EventHandler<MouseEvent> handler)
  {
    double p=m.getX()/(double)bar.widthProperty().get();   
    this.setProgress(Math3.Round((p < 0 ? 0 : p > 1 ? 1 : p)*max));
    if(handler!=null)
      handler.handle(m);
  }
  

  public double progress()
  {
    return bar.getProgress();
  }
  
  public FxProgressBar min(int min)
  {
    this.min = min;
    return this;
  }
  
  public FxProgressBar max(int max)
  {
    this.max = max;
    return this;
  }

  public void setProgress(int nb)
  {
    this.bar.setProgress(nb/(double)max);
    this.setText(nb + "/" + max);
  }
  
  public void setProgress(int nb, int max)
  {
    this.max = max;
    this.bar.setProgress(nb/(double)max);
    this.setText(nb + "/" + max);
  }

  public void setText(String text)
  {
    this.text.setText(text);
  }

  public FxProgressBar size(double w, double h)
  {
    if (w > 0)
    {
      this.setPrefWidth(w);
      bar.setPrefWidth(w);
    }
    if (h > 0)
    {
      this.setPrefHeight(h);
      bar.setPrefHeight(h);
    }
    return this;
  }

}
