package sugarcube.formats.pdf.reader.pdf.node.font.aff;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.IntArray;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.data.collections.Unicodes;
import sugarcube.common.graphics.geom.Circle3;
import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.graphics.Stroke3;
import sugarcube.common.system.io.File3;
import sugarcube.common.data.xml.Nb;
import sugarcube.formats.pdf.reader.pdf.node.font.PDFGlyph;
import sugarcube.formats.pdf.reader.pdf.object.PDFStream;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;

public class AFF
{
    public static boolean DEBUG = false;
    public static int DEBUG_COUNTER = 0;

    private int callcount = 0;
    private int password, lenIV;
    private byte[][] subrs;
    public String[] chr2name;
    public StringMap<Object> name2outline;
    public StringMap<Point3> name2width;
    public Transform3 transform;
    private String fontname;
    private String lastDebugName = "";


    public AFF(String fontname)
    {
        this.fontname = fontname;
        Log.debug(DEBUG, this, " - " + fontname);
    }

    public AFF parse(PDFStream stream)
    {
        byte[] data = stream.byteValues();
        int start = stream.get("Length1").intValue(-1);
        if (start < 0)
            start = 0;
        int length = stream.get("Length2").intValue(-1);
        if (length < 0)
            length = data.length - start;
        return parse(data, start, length);
    }

    public AFF parse(byte[] font, int start, int len)
    {
        this.name2width = new StringMap<>();

        byte[] data = null;

        if (IsASCII(font, start))
        {
            byte[] bData = ReadASCII(font, start, start + len);
            data = Decrypt(bData, 0, bData.length, 55665, 4);
        } else
            data = Decrypt(font, start, start + len, 55665, 4);

        this.chr2name = readEncoding(font);
        int lenIVLoc = FindSlashName(data, "lenIV");
        PSParser psp = new PSParser(data, 0);
        if (lenIVLoc < 0)
            this.lenIV = 4;
        else
        {
            psp.loc = lenIVLoc + 6;
            this.lenIV = Integer.parseInt(psp.read());
        }
        this.password = 4330;
        int matrixloc = FindSlashName(font, "FontMatrix");
        if (matrixloc < 0)
        {
            System.out.println("No FontMatrix!");
            this.transform = new Transform3(0.001f, 0, 0, 0.001f, 0, 0);
        } else
        {
            PSParser psp2 = new PSParser(font, matrixloc + 11);
            this.transform = new Transform3(psp2.readArray(6));
        }

        this.subrs = readSubrs(data);
        this.name2outline = AFF.ReadChars(data, password, lenIV);

        // for(String name: name2outline.keySet())
        // {
        // Object o = name2outline.get(name);
        // Log.debug(this,
        // ".parseFont - "+this.fontname+": name="+name+",
        // bytes="+((byte[])o).length);
        // }
        return this;
    }

    public PDFGlyph outline(String name, Unicodes uni, int code)
    {
        // Log.debug(this, ".outline - "+this.fontname+": " + name + " " + uni + " "
        // + code);
        if (name == null || name.equals(".notdef"))
        {
            // if (code == 131)
            // Log.debug(this, ".outline " + this.fontname + ":" + name + " -> " +
            // code);
            int index = code;
            if (index >= 0 && index < chr2name.length)
                name = chr2name[index];
        }

        float width = name2width != null && name2width.containsKey(name) ? name2width.get(name).x : 1;
        PDFGlyph outline = this.name2outline != null && this.name2outline.containsKey(name) ? getOutline(name, width)
                : getOutline((char) code, getWidth((char) code, null));
        outline = outline == null ? null : outline.reverseY();
        return outline;
    }

