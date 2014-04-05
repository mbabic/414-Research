package test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import Project.Encryption;
import Project.IOUtils;
import Project.Settings;

import org.junit.*;

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
	@Test
	public void testEncryptDecryptFile() {
		Encryption e = new Encryption(null);
		String testFile = Settings.OUT + "testFile";
		try {
			// Get file bytes so we can compare results at byte level
			byte[] inBytes = IOUtils.fileToBytes(testFile);
			
			e.encryptFile(testFile, "testFileEncrypted");
			
			e.decryptFile(Settings.OUT + "testFileEncrypted", "testFileDecrypted");
			
			byte[] outBytes = Project.IOUtils.fileToBytes(Settings.OUT + "testFileDecrypted");
			
			assert(outBytes.length == inBytes.length);
			for (int i = 0; i < outBytes.length; i++) {
				assert(outBytes[i] == inBytes[i]);
			}
			// Also manually examine file to ensure they are the same.
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}
		
	}

}
