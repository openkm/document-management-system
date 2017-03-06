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

package com.openkm.util.tags;

import com.openkm.dao.bean.AutomationMetadata;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

@SuppressWarnings("serial")
public class AutomationFormElementTag extends TagSupport {
	private String type;
	private String source;
	private String value;
	private String name;
	private boolean readonly;

	@Override
	public int doStartTag() {
		String html = "";

		if (type.equals(AutomationMetadata.TYPE_TEXT)) {
			if (source != null) {
				if (source.equals("")) {
					html += "<input class=\":required :only_on_blur\" size=\"40\" type=\"text\" name=\"" + name + "\" id=\""
							+ name + "\" value=\"" + ((value == null) ? "" : value) + "\" "
							+ ((readonly) ? "readonly=\"readonly\"" : "") + ">";
				} else if (source.equals(AutomationMetadata.SOURCE_FOLDER)) {
					// @formatter:off
					html += "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">"
							+ "<tr><td>"
							+ "<input class=\":required :only_on_blur\" size=\"40\" type=\"text\" name=\"" + name
							+ "\" id=\"" + name + "\" value=\"" + ((value == null) ? "" : value) + "\" "
							+ "readonly=\"readonly\">"
							+ "</td><td>&nbsp;"
							+ "<a class=\"ds\" href=\"../extension/DataBrowser?action=repo&sel=fld&dst=" + name
							+ "\"><img src=\"img/action/browse_repo.png\"/></a>"
							+ "</td></tr>"
							+ "</table>";
					// @formatter:on
				}
			}
		} else if (type.equals(AutomationMetadata.TYPE_INTEGER)) {
			html += "<input class=\":integer :required :only_on_blur\" size=\"40\" type=\"text\" name=\"" + name + "\" id=\""
					+ name + "\" value=\"" + ((value == null) ? "" : value) + "\" "
					+ ((readonly) ? "readonly=\"readonly\"" : "") + ">";
		} else if (type.equals(AutomationMetadata.TYPE_BOOLEAN)) {
			if (readonly) {
				html += "<input type=\"hidden\" name=\"" + name + "\" id=\"" + name + "\">"
						+ ((value == null) ? "" : value);
			} else {
				html += "<select name=\"" + name + "\" id=\"" + name + "\">";

				if (value != null) {
					if (Boolean.valueOf(value).booleanValue()) {
						html += "<option value=\"" + String.valueOf(Boolean.FALSE) + "\">false</option>";
						html += "<option value=\"" + String.valueOf(Boolean.TRUE) + "\" selected=\"selected\">true</option>";
					} else {
						html += "<option value=\"" + String.valueOf(Boolean.FALSE) + "\" selected=\"selected\">false</option>";
						html += "<option value=\"" + String.valueOf(Boolean.TRUE) + "\">true</option>";
					}
				} else {
					html += "<option value=\"" + String.valueOf(Boolean.FALSE) + "\">false</option>";
					html += "<option value=\"" + String.valueOf(Boolean.TRUE) + "\">true</option>";
				}

				html += "</select>";
			}
		} else if (type.equals(AutomationMetadata.TYPE_TEXTAREA)) {
			// @formatter:off
			// Table
			html += "<table class=\"form\">"
					+ "<tr><td>";
			// Text are
			html += "<textarea cols=\"80\" rows=\"25\" name=\"" + name + "\" id=\"" + name + "\">" + ((value != null) ? value : "") + "</textarea>";
			// Js and css resources
			html += "<link rel=\"stylesheet\" type=\"text/css\" href=\"js/codemirror/lib/codemirror.css\" />\n"
					+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"js/codemirror/mode/clike/clike.css\" />\n"
					+ "<style type=\"text/css\">"
					+ ".CodeMirror { width: 600px; height: 300px; background-color: #f8f6c2; }\n"
					+ ".activeline { background: #f0fcff !important; }\n"
					+ "</style>\n"
					+ "<script type=\"text/javascript\" src=\"js/codemirror/lib/codemirror.js\"></script>\n"
					+ "<script type=\"text/javascript\" src=\"js/codemirror/mode/clike/clike.js\"></script>\n";
			// Initilizing codemirror
			html += "<script type=\"text/javascript\">\n"
					+ "cm = CodeMirror.fromTextArea(document.getElementById('" + name + "'), {\n"
					+ "lineNumbers: true,\n"
					+ "matchBrackets: true,\n"
					+ "indentUnit: 4,\n"
					+ "mode: \"text/x-java\",\n"
					+ "onCursorActivity: function() {\n"
					+ "cm.setLineClass(hlLine, null);\n"
					+ "hlLine = cm.setLineClass(cm.getCursor().line, \"activeline\");\n"
					+ "}\n"
					+ "});\n"
					+ "\n"
					+ "hlLine = cm.setLineClass(0, \"activeline\");\n"
					+ "</script>\n";
			// closing table
			html += "</td></tr>"
					+ "</table>";
			// @formatter:on
		}

		try {
			pageContext.getOut().write(html);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return Tag.SKIP_BODY;
	}

	@Override
	public void release() {
		super.release();
		type = null;
		source = null;
		value = null;
		name = null;
		readonly = false;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setreadonly(boolean readonly) {
		this.readonly = readonly;
	}
}
