package sugarcube.formats.pdf.reader.pdf.encryption;

public class ArcFour
{
  private byte[] state = new byte[256];
  private int index1 = 0;
  private int index2 = 0;

  public void encrypt(byte[] input, int inputOffset, byte[] output, int outputOffset, int length)
  {
    pseudoRandomGenerationAlgorithm(input, inputOffset, output, outputOffset, length);
  }

  public void decrypt(byte[] input, int inputOffset, byte[] output, int outputOffset, int length)
  {
    encrypt(input, inputOffset, output, outputOffset, length);
  }

  /**
   * KSA (Key scheduling algorithm)
   */
  public void createEncryptionKey(byte[] key, int keyLength)
  {
    index1 = index2 = 0;
    for (int s = 0; s < 256; s++)
      state[s] = (byte) s;
    int value1, value2;
    for (int i1 = 0, i2 = 0; i1 < 256; i1++)
    {
      value1 = state[i1];
      i2 = (i2 + value1 + key[i1 % keyLength]) & 255;
      value2 = state[i2];
      state[i2] = (byte) (value1 & 255);
      state[i1] = (byte) (value2 & 255);
    }
  }

  /*public byte[] getCurrentEncryptionKey(){
   byte[] encryptionKey = new byte[state.length];
   for (int i = 0; i < state.length; i++)
   encryptionKey[i] = state[i];
   return encryptionKey;
   }
   
   public void setCurrentEncryptionKey(byte[] encryptionKey){
   for (int i = 0; i < encryptionKey.length; i++)
   state[i] = encryptionKey[i];
   }*/
  private void pseudoRandomGenerationAlgorithm(byte[] input, int inputOffset, byte[] output, int outputOffset, int length)
  {
    int value1, value2;
    int end = inputOffset + length;
    for (int i = inputOffset, j = outputOffset; i < end; i++, j++)
    {
      index1 = (index1 + 1) & 255;
      value1 = state[index1];
      index2 = (value1 + index2) & 255;
      value2 = state[index2];
      state[index1] = (byte) value2;
      state[index2] = (byte) value1;
      output[j] = (byte) ((input[i] ^ state[(state[index1] + state[index2]) & 255]) & 255);
    }
  }
}
