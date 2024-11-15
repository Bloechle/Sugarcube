package sugarcube.formats.epub.structure.otf;

import sugarcube.common.system.log.Log;

import java.util.Comparator;

public class Table
{
  public String id = "NO_ID";

  public int checksum = 0;// uint32
  public int offset = 0;// uint32
  public int length = 0;// uint32

  protected FontData font_data = null;

  public String get_id()
  {
    return id;
  }

  public FontData fontData()
  {
    if (font_data == null)
    {
      font_data = new FontData(1024);
    }
    return font_data;
  }

  public boolean has_data()
  {
    return length > 0;
  }

  public void parse(FontData dis)
  {

  }

  /** Validate table checksum. */
  public boolean validate(FontData dis)
  {
    boolean valid;

    if (length == 0)
    {
      Log.debug(this, "Table $id is of zero length.\n");
      valid = false;
    } else
    {
      valid = Table.validate_table(dis, checksum, offset, length, id);
    }

    if (!valid)
    {
      Log.warn(this, "Table $id is invalid.\n");
    }

    return valid;
  }

  // int -> uint32
  public static boolean validate_table(FontData dis, int checksum, int offset, int length, String name)
  {
    int ch = calculate_checksum(dis, offset, length, name);
    boolean c;

    c = (ch == checksum);

    if (!c)
    {
      Log.debug(Table.class, "Checksum does not match data for $(name).\n" + "name: $name, checksum: $checksum, offset: $offset, length: $length\n"
          + "calculated checksum $(ch)\n");
    }

    return c;
  }

  // int -> uint32
  public static int calculate_checksum(FontData dis, int offset, int length, String name)
  {
    int checksum = 0;
    int l;

    dis.seek(offset);

    l = (length % 4 > 0) ? length / 4 + 1 : length / 4;

    for (int i = 0; i < l; i++)
    {
      checksum += dis.read_ulong();
    }

    return checksum;
  }

  // int -> uint16
  public static int max_pow_2_less_than_i(int ind)
  {
    int last = 0;
    int i = 1;

    while ((i <<= 1) < ind)
    {
      last = i;
    }

    return last;
  }

  // int -> uint16
  public static int max_log_2_less_than_i(int ind)
  {
    return (int) (Math.log(ind) / Math.log(2));
  }

  public static Comparator<Table> idComparator()
  {
    return new Comparator<Table>()
    {
      @Override
      public int compare(Table t1, Table t2)
      {
        return t1.id.compareTo(t2.id);
      }
    };
  }
}
