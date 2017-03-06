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

package com.openkm.extension.frontend.client.widget.wiki;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.*;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.extension.GWTWikiPage;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

import java.util.LinkedList;

/**
 * TabToolbarWiki
 *
 * @author jllort
 *
 */
public class TabToolbarWiki extends Composite {
	public static final String MAIN_PAGE_TITTLE = "index";

	public static final int MODE_PAGE = 0;
	public static final int MODE_PAGE_LOCKED = 1;
	public static final int MODE_EMPTY_PAGE = 2;
	public static final int MODE_CREATE = 3;
	public static final int MODE_UPDATE = 4;
	public static final int MODE_HISTORY = 5;
	public static final int MODE_SHOW_HISTORY = 6;

	private HorizontalPanel hPanel;
	private Button home;
	private Button create;
	private Button update;
	private Button delete;
	private Button history;
	private Button unlock;
	private HTML createWikiPage;
	private HTML updateWikiPage;
	private HTML lockedWikiPage;
	private HTML historyWikiPage;
	private HorizontalPanel infoPanel;
	private HTML updatedDateText;
	private HTML updatedDate;
	private HTML lockedByText;
	private HTML lockedBy;
	private HTML userText;
	private HTML user;
	private GWTWikiPage wikiPage;
	private boolean isDashboard = false;
	private LinkedList<GWTWikiPage> wikiPageHistory;
	private int index = -1; // wiki page history index
	private HorizontalPanel hHistoryPanel;
	private Image previous;
	private Image next;
	private boolean isHomePresed = false;
	private boolean updatedFlag = false;
	private HorizontalPanel verticalLine;
	private int mode = -1;

