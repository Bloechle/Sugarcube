package sugarcube.formats.pdf.writer.document;

import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Stroke3;
import sugarcube.formats.ocd.objects.OCDClip;
import sugarcube.formats.ocd.objects.OCDImage;
import sugarcube.formats.ocd.objects.OCDPaintableLeaf;
import sugarcube.formats.ocd.objects.OCDText;
import sugarcube.formats.pdf.writer.PDFColorSpaceManager;
import sugarcube.formats.pdf.writer.PDFParams;
import sugarcube.formats.pdf.writer.core.Lexic;
import sugarcube.formats.pdf.writer.core.Util;
import sugarcube.formats.pdf.writer.document.graphics.GraphicsProducer;
import sugarcube.formats.pdf.writer.document.image.ImageManager;
import sugarcube.formats.pdf.writer.document.text.font.Font;
import sugarcube.formats.pdf.writer.document.text.font.FontManager;
import sugarcube.formats.pdf.writer.exception.PDFException;

import java.text.DecimalFormat;


public class GraphicState implements Lexic {

	// standard matrices
	private static final float[] VERTICAL_REFLECTION_MATRIX = new float[] { 1, 0, 0, -1, 0, 0 };
	private static final float[] IDENTITY_MATRIX = new float[] { 1, 0, 0, 1, 0, 0 };
	// state
	private final static String PDF_SAVE_GRAPHIC_STATE = "q";
	private final static String PDF_RESTORE_GRAPHIC_STATE = "Q";
	// graphics
	private final static int PDF_LINE_CAP_BUTT = 0;
	private final static int PDF_LINE_CAP_ROUND = 1;
	private final static int PDF_LINE_CAP_PROJECTING_SQUARE = 2;
	private final static int PDF_LINE_MITER_JOIN = 0;
	private final static int PDF_LINE_ROUND_JOIN = 1;
	private final static int PDF_LINE_BEVEL_JOIN = 2;
	private final static String PDF_TRANSFORM_MATRIX = "cm";
	private final static String PDF_LINE_WIDTH = "w";
	private final static String PDF_LINE_CAP = "J";
	private final static String PDF_LINE_JOIN = "j";
	private final static String PDF_MITER_LIMIT = "M";
	private final static String PDF_DASH = "d";
	private final static String PDF_STROKE_COLOR_RGB = "RG";
	private final static String PDF_FILL_COLOR_RGB = "rg";
	private final static String PDF_STROKE_COLOR_CMYK = "K";
	private final static String PDF_FILL_COLOR_CMYK = "k";
	// text
	private final static String PDF_TEXT_MATRIX = "Tm";
	private final static String PDF_FONT_SIZE = "Tf";
	// clipping
	private final static String PDF_EVEN_ODD_CLIPPING = "W*";
	private final static String PDF_NON_ZERO_CLIPPING = "W";

	private static final DecimalFormat DF = new DecimalFormat("#");
	static
	{
		DF.setMaximumFractionDigits(8);
	}

	// matrices
	private float[] textMatrix = IDENTITY_MATRIX;
	// mixed parameters
	private float[] strokeColor = new float[] { 0, 0, 0 };
	private float[] fillColor = new float[] { 0, 0, 0 };
	// graphics parameters
	private float lineWidth = 1.0f;
	private int lineCap = 0;
	private int lineJoin = 0;
	private float miterLimit = 10.0f;
	private float[] dashArray = new float[0];
	private float dashPhase = 0;
	// text parameters
	private Font font;
	private float currentFontSize = -1;
	// clipping path
	private OCDClip clip;
	private boolean beginText = false;
	private boolean beginGraphics = true;
	private double epsilon = 0.00001;
	private PDFParams params;

	public GraphicState(PDFParams params) {
		this.params = params;
		if (params.getColorSpace() == PDFParams.VALUE_CMYK_COLORSPACE) {
			strokeColor = new float[] { 0, 0, 0, 1 };
			fillColor = new float[] { 0, 0, 0, 1 };
			/*
			 * try{ cmykColorSpace = new
			 * ICC_ColorSpace(ICC_Profile.getInstance(ICC_Profile.icSigCmykData));
			 * }catch(Exception e){ e.printStackTrace(); }
			 */
		}
	}

