package sugarcube.formats.pdf.reader.pdf.object;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.xml.Treezable;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class PDFObject implements Treezable, Iterable<PDFObject>
{
    public static final int DEFAULT_INT_VALUE = -1;

    public enum Type
    {
        Environment, Array, Rectangle, Boolean, Dictionary, IndirectReference, Name, Null, Number, Operator, Stream, String, Trailer;
    }

    private final PDFEnvironment environment;
    protected LinkedList<PDFObject> children = new LinkedList<PDFObject>();
    protected PDFObject parent;
    protected boolean isValid = true;
    // reference pointing on this PDFObject if any
    protected Reference reference = Reference.UNDEF;
    protected StreamLocator streamLocator = null;
    public final Type type;
    public String debug = "";

    protected PDFObject(Type type)
    {
        this.type = type;
        this.isValid = false;
        this.environment = null;
    }

    /**
     * pdfObject==null only if PDFEnvironment!!!
     */
    public PDFObject(Type type, PDFObject po)
    {
        this.type = type;
        if (po == null)
            this.environment = (PDFEnvironment) this;
        else
            this.environment = po.environment();
    }

    /**
     * Every PDFObject should return a string value. This string value should
     * represent it's content with a string. For example a PDFNumber may return
     * "-2.2", a PDFBoolean "true" and so on.
     *
     * @return The string value of this PDFObject
     */
    public abstract String stringValue();

    /**
     * Each PDFObject should return information about it's content. This
     * information is intended to be printed on screen. For example on a JTree
     * panel. The information should be precise and short, e.g, the name of the
     * object with a key feature (FontType1 - Arial)
     *
     * @return Information about the PDFObject
     */
    @Override
    public abstract String sticker();

    public String nodeNamePrefix()
    {
        if (parent != null && parent.isPDFDictionary())
            return reference + " " + parent.toPDFDictionary().key(this) + " » ";
        else
            return reference.toString() + " ";
    }

    public PDFObject setStreamLocator(StreamLocator streamLocator)
    {
        this.streamLocator = streamLocator;
        return this;
    }

    public StreamLocator streamLocator()
    {
        return streamLocator;
    }

    private List<PDFObject> addSubTree(List<PDFObject> nodes, PDFObject parent, PDFObject.Type type)
    {
        if (type == null || parent.type == type)
            nodes.add(parent);
        for (PDFObject child : parent)
            addSubTree(nodes, child, type);
        return nodes;
    }

    public List<PDFObject> getNodes()
    {
        return getNodes(null);
    }

    public List<PDFObject> getNodes(PDFObject.Type type)
    {
        return addSubTree(new LinkedList<PDFObject>(), this, type);
    }

    /**
     * ******************** Node Part **************************
     */
    public boolean add(PDFObject pdfObject)
    {
        pdfObject.parent = this;
        return children.add(pdfObject);
    }

    public boolean addFirst(PDFObject pdfObject)
    {
        pdfObject.parent = this;
        children.addFirst(pdfObject);
        return true;
    }

    public boolean addAll(Collection<? extends PDFObject> pdfObjects)
    {
        for (PDFObject pdfObject : pdfObjects)
            add(pdfObject);
        return true;
    }

    @Override
    public Iterator<PDFObject> iterator()
    {
        return children.iterator();
    }

    public PDFObject get(int index)
    {
        return children.get(index);
    }

    public PDFObject first()
    {
        return children.getFirst();
    }

    public PDFObject second()
    {
        return children.get(1);
    }

    public PDFObject last()
    {
        return children.getLast();
    }

    public PDFObject[] array()
    {
        return children.toArray(new PDFObject[0]);
    }

    public List<PDFObject> list()
    {
        return children;
    }

    @Override
    public List<PDFObject> children()
    {
        return children;
    }

    public PDFObject remove(PDFObject pdfObject)
    {
        return children.remove(pdfObject) ? pdfObject : null;
    }

    public PDFObject removeLast()
    {
        return children.removeLast();
    }

    public int nbOfChildren()
    {
        return children.size();
    }

    @Override
    public PDFObject parent()
    {
        return this.parent;
    }

    /**
     * ******************** Object Part ******************************
     */
    /**
     * An indirect object has an id and a generation number, stands generally at
     * the root of the file, but may be in streams since PDF 1.5.
     */
    public boolean isIndirectObject()
    {
        return reference.isIndirectReference();
    }

    public PDFEnvironment environment()
    {
        return environment == null && type == Type.Environment ? (PDFEnvironment) this : environment;
    }

    public boolean isEncrypted()
    {
        return (environment() != null && environment().getTrailer() != null && environment().getTrailer().isEncrypted());
    }

    public PDFObject parsePDFObject(String word, StreamReader reader)
    {
        return environment.parsePDFObject(word, reader);
    }

    public PDFObject parsePDFObject(int id, int generation, String word, StreamReader reader)
    {
        return environment.parsePDFObject(id, generation, word, reader);
    }

    /**
     * An object may be invalid, this should avoid throwing exceptions, for
     * example an invalid PDFNumber is equal to Double.MIN_VALUE. Caution: PDFNull
     * returns always false!
     *
     * @return the validity.
     */
    public boolean isValid()
    {
        return this.isValid;
    }

    public boolean isInvalid()
    {
        return !this.isValid();
    }

    public void invalidate()
    {
        this.isValid = false;
    }

    protected void addTrigger(Reference reference, PDFObject pdfObject)
    {
        this.environment().triggers().put(reference, pdfObject);
    }

    protected void trigger(Reference reference, PDFObject pdfObject)
    {
    }

    public boolean hasReference()
    {
        return this.reference != null && !this.reference.isUndef();
    }

    public void setReference(PDFPointer pointer)
    {
        this.reference = pointer == null ? reference : pointer.get();
    }

    public void setReference(int id, int generation)
    {
        this.reference = new Reference(id, generation);
    }

    public void setReference(Reference reference)
    {
        this.reference = reference;
    }

    public Reference reference()
    {
        return reference;
    }

    public boolean isReference(int id)
    {
        return this.reference != null && this.reference.id() == id;
    }

    public boolean isReference(int id, int gen)
    {
        return this.reference != null && this.reference.id() == id && this.reference.generation() == gen;
    }

    public Reference nearestReference()
    {
        PDFObject nearest = this;
        while (!nearest.reference().isIndirectReference() && nearest.parent != null)
            nearest = nearest.parent;
        return nearest.reference;
    }

    public boolean booleanValue(boolean def)
    {
        if (this.unreference().isValid())
            return unreference().booleanValue();
        else
            return def;
    }

    public boolean booleanValue()
    {
        if (true)
            throw new RuntimeException();
        Log.warn(this, ".booleanValue  - default \"false\" value returned: this.class=" + this.getClass().getName());
        return false;
    }

    public int intValue(int def)
    {
        if (this.unreference().isValid())
            return unreference().intValue();// never change to intValue(defaultValue)
            // => infinite recursion
        else
            return def;
    }

    public int intValue()
    {
        Log.warn(this, ".intValue - default \"-1\" value returned: this.class=" + this.getClass().getName() + " " + this.reference());
        try
        {
            if (false)
                throw new Exception();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return DEFAULT_INT_VALUE;// never change this
    }

    public int[] intValues(int... def)
    {
        if (this.unreference().isValid())
            return unreference().intValues();
        else
            return def;
    }

    public int[] intValues()
    {
        if (true)
            throw new RuntimeException();
        Log.warn(this, ".intValues - default \"new int[0]\" value returned: this.class=" + this.getClass().getName());
        return new int[0];
    }

    public double doubleValue(double def)
    {
        if (this.unreference().isValid())
            return unreference().doubleValue();
        else
            return def;
    }

    public double doubleValue()
    {
//    Log.Stacktrace(this,  ".doubleValue");
        Log.warn(this, ".doubleValue - default \"-1.0\" value returned: " + this.getClass().getSimpleName());
        return -1.0;
    }

    public double[] doubleValues(double... def)
    {
        if (this.unreference().isValid())
            return unreference().doubleValues();
        else
            return def;
    }

    public double[] doubleValues()
    {
//    if (true)
//      throw new RuntimeException();
        Log.warn(this, ".doubleValues - default value \"new double[0]\" returned: this.class=" + this.getClass().getName());
        return new double[0];
    }

    public float floatValue(float def)
    {
        return (float) doubleValue(def);
    }

    public float floatValue()
    {
        return (float) doubleValue();
    }

    public float[] floatValues(float... def)
    {
        if (this.unreference().isValid())
            return unreference().floatValues();
        else
            return def;
    }

    public float[] floatValues()
    {
        Log.warn(this, ".floatValues - default \"-1.0f\" value returned: this.class=" + this.getClass().getName());
        return new float[0];
    }

    public boolean[] booleanValues(boolean... def)
    {
        if (this.unreference().isValid())
            return unreference().booleanValues();
        else
            return def;
    }

    public boolean[] booleanValues()
    {
        Log.warn(this, ".booleanValues - default \"false\" value returned: this.class=" + this.getClass().getName());
        return new boolean[0];
    }

    public String xmlStringValue(String def)
    {
        StringBuilder sb = new StringBuilder(stringValue(def));
        for (int i = 0; i < sb.length(); i++)
            if (sb.charAt(i) < 32)
                sb.setCharAt(i, ' ');
        return sb.toString();
    }

    public String stringValue(String def)
    {
        if (this.unreference().isValid())
            return unreference().stringValue();
        else
            return def;
    }

    @Override
    public String toString()
    {
        return type.name();
    }

    public boolean isType(Type type)
    {
        return this.type.equals(type);
    }

    public boolean isPDFArray()
    {
        return type == Type.Array;
    }

    public PDFArray toPDFArray(PDFArray pdfArray)
    {
        PDFObject pdfObject = unreference();
        return pdfObject.isPDFArray() ? (PDFArray) pdfObject : pdfArray;
    }

    public PDFArray toPDFArray()
    {
        return toPDFArray(PDFArray.NULL_PDFARRAY);
    }

    public PDFRectangle toPDFRectangle(PDFRectangle pdfRectangle)
    {
        PDFObject pdfObject = unreference();
        return pdfObject.type == Type.Array ? ((PDFArray) pdfObject).pdfRectangle() : pdfRectangle;
    }

    public PDFRectangle toPDFRectangle()
    {
        return toPDFRectangle(PDFRectangle.NULL_PDFRECTANGLE);
    }

    public boolean isPDFBoolean()
    {
        return type == Type.Boolean;
    }

    public PDFBoolean toPDFBoolean(PDFBoolean pdfBoolean)
    {
        PDFObject pdfObject = unreference();
        return pdfObject.isPDFBoolean() ? (PDFBoolean) pdfObject : pdfBoolean;
    }

    public PDFBoolean toPDFBoolean()
    {
        return toPDFBoolean(PDFBoolean.NULL_PDFBOOLEAN);
    }

    public boolean isPDFDictionary()
    {
        return type == Type.Dictionary || type == Type.Stream || type == Type.Trailer;
    }

    public PDFDictionary toPDFDictionary(PDFDictionary pdfDictionary)
    {
        PDFObject pdfObject = unreference();
        return pdfObject.isPDFDictionary() ? (PDFDictionary) pdfObject : pdfDictionary;
    }

    public PDFDictionary toPDFDictionary()
    {
        return toPDFDictionary(PDFDictionary.NULL_PDFDICTIONARY);
    }

    public boolean isPDFName()
    {
        return type == Type.Name;
    }

    public PDFName toPDFName(PDFName pdfName)
    {
        PDFObject pdfObject = unreference();
        return pdfObject.isPDFName() ? (PDFName) pdfObject : pdfName;
    }

    public PDFName toPDFName()
    {
        return toPDFName(PDFName.NULL_PDFNAME);
    }

    public boolean isPDFNull()
    {
        return type == Type.Null;
    }

    public PDFNull toPDFNull()
    {
        PDFObject pdfObject = unreference();
        return pdfObject.isPDFNull() ? (PDFNull) pdfObject : PDFNull.NULL_PDFNULL;
    }

    public boolean isPDFNumber()
    {
        return type == Type.Number;
    }

    public PDFNumber toPDFNumber(double number)
    {
        PDFObject pdfObject = unreference();
        return pdfObject.isPDFNumber() ? (PDFNumber) pdfObject : new PDFNumber(this, number);
    }

    public PDFNumber toPDFNumber(PDFNumber pdfNumber)
    {
        PDFObject pdfObject = unreference();
        return pdfObject.type == Type.Number ? (PDFNumber) pdfObject : pdfNumber;
    }

    public PDFNumber toPDFNumber()
    {
        return toPDFNumber(PDFNumber.NULL_PDFNUMBER);
    }

    public boolean isPDFOperator()
    {
        return type == Type.Operator;
    }

    public PDFOperator toPDFOperator(PDFOperator pdfOperator)
    {
        PDFObject pdfObject = unreference();
        return pdfObject.isPDFOperator() ? (PDFOperator) pdfObject : pdfOperator;
    }

    public PDFOperator toPDFOperator()
    {
        return toPDFOperator(PDFOperator.NULL_PDFOPERATOR);
    }

    public boolean isPDFStream()
    {
        return type == Type.Stream;
    }

    public PDFStream toPDFStream(PDFStream pdfStream)
    {
        PDFObject pdfObject = unreference();
        return pdfObject.isPDFStream() ? (PDFStream) pdfObject : pdfStream;
    }

    public PDFStream toPDFStream()
    {
        return toPDFStream(PDFStream.NULL_PDFSTREAM);
    }

    public boolean isPDFString()
    {
        return type == Type.String;
    }

    public PDFString toPDFString(PDFString pdfString)
    {
        PDFObject pdfObject = unreference();
        return pdfObject.isPDFString() ? (PDFString) pdfObject : pdfString;
    }

    public PDFString toPDFString()
    {
        return toPDFString(PDFString.NULL_PDFSTRING);
    }

    public boolean isPDFPointer()
    {
        return type == Type.IndirectReference;
    }

    public PDFPointer toPDFPointer()
    {
        return isPDFPointer() ? (PDFPointer) this : PDFPointer.NULL_POINTER;
    }

    public PDFObject unreference()
    {
        PDFObject pdfObject = this;
        PDFPointer pointer = null;
        while (pdfObject.isPDFPointer())
            pdfObject = environment.getPDFObject((pointer = (PDFPointer) pdfObject).get());
        if (pointer != null)
            pdfObject.setReference(pointer);
        return pdfObject;
    }
}
