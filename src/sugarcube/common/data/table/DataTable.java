package sugarcube.common.data.table;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.*;
import sugarcube.common.data.json.JsonArray;
import sugarcube.common.data.json.JsonMap;
import sugarcube.common.interfaces.Jsonable;
import sugarcube.common.interfaces.Keepable;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;
import sugarcube.common.data.xml.Nb;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class DataTable implements ITable, Iterable<Record>, Jsonable
{

    private static class Trash implements Keepable<Record>
    {
        private Set3<Record> trashSet;

        public Trash(Set3<Record> trashSet)
        {
            this.trashSet = trashSet;
        }

        @Override
        public boolean doKeep(Record rec)
        {
            return trashSet.hasnt(rec);
        }
    }

    protected String name = "";
    protected StringMap<Integer> fields = new StringMap<>();
    protected Array3<Record> rows = new Array3<>();
    protected boolean firstRowHasFields = false;
    protected String filePath = null;

    public DataTable()
    {
    }

    public DataTable(String... fields)
    {
        this.fields(fields);
    }

    public DataTable(String[] fields, String[][] data)
    {
        this.fields(fields);
        for (int i = 0; i < data.length; i++)
            rows.add(new Record(this, data[i]));
    }

    public DataTable(JsonMap json)
    {
        JsonArray table = json.array("data");
        this.fields(table.array(0).strings());
        for (int i = 1; i < table.size(); i++)
            this.addRecord(table.array(i).strings());
    }

    public DataTable addColumn(String field, String def)
    {
        fields.put(field, fields.size());
        for (int i = 0; i < rows.size(); i++)
            rows.get(i).addNewValue(i == 0 && firstRowHasFields ? field : def);
        return this;
    }

    public DataTable removeCols(String... fields)
    {
        this.fields.removeAll(fields);
        IntSet oldIndexes = new IntSet(this.fields.values());
        this.updateMapIndexes();
        for (int i = 0; i < rows.size(); i++)
        {
            String[] values = new String[this.fields.size()];
            Record rec = rows.get(i);
            int col = 0;
            for (int j = 0; j < rec.values.length; j++)
                if (oldIndexes.contains(j))
                    values[col++] = rec.values[j];
            rec.values = values;
        }
        return this;
    }

    public boolean hasFirstRowFields()
    {
        return firstRowHasFields;
    }

    public String name()
    {
        return name;
    }

    public DataTable name(String name)
    {
        this.name = name;
        return this;
    }

    public int firstRowIndex()
    {
        return firstRowHasFields ? 1 : 0;
    }

    public File3 file()
    {
        return File3.Get(filePath);
    }

    public DataTable filePath(String path)
    {
        this.filePath = path;
        return this;
    }

    @Override
    public String[] fields()
    {
        return this.fields.keyArray();
    }

    public StringMap<Integer> fieldsMap()
    {
        return this.fields;
    }

    public DataTable fields(String[] fields)
    {
        this.fields.clear();
        for (int col = 0; col < fields.length; col++)
            this.fields.put(fields[col], col);
        return this;
    }

    public DataTable updateMapIndexes()
    {
        return fields(fields.keyArray());
    }

    public boolean isEmpty()
    {
        return this.rows.isEmpty();
    }

    public boolean hasRow()
    {
        return !this.rows.isEmpty();
    }

    public boolean hasOneRow()
    {
        return isRowSize(1);
    }

    public boolean hasntOneRow()
    {
        return !hasOneRow();
    }

    public boolean isRowSize(int size)
    {
        return rows.size() == size;
    }

    @Override
    public int cols()
    {
        return fields.size();
    }

    @Override
    public int rows()
    {
        return rows.size();
    }

    public Array3<Record> records()
    {
        return rows;
    }

    public float[][] matrix(boolean withFirstRow, float def, int... indexes)
    {
        float[][] a = new float[withFirstRow ? rows.size() : rows.size() - 1][indexes.length];
        for (int i = 0; i < a.length; i++)
        {
            Record rec = rows.get(withFirstRow ? i : i + 1);
            for (int j = 0; j < indexes.length; j++)
                a[i][j] = rec.real(indexes[j], def);
        }
        return a;
    }

    public Record first()
    {
        return record();
    }

    public Record record()
    {
        return record(0);
    }

    public Record record(int row)
    {
        return row > -1 && row < rows.size() ? rows.get(row) : null;
    }

    public String[] row(int index)
    {
        Record rec = record(index);
        return rec == null ? null : rec.values;
    }

    public String[] col(String field)
    {
        return col(field, "");
    }

    public String[] col(String name, String def)
    {
        String[] col = new String[rows.size()];
        for (int i = 0; i < col.length; i++)
            col[i] = rows.get(i).get(name, def);
        return col;
    }

    public DataTable addRecord(Record rec)
    {
        this.rows.add(rec);
        return this;
    }

    public DataTable addRecord(String... row)
    {
        this.rows.add(new Record(this, row));
        return this;
    }

    public void addAll(Record... records)
    {
        for (Record rec : records)
            this.addRecord(rec);
    }

    public void addAll(Iterable<Record> records)
    {
        for (Record rec : records)
            this.addRecord(rec);
    }

    public void setAll(Iterable<Record> records)
    {
        this.rows.clear();
        this.addAll(records);
    }

    public DataTable removeLast()
    {
        rows.removeLast();
        return this;
    }

    public Record last()
    {
        return rows.last();
    }

    public <T extends Record> T lastCast()
    {
        return (T) rows.last();
    }


    @Override
    public RecIt iterator()
    {
        return new RecIt(rows.iterator());
    }

    public <T extends Record> Iterable<T> iterable()
    {
        final Iterator<T> it = castIt();
        return () -> it;
    }

    public <T extends Record> Iterator<T> castIt()
    {
        final Iterator<Record> it = iterator();
        return new Iterator<T>()
        {
            @Override
            public boolean hasNext()
            {
                return it.hasNext();
            }

            @Override
            public T next()
            {
                return (T) it.next();
            }

            public void remove()
            {
                it.remove();
            }
        };
    }

    public DataTable clear()
    {
        this.rows.clear();
        return this;
    }

    public boolean isSize(int size)
    {
        return this.rows.size() == size;
    }

    public int index(String field)
    {
        int index = fields.get(field, -1);
        if (index < 0 && firstRowHasFields)
        {
            String[] names = row(0);
            for (int col = 0; col < names.length; col++)
                if (field.equals(names[col]))
                {
                    fields.put(field, col);
                    return col;
                }
        }
        return index;
    }

    public DataTable removeAll(boolean removeIfEqual, String field, String... values)
    {
        RecIt it = iterator();
        while (it.hasNext())
        {
            Record rec = it.next();
            boolean isEqual = rec.is(field, values);
            if (isEqual == removeIfEqual)
                it.remove();
        }
        return this;
    }

    // public int fieldIndex(String name)
    // {
    // return fields.get(name);
    // }

    // public int[] indexes()
    // {
    // Ints indexes = new Ints();
    // String[] fields = this.fields();
    // for (int i = 0; i < fields.length; i++)
    // if (!unactiveColumns.contains(fields[i]))
    // indexes.add(i);
    // return indexes.array();
    // }

    @Override
    public String get(int row, int col, String def)
    {
        Record rec = row > -1 && row < rows.size() ? rows.get(row) : null;
        return rec == null || col < 0 || col >= rec.size() ? def : rec.get(col);
    }

    @Override
    public String get(String field, int row, String def)
    {
        return get(row, index(field), def);
    }

    public int integer(String label, int row, int def)
    {
        return Nb.Int(get(label, row, "" + def), def);
    }

    public double real(String label, int row, double def)
    {
        return Nb.Double(get(label, row, "" + def), def);
    }

    public void set(String field, int row, String value)
    {
        Record rec = record(row);
        int index = index(field);
        if (rec != null && index > -1)
            rec.set(index, value);
    }

    public void set(int row, int col, String value)
    {
        Record rec = record(row);
        if (rec != null && col > -1)
            rec.set(col, value);
    }

    public String[] array(int row, String def, String... labels)
    {
        String[] values = new String[labels.length];
        for (int i = 0; i < labels.length; i++)
            values[i] = get(labels[i], row, def).trim();
        return values;
    }

    public Props props()
    {
        return props(0);
    }

    public Props props(int row)
    {
        return map(row);
    }

    public Props map(int row)
    {
        Props props = new Props();
        Record rec = record(row);
        String[] fields = this.fields();
        if (rec != null)
            for (int i = 0; i < rec.size(); i++)
                props.put(fields[i], rec.get(i));
        return props;
    }

    public Props[] allProps()
    {
        int firstIndex = firstRowHasFields ? 1 : 0;
        Props[] props = new Props[rows.size() - firstIndex];
        String[] keys = fields();
        for (int i = firstIndex; i < rows.size(); i++)
            props[i - firstIndex] = new Props(keys, record(i).values);
        return props;
    }

    @Override
    public String[][] values()
    {
        String[][] values = new String[rows()][];
        for (int row = 0; row < values.length; row++)
            values[row] = record(row).values;
        return values;
    }

    public String[][] onlyValues()
    {
        int delta = firstRowHasFields ? 1 : 0;
        String[][] values = new String[rows() - delta][];
        for (int row = delta; row < values.length; row++)
            values[row] = record(row).values;
        return values;
    }

    public String[][] values(boolean withFields)
    {
        int rows = rows();
        int size = rows;

        if (withFields && !firstRowHasFields)
            size++;
        else if (!withFields && firstRowHasFields)
            size--;

        String[][] values = new String[size][];

        if (size == rows + 1)
        {
            for (int row = 0; row < rows; row++)
                values[row + 1] = record(row).values;
            values[0] = fields();
        } else if (size == rows - 1)
        {
            for (int row = 1; row < rows; row++)
                values[row - 1] = record(row).values;
        } else
            for (int row = 0; row < rows; row++)
                values[row] = record(row).values;

        return values;
    }

    public DataTable sort(String field, boolean asc)
    {
        if (asc)
            sort(field, (o1, o2) -> (o1 == null ? -1 : o1.compareTo(o2)));
        else
            sort(field, (o1, o2) -> (o1 == null ? 1 : o2.compareTo(o1)));
        return this;
    }

    public DataTable sortID(boolean asc)
    {
        if (asc)
            sort("id", (o1, o2) -> (o1 == null ? -1 : Integer.compare(Nb.Int(o1), Nb.Int(o2))));
        else
            sort("id", (o1, o2) -> (o1 == null ? 1 : Integer.compare(Nb.Int(o2), Nb.Int(o1))));
        return this;
    }

    public DataTable sort(String field, Comparator<String> cmp)
    {
        Record fields = firstRowHasFields ? rows.removeFirst() : null;

        int index = index(field);
        if (index < 0)
            Log.debug(this, ".sort - field not found: " + field);
        else
            try
            {
                rows.sort((a, b) -> cmp.compare(a.get(index, ""), b.get(index, "")));
            } catch (Exception e)
            {
                e.printStackTrace();
            }

        if (fields != null)
            rows.add(0, fields);

        return this;
    }

    public DataTable sort(final int col, final boolean ascending)
    {
        Comparator<Record> cmp = (r1, r2) ->
        {
            if (r1 == r2)
                return 0;
            else if (r1 == null)
                return -1;
            else if (r2 == null)
                return 1;
            return ascending ? r1.values[col].compareTo(r2.values[col]) : r2.values[col].compareTo(r1.values[col]);
        };
        Collections.sort(rows, cmp);
        return this;
    }

    public String[][] jsonData()
    {
        String[][] table = new String[rows.size() + 1][];
        table[0] = fields.keyArray();
        for (int i = 0; i < rows.size(); i++)
            table[i + 1] = rows.get(i).values;
        return table;
    }

    public boolean doKeep(Record rec)
    {
        return true;
    }

    public boolean write()
    {
        return write(filePath);
    }

    public boolean write(String path)
    {
        return write(path, null);
    }

    public boolean writeWithout(String path, Set3<Record> trash)
    {
        return write(path, new Trash(trash));
    }

    public boolean write(String path, Keepable<Record> keeper)
    {
        return write(File3.Get(path), keeper);
    }

    public boolean write(File3 file)
    {
        return write(file, null);
    }

    public boolean writeWithout(File3 file, Set3<Record> trash)
    {
        return write(file, new Trash(trash));
    }


    public boolean write(File3 file, Keepable<Record> keeper)
    {
        if (file == null)
            return false;

        filePath = file.path();
        file = file.extense(".csv_w");
        if (file.exists())
            file.delete();

        boolean written = IO.WriteText(file, csv(keeper));
        if (written)
            file.rename(file.extense(".csv"));

        return written;
    }

    public String csv()
    {
        return csv(null);
    }

    public String csv(Keepable<Record> keeper)
    {
        Stringer sg = new Stringer();

        String[] fields = fields();
        int cols = fields.length;

        if (cols > 0)
        {
            for (int i = 0; i < cols; i++)
                sg.append(CSV.Norm(fields[i])).add(i == cols - 1 ? CSV.NEW_LINE : CSV.SEMICOLON);
        } else if (!rows.isEmpty())
            cols = rows.get(0).size();

        for (int i = firstRowHasFields ? 1 : 0; i < rows.size(); i++)
        {
            try
            {
                Record rec = rows.get(i);
                if ((keeper == null || keeper.doKeep(rec)) && doKeep(rec) && rec.doKeep())
                    for (int j = 0; j < cols; j++)
                        sg.append(j < rec.values.length ? CSV.Norm(rec.values[j]) : "").add(j == cols - 1 ? CSV.NEW_LINE : CSV.SEMICOLON);
            } catch (Exception e)
            {
                e.printStackTrace();
                Log.debug(this, ".csv - generation failed at index: " + i);
            }
        }

        return sg.toString();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (Record row : rows)
        {
            for (String value : row.values)
                sb.append(value + "|");
            sb.append("\n");
        }
        return sb.toString();
    }

}
