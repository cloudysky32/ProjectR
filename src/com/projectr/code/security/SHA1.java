package com.projectr.code.security;

import java.io.*;
import java.security.*;

public class SHA1 {
	public static byte[] getHash(byte[] input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			return md.digest(input);
		} catch (NoSuchAlgorithmException e) {
			// 일어날 경우가 없다고 보지만 만약을 위해 Exception 발생
			throw new RuntimeException("SHA1" + " Algorithm Not Found", e);
		}
	}
	
	public static String password(byte[] input)  {
		byte[] digest = null;
		
		digest = getHash(input);
		
		StringBuilder sb = new StringBuilder(1 + digest.length);
//		sb.append("*");
		sb.append(ByteUtils.toHexString(digest).toUpperCase());
		return sb.toString();
	}

	public static String password(String input) {
		if (input == null) {
			return null;
		}
		return password(input.getBytes());
	}

	public static String password(String input, String charsetName) throws UnsupportedEncodingException {
		if (input == null) {
			return null;
		}
		return password(input.getBytes(charsetName));
	}	
}
