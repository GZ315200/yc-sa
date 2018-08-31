package com.unistack.tamboo.commons.utils.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * 数据加密工具 
 *
 */
public class DESUtil {

	/** 
     * DES key
     */  
    private static  String KEY = "tamboo";
    private static  String ALGORITHM = "DES";
    
    /**
     * encrypt data with des and base64
     * @param data
     * @return
     */
	@SuppressWarnings("restriction")
    public static String encrypt(String data) {  
    	return new sun.misc.BASE64Encoder().encode(des(data.getBytes(), Cipher.ENCRYPT_MODE));
    }  

    /**
     * decrypt encrypted data
     * @param data
     * @return
     */
	@SuppressWarnings("restriction")
    public static String decrypt(String data) {
		try {
			byte[] bs = new sun.misc.BASE64Decoder().decodeBuffer(data);
			return new String(des(bs, Cipher.DECRYPT_MODE));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }  
    
    private static byte[] des(byte[] bs, int cipherMode){
    	try {  
    		byte[] key = Arrays.copyOf(KEY.getBytes(), 8);
    		DESKeySpec desKeySpec = new DESKeySpec(key);  

    		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);  
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);  
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);  
            cipher.init(cipherMode, secretKey, new SecureRandom()); 
            return cipher.doFinal(bs);
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }
    }

    public static void main(String[] args) {

//        System.out.println(decrypt("rDLFepZfTkQprVGuXmQuIQ=="));
        System.out.println(decrypt("Jl/SZ8s4UbfBbbRxB8lbvg=="));
//    	if(args.length > 0){
//    		if("-encrypt".equals(args[0])){
//           	 System.out.println("encrypted data: " + encrypt(args[1]));
//           }else if("-decrypt".enquals(args[0])){
//           	System.out.println("decrypted data: " + decrypt(args[1]));
//           }
//    	}else{
//    		System.out.println("usage: -encrypt data or -decrypt data");
//        	System.out.println("encrypt sample: -encrypt test");
//        	System.out.println("decrypt sample: -decrypt nSQdx4jgrj4=");
//        }
    }  
}
