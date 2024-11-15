package sugarcube.common.data.table;

import sugarcube.common.system.log.Log;
import sugarcube.common.system.util.Sys;
import sugarcube.common.data.collections.Array3;
import sugarcube.common.data.collections.Files3;
import sugarcube.common.data.collections.StringList;
import sugarcube.common.data.collections.Stringer;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;

import java.io.*;

public class CSV extends Stringer
{
    public static final int BUFFER_SIZE = 1024;
    public static final char EOF = (char) -1;
    public static final char COMMA = ',';
    public static final char SEMICOLON = ';';
    public static final char QUOTES = '"';
    public static final char NEW_LINE = (char) 10;
    public static final char CARRIAGE_RETURN = (char) 13;
    public static final char NUL = (char) 0;

    public CSV()
    {

    }

    public CSV(String... fields)
    {
        this(1024, fields);
    }

    public CSV(int capacity, String... fields)
    {
        super(capacity);
        if (fields != null && fields.length > 0)
            separate(fields);
    }

    public static String Merge(File3[] files)
    {
        StringList csv = new StringList();
        for (int i = 0; i < files.length; i++)
            files[i].readLinesAsList(true, csv, i != 0);
        return csv.string("\n");
    }

    public static DataTable ReadString(String csv)
    {
        return new Reader(new ByteArrayInputStream(csv.getBytes()), true);
    }

    public static DataTable Read(String path)
    {
        return Read(path, DEFAULT_SEP);
    }

    public static DataTable Read(String path, char separator)
    {
        return Read(File3.Get(path), separator);
    }

    public static DataTable Read(File3 file)
    {
        return Read(file, DEFAULT_SEP);
    }

    public static DataTable Read(File3 file, char separator)
    {
        if (!File3.Exists(file))
        {
            Log.debug(CSV.class, ".Read - file not found: " + (file == null ? "null" : file.path()));
            return null;
        }
        return new Reader(file, true, separator);
    }

    public static DataTable Read(InputStream stream)
    {
        return stream == null ? null : new Reader(stream, true);
    }

    public static String Norm(String value)
    {
        return value == null ? "" : value.replace(";", ".,").replace("\n", "|").replace("\r", " ").replace("\"", "''");
    }

    private static class Reader extends DataTable
    {
        private BufferedReader inputStream;
        private char[] buffer = new char[CSV.BUFFER_SIZE];
        private int index = CSV.BUFFER_SIZE;
        private int size;
        private boolean checkBOM = false;
        private char separator = SEMICOLON;

        public Reader(File file, boolean utf8)
        {
            this(file, utf8, SEMICOLON);
        }

        public Reader(File file, boolean utf8, char separator)
        {
            this(IO.InputStream(file), utf8, separator);
        }

        public Reader(InputStream stream, boolean utf8)
        {
            this(stream, utf8, SEMICOLON);
        }

        public Reader(InputStream stream, boolean utf8, char separator)
        {
            try
            {
                this.separator = separator;
                this.inputStream = new BufferedReader(new InputStreamReader(stream, utf8 ? IO.UTF8 : "windows-1252"));
                this.checkBOM = utf8;
                parse();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            IO.Close(inputStream);
        }

        private void parse()
        {
            StringList row = new StringList();
            int cols = 0;
            char c;
            char lastChar = NUL;
            String text = "";
            boolean isInQuotes = false;
            while ((c = read()) != EOF)
            {
                switch (c)
                {
                    case QUOTES:
                        isInQuotes = !isInQuotes;
                        if (lastChar == QUOTES)
                            text += "\"";
                        break;
                    case SEMICOLON:
                    case COMMA:
                        if (c == separator)
                        {
                            if (!isInQuotes)
                            {
                                row.add(clean(text));
                                text = "";
                            } else
                                text += separator;
                        } else
                            text += c;
                        break;
                    case CARRIAGE_RETURN:
                        if (!isInQuotes)
                        {
                            row.add(clean(text));
                            addRecord(row.array());
                            if (row.size() > cols)
                                cols = row.size();
                            text = "";
                            row.clear();
                        } else
                            text += CARRIAGE_RETURN;
                        break;
                    case CSV.NEW_LINE:
                        if (lastChar != CARRIAGE_RETURN && !isInQuotes)
                        {
                            row.add(clean(text));
                            addRecord(row.array());
                            if (row.size() > cols)
                                cols = row.size();
                            text = "";
                            row.clear();
                        } else if (isInQuotes)
                            text += CSV.NEW_LINE;
                        break;
                    default:
                        text += c;
                }
                lastChar = c;
            }

            if (!row.isEmpty())
            {
                row.add(clean(text));
                addRecord(row.array());
                if (row.size() > cols)
                    cols = row.size();
                text = "";
                row.clear();
            }

            for (Record rec : this.rows)
                rec.resize(cols);

            if (!rows.isEmpty())
                this.fields(rows.get(0).values);

            this.firstRowHasFields = true;
        }

        private String clean(String text)
        {
            return text.equals("\"") ? "" : text; // ;""; -> ;";
        }

        private char read()
        {
            if (size < BUFFER_SIZE && index == size)
                return EOF;
            if (index == BUFFER_SIZE)
            {
                index = 0;
                try
                {
                    size = inputStream.read(buffer);
                    if (checkBOM)
                    {
                        // Log.debug(this, ".read() - buffer[0]="+buffer[0]);
                        if (buffer[0] == '\uFEFF')
                            index++;
                        checkBOM = false;
                    }
                    if (size == -1)
                        return EOF;
                } catch (IOException e)
                {
                    e.printStackTrace();
                    Log.warn(this, ".read - unable to read the CSV file");
                }
            }
            return buffer[index++];
        }
    }

