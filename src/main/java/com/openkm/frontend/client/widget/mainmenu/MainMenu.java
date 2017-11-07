/**
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

package com.openkm.frontend.client.widget.mainmenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.*;
import com.openkm.frontend.client.constants.GWTRepository;
import com.openkm.frontend.client.constants.ui.UIDashboardConstants;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.constants.ui.UIMenuConstants;
import com.openkm.frontend.client.extension.widget.menu.MenuItemExtension;
import com.openkm.frontend.client.service.OKMDocumentService;
import com.openkm.frontend.client.service.OKMDocumentServiceAsync;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.ConfirmPopup;
import com.openkm.frontend.client.widget.notify.NotifyPopup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main menu
 *
 * @author jllort
 */
public class MainMenu extends Composite {
	private final OKMDocumentServiceAsync documentService = (OKMDocumentServiceAsync) GWT.create(OKMDocumentService.class);

	private static final int OUTPUT_PDF = 2;
	private static final int OUTPUT_RTF = 3;
	private static final int OUTPUT_CSV = 4;

	// URI CONSTANTS
	public static final String URI_HELP = "http://www.openkm.com";
	public static final String URI_BUG_REPORT = "http://issues.openkm.com";
	public static final String URI_SUPPORT_REQUEST = "http://www.openkm.com/Contact/";
	public static final String URI_PUBLIC_FORUM = "http://forum.openkm.com";
	public static final String URI_PROJECT_WEB = "http://www.openkm.com";
	public static final String URI_DOCUMENTATION = "http://wiki.openkm.com";
	public static final String URI_VERSION_CHANGES = "http://wiki.openkm.com/index.php/Changelog";

	private ToolBarOption mainMenuOption;
	public Bookmark bookmark;
	public BookmarkPopup bookmarkPopup;
	public ManageBookmarkPopup manageBookmarkPopup;
	public int reportOutput = OUTPUT_PDF;
	private Map<String, MenuBar> fldMenus;
	private List<MenuItem> templateMenuItems;
	private Timer menusRefreshing;

	private MenuBar mainMenu;
	private MenuItem menuFile;
	private MenuBar subMenuFile;
	private MenuItem findFolder;
	private MenuItem findDocument;
	private MenuItem findSimilarDocument;
	private MenuItem createFolder;
	private MenuItem addDocument;
	private MenuItem download;
	private MenuItem downloadPdf;
	private MenuItem sendDocumentLink;
	private MenuItem sendDocumentAttachment;
	private MenuItem forwardMail;
	private MenuItem createFromTemplate;
	private MenuItem export;
	private MenuItem horizontalLineFile1;
	private MenuItem startWorkflow;
	private MenuItem horizontalLineFile2;
	private MenuItem refresh;
	private MenuItem horizontalLineFile3;
	private MenuItem restore;
	private MenuItem purge;
	private MenuItem purgeTrash;
	private MenuItem horizontalLineFile4;
	private MenuItem exit;
	private MenuItem menuEdit;
	private MenuBar subMenuEdit;
	private MenuItem lock;
	private MenuItem unlock;
	private MenuItem horizontalLineEdit1;
	private MenuItem checkout;
	private MenuItem checkin;
	private MenuItem cancelCheckout;
	private MenuItem horizontalLineEdit2;
	private MenuItem delete;
	private MenuItem copy;
	private MenuItem move;
	private MenuItem rename;
	private MenuItem note;
	private MenuItem category;
	private MenuItem keyword;
	private MenuItem merge;
	private MenuItem horizontalLineEdit3;
	private MenuItem addPropertyGroup;
	private MenuItem updatePropertyGroup;
	private MenuItem removePropertyGroup;
	private MenuItem horizontalLineEdit4;
	private MenuItem addSubscription;
	private MenuItem removeSubscription;
	private MenuItem menuTools;
	private MenuBar subMenuTools;
	private MenuItem language;
	private MenuBar subMenuLanguage;
	private MenuBar subMenuSkin;
	private MenuItem skinDefault;
	private MenuItem skinMediumFont;
	private MenuItem skinBigFont;
	private MenuItem skin;
	private MenuItem debugConsole;
	public MenuItem administration;
	private MenuItem preferences;
	private MenuBar subMenuPreferences;
	private MenuItem userPreferences;
	private MenuItem omr;
	private MenuItem convert;
	private MenuItem menuBookmark;
	public MenuBar subMenuBookmark;
	private MenuItem home;
	private MenuItem defaultHome;
	private MenuItem addBookmark;
	private MenuItem manageBookmark;
	private MenuItem horizontalLineBookmark1;
	private MenuItem menuReports;
	private MenuItem reportFormat;
	private MenuBar subMenuReportFormat;
	private MenuItem reportFormatPdf;
	private MenuItem reportFormatRtf;
	private MenuItem reportFormatCsv;
	private MenuBar subMenuReports;
	private MenuItem menuTemplates;
	private MenuBar subMenuTemplates;
	private MenuItem menuHelp;
	private MenuBar subMenuHelp;
	private MenuItem help;
	private MenuItem documentation;
	private MenuItem bugReport;
	private MenuItem supportRequest;
	private MenuItem publicForum;
	private MenuItem versionChanges;
	private MenuItem projectWeb;
	private MenuItem about;

