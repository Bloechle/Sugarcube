package sugarcube.formats.pdf.writer.document.text.font;

import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.Lexic;
import sugarcube.formats.pdf.writer.core.object.DictionaryObject;
import sugarcube.formats.pdf.writer.core.writer.StringWriter;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.exception.PDFException;

public class Encoding extends DictionaryObject{
	private String[] glyphsNames;
	private int[] codes;

	public Encoding(PDFWriter environment, String[] glyphsNames, int[] codes) throws PDFException {
		super(environment);
		this.glyphsNames = glyphsNames;
		this.codes = codes;
		write();
	}

	public void addDictionaryEntries() throws PDFException {
		addDictionaryEntry("Type", "Encoding", Writer.NAME);
		StringWriter writer = new StringWriter();
		writer.openArray();
		for (int c = 0; c < codes.length; c++){
			writer.write((long)codes[c]);
			writer.write(Lexic.SPACE);
			writer.writeName(glyphsNames[c]);
			writer.write(Lexic.LINE_FEED);
		}
		writer.closeArray();
		addDictionaryEntry("Differences", writer.toString(), Writer.GENERIC_STRING);
	}

}
