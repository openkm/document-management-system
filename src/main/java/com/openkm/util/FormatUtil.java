/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
 * <p>
 * No bytes were intentionally harmed during the development of this application.
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.util;

import com.openkm.bean.LogMessage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author pavila
 *
 */
public class FormatUtil {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(FormatUtil.class);
	private static final String[] UNITS = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB"};

	/**
	 * Detect if the current browser is a mobile one
	 */
	public static final boolean isMobile(HttpServletRequest request) {
		String userAgent = request.getHeader("user-agent").toLowerCase();
		return userAgent.contains("android") || userAgent.contains("iphone") || userAgent.contains("ipad")
				|| userAgent.contains("blackberry");
	}

	/**
	 * Format the document size for human readers
	 */
	public static String formatSize(long bytes) {
		for (int i = 6; i > 0; i--) {
			double step = Math.pow(1024, i);
			if (bytes > step)
				return String.format(Locale.ROOT, "%3.1f %s", bytes / step, UNITS[i]);
		}

		return Long.toString(bytes) + " " + UNITS[0];
	}

	/**
	 * Parse human-readable sizes
	 */
	public static long parseSize(String text) {
		double d = Double.parseDouble(text.replaceAll("[GMK]B$", ""));
		long l = Math.round(d * 1024 * 1024 * 1024L);

		switch (text.charAt(Math.max(0, text.length() - 2))) {
			default:
				l /= 1024;
			case 'K':
				l /= 1024;
			case 'M':
				l /= 1024;
			case 'G':
				return l;
		}
	}

	/**
	 * Format time for human readers
	 */
	public static String formatTime(long time) {
		DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
		String str = df.format(time);
		return str;
	}

