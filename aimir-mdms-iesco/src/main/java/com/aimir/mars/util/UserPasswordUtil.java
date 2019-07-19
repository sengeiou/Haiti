package com.aimir.mars.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.util.Base64Utils;

public class UserPasswordUtil {
	private static String defaultPassword = "nURWfEf3/9uL";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
        try { 
        	String originalText = "aimiramm";
        	String en = encrypt( originalText );
        	String de = decrypt( en );
        	
        	System.out.println( "Original Text is " + originalText);
        	System.out.println( "Encrypted Text is " + en );
        	System.out.println( "Decrypted Text is " + de );
        	
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
	}
	    
	public static String decrypt(String text) throws Exception
	{
		return decrypt(text, defaultPassword);
	}
	
    public static String decrypt(String text, String key) throws Exception
    {
    	Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    	byte[] keyBytes= new byte[16];
    	byte[] b= key.getBytes("UTF-8");
    	int len= b.length;
    	if (len > keyBytes.length) len = keyBytes.length;
    	System.arraycopy(b, 0, keyBytes, 0, len);
    	SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
    	IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
    	cipher.init(Cipher.DECRYPT_MODE,keySpec,ivSpec);


//    	BASE64Decoder decoder = new BASE64Decoder();
//    	byte [] results = cipher.doFinal(decoder.decodeBuffer(text));
    	byte [] results = cipher.doFinal(Base64Utils.decodeFromString(text));
    	return new String(results,"UTF-8");
    }

    public static String encrypt(String text) throws Exception
    {
    	return encrypt(text, defaultPassword);
    }
    
    public static String encrypt(String text, String key) throws Exception
    {
    	Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    	byte[] keyBytes= new byte[16];
    	byte[] b= key.getBytes("UTF-8");
    	int len= b.length;
    	if (len > keyBytes.length) len = keyBytes.length;
    	System.arraycopy(b, 0, keyBytes, 0, len);
    	SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
    	IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
    	cipher.init(Cipher.ENCRYPT_MODE,keySpec,ivSpec);
    	 

    	byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
//    	BASE64Encoder encoder = new BASE64Encoder();
//    	return encoder.encode(results);
    	return Base64Utils.encodeToString(results);
    }
}