	public GraphicState beginText() {
		this.beginText = true;
		return this;
	}

	public void beginGraphics() {
		this.beginGraphics = true;
	}

	public void setClip(OCDClip clip, Page page, StringBuilder sb) {
		if (clip == null)
			return;

		// Log.debug(this, ".setClip - " + clip.id() + clip.bounds() + ", " +
		// (this.clip == null ? "null" : this.clip.id() + this.clip.bounds()));
		// if (this.clip != null && clip.id().equals(this.clip.id()))
		// {
		// Log.debug(this, ".setClip - return: " + clip.id());
		// return;
		// }

		this.clip = clip;

		GraphicsProducer producer = page.pdfWriter().graphicsProducer;
		producer.createPath(sb, clip.path(), new Transform3());
		sb.append((clip.path().getWindingRule() == Path3.WIND_EVEN_ODD ? PDF_EVEN_ODD_CLIPPING : PDF_NON_ZERO_CLIPPING)
				+ LINE_FEED + "n" + LINE_FEED);
	}

	public int setImageParameters(Page page, OCDImage image, StringBuilder sb) throws PDFException {
		// clipping
		setClip(image.clip(), page, sb);
		// transform
		Transform3 tm = new Transform3();
		tm.concatenate(image.transform3());
		tm.concatenate(new Transform3(new float[] { 1, 0, 0, -1, 0, image.height() })); // mirror in PDF
		tm.concatenate(new Transform3(new float[] { image.width(), 0, 0, image.height(), 0, 0 }));
		writeMatrix(sb, tm.floatValues());
		// get image resource
		int imageID = page.pdfWriter().imageManager.resolveImageID(image);
		page.linkImage(imageID, ImageManager.IMAGE_SUFFIX + imageID);
		return imageID;
	}

	public void setTextParameters(Page page, OCDText text, StringBuilder sb, boolean firstText) throws PDFException {
		Font font = page.pdfWriter().fontManager.resolveFontID(text.fontname());
		// transform
		writeTextMatrix(sb, this.textMatrix = text.transform3().floatValues());

		// parameters
		if (this.font == null || this.font.getID() != font.getID() || text.fontsize() != currentFontSize) {
			this.font = font;
			page.linkFont(font);
			String fontName = FontManager.FONT_SUFFIX + font.getID();
			currentFontSize = text.fontsize();
			sb.append(SOLIDUS + fontName + SPACE + S(currentFontSize) + SPACE + PDF_FONT_SIZE + LINE_FEED);
		}
		setGraphicsParameters(page, text, sb);
	}

	public void setGraphicsParameters(Page page, OCDPaintableLeaf node, StringBuilder sb) {
		// clipping
		if (!node.is(OCDText.TAG)) {
			setClip(node.clip(), page, sb);
		}
		// transform
		// float[] transformMatrix =
		// paintable.transform().transform3().floatValues();
		// writeMatrix(stringBuilder, transformMatrix);
		// stroke
		Stroke3 stroke = node.stroke();
		float width = Math.abs(stroke.getLineWidth() * node.transform3().floatValues()[0]);
		if (beginText || neq(this.lineWidth, width) || beginGraphics) {
			sb.append(S(this.lineWidth = width) + SPACE + PDF_LINE_WIDTH + LINE_FEED);
		}

		int cap = convertLineCap(stroke.getEndCap());
		if (beginText || this.lineCap != cap)
			sb.append((this.lineCap = cap) + SPACE + PDF_LINE_CAP + LINE_FEED);

		int join = convertLineJoin(stroke.getLineJoin());
		if (beginText || this.lineJoin != join)
			sb.append((this.lineJoin = join) + SPACE + PDF_LINE_JOIN + LINE_FEED);

		float miter = stroke.getMiterLimit();
		if (beginText || neq(this.miterLimit, miter))
			sb.append(S(this.miterLimit = miter) + SPACE + PDF_MITER_LIMIT + LINE_FEED);

		float[] dashArray = stroke.getDashArray();
		if (dashArray == null)
			dashArray = new float[0];
		float dashPhase = stroke.getDashPhase();
		if (beginText || !equals(this.dashArray, dashArray) || neq(this.dashPhase, dashPhase)) {
			this.dashArray = dashArray;
			this.dashPhase = dashPhase;
			sb.append(LEFT_SQUARE_BRACKET);
			for (int d = 0; d < dashArray.length; d++) {
				if (d > 0)
					sb.append(SPACE);
				sb.append(PDFUtil.Print(dashArray[d]));
			}
			sb.append(RIGHT_SQUARE_BRACKET + SPACE + PDFUtil.Print(dashPhase) + SPACE + PDF_DASH + LINE_FEED);
		}
		// colors
		setColors(node.strokeColor(), node.fillColor(), sb);

		beginText = false;
		beginGraphics = false;
	}

