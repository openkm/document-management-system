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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.openkm.extension.frontend.client.service.OKMGeneralService;
import com.openkm.extension.frontend.client.service.OKMGeneralServiceAsync;
import com.openkm.extension.frontend.client.widget.activitylog.ActivityLog;
import com.openkm.extension.frontend.client.widget.externaltab.ExternalTabDocument;
import com.openkm.extension.frontend.client.widget.externaltab.ExternalTabFolder;
import com.openkm.extension.frontend.client.widget.externaltab.ExternalTabMail;
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
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Customization
 *
 * @author jllort
 */
public class Customization {

	/**
	 * getExtensionWidgets
	 */
	public static List<Object> getExtensionWidgets(List<String> uuidList) {
		List<Object> extensions = new ArrayList<>();

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

		// OPENKM PROPRIETARY EXTENSIONS
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

		return extensions;
	}

	/**
	 * getDynamicExtensionWidgets
	 */
	public static Map<String, String> getDynamicExtensionWidgets(List<String> uuidList) {
		Map<String, String> dynamicExtensionsMap = new HashMap<String, String>();
		if (ExternalTabDocument.isRegistered(uuidList)) {
			dynamicExtensionsMap.put(ExternalTabDocument.getSimpleName(), ExternalTabDocument.KEY);
		}
		if (ExternalTabFolder.isRegistered(uuidList)) {
			dynamicExtensionsMap.put(ExternalTabFolder.getSimpleName(), ExternalTabFolder.KEY);
		}
		if (ExternalTabMail.isRegistered(uuidList)) {
			dynamicExtensionsMap.put(ExternalTabMail.getSimpleName(), ExternalTabMail.KEY);
		}
		if (ExtraTabWorkspace.isRegistered(uuidList)) {
			dynamicExtensionsMap.put(ExtraTabWorkspace.getSimpleName(), ExtraTabWorkspace.KEY);
		}
		return dynamicExtensionsMap;
	}

	/**
	 * loadDynamicExtensionWidgets
	 */
	public static void loadDynamicExtensionWidgets() {
		OKMGeneralServiceAsync generalService = GWT.create(OKMGeneralService.class);
		final String className = Main.get().startUp.getDynamicExtensionsMap().keySet().iterator().next();
		String key = Main.get().startUp.getDynamicExtensionsMap().get(className);

		if (ExternalTabDocument.getSimpleName().equals(className)) {
			generalService.getConfigParam(key, new AsyncCallback<List<String>>() {
				@Override
				public void onSuccess(List<String> result) {
					for (String param : result) {
						final String[] options = param.split(";");
						if (options.length == 2) {
							ExternalTabDocument etDocument = new ExternalTabDocument(Main.get().getExtensionUuidList());
							etDocument.setName(options[0]);
							etDocument.setUrl(options[1]);
							Main.get().startUp.addExtension(etDocument);
						} else {
							Main.get().showError("Invalid external tab document option: " + param);
						}
					}

					Main.get().startUp.getDynamicExtensionsMap().remove(className);
					if (Main.get().startUp.getDynamicExtensionsMap().size() > 0) {
						loadDynamicExtensionWidgets();
					} else {
						Main.get().startUp.startExtensions();
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					GeneralComunicator.showError("getConfigParam", caught);
					Main.get().startUp.startExtensions();
				}
			});
		} else if (ExternalTabFolder.getSimpleName().equals(className)) {
			generalService.getConfigParam(key, new AsyncCallback<List<String>>() {
				@Override
				public void onSuccess(List<String> result) {
					for (String param : result) {
						final String[] options = param.split(";");
						if (options.length == 2) {
							ExternalTabFolder etFolder = new ExternalTabFolder(Main.get().getExtensionUuidList());
							etFolder.setName(options[0]);
							etFolder.setUrl(options[1]);
							Main.get().startUp.addExtension(etFolder);
						} else {
							Main.get().showError("Invalid external tab folder option: " + param);
						}
					}

					Main.get().startUp.getDynamicExtensionsMap().remove(className);
					if (Main.get().startUp.getDynamicExtensionsMap().size() > 0) {
						loadDynamicExtensionWidgets();
					} else {
						Main.get().startUp.startExtensions();
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					GeneralComunicator.showError("getConfigParam", caught);
					Main.get().startUp.startExtensions();
				}
			});
		} else if (ExternalTabMail.getSimpleName().equals(className)) {
			generalService.getConfigParam(key, new AsyncCallback<List<String>>() {
				@Override
				public void onSuccess(List<String> result) {
					for (String param : result) {
						final String[] options = param.split(";");
						if (options.length == 2) {
							ExternalTabMail etMail = new ExternalTabMail(Main.get().getExtensionUuidList());
							etMail.setName(options[0]);
							etMail.setUrl(options[1]);
							Main.get().startUp.addExtension(etMail);
						} else {
							Main.get().showError("Invalid external tab mail option: " + param);
						}
					}

					Main.get().startUp.getDynamicExtensionsMap().remove(className);
					if (Main.get().startUp.getDynamicExtensionsMap().size() > 0) {
						loadDynamicExtensionWidgets();
					} else {
						Main.get().startUp.startExtensions();
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					GeneralComunicator.showError("getConfigParam", caught);
					Main.get().startUp.startExtensions();
				}
			});
		} else if (ExtraTabWorkspace.getSimpleName().equals(className)) {
			generalService.getConfigParam(key, new AsyncCallback<List<String>>() {
				@Override
				public void onSuccess(List<String> result) {
					for (String param : result) {
						final String[] options = param.split(";");
						if (options.length == 2) {
							ExtraTabWorkspace etWorkspace = new ExtraTabWorkspace(Main.get().getExtensionUuidList());
							etWorkspace.getTabWorkspace().setTextLabel(options[0]);
							etWorkspace.getTabWorkspace().setUrl(options[1]);
							Main.get().startUp.addExtension(etWorkspace.getTabWorkspace());
						} else {
							Main.get().showError("Invalid external tab workspace option: " + param);
						}
					}

					Main.get().startUp.getDynamicExtensionsMap().remove(className);
					if (Main.get().startUp.getDynamicExtensionsMap().size() > 0) {
						loadDynamicExtensionWidgets();
					} else {
						Main.get().startUp.startExtensions();
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					GeneralComunicator.showError("getConfigParam", caught);
					Main.get().startUp.startExtensions();
				}
			});
		}
	}
}
