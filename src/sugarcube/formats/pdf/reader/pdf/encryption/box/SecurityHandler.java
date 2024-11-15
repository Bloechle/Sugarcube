package sugarcube.formats.pdf.reader.pdf.encryption.box;


import sugarcube.common.system.log.Log3;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;


/**
 * A security handler as described in the PDF specifications.
 * A security handler is responsible of documents protection.
 *
 * @author Ben Litchfield
 * @author Benoit Guillon
 * @author Manuel Kasper
 */
public abstract class SecurityHandler
{

    private static final int DEFAULT_KEY_LENGTH = 40;

    // see 7.6.2, page 58, PDF 32000-1:2008
    private static final byte[] AES_SALT = { (byte) 0x73, (byte) 0x41, (byte) 0x6c, (byte) 0x54 };

    /** The length in bits of the secret key used to encrypt the document. */
    protected int keyLength = DEFAULT_KEY_LENGTH;

    /** The encryption key that will used to encrypt / decrypt.*/
    protected byte[] encryptionKey;

    /** The RC4 implementation used for cryptographic functions. */
    private final RC4Cipher rc4 = new RC4Cipher();

    /** indicates if the Metadata have to be decrypted of not. */
    private boolean decryptMetadata;


    private boolean useAES;

    /**
     * The access permission granted to the current user for the document. These
     * permissions are computed during decryption and are in read only mode.
     */
//    private AccessPermission currentAccessPermission = null;


    /**
     * Encrypt or decrypt a set of data.
     *
     * @param objectNumber The data object number.
     * @param genNumber The data generation number.
     * @param data The data to encrypt.
     * @param output The output to write the encrypted data to.
     * @param decrypt true to decrypt the data, false to encrypt it.
     *
     * @throws IOException If there is an error reading the data.
     */
    public void encryptData(long objectNumber, long genNumber, InputStream data,
                            OutputStream output, boolean decrypt) throws IOException
    {
        // Determine whether we're using Algorithm 1 (for RC4 and AES-128), or 1.A (for AES-256)
        if (useAES && encryptionKey.length == 32)
        {
            encryptDataAES256(data, output, decrypt);
        }
        else
        {
            byte[] finalKey = calcFinalKey(objectNumber, genNumber);

            if (useAES)
            {
                encryptDataAESother(finalKey, data, output, decrypt);
            }
            else
            {
                encryptDataRC4(finalKey, data, output);
            }
        }
        output.flush();
    }

    /**
     * Calculate the key to be used for RC4 and AES-128.
     *
     * @param objectNumber The data object number.
     * @param genNumber The data generation number.
     * @return the calculated key.
     */
    private byte[] calcFinalKey(long objectNumber, long genNumber)
    {
        byte[] newKey = new byte[encryptionKey.length + 5];
        System.arraycopy(encryptionKey, 0, newKey, 0, encryptionKey.length);
        // PDF 1.4 reference pg 73
        // step 1
        // we have the reference
        // step 2
        newKey[newKey.length - 5] = (byte) (objectNumber & 0xff);
        newKey[newKey.length - 4] = (byte) (objectNumber >> 8 & 0xff);
        newKey[newKey.length - 3] = (byte) (objectNumber >> 16 & 0xff);
        newKey[newKey.length - 2] = (byte) (genNumber & 0xff);
        newKey[newKey.length - 1] = (byte) (genNumber >> 8 & 0xff);
        // step 3
        MessageDigest md = MessageDigests.getMD5();
        md.update(newKey);
        if (useAES)
        {
            md.update(AES_SALT);
        }
        byte[] digestedKey = md.digest();
        // step 4
        int length = Math.min(newKey.length, 16);
        byte[] finalKey = new byte[length];
        System.arraycopy(digestedKey, 0, finalKey, 0, length);
        return finalKey;
    }

    /**
     * Encrypt or decrypt data with RC4.
     *
     * @param finalKey The final key obtained with via {@link #calcFinalKey(long, long)}.
     * @param input The data to encrypt.
     * @param output The output to write the encrypted data to.
     *
     * @throws IOException If there is an error reading the data.
     */
    protected void encryptDataRC4(byte[] finalKey, InputStream input, OutputStream output)
            throws IOException
    {
        rc4.setKey(finalKey);
        rc4.write(input, output);
    }

    /**
     * Encrypt or decrypt data with RC4.
     *
     * @param finalKey The final key obtained with via {@link #calcFinalKey(long, long)}.
     * @param input The data to encrypt.
     * @param output The output to write the encrypted data to.
     *
     * @throws IOException If there is an error reading the data.
     */
    protected void encryptDataRC4(byte[] finalKey, byte[] input, OutputStream output) throws IOException
    {
        rc4.setKey(finalKey);
        rc4.write(input, output);
    }


