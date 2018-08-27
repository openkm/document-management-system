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

package com.openkm.frontend.client.widget.properties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.bean.GWTPropertyGroup;
import com.openkm.frontend.client.extension.event.HasMailEvent;
import com.openkm.frontend.client.extension.event.handler.MailHandlerExtension;
import com.openkm.frontend.client.extension.event.handler.PropertyGroupHandlerExtension;
import com.openkm.frontend.client.extension.event.hashandler.HasMailHandlerExtension;
import com.openkm.frontend.client.extension.event.hashandler.HasPropertyGroupHandlerExtension;
import com.openkm.frontend.client.extension.widget.tabmail.TabMailExtension;
import com.openkm.frontend.client.service.OKMPropertyGroupService;
import com.openkm.frontend.client.service.OKMPropertyGroupServiceAsync;
import com.openkm.frontend.client.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The tab mail
 *
 * @author jllort
 *
 */
public class TabMail extends Composite implements HasMailEvent, HasMailHandlerExtension, HasPropertyGroupHandlerExtension {
	private final OKMPropertyGroupServiceAsync propertyGroupService = GWT.create(OKMPropertyGroupService.class);

	private static final int TAB_HEIGHT = 20;
	private int SECURITY_TAB = -1;

	public TabLayoutPanel tabPanel;
	public Mail mail;
	public Notes notes;
	public MailViewer mailViewer;
	public SecurityScrollTable security;
	private VerticalPanel panel;
	private List<PropertyGroup> propertyGroup;
	private List<TabMailExtension> widgetExtensionList;
	private List<MailHandlerExtension> mailHandlerExtensionList;
	private List<PropertyGroupHandlerExtension> propertyGroupHandlerExtensionList;
	private boolean visibleButton = true; // Sets visibleButtons enabled to default view 
	private int selectedTab = 0; // Used to determine selected tab to maintain on change document, because not all documents
	// have the same numeber of tabs ( document group properties are variable )
	private int latestSelectedTab = 0;
	private boolean propertiesVisible = false;
	private boolean notesVisible = false;
	private boolean previewVisible = false;
	private boolean securityVisible = false;
	private int height = 0;
	private int width = 0;
	private boolean propertyGroupsVisible = false;
	private int IEBugCorrections = 0;

