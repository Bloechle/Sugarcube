package sugarcube.formats.pdf.reader.pdf.object;

import sugarcube.common.system.log.Log;
import sugarcube.formats.pdf.reader.pdf.encryption.PDFCipher;

import java.util.Arrays;

public class PDFTrailer extends PDFDictionary
{
  protected int size = -1;
  protected int prev = -1;
  protected PDFObject lastRoot = null;
  protected PDFObject root = null;
  protected PDFObject encrypt = null;
  protected PDFObject info = null;
  protected PDFObject id = null;
  protected PDFObject index = null;
  protected PDFObject w = null;
  protected PDFTrailerCrossRef[] crossRefs = null; //
  protected PDFDictionary foundDico = null;
  protected PDFPagesRecover recoverDico = new PDFPagesRecover(this);
  protected String references = "";
  protected PDFCipher cipher;
  public byte[] idBytes = new byte[0];

  public PDFTrailer(PDFEnvironment environment)
  {
    super(Type.Trailer, environment);
    this.add("PageList", recoverDico);
  }

  public PDFPagesRecover recoverDico()
  {
    return this.recoverDico;
  }

  public void updatePages(PDFDictionary dico)
  {
    if (dico.is("Type", "Pages"))
      this.foundDico = dico;
  }

  public void addPage(PDFDictionary dico)
  {
    if (dico.is("Type", "Page"))
    {
      this.recoverDico.addPage(dico);
    }
  }

  public void update(PDFDictionary trailer, String debug)
  {
//    Log.debug(this, ".update - Trailer: " + trailer.reference() + ", from=" + debug);
    this.references += trailer.reference() + " ";
    if (trailer.contains("Size"))
    {
      this.size = trailer.get("Size").intValue();
      this.add("Size", trailer.get("Size"));
    }

    if (trailer.contains("Prev"))
    {
      this.prev = trailer.get("Prev").intValue();
      this.add("Prev", trailer.get("Prev"));
    }

    if (trailer.contains("Root"))
    {
      this.root = trailer.get("Root");
      this.add("Root", root);
    }

    if (trailer.contains("Encrypt"))
    {
      this.encrypt = trailer.get("Encrypt");
      this.add("Encrypt", encrypt);
    }

    if (trailer.contains("Info"))
    {
      this.info = trailer.get("Info");
      this.add("Info", info);
    }

    if (trailer.contains("ID"))
    {
      this.id = trailer.get("ID");
      this.add("ID", id);

      PDFArray array = id.toPDFArray();
      if (array.size() > 0)
      {
        this.idBytes = array.first().toPDFString().byteValues();
//        Log.debug(this, ".update - idBytes=" + Arrays.toString(idBytes));
      }

    }

    if (trailer.isPDFStream())
    {
      PDFStream stream = trailer.toPDFStream();
      // Log.debug(this, ".update - stream: " +
      // Arrays.toString(stream.byteValues()));
      if (trailer.contains("Index"))
      {
        this.index = trailer.get("Index");
        this.add("Index", index);
      }

      if (trailer.contains("W"))
      {
        this.w = trailer.get("W");
        this.add("W", w);
        int[] nbOfBytes = w.toPDFArray().intValues();
        Log.debug(this, ".update - Stream Trailer: w=" + Arrays.toString(nbOfBytes));

        // crossRefs = PDFTrailerCrossRef.Parse(nbOfBytes, stream.byteValues());
        // for (PDFTrailerCrossRef ref : crossRefs)
        // {
        //
        // }
      }

    }
  }

  public void decipher()
  {
    if (encrypt != null)
      try
      {
        this.cipher = new PDFCipher(this, encrypt.toPDFDictionary());
      } catch (Exception e)
      {
        Log.warn(this, ".decipher - PDFCipher instantiation failed: " + e);
        e.printStackTrace();
      }
  }

  @Override
  public boolean add(String key, PDFObject value)
  {
    if (value.isValid)
    {
      map.put(key, value);
      children.add(value);
      if (map.size() != children.size())
      {
        children.clear();
        for (PDFObject o : this.map.values())
          children.add(o);
      }
      return true;
    } else
      return false;
  }

  @Override
  public boolean isEncrypted()
  {
    return encrypt != null && encrypt.toPDFDictionary().isValid();
  }

  public boolean hasInfo()
  {
    return this.info != null && this.info.unreference().isPDFDictionary();
  }

  public PDFDictionary getInfo()
  {
    return info == null ? null : info.toPDFDictionary();
  }

  public PDFArray getFileID()
  {
    return id.toPDFArray();
  }

  public boolean hasRoot()
  {
    return root != null || lastRoot != null;
  }

  public PDFDictionary getRoot()
  {
    return root != null ? root.toPDFDictionary() : lastRoot.toPDFDictionary();
  }

  public PDFDictionary getPages()
  {
    return foundDico;
  }

  public PDFDictionary getEncrypt()
  {
    return encrypt.toPDFDictionary();
  }

  @Override
  public String stringValue()
  {
    return sticker();
  }

  @Override
  public String sticker()
  {
    return nodeNamePrefix() + "Trailer " + references;
  }
}