    /**
     * Encrypt or decrypt data with AES with key length other than 256 bits.
     *
     * @param finalKey The final key obtained with via {@link #calcFinalKey(long, long)}.
     * @param data The data to encrypt.
     * @param output The output to write the encrypted data to.
     * @param decrypt true to decrypt the data, false to encrypt it.
     *
     * @throws IOException If there is an error reading the data.
     */
    private void encryptDataAESother(byte[] finalKey, InputStream data, OutputStream output, boolean decrypt)
            throws IOException
    {
        byte[] iv = new byte[16];

        if (!prepareAESInitializationVector(decrypt, iv, data, output))
        {
            return;
        }

        try
        {
            Cipher decryptCipher;
            try
            {
                decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            }
            catch (NoSuchAlgorithmException e)
            {
                // should never happen
                throw new RuntimeException(e);
            }

            SecretKey aesKey = new SecretKeySpec(finalKey, "AES");
            IvParameterSpec ips = new IvParameterSpec(iv);
            decryptCipher.init(decrypt ? Cipher.DECRYPT_MODE : Cipher.ENCRYPT_MODE, aesKey, ips);
            byte[] buffer = new byte[256];
            int n;
            while ((n = data.read(buffer)) != -1)
            {
                byte[] dst = decryptCipher.update(buffer, 0, n);
                if (dst != null)
                {
                    output.write(dst);
                }
            }
            output.write(decryptCipher.doFinal());
        }
        catch (InvalidKeyException e)
        {
            throw new IOException(e);
        }
        catch (InvalidAlgorithmParameterException e)
        {
            throw new IOException(e);
        }
        catch (NoSuchPaddingException e)
        {
            throw new IOException(e);
        }
        catch (IllegalBlockSizeException e)
        {
            throw new IOException(e);
        }
        catch (BadPaddingException e)
        {
            throw new IOException(e);
        }
    }

    /**
     * Encrypt or decrypt data with AES256.
     *
     * @param data The data to encrypt.
     * @param output The output to write the encrypted data to.
     * @param decrypt true to decrypt the data, false to encrypt it.
     *
     * @throws IOException If there is an error reading the data.
     */
    private void encryptDataAES256(InputStream data, OutputStream output, boolean decrypt) throws IOException
    {
        byte[] iv = new byte[16];

        if (!prepareAESInitializationVector(decrypt, iv, data, output))
        {
            return;
        }

        Cipher cipher;
        try
        {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(encryptionKey, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(decrypt ? Cipher.DECRYPT_MODE : Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        }
        catch (GeneralSecurityException e)
        {
            throw new IOException(e);
        }

        CipherInputStream cis = new CipherInputStream(data, cipher);
        try
        {
            copy(cis, output);
        }
        catch(IOException exception)
        {
            // starting with java 8 the JVM wraps an IOException around a GeneralSecurityException
            // it should be safe to swallow a GeneralSecurityException
            if (!(exception.getCause() instanceof GeneralSecurityException))
            {
                throw exception;
            }
            Log3.debug(this, "A GeneralSecurityException occured when decrypting some stream data: "+exception);
        }
        finally
        {
            cis.close();
        }
    }

    private boolean prepareAESInitializationVector(boolean decrypt, byte[] iv, InputStream data, OutputStream output) throws IOException
    {
        if (decrypt)
        {
            // read IV from stream
            int ivSize = data.read(iv);
            if (ivSize == -1)
            {
                return false;
            }
            if (ivSize != iv.length)
            {
                throw new IOException(
                        "AES initialization vector not fully read: only "
                                + ivSize + " bytes read instead of " + iv.length);
            }
        }
        else
        {
            // generate random IV and write to stream
            SecureRandom rnd = new SecureRandom();
            rnd.nextBytes(iv);
            output.write(iv);
        }
        return true;
    }


    public int getKeyLength()
    {
        return keyLength;
    }

    public void setKeyLength(int keyLen)
    {
        this.keyLength = keyLen;
    }

//
//    public void setCurrentAccessPermission(AccessPermission currentAccessPermission)
//    {
//        this.currentAccessPermission = currentAccessPermission;
//    }
//
//
//    public AccessPermission getCurrentAccessPermission()
//    {
//        return currentAccessPermission;
//    }

    public boolean isAES()
    {
        return useAES;
    }


    public void setAES(boolean aesValue)
    {
        useAES = aesValue;
    }

    public abstract boolean hasProtectionPolicy();
    
    public static long copy(InputStream input, OutputStream output) throws IOException
    {
        byte[] buffer = new byte[4096];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer)))
        {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