	public void saveState(StringBuilder sb) {
		sb.append(PDF_SAVE_GRAPHIC_STATE + LINE_FEED);
		lineWidth = 1.0f;
		lineCap = 0;
		lineJoin = 0;
		miterLimit = 10f;
		dashArray = new float[0];
		dashPhase = 0;
		strokeColor = fillColor = (params.getColorSpace() == PDFParams.VALUE_CMYK_COLORSPACE
				? new float[] { 0, 0, 0, 1 }
				: new float[] { 0, 0, 0 });

	}

	public void restoreState(StringBuilder sb) {
		sb.append(PDF_RESTORE_GRAPHIC_STATE + LINE_FEED);
		// OCD don't save the graphic state
		sb.append(PDFUtil.Print(lineWidth) + SPACE + PDF_LINE_WIDTH + LINE_FEED);
		sb.append(lineCap + SPACE + PDF_LINE_CAP + LINE_FEED);
		sb.append(lineJoin + SPACE + PDF_LINE_JOIN + LINE_FEED);
		sb.append(PDFUtil.Print(miterLimit) + SPACE + PDF_MITER_LIMIT + LINE_FEED);

		sb.append(LEFT_SQUARE_BRACKET);
		for (int d = 0; d < dashArray.length; d++) {
			if (d > 0)
				sb.append(SPACE);
			sb.append(PDFUtil.Print(dashArray[d]));
		}
		sb.append(RIGHT_SQUARE_BRACKET + SPACE + PDFUtil.Print(dashPhase) + SPACE + PDF_DASH + LINE_FEED);

		for (int c = 0; c < strokeColor.length; c++)
			sb.append(PDFUtil.Print(strokeColor[c]) + SPACE);
		if (params.getColorSpace() == PDFParams.VALUE_CMYK_COLORSPACE)
			sb.append(PDF_STROKE_COLOR_CMYK + LINE_FEED);
		else
			sb.append(PDF_STROKE_COLOR_RGB + LINE_FEED);
		for (int c = 0; c < fillColor.length; c++)
			sb.append(PDFUtil.Print(fillColor[c]) + SPACE);
		if (params.getColorSpace() == PDFParams.VALUE_CMYK_COLORSPACE)
			sb.append(PDF_FILL_COLOR_CMYK + LINE_FEED);
		else
			sb.append(PDF_FILL_COLOR_RGB + LINE_FEED);
	}

	private void setColors(Color3 stroke, Color3 fill, StringBuilder sb) {
		boolean isCMYK = params.getColorSpace() == PDFParams.VALUE_CMYK_COLORSPACE;

		if (stroke != null && !stroke.isTransparent()) {
			float[] strokes;
			if (stroke.getTransparency() == Color3.OPAQUE) {
				strokes = stroke.getRGBColorComponents(new float[3]);
				if (isCMYK) {
					strokes = rgbToCmyk(strokes);
				}
				if (beginText || !equals(this.strokeColor, strokes)) {
					this.strokeColor = strokes;
				}
			} else {
				strokes = new float[] { fill.red(), fill.green(), fill.blue() };
				if (isCMYK) {
					strokes = rgbToCmyk(strokes);
				}
				this.strokeColor = strokes;
			}
			for (int c = 0; c < strokes.length; c++) {
				sb.append(PDFUtil.Print(strokes[c]) + SPACE);
			}
			if (isCMYK)
				sb.append(PDF_STROKE_COLOR_CMYK + LINE_FEED);
			else
				sb.append(PDF_STROKE_COLOR_RGB + LINE_FEED);
		}

		if (fill != null && !fill.isTransparent()) {
			// fill
			float[] fills;
			if (fill.getTransparency() == Color3.OPAQUE) {
				fills = fill.getRGBColorComponents(new float[3]);
				if (isCMYK) {
					fills = rgbToCmyk(fills);
				}
				if (beginText || !equals(this.fillColor, fills)) {
					this.fillColor = fills;
				}
			} else {
				fills = new float[] { fill.red(), fill.green(), fill.blue() };
				if (isCMYK) {
					fills = rgbToCmyk(fills);
				}
				this.fillColor = fills;
			}
			for (int c = 0; c < fills.length; c++) {
				sb.append(PDFUtil.Print(fills[c]) + SPACE);
			}
			if (isCMYK)
				sb.append(PDF_FILL_COLOR_CMYK + LINE_FEED);
			else
				sb.append(PDF_FILL_COLOR_RGB + LINE_FEED);
		}
	}

