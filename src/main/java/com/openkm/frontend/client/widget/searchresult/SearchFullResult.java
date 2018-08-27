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

package com.openkm.frontend.client.widget.searchresult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.*;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.service.OKMPropertyGroupService;
import com.openkm.frontend.client.service.OKMPropertyGroupServiceAsync;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.WidgetUtil;
import com.openkm.frontend.client.widget.dashboard.keymap.TagCloud;
import com.openkm.frontend.client.widget.form.FormManager;
import com.openkm.frontend.client.widget.searchin.SearchControl;

import java.util.*;

/**
 * SearchFullResult
 *
 * @author jllort
 *
 */
public class SearchFullResult extends Composite {
	private final OKMPropertyGroupServiceAsync propertyGroupService = GWT.create(OKMPropertyGroupService.class);

	private ScrollPanel scrollPanel;
	private FlexTable table;
	private FormManager formManager;

	// Columns
	private GWTProfileFileBrowser profileFileBrowser;

	/**
	 * SearchFullResult
	 */
	public SearchFullResult() {
		formManager = new FormManager(null); // Used to draw extended columns
		table = new FlexTable();
		scrollPanel = new ScrollPanel(table);
		scrollPanel.setStyleName("okm-Input");

		initWidget(scrollPanel);
	}

	/* (non-Javadoc)
	* @see com.google.gwt.user.client.ui.UIObject#setPixelSize(int, int)
	*/
	public void setPixelSize(int width, int height) {
		table.setWidth("100%");
		scrollPanel.setPixelSize(width, height);
	}

	/**
	 * Adds a document to the panel
	 *
	 * @param doc The doc to add
	 */
	public void addRow(GWTQueryResult gwtQueryResult) {
		if (gwtQueryResult.getDocument() != null || gwtQueryResult.getAttachment() != null) {
			addDocumentRow(gwtQueryResult, new Score(gwtQueryResult.getScore()));
		} else if (gwtQueryResult.getFolder() != null) {
			addFolderRow(gwtQueryResult, new Score(gwtQueryResult.getScore()));
		} else if (gwtQueryResult.getMail() != null) {
			addMailRow(gwtQueryResult, new Score(gwtQueryResult.getScore()));
		}
	}

