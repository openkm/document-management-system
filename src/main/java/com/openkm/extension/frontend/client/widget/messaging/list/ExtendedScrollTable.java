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

package com.openkm.extension.frontend.client.widget.messaging.list;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.openkm.extension.frontend.client.service.*;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.extension.frontend.client.widget.messaging.MessagingToolBarBox;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.extension.*;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.comunicator.SearchComunicator;
import com.openkm.frontend.client.extension.comunicator.UtilComunicator;
import com.openkm.frontend.client.extension.comunicator.WorkspaceComunicator;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Extends ScrollTable functionalities
 *
 * @author jllort
 */
public class ExtendedScrollTable extends ScrollTable {
	private final OKMProposedSubscriptionServiceAsync proposedSubscriptionService = (OKMProposedSubscriptionServiceAsync) GWT
			.create(OKMProposedSubscriptionService.class);
	private final OKMProposedQueryServiceAsync proposedQueryService = (OKMProposedQueryServiceAsync) GWT
			.create(OKMProposedQueryService.class);
	private final OKMMessageServiceAsync messageService = (OKMMessageServiceAsync) GWT.create(OKMMessageService.class);

	// Special event case
	private static final int EVENT_ONMOUSEDOWN_RIGHT = -2;

	// Holds the data rows of the table this is a list of RowData Object
	public Map<Integer, Object> data = new HashMap<Integer, Object>();
	private int mouseX = 0;
	private int mouseY = 0;
	private int dataIndexValue = 0;
	private boolean panelSelected = false; // Indicates if panel is selected
	private FixedWidthGrid dataTable;
	private FixedWidthFlexTable headerTable;
	private int selectedRow = -1;

