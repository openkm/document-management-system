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

package com.openkm.frontend.client.widget.dashboard;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.constants.ui.UIDashboardConstants;
import com.openkm.frontend.client.extension.widget.toolbar.ToolBarBoxExtension;
import com.openkm.frontend.client.util.OKMBundleResources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * HorizontalToolBar
 *
 * @author jllort
 *
 */
public class HorizontalToolBar extends Composite {

	private HorizontalPanel hPanel;
	private ToolBarBox user;
	private ToolBarBox mail;
	private ToolBarBox news;
	private ToolBarBox general;
	private ToolBarBox workflow;
	private ToolBarBox keywordMap;
	private Widget enabledWidget;
	private List<ToolBarBoxExtension> widgetExtensionList;

	MouseOverHandler mouseOverHandler = new MouseOverHandler() {
		@Override
		public void onMouseOver(MouseOverEvent event) {
			Widget sender = (Widget) event.getSource();
			sender.addStyleName("okm-ToolBar-BigTMP-selected");
		}
	};
	MouseOutHandler mouseOutHandler = new MouseOutHandler() {
		@Override
		public void onMouseOut(MouseOutEvent event) {
			Widget sender = (Widget) event.getSource();
			sender.removeStyleName("okm-ToolBar-BigTMP-selected");
		}
	};

