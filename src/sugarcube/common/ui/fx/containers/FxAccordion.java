package sugarcube.common.ui.fx.containers;

import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import sugarcube.common.ui.fx.base.Fx;

public class FxAccordion extends Accordion
{
  public FxAccordion()
  {
  }

  public FxTitledPane add(String title, Node content, boolean expand)
  {
    FxTitledPane pane = new FxTitledPane(title, content);
    this.getPanes().add(pane);
    if (expand)
      this.setExpandedPane(pane);
    return pane;
  }

  public FxAccordion preventFullCollapse()
  {
    this.expandedPaneProperty().addListener((obs, old, val) -> {
      TitledPane expand = old;      
      for (TitledPane pane : this.getPanes())
      {
        if (pane.isExpanded())
        {
          expand = null;
          break;
        }
        else if (pane != old)
          expand = pane;
      }

      final TitledPane newPane = expand;
      if (newPane != null)
        Fx.Run(() -> this.setExpandedPane(newPane));
    });
    return this;
  }

  public void clear()
  {
    this.getPanes().clear();
  }
}
