package com.unitap.unitap.Encryption;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/*The apache encryption library v1.10 must be added to the project path (Project Libraries)
it's stored in the /libs/ folder of the original project*/
public class AdvancedEncryptionStandard
{
    private String encryptionKey;

    /**
     * The key will be passed to the function once.  This avoids storing it in the main activity
     * @param encryptionKey some 128/192/256 bit key
     */
    public AdvancedEncryptionStandard(String encryptionKey)
    {
        this.encryptionKey = encryptionKey;
    }

    /**
     * Directly encrypt a string.  Easier to keep here since we are going to be encrypting a lot of strings
     * @param plainText Some text to encrypt with the stored key
     * @return An encrypted string
     */
    public String encrypt(String plainText)
    {
        byte[] plainBytes = encrypt(plainText.getBytes());
        if (plainBytes == null)
            return null;
        return new String(plainBytes);
    }

    /**
     * Plain byte array encrypting. Uses the stored key to encrypt a byte array
     * @param bArray input byteArray
     * @return an encrypted byte array
     */
    public byte[] encrypt(byte[] bArray)
    {
        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
            byte[] encryptedBytes = cipher.doFinal(bArray);

            return Base64.encodeBase64(encryptedBytes);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String encrypted)
    {
        byte[] plainBytes = decrypt(encrypted.getBytes());
        if (plainBytes == null)
            return null;
        return new String(plainBytes);
    }

    /**
     * Plain byte array decrypting. Uses the stored key to decrypt a byte array
     * @param bArray input byte array
     * @return a decrypted byte array
     */
    public byte[] decrypt(byte[] bArray)
    {
        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
            byte[] plainBytes = cipher.doFinal(Base64.decodeBase64(bArray));

            return plainBytes;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private Cipher getCipher(int cipherMode) throws Exception
    {
        String encryptionAlgorithm = "AES";
        SecretKeySpec keySpecification = new SecretKeySpec(
                encryptionKey.getBytes("UTF-8"), encryptionAlgorithm);
        Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
        cipher.init(cipherMode, keySpecification);

        return cipher;
    }
}