	/**
	 * ExtendedScrollTable
	 */
	public ExtendedScrollTable(FixedWidthGrid dataTable, FixedWidthFlexTable headerTable,
	                           ScrollTableImages scrollTableImages) {
		super(dataTable, headerTable, scrollTableImages);
		this.dataTable = dataTable;
		this.headerTable = headerTable;

		dataTable.setSelectionPolicy(SelectionGrid.SelectionPolicy.ONE_ROW);
		setResizePolicy(ResizePolicy.UNCONSTRAINED);
		setScrollPolicy(ScrollPolicy.BOTH);

		dataTable.setColumnSorter(new ExtendedColumnSorter());

		// Sets some events
		DOM.sinkEvents(getDataWrapper(), Event.ONCLICK | Event.ONDBLCLICK | Event.ONMOUSEDOWN);
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.user.client.EventListener#onBrowserEvent(com.google.gwt.user.client.Event)
	 */
	public void onBrowserEvent(Event event) {
		boolean headerFired = false; // Controls when event is fired by header

		// Case targe event is header must disable drag & drop
		if (headerTable.getEventTargetCell(event) != null) {
			headerFired = true;
		}

		// Selects the panel
		setSelectedPanel(true);

		// When de button mouse is released
		mouseX = DOM.eventGetClientX(event);
		mouseY = DOM.eventGetClientY(event);

		int type = DOM.eventGetType(event);

		if (type == Event.ONMOUSEDOWN && DOM.eventGetButton(event) == Event.BUTTON_RIGHT) {
			type = EVENT_ONMOUSEDOWN_RIGHT; // Special case, that must be so much similar to click event
		}

		switch (type) {
			case Event.ONDBLCLICK:
				DOM.eventCancelBubble(event, true);

			case Event.ONCLICK:
			case EVENT_ONMOUSEDOWN_RIGHT:
				// Only for right mouuse button
				if (!headerFired && type == EVENT_ONMOUSEDOWN_RIGHT) {
					MessagingToolBarBox.get().messageDashboard.messageBrowser.message.menuPopup.setPopupPosition(
							mouseX, mouseY);
					MessagingToolBarBox.get().messageDashboard.messageBrowser.message.menuPopup.show();
					DOM.eventPreventDefault(event); // Prevent to fire event to browser
				}

				if (dataTable.getEventTargetCell(event) != null) {
					// Mark panel as selected and disables tree navigator panel
					if (getSelectedRow() >= 0
							&& !MessagingToolBarBox.get().messageDashboard.messageBrowser.message.isPanelSelected()) {
						MessagingToolBarBox.get().messageDashboard.messageBrowser.message.setSelectedPanel(true);
					}

					// And row must be other than the selected one
					if (getSelectedRow() >= 0 && getSelectedRow() != selectedRow) {
						selectedRow = getSelectedRow();
						if (isProposedSubscriptionReceivedSelected()) {
							final int rowToRefresh = selectedRow;
							final GWTProposedSubscriptionReceived propose = getProposedSubscriptionReceived();
							MessagingToolBarBox.get().messageDashboard.messageBrowser.messageDetail.set(propose);
							if (propose.getSeenDate() == null) {
								MessagingToolBarBox.get().messageDashboard.messageBrowser.message.status
										.setFlag_markProposeAsSeen();
								proposedSubscriptionService.markSeen(propose.getId(), new AsyncCallback<Object>() {
									@Override
									public void onSuccess(Object result) {
										MessagingToolBarBox.get().messageDashboard.messageBrowser.message.status
												.unsetFlag_markProposeAsSeen();
										propose.setSeenDate(new Date());
										MessagingToolBarBox.get().messageDashboard.messageStack.proposedSubscriptionReceived
												.markProposeAsSeen();
										refreshRowData(propose, rowToRefresh);
									}

									@Override
									public void onFailure(Throwable caught) {
										MessagingToolBarBox.get().messageDashboard.messageBrowser.message.status
												.unsetFlag_markProposeAsSeen();
										GeneralComunicator.showError("markSeen", caught);
									}
								});
							}

						} else if (isProposedSubscriptionSentSelected()) {
							GWTProposedSubscriptionSent propose = getProposedSubscriptionSent();
							MessagingToolBarBox.get().messageDashboard.messageBrowser.messageDetail.set(propose);

						} else if (isProposedQueryReceivedSelected()) {
							final int rowToRefresh = selectedRow;
							final GWTProposedQueryReceived propose = getProposedQueryReceived();
							MessagingToolBarBox.get().messageDashboard.messageBrowser.messageDetail.set(propose);
							if (propose.getSeenDate() == null) {
								MessagingToolBarBox.get().messageDashboard.messageBrowser.message.status
										.setFlag_markProposeAsSeen();
								proposedQueryService.markSeen(propose.getId(), new AsyncCallback<Object>() {
									@Override
									public void onSuccess(Object result) {
										MessagingToolBarBox.get().messageDashboard.messageBrowser.message.status
												.unsetFlag_markProposeAsSeen();
										propose.setSeenDate(new Date());
										MessagingToolBarBox.get().messageDashboard.messageStack.proposedQueryReceived
												.markProposeAsSeen();
										refreshRowData(propose, rowToRefresh);
									}

									@Override
									public void onFailure(Throwable caught) {
										MessagingToolBarBox.get().messageDashboard.messageBrowser.message.status
												.unsetFlag_markProposeAsSeen();
										GeneralComunicator.showError("markSeen", caught);
									}
								});
							}

						} else if (isProposedQuerySentSelected()) {
							GWTProposedQuerySent propose = getProposedQuerySent();
							MessagingToolBarBox.get().messageDashboard.messageBrowser.messageDetail.set(propose);

						} else if (isMessageSentSelected()) {
							GWTTextMessageSent messageSent = getMessageSent();
							MessagingToolBarBox.get().messageDashboard.messageBrowser.messageDetail.set(messageSent);

						} else if (isMessageReceivedSelected()) {
							final int rowToRefresh = selectedRow;
							final GWTMessageReceived messageReceived = getMessageReceived();
							MessagingToolBarBox.get().messageDashboard.messageBrowser.messageDetail
									.set(messageReceived);
							if (messageReceived.getSeenDate() == null) {
								MessagingToolBarBox.get().messageDashboard.messageBrowser.message.status
										.setFlag_markMessageReceivedAsSeen();
								messageService.markSeen(messageReceived.getId(), new AsyncCallback<Object>() {
									@Override
									public void onSuccess(Object result) {
										MessagingToolBarBox.get().messageDashboard.messageBrowser.message.status
												.unsetFlag_markMessageReceivedAsSeen();
										messageReceived.setSeenDate(new Date());
										MessagingToolBarBox.get().messageDashboard.messageStack.messageReceived
												.markProposeAsSeen();
										refreshRowData(messageReceived, rowToRefresh);
									}

									@Override
									public void onFailure(Throwable caught) {
										MessagingToolBarBox.get().messageDashboard.messageBrowser.message.status
												.unsetFlag_markMessageReceivedAsSeen();
										GeneralComunicator.showError("markSeen", caught);
									}
								});
							}
						}
					}
				}
				break;
		}

		super.onBrowserEvent(event);
	}

	/**
	 * Sets the selected panel value
	 *
	 * @param selected The selected panel value
	 */
	public void setSelectedPanel(boolean selected) {
		if (selected) {
			MessagingToolBarBox.get().messageDashboard.messageBrowser.message.addStyleName("okm-PanelSelected");
			MessagingToolBarBox.get().messageDashboard.messageStack.proposedSubscriptionReceived
					.setSelectedPanel(false);
			MessagingToolBarBox.get().messageDashboard.messageStack.proposedQueryReceived.setSelectedPanel(false);
			MessagingToolBarBox.get().messageDashboard.messageStack.messageReceived.setSelectedPanel(false);
			MessagingToolBarBox.get().messageDashboard.messageStack.messageSent.setSelectedPanel(false);
		} else {
			MessagingToolBarBox.get().messageDashboard.messageBrowser.messageDetail
					.removeStyleName("okm-PanelSelected");
		}
		panelSelected = selected;
	}

	/**
	 * Is panel selected
	 *
	 * @return The panel selected value
	 */
	public boolean isPanelSelected() {
		return panelSelected;
	}

	/**
	 * Gets the X position on mouse click
	 *
	 * @return The x position on mouse click
	 */
	public int getMouseX() {
		return mouseX;
	}

	/**
	 * Gets the Y position on mouse click
	 *
	 * @return The y position on mouse click
	 */
	public int getMouseY() {
		return mouseY;
	}

	/**
	 * Gets the selected row
	 *
	 * @return The selected row
	 */
	public int getSelectedRow() {
		int selectedRow = -1;

		if (!dataTable.getSelectedRows().isEmpty()) {
			selectedRow = ((Integer) dataTable.getSelectedRows().iterator().next()).intValue();
		}

		Log.debug("ExtendedScrollPanel selectedRow:" + selectedRow);
		return selectedRow;
	}

	/**
	 * Resets the values
	 */
	public void reset() {
		selectedRow = -1;
		mouseX = 0;
		mouseY = 0;
		dataIndexValue = 0;

		// Only resets rows table the header is never reset
		data = new HashMap<Integer, Object>();
	}

	/**
	 * Adds values to the panel
	 *
	 * @param
	 */
	public void addRow(Object obj) {
		if (obj instanceof GWTProposedSubscriptionReceived) {
			addProposedSubscriptionReceivedRow((GWTProposedSubscriptionReceived) obj);
		} else if (obj instanceof GWTProposedQueryReceived) {
			addProposedQueryReceivedRow((GWTProposedQueryReceived) obj);
		} else if (obj instanceof GWTTextMessageSent) {
			addMessageSentRow((GWTTextMessageSent) obj);
		} else if (obj instanceof GWTMessageReceived) {
			addMessageReceivedRow((GWTMessageReceived) obj);
		} else if (obj instanceof GWTProposedQuerySent) {
			addProposedQuerySentRow((GWTProposedQuerySent) obj);
		} else if (obj instanceof GWTProposedSubscriptionSent) {
			addProposedSubscriptionSentRow((GWTProposedSubscriptionSent) obj);
		}
	}

	/**
	 * refreshRowData
	 *
	 * @param obj
	 * @param row
	 */
	private void refreshRowData(Object obj, int row) {
		if (obj instanceof GWTProposedSubscriptionReceived) {
			refreshProposedSubscriptionRow((GWTProposedSubscriptionReceived) obj, row);
		} else if (obj instanceof GWTProposedQueryReceived) {
			refreshProposedQueryRow((GWTProposedQueryReceived) obj, row);
		} else if (obj instanceof GWTMessageReceived) {
			refreshMessageReceivedRow((GWTMessageReceived) obj, row);
		}
	}

	/**
	 * Adding proposed subscription received row
	 *
	 * @param propose
	 */
	private void addProposedSubscriptionReceivedRow(final GWTProposedSubscriptionReceived propose) {
		int rows = dataTable.getRowCount();
		boolean seen = (propose.getSeenDate() == null);
		dataTable.insertRow(rows);

		// Sets folder object
		data.put(new Integer(dataIndexValue), propose);

		if (propose.isAccepted()) {
			dataTable.setWidget(rows, 0, new Image(OKMBundleResources.INSTANCE.yes()));
		} else {
			dataTable.setHTML(rows, 0, "");
		}

		dataTable.setHTML(rows, 1, UtilComunicator.getTextAsBoldHTML(propose.getFrom(), seen));
		String docType = "";
		if (propose.getType().equals(GWTFolder.TYPE)) {

			docType = GeneralComunicator.i18nExtension("messaging.message.type.propose.folder");
		} else {
			docType = GeneralComunicator.i18nExtension("messaging.message.type.propose.document");
		}
		dataTable.setHTML(rows, 2, UtilComunicator.getTextAsBoldHTML(docType, seen));
		DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18nExtension("general.date.pattern"));
		dataTable.setHTML(rows, 3, UtilComunicator.getTextAsBoldHTML(dtf.format(propose.getSentDate()), seen));

		Anchor anchor = new Anchor();
		String path = propose.getPath().substring(propose.getPath().lastIndexOf("/") + 1);
		anchor.setHTML(UtilComunicator.getTextAsBoldHTML(path, seen));
		anchor.setTitle(propose.getPath());
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				if (propose.getType().equals(GWTFolder.TYPE)) {
					GeneralComunicator.openPath(propose.getPath(), null);
				} else if (propose.getType().equals(GWTDocument.TYPE)) {
					String fldPath = propose.getPath().substring(0, propose.getPath().lastIndexOf("/"));
					GeneralComunicator.openPath(fldPath, propose.getPath());
				}
			}
		});

		anchor.setStyleName("okm-KeyMap-ImageHover");
		dataTable.setWidget(rows, 4, anchor);
		dataTable.setHTML(rows, 5, "" + (dataIndexValue++));

		// Format
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 1, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 2, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 3, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 4, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setVisible(rows, 5, false);

		for (int i = 0; i < 5; i++) {
			dataTable.getCellFormatter().addStyleName(rows, i, "okm-DisableSelect");
		}
	}

	/**
	 * Adding proposed subscription sent row
	 *
	 * @param propose
	 */
	private void addProposedSubscriptionSentRow(final GWTProposedSubscriptionSent propose) {
		int rows = dataTable.getRowCount();
		dataTable.insertRow(rows);
		// Sets folder object
		data.put(new Integer(dataIndexValue), propose);

		dataTable.setHTML(rows, 0, "");
		dataTable.setHTML(rows, 1, propose.getFrom());
		String docType = "";

		if (propose.getType().equals(GWTFolder.TYPE)) {
			docType = GeneralComunicator.i18nExtension("messaging.message.type.propose.folder");
		} else {
			docType = GeneralComunicator.i18nExtension("messaging.message.type.propose.document");
		}

		dataTable.setHTML(rows, 2, docType);
		DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18nExtension("general.date.pattern"));
		dataTable.setHTML(rows, 3, dtf.format(propose.getSentDate()));

		Anchor anchor = new Anchor();
		String path = propose.getPath().substring(propose.getPath().lastIndexOf("/") + 1);
		anchor.setHTML(path);
		anchor.setTitle(propose.getPath());
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				if (propose.getType().equals(GWTFolder.TYPE)) {
					GeneralComunicator.openPath(propose.getPath(), null);
				} else if (propose.getType().equals(GWTDocument.TYPE)) {
					String fldPath = propose.getPath().substring(0, propose.getPath().lastIndexOf("/"));
					GeneralComunicator.openPath(fldPath, propose.getPath());
				}
			}
		});

		anchor.setStyleName("okm-KeyMap-ImageHover");
		dataTable.setWidget(rows, 4, anchor);
		dataTable.setHTML(rows, 5, "" + (dataIndexValue++));

		// Format
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 1, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 2, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 3, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 4, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setVisible(rows, 5, false);

		for (int i = 0; i < 5; i++) {
			dataTable.getCellFormatter().addStyleName(rows, i, "okm-DisableSelect");
		}
	}

	/**
	 * Refresh proposed subscription row
	 *
	 * @param propose
	 */
	private void refreshProposedSubscriptionRow(final GWTProposedSubscriptionReceived propose, int row) {
		boolean seen = (propose.getSeenDate() == null);
		// Sets folder object
		data.put(new Integer(dataTable.getHTML(row, 5)), propose);

		if (propose.isAccepted()) {
			dataTable.setWidget(row, 0, new Image(OKMBundleResources.INSTANCE.yes()));
		} else {
			dataTable.setHTML(row, 0, "");
		}

		dataTable.setHTML(row, 1, UtilComunicator.getTextAsBoldHTML(propose.getFrom(), seen));
		String docType = "";

		if (propose.getType().equals(GWTFolder.TYPE)) {
			docType = GeneralComunicator.i18nExtension("messaging.message.type.propose.folder");
		} else {
			docType = GeneralComunicator.i18nExtension("messaging.message.type.propose.document");
		}

		dataTable.setHTML(row, 2, UtilComunicator.getTextAsBoldHTML(docType, seen));
		DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18nExtension("general.date.pattern"));
		dataTable.setHTML(row, 3, UtilComunicator.getTextAsBoldHTML(dtf.format(propose.getSentDate()), seen));
		Anchor anchor = new Anchor();
		String path = propose.getPath().substring(propose.getPath().lastIndexOf("/") + 1);
		anchor.setHTML(UtilComunicator.getTextAsBoldHTML(path, seen));
		anchor.setTitle(propose.getPath());
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				if (propose.getType().equals(GWTFolder.TYPE)) {
					GeneralComunicator.openPath(propose.getPath(), null);
				} else if (propose.getType().equals(GWTDocument.TYPE)) {
					String fldPath = propose.getPath().substring(0, propose.getPath().lastIndexOf("/"));
					GeneralComunicator.openPath(fldPath, propose.getPath());
				}
			}
		});

		anchor.setStyleName("okm-KeyMap-ImageHover");
		dataTable.setWidget(row, 4, anchor);
	}

	/**
	 * Adding proposed query row
	 *
	 * @param propose
	 */
	private void addProposedQueryReceivedRow(final GWTProposedQueryReceived propose) {
		int rows = dataTable.getRowCount();
		boolean seen = (propose.getSeenDate() == null);
		dataTable.insertRow(rows);
		// Sets folder object
		data.put(new Integer(dataIndexValue), propose);

		if (propose.isAccepted()) {
			dataTable.setWidget(rows, 0, new Image(OKMBundleResources.INSTANCE.yes()));
		} else {
			dataTable.setHTML(rows, 0, "");
		}

		dataTable.setHTML(rows, 1, UtilComunicator.getTextAsBoldHTML(propose.getFrom(), seen));
		String queryType = "";

		if (!propose.getParams().isDashboard()) {
			queryType = GeneralComunicator.i18nExtension("messaging.message.type.propose.query");
		} else {
			queryType = GeneralComunicator.i18nExtension("messaging.message.type.propose.user.query");
		}

		dataTable.setHTML(rows, 2, UtilComunicator.getTextAsBoldHTML(queryType, seen));
		DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18nExtension("general.date.pattern"));
		dataTable.setHTML(rows, 3, UtilComunicator.getTextAsBoldHTML(dtf.format(propose.getSentDate()), seen));
		Anchor anchor = new Anchor();
		String queryName = propose.getParams().getQueryName();
		anchor.setHTML(UtilComunicator.getTextAsBoldHTML(queryName, seen));
		anchor.setTitle(queryName);
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				WorkspaceComunicator.changeSelectedTab(UIDockPanelConstants.SEARCH);
				SearchComunicator.setSavedSearch(propose.getParams());
			}
		});

		anchor.setStyleName("okm-KeyMap-ImageHover");
		dataTable.setWidget(rows, 4, anchor);
		dataTable.setHTML(rows, 5, "" + (dataIndexValue++));

		// Format
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 1, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 2, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 3, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 4, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setVisible(rows, 5, false);

		for (int i = 0; i < 5; i++) {
			dataTable.getCellFormatter().addStyleName(rows, i, "okm-DisableSelect");
		}
	}

	/**
	 * Adding proposed query sent row
	 *
	 * @param propose
	 */
	private void addProposedQuerySentRow(final GWTProposedQuerySent propose) {
		int rows = dataTable.getRowCount();
		dataTable.insertRow(rows);
		// Sets folder object
		data.put(new Integer(dataIndexValue), propose);

		dataTable.setHTML(rows, 0, "");
		dataTable.setHTML(rows, 1, propose.getFrom());
		String queryType = "";

		if (!propose.getParams().isDashboard()) {
			queryType = GeneralComunicator.i18nExtension("messaging.message.type.propose.query");
		} else {
			queryType = GeneralComunicator.i18nExtension("messaging.message.type.propose.user.query");
		}

		dataTable.setHTML(rows, 2, queryType);
		DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18nExtension("general.date.pattern"));
		dataTable.setHTML(rows, 3, dtf.format(propose.getSentDate()));
		Anchor anchor = new Anchor();
		String queryName = propose.getParams().getQueryName();
		anchor.setHTML(queryName);
		anchor.setTitle(queryName);
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				WorkspaceComunicator.changeSelectedTab(UIDockPanelConstants.SEARCH);
				SearchComunicator.setSavedSearch(propose.getParams());
			}
		});

		anchor.setStyleName("okm-KeyMap-ImageHover");
		dataTable.setWidget(rows, 4, anchor);
		dataTable.setHTML(rows, 5, "" + (dataIndexValue++));

		// Format
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 1, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 2, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 3, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 4, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setVisible(rows, 5, false);

		for (int i = 0; i < 5; i++) {
			dataTable.getCellFormatter().addStyleName(rows, i, "okm-DisableSelect");
		}
	}

	/**
	 * Refresh proposed query row
	 *
	 * @param propose
	 */
	private void refreshProposedQueryRow(final GWTProposedQueryReceived propose, int row) {
		boolean seen = (propose.getSeenDate() == null);
		// Sets folder object
		data.put(new Integer(dataTable.getHTML(row, 5)), propose);

		if (propose.isAccepted()) {
			dataTable.setWidget(row, 0, new Image(OKMBundleResources.INSTANCE.yes()));
		} else {
			dataTable.setHTML(row, 0, "");
		}

		dataTable.setHTML(row, 1, UtilComunicator.getTextAsBoldHTML(propose.getFrom(), seen));
		String queryType = "";

		if (!propose.getParams().isDashboard()) {
			queryType = GeneralComunicator.i18nExtension("messaging.message.type.propose.query");
		} else {
			queryType = GeneralComunicator.i18nExtension("messaging.message.type.propose.user.query");
		}

		dataTable.setHTML(row, 2, UtilComunicator.getTextAsBoldHTML(queryType, seen));
		DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18nExtension("general.date.pattern"));
		dataTable.setHTML(row, 3, UtilComunicator.getTextAsBoldHTML(dtf.format(propose.getSentDate()), seen));
		Anchor anchor = new Anchor();
		String queryName = propose.getParams().getQueryName();
		anchor.setHTML(UtilComunicator.getTextAsBoldHTML(queryName, seen));
		anchor.setTitle(queryName);
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				WorkspaceComunicator.changeSelectedTab(UIDockPanelConstants.SEARCH);
				SearchComunicator.setSavedSearch(propose.getParams());
			}
		});

		anchor.setStyleName("okm-KeyMap-ImageHover");
		dataTable.setWidget(row, 4, anchor);
	}

	/**
	 * Adding addMessageSentRow
	 *
	 * @param messageSent
	 */
	private void addMessageSentRow(final GWTTextMessageSent messageSent) {
		int rows = dataTable.getRowCount();
		dataTable.insertRow(rows);
		// Sets folder object
		data.put(new Integer(dataIndexValue), messageSent);

		dataTable.setHTML(rows, 0, "");
		dataTable.setHTML(rows, 1, messageSent.getTo());
		dataTable.setHTML(rows, 2, GeneralComunicator.i18nExtension("messaging.message.type.message.sent"));
		DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18nExtension("general.date.pattern"));
		dataTable.setHTML(rows, 3, dtf.format(messageSent.getSentDate()));
		dataTable.setHTML(rows, 4, messageSent.getSubject());
		dataTable.setHTML(rows, 5, "" + (dataIndexValue++));

		// Format
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 1, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 2, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 3, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 4, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setVisible(rows, 5, false);

		for (int i = 0; i < 5; i++) {
			dataTable.getCellFormatter().addStyleName(rows, i, "okm-DisableSelect");
		}
	}

	/**
	 * Adding addMessageReceivedRow
	 *
	 * @param messageReceived
	 */
	private void addMessageReceivedRow(final GWTMessageReceived messageReceived) {
		int rows = dataTable.getRowCount();
		boolean seen = (messageReceived.getSeenDate() == null);
		dataTable.insertRow(rows);

		// Sets folder object
		data.put(new Integer(dataIndexValue), messageReceived);

		dataTable.setHTML(rows, 0, "");
		dataTable.setHTML(rows, 1, UtilComunicator.getTextAsBoldHTML(messageReceived.getFrom(), seen));
		dataTable.setHTML(
				rows,
				2,
				UtilComunicator.getTextAsBoldHTML(
						GeneralComunicator.i18nExtension("messaging.message.type.message.sent"), seen));
		DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18nExtension("general.date.pattern"));
		dataTable.setHTML(rows, 3, UtilComunicator.getTextAsBoldHTML(dtf.format(messageReceived.getSentDate()), seen));
		dataTable.setHTML(rows, 4, UtilComunicator.getTextAsBoldHTML(messageReceived.getSubject(), seen));
		dataTable.setHTML(rows, 5, "" + (dataIndexValue++));

		// Format
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 1, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 2, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 3, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 4, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setVisible(rows, 5, false);

		for (int i = 0; i < 5; i++) {
			dataTable.getCellFormatter().addStyleName(rows, i, "okm-DisableSelect");
		}
	}

	/**
	 * Refresh message received row
	 *
	 * @param messageReceived
	 */
	private void refreshMessageReceivedRow(final GWTMessageReceived messageReceived, int row) {
		boolean seen = (messageReceived.getSeenDate() == null);

		// Sets folder object
		data.put(new Integer(dataTable.getHTML(row, 5)), messageReceived);
		dataTable.setHTML(row, 0, "");
		dataTable.setHTML(row, 1, UtilComunicator.getTextAsBoldHTML(messageReceived.getFrom(), seen));
		dataTable.setHTML(
				row,
				2,
				UtilComunicator.getTextAsBoldHTML(
						GeneralComunicator.i18nExtension("messaging.message.type.message.sent"), seen));
		DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18nExtension("general.date.pattern"));
		dataTable.setHTML(row, 3, UtilComunicator.getTextAsBoldHTML(dtf.format(messageReceived.getSentDate()), seen));
		dataTable.setHTML(row, 4, UtilComunicator.getTextAsBoldHTML(messageReceived.getSubject(), seen));
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		for (int rows = 0; rows < dataTable.getRowCount(); rows++) {
			Object obj = data.get(Integer.parseInt(dataTable.getText(rows, 5)));

			if (obj instanceof GWTProposedSubscriptionReceived) {
				GWTProposedSubscriptionReceived propose = (GWTProposedSubscriptionReceived) obj;
				boolean seen = (propose.getSeenDate() == null);
				String docType = "";

				if (propose.getType().equals(GWTFolder.TYPE)) {
					docType = GeneralComunicator.i18nExtension("messaging.message.type.propose.folder");
				} else {
					docType = GeneralComunicator.i18nExtension("messaging.message.type.propose.document");
				}

				dataTable.setHTML(rows, 2, UtilComunicator.getTextAsBoldHTML(docType, seen));
				DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18nExtension("general.date.pattern"));
				dataTable.setHTML(rows, 3, UtilComunicator.getTextAsBoldHTML(dtf.format(propose.getSentDate()), seen));
			} else if (obj instanceof GWTProposedSubscriptionSent) {
				GWTProposedSubscriptionSent propose = (GWTProposedSubscriptionSent) obj;
				String docType = "";

				if (propose.getType().equals(GWTFolder.TYPE)) {
					docType = GeneralComunicator.i18nExtension("messaging.message.type.propose.folder");
				} else {
					docType = GeneralComunicator.i18nExtension("messaging.message.type.propose.document");
				}

				dataTable.setHTML(rows, 2, docType);
				DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18nExtension("general.date.pattern"));
				dataTable.setHTML(rows, 3, dtf.format(propose.getSentDate()));
			} else if (obj instanceof GWTProposedQueryReceived) {
				GWTProposedQueryReceived propose = (GWTProposedQueryReceived) obj;
				boolean seen = (propose.getSeenDate() == null);
				String queryType = "";

				if (!propose.getParams().isDashboard()) {
					queryType = GeneralComunicator.i18nExtension("messaging.message.type.propose.query");
				} else {
					queryType = GeneralComunicator.i18nExtension("messaging.message.type.propose.user.query");
				}

				dataTable.setHTML(rows, 2, UtilComunicator.getTextAsBoldHTML(queryType, seen));
				DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18nExtension("general.date.pattern"));
				dataTable.setHTML(rows, 3, UtilComunicator.getTextAsBoldHTML(dtf.format(propose.getSentDate()), seen));
			} else if (obj instanceof GWTProposedQuerySent) {
				GWTProposedQuerySent propose = (GWTProposedQuerySent) obj;
				String queryType = "";

				if (!propose.getParams().isDashboard()) {
					queryType = GeneralComunicator.i18nExtension("messaging.message.type.propose.query");
				} else {
					queryType = GeneralComunicator.i18nExtension("messaging.message.type.propose.user.query");
				}

				dataTable.setHTML(rows, 2, queryType);
				DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18nExtension("general.date.pattern"));
				dataTable.setHTML(rows, 3, dtf.format(propose.getSentDate()));
			} else if (obj instanceof GWTTextMessageSent) {
				GWTTextMessageSent messageSent = (GWTTextMessageSent) obj;
				dataTable.setHTML(rows, 2, GeneralComunicator.i18nExtension("messaging.message.type.message.sent"));
				DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18nExtension("general.date.pattern"));
				dataTable.setHTML(rows, 3, dtf.format(messageSent.getSentDate()));
			} else if (obj instanceof GWTMessageReceived) {
				GWTMessageReceived messageReceived = (GWTMessageReceived) obj;
				boolean seen = (messageReceived.getSeenDate() == null);
				dataTable.setHTML(
						rows,
						2,
						UtilComunicator.getTextAsBoldHTML(
								GeneralComunicator.i18nExtension("messaging.message.type.message.sent"), seen));
				DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18nExtension("general.date.pattern"));
				dataTable.setHTML(rows, 3,
						UtilComunicator.getTextAsBoldHTML(dtf.format(messageReceived.getSentDate()), seen));
			}
		}
	}

	/**
	 * Sets the selected row
	 *
	 * @param row The row number
	 */
	public void setSelectedRow(int row) {
		Log.debug("ExtendedScrollPanel setSelectedRow:" + row);
		dataTable.selectRow(row, true);
	}

	/**
	 * Gets a actual proposed subscription received object row
	 *
	 * @return
	 */
	public GWTProposedSubscriptionReceived getProposedSubscriptionReceived() {
		if (isProposedSubscriptionReceivedSelected()) {
			return ((GWTProposedSubscriptionReceived) data
					.get(Integer.parseInt(dataTable.getText(getSelectedRow(), 5))));
		} else {
			return null;
		}
	}

	/**
	 * Gets a actual proposed subscription sent object row
	 *
	 * @return
	 */
	public GWTProposedSubscriptionSent getProposedSubscriptionSent() {
		if (isProposedSubscriptionSentSelected()) {
			return ((GWTProposedSubscriptionSent) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), 5))));
		} else {
			return null;
		}
	}

	/**
	 * Gets a actual proposed query object row
	 *
	 * @return
	 */
	public GWTProposedQueryReceived getProposedQueryReceived() {
		if (isProposedQueryReceivedSelected()) {
			return ((GWTProposedQueryReceived) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), 5))));
		} else {
			return null;
		}
	}

	/**
	 * Gets a actual proposed query object row
	 *
	 * @return
	 */
	public GWTProposedQuerySent getProposedQuerySent() {
		if (isProposedQuerySentSelected()) {
			return ((GWTProposedQuerySent) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), 5))));
		} else {
			return null;
		}
	}

	/**
	 * Gets a actual message sent object row
	 *
	 * @return
	 */
	public GWTTextMessageSent getMessageSent() {
		if (isMessageSentSelected()) {
			return ((GWTTextMessageSent) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), 5))));
		} else {
			return null;
		}
	}

	/**
	 * Gets a actual message received object row
	 *
	 * @return
	 */
	public GWTMessageReceived getMessageReceived() {
		if (isMessageReceivedSelected()) {
			return ((GWTMessageReceived) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), 5))));
		} else {
			return null;
		}
	}

	/**
	 * Return true or false if actual selected row is proposed subscription received
	 *
	 * @return True or False if actual row is proposed subscription type
	 */
	public boolean isProposedSubscriptionReceivedSelected() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			if (data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), 5))) instanceof GWTProposedSubscriptionReceived) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Return true or false if actual selected row is proposed subscription sent
	 *
	 * @return True or False if actual row is proposed subscription type
	 */
	public boolean isProposedSubscriptionSentSelected() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			if (data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), 5))) instanceof GWTProposedSubscriptionSent) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Return true or false if actual selected row is proposed query
	 *
	 * @return True or False if actual row is proposed query type
	 */
	public boolean isProposedQueryReceivedSelected() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			if (data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), 5))) instanceof GWTProposedQueryReceived) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Return true or false if actual selected row is proposed query
	 *
	 * @return True or False if actual row is proposed query type
	 */
	public boolean isProposedQuerySentSelected() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			if (data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), 5))) instanceof GWTProposedQuerySent) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Return true or false if actual selected row is message sent
	 *
	 * @return True or False if actual row is message sent type
	 */
	public boolean isMessageSentSelected() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			if (data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), 5))) instanceof GWTTextMessageSent) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Return true or false if actual selected row is message received
	 *
	 * @return True or False if actual row is message sent type
	 */
	public boolean isMessageReceivedSelected() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			if (data.get(Integer.parseInt(dataTable.getText(getSelectedRow(), 5))) instanceof GWTMessageReceived) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * markProposeAsAccepted
	 */
	public void markProposeAsAccepted() {
		if (isProposedSubscriptionReceivedSelected()) {
			getProposedSubscriptionReceived().setAccepted(true);
			dataTable.setWidget(getSelectedRow(), 0, new Image(OKMBundleResources.INSTANCE.yes()));
		} else if (isProposedQueryReceivedSelected()) {
			getProposedQueryReceived().setAccepted(true);
			dataTable.setWidget(getSelectedRow(), 0, new Image(OKMBundleResources.INSTANCE.yes()));
		}
	}
}