	public MainMenu() {
		fldMenus = new HashMap<String, MenuBar>();
		templateMenuItems = new ArrayList<MenuItem>();

		// The bookmark
		bookmark = new Bookmark();
		bookmarkPopup = new BookmarkPopup();
		bookmarkPopup.setWidth("310px");
		bookmarkPopup.setHeight("100px");
		bookmarkPopup.setStyleName("okm-Popup");

		// The bookmark management
		manageBookmarkPopup = new ManageBookmarkPopup();
		manageBookmarkPopup.setWidth("400px");
		manageBookmarkPopup.setHeight("230px");
		manageBookmarkPopup.setStyleName("okm-Popup");

		// General menu
		mainMenu = new MenuBar(false);
		mainMenu.setStyleName("okm-TopMenuBar");

		// File menu
		// First we must create menus and submenus on inverse order
		findFolder = new MenuItem(Util.menuHTML("img/icon/actions/folder_find.gif", Main.i18n("general.menu.file.find.folder")), true,
				findFolderOKM);
		findFolder.addStyleName("okm-MainMenuItem");
		findDocument = new MenuItem(Util.menuHTML("img/icon/actions/document_find.png", Main.i18n("general.menu.file.find.document")),
				true, findDocumentOKM);
		findDocument.addStyleName("okm-MainMenuItem");
		findSimilarDocument = new MenuItem(Util.menuHTML("img/icon/actions/similar_find.png",
				Main.i18n("general.menu.file.find.similar.document")), true, findSimilarDocumentOKM);
		findSimilarDocument.addStyleName("okm-MainMenuItem");
		createFolder = new MenuItem(Util.menuHTML("img/icon/actions/add_folder.gif", Main.i18n("general.menu.file.create.directory")),
				true, createFolderOKM);
		createFolder.addStyleName("okm-MainMenuItem");
		addDocument = new MenuItem(Util.menuHTML("img/icon/actions/add_document.gif", Main.i18n("general.menu.file.add.document")), true,
				addDocumentOKM);
		addDocument.addStyleName("okm-MainMenuItem");
		download = new MenuItem(Util.menuHTML("img/icon/actions/download.gif", Main.i18n("general.menu.file.download.document")), true,
				downloadOKM);
		download.addStyleName("okm-MainMenuItem");
		downloadPdf = new MenuItem(
				Util.menuHTML("img/icon/actions/download_pdf.gif", Main.i18n("general.menu.file.download.document.pdf")), true,
				downloadPdfOKM);
		downloadPdf.addStyleName("okm-MainMenuItem");
		sendDocumentLink = new MenuItem(Util.menuHTML("img/icon/actions/send_document_link.gif", Main.i18n("general.menu.file.send.link")),
				true, sendDocumentLinkOKM);
		sendDocumentLink.addStyleName("okm-MainMenuItem");
		sendDocumentAttachment = new MenuItem(Util.menuHTML("img/icon/actions/send_document_attachment.gif",
				Main.i18n("general.menu.file.send.attachment")), true, sendDocumentAttachmentOKM);
		sendDocumentAttachment.addStyleName("okm-MainMenuItem");
		forwardMail = new MenuItem(Util.menuHTML("img/email_forward.png", Main.i18n("general.menu.file.send.forward.mail")), true,
				sendMailForwardOKM);
		forwardMail.addStyleName("okm-MainMenuItem");
		createFromTemplate = new MenuItem(Util.menuHTML("img/icon/actions/create_from_template.gif",
				Main.i18n("general.menu.file.create.from.template")), true, createFromTemplateOKM);
		createFromTemplate.addStyleName("okm-MainMenuItem");
		export = new MenuItem(Util.menuHTML("img/icon/actions/export.gif", Main.i18n("general.menu.file.export")), true, exportToFile);
		export.addStyleName("okm-MainMenuItem");
		horizontalLineFile1 = new MenuItem("", true, nullExecute);
		horizontalLineFile1.setStyleName("okm-MainMenuItem");
		horizontalLineFile1.addStyleName("okm-MainMenuItem-Base-HorizontalSeparator");
		horizontalLineFile1.setHeight("2px");
		startWorkflow = new MenuItem(Util.menuHTML("img/icon/actions/start_workflow.gif", Main.i18n("general.menu.file.start.workflow")),
				true, startWorkflowOKM);
		horizontalLineFile2 = new MenuItem("", true, nullExecute);
		horizontalLineFile2.setStyleName("okm-MainMenuItem");
		horizontalLineFile2.addStyleName("okm-MainMenuItem-Base-HorizontalSeparator");
		horizontalLineFile2.setHeight("2px");
		refresh = new MenuItem(Util.menuHTML("img/icon/actions/refresh.gif", Main.i18n("general.menu.file.refresh")), true, refreshOKM);
		horizontalLineFile3 = new MenuItem("", true, nullExecute);
		horizontalLineFile3.setStyleName("okm-MainMenuItem");
		horizontalLineFile3.addStyleName("okm-MainMenuItem-Base-HorizontalSeparator");
		horizontalLineFile3.setHeight("2px");
		restore = new MenuItem(Util.menuHTML("img/icon/actions/restore.gif", Main.i18n("general.menu.file.restore")), true, restoreOKM);
		restore.addStyleName("okm-MainMenuItem");
		purge = new MenuItem(Util.menuHTML("img/icon/actions/purge.gif", Main.i18n("general.menu.file.purge")), true, purgeOKM);
		purge.addStyleName("okm-MainMenuItem");
		purgeTrash = new MenuItem(Util.menuHTML("img/icon/actions/purge_trash.gif", Main.i18n("general.menu.file.purge.trash")), true,
				purgeTrashOKM);
		purgeTrash.addStyleName("okm-MainMenuItem");
		horizontalLineFile4 = new MenuItem("", true, nullExecute);
		horizontalLineFile4.setStyleName("okm-MainMenuItem");
		horizontalLineFile4.addStyleName("okm-MainMenuItem-Base-HorizontalSeparator");
		horizontalLineFile4.setHeight("2px");
		exit = new MenuItem(Util.menuHTML("img/icon/menu/exit.gif", Main.i18n("general.menu.file.exit")), true, exitOKM);
		exit.addStyleName("okm-MainMenuItem");
		subMenuFile = new MenuBar(true);
		subMenuFile.setStyleName("okm-SubMenuBar");
		subMenuFile.setAutoOpen(true);
		subMenuFile.addItem(findFolder);
		subMenuFile.addItem(findDocument);
		subMenuFile.addItem(findSimilarDocument);
		subMenuFile.addItem(createFolder);
		subMenuFile.addItem(addDocument);
		subMenuFile.addItem(download);
		subMenuFile.addItem(downloadPdf);
		subMenuFile.addItem(sendDocumentLink);
		subMenuFile.addItem(sendDocumentAttachment);
		subMenuFile.addItem(forwardMail);
		subMenuFile.addItem(createFromTemplate);
		subMenuFile.addItem(export);
		subMenuFile.addItem(horizontalLineFile1);
		subMenuFile.addItem(startWorkflow);
		subMenuFile.addItem(horizontalLineFile2);
		subMenuFile.addItem(refresh);
		subMenuFile.addItem(horizontalLineFile3);
		subMenuFile.addItem(restore);
		subMenuFile.addItem(purge);
		subMenuFile.addItem(purgeTrash);
		subMenuFile.addItem(horizontalLineFile4);
		subMenuFile.addItem(exit);
		menuFile = new MenuItem(Main.i18n("general.menu.file"), subMenuFile);
		menuFile.addStyleName("okm-MainMenuBar");

		// Edit menu
		// First we must create menus and submenus on inverse order
		lock = new MenuItem(Util.menuHTML("img/icon/actions/lock.gif", Main.i18n("general.menu.edit.lock")), true, lockOKM);
		lock.addStyleName("okm-MainMenuItem");
		unlock = new MenuItem(Util.menuHTML("img/icon/actions/unlock.gif", Main.i18n("general.menu.edit.unlock")), true, unlockOKM);
		unlock.addStyleName("okm-MainMenuItem");
		horizontalLineEdit1 = new MenuItem("", true, nullExecute);
		horizontalLineEdit1.setStyleName("okm-MainMenuItem");
		horizontalLineEdit1.addStyleName("okm-MainMenuItem-Base-HorizontalSeparator");
		horizontalLineEdit1.setHeight("2px");
		checkout = new MenuItem(Util.menuHTML("img/icon/actions/checkout.gif", Main.i18n("general.menu.edit.checkout")), true, checkoutOKM);
		checkout.addStyleName("okm-MainMenuItem");
		checkin = new MenuItem(Util.menuHTML("img/icon/actions/checkin.gif", Main.i18n("general.menu.edit.checkin")), true, checkinOKM);
		checkin.addStyleName("okm-MainMenuItem");
		cancelCheckout = new MenuItem(
				Util.menuHTML("img/icon/actions/cancel_checkout.gif", Main.i18n("general.menu.edit.cancel.checkout")), true,
				cancelCheckoutOKM);
		cancelCheckout.addStyleName("okm-MainMenuItem");
		horizontalLineEdit2 = new MenuItem("", true, nullExecute);
		horizontalLineEdit2.setStyleName("okm-MainMenuItem");
		horizontalLineEdit2.addStyleName("okm-MainMenuItem-Base-HorizontalSeparator");
		horizontalLineEdit2.setHeight("2px");
		delete = new MenuItem(Util.menuHTML("img/icon/actions/delete.gif", Main.i18n("general.menu.edit.delete")), true, deleteOKM);
		delete.addStyleName("okm-MainMenuItem");
		copy = new MenuItem(Util.menuHTML("img/icon/actions/copy.gif", Main.i18n("general.menu.edit.copy")), true, copyOKM);
		copy.addStyleName("okm-MenuItem-strike");
		move = new MenuItem(Util.menuHTML("img/icon/actions/move_document.gif", Main.i18n("general.menu.edit.move")), true, moveOKM);
		move.addStyleName("okm-MenuItem-strike");
		rename = new MenuItem(Util.menuHTML("img/icon/actions/rename.gif", Main.i18n("general.menu.edit.rename")), true, renameOKM);
		rename.addStyleName("okm-MenuItem-strike");
		note = new MenuItem(Util.menuHTML("img/icon/actions/add_note.png", Main.i18n("general.menu.edit.add.note")), true, addNote);
		note.addStyleName("okm-MenuItem-strike");
		category = new MenuItem(Util.menuHTML("img/icon/stackpanel/table_key.gif", Main.i18n("category.add")), true, addCategory);
		category.addStyleName("okm-MenuItem-strike");
		keyword = new MenuItem(Util.menuHTML("img/icon/actions/book_add.png", Main.i18n("keyword.add")), true, addKeyword);
		keyword.addStyleName("okm-MenuItem-strike");
		merge = new MenuItem(Util.menuHTML("img/icon/actions/merge_pdf.png", Main.i18n("general.menu.edit.merge.pdf")), true, mergePdf);
		merge.addStyleName("okm-MenuItem-strike");
		horizontalLineEdit3 = new MenuItem("", true, nullExecute);
		horizontalLineEdit3.setStyleName("okm-MainMenuItem");
		horizontalLineEdit3.addStyleName("okm-MainMenuItem-Base-HorizontalSeparator");
		horizontalLineEdit3.setHeight("2px");
		addPropertyGroup = new MenuItem(Util.menuHTML("img/icon/actions/add_property_group.gif",
				Main.i18n("general.menu.edit.add.property.group")), true, addPropertyGroupOKM);
		addPropertyGroup.addStyleName("okm-MenuItem-strike");
		updatePropertyGroup = new MenuItem(Util.menuHTML("img/icon/actions/update_property_group.png",
				Main.i18n("general.menu.edit.update.property.group")), true, updatePropertyGroupOKM);
		updatePropertyGroup.addStyleName("okm-MenuItem-strike");
		removePropertyGroup = new MenuItem(Util.menuHTML("img/icon/actions/remove_property_group.gif",
				Main.i18n("general.menu.edit.remove.property.group")), true, removePropertyGroupOKM);
		removePropertyGroup.addStyleName("okm-MenuItem-strike");
		horizontalLineEdit4 = new MenuItem("", true, nullExecute);
		horizontalLineEdit4.setStyleName("okm-MainMenuItem");
		horizontalLineEdit4.addStyleName("okm-MainMenuItem-Base-HorizontalSeparator");
		horizontalLineEdit4.setHeight("2px");
		addSubscription = new MenuItem(Util.menuHTML("img/icon/actions/add_subscription.gif",
				Main.i18n("general.menu.edit.add.subscription")), true, addSubscriptionOKM);
		addSubscription.addStyleName("okm-MenuItem-strike");
		removeSubscription = new MenuItem(Util.menuHTML("img/icon/actions/remove_subscription.gif",
				Main.i18n("general.menu.edit.remove.subscription")), true, removeSubscriptionOKM);
		removeSubscription.addStyleName("okm-MenuItem-strike");
		// Submenu edit
		subMenuEdit = new MenuBar(true);
		subMenuEdit.setStyleName("okm-SubMenuBar");
		subMenuEdit.setAutoOpen(true);
		subMenuEdit.addItem(lock);
		subMenuEdit.addItem(unlock);
		subMenuEdit.addItem(horizontalLineEdit1);
		subMenuEdit.addItem(checkout);
		subMenuEdit.addItem(checkin);
		subMenuEdit.addItem(cancelCheckout);
		subMenuEdit.addItem(horizontalLineEdit2);
		subMenuEdit.addItem(delete);
		subMenuEdit.addItem(copy);
		subMenuEdit.addItem(move);
		subMenuEdit.addItem(rename);
		subMenuEdit.addItem(note);
		subMenuEdit.addItem(category);
		subMenuEdit.addItem(keyword);
		subMenuEdit.addItem(merge);
		subMenuEdit.addItem(horizontalLineEdit3);
		subMenuEdit.addItem(addPropertyGroup);
		subMenuEdit.addItem(updatePropertyGroup);
		subMenuEdit.addItem(removePropertyGroup);
		subMenuEdit.addItem(horizontalLineEdit4);
		subMenuEdit.addItem(addSubscription);
		subMenuEdit.addItem(removeSubscription);

		// Menu edit
		menuEdit = new MenuItem(Main.i18n("general.menu.edit"), subMenuEdit);
		menuEdit.addStyleName("okm-MainMenuBar");

		// Tools menu
		// First we must create menus and submenus on inverse order
		// Submenu Language options
		subMenuLanguage = new MenuBar(true);
		subMenuLanguage.setStyleName("okm-SubMenuBar");
		// Submenu language
		language = new MenuItem(Util.menuHTML("img/icon/menu/language.gif", Main.i18n("general.menu.tools.languages")), true,
				subMenuLanguage);
		language.addStyleName("okm-MainMenuItem");
		// language.addStyleName("okm-MainMenuItem-Base-Childs");

		// Submenu skin options
		subMenuSkin = new MenuBar(true);
		subMenuSkin.setStyleName("okm-SubMenuBar");
		skinDefault = new MenuItem(Util.menuHTML("img/icon/menu/skin_default.gif", Main.i18n("general.menu.tools.skin.default")), true,
				setSkinDefault);
		skinMediumFont = new MenuItem(Util.menuHTML("img/icon/menu/skin_test.gif", Main.i18n("general.menu.tools.skin.mediumfont")), true,
				setSkinMediumFont);
		skinBigFont = new MenuItem(Util.menuHTML("img/icon/menu/skin_test.gif", Main.i18n("general.menu.tools.skin.bigfont")), true,
				setSkinBigFont);
		skinDefault.addStyleName("okm-MainMenuItem");
		skinMediumFont.addStyleName("okm-MainMenuItem");
		skinBigFont.addStyleName("okm-MainMenuItem");
		subMenuSkin.addItem(skinDefault);
		subMenuSkin.addItem(skinMediumFont);
		subMenuSkin.addItem(skinBigFont);

		// Submenu skin
		skin = new MenuItem(Util.menuHTML("img/icon/menu/skin.gif", Main.i18n("general.menu.tools.skin")), true, subMenuSkin);
		skin.addStyleName("okm-MainMenuItem");

		// Other tools options
		debugConsole = new MenuItem(Util.menuHTML("img/icon/menu/console.gif", Main.i18n("general.menu.tools.debug.console")), true,
				setViewDebugConsole);
		debugConsole.addStyleName("okm-MainMenuItem");
		administration = new MenuItem(Util.menuHTML("img/icon/menu/administration.gif", Main.i18n("general.menu.tools.administration")),
				true, showAdministration);
		administration.addStyleName("okm-MainMenuItem");
		administration.setVisible(false);

		// Submenu preferences opions
		subMenuPreferences = new MenuBar(true);
		subMenuPreferences.setStyleName("okm-SubMenuBar");
		userPreferences = new MenuItem(
				Util.menuHTML("img/icon/menu/user_preferences.gif", Main.i18n("general.menu.tools.user.preferences")), true,
				setUserPreferences);
		userPreferences.addStyleName("okm-MainMenuItem");
		subMenuPreferences.addItem(userPreferences);

		// Submenu preferences
		preferences = new MenuItem(Util.menuHTML("img/icon/menu/preferences.gif", Main.i18n("general.menu.tools.preferences")), true,
				subMenuPreferences);
		preferences.addStyleName("okm-MainMenuItem");

		// OMR
		omr = new MenuItem(Util.menuHTML("img/icon/actions/omr.png", Main.i18n("general.menu.tools.omr")), true, executeOmr);
		omr.addStyleName("okm-MainMenuItem");
		
		// Convert
		convert = new MenuItem(Util.menuHTML("img/icon/menu/convert.png", Main.i18n("general.menu.tools.convert")), true, showConvert);
		convert.addStyleName("okm-MainMenuItem");
		convert.setVisible(false);

		// Submenu tools
		subMenuTools = new MenuBar(true);
		subMenuTools.setStyleName("okm-SubMenuBar");
		subMenuTools.setAutoOpen(true);
		subMenuTools.addItem(language);
		subMenuTools.addItem(skin);
		subMenuTools.addItem(debugConsole);
		subMenuTools.addItem(administration);
		subMenuTools.addItem(preferences);
		subMenuTools.addItem(omr);
		subMenuTools.addItem(convert);

		// Menu tools
		menuTools = new MenuItem(Main.i18n("general.menu.tools"), subMenuTools);
		menuTools.addStyleName("okm-MainMenuBar");

		home = new MenuItem(Util.menuHTML("img/icon/actions/bookmark_go.gif", Main.i18n("general.menu.bookmark.home")), true, goToUserHome);
		home.addStyleName("okm-MainMenuItem");
		defaultHome = new MenuItem(Util.menuHTML("img/icon/actions/bookmark.gif", Main.i18n("general.menu.bookmark.default.home")), true,
				setDefaultHome);
		defaultHome.addStyleName("okm-MainMenuItem");
		addBookmark = new MenuItem(Util.menuHTML("img/icon/actions/add_bookmark.gif", Main.i18n("general.menu.bookmark.add")), true,
				addBookmarkOKM);
		addBookmark.addStyleName("okm-MainMenuItem");
		manageBookmark = new MenuItem(Util.menuHTML("img/icon/actions/bookmark_edit.gif", Main.i18n("general.menu.bookmark.edit")), true,
				editBookmark);
		manageBookmark.addStyleName("okm-MainMenuItem");
		horizontalLineBookmark1 = new MenuItem("", true, nullExecute);
		horizontalLineBookmark1.setStyleName("okm-MainMenuItem");
		horizontalLineBookmark1.addStyleName("okm-MainMenuItem-Base-HorizontalSeparator");
		horizontalLineBookmark1.setHeight("2px");

		// Submenu tools
		subMenuBookmark = new MenuBar(true);
		subMenuBookmark.setStyleName("okm-SubMenuBar");
		subMenuBookmark.setAutoOpen(true);
		subMenuBookmark.addItem(home);
		subMenuBookmark.addItem(defaultHome);
		subMenuBookmark.addItem(addBookmark);
		subMenuBookmark.addItem(manageBookmark);
		subMenuBookmark.addItem(horizontalLineBookmark1);

		// Menu bookmark
		menuBookmark = new MenuItem(Main.i18n("general.menu.bookmark"), subMenuBookmark);
		menuBookmark.addStyleName("okm-MainMenuBar");

		// Submenu help option
		help = new MenuItem(Util.menuHTML("img/icon/menu/help.gif", Util.windowOpen(Main.i18n("general.menu.help"), URI_HELP)), true,
				nullExecute);
		help.addStyleName("okm-MainMenuItem");
		documentation = new MenuItem(Util.menuHTML("img/icon/menu/documentation.gif",
				Util.windowOpen(Main.i18n("general.menu.help.documentation"), URI_DOCUMENTATION)), true, nullExecute);
		documentation.addStyleName("okm-MainMenuItem");
		bugReport = new MenuItem(Util.menuHTML("img/icon/menu/bugs.gif",
				Util.windowOpen(Main.i18n("general.menu.help.bug.report"), URI_BUG_REPORT)), true, nullExecute);
		bugReport.addStyleName("okm-MainMenuItem");
		supportRequest = new MenuItem(Util.menuHTML("img/icon/menu/support.gif",
				Util.windowOpen(Main.i18n("general.menu.help.support.request"), URI_SUPPORT_REQUEST)), true, nullExecute);
		supportRequest.addStyleName("okm-MainMenuItem");
		publicForum = new MenuItem(Util.menuHTML("img/icon/menu/forum.gif",
				Util.windowOpen(Main.i18n("general.menu.help.public.forum"), URI_PUBLIC_FORUM)), true, nullExecute);
		publicForum.addStyleName("okm-MainMenuItem");
		versionChanges = new MenuItem(Util.menuHTML("img/icon/menu/brick.gif",
				Util.windowOpen(Main.i18n("general.menu.help.version.changes"), URI_VERSION_CHANGES)), true, nullExecute);
		versionChanges.addStyleName("okm-MainMenuItem");
		projectWeb = new MenuItem(Util.menuHTML("img/icon/menu/home.gif",
				Util.windowOpen(Main.i18n("general.menu.help.project.web"), URI_PROJECT_WEB)), true, nullExecute);
		projectWeb.addStyleName("okm-MainMenuItem");
		about = new MenuItem(Util.menuHTML("img/icon/menu/about.gif", Main.i18n("general.menu.help.about")), true, aboutOKM);
		about.addStyleName("okm-MainMenuItem");

		// Submenu report format
		subMenuReportFormat = new MenuBar(true);
		subMenuReportFormat.setStyleName("okm-SubMenuBar");
		reportFormatPdf = new MenuItem(Util.menuHTML("img/icon/security/yes.gif", Main.i18n("general.menu.report.format.pdf")), true,
				enablePdfReporFormat);
		reportFormatPdf.addStyleName("okm-MainMenuItem");
		reportFormatRtf = new MenuItem(Util.menuHTML("img/icon/security/no.gif", Main.i18n("general.menu.report.format.rtf")), true,
				enableTextReporFormat);
		reportFormatRtf.addStyleName("okm-MainMenuItem");
		reportFormatRtf.addStyleName("okm-MenuItem-strike");
		reportFormatCsv = new MenuItem(Util.menuHTML("img/icon/security/no.gif", Main.i18n("general.menu.report.format.csv")), true,
				enableCsvReporFormat);
		reportFormatCsv.addStyleName("okm-MainMenuItem");
		reportFormatCsv.addStyleName("okm-MenuItem-strike");
		subMenuReportFormat.addItem(reportFormatPdf);
		subMenuReportFormat.addItem(reportFormatRtf);
		subMenuReportFormat.addItem(reportFormatCsv);

		reportFormat = new MenuItem(Util.menuHTML("img/icon/menu/preferences.gif", Main.i18n("general.menu.report.format")), true,
				subMenuReportFormat);
		reportFormat.addStyleName("okm-MainMenuItem");

		// Submenu tools
		subMenuReports = new MenuBar(true);
		subMenuReports.setStyleName("okm-SubMenuBar");
		subMenuReports.setAutoOpen(true);
		subMenuReports.addItem(reportFormat);

		// Menu bookmark
		menuReports = new MenuItem(Main.i18n("general.menu.report"), subMenuReports);
		menuReports.addStyleName("okm-MainMenuBar");

		reportFormat = new MenuItem(Util.menuHTML("img/icon/menu/preferences.gif", Main.i18n("general.menu.report.format")), true,
				subMenuReportFormat);
		reportFormat.addStyleName("okm-MainMenuItem");

		// Submenu documents
		subMenuTemplates = new MenuBar(true);
		subMenuTemplates.setStyleName("okm-SubMenuBar");
		subMenuTemplates.setAutoOpen(true);

		// Menu documents
		menuTemplates = new MenuItem(Main.i18n("general.menu.templates"), subMenuTemplates);
		menuTemplates.addStyleName("okm-MainMenuBar");

		// Submenu help
		subMenuHelp = new MenuBar(true);
		subMenuHelp.setStyleName("okm-SubMenuBar");
		subMenuHelp.setAutoOpen(true);
		// subMenuHelp.addItem(help);
		subMenuHelp.addItem(documentation);
		subMenuHelp.addItem(bugReport);
		subMenuHelp.addItem(supportRequest);
		subMenuHelp.addItem(publicForum);
		subMenuHelp.addItem(versionChanges);
		subMenuHelp.addItem(projectWeb);
		subMenuHelp.addItem(about);

		// Help menu
		menuHelp = new MenuItem(Main.i18n("general.menu.help"), subMenuHelp);
		menuHelp.addStyleName("okm-MainMenuBar");

		// Create final general menu adding cascade menus to it
		mainMenu.addItem(menuFile);
		mainMenu.addItem(menuEdit);
		mainMenu.addItem(menuTools);
		mainMenu.addItem(menuBookmark);
		mainMenu.addItem(menuReports);
		mainMenu.addItem(menuTemplates);
		mainMenu.addItem(menuHelp);
		mainMenu.setAutoOpen(false);

		// By default hide menus
		menuFile.setVisible(false);
		menuEdit.setVisible(false);
		menuTools.setVisible(false);
		menuBookmark.setVisible(false);
		menuTemplates.setVisible(false);
		menuReports.setVisible(false);
		menuHelp.setVisible(false);

		initWidget(mainMenu);
	}

