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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.extension.frontend.client.service.OKMWikiService;
import com.openkm.extension.frontend.client.service.OKMWikiServiceAsync;
import com.openkm.frontend.client.bean.extension.GWTWikiPage;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.util.Util;

import java.util.List;

/**
 * WikiManager
 *
 * @author jllort
 *
 */
public class WikiManager extends Composite implements WikiController {
	private final OKMWikiServiceAsync wikiService = (OKMWikiServiceAsync) GWT.create(OKMWikiService.class);
	private final static int IE_SIZE_RECTIFICATION = (Util.getUserAgent().startsWith("ie") ? 1 : 0);

	private VerticalPanel vPanel;
	private TabToolbarWiki toolbar;
	private GWTWikiPage gWTwikiPage;
	private WikiPage wikiPage;

	// wiki panels
	private ScrollPanel scrollPanelWiki;
	private VerticalPanel vWikiPanel;
	private WikiEditor wikiEditor;
	private WikiHistory wikiHistory;
	private boolean isDashboard = false;

	// Toolbar height
	public static final int TOOLBAR_HEADER = 25;

	/**
	 * WikiManager
	 */
	public WikiManager(boolean isDashboard) {
		this.isDashboard = isDashboard;
		vPanel = new VerticalPanel();
		toolbar = new TabToolbarWiki(this, isDashboard);

		// Post Editor
		vWikiPanel = new VerticalPanel();
		vWikiPanel.setWidth("100%");
		wikiEditor = new WikiEditor(this);
		wikiEditor.setStyleName("okm-Mail");
		wikiHistory = new WikiHistory(this);
		wikiPage = new WikiPage();
		scrollPanelWiki = new ScrollPanel(vWikiPanel);

		vPanel.add(toolbar); // Always visible

		toolbar.setHeight("" + TOOLBAR_HEADER + "px");
		toolbar.setWidth("100%");
		vPanel.setCellHeight(toolbar, "" + TOOLBAR_HEADER + "px");

		initWidget(vPanel);
	}

	/**
	 * addDocumentTag
	 *
	 * @param uuid
	 * @param docName
	 */
	public void addDocumentTag(String uuid, String docName) {
		wikiEditor.addDocumentTag(uuid, docName);
	}

	/**
	 * addDocumentTag
	 */
	public void addImageTag(String url, String params) {
		wikiEditor.addImageTag(url, params);
	}

	/**
	 * addFolderTag
	 *
	 * @param uuid
	 * @param fldName
	 */
	public void addFolderTag(String uuid, String fldName) {
		wikiEditor.addFolderTag(uuid, fldName);
	}

	/**
	 * addWigiTag
	 *
	 * @param wikiTitle
	 */
	public void addWigiTag(String wikiTitle) {
		wikiEditor.addWigiTag(wikiTitle);
	}

	@Override
	public void showWikiPage(GWTWikiPage wikiPage) {
		this.gWTwikiPage = wikiPage;
		resetVPanel();
		toolbar.setWikiPage(wikiPage);
		if (wikiPage != null) {
			toolbar.switchViewMode(TabToolbarWiki.MODE_PAGE);
		} else {
			toolbar.switchViewMode(TabToolbarWiki.MODE_EMPTY_PAGE);
		}
		this.wikiPage.setContent(wikiPage);
		vPanel.add(scrollPanelWiki);
		resetWikiPanel();
		vWikiPanel.add(this.wikiPage);
		wikiEditor.reset();
		scrollPanelWiki.setStyleName("gwt-ScrollTable");
		vWikiPanel.setStyleName("gwt-ScrollTable");
	}

	@Override
	public void cancelWikiPageCreation() {
		showWikiPage(gWTwikiPage);
	}

	@Override
	public void newWikiPageCreated(GWTWikiPage wikiPage) {
		this.gWTwikiPage = wikiPage;
		resetVPanel();
		toolbar.setWikiPage(wikiPage);
		toolbar.switchViewMode(TabToolbarWiki.MODE_PAGE);
		this.wikiPage.setContent(wikiPage);
		vPanel.add(scrollPanelWiki);
		resetWikiPanel();
		vWikiPanel.add(this.wikiPage);
		wikiEditor.reset();
		scrollPanelWiki.setStyleName("gwt-ScrollTable");
		vWikiPanel.setStyleName("gwt-ScrollTable");
	}

