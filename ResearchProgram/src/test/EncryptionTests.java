package test;

import static org.junit.Assert.*;

import org.junit.Test;

import Project.Encryption;


public class EncryptionTests {

	@Test
	public void testEncryptDescrypt() {
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

}
