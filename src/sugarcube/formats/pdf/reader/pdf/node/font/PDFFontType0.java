package sugarcube.formats.pdf.reader.pdf.node.font;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.data.collections.Unicodes;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.node.font.encoding.CIDSystemInfo;
import sugarcube.formats.pdf.reader.pdf.object.*;

public class PDFFontType0 extends PDFFont
{
    // page 423
    public static StringSet debugMsg = new StringSet();
    public CIDSystemInfo cidSystemInfo = null;

    // also called composite fonts
    public PDFFontType0(PDFNode parent, PDFDictionary fontMap)
    {
        super(parent, fontMap);
        PDFArray array = fontMap.get("DescendantFonts").toPDFArray();
        loadDescendantFonts(array.first().toPDFDictionary());
        finalizeConstruction();
        if(array.size()>1)
            Log.warn(this, " - more than one descendant font found: "+array);
    }

    // CID for character identifier [0,max], glyph[CID], no name index != Type1
    // CMap = code=>CID
    // also called composite font
    // page 407
    private void loadDescendantFonts(PDFDictionary map)
    {
        this.basefont = map.get("BaseFont").toString();
        // CIDToGIDMap
        PDFObject po;
        if ((po = map.get("W")).isValid())
        {
            PDFArray array = po.toPDFArray();
            for (int i = 0; i < array.size(); i++)
            {
                int cid = array.get(i).intValue();
                if ((po = array.get(++i)).type == PDFObject.Type.Number)
                {
                    double width = array.get(++i).doubleValue();
                    while (cid <= po.intValue())
                        widths.put(cid++, width);
                } else
                {
                    PDFArray range = po.toPDFArray();
                    for (int j = 0; j < range.size(); j++)
                        widths.put(cid + j, range.get(j).doubleValue());
                }
            }
        }
        if (map.has("DW"))
        {
            defaultWidth = map.get("DW").doubleValue();
            widths.setDefault(defaultWidth);
        }

        if ((po = map.get("W2")).isValid())
        {
            PDFArray array = po.toPDFArray();
            for (int i = 0; i < array.size(); i++)
            {
                int cid = array.get(i).intValue();
                if ((po = array.get(++i)).type == PDFObject.Type.Number)
                {
                    Height height = new Height(array.get(++i).doubleValue(), array.get(++i).doubleValue(), array.get(++i).doubleValue());
                    while (cid <= po.intValue())
                        heights.put(cid++, height);
                } else
                {
                    PDFArray range = po.toPDFArray();
                    for (int j = 0; j < range.size() - 2; j += 3)
                        heights.put(cid + j, new Height(range.get(j).doubleValue(), range.get(j + 1).doubleValue(), range.get(j + 2).doubleValue()));
                }
            }
        }
        if (map.has("DW2"))
        {
            po = map.get("DW2");
            PDFArray array = po.toPDFArray();
            double oy = array.get(0).doubleValue();
            defaultHeight = new Height(array.get(1).doubleValue(), 0, oy);
            heights.setDefault(defaultHeight);
        }

        if (descriptor == null)
            add(this.descriptor = new FontDescriptor(this, map.get("FontDescriptor").toPDFDictionary()));

        po = map.get("CIDToGIDMap").unreference();
        if (po.isValid() && !(po.isPDFName() && po.toString().contains("Identity")))
        {
            PDFObject c2g = map.get("CIDToGIDMap");
            String c2gString = c2g.toString();
            if (debugMsg.notYet(c2gString))
                Log.debug(this, ".loadDescendantFonts - CIDToGIDMap: " + c2gString);
            if (po.isPDFStream())
            {
                this.readCIDToGIDMap(po.toPDFStream());
            }

        }

        if (map.has("CIDSystemInfo"))
        {
            // Log.debug(this, " - has CIDSystemInfo");
            this.cidSystemInfo = new CIDSystemInfo(map.get("CIDSystemInfo"));
            this.cidSystemInfo.populateEncoding(encoding);
        }
    }

    private void readCIDToGIDMap(PDFStream stream)
    {
        try
        {
            byte[] bytes = stream.byteValues();
            int size = bytes.length / 2;
            int offset = 0;
            for (int index = 0; index < size; index++)
            {
                int code = 0;
                for (int i = 0; i < 2; i++)
                {
                    code <<= 8;
                    code |= (bytes[offset + i] + 256) % 256;
                }

                cid2gid.put(index, code);
//        Log.debug(this, ".readCIDToGIDMap - cid=" + index + ", gid=" + code);
                offset += 2;
            }
        } catch (Exception exception)
        {
            Log.debug(this, ".readCIDToGIDMap - hiccup: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    @Override
    public Unicodes fontCodes(PDFString string)
    {
        return new Unicodes(string.hexa2B());
    }


    @Override
    public boolean isFontType0()
    {
        return true;
    }


    // @Override
    // public Path3 glyph(int code, float size)
    // {
    //
    //
    //
    // Path3 glyph = null;
    // Unicodes uni = encoding.getUnicode(code);
    // if (uni != null)
    // glyph = font.glyph(uni);
    // else if (codeToGID.contains(code))
    // glyph = font.glyph(codeToGID.get(code));
    // else
    // glyph = font.glyph(code);
    // return glyph.scale(size);
    // }

    @Override
    public String toString()
    {
        return super.toString() + "\n" + (cidSystemInfo == null ? "CIDSystemInfo=null" : cidSystemInfo.toString());
    }
}
