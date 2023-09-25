package com.igsl;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Logic of Confluence tiny URL generation:
 * https://confluence.atlassian.com/confkb/how-to-programmatically-generate-the-tiny-link-of-a-confluence-page-956713432.html
 * 
 * TL/DR version, to encode:
 * Write page ID integer as byte array (little endian).
 * Base-64 encode the array.
 * Replace characters: 
 * 		equal = to nothing
 * 		slash / to dash -
 * 		plus + to underscore _
 * 		newline \n to slash / (This should not happen but is in the document)
 * Remove all trailing A's.
 */
public class TinyURLGenerator {
	private static final String ENCODING = "ASCII";
	private static final String[] MATCHES = {
		"=",
		"/",
		"+"
	};
	private static final String[] REPLACEMENTS = {
		"",
		"-",
		"_"
	};
	
	private static void reverse(byte[] array) {
		if (array == null) {
			return;
		}
		int i = 0;
		int j = array.length - 1;
		byte tmp;
		while (j > i) {
			tmp = array[j];
			array[j] = array[i];
			array[i] = tmp;
			j--;
			i++;
		}
	}

	public static String pack(int pageId) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeInt(pageId);
		dos.flush();
		byte[] b = baos.toByteArray();
		reverse(b);
		String s = Base64.getEncoder().encodeToString(b);
		for (int i = 0; i < MATCHES.length; i++) {
			s = s.replaceAll(Pattern.quote(MATCHES[i]), REPLACEMENTS[i]);
		}
		s = s.replaceAll("A+$", "");
		return s;
	}

	public static int unpack(String tinyURL) throws IOException {
		for (int i = 1; i < REPLACEMENTS.length; i++) {	// Skip empty string
			tinyURL = tinyURL.replaceAll(Pattern.quote(REPLACEMENTS[i]), MATCHES[i]);
		}
		int mod = tinyURL.length() % 8;
		if (mod != 0) {
			// Add back trailing A's
			for (int i = 0; i < 8 - mod; i++) {
				tinyURL += "A";
			}
		}
		byte[] bytes = tinyURL.getBytes(ENCODING);
		byte[] decoded = Base64.getDecoder().decode(bytes);
		ByteBuffer buf = ByteBuffer.wrap(decoded);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		// Read as integer
		int i = buf.getInt();
		return i;
	}
	
//	public static void main(String[] args) throws Exception {
//		System.out.println(pack(98383));
//		System.out.println(unpack("T4AB"));
//	}
}
