package sugarcube.formats.pdf.reader.pdf.node.font;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.IdMap;
import sugarcube.common.data.collections.IntMap;
import sugarcube.common.data.collections.Unicodes;
import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.formats.pdf.reader.pdf.node.PDFContent;
import sugarcube.formats.pdf.reader.pdf.node.PDFInstr;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.node.PDFPath;
import sugarcube.formats.pdf.reader.pdf.node.image.PDFImage;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;
import sugarcube.formats.pdf.reader.pdf.object.PDFOperator.OP;
import sugarcube.formats.pdf.reader.pdf.object.PDFStream;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * - no fontfile - has no built-in encoding
 * <p>
 * For each character code shown by a text-showing operator that uses a Type 3
 * font, the consumer application does the following: 1. Looks up the character
 * code in the font’s Encoding entry, as described in Section 5.5.5, “Character
 * Encoding,” to obtain a character name. 2. Looks up the character name in the
 * font’s CharProcs dictionary to obtain a stream object containing a glyph
 * description. (If the name is not present as a key in CharProcs, no glyph is
 * painted.) 3. Invokes the glyph description, as described below. The graphics
 * state is saved before this invocation and restored afterward; therefore, any
 * changes the glyph description makes to the graphics state do not persist
 * after it finishes. When the glyph description begins execution, the current
 * transformation matrix (CTM) is the concatenation of the font matrix
 * (FontMatrix in the current font dictionary) and the text space that was in
 * effect at the time the text-showing operator was invoked (see Section 5.3.3,
 * “Text Space Details”).
 */
public class PDFFontType3 extends PDFFont implements PDFGlyph.Interface
{
    private static int DEBUG_COUNTER = 0;
    private Transform3 transform = Transform3.IDENTITY.copy();
    private IdMap<PDFGlyph> nameMap = new IdMap<PDFGlyph>("NameToOutline");
    private IntMap<PDFGlyph> codeMap = new IntMap<PDFGlyph>("CodeToOutline");

    public PDFFontType3(PDFNode node, PDFDictionary fontMap)
    {
        super(node, fontMap);
        this.format = FontFormat.Path;
        this.outlines = this;
        double[] array = fontMap.get("FontMatrix").toPDFArray().doubleValues(0.001, 0, 0, 0.001, 0, 0);
        this.transform = new Transform3(array);

        this.processCharacters(fontMap.get("CharProcs").toPDFDictionary());
        this.scaleWidth(1000 * this.transform.scaleX());
        finalizeConstruction();
    }

    private void processCharacters(PDFDictionary dictionary)
    {
        int index = 0;
        for (Map.Entry<String, PDFObject> entry : dictionary.getEntries())
            try
            {
                // Log.debug(this,  ".processCharacters - "+entry.getKey());
                // if (!entry.getValue().unreference().isPDFStream())
                // XED.LOG.warn(this, ".processCharacters - PDFStream not found: " +
                // entry.getValue().type);
                PDFStream stream = entry.getValue().unreference().toPDFStream();
                PDFContent content = new PDFContent(this, stream);
                Path3 path = null;
                for (PDFNode node : content.children())
                {
                    if (node.isPath())
                    {
                        PDFPath pdfPath = node.toPath();
                        Transform3 tm = transform.concat(pdfPath.transform());
                        if (path == null)
                            path = pdfPath.path().transform(tm);
                        else
                            path.append(pdfPath.path().transform(tm), false);
                        path = path.reverseY();
                        break;
                    } else
                    {
                        Log.debug(this, " - node: " + node);
                    }
                }
                if (path == null)
                {
                    this.format = FontFormat.Bitmap;
                    path = new Path3();
                    for (PDFNode node : content.children())
                        if (node.isImage())
                        {
                            PDFImage pdfImage = node.toImage();
                            BufferedImage image = pdfImage.image();
                            int w = image.getWidth();
                            int h = image.getHeight();
                            Transform3 tm = pdfImage.um().concat(pdfImage.tm()).concat(transform.floatValues()).transform();
//              ImageIO.write(image, "png",File3.userDesktop("tmp/" + (DEBUG_COUNTER++) + "-" + w + "-" + h + "-" + pdfImage.tm() + "-" + image.getType() + ".png"));
                            Path3 subPath = new Path3();
                            for (int y = 0; y < h; y++)
                                for (int x = 0; x < w; x++)
                                    if (image.getRGB(x, y) != 0)
                                        subPath.append(tm.transform(new Rectangle3(x, -y - 1, 1, 1)), false);
                            path.append(subPath.reverseY(), false);
                        }
                }

                PDFInstr op = content.instruction(OP.d0.name);
                if (op == null)
                    op = content.instruction(OP.d1.name);

//        Image3 image = path.image(100);
//        image.write(File3.Desk("bou/"+entry.getKey()+".png"));
//        path = path.shift(path.origin(), true);

                PDFGlyph outline = new PDFGlyph(path, op.params().get(0).floatValue());
                nameMap.put(entry.getKey(), outline);
                codeMap.put(firstChar + index++, outline);
            } catch (Exception e)
            {
                Log.warn(this, ".processCharacters -  exception while processing character: " + entry.getValue().reference());
                e.printStackTrace();
            }
    }

    @Override
    public PDFGlyph outline(String name, Unicodes uni, int code)
    {
        if (name != null & nameMap.has(name))
            return nameMap.get(name);
        else if (uni != null && nameMap.has(uni.string()))
            return nameMap.get(uni.string());
        else if (codeMap.contains(code))
            return codeMap.get(code);
        else
            return null;
    }

    @Override
    public Transform3 transform()
    {
        return transform;
    }
}
