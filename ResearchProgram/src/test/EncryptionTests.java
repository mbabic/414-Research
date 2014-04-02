package test;

import static org.junit.Assert.*;

import org.junit.Test;

import Project.Encryption;


public class EncryptionTests {

	@Test
	public void testEncryptDecrypt() {
		String plainText = "Please encrypt me.";
		Encryption encrypter = new Encryption(null);
		try {
			byte[] out = encrypter.encrypt(plainText.getBytes("UTF-8"));
			String ret = new String(encrypter.decrypt(out));
			assert(ret.equals(plainText));
		} catch (Exception e) {
			fail("Exception thrown: " + e.toString());
		}
		
	}
	
	@Test
	public void testEncryptDecryptByteArray() {
		String plaintText = "032uopijr 230r23023r psjf 203rj 2-3r0u 2";
		Encryption encrypter = new Encryption(null);
		try {
			byte[] out = encrypter.encrypt(Encryption.toByteArray(plaintText));
			String ret = new String(encrypter.decrypt(out));
			assert(ret.equals(plaintText));
		} catch (Exception e) {
			fail("Exception thrown: " + e.toString());
		}
	}

}
