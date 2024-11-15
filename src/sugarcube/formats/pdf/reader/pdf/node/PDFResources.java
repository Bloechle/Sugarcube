package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.formats.pdf.reader.Dexter;
import sugarcube.formats.pdf.reader.pdf.node.colorspace.PDFColorSpace;
import sugarcube.formats.pdf.reader.pdf.node.colorspace.PDFPattern;
import sugarcube.formats.pdf.reader.pdf.node.font.PDFFont;
import sugarcube.formats.pdf.reader.pdf.node.function.PDFFunction;
import sugarcube.formats.pdf.reader.pdf.node.image.PDFImage;
import sugarcube.formats.pdf.reader.pdf.node.shade.PDFShading;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary.PDFEntry;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;
import sugarcube.formats.pdf.reader.pdf.object.PDFStream;
import sugarcube.formats.pdf.reader.pdf.object.Reference;
import sugarcube.formats.pdf.reader.pdf.util.PDFNameMap;

import java.util.Collection;

public class PDFResources extends PDFNode
{
    protected PDFResources parentResources = null;
    public PDFNameMap<PDFFont> fonts = new PDFNameMap<>("Fonts");
    public PDFNameMap<PDFShading> shadings = new PDFNameMap<>("Shadings");
    public PDFNameMap<PDFColorSpace> colorSpaces = new PDFNameMap<>("ColorSpaces");
    public PDFNameMap<PDFPattern> patterns = new PDFNameMap<>("Patterns");
    public PDFNameMap<PDFNode> xObjects = new PDFNameMap<>("XObjects");
    public PDFNameMap<PDFExtGState> gStates = new PDFNameMap<>("ExtGStates");
    public PDFNameMap<PDFNode> properties = new PDFNameMap<>("Properties");
    // default values of ExtGState at the begining of a page
    protected PDFFunction defaultBG = null;
    protected PDFFunction defaultUCR = null;
    protected PDFFunction[] defaultTR = null;

    public PDFResources(PDFNode node)
    {
        super(Dexter.RESOURCES, node);
    }

    public void setParentPDFResources(PDFResources parent)
    {
        this.parentResources = parent;
    }

    /**
     * Used by PDFDocument to populate resources for page nodes and page...
     */
    public void populate(PDFDictionary map, PDFResources resources)
    {
        if (map.contains("Resources"))
            this.populate(map.get("Resources").toPDFDictionary());
        else
            this.populate(resources);
    }

    public void populate(PDFResources resources)
    {
        this.fonts = resources.fonts;
        this.shadings = resources.shadings;
        this.colorSpaces = resources.colorSpaces;
        this.patterns = resources.patterns;
        this.xObjects = resources.xObjects;
        this.gStates = resources.gStates;
        this.properties = resources.properties;
    }

    public void populate(PDFDictionary map)
    {
        // this.reference = map.reference();

        if (Dexter.DEBUG_MODE)
            Log.debug(this, ".populate - " + map);
        List3<PDFEntry> entries = map.entries();
        for (PDFEntry entry : entries)
        {
            PDFDictionary dico = entry.obj.toPDFDictionary();
            List3<PDFEntry> subEntries = dico.entries();
            // Log.debug(this, ".addResources - key="+key+", value="+dico);
            for (PDFEntry subEntry : subEntries)
            {
                String key = subEntry.key;
                PDFObject obj = subEntry.unreference();
                Reference ptr = obj.reference();
                // Log.debug(this, ".addResources - ptr="+ptr+", subEntry="+subEntry);
                PDFNode poolNode = (PDFNode) document.pool.get(ptr);
                // entry.key is not the same as subEntry.key :-)
                PDFNode node = null;
                switch (entry.key)
                {
                    case "XObject":
                        //do not pool XObjects
                        addXObjects(key, obj);
                        break;
                    case "Font":
                        node = addFonts(key, obj, poolNode);
                        break;
                    case "Shading":
                        node = addShadings(key, obj, poolNode);
                        break;
                    case "ColorSpace":
                        node = addColorSpaces(key, obj, poolNode);
                        break;
                    case "Pattern":
                        node = addPatterns(key, obj, poolNode);
                        break;
                    case "ExtGState":
                        node = addExtGStates(key, obj, poolNode);
                        break;
                    case "Properties":
                        node = addProperties(key, obj, poolNode);
                        break;
                }
                if (node != null && node != poolNode)
                    document.pool.add(ptr, node);
            }
        }

        // adding is only useful for debugging purpose (PDF Tree display in PDF
        // Inspector)
        add(fonts.wrap(this));
        add(shadings.wrap(this));
        add(colorSpaces.wrap(this));
        add(patterns.wrap(this));
        add(xObjects.wrap(this));
        add(gStates.wrap(this));
        add(properties.wrap(this));
    }