	/**
	 * The Document tab
	 */
	public TabMail() {
		widgetExtensionList = new ArrayList<TabMailExtension>();
		mailHandlerExtensionList = new ArrayList<MailHandlerExtension>();
		propertyGroupHandlerExtensionList = new ArrayList<PropertyGroupHandlerExtension>();
		tabPanel = new TabLayoutPanel(TAB_HEIGHT, Unit.PX);
		mail = new Mail();
		notes = new Notes(Notes.MAIL_NOTE);
		mailViewer = new MailViewer();
		security = new SecurityScrollTable();
		panel = new VerticalPanel();
		propertyGroup = new ArrayList<PropertyGroup>();

		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				int tabIndex = event.getSelectedItem().intValue();
				Main.get().mainPanel.topPanel.toolBar.evaluateRemovePropertyGroup(isRemovePropertyGroupEnabled(tabIndex));
				selectedTab = tabIndex;

				if (tabIndex == SECURITY_TAB) {
					Timer timer = new Timer() {
						@Override
						public void run() {
							security.fillWidth(); // Always when shows fires fill width
						}
					};
					timer.schedule(500); // Fill width must be done after really it'll be visible
				}

				fireEvent(HasMailEvent.TAB_CHANGED);
			}
		});

		panel.add(tabPanel);
		tabPanel.setWidth("100%");
		mail.setSize("100%", "100%");
		notes.setSize("100%", "100%");
		mailViewer.setSize("100%", "100%");
		panel.setSize("100%", "100%");
		tabPanel.setStyleName("okm-DisableSelect");

		initWidget(panel);
	}

	/**
	 * Sets the size
	 *
	 * @param width With of the widget
	 * @param height Height of the widget
	 */
	public void setPixelSize(int width, int height) {
		this.height = height;
		this.width = width;
		tabPanel.setPixelSize(width, height);
		mail.setPixelSize(width, height - TAB_HEIGHT);
		notes.setPixelSize(width, height - TAB_HEIGHT);
		mailViewer.setPixelSize(width, height - TAB_HEIGHT);
		security.setPixelSize(width, height - TAB_HEIGHT);
		security.fillWidth();

		// Setting size to extension
		for (Iterator<TabMailExtension> it = widgetExtensionList.iterator(); it.hasNext(); ) {
			it.next().setPixelSize(width, height - TAB_HEIGHT);
		}

		if (!propertyGroup.isEmpty()) { // Sets size to propety groups	
			for (Iterator<PropertyGroup> it = propertyGroup.iterator(); it.hasNext(); ) {
				PropertyGroup group = it.next();
				group.setPixelSize(width, height - TAB_HEIGHT);
			}
		}

		fireEvent(HasMailEvent.PANEL_RESIZED);
	}

	/**
	 * Sets document values
	 *
	 * @param doc The document object
	 */
	public void setProperties(GWTMail gWTMail) {
		mail.set(gWTMail);
		notes.set(gWTMail);       // Used by TabDocumentCommunicator
		mailViewer.set(gWTMail);

		selectedTab = tabPanel.getSelectedIndex();    // Sets the actual selected Tab
		latestSelectedTab = selectedTab;            // stores latest selected tab

		if (securityVisible) {
			security.setUuid(gWTMail.getUuid());
			security.GetGrants();

			GWTFolder parentFolder = Main.get().activeFolderTree.getFolder();
			if ((parentFolder.getPermissions() & GWTPermission.SECURITY) == GWTPermission.SECURITY &&
					(gWTMail.getPermissions() & GWTPermission.SECURITY) == GWTPermission.SECURITY) {
				security.setChangePermision(true);
			} else {
				security.setChangePermision(false);
			}
		}

		if (propertyGroupsVisible) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.setGroupProperties();
		}

		if (!propertyGroup.isEmpty()) {
			for (Iterator<PropertyGroup> it = propertyGroup.iterator(); it.hasNext(); ) {
				tabPanel.remove(it.next());
			}
			propertyGroup.clear();
		}

		// Only gets groups if really are visible
		if (propertyGroupsVisible) {
			getGroups(mail.get().getPath()); // Gets all the property group assigned to a document
			// Here evalutates selectedTab
		}

		// Setting folder object to extensions
		for (Iterator<TabMailExtension> it = widgetExtensionList.iterator(); it.hasNext(); ) {
			it.next().set(gWTMail);
		}

		// TODO:Solves minor bug with IE ( now shows contents )
		if (Util.getUserAgent().startsWith("ie") && IEBugCorrections == 1) {
			Timer timer = new Timer() {
				@Override
				public void run() {
					correctIEDefect();
				}
			};

			timer.schedule(500);
		}

		fireEvent(HasMailEvent.MAIL_CHANGED);
	}

	/**
	 * correctIEDefect
	 */
	public void correctIEDefect() {
		IEBugCorrections++;

		if (tabPanel.getWidgetCount() > 1) {
			tabPanel.selectTab(1);
			tabPanel.selectTab(0);
		} else if (tabPanel.getWidgetCount() > 1) {
			tabPanel.selectTab(0);
		}
	}

	/**
	 * Refresh security values
	 */
	public void securityRefresh() {
		fireEvent(HasMailEvent.SECURITY_CHANGED);
		Main.get().mainPanel.desktop.browser.fileBrowser.securityRefresh();
	}

	/**
	 * isWidgetExtensionVisible
	 */
	public boolean isWidgetExtensionVisible(Widget widget) {
		if (tabPanel.getWidget(selectedTab).equals(widget)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * getSelectedTab
	 */
	public int getSelectedTab() {
		return selectedTab;
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		selectedTab = tabPanel.getSelectedIndex();

		while (tabPanel.getWidgetCount() > 0) {
			tabPanel.remove(0);
		}

		if (propertiesVisible) {
			tabPanel.add(mail, Main.i18n("tab.mail.properties"));
			mail.langRefresh();
		}

		if (notesVisible) {
			tabPanel.add(notes, Main.i18n("tab.mail.notes"));
			notes.langRefresh();
		}

		if (previewVisible) {
			tabPanel.add(mailViewer, Main.i18n("tab.mail.preview"));
			mailViewer.langRefresh();
		}

		if (securityVisible) {
			tabPanel.add(security, Main.i18n("tab.mail.security"));
			security.langRefresh();
		}

		// Adding extensions
		for (Iterator<TabMailExtension> it = widgetExtensionList.iterator(); it.hasNext(); ) {
			TabMailExtension extension = it.next();
			tabPanel.add(extension, extension.getTabText());
		}

		// Refresh lang property group
		if (!propertyGroup.isEmpty()) {
			for (Iterator<PropertyGroup> it = propertyGroup.iterator(); it.hasNext(); ) {
				PropertyGroup group = it.next();
				tabPanel.add(group, group.getGrpLabel());
				group.langRefresh();
			}
		}

		tabPanel.selectTab(selectedTab);

		resizingIncubatorWidgets();
	}

	/**
	 * Sets visibility to buttons ( true / false )
	 *
	 * @param visible The visible value
	 */
	public void setVisibleButtons(boolean visible) {
		this.visibleButton = visible;
		mail.setVisibleButtons(visible);
		notes.setVisibleButtons(visible);
		security.setVisibleButtons(visible);
		fireEvent(HasMailEvent.SET_VISIBLE_BUTTONS);
	}

	/**
	 * Gets asynchronous to get all groups assigned to a document
	 */
	final AsyncCallback<List<GWTPropertyGroup>> callbackGetGroups = new AsyncCallback<List<GWTPropertyGroup>>() {
		public void onSuccess(List<GWTPropertyGroup> result) {
			boolean enableUpdatePropertyGroup = false;
			for (GWTPropertyGroup gwtGroup : result) {
				String groupTranslation = gwtGroup.getLabel();
				PropertyGroup group = new PropertyGroup(gwtGroup, mail.get(), visibleButton, gwtGroup.isReadonly());
				tabPanel.add(group, groupTranslation);
				propertyGroup.add(group);

				// Adds property group handlers
				for (Iterator<PropertyGroupHandlerExtension> itx = propertyGroupHandlerExtensionList.iterator(); itx.hasNext(); ) {
					group.addPropertyGroupHandlerExtension(itx.next());
				}

				// has update property group
				if (!enableUpdatePropertyGroup && group.isUpdatePropertyGrouupEnabled()) {
					enableUpdatePropertyGroup = true;
				}
			}

			// enable property group
			if (!enableUpdatePropertyGroup) {
				Main.get().mainPanel.topPanel.toolBar.enableUpdatePropertyGroup();
			}

			// To prevent change on document that has minor tabs than previous the new selected tab it'll be the max - 1 on that cases
			if (tabPanel.getWidgetCount() - 1 < latestSelectedTab) {
				tabPanel.selectTab(tabPanel.getWidgetCount() - 1);
			} else {
				tabPanel.selectTab(latestSelectedTab);    // Always enable selected tab because on document change tab group are removed
				// and on remove loses selectedTab
			}

			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetGroupProperties();
		}

		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetGroupProperties();
			Main.get().showError("GetGroups", caught);
		}
	};

	/**
	 * Gets all property groups assigned to document
	 */
	private void getGroups(String path) {
		Main.get().mainPanel.desktop.browser.tabMultiple.status.setGroupProperties();
		propertyGroupService.getGroups(path, callbackGetGroups);
	}

	/**
	 * Removes the actual property group
	 */
	public void removePropertyGroup() {
		selectedTab = tabPanel.getSelectedIndex(); // Sets the actual selected Tab

		// Removes group 
		PropertyGroup group = (PropertyGroup) tabPanel.getWidget(selectedTab);
		group.removeGroup();
		propertyGroup.remove(group);

		// Remove tab
		tabPanel.remove(selectedTab);

		// If removed tab is last the new selected tab is selectedTab -1
		if (tabPanel.getWidgetCount() - 1 < selectedTab) {
			selectedTab--;
		}

		// Sets the new selected tab
		tabPanel.selectTab(selectedTab);

	}

	/**
	 * Return if actual tab selected is group property and can be removed
	 */
	private boolean isRemovePropertyGroupEnabled(int tabIndex) {
		if ((tabPanel.getWidget(tabIndex) instanceof PropertyGroup)) {
			return ((PropertyGroup) (tabPanel.getWidget(tabIndex))).isRemovePropertyGroupEnabled();
		} else {
			return false;
		}
	}

	/**
	 * showProperties
	 */
	public void showProperties() {
		tabPanel.add(mail, Main.i18n("tab.mail.properties"));
		propertiesVisible = true;
	}

	/**
	 * showNotes
	 */
	public void showNotes() {
		tabPanel.add(notes, Main.i18n("tab.document.notes"));
		notesVisible = true;
	}

	/**
	 * showPreview
	 */
	public void showPreview() {
		tabPanel.add(mailViewer, Main.i18n("tab.mail.preview"));
		previewVisible = true;
	}

	/**
	 * showSecurity
	 */
	public void showSecurity() {
		tabPanel.add(security, Main.i18n("tab.mail.security"));
		securityVisible = true;
		SECURITY_TAB = tabPanel.getWidgetCount() - 1; // Starts at 0
	}

	/**
	 * showExtensions
	 */
	public void showExtensions() {
		for (TabMailExtension extension : widgetExtensionList) {
			tabPanel.add(extension, extension.getTabText());
			extension.setPixelSize(width, height - TAB_HEIGHT);
		}
	}

	/**
	 * setKeywordEnabled
	 */
	public void setKeywordEnabled(boolean enabled) {
		mail.setKeywordEnabled(enabled);
	}

	/**
	 * showPropertyGroups
	 */
	public void showPropertyGroups() {
		propertyGroupsVisible = true;
	}

	/**
	 * init
	 */
	public void init() {
		if (tabPanel.getWidgetCount() > 0) {
			tabPanel.selectTab(0);

			if (securityVisible && mail.get() != null) {
				security.setUuid(mail.get().getUuid());
				security.GetGrants();

				GWTFolder parentFolder = Main.get().activeFolderTree.getFolder();
				if ((parentFolder.getPermissions() & GWTPermission.SECURITY) == GWTPermission.SECURITY &&
						(mail.get().getPermissions() & GWTPermission.SECURITY) == GWTPermission.SECURITY) {
					security.setChangePermision(true);
				} else {
					security.setChangePermision(false);
				}
			}
		}
	}

	/**
	 * getMail
	 */
	public GWTMail getMail() {
		return mail.get();
	}

	/**
	 * addMailExtension
	 */
	public void addMailExtension(TabMailExtension extension) {
		widgetExtensionList.add(extension);
	}

	/**
	 * resizingIncubatorWidgets 
	 *
	 * Needs resizing if not widgets disappears
	 */
	public void resizingIncubatorWidgets() {
		security.setPixelSize(getOffsetWidth(), getOffsetHeight() - TAB_HEIGHT);
		security.fillWidth();

		// TODO:Solves minor bug with IE
		if (Util.getUserAgent().startsWith("ie")) {
			Timer timer = new Timer() {
				@Override
				public void run() {
					tabPanel.setWidth("" + width + "px");
					tabPanel.setWidth("" + (width + 1) + "px");

					Timer timer = new Timer() {
						@Override
						public void run() {
							tabPanel.setWidth("" + width + "px");
						}
					};

					timer.schedule(50);
				}
			};

			timer.schedule(100);
		}
	}

	/**
	 * initExtendedSecurity
	 */
	public void initExtendedSecurity(int extendedSecurity) {
		security.initExtendedSecurity(extendedSecurity);
	}

	@Override
	public void addMailHandlerExtension(MailHandlerExtension handlerExtension) {
		mailHandlerExtensionList.add(handlerExtension);
	}

	@Override
	public void fireEvent(MailEventConstant event) {
		for (MailHandlerExtension handlerExtension : mailHandlerExtensionList) {
			handlerExtension.onChange(event);
		}
	}

	@Override
	public void addPropertyGroupHandlerExtension(PropertyGroupHandlerExtension handlerExtension) {
		propertyGroupHandlerExtensionList.add(handlerExtension);
	}
}