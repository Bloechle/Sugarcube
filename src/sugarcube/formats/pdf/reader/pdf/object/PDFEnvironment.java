package sugarcube.formats.pdf.reader.pdf.object;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Map3;
import sugarcube.common.interfaces.Progressable;
import sugarcube.common.system.process.Progression;
import sugarcube.formats.pdf.reader.pdf.encryption.PDFCipher;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Not a real pdf object... but convenient to do it like this PDF Refererence
 * says: The body of a PDF file consists of a sequence of indirect objects
 * representing the contents of a document. The objects, which are of the basic
 * types described in Section 3.2, Objects, represent components of the document
 * such as fonts, pages, and sampled images. Beginning with PDF 1.5, the body
 * can also contain object streams, each of which contains a sequence of
 * indirect objects; see Section 3.4.6, Object Streams.
 */
public class PDFEnvironment extends PDFObject implements Progressable {
	private Progression progression = new Progression("Dexter is Parsing PDF File");
	private final Map3<Reference, PDFObject> triggers = new Map3<>();
	private final Map3<Reference, PDFObject> map = new Map3<>();
	private final Map3<Reference, PDFObject> duplicateMap = new Map3<>();
	public PDFArray lastMaybeSameArray = null;
	private PDFTrailer trailer;
	private String filePath = "null";
	private RandomAccessFile raf = null;

	public PDFEnvironment() {
		super(Type.Environment, null);
		this.initialize();
	}

	public PDFEnvironment(File file) {
		this(file, null);
	}

	public PDFEnvironment(File file, Progression progression) {
		this();
		this.setProgression(progression);
		this.parse(file);
	}

	public PDFEnvironment(Progression progression) {
		this();
		this.setProgression(progression);
	}

	public final void initialize() {
		this.children.clear();
		this.triggers.clear();
		this.map.clear();
		this.trailer = new PDFTrailer(this);
		this.progression.reset();
	}

	public void setProgression(Progression progression) {
		if (progression != null)
			this.progression = progression;
	}

	@Override
	public float progress() {
		return progression.progress();
	}

	@Override
	public String progressName() {
		return progression.progressName();
	}

	@Override
	public int progressState() {
		return progression.progressState();
	}

	@Override
	public String progressDescription() {
		return progression.progressDescription();
	}

	public boolean parse(File file) {
		try {
			return parse(file, false);
		} catch (Exception e) {

		}
		return false;
	}

	public boolean parse(File file, boolean propagateExceptions) throws Exception {
		progression.reset();
		try {
			float fileSize = file.length();
			this.filePath = file.getCanonicalPath();

			Log.debug(this, ".parse - file=" + file + ", exists=" + file.exists());

			StreamReader reader = new StreamReader(new PDFStream(this, raf = new RandomAccessFile(file, "r")));

			String id = null;
			String gen = null;
			String token;
			int lastStreamPos = -1;
			while ((token = reader.token()) != null) {
//        System.out.print("\n"+token + " ");

//				System.out.println("Reading PDF Object no " + this.children.size() + " at " + (reader.pos() / 1000) + " of " + ((int) fileSize / 1000) + " ko");

				if (reader.pos < lastStreamPos) {
					Log.warn(this, ".parse - looping stream: " + filePath);
					return false;
				}

				lastStreamPos = reader.pos;

				// Log.debug(this, ".parse - token="+token);
				progression.setProgress(reader.pos() / fileSize);
				progression.setDescription("Reading PDF Object no " + this.children.size() + " at "
						+ (reader.pos() / 1000) + " of " + ((int) fileSize / 1000) + " ko");
				if (token.equals("obj")) {
					// Sys.Println("Obj " + id + ", Pos " + reader.pos);
					PDFObject o = parsePDFObject(id, gen, reader.token(), reader);
					if (o.isPDFDictionary()) {
						PDFDictionary dico = o.toPDFDictionary();
						if (dico.is("Type", "XRef"))
							trailer.update(dico, "obj");
						else if (dico.is("Type", "Pages"))
							trailer.updatePages(dico);
						else if (dico.is("Type", "Page"))
							trailer.addPage(dico);
					}

				} else if (token.equals("stream")) {
					// already parsed as PDFDictionary and rewrapped here
					PDFStream stream = parsePDFStream(reader);

					// second update with same dictionary... but wrapped in a PDFStream
					if (stream.is("Type", "XRef"))
						trailer.update(stream, "stream");
				} else if (token.equals("R"))
					parsePDFObject(id, gen, token, reader);
				else if (token.equals("xref"))
					parseXRef(reader);
				else if (token.equals("trailer")) {
					token = reader.token();
					if (token.equals("<<"))
						trailer.update(new PDFDictionary(this, reader), "<<");
					else
						Log.warn(this, "parse - no trailer found");
				} else if (token.equals("startxref")) {
					Log.debug(this, ".parse - startxref");
					reader.token();
				} else if (token.equals("%"))
					token = reader.token();
				else {
					id = gen;
					gen = token;
				}
			}

			this.addFirst(trailer);

			trailer.decipher();

			List3<PDFStream> streams = streams();

			// this loop looks for streamed PDF objects (since PDF 1.5)
			for (PDFStream stream : streams)
				if (stream.isObjStm())
					unpackObjStm(stream);

			if (this.duplicateMap.isPopulated())
				Log.debug(this, ".parse - duplicate: size=" + duplicateMap.size() + ", refs=" + duplicateMap.keySet());

			Collections.sort(this.children, (o1, o2) -> Integer.compare(o1.reference.id(), o2.reference.id()));

		} catch (Exception e) {
			if (propagateExceptions) {
				throw e;
			} else {
				e.printStackTrace();
				return false;
			}
		}

		progression.complete();
		return true;
	}