	/**
	 * HorizontalToolBar
	 */
	public HorizontalToolBar() {
		widgetExtensionList = new ArrayList<ToolBarBoxExtension>();
		hPanel = new HorizontalPanel();
		user = new ToolBarBox(new Image(OKMBundleResources.INSTANCE.userIcon()), Main.i18n("dashboard.tab.user"));
		mail = new ToolBarBox(new Image(OKMBundleResources.INSTANCE.mailIcon()), Main.i18n("dashboard.tab.mail"));
		news = new ToolBarBox(new Image(OKMBundleResources.INSTANCE.newsIcon()), Main.i18n("dashboard.tab.news"));
		general = new ToolBarBox(new Image(OKMBundleResources.INSTANCE.generalIcon()), Main.i18n("dashboard.tab.general"));
		workflow = new ToolBarBox(new Image(OKMBundleResources.INSTANCE.workflowIcon()), Main.i18n("dashboard.tab.workflow"));
		keywordMap = new ToolBarBox(new Image(OKMBundleResources.INSTANCE.keywordMapIcon()), Main.i18n("dashboard.tab.keymap"));

		enabledWidget = user; // Setting the enabled widget

		user.addMouseOverHandler(mouseOverHandler);
		user.addMouseOutHandler(mouseOutHandler);
		mail.addMouseOverHandler(mouseOverHandler);
		mail.addMouseOutHandler(mouseOutHandler);
		news.addMouseOverHandler(mouseOverHandler);
		news.addMouseOutHandler(mouseOutHandler);
		general.addMouseOverHandler(mouseOverHandler);
		general.addMouseOutHandler(mouseOutHandler);
		workflow.addMouseOverHandler(mouseOverHandler);
		workflow.addMouseOutHandler(mouseOutHandler);
		keywordMap.addMouseOverHandler(mouseOverHandler);
		keywordMap.addMouseOutHandler(mouseOutHandler);

		user.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Widget sender = (Widget) event.getSource();
				enabledWidget.removeStyleName("okm-ToolBar-Big-selected");
				sender.setStyleName("okm-ToolBar-Big-selected");
				enabledWidget = sender;
				Main.get().mainPanel.dashboard.changeView(UIDashboardConstants.DASHBOARD_USER);
			}
		});

		mail.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Widget sender = (Widget) event.getSource();
				enabledWidget.removeStyleName("okm-ToolBar-Big-selected");
				sender.setStyleName("okm-ToolBar-Big-selected");
				enabledWidget = sender;
				Main.get().mainPanel.dashboard.changeView(UIDashboardConstants.DASHBOARD_MAIL);
			}
		});

		news.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Widget sender = (Widget) event.getSource();
				enabledWidget.removeStyleName("okm-ToolBar-Big-selected");
				sender.setStyleName("okm-ToolBar-Big-selected");
				enabledWidget = sender;
				Main.get().mainPanel.dashboard.changeView(UIDashboardConstants.DASHBOARD_NEWS);
			}
		});

		general.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Widget sender = (Widget) event.getSource();
				enabledWidget.removeStyleName("okm-ToolBar-Big-selected");
				sender.setStyleName("okm-ToolBar-Big-selected");
				enabledWidget = sender;
				Main.get().mainPanel.dashboard.changeView(UIDashboardConstants.DASHBOARD_GENERAL);
			}
		});

		workflow.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Widget sender = (Widget) event.getSource();
				enabledWidget.removeStyleName("okm-ToolBar-Big-selected");
				sender.setStyleName("okm-ToolBar-Big-selected");
				enabledWidget = sender;
				Main.get().mainPanel.dashboard.changeView(UIDashboardConstants.DASHBOARD_WORKFLOW);
			}
		});

		keywordMap.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Widget sender = (Widget) event.getSource();
				enabledWidget.removeStyleName("okm-ToolBar-Big-selected");
				sender.setStyleName("okm-ToolBar-Big-selected");
				enabledWidget = sender;
				Main.get().mainPanel.dashboard.changeView(UIDashboardConstants.DASHBOARD_KEYMAP);
			}
		});

		user.setStyleName("okm-ToolBar-Big-selected");
		hPanel.setStyleName("okm-ToolBar");
		hPanel.addStyleName("okm-ToolBar-Border");
		hPanel.addStyleName("okm-DisableSelect");

		initWidget(hPanel);
	}

	/**
	 * Refreshing language
	 */
	public void langRefresh() {
		user.setLabelText(Main.i18n("dashboard.tab.user"));
		mail.setLabelText(Main.i18n("dashboard.tab.mail"));
		news.setLabelText(Main.i18n("dashboard.tab.news"));
		general.setLabelText(Main.i18n("dashboard.tab.general"));
		workflow.setLabelText(Main.i18n("dashboard.tab.workflow"));
		keywordMap.setLabelText(Main.i18n("dashboard.tab.keymap"));
	}

	/**
	 * Show the user view
	 */
	public void showUserView() {
		enabledWidget.removeStyleName("okm-ToolBar-Big-selected");
		user.setStyleName("okm-ToolBar-Big-selected");
		enabledWidget = user;
		Main.get().mainPanel.dashboard.changeView(UIDashboardConstants.DASHBOARD_USER);
	}

	/**
	 * Shows the news view
	 */
	public void showNewsView() {
		enabledWidget.removeStyleName("okm-ToolBar-Big-selected");
		news.setStyleName("okm-ToolBar-Big-selected");
		enabledWidget = news;
		Main.get().mainPanel.dashboard.changeView(UIDashboardConstants.DASHBOARD_NEWS);
	}

	/**
	 * Shows the workflow view
	 */
	public void showWorkflowView() {
		enabledWidget.removeStyleName("okm-ToolBar-Big-selected");
		workflow.setStyleName("okm-ToolBar-Big-selected");
		enabledWidget = workflow;
		Main.get().mainPanel.dashboard.changeView(UIDashboardConstants.DASHBOARD_WORKFLOW);
	}

	/**
	 * showUser
	 */
	public void showUser() {
		hPanel.add(user);
		hPanel.setCellWidth(user, "80px");
	}

	/**
	 * showMail
	 */
	public void showMail() {
		hPanel.add(mail);
		hPanel.setCellWidth(mail, "80px");
	}

	/**
	 * showNews
	 */
	public void showNews() {
		hPanel.add(news);
		hPanel.setCellWidth(news, "80px");
	}

	/**
	 * showGeneral
	 */
	public void showGeneral() {
		hPanel.add(general);
		hPanel.setCellWidth(general, "80px");
	}

	/**
	 * showWorkflow
	 */
	public void showWorkflow() {
		hPanel.add(workflow);
		hPanel.setCellWidth(workflow, "80px");
	}

	/**
	 * showKeywords
	 */
	public void showKeywords() {
		hPanel.add(keywordMap);
		hPanel.setCellWidth(keywordMap, "80px");
	}

	/**
	 * selectedExtension
	 *
	 * @return
	 */
	public int getSelectedExtension() {
		int count = 0;
		for (Iterator<ToolBarBoxExtension> it = widgetExtensionList.iterator(); it.hasNext(); ) {
			if (it.next().equals(enabledWidget)) {
				return count;
			}
			count++;
		}
		return 0;
	}

	/**
	 * init
	 */
	public void init() {
		for (Iterator<ToolBarBoxExtension> it = widgetExtensionList.iterator(); it.hasNext(); ) {
			ToolBarBoxExtension extension = it.next();
			hPanel.add(extension);
			hPanel.setCellWidth(extension, "80px");
			extension.addMouseOverHandler(mouseOverHandler);
			extension.addMouseOutHandler(mouseOutHandler);
			extension.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Widget sender = (Widget) event.getSource();
					enabledWidget.removeStyleName("okm-ToolBar-Big-selected");
					sender.setStyleName("okm-ToolBar-Big-selected");
					enabledWidget = sender;
					Main.get().mainPanel.dashboard.changeView(UIDashboardConstants.DASHBOARD_EXTENSION);
				}
			});
		}
		HTML space = new HTML("&nbsp;");
		hPanel.add(space);
	}

	/**
	 * addToolBarBoxExtension
	 *
	 * @param extension
	 */
	public void addToolBarBoxExtension(ToolBarBoxExtension extension) {
		widgetExtensionList.add(extension);
	}

	/**
	 * showToolBoxExtension
	 *
	 * @param widget
	 */
	public void showToolBoxExtension(ToolBarBoxExtension extension) {
		enabledWidget.removeStyleName("okm-ToolBar-Big-selected");
		((Widget) extension).setStyleName("okm-ToolBar-Big-selected");
		enabledWidget = ((Widget) extension);
		Main.get().mainPanel.dashboard.changeView(UIDashboardConstants.DASHBOARD_EXTENSION);
	}
}