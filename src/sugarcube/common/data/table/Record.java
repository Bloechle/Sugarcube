package sugarcube.common.data.table;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.A;
import sugarcube.common.data.collections.Props;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.json.Json;
import sugarcube.common.data.xml.Nb;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class Record implements Iterable<String>
{
    private transient DataTable data;
    public long timestamp;
    public String[] values = null;

    public Record(DataTable data, String... values)
    {
        this.data = data;
        this.values = values;

        int cols = data == null ? 0 : data.cols();

        if (cols == 0 || values.length == cols)
            this.values = values;
        else if (values.length == 0)
        {
            this.values = new String[cols];
            for (int i = 0; i < values.length; i++)
                values[i] = "";
        } else
            Log.warn(this, " - number of values do not match writer columns");
    }

    public Record addNewValue(String value)
    {
        resize(values.length + 1);
        values[values.length - 1] = value;
        return this;
    }

    public boolean doKeep()
    {
        return true;
    }

    public int size()
    {
        return values == null ? 0 : values.length;
    }

    public String[] values(String... fields)
    {
        if (fields.length == 0)
            return this.values;
        String[] values = new String[fields.length];
        for (int i = 0; i < fields.length; i++)
            values[i] = get(fields[i]);
        return values;
    }

    public Record resize(int size)
    {
        if (size != values.length)
        {
            String[] newValues = new String[size];
            System.arraycopy(values, 0, newValues, 0, size < values.length ? size : values.length);
            this.values = newValues;
        }
        return this;
    }

    public Props props()
    {
        String[] fields = data.fields();
        Props props = new Props();
        for (int i = 0; i < values.length; i++)
            props.put(fields[i], values[i]);
        return props;
    }

    public Record setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
        return this;
    }

    public void setAll(Map<String, String> fields)
    {
        for (Map.Entry<String, String> field : fields.entrySet())
            set(field.getKey(), field.getValue());
    }

    public Record set(String field, String value)
    {
        return set(data.index(field), value);
    }

    public Record concat(String field, String value)
    {
        return set(field, get(field, "") + value);
    }

    public Record concat(Record rec, String... fields)
    {
        if (fields.length == 0 && data != null)
            fields = data.fields();

        for (String field : fields)
        {
            String base = this.trim(field, "");
            String add = rec.trim(field, "");
            if (Str.IsVoid(add))
                return this;
            set(field, (base.endsWith("-") ? base.substring(0, base.length() - 1) : base + " ") + add);
        }
        return this;
    }

    public Record set(int index, String value)
    {
        this.values[index] = value;
        return this;
    }

    public String id()
    {
        return trim("id");
    }

    public boolean isID(String id)
    {
        return id.equals(id());
    }

    public String get(int index)
    {
        return get(index, "");
    }

    public String get(int index, String def)
    {
        String value = index > -1 && index < values.length ? values[index] : null;
        return value == null ? def : value;
    }

    public String get(String field)
    {
        return get(field, "");
    }

    public String concat(String sep, String... fields)
    {
        StringBuilder sb = new StringBuilder(fields.length * 16);
        for (int i = 0; i < fields.length; i++)
        {
            sb.append(get(fields[i]));
            if (i != fields.length - 1)
                sb.append(sep);
        }
        return sb.toString();
    }

    public String concatCols(String sep, int... columnIndexes)
    {
        StringBuilder sb = new StringBuilder(columnIndexes.length * 16);
        for (int i = 0; i < columnIndexes.length; i++)
        {
            sb.append(get(columnIndexes[i]));
            if (i != columnIndexes.length - 1)
                sb.append(sep);
        }
        return sb.toString();
    }

    public Props jsonProps(String field)
    {
        return Json.Read(trim(field)).props();
    }

    public String trim(String field)
    {
        return trim(field, "");
    }

    public String trim(String field, String def)
    {
        return get(field, def).trim();
    }

    public String trimAsNull(String field)
    {
        String value = get(field, null);
        return value == null || (value = value.trim()).isEmpty() ? null : value;
    }

    public String trim(int index)
    {
        return trim(index, "");
    }

    public String trim(int index, String def)
    {
        return get(index, def).trim();
    }

    public String trimAsNull(int index)
    {
        String value = get(index, null);
        return value == null || (value = value.trim()).isEmpty() ? null : value;
    }


    public String get(String field, String def)
    {
        return get(data.index(field), def);
    }

    public boolean hasData(int index)
    {
        return Str.HasData(get(index));
    }

    public boolean sameValueAsField(String field)
    {
        return is(field, field);
    }

    public boolean is(String field, String... values)
    {
        return Str.Equals(trim(field, null), values);
    }

    public boolean is(int index, String... values)
    {
        return Str.Equals(trim(index, null), values);
    }

    public String first()
    {
        return get(0);
    }

    public String last()
    {
        return get(values.length - 1);
    }

    public int[] ints(int... indexes)
    {
        int[] ints = new int[indexes.length];
        for(int i=0; i<indexes.length; i++)
            ints[i] = integer(indexes[i]);
        return ints;
    }

    public int integer(int index)
    {
        return integer(index, 0);
    }

    public int integer(int index, int def)
    {
        return Nb.Int(get(index, def + ""));
    }

    public int integer(String field)
    {
        return integer(field, 0);
    }

    public int integer(String field, int def)
    {
        return Nb.Int(get(field, def + ""), def);
    }

    public float real(int index)
    {
        return real(index, 0);
    }

    public float real(int index, float def)
    {
        return Nb.Float(get(index, def + ""), def);
    }

    public float real(String field)
    {
        return real(field, 0f);
    }

    public float real(String field, float def)
    {
        return Nb.Float(get(field, def + ""), def);
    }

    public boolean bool(int index, boolean def)
    {
        return Nb.Bool(get(index, def + ""), def);
    }

    public boolean bool(String field, boolean def)
    {
        return Nb.Bool(get(field, def + ""), def);
    }

    public final void setValues(String... values)
    {
        this.values = values;
    }

    public DataTable remove()
    {
        if (data != null)
            data.rows.remove(this);
        return data;
    }

    public boolean isEmpty(String field)
    {
        return isDataEmpty(get(field));
    }

    public boolean isDataEmpty(String data)
    {
        return data == null || data.isEmpty();
    }

    public boolean has(String field)
    {
        return !isEmpty(field);
    }

    public boolean hasVoid()
    {
        for (String text : values)
            if (Str.IsVoid(text))
                return true;
        return false;
    }

    public Record copy()
    {
        return new Record(data, A.Copy(values));
    }

    @Override
    public Iterator<String> iterator()
    {
        return Arrays.asList(values).iterator();
    }

    @Override
    public String toString()
    {
        return Arrays.toString(values);
    }

    public String string(String... fields)
    {
        return Arrays.toString(values(fields));
    }

    public boolean equals(Record record, String... fields)
    {
        if (fields.length == 0)
            fields = data.fields();
        for (String field : fields)
            if (!record.get(field).equals(get(field)))
                return false;
        return true;
    }
}
