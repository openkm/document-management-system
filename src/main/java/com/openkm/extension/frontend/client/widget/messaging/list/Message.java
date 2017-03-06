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

package com.openkm.extension.frontend.client.widget.messaging.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollTableImages;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.openkm.extension.frontend.client.service.*;
import com.openkm.extension.frontend.client.widget.messaging.MessagingToolBarBox;
import com.openkm.frontend.client.bean.extension.*;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

/**
 * Message
 *
 * @author jllort
 *
 */
public class Message extends Composite {

	private final OKMMessageServiceAsync messageService = (OKMMessageServiceAsync) GWT.create(OKMMessageService.class);
	private final OKMProposedQueryServiceAsync proposedQueryService = (OKMProposedQueryServiceAsync) GWT.create(OKMProposedQueryService.class);
	private final OKMProposedSubscriptionServiceAsync proposedSubscriptionService = (OKMProposedSubscriptionServiceAsync) GWT.create(OKMProposedSubscriptionService.class);

	// Number of columns
	public static final int NUMBER_OF_COLUMNS = 6;

	private SimplePanel sp;
	public ExtendedScrollTable table;
	private FixedWidthFlexTable headerTable;
	private FixedWidthGrid dataTable;
	public Status status;
	public MenuPopup menuPopup;

