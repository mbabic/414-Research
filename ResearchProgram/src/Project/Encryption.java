package Project;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Encryption module. Performs AES symmetric key
 * 
 * @author Marko Babic, Marcus Karpoff
 */
public class Encryption {

	byte[] _key;

	byte[] _iv;

	AlgorithmParameters _params;

	Cipher _cipher;

	IvParameterSpec _ivSpec;

	SecretKeySpec _keySpec;

	private static byte[] iv() {
		byte[] iv = new byte[16];
		Random random = new Random();
		random.nextBytes(iv);
		return iv;
	}

	/**
	 * @param password
	 *            The password from which the encryption key will be derived.
	 */
	public Encryption(String password) {
		MessageDigest sha;
		String salt;

		if (password == null) {
			password = Settings.PASSWORD;
		}

		// TODO: select better salt
		salt = "salt!";

		try {
			_key = (salt + password).getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			_key = sha.digest(_key);
			_key = Arrays.copyOf(_key, 16);
			_keySpec = new SecretKeySpec(_key, "AES");
			_cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			_iv = iv();
			_ivSpec = new IvParameterSpec(_iv);
		} catch (UnsupportedEncodingException uee) {
			System.err.println(uee.toString());
			System.exit(1);
		} catch (NoSuchAlgorithmException nsae) {
			System.err.println(nsae.toString());
			System.exit(1);
		} catch (NoSuchPaddingException nspe) {
			System.err.println(nspe.toString());
			System.exit(1);
		}
	}

	/**
	 * Encrypt the file whose path is specified by the provided argument.
	 * 
	 * @param in
	 *            The path (relative or absolute) to the file to be encrypted.
	 * @param out
	 *            The name of the output file to be generated in the
	 *            Settings.OUT folder.
	 */
	public void encryptFile(String in, String out) {
		byte[] bytes, encrypted;
		try {
			bytes = IOUtils.fileToBytes(in);

			encrypted = encrypt(bytes);

			IOUtils.bytesToFile(encrypted, out);

		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Decrypt the file whose path is specified by the provided argument.
	 * 
	 * @param in
	 *            The path (relative or absolute) to the file to be encrypted.
	 * @param out
	 *            The name of the output file to be generated in the
	 *            Settings.OUT folder.
	 */
	public void decryptFile(String in, String out) {
		byte[] encrypted, decrypted;
		try {
			encrypted = IOUtils.fileToBytes(in);
			decrypted = decrypt(encrypted);
			IOUtils.bytesToFile(decrypted, out);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Encrypt the given input key using
	 */
	public byte[] encrypt(byte[] in) {
		try {
			_cipher.init(Cipher.ENCRYPT_MODE, _keySpec);
			return _cipher.doFinal(in);
		} catch (Exception e) {
			System.err.println(e.toString());
			System.exit(1); // Exit. Should not fail on encryption.
		}
		// Not reached.
		return null;
	}

	/**
	 * This will take an encrypted video stream and return an video stream
	 */
	public byte[] decrypt(byte[] in) {
		try {
			_cipher.init(Cipher.DECRYPT_MODE, _keySpec);
			return _cipher.doFinal(in);
		} catch (InvalidKeyException ike) {
			// Bad key, do not decrypt.
			return null;
		} catch (Exception e) {
			System.err.println(e.toString());
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	/**
	 * Convert the given java.Object to a series of bytes (in preperation for
	 * encryption/decryption).
	 * 
	 * @param obj
	 *            The object to be written to a byte array.
	 * @return Byte representation of the object passed.
	 */
	public static byte[] toByteArray(Object obj) {
		try {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bytes);
			oos.writeObject(obj);
			return bytes.toByteArray();
		} catch (IOException ioe) {
			return null;
		}
	}
}