	// Lang refresh
	public void langRefresh() {
		bookmarkPopup.langRefresh(); // Refreshing popup
		manageBookmarkPopup.langRefresh(); // Refreshing management popup
		menuFile.setText(Main.i18n("general.menu.file"));
		findFolder.setHTML(Util.menuHTML("img/icon/actions/folder_find.gif", Main.i18n("general.menu.file.find.folder")));
		findDocument.setHTML(Util.menuHTML("img/icon/actions/document_find.png", Main.i18n("general.menu.file.find.document")));
		findSimilarDocument
				.setHTML(Util.menuHTML("img/icon/actions/similar_find.png", Main.i18n("general.menu.file.find.similar.document")));
		createFolder.setHTML(Util.menuHTML("img/icon/actions/add_folder.gif", Main.i18n("general.menu.file.create.directory")));
		download.setHTML(Util.menuHTML("img/icon/actions/download.gif", Main.i18n("general.menu.file.download.document")));
		downloadPdf.setHTML(Util.menuHTML("img/icon/actions/download_pdf.gif", Main.i18n("general.menu.file.download.document.pdf")));
		sendDocumentLink.setHTML(Util.menuHTML("img/icon/actions/send_document_link.gif", Main.i18n("general.menu.file.send.link")));
		sendDocumentAttachment.setHTML(Util.menuHTML("img/icon/actions/send_document_attachment.gif",
				Main.i18n("general.menu.file.send.attachment")));
		forwardMail.setHTML(Util.menuHTML("img/email_forward.png", Main.i18n("general.menu.file.send.forward.mail")));
		createFromTemplate.setHTML(Util.menuHTML("img/icon/actions/create_from_template.gif",
				Main.i18n("general.menu.file.create.from.template")));
		export.setHTML(Util.menuHTML("img/icon/actions/export.gif", Main.i18n("general.menu.file.export")));
		startWorkflow.setHTML(Util.menuHTML("img/icon/actions/start_workflow.gif", Main.i18n("general.menu.file.start.workflow")));
		refresh.setHTML(Util.menuHTML("img/icon/actions/refresh.gif", Main.i18n("general.menu.file.refresh")));
		restore.setHTML(Util.menuHTML("img/icon/actions/restore.gif", Main.i18n("general.menu.file.restore")));
		purge.setHTML(Util.menuHTML("img/icon/actions/purge.gif", Main.i18n("general.menu.file.purge")));
		purgeTrash.setHTML(Util.menuHTML("img/icon/actions/purge_trash.gif", Main.i18n("general.menu.file.purge.trash")));
		exit.setHTML(Util.menuHTML("img/icon/menu/exit.gif", Main.i18n("general.menu.file.exit")));
		menuEdit.setText(Main.i18n("general.menu.edit"));
		lock.setHTML(Util.menuHTML("img/icon/actions/lock.gif", Main.i18n("general.menu.edit.lock")));
		unlock.setHTML(Util.menuHTML("img/icon/actions/unlock.gif", Main.i18n("general.menu.edit.unlock")));
		addDocument.setHTML(Util.menuHTML("img/icon/actions/add_document.gif", Main.i18n("general.menu.file.add.document")));
		checkout.setHTML(Util.menuHTML("img/icon/actions/checkout.gif", Main.i18n("general.menu.edit.checkout")));
		checkin.setHTML(Util.menuHTML("img/icon/actions/checkin.gif", Main.i18n("general.menu.edit.checkin")));
		cancelCheckout.setHTML(Util.menuHTML("img/icon/actions/cancel_checkout.gif", Main.i18n("general.menu.edit.cancel.checkout")));
		delete.setHTML(Util.menuHTML("img/icon/actions/delete.gif", Main.i18n("general.menu.edit.delete")));
		move.setHTML(Util.menuHTML("img/icon/actions/move_document.gif", Main.i18n("general.menu.edit.move")));
		copy.setHTML(Util.menuHTML("img/icon/actions/copy.gif", Main.i18n("general.menu.edit.copy")));
		rename.setHTML(Util.menuHTML("img/icon/actions/rename.gif", Main.i18n("general.menu.edit.rename")));
		note.setHTML(Util.menuHTML("img/icon/actions/add_note.png", Main.i18n("general.menu.edit.add.note")));
		category.setHTML(Util.menuHTML("img/icon/stackpanel/table_key.gif", Main.i18n("category.add")));
		keyword.setHTML(Util.menuHTML("img/icon/actions/book_add.png", Main.i18n("keyword.add")));
		merge.setHTML(Util.menuHTML("img/icon/actions/merge_pdf.png", Main.i18n("general.menu.edit.merge.pdf")));
		addPropertyGroup
				.setHTML(Util.menuHTML("img/icon/actions/add_property_group.gif", Main.i18n("general.menu.edit.add.property.group")));
		updatePropertyGroup.setHTML(Util.menuHTML("img/icon/actions/update_property_group.png",
				Main.i18n("general.menu.edit.update.property.group")));
		removePropertyGroup.setHTML(Util.menuHTML("img/icon/actions/remove_property_group.gif",
				Main.i18n("general.menu.edit.remove.property.group")));
		addSubscription.setHTML(Util.menuHTML("img/icon/actions/add_subscription.gif", Main.i18n("general.menu.edit.add.subscription")));
		removeSubscription.setHTML(Util.menuHTML("img/icon/actions/remove_subscription.gif",
				Main.i18n("general.menu.edit.remove.subscription")));
		menuTools.setText(Main.i18n("general.menu.tools"));
		language.setHTML(Util.menuHTML("img/icon/menu/language.gif", Main.i18n("general.menu.tools.languages")));
		skin.setHTML(Util.menuHTML("img/icon/menu/skin.gif", Main.i18n("general.menu.tools.skin")));
		skinDefault.setHTML(Util.menuHTML("img/icon/menu/skin_default.gif", Main.i18n("general.menu.tools.skin.default")));
		skinMediumFont.setHTML(Util.menuHTML("img/icon/menu/skin_test.gif", Main.i18n("general.menu.tools.skin.mediumfont")));
		skinBigFont.setHTML(Util.menuHTML("img/icon/menu/skin_test.gif", Main.i18n("general.menu.tools.skin.bigfont")));
		debugConsole.setHTML(Util.menuHTML("img/icon/menu/console.gif", Main.i18n("general.menu.tools.debug.console")));
		administration.setHTML(Util.menuHTML("img/icon/menu/administration.gif", Main.i18n("general.menu.tools.administration")));
		preferences.setHTML(Util.menuHTML("img/icon/menu/preferences.gif", Main.i18n("general.menu.tools.preferences")));
		userPreferences.setHTML(Util.menuHTML("img/icon/menu/user_preferences.gif", Main.i18n("general.menu.tools.user.preferences")));
		omr.setHTML(Util.menuHTML("img/icon/actions/omr.png", Main.i18n("general.menu.tools.omr")));
		convert.setHTML(Util.menuHTML("img/icon/menu/convert.png", Main.i18n("general.menu.tools.convert")));
		menuBookmark.setText(Main.i18n("general.menu.bookmark"));
		home.setHTML(Util.menuHTML("img/icon/actions/bookmark_go.gif", Main.i18n("general.menu.bookmark.home")));
		defaultHome.setHTML(Util.menuHTML("img/icon/actions/bookmark.gif", Main.i18n("general.menu.bookmark.default.home")));
		addBookmark.setHTML(Util.menuHTML("img/icon/actions/add_bookmark.gif", Main.i18n("general.menu.bookmark.add")));
		manageBookmark.setHTML(Util.menuHTML("img/icon/actions/bookmark_edit.gif", Main.i18n("general.menu.bookmark.edit")));
		menuReports.setText(Main.i18n("general.menu.report"));
		reportFormat.setHTML(Util.menuHTML("img/icon/menu/preferences.gif", Main.i18n("general.menu.report.format")));
		refreshReportFormatMenu();
		menuTemplates.setHTML(Main.i18n("general.menu.templates"));
		menuHelp.setText(Main.i18n("general.menu.help"));
		help.setHTML(Util.menuHTML("img/icon/menu/help.gif", Util.windowOpen(Main.i18n("general.menu.help"), URI_HELP)));
		documentation.setHTML(Util.menuHTML("img/icon/menu/documentation.gif",
				Util.windowOpen(Main.i18n("general.menu.help.documentation"), URI_DOCUMENTATION)));
		bugReport.setHTML(Util.menuHTML("img/icon/menu/bugs.gif",
				Util.windowOpen(Main.i18n("general.menu.help.bug.report"), URI_BUG_REPORT)));
		supportRequest.setHTML(Util.menuHTML("img/icon/menu/support.gif",
				Util.windowOpen(Main.i18n("general.menu.help.support.request"), URI_SUPPORT_REQUEST)));
		publicForum.setHTML(Util.menuHTML("img/icon/menu/forum.gif",
				Util.windowOpen(Main.i18n("general.menu.help.public.forum"), URI_PUBLIC_FORUM)));
		versionChanges.setHTML(Util.menuHTML("img/icon/menu/brick.gif",
				Util.windowOpen(Main.i18n("general.menu.help.version.changes"), URI_VERSION_CHANGES)));
		projectWeb.setHTML(Util.menuHTML("img/icon/menu/home.gif",
				Util.windowOpen(Main.i18n("general.menu.help.project.web"), URI_PROJECT_WEB)));
		about.setHTML(Util.menuHTML("img/icon/menu/about.gif", Main.i18n("general.menu.help.about")));
	}

