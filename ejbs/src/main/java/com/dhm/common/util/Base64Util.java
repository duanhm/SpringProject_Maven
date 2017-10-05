package com.dhm.common.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;

public class Base64Util
{
	public static String encode(String data) throws UnsupportedEncodingException
	{
		return encode(data, Charset.forName("UTF-8"));
	}

	public static String encode(String data, Charset encoding) throws UnsupportedEncodingException
	{
		byte[] binaryData = data.getBytes(encoding);
		byte[] base64 = encode(binaryData);
		return new String(base64, encoding);
	}

	public static byte[] encode(byte[] data) throws UnsupportedEncodingException
	{
		return Base64.encodeBase64(data, true);
	}


	public static String decode(String base64)
	{
		return decode(base64, Charset.forName("UTF-8"));
	}

	public static String decode(String base64, Charset encoding)
	{
		byte[] binaryData = base64.getBytes(encoding);
		byte[] data = decode(binaryData);
		return new String(data, encoding);
	}
	public static byte[] decode(byte[] base64)
	{
		return Base64.decodeBase64(base64);
	}
}
