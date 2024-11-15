package sugarcube.formats.pdf.writer.document.image;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.graphics.Image3;
import sugarcube.formats.ocd.objects.OCDImage;
import sugarcube.formats.pdf.writer.PDFColorSpaceManager;
import sugarcube.formats.pdf.writer.PDFParams;
import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.Lexic;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.exception.PDFException;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.zip.DeflaterOutputStream;

public class ImageManager {
	private final static int RGB_NUMBER_OF_SAMPLES = 3;
	private final static int CMYK_NUMBER_OF_SAMPLES = 4;
	public final static String IMAGE_SUFFIX = "I";
	private StringMap<Integer> map = new StringMap<>();
	private PDFWriter env;
	private byte[] compressedStream;
	private int compressedStreamLength;
	private int decompressedStreamLength;
	private Object bitsPerComponent = 8;

	public ImageManager(PDFWriter env) {
		this.env = env;
	}

	public int resolveImageID(OCDImage image) throws PDFException {
		String id = image.filename();
		if (map.containsKey(id))
			return map.get(id);
		int maskID = -1;

		Image3 image3 = image.image3();
		if (image3 == null) {
			Log.warn(this, ".resolveImageID - null image: " + image);
			return -1;
		}

		double scale = env.params().sampling(1.0);
		if (scale != 1.0)
			image3 = image3.scale(scale);
		
		if (image3.getTransparency() != Transparency.OPAQUE) {
			maskID = createImage(image, image3, -1, true);
		}
		int ref = createImage(image, image3, maskID, false);
		map.put(id, ref);
		return ref;
	}

	private final int createImage(OCDImage image, Image3 image3, int maskID, boolean isMask) throws PDFException {
		int id = env.computeID();
		Writer writer = env.getWriter();
		env.registerEntry(id, writer.getWrittenBytes());
		boolean binarize = env.params().binarize(-1) > -1;
		if (env.params().getColorSpace() == PDFParams.VALUE_CMYK_COLORSPACE && !isMask) {
			decompressedStreamLength = image3.width() * image3.height() * CMYK_NUMBER_OF_SAMPLES;
		} else {
			if (binarize)
				decompressedStreamLength = ((image3.width() + 7) / 8) * 8 * image3.height();
			else
				decompressedStreamLength = image3.width() * image3.height() * (isMask ? 1 : RGB_NUMBER_OF_SAMPLES);
		}
		compressedStreamLength = compressStream(image, image3, isMask, maskID);
		// Log.debug(this, ".createImage - dec=" + decompressedStreamLength +
		// ", comp=" + compressedStreamLength);
		writer.openObject(id);
		writeDictionary(writer, image, image3, isMask, maskID);
		writeStream(writer, image, maskID);
		writer.closeObject();
		return id;
	}

	private final void writeStream(Writer writer, OCDImage image, int maskID) throws PDFException {
		writer.openStream();
		writer.write(compressedStream, compressedStreamLength);
		writer.write(Lexic.LINE_FEED);
		writer.closeStream();
	}

