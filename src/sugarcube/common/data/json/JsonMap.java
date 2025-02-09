package sugarcube.common.data.json;

import javafx.beans.property.BooleanProperty;
import sugarcube.common.data.collections.ObjectMap;
import sugarcube.common.data.collections.Props;
import sugarcube.common.data.xml.Nb;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class JsonMap extends ObjectMap implements Json.Able {
    public JsonMap() {
        super();
    }

    public JsonMap(Map<String, Object> map) {
        super(map);
    }

    public Props props() {
        return props(new Props());
    }

    public Props props(Props props) {
        for (String key : keySet())
            props.put(key, string(key, null));
        return props;
    }

    public void populate(Map<String, String> map) {
        for (String key : keySet())
            map.put(key, string(key, null));
    }

    public JsonMap putValueIfNotZero(String key, float value) {
        if (Math.abs(value) > 0.0001f)
            this.put(key, value);
        return this;
    }

    public JsonMap putValueIfNotOne(String key, float value) {
        if (Math.abs(value - 1.0f) > 0.0001f)
            this.put(key, value);
        return this;
    }


    public JsonMap putIfTrue(String key, boolean value) {
        if (value)
            this.put(key, true);
        return this;
    }

    public JsonMap object(String key) {
        return (JsonMap) get(key);
    }

    public JsonMap map(String key) {
        return (JsonMap) get(key);
    }

    public JsonArray array(String key) {
        return (JsonArray) get(key);
    }

    public String string(String key, String def) {
        Object val = this.get(key, def);
        return val == null ? def : val.toString();
    }

    public int integer(String key, int def) {
        return Nb.Int(string(key, "" + def), def);
    }

    public float real(String key, float def) {
        return Nb.Float(string(key, "" + def), def);
    }

    public boolean bool(String key, boolean def) {
        return Nb.Bool(string(key, "" + def), def);
    }

    public boolean bool(String key, BooleanProperty bool) {
        boolean b = Nb.Bool(string(key, "" + bool.get()), bool.get());
        if (b != bool.get())
            bool.set(b);
        return b;
    }

    @Override
    public void writeJson(Writer out, String indent) throws IOException {
        Json.WriteJson(this, out, indent);
    }

    @Override
    public String toJson(String indent) {
        return Json.ToJson(this, indent);
    }

    @Override
    public String toString() {
        return toJson("");
    }

}
