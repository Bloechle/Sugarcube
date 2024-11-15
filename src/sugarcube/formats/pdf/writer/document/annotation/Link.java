package sugarcube.formats.pdf.writer.document.annotation;

import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.formats.ocd.objects.OCDAnnot;
import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.Util;
import sugarcube.formats.pdf.writer.core.object.DictionaryObject;
import sugarcube.formats.pdf.writer.core.writer.StringWriter;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.document.annotation.action.URIAction;
import sugarcube.formats.pdf.writer.exception.PDFException;

public class Link extends DictionaryObject {
	private OCDAnnot link;
	private int actionID;
	private Transform3 transform;

	public Link(PDFWriter environment, Transform3 transform, OCDAnnot link) throws PDFException {
		super(environment);
		this.transform = transform;
		this.link = link;
		actionID = new URIAction(environment, link.get("url")).getID();
		write();
	}

	public void addDictionaryEntries() throws PDFException {
		addDictionaryEntry("Type", "Annot", Writer.NAME);
		addDictionaryEntry("Subtype", "Link", Writer.NAME);
		Rectangle3 rect = new Rectangle3(transform.transform(link.bounds()).getBounds2D());
		addDictionaryEntry("Rect", Util.rectangleToFloatArray(rect), Writer.REAL_ARRAY);
		addDictionaryEntry("H", "I", Writer.NAME);
		addDictionaryEntry("C", new Float[]{1f, 0f, 0f}, Writer.REAL_ARRAY);
		addDictionaryEntry("Border", new Float[]{0f, 0f, 0f}, Writer.REAL_ARRAY);
		addDictionaryEntry("A", actionID, Writer.INDIRECT_REFERENCE);
		StringWriter writer = new StringWriter();
		int extractDict;
		writer.openDictionary();
		writer.writeDictionaryPair("S", "S", Writer.NAME);
		writer.writeDictionaryPair("Type", "Border", Writer.NAME);
		writer.writeDictionaryPair("W", 2, Writer.INTEGER);
		writer.closeDictionary();
		addDictionaryEntry("BS", writer.toString(), Writer.GENERIC_STRING);
	}

}