    public PDFGlyph getOutline(String name, float width)
    {
        this.lastDebugName = name;
        if ((name == null) || (!name2outline.containsKey(name)))
            name = ".notdef";

        Object obj = name2outline.get(name);
        if ((obj instanceof Path3))
            // Log.debug(this, ".getOutline - "+name);
            return new PDFGlyph((Path3) obj, width);
        byte[] cs = (byte[]) obj;
        Point3 advance = new Point3();

        if (DEBUG)
            Log.debug(this, ".getOutline - " + fontname + " " + name + " ");

        Path3 path = parseGlyph(cs, advance, transform, name);
        // this.debugChar(path, name + "-getOutline", 1000);
        // if ((width != 0.0F) && (advance.x != 0.0F))
        // {
        // Point2D p = new Point2D.Float(advance.x, advance.y);
        // this.at.transform(p, p);
        //
        // double scale = width / p.getX();
        // AffineTransform xform = AffineTransform.getScaleInstance(scale, 1.0D);
        // gp.transform(xform);
        // }

        // Log.debug(this, ".getOutline - " + this.fontname + ": name=" + name +
        // " width=" + width + " contains=" + this.name2outline.containsKey(name));
        name2outline.put(name, path);
        name2width.put(name, advance);
        return new PDFGlyph(path, advance.x, advance.y);
    }

    private PDFGlyph getOutline(char src, float width)
    {
        return getOutline(this.chr2name[(src & 0xFF)], width);
    }

    public float getWidth(char code, String name)
    {
        String key = this.chr2name[(code & 0xFF)];
        if (name != null)
            key = name;
        if ((key != null) && (this.name2outline.containsKey(key)))
        {
            if (!this.name2width.containsKey(key))
                getOutline(key, 0.0F);
            Point3 width = this.name2width.get(key);
            return width.x;
        }
        return 0.0F;
    }

    private synchronized Path3 parseGlyph(byte[] cs, Point3 advance, AffineTransform tm, String name)
    {
        AFFGlyph g = new AFFGlyph(name, advance);
        parse(-1, cs, g, new IntArray(100));
        // Log.debug(this, ".parseGlyph - "+this.lastDebugName+": "+new
        // Transform3(tm));
        // this.debugChar(g.path, name + "-parseGlyph", 1);
        g.path.transform(tm);
        return g.path;
    }

