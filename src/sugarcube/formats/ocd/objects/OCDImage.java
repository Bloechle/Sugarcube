package sugarcube.formats.ocd.objects;

import javafx.scene.image.Image;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.Base;
import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.system.io.Class3;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDPageContent.State;
import sugarcube.formats.ocd.resources.RS_OCD;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;

public class OCDImage extends OCDPaintableLeaf
{
    // public static final int[] PNG_MAGIC = new int[]{137, 80, 78, 71, 13, 10,
    // 26, 10};

    public static final String TAG = "image";

    public static final String FORMAT_MP3 = "mp3";
    public static final String FORMAT_MP4 = "mp4";
    public static final String FORMAT_PNG = "png";
    public static final String FORMAT_JPG = "jpg";
    public static final String VIEW = "view";

    protected String filename;
    protected int width = 0;
    protected int height = 0;
    protected byte[] data = null;
    protected boolean modified = false;

    public OCDImage()
    {
        super(TAG);
    }

    public OCDImage(OCDNode parent)
    {
        super(TAG, parent);
    }

    @Override
    public boolean modified()
    {
        return modified;
    }

    public int dpiX()
    {
        OCDDocument ocd = this.doc();
        float dpi = ocd == null ? OCD.DPI : ocd.dpi;
        return (int) Math.abs(dpi / xScale);
    }

    public int dpiY()
    {
        OCDDocument ocd = this.doc();
        float dpi = ocd == null ? OCD.DPI : ocd.dpi;
        return (int) Math.abs(dpi / yScale);
    }

    public long dataSize()
    {
        return data == null ? 0 : data.length;
    }

    public boolean hasData()
    {
        return this.data != null && this.data.length > 0;
    }

    public void setImageView(Rectangle3 box)
    {
        this.filename = VIEW;
        this.width = box.intWidth();
        this.height = box.intHeight();
        this.setTransform(1, 0, 0, 1, box.x, box.y);
    }

    public boolean isView()
    {
        return this.filename.equals(VIEW);
    }

    public OCDImage modify()
    {
        this.modified = true;
        return this;
    }

