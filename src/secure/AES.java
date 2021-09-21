package secure;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AES {

	public static byte[] encrypt(int n, SecretKey secretKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		return encrypt(ByteBuffer.allocate(4).putInt(n).array(), secretKey.toBytes());
	}
	public static byte[] encrypt(byte[] text, SecretKey secretKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		return encrypt(text, secretKey.toBytes());
	}
	public static byte[] encrypt(SecretKey text, SecretKey secretKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		return encrypt(text.toBytes(), secretKey.toBytes());
	}

	public static byte[] encrypt(String text, SecretKey secretKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		return encrypt(text.getBytes(), secretKey.toBytes());
	}

	public static byte[] encrypt(String text, byte[] secretKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		return encrypt(text.getBytes(), secretKey);
	}

	public static byte[] encrypt(byte[] text, byte[] secretKey) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException  {
		Key key = new SecretKeySpec(secretKey, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(text);
	}

	public static byte[] decrypt(byte[] text, SecretKey secretKey) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		System.out.println(secretKey);
		return decrypt(text, secretKey.toBytes());
	}
	public static byte[] decrypt(byte[] text, byte[] secretKey) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
	  	 Key key = new SecretKeySpec(secretKey, "AES");
		 Cipher decipher = Cipher.getInstance("AES");
		 decipher.init(Cipher.DECRYPT_MODE, key);
    	 return decipher.doFinal(text);
	}
}