    private void parse(int sub, byte[] cs, AFFGlyph g, IntArray history)
    {
        int loc = 0;
        float x1, y1, x2, y2, x3, y3;
        if (cs != null)
            while (loc < cs.length)
            {
                int op = cs[loc++] & 0xFF;
                if (op == 255)
                {
                    g.stack[(g.k++)] = (((cs[loc] & 0xFF) << 24) + ((cs[(loc + 1)] & 0xFF) << 16) + ((cs[(loc + 2)] & 0xFF) << 8) + (cs[(loc + 3)] & 0xFF));
                    loc += 4;
                } else if (op >= 251)
                {
                    g.stack[(g.k++)] = (-(op - 251 << 8) - (((int) cs[loc]) & 0xff) - 108);
                    loc++;
                } else if (op >= 247)
                {
                    g.stack[(g.k++)] = ((op - 247 << 8) + (((int) cs[loc]) & 0xff) + 108);
                    loc++;
                } else if (op >= 32)
                    g.stack[(g.k++)] = (op - 139);
                else
                {

                    if (DEBUG)
                        System.out.print(g.name + "-" + op + ", ");
                    if (DEBUG && fontname.startsWith("Garamond-Bold") && "P".equals(g.name) && ++DEBUG_COUNTER < 900)
                    {
                        int v12 = op == 12 ? ((int) cs[loc]) & 0xff : -1;
                        String filename = Str.Prefix("" + DEBUG_COUNTER, '0', 4) + "_op" + (v12 > -1 ? "12-" + v12 : op);
                        if (sub > -1)
                            filename += "_sub" + sub;
                        DebugChar(g, history, filename, 1, fontname);
                    }
                    history.add(op);
                    switch (op)
                    {
                        case 1:
                            g.k = 0;
                            break;
                        case 3:
                            g.k = 0;
                            break;
                        case 4:
                            g.y += g.pop();
                            g.path.lineMoveTo(g.x, g.y);
                            g.k = 0;
                            break;
                        case 5:
                            g.y += g.pop();
                            g.x += g.pop();
                            g.path.lineTo(g.x, g.y);
                            g.k = 0;
                            break;
                        case 6:
                            g.x += g.pop();
                            g.path.lineTo(g.x, g.y);
                            g.k = 0;
                            break;
                        case 7:
                            g.y += g.pop();
                            g.path.lineTo(g.x, g.y);
                            g.k = 0;
                            break;
                        case 8:// x1 y1 x2 y2 x3 y3 rcurveto
                            y3 = g.pop();
                            x3 = g.pop();
                            y2 = g.pop();
                            x2 = g.pop();
                            y1 = g.pop();
                            x1 = g.pop();
                            g.path.curveTo(g.x + x1, g.y + y1, g.x + x1 + x2, g.y + y1 + y2, g.x + x1 + x2 + x3, g.y + y1 + y2 + y3);

                            g.x += x1 + x2 + x3;
                            g.y += y1 + y2 + y3;
                            g.k = 0;
                            break;
                        case 9:
                            g.path.closePath();
                            g.k = 0;
                            break;
                        case 10:
                            int n = (int) g.pop();
                            if (this.subrs[n] == null)
                                System.out.println("No subroutine #" + n);
                            else
                            {
                                this.callcount++;
                                if (this.callcount > 10)
                                    System.out.println("Call stack too large");
                                else
                                    parse(n, this.subrs[n], g, new IntArray(50));
                                this.callcount--;
                            }
                            break;
                        case 11:
                            return;
                        case 12:
                            op = ((int) cs[loc++]) & 0xff;
                            history.add(-op);
                            if (false && DEBUG)
                                System.out.print("+" + op + ", ");
                            if (op == 6)
                            {
                                char b = (char) g.pop();
                                char a = (char) g.pop();
                                float y = g.pop();
                                float x = g.pop();
                                combineAccent(x, y, a, b, g);
                                g.k = 0;
                            } else if (op == 7)
                            {
                                g.h = g.pop();
                                g.w = g.pop();
                                g.y = g.pop();
                                g.x = g.pop();
                                g.k = 0;
                            } else if (op == 12)
                            {
                                float b = g.pop();
                                float a = g.pop();
                                g.stack[(g.k++)] = (a / b);
                            } else if (op == 33)
                            {
                                g.y = g.pop();
                                g.x = g.pop();
                                g.path.lineMoveTo(g.x, g.y);
                                g.k = 0;
                            } else if (op == 0)
                                g.k = 0;
                            else if (op == 1)
                                g.k = 0;
                            else if (op == 2)
                                g.k = 0;
                            else if (op == 16)
                            {
                                int cn = (int) g.pop();
                                int countargs = (int) g.pop();
                                switch (cn)
                                {
                                    case 0:
                                        g.psStack[g.psk++] = g.pop();
                                        g.psStack[g.psk++] = g.pop();
                                        g.pop();
                                        break;
                                    case 3:
                                        g.psStack[g.psk++] = 3.0f;
                                        break;
                                    default:
                                        for (int i = 0; i > countargs; i--)
                                            g.psStack[g.psk++] = g.pop();
                                        break;
                                }
                            } else if (op == 17)
                            {
                                g.stack[g.k++] = g.psStack[g.psk - 1];
                                g.psk--;
                            } else
                                throw new RuntimeException("Bad command (" + op + ")");
                            break;// zoubi add, a break is obviously needed here !!!
                        case 13:
                            g.w = g.pop();
                            g.h = 0.0f;
                            g.x = g.pop();
                            g.y = 0.0f;
                            g.k = 0;
                            break;
                        case 14:
                            break;
                        case 21:// x y rmoveto
                            if (DEBUG)
                                System.out.println(Zen.Array.String(history.array()));
                            g.y += g.pop();
                            g.x += g.pop();
                            if (history.endsWith(10, 21) || history.endsWith(10, 12, -12, 22))// cyberlibris lineto pattern
                                g.path.lineMoveTo(g.x, g.y);// 2014-02-13 (cyberlibris patch)
                            else
                                g.path.moveTo(g.x, g.y);
                            g.k = 0;
                            break;
                        case 22: // x hmoveto
                            if (DEBUG)
                                System.out.println(Zen.Array.String(history.array()));
                            g.x += g.pop();

                            if (history.endsWith(10, 22) || history.endsWith(10, 12, -12, 22))// cyberlibris and publygen lineto pattern
                                g.path.lineMoveTo(g.x, g.y);// 2014-02-13 (cyberlibris patch) and 2018-01-06 (Publygen)
                            else
                                g.path.moveTo(g.x, g.y);
                            g.k = 0;
                            break;
                        case 30:
                            x3 = g.pop();
                            y2 = g.pop();
                            x2 = g.pop();
                            y1 = g.pop();
                            x1 = y3 = 0.0f;
                            g.path.curveTo(g.x, g.y + y1, g.x + x2, g.y + y1 + y2, g.x + x2 + x3, g.y + y1 + y2);
                            g.x += x2 + x3;
                            g.y += y1 + y2;
                            g.k = 0;
                            break;
                        case 31:
                            y3 = g.pop();
                            y2 = g.pop();
                            x2 = g.pop();
                            x1 = g.pop();
                            y1 = x3 = 0.0f;
                            g.path.curveTo(g.x + x1, g.y, g.x + x1 + x2, g.y + y2, g.x + x1 + x2, g.y + y2 + y3);
                            g.x += x1 + x2;
                            g.y += y2 + y3;
                            g.k = 0;
                            break;
                        default:
                            if (op < 32)
                                Log.warn(this, ".parse - bad command (" + op + ")");
                            break;
                    }
                }
            }
        if (DEBUG)
            System.out.print("exit, ");
    }

