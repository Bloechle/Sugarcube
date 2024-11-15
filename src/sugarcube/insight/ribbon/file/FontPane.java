package sugarcube.insight.ribbon.file;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Stringer;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.graphics.Color3;
import sugarcube.common.data.xml.CharRef;
import sugarcube.common.data.xml.Nb;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.core.dialogs.symbol.SymbolDialog;
import sugarcube.insight.interfaces.FxPaneLoader;
import sugarcube.formats.ocd.objects.font.SVGFont;
import sugarcube.formats.ocd.objects.font.SVGGlyph;
import sugarcube.formats.pdf.resources.fontmapping.CharMap;
import sugarcube.formats.pdf.resources.icons.Icon;


import java.util.Arrays;

public class FontPane extends FxEnvironmentPane implements GlyphPane.Listener, CharMap.Listener
{
  public static FxPaneLoader LOADER = env -> new FontPane(env);

  private @FXML ListView<String> fontList;
  private @FXML Label fontnameLabel;
  private @FXML ChoiceBox<String> weightChoice;
  private @FXML ChoiceBox<String> styleChoice;
  private @FXML TextField ascentField;
  private @FXML TextField descentField;
  private @FXML TextField charField;
  private @FXML Button charBt;
  private @FXML Button updateBt;
  private @FXML Label widthLabel;
  private @FXML Label codeLabel;
  private @FXML TextArea pathArea;
  private @FXML FlowPane glyphsPane;

  public int fontsize = 40;
  public GlyphPane selGlyph = null;
  public SVGFont selFont = null;

  public FontPane(FxEnvironment env)
  {
    super(env, "Fonts", "Font Inspector", Icon.Awesome(Icon.FONT, 32, Color3.ANTHRACITE));

    fontList.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> refreshFont(val));

    Fx.Init(weightChoice, "normal", "normal", "bold", "100", "200", "300", "400", "500", "600", "700");
    Fx.Init(styleChoice, "normal", "normal", "italic", "oblique");

    charBt.setOnAction(e -> new SymbolDialog(env.window(), this));

    updateBt.setOnAction(e -> {
      if (selFont != null)
      {
        selFont.setStyle(styleChoice.getValue());
        selFont.setWeight(weightChoice.getValue());
        selFont.setAscent1000(Nb.Int(ascentField.getText(), selFont.ascent1000()));
        selFont.setDescent1000(Nb.Int(descentField.getText(), selFont.descent1000()));

        if (selGlyph != null)
        {
          SVGGlyph g = selGlyph.glyph;
          g.setRemap(CharRef.UnHtml(charField.getText()));
          Log.debug(this,  " - remap="+g.unicode());
          if (!selGlyph.glyph.pathData().trim().equals(pathArea.getText().trim()))
            g.setPathData(pathArea.getText());
          this.refreshFont(fontList.getSelectionModel().getSelectedItem());
        }
        Log.debug(this, " - font modified="+selFont.modified());
      }
    });
  }

  @Override
  public void refresh()
  {
    if (env.hasOCD())
      refreshFontList();
  }

  private void refreshFontList()
  {
    String[] fontNames = env.ocd().fontHandler.map().keySet().toArray(new String[0]);
    Arrays.sort(fontNames, Stringer.STRING_COMPARATOR);
    fontList.getItems().setAll(fontNames);

    if (fontNames.length > 0)
      fontList.getSelectionModel().select(0);
  }

  public void refreshFont(String fontname)
  {
    if (fontname == null)
      return;

    this.fontnameLabel.setText("Font : " + fontname);

    selGlyph = null;
    selFont = env.ocd.fontHandler.get(fontname);
    weightChoice.getSelectionModel().select(selFont.weight());
    styleChoice.getSelectionModel().select(selFont.style());
    ascentField.setText(selFont.ascent1000() + "");
    descentField.setText(selFont.descent1000() + "");
    glyphsPane.getChildren().clear();
    
    Log.debug(this, ".refreshFont - " + fontname+", glyphs: "+selFont.glyphArray().length);
    
    for (SVGGlyph glyph : selFont.glyphArray())
    {
      GlyphPane pane = new GlyphPane(glyph, fontsize, this);
      glyphsPane.getChildren().add(pane);
      if (selGlyph == null)
        selGlyph = pane;
    }
    this.selectGlyph(selGlyph);
  }

  @Override
  public void selectGlyph(GlyphPane pane)
  {
    if (selGlyph != null)
      selGlyph.highlight(false);
    this.selGlyph = pane;
    if (selGlyph != null)
    {
      SVGGlyph g = pane.glyph;
      selGlyph.highlight(true);
      charField.setText(g.unicode());
      pathArea.setText(g.pathData());
      widthLabel.setText("width "+Math.round(1000*+g.horizAdvX));
      
      codeLabel.setText("code "+g.code+" ("+g.code()+")");
    } else
    {
      charField.setText("");
      pathArea.setText("");
      widthLabel.setText("");
      codeLabel.setText("");
    }

  }

  @Override
  public void selectCodes(CharMap.Codes codes)
  {
    charField.setText(codes.unicode);
  }

}
