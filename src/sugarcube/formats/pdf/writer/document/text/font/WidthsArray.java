package sugarcube.formats.pdf.writer.document.text.font;

import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.object.ArrayObject;
import sugarcube.formats.pdf.writer.exception.PDFException;

public class WidthsArray extends ArrayObject<Float> {
	private Float[] widths;

	public WidthsArray(PDFWriter environment, Float[] widths) throws PDFException {
		super(environment);
		this.widths = widths;
		write();
	}

	public Float[] getArrayEntries() throws PDFException {
		return widths;
	}

}
