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

package com.openkm.extension.frontend.client.widget.htmleditor;

import com.openkm.extension.frontend.client.widget.htmleditor.finddocument.FindDocumentSelectPopup;
import com.openkm.extension.frontend.client.widget.htmleditor.findfolder.FindFolderSelectPopup;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.extension.event.HasLanguageEvent;
import com.openkm.frontend.client.extension.event.HasLanguageEvent.LanguageEventConstant;
import com.openkm.frontend.client.extension.event.handler.LanguageHandlerExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * HTMLEditor
 *
 * @author jllort
 *
 */
public class HTMLEditor implements LanguageHandlerExtension {
	private static final String UUID = "87f250d5-526d-4d8a-96ee-1e5be7910bd8";
	private static HTMLEditor singleton;
	private static SubMenuHTMLEditor subMenuHTMLEditor;
	public HTMLEditorPopup hTMLEditorPopup;
	public CheckinPopup checkinPopup;
	public Status status;
	public FindDocumentSelectPopup findDocumentSelectPopup;
	public FindFolderSelectPopup findFolderSelectPopup;

	/**
	 * Editor
	 */
	public HTMLEditor(List<String> uuidList) {
		if (isRegistered(uuidList)) {
			singleton = this;
			subMenuHTMLEditor = new SubMenuHTMLEditor();
			hTMLEditorPopup = new HTMLEditorPopup();
			hTMLEditorPopup.setStyleName("okm-Popup");
			status = new Status();
			status.setStyleName("okm-StatusPopup");
			checkinPopup = new CheckinPopup();
			checkinPopup.setWidth("400px");
			checkinPopup.setStyleName("okm-Popup");
			findDocumentSelectPopup = new FindDocumentSelectPopup();
			findDocumentSelectPopup.setWidth("700px");
			findDocumentSelectPopup.setHeight("390px");
			findDocumentSelectPopup.setStyleName("okm-Popup");
			findDocumentSelectPopup.addStyleName("okm-DisableSelect");
			findFolderSelectPopup = new FindFolderSelectPopup();
			findFolderSelectPopup.setWidth("700px");
			findFolderSelectPopup.setHeight("390px");
			findFolderSelectPopup.setStyleName("okm-Popup");
			findFolderSelectPopup.addStyleName("okm-DisableSelect");
		}
	}

	/**
	 * getExtensions
	 */
	public List<Object> getExtensions() {
		List<Object> extensions = new ArrayList<Object>();
		extensions.add(singleton);
		extensions.add(subMenuHTMLEditor.getMenu());
		return extensions;
	}

	/**
	 * HTMLEditor
	 */
	public static HTMLEditor get() {
		return singleton;
	}

	/**
	 * enableAdvancedFilter
	 */
	public static void enableAdvancedFilter() {
		HTMLEditor.get().checkinPopup.enableAdvancedFilter();
	}

	/**
	 * enableNotifyExternalUsers
	 */
	public static void enableNotifyExternalUsers() {
		HTMLEditor.get().checkinPopup.enableNotifyExternalUsers();
	}

	/**
	 * isEnabled
	 */
	public static boolean isEnabled() {
		return subMenuHTMLEditor.isEnabled();
	}

	/**
	 * edit
	 */
	public void edit(GWTDocument doc) {
		hTMLEditorPopup.center();
		hTMLEditorPopup.edit(doc);
	}

	/**
	 *
	 */
	public String getTexteAreaText() {
		return hTMLEditorPopup.getTexteAreaText();
	}

	/**
	 * initJavaScriptApi
	 */
	public void initJavaScriptApi() {
		hTMLEditorPopup.initJavaScriptApi(hTMLEditorPopup);
		findDocumentSelectPopup.initJavaScriptApi(findDocumentSelectPopup);
		findFolderSelectPopup.initJavaScriptApi(findFolderSelectPopup);
	}

	/**
	 * isEditableDocument
	 */
	public static boolean isEditableDocument(GWTDocument doc) {
		return doc.getMimeType().equals("text/html");
	}

	/**
	 * setIncreaseVersion
	 */
	public static void setIncreaseVersion(int incrementVersion) {
		HTMLEditor.get().checkinPopup.setIncreaseVersion(incrementVersion);
	}

	@Override
	public void onChange(LanguageEventConstant event) {
		if (event.equals(HasLanguageEvent.LANGUAGE_CHANGED)) {
			subMenuHTMLEditor.langRefresh();
			findDocumentSelectPopup.langRefresh();
			findFolderSelectPopup.langRefresh();
		}
	}

	/**
	 * isRegistered
	 */
	public static boolean isRegistered(List<String> uuidList) {
		return uuidList.contains(UUID);
	}
}