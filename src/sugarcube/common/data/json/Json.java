package sugarcube.common.data.json;

import javafx.beans.property.BooleanProperty;
import sugarcube.common.system.reflection.Annot._Json;
import sugarcube.common.system.log.Log;
import sugarcube.common.system.util.Sys;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Property3;
import sugarcube.common.data.collections.Props;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.data.json.parser.JsonParser;
import sugarcube.common.data.json.parser.ParseException;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;
import sugarcube.common.system.reflection.ClassField;
import sugarcube.common.system.reflection.Reflect;
import sugarcube.common.ui.fx.beans.PBool;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class Json
{
    public static Set3<Class> RAW_TYPES = new Set3<>(String.class, float.class, Float.class, double.class, Double.class, int.class, Integer.class, boolean.class, Boolean.class,
            PBool.class);
    public static boolean NO_NULL = true;
    public static String INDENT = "  ";
    public static final String EXT = ".json";

    public interface Able
    {
        String toJson(String indent);

        void writeJson(Writer out, String indent) throws IOException;
    }

    public static JsonMap Create(Object... keyValueList)
    {
        JsonMap obj = new JsonMap();
        if (keyValueList.length == 1)
        {
            Object bean = keyValueList[0];
            for (Field field : Json.Fields(bean))
            {
                Object value = Reflect.Get(field, bean, null);
                obj.put(Key(field), Jsonize(value));
            }
        } else
        {
            for (int i = 0; i + 1 < keyValueList.length; i += 2)
                obj.put(keyValueList[i].toString(), keyValueList[i + 1]);
            if (keyValueList.length % 2 != 0)
                obj.put(Property3.EMPTY_KEY, keyValueList[keyValueList.length - 1]);
        }
        return obj;
    }

    public static JsonMap Read(File file)
    {
        File3 file3 = new File3(file);
        Reader reader = file3.reader();
        JsonMap obj = Read(reader);
        IO.Close(reader);
        return obj;
    }

    public static JsonMap Read(Reader in)
    {
        JsonParser parser = new JsonParser();
        try
        {
            Object obj = parser.parse(in);
            return obj instanceof JsonMap ? (JsonMap) obj : null;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static Props ReadProps(String json)
    {
        JsonMap obj = Read(json);
        return obj == null ? new Props() : obj.props();
    }

    public static Object ReadObject(String json)
    {
        JsonParser parser = new JsonParser();
        json = json == null ? "" : json.trim();
        if (!json.isEmpty())
            try
            {
                return parser.parse(json);
            } catch (ParseException e)
            {
                e.printStackTrace();
            }
        return null;
    }

    public static JsonArray ReadArray(String json)
    {
        Object obj = ReadObject(json);
        return obj instanceof JsonArray ? (JsonArray) obj : new JsonArray();
    }

    public static JsonMap Read(String json)
    {
        Object obj = ReadObject(json);
        return obj instanceof JsonMap ? (JsonMap) obj : new JsonMap();
    }

    public static void WriteJson(Map map, Writer out, String indent) throws IOException
    {
        if (map == null)
        {
            out.write("null");
            return;
        }

        boolean lineFeed = true;
        if (indent == null)
        {
            lineFeed = false;
            indent = "";
        }

        boolean first = true;
        Iterator iter = map.entrySet().iterator();

        out.write('{');
        String newIndent = indent + INDENT;
        if (lineFeed)
            out.write('\n');
        out.write(newIndent);
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            Object value = entry.getValue();
            if (value == null && NO_NULL)
                continue;
            if (first)
                first = false;
            else
            {
                out.write(',');
                if (lineFeed)
                    out.write('\n');
                out.write(newIndent);
            }

            out.write('\"');
            out.write(Escape(String.valueOf(entry.getKey())));
            out.write('\"');
            out.write(" : ");
            WriteJson(value, out, lineFeed ? null : newIndent);
        }
        if (lineFeed)
            out.write('\n');
        out.write(indent);
        out.write('}');
    }

    public static String ToJson(Map map, String indent)
    {
        final StringWriter writer = new StringWriter();
        try
        {
            Json.WriteJson(map, writer, indent);
            return writer.toString();
        } catch (IOException e)
        {
            // This should never happen with a StringWriter
            throw new RuntimeException(e);
        }
    }

    public static void WriteJson(Object value, Writer out, String indent) throws IOException
    {
        if (value == null)
        {
            out.write("null");
            return;
        }

        if (value instanceof String)
        {
            out.write('\"');
            out.write(Escape((String) value));
            out.write('\"');
            return;
        }

        if (value instanceof Double)
        {
            if (((Double) value).isInfinite() || ((Double) value).isNaN())
                out.write("null");
            else
                out.write(value.toString());
            return;
        }

        if (value instanceof Float)
        {
            if (((Float) value).isInfinite() || ((Float) value).isNaN())
                out.write("null");
            else
                out.write(value.toString());
            return;
        }

        if (value instanceof Number)
        {
            out.write(value.toString());
            return;
        }

        if (value instanceof Boolean)
        {
            out.write(value.toString());
            return;
        }

        if (value instanceof BooleanProperty)
        {
            out.write(((BooleanProperty) value).getValue().toString());
            return;
        }

        if ((value instanceof Json.Able))
        {
            out.write(((Json.Able) value).toJson(indent));
            return;
        }

        if (value instanceof Map)
        {
            Json.WriteJson((Map) value, out, indent);
            return;
        }

        if (value instanceof Collection)
        {
            JsonArray.writeJson((Collection) value, out, indent);
            return;
        }

        if (value instanceof byte[])
        {
            JsonArray.writeJson((byte[]) value, out);
            return;
        }

        if (value instanceof short[])
        {
            JsonArray.writeJson((short[]) value, out);
            return;
        }

        if (value instanceof int[])
        {
            JsonArray.writeJson((int[]) value, out);
            return;
        }

        if (value instanceof long[])
        {
            JsonArray.writeJson((long[]) value, out);
            return;
        }

        if (value instanceof float[])
        {
            JsonArray.writeJson((float[]) value, out);
            return;
        }

        if (value instanceof double[])
        {
            JsonArray.writeJson((double[]) value, out);
            return;
        }

        if (value instanceof boolean[])
        {
            JsonArray.writeJson((boolean[]) value, out);
            return;
        }

        if (value instanceof char[])
        {
            JsonArray.writeJson((char[]) value, out);
            return;
        }

        if (value instanceof Object[])
        {
            JsonArray.writeJson((Object[]) value, out, indent);
            return;
        }

        out.write(value.toString());
    }

    public static boolean IsRawType(Object obj)
    {
        return IsRawType(obj.getClass());
    }

    public static boolean IsRawType(Class type)
    {
        return RAW_TYPES.has(type);
    }

    public static String Quote(String s)
    {
        return "\"" + s + "\"";
    }

    public static String QuoteEscape(String s)
    {
        return "\"" + Escape(s) + "\"";
    }

    /**
     * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters
     * (U+0000 through U+001F).
     */
    public static String Escape(String s)
    {
        if (s == null)
            return null;
        StringBuffer sb = new StringBuffer();
        Escape(s, sb);
        return sb.toString();
    }

    public static void Escape(String s, StringBuffer sb)
    {
        final int len = s.length();
        for (int i = 0; i < len; i++)
        {
            char ch = s.charAt(i);
            switch (ch)
            {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
//                case '/':
//                    sb.append("\\/");
//                    break;
                default:
                    // Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if ((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF'))
                    {
                        String hex = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - hex.length(); k++)
                            sb.append('0');
                        sb.append(hex.toUpperCase());
                    } else
                        sb.append(ch);
            }
        }
    }

    public static Field Field(String name, Object obj)
    {
        try
        {
            Class cls = obj.getClass();
            do
            {
                Field field = cls.getDeclaredField(name);
                if (field != null)
                    return field;
            } while ((cls = cls.getSuperclass()) != null);
            return null;
        } catch (Exception e)
        {
        }
        return null;
    }

    public static Field[] Fields(Object obj)
    {
        List3<Field> fields = new List3<>();
        if (obj != null)
        {
            int mods;
            Class cls = obj.getClass();
            do
            {
                for (Field field : cls.getDeclaredFields())
                {
                    _Json annot = field.getAnnotation(_Json.class);
                    boolean isJson = annot != null;
                    boolean isHidden = isJson && annot.hide();
                    if (!isHidden)
                        if (isJson || Modifier.isPublic(mods = field.getModifiers()) && !Modifier.isStatic(mods) && !Modifier.isTransient(mods)
                                && !Modifier.isFinal(mods))
                            fields.add(field);
                }
            } while ((cls = cls.getSuperclass()) != null);
        }
        return fields.toArray(new Field[0]);
    }

    public static String Stringify(Object obj)
    {
        return (Jsonize(obj)).toString();
    }

    public static Object Jsonize(Object obj)
    {
        if (obj == null)
            return null;
        Class type = obj.getClass();

        if (type.isArray())
        {
            JsonArray jsonArray = new JsonArray();
            int size = Array.getLength(obj);

            for (int i = 0; i < size; i++)
            {
                Object o = Array.get(obj, i);
                // Log.debug(Json.class,
                // ".jsonize array["+size+"] - obj["+i+"]="+o+", obj="+obj);
                jsonArray.add(Jsonize(o));
            }
            return jsonArray;
        } else if (IsRawType(type))
        {
            return obj;
        } else if (obj instanceof Map)
        {
            JsonArray jsonArray = new JsonArray();
            Map map = (Map) obj;
            for (Object key : map.keySet())
            {
                jsonArray.add(Jsonize(map.get(key)));
            }
            return jsonArray;
        } else if (obj instanceof List)
        {
            JsonArray jsonArray = new JsonArray();
            for (Object o : (List) obj)
            {
                jsonArray.add(Jsonize(o));
            }
            return jsonArray;
        } else
        {
            JsonMap jsonObj = new JsonMap();
            for (Field field : Json.Fields(obj))
            {
                Object value = Reflect.Get(field, obj, null);
                jsonObj.put(Key(field), Jsonize(value));
            }
            return jsonObj;
        }
    }

    public static void Populate(String json, Object obj)
    {
        JsonMap jsonMap = Json.Read(json);
        Populate(jsonMap, obj);
    }

    public static void Populate(Object json, Object obj, Field... fields)
    {

        if (json instanceof JsonArray && fields.length > 0 && fields[0].getType().isArray())
        {
            JsonArray jsonArray = (JsonArray) json;
            Field field = fields[0];
            try
            {
                int size = jsonArray.size();
                int dim = 1;
                Class arrayCls = field.getType().getComponentType();
                Class cls = arrayCls;
                while (cls.isArray())
                {
                    cls = cls.getComponentType();
                    dim++;
                }

                Object array = Array.newInstance(arrayCls, size);
                if (dim == 1)
                    for (int i = 0; i < size; i++)
                    {
                        Object val = jsonArray.get(i);
                        if (IsRawType(arrayCls))
                            Array.set(array, i, ClassField.cast(val, arrayCls));
                        else if (val instanceof JsonArray)
                        {
                            Log.debug(Json.class, ".populate - nested array 2D");
                        } else if (val instanceof JsonMap)
                        {
                            Object instance = Reflect.Instance(arrayCls);
                            Array.set(array, i, instance);
                            Populate((JsonMap) val, instance);
                        }
                    }
                else if (dim == 2)
                    for (int i = 0; i < size; i++)
                    {
                        JsonArray jsonArray2 = (JsonArray) jsonArray.get(i);
                        int size2 = jsonArray2.size();
                        Object array2 = Array.newInstance(cls, size2);
                        for (int j = 0; j < size2; j++)
                        {

                            Object val = jsonArray2.get(j);
                            // Log.debug(Json.class, ".populate - elem=" + elem +
                            // ", arrayType=" + arrayType);
                            if (IsRawType(cls))
                            {
                                Array.set(array2, j, ClassField.cast(val, cls));
                            } else if (val instanceof JsonArray)
                            {
                                Log.debug(Json.class, ".populate - nested array 2D");
                            } else if (val instanceof JsonMap)
                            {
                                Object instance = Reflect.Instance(cls);
                                Array.set(array2, j, instance);
                                Populate((JsonMap) val, instance);
                            }
                        }
                        Array.set(array, i, array2);
                    }
                else
                    Log.debug(Json.class, ".populate - arrays having more than 2 dimensions are not yet implemented");
                Reflect.Set(field, obj, array);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

        } else if (json instanceof JsonMap)
        {
            JsonMap jsonObj = (JsonMap) json;
            for (Field field : fields == null || fields.length == 0 ? Json.Fields(obj) : fields)
            {
                Class cls = field.getType();
                String key = Key(field);
                Object value = jsonObj.get(key);
                if (value != null)
                {
                    if (IsRawType(cls))
                        Reflect.Set(field, obj, value);
                    else if (value instanceof JsonArray)
                    {
                        Populate(value, obj, field);
                    } else if (value instanceof JsonMap)
                    {
                        Object instance = Reflect.Get(field, obj, null);
                        if (instance == null)
                        {
                            instance = Reflect.Instance(cls);
                            Reflect.Set(field, obj, instance);
                        }
                        Populate((JsonMap) value, instance);
                    } else
                        Log.debug(Json.class, ".populate - json does not match object fields: json=" + json + ", object=" + obj + ", value=" + value);
                }
            }
        }
    }

    public static String Key(Field field)
    {
        String key = field.getName();
        _Json annot = (_Json) field.getAnnotation(_Json.class);
        if (annot != null && annot.key() != null && !annot.key().isEmpty())
            key = annot.key();
        return key;
    }

    public static String Get(String[] array)
    {
        String json = "[";
        for (int i = 0; i < array.length; i++)
            json += QuoteEscape(array[i]) + ((i < array.length - 1) ? "," : "");
        return json + "]";
    }

    public static String Get(String[][] table)
    {
        String json = "[";
        for (int i = 0; i < table.length; i++)
            json += Get(table[i]) + ((i < table.length - 1) ? "," : "");
        return json + "]";
    }

    public static String Get(String[] table, String name)
    {
        return "{" + QuoteEscape(name) + ":" + Get(table) + "}";
    }

    public static String Get(String[][] table, String name)
    {
        return "{" + QuoteEscape(name) + ":" + Get(table) + "}";
    }

    public static String Map(String... pairs)
    {
        StringBuilder json = new  StringBuilder(pairs.length*25);
        json.append("{");
        for (int i = 0; i < pairs.length; i += 2)
            json.append(QE(pairs[i])).append(":").append(QE(pairs[i + 1])).append(i < pairs.length - 2 ? "," : "");

        return json + "}";
    }

    public static String Map(HashMap<String, String> props)
    {
        int size = props.size();
        StringBuilder json = new StringBuilder(props.size()*50);
        json.append("{");
        int index = 0;
        for (String key : props.keySet())
            json.append(QE(key)).append(":").append(QE(props.get(key))).append((index++ < size - 1 ? ",\n" : ""));
        return json.append("}").toString();
    }

    private static String QE(String s)
    {
        return QuoteEscape(s);
    }

    public static void main(String... args)
    {
        Sys.Println("json=" + Get(new String[][]
                {
                        {"titi", "to\"o", "tata"},
                        {"a", "b", "c"}}, "bou"));
    }
}
