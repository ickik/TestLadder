package com.ickik.ladder.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.ickik.ladder.exception.LadderException;

/**
 * Abstract class that contains encryption and decryption common methods.
 * @author Patrick Allgeyer (patrick.allgeyer@alcatel-lucent.com)
 * @version 0.1.000, 08/03/2010
 */
public final class TestLadderEncoding {

	private static final String ALGORITHM = "Blowfish";
	private static final String TRANSFORMATION = "Blowfish/CBC/PKCS5Padding";
	private static final byte[] iv = { (byte) 0xc9, (byte) 0x36, (byte) 0x78, (byte) 0x99,
			  (byte) 0x52, (byte) 0x3e, (byte) 0xea, (byte) 0xf2 };
	
	
	/**
	 * Encrypts the password given by argument with the login as key. This
	 * method is only used to encrypt the password for the .properties files
	 * stored in local.
	 * <br>The algorithm chooses is blowfish.
	 * @param login the login of the user used as key.
	 * @param password the password associates to the login.
	 * @return the password encrypted with blowfish algorithm.
	 * @throws LadderException this exception is thrown when an error occurs
	 * during the execution of this method. The problem could be that the
	 * algorithm was not found or the password or/and the key is/are null.
	 */
	public static final String encryptionPassword(String login, String password) throws LadderException {
		IvParameterSpec salt = new IvParameterSpec(iv);
		SecretKey skeySpec = new SecretKeySpec(login.getBytes(), ALGORITHM);
		try {
			Cipher c = Cipher.getInstance(TRANSFORMATION);
			c.init(Cipher.ENCRYPT_MODE, skeySpec, salt);
			byte[] buffer = c.doFinal(password.getBytes());
			return new String(buffer);
		} catch (NoSuchAlgorithmException e) {
			throw new LadderException("");
		} catch (NoSuchPaddingException e) {
			throw new LadderException("");
		} catch (InvalidKeyException e) {
			throw new LadderException("");
		} catch (InvalidAlgorithmParameterException e) {
			throw new LadderException("");
		} catch (IllegalBlockSizeException e) {
			throw new LadderException("");
		} catch (BadPaddingException e) {
			throw new LadderException("");
		}
	}

	/**
	 * Decrypts the password given by argument with the login as key. This
	 * method is only used to decrypt the password for the .properties files
	 * stored in local.
	 * <br>The algorithm chooses is blowfish.
	 * @param login the login of the user used as key.
	 * @param password the password associates to the login.
	 * @return the password decrypted with blowfish algorithm.
	 * @throws LadderException this exception is thrown when an error occurs
	 * during the execution of this method. The problem could be that the
	 * algorithm was not found or the password or/and the key is/are null.
	 */
	public static final String decryptionPassword(String login, String password) throws LadderException {
		IvParameterSpec salt = new IvParameterSpec(iv);
		SecretKey skeySpec = new SecretKeySpec(login.getBytes(), ALGORITHM);
		try {
			Cipher c = Cipher.getInstance(TRANSFORMATION);
			c.init(Cipher.DECRYPT_MODE, skeySpec, salt);
			byte[] buffer = c.doFinal(password.getBytes());
			return new String(buffer);
		} catch (NoSuchAlgorithmException e) {
			throw new LadderException("decryption.algorithm.invalid");
		} catch (NoSuchPaddingException e) {
			throw new LadderException("decryption.padding.invalid");
		} catch (InvalidKeyException e) {
			throw new LadderException("decryption.key.invalid");
		} catch (InvalidAlgorithmParameterException e) {
			throw new LadderException("decryption.algorithm.parameter.invalid");
		} catch (IllegalBlockSizeException e) {
			throw new LadderException("decryption.blocksize.illegal");
		} catch (BadPaddingException e) {
			e.printStackTrace();
			//throw new LadderException("decryption.padding.bad");
		}
		return password;
	}
	
	/**
	 * Encrypts the password with MD5 algorithm. This method is used
	 * the encrypt password to insert or check the users in database.
	 * @param password the password to encrypt with MD5 algorithm.
	 * @return the encrypted password under String format.
	 * @throws LadderException
	 */
	public static String encryptionPassword(String password) throws LadderException {
		byte[] uniqueKey = password.getBytes();
		StringBuilder hashString = new StringBuilder();
		try {
			byte[] hash = MessageDigest.getInstance("MD5").digest(uniqueKey);
			int len = hash.length;
			for (int i = 0; i < len; i++) {
				String hex = Integer.toHexString(hash[i]);
				if (hex.length() == 1) {
					hashString.append('0'); 
					hashString.append(hex.charAt(hex.length() - 1));
				} else {
					hashString.append(hex.substring(hex.length() - 2));
				}
			}
		} catch (NoSuchAlgorithmException e) {
			throw new LadderException("", e);
		}
		return hashString.toString();
	}

}
