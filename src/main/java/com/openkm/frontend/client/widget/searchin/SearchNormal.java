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

package com.openkm.frontend.client.widget.searchin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTUser;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;
import com.openkm.frontend.client.service.OKMAuthService;
import com.openkm.frontend.client.service.OKMAuthServiceAsync;
import com.openkm.frontend.client.util.OKMBundleResources;

import java.util.Date;
import java.util.List;

/**
 * SearchNormal
 *
 * @author jllort
 *
 */
public class SearchNormal extends Composite {
	private final OKMAuthServiceAsync authService = (OKMAuthServiceAsync) GWT.create(OKMAuthService.class);

	private static final int CALENDAR_FIRED_NONE = -1;
	private static final int CALENDAR_FIRED_START = 0;
	private static final int CALENDAR_FIRED_END = 1;

	private ScrollPanel scrollPanel;
	private FlexTable table;
	public ListBox context;
	public TextBox content;
	public TextBox name;
	public TextBox keywords;
	public ListBox userListBox;
	public TextBox startDate;
	public TextBox endDate;
	public HorizontalPanel dateRange;
	public PopupPanel calendarPopup;
	public CalendarWidget calendar;
	public Image startCalendarIcon;
	public Image endCalendarIcon;
	public Image cleanIcon;
	public HTML dateBetween;
	public int calendarFired = CALENDAR_FIRED_NONE;
	public Date modifyDateFrom;
	public Date modifyDateTo;
	private int posTaxonomy = 0;
	private int posTemplates = 0;
	private int posPersonal = 0;
	private int posMail = 0;
	private int posTrash = 0;
	private boolean templatesVisible = false;
	private boolean personalVisible = false;
	private boolean mailVisible = false;
	private boolean trashVisible = false;
	private String trashContextValue = "";
	private String personalContextValue = "";
	private String mailContextValue = "";
	private String templatesContextValue = "";

