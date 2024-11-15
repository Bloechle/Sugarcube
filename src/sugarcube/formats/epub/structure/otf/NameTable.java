package sugarcube.formats.epub.structure.otf;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.StringList;
import sugarcube.formats.ocd.objects.font.SVGFont;

public class NameTable extends Table
{

  public static final int COPYRIGHT_NOTICE = 0;
  public static final int FONT_NAME = 1;
  public static final int SUBFAMILY_NAME = 2;
  public static final int UNIQUE_IDENTIFIER = 3;
  public static final int FULL_FONT_NAME = 4; // name + subfamily
  public static final int VERSION = 5;
  public static final int POSTSCRIPT_NAME = 6;
  public static final int DESCRIPTION = 10;
  public static final int PREFERED_FAMILY = 16;
  public static final int PREFERED_SUB_FAMILY = 17;

  List3<Integer> identifiers;
  StringList text;
  SVGFont font;

  public NameTable(SVGFont font)
  {
    id = "name";
    this.font = font;
    text = new StringList();
    identifiers = new List3<Integer>();
  }

  public String get_name(int identifier)
  {
    int i = 0;

    for (int n : identifiers)
    {
      if (n == identifier)
      {
        return text.get(i);
      }
      i++;
    }
    return "";
  }

  public void process()
  {
    FontData fd = new FontData();

    int len = 0;
    String t;
    int p;
    int l;
    int num_records;

    List3<Integer> type = new List3<Integer>();
    StringList text = new StringList();

    text.add("Copyright All Rights Reserved");
    type.add(COPYRIGHT_NOTICE);

    text.add(font.fontFamily);
    type.add(FONT_NAME);

    text.add("Regular");
    type.add(SUBFAMILY_NAME);

    text.add(font.fontFamily + "_" + font.nbOfGlyphs());
    type.add(UNIQUE_IDENTIFIER);

    text.add(font.fontFamily);
    type.add(FULL_FONT_NAME);

    text.add("Version 1,0");
    type.add(VERSION);

    text.add(font.fontFamily);
    type.add(POSTSCRIPT_NAME);

    text.add("Converted from SVG Font only for rendering purpose - no hints, no kerning, restricted glyph set");
    type.add(DESCRIPTION);

    text.add(font.fontFamily);
    type.add(PREFERED_FAMILY);

    text.add(font.fontFamily);
    type.add(PREFERED_SUB_FAMILY);

    num_records = text.size();

    fd.add_ushort(0); // format 1
    fd.add_ushort(2 * num_records); // nplatforms * nrecords
    fd.add_ushort(6 + 12 * 2 * num_records); // string storage offset

    for (int i = 0; i < num_records; i++)
    {
      t = text.get(i);
      p = type.get(i);
      l = t.length();

      fd.add_ushort(1); // platform
      fd.add_ushort(0); // encoding id
      fd.add_ushort(0); // language
      fd.add_ushort(p); // name id
      fd.add_ushort(l); // strlen
      fd.add_ushort(len); // offset from begining of string storage
      len += l;
    }

    for (int i = 0; i < num_records; i++)
    {
      t = text.get(i);
      p = type.get(i);
      l = (int) (2 * t.length());

      fd.add_ushort(3); // platform
      fd.add_ushort(1); // encoding id
      fd.add_ushort(0x0409); // language
      fd.add_ushort(p); // name id
      fd.add_ushort(l); // strlen
      fd.add_ushort(len); // offset from begining of string storage
      len += l;
    }

    // platform 1
    for (String s : text)
    {
      fd.add_str(s);
    }

    // platform 3
    for (String s : text)
    {
      fd.add_str_utf16(s);
    }

    fd.pad();

    this.font_data = fd;
  }
}
