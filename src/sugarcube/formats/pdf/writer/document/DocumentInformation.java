package sugarcube.formats.pdf.writer.document;

import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.object.DictionaryObject;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.exception.PDFException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DocumentInformation extends DictionaryObject {

	public DocumentInformation(PDFWriter environment) {
		super(environment);
	}

	public void addDictionaryEntries() throws PDFException {
		addDictionaryEntry("Creator", "sugarcube Dexter 1.0", Writer.LITERAL);
		addDictionaryEntry("Producer", "sugarcube Dexter-OCD2PDF 0.6", Writer.LITERAL);
		String dateFormat = "yyyyMMddHHmmssZ";
		String date = "D:" + new SimpleDateFormat(dateFormat).format(new Date(System.currentTimeMillis()));
		int index = date.length() - 2;
		date = date.substring(0, index) + "'" + date.substring(index);
		addDictionaryEntry("CreationDate", date, Writer.LITERAL);
	}

}
