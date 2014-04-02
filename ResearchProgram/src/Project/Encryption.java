package Project;

import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Encryption module.  Performs AES symmetric key 
 * @author Marko Babic, Marcus Karpoff
 */
public class Encryption {

	byte[] _key;
	
	byte[] _iv;
	
	AlgorithmParameters _params;
	
	Cipher _cipher;
	
	SecretKeySpec _keySpec;
	
	/**
	 * @param password
	 * 		The password from which the encryption key will be derived.
	 */
	public Encryption(String password) {
		MessageDigest sha;
		String salt;
		
		if (password == null) {
			password = Settings.DEFAULT_PASSWORD;
		}
		
		// TODO: select better salt
		salt = "salt!";
		
		try {
			_key = (salt + password).getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			_key = sha.digest(_key);
			_key = Arrays.copyOf(_key, 32);
			_keySpec = new SecretKeySpec(_key, "AES");
			_cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			_params = _cipher.getParameters();
			_iv = _params.getParameterSpec(IvParameterSpec.class).getIV();
		} catch (UnsupportedEncodingException uee) {
			System.err.println(uee.toString());
			System.exit(1);
		} catch (NoSuchAlgorithmException nsae) {
			System.err.println(nsae.toString());
			System.exit(1);			
		} catch (NoSuchPaddingException nspe) {
			System.err.println(nspe.toString());
			System.exit(1);			
		} catch (InvalidParameterSpecException ipse) {
			System.err.println(ipse.toString());
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
		} catch(Exception e) {
			System.err.println(e.toString());
			System.exit(1);	// Exit.  Should not fail on encryption.
		}
		// Not reached.
		return null;
	}
	
	/**
	 * This will take an encrypted video stream and return an video stream
	 */
	public byte[] decrypt(byte[] in) {
		
		
		return null;
	}
}