	/**
	 * Format time interval for humans
	 */
	public static String formatSeconds(long time) {
		long hours, minutes, seconds;
		time = time / 1000;
		hours = time / 3600;
		time = time - (hours * 3600);
		minutes = time / 60;
		time = time - (minutes * 60);
		seconds = time;
		return (hours < 10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":"
				+ (seconds < 10 ? "0" + seconds : seconds);
	}

	/**
	 * Format time interval for humans
	 */
	public static String formatMiliSeconds(long time) {
		long hours, minutes, seconds, mseconds;
		mseconds = time % 1000;
		time = time / 1000;
		hours = time / 3600;
		time = time - (hours * 3600);
		minutes = time / 60;
		time = time - (minutes * 60);
		seconds = time;
		return (hours < 10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":"
				+ (seconds < 10 ? "0" + seconds : seconds) + "."
				+ (mseconds < 10 ? "00" + mseconds : (mseconds < 100 ? "0" + mseconds : mseconds));
	}

	/**
	 * Format calendar date
	 */
	public static String formatDate(Calendar cal) {
		return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(cal.getTime());
	}

	/**
	 * Format string array
	 */
	public static String formatArray(String[] values) {
		if (values != null) {
			if (values.length == 1) {
				return values[0];
			} else {
				return ArrayUtils.toString(values);
			}
		} else {
			return "NULL";
		}
	}

	/**
	 * Format object
	 */
	public static String formatObject(Object value) {
		if (value != null) {
			if (value instanceof Object[]) {
				return ArrayUtils.toString(value);
			} else {
				return value.toString();
			}
		} else {
			return "NULL";
		}
	}

	/**
	 * Escape html tags
	 */
	public static String escapeHtml(String str) {
		return str.replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>");
	}

	/**
	 * Split by separator
	 */
	public static String splitBySeparator(String str) {
		String ret = str.replace(File.pathSeparator, "<br/>");
		ret = ret.replace(",", "<br/>");
		return ret;
	}

	/**
	 * Sanitize HTML input
	 *
	 * @see http://www.rgagnon.com/javadetails/java-0627.html
	 */
	public static String sanitizeInput(String string) {
		return string
				.replaceAll("(?i)<script.*?>.*?</script.*?>", "") // case 1 - Open and close
				.replaceAll("(?i)<script.*?/>", "") // case 1 - Open / close
				.replaceAll("(?i)<script.*?>", "") // case 1 - Open and !close
				.replaceAll("(?i)<.*?javascript:.*?>.*?</.*?>", "") // case 2 - Open and close
				.replaceAll("(?i)<.*?javascript:.*?/>", "") // case 2 - Open / close
				.replaceAll("(?i)<.*?javascript:.*?>", "") // case 2 - Open and !close
				.replaceAll("(?i)<.*?\\s+on.*?>.*?</.*?>", "") // case 3 - Open and close
				.replaceAll("(?i)<.*?\\s+on.*?/>", "") // case 3 - Open / close
				.replaceAll("(?i)<.*?\\s+on.*?>", ""); // case 3 - Open and !close
	}

	/**
	 * Clean HTML input
	 *
	 * @see http://greatwebguy.com/programming/java/simple-cross-site-scripting-xss-servlet-filter/
	 */
	public static String cleanXSS(String value) {
		value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
		value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
		value = value.replaceAll("'", "& #39;");
		value = value.replaceAll("eval\\((.*)\\)", "");
		value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		value = value.replaceAll("script", "");
		return value;
	}

	/**
	 * Parser log file
	 */
	public static Collection<LogMessage> parseLog(File flog, int begin, int end, String str) throws IOException {
		// log.debug("parseLog({}, {}, {}, {})", new Object[] { flog, begin, end, str });
		ArrayList<LogMessage> al = new ArrayList<LogMessage>();
		int i = 0;

		if (begin < 0 || end < 0) {
			int maxLines = 0;

			for (LineIterator lit = FileUtils.lineIterator(flog); lit.hasNext(); ) {
				lit.nextLine();
				maxLines++;
			}

			if (begin < 0) {
				begin += maxLines;
			}

			if (end < 0) {
				end += maxLines + 1;
			}
		}

		for (LineIterator lit = FileUtils.lineIterator(flog); lit.hasNext(); ) {
			String line = lit.nextLine();
			int idx = (line != null) ? line.indexOf(str) : -1;
			i++;

			if (idx > -1 && i >= begin && i <= end) {
			    if (idx > -1 && !str.isEmpty()) {
					StringBuilder sb = new StringBuilder();
					sb.append(line.substring(0, idx));
					sb.append("<span class=\"highlight\" style=\"font-weight: bold\">");
					sb.append(line.substring(idx, idx + str.length()));
					sb.append("</span>");
					sb.append(line.substring(idx + str.length()));
                    line = sb.toString().replace(",", ", ");
				}

				LogMessage lm = new LogMessage();
				lm.setLine(i);
				lm.setMessage(line);
				al.add(lm);
			}
		}

		// log.debug("parseLog: {}", al);
		return al;
	}

	/**
	 * Check for valid UTF8
	 */
	public static boolean validUTF8(byte[] input) {
		CharsetDecoder cd = Charset.availableCharsets().get("UTF-8").newDecoder();

		try {
			cd.decode(ByteBuffer.wrap(input));
		} catch (CharacterCodingException e) {
			return false;
		}

		return true;
	}

	/**
	 * Fix UTF-8 NULL
	 */
	public static byte[] fixUTF8(byte[] input) {
		byte[] fixed = new byte[input.length];

		for (int i = 0; i < input.length; i++) {
			if (input[i] == 0x00) {
				fixed[i] = 0x20;
			} else {
				fixed[i] = input[i];
			}
		}

		return fixed;
	}

	/**
	 * Fix UTF-8 NULL
	 */
	public static String fixUTF8(String input) {
		return input.replace('\u0000', '\u0020');
	}

	/**
	 * Trim Unicode surrogate characters
	 *
	 * http://en.wikipedia.org/wiki/Mapping_of_Unicode_characters#Surrogates
	 */
	public static String trimUnicodeSurrogates(String text) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);

			if (!Character.isHighSurrogate(ch) && !Character.isLowSurrogate(ch)) {
				sb.append(ch);
			}
		}

		return sb.toString();
	}

	/**
	 * Check if the string is a valid UUID.
	 */
	public static boolean isValidUUID(String str) {
		try {
			UUID.fromString(str);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * This method ensures that the output String has only
	 * valid XML unicode characters as specified by the
	 * XML 1.0 standard. For reference, please see
	 * <a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the
	 * standard</a>. This method will return an empty
	 * String if the input is null or empty.
	 *
	 * @param in The String whose non-valid characters we want to remove.
	 * @return The in String, stripped of non-valid characters.
	 */
	public static String stripNonValidXMLCharacters(String in) {
		StringBuffer out = new StringBuffer(); // Used to hold the output.
		char current; // Used to reference the current character.

		if (in == null || "".equals(in)) return ""; // vacancy test.
		for (int i = 0; i < in.length(); i++) {
			current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
			if ((current == 0x9) ||
					(current == 0xA) ||
					(current == 0xD) ||
					((current >= 0x20) && (current <= 0xD7FF)) ||
					((current >= 0xE000) && (current <= 0xFFFD)) ||
					((current >= 0x10000) && (current <= 0x10FFFF)))
				out.append(current);
		}
		return out.toString();
	}
}
