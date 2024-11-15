package sugarcube.insight.ribbon.file;

import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.base.FxText;
import sugarcube.common.ui.fx.containers.FxAnchorPane;
import sugarcube.common.ui.fx.shapes.FxCircle;
import sugarcube.common.ui.fx.shapes.FxLine;
import sugarcube.common.ui.fx.shapes.FxPath;
import sugarcube.formats.ocd.objects.font.SVGGlyph;

public class GlyphPane extends FxAnchorPane
{
  public interface Listener
  {
    public void selectGlyph(GlyphPane pane);
  }
  
  private static final Color GREEN = Color3.GREEN_DARK.alpha(0.8).fx();
  private static final Background WHITE = Color3.WHITE.fxBackground();

  public SVGGlyph glyph;
  public Listener listener;

  public GlyphPane(SVGGlyph glyph, int fontsize, Listener listener)
  {
    this.glyph = glyph;
    double size = fontsize;
    double w = size;
    double h = 2 * size;
    double b = 4 * size / 3;
    double dx = w/5;
    double dy = h/5;

    FxPath path = glyph.path(size).fx();
    path.fill(Color3.BLACK);
    path.setStrokeWidth(0);
    path.setTranslateX(dx);
    path.setTranslateY(b);


    this.add(new FxLine(dx, b, glyph.horizAdvX*size+dx, b).stroke(GREEN));
    this.add(new FxCircle(dx,b,2).fill(GREEN));
    this.add(path);
    this.add(new FxText(dx, dy, glyph.unicode()));

    this.setPrefSize(w, h);

    this.setBackground(WHITE);

    String style = "-fx-border-width:2px; -fx-border-color:rgba(0,0,0,0.1);";

    this.setStyle(style);

    this.setOnMouseEntered(e -> this.setStyle(style.replace("rgba(0,0,0,0.1)", "orange")));
    this.setOnMouseExited(e -> this.setStyle(style));
    this.setOnMouseClicked(e -> listener.selectGlyph(this));
  }

  public void highlight(boolean highlight)
  {
    this.setBackground(highlight ? Color3.ORANGE.alpha(0.8).fxBackground() : WHITE);
  }

}
