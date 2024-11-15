package sugarcube.insight.ribbon.insert.shape;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import sugarcube.common.graphics.geom.*;
import sugarcube.common.ui.fx.dialogs.FxWindow;
import sugarcube.common.ui.gui.Font3;
import sugarcube.insight.core.IS;
import sugarcube.insight.ribbon.insert.InsertRibbon;
import sugarcube.formats.pdf.resources.icons.Icon;

import java.awt.*;

public class ShapeDialog extends FxWindow
{
  private static Font3 FONT;

  private InsertRibbon tab;
  private @FXML BorderPane root;
  private @FXML ScrollPane scrollPane;
  private @FXML TilePane tilePane;
  private int size = 18;

  public ShapeDialog(InsertRibbon tab)
  {
    super("Insert Shape", true, tab.window());
    this.tab = tab;
    IS.InsightCSS(windowPane);
    this.icon(Icon.Image(Icon.CIRCLE, 48, IS.GREEN_LIGHT));
    this.minSize(600, 400);
    this.noModality();

    this.bindSize(root);
    this.dnd();
    this.show();

    if (FONT == null)
      FONT = Font3.Load(Icon.class, Icon.FONT_AWESOME_FILENAME);

    
    int shapeSize = 4*size/3;
    
    addShape(new Circle3(0, 0, shapeSize/2));
    addShape(new Rectangle3(0,0,shapeSize, shapeSize));
    addShape(new Triangle3(new Point3(0,0), shapeSize*2/3, Math.PI));

    for (Icon icon : Icon.AWESOME_ICONS)
      addShape(FONT.glyph((int) icon.character).scale(size));

  }

  public ShapePath addShape(Shape shape)
  {
    return addShape(new Path3(shape));
  }

  public ShapePath addShape(Path3 path)
  {
    ShapePath shape = new ShapePath(tab, path);
    this.tilePane.getChildren().add(shape);
    return shape;
  }

  public static void Show(InsertRibbon tab)
  {
    // Fx.Tip("Insert Shape...", button);
    // button.setGraphic(Iconic.Get(Iconic.TAGS, 26, Color3.ORANGE_PURE));
    ShapeDialog dialog = new ShapeDialog(tab);
    dialog.setOnClose(() -> {
      tab.refresh();
    });

  }
}
