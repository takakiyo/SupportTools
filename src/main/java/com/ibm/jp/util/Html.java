/*
 * Html.java
 * 
 * 作成日: 2005/02/16
 * Created by: T.Takakiyo
 * $Id: Html.java,v 1.1 2005/02/16 04:45:57 takakiyo Exp $
 */
package com.ibm.jp.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Html
 * 
 * Created: 2005/02/16
 * @author T.Takakiyo
 */
public class Html {
	/**
	 * Html コンストラクター・コメント。
	 */
	private Html() {
	}
	
	/**
	 * <p>HTMLに表示すると危険な文字を実体参照に置き換える。
	 * 変換されるのは以下の文字。</p>
	 * 
	 * <table>
	 * <tr><th>変換前</th><th>変換後</th></tr>
	 * <tr><td>&lt;</td><td>&amp;lt;</td></tr>
	 * <tr><td>&gt;</td><td>&amp;gt;</td></tr>
	 * <tr><td>&amp;</td><td>&amp;amp;</td></tr>
	 * <tr><td>&quot;</td><td>&amp;quot;</td></tr>
	 * </table>
	 * 
	 * 作成日 : (2001/04/29 1:20:58)
	 * @return java.lang.String
	 * @param str java.lang.String
	 */
	public static String escapeChar(final String str) {
		if (str == null || str.equals("")) return str;
		int len = str.length();
		for (int i = 0; i < str.length(); i++) {
			switch (str.charAt(i)) {
				case '<' : // to "&lt;"
					len += 3;
					break;
				case '>' : // to "&gt;"
					len += 3;
					break;
				case '&' : // to "&amp;"
					len += 4;
					break;
				case '"' : // to "&quot;"
					len += 5;
					break;
			}
		}
		if (len == str.length()) return str;
		char ret[] = new char[len];
		int j = 0;
		for (int i = 0; i < str.length(); i++) {
			switch (str.charAt(i)) {
				case '<' : // to "&lt;"
					ret[j++] = '&';
					ret[j++] = 'l';
					ret[j++] = 't';
					ret[j++] = ';';
					break;
				case '>' : // to "&gt;"
					ret[j++] = '&';
					ret[j++] = 'g';
					ret[j++] = 't';
					ret[j++] = ';';
					break;
				case '&' : // to "&amp;"
					ret[j++] = '&';
					ret[j++] = 'a';
					ret[j++] = 'm';
					ret[j++] = 'p';
					ret[j++] = ';';
					break;
				case '"' : // to "&quot;"
					ret[j++] = '&';
					ret[j++] = 'q';
					ret[j++] = 'u';
					ret[j++] = 'o';
					ret[j++] = 't';
					ret[j++] = ';';
					break;
				default :
					ret[j++] = str.charAt(i);
					break;
			}
		}
		return new String(ret);
	}
	
	/**
	 * <p>Exceptionのスタックトレースを，HTMLで表示して問題ないようにエスケープする。</p>
	 * 
	 * 作成日 : (2001/04/29 1:25:07)
	 * @return java.lang.String
	 * @param exception java.lang.Exception
	 */
	public static String escapeStackTrace(Exception exception) {
		StringWriter buf = new StringWriter();
		PrintWriter out = new PrintWriter(buf);
		exception.printStackTrace(out);
		out.flush();
		out.close();
		return escapeChar(buf.toString());
	}
	
	/**
	 * <p>escapeChar()とは逆に，エスケープされた実体参照を元に戻す。</p>
	 * 
	 * @param s
	 * @return
	 */
	public static String unescapeChar(String s) {
		char[] buf = s.toCharArray();
		int i = 0, j = 0;
		while (i < buf.length) {
			if (buf[i] == '&') {
				if (checkbuf(buf, i, "&lt;")) {
					buf[j++] = '<';
					i += 4;
					continue;
				}
				if (checkbuf(buf, i, "&gt;")) {
					buf[j++] = '>';
					i += 4;
					continue;
				}
				if (checkbuf(buf, i, "&amp;")) {
					buf[j++] = '&';
					i += 5;
					continue;
				}
				if (checkbuf(buf, i, "&quot;")) {
					buf[j++] = '"';
					i += 6;
					continue;
				}
			}
			buf[j++] = buf[i++];
		}
		return (i == j)? s : new String(buf, 0, j);
	}
	
	private static boolean checkbuf(char[] buf, int off, String str) {
		if (off+str.length() > buf.length) return false;
		for (int i = 0; i < str.length(); i++) {
			if (buf[off+i] != str.charAt(i)) return false;
		}
		return true;
	}
	
	/**
	 * <p>MSIEが送ってくるUTF-8のパラメーターをデコードする。
	 * 「&#20170;&#24180;&#12398;」のような文字列を「今年の」にデコードする。</p>
	 * 
	 * 作成日 : (2002/01/24 14:25:30)
	 * @return java.lang.String
	 * @param str java.lang.String
	 */
	public static String decodeUTF8(String str) {
		if (str == null)
			return null;
		if (str.equals(""))
			return "";

		char[] buf = new char[8];
		int bufc = 0;
		char[] ret = new char[str.length()];
		int retc = 0;

		// &#[0-9]+; の正規表現の処理                 '0'-'9'
		//                                             +---+
		//                                             |   |
		//         '&'           '#'         '0'-'9'   V  /  ';'
		// [ 0 ] ------> [ 1 ] ------> [ 2 ] ------> [ 3 ] -------> Match!
		int state = 0;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			switch (state) {
				case 0 :
					if (c == '&') {
						state = 1;
					} else {
						ret[retc++] = c;
					}
					break;
				case 1 :
					if (c == '#') {
						state = 2;
					} else {
						ret[retc++] = '&';
						ret[retc++] = c;
						state = 0;
					}
					break;
				case 2 :
					if (Character.isDigit(c)) {
						bufc = 0;
						buf[bufc++] = c;
						state = 3;
					} else {
						ret[retc++] = '&';
						ret[retc++] = '#';
						ret[retc++] = c;
						state = 0;
					}
					break;
				case 3 :
					if (Character.isDigit(c) && bufc < 5) {
						buf[bufc++] = c;
					} else if (c == ';') {
						int n = Integer.parseInt(new String(buf, 0, bufc));
						ret[retc++] = (char) n;
						state = 0;
					} else {
						ret[retc++] = '&';
						ret[retc++] = '#';
						for (int j = 0; j < bufc; j++)
							ret[retc++] = buf[j];
						ret[retc++] = c;
						state = 0;
					}
					break;
			}
		}
		switch (state) {
			case 0 :
				break;
			case 1 :
				ret[retc++] = '&';
				break;
			case 2 :
				ret[retc++] = '&';
				ret[retc++] = '#';
				break;
			case 3 :
				ret[retc++] = '&';
				ret[retc++] = '#';
				for (int j = 0; j < bufc; j++)
					ret[retc++] = buf[j];
				break;
		}
		return new String(ret, 0, retc);
	}
}