	/**
	 * Adding document row
	 *
	 * @param gwtQueryResult Query result
	 * @param score Document score
	 */
	private void addDocumentRow(GWTQueryResult gwtQueryResult, Score score) {
		int rows = table.getRowCount();
		final GWTDocument doc;

		if (gwtQueryResult.getDocument() != null) {
			doc = gwtQueryResult.getDocument();
		} else if (gwtQueryResult.getAttachment() != null) {
			doc = gwtQueryResult.getAttachment();
		} else {
			doc = new GWTDocument();
		}

		// Document row
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setStyleName("okm-NoWrap");
		hPanel.add(new HTML(score.getHTML()));
		hPanel.add(Util.hSpace("5px"));

		hPanel.add(new HTML(Util.mimeImageHTML(doc.getMimeType())));
		hPanel.add(Util.hSpace("5px"));
		Anchor anchor = new Anchor();
		anchor.setHTML(doc.getName());
		anchor.setStyleName("okm-Hyperlink");
		String path = "";

		// On attachment case must remove last folder path, because it's internal usage not for visualization
		if (doc.isAttachment()) {
			anchor.setTitle(Util.getParent(doc.getParentPath()));
			path = doc.getParentPath(); // path will contains mail path
		} else {
			anchor.setTitle(doc.getParentPath());
			path = doc.getPath();
		}

		final String docPath = path;
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CommonUI.openPath(Util.getParent(docPath), docPath);
			}
		});

		hPanel.add(anchor);
		hPanel.add(Util.hSpace("5px"));
		hPanel.add(new HTML(doc.getActualVersion().getName()));
		hPanel.add(Util.hSpace("5px"));

		// Search similar documents
		if (Main.get().workspaceUserProperties.getWorkspace().getAvailableOption().isSimilarDocumentVisible()) {
			final String uuid = doc.getUuid();
			Image findSimilarDocument = new Image(OKMBundleResources.INSTANCE.findSimilarDocument());
			findSimilarDocument.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Main.get().findSimilarDocumentSelectPopup.show();
					Main.get().findSimilarDocumentSelectPopup.find(uuid);
				}
			});

			findSimilarDocument.setTitle(Main.i18n("general.menu.file.find.similar.document"));
			findSimilarDocument.setStyleName("okm-KeyMap-ImageHover");
			hPanel.add(findSimilarDocument);
			hPanel.add(Util.hSpace("5px"));
		}

		// Download
		if (Main.get().workspaceUserProperties.getWorkspace().getAvailableOption().isDownloadOption()) {
			Image downloadDocument = new Image(OKMBundleResources.INSTANCE.download());
			downloadDocument.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Util.downloadFileByUUID(doc.getUuid(), "");
				}
			});

			downloadDocument.setTitle(Main.i18n("general.menu.file.download.document"));
			downloadDocument.setStyleName("okm-KeyMap-ImageHover");
			hPanel.add(downloadDocument);
		}

		table.setWidget(rows++, 0, hPanel);

		// Excerpt row
		if ((Main.get().mainPanel.search.searchBrowser.searchIn.searchControl.getSearchMode() == SearchControl.SEARCH_MODE_SIMPLE ||
				!Main.get().mainPanel.search.searchBrowser.searchIn.searchNormal.content.getText().equals("")) &&
				gwtQueryResult.getExcerpt() != null) {
			table.setHTML(rows++, 0, "" + gwtQueryResult.getExcerpt() + (gwtQueryResult.getExcerpt().length() > 256 ? " ..." : ""));
			HTML space = new HTML();
			table.setWidget(rows, 0, space);
			table.getFlexCellFormatter().setHeight(rows++, 0, "5px");
		}

		// Folder row
		HorizontalPanel hPanel2 = new HorizontalPanel();
		hPanel2.setStyleName("okm-NoWrap");
		hPanel2.add(new HTML("<b>" + Main.i18n("document.folder") + ":</b>&nbsp;"));
		if (doc.isAttachment()) {
			String convertedPath = doc.getParentPath();
			convertedPath = Util.getParent(convertedPath) + "/" + Util.getName(convertedPath).substring(37);
			hPanel2.add(drawMailWithAttachment(convertedPath, path));
		} else {
			hPanel2.add(drawFolder(doc.getParentPath()));
		}
		table.setWidget(rows++, 0, hPanel2);

		// Document detail
		HorizontalPanel hPanel4 = new HorizontalPanel();
		hPanel4.setStyleName("okm-NoWrap");
		hPanel4.add(new HTML("<b>" + Main.i18n("search.result.author") + ":</b>&nbsp;"));
		hPanel4.add(new HTML(doc.getActualVersion().getUser().getUsername()));
		hPanel4.add(Util.hSpace("33px"));
		hPanel4.add(new HTML("<b>" + Main.i18n("search.result.size") + ":</b>&nbsp;"));
		hPanel4.add(new HTML(Util.formatSize(doc.getActualVersion().getSize())));
		hPanel4.add(Util.hSpace("33px"));
		hPanel4.add(new HTML("<b>" + Main.i18n("search.result.version") + ":</b>&nbsp;"));
		hPanel4.add(new HTML(doc.getActualVersion().getName()));
		hPanel4.add(Util.hSpace("33px"));
		hPanel4.add(new HTML("<b>" + Main.i18n("search.result.date.update") + ":&nbsp;</b>"));
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		hPanel4.add(new HTML(dtf.format(doc.getLastModified())));
		table.setWidget(rows++, 0, hPanel4);

		// Extended columns
		if (profileFileBrowser.isExtraColumns()) {
			Map<GWTFilebrowseExtraColumn, GWTFormElement> ecMap = new LinkedHashMap<GWTFilebrowseExtraColumn, GWTFormElement>();
			if (profileFileBrowser.getColumn0() != null) {
				ecMap.put(profileFileBrowser.getColumn0(), doc.getColumn0());
			}
			if (profileFileBrowser.getColumn1() != null) {
				ecMap.put(profileFileBrowser.getColumn1(), doc.getColumn1());
			}
			if (profileFileBrowser.getColumn2() != null) {
				ecMap.put(profileFileBrowser.getColumn2(), doc.getColumn2());
			}
			if (profileFileBrowser.getColumn3() != null) {
				ecMap.put(profileFileBrowser.getColumn3(), doc.getColumn3());
			}
			if (profileFileBrowser.getColumn4() != null) {
				ecMap.put(profileFileBrowser.getColumn4(), doc.getColumn4());
			}
			if (profileFileBrowser.getColumn5() != null) {
				ecMap.put(profileFileBrowser.getColumn5(), doc.getColumn5());
			}
			if (profileFileBrowser.getColumn6() != null) {
				ecMap.put(profileFileBrowser.getColumn6(), doc.getColumn6());
			}
			if (profileFileBrowser.getColumn7() != null) {
				ecMap.put(profileFileBrowser.getColumn7(), doc.getColumn7());
			}
			if (profileFileBrowser.getColumn8() != null) {
				ecMap.put(profileFileBrowser.getColumn8(), doc.getColumn8());
			}
			if (profileFileBrowser.getColumn9() != null) {
				ecMap.put(profileFileBrowser.getColumn9(), doc.getColumn9());
			}
			rows = addExtendedColumns(table, ecMap);
		}

		// Categories and tagcloud
		rows = addCategoriesKeywords(doc.getCategories(), doc.getKeywords(), table);

		// PropertyGroups
		rows = addPropertyGroups(doc.getPath(), table);

		// Separator end line
		Image horizontalLine = new Image("img/transparent_pixel.gif");
		horizontalLine.setStyleName("okm-TopPanel-Line-Border");
		horizontalLine.setSize("100%", "2px");
		table.setWidget(rows, 0, horizontalLine);
		table.getFlexCellFormatter().setVerticalAlignment(rows, 0, HasAlignment.ALIGN_BOTTOM);
		table.getFlexCellFormatter().setHeight(rows, 0, "30px");
	}

	/**
	 * addExtendedColumns
	 */
	private int addExtendedColumns(FlexTable table, Map<GWTFilebrowseExtraColumn, GWTFormElement> ecMap) {
		int rows = table.getRowCount();
		int actual = 0;
		HorizontalPanel hPanel = new HorizontalPanel();

		for (GWTFilebrowseExtraColumn column : ecMap.keySet()) {
			if (actual % 4 == 0) {
				table.setWidget(rows, 0, new HorizontalPanel());
				hPanel = (HorizontalPanel) table.getWidget(rows, 0);
				hPanel.setStyleName("okm-NoWrap");
				rows++;
			} else {
				hPanel.add(Util.hSpace("33px"));
			}

			hPanel.add(new HTML("<b>" + column.getFormElement().getLabel() + ":</b>&nbsp;"));
			hPanel.add(formManager.getDrawFormElement(ecMap.get(column)));
			actual++;
		}

		return rows;
	}

	/**
	 * addPropertyGroups
	 */
	private int addPropertyGroups(final String path, FlexTable table) {
		int rows = table.getRowCount();
		if (Main.get().mainPanel.search.searchBrowser.searchIn.searchControl.showPropertyGroups.getValue()) {
			final HorizontalPanel propertyGroupsPanel = new HorizontalPanel();
			table.setWidget(rows++, 0, propertyGroupsPanel);
			propertyGroupService.getGroups(path, new AsyncCallback<List<GWTPropertyGroup>>() {
				@Override
				public void onSuccess(List<GWTPropertyGroup> result) {
					drawPropertyGroups(path, result, propertyGroupsPanel);
				}

				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("getGroups", caught);
				}
			});
		}
		return rows;
	}

	/**
	 * drawCategoriesKeywords
	 */
	private int addCategoriesKeywords(Set<GWTFolder> categories, Set<String> keywords, FlexTable table) {
		int rows = table.getRowCount();

		// Categories and tagcloud
		if (categories.size() > 0 || keywords.size() > 0) {
			HorizontalPanel hPanel = new HorizontalPanel();
			hPanel.setStyleName("okm-NoWrap");

			if (categories.size() > 0) {
				FlexTable tableSubscribedCategories = new FlexTable();
				tableSubscribedCategories.setStyleName("okm-DisableSelect");

				// Sets the document categories
				for (Iterator<GWTFolder> it = categories.iterator(); it.hasNext(); ) {
					drawCategory(tableSubscribedCategories, it.next());
				}

				hPanel.add(new HTML("<b>" + Main.i18n("document.categories") + "</b>"));
				hPanel.add(Util.hSpace("5px"));
				hPanel.add(tableSubscribedCategories);
				hPanel.add(Util.hSpace("33px"));
			}

			if (keywords.size() > 0) {
				// Tag cloud
				TagCloud keywordsCloud = new TagCloud();
				keywordsCloud.setWidth("350px");
				WidgetUtil.drawTagCloud(keywordsCloud, keywords);
				hPanel.add(new HTML("<b>" + Main.i18n("document.keywords.cloud") + "</b>"));
				hPanel.add(Util.hSpace("5px"));
				hPanel.add(keywordsCloud);
			}

			table.setWidget(rows++, 0, hPanel);
		}

		return rows;
	}

	/**
	 * drawPropertyGroups
	 */
	private void drawPropertyGroups(final String docPath, final List<GWTPropertyGroup> propertyGroups,
	                                final HorizontalPanel propertyGroupsPanel) {
		if (propertyGroups.size() > 0) {
			Status status = Main.get().mainPanel.search.searchBrowser.searchResult.status;
			status.setFlag_refreshPropertyGroups();
			final GWTPropertyGroup propertyGroup = propertyGroups.remove(0);
			propertyGroupService.getProperties(docPath, propertyGroup.getName(), false, new AsyncCallback<List<GWTFormElement>>() {
				@Override
				public void onSuccess(List<GWTFormElement> result) {
					if (propertyGroupsPanel.getWidgetCount() == 0) {
						HTML label = new HTML("");
						label.setStyleName("okm-Security-Title");
						label.setHeight("15px");
						Image verticalLine = new Image("img/transparent_pixel.gif");
						verticalLine.setStyleName("okm-Vertical-Line-Border");
						verticalLine.setSize("2px", "100%");
						VerticalPanel vlPanel = new VerticalPanel();
						vlPanel.add(label);
						vlPanel.add(verticalLine);
						vlPanel.setCellWidth(verticalLine, "7px");
						vlPanel.setCellHeight(verticalLine, "100%");
						vlPanel.setHeight("100%");
						propertyGroupsPanel.add(vlPanel);
						propertyGroupsPanel.setCellHorizontalAlignment(vlPanel, HasAlignment.ALIGN_LEFT);
						propertyGroupsPanel.setCellWidth(vlPanel, "7px");
						propertyGroupsPanel.setCellHeight(vlPanel, "100%");
					}

					Image verticalLine = new Image("img/transparent_pixel.gif");
					verticalLine.setStyleName("okm-Vertical-Line-Border");
					verticalLine.setSize("2px", "100%");
					FormManager manager = new FormManager(null);
					manager.setFormElements(result);
					manager.draw(true); // read only !
					VerticalPanel vPanel = new VerticalPanel();
					HTML label = new HTML(propertyGroup.getLabel());
					label.setStyleName("okm-Security-Title");
					label.setHeight("15px");
					vPanel.add(label);
					vPanel.add(manager.getTable());
					propertyGroupsPanel.add(vPanel);
					propertyGroupsPanel.add(verticalLine);
					propertyGroupsPanel.setCellVerticalAlignment(vPanel, HasAlignment.ALIGN_TOP);
					propertyGroupsPanel.setCellHorizontalAlignment(verticalLine, HasAlignment.ALIGN_CENTER);
					propertyGroupsPanel.setCellWidth(verticalLine, "12px");
					propertyGroupsPanel.setCellHeight(verticalLine, "100%");
					drawPropertyGroups(docPath, propertyGroups, propertyGroupsPanel);
				}

				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("drawPropertyGroups", caught);
				}
			});
		} else {
			Status status = Main.get().mainPanel.search.searchBrowser.searchResult.status;
			status.unsetFlag_refreshPropertyGroups();
		}
	}

	/**
	 * Adding folder
	 *
	 * @param gwtQueryResult Query result
	 * @param score The folder score
	 */
	private void addFolderRow(GWTQueryResult gwtQueryResult, Score score) {
		int rows = table.getRowCount();
		final GWTFolder folder = gwtQueryResult.getFolder();

		// Folder row
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setStyleName("okm-NoWrap");
		hPanel.add(new HTML(score.getHTML()));
		hPanel.add(Util.hSpace("5px"));

		// Looks if must change icon on parent if now has no childs and properties with user security atention
		if ((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) {
			if (folder.isHasChildren()) {
				hPanel.add(new HTML(Util.imageItemHTML("img/menuitem_childs.gif")));
			} else {
				hPanel.add(new HTML(Util.imageItemHTML("img/menuitem_empty.gif")));
			}
		} else {
			if (folder.isHasChildren()) {
				hPanel.add(new HTML(Util.imageItemHTML("img/menuitem_childs_ro.gif")));
			} else {
				hPanel.add(new HTML(Util.imageItemHTML("img/menuitem_empty_ro.gif")));
			}
		}

		Anchor anchor = new Anchor();
		anchor.setHTML(folder.getName());
		anchor.setTitle(folder.getParentPath());
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CommonUI.openPath(folder.getPath(), "");
			}
		});
		anchor.setStyleName("okm-Hyperlink");
		hPanel.add(anchor);
		table.setWidget(rows++, 0, hPanel);

		// Folder row
		HorizontalPanel hPanel2 = new HorizontalPanel();
		hPanel2.setStyleName("okm-NoWrap");
		hPanel2.add(new HTML("<b>" + Main.i18n("folder.parent") + ":</b>&nbsp;"));
		hPanel2.add(drawFolder(folder.getParentPath()));
		table.setWidget(rows++, 0, hPanel2);

		// Folder detail
		HorizontalPanel hPanel3 = new HorizontalPanel();
		hPanel3.setStyleName("okm-NoWrap");
		hPanel3.add(new HTML("<b>" + Main.i18n("search.result.author") + ":</b>&nbsp;"));
		hPanel3.add(new HTML(folder.getUser().getUsername()));
		hPanel3.add(Util.hSpace("33px"));
		hPanel3.add(new HTML("<b>" + Main.i18n("folder.created") + ":&nbsp;</b>"));
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		hPanel3.add(new HTML(dtf.format(folder.getCreated())));
		table.setWidget(rows++, 0, hPanel3);

		// Extended columns
		if (profileFileBrowser.isExtraColumns()) {
			Map<GWTFilebrowseExtraColumn, GWTFormElement> ecMap = new LinkedHashMap<GWTFilebrowseExtraColumn, GWTFormElement>();
			if (profileFileBrowser.getColumn0() != null) {
				ecMap.put(profileFileBrowser.getColumn0(), folder.getColumn0());
			}
			if (profileFileBrowser.getColumn1() != null) {
				ecMap.put(profileFileBrowser.getColumn1(), folder.getColumn1());
			}
			if (profileFileBrowser.getColumn2() != null) {
				ecMap.put(profileFileBrowser.getColumn2(), folder.getColumn2());
			}
			if (profileFileBrowser.getColumn3() != null) {
				ecMap.put(profileFileBrowser.getColumn3(), folder.getColumn3());
			}
			if (profileFileBrowser.getColumn4() != null) {
				ecMap.put(profileFileBrowser.getColumn4(), folder.getColumn4());
			}
			if (profileFileBrowser.getColumn5() != null) {
				ecMap.put(profileFileBrowser.getColumn5(), folder.getColumn5());
			}
			if (profileFileBrowser.getColumn6() != null) {
				ecMap.put(profileFileBrowser.getColumn6(), folder.getColumn6());
			}
			if (profileFileBrowser.getColumn7() != null) {
				ecMap.put(profileFileBrowser.getColumn7(), folder.getColumn7());
			}
			if (profileFileBrowser.getColumn8() != null) {
				ecMap.put(profileFileBrowser.getColumn8(), folder.getColumn8());
			}
			if (profileFileBrowser.getColumn9() != null) {
				ecMap.put(profileFileBrowser.getColumn9(), folder.getColumn9());
			}
			rows = addExtendedColumns(table, ecMap);
		}

		// Categories and tagcloud
		rows = addCategoriesKeywords(folder.getCategories(), folder.getKeywords(), table);

		// PropertyGroups
		rows = addPropertyGroups(folder.getPath(), table);

		// Separator end line
		Image horizontalLine = new Image("img/transparent_pixel.gif");
		horizontalLine.setStyleName("okm-TopPanel-Line-Border");
		horizontalLine.setSize("100%", "2px");
		table.setWidget(rows, 0, horizontalLine);
		table.getFlexCellFormatter().setVerticalAlignment(rows, 0, HasAlignment.ALIGN_BOTTOM);
		table.getFlexCellFormatter().setHeight(rows, 0, "30px");
	}

	/**
	 * Adding mail
	 *
	 * @param gwtQueryResult Query result
	 * @param score The mail score
	 */
	private void addMailRow(GWTQueryResult gwtQueryResult, Score score) {
		int rows = table.getRowCount();
		final GWTMail mail = gwtQueryResult.getMail();

		// Mail row
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setStyleName("okm-NoWrap");
		hPanel.add(new HTML(score.getHTML()));
		hPanel.add(Util.hSpace("5px"));

		if (mail.getAttachments().size() > 0) {
			hPanel.add(new HTML(Util.imageItemHTML("img/email_attach.gif")));
		} else {
			hPanel.add(new HTML(Util.imageItemHTML("img/email.gif")));
		}

		Anchor anchor = new Anchor();
		anchor.setHTML(mail.getSubject());
		anchor.setTitle(mail.getParentPath());
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String docPath = mail.getPath();
				CommonUI.openPath(Util.getParent(docPath), docPath);
			}
		});
		anchor.setStyleName("okm-Hyperlink");
		hPanel.add(anchor);
		table.setWidget(rows++, 0, hPanel);

		// Mail Subject
		HorizontalPanel hPanel2 = new HorizontalPanel();
		hPanel2.setStyleName("okm-NoWrap");
		hPanel2.add(new HTML("<b>" + Main.i18n("mail.subject") + ":</b>&nbsp;"));
		hPanel2.add(new HTML(mail.getSubject()));

		// Excerpt row
		if ((Main.get().mainPanel.search.searchBrowser.searchIn.searchControl.getSearchMode() == SearchControl.SEARCH_MODE_SIMPLE ||
				!Main.get().mainPanel.search.searchBrowser.searchIn.searchNormal.content.getText().equals("")) &&
				gwtQueryResult.getExcerpt() != null) {
			table.setHTML(rows++, 0, "" + gwtQueryResult.getExcerpt() + (gwtQueryResult.getExcerpt().length() > 256 ? " ..." : ""));
			HTML space = new HTML();
			table.setWidget(rows, 0, space);
			table.getFlexCellFormatter().setHeight(rows++, 0, "5px");
		}

		// Folder row
		HorizontalPanel hPanel3 = new HorizontalPanel();
		hPanel3.setStyleName("okm-NoWrap");
		hPanel3.add(new HTML("<b>" + Main.i18n("document.folder") + ":</b>&nbsp;"));
		hPanel3.add(drawFolder(mail.getParentPath()));
		table.setWidget(rows++, 0, hPanel3);

		// mail details
		HorizontalPanel hPanel4 = new HorizontalPanel();
		hPanel4.setStyleName("okm-NoWrap");
		hPanel4.add(new HTML("<b>" + Main.i18n("search.result.author") + ":</b>&nbsp;"));
		hPanel4.add(new HTML(mail.getAuthor()));
		hPanel4.add(Util.hSpace("33px"));
		hPanel4.add(new HTML("<b>" + Main.i18n("search.result.size") + ":</b>&nbsp;"));
		hPanel4.add(new HTML(Util.formatSize(mail.getSize())));
		hPanel4.add(Util.hSpace("33px"));
		hPanel4.add(new HTML("<b>" + Main.i18n("search.result.date.create") + ":&nbsp;</b>"));
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		hPanel4.add(new HTML(dtf.format(mail.getCreated())));
		table.setWidget(rows++, 0, hPanel4);

		// Extended columns
		if (profileFileBrowser.isExtraColumns()) {
			Map<GWTFilebrowseExtraColumn, GWTFormElement> ecMap = new LinkedHashMap<GWTFilebrowseExtraColumn, GWTFormElement>();
			if (profileFileBrowser.getColumn0() != null) {
				ecMap.put(profileFileBrowser.getColumn0(), mail.getColumn0());
			}
			if (profileFileBrowser.getColumn1() != null) {
				ecMap.put(profileFileBrowser.getColumn1(), mail.getColumn1());
			}
			if (profileFileBrowser.getColumn2() != null) {
				ecMap.put(profileFileBrowser.getColumn2(), mail.getColumn2());
			}
			if (profileFileBrowser.getColumn3() != null) {
				ecMap.put(profileFileBrowser.getColumn3(), mail.getColumn3());
			}
			if (profileFileBrowser.getColumn4() != null) {
				ecMap.put(profileFileBrowser.getColumn4(), mail.getColumn4());
			}
			if (profileFileBrowser.getColumn5() != null) {
				ecMap.put(profileFileBrowser.getColumn5(), mail.getColumn5());
			}
			if (profileFileBrowser.getColumn6() != null) {
				ecMap.put(profileFileBrowser.getColumn6(), mail.getColumn6());
			}
			if (profileFileBrowser.getColumn7() != null) {
				ecMap.put(profileFileBrowser.getColumn7(), mail.getColumn7());
			}
			if (profileFileBrowser.getColumn8() != null) {
				ecMap.put(profileFileBrowser.getColumn8(), mail.getColumn8());
			}
			if (profileFileBrowser.getColumn9() != null) {
				ecMap.put(profileFileBrowser.getColumn9(), mail.getColumn9());
			}
			rows = addExtendedColumns(table, ecMap);
		}

		// Categories and tagcloud
		rows = addCategoriesKeywords(mail.getCategories(), mail.getKeywords(), table);

		// PropertyGroups
		rows = addPropertyGroups(mail.getPath(), table);

		// From, To and Reply panel
		HorizontalPanel hPanel5 = new HorizontalPanel();
		hPanel5.setStyleName("okm-NoWrap");
		hPanel5.add(new HTML("<b>" + Main.i18n("mail.from") + ":</b>&nbsp;"));
		hPanel5.add(new HTML(mail.getFrom()));

		if (mail.getTo().length > 0) {
			VerticalPanel toPanel = new VerticalPanel();

			for (int i = 0; i < mail.getTo().length; i++) {
				Anchor hTo = new Anchor();
				final String mailTo = mail.getTo()[i].contains("<") ?
						mail.getTo()[i].substring(mail.getTo()[i].indexOf("<") + 1,
								mail.getTo()[i].indexOf(">")) : mail.getTo()[i];
				hTo.setHTML(mail.getTo()[i].replace("<", "&lt;").replace(">", "&gt;"));
				hTo.setTitle("mailto:" + mailTo);
				hTo.setStyleName("okm-Mail-Link");
				hTo.addStyleName("okm-NoWrap");
				hTo.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						Window.open("mailto:" + mailTo, "_blank", "");
					}
				});

				toPanel.add(hTo);
			}

			hPanel5.add(Util.hSpace("33px"));
			hPanel5.add((new HTML("<b>" + Main.i18n("mail.to") + ":</b>&nbsp;")));
			hPanel5.add(toPanel);
		}

		if (mail.getReply().length > 0) {
			VerticalPanel replyPanel = new VerticalPanel();

			for (int i = 0; i < mail.getReply().length; i++) {
				Anchor hReply = new Anchor();
				final String mailReply = mail.getReply()[i].contains("<") ?
						mail.getReply()[i].substring(mail.getReply()[i].indexOf("<") + 1,
								mail.getReply()[i].indexOf(">")) : mail.getReply()[i];
				hReply.setHTML(mail.getReply()[i].replace("<", "&lt;").replace(">", "&gt;"));
				hReply.setTitle("mailto:" + mailReply);
				hReply.setStyleName("okm-Mail-Link");
				hReply.addStyleName("okm-NoWrap");
				hReply.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						Window.open("mailto:" + mailReply, "_blank", "");
					}
				});

				replyPanel.add(hReply);
			}

			hPanel5.add(Util.hSpace("33px"));
			hPanel5.add(new HTML("<b>" + Main.i18n("mail.reply") + ":</b>&nbsp;"));
			hPanel5.add(replyPanel);
		}

		table.setWidget(rows++, 0, hPanel5);

		// Separator end line
		Image horizontalLine = new Image("img/transparent_pixel.gif");
		horizontalLine.setStyleName("okm-TopPanel-Line-Border");
		horizontalLine.setSize("100%", "2px");
		table.setWidget(rows, 0, horizontalLine);
		table.getFlexCellFormatter().setVerticalAlignment(rows, 0, HasAlignment.ALIGN_BOTTOM);
		table.getFlexCellFormatter().setHeight(rows, 0, "30px");
	}

	/**
	 * drawCategory
	 */
	private void drawCategory(final FlexTable tableSubscribedCategories, final GWTFolder category) {
		int row = tableSubscribedCategories.getRowCount();
		Anchor anchor = new Anchor();

		// Looks if must change icon on parent if now has no childs and properties with user security atention
		String path = category.getPath().substring(16); // Removes /okm:categories

		if (category.isHasChildren()) {
			anchor.setHTML(Util.imageItemHTML("img/menuitem_childs.gif", path, "top"));
		} else {
			anchor.setHTML(Util.imageItemHTML("img/menuitem_empty.gif", path, "top"));
		}

		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				CommonUI.openPath(category.getPath(), null);
			}
		});

		anchor.setStyleName("okm-KeyMap-ImageHover");
		tableSubscribedCategories.setWidget(row, 0, anchor);
	}

	/**
	 * drawFolder
	 */
	private Anchor drawFolder(final String path) {
		Anchor anchor = new Anchor();
		anchor.setHTML(Util.imageItemHTML("img/menuitem_childs.gif", path, "top"));
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				CommonUI.openPath(path, null);
			}
		});
		anchor.setStyleName("okm-KeyMap-ImageHover");
		return anchor;
	}

	/**
	 * drawMailWithAttachment
	 */
	private Anchor drawMailWithAttachment(String convertedPath, final String path) {
		Anchor anchor = new Anchor();
		anchor.setHTML(Util.imageItemHTML("img/email_attach.gif", convertedPath, "top"));
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				CommonUI.openPath(Util.getParent(path), path);
			}
		});
		anchor.setStyleName("okm-KeyMap-ImageHover");
		return anchor;
	}

	/**
	 * removeAllRows
	 */
	public void removeAllRows() {
		table.removeAllRows();
	}

	/**
	 * setProfileFileBrowser
	 *
	 * @param profileFileBrowser
	 */
	public void setProfileFileBrowser(GWTProfileFileBrowser profileFileBrowser) {
		this.profileFileBrowser = profileFileBrowser;
	}
}