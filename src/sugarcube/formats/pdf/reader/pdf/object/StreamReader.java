package sugarcube.formats.pdf.reader.pdf.object;

import sugarcube.common.system.io.IO;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * If you transfer an Acrobat PDF file to a Windows or MS-DOS machine, transfer in binary mode. Although a version 1.0 of the PDF format was advertised as being
 * 7-bit ASCII and "platform independent," in fact a PDF file contains coded byte offsets. If you transfer in ASCII (text) mode you will alter the lineends from
 * one character (CR or LF) to two (CR/LF) and thereby disturb the offsets. The Reader will rebuild them, but not before frightening you with a "File damaged"
 * alert. Binary mode is now formally part of the PDF 1.1 standard (and its successor versions). Binary mode was formerly deprecated by Adobe, but starting with
 * PDF-1.1 (corresponding to version 2 of the Acrobat products), Adobe is encouraging the use of binary PDF files. This gives you all the more reason to
 * transfer in binary mode.
 */
public class StreamReader
{
    //PDFStream only useful to keep track on reference
    protected PDFStream[] streams;
    protected int[] streamMarkers;//use when page content is defined by several content objects
    protected int begin;
    protected int end;
    protected int pos;
    protected int objectPos;
    protected byte[] decoded;
    protected RandomAccessFile file;

    public StreamReader(PDFStream[] streams, int[] streamMarkers, byte[] decoded)
    {
        this.streams = streams;
        this.streamMarkers = streamMarkers;
        this.decoded = decoded;
        this.file = null;
        this.pos = 0;
        this.begin = 0;
        this.end = decoded.length;
    }

    public StreamReader(PDFStream stream)
    {
        this.streams = new PDFStream[1];
        this.streams[0] = stream;
        if (stream.file() == null || stream.isEncoded())
        {
            this.decoded = stream.byteValues();
            this.file = null;
            this.pos = 0;
            this.begin = 0;
            this.end = decoded.length;
        } else
        {
            this.decoded = null;
            this.file = stream.file();
            this.pos = stream.locator().pointer;
            this.begin = this.pos;
            this.end = this.pos + stream.locator().length;
            this.fileSeek(this.pos);
        }
    }

    public StreamReader(InputStream stream)
    {
        this.streams = null;
        this.decoded = IO.ReadBytes(stream);
        this.file = null;
        this.pos = 0;
        this.begin = 0;
        this.end = decoded.length;
    }

    public long pos()
    {
        return this.pos;
    }

    private void fileSeek(int pos)
    {
        if (file != null)
            try
            {
                file.seek(pos);
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
    }

    public PDFStream pdfStreams(int index)
    {
        return this.streams[index];
    }

    public Reference streamReference()
    {
        return this.streamReference(pos);
    }

    public Reference streamReference(long pos)
    {
        int i = 0;
        if (this.streamMarkers != null)
            while (i < streamMarkers.length-1 && pos > streamMarkers[i])
                i++;
        return this.streams == null ? Reference.UNDEF : this.streams[i].reference;
    }

    public final int read()
    {
        return read(pos);
    }

    public final int read(long pos)
    {
        int data = view();
        this.pos++;
        return data;
    }

    public final int view()
    {
        return view(pos);
    }

    public final String viewChars(int size)
    {
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++)
            sb.append(view(pos + size));
        return sb.toString();
    }

    public final void seek(int pos)
    {
        this.view(pos + begin);
    }

    /**
     * The rule: the pointer points to the byte to read, if we view it, nothing is changed, if we read it, the byte is consumed and the pointer moves forward
     * (e.g. b_).
     */
    public final int view(int pos)
    {
        int data = -1;
        if (pos < end)
            if (decoded == null)
                try
                {
                    if (file.getFilePointer() != pos)
                        file.seek(pos);
                    data = file.readByte() & 0xff;
                } catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            else
                data = decoded[pos] & 0xff;
        return data;
    }

    public boolean hasDecoded()
    {
        return decoded != null;
    }

    public int equal(int[] token, int pos)
    {
        if (pos + token.length > end)
            return 0;
        for (int i = 0; i < token.length; i++)
            if (view(pos + i) != token[i])
                return 0;
        return token.length;
    }

    public int skip(int size)
    {
        this.pos += size;
        return size;
    }

    public int skip(int[] token)
    {
        int size = equal(token, pos);
        this.pos += size;
        return size;
    }

    public boolean isEOS()
    {
        return read() == PDF.EOS;
    }

    public double loadedRatio()
    {
        double ratio = (objectPos - begin) / (double) (end - begin);
        return ratio < 0.0 ? 0.0 : ratio > 1.0 ? 1.0 : ratio;
    }

