package com.example.demo.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

/*
 * Used to generate keys
 */
public class KeyGeneratorUtility {

	
	public static KeyPair generateKeyPairs() {
		
		KeyPair keyPair;
		
		try {
			
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.genKeyPair();
			
		}catch (Exception e) {
			throw new IllegalStateException();
		}
		
		return keyPair;
	}
}