	/**
	 * TabToolbarWiki
	 *
	 * @param contoller
	 */
	public TabToolbarWiki(final WikiController controller, final boolean isDashboard) {
		this.isDashboard = isDashboard;
		wikiPageHistory = new LinkedList<GWTWikiPage>();
		hPanel = new HorizontalPanel();
		HorizontalPanel buttonsPanel = new HorizontalPanel();

		// Left Space
		HTML space = new HTML("&nbsp;");
		hPanel.add(space);

		// Navigator
		HorizontalPanel navigator = new HorizontalPanel();

		// Create
		home = new Button(GeneralComunicator.i18nExtension("button.home"));
		if (!isDashboard) {
			home.setHTML(GeneralComunicator.i18nExtension("wiki.current.page"));
		}
		home.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (isDashboard) {
					isHomePresed = true;
					controller.findWikiPageByTitle(MAIN_PAGE_TITTLE);
				} else {
					controller.showWikiPage(wikiPage);
				}
			}
		});
		home.setStyleName("okm-HomeButton");
		navigator.add(home);
		navigator.add(new HTML("&nbsp;"));
		navigator.setCellVerticalAlignment(home, HasAlignment.ALIGN_MIDDLE);

		hHistoryPanel = new HorizontalPanel();
		previous = new Image(OKMBundleResources.INSTANCE.previousDisabled());
		previous.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (index > 0 && !wikiPageHistory.isEmpty()) {
					switch (mode) {
						case MODE_PAGE:
						case MODE_PAGE_LOCKED:
						case MODE_EMPTY_PAGE:
						case MODE_CREATE:
							index--;

						case MODE_UPDATE:
						case MODE_HISTORY:
						case MODE_SHOW_HISTORY:
							// Go to last viewed ( actual index i right ). 
							break;
					}
					controller.showWikiPage(wikiPageHistory.get(index));
				}
			}
		});
		previous.setStyleName("okm-Hyperlink");
		next = new Image(OKMBundleResources.INSTANCE.nextDisabled());
		next.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (wikiPageHistory.size() - 1 > index) {
					index++;
					controller.showWikiPage(wikiPageHistory.get(index));
				}
			}
		});
		next.setStyleName("okm-Hyperlink");
		hHistoryPanel.add(new HTML("&nbsp;"));
		hHistoryPanel.add(previous);
		hHistoryPanel.add(new HTML("&nbsp;"));
		hHistoryPanel.add(next);
		hHistoryPanel.add(new HTML("&nbsp;"));
		hHistoryPanel.setCellVerticalAlignment(previous, HasAlignment.ALIGN_MIDDLE);
		hHistoryPanel.setCellVerticalAlignment(next, HasAlignment.ALIGN_MIDDLE);
		navigator.add(hHistoryPanel);
		navigator.setCellVerticalAlignment(hHistoryPanel, HasAlignment.ALIGN_MIDDLE);

		// Vertical line
		verticalLine = new HorizontalPanel();
		HTML vertical = new HTML("&nbsp;");
		vertical.setHeight("24px");
		vertical.setStyleName("okm-Border-Right");
		verticalLine.add(vertical);
		navigator.add(verticalLine);

		// adding navigator
		buttonsPanel.add(navigator);
		buttonsPanel.setCellVerticalAlignment(navigator, HasAlignment.ALIGN_MIDDLE);

		// Create
		create = new Button(GeneralComunicator.i18n("button.create"));
		create.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.showCreateWikiPage();
			}
		});
		create.setStyleName("okm-AddButton");
		buttonsPanel.add(create);
		buttonsPanel.add(new HTML("&nbsp;"));
		buttonsPanel.setCellVerticalAlignment(create, HasAlignment.ALIGN_MIDDLE);

		// Edit
		update = new Button(GeneralComunicator.i18n("button.update"));
		update.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (wikiPage.getLockUser() == null || wikiPage.getLockUser().equals("")) {
					controller.lockWikiPage(wikiPage);
					wikiPage.setLockUser(GeneralComunicator.getUser());
				} else if (!wikiPage.getLockUser().equals(GeneralComunicator.getUser())) {
					// Case that never might happens
					GeneralComunicator.showError(GeneralComunicator.i18nExtension("wiki.error.lock"), new Throwable("lock()"));
				} else {
					controller.showUpdateWikiPage(wikiPage);
				}
			}
		});
		update.setStyleName("okm-ChangeButton");
		buttonsPanel.add(update);
		buttonsPanel.add(new HTML("&nbsp;"));
		buttonsPanel.setCellVerticalAlignment(update, HasAlignment.ALIGN_MIDDLE);

		// history
		history = new Button(GeneralComunicator.i18nExtension("button.history"));
		history.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.historyWikiPage(wikiPage.getTitle());
			}
		});
		history.setStyleName("okm-ViewButton");
		buttonsPanel.add(history);
		buttonsPanel.add(new HTML("&nbsp;"));
		buttonsPanel.setCellVerticalAlignment(history, HasAlignment.ALIGN_MIDDLE);

		// Delete
		delete = new Button(GeneralComunicator.i18n("button.delete"));
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Wiki.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_WIKI_PAGE, wikiPage, controller);
				Wiki.get().confirmPopup.center();
			}
		});
		delete.setStyleName("okm-DeleteButton");
		buttonsPanel.add(delete);
		buttonsPanel.add(new HTML("&nbsp;"));
		buttonsPanel.setCellVerticalAlignment(delete, HasAlignment.ALIGN_MIDDLE);

		// unlock
		unlock = new Button(GeneralComunicator.i18nExtension("button.unlock"));
		unlock.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.unlockWikiPage(wikiPage);
				wikiPage.setLockUser("");
				controller.showWikiPage(wikiPage);
			}
		});
		unlock.setStyleName("okm-ChangeButton");
		buttonsPanel.add(unlock);
		buttonsPanel.add(new HTML("&nbsp;"));
		buttonsPanel.setCellVerticalAlignment(unlock, HasAlignment.ALIGN_MIDDLE);

		// Action
		HorizontalPanel actions = new HorizontalPanel();
		createWikiPage = new HTML(GeneralComunicator.i18nExtension("wiki.create.page").toUpperCase());
		updateWikiPage = new HTML(GeneralComunicator.i18nExtension("wiki.edit.page").toUpperCase());
		lockedWikiPage = new HTML(GeneralComunicator.i18nExtension("wiki.locked.page").toUpperCase());
		historyWikiPage = new HTML(GeneralComunicator.i18nExtension("wiki.history.page").toUpperCase());
		createWikiPage.setStyleName("okm-Forum-Topic");
		updateWikiPage.setStyleName("okm-Forum-Topic");
		lockedWikiPage.setStyleName("okm-Forum-Topic");
		historyWikiPage.setStyleName("okm-Forum-Topic");
		actions.add(createWikiPage);
		actions.add(updateWikiPage);
		actions.add(lockedWikiPage);
		actions.add(historyWikiPage);
		buttonsPanel.add(actions);

		// Vertical line
		HorizontalPanel verticalLine2 = new HorizontalPanel();
		HTML vertical2 = new HTML("&nbsp;");
		vertical2.setHeight("24px");
		vertical2.setStyleName("okm-Border-Right");
		verticalLine2.add(vertical2);
		buttonsPanel.add(verticalLine2);

		// Adding buttons panel
		hPanel.add(buttonsPanel);

		// Info panel
		infoPanel = new HorizontalPanel();
		lockedByText = new HTML("<b>" + GeneralComunicator.i18nExtension("wiki.page.locked.by") + "</b>");
		updatedDateText = new HTML("<b>" + GeneralComunicator.i18nExtension("wiki.page.date") + "</b>");
		userText = new HTML("<b>" + GeneralComunicator.i18nExtension("wiki.page.user") + "</b>");
		lockedBy = new HTML("");
		lockedBy.setStyleName("okm-Input-Error");
		updatedDate = new HTML("");
		user = new HTML("");
		infoPanel.add(lockedByText);
		infoPanel.add(new HTML("&nbsp;"));
		infoPanel.add(lockedBy);
		infoPanel.add(new HTML("&nbsp;&nbsp;&nbsp;"));
		infoPanel.add(updatedDateText);
		infoPanel.add(new HTML("&nbsp;"));
		infoPanel.add(updatedDate);
		infoPanel.add(new HTML("&nbsp;&nbsp;"));
		infoPanel.add(userText);
		infoPanel.add(new HTML("&nbsp;"));
		infoPanel.add(user);
		infoPanel.add(new HTML("&nbsp;&nbsp;"));
		hPanel.add(infoPanel);

		buttonsPanel.setCellVerticalAlignment(actions, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellHorizontalAlignment(buttonsPanel, HasAlignment.ALIGN_LEFT);
		hPanel.setCellVerticalAlignment(buttonsPanel, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellVerticalAlignment(infoPanel, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellHorizontalAlignment(infoPanel, HasAlignment.ALIGN_RIGHT);
		hPanel.setCellWidth(space, "5px");

		hPanel.setStyleName("okm-Mail");
		hPanel.addStyleName("okm-Border-Bottom");

		// First time must be in topic mode
		switchViewMode(MODE_EMPTY_PAGE);

		initWidget(hPanel);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		unlock.setHTML(GeneralComunicator.i18n("button.unlock"));
		if (!isDashboard) {
			home.setHTML(GeneralComunicator.i18nExtension("wiki.current.page"));
		} else {
			home.setHTML(GeneralComunicator.i18nExtension("button.home"));
		}
		create.setHTML(GeneralComunicator.i18n("button.create"));
		update.setHTML(GeneralComunicator.i18n("button.update"));
		history.setHTML(GeneralComunicator.i18nExtension("button.history"));
		delete.setHTML(GeneralComunicator.i18n("button.delete"));
		lockedWikiPage.setHTML(GeneralComunicator.i18nExtension("wiki.locked.page").toUpperCase());
		lockedByText.setHTML("<b>" + GeneralComunicator.i18nExtension("wiki.page.locked.by") + "</b>");
		updatedDateText.setHTML("<b>" + GeneralComunicator.i18nExtension("wiki.page.date") + "</b>");
		userText.setHTML("<b>" + GeneralComunicator.i18nExtension("wiki.page.user") + "</b>");
	}

	/**
	 * setUpdatedFlag
	 */
	public void setUpdatedFlag() {
		updatedFlag = true;
	}

	/**
	 * setHistoryWikiPage
	 *
	 * @param wikiPage
	 */
	public void setHistoryWikiPage(GWTWikiPage wikiPage) {
		// Preserve return wikiPage used by home buttom ( wikiPage must not be changed ).  
		GWTWikiPage latestWikiPage = this.wikiPage.clone();
		setWikiPage(wikiPage);
		this.wikiPage = latestWikiPage;
	}

	/**
	 * setWikiPage
	 *
	 * @param wikiPage
	 */
	public void setWikiPage(GWTWikiPage wikiPage) {
		this.wikiPage = wikiPage;
		if (wikiPage != null) {
			// Evaluating lock
			if (wikiPage.getLockUser() != null && !wikiPage.getLockUser().equals("")) {
				lockedByText.setVisible(true);
				lockedBy.setVisible(true);
				lockedBy.setHTML(wikiPage.getLockUser());
			} else {
				lockedByText.setVisible(false);
				lockedBy.setVisible(false);
			}
			// Evaluating last modified
			if (wikiPage.getDate() != null) {
				updatedDateText.setVisible(true);
				updatedDate.setVisible(true);
				DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
				updatedDate.setHTML(dtf.format(wikiPage.getDate()));
			} else {
				updatedDateText.setVisible(false);
				updatedDate.setVisible(false);
			}
			// Evaluating writer ( user )
			if (wikiPage.getUser() != null && !wikiPage.getUser().equals("")) {
				userText.setVisible(true);
				user.setVisible(true);
				user.setText(wikiPage.getUser());
			} else {
				userText.setVisible(false);
				user.setVisible(false);
			}
		} else {
			updatedDateText.setVisible(false);
			updatedDate.setVisible(false);
			lockedByText.setVisible(false);
			lockedBy.setVisible(false);
			userText.setVisible(false);
			user.setVisible(false);
		}
	}

	/**
	 * @param mode
	 */
	public void switchViewMode(int mode) {
		this.mode = mode;
		boolean lock = false;

		// Show or hide unlock
		if (wikiPage != null && wikiPage.getLockUser() != null && wikiPage.getLockUser().equals(GeneralComunicator.getUser())) {
			lock = true;
		}

		// Show or hide navigator
		hHistoryPanel.setVisible(isDashboard);

		switch (mode) {
			case MODE_PAGE:
				if (isDashboard) {
					home.setVisible(!wikiPage.getTitle().equals(MAIN_PAGE_TITTLE));
				} else {
					home.setVisible(true);
				}
				verticalLine.setVisible(true);
				create.setVisible(false);
				update.setVisible(true);
				delete.setVisible(true);
				history.setVisible(true);
				unlock.setVisible(lock);
				createWikiPage.setVisible(false);
				updateWikiPage.setVisible(false);
				lockedWikiPage.setVisible(false);
				historyWikiPage.setVisible(false);
				break;

			case MODE_PAGE_LOCKED:
				home.setVisible(false);
				verticalLine.setVisible(isDashboard);
				create.setVisible(false);
				update.setVisible(false);
				delete.setVisible(false);
				history.setVisible(true);
				unlock.setVisible(lock);
				createWikiPage.setVisible(false);
				updateWikiPage.setVisible(false);
				lockedWikiPage.setVisible(true);
				historyWikiPage.setVisible(false);
				break;

			case MODE_EMPTY_PAGE:
				home.setVisible(false);
				verticalLine.setVisible(isDashboard);
				create.setVisible(true);
				update.setVisible(false);
				delete.setVisible(false);
				history.setVisible(false);
				unlock.setVisible(false);
				createWikiPage.setVisible(false);
				updateWikiPage.setVisible(false);
				lockedWikiPage.setVisible(false);
				historyWikiPage.setVisible(false);
				break;

			case MODE_CREATE:
				home.setVisible(true);
				verticalLine.setVisible(true);
				create.setVisible(false);
				update.setVisible(false);
				delete.setVisible(false);
				history.setVisible(false);
				unlock.setVisible(false);
				createWikiPage.setVisible(true);
				updateWikiPage.setVisible(false);
				lockedWikiPage.setVisible(false);
				historyWikiPage.setVisible(false);
				break;

			case MODE_UPDATE:
				home.setVisible(true);
				verticalLine.setVisible(true);
				create.setVisible(false);
				update.setVisible(false);
				delete.setVisible(false);
				history.setVisible(false);
				unlock.setVisible(false);
				createWikiPage.setVisible(false);
				updateWikiPage.setVisible(true);
				lockedWikiPage.setVisible(false);
				historyWikiPage.setVisible(false);
				break;

			case MODE_HISTORY:
			case MODE_SHOW_HISTORY:
				home.setVisible(true);
				verticalLine.setVisible(true);
				create.setVisible(false);
				update.setVisible(false);
				delete.setVisible(false);
				history.setVisible(false);
				unlock.setVisible(false);
				createWikiPage.setVisible(false);
				updateWikiPage.setVisible(false);
				lockedWikiPage.setVisible(false);
				historyWikiPage.setVisible(true);
				break;
		}

		// Main wiki page can not be deleted 
		if (wikiPage != null && wikiPage.getTitle().equals(MAIN_PAGE_TITTLE)) {
			delete.setVisible(false);
		}

		// store to history
		if (isDashboard) {
			if (wikiPage != null) {
				// Reset navigator list when go home
				if (isHomePresed) {
					index = -1;
					wikiPageHistory = new LinkedList<GWTWikiPage>();
				}

				switch (mode) {
					case MODE_PAGE:
					case MODE_PAGE_LOCKED:
						if (wikiPageHistory.isEmpty()) {
							wikiPageHistory.add(wikiPage);
							index = 0;
						} else {
							if (updatedFlag) {
								// must change actual index wiki page for newer ( updated or restored )
								wikiPageHistory.remove(index);
								wikiPageHistory.add(index, wikiPage);
								// if is other wiki page must add it on history list
							} else if (!wikiPageHistory.get(index).getTitle().equals(wikiPage.getTitle())) {
								while (wikiPageHistory.size() - 1 > index) {
									wikiPageHistory.remove(index + 1);
								}
								wikiPageHistory.add(wikiPage);
								index++;
							}
						}
						break;
					case MODE_EMPTY_PAGE:
					case MODE_CREATE:
					case MODE_UPDATE:
					case MODE_HISTORY:
					case MODE_SHOW_HISTORY:
						// Nothing to do here
						break;
				}
			}
			// Evaluate images previous / next
			if (!wikiPageHistory.isEmpty()) {
				// evaluate previous
				if (index > 0 && wikiPageHistory.size() > 1) {
					previous.setResource(OKMBundleResources.INSTANCE.previous());
				} else {
					previous.setResource(OKMBundleResources.INSTANCE.previousDisabled());
				}
				// evaluate next
				if (wikiPageHistory.size() - 1 > index) {
					next.setResource(OKMBundleResources.INSTANCE.next());
				} else {
					next.setResource(OKMBundleResources.INSTANCE.nextDisabled());
				}
			}
		}

		// reseting some flags
		isHomePresed = false;    // button home pressed
		updatedFlag = false;    // Reset updated flag ( indicates wikipage has been updated or restored )
	}

	/**
	 * delete
	 *
	 * @return
	 */
	public GWTWikiPage refreshAfterDeleted() {
		if (isDashboard && wikiPageHistory.size() > 0) {
			// Case never might happens
			if (index > 0) {
				// Must ensure all references to actual wiki page are deleted in navigation 
				// we could be passed several times on same navigation process to same wiki page
				// must return to first location
				GWTWikiPage wikiPage = wikiPageHistory.get(index); // Actual wiki page
				for (int i = 0; i < wikiPageHistory.size(); i++) {
					if (wikiPageHistory.get(i).getTitle().equals(wikiPage.getTitle())) {
						index = i;
						break;
					}
				}
				while (wikiPageHistory.size() - 1 >= index) {
					wikiPageHistory.remove(index);
				}
				index--;
			}
			return wikiPageHistory.get(index);
		} else {
			return null;
		}
	}
}