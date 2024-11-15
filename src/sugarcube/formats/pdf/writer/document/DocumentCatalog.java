package sugarcube.formats.pdf.writer.document;

import sugarcube.formats.pdf.writer.Constants;
import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.object.DictionaryObject;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.exception.PDFException;

public class DocumentCatalog extends DictionaryObject{
	//ignored
	//Extensions, PageLabels, Names, Dests, ViewerPreferences, PageLayout, PageMode
	//OpenAction, AA, URI, AcroForm, Metadata, StructTreeRoot, MarkInfo, Lang
	//SpiderInfo, OutputIntents, PieceInfo, OCProperties, Perms, Legal
	//Requirements, Collection, NeedsRendering
	private int flag;
	
	public DocumentCatalog(PDFWriter environment, int flag) throws PDFException{
		super(environment);
		this.flag = flag;
		write();
	}

	public void addDictionaryEntries() {
		PDFWriter environment = pdfWriter();
		int nextId = getID();
		addDictionaryEntry("Type", "Catalog", Writer.NAME);
		addDictionaryEntry("Version", environment.getVersion(), Writer.NAME);
		if ((flag & Constants.OUTLINES) == Constants.OUTLINES)
			addDictionaryEntry("Outlines", ++nextId, Writer.INDIRECT_REFERENCE);
		if ((flag & Constants.THREADS) == Constants.THREADS)
			addDictionaryEntry("Threads", ++nextId, Writer.INDIRECT_REFERENCE);
		addDictionaryEntry("Pages", ++nextId, Writer.INDIRECT_REFERENCE);
	}
}