    public static class Processor
    {
        protected String csvPath;
        protected DataTable table;
        protected String postfix = "-modified";
        protected char separator = CSV.DEFAULT_SEP;

        public Processor(char separator)
        {
            this.separator = separator;
        }

        public Processor(String path)
        {
            this(path, CSV.DEFAULT_SEP);
        }

        public Processor(String path, char separator)
        {
            this.separator = separator;
            this.reset(path);
        }

        public void reset(String path)
        {
            this.csvPath = path;
            this.table = CSV.Read(path, separator);
        }

        public void processFolder(File3 folder)
        {
            Files3 files = folder.children(".csv");
            int counter = 0;
            int total = files.size();
            for (File3 file : files)
            {
                Sys.Println(++counter+"/"+total+" "+file);
                reset(file.path());
                process();
                done();
            }
        }

        public void process(boolean write)
        {
            this.process();
            if (write)
                write();
        }

        public void process()
        {
            Array3<Record> records = table.records();
            for (int i = 0; i < records.size(); i++)
                processRow(i, records.get(i));
        }

        public void processRow(int index, Record rec)
        {
            rec.values = processRow(index, rec.values);
        }

        public String[] processRow(int index, String[] cols)
        {
            return cols;
        }

        public void done()
        {

        }

        public void write(String oldPath, String newPath)
        {
            table.write(new File3(csvPath).repath(oldPath, newPath).needDirs(false));
        }

        public void write()
        {
            write(".csv", postfix + ".csv");
        }

    }


//  private static final char QUOTES = '\"';
//  private static final char SEMICOLON = ';';
//  private static final char COMMA = ',';


//  public static String TrimQuotes(String string)
//  {
//    string = string.trim();
//    int size = string.length();
//    boolean starts = string.startsWith("\"");
//    boolean ends = string.endsWith("\"");
//    return starts && ends ? string.substring(1, size - 1) : starts ? string.substring(1) : ends ? string.substring(0, size - 1) : string;
//  }

//  public static String[] parse(String row)
//  {
//    return parse(row, true);
//  }
//
//  public static String[] parse(String row, boolean semicolon)
//  {
//    return parse(row, true, true);
//  }
//
//  public static String[] parse(String row, boolean semicolon, boolean trimQuotes)
//  {
//
//    row = row == null ? "" : row.trim();
//
//    List8 cells = new List8();
//    char c;
//    String cell = "";
//    boolean quoted = false;
//
//    for (int i = 0; i < row.length(); i++)
//    {
//      c = row.charAt(i);
//      switch (c)
//      {
//      case QUOTES:
//        quoted = !quoted;
//      case SEMICOLON:
//      case COMMA:
//        if ((semicolon && c == SEMICOLON || !semicolon && c == COMMA) && !quoted)
//        {
//          cells.add(clean(cell, trimQuotes));
//          cell = "";
//          break;
//        }
//      default:
//        cell += c;
//      }
//    }
//
//    if (!cell.isEmpty())
//      cells.add(clean(cell, trimQuotes));
//
//    return cells.array();
//  }


//
//  private static String clean(String text, boolean trimQuotes)
//  {
//    if (trimQuotes)
//      text = TrimQuotes(text);
//    return text.replaceAll("\"\"", "\"");
//  }
}