	/**
	 * refreshReportFormatMenu
	 */
	private void refreshReportFormatMenu() {
		switch (reportOutput) {
			case OUTPUT_PDF:
				reportFormatPdf.setHTML(Util.menuHTML("img/icon/security/yes.gif", Main.i18n("general.menu.report.format.pdf")));
				reportFormatRtf.setHTML(Util.menuHTML("img/icon/security/no.gif", Main.i18n("general.menu.report.format.rtf")));
				reportFormatCsv.setHTML(Util.menuHTML("img/icon/security/no.gif", Main.i18n("general.menu.report.format.csv")));
				enable(reportFormatPdf);
				disable(reportFormatRtf);
				disable(reportFormatCsv);
				break;

			case OUTPUT_RTF:
				reportFormatPdf.setHTML(Util.menuHTML("img/icon/security/no.gif", Main.i18n("general.menu.report.format.pdf")));
				reportFormatRtf.setHTML(Util.menuHTML("img/icon/security/yes.gif", Main.i18n("general.menu.report.format.rtf")));
				reportFormatCsv.setHTML(Util.menuHTML("img/icon/security/no.gif", Main.i18n("general.menu.report.format.csv")));
				disable(reportFormatPdf);
				enable(reportFormatRtf);
				disable(reportFormatCsv);
				break;

			case OUTPUT_CSV:
				reportFormatPdf.setHTML(Util.menuHTML("img/icon/security/no.gif", Main.i18n("general.menu.report.format.pdf")));
				reportFormatRtf.setHTML(Util.menuHTML("img/icon/security/no.gif", Main.i18n("general.menu.report.format.rtf")));
				reportFormatCsv.setHTML(Util.menuHTML("img/icon/security/yes.gif", Main.i18n("general.menu.report.format.csv")));
				disable(reportFormatPdf);
				disable(reportFormatRtf);
				enable(reportFormatCsv);
				break;
		}
	}

