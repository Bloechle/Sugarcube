package sugarcube.insight.core;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.Color3;
import sugarcube.common.system.io.SoftVersion;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.controls.FxButton;
import sugarcube.formats.pdf.resources.icons.Icon;

public class IS
{

    public static SoftVersion VERSION = new SoftVersion("Insight", "2022-01-01", 0, 8, 6);
    public static final String FONTS_ON = "IS-fontsOn";
    public static final String IMAGES_ON = "IS-imagesOn";
    public static final String PATHS_ON = "IS-pathsOn";
    public static final String TEXTS_ON = "IS-textsOn";
    public static final String CLIPS_ON = "IS-clipsOn";
    public static final String ELEMENTS_NB = "IS-elementsNb";
    public static final String CLIPS_HIGH = "IS-clipsHigh";
    public static final String TEXTS_HIGH = "IS-textsHigh";
    public static final String PATHS_HIGH = "IS-pathsHigh";
    public static final String SPACES_HIGH = "IS-spacesHight";
    public static String[] VIEWBOXES = "CanvasBox MediaBox ViewBox TrimBox".split(" ");
    public static String[] UNITS = "px mm cm in pt pc".split(" ");
    public static String[] DASHES = "none solid dotted dashed".split(" ");
    public static String[] FONTSIZES = "4 5 6 7 8 9 10 11 12 13 14 15 16 18 20 22 24 26 28 32 36 40 44 48 54 60 66 72 78 84 90 96 108 120 132 144 168 192"
            .split(" ");
    public static String[] THICKNESSES = "0.0 0.01 0.02 0.05 0.1 0.2 0.5 1.0 1.5 2.0 3.0 5.0 8.0 10.0 15.0 20.0 30.0 50.0 80.0 100.0".split(" ");
    public static String[] INTERLINES = "0.5 0.6 0.7 0.8 0.9 0.95 1.0 1.1 1.2 1.3 1.4 1.5 1.6 1.7 1.8 1.9 2.0".split(" ");
    public static String[] CHARSPACES = "-1.0 -0.9 -0.8 -0.7 -0.6 -0.5 -0.4 -0.3 -0.2 -0.1 0.0 0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1.0".split(" ");
    public static int[] ZOOMS =
            {5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200, 250, 300, 350, 400, 450, 500, 600, 700, 800, 900, 1000, 1250, 1500, 1750, 2000};
    public static int FOOTER_HEIGHT = 30;

    public static Color ORANGE_FILL = new Color(0.7, 0.2, 0, 0.1);
    public static Color ORANGE_STROKE = new Color(1, 0.4, 0, 0.5);
    public static Color SMOKED_GLASS = new Color(0.0, 0.0, 0.0, 0.1);
    public static Color DARK_GLASS = new Color(0.0, 0.0, 0.0, 0.4);
    public static Color GREEN_DARK = Color3.GREEN_DARK.fx();

    public static Color3 SC_BLUE = Color3.SC_BLUE;
    public static Color3 INTERACTOR_COLOR = Color3.SC_BLUE.alpha(0.2);
    public static Color3 HIGHLIGHTED_COLOR = Color3.ORANGE.alpha(0.5);
    public static Color3 SELECTED_COLOR = Color3.ORANGE_RED.alpha(0.5);
    public static Color3 RED = Color3.RED;
    public static Color3 ORANGE = Color3.ORANGE_RED;
    public static Color3 GREEN_LIGHT = new Color3(0xff99cc66);
    public static Color3 ORANGE_LIGHT = new Color3(0xfff19941);
    public static Color3 WHITE_DUST = Color3.DUST_WHITE;
    public static String GUI_BLUE = "#4682B4";
    public static String DARK_RGB = "rgb(50,50,50)";
    public static String BLACK_RGB = "rgb(31,31,31)";
    public static String DARK_BG_STYLE = "-fx-background-color:" + DARK_RGB + ";";
    public static String BLACK_BG_STYLE = "-fx-background-color:" + BLACK_RGB + ";";
    public static String GRAY_BASE_STYLE = "-fx-base: rgb(120,120,120);";
    public static String DUST_TEXT_STYLE = "-fx-text-fill:rgb(240,240,240);";
    public static String THUMB_PAD_STYLE = "-fx-padding: 8px 8px 8px 8px;";

    public static int ICON_SIZE = 24;

    // public interface FileProcessable
    // {
    // public boolean process(FxEnvironment env, File3 file,
    // Worker3.Listener<File3>... listeners);
    // }

    public static void InsightCSS(Parent pane)
    {
        Fx.AddCSS(pane, IS.class, "Insight.css");
    }

    public static void DarkPane(Parent pane, boolean insight)
    {
        if(pane!=null)
        {
            pane.setStyle(DARK_BG_STYLE + GRAY_BASE_STYLE);
            if (insight)
                InsightCSS(pane);
        }
        else
            Log.warn(IS.class, ".DarkPane - null pane");
    }

    public static void Darky(Parent pane, Parent root)
    {
        InsightCSS(pane);
        DarkPane(root, false);
    }

    public static FxButton FooterBt(String text, EventHandler<ActionEvent> handler)
    {
        FxButton bt = new FxButton(text).fontSize(14).size(FOOTER_HEIGHT, FOOTER_HEIGHT);
        bt.setOnAction(handler);
        return bt;
    }

    public static FxButton FooterBt(Icon icon, EventHandler<ActionEvent> handler)
    {
        FxButton bt = new FxButton(icon.get(20)).size(FOOTER_HEIGHT, FOOTER_HEIGHT);
        bt.setOnAction(handler);
        return bt;
    }

    public static void Log(FxFinalEnvironment tab, String msg, Boolean happy)
    {
        if (tab == null)
            Log.msg(tab, msg, happy);
        else
            tab.message(msg, 1, happy);
    }

    public static int ZoomIndex(double scale)
    {
        int index = 0;
        double min = Integer.MAX_VALUE;
        double d;
        for (int i = 0; i < IS.ZOOMS.length; i++)
            if ((d = Math.abs(IS.ZOOMS[i] / 100f - scale)) < min)
            {
                min = d;
                index = i;
            }
        return index;
    }

}
