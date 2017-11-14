/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017  Paco Avila & Josep Llort
 * <p>
 * No bytes were intentionally harmed during the development of this application.
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General License for more details.
 * <p>
 * You should have received a copy of the GNU General License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.frontend.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * OKMBundleResources
 *
 * @author jllort
 *
 */
public interface OKMBundleResources extends ClientBundle {
	OKMBundleResources INSTANCE = GWT.create(OKMBundleResources.class);

	@Source("com/openkm/frontend/public/img/icon/actions/delete.gif")
	ImageResource deleteIcon();

	@Source("com/openkm/frontend/public/img/icon/actions/clean.png")
	ImageResource cleanIcon();

	@Source("com/openkm/frontend/public/img/icon/stackpanel/book_open.gif")
	ImageResource bookOpenIcon();

	@Source("com/openkm/frontend/public/img/icon/stackpanel/table_key.gif")
	ImageResource tableKeyIcon();

	@Source("com/openkm/frontend/public/img/icon/toolbar/user.png")
	ImageResource userIcon();

	@Source("com/openkm/frontend/public/img/icon/toolbar/mail.png")
	ImageResource mailIcon();

	@Source("com/openkm/frontend/public/img/icon/toolbar/news.png")
	ImageResource newsIcon();

	@Source("com/openkm/frontend/public/img/icon/toolbar/general.png")
	ImageResource generalIcon();

	@Source("com/openkm/frontend/public/img/icon/toolbar/workflow.png")
	ImageResource workflowIcon();

	@Source("com/openkm/frontend/public/img/icon/toolbar/keyword_map.png")
	ImageResource keywordMapIcon();

	@Source("com/openkm/frontend/public/img/icon/actions/add_folder.gif")
	ImageResource createFolder();

	@Source("com/openkm/frontend/public/img/icon/actions/add_folder_disabled.gif")
	ImageResource createFolderDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/folder_find.gif")
	ImageResource findFolder();

	@Source("com/openkm/frontend/public/img/icon/actions/folder_find_disabled.gif")
	ImageResource findFolderDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/document_find.png")
	ImageResource findDocument();

	@Source("com/openkm/frontend/public/img/icon/actions/document_find_disabled.png")
	ImageResource findDocumentDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/lock.gif")
	ImageResource lock();

	@Source("com/openkm/frontend/public/img/icon/actions/lock_disabled.gif")
	ImageResource lockDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/unlock.gif")
	ImageResource unLock();

	@Source("com/openkm/frontend/public/img/icon/actions/unlock_disabled.gif")
	ImageResource unLockDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/add_document.gif")
	ImageResource addDocument();

	@Source("com/openkm/frontend/public/img/icon/actions/add_document_disabled.gif")
	ImageResource addDocumentDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/delete.gif")
	ImageResource delete();

	@Source("com/openkm/frontend/public/img/icon/actions/delete_disabled.gif")
	ImageResource deleteDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/checkout.gif")
	ImageResource checkout();

	@Source("com/openkm/frontend/public/img/icon/actions/checkout_disabled.gif")
	ImageResource checkoutDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/checkin.gif")
	ImageResource checkin();

	@Source("com/openkm/frontend/public/img/icon/actions/checkin_disabled.gif")
	ImageResource checkinDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/cancel_checkout.gif")
	ImageResource cancelCheckout();

	@Source("com/openkm/frontend/public/img/icon/actions/cancel_checkout_disabled.gif")
	ImageResource cancelCheckoutDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/download.gif")
	ImageResource download();

	@Source("com/openkm/frontend/public/img/icon/actions/download_disabled.gif")
	ImageResource downloadDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/download_pdf.gif")
	ImageResource downloadPdf();

	@Source("com/openkm/frontend/public/img/icon/actions/download_pdf_disabled.gif")
	ImageResource downloadPdfDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/add_property_group.gif")
	ImageResource addPropertyGroup();

	@Source("com/openkm/frontend/public/img/icon/actions/add_property_group_disabled.gif")
	ImageResource addPropertyGroupDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/remove_property_group.gif")
	ImageResource removePropertyGroup();