	/**
	 * Enables menu item
	 *
	 * @param menuItem The menu item
	 */
	public void enable(MenuItem menuItem) {
		menuItem.removeStyleName("okm-MenuItem-strike");
	}

	/**
	 * Disables the menu item with and strike
	 *
	 * @param menuItem The menu item
	 */
	public void disable(MenuItem menuItem) {
		menuItem.addStyleName("okm-MenuItem-strike");
	}

	/**
	 * Enables or disables menu option on privileges
	 */
	public void evaluateMenuOptions() {
		if (mainMenuOption.createFolderOption) {
			enable(createFolder);
		} else {
			disable(createFolder);
		}

		if (mainMenuOption.findFolderOption) {
			enable(findFolder);
		} else {
			disable(findFolder);
		}

		if (mainMenuOption.findDocumentOption) {
			enable(findDocument);
		} else {
			disable(findDocument);
		}

		if (mainMenuOption.findSimilarDocumentOption) {
			enable(findSimilarDocument);
		} else {
			disable(findSimilarDocument);
		}

		if (mainMenuOption.downloadOption) {
			enable(download);
		} else {
			disable(download);
		}

		if (mainMenuOption.downloadPdfOption) {
			enable(downloadPdf);
		} else {
			disable(downloadPdf);
		}

		if (mainMenuOption.sendDocumentLinkOption) {
			enable(sendDocumentLink);
		} else {
			disable(sendDocumentLink);
		}

		if (mainMenuOption.sendDocumentAttachmentOption) {
			enable(sendDocumentAttachment);
		} else {
			disable(sendDocumentAttachment);
		}

		if (mainMenuOption.mailForwardOption) {
			enable(forwardMail);
		} else {
			disable(forwardMail);
		}

		if (mainMenuOption.createFromTemplateOption) {
			enable(createFromTemplate);
		} else {
			disable(createFromTemplate);
		}

		if (mainMenuOption.exportOption) {
			enable(export);
		} else {
			disable(export);
		}

		if (mainMenuOption.workflowOption) {
			enable(startWorkflow);
		} else {
			disable(startWorkflow);
		}

		if (mainMenuOption.refreshOption) {
			enable(refresh);
		} else {
			disable(refresh);
		}

		if (mainMenuOption.restore) {
			enable(restore);
		} else {
			disable(restore);
		}

		if (mainMenuOption.purge) {
			enable(purge);
		} else {
			disable(purge);
		}

		if (mainMenuOption.lockOption) {
			enable(lock);
		} else {
			disable(lock);
		}

		if (mainMenuOption.unLockOption) {
			enable(unlock);
		} else {
			disable(unlock);
		}

		if (mainMenuOption.addDocumentOption) {
			enable(addDocument);
		} else {
			disable(addDocument);
		}

		if (mainMenuOption.checkoutOption) {
			enable(checkout);
		} else {
			disable(checkout);
		}

		if (mainMenuOption.checkinOption) {
			enable(checkin);
		} else {
			disable(checkin);
		}

		if (mainMenuOption.cancelCheckoutOption) {
			enable(cancelCheckout);
		} else {
			disable(cancelCheckout);
		}

		if (mainMenuOption.deleteOption) {
			enable(delete);
		} else {
			disable(delete);
		}

		if (mainMenuOption.copyOption) {
			enable(copy);
		} else {
			disable(copy);
		}

		if (mainMenuOption.moveOption) {
			enable(move);
		} else {
			disable(move);
		}

		if (mainMenuOption.addNoteOption) {
			enable(note);
		} else {
			disable(note);
		}

		if (mainMenuOption.addCategoryOption) {
			enable(category);
		} else {
			disable(category);
		}

		if (mainMenuOption.addKeywordOption) {
			enable(keyword);
		} else {
			disable(keyword);
		}

		if (mainMenuOption.mergePdfOption) {
			enable(merge);
		} else {
			disable(merge);
		}

		if (mainMenuOption.renameOption) {
			enable(rename);
		} else {
			disable(rename);
		}

		if (mainMenuOption.addPropertyGroupOption) {
			enable(addPropertyGroup);
		} else {
			disable(addPropertyGroup);
		}

		if (mainMenuOption.updatePropertyGroupOption) {
			enable(updatePropertyGroup);
		} else {
			disable(updatePropertyGroup);
		}

		if (mainMenuOption.addSubscription) {
			enable(addSubscription);
		} else {
			disable(addSubscription);
		}

		if (mainMenuOption.removeSubscription) {
			enable(removeSubscription);
		} else {
			disable(removeSubscription);
		}

		if (mainMenuOption.omrOption) {
			enable(omr);
		} else {
			disable(omr);
		}
		
		if (mainMenuOption.convertOption) {
			enable(convert);
		} else {
			disable(convert);
		}

		// Property group is evaluated in toolbar getAllGroups()
		if (mainMenuOption.homeOption) {
			enable(home);
			enable(defaultHome);
			enable(addBookmark);
		} else {
			disable(home);
			disable(defaultHome);
			disable(addBookmark);
		}

		for (MenuItem item : templateMenuItems) {
			if (mainMenuOption.createFromTemplateOption) {
				enable(item);
			} else {
				disable(item);
			}
		}
	}

