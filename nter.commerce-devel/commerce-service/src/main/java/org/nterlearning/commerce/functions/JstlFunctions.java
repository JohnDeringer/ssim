package org.nterlearning.commerce.functions;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import org.apache.commons.lang.StringEscapeUtils;

public final class JstlFunctions {
	private JstlFunctions() {}

	public static String urlEncode(String value, String charset) throws UnsupportedEncodingException {
		return URLEncoder.encode(value, charset);
	}

	public static String escapeCsv(String value) {
		return StringEscapeUtils.escapeCsv(value);
		//String newVal = value.replace("\"", "\"\"");
		//return "\"" + newVal + "\"";
		//String newVal = value.replace(",", "\\,");
		//return newVal;
	}
}
