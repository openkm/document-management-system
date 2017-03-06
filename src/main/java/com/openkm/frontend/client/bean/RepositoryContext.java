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

package com.openkm.frontend.client.bean;


/**
 * RepositoryContext
 *
 * @author jllort
 *
 */
public class RepositoryContext {

	private String contextTaxonomy = "";
	private String contextPersonal = "";
	private String contextTemplates = "";
	private String contextMail = "";
	private String contextTrash = "";

	public RepositoryContext() {
	}

	public String getContextTaxonomy() {
		return contextTaxonomy;
	}

	public void setContextTaxonomy(String contextTaxonomy) {
		this.contextTaxonomy = contextTaxonomy;
	}

	public String getContextPersonal() {
		return contextPersonal;
	}

	public void setContextPersonal(String contextPersonal) {
		this.contextPersonal = contextPersonal;
	}

	public String getContextTemplates() {
		return contextTemplates;
	}

	public void setContextTemplates(String contextTemplates) {
		this.contextTemplates = contextTemplates;
	}

	public String getContextMail() {
		return contextMail;
	}

	public void setContextMail(String contextMail) {
		this.contextMail = contextMail;
	}

	public String getContextTrash() {
		return contextTrash;
	}

	public void setContextTrash(String contextTrash) {
		this.contextTrash = contextTrash;
	}
}