    public PDFExtGState addExtGStates(String key, PDFObject obj, PDFNode pool)
    {
        PDFExtGState xState;
        if (pool != null && pool instanceof PDFExtGState)
            gStates.put(key, xState = (PDFExtGState) pool);
        else
            gStates.put(key, xState = new PDFExtGState(this, key, obj.toPDFDictionary()));
        return xState;
    }

    public PDFObjectNode addProperties(String key, PDFObject obj, PDFNode pool)
    {
        PDFObjectNode objNode;
        if (pool != null && pool instanceof PDFObjectNode)
            properties.put(key, objNode = (PDFObjectNode) pool);
        else
            properties.put(key, objNode = new PDFObjectNode(this, obj));
        return objNode;
    }

    public void addXObjects(String key, PDFObject obj)
    {
        if (obj.reference().id() == parent.reference.id())
        {
            Log.warn(this, ".addXObjects - recursive object: parent Stream ID=" + parent.reference.id() + " vs child XObject ID=" + obj.reference().id());
            return;
        }

        PDFStream stream = obj.toPDFStream();
        if (stream.is("Subtype", "Image"))
            xObjects.put(key, new PDFImage(this, key, stream));
        else if (stream.is("Subtype", "Form"))
            xObjects.put(key, new PDFContent(this, key, stream));
        else
            Log.warn(this, ".addXObjects - not yet implemented object: " + stream.get("Subtype").stringValue());
    }

    public PDFColorSpace addColorSpaces(String key, PDFObject obj, PDFNode pool)
    {
        PDFColorSpace cSpace;
        if (pool != null && pool instanceof PDFColorSpace)
            colorSpaces.put(key, cSpace = (PDFColorSpace) pool);
        else
            colorSpaces.put(key, cSpace = PDFColorSpace.instance(this, key, obj));
        return cSpace;
    }

    public PDFPattern addPatterns(String key, PDFObject obj, PDFNode pool)
    {
        PDFPattern pattern;
        if (pool != null && pool instanceof PDFPattern)
            patterns.put(key, pattern = (PDFPattern) pool);
        else
            patterns.put(key, pattern = PDFPattern.instance(this, key, obj));
        return pattern;
    }

    public PDFFont addFonts(String key, PDFObject obj, PDFNode pool)
    {
        PDFFont font;
        if (pool != null && pool instanceof PDFFont)
            fonts.put(key, font = (PDFFont) pool);
        else
        {
            font = PDFFont.Instance(this, key, obj);
            if (font != null)
                fonts.put(key, font);
        }
        return font;
    }

    public PDFShading addShadings(String key, PDFObject obj, PDFNode pool)
    {
        PDFShading shading;
        if (pool != null && pool instanceof PDFShading)
            shadings.put(key, shading = (PDFShading) pool);
        else
        {
            shading = PDFShading.instance(this, key, obj.toPDFDictionary());
            if (shading != null)
                shadings.put(key, shading);
        }
        return shading;
    }

    public PDFNode getXObject(String name)
    {
        PDFResources res = this;
        PDFNode obj = null;
        while (res != null && (obj = res.xObjects.get(name)) == null && (res = res.parentResources) != this)
            ;
        return obj;
    }

    public PDFColorSpace colorspace(String name)
    {
        PDFResources res = this;
        PDFColorSpace obj = null;
        while (res != null && (obj = res.colorSpaces.get(name)) == null && (res = res.parentResources) != this)
            ;
        return obj;
    }

    public PDFFont getFont(String name)
    {
        PDFResources res = this;
        PDFFont obj = null;
        while (res != null && (obj = res.fonts.get(name)) == null && (res = res.parentResources) != this)
            ;
        return obj;
    }

    public Collection<PDFFont> getFonts()
    {
        List3<PDFFont> list = new List3<>(fonts.values());
        for (PDFNode node : xObjects.values())
            if (node.isContent() && node.toContent().hasResources())
                list.addAll(node.toContent().resources().getFonts());
        return list;
    }

    public PDFShading getShading(String name)
    {
        PDFResources res = this;
        PDFShading obj = null;
        while (res != null && (obj = res.shadings.get(name)) == null && (res = res.parentResources) != this)
            ;
        return obj;
    }

    public PDFPattern getPattern(String name)
    {
        PDFResources res = this;
        PDFPattern obj = null;
        while (res != null && (obj = res.patterns.get(name)) == null && (res = res.parentResources) != this)
            ;
        return obj;
    }

    public Collection<PDFShading> getShadings()
    {
        List3<PDFShading> list = new List3<PDFShading>(shadings.values());
        for (PDFNode node : xObjects.values())
            if (node.isContent() && node.toContent().hasResources())
                list.addAll(node.toContent().resources().getShadings());
        return list;
    }

    public PDFExtGState getExtGState(String name)
    {
        PDFResources res = this;
        PDFExtGState obj = null;
        while (res != null && (obj = res.gStates.get(name)) == null && (res = res.parentResources) != this)
            ;
        return obj;
    }

    @Override
    public String sticker()
    {
        return "Resources";
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Resources");
        sb.append("\nFonts" + this.fonts);
        return sb.toString();
    }
}
