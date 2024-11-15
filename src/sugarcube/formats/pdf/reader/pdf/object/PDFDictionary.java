package sugarcube.formats.pdf.reader.pdf.object;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.StringList;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.data.Base;

import java.util.Map;
import java.util.Set;

public class PDFDictionary extends PDFObject
{
    public static class PDFEntry
    {
        public String key;
        public PDFObject obj;

        public PDFEntry(String key, PDFObject obj)
        {
            this.key = key;
            this.obj = obj;
        }

        public String getKey()
        {
            return key;
        }

        public PDFObject getValue()
        {
            return obj;
        }

        public PDFObject unreference()
        {
            return obj.unreference();
        }

        @Override
        public String toString()
        {
            return key + " > " + obj;
        }
    }

    public static final PDFDictionary NULL_PDFDICTIONARY = new PDFDictionary();
    protected StringMap<PDFObject> map = new StringMap<PDFObject>();

    private PDFDictionary()
    {
        super(Type.Dictionary);
    }

    protected PDFDictionary(Type type)
    {
        super(type);
    }

    public PDFDictionary(PDFObject po)
    {
        super(Type.Dictionary, po);
    }

    protected PDFDictionary(Type type, PDFObject po)
    {
        super(type, po);
    }

    protected PDFDictionary(Type type, PDFDictionary dictionary)
    {
        super(type, dictionary);
        this.map.putAll(dictionary.map);
        this.addAll(map.values());
        this.setReference(dictionary.reference());
    }

    public PDFDictionary(PDFEnvironment pdfEnvironment, StreamReader reader)
    {
        super(Type.Dictionary, pdfEnvironment);

        String token;
        PDFName previousKey = null;

        // used only to debug: lasts = new List8();
        StringList lasts = new StringList();

        // TODO: seems to work quite well, but may possibly be improved...
        while ((token = reader.token()) != null && !token.equals(">>"))
        {
            if (lasts != null)
                lasts.add(token);
            if (token.equals("/"))
            {
                token = PDF.isWhiteSpaceOrDelimiter(reader.view()) ? "" : reader.token();
                if (lasts != null)
                    lasts.add("/" + token);
            }
            PDFName key = new PDFName(this, token);
            token = reader.token();

            if (lasts != null)
            {
                lasts.add("parse[" + token + "]");
                while (lasts.size() > 10)
                    lasts.removeFirst();
            }

            PDFObject value = parsePDFObject(token, reader);

            // <</Type/Page/Parent 4 0 R/Resource ...
            if (value.type == Type.IndirectReference)
            {
                // gets the two numbers 4 and 0
                int indirectId = map.get(previousKey.toString()).toPDFNumber().intValue();
                int indirectGeneration = new PDFNumber(this, key.toString()).intValue();

                // and finally assigns the key and pdfObject
                key = previousKey;
                value = new PDFPointer(this, indirectId, indirectGeneration);

                if (key.isName("Root"))
                    this.environment().lastRoot(value);
            }

            previousKey = key;
            if (value.isValid())
                map.put(key.toString(), value);
            else
            {
                map.put(Base.x32.random12() + "_" + key.toString(), value);
                Log.warn(this, " - invalid pdf object: " + value + ", key=" + key + ", value=" + value.sticker() + ", token="
                        + (lasts == null ? token : lasts) + "\nstream=" + reader.toString());
            }
        }

        this.addAll(map.values());
    }

    public boolean add(String key, PDFObject value)
    {
        map.put(key, value);
        return this.add(value);
    }

    public Set<Map.Entry<String, PDFObject>> getEntries()
    {
        return map.entrySet();
    }

    public List3<PDFEntry> entries()
    {
        List3<PDFEntry> entries = new List3<PDFEntry>();
        for (String key : map.keySet())
        {
            entries.add(new PDFEntry(key, map.get(key, null)));
        }
        return entries;
    }

    public Set<String> keys()
    {
        return map.keySet();
    }

    public PDFObject get(String... keys)
    {
        for (String key : keys)
            if (map.containsKey(key))
                return map.get(key);
        return PDFNull.NULL_PDFNULL;
    }

    public PDFObject get(boolean unreference, String... keys)
    {
        PDFObject obj = get(keys);
        if (unreference)
            return obj != null && obj != PDFNull.NULL_PDFNULL ? obj.unreference() : obj;
        else
            return obj;
    }

    public PDFObject get(PDFName key)
    {
        return get(key.stringValue());
    }

    public PDFObject remove(String... keys)
    {
        for (String key : keys)
            if (map.containsKey(key))
                return map.remove(key);
        return PDFNull.NULL_PDFNULL;
    }

    public boolean has(String... keys)
    {
        return this.contains(keys);
    }

    public boolean contains(String... keys)
    {
        for (String key : keys)
            if (map.containsKey(key))
                return true;
        return false;
    }

    public String type()
    {
        if (!map.containsKey("Type"))
            return "";

        PDFObject pdfObject = map.get("Type").unreference();
        if (!pdfObject.isValid())
            Log.warn(this, "is invalid pdfObject, key=Type");
        return pdfObject.isValid() ? pdfObject.stringValue() : "";
    }

    public boolean is(String key, String value)
    {
        if (!map.containsKey(key))
            return false;

        PDFObject pdfObject = map.get(key).unreference();
        if (!pdfObject.isValid())
            Log.warn(this, "is invalid pdfObject, key=" + key);
        return pdfObject.isValid() && pdfObject.stringValue().equals(value);
    }

    public String key(PDFObject reference)
    {
        for (String key : map.keySet())
            if (map.get(key) == reference)
                return key;
        return null;
    }

    public String value(String key)
    {
        return get(key).unreference().stringValue();
    }

    public int size()
    {
        return this.nbOfChildren();
    }

    @Override
    public String stringValue()
    {
        return "Dictionary[" + size() + "]";
    }

    @Override
    public String toString()
    {
        return reference() + " " + map;
    }

    @Override
    public String sticker()
    {
        return nodeNamePrefix() + "Dictionary[" + (map.containsKey("Type") ? map.get("Type").stringValue() : size()) + "]" + debug;
    }
}