	@Override
	public void showUpdateWikiPage(GWTWikiPage wikiPage) {
		this.gWTwikiPage = wikiPage;
		resetVPanel();
		toolbar.setWikiPage(gWTwikiPage);
		toolbar.switchViewMode(TabToolbarWiki.MODE_UPDATE);
		vPanel.add(scrollPanelWiki);
		resetWikiPanel();
		vWikiPanel.add(wikiEditor);
		wikiEditor.reset();
		wikiEditor.setAction(WikiEditor.UPDATE);
		wikiEditor.setWikiPage(gWTwikiPage);
		scrollPanelWiki.setStyleName("okm-Mail");
		vWikiPanel.setStyleName("okm-Mail");
	}

	@Override
	public void lockWikiPage(GWTWikiPage wikiPage) {
		this.gWTwikiPage = wikiPage;
		Wiki.get().status.setLockWikiPage();
		wikiService.lock(wikiPage, new AsyncCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				gWTwikiPage.setLockUser(GeneralComunicator.getUser());
				showUpdateWikiPage(gWTwikiPage);
				Wiki.get().status.unsetLockWikiPage();
			}

			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("lock", caught);
				Wiki.get().status.unsetLockWikiPage();
			}
		});
	}

	@Override
	public void showCreateWikiPage() {
		resetVPanel();
		toolbar.setWikiPage(gWTwikiPage);
		toolbar.switchViewMode(TabToolbarWiki.MODE_CREATE);
		vPanel.add(scrollPanelWiki);
		resetWikiPanel();
		vWikiPanel.add(wikiEditor);
		wikiEditor.reset();
		wikiEditor.setAction(WikiEditor.CREATE);
		wikiEditor.setWikiPage(null);
		scrollPanelWiki.setStyleName("okm-Mail");
		vWikiPanel.setStyleName("okm-Mail");
	}

	@Override
	public void historyWikiPage(String title) {
		resetVPanel();
		toolbar.setWikiPage(gWTwikiPage);
		toolbar.switchViewMode(TabToolbarWiki.MODE_HISTORY);
		vPanel.add(scrollPanelWiki);
		resetWikiPanel();
		wikiHistory.reset();
		vWikiPanel.add(wikiHistory);
		scrollPanelWiki.setStyleName("gwt-ScrollTable");
		vWikiPanel.setStyleName("gwt-ScrollTable");
		Wiki.get().status.setGetHistoryWikiPage();
		wikiService.findAllHistoricByTitle(title, new AsyncCallback<List<GWTWikiPage>>() {
			@Override
			public void onSuccess(List<GWTWikiPage> result) {
				wikiHistory.showHistory(result, (gWTwikiPage.getLockUser() != null && !gWTwikiPage.getLockUser().equals("")));
				Wiki.get().status.unsetGetHistoryWikiPage();
			}

			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("findAllByTitle", caught);
				Wiki.get().status.unsetGetHistoryWikiPage();
			}
		});
	}

	@Override
	public void restoreWikiPage(final GWTWikiPage wikiPage) {
		Wiki.get().status.setLockWikiPage();
		wikiService.lock(this.gWTwikiPage, new AsyncCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				gWTwikiPage.setLockUser(GeneralComunicator.getUser());
				Wiki.get().status.setRestoreWikiPage();
				Wiki.get().status.unsetLockWikiPage();
				wikiService.restoreWikiPage(wikiPage, new AsyncCallback<GWTWikiPage>() {
					@Override
					public void onSuccess(GWTWikiPage result) {
						toolbar.setUpdatedFlag(); // indicates wikiPage has been updated
						showWikiPage(result);
						Wiki.get().status.unsetRestoreWikiPage();
					}

					@Override
					public void onFailure(Throwable caught) {
						GeneralComunicator.showError("restoreWikiPage", caught);
						Wiki.get().status.unsetRestoreWikiPage();
					}
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("lock", caught);
				Wiki.get().status.unsetLockWikiPage();
			}
		});
	}

	@Override
	public void showHistoryWikiPageVersion(GWTWikiPage wikiPage) {
		resetVPanel();
		toolbar.setHistoryWikiPage(wikiPage);
		toolbar.switchViewMode(TabToolbarWiki.MODE_SHOW_HISTORY);
		this.wikiPage.setContent(wikiPage);
		vPanel.add(scrollPanelWiki);
		resetWikiPanel();
		vWikiPanel.add(this.wikiPage);
		wikiEditor.reset();
		scrollPanelWiki.setStyleName("gwt-ScrollTable");
		vWikiPanel.setStyleName("gwt-ScrollTable");
	}

	@Override
	public void updateWikiPage(GWTWikiPage wikiPage) {
		Wiki.get().status.setUpdateWikiPage();
		wikiService.updateWikiPage(wikiPage, new AsyncCallback<GWTWikiPage>() {
			@Override
			public void onSuccess(GWTWikiPage result) {
				toolbar.setUpdatedFlag(); // indicates wikiPage has been updated
				showWikiPage(result);
				Wiki.get().status.unsetUpdateWikiPage();
			}

			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("updateWikiPage", caught);
				Wiki.get().status.unsetUpdateWikiPage();
			}
		});
	}

	@Override
	public void deleteWikiPage(GWTWikiPage wikiPage) {
		if (wikiPage.getLockUser() == null || wikiPage.getLockUser().equals("")) {
			wikiService.deleteWikiPage(wikiPage, new AsyncCallback<Object>() {
				@Override
				public void onSuccess(Object result) {
					if (isDashboard) {
						showWikiPage(toolbar.refreshAfterDeleted());
					} else {
						showWikiPage(null);
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					GeneralComunicator.showError("deleteWikiPage", caught);
				}
			});
		}
	}

	@Override
	public void createNewWikiPage(GWTWikiPage wikiPage) {
		Wiki.get().status.setCreateWikiPage();
		wikiService.createNewWikiPage(wikiPage, new AsyncCallback<GWTWikiPage>() {
			@Override
			public void onSuccess(GWTWikiPage result) {
				newWikiPageCreated(result);
				Wiki.get().status.unsetCreateWikiPage();
			}

			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("create", caught);
				Wiki.get().status.unsetCreateWikiPage();
			}
		});
	}

	@Override
	public void unlockWikiPage(final GWTWikiPage wikiPage) {
		Wiki.get().status.setLockWikiPage();
		wikiService.unlock(wikiPage, new AsyncCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				wikiPage.setLockUser("");
				showWikiPage(wikiPage);
				Wiki.get().status.unsetLockWikiPage();
			}

			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("unlock", caught);
				Wiki.get().status.unsetLockWikiPage();
			}
		});
	}

	@Override
	public void findWikiPageByTitle(final String title) {
		Wiki.get().status.setGetWikiPage();
		wikiService.findLatestByTitle(title, new AsyncCallback<GWTWikiPage>() {
			@Override
			public void onSuccess(GWTWikiPage result) {
				// Wiki page are not created by default
				if (result == null) {
					GWTWikiPage wikiPage = new GWTWikiPage();
					wikiPage.setTitle(title);
					wikiPage.setUser(GeneralComunicator.getUser());
					showCreateWikiPage();
					toolbar.setWikiPage(null);
					wikiEditor.setWikiPage(wikiPage);
				} else {
					showWikiPage(result);
				}
				Wiki.get().status.unsetGetWikiPage();
			}

			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("findWikiPageByTitle", caught);
				Wiki.get().status.unsetGetWikiPage();
			}
		});
	}

	@Override
	public void findWikiPageByNode(String uuid) {
		Wiki.get().status.setGetWikiPage();
		wikiService.findLatestByNode(uuid, new AsyncCallback<GWTWikiPage>() {
			@Override
			public void onSuccess(GWTWikiPage result) {
				showWikiPage(result);
				Wiki.get().status.unsetGetWikiPage();
			}

			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("findLatestByNode", caught);
				Wiki.get().status.unsetGetWikiPage();
			}
		});
	}

	/**
	 * resetWikiPanel
	 */
	private void resetWikiPanel() {
		while (vWikiPanel.getWidgetCount() > 0) {
			vWikiPanel.remove(0);
		}
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		toolbar.langRefresh();
		wikiHistory.langRefresh();
		wikiEditor.langRefresh();
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.UIObject#setPixelSize(int, int)
	 */
	public void setPixelSize(int width, int height) {
		vPanel.setPixelSize(width, height);
		scrollPanelWiki.setPixelSize(width - IE_SIZE_RECTIFICATION, height - (TOOLBAR_HEADER + IE_SIZE_RECTIFICATION));
		wikiEditor.setEditorSize(width - IE_SIZE_RECTIFICATION);
	}

	/**
	 * resetVPanel
	 */
	private void resetVPanel() {
		while (vPanel.getWidgetCount() > 1) {
			vPanel.remove(1);
		}
	}
}