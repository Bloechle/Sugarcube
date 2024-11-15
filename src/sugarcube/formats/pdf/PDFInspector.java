package sugarcube.formats.pdf;

import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.system.Prefs;
import sugarcube.formats.pdf.reader.gui.PDFInspectorGUI;

public class PDFInspector implements Unjammable
{
    static
    {
        Prefs.Need();
    }

    public static void main(String... args)
    {
        PDFInspectorGUI.main(args);
    }
}
