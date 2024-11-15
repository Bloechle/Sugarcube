package sugarcube.formats.pdf.writer.document.text.font;

import sugarcube.common.system.log.Log;
import sugarcube.formats.ocd.objects.font.SVGFont;
import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.Util;
import sugarcube.formats.pdf.writer.core.object.DictionaryObject;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.exception.PDFException;

public class FontDescriptor extends DictionaryObject {
	private SVGFont font;

	public FontDescriptor(PDFWriter environment, SVGFont font) throws PDFException {
		super(environment);
		this.font = font;
		write();
	}

  @Override
	public void addDictionaryEntries() {
		Log.debug(this, ".addDictionaryEntries - FontWeight, Flags, ItalicAngle not yet used");
		addDictionaryEntry("Type", "FontDescriptor", Writer.NAME);
		addDictionaryEntry("FontName", font.fontname(), Writer.NAME);
		addDictionaryEntry("FontStretch", getFontStretch(), Writer.NAME);
		addDictionaryEntry("FontWeight", 400, Writer.INTEGER);
		addDictionaryEntry("Flags", 0, Writer.INTEGER);
		addDictionaryEntry("FontBBox", Util.rectangleToFloatArray(font.bounds(), .001f, true), Writer.REAL_ARRAY);
		addDictionaryEntry("ItalicAngle", 0, Writer.INTEGER);
	}

	private String getFontStretch(){
		final String[] types = new String[]{"UltraCondensed", "ExtraCondensed", "Condensed",
				"SemiCondensed", "SemiExpanded", "Expanded", "ExtraExpanded", "UltraExpanded"};
		String name = font.fontname();
		for (int t = 0; t < types.length; t++)
			if (name.contains(types[t]))
				return types[t];
		return "Normal";
	}
}
