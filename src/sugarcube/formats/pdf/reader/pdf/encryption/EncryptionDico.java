package sugarcube.formats.pdf.reader.pdf.encryption;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Byte;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFTrailer;

import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionDico
{
  private final static byte[] paddingString = new byte[]
  { 0x28, (byte) (0xBF & 0xFF), 0x4E, 0x5E, 0x4E, 0x75, (byte) (0x8A & 0xFF), 0x41, 0x64, 0x00, 0x4E, 0x56, (byte) (0xFF & 0xFF),
      (byte) (0xFA & 0xFF), 0x01, 0x08, 0x2E, 0x2E, 0x00, (byte) (0xB6 & 0xFF), (byte) (0xD0 & 0xFF), 0x68, 0x3E, (byte) (0x80 & 0xFF), 0x2F, 0x0C,
      (byte) (0xA9 & 0xFF), (byte) (0xFE & 0xFF), 0x64, 0x53, 0x69, 0x7A };

  protected String handlerName;
  protected String subFilter;
  protected int algorithmCode; // algorithm code
  public int length; // key length in bits
  // not yet implemented CF, StmF, StrF, EFF
  protected PDFDictionary encrypt;
  protected PDFDictionary cf;
  protected PDFDictionary stdCF;
  protected String StmF;
  protected String StrF;
  public String cfm;
  public int cfLength;
  public int revision;
  public int permissions;
  public int version;
  public byte[] ownerKey;
  public byte[] userKey;
  public byte[] oe;
  public byte[] ue;
  public byte[] encryptionKey;
  public byte[] fileID;
  public boolean encryptMetadata;
  public boolean requiresUserPassword;
  public String password = "";
  public byte[] idBytes;
  public byte[] perms;

  public EncryptionDico(PDFTrailer trailer, PDFDictionary dico)
  {    
    this.handlerName = "Standard";
    this.encrypt = dico;
    this.subFilter = dico.get("SubFilter").stringValue("null");
    this.algorithmCode = dico.get("V").intValue(0);
    this.length = dico.get("Length").intValue(40);
    if (dico.has("CF"))
    {
      this.cf = dico.get("CF").toPDFDictionary();
      Log.debug(this, " - CF exists");

      if (cf.has("StdCF"))
      {
        Log.debug(this, " - StdCF exists");
        this.stdCF = cf.get("StdCF").toPDFDictionary();
        if (stdCF.has("CFM"))
        {
          cfm = stdCF.get("CFM").stringValue();
          Log.debug(this,  " - cfm="+cfm);
        }
        if (stdCF.has("Length"))
          cfLength = stdCF.get("Length").intValue();
      }

    }
    this.revision = dico.get("R").intValue();
    this.permissions = dico.get("P").intValue();
    this.ownerKey = bytes("O", false);
    this.userKey = bytes("U", false);
    this.version = dico.get("V").intValue();
    this.encryptMetadata = dico.get("EncryptMetadata").booleanValue(true);
    this.oe = bytes("OE", true);
    this.ue = bytes("UE", true);
    this.perms = bytes("Perms", true);
    
    this.fileID = trailer.getFileID().first().toPDFString().byteValues();
    this.idBytes = trailer.idBytes;
    this.requiresUserPassword = !checkUserPassword(bytes(""));

    if (this.requiresUserPassword())
    {
      boolean ok = false;
      while (!ok && password != null)
        if ((password = (String) JOptionPane.showInputDialog(null, "Please enter a password:\n", "Warning - File Encrypted",
            JOptionPane.WARNING_MESSAGE, null, null, null)) != null)
          ok = this.checkUserPassword(bytes(password));
    } else
      this.checkUserPassword(bytes(""));
  }

  
  public boolean isAESV2or3()
  {
    return cfm != null && (cfm.equals("AESV2") || cfm.equals("AESV3"));
  }


  protected byte[] bytes(String s)
  {
    try
    {
      if (false)
        return s.getBytes("UTF-8");
      else
        return s.getBytes();
    } catch (UnsupportedEncodingException ex)
    {
      Log.error(this, "bytes: Encoding error");
      ex.printStackTrace();
      return new byte[0];
    }
  }

  public byte[] bytes(String key, boolean defNull)
  {
    if (encrypt.has(key))
      return encrypt.get(key).toPDFString().byteValues();
    else
      return defNull ? null : new byte[0];
  }

  public final boolean requiresUserPassword()
  {
    return requiresUserPassword;
  }

  public byte[] getEncryptionKey()
  {
    if (encryptionKey == null)
      Log.warn(this, ".getEncryptionKey - the encryption key does not exist since the user password is undefined.");
    byte[] key = new byte[revision == 2 ? 5 : length / 8];
    System.arraycopy(encryptionKey, 0, key, 0, key.length);
    return key;
  }

  @Override
  public String toString()
  {
    return "Revision[" + revision + "]" + "\nAlgorithmCode[" + algorithmCode + "]" + "\nKeyLength[" + length + "]" + "\nOwnerEncryptionKey["
        + Byte.bytesToHexa(ownerKey) + "]" + "\nUserEncyptionKey[" + Byte.bytesToHexa(userKey) + "]" + "\nPermissions["
        + Integer.toBinaryString(permissions & 0xFFFFFFFF) + "]";
  }

  private byte[] createEncryptionKey(byte[] password)
  {
    try
    {
      byte[] pwd = pad(password);
      MessageDigest messageDigest = MessageDigest.getInstance("MD5");
      messageDigest.update(pwd);
      messageDigest.update(ownerKey);
      byte[] permissionsBytes = Byte.intToBytesLE(permissions);
      messageDigest.update(permissionsBytes);
      messageDigest.update(fileID);
      // step 6
      if (revision > 3 && !encryptMetadata)
      {
        Log.warn(this, ".createEncryptionKey - step 6 not yet tested");
        byte[] data = new byte[4];
        for (int i = 0; i < data.length; i++)
          data[i] = (byte) 0xff;
        messageDigest.update(data);
      }
      byte[] hashKey = messageDigest.digest();
      int bytes = revision == 2 ? 5 : length / 8;
      if (revision > 2)
        for (int i = 0; i < 50; i++)
        {
          messageDigest.update(hashKey, 0, bytes);
          hashKey = messageDigest.digest();
        }
      byte[] finalKey = new byte[bytes];
      System.arraycopy(hashKey, 0, finalKey, 0, bytes);
      return finalKey;
    } catch (NoSuchAlgorithmException e)
    {
      Log.warn(this, ".createEncryptionKey - unable to instanciate MD5 and to digest password: " + e);
      return null;
    }
  }

  public boolean checkOwnerPassword(byte[] ownerPassword, byte[] userPassword)
  {
    try
    {
      byte[] key = pad(ownerPassword == null || ownerPassword.length == 0 ? userPassword : ownerPassword);
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      key = md5.digest(key);
      if (revision > 2)
        for (int i = 0; i < 50; i++)
          key = md5.digest(key);
      int keyLength = revision == 2 ? 5 : length / 8;
      ArcFour arc4 = new ArcFour();
      byte[] decryptedKey = new byte[ownerKey.length];
      if (revision == 2)
      {
        arc4.createEncryptionKey(key, keyLength);
        arc4.decrypt(ownerKey, 0, decryptedKey, 0, ownerKey.length);
      } else if (revision > 2)
      {
        byte[] currentEncryptionKey = new byte[ownerKey.length];
        decryptedKey = ownerKey.clone();
        for (int i = 19; i >= 0; i--)
        {
          for (int j = 0; j < key.length; j++)
            currentEncryptionKey[j] = (byte) ((key[j] ^ i) & 255);
          arc4.createEncryptionKey(currentEncryptionKey, keyLength);
          arc4.decrypt(decryptedKey, 0, decryptedKey, 0, decryptedKey.length);
        }
      }
      return checkUserPassword(decryptedKey);
    } catch (NoSuchAlgorithmException e)
    {
      e.printStackTrace();
      return false;
    }
  }

  public void setOwnerEncryptionKey(byte[] ownerPassword, byte[] userPassword)
  {
    try
    {
      byte[] key = pad(ownerPassword == null || ownerPassword.length == 0 ? userPassword : ownerPassword);
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      key = md5.digest(key);
      if (revision > 2)
        for (int i = 0; i < 50; i++)
          key = md5.digest(key);
      int keyLength = revision == 2 ? 5 : length / 8;
      ArcFour arc4 = new ArcFour();
      arc4.createEncryptionKey(key, keyLength);
      byte[] userEncyptionKey = pad(userPassword);
      arc4.encrypt(userEncyptionKey, 0, userEncyptionKey, 0, userEncyptionKey.length);
      if (revision > 2)
      {
        byte[] currentEncriptionKey = new byte[key.length];
        for (int i = 1; i < 20; i++)
        {
          for (int j = 0; j < currentEncriptionKey.length; j++)
            currentEncriptionKey[j] = (byte) ((key[j] ^ i) & 255);
          arc4.createEncryptionKey(currentEncriptionKey, keyLength);
          arc4.encrypt(userEncyptionKey, 0, userEncyptionKey, 0, userEncyptionKey.length);
        }
      }
      ownerKey = new byte[userEncyptionKey.length];
      System.arraycopy(userEncyptionKey, 0, ownerKey, 0, userEncyptionKey.length);
    } catch (NoSuchAlgorithmException e)
    {
      e.printStackTrace();
    }
  }

  public void setUserEncryptionKey(byte[] password)
  {
    this.userKey = createUserEncryptionKey(password);
  }

  public boolean checkUserPassword(byte[] password)
  {
    byte[] passwordKey = createUserEncryptionKey(password);
    for (int i = revision == 2 ? passwordKey.length - 1 : 15; i >= 0; i--)
      if (passwordKey[i] != userKey[i])
        return false;
    return true;
  }

  private byte[] createUserEncryptionKey(byte[] password)
  {
    this.encryptionKey = createEncryptionKey(password);
    if (revision == 2)
    {
      ArcFour arc4 = new ArcFour();
      arc4.createEncryptionKey(encryptionKey, encryptionKey.length);
      byte[] res = new byte[paddingString.length];
      arc4.encrypt(paddingString, 0, res, 0, paddingString.length);
      return res;
    } else if (revision > 2)
      try
      {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(paddingString);
        byte[] hashKey = messageDigest.digest(fileID);
        ArcFour arc4 = new ArcFour();
        arc4.createEncryptionKey(encryptionKey, encryptionKey.length);
        byte[] encryptedHashKey = new byte[hashKey.length];
        arc4.encrypt(hashKey, 0, encryptedHashKey, 0, hashKey.length);
        byte[] currentEncryptionKey = new byte[encryptionKey.length];
        for (int i = 1; i <= 19; i++)
        {
          for (int j = 0; j < encryptionKey.length; j++)
            currentEncryptionKey[j] = (byte) ((encryptionKey[j] ^ i) & 255);
          arc4.createEncryptionKey(currentEncryptionKey, currentEncryptionKey.length);
          arc4.encrypt(encryptedHashKey, 0, encryptedHashKey, 0, encryptedHashKey.length);
        }
        byte[] res = new byte[32];
        System.arraycopy(encryptedHashKey, 0, res, 0, 16);
        System.arraycopy(paddingString, 0, res, 16, 16); // arbitrary
        return res;
      } catch (NoSuchAlgorithmException e)
      {
        Log.error(this, ".createUserEncryptionKey - unable to instanciate MD5 and to digest password: " + e);
        e.printStackTrace();
      }
    return null;
  }

  private byte[] pad(byte[] password)
  {
    byte[] pwd = new byte[32];
    int codes = 0;
    for (int i = 0; i < pwd.length && i < password.length; i++, codes++)
      pwd[i] = password[i];
    for (int i = codes, j = 0; i < pwd.length; i++, j++)
      pwd[i] = paddingString[j];
    return pwd;
  }
}