    private void combineAccent(float x, float y, char base, char accent, AFFGlyph g)
    {
        if (DEBUG)
            Log.debug(this, ".combineAccent - " + g.name + ": " + base + ", " + accent + ", " + x + ", " + y);

        String sBase = g.name.length() > 0 ? g.name.substring(0, 1) : null;
        String sAccent = g.name.length() > 1 ? g.name.substring(1) : null;

        if (this.name2outline.hasnt(sBase))
            sBase = null;
        if (this.name2outline.hasnt(sAccent))
            sAccent = null;

        Path3 pAccent = sAccent == null ? getOutline(accent, getWidth(accent, null)).pathCopy() : getOutline(sAccent, 1).pathCopy();
        Path3 pBase = sBase == null ? getOutline(base, getWidth(base, null)).pathCopy() : getOutline(sBase, 1).pathCopy();

        pAccent = pAccent.transform(transform.inverse()).translate(0, y);
        pBase = pBase.transform(transform.inverse());

        g.path.append(pAccent, false);
        g.path.append(pBase, false);

        // Log.debug(this, ".combineAccent - x=" + x + ", y=" + y + ", " + base +
        // "[" + baseName + "], " + accent + "[" + accentName + "]");
        // this.debugChar(pBase, baseName + "-combineAccent", 1);
        // this.debugChar(pAccent, accentName + "-combineAccent", 1);
        // this.debugChar(g.path, baseName + accentName + "-combineAccent", 1);
    }

    private String[] readEncoding(byte[] d)
    {
        byte[][] ary = readArray(d, "Encoding", "def");
        String[] res = new String[256];
        for (int i = 0; i < ary.length; i++)
            if (ary[i] != null)
                res[i] = ary[i][0] == '/' ? new String(ary[i]).substring(1) : new String(ary[i]);
            else
                res[i] = null;
        return res;
    }

    private byte[][] readSubrs(byte[] d)
    {
        return readArray(d, "Subrs", "index");
    }

    private byte[][] readArray(byte[] d, String key, String end)
    {
        int i = AFF.FindSlashName(d, key);
        if (i < 0)
            return new byte[0][];

        PSParser psp = new PSParser(d, i);
        String type = psp.read();

        type = psp.read();
        if (type.equals("StandardEncoding"))
        {
            byte[][] stdenc = new byte[FontSupport.standardEncoding.length][];
            for (i = 0; i < stdenc.length; i++)
                stdenc[i] = FontSupport.getName(FontSupport.standardEncoding[i]).getBytes();
            return stdenc;
        }
        int len = Integer.parseInt(type);
        byte[][] out = new byte[len][];
        while (true)
        {
            String s = psp.read();
            // Log.debug(this, ".readArray - " + s);
            if (s.equals("dup"))
            {
                String nb = psp.read();
                String elt = psp.read();
                // Log.debug(this, ".readArray - id=" + nb + ", elt=" + elt);
                int index = Nb.Int(nb, -1);
                if (index < 0)
                    break;
                byte[] line = elt.getBytes();
                if (Character.isDigit(elt.charAt(0)))
                {
                    int hold = Integer.parseInt(elt);
                    String special = psp.read();
                    if ((special.equals("-|")) || (special.equals("RD")))
                    {
                        psp.loc++;
                        line = psp.getNEncodedBytes(hold, this.password, this.lenIV);
                    }
                }
                out[index] = line;
            } else if (s.equals(end))
                break;
        }
        return out;
    }

