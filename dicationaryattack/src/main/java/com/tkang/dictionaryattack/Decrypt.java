package com.tkang.dictionaryattack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class Decrypt {
	
	final static String cipher = "764aa26b55a4da654df6b19e4bce00f4ed05e09346fb0e762583cb7da2ac93a2";
	final static String iv = "aabbccddeeff00998877665544332211";
	final static String plainText = "secret";
	
	public static void main(String[] args) {
		HashMap<String, String> potentialMatches = new HashMap<String, String>();
		try {
			byte[] cipherText = new Hex()
					.decode(cipher.getBytes());
			IvParameterSpec ivSpec = new IvParameterSpec(new Hex().decode(iv.getBytes()));

			File file = new File("dictionary.txt");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String readLine = "";
			for (int i = 0;(readLine = reader.readLine()) != null; i++) {
				System.out.print("Processing Word #" + i + "\r");
				if (readLine.length() < 16) {
					while (readLine.length() < 16) {
						readLine = readLine + "#";
					}
					SecretKeySpec key = new SecretKeySpec(readLine.getBytes("UTF-8"), "AES");
					Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
					cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
					String original = "";
					try {
						original = new String((cipher.doFinal(cipherText)));
					} catch (Exception e) {
						//Ignore incorrectly formatted passwords
					}
					if (original.toLowerCase().contains(plainText.toLowerCase())) {
						potentialMatches.put(readLine, original);
					}
				}
			}
			reader.close();

		} catch (DecoderException e1) {
			System.out.println("\n Invalid Cipher Text");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n --- Results ---");
		for (Entry<String, String> e : potentialMatches.entrySet()) {
			System.out.println(e.getKey() + " | " + e.getValue());
		}
	}
}
