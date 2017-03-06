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

import com.openkm.extension.frontend.client.widget.activitylog.ActivityLog;
import com.openkm.extension.frontend.client.widget.extraworkspace.ExtraTabWorkspace;
import com.openkm.extension.frontend.client.widget.forum.Forum;
import com.openkm.extension.frontend.client.widget.htmleditor.HTMLEditor;
import com.openkm.extension.frontend.client.widget.macros.Macros;
import com.openkm.extension.frontend.client.widget.messaging.MessagingToolBarBox;
import com.openkm.extension.frontend.client.widget.stapling.Stapling;
import com.openkm.extension.frontend.client.widget.toolbar.downloadButton.DownloadButton;
import com.openkm.extension.frontend.client.widget.toolbar.downloadPdfButton.DownloadPdfButton;
import com.openkm.extension.frontend.client.widget.wiki.Wiki;
import com.openkm.extension.frontend.client.widget.workflow.Workflow;
import com.openkm.extension.frontend.client.widget.zoho.Zoho;

import java.util.ArrayList;
import java.util.List;

/**
 * Customization
 *
 * @author jllort
 *
 */
public class Customization {

	/**
	 * getExtensionWidgets
	 */
	public static List<Object> getExtensionWidgets(List<String> uuidList) {
		List<Object> extensions = new ArrayList<Object>();

		// add here your widget extensions
		if (uuidList.contains("d9dab640-d098-11df-bd3b-0800200c9a66")) {
			extensions.add(new HelloWorld());
		}

		if (uuidList.contains("9f84b330-d096-11df-bd3b-0800200c9a66")) {
			extensions.add(new ToolBarButtonExample().getButton());
		}

		if (uuidList.contains("d95e01a0-d097-11df-bd3b-0800200c9a66")) {
			extensions.add(new TabFolderExample());
		}

		if (uuidList.contains("44f94470-d097-11df-bd3b-0800200c9a66")) {
			extensions.add(new TabWorkspaceExample());
		}

		if (uuidList.contains("4d245f30-ef47-11df-98cf-0800200c9a66")) {
			extensions.add(new ToolBarBoxExample().getToolBarBox());
		}

		// extensions.add(new MainMenuExample().getNewMenu());
		// extensions.add(new HandlersTest());

		// OPENKM PROPIETARY EXTENSIONS
		if (DownloadButton.isRegistered(uuidList)) {
			extensions.add(new DownloadButton(uuidList).getButton());
		}

		if (DownloadPdfButton.isRegistered(uuidList)) {
			extensions.add(new DownloadPdfButton(uuidList).getButton());
		}

		if (Stapling.isRegistered(uuidList)) {
			extensions.addAll(new Stapling(uuidList).getExtensions());
		}

		if (MessagingToolBarBox.isRegistered(uuidList)) {
			extensions.addAll(new MessagingToolBarBox(uuidList).getExtensions());
		}

		if (ActivityLog.isRegistered(uuidList)) {
			extensions.addAll(new ActivityLog(uuidList).getExtensions());
		}

		if (Zoho.isRegistered(uuidList)) {
			extensions.addAll(new Zoho(uuidList).getExtensions());
		}

		if (Forum.isRegistered(uuidList)) {
			extensions.addAll(new Forum(uuidList).getExtensions());
		}

		if (Wiki.isRegistered(uuidList)) {
			Wiki wiki = new Wiki(uuidList);
			wiki.initJavaScriptApi(wiki);
			extensions.addAll(wiki.getExtensions());
		}

		if (Workflow.isRegistered(uuidList)) {
			extensions.addAll(new Workflow(uuidList).getExtensions());
		}

		if (Macros.isRegistered(uuidList)) {
			extensions.addAll(new Macros(uuidList).getExtensions());
		}

		if (HTMLEditor.isRegistered(uuidList)) {
			HTMLEditor htmlEditor = new HTMLEditor(uuidList);
			extensions.addAll(htmlEditor.getExtensions());
			htmlEditor.initJavaScriptApi();
		}

		if (ExtraTabWorkspace.isRegistered(uuidList)) {
			extensions.addAll(new ExtraTabWorkspace(uuidList).getExtensions());
		}

		return extensions;
	}
}