	public void writeMatrix(StringBuilder sb, float[] matrix) {
		if (equals(matrix, IDENTITY_MATRIX))
			return;
		for (int c = 0; c < matrix.length; c++)
			sb.append(PDFUtil.Print(matrix[c]) + SPACE);
		sb.append(PDF_TRANSFORM_MATRIX + LINE_FEED);
	}

	private void writeTextMatrix(StringBuilder sb, float[] matrix) {
		Transform3 af = new Transform3();
		af.concatenate(new Transform3(matrix));
		af.concatenate(new Transform3(VERTICAL_REFLECTION_MATRIX));
		matrix = af.floatValues();
		// clean
		for (int c = 0; c < matrix.length; c++)
			sb.append(PDFUtil.Print(Util.format(matrix[c])) + SPACE);
		sb.append(PDF_TEXT_MATRIX + LINE_FEED);
	}

	private int convertLineCap(int value) {
		switch (value) {
		case Stroke3.CAP_BUTT:
			return PDF_LINE_CAP_BUTT;
		case Stroke3.CAP_ROUND:
			return PDF_LINE_CAP_ROUND;
		case Stroke3.CAP_SQUARE:
			return PDF_LINE_CAP_PROJECTING_SQUARE;
		}
		return PDF_LINE_CAP_BUTT;
	}

	private int convertLineJoin(int value) {
		switch (value) {
		case Stroke3.JOIN_MITER:
			return PDF_LINE_MITER_JOIN;
		case Stroke3.JOIN_ROUND:
			return PDF_LINE_ROUND_JOIN;
		case Stroke3.JOIN_BEVEL:
			return PDF_LINE_BEVEL_JOIN;
		}
		return PDF_LINE_MITER_JOIN;
	}

	private boolean equals(float[] a0, float[] a1) {
		if (a0 == null && a1 != null || a0 != null && a1 == null)
			return false;
		if (a0 == null && a1 == null)
			return true;
		if (a0.length != a1.length)
			return false;
		for (int i = 0; i < a0.length; i++)
			if (neq(a0[i], a1[i]))
				return false;

		return true;
	}

	private boolean neq(double a, double b) {
		return Math.abs(a - b) > epsilon;
	}

	public Font getFont() {
		return font;
	}

	public float[] rgbToCmyk(float... rgb) {
		/*float[] cmyk = new float[4];
		if (rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 0) {
			cmyk[3] = 1;
		} else {
			cmyk[0] = 1 - rgb[0];
			cmyk[1] = 1 - rgb[1];
			cmyk[2] = 1 - rgb[2];
			cmyk[3] = Math.min(cmyk[0], Math.min(cmyk[1], cmyk[2]));

			cmyk[0] = (cmyk[0] - cmyk[3]) / (1 - cmyk[3]);
			cmyk[1] = (cmyk[1] - cmyk[3]) / (1 - cmyk[3]);
			cmyk[2] = (cmyk[2] - cmyk[3]) / (1 - cmyk[3]);
		}
		return cmyk;*/
		float[] result = PDFColorSpaceManager.getCYMKColor(rgb);
		return result;
	}

	private static String S(double v)
	{
		return PDFUtil.Print(v);
	}
}