    public static byte[] Decrypt(byte[] d, int start, int end, int key, int skip)
    {
        if (end - start - skip < 0)
            skip = 0;
        byte[] o = new byte[end - start - skip];
        int r = key;

        int c1 = 52845;
        int c2 = 22719;
        for (int ipos = start; ipos < end; ipos++)
        {
            int c = d[ipos] & 0xFF;
            int p = (c ^ r >> 8) & 0xFF;
            r = (c + r) * c1 + c2 & 0xFFFF;
            if (ipos - start - skip >= 0)
                o[(ipos - start - skip)] = (byte) p;
        }
        return o;
    }

    private static byte[] ReadASCII(byte[] data, int start, int end)
    {
        byte[] o = new byte[(end - start) / 2];

        int count = 0;
        int bit = 0;

        for (int loc = start; loc < end; loc++)
        {
            char c = (char) (data[loc] & 0xFF);
            byte b = 0;

            if ((c >= '0') && (c <= '9'))
                b = (byte) (c - '0');
            else if ((c >= 'a') && (c <= 'f'))
                b = (byte) (10 + (c - 'a'));
            else
            {
                if ((c < 'A') || (c > 'F'))
                    continue;
                b = (byte) (10 + (c - 'A'));
            }

            if (bit++ % 2 == 0)
                o[count] = (byte) (b << 4);
            else
            {
                count++;
                o[count] = (byte) (o[count] | b);
            }
        }

        return o;
    }

    private static boolean IsASCII(byte[] data, int start)
    {
        for (int i = start; i < start + 4; i++)
        {
            char c = (char) (data[i] & 0xFF);
            if ((c >= '0') && (c <= '9'))
                continue;
            if ((c >= 'a') && (c <= 'f'))
                continue;
            if ((c < 'A') || (c > 'F'))
                return false;
        }
        return true;
    }

    private static int FindSlashName(byte[] d, String name)
    {
        for (int i = 0; i < d.length; i++)
        {
            if (d[i] != 47)
                continue;
            boolean found = true;
            for (int j = 0; j < name.length(); j++)
                if (d[(i + j + 1)] != name.charAt(j))
                {
                    found = false;
                    break;
                }
            if (found)
                return i;
        }

        return -1;
    }

    private static StringMap<Object> ReadChars(byte[] d, int password, int lenIV)
    {
        StringMap<Object> map = new StringMap<>();
        int i = FindSlashName(d, "CharStrings");

        // BoyerMoore bm = new BoyerMoore("CharStrings");
        // IntArray indexes = bm.searchAll(d, 0, d.length);
        // Log.debug(this, ".readChars - CharStrings at "+indexes);

        if (i < 0)
            return map;
        PSParser psp = new PSParser(d, i);
        while (true)
        {
            String s = psp.read();
            // Log.debug(this, ".readChars - "+s);
            char c = s.charAt(0);
            if (c == '/')
            {
                int len = Integer.parseInt(psp.read());
                String go = psp.read();
                if ((go.equals("-|")) || (go.equals("RD")))
                {
                    psp.loc++;
                    byte[] line = psp.getNEncodedBytes(len, password, lenIV);
                    map.put(s.substring(1), line);
                }
            } else if (s.equals("end"))
                break;
        }
        return map;
    }

    private static void DebugChar(AFFGlyph glyph, IntArray hist, String filename, float scale, String fontname)
    {
        try
        {
            Image3 image = new Image3(100, 100);
            Graphics3 g = image.graphics();
            Transform3 tm = new Transform3(0.1 * scale, 0, 0, 0.1 * scale, 5, 5);
            Shape shape = tm.transform(glyph.path);
            g.setColor(Color3.WHITE.alpha(0.2));
            g.fill(tm.transform(glyph.path));
            g.setColor(Color3.WHITE);
            g.setStroke(Stroke3.LINE);
            g.fill(tm.transform(new Circle3(glyph.x, glyph.y, 20)));
            g.draw(shape);
            g.dispose();

            File3 file = File3.Desk("tmp/" + fontname + "/" + filename + ".png");
            file.mkdirs();
            ImageIO.write(image, "png", file);
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public static AFF ParseFont(String fontname, PDFStream stream)
    {
        return new AFF(fontname).parse(stream);
    }

}