	@Source("com/openkm/frontend/public/img/icon/actions/remove_property_group_disabled.gif")
	ImageResource removePropertyGroupDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/start_workflow.gif")
	ImageResource startWorkflow();

	@Source("com/openkm/frontend/public/img/icon/actions/start_workflow_disabled.gif")
	ImageResource startWorkflowDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/add_subscription.gif")
	ImageResource addSubscription();

	@Source("com/openkm/frontend/public/img/icon/actions/add_subscription_disabled.gif")
	ImageResource addSubscriptionDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/remove_subscription.gif")
	ImageResource removeSubscription();

	@Source("com/openkm/frontend/public/img/icon/actions/remove_subscription_disabled.gif")
	ImageResource removeSubscriptionDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/propose_subscription.png")
	ImageResource proposeSubscription();

	@Source("com/openkm/frontend/public/img/icon/actions/bookmark_go.gif")
	ImageResource home();

	@Source("com/openkm/frontend/public/img/icon/actions/bookmark_go_disabled.gif")
	ImageResource homeDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/refresh.gif")
	ImageResource refresh();

	@Source("com/openkm/frontend/public/img/icon/actions/refresh_disabled.gif")
	ImageResource refreshDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/scanner_disabled.gif")
	ImageResource scannerDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/scanner.gif")
	ImageResource scanner();

	@Source("com/openkm/frontend/public/img/icon/actions/upload_disabled.gif")
	ImageResource uploaderDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/upload.gif")
	ImageResource uploader();

	@Source("com/openkm/frontend/public/img/icon/chat/chat_disconnected.png")
	ImageResource chatDisconnected();

	@Source("com/openkm/frontend/public/img/icon/chat/chat_connected.png")
	ImageResource chatConnected();

	@Source("com/openkm/frontend/public/img/icon/chat/new_chat_room.png")
	ImageResource newChatRoom();

	@Source("com/openkm/frontend/public/img/icon/chat/add_user.png")
	ImageResource addUserToChatRoom();

	@Source("com/openkm/frontend/public/img/icon/connect.gif")
	ImageResource openkmConnected();

	@Source("com/openkm/frontend/public/img/icon/user_repository.gif")
	ImageResource repositorySize();

	@Source("com/openkm/frontend/public/img/icon/subscribed.gif")
	ImageResource subscribed();

	@Source("com/openkm/frontend/public/img/icon/news_alert.gif")
	ImageResource newsAlert();

	@Source("com/openkm/frontend/public/img/icon/news.gif")
	ImageResource news();

	@Source("com/openkm/frontend/public/img/icon/workflow_tasks.gif")
	ImageResource workflowTasks();

	@Source("com/openkm/frontend/public/img/icon/workflow_tasks_alert.gif")
	ImageResource workflowTasksAlert();

	@Source("com/openkm/frontend/public/img/icon/workflow_pooled_tasks.gif")
	ImageResource workflowPooledTasks();

	@Source("com/openkm/frontend/public/img/icon/workflow_pooled_tasks_alert.gif")
	ImageResource workflowPooledTasksAlert();

	@Source("com/openkm/frontend/public/img/icon/warning.gif")
	ImageResource warning();

	@Source("com/openkm/frontend/public/img/icon/toolbar/separator.gif")
	ImageResource separator();

	@Source("com/openkm/frontend/public/img/zoom_out.gif")
	ImageResource zoomOut();

	@Source("com/openkm/frontend/public/img/zoom_in.gif")
	ImageResource zoomIn();

	@Source("com/openkm/frontend/public/img/viewed.gif")
	ImageResource viewed();

	@Source("com/openkm/frontend/public/img/viewed_pending.gif")
	ImageResource pending();

	@Source("com/openkm/frontend/public/img/feed.png")
	ImageResource feed();

	@Source("com/openkm/frontend/public/img/icon/loaded.gif")
	ImageResource loadedIcon();

	@Source("com/openkm/frontend/public/img/icon/loaded_disabled.gif")
	ImageResource loadedDisabledIcon();

