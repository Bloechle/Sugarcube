package sugarcube.common.data.collections;

import javafx.scene.control.CheckBox;
import sugarcube.common.data.Zen;
import sugarcube.common.data.Base64;
import sugarcube.common.data.json.Json;
import sugarcube.common.data.json.JsonArray;
import sugarcube.common.data.table.DataTable;
import sugarcube.common.interfaces.Valuable;
import sugarcube.common.system.io.File3;
import sugarcube.common.data.xml.*;
import sugarcube.common.data.xml.css.CSS;
import sugarcube.common.data.xml.css.CSSBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class Props extends Map3<String, String> implements XmlINode, Xmlizer, Valuable
{
    public static final String ID = "id";

    public static final String DEFAULT_SEPARATOR = "|";
    private boolean unfold = false;
    private boolean escAttr = true;
    private boolean escCData = false;

    public Props(String... keyValuePairs)
    {
        this(keyValuePairs.length / 2 + 1);
        this.putAll(keyValuePairs);
    }

    public Props(String[] keys, String[] values)
    {
        this(keys.length + 1);
        this.putAll(keys, values);
    }

    public Props(int initialCapacity)
    {
        super(initialCapacity);
        this.doUnfold(false);
        this.escapeCData(true);
    }

    public Props(Map<String, String> map)
    {
        this(map.size() + 1);
        this.putAll(map);
    }

    public String trim(String key)
    {
        String value = get(key);
        return value == null ? "" : value.trim();
    }

    public Props jsonProps(String key)
    {
        return Json.Read(trim(key)).props();
    }

    public String id()
    {
        return get(ID);
    }

    public Props reset(Props props)
    {
        this.clear();
        this.putAll(props);
        return this;
    }

    @Override
    public Props removeAll(String... keys)
    {
        super.removeAll(keys);
        return this;
    }

    public Props delete(String... keys)
    {
        return removeAll(keys);
    }

    public Props keep(String... keys)
    {
        StringSet set = new StringSet(keys);
        for (String key : this.keys())
            if (set.hasnt(key))
                this.remove(key);
        return this;
    }

    public StringList keyList8()
    {
        StringList list = new StringList();
        list.addAll(this.keySet());
        return list;
    }

    public String[] keys()
    {
        return this.keySet().toArray(new String[0]);
    }

    public Props clean(boolean emptyToo)
    {
        for (String key : keySet().toArray(new String[0]))
        {
            String value = get(key);
            if (value == null || (emptyToo && value.isEmpty()))
                this.remove(key);
        }
        return this;
    }

    public Props rename(String key, String newKey)
    {
        if (key != null && newKey != null && !key.equals(newKey) && this.has(key))
        {
            this.put(newKey, get(key));
            this.remove(key);
        }
        return this;
    }

    public boolean doFold()
    {
        return !unfold;
    }

    public boolean doUnfold()
    {
        return unfold;
    }

    public boolean escapeAttributes()
    {
        return escAttr;
    }

    public boolean escapeCData()
    {
        return escCData;
    }

    public Props doUnfold(boolean unfold)
    {
        this.unfold = unfold;
        return this;
    }

    public Props escapeAttributes(boolean esc)
    {
        this.escAttr = esc;
        return this;
    }

    public Props escapeCData(boolean esc)
    {
        this.escCData = esc;
        return this;
    }

    public Props escape(boolean attributes, boolean cdata)
    {
        this.escAttr = attributes;
        this.escCData = cdata;
        return this;
    }

    public Property3[] entries()
    {
        Property3[] props = new Property3[this.size()];
        int index = 0;
        for (Map.Entry<String, String> entry : this.entrySet())
            props[index++] = new Property3(this, entry.getKey(), entry.getValue());
        return props;
    }

    public Props set(String key, Object value)
    {
        put(key, value);
        return this;
    }

    public String put(String key, Object value)
    {
        if (value instanceof CheckBox)
            value = ((CheckBox) value).selectedProperty().get();
        return super.put(key, value == null ? null : value.toString());
    }

    public void putOrRemove(String key, Object value)
    {
        if (value == null)
            this.remove(key);
        else
            this.put(key, value);
    }

    public String put(Property3 property)
    {
        return this.put(property.key, property.value);
    }

    public String put64(String key, String value)
    {
        return this.put(key, Base64.encodeUrl(value));
    }

    public Props putAllWithPrefix(Map<String, String> map, String prefix)
    {
        for (Map.Entry<String, String> entry : map.entrySet())
            this.put(prefix + entry.getKey(), entry.getValue());
        return this;
    }

    public Props getAllWithPrefix(String prefix, boolean removePrefix)
    {
        Props props = new Props();
        for (Map.Entry<String, String> entry : entrySet())
            if (entry.getKey().startsWith(prefix))
                props.put(removePrefix ? entry.getKey().substring(prefix.length()) : entry.getKey(), entry.getValue());
        return props;
    }

    public Props putAll(String... keyValues)
    {
        for (int i = 0; i + 1 < keyValues.length; i += 2)
            this.put(keyValues[i], keyValues[i + 1]);
        if (keyValues.length % 2 != 0)
            this.put(Property3.EMPTY_KEY, keyValues[keyValues.length - 1]);
        return this;
    }

    public Props putAll(String[] keys, String[] values)
    {
        for (int i = 0; i < keys.length && i < values.length; i++)
            this.put(keys[i], values[i]);
        return this;
    }

    public Props putWithSplit(String keyValue, char separator)
    {
        int i = keyValue.indexOf(separator);
        put((i > 0 ? keyValue.substring(0, i) : keyValue).trim(), i > 0 ? keyValue.substring(i + 1).trim() : "");
        return this;
    }

    public void setEmptyValue(String value)
    {
        this.put(Property3.EMPTY_KEY, value);
    }

    public void putEmptyValue(String value)
    {
        this.put(Property3.EMPTY_KEY, value);
    }

    public String cdataValue(String def)
    {
        String cdata = this.get(Property3.EMPTY_KEY, null);
        if (cdata == null)
            this.get(Property3.CDATA, null);
        return cdata == null ? def : cdata;
    }

    public String emptyValue()
    {
        return this.get(Property3.EMPTY_KEY);
    }

    public boolean hasEmptyKey()
    {
        return this.containsKey(Property3.EMPTY_KEY);
    }

    public boolean containsEmptyKey()
    {
        return this.containsKey(Property3.EMPTY_KEY);
    }

    public boolean hasPairs(String... keyValues)
    {
        return keyValues.length == 0 || super.has(new Props(keyValues));
    }

    public boolean is(String key, String value)
    {
        return has(key) && Str.Equals(get(key), value);
    }

    public boolean isVoid(String key)
    {
        return Str.IsVoid(get(key));
    }

    public Props unvoid(String key, String def)
    {
        if (isVoid(key))
            this.put(key, def);
        return this;
    }

    public Props setList(String key, StringList list)
    {
        return setList(key, list, DEFAULT_SEPARATOR);
    }

    public Props setList(String key, StringList list, String separator)
    {
        if (list == null)
            this.remove(key);
        else
            this.put(key, list.toString(separator));
        return this;
    }

    public String[] array(String key, String separator)
    {
        String data = get(key, "");
        return Str.IsVoid(data) ? new String[0] : data.split("\\s*" + Pattern.quote(separator) + "\\s*");
    }

    public StringList list(String key)
    {
        return list(key, DEFAULT_SEPARATOR);
    }

    public StringList list(String key, String separator)
    {
        return new StringList(array(key, separator));
    }

    @Override
    public int integer(String key)
    {
        return intValue(key, 0);
    }

    public int integer(String key, int def)
    {
        return intValue(key, def);
    }

    public int intValue(String key, int def)
    {
        String value = get(key);
        return Zen.isVoid(value) ? def : Nb.Int(value, def);
    }

    public String get64(String key, String def)
    {
        return has(key) ? Base64.decodeUrlString(get(key, "")) : def;
    }

    @Override
    public String value(String key)
    {
        return get(key);
    }

    public String value(String key, String def)
    {
        return get(key, def);
    }

    public String string(String key, String def)
    {
        return this.get(key, def);
    }

    public String stringValue(String key, String def)
    {
        return this.get(key, def);
    }

    public String lower(String key, String def)
    {
        return has(key) ? get(key).toLowerCase() : def;
    }

    @Override
    public float real(String key)
    {
        return floatValue(key, 0);
    }

    public float real(String key, double def)
    {
        return floatValue(key, def);
    }

    public float realize(String key, double def)
    {
        String value = get(key);
        return Str.IsVoid(value) ? (float) def : (float) Nb.toDouble(true, value, def);
    }

    public float floatValue(String key, double def)
    {
        String value = get(key);
        return Str.IsVoid(value) ? (float) def : Nb.Float(value, def);
    }

    @Override
    public boolean bool(String key)
    {
        return booleanValue(key, false);
    }

    public boolean bool(String key, boolean def)
    {
        return booleanValue(key, def);
    }

    public boolean booleanValue(String key, boolean def)
    {
        String value = get(key);
        return Str.IsVoid(value) ? def : Nb.Bool(value, def);
    }

    public File3 file(String key)
    {
        return file(key, null);
    }

    public File3 file(String key, File3 def)
    {
        String value = get(key);
        return Str.IsVoid(value) ? def : File3.Get(value);
    }

    public String select(String key, String def, String... choices)
    {
        String v = get(key);
        for (String s : choices)
            if (Str.Equals(v, s))
                return v;
        return def;
    }

    public Props copy()
    {
        Props props = new Props(this);
        props.unfold = this.unfold;
        props.escCData = this.escCData;
        return props;
    }

    @Override
    public XmlINode parent()
    {
        throw null;
    }

    @Override
    public Collection<? extends XmlINode> children()
    {
        return doUnfold() ? Arrays.asList(entries()) : Collections.EMPTY_LIST;
    }

    @Override
    public String tag()
    {
        return "properties";
    }

    @Override
    public String sticker()
    {
        return "properties";
    }

    @Override
    public Xmlizer xmlizer()
    {
        return this;
    }

    @Override
    public Collection<? extends XmlINode> writeAttributes(Xml xml)
    {
        return writeAttributes(xml, escapeAttributes(), escapeCData());
    }

    public Collection<? extends XmlINode> writeAttributes(Xml xml, boolean escapeProps, boolean escapeCData)
    {
        if (!doUnfold())
        {
            StringBuilder style = new StringBuilder();
            for (Property3 entry : entries())
            {
                // Log.debug(this, ".writeAttributes - " + entry.string());
                if (!entry.isEmptyKey())
                    if (entry.isCssKey())
                        style.append(entry.key).append(entry.value.endsWith(";") ? entry.value : entry.value + ";");
                    else if (xml.isHTML() && entry.value != null && entry.value.isEmpty())
                    {
                        xml.writeAlone(entry.key);
                    } else
                        xml.write(entry.key, entry.value, escapeProps);
            }
            if (style.length() > 0)
                xml.write("style", style.toString());
            if (hasEmptyKey())
                xml.writeCData(emptyValue(), escapeCData);
        }
        return this.children();
    }

    @Override
    public void readAttributes(DomNode dom)
    {
        if (!doUnfold())
            this.readAttributes(dom, false);
    }

    public void readAttributes(DomNode dom, boolean readCData)
    {

        for (String key : dom.remainingAttributes())
        {
            // Log.debug(this, ".readAttributes - "+dom+": "+key+", "+dom.value(key));
            String value = dom.value(key);
            if (key.equals(CSS.STYLE) && value.contains(":"))
                CSSBuilder.parseProperties(value, this);
            else
                this.put(key, value);
        }
        if (readCData)
        {
            String cdata = dom.hasOnlyCData() ? dom.cdata(null) : null;
            if (cdata != null)
                this.setEmptyValue(cdata);
        }
    }

    // public void attributes(DomNode dom, String... unuseful)
    // {
    // Set3<String> set = new Set3<String>(unuseful);
    // for (String key : dom.attributes())
    // if (!set.contains(key))
    // if (key.equals(CSS.TAG_STYLE) && key.contains(":"))
    // CSSBuilder.parseProperties(dom.value(key), this);
    // else
    // this.put(key, dom.value(key));
    // String cdata = dom.hasCData() ? dom.cdata(null) : null;
    // if (cdata != null)
    // this.setEmptyValue(cdata);
    // }
    @Override
    public XmlINode newChild(DomNode child)
    {
        return new Property3(this);
    }

    @Override
    public void endChild(XmlINode child)
    {
        if (child != null)
            this.put((Property3) child);
    }

    public String toSuperString()
    {
        return super.toString();
    }

    @Override
    public String toString()
    {
        return tag() + super.toString();
    }

    public String toXml()
    {
        return Xml.toString(this);
    }

    public String[] array()
    {
        String[] pairs = new String[this.size() * 2];
        int i = 0;
        for (Entry<String, String> entry : this.entrySet())
        {
            pairs[i++] = entry.getKey();
            pairs[i++] = entry.getValue();
        }
        return pairs;
    }

    @Override
    public void setParent(XmlINode parent)
    {

    }

    public String toString(String sep)
    {
        Stringer sg = new Stringer();
        int counter = 0;
        for (Map.Entry<String, String> e : this.entrySet())
            sg.span(counter++ == 0 ? "" : sep, Zen.unnull(e.getKey()), sep, Zen.unnull(e.getValue()));
        return sg.toString();
    }

    public static Props FromString(String data, String sep)
    {
        Props props = new Props();
        if (!Str.IsVoid(data))
        {
            Tokens tks = Tokens.Split(data, sep);
            while (tks.hasNext())
                props.put(tks.next(), tks.next());
        }
        return props;
    }

    public String toJson()
    {
        return Json.Map(this);
    }

    public String json()
    {
        return toJson();
    }

    public String jsonData()
    {
        return jsonData("data");
    }

    public String jsonData(String name)
    {
        String[] keys = this.keys();
        String[] values = new String[keys.length];
        for (int i = 0; i < keys.length; i++)
            values[i] = get(keys[i]);
        String[][] table =
                {keys, values};
        String json = JsonArray.toJson(table, " ");
        return Str.IsVoid(name) ? json : "{\"" + name + "\":" + json + "}";
    }

    public DataTable table()
    {
        String[] fields = this.keys();
        DataTable table = new DataTable(fields);
        table.addRecord(this.values().toArray(new String[0]));
        return table;
    }

    public Props ReadJson(String json)
    {
        Json.Read(json).props(this);
        return this;
    }
}
