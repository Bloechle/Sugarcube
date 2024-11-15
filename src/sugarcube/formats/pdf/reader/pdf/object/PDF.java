package sugarcube.formats.pdf.reader.pdf.object;

import sugarcube.common.data.collections.Ints;
import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.system.io.File3;

import java.awt.image.BufferedImage;


/**
 * This class contains useful raw PDF constants and method. Mainly used by the StreamReader class.
 */
public class PDF
{
    public final static String FILE_EXTENSION = ".pdf";
    public final static int[] EOL_LF = new int[]
            {
                    10
            };
    public final static int[] EOL_CR_LF = new int[]
            {
                    13, 10
            };
    /**
     * Java end of stream marker (InputStream)
     */
    public final static int EOS = -1;
    /**
     * Null character in PDF.
     */
    public final static int NUL = 0;
    /**
     * Tabulation character.
     */
    public final static int HT = 9;
    /**
     * Line feed character.
     */
    public final static int LF = 10;
    /**
     * Form feed character.
     */
    public final static int FF = 12;
    /**
     * Carriage return character.
     */
    public final static int CR = 13;
    /**
     * Space character.
     */
    public final static int SP = 32;

    public final static int COMMENTARY = '%';
    public final static int NAME = '/';
    public final static int STRING_OPEN = '(';
    public final static int STRING_CLOSE = ')';
    public final static int STRING_OR_DICTIONARY_OPEN = '<';
    public final static int STRING_OR_DICTIONARY_CLOSE = '>';
    public final static int ARRAY_OPEN = '[';
    public final static int ARRAY_CLOSE = ']';

    public static int MAX_IMG_SIZE = 8000; //<width || <height

//  static
//  {
////    Date3.timize();
//  }

    private PDF()
    {
        // This is a pure static class, no instance allowed
    }

    public static boolean isImageStreamTerminator(int c)
    {
        switch (c)//what character do we find just after EI (or endstream)?
        {
            case LF:
            case CR:
            case SP:
            case -1:
                return true;//stream is terminated, we found a valid delimiter which we hope is not part of stream data together with EI...
        }
        return false;
    }

    public static boolean isStreamTerminator(int c, boolean isEI)
    {
        if (isEI)
            return isImageStreamTerminator(c);
        else
            switch (c)//what character do we find just after EI (or endstream)?
            {
                case NUL:
                case HT:
                case LF:
                case FF:
                case CR:
                case SP:
                case NAME:
                case STRING_OPEN:
                case STRING_OR_DICTIONARY_OPEN:
                case ARRAY_OPEN:
                case COMMENTARY:
                case -1:
                    return true;//stream is terminated, we found a valid delimiter which we hope is not part of stream data together with EI...
            }
        return false;
    }

    public static boolean isWhiteSpaceOrDelimiter(int c)
    {
        return isWhiteSpace(c) || isDelimiter(c);
    }

    public static boolean isWhiteSpace(int c)
    {
        switch (c)
        {
            case NUL:
            case HT:
            case LF:
            case FF:
            case CR:
            case SP:
                return true;
        }
        return false;
    }


    public static boolean isDelimiter(int c)
    {
        switch (c)
        {
            case NAME:
            case STRING_OPEN:
            case STRING_CLOSE:
            case STRING_OR_DICTIONARY_OPEN:
            case STRING_OR_DICTIONARY_CLOSE:
            case ARRAY_OPEN:
            case ARRAY_CLOSE:
                return true;
        }
        return false;
    }

    public static boolean isToken(int c)
    {
        switch (c)
        {
            case EOS:
            case NUL://whitespaces
            case HT:
            case LF:
            case FF:
            case CR:
            case SP:
            case COMMENTARY: //commentary
            case NAME://keywords
            case STRING_OPEN:
            case STRING_CLOSE:
            case STRING_OR_DICTIONARY_OPEN:
            case STRING_OR_DICTIONARY_CLOSE:
            case ARRAY_OPEN:
            case ARRAY_CLOSE:
                return false;
        }
        return true;
    }

    public static boolean isCommentary(int c)
    {
        return c == COMMENTARY;
    }

    public static boolean isStringOrDictionary(int c)
    {
        return c == STRING_OR_DICTIONARY_OPEN || c == STRING_OR_DICTIONARY_CLOSE;
    }

    public static boolean isEOL(int c)
    {
        return c == LF || c == CR;
    }

    public static boolean isPDF(File3 pdf)
    {
        if (pdf.isExtension(".pdf", ".PDF") && pdf.exists())
        {
            Ints eofs = pdf.find("%%EOF");
            int size = eofs.last(-1);
            int eof = eofs.beforeLast(-1);
            if (size > 0 && eof > 0 && size - eof < 256)
                return true;
            else
            {
                Log.debug(PDF.class, ".isPDF - poor PDF file: " + pdf.path() + ", size=" + size + ", eof=" + eof);
                return false;
            }
        } else
            return false;
    }

    public static String toString(float... values)
    {
        float norm = 1000;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < values.length - 1; i++)
            sb.append((Math.round(values[i] * norm)) / norm).append(",");
        if (values.length > 0)
            sb.append((Math.round(values[values.length - 1] * norm)) / norm);
        else
            sb.append("none");
        sb.append("]");
        return sb.toString();
    }

    public static Image3 ImageARGB(BufferedImage img)
    {
        return Image(img.getWidth(), img.getHeight(), true);
    }

    public static Image3 ImageARGB(double width, double height)
    {
        return Image(width, height, true);
    }

    public static Image3 ImageARGB(Rectangle3 bounds)
    {
        return Image(bounds.width, bounds.height, true);
    }

    public static Image3 ImageRGB(Rectangle3 bounds)
    {
        return Image(bounds.width, bounds.height, false);
    }

    public static Image3 ImageRGB(double width, double height)
    {
        return Image(width, height, false);
    }

    public static Image3 Image(Rectangle3 bounds, boolean alpha)
    {
        return Image(bounds.width, bounds.height, alpha);
    }

    public static Image3 Image(BufferedImage img, boolean alpha)
    {
        return Image(img.getWidth(), img.getHeight(), alpha);
    }

    public static Image3 Image(double width, double height, boolean alpha)
    {
        return Image(width, height, alpha ? Image3.TYPE_INT_ARGB : Image3.TYPE_INT_RGB);
    }

    public static Image3 Image(double width, double height, int type)
    {
        if (width < 1 || height < 1 || width > MAX_IMG_SIZE || height > MAX_IMG_SIZE)
        {
            String warn = ".Image - strange dimension: width=" + ((int) width) + ", height=" + ((int) height);
            Log.warn(PDF.class, warn);
            try
            {
                throw new Exception("PDF" + warn);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        int w = Math.max((int) Math.round(width), 1);
        int h = Math.max((int) Math.round(height), 1);
        if (w > MAX_IMG_SIZE || h > MAX_IMG_SIZE)
        {
            float s = Math.min((MAX_IMG_SIZE / 2) / (float) w, (MAX_IMG_SIZE / 2) / (float) h);
            w = Math.max(100, Math.round(s * w));
            h = Math.max(100, Math.round(s * h));
        }
        return new Image3(w, h, type);
    }
}
