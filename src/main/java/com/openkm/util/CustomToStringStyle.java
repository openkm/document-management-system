package com.openkm.util;

import org.apache.commons.lang3.builder.ToStringStyle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CustomToStringStyle extends ToStringStyle {
	public static final ToStringStyle SHORT_PREFIX_STYLE = new CustomToStringStyle();
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final long serialVersionUID = 1L;

	protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
		if (value instanceof Date) {
			value = new SimpleDateFormat(DATE_FORMAT).format(value);
		} else if (value instanceof Calendar) {
			value = new SimpleDateFormat(DATE_FORMAT).format(((Calendar) value).getTime());
		}

		buffer.append(value);
	}
}
