package sugarcube.insight.core;

import javafx.scene.Parent;
import javafx.scene.text.Font;
import sugarcube.common.system.log.Log;
import sugarcube.common.system.util.Sys;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.data.collections.Stringer;
import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.system.io.Class3;
import sugarcube.common.ui.fx.base.FxApp;
import sugarcube.common.ui.fx.base.FxFont;
import sugarcube.common.ui.fx.containers.FxBorderPane;
import sugarcube.common.ui.fx.containers.FxVBox;
import sugarcube.common.ui.fx.controls.FxLabel;
import sugarcube.common.ui.fx.controls.FxTextArea;
import sugarcube.common.ui.gui.Font3;
import sugarcube.formats.ocd.objects.font.SVGFont;

public class FxFonts implements Unjammable
{
    // palatino, myriad pro, caslon, lucida, calibri
    public static final String Bodoni = "Bodoni";
    public static final String BookmanOldStyle = "Bookman_Old_Style";
    public static final String Courier = "Courier";
    public static final String Garamond = "Garamond";
    public static final String Helvetica = "Helvetica";
    public static final String Times = "Times_New_Roman";
    public static final String TwentyCentury = "TwentyCentury";
    public static final String Verdana = "Verdana";

    public static Font3 DEFAULT = Font3.CALIBRI_FONT;
    public static String[] NAMES = Sys.Fonts();

    protected static StringMap<Font3> map = new StringMap<>();

    static
    {
        Class3 cls = new Class3("sugarcube.formats.pdf.resources.fonts.FONTS");
        if (cls.exists())
            Font3.CLASSES.add(cls.type);
    }

    public static StringSet subset(boolean serif, boolean mono)
    {
        StringSet set = new StringSet();
        for (String name : NAMES)
            if (isSerif(name) == serif && isMono(name) == mono)
                set.add(name);
        return set;
    }

    public static boolean isMono(String name)
    {
        switch (SVGFont.FontFamily(rename(name)))
        {
            case Courier:
                return true;
        }
        return false;
    }

    public static boolean isSerif(String name)
    {
        switch (SVGFont.FontFamily(rename(name)))
        {
            case Bodoni:
            case Courier:
            case Garamond:
            case Times:
                return true;
        }
        return false;
    }

    public static String rename(String fontname)
    {
        fontname = fontname.replace(' ', '_').replace("Times_New_Roman", "Times");
        return fontname;
    }

    public Font3 font(String name)
    {
        Font3 font = map.get(name);
        if (font == null)
            map.put(name, font = new Font3(name));
        return font;
    }

    public static boolean has(String name)
    {
        return map.has(rename(name));
    }

    public static void main(String... args)
    {
        FxFontApp.launch(FxFontApp.class, args);
    }

    public static class FxFontApp extends FxApp
    {

        @Override
        public void init()
        {

        }

        public Parent fxInit()
        {
            FxTextArea area = new FxTextArea();
            FxVBox vBox = new FxVBox();
            FxBorderPane pane = new FxBorderPane(area);
            pane.setRight(vBox);

            Class3 cls = new Class3("sugarcube.formats.pdf.resources.fonts.FONTS");
            if (cls.exists())
                for (String name : NAMES)
                {
                    Font font = FxFont.load(cls.type, name, 24);

                    // font = FxFont.load(name, 24);
                    if (font == null)
                    {
                        Log.debug(this, ".start - null font: " + name);
                    }
                    FxLabel label = new FxLabel(name, font);
                    vBox.add(label);
                }

            Stringer sg = new Stringer();

            int i = 0;
            for (String family : Font.getFamilies())
            {
                sg.span(++i, ") family - ", family).br();
            }
            i = 0;
            sg.br2();
            for (String fontname : Font.getFontNames())
            {
                sg.span(++i, ") name - ", fontname).br();
            }

            area.setText(sg.toString());

            this.resizable(true);
            this.startWidth = 400;
            this.startHeight = 800;

            return pane;
        }
    }

}
