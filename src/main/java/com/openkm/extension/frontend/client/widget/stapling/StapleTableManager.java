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

package com.openkm.extension.frontend.client.widget.stapling;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.openkm.extension.frontend.client.service.OKMStaplingService;
import com.openkm.extension.frontend.client.service.OKMStaplingServiceAsync;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.bean.extension.GWTStaple;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.util.Util;


/**
 * StapleTableManager
 *
 * @author jllort
 *
 */
public class StapleTableManager {

	private final static OKMStaplingServiceAsync staplingService = (OKMStaplingServiceAsync) GWT.create(OKMStaplingService.class);

	/**
	 * addDocument
	 */
	public static void addDocument(FlexTable table, final GWTStaple staple, final String uuid, boolean enableDelete) {
		int row = table.getRowCount();
		final GWTDocument doc = staple.getDoc();

		if (doc.isCheckedOut()) {
			table.setHTML(row, 0, Util.imageItemHTML("img/icon/edit.png"));
		} else if (doc.isLocked()) {
			table.setHTML(row, 0, Util.imageItemHTML("img/icon/lock.gif"));
		} else {
			table.setHTML(row, 0, "&nbsp;");
		}

		// Subscribed is a special case, must add icon with others
		if (doc.isSubscribed()) {
			table.setHTML(row, 0, table.getHTML(row, 0) + Util.imageItemHTML("img/icon/subscribed.gif"));
		}

		if (doc.isHasNotes()) {
			table.setHTML(row, 0, table.getHTML(row, 0) + Util.imageItemHTML("img/icon/note.gif"));
		}

		table.setHTML(row, 1, Util.mimeImageHTML(doc.getMimeType()));
		Anchor anchor = new Anchor();
		anchor.setHTML(doc.getName());
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				String docPath = doc.getPath();
				String path = docPath.substring(0, docPath.lastIndexOf("/"));
				GeneralComunicator.openPath(path, doc.getPath());
			}
		});
		anchor.setStyleName("okm-KeyMap-ImageHover");
		table.setWidget(row, 2, anchor);
		table.setHTML(row, 3, Util.formatSize(doc.getActualVersion().getSize()));

		if (enableDelete) {
			Image delete = new Image(OKMBundleResources.INSTANCE.deleteIcon());
			delete.setStyleName("okm-KeyMap-ImageHover");
			delete.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					staplingService.removeStaple(String.valueOf(staple.getId()), new AsyncCallback<Object>() {
						@Override
						public void onSuccess(Object result) {
							if (staple.getType().equals(GWTStaple.STAPLE_FOLDER)) {
								Stapling.get().refreshFolder(uuid);
							} else if (staple.getType().equals(GWTStaple.STAPLE_DOCUMENT)) {
								Stapling.get().refreshDocument(uuid);
							} else if (staple.getType().equals(GWTStaple.STAPLE_MAIL)) {
								Stapling.get().refreshMail(uuid);
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							GeneralComunicator.showError("remove", caught);
						}
					});
				}
			});

			table.setWidget(row, 4, delete);
		} else {
			table.setHTML(row, 4, "");
		}

		table.getCellFormatter().setWidth(row, 0, "60px");
		table.getCellFormatter().setWidth(row, 1, "25px");
		table.getCellFormatter().setWidth(row, 4, "25px");

		table.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		table.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
		table.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_LEFT);
		table.getCellFormatter().setHorizontalAlignment(row, 3, HasHorizontalAlignment.ALIGN_CENTER);
		table.getCellFormatter().setHorizontalAlignment(row, 4, HasHorizontalAlignment.ALIGN_CENTER);
	}

	/**
	 * addFolder
	 */
	public static void addFolder(FlexTable table, final GWTStaple staple, final String uuid, boolean enableDelete) {
		int row = table.getRowCount();
		final GWTFolder folder = staple.getFolder();

		// Subscribed is a special case, must add icon with others
		if (folder.isSubscribed()) {
			table.setHTML(row, 0, Util.imageItemHTML("img/icon/subscribed.gif"));
		} else {
			table.setHTML(row, 0, "&nbsp;");
		}

		// Looks if must change icon on parent if now has no childs and properties with user security atention
		if ((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) {
			if (folder.isHasChildren()) {
				table.setHTML(row, 1, Util.imageItemHTML("img/menuitem_childs.gif"));
			} else {
				table.setHTML(row, 1, Util.imageItemHTML("img/menuitem_empty.gif"));
			}
		} else {
			if (folder.isHasChildren()) {
				table.setHTML(row, 1, Util.imageItemHTML("img/menuitem_childs_ro.gif"));
			} else {
				table.setHTML(row, 1, Util.imageItemHTML("img/menuitem_empty_ro.gif"));
			}
		}

		Anchor anchor = new Anchor();
		anchor.setHTML(folder.getName());
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				GeneralComunicator.openPath(folder.getPath(), null);
			}
		});
		anchor.setStyleName("okm-KeyMap-ImageHover");
		table.setWidget(row, 2, anchor);
		table.setHTML(row, 3, "&nbsp;");

		if (enableDelete) {
			Image delete = new Image(OKMBundleResources.INSTANCE.deleteIcon());
			delete.setStyleName("okm-KeyMap-ImageHover");
			delete.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					staplingService.removeStaple(String.valueOf(staple.getId()), new AsyncCallback<Object>() {
						@Override
						public void onSuccess(Object result) {
							if (staple.getType().equals(GWTStaple.STAPLE_FOLDER)) {
								Stapling.get().refreshFolder(uuid);
							} else if (staple.getType().equals(GWTStaple.STAPLE_DOCUMENT)) {
								Stapling.get().refreshDocument(uuid);
							} else if (staple.getType().equals(GWTStaple.STAPLE_MAIL)) {
								Stapling.get().refreshMail(uuid);
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							GeneralComunicator.showError("remove", caught);
						}
					});
				}
			});

			table.setWidget(row, 4, delete);
		} else {
			table.setHTML(row, 4, "");
		}

		table.getCellFormatter().setWidth(row, 0, "60px");
		table.getCellFormatter().setWidth(row, 1, "25px");
		table.getCellFormatter().setWidth(row, 4, "25px");

		table.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		table.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
		table.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_LEFT);
		table.getCellFormatter().setHorizontalAlignment(row, 3, HasHorizontalAlignment.ALIGN_CENTER);
		table.getCellFormatter().setHorizontalAlignment(row, 4, HasHorizontalAlignment.ALIGN_CENTER);
	}

	/**
	 * addMail
	 */
	public static void addMail(FlexTable table, final GWTStaple staple, final String uuid, boolean enableDelete) {
		int row = table.getRowCount();
		final GWTMail mail = staple.getMail();

		// Mail is never checkout or subscribed ( because can not be changed )
		table.setHTML(row, 0, "&nbsp;");

		if (mail.getAttachments().size() > 0) {
			table.setHTML(row, 1, Util.imageItemHTML("img/email_attach.gif"));
		} else {
			table.setHTML(row, 1, Util.imageItemHTML("img/email.gif"));
		}

		Anchor anchor = new Anchor();
		anchor.setHTML(mail.getSubject());
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				String docPath = mail.getPath();
				String path = docPath.substring(0, docPath.lastIndexOf("/"));
				GeneralComunicator.openPath(path, docPath);
			}
		});
		anchor.setStyleName("okm-KeyMap-ImageHover");
		table.setWidget(row, 2, anchor);
		table.setHTML(row, 3, Util.formatSize(mail.getSize()));


		if (enableDelete) {
			Image delete = new Image(OKMBundleResources.INSTANCE.deleteIcon());
			delete.setStyleName("okm-KeyMap-ImageHover");
			delete.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					staplingService.removeStaple(String.valueOf(staple.getId()), new AsyncCallback<Object>() {
						@Override
						public void onSuccess(Object result) {
							if (staple.getType().equals(GWTStaple.STAPLE_FOLDER)) {
								Stapling.get().refreshFolder(uuid);
							} else if (staple.getType().equals(GWTStaple.STAPLE_DOCUMENT)) {
								Stapling.get().refreshDocument(uuid);
							} else if (staple.getType().equals(GWTStaple.STAPLE_MAIL)) {
								Stapling.get().refreshMail(uuid);
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							GeneralComunicator.showError("remove", caught);
						}
					});
				}
			});

			table.setWidget(row, 4, delete);
		} else {
			table.setHTML(row, 4, "");
		}

		table.getCellFormatter().setWidth(row, 0, "60px");
		table.getCellFormatter().setWidth(row, 1, "25px");
		table.getCellFormatter().setWidth(row, 4, "25px");

		table.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		table.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
		table.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_LEFT);
		table.getCellFormatter().setHorizontalAlignment(row, 3, HasHorizontalAlignment.ALIGN_CENTER);
		table.getCellFormatter().setHorizontalAlignment(row, 4, HasHorizontalAlignment.ALIGN_CENTER);
	}
}