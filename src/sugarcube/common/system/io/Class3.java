package sugarcube.common.system.io;

import sugarcube.common.system.log.Log;
import sugarcube.common.ui.gui.icons.ImageIcon3;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class Class3 {
    public final Class type;

    public Class3(Object o) {
        if (o instanceof String)
            this.type = Forname((String) o);
        else if (o instanceof Class)
            this.type = (Class) o;
        else
            this.type = o.getClass();
    }

    public Class3(Class type) {
        this.type = type;
    }

    public Class3(String path) {
        this.type = Forname(path);
    }

    public String name() {
        return type.getSimpleName();
    }

    public String name(String extension) {
        return File3.Extense(name(), extension);
    }

    public boolean exists() {
        return type != null;
    }

    public boolean exists(String filename) {
        return type.getResource(filename) != null;
    }

    public String externalForm(Object o, String filename) {
        URL url = Url(o, filename);
        return url == null ? "" : url.toExternalForm();
    }

    public URL url(String filename) {
        return type == null ? null : type.getResource(filename);
    }

    public String path(String filename) {
        return type.getResource(filename).toExternalForm();
    }

    public byte[] bytes(String filename) {
        return IO.ReadBytes(stream(filename));
    }

    public String string(String filename) {
        return IO.ReadText(stream(filename));
    }

    public String[] lines(String filename) {
        return IO.ReadLines(stream(filename));
    }

    public InputStream stream(String filename) {
        if (type == null)
            return null;
        try {
            return type.getResourceAsStream(filename);
        } catch (Exception e) {
            Log.debug(this, ".stream - " + e.getMessage());
            return null;
        }
    }

    public ImageIcon3 icon(String filename) {
        return new ImageIcon3(type, filename);
    }

    public static String Path(Object o, String filename) {
        return (new Class3(o)).url(filename).toExternalForm();
    }

    public static Class Forname(String path) {
        try {
            return Class.forName(path);
        } catch (ClassNotFoundException e) {
            Log.debug(Class3.class, " - not found: " + path);
            e.printStackTrace();
        }
        return null;
    }

    public static String[] Lines(Object o, String filename) {
        return Class3.Get(o).lines(filename);
    }

    public static InputStream Stream(Object o, String filename) {
        return Class3.Get(o).stream(filename);
    }

    public static InputStreamReader Reader(Object o, String filename) {
        try {
            return new InputStreamReader(Stream(o, filename), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStreamReader BufferedReader(Object o, String filename) {
        try {
            return new InputStreamReader(Stream(o, filename), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] Bytes(Object o, String filename) {
        return Class3.Get(o).bytes(filename);
    }

    public static String String(Object o, String filename) {
        return Class3.Get(o).string(filename);
    }

    public static String Html(Object o) {
        Class3 cls = Class3.Get(o);
        return cls.string(cls.name(".html"));
    }

    public static URL Url(Object o, String filename) {
        return (new Class3(o)).url(filename);
    }

    public static URL Url(Object o, String filename, String ext) {
        String prefix = filename.contains("/") ? filename.substring(0, filename.lastIndexOf('/') + 1) : "";
        String url = prefix + File3.Filename(filename, true) + (ext.startsWith(".") ? ext : "." + ext);
        return (new Class3(o)).url(url);
    }

    public static Class3 Get(Object o) {
        return new Class3(o);
    }

    // if (cls == String.class)
    // a1[a0.length] = node.props.get(key, "");
    // else if (cls == Double.class || cls == double.class)
    // a1[a0.length] = (double) node.props.real(key, 0);
    // else if (cls == Float.class || cls == float.class)
    // a1[a0.length] = node.props.real(key, 0);
    // else if (cls == Integer.class || cls == int.class)
    // a1[a0.length] = node.props.integer(key, 0);
    // else if (cls == Boolean.class || cls == boolean.class)
    // a1[a0.length] = node.props.bool(key, false);

    public static boolean isString(Class cls) {
        return cls == String.class;
    }

    public static boolean isDouble(Class cls) {
        return cls == Double.class || cls == double.class;
    }

    public static boolean isFloat(Class cls) {
        return cls == Float.class || cls == float.class;
    }

    public static boolean isInteger(Class cls) {
        return cls == Integer.class || cls == int.class;
    }

    public static boolean isBoolean(Class cls) {
        return cls == Boolean.class || cls == boolean.class;
    }
}