	@Source("com/openkm/frontend/public/img/icon/loaded_error.gif")
	ImageResource loadedErrorIcon();

	@Source("com/openkm/frontend/public/img/icon/security/add.gif")
	ImageResource add();

	@Source("com/openkm/frontend/public/img/icon/security/remove.gif")
	ImageResource remove();

	@Source("com/openkm/frontend/public/img/icon/quota/quota1.gif")
	ImageResource quota1();

	@Source("com/openkm/frontend/public/img/icon/quota/quota2.gif")
	ImageResource quota2();

	@Source("com/openkm/frontend/public/img/icon/quota/quota3.gif")
	ImageResource quota3();

	@Source("com/openkm/frontend/public/img/icon/quota/quota4.gif")
	ImageResource quota4();

	@Source("com/openkm/frontend/public/img/icon/quota/quota5.gif")
	ImageResource quota5();

	@Source("com/openkm/frontend/public/img/icon/quota/quota6.gif")
	ImageResource quota6();

	@Source("com/openkm/frontend/public/img/icon/search/calendar.gif")
	ImageResource calendar();

	@Source("com/openkm/frontend/public/img/icon/search/calendar_disabled.gif")
	ImageResource calendarDisabled();

	@Source("com/openkm/frontend/public/img/icon/security/yes.gif")
	ImageResource yes();

	@Source("com/openkm/frontend/public/img/icon/security/no.gif")
	ImageResource no();

	@Source("com/openkm/frontend/public/img/icon/actions/comment_edit.gif")
	ImageResource noteEdit();

	@Source("com/openkm/frontend/public/img/icon/actions/comment_delete.gif")
	ImageResource noteDelete();

	@Source("com/openkm/frontend/public/img/icon/search/folder_explore.gif")
	ImageResource folderExplorer();

	@Source("com/openkm/frontend/public/img/indicator.gif")
	ImageResource indicator();

	@Source("com/openkm/frontend/public/img/icon/actions/share_query.gif")
	ImageResource sharedQuery();

	@Source("com/openkm/frontend/public/img/icon/actions/printer.png")
	ImageResource print();

	@Source("com/openkm/frontend/public/img/icon/actions/printer_disabled.png")
	ImageResource printDisabled();

	@Source("com/openkm/frontend/public/img/icon/editor/justifyCenter.gif")
	ImageResource justifyCenter();

	@Source("com/openkm/frontend/public/img/icon/editor/justify.gif")
	ImageResource justify();

	@Source("com/openkm/frontend/public/img/icon/editor/justifyLeft.gif")
	ImageResource justifyLeft();

	@Source("com/openkm/frontend/public/img/icon/editor/justifyRight.gif")
	ImageResource justifyRight();

	@Source("com/openkm/frontend/public/img/icon/editor/bold.gif")
	ImageResource bold();

	@Source("com/openkm/frontend/public/img/icon/editor/italic.gif")
	ImageResource italic();

	@Source("com/openkm/frontend/public/img/icon/editor/underline.gif")
	ImageResource underline();

	@Source("com/openkm/frontend/public/img/icon/editor/stroke.gif")
	ImageResource stroke();

	@Source("com/openkm/frontend/public/img/icon/editor/subscript.gif")
	ImageResource subScript();

	@Source("com/openkm/frontend/public/img/icon/editor/superscript.gif")
	ImageResource superScript();

	@Source("com/openkm/frontend/public/img/icon/editor/unordered.gif")
	ImageResource unOrdered();

	@Source("com/openkm/frontend/public/img/icon/editor/ordered.gif")
	ImageResource ordered();

	@Source("com/openkm/frontend/public/img/icon/editor/identLeft.gif")
	ImageResource identLeft();

	@Source("com/openkm/frontend/public/img/icon/editor/identRight.gif")
	ImageResource identRight();

	@Source("com/openkm/frontend/public/img/icon/editor/createEditorLink.gif")
	ImageResource createEditorLink();

	@Source("com/openkm/frontend/public/img/icon/editor/breakEditorLink.gif")
	ImageResource breakEditorLink();