	/**
	 * enableAddPropertyGroup
	 */
	public void enableAddPropertyGroup() {
		if (mainMenuOption != null) { // Condition caused by loading case
			mainMenuOption.addPropertyGroupOption = true;
		}
		enable(addPropertyGroup);
	}

	/**
	 * disableAddPropertyGroup
	 */
	public void disableAddPropertyGroup() {
		if (mainMenuOption != null) { // Condition caused by loading case
			mainMenuOption.addPropertyGroupOption = false;
		}
		disable(addPropertyGroup);
	}

	/**
	 * enableUpdatePropertyGroup
	 */
	public void enableUpdatePropertyGroup() {
		if (mainMenuOption != null) { // Condition caused by loading case
			mainMenuOption.updatePropertyGroupOption = true;
		}
		enable(updatePropertyGroup);
	}

	/**
	 * disableAddPropertyGroup
	 */
	public void disableUpdatePropertyGroup() {
		if (mainMenuOption != null) { // Condition caused by loading case
			mainMenuOption.updatePropertyGroupOption = false;
		}
		disable(updatePropertyGroup);
	}

	/**
	 * enablePdfMerge
	 */
	public void enablePdfMerge() {
		if (mainMenuOption != null) { // Condition caused by loading case
			mainMenuOption.mergePdfOption = true;
		}
		enable(merge);
	}

	/**
	 * disablePdfMerge
	 */
	public void disablePdfMerge() {
		if (mainMenuOption != null) { // Condition caused by loading case
			mainMenuOption.mergePdfOption = false;
		}
		disable(merge);
	}

	/**
	 * enableRemovePropertyGroup
	 */
	public void enableRemovePropertyGroup() {
		if (mainMenuOption != null) { // Condition caused by loading case
			mainMenuOption.removePropertyGroupOption = true;
		}
		enable(removePropertyGroup);
	}

	/**
	 * disableRemovePropertyGroup
	 */
	public void disableRemovePropertyGroup() {
		if (mainMenuOption != null) { // Condition caused by loading case
			mainMenuOption.removePropertyGroupOption = false;
		}
		disable(removePropertyGroup);
	}

	/**
	 * disableAllOptions
	 */
	public void disableAllOptions() {
		mainMenuOption = new ToolBarOption();
		evaluateMenuOptions();
	}

	/**
	 * Sets the main menu options
	 *
	 * @param mainMenuOption The manin Menu options
	 */
	public void setOptions(ToolBarOption mainMenuOption) {
		this.mainMenuOption = mainMenuOption;
		evaluateMenuOptions();
	}

