package sugarcube.common.ui.fx.base;

import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.menus.FxIcon;
import sugarcube.formats.pdf.resources.icons.Icon;

public class FxText extends Text implements FxIcon
{
    public FxText()
    {
        super();
    }

    public FxText(double x, double y, String text)
    {
        super(x, y, text);
    }

    public FxText(String text)
    {
        super(text);
    }

    public FxText style(String style)
    {
        FxCSS.Style(this, style);
        return this;
    }

    public FxText hide()
    {
        return visible(false);
    }

    public FxText show()
    {
        return visible(true);
    }

    public FxText opacity(double opacity)
    {
        this.setOpacity(opacity);
        return this;
    }

    public FxText visible(boolean isVisible)
    {
        if (this.isVisible() != isVisible)
            this.setVisible(isVisible);
        return this;
    }

    public FxText fill(Color3 c)
    {
        this.setFill(c.fx());
        return this;
    }

    public FxText font(Font f)
    {
        this.setFont(f);
        return this;
    }

    public FxText setXY(double x, double y)
    {
        this.setX(x);
        this.setY(y);
        return this;
    }

    public double width()
    {
        return this.getLayoutBounds().getWidth();
    }

    public double height()
    {
        return this.getLayoutBounds().getWidth();
    }

    public FxText awesomeIcon(int size, Color3 color)
    {
        return icon(Icon.FONT_AWESOME_NAME, size, color);
    }

    public FxText materialIcon(int size, Color3 color)
    {
        return icon(Icon.FONT_MATERIAL_NAME, size, color);
    }

    public FxText icon(String fontFamily, int size, Color3 color)
    {
        this.setStyle("-fx-base: " + color.cssHexValue() + "; -fx-stroke-width: 0; -fx-font-family: " + fontFamily + "; -fx-font-size: " + size + ";"
                + " -fx-font-smoothing-type: gray; -fx-opacity: 1; -fx-fill: linear-gradient(derive(-fx-base, 10%) 0%, derive(-fx-base, 0%) 60%, derive(-fx-base, 0%) 100%);");
        return this;
    }

    public FxText effect(Effect value)
    {
        this.setEffect(value);
        return this;
    }

    public FxText cache()
    {
      this.setCache(true);
      return this;
    }

    @Override
    public Node node()
    {
        return this;
    }

    public static FxText Get(double x, double y, String text)
    {
        return new FxText(x, y, text);
    }
}