	/**
	 * SearchResult
	 */
	public Message() {
		menuPopup = new MenuPopup();
		menuPopup.setStyleName("okm-MenuPopup");
		status = new Status();
		status.setStyleName("okm-StatusPopup");

		ScrollTableImages scrollTableImages = new ScrollTableImages() {
			public AbstractImagePrototype scrollTableAscending() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/sort_asc.gif");
					}

					public Image createImage() {
						return new Image("img/sort_asc.gif");
					}

					public String getHTML() {
						return "<img border=\"0\" src=\"img/sort_asc.gif\"/>";
					}
				};
			}

			public AbstractImagePrototype scrollTableDescending() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/sort_desc.gif");
					}

					public Image createImage() {
						return new Image("img/sort_desc.gif");
					}

					public String getHTML() {
						return "<img border=\"0\" src=\"img/sort_desc.gif\"/>";
					}
				};
			}

			public AbstractImagePrototype scrollTableFillWidth() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/fill_width.gif");
					}

					public Image createImage() {
						return new Image("img/fill_width.gif");
					}

					public String getHTML() {
						return "<img border=\"0\" src=\"img/fill_width.gif\"/>";
					}
				};
			}
		};

		headerTable = new FixedWidthFlexTable();
		dataTable = new FixedWidthGrid();
		table = new ExtendedScrollTable(dataTable, headerTable, scrollTableImages);
		table.setCellSpacing(0);
		table.setCellPadding(2);
		table.setSize("540px", "140px");

		// Level 1 headers
		headerTable.setHTML(0, 0, "&nbsp;");
		headerTable.setHTML(0, 1, GeneralComunicator.i18nExtension("messaging.message.user"));
		headerTable.setHTML(0, 2, GeneralComunicator.i18nExtension("messaging.message.type"));
		headerTable.setHTML(0, 3, GeneralComunicator.i18nExtension("messaging.message.date"));
		headerTable.setHTML(0, 4, GeneralComunicator.i18nExtension("messaging.message.proposed.folder.document"));
		headerTable.setHTML(0, 5, "");

		// Format
		table.setColumnWidth(0, 30);
		table.setColumnWidth(1, 150);
		table.setColumnWidth(2, 175);
		table.setColumnWidth(3, 150);
		table.setColumnWidth(4, 400);

		table.setPreferredColumnWidth(0, 30);
		table.setPreferredColumnWidth(2, 175);
		table.setPreferredColumnWidth(3, 150);

		table.addStyleName("okm-DisableSelect");
		table.addStyleName("okm-Left-Bottom-Border");

		sp = new SimplePanel();
		sp.add(table);

		initWidget(sp);
	}

	/**
	 * Refreshing lang
	 */
	public void langRefresh() {
		changeHeaderText();
		menuPopup.langRefresh();
		table.langRefresh();
	}

	/**
	 * changeHeaderText
	 */
	public void changeHeaderText() {
		if (MessagingToolBarBox.get().messageDashboard.messageStack.isProposedSubscriptionReceivedVisible()) {
			headerTable.setHTML(0, 1, GeneralComunicator.i18nExtension("messaging.message.user"));
			headerTable.setHTML(0, 2, GeneralComunicator.i18nExtension("messaging.message.type"));
			headerTable.setHTML(0, 3, GeneralComunicator.i18nExtension("messaging.message.date"));
			headerTable.setHTML(0, 4, GeneralComunicator.i18nExtension("messaging.message.proposed.folder.document"));
		} else if (MessagingToolBarBox.get().messageDashboard.messageStack.isProposedQueryReceivedVisible()) {
			headerTable.setHTML(0, 1, GeneralComunicator.i18nExtension("messaging.message.user"));
			headerTable.setHTML(0, 2, GeneralComunicator.i18nExtension("messaging.message.type"));
			headerTable.setHTML(0, 3, GeneralComunicator.i18nExtension("messaging.message.date"));
			headerTable.setHTML(0, 4, GeneralComunicator.i18nExtension("messaging.message.proposed.query"));
		} else if (MessagingToolBarBox.get().messageDashboard.messageStack.isMessagesSentVisible()) {
			headerTable.setHTML(0, 1, GeneralComunicator.i18nExtension("messaging.message.send.to"));
			headerTable.setHTML(0, 2, GeneralComunicator.i18nExtension("messaging.message.type"));
			headerTable.setHTML(0, 3, GeneralComunicator.i18nExtension("messaging.message.date"));
			headerTable.setHTML(0, 4, GeneralComunicator.i18nExtension("messaging.message.subject"));
		} else if (MessagingToolBarBox.get().messageDashboard.messageStack.isMessagesReceivedVisible()) {
			headerTable.setHTML(0, 1, GeneralComunicator.i18nExtension("messaging.message.send.from"));
			headerTable.setHTML(0, 2, GeneralComunicator.i18nExtension("messaging.message.type"));
			headerTable.setHTML(0, 3, GeneralComunicator.i18nExtension("messaging.message.date"));
			headerTable.setHTML(0, 4, GeneralComunicator.i18nExtension("messaging.message.subject"));
		}
	}

	/**
	 * Removes all rows except the first
	 */
	public void removeAllRows() {
		// Purge all rows 
		while (dataTable.getRowCount() > 0) {
			dataTable.removeRow(0);
		}

		table.reset();
		table.getDataTable().resize(0, NUMBER_OF_COLUMNS);
	}

	/**
	 * Adds a message to the panel
	 *
	 * @param doc The doc to add
	 */
	public void addRow(Object obj) {
		table.addRow(obj);
	}

	/**
	 * Indicates if panel is selected
	 *
	 * @return The value of panel ( selected )
	 */
	public boolean isPanelSelected() {
		return table.isPanelSelected();
	}

	/**
	 * Sets the selected panel value
	 *
	 * @param selected The select panel value
	 */
	public void setSelectedPanel(boolean selected) {
		table.setSelectedPanel(selected);
	}

	/**
	 * Fix width
	 */
	public void fixWidth() {
		table.fillWidth();
	}

	/**
	 * deleteMessageSent
	 */
	public void deleteMessageSent() {
		if (table.isMessageSentSelected()) {
			GWTTextMessageSent messageSent = table.getMessageSent();
			status.setFlag_deleteMessageSent();
			messageService.deleteSent(messageSent.getId(), new AsyncCallback<Object>() {
				@Override
				public void onSuccess(Object result) {
					status.unsetFlag_deleteMessageSent();
					MessagingToolBarBox.get().messageDashboard.messageStack.messageSent.findAllUsersMessageSent();
				}

				@Override
				public void onFailure(Throwable caught) {
					status.unsetFlag_deleteMessageSent();
					GeneralComunicator.showError("deleteSent", caught);
				}
			});
		}
	}

	/**
	 * deleteMessageReceived
	 */
	public void deleteMessageReceived() {
		if (table.isMessageReceivedSelected()) {
			GWTMessageReceived messageReceived = table.getMessageReceived();
			status.setFlag_deleteMessageReceived();
			messageService.deleteReceived(messageReceived.getId(), new AsyncCallback<Object>() {
				@Override
				public void onSuccess(Object result) {
					status.unsetFlag_deleteMessageReceived();
					MessagingToolBarBox.get().messageDashboard.messageStack.messageReceived.findAllMessageReceived();
				}

				@Override
				public void onFailure(Throwable caught) {
					status.unsetFlag_deleteMessageReceived();
					GeneralComunicator.showError("deleteReceived", caught);
				}
			});
		}
	}

	/**
	 * deleteProposedQueryReceived
	 */
	public void deleteProposedQueryReceived() {
		if (table.isProposedQueryReceivedSelected()) {
			GWTProposedQueryReceived propose = table.getProposedQueryReceived();
			status.setFlag_deletePropose();
			proposedQueryService.deleteReceived(propose.getId(), new AsyncCallback<Object>() {
				@Override
				public void onSuccess(Object result) {
					status.unsetFlag_deletePropose();
					MessagingToolBarBox.get().messageDashboard.messageStack.proposedQueryReceived.findAllProposedQueries();
				}

				@Override
				public void onFailure(Throwable caught) {
					status.unsetFlag_deletePropose();
					GeneralComunicator.showError("deleteReceived", caught);
				}
			});
		}
	}

	/**
	 * deleteProposedQuerySent
	 */
	public void deleteProposedQuerySent() {
		if (table.isProposedQuerySentSelected()) {
			GWTProposedQuerySent propose = table.getProposedQuerySent();
			status.setFlag_deletePropose();
			proposedQueryService.deleteSent(propose.getId(), new AsyncCallback<Object>() {
				@Override
				public void onSuccess(Object result) {
					status.unsetFlag_deletePropose();
					MessagingToolBarBox.get().messageDashboard.messageStack.messageSent.findAllUsersMessageSent();
				}

				@Override
				public void onFailure(Throwable caught) {
					status.unsetFlag_deletePropose();
					GeneralComunicator.showError("deleteSent", caught);
				}
			});
		}
	}

	/**
	 * deleteProposedSubscriptionReceived
	 */
	public void deleteProposedSubscriptionReceived() {
		if (table.isProposedSubscriptionReceivedSelected()) {
			GWTProposedSubscriptionReceived propose = table.getProposedSubscriptionReceived();
			status.setFlag_deletePropose();
			proposedSubscriptionService.deleteReceived(propose.getId(), new AsyncCallback<Object>() {
				@Override
				public void onSuccess(Object result) {
					status.unsetFlag_deletePropose();
					MessagingToolBarBox.get().messageDashboard.messageStack.proposedSubscriptionReceived.findAllProposedSubscriptions();
				}

				@Override
				public void onFailure(Throwable caught) {
					status.unsetFlag_deletePropose();
					GeneralComunicator.showError("deleteReceived", caught);
				}
			});
		}
	}

	/**
	 * deleteProposedSubscriptionSent
	 */
	public void deleteProposedSubscriptionSent() {
		if (table.isProposedSubscriptionSentSelected()) {
			GWTProposedSubscriptionSent propose = table.getProposedSubscriptionSent();
			status.setFlag_deletePropose();
			proposedSubscriptionService.deleteSent(propose.getId(), new AsyncCallback<Object>() {
				@Override
				public void onSuccess(Object result) {
					status.unsetFlag_deletePropose();
					MessagingToolBarBox.get().messageDashboard.messageStack.messageSent.findAllUsersMessageSent();
				}

				@Override
				public void onFailure(Throwable caught) {
					status.unsetFlag_deletePropose();
					GeneralComunicator.showError("deleteSent", caught);
				}
			});
		}
	}
}
	
	