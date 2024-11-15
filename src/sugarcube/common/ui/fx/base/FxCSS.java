package sugarcube.common.ui.fx.base;

import javafx.collections.ObservableList;
import javafx.scene.Node;

public class FxCSS
{
  public static void Glass(Node... nodes)
  {
    for (Node node : nodes)
      node.setStyle("-fx-base: transparent; -fx-focus-color: transparent; ");
  }

  public static void Styles(String style, Node... nodes)
  {
    for (Node node : nodes)
      Style(node, style);
  }

  public static void NoPadding(Node... nodes)
  {
    for (Node node : nodes)
      Style(node, "-fx-padding: 0 0 0 0", false);
  }

  public static Node Style(Node node, String style)
  {
    return Style(node, style, false);
  }

  public static Node Style(Node node, String style, boolean restyle)
  {
    style = style == null ? "" : style.trim();

    if (style.indexOf(';') > 0 || style.indexOf(':') > 0)
    {
      style.replace("bg-col", "-fx-background-color");
      node.setStyle(style);
    } else
    {
      ObservableList<String> classes = node.getStyleClass();

      if (restyle)
        classes.clear();

      if (!style.isEmpty())
      {
        String[] tokens = style.replace(".", "").split("\\s+");
        for (String tk : tokens)
        {
          if ((tk = tk.trim()).isEmpty())
            continue;
          String sub = tk.substring(1);
          boolean has = classes.contains(sub);
          switch (tk.charAt(0))
          {
          case '+':
            if (!has)
              classes.add(sub);
            break;
          case '-':
            if (has)
              classes.remove(sub);
            break;
          case '!':
            if (has)
              classes.remove(sub);
            else
              classes.add(sub);
            break;
          default:
            if (!classes.contains(tk))
              classes.add(tk);
            // Log.debug(FxCSS.class, ".style - "+tk+": "+classes);
            break;
          }
          // JsLog.debug(CSS.class, " tk="+tk+", all="+element.getClassName());
        }

        // Log.debug(FxCSS.class, ".style - styles="+node.getStyleClass());
      }
    }
    return node;
  }
}
