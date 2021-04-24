package rawbot.utils;

import java.security.Key;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.BadPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.ShortBufferException;

public class CipherUtil {

  public static byte[] oneTimeDecryptData(Key key, byte[] data) {
    try {
      Cipher cipher = Cipher.getInstance(key.getAlgorithm());
      cipher.init(2, key);
      return cipher.doFinal(data);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e.getMessage());
    } catch (InvalidKeyException e) {
      throw new RuntimeException(e.getMessage());
    } catch (IllegalBlockSizeException e) {
      throw new RuntimeException(e.getMessage());
    } catch (NoSuchPaddingException e) {
      throw new RuntimeException(e.getMessage());
    } catch (BadPaddingException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public static byte[] oneTimeEncryptData(Key key, byte[] data) {
    try {
      Cipher cipher = Cipher.getInstance(key.getAlgorithm());
      cipher.init(1, key);
      return cipher.doFinal(data);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e.getMessage());
    } catch (InvalidKeyException e) {
      throw new RuntimeException(e.getMessage());
    } catch (IllegalBlockSizeException e) {
      throw new RuntimeException(e.getMessage());
    } catch (NoSuchPaddingException e) {
      throw new RuntimeException(e.getMessage());
    } catch (BadPaddingException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private Cipher decode_cipher;
  private Cipher encode_cipher;

  public CipherUtil(Key key) {
    try {
      this.encode_cipher = Cipher.getInstance("AES/CFB8/NoPadding");
      this.encode_cipher.init(1, key, new IvParameterSpec(key.getEncoded()));
      this.decode_cipher = Cipher.getInstance("AES/CFB8/NoPadding");
      this.decode_cipher.init(2, key, new IvParameterSpec(key.getEncoded()));
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("NoSuchAlgorithmException " + e.getMessage());
    } catch (InvalidKeyException e) {
      throw new RuntimeException("InvalidKeyException " + e.getMessage());
    } catch (NoSuchPaddingException e) {
      throw new RuntimeException("NoSuchPaddingException " + e.getMessage());
    } catch (InvalidAlgorithmParameterException e) {
      throw new RuntimeException("InvalidAlgorithmParameterException " + e.getMessage());
    }
  }

  public void decipher(byte[] in, int in_offset, byte[] out, int out_offset, int length) {
    /*
    if(this.decode_cipher.getOutputSize(length) != length) {
      throw new RuntimeException("Decipher input is " + Integer.toString(length) + ", output is " + Integer.toString(this.decode_cipher.getOutputSize(length)));
    }
    */
    try {
      this.decode_cipher.update(in, in_offset, length, out, out_offset);
    } catch (ShortBufferException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public byte[] cipher(byte[] in) {
    return this.encode_cipher.update(in);
  }
}
