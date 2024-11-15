package sugarcube.common.system;

import com.sun.media.imageioimpl.plugins.tiff.TIFFImageReaderSpi;
import com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriterSpi;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Props;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.system.io.File3;
import sugarcube.common.data.xml.Nb;
import sugarcube.common.ui.gui.Font3;
import sugarcube.formats.pdf.resources.fonts.FONTS;

import javax.imageio.spi.IIORegistry;
import java.awt.geom.Point2D;
import java.util.prefs.Preferences;

public class Prefs implements Unjammable
{
    static
    {
        try
        {
            System.setProperty("file.encoding", "UTF-8");
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        IIORegistry registry = IIORegistry.getDefaultInstance();
        registry.registerServiceProvider(new TIFFImageWriterSpi());
        registry.registerServiceProvider(new TIFFImageReaderSpi());
        // registry.registerServiceProvider(new
        // com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageWriterSpi());
        // registry.registerServiceProvider(new
        // com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageReaderSpi());

        Font3.CLASSES.add(FONTS.class);
    }

    public static final void Need()
    {

    }

    public static final String FOLDER = ".sugarcube/";
    public static final File3 SC_DIR = File3.home(FOLDER);
    public static final File3 TMP_DIR = File3.home(FOLDER + "tmp/");

    public static final String ID = "id";
    public static final String WINDOW_BOX = "window-box";
    public static final String RECENT_FILES = "recent-files";
    public static final String LAST_PAGE = "last-page";
    public static final String UNIT = "unit";
    public static final String ZOOM = "zoom";
    public static final String LEFT_SIDE_WIDTH = "left-side-width";
    public static final String OCR_HOTFOLDER = "ocr-hotfolder";

    public static float screenDpi = 72;

    static
    {
        try
        {
            SC_DIR.needDirs(true);
            TMP_DIR.needDirs(true);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private Preferences prefs;
    private int defMaxFiles = 10;

    public Prefs(Preferences prefs)
    {
        this.prefs = prefs;
    }

    public Prefs set(boolean read, String key, CheckBox check)
    {
        return read ? read(key, check) : store(key, check);
    }

    public Prefs set(boolean read, String key, TextField field)
    {
        return read ? read(key, field) : store(key, field);
    }

    public Prefs set(boolean read, String key, Slider slider)
    {
        return read ? read(key, slider) : store(key, slider);
    }

    public Prefs set(boolean read, String key, Spinner spinner)
    {
        return read ? read(key, spinner) : store(key, spinner);
    }

    public Prefs read(String key, CheckBox check)
    {
        check.setSelected(bool(key, check.isSelected()));
        return this;
    }

    public Prefs read(String key, TextField field)
    {
        field.setText((get(key, field.getText())));
        return this;
    }

    public Prefs read(String key, Slider slider)
    {
        slider.setValue(real(key, slider.getValue()));
        return this;
    }

    public Prefs read(String key, Spinner spinner)
    {
        // if (spinner.getValue() instanceof Integer)
        // spinner.val().setValue((Integer)integer(key, (Integer)
        // spinner.getValue()));
        // else if (spinner.getValue() instanceof Double)
        // spinner.valueFactoryProperty().setValue((Double)(double)real(key,
        // (Double) spinner.getValue()));
        // else
        Log.warn(this, ".read - spinner type not yet implemented: key=" + key + ", value=" + spinner);
        return this;
    }

    public Prefs store(String key, CheckBox check)
    {
        return store(key, check.isSelected());
    }

    public Prefs store(String key, TextField field)
    {
        return store(key, field.getText());
    }

    public Prefs store(String key, Slider slider)
    {
        return store(key, slider.getValue());
    }

    public Prefs store(String key, Spinner spinner)
    {
        return store(key, spinner.getValue());
    }

    public Prefs store(String key, Object value)
    {
        put(key, value);
        return this;
    }

    public Prefs putInts(String key, int... values)
    {
        for (int i = 0; i < values.length; i++)
            prefs.putInt(key + "[" + i + "]", values[i]);
        return this;
    }

    public int[] ints(String key, int size)
    {
        int[] values = new int[size];
        for (int i = 0; i < values.length; i++)
            values[i] = prefs.getInt(key + "[" + i + "]", 0);
        return values;
    }

    public boolean has(String key)
    {
        return !Str.IsVoid(get(key, null));
    }

    public File3 file(String key, File3 def)
    {
        String file = get(key, null);
        return Str.IsVoid(file) ? def : File3.Get(file);
    }

    public int integer(String key, int def)
    {
        String val = get(key, null);
        return val == null ? def : Nb.Int(val, def);
    }

    public double real(String key, double def)
    {
        String val = get(key, null);
        return val == null ? def : Nb.Double(val, def);
    }

    public Color3 color(String key, Color3 def)
    {
        return Color3.ParseCss(get(key, null), def);
    }

    public boolean bool(String key, boolean def)
    {
        return Nb.Bool(get(key, null), def);
    }

    public Prefs putColor(String key, Color3 color)
    {
        return put(key, color == null ? null : color.cssRGBAValue());
    }

    public String get(String key, String def)
    {
        return prefs == null ? def : prefs.get(key, def);
    }

    public String get(String key)
    {
        return get(key, null);
    }


    public Prefs putUnit(String key, String unit)
    {
        return put(UNIT, unit);
    }

    public String unit(String def)
    {
        return prefs == null ? def : prefs.get(UNIT, def);
    }

    public Prefs putPoint(String key, Point2D p)
    {
        if (prefs != null)
        {
            prefs.putDouble(key + "_x", p.getX());
            prefs.putDouble(key + "_y", p.getY());
        }
        return this;
    }

    public Point3 point(String key)
    {
        if (prefs != null)
        {
            double x = prefs.getDouble(key + "_x", -1);
            double y = prefs.getDouble(key + "_y", -1);
            return new Point3(x, y);
        } else
            return null;
    }

    public Prefs putDimension(String key, Point2D p)
    {
        if (prefs != null)
        {
            prefs.putDouble(key + "_w", p.getX());
            prefs.putDouble(key + "_h", p.getY());
        }
        return this;
    }

    public Point3 dimension(String key)
    {
        if (prefs != null)
        {
            double x = prefs.getDouble(key + "_w", -1);
            double y = prefs.getDouble(key + "_h", -1);
            return new Point3(x, y);
        } else
            return null;
    }

    public Line3 line(String key)
    {
        if (prefs != null)
        {
            double x1 = prefs.getDouble(key + "_x1", -1);
            double y1 = prefs.getDouble(key + "_y1", -1);
            double x2 = prefs.getDouble(key + "_x2", -1);
            double y2 = prefs.getDouble(key + "_y2", -1);
            return new Line3(x1, y1, x2, y2);
        } else
            return null;
    }

    public boolean has(String... keys)
    {
        if (prefs == null)
            return false;
        for (String key : keys)
            if (prefs.get(key, null) == null)
                return false;
        return true;
    }

    public Prefs putLine(String key, Line3 l)
    {
        if (prefs != null)
        {
            prefs.putDouble(key + "_x1", l.x1);
            prefs.putDouble(key + "_y1", l.y1);
            prefs.putDouble(key + "_x2", l.x2);
            prefs.putDouble(key + "_y2", l.y2);
        }
        return this;
    }

    public Prefs putBox(String key, Rectangle3 r)
    {
        if (prefs != null)
        {
            prefs.putDouble(key + "_x", r.x());
            prefs.putDouble(key + "_y", r.y());
            prefs.putDouble(key + "_w", r.width());
            prefs.putDouble(key + "_h", r.height());
        }
        return this;
    }

    public Rectangle3 box(String key)
    {
        if (prefs != null)
        {
            double x = prefs.getDouble(key + "_x", -1);
            double y = prefs.getDouble(key + "_y", -1);
            double w = prefs.getDouble(key + "_w", -1);
            double h = prefs.getDouble(key + "_h", -1);
            return new Rectangle3(x, y, w, h);
        } else
            return null;
    }

    public Prefs put(String key, Object value)
    {
        if (prefs == null)
            return this;
        else if (value == null)
            prefs.remove(key);
        else
            prefs.put(key, value.toString());
        return this;
    }

    public Prefs putWindowBox(Rectangle3 r)
    {
        return this.putBox(WINDOW_BOX, r);
    }

    public Rectangle3 windowBox()
    {
        return box(WINDOW_BOX);
    }

    public Rectangle3 windowBox(int minWidth, int minHeight)
    {
        Rectangle3 box = box(WINDOW_BOX);
        if (box.width > -1 && box.width < minWidth)
            box.width = minWidth;
        if (box.height > -1 && box.height < minHeight)
            box.height = minHeight;
        return box;
    }

    public StringSet recentFiles(int max)
    {
        if (max <= 0)
            max = this.defMaxFiles;
        StringSet paths = new StringSet();
        if (prefs != null)
            for (int i = 0; i < max; i++)
                paths.addNotNull(recentFile(i, null));
        return paths;
    }

    public StringSet recentExistingFiles(int max)
    {
        if (max <= 0)
            max = this.defMaxFiles;
        StringSet paths = new StringSet();
        if (prefs != null)
            for (int i = 0; i < max; i++)
                if (File3.Exists(recentFile(i, null)))
                    paths.add(recentFile(i, null));
        return paths;
    }

    public boolean removeRecentFiles(String... filePaths)
    {
        if (prefs == null)
            return false;
        StringSet files = new StringSet(filePaths);
        StringSet paths = new StringSet();

        for (String path : recentFiles(-1))
            if (!files.has(path))
                paths.add(path);

        String[] array = paths.array();
        for (int i = 0; i < array.length; i++)
            if (array[i] != null)
                prefs.put(RECENT_FILES + "_" + i, array[i]);

        return true;
    }

    public Prefs putLastFile(String path)
    {
        return this.putLastFile(path, "", 10);
    }

    public Prefs putLastFile(String path, String pageName, int max)
    {
        if (prefs == null)
            return this;
        if (max <= 0)
            max = this.defMaxFiles;
        StringSet paths = new StringSet();
        paths.add(path);
        paths.addAll(recentFiles(max));
        String[] array = paths.array();
        for (int i = 0; i < max && i < array.length; i++)
            if (array[i] != null)
                prefs.put(RECENT_FILES + "_" + i, array[i]);
        if (Str.HasData(pageName))
            prefs.put(LAST_PAGE, pageName);
        return this;
    }

    public Prefs setLastFiles(String... paths)
    {
        if (prefs != null)
        {
            for (int i = 0; i < paths.length; i++)
                if (paths[i] != null)
                    prefs.put(RECENT_FILES + "_" + i, paths[i]);
            for (int i = paths.length; i <= 100; i++)
                prefs.remove(RECENT_FILES + "_");
        }
        return this;
    }

    public String lastFile(String def)
    {
        return recentFile(0, def);
    }

    public String recentFile(int index, String def)
    {
        return prefs == null ? def : prefs.get(RECENT_FILES + "_" + index, def);
    }

    public File3 lastExistingFile()
    {
        if (prefs != null)
            for (int i = 0; i < 20; i++)
                if (File3.Exists(recentFile(i, null)))
                    return File3.Get(recentFile(i, null));
        return null;
    }

    public File3 lastExistingDir()
    {
        File3 file = lastExistingFile();
        return file == null ? null : file.directory();
    }

    public String lastPage(String def)
    {
        return prefs == null ? def : prefs.get(LAST_PAGE, def);
    }

    @Override
    public String toString()
    {
        return this.prefs.toString();
    }

    public static File3 Temp(String path)
    {
        if (!File3.Exists(TMP_DIR))
        {
            Log.warn(Prefs.class, ".Temp - tmp directory not found: " + TMP_DIR);
            File3.mkdirs(path);
        }
        return new File3(TMP_DIR, path);
    }

    public static Prefs Get(Object o)
    {
        return Get(o, null);
    }

    public static Prefs Get(Object o, String id)
    {
//        Log.debug(Prefs.class, ".Get - " + (o == null ? "null" : (o instanceof Class ? (Class) o : o.getClass()).getName()));
        Preferences pref = null;

        Class cls = o == null ? Prefs.class : (o instanceof Class ? (Class) o : o.getClass());

        if (Str.HasData(id))
        {
            String fullID = cls.getName() + "#" + id.replace("/", "_");
            Log.info(Prefs.class, ".Get - Preferences id: "+fullID);
            try
            {
                pref = Preferences.userRoot().node(fullID);
            } catch (Exception e)
            {
                Log.warn(Prefs.class, ".Get - Preferences.userRoot().node("+fullID+") not found: "+e.getMessage());
            }
            if (pref == null)
                try
                {
                    pref = Preferences.systemRoot().node(fullID);
                } catch (Exception e)
                {
                    Log.warn(Prefs.class, ".Get - Preferences.systemRoot("+fullID+") not found: "+e.getMessage());
                }
        }

        if (pref == null)
            try
            {
                pref = Preferences.userNodeForPackage(cls);
            } catch (Exception e)
            {
                Log.warn(Prefs.class, ".Get - Preferences.userNodeForPackage not found: "+e.getMessage());
            }
        if (pref == null)
            try
            {
                pref = Preferences.systemNodeForPackage(cls);
            } catch (Exception e)
            {
                Log.warn(Prefs.class, ".Get - Preferences.systemNodeForPackage not found: "+e.getMessage());
            }
        if (pref == null)
            try
            {
                pref = Preferences.userRoot();
            } catch (Exception e)
            {
                Log.warn(Prefs.class, ".Get - Preferences.userRoot not found: "+e.getMessage());
            }
        if (pref == null)
            try
            {
                pref = Preferences.systemRoot();
            } catch (Exception e)
            {
                Log.warn(Prefs.class, ".Get - Preferences.systemRoot not found: "+e.getMessage());
            }

        return new Prefs(pref);
    }

    public Props props()
    {
        Props props = new Props();
        try
        {
            for (String key : prefs.keys())
                props.put(key, prefs.get(key, ""));
        } catch (Exception e)
        {

        }
        return props;
    }

    public String json()
    {
        return props().toJson();
    }

}
