package sugarcube.insight.core.dialogs.symbol;

import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import sugarcube.common.ui.fx.base.FxText;
import sugarcube.common.ui.fx.containers.FxAnchorPane;
import sugarcube.common.ui.fx.shapes.FxLine;
import sugarcube.common.graphics.Color3;
import sugarcube.formats.pdf.resources.fontmapping.CharMap;

public class SymbolPane extends FxAnchorPane {
    public interface Listener {
        void selectSymbol(SymbolPane symbol);
    }

    private static final Color GREEN = Color3.GREEN_DARK.alpha(0.8).fx();
    private static final Background WHITE = Color3.WHITE.fxBackground();

    public Font font;
    public CharMap.Codes codes;
    public Listener listener;

    public SymbolPane(Font font, CharMap.Codes codes, Listener listener) {
        this.font = font;
        this.codes = codes;
        double size = font.getSize();
        double w = size;
        double h = 2 * size;
        double b = 4 * size / 3;
        double dx = w / 5;

        this.add(new FxLine(dx, b, w + dx, b).stroke(GREEN));
        this.add(new FxText(dx, b, codes.unicode).font(font));

        this.setPrefHeight(h);

        this.setBackground(WHITE);

        String style = "-fx-border-width:2px; -fx-border-color:rgba(0,0,0,0.1);";

        this.setStyle(style);

        this.setOnMouseEntered(e -> this.setStyle(style.replace("rgba(0,0,0,0.1)", "orange")));
        this.setOnMouseExited(e -> this.setStyle(style));
        this.setOnMouseClicked(e -> listener.selectSymbol(this));
    }

    public void highlight(boolean highlight) {
        this.setBackground(highlight ? Color3.ORANGE.alpha(0.8).fxBackground() : WHITE);
    }

}
