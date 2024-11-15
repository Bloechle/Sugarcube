/*
 * $Id: TTFFont.java,v 1.10 2009/02/23 15:29:19 tomoke Exp $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package sugarcube.formats.pdf.reader.pdf.node.font;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Unicodes;
import sugarcube.common.graphics.geom.Circle3;
import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.font.ttf.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

public class ReaderTTF extends FontReader
{
    private TTF font;
    private float unitsPerEm;
    private boolean debug = false;
    private boolean isCID = false;
    private CmapTable cmap;
    private PostTable post;

    /**
     * create a new TrueTypeFont object based on a description of the font from
     * the PDF file. If the description happens to contain an in-line true-type
     * font file (under key "FontFile2"), use the true type font. Otherwise, parse
     * the description for key information and use that to generate an appropriate
     * font.
     */
    public ReaderTTF(FontDescriptor desc, TTF font)
    {
        super("Type1CFont", desc);
        this.isCID = desc.pdfFont.isCID();
        this.font = font;
        debug = false;
        try
        {
            // read the units per em from the head table
            HeadTable head = (HeadTable) font.getTable("head");
            unitsPerEm = head.getUnitsPerEm();
            this.cmap = (CmapTable) font.getTable("cmap");
            this.post = (PostTable) font.getTable("post");
            Log.debug(debug && cmap == null, this, " - no cmap table: " + desc.fontname);
            Log.debug(debug && post == null, this, " - no post table: " + desc.fontname);
        } catch (Exception e)
        {
            e.printStackTrace();
            // font = Font3.read(Font.TRUETYPE_FONT, stream.inputStream(), null);
        }
    }

    @Override
    public Transform3 transform()
    {
        return new Transform3();
    }

    @Override
    public PDFGlyph outline(String name, Unicodes uni, int code)
    {
        return outline(name, uni, code, false);
    }

    public PDFGlyph outline(String name, Unicodes uni, int code, boolean resource)
    {
        // Log.debug(this,
        // ".outline - encoding=" + encoding.encoding() + "[Identity=" +
        // isIdentityEncoding + "], font=" + this.fontDescriptor.pdfFont.fontname()
        // + ", name=" + name + ", uni=" + uni + ", code=" + code);
        PDFGlyph outline = null;

        if (post != null && post.getGlyphNameIndex(name) > 0 && (outline = getOutline(name)) != null)
        {
            return outline.reverseY();
        }

        // first, CID encoding if active
        if (outline == null && this.isCID)
        {
            outline = getOutline(descriptor.pdfFont.cid2gid.get(code, code));
        }

        // Log.debug(debug, this, ".outline - getOutline: " + (outline == null ?
        // "null" : ":-)"));
        // second, character code mapping
        if (outline == null)
            outline = getOutlineFromCMaps((char) code);

        // Log.debug(debug, this, ".outline - getOutlineFromCMaps: " + (outline ==
        // null ? "null" : ":-)"));
        // third, character name mapping
        if (outline == null)
            outline = getOutline(name);

        // Log.debug(debug, this, ".outline - getOutline: " + (outline == null ?
        // "null" : ":-)"));
        // don't uncomment... or add some check about encoding
        // //fourth, try wider character code mapping
        // if (outline == null)
        // outline = getOutline((char) code);

        // fifth, try to recover by character unicode
//    if (outline == null && uni != null && uni.length() == 1)
//      outline = getOutline(uni.charAt(0));

        // Log.debug(debug = true, this, ".outline - getOutline: " + (outline ==
        // null ? "null" : ":-)") + ", fontname=" +
        // this.fontDescriptor.pdfFont.fontname);

        if (outline == null && descriptor.pdfFont.fontname.startsWith("Wingdings"))
            outline = new PDFGlyph(new Path3(0, 0));

        return outline == null ? null : outline.reverseY();
    }

    /**
     * Get the outline of a character given the character code
     */
    protected synchronized PDFGlyph getOutline(char src)
    {
        if (src < 0)
            return null;
        if (cmap == null)// if there are no cmaps, this is (hopefully) a cid-mapped
            // font, so just trust the value we were given for src
            return null;// getOutline((int) src);//zoubi commented
        CMap[] maps = cmap.getCMaps();
        for (int i = 0; i < maps.length; i++)
        {
            int gid = maps[i].map(src);
            Log.debug(debug, this, ".getOutline(char src) - CMAP found: char=" + src + ", idx=" + gid);
            if (gid != 0)
                return getOutline(gid);
        }
        return null;
    }

    /**
     * lookup the outline using the CMAPs, as specified in 32000-1:2008, 9.6.6.4,
     * when an Encoding is specified.
     */
    protected synchronized PDFGlyph getOutlineFromCMaps(char val)
    {
        if (val < 0 || cmap == null)
            return null;
        // try maps in required order of (3, 1), (1, 0)
        CMap map = cmap.getCMap((short) 3, (short) 1);
        if (map == null)
            map = cmap.getCMap((short) 1, (short) 0);
        if (map == null)// no cmap
            return null;
        int gid = map.map(val);

        // Log.debug(this, ".getOutlineFromCMaps - val=" + (int) val + ", idx=" +
        // idx);
        if (gid != 0)
            return getOutline(gid);
        return null;
    }

    /**
     * Get the outline of a character given the character name
     */
    protected synchronized PDFGlyph getOutline(String name)
    {
        if (name == null)
            return null;
        int gid;
        if (post != null)
        {
            gid = post.getGlyphNameIndex(name);
            Log.debug(debug, this, ".getOutline(String name): name=" + name + ", idx=" + gid);
            if (gid != 0)
                return getOutline(gid);
            return null;
        }

        // Integer res = AdobeGlyphList.getGlyphNameIndex(name);
        // if (res != null)
        // {
        // idx = res;
        // return getOutlineFromCMaps((char) idx);
        // }
        return null;
    }

    protected synchronized PDFGlyph getOutline(int gid)
    {
        if (gid < 0)
            return null;
        // find the glyph itself
        GlyfTable glyf = (GlyfTable) font.getTable("glyf");
        if (glyf == null)
        {
            Log.debug(this, ".getOutline - glyf table not found: gid=" + gid);
            return null;
        }
        Glyf g = glyf.getGlyph(gid);

        GeneralPath gp = null;

        if (g instanceof GlyfSimple)
            gp = renderSimpleGlyph((GlyfSimple) g);
        else if (g instanceof GlyfCompound)
            gp = renderCompoundGlyph(glyf, (GlyfCompound) g);
        if (g != null && gp == null)
        {
            // the glyph exists but has no shape, such as blank
            gp = new GeneralPath();
            gp.moveTo(0, 0);
        }

        // calculate the advance
        HmtxTable hmtx = (HmtxTable) font.getTable("hmtx");
        float advance = (float) hmtx.getAdvance(gid) / (float) unitsPerEm;

        // // scale the glyph to match the desired advance
        // float widthfactor = width / advance;
        // the base transform scales the glyph to 1x1
        AffineTransform at = AffineTransform.getScaleInstance(1 / unitsPerEm, 1 / unitsPerEm);
        // at.concatenate(AffineTransform.getScaleInstance(widthfactor, 1));
        if (gp != null)
            gp.transform(at);
        return gp == null ? null : new PDFGlyph(new Path3(gp), advance);
    }

    protected GeneralPath renderSimpleGlyph(GlyfSimple g)
    {
        // the current contour
        int curContour = 0;
        // the render state
        RenderState rs = new RenderState();
        rs.gp = new GeneralPath();
        for (int i = 0; i < g.getNumPoints(); i++)
        {
            PointRec rec = new PointRec(g, i);

            if (rec.onCurve)
                addOnCurvePoint(rec, rs);
            else
                addOffCurvePoint(rec, rs);

            // see if we just ended a contour
            if (i == g.getContourEndPoint(curContour))
            {
                curContour++;

                if (rs.firstOff != null)
                    addOffCurvePoint(rs.firstOff, rs);

                if (rs.firstOn != null)
                    addOnCurvePoint(rs.firstOn, rs);

                rs.firstOn = null;
                rs.firstOff = null;
                rs.prevOff = null;
            }
        }

        return rs.gp;
    }

    protected GeneralPath renderCompoundGlyph(GlyfTable glyf, GlyfCompound g)
    {
        GeneralPath gp = new GeneralPath();
        if (g == null)
            Log.debug(this, ".renderCompoundGlyph - null glyph compound in " + descriptor.pdfFont.fontID());
        else
        {
            int size = g.getNumComponents();
            // Log.debug(this, ".renderCompoundGlyph: numComp=" + size);
            for (int i = 0; i < size; i++)
            {
                // find and render the component glyf
                Object obj = glyf.getGlyph(g.getGlyphIndex(i));

                if (obj instanceof GlyfSimple)
                {
                    GlyfSimple gs = (GlyfSimple) obj;
                    GeneralPath path = renderSimpleGlyph(gs);

                    // multiply the translations by units per em
                    double[] matrix = g.getTransform(i);
//          if (i == 1 && size == 2 && Math.abs(matrix[4]) < 1)
//          {
//            Rectangle2D r0 = gp.getBounds2D();
//            Rectangle2D r1 = path.getBounds2D();
//            matrix[4] = (r0.getWidth() - r1.getWidth()) / 2;
//            Log.debug(this,
//                ".renderCompoundGlyph - accent adjusted: x0=" + r0.getMinX() + ", x1=" + r1.getMinX() + ", matrix=" + Arrays.toString(matrix));
//          }

                    // Log.debug(this, ".renderCompoundGlyph: matrix=" +
                    // Arrays.toString(matrix));
                    // transform the path
                    path.transform(new AffineTransform(matrix));
                    // add it to the global path
                    gp.append(path, false);
                } else
                {
                    // Log.debug(this, ".renderCompoundGlyph: compound");
                    GlyfCompound gs = (GlyfCompound) obj;
                    GeneralPath path = renderCompoundGlyph(glyf, gs);

                    // multiply the translations by units per em
                    double[] matrix = g.getTransform(i);

                    // transform the path
                    path.transform(new AffineTransform(matrix));

                    // add it to the global path
                    gp.append(path, false);
                }
            }
        }
        return gp;
    }

    private void addOnCurvePoint(PointRec rec, RenderState rs)
    {
        // if the point is on the curve, either move to it,
        // or draw a line from the previous point
        if (rs.firstOn == null)
        {
            rs.firstOn = rec;
            rs.gp.moveTo(rec.x, rec.y);
        } else if (rs.prevOff != null)
        {
            rs.gp.quadTo(rs.prevOff.x, rs.prevOff.y, rec.x, rec.y);
            rs.prevOff = null;
        } else
            rs.gp.lineTo(rec.x, rec.y);
    }

    /**
     * add a point off the curve
     */
    private void addOffCurvePoint(PointRec rec, RenderState rs)
    {
        if (rs.prevOff != null)
        {
            PointRec oc = new PointRec((rec.x + rs.prevOff.x) / 2, (rec.y + rs.prevOff.y) / 2, true);
            addOnCurvePoint(oc, rs);
        } else if (rs.firstOn == null)
            rs.firstOff = rec;
        rs.prevOff = rec;
    }

    class RenderState
    {
        // the shape itself
        GeneralPath gp;
        // the first off and on-curve points in the current segment
        PointRec firstOn;
        PointRec firstOff;
        // the previous off and on-curve points in the current segment
        PointRec prevOff;
    }

    @Override
    public String sticker()
    {
        return "ReaderTTF";
    }

    @Override
    public String toString()
    {
        return "ReaderTTF" + "\nisCID[" + this.isCID + "]";
    }

    /**
     * a point on the stack of points
     */
    class PointRec
    {
        int x;
        int y;
        boolean onCurve;

        public PointRec(int x, int y, boolean onCurve)
        {
            this.x = x;
            this.y = y;
            this.onCurve = onCurve;
        }

        public PointRec(GlyfSimple g, int idx)
        {
            x = g.getXCoord(idx);
            y = g.getYCoord(idx);
            onCurve = g.onCurve(idx);
        }
    }

    @Override
    public void paint(Graphics3 g, PDFDisplayProps props)
    {
        float scale = props.displayScaling;
        int size = (int) (36 * scale);
        int d = size / 2;
        int x = d;
        int y = size + d;

        CmapTable cmap = (CmapTable) font.getTable("cmap");
        if (cmap == null)
        {
            Log.info(this, ".paint - no CMap table");
        }

        // CMap[] maps = cmap.getCMaps();

        GlyfTable table = (GlyfTable) font.getTable("glyf");

        g.setColor(Color.BLACK);
        for (int i = 0; i < 256; i++)
        {

            if (x > g.width() - (size + size / 2))
            {
                x = d;
                y += (d + size);
            }

            PDFGlyph glyph = this.outline("", null, i);
            if (glyph == null)
                continue;

            g.setColor(Color.BLACK);

            g.fill(new Transform3(size, 0, 0, size, x, y).transform(glyph.path));
            g.setColor(Color.GREEN.darker());
            g.fill(new Circle3(x, y, 2));

            g.draw(new Line3(x, y, x + (int) (glyph.width()), y));
            g.graphics().drawString("" + i, x, y + size / 2);
            x += (d + 3 * size / 2);
        }
    }
}