    private static BufferedImage iconImage(boolean isVideo)
    {
        try
        {
            return ImageIO.read(Class3.Stream(RS_OCD.class, isVideo ? "VideoIcon.png" : "AudioIcon.png"));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void setFromBytes(String name, byte[] data, Line3 extent)
    {
        try
        {
            boolean isVideo = name.endsWith(".mp4");
            boolean isAudio = name.endsWith(".mp3");
            BufferedImage bi = data == null ? null : (isVideo || isAudio ? iconImage(isVideo) : ImageIO.read(new ByteArrayInputStream(data)));
            if (bi != null)
            {
                setDimension(bi.getWidth(), bi.getHeight());
                setData(data);
                setFilename(OCD.RenameFile(name));
                setXY(extent.x1, extent.y1);
                setScale(extent.width() / width(), extent.height() / height());
                needID();
            }
        } catch (Exception e)
        {
            Log.debug(this, ".setFromFile - image reading failed: " + name);
        }
    }

    public void setFromFile(File3 file, Line3 extent)
    {
        setFromBytes(file.name(), file.bytes(), extent);
    }

    public void setImage(String filename, int width, int height, byte[] data)
    {
        this.filename = filename;
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public void updateImage(BufferedImage bi)
    {
        this.setImage(filename, bi, 1);
    }

    public final void setImage(BufferedImage bi, double compression)
    {
        this.setImage(null, bi, compression);
    }

    public final void setImage(String filename, BufferedImage bi, double compression)
    {
        Image3 image = bi instanceof Image3 ? (Image3) bi : new Image3(bi);

        boolean isPNG = compression <= 0 || !image.isFullyOpaque() || image.isPaletted(16);

        if (filename != null)
        {
            filename = File3.Extense(filename, isPNG ? ".png" : ".jpg");
        }

        if (!isPNG && image.hasTransparency())
        {
            // jpg doesn't like RGBA bufferedimage very much...
            Image3 opaque = new Image3(image.width(), image.height(), false);
            opaque.graphics().draw(image);
            image = opaque;
        }

        this.width = image.getWidth();
        this.height = image.getHeight();
        ByteArrayOutputStream stream = new ByteArrayOutputStream(image.getWidth() * image.getHeight() / 10);
        try
        {
            image.write(stream, isPNG ? -1 : compression);
            this.data = stream.toByteArray();
            this.filename = filename == null ? filename(isPNG ? ".png" : ".jpg") : filename;
            this.name = filename;
        } catch (Exception ex)
        {
            Log.warn(this, ".setImage - " + ex + ": filename=" + filename + " width=" + image.getWidth() + " height=" + image.getHeight() + " filesize=");
            ex.printStackTrace();
        }
    }

    public static boolean hasLessColorThan(BufferedImage image, int nbOfColors)
    {
        HashSet<Integer> colors = new HashSet<>();
        for (int y = 0; y < image.getHeight(); y++)
            for (int x = 0; x < image.getWidth(); x++)
            {
                colors.add(image.getRGB(x, y));
                if (colors.size() > nbOfColors)
                    return false;
            }
        return true;
    }

    @Override
    public Collection<? extends OCDNode> writeAttributes(Xml xml)
    {
        State state = this.page().content(false).state();
        this.writeXmlID(xml);
        this.writeXmlName(xml);
        this.writeXmlClip(xml, state);
        this.writeXmlBlend(xml, state);
        this.writeXmlZOrder(xml, state);
        xml.write("src", this.filename);
        xml.write("width", width);
        xml.write("height", height);

        Color3 color = this.fillColor();
        if (!color.isOpaque())
            xml.write("alpha", color.alpha());

        this.writeXmlTransform(xml, state);
        this.props.writeAttributes(xml);
        return this.children();
    }

    @Override
    public void readAttributes(DomNode dom)
    {
        State state = this.page().content(false).state();
        this.readXmlID(dom);
        this.readXmlName(dom);
        this.readXmlClip(dom, state);
        this.readXmlBlend(dom, state);
        this.readXmlZOrder(dom, state);
        this.filename = dom.value("src", dom.value("name"));// backcomp
        this.name = filename;
        this.width = dom.integer("width");
        this.height = dom.integer("height");

        if (dom.has("alpha"))
            this.fillColor = Color3.BLACK.alpha(dom.real("alpha", 1f)).argb();

        this.readXmlTransform(dom, state);
        this.props.readAttributes(dom);
    }

    @Override
    public String sticker()
    {
        return filename;
    }

    @Override
    public void paint(Graphics3 g, OCD.ViewProps props)
    {
        this.paintClip(g, props);
        this.paintBlend(g, props);
        if (props.paint_images)
            g.draw(props.use_colors ? image3(true) : gray(), transform());
    }

    @Override
    public void freeMemory()
    {
        if (!this.modified())
            this.data = null;
        else
            Log.debug(this, ".freeMemory - modified image kept in memory: " + filename);
    }

    public InputStream stream()
    {
        try
        {
            return data == null ? (isView() ? null : document().imageHandler.stream(filename)) : new ByteArrayInputStream(data);
        } catch (Exception e)
        {
            Log.warn(this, ".stream - exception: " + filename + ", document null=" + (document() == null));
            e.printStackTrace();
        }
        return null;
    }

    public byte[] data()
    {
        try
        {
            return data == null ? (isView() ? data : (data = document().imageHandler.data(filename))) : data;
        } catch (Exception e)
        {
            Log.warn(this, ".data - exception: " + filename + ", document null=" + (document() == null));
            e.printStackTrace();
        }
        return new byte[0];
    }

    public void setData(byte[] data)
    {
        this.data = data;
    }

    public void setStream(InputStream stream)
    {
        this.data = IO.ReadBytes(stream);
    }

    public boolean isPNG()
    {
        return filename != null && filename.endsWith("png");
    }

    public boolean isJPG()
    {
        return filename != null && filename.endsWith("jpg");
    }

    public boolean isMP4()
    {
        return filename != null && filename.endsWith("mp4");
    }

    public boolean isMP3()
    {
        return filename != null && filename.endsWith("mp3");
    }

    public void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public int w()
    {
        return width;
    }

    public int width()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int h()
    {
        return height;
    }

    public int height()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public void setDimension(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public float alpha()
    {
        return ((fillColor >> 24) & 0xff) / 255f;
    }

    public boolean hasAlpha()
    {
        return ((fillColor >> 24) & 0xff) != 255;
    }

    public void setAlpha(float alpha)
    {
        this.fillColor = Color3.BLACK.alpha(alpha).argb();
    }

    public javafx.scene.image.Image fxImage()
    {
        Image image = null;
        boolean isVideo = isMP4();
        boolean isAudio = isMP3();
        InputStream stream = isVideo || isAudio ? Class3.Stream(RS_OCD.class, isVideo ? "VideoIcon.png" : "AudioIcon.png") : stream();
        if (stream != null)
            try
            {
                image = new Image(stream);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        IO.Close(stream);
        return image;
    }

    public Image3 image3()
    {
        return Image3.Read(data());
    }

    public Image3 image3(boolean alpha)
    {
        Image3 image = image3();
        return image != null && hasAlpha() ? image.alpha(alpha()) : image;
    }

    public Image3 gray()
    {
        Image3 image = image3();
        Image3 gray = new Image3(image.width(), image.height(), Image3.TYPE_BYTE_GRAY);
        gray.graphics().draw(image);
        return gray;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public String filename()
    {
        return filename;
    }

    public String filename(String extension)
    {
        return filename == null ? Filename(width, height, data(), extension) : filename;
    }

    public static String Filename(int width, int height, byte[] data, String ext)
    {
        return width + "x" + height + "-" + HashString(data, width, height) + (ext.startsWith(".") ? ext : "." + ext);
    }

    @Override
    public boolean isOpaque()
    {
        Image3 image = image3();
        return image != null && image.isOpaque();
    }

    // do not use hashCode, since this object is a node used in trees and which
    // uses hashCodes !!!
    public static String HashString(byte[] data, int width, int height)
    {
        int size = data.length;
        long result = 1;
        result = 31 * result + size;
        result = 31 * result + width;
        result = 31 * result + height;
        for (int i = 0; i < size; i++)
            result = 31 * result + data[i];
        return Base.x32.get(result);
    }

    @Override
    public Rectangle3 bounds()
    {
        Rectangle3 r = shape().bounds();
        // Log.debug(this, ".bounds - bounds="+r);
        return r;
    }

    @Override
    public Rectangle3 bounds(boolean bbox)
    {
        return bounds();
    }

    @Override
    public Path3 shape()
    {
        // Area bounds = new Area(transform().transform(new Rectangle3(0, 0, width,
        // height)));
        // if (clip() != null)
        // bounds.intersect(new Area(clip()));
        // return new Path3(bounds);
        return new Path3(transform().transform(new Rectangle3(0, 0, width, height)));
    }

    public Line3 extent()
    {
        return this.bounds().extent();
    }

    @Override
    public void setExtent(Line3 l)
    {
        float sx = l.width() / width;
        float sy = l.height() / height;
        this.setTransform(sx, 0, 0, sy, l.x(), l.y());
    }

    // @Override
    public OCDImage normalize()
    {
        // this.clipPointer = OCDClip.REF_NONE;
        return this;
    }

    public OCDImage copyTo(OCDImage node)
    {
        super.copyTo(node);
        node.filename = this.filename;
        node.width = this.width;
        node.height = this.height;
        node.data = Zen.Array.copy(data());
        return node;
    }

    @Override
    public OCDImage copy()
    {
        return copyTo(new OCDImage(parent()));
    }

    public boolean write(String path)
    {
        return write(File3.Get(path));
    }

    public boolean write(File3 file)
    {
        InputStream stream = null;
        try
        {
            if (modified && data != null && data.length > 0)
                return file.write(data);
            if ((stream = document().imageHandler.stream(filename)) == null)
                return false;
            return file.write(stream, true);
        } catch (Exception e)
        {
            IO.Close(stream);
            e.printStackTrace();
        }
        return false;
    }

    public boolean writeToFolder(File3 folder)
    {
        return this.write(folder.get(filename()));
    }

    public boolean isBackground()
    {
        if (isRole("background"))
            return true;
        OCDNode node = parent();
        if (node instanceof OCDPageContent)
        {
            OCDPageContent content = (OCDPageContent) node;
            if (content.first() == this && bounds().overlapMax(node.page().bounds()) > 0.8)
                return true;
        }
        return false;
    }

    @Override
    public String toString()
    {
        byte[] data = data();
        return "Image[" + filename + "]" + "\nID[" + id() + "]" + "\nZOrder[" + this.zOrder + "]" + "\nClipID[" + clipID + "]" + "\nTransform["
                + this.transform() + "]" + "\nBounds" + this.bounds() + "\nClip" + (clip() == null ? "null" : clip().bounds()) + "\nSize[" + width + ","
                + height + "]" + "\nData[" + (data == null ? 0 : data().length / 1000) + "ko]" + "\nModified[" + this.modified() + "]";
    }
}
