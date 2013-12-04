package com.projectr.code.security;

import java.security.*;
import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.spec.*;

public class AES {
	private static Key generateKey(String algorithm) throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
		SecretKey key = keyGenerator.generateKey();
		return key;
	}
	
	private static Key generateKey(String algorithm, byte[] keyData) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
		String upper = algorithm;
		if ("DES".equals(upper)) {
			KeySpec keySpec = new DESKeySpec(keyData);
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
			SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
			return secretKey;
		} else if ("DESede".equals(upper) || "TripleDES".equals(upper)) {
			KeySpec keySpec = new DESedeKeySpec(keyData);
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
			SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
			return secretKey;
		} else {
			SecretKeySpec keySpec = new SecretKeySpec(keyData, algorithm);
			return keySpec;
		}
	}

	public static byte[] encryptAES(String str) throws Exception {
		Key key = generateKey("AES", ByteUtils.toBytes("63617463686d656966796f7563616e21", 16));

		String transformation = "AES/ECB/PKCS5Padding";
		Cipher cipher = Cipher.getInstance(transformation);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		
		byte[] plain = str.getBytes();
		byte[] encrypt = cipher.doFinal(plain);
		
		return encrypt;
	}

	public static byte[] decryptAES(byte[] encrypt) throws Exception {
		Key key = generateKey("AES", ByteUtils.toBytes("63617463686d656966796f7563616e21", 16));

		String transformation = "AES/ECB/PKCS5Padding";
		Cipher cipher = Cipher.getInstance(transformation);
		
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decrypt = cipher.doFinal(encrypt);
		
		return decrypt;
	}
}
