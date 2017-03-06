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

package com.openkm.extension.frontend.client.widget.zoho;

import com.openkm.frontend.client.extension.comunicator.TabDocumentComunicator;
import com.openkm.frontend.client.extension.comunicator.TabFolderComunicator;
import com.openkm.frontend.client.extension.comunicator.TabMailComunicator;
import com.openkm.frontend.client.extension.event.HasDocumentEvent;
import com.openkm.frontend.client.extension.event.HasDocumentEvent.DocumentEventConstant;
import com.openkm.frontend.client.extension.event.HasFolderEvent;
import com.openkm.frontend.client.extension.event.HasFolderEvent.FolderEventConstant;
import com.openkm.frontend.client.extension.event.HasLanguageEvent;
import com.openkm.frontend.client.extension.event.HasLanguageEvent.LanguageEventConstant;
import com.openkm.frontend.client.extension.event.HasMailEvent;
import com.openkm.frontend.client.extension.event.HasMailEvent.MailEventConstant;
import com.openkm.frontend.client.extension.event.handler.DocumentHandlerExtension;
import com.openkm.frontend.client.extension.event.handler.FolderHandlerExtension;
import com.openkm.frontend.client.extension.event.handler.LanguageHandlerExtension;
import com.openkm.frontend.client.extension.event.handler.MailHandlerExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * Zoho
 *
 * @author jllort
 */
public class Zoho implements DocumentHandlerExtension, FolderHandlerExtension, MailHandlerExtension, LanguageHandlerExtension {
	public static final int TAB_DOCUMENT = 0;
	public static final int TAB_FOLDER = 1;
	public static final int TAB_MAIL = 2;

	public static Zoho singleton;
	private static final String UUID = "bb3feb20-570b-11e0-b8af-0800200c9a66";

	private SubMenuZoho subMenuZoho;
	private int selectedPanel = TAB_DOCUMENT;

	/**
	 * Zoho
	 */
	public Zoho(List<String> uuidList) {
		if (isRegistered(uuidList)) {
			singleton = this;
			subMenuZoho = new SubMenuZoho();
		}
	}

	/**
	 * getExtensions
	 */
	public List<Object> getExtensions() {
		List<Object> extensions = new ArrayList<Object>();
		extensions.add(singleton);
		extensions.add(subMenuZoho.getMenu());
		return extensions;
	}

	/**
	 * get
	 */
	public static Zoho get() {
		return singleton;
	}

	/**
	 * getUuid
	 */
	public String getUuid() {
		switch (selectedPanel) {
			case TAB_DOCUMENT:
				return TabDocumentComunicator.getDocument().getUuid();

			case TAB_FOLDER:
				return TabFolderComunicator.getFolder().getUuid();

			case TAB_MAIL:
				return TabMailComunicator.getMail().getUuid();

			default:
				return null;
		}
	}

	@Override
	public void onChange(DocumentEventConstant event) {
		if (event.equals(HasDocumentEvent.DOCUMENT_CHANGED)) {
			selectedPanel = TAB_DOCUMENT;
			subMenuZoho.evaluateMenus();
		}
	}

	@Override
	public void onChange(FolderEventConstant event) {
		if (event.equals(HasFolderEvent.FOLDER_CHANGED)) {
			selectedPanel = TAB_FOLDER;
			subMenuZoho.disableAllMenus();
		}
	}

	@Override
	public void onChange(MailEventConstant event) {
		if (event.equals(HasMailEvent.MAIL_CHANGED)) {
			selectedPanel = TAB_MAIL;
			subMenuZoho.disableAllMenus();
		}
	}

	@Override
	public void onChange(LanguageEventConstant event) {
		if (event.equals(HasLanguageEvent.LANGUAGE_CHANGED)) {
			subMenuZoho.langRefresh();
		}
	}

	/**
	 * isRegistered
	 */
	public static boolean isRegistered(List<String> uuidList) {
		return uuidList.contains(UUID);
	}
}