	@Source("com/openkm/frontend/public/img/icon/editor/line.gif")
	ImageResource line();

	@Source("com/openkm/frontend/public/img/icon/editor/html.gif")
	ImageResource html();

	@Source("com/openkm/frontend/public/img/icon/editor/picture.gif")
	ImageResource picture();

	@Source("com/openkm/frontend/public/img/icon/editor/removeFormat.gif")
	ImageResource removeFormat();

	@Source("com/openkm/frontend/public/img/icon/actions/folder_edit.png")
	ImageResource folderEdit();

	@Source("com/openkm/frontend/public/img/icon/actions/new_record.png")
	ImageResource newRecord();

	@Source("com/openkm/frontend/public/img/icon/actions/database_record.png")
	ImageResource databaseRecord();

	@Source("com/openkm/frontend/public/img/icon/actions/search.png")
	ImageResource search();

	@Source("com/openkm/frontend/public/img/icon/actions/search_disabled.png")
	ImageResource searchDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/application_resize.png")
	ImageResource splitterResize();

	@Source("com/openkm/frontend/public/img/icon/actions/application_resize_disabled.png")
	ImageResource splitterResizeDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/arrow_up.png")
	ImageResource arrowUp();

	@Source("com/openkm/frontend/public/img/icon/actions/massive_actions.png")
	ImageResource massive();

	@Source("com/openkm/frontend/public/img/icon/actions/arrow_down.png")
	ImageResource arrowDown();

	@Source("com/openkm/frontend/public/img/icon/actions/find.png")
	ImageResource find();

	@Source("com/openkm/frontend/public/img/icon/actions/find_disabled.png")
	ImageResource findDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/similar_find.png")
	ImageResource findSimilarDocument();

	@Source("com/openkm/frontend/public/img/icon/actions/folder.png")
	ImageResource folder();

	@Source("com/openkm/frontend/public/img/icon/actions/folder_disabled.png")
	ImageResource folderDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/document.png")
	ImageResource document();

	@Source("com/openkm/frontend/public/img/icon/actions/document_disabled.png")
	ImageResource documentDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/mail.png")
	ImageResource mail();

	@Source("com/openkm/frontend/public/img/icon/actions/mail_disabled.png")
	ImageResource mailDisabled();

	@Source("com/openkm/frontend/public/img/icon/search/resultset_next.gif")
	ImageResource next();

	@Source("com/openkm/frontend/public/img/icon/search/resultset_next_disabled.gif")
	ImageResource nextDisabled();

	@Source("com/openkm/frontend/public/img/icon/search/resultset_previous.gif")
	ImageResource previous();

	@Source("com/openkm/frontend/public/img/icon/search/resultset_previous_disabled.gif")
	ImageResource previousDisabled();

	@Source("com/openkm/frontend/public/img/icon/search/goto_end.gif")
	ImageResource gotoEnd();

	@Source("com/openkm/frontend/public/img/icon/search/goto_end_disabled.gif")
	ImageResource gotoEndDisabled();

	@Source("com/openkm/frontend/public/img/icon/search/goto_start.gif")
	ImageResource gotoStart();

	@Source("com/openkm/frontend/public/img/icon/search/goto_start_disabled.gif")
	ImageResource gotoStartDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/filter.png")
	ImageResource filter();

	@Source("com/openkm/frontend/public/img/icon/actions/export_csv.png")
	ImageResource exportCSV();

	@Source("com/openkm/frontend/public/img/icon/actions/options.png")
	ImageResource options();	
	
	@Source("com/openkm/frontend/public/img/icon/actions/omr.png")
	ImageResource omr();
	
	@Source("com/openkm/frontend/public/img/icon/actions/omr_disabled.png")
	ImageResource omrDisabled();

	@Source("com/openkm/frontend/public/img/icon/actions/clipboard.png")
	ImageResource clipboard();

	@Source("com/openkm/frontend/public/img/icon/actions/clipboard_small.png")
	ImageResource clipboardSmall();
}
