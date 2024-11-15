package sugarcube.formats.pdf.reader.pdf.encryption;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Byte;
import sugarcube.formats.pdf.reader.pdf.encryption.box.StandardSecurityHandler;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;
import sugarcube.formats.pdf.reader.pdf.object.PDFTrailer;
import sugarcube.formats.pdf.reader.pdf.object.Reference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PDFCipher
{
  private EncryptionDico encryptDico;

  public PDFCipher(PDFTrailer trailer, PDFDictionary encrypt)
  {

    Log.debug(this, " - " + encrypt);

    String filter = encrypt.get("Filter").stringValue();

    if (filter.equals("Standard"))
      this.encryptDico = new EncryptionDico(trailer, encrypt);
    else
      Log.warn(this, " - unknown encryption handler: " + filter);

    if (encryptDico.requiresUserPassword())
      Log.warn(this, " - this file requires user password");
  }

  public byte[] decryptAES(PDFObject obj, byte[] data)
  {
    try
    {
      StandardSecurityHandler handler = new StandardSecurityHandler(encryptDico);
      Reference ref = obj.nearestReference();
      ByteArrayOutputStream outStream = new ByteArrayOutputStream();

      handler.encryptData(ref.id(), ref.generation(), new ByteArrayInputStream(data), outStream, true);
      return outStream.toByteArray();
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return data;
  }

  public byte[] decrypt(PDFObject obj, byte[] data)
  {
    boolean isAES = encryptDico != null && encryptDico.isAESV2or3();    
    
    Reference reference = obj.nearestReference();
//    Log.debug(this,  ".decrypt - "+reference+", AES="+isAES);
    if (isAES)        
      return decryptAES(obj, data);
 
    byte[] key = encryptDico.getEncryptionKey();
    int keyLength = key.length;
    byte[] key5 = new byte[keyLength + 5];
    System.arraycopy(key, 0, key5, 0, keyLength);
    byte[] supplementalData = Byte.intToBytesLE(reference.id());
    System.arraycopy(supplementalData, 0, key5, keyLength, 3);
    supplementalData = Byte.intToBytesLE(reference.generation());
    System.arraycopy(supplementalData, 0, key5, keyLength + 3, 2);
    try
    {
      MessageDigest messageDigest = MessageDigest.getInstance("MD5");
      key5 = messageDigest.digest(key5);
    } catch (NoSuchAlgorithmException e)
    {
      Log.warn(this, ".encrypt - unable to instanciate MD5 and to digest key");
      e.printStackTrace();
      return null;
    }
    // step 4
    ArcFour arc4 = new ArcFour();
    arc4.createEncryptionKey(key5, Math.min(keyLength + 5, 16));
    byte[] out = new byte[data.length]; // data.length-2? c'est quoi ce bordel?
    arc4.decrypt(data, 0, out, 0, out.length); // TODO correct stream length*/
    /*
     * System.out.println(id + " " + generation + " " + data2.length); for (int
     * i = 0; i < data2.length; i++) System.out.print((char)(data2[i] & 255));
     * System.out.println();
     */

    if (false)
    {
      boolean identity = data.length == out.length;
      if (identity)
        for (int i = 0; i < data.length; i++)
          if (data[i] != out[i])
          {
            identity = false;
            break;
          }
      Log.debug(this, ".encrypt - in.length=" + data.length + ", out.length=" + out.length + ", identity=" + identity);
    }
    return out;
  }

  @Override
  public String toString()
  {
    return encryptDico.toString();
  }
}