	private final int compressStream(final OCDImage image, Image3 image3, boolean isMask, int maskID)
			throws PDFException {
		byte[] stream;
		int binarize = env.params().binarize(-1);
		boolean jpx = env.params().jpx();

		try {
			if (env.params().getColorSpace() == PDFParams.VALUE_CMYK_COLORSPACE && !isMask) {
				stream = ConvertToCMYKImage(image.image3());
			}else if (jpx) {
				JPXEncoder enc = JPXEncoder.New();
				if (binarize > 0) {
					image3 = image3.binary(false, binarize);
					enc.lossless(true);
				}
				stream = enc.compress(image3);
			} else if (binarize > -1) {
				// Log.debug(this, ".compressStream - ccitt threshold="+binarize);
				stream = CCITTG4Encoder.Compress(image3, binarize > -1 ? binarize : 128);
			} else if (isMask || image.filename().endsWith("png") || image3.nbOfComponents() > 3) {
				// create a standard JPG encoder
				Iterator<ImageWriter> jpgWritersList = ImageIO.getImageWritersByFormatName("jpeg");
				ImageWriter jpgWriter = (ImageWriter) jpgWritersList.next();
				ImageWriteParam jpgWriterParameter = jpgWriter.getDefaultWriteParam();
				jpgWriterParameter.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				jpgWriterParameter.setCompressionQuality(1);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				MemoryCacheImageOutputStream mos = new MemoryCacheImageOutputStream(os);
				jpgWriter.setOutput(mos);
				BufferedImage tempImage;
				if (isMask) {
					// create alpha channel for masks
					Raster raster = image3.getAlphaRaster();
					// if indexed PNG, convert to ARGB
					if (raster == null) {
						BufferedImage newImage = new BufferedImage(image3.width(), image3.height(),
								BufferedImage.TYPE_4BYTE_ABGR);
						Graphics2D g2 = newImage.createGraphics();
						g2.drawImage(image3, 0, 0, null);
						raster = newImage.getAlphaRaster();
					}
					tempImage = new BufferedImage(image3.width(), image3.height(), BufferedImage.TYPE_BYTE_GRAY);
					WritableRaster writableRaster = tempImage.getRaster();
					try {
						for (int x = 0; x < image3.width(); x++) {
							for (int y = 0; y < image3.height(); y++) {
								writableRaster.setSample(x, y, 0, raster.getSample(x, y, 0));
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					// create standard image
					tempImage = new BufferedImage(image3.width(), image3.height(), BufferedImage.TYPE_INT_RGB);
					Graphics2D g2 = tempImage.createGraphics();
					g2.setColor(Color.WHITE);
					g2.fillRect(0, 0, image3.width(), image3.height());
					g2.drawImage(image3, 0, 0, null);
				}
				IIOImage img = new IIOImage(tempImage, null, null);
				try {
					jpgWriter.write(null, img, jpgWriterParameter);
				} catch (IOException e) {
					throw new PDFException("unable to convert png format to jpg stream");
				}
				jpgWriter.dispose();
				mos.flush();
				os.flush();
				stream = os.toByteArray();
				mos.close();
				os.close();
			} else
				stream = image.data();

			// zip compression
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			DeflaterOutputStream zos = new DeflaterOutputStream(os);
			zos.write(stream);
			zos.flush();
			zos.finish();
			zos.close();
			os.flush();
			os.close();
			compressedStream = os.toByteArray();

			return compressedStream.length;
		} catch (Exception e) {
			e.printStackTrace();
			throw new PDFException("unable to compress stream");
		}
	}
	
	public static final String JAVAX_IMAGEIO_JPEG_IMAGE_1_0 = "javax_imageio_jpeg_image_1.0";
	private byte[] ConvertToCMYKImage(Image3 sourceImage) {
		//get color space
		ColorSpace cmykColorSpace = PDFColorSpaceManager.getCMKYColorSpace();
		ColorModel colorModel = new ComponentColorModel(cmykColorSpace, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		WritableRaster writableRaster = colorModel.createCompatibleWritableRaster(sourceImage.getWidth(), sourceImage.getHeight());
		//copy image
		BufferedImage destinationImage = new BufferedImage(colorModel, writableRaster, colorModel.isAlphaPremultiplied(), null);
		byte[] stream = new byte[0]; 
		try {
			Iterator<ImageWriter> jpgWritersList = ImageIO.getImageWritersByFormatName("jpeg");
			ImageWriter jpgWriter = (ImageWriter) jpgWritersList.next();
			ImageWriteParam jpgWriterParameter = jpgWriter.getDefaultWriteParam();
			//create metadata
			IIOMetadata metadata = jpgWriter.getDefaultImageMetadata(ImageTypeSpecifier.createFromRenderedImage(destinationImage), jpgWriterParameter);
			IIOMetadataNode jpegMeta = new IIOMetadataNode(JAVAX_IMAGEIO_JPEG_IMAGE_1_0);
			jpegMeta.appendChild(new IIOMetadataNode("JPEGVariety"));
			IIOMetadataNode markerSequence = new IIOMetadataNode("markerSequence");
			jpegMeta.appendChild(markerSequence);
			IIOMetadataNode app14Adobe = new IIOMetadataNode("app14Adobe");
			app14Adobe.setAttribute("transform", "0");
			markerSequence.appendChild(app14Adobe);
			metadata.mergeTree(JAVAX_IMAGEIO_JPEG_IMAGE_1_0, jpegMeta);
	        //prepare stream
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			MemoryCacheImageOutputStream mos = new MemoryCacheImageOutputStream(os);
			jpgWriter.setOutput(mos);
            ColorConvertOp rgbToCmyk = new ColorConvertOp(sourceImage.getColorModel().getColorSpace(), cmykColorSpace, null);
            rgbToCmyk.filter(sourceImage, destinationImage);
			//write
			Raster raster = destinationImage.getData();
			jpgWriter.write(null, new IIOImage(raster, null, metadata), jpgWriterParameter);
			mos.flush();
			os.flush();
			stream = os.toByteArray();
			mos.close();
			os.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
		}
		return stream;
	}

	private final void writeDictionary(Writer writer, OCDImage image, Image3 image3, boolean isMask, int maskID)
			throws PDFException {
		boolean jpx = env.params().jpx();
		writer.openDictionary();
		writer.writeDictionaryPair("Type", "XObject", Writer.NAME);
		writer.writeDictionaryPair("Subtype", "Image", Writer.NAME);
		writer.writeDictionaryPair("Width", image3.width(), Writer.INTEGER);
		writer.writeDictionaryPair("Height", image3.height(), Writer.INTEGER);
		boolean ccitt = env.params().binarize(-1) > -1;
		String cs;
		if (env.params().getColorSpace() == PDFParams.VALUE_CMYK_COLORSPACE && !isMask) {
			cs = "DeviceCMYK";
		}else {
			cs = isMask || ccitt || !image.isPNG() && image3.nbOfComponents() < 3 ? "DeviceGray" : "DeviceRGB";
		}
		writer.writeDictionaryPair("ColorSpace", cs, Writer.NAME);
		writer.writeDictionaryPair("BitsPerComponent", !jpx && ccitt ? 1 : bitsPerComponent, Writer.INTEGER);
		writer.writeDictionaryPair("Length", compressedStreamLength, Writer.INTEGER);
		writer.writeDictionaryPair("DL", decompressedStreamLength, Writer.INTEGER);
		// filters
		writer.writeName("Filter");
		writer.write(Lexic.SPACE);
		writer.openArray();
		writer.writeName("FlateDecode");
		writer.write(Lexic.SPACE);
		if (!jpx && ccitt)
			writer.writeName("CCITTFaxDecode");
		else if (jpx)
			writer.writeName("DCTDecode");
		else
			writer.writeName("DCTDecode");
		writer.closeArray();
		// decode params for ccitt
		if (!jpx && ccitt) {
			writer.writeName("DecodeParms");
			writer.openArray();
			writer.writeNull();
			writer.openDictionary();
			writer.writeDictionaryPair("K", -1, Writer.INTEGER);
			writer.writeDictionaryPair("Columns", image3.width(), Writer.INTEGER);
			writer.writeDictionaryPair("Rows", image3.height(), Writer.INTEGER);
			writer.closeDictionary();
			writer.closeArray();
		}
		if (maskID != -1)
			writer.writeDictionaryPair("SMask", maskID, Writer.INDIRECT_REFERENCE);
		writer.closeDictionary();
	}

	public void dispose() {
		this.compressedStream = null;
	}
}