	// Command menu to restore
	Command restoreOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.restore) {
				Main.get().mainPanel.topPanel.toolBar.executeRestore();
			}
		}
	};

	// Command menu to purge
	Command purgeOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.purge) {
				Main.get().mainPanel.topPanel.toolBar.executePurge();
			}
		}
	};

	// Command menu to purge trash
	Command purgeTrashOKM = new Command() {
		@Override
		public void execute() {
			Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_EMPTY_TRASH);
			Main.get().confirmPopup.show();
		}
	};

	// Command menu to create folder
	Command createFolderOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.createFolderOption) {
				Main.get().mainPanel.topPanel.toolBar.executeFolderDirectory();
			}
		}
	};

	// Command menu to find folder
	Command findFolderOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.findFolderOption) {
				Main.get().mainPanel.topPanel.toolBar.executeFindFolder();
			}
		}
	};

	// Command menu to find document
	Command findDocumentOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.findDocumentOption) {
				Main.get().mainPanel.topPanel.toolBar.executeFindDocument();
			}
		}
	};

	// Command menu to find similar document
	Command findSimilarDocumentOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.findSimilarDocumentOption) {
				Main.get().mainPanel.topPanel.toolBar.executeFindSimilarDocument();
			}
		}
	};

	// Command menu to download
	Command downloadOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.downloadOption) {
				Main.get().mainPanel.topPanel.toolBar.executeDownload();
			}
		}
	};

	// Command menu to download as PDF
	Command downloadPdfOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.downloadPdfOption) {
				Main.get().mainPanel.topPanel.toolBar.executeDownloadPdf();
			}
		}
	};

	// Command menu to send document link
	Command sendDocumentLinkOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.sendDocumentLinkOption) {
				Main.get().notifyPopup.executeSendDocument(NotifyPopup.NOTIFY_WITH_LINK);
			}
		}
	};

	// Command menu to send document attachment
	Command sendDocumentAttachmentOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.sendDocumentAttachmentOption) {
				Main.get().notifyPopup.executeSendDocument(NotifyPopup.NOTIFY_WITH_ATTACHMENT);
			}
		}
	};

	// Command menu to forward mail
	Command sendMailForwardOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.mailForwardOption) {
				Main.get().notifyPopup.executeForwardMail();
			}
		}
	};

	// Command menu to send document attachment
	Command createFromTemplateOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.createFromTemplateOption) {
				Main.get().mainPanel.topPanel.toolBar.executeCreateFromTemplate();
			}
		}
	};

	// Command menu to export
	Command exportToFile = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.exportOption) {
				Main.get().mainPanel.topPanel.toolBar.executeExport();
			}
		}
	};

	// Command menu to start workflow
	Command startWorkflowOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.workflowOption) {
				Main.get().mainPanel.topPanel.toolBar.executeAddWorkflow();
			}
		}
	};

	// Command menu to lock
	Command lockOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.lockOption) {
				Main.get().mainPanel.topPanel.toolBar.executeLock();
			}
		}
	};

	// Command menu to unlock
	Command unlockOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.unLockOption) {
				Main.get().mainPanel.topPanel.toolBar.executeUnlock();
			}
		}
	};

	// Command menu to add documen
	Command addDocumentOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.addDocumentOption) {
				Main.get().mainPanel.topPanel.toolBar.executeAddDocument();
			}
		}
	};

	// Command menu to edit (checkout)
	Command checkoutOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.checkoutOption) {
				Main.get().mainPanel.topPanel.toolBar.executeCheckout();
			}
		}
	};

	// Command menu to checkin
	Command checkinOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.checkinOption) {
				Main.get().mainPanel.topPanel.toolBar.executeCheckin();
			}
		}
	};

	// Command menu to cancel checkout
	Command cancelCheckoutOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.cancelCheckoutOption) {
				Main.get().mainPanel.topPanel.toolBar.executeCancelCheckout();
			}
		}
	};

	// Command menu to copy
	Command copyOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.copyOption) {
				Main.get().mainPanel.topPanel.toolBar.executeCopy();
			}
		}
	};

	// Command menu to copy
	Command moveOKM = new Command() {
		@Override
		public void execute() {
			Main.get().mainPanel.topPanel.toolBar.executeMove();
		}
	};

	// Command menu to copy
	Command renameOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.renameOption) {
				Main.get().mainPanel.topPanel.toolBar.executeRename();
			}
		}
	};

	// Command menu to add note
	Command addNote = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.addNoteOption) {
				Main.get().mainPanel.topPanel.toolBar.addNote();
			}
		}
	};

	// Command menu to add category
	Command addCategory = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.addCategoryOption) {
				Main.get().mainPanel.topPanel.toolBar.addCategory();
			}
		}
	};

	// Command menu to add keyword
	Command addKeyword = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.addKeywordOption) {
				Main.get().mainPanel.topPanel.toolBar.addKeyword();
			}
		}
	};

	// Command menu to mergePdf
	Command mergePdf = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.mergePdfOption) {
				Main.get().mainPanel.topPanel.toolBar.mergePdf();
			}
		}
	};

	// Command menu to add property group
	Command addPropertyGroupOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.addPropertyGroupOption) {
				Main.get().mainPanel.topPanel.toolBar.addPropertyGroup();
			}
		}
	};

	// Command menu to update property group
	Command updatePropertyGroupOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.updatePropertyGroupOption) {
				Main.get().mainPanel.topPanel.toolBar.updatePropertyGroup();
			}
		}
	};

	// Command menu to remove property group
	Command removePropertyGroupOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.removePropertyGroupOption) {
				Main.get().mainPanel.topPanel.toolBar.executeRemovePropertyGroup();
			}
		}
	};

	// Command menu to add subscription
	Command addSubscriptionOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.addSubscription) {
				Main.get().mainPanel.topPanel.toolBar.executeAddSubscription();
			}
		}
	};

	// Command menu to add subscription
	Command removeSubscriptionOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.removeSubscription) {
				Main.get().mainPanel.topPanel.toolBar.executeRemoveSubscription();
			}
		}
	};

	// Command menu to delete
	Command deleteOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.deleteOption) {
				Main.get().mainPanel.topPanel.toolBar.executeDelete();
			}
		}
	};

	// Command menu to delete
	Command refreshOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.refreshOption) {
				Main.get().mainPanel.topPanel.toolBar.executeRefresh();
			}
		}
	};

	// Command menu to exit application
	Command exitOKM = new Command() {
		@Override
		public void execute() {
			if (Main.get().mainPanel.dashboard.userDashboard.getCheckouts() > 0) {
				Main.get().mainPanel.dashboard.changeView(UIDashboardConstants.DASHBOARD_USER);
				Main.get().mainPanel.topPanel.tabWorkspace.changeSelectedTab(UIDockPanelConstants.DASHBOARD);
				Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_LOGOUT_DOCUMENTS_CHECKOUT);
				Main.get().confirmPopup.show();
			} else {
				Main.get().logoutPopup.logout();
			}
		}
	};

	// Command menu to show about
	Command aboutOKM = new Command() {
		@Override
		public void execute() {
			Main.get().aboutPopup.show();
		}
	};

	// Command menu to set default skin
	Command setSkinDefault = new Command() {
		@Override
		public void execute() {
			Util.changeCss("default");
		}
	};

	// Command menu to set test skin
	Command setSkinDefault2 = new Command() {
		@Override
		public void execute() {
			Util.changeCss("test");
		}
	};

	// Command menu to set test skin
	Command setSkinMediumFont = new Command() {
		@Override
		public void execute() {
			Util.changeCss("mediumfont");
		}
	};

	// Command menu to set test skin
	Command setSkinBigFont = new Command() {
		@Override
		public void execute() {
			Util.changeCss("bigfont");
		}
	};

	// Command menu to show debug console
	Command setViewDebugConsole = new Command() {
		@Override
		public void execute() {
			Main.get().debugConsolePopup.center();
		}
	};

	// Command menu to show administration
	Command showAdministration = new Command() {
		@Override
		public void execute() {
			Window.open(Main.CONTEXT + "/admin/index.jsp", "Administration", "");
		}
	};

	// Command menu to go to set user preferences
	Command setUserPreferences = new Command() {
		@Override
		public void execute() {
			Main.get().userPopup.reset();
		}
	};

	// Command menu to go to set omr capture
	Command executeOmr = new Command() {
		public void execute() {
			if (mainMenuOption.omrOption) {
				Main.get().mainPanel.topPanel.toolBar.executeOmr();
			}
		}
	};
	
	// Command menu to convert
	Command showConvert = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.convertOption) {
				Main.get().mainPanel.topPanel.toolBar.executeConvert();
			}
		}
	};

	// Command enable pdf report format
	Command enablePdfReporFormat = new Command() {
		@Override
		public void execute() {
			reportOutput = OUTPUT_PDF;
			refreshReportFormatMenu();
		}
	};

	// Command enable text report format
	Command enableTextReporFormat = new Command() {
		@Override
		public void execute() {
			reportOutput = OUTPUT_RTF;
			refreshReportFormatMenu();
		}
	};

	// Command enable CSV report format
	Command enableCsvReporFormat = new Command() {
		@Override
		public void execute() {
			reportOutput = OUTPUT_CSV;
			refreshReportFormatMenu();
		}
	};

	// Command menu to go to user home
	Command goToUserHome = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.homeOption) {
				Main.get().mainPanel.topPanel.toolBar.executeGoToUserHome();
			}
		}
	};

	// Command menu to go to user home
	Command setDefaultHome = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.homeOption) {
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
					Main.get().mainPanel.desktop.browser.fileBrowser.setHome();
				} else if (Main.get().activeFolderTree.isPanelSelected()) {
					Main.get().activeFolderTree.setHome();
				}
			}
		}
	};

	// Command menu to add bookmark
	Command addBookmarkOKM = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.bookmarkOption) {
				Main.get().mainPanel.topPanel.toolBar.executeAddBookmark();
			}
		}
	};

	// Command menu to go to user home
	Command editBookmark = new Command() {
		@Override
		public void execute() {
			if (mainMenuOption.homeOption) {
				manageBookmarkPopup.showPopup();
			}
		}
	};

	// Command menu that executes void
	Command nullExecute = new Command() {
		@Override
		public void execute() {
		}
	};

	/**
	 * Gets the tools bar options
	 *
	 * @return The tool bar options values
	 */
	public ToolBarOption getToolBarOption() {
		return mainMenuOption;
	}

	/**
	 * setAvailableOption
	 *
	 * @param option
	 */
	public void setAvailableOption(GWTWorkspace workspace) {
		GWTAvailableOption option = workspace.getAvailableOption();

		// FILE MENU
		createFolder.setVisible(option.isCreateFolderOption());
		addDocument.setVisible(option.isAddDocumentOption());
		findFolder.setVisible(option.isFindFolderOption());
		findDocument.setVisible(option.isFindDocumentOption());
		findSimilarDocument.setVisible(option.isSimilarDocumentVisible());
		download.setVisible(option.isDownloadOption());
		downloadPdf.setVisible(option.isDownloadPdfOption());
		sendDocumentLink.setVisible(option.isSendDocumentLinkOption());
		sendDocumentAttachment.setVisible(option.isSendDocumentAttachmentOption());
		forwardMail.setVisible(option.isForwardMailOption());
		createFromTemplate.setVisible(option.isCreateFromTemplateOption());
		export.setVisible(option.isExportOption());
		horizontalLineFile1.setVisible(option.isCreateFolderOption() || option.isFindFolderOption() || option.isFindDocumentOption()
				|| option.isSimilarDocumentVisible() || option.isAddDocumentOption() || option.isDownloadOption()
				|| option.isDownloadPdfOption() || option.isSendDocumentLinkOption() || option.isSendDocumentAttachmentOption()
				|| option.isExportOption() || option.isCreateFromTemplateOption());
		startWorkflow.setVisible(option.isWorkflowOption());
		horizontalLineFile2.setVisible(option.isWorkflowOption());
		refresh.setVisible(option.isRefreshOption());
		horizontalLineFile3.setVisible(option.isRefreshOption());
		restore.setVisible(option.isRestoreOption());
		purge.setVisible(option.isPurgeOption());
		purgeTrash.setVisible(option.isPurgeTrashOption());
		horizontalLineFile4.setVisible(option.isPurgeTrashOption() || option.isPurgeOption() || option.isRestoreOption());

		// EDIT MENU
		lock.setVisible(option.isLockOption());
		unlock.setVisible(option.isUnLockOption());
		horizontalLineEdit1.setVisible(option.isLockOption() || option.isUnLockOption());
		checkout.setVisible(option.isCheckoutOption());
		checkin.setVisible(option.isCheckinOption());
		cancelCheckout.setVisible(option.isCancelCheckoutOption());
		horizontalLineEdit2.setVisible(option.isCheckoutOption() || option.isCheckinOption() || option.isCancelCheckoutOption());
		delete.setVisible(option.isDeleteOption());
		copy.setVisible(option.isCopyOption());
		move.setVisible(option.isMoveOption());
		rename.setVisible(option.isRenameOption());
		note.setVisible(option.isAddNoteOption());
		category.setVisible(option.isAddCategoryOption());
		keyword.setVisible(option.isAddKeywordOption());
		merge.setVisible(option.isMergePdfOption());
		horizontalLineEdit3.setVisible(option.isDeleteOption() || option.isCopyOption() || option.isMoveOption() || option.isRenameOption()
				|| option.isAddNoteOption() || option.isAddCategoryOption() || option.isAddKeywordOption() || option.isMergePdfOption());
		addPropertyGroup.setVisible(option.isAddPropertyGroupOption());
		updatePropertyGroup.setVisible(option.isUpdatePropertyGroupOption());
		removePropertyGroup.setVisible(option.isRemovePropertyGroupOption());
		horizontalLineEdit4.setVisible(option.isAddPropertyGroupOption() || option.isUpdatePropertyGroupOption()
				|| option.isRemovePropertyGroupOption());
		addSubscription.setVisible(option.isAddSubscriptionOption());
		removeSubscription.setVisible(option.isRemoveSubscriptionOption());

		// MENU TOOLS
		if (!option.isLanguagesOption()) {
			subMenuTools.removeItem(language);
		}
		if (!option.isSkinOption()) {
			subMenuTools.removeItem(skin);
		}
		debugConsole.setVisible(option.isDebugOption());
		administration.setVisible(option.isAdministrationOption());
		if (!option.isPreferencesOption()) {
			subMenuTools.removeItem(preferences);
		}
		omr.setVisible(option.isOmr());
		convert.setVisible(option.isConvertOption());

		// MENU BOOKMARKS
		home.setVisible(option.isHomeOption());
		defaultHome.setVisible(option.isAddBookmarkOption());
		addBookmark.setVisible(option.isAddBookmarkOption());
		manageBookmark.setVisible(option.isManageBookmarkOption());
		horizontalLineBookmark1.setVisible(option.isHomeOption() || option.isAddBookmarkOption() || option.isAddBookmarkOption());

		// MENU REPORTS
		if (workspace.getReports().size() > 0) {
			menuReports.setVisible(true);
			for (final GWTReport report : workspace.getReports()) {
				MenuItem reportMenuItem = new MenuItem(Util.menuHTML("img/icon/menu/report.png", report.getName()), true, new Command() {
					@Override
					public void execute() {
						if (report.getFormElements().size() > 0) {
							Main.get().reportPopup.setReport(report);
							Main.get().reportPopup.center();
						} else {
							Map<String, String> parameters = new HashMap<String, String>();
							parameters.put("format", String.valueOf(reportOutput));
							Util.executeReport(report.getId(), parameters);
						}
					}
				});
				reportMenuItem.addStyleName("okm-MainMenuItem");
				subMenuReports.addItem(reportMenuItem);
			}
		}
		// MENU HELP
		help.setVisible(option.isHelpOption());
		documentation.setVisible(option.isDocumentationOption());
		bugReport.setVisible(option.isBugReportOption());
		supportRequest.setVisible(option.isSupportRequestOption());
		publicForum.setVisible(option.isPublicForumOption());
		versionChanges.setVisible(option.isVersionChangesOption());
		projectWeb.setVisible(option.isProjectWebOption());
		about.setVisible(option.isAboutOption());
	}

	/**
	 * setEditMenuVisible
	 *
	 * @param visible
	 */
	public void setEditMenuVisible(boolean visible) {
		menuEdit.setVisible(visible);
	}

	/**
	 * setToolsMenuVisible
	 *
	 * @param visible
	 */
	public void setToolsMenuVisible(boolean visible) {
		menuTools.setVisible(visible);
	}

	/**
	 * setBookmarkMenuVisible
	 *
	 * @param visible
	 */
	public void setBookmarkMenuVisible(boolean visible) {
		menuBookmark.setVisible(visible);
	}

	/**
	 * setTemplatesMenuVisible
	 *
	 * @param visible
	 */
	public void setTemplatesMenuVisible(boolean visible) {
		menuTemplates.setVisible(visible);
	}

	/**
	 * setHelpMenuVisible
	 *
	 * @param visible
	 */
	public void setHelpMenuVisible(boolean visible) {
		menuHelp.setVisible(visible);
	}

	/**
	 * setFileMenuVisible
	 *
	 * @param visible
	 */
	public void setFileMenuVisible(boolean visible) {
		menuFile.setVisible(visible);
	}

	/**
	 * addMenu
	 *
	 * @param extension
	 */
	public void addMenuExtension(MenuItemExtension extension) {
		switch (extension.getMenuLocation()) {
			case UIMenuConstants.NEW_MENU:
				mainMenu.addItem(extension);
				break;
			case UIMenuConstants.MAIN_MENU_FILE:
				subMenuFile.addItem(extension);
				break;
			case UIMenuConstants.MAIN_MENU_EDIT:
				subMenuEdit.addItem(extension);
				break;
			case UIMenuConstants.MAIN_MENU_TOOLS:
				subMenuTools.addItem(extension);
				break;
			case UIMenuConstants.MAIN_MENU_BOOKMARS:
				subMenuBookmark.addItem(extension);
				break;
			case UIMenuConstants.MAIN_MENU_REPORTS:
				subMenuReports.addItem(extension);
				menuReports.setVisible(true);
				break;
			case UIMenuConstants.MAIN_MENU_HELP:
				subMenuHelp.addItem(extension);
				break;
		}

	}

	/**
	 * getReportOutput
	 */
	public int getReportOutput() {
		return reportOutput;
	}

	/**
	 * initAvailableLanguage
	 */
	public void initAvailableLanguage(List<GWTLanguage> langs) {
		for (final GWTLanguage lang : langs) {
			MenuItem menuItem = new MenuItem(Util.flagMenuHTML(lang.getId(), lang.getName()), true, new Command() {
				@Override
				public void execute() {
					Main.get().refreshLang(lang.getId());
				}
			});

			menuItem.addStyleName("okm-MainMenuItem");
			subMenuLanguage.addItem(menuItem);
		}
	}

	/**
	 * refreshAvailableTemplates
	 */
	public void refreshAvailableTemplates() {
		if (menuTemplates.isVisible()) {
			documentService.getAllTemplates(new AsyncCallback<List<GWTDocument>>() {
				@Override
				public void onSuccess(List<GWTDocument> result) {
					subMenuTemplates.clearItems(); // Remove all items
					fldMenus = new HashMap<String, MenuBar>();
					templateMenuItems = new ArrayList<MenuItem>();
					fldMenus.put(GWTRepository.TEMPLATES, subMenuTemplates);

					for (final GWTDocument doc : result) {
						String[] paths = doc.getPath().split("/");
						String actualPath = GWTRepository.TEMPLATES;

						for (int i = 2; i < paths.length - 1; i++) {
							// Evaluates if new path exist otherside creates
							if (!fldMenus.containsKey(actualPath + "/" + paths[i])) {
								createSubMenu(actualPath, paths[i]); // Menuitem
								// folder
							}

							actualPath += "/" + paths[i];
						}

						MenuItem docItem = new MenuItem(Util.menuHTML("../mime/" + doc.getMimeType(), doc.getName()), true, new Command() {
							@Override
							public void execute() {
								if (mainMenuOption.createFromTemplateOption) {
									Main.get().templatePopup.reset(doc, Main.get().activeFolderTree.getActualPath(), false);
									Main.get().templatePopup.center();
								}
							}
						});
						docItem.addStyleName("okm-MainMenuItem");
						fldMenus.get(actualPath).addItem(docItem);
						templateMenuItems.add(docItem);

						if (mainMenuOption.createFromTemplateOption) {
							enable(docItem);
						} else {
							disable(docItem);
						}
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("getAllTemplatesByType", caught);
				}
			});
		}
	}

	/**
	 * createSubMenu
	 */
	private void createSubMenu(String parentFolderPath, String fldName) {
		MenuBar subMenuFolder = new MenuBar(true);
		subMenuFolder.setStyleName("okm-SubMenuBar");

		MenuItem menuFolder = new MenuItem(Util.menuHTML("img/menuitem_empty.gif", fldName), true, subMenuFolder);
		menuFolder.addStyleName("okm-MainMenuItem");

		fldMenus.put(parentFolderPath + "/" + fldName, subMenuFolder);
		fldMenus.get(parentFolderPath).addItem(menuFolder);
	}

	/**
	 * startRefreshingMenus
	 */
	public void startRefreshingMenus(double scheduleTime) {
		menusRefreshing = new Timer() {
			@Override
			public void run() {
				refreshAvailableTemplates();
			}
		};

		menusRefreshing.scheduleRepeating(new Double(scheduleTime).intValue());
	}
}
