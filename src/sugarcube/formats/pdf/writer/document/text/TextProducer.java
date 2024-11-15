package sugarcube.formats.pdf.writer.document.text;

import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.geom.Coords;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.formats.ocd.objects.OCDText;
import sugarcube.formats.ocd.objects.OCDTextBlock;
import sugarcube.formats.ocd.objects.OCDTextLine;
import sugarcube.formats.ocd.objects.font.SVGGlyph;
import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.Lexic;
import sugarcube.formats.pdf.writer.core.Util;
import sugarcube.formats.pdf.writer.core.object.Stream;
import sugarcube.formats.pdf.writer.document.GraphicState;
import sugarcube.formats.pdf.writer.document.Page;
import sugarcube.formats.pdf.writer.document.text.font.Font;
import sugarcube.formats.pdf.writer.exception.PDFException;

public class TextProducer
{
    // ignored
    /*
     * private static final String PDF_SHOW_WORD = "Tj"; private static final
     * String PDF_CHAR_SPACE = "Tc"; private static final String PDF_WORD_SPACE =
     * "Tw";
     */
    // paint
    private static final String PDF_BEGIN_TEXT = "BT";
    private static final String PDF_END_TEXT = "ET";
    private static final String PDF_SHOW_STRING = "TJ";
    private static final String PDF_NEW_LINE = "Td";
    private StringBuilder content = new StringBuilder(1000);
    private StringBuilder string = new StringBuilder(1000);
    private Stream stream;
    private PDFWriter writer;
    public boolean hasOpenGroup = false;

    public TextProducer(PDFWriter writer)
    {
        this.writer = writer;
    }

    public TextProducer openGroupIfClosed(Page page)
    {
        if (!hasOpenGroup)
        {
            content.setLength(0);
            stream = new Stream(page.pdfWriter());
            page.linkContent(stream.getID());
            hasOpenGroup = true;
        }
        return this;
    }

    public void closeGroupIfOpen()
    {
        // Log.debug(this, ".write - closeGroup="+stream.getID()+",
        // "+content+"\n\n");
        if (hasOpenGroup)
        {
            try
            {
                stream.write(content);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            hasOpenGroup = false;
        }
    }

    public TextProducer write(Page page, OCDTextBlock block) throws PDFException
    {
        this.string.setLength(0);
        try
        {
            GraphicState graphicState = page.getGraphicState().beginText();
            OCDTextLine line;
            OCDText text;
            Font font = null;
            float newCS = 0;
            Point3 coord = null;
            float[] charSpaces;
            Coords coordinates;
            SVGGlyph glyph;
            Transform3 lineTransform = new Transform3();
            StringBuilder style = new StringBuilder();
            boolean lineStyleChanged = false;
            // clipping
            // //////////graphicState.clip(block.clip(), page, stringBuilder);
            // new block
            string.append(PDF_BEGIN_TEXT + Lexic.LINE_FEED);
            boolean opened = false;
            for (int lineIndex = 0; lineIndex < block.size(); lineIndex++)
            {
                line = block.get(lineIndex);
                // stringBuilder.append(line.stringValue());
                // write text
                OCDText[] texts = line.zTexts();
                for (int textIndex = 0; textIndex < texts.length; textIndex++)
                {
                    text = texts[textIndex];

//          if(text.clip().bounds().overlap(text.bounds(true))<0.2)
//            continue;

                    // Log.debug(this,
                    // ".write - " + text.uniString() + ", z=" + text.zOrder + ", col=" +
                    // text.fillColor().cssRGBAValue() + ", mat=" + text.transform());

                    // scale = text.scaleX()!=0 && text.scaleY()!=0 ?
                    // text.scaleX()/text.scaleY() : 1;

                    graphicState.setTextParameters(page, text, style, lineIndex == 0 && textIndex == 0);

                    font = page.getGraphicState().getFont();
                    charSpaces = text.charSpaces();
                    coordinates = text.coords();
                    // new line
                    if (textIndex == 0 && lineIndex > 0)
                    {
                        if (lineStyleChanged)
                        {
                            lineStyleChanged = false;
                        } else
                        {
                            coord = lineTransform.transform(coordinates.get(0));
                            string.append(Util.format(coord.x) + Lexic.SPACE + -Util.format(coord.y) + Lexic.SPACE + PDF_NEW_LINE + Lexic.LINE_FEED);
                        }
                    }
                    // font change ou new line?
                    if (style.length() > 0 || textIndex == 0)
                    {
                        if (textIndex > 0)
                        {
                            string.append(Lexic.GREATER_THAN + Lexic.RIGHT_SQUARE_BRACKET + PDF_SHOW_STRING + Lexic.LINE_FEED);
                            lineStyleChanged = true;
                        }
                        string.append(style.toString());
                        // System.out.println("\n" + style.toString());
                        style.setLength(0);
                        string.append(Lexic.LEFT_SQUARE_BRACKET).append(Lexic.LESS_THAN);
                        opened = true;
                    }

                    float csi = 0; // char space at textLineIndex i
                    // System.out.println("\nscale="+scale+", text=" + text.string());
                    int size = text.nbOfChars();
                    for (int i = 0; i < size; i++)
                    {
                        glyph = text.font().glyph(text.charAt(i));

                        if (i < charSpaces.length && !Float.isNaN(charSpaces[i]))
                            csi = charSpaces[i];

                        boolean isSpace = false;
                        if (glyph == null)
                        {
                            System.out.println(this + " page " + text.page().number() + ": " + text.string() + " " + (int) text.charAt(i));
                            newCS = 0;
                        } else
                        {
                            newCS = -csi * 1000;// * scale;
                            if (glyph.isPathEmpty() && (isSpace = true))
                                newCS -= glyph.width() * 1000;
                        }

                        if (!isSpace)
                        {
                            String c = Integer.toHexString((char) font.getMappedCode((int) text.charAt(i)));
                            if (c.length() == 1)
                                c = "0" + c;
                            string.append(c);
                        }
                        int roundCS = Math.round(newCS);
                        if (roundCS != 0)
                            string.append(Lexic.GREATER_THAN + roundCS + Lexic.LESS_THAN);
                    }
                    if (textIndex == 0)
                        lineTransform = text.transform3().inverse();
                }
                if (opened)
                    string.append(Lexic.GREATER_THAN + Lexic.RIGHT_SQUARE_BRACKET + PDF_SHOW_STRING + Lexic.LINE_FEED);
            }

            // 2017.11.29 - guess why but when compressing text having very few data,
            // ET may be "eaten" by the compression !!!!!
            string.append(PDF_END_TEXT + Lexic.LINE_FEED + (PDFWriter.COMPRESS ? Lexic.LINE_FEED + Lexic.LINE_FEED : ""));
            content.append(string.toString());

        } catch (Exception e)
        {
            e.printStackTrace();
            Log.warn(this, ".write - exception: " + e.getMessage());
            string.setLength(0);
            string.append(PDF_BEGIN_TEXT + Lexic.LINE_FEED);
            string.append(PDF_END_TEXT + Lexic.LINE_FEED + (PDFWriter.COMPRESS ? Lexic.LINE_FEED + Lexic.LINE_FEED : ""));
            content.append(string.toString());
        }
        return this;
    }

    public void dispose()
    {
        this.string = null;
        this.content = null;
        this.stream = null;
    }
}
