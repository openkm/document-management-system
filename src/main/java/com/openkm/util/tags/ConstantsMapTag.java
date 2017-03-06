/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017  Paco Avila & Josep Llort
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.util.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ConstantsMapTag extends SimpleTagSupport {
	private String className;
	private String varName;
	//private String scopeName;

	public void setClassName(String value) {
		this.className = value;
	}

	public void setVar(String value) {
		this.varName = value;
	}
	//public void setScope( String value ) { this.scopeName = value; }

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void doTag() throws JspException {
		try {
			Map constantsMap = new HashMap();
			Class declaringClass = Class.forName(this.className);
			Field[] fields = declaringClass.getFields();

			for (int n = 0; n < fields.length; n++) {
				if (Modifier.isPublic(fields[n].getModifiers()) &&
						Modifier.isStatic(fields[n].getModifiers()) /*&&
	                Modifier.isFinal( fields[n].getModifiers() )*/) {
					constantsMap.put(fields[n].getName(), fields[n].get(null));
				}
			}

			//ScopedContext scopedContext = (this.scopeName == null) ?
			//    ScopedContext.PAGE : ScopedContext.getInstance( this.scopeName );
			//getJspContext().setAttribute(this.varName, constantsMap, scopedContext.getValue());
			getJspContext().setAttribute(this.varName, constantsMap);
		} catch (Exception e) {
			throw new JspException("Exception setting constants map for " + this.className, e);
		}
	}
}
