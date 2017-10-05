package com.dhm.common.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 实现3des加解密
 * @author EX-DUANHONGMEI001
 *
 */
public class DESEncrptionUtil
{
	private static Log logger=LogFactory.getLog(DESEncrptionUtil.class);
	
	//秘钥库的秘钥别名
	private static final String keyAlias = "DESede.encryptKey";
	//private String keyType="DESede";
	
	//秘钥库类型
	private static final String keyStoreType="jceks";
	//秘钥库加密方式
	private static final String algorithm="DESede/ECB/PKCS5Padding";
	

	/***
	 * 读取秘钥库 3DES秘钥信息
	 * @param keyStorePassword 秘钥库密码
	 * @param keyStoreFileName 秘钥库文件名
	 * @return 秘钥对象
	 * @throws Exception
	 */
	private static SecretKey loadSecretKeyFromKeyStore(String keyStorePassword,String keyStoreFileName) throws Exception {
		InputStream is = null;
		SecretKey secretKey = null;
		try {
			is = DESEncrptionUtil.class.getClassLoader().getResourceAsStream(keyStoreFileName);
			KeyStore jceks = KeyStore.getInstance(keyStoreType);
			jceks.load(is, keyStorePassword.toCharArray());
			KeyStore.SecretKeyEntry encryptKey = (KeyStore.SecretKeyEntry) jceks
					.getEntry(keyAlias, new KeyStore.PasswordProtection(keyStorePassword.toCharArray()));
			secretKey = encryptKey.getSecretKey();
		} catch (Exception e) {
			if (is != null) {
				try {
					is.close();
				} catch (Exception ex) {
				}
			}
			throw e;
		}
		return secretKey;
	}
	
	/***
	 * 3DES解密封装方法，传递密文参数 返回明文内容，秘钥等配置在系统配置文件中读取
	 * @param data 密文
	 * @return 解密后的明文
	 * @throws Exception
	 */
	public static String decrypt(String data, Charset encoding,String keyStorePassword,String keyStoreFileName) throws Exception
	{
		
		Cipher cipher = Cipher.getInstance(algorithm);
		SecretKey secretKey = loadSecretKeyFromKeyStore(keyStorePassword,keyStoreFileName);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] base64Bytes = Base64Util.decode(data.getBytes(encoding));
		byte[] decryptBytes = cipher.doFinal(base64Bytes);
		return new String(decryptBytes, encoding);
	}

	public static String decrypt(String data,String sCharset,String keyStorePassword,String keyStoreFileName) throws Exception
	{
		return decrypt(data, Charset.forName(sCharset),keyStorePassword,keyStoreFileName);
	}
	
	/***
	 * 3DES加密封装方法，传递明文参数 返回密文内容，秘钥等配置在系统配置文件中读取
	 * @param data
	 * @return
	 * @throws Exception
	 */

	public final static String encrypt(String data, Charset encoding,String keyStorePassword,String keyStoreFileName) throws Exception
	{		
		Cipher cipher = Cipher.getInstance(algorithm);
		SecretKey secretKey = loadSecretKeyFromKeyStore(keyStorePassword,keyStoreFileName);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encrptBytes = cipher.doFinal(data.getBytes(encoding));
		byte[] base64Bytes = Base64Util.encode(encrptBytes);
		return new String(base64Bytes, encoding);
	}

	public final static String encryptByStatic(String data,String sCharset,String keyStorePassword,String keyStoreFileName) throws Exception
	{
		return encrypt(data, Charset.forName(sCharset),keyStorePassword,keyStoreFileName);
	}
	
	public final static String encrypt(String data,String sCharset,String keyStorePassword,String keyStoreFileName) throws Exception
	{
		return encrypt(data, Charset.forName(sCharset),keyStorePassword,keyStoreFileName);
	}
	
	/****
	   * 3des加密方法，不依赖文件的加密方式
	   * 
	    * @author LIJIAN042
	   * @throws NoSuchPaddingException
	   * @throws NoSuchAlgorithmException
	   * @throws InvalidKeyException
	   * @throws BadPaddingException
	   * @throws IllegalBlockSizeException
	   * @throws UnsupportedEncodingException
	   */

	public final static String encryptForUndependFile(String passWord,
			String srcData) throws Exception {
		logger.info("encryptForUndependFile, --加密开始--");
		byte[] binaryData = passWord.getBytes(Charset.forName("UTF-8"));
		byte[] data = Base64.decodeBase64(binaryData);
		Cipher cipher = Cipher.getInstance(algorithm);
		SecretKey secretKey = new SecretKeySpec(data, "DESede");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encrptBytes = cipher.doFinal(srcData.getBytes(Charset
				.forName("UTF-8")));
		byte[] base64Bytes = Base64Util.encode(encrptBytes);
		logger.info("encryptForUndependFile, --加密结束--");
		return new String(base64Bytes, Charset.forName("UTF-8"));
	}

	   /****
		   * 3des加密方法，不依赖文件的加密方式
		   * 
		   * @author LIJIAN042
		   * @throws NoSuchPaddingException
		   * @throws NoSuchAlgorithmException
		   * @throws InvalidKeyException
		   * @throws BadPaddingException
		   * @throws IllegalBlockSizeException
		   * @throws UnsupportedEncodingException
		   */

	public final static String decryptForUndependFile(String passWord,
		         String srcData) throws Exception {
		logger.info("decryptForUndependFile, --解密开始--");
		byte[] binaryData = passWord.getBytes(Charset.forName("UTF-8"));
		byte[] data = Base64.decodeBase64(binaryData);
		Cipher cipher = Cipher.getInstance(algorithm);
		SecretKey secretKey = new SecretKeySpec(data, "DESede");
		byte[] base64Bytes = Base64Util.decode(srcData.getBytes(Charset
            .forName("UTF-8")));
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decrptBytes = cipher.doFinal(base64Bytes);
		logger.info("decryptForUndependFile, --解密结束--");
		return new String(decrptBytes, Charset.forName("UTF-8"));
	}
}