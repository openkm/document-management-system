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

package com.openkm.extension.frontend.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.comunicator.TabDocumentComunicator;
import com.openkm.frontend.client.extension.event.HasDocumentEvent;
import com.openkm.frontend.client.extension.event.HasDocumentEvent.DocumentEventConstant;
import com.openkm.frontend.client.extension.event.HasLanguageEvent;
import com.openkm.frontend.client.extension.event.HasLanguageEvent.LanguageEventConstant;
import com.openkm.frontend.client.extension.event.handler.DocumentHandlerExtension;
import com.openkm.frontend.client.extension.event.handler.LanguageHandlerExtension;
import com.openkm.frontend.client.extension.widget.tabdocument.TabDocumentExtension;

/**
 * DocumentForum
 *
 * @author jllort
 *
 */
public class HelloWorld extends TabDocumentExtension implements DocumentHandlerExtension, LanguageHandlerExtension {
	Button refresh;
	VerticalPanel vPanel;

	public HelloWorld() {
		HTML html = new HTML("Hello Word");
		refresh = new Button("refresh UI");
		refresh.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GeneralComunicator.refreshUI();
			}
		});

		vPanel = new VerticalPanel();
		vPanel.add(html);
		vPanel.add(refresh);

		refresh.setStyleName("okm-Input");

		initWidget(vPanel);
	}

	@Override
	public String getTabText() {
		return "Hello tab";
	}

	@Override
	public void onChange(DocumentEventConstant event) {
		if (event.equals(HasDocumentEvent.DOCUMENT_CHANGED)) {
			Window.alert("document changed - " + event.getType());
		} else if (event.equals(HasDocumentEvent.KEYWORD_ADDED)) {
			Window.alert("keyword added - " + event.getType());
		} else if (event.equals(HasDocumentEvent.KEYWORD_REMOVED)) {
			Window.alert("keyword removed - " + event.getType());
		} else if (event.equals(HasDocumentEvent.CATEGORY_ADDED)) {
			Window.alert("category added - " + event.getType());
		} else if (event.equals(HasDocumentEvent.CATEGORY_REMOVED)) {
			Window.alert("category removed - " + event.getType());
		} else if (event.equals(HasDocumentEvent.TAB_CHANGED)) {
			Window.alert("tab changed - " + event.getType() + " - actual tab " + TabDocumentComunicator.getSelectedTab());
		} else if (event.equals(HasDocumentEvent.PANEL_RESIZED)) {
			Window.alert("panel resized - " + event.getType());
		} else if (event.equals(HasDocumentEvent.SECURITY_CHANGED)) {
			Window.alert("security changed - " + event.getType());
		} else if (event.equals(HasDocumentEvent.NOTE_ADDED)) {
			Window.alert("note added - " + event.getType());
		}
	}

	@Override
	public void onChange(LanguageEventConstant event) {
		if (event.equals(HasLanguageEvent.LANGUAGE_CHANGED)) {
			Window.alert("language changed");
		}
	}
}
