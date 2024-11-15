package sugarcube.formats.ocd.analysis.text;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Shape3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.interfaces.Glyph;
import sugarcube.formats.ocd.objects.*;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class OCDDoctor
{
    public static void CleanWhiteBg(OCDPage ocdPage)
    {
        // primitives need to be z-ordered
        // removes white background since it is by default white !!!

        Iterator<OCDPaintable> paintit = ocdPage.content().iterator();
        boolean onemore = true;
        while (paintit.hasNext() && onemore)
        {
            onemore = false;
            OCDPaintable paint = paintit.next();
            if (paint.isPath())
            {
                OCDPath path = paint.asPath();
                onemore = true;
                if (path.strokeWidth() > 0)
                {
                    Color3 stroke = path.strokeColor();
                    if (!stroke.isWhite() && !stroke.isTransparent())
                        onemore = false;
                }
                if (onemore)
                {
                    Color3 fill = path.fillColor();
                    if (!fill.isWhite() && !fill.isTransparent())
                        onemore = false;
                }
            }
            if (onemore)
                paintit.remove();
        }
    }

    public static void CleanClipping(OCDPage page)
    {
        for (OCDPaintable node : page.content().children())
        {
            if (node.isImage())
            {
                OCDImage image = node.asImage();

                OCDClip clip = image.clip();
                if (clip != null)
                    if (clip.path().isBBox(0.01))
                    {
                        Rectangle3 box = image.bounds();
                        Rectangle3 clipBox = clip.bounds();
                        if (clipBox.contains(box) || clipBox.equals(box, 0.5))
                        {
                            image.setClipID(OCDClip.ID_PAGE);
                        }

                    } else
                    {
                        Image3 src = image.image3();
                        // deactivated since it gives poor results
                        boolean paletted = false;
                        if (paletted && src.isPaletted(16))
                        {
                            Transform3 tm = image.transform();
                            int scale = 2;
                            Image3 res = src.scale(scale).softClip(tm.inv().transform(clip.path()).scale(scale));
                            image.setClipID(OCDClip.ID_PAGE);
                            image.setImage(res, -1);
                            image.setTransform(tm.sx() / scale, tm.hy(), tm.hx(), tm.sy() / scale, tm.x(), tm.y());
                            // res.write(File3.userDesktop("/tmp/" + Base.x32.random8() +
                            // ".png"));
                        }
                    }
            }
        }
    }

    public static void CleanOcclusion(OCDPage page)
    {
        Set3<OCDText> hidden = new Set3<>();
        int removedChars = 0;

        // copy the original list since we do not want to reverse the OCD primitives
        List3<OCDPaintable> nodes = new List3<>(page.content().children());

        Collections.reverse(nodes);

        List3<Path3> masks = new List3<>();
        for (OCDPaintable node : nodes)
        {
            if (node.isText())
            {
                OCDText text = node.asText();
                for (Path3 mask : masks)
                {
                    if (mask.contains(node.bounds()))
                    {
                        hidden.add(text);
                        removedChars += text.nbOfChars();
                        Log.debug(OCDDoctor.class, ".cleanOcclusion - hidden text: " + text.string() + " at page " + page.nbOfPages());
                        break;
                    }
                }
            } else if (node.isPaintableLeaf())
            {
                // too much time consuming
                OCDPaintableLeaf leaf = node.asPaintableLeaf();
                if (leaf.isOpaque())
                {
                    Shape3 shape = leaf.shape();
                    Path3 path = shape instanceof Path3 ? (Path3) shape : new Path3(shape);
                    OCDClip clip = leaf.clip();
                    if (clip != null)
                    {
                        Path3 clipPath = clip.path();
                        Rectangle2D pathBox = path.getBounds2D();
                        if (!clipPath.contains(pathBox))
                        {
                            if (clipPath.intersects(pathBox))
                            {
                                Area area = new Area(path);
                                area.intersect(new Area(clipPath));
                                path = new Path3(area);
                            } else
                                path = null;
                        }
                    } else if (path != null)
                    {
                        masks.add(path);
                    }
                }
            }
        }

        Iterator<OCDPaintable> paintit = page.content().children().iterator();
        while (paintit.hasNext())
        {
            OCDPaintable node = paintit.next();
            if (hidden.contains(node))
                paintit.remove();
        }

        if (removedChars > 0)
        {
            Log.debug(OCDDoctor.class, ".cleanOcclusion - page " + page.pageNb() + " cleaned chars: " + removedChars);
        }
    }

    public static OCDTextBlock CleanWhiteCharSpaces(OCDTextBlock block)
    {
        for (OCDTextLine line : block)
        {
            OCDText last = null;
            for (OCDText text : line)
            {
                Glyph[] glyphs = text.glyphs();
                for (int i = 1; i < glyphs.length; i++)
                {
                    String c = glyphs[i].code();
                    if (c.length() == 1 && Character.isWhitespace(c.charAt(0)) && text.charSpace(i - 1) != 0)
                    {
                        float[] cs = text.charSpaces(true);
                        cs[i] = cs[i] + cs[i - 1];
                        cs[i - 1] = 0;
                        text.setCharSpaces(cs);
                    }
                }
                last = text;
            }
            SortCX(line.children());
            if (last != null)
                last.setLastCharSpace(0);
        }
        return block;
    }

    public static void SortCX(List<? extends OCDNode> list)
    {
        Collections.sort(list, Comparator.comparingDouble(a -> a.bounds().cx()));
    }


    public static void GenerateIDs(OCDPage page)
    {
        int index = 0;
        for (OCDImage image : page.images())
            if (!image.hasID())
                image.setID("image" + (index++));

        index = 0;
        for (OCDTextBlock block : page.blocks())
            if (!block.hasID())
                block.setID("p" + (index++));

        index = 0;
        for (OCDContent content : page.subContents())
            if (!content.hasID())
                content.setID("sub" + (index++));
    }

}