    public StreamLocator streamLocator()
    {
        int i = 0;
        if (this.streamMarkers != null)
            while (i < streamMarkers.length-1 && pos > streamMarkers[i])
                i++;
        StreamLocator locator = new StreamLocator(objectPos - (streamMarkers == null || i < 1 ? 0 : streamMarkers[i - 1]), pos - objectPos, this.streams == null ? Reference.UNDEF : this.streams[i].reference);
        this.objectPos = pos;
        return locator;
    }

    /**
     * PDF Reference says: The keyword stream that follows the stream dictionary should be followed by an end-of-line marker consisting of either a carriage
     * return and a line feed (CR LF) or just a line feed (LF), and not by a carriage return alone. The same recommandation is done before the endstream keyword.
     * At the end pos references the next byte (e.g. endstream_).
     */
    public byte[] readStream(int length, BoyerMoore matcher, boolean isEI)
    {
        StreamLocator locator = locateStream(length, matcher, isEI);
        byte[] bytes = new byte[(int) locator.length];
        if (this.decoded == null)
            try
            {
                //TODO check this
                file.read(bytes, 0, bytes.length); //0 or pos?
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        else
            System.arraycopy(decoded, locator.pointer, bytes, 0, bytes.length);
        return bytes;
    }

    public StreamLocator locateStream(int length, BoyerMoore matcher, boolean isEI)
    {
        int patternSize = matcher.pattern().length;
        //XED.LOG.debug(this, ".locateStream - length="+length);
        if (skip(PDF.EOL_LF) == 0)
            skip(PDF.EOL_CR_LF);

        int endPos = -1;

        boolean match = false;
        int delta = length;
        do
        {
            if (this.decoded == null)
                endPos = matcher.search(file, pos + delta, end);
            else
                endPos = matcher.search(decoded, pos + delta, end);

            //looking for real end pattern when length is not specified since EI may be part of image stream data
            match = length > 0 ? true : PDF.isStreamTerminator(this.view(endPos + patternSize), isEI);
            delta = endPos - pos + patternSize;
        }
        while (!match);

        //we have approximative length...
        if(isEI)
            length=-1;

        int eolSize = 0;

        // if length<=0, we don't have the Length information (sequential read),
        // if we have the length, we use it, CR may be alone at the end (though it's forbidden)
        if (length <= 0)
        {
            eolSize = equal(PDF.EOL_CR_LF, endPos - PDF.EOL_CR_LF.length);
            if (eolSize == 0)
                eolSize = equal(PDF.EOL_LF, endPos - PDF.EOL_LF.length);
            endPos -= eolSize;
            length = endPos - pos;

            if (length == -1)
                System.out.println("pos=" + pos + " endpos=" + endPos);
        }

        if (length > end - pos)
            length = end - pos;

        StreamLocator location = new StreamLocator(pos, length, this.streamReference(0));
        this.pos = (endPos + eolSize + patternSize);
        this.fileSeek(pos);

        return location;
    }

    public int integer() throws NumberFormatException
    {
        return Integer.parseInt(token());
    }

    /**
     * Reads a token from the stream reader. A the last byte read must have been consumed with read(), to be sure that the pointer references the new byte to read
     * (e.g. wordread_).
     *
     * @return the word as string
     */
    public String token()
    {
        StringBuilder token = new StringBuilder();
        int c;
        do
        {
            c = read();
            if (c == PDF.EOS)
                return null;
        }
        while (PDF.isWhiteSpace(c));

        if (PDF.isDelimiter(c))
            if (PDF.isStringOrDictionary(c) && view() == c)
                return token.appendCodePoint(c).appendCodePoint(read()).toString();
            else
                return token.appendCodePoint(c).toString();
        else if (PDF.isCommentary(c))
        {
            do
            {
                //System.out.println("StreamReader.token: c=" + (char) c);
                c = read();
                if (c == PDF.EOS)
                    return null;
            }
            while (!PDF.isEOL(c));

            return token();
        } else // isToken(c)==true is inevitable and mandatory
        {
            token.appendCodePoint(c);
            while (PDF.isToken(c = read()))
                token.appendCodePoint(c);
            this.pos--;
            return token.length() != 0 ? token.toString() : token();
        }
    }

    public int readAscii()
    {
        int c;
        while ((c = this.read()) > 0)
            if (!PDF.isWhiteSpace(c))
                return c;
        return -1;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(end - begin);
        for (int i = this.begin; i < this.end; i++)
            sb.append((char) this.view(i));
        return sb.toString();
    }
}