	/**
	 * SearchNormal
	 */
	public SearchNormal() {
		table = new FlexTable();
		scrollPanel = new ScrollPanel(table);

		context = new ListBox();
		context.setStyleName("okm-Select");
		int count = 0;
		posTaxonomy = count++;
		context.addItem(Main.i18n("leftpanel.label.taxonomy"), "");

		if (templatesVisible) {
			posTemplates = count++;
			context.addItem(Main.i18n("leftpanel.label.templates"), "");
		}

		if (personalVisible) {
			posPersonal = count++;
			context.addItem(Main.i18n("leftpanel.label.my.documents"), "");
		}

		if (mailVisible) {
			posMail = count++;
			context.addItem(Main.i18n("leftpanel.label.mail"), "");
		}

		if (trashVisible) {
			posTrash = count++;
			context.addItem(Main.i18n("leftpanel.label.trash"), "");
		}

		context.setSelectedIndex(posTaxonomy);

		context.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				// each time list is changed must clean folder
				Main.get().mainPanel.search.searchBrowser.searchIn.searchAdvanced.path.setText("");

				// Always enable mail search in mail view
				if (mailVisible && context.getSelectedIndex() == posMail) {
					Main.get().mainPanel.search.searchBrowser.searchIn.searchAdvanced.enableMailSearch();
				}
			}
		});

		content = new TextBox();
		name = new TextBox();
		keywords = new TextBox();
		userListBox = new ListBox();
		startDate = new TextBox();
		endDate = new TextBox();
		dateRange = new HorizontalPanel();
		calendar = new CalendarWidget();
		calendarPopup = new PopupPanel(true);
		startCalendarIcon = new Image(OKMBundleResources.INSTANCE.calendar());
		endCalendarIcon = new Image(OKMBundleResources.INSTANCE.calendar());
		cleanIcon = new Image(OKMBundleResources.INSTANCE.cleanIcon());
		dateBetween = new HTML("&nbsp;&harr;&nbsp;");

		userListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				Main.get().mainPanel.search.searchBrowser.searchIn.searchControl.evaluateSearchButtonVisible();
			}
		});

		// Calendar widget
		calendarPopup.setWidget(calendar);

		calendar.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				calendarPopup.hide();
				DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.day.pattern"));

				switch (calendarFired) {
					case CALENDAR_FIRED_START:
						startDate.setText(dtf.format(calendar.getDate()));
						modifyDateFrom = (Date) calendar.getDate().clone();
						break;

					case CALENDAR_FIRED_END:
						endDate.setText(dtf.format(calendar.getDate()));
						modifyDateTo = (Date) calendar.getDate().clone();
						break;
				}

				calendarFired = CALENDAR_FIRED_NONE;
				Main.get().mainPanel.search.searchBrowser.searchIn.searchControl.evaluateSearchButtonVisible();
			}
		});

		startCalendarIcon.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				calendarFired = CALENDAR_FIRED_START;
				if (modifyDateFrom != null) {
					calendar.setNow((Date) modifyDateFrom.clone());
				} else {
					calendar.setNow(null);
				}
				calendarPopup.setPopupPosition(startCalendarIcon.getAbsoluteLeft(), startCalendarIcon.getAbsoluteTop() - 2);
				calendarPopup.show();
			}
		});
		startCalendarIcon.setStyleName("okm-Hyperlink");

		endCalendarIcon.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				calendarFired = CALENDAR_FIRED_END;
				if (modifyDateTo != null) {
					calendar.setNow((Date) modifyDateTo.clone());
				} else {
					calendar.setNow(null);
				}
				calendarPopup.setPopupPosition(endCalendarIcon.getAbsoluteLeft(), endCalendarIcon.getAbsoluteTop() - 2);
				calendarPopup.show();
			}
		});
		endCalendarIcon.setStyleName("okm-Hyperlink");

		cleanIcon.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				startDate.setText("");
				modifyDateFrom = null;
				endDate.setText("");
				modifyDateTo = null;
			}
		});
		cleanIcon.setStyleName("okm-Hyperlink");

		// Date range panel
		dateRange.add(startDate);
		dateRange.add(startCalendarIcon);
		dateRange.add(dateBetween);
		dateRange.add(endDate);
		dateRange.add(endCalendarIcon);
		dateRange.add(cleanIcon);
		startDate.setWidth("70px");
		endDate.setWidth("70px");
		startDate.setMaxLength(10);
		endDate.setMaxLength(10);
		startDate.setReadOnly(true);
		endDate.setReadOnly(true);
		dateRange.setCellVerticalAlignment(startCalendarIcon, HasAlignment.ALIGN_MIDDLE);
		dateRange.setCellVerticalAlignment(endCalendarIcon, HasAlignment.ALIGN_MIDDLE);
		dateRange.setCellVerticalAlignment(cleanIcon, HasAlignment.ALIGN_MIDDLE);
		dateRange.setCellWidth(cleanIcon, "25px");
		dateRange.setCellHorizontalAlignment(cleanIcon, HasAlignment.ALIGN_RIGHT);
		dateBetween.addStyleName("okm-NoWrap");

		table.setHTML(0, 0, Main.i18n("search.context"));
		table.setWidget(0, 1, context);
		table.setHTML(1, 0, Main.i18n("search.content"));
		table.setWidget(1, 1, content);
		table.setHTML(2, 0, Main.i18n("search.name"));
		table.setWidget(2, 1, name);
		table.setHTML(3, 0, Main.i18n("search.keywords"));
		table.setWidget(3, 1, keywords);
		table.setHTML(4, 0, Main.i18n("search.user"));
		table.setWidget(4, 1, userListBox);
		table.setHTML(5, 0, Main.i18n("search.date.range"));
		table.setWidget(5, 1, dateRange);

		CellFormatter cellFormatter = table.getCellFormatter();
		cellFormatter.setStyleName(0, 0, "okm-DisableSelect");
		cellFormatter.setStyleName(1, 0, "okm-DisableSelect");
		cellFormatter.setStyleName(2, 0, "okm-DisableSelect");
		cellFormatter.setStyleName(3, 0, "okm-DisableSelect");
		cellFormatter.setStyleName(4, 0, "okm-DisableSelect");
		cellFormatter.setStyleName(5, 0, "okm-DisableSelect");

		// Sets wordWrap for al rows
		setRowWordWarp(table, 0, 1, false);
		setRowWordWarp(table, 1, 1, false);
		setRowWordWarp(table, 2, 1, false);
		setRowWordWarp(table, 3, 1, false);
		setRowWordWarp(table, 4, 1, false);
		setRowWordWarp(table, 5, 1, false);

		content.setStyleName("okm-Input");
		name.setStyleName("okm-Input");
		keywords.setStyleName("okm-Input");
		userListBox.setStyleName("okm-Input");
		startDate.setStyleName("okm-Input");
		endDate.setStyleName("okm-Input");

		initWidget(scrollPanel);
	}

	/**
	 * Set the WordWarp for all the row cells
	 *
	 * @param row The row cell
	 * @param columns Number of row columns
	 * @param warp
	 */
	private void setRowWordWarp(FlexTable table, int row, int columns, boolean wrap) {
		CellFormatter cellFormatter = table.getCellFormatter();

		for (int i = 0; i < columns; i++) {
			cellFormatter.setWordWrap(row, i, wrap);
		}
	}

	/**
	 * Gets all users
	 */
	public void getAllUsers() {
		authService.getAllUsers(new AsyncCallback<List<GWTUser>>() {
			public void onSuccess(List<GWTUser> result) {
				userListBox.addItem("", ""); // Add first value empty
				for (GWTUser user : result) {
					userListBox.addItem(user.getUsername(), user.getId());
				}
			}

			public void onFailure(Throwable caught) {
				Main.get().showError("GetAllUsers", caught);
			}
		});
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		table.setHTML(0, 0, Main.i18n("search.context"));
		table.setHTML(1, 0, Main.i18n("search.content"));
		table.setHTML(2, 0, Main.i18n("search.name"));
		table.setHTML(3, 0, Main.i18n("search.keywords"));
		table.setHTML(4, 0, Main.i18n("search.user"));
		table.setHTML(5, 0, Main.i18n("search.date.range"));

		context.setItemText(posTaxonomy, Main.i18n("leftpanel.label.taxonomy"));

		if (templatesVisible) {
			context.setItemText(posTemplates, Main.i18n("leftpanel.label.templates"));
		}

		if (personalVisible) {
			context.setItemText(posPersonal, Main.i18n("leftpanel.label.my.documents"));
		}

		if (mailVisible) {
			context.setItemText(posMail, Main.i18n("leftpanel.label.mail"));
		}

		if (trashVisible) {
			context.setItemText(posTrash, Main.i18n("leftpanel.label.trash"));
		}

		calendar.langRefresh();
	}

	/**
	 * Sets the context values
	 *
	 * @param contextValue The context value
	 * @param stackView The stack view
	 */
	public void setContextValue(String contextValue, int stackView) {
		switch (stackView) {
			case UIDesktopConstants.NAVIGATOR_TAXONOMY:
				context.setValue(posTaxonomy, contextValue);
				break;

			case UIDesktopConstants.NAVIGATOR_TEMPLATES:
				templatesContextValue = contextValue;

				if (templatesVisible) {
					posTemplates = context.getItemCount(); // Item count by default is good id, 0 is first item, etc...
					context.addItem(Main.i18n("leftpanel.label.templates"), templatesContextValue);
				}
				break;

			case UIDesktopConstants.NAVIGATOR_PERSONAL:
				personalContextValue = contextValue;

				if (personalVisible) {
					posPersonal = context.getItemCount();
					context.addItem(Main.i18n("leftpanel.label.my.documents"), personalContextValue);
				}
				break;

			case UIDesktopConstants.NAVIGATOR_MAIL:
				mailContextValue = contextValue;

				if (mailVisible) {
					posMail = context.getItemCount();
					context.addItem(Main.i18n("leftpanel.label.mail"), mailContextValue);
				}
				break;

			case UIDesktopConstants.NAVIGATOR_TRASH:
				trashContextValue = contextValue;

				if (trashVisible) {
					posTrash = context.getItemCount();
					context.addItem(Main.i18n("leftpanel.label.trash"), trashContextValue);
				}
				break;
		}
	}

	/**
	 * showTemplates
	 */
	public void showTemplates() {
		templatesVisible = true;
	}

	/**
	 * showPersonal
	 */
	public void showPersonal() {
		personalVisible = true;
	}

	/**
	 * showMail
	 */
	public void showMail() {
		mailVisible = true;
	}

	/**
	 * showTrash
	 */
	public void showTrash() {
		trashVisible = true;
	}
}