	public void unpackObjStm(PDFStream stream) {
		try {
			// streamed object are now taken into account!
			if (stream.isObjStm()) {
				stream.debug += "'";
				boolean isReferencePart = true;
				StreamReader subReader = new StreamReader(stream);
				int refSize = stream.get("N").intValue();
				LinkedList<PDFNumber> refs = new LinkedList<>();
				String subToken;

				// Log.debug(this, ".unpackObjStm - "+stream.asciiValue());

				while ((subToken = subReader.token()) != null) {
					PDFObject o = this.parsePDFObject(subToken, subReader);
					o.debug += "'";

					if (isReferencePart && o.type.equals(PDFObject.Type.Number) && refs.size() < 2 * refSize) {
						refs.add(o.toPDFNumber());
						if (refs.size() == 2 * refSize)
							isReferencePart = false;
					} else if (o.isValid && refs.size() >= 2) {
						int refId = refs.remove().intValue();
						// if(refId==2138)
						// {
						// Log.stacktrace(this, ".parse - "+subReader.toString());
						// }
						o.setReference(refId, 0);
						// Log.debug(this, "unpackObjStm - refId="+refId);
						// here we should use and check for offset correctness...
						int refOffset = refs.remove().intValue();
						addPDFObject(o, false);
					} else
						Log.debug(this, ".parse - unexpected streamed object: " + o);

					if (o.isPDFDictionary()) {
						PDFDictionary dico = o.toPDFDictionary();
						if (dico.is("Type", "XRef")) {
							Log.debug(this, ".parse - XRef: " + dico.reference());
							// trailer.update(dico);
						}
						if (dico.is("Type", "Pages"))
							trailer.updatePages(dico);
						else if (dico.is("Type", "Page"))
							trailer.addPage(dico);
					}
				}
				if (!refs.isEmpty())
					Log.debug(this, "unpackObjStm - remaining refs: " + refs);

			}
		} catch (Exception e) {
			Log.warn(this, ".parse - object stream parsing failed: " + stream.reference);
		}
	}

	// public Map3<Reference, PDFObject> objects()
	// {
	// return this.map;
	// }

	public PDFCipher getCipher() {
		return trailer.cipher;
	}

	public PDFTrailer getTrailer() {
		return trailer;
	}

	public Map<Reference, PDFObject> triggers() {
		return triggers;
	}

	public List3<PDFStream> streams() {
		List3<PDFStream> list = new List3<>();
		for (PDFObject po : map.values())
			if (po.isPDFStream())
				list.add(po.toPDFStream());
		for (PDFObject po : duplicateMap.values())
			if (po.isPDFStream())
				list.add(po.toPDFStream());
		return list;
	}

	public String fileName() {
		return new File(filePath).getName();
	}

	public String filePath() {
		return filePath;
	}

	public void lastRoot(PDFObject root) {
		this.trailer.lastRoot = root;
	}

	public void check() {
		Log.debug(this, ".check - missing references: " + this.checkIndirectReferences());
		Log.debug(this, ".check - wrong stream lengths: " + this.checkStreamSizes());
	}

	public List<PDFPointer> checkIndirectReferences() {
		List<PDFPointer> missing = new LinkedList<>();
		for (PDFObject node : this.getNodes(PDFObject.Type.IndirectReference))
			if (!map.containsKey(node.toPDFPointer().get()))
				missing.add(node.toPDFPointer());
		return missing;
	}

	public List<PDFStream> checkStreamSizes() {
		List<PDFStream> wrongs = new LinkedList<>();
		for (PDFObject node : this.getNodes(PDFObject.Type.Stream))
			if (node.toPDFStream().get("Length").toPDFNumber().intValue() != node.toPDFStream().length())
				wrongs.add(node.toPDFStream());
		return wrongs;
	}

	public PDFObject addPDFObject(PDFObject obj, boolean overrideLast) {
		if (obj.isIndirectObject()) {
			Reference ref = obj.reference();
			PDFObject old = map.get(ref);
			if (old != null && old != obj) {
				Log.debug(this, ".addPDFObject - duplicate: \n\nold=" + old.type + " - " + old + "\n\nnew=" + obj.type
						+ " - " + obj);
				if (duplicateMap.has(ref))
					Log.debug(this, ".addPDFObject - triplet: " + ref);
				else
					duplicateMap.put(ref, map.get(ref));
			}
			map.put(ref, obj);
			add(obj);

			if (triggers.containsKey(obj.reference()))
				triggers.get(obj.reference()).trigger(obj.reference(), obj);
		}
		return obj;
	}

	public PDFObject getPDFObject(Reference reference) {
		PDFObject pdfObject = map.get(reference);
		// as specified in PDF Reference
		return pdfObject != null ? pdfObject : new PDFNull(this);
	}

	public void parseXRef(StreamReader reader) {
		Log.debug(this, ".parseXRef");
		reader.token();
		try {
			int nbOfReferences = reader.integer();
			for (int i = 0; i < nbOfReferences; i++)
				for (int j = 0; j < 3; j++)
					// three words per line
					reader.token();
		} catch (Exception e) {
			Log.warn(this, ".parseXRef exception: " + e.getMessage());
		}
	}

	public PDFObject remove(PDFObject pdfObject) {
		return children.remove(pdfObject) ? pdfObject : null;
	}

	public PDFObject removeLast() {
		PDFObject obj = children.removeLast();
		if (obj != null && obj.isIndirectObject())
			map.remove(obj.reference);
		return obj;
	}

	public PDFStream parsePDFStream(StreamReader reader) {
		PDFStream pdfStream = new PDFStream(removeLast().toPDFDictionary(), reader);
		if (pdfStream.isIndirectObject())
			addPDFObject(pdfStream, true);
		else
			Log.debug(this, ".parsePDFStream - not indirect object: " + pdfStream.reference());
		return pdfStream;
	}

	@Override
	public PDFObject parsePDFObject(String word, StreamReader reader) {
		return parsePDFObject(-1, -1, word, reader);
	}

	public PDFObject parsePDFObject(String id, String generation, String token, StreamReader reader) {
		return parsePDFObject(Integer.parseInt(id), Integer.parseInt(generation), token, reader);
	}

	@Override
	public PDFObject parsePDFObject(int id, int generation, String token, StreamReader reader) {
		if (token != null && !token.equals("endobj") && !token.equals("endstream")) {
			PDFObject po;
			if (token.equals("/")) {
				// there is no space between / and name, moreover, / may exists alone
				po = new PDFName(this, PDF.isWhiteSpaceOrDelimiter(reader.view()) ? "" : reader.token());
			} else if (token.equals("(") || token.equals("<"))
				po = new PDFString(this, token, reader);
			else if (token.equals("true") || token.equals("false"))
				po = new PDFBoolean(this, token);
			else if (token.equals("["))
				po = new PDFArray(this, reader);
			else if (token.equals("R"))
				po = new PDFPointer(this, id, generation);
			else if (token.equals("<<"))
				po = new PDFDictionary(this, reader);
			else if (token.equals("null"))
				po = new PDFNull(this, token);
			else if (PDFOperator.isOperator(token))
				po = new PDFOperator(this, token, reader);
			else
				po = new PDFNumber(this, token);

			// Log.debug(this, ".parsePDFObject - " + token + ": " + po.type);
			po.setReference(id, generation);
			po.setStreamLocator(reader.streamLocator());

			if (po.isValid)
				return addPDFObject(po, false);
			// else
			// Log.warn(this, ".parsePDFObject - object is invalid: type=" + po.type +
			// " value=" + token + " pos=" + po.streamPos);
		}
		return new PDFNull(this);
	}

	@Override
	public PDFEnvironment environment() {
		return this;
	}

	@Override
	public String stringValue() {
		return "Environment";
	}

	@Override
	public String toString() {
		return "PDFEnvironment[" + this.nbOfChildren() + "]";
	}

	@Override
	public String sticker() {
		return nodeNamePrefix() + "PDFEnvironment[" + this.nbOfChildren() + "]";
	}

	public boolean dispose() {
		if (raf != null)
			try {
				raf.close();
				return true;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		return false;
	}
}
