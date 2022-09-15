/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
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

package com.openkm.extension.frontend.client.util;

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

	@Source("com/openkm/extension/frontend/public/img/icon/actions/download.gif")
	ImageResource download();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/download_disabled.gif")
	ImageResource downloadDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/download_pdf.gif")
	ImageResource downloadPdf();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/download_pdf_disabled.gif")
	ImageResource downloadPdfDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/stapling.gif")
	ImageResource stapling();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/stapling_disabled.gif")
	ImageResource staplingDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/stapling_stop.gif")
	ImageResource staplingStop();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/stapling_stop_disabled.gif")
	ImageResource staplingStopDisabled();

	@Source("com/openkm/extension/frontend/public/img/indicator.gif")
	ImageResource indicator();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/propose_subscription.png")
	ImageResource proposeSubscription();

	@Source("com/openkm/extension/frontend/public/img/icon/toolbar/messaging.png")
	ImageResource messaging();

	@Source("com/openkm/extension/frontend/public/img/icon/general/yes.gif")
	ImageResource yes();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/delete.png")
	ImageResource delete();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/run.png")
	ImageResource run();

	@Source("com/openkm/extension/frontend/public/img/icon/security/add.gif")
	ImageResource add();

	@Source("com/openkm/extension/frontend/public/img/icon/security/remove.gif")
	ImageResource remove();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/share_query.gif")
	ImageResource shareQuery();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/message.png")
	ImageResource messageSent();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/message_received.png")
	ImageResource messageReceived();

	@Source("com/openkm/extension/frontend/public/img/icon/toolbar/stamp.png")
	ImageResource stamp();

	@Source("com/openkm/extension/frontend/public/img/icon/toolbar/stamp_disabled.png")
	ImageResource stampDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/digital_signature.png")
	ImageResource digitalSignature();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/edit.png")
	ImageResource edit();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/contact_add.png")
	ImageResource addContact();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/pencil.png")
	ImageResource pencil();

	@Source("com/openkm/extension/frontend/public/img/icon/toolbar/forum.png")
	ImageResource forum();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_biggrin.gif")
	ImageResource smileBigGrin();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_sad.gif")
	ImageResource smileSad();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_smile.gif")
	ImageResource smileSmile();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_eek.gif")
	ImageResource smileEek();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_surprised.gif")
	ImageResource smileSurprised();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_confused.gif")
	ImageResource smileConfused();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_cool.gif")
	ImageResource smileCool();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_lol.gif")
	ImageResource smileLol();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_mad.gif")
	ImageResource smileMad();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_razz.gif")
	ImageResource smileRazz();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_redface.gif")
	ImageResource smileRedface();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_cry.gif")
	ImageResource smileCry();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_evil.gif")
	ImageResource smileEvil();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_twisted.gif")
	ImageResource smileTwisted();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_rolleyes.gif")
	ImageResource smileRolleyes();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_wink.gif")
	ImageResource smileWink();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_exclaim.gif")
	ImageResource smileExclaim();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_idea.gif")
	ImageResource smileIdea();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_question.gif")
	ImageResource smileQuestion();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_arrow.gif")
	ImageResource smileArrow();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_neutral.gif")
	ImageResource smileNeutral();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_mrgreen.gif")
	ImageResource smileMrgreen();

	@Source("com/openkm/extension/frontend/public/img/icon/editor/justifyCenter.gif")
	ImageResource justifyCenter();

	@Source("com/openkm/extension/frontend/public/img/icon/editor/justifyLeft.gif")
	ImageResource justifyLeft();

	@Source("com/openkm/extension/frontend/public/img/icon/editor/justifyRight.gif")
	ImageResource justifyRight();

	@Source("com/openkm/extension/frontend/public/img/icon/editor/bold.gif")
	ImageResource bold();

	@Source("com/openkm/extension/frontend/public/img/icon/editor/italic.gif")
	ImageResource italic();

	@Source("com/openkm/extension/frontend/public/img/icon/editor/underline.gif")
	ImageResource underline();

	@Source("com/openkm/extension/frontend/public/img/icon/editor/createLink.gif")
	ImageResource createLink();

	@Source("com/openkm/extension/frontend/public/img/icon/editor/insertImage.gif")
	ImageResource insertImage();

	@Source("com/openkm/extension/frontend/public/img/icon/editor/strikeThrough.gif")
	ImageResource strikeThrough();

	@Source("com/openkm/extension/frontend/public/img/icon/editor/justify.gif")
	ImageResource justify();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/municipality.png")
	ImageResource municipality();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/database_record.png")
	ImageResource databaseRecord();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/new_record.png")
	ImageResource newRecord();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/new_record_disabled.png")
	ImageResource newRecordDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/new_entry.png")
	ImageResource newEntry();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/new_entry_disabled.png")
	ImageResource newEntryDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/contacts.png")
	ImageResource contacts();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/contacts_disabled.png")
	ImageResource contactsDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/document_find.png")
	ImageResource findDocument();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/open_folder.gif")
	ImageResource openFolder();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/folder_find.gif")
	ImageResource findFolder();

	@Source("com/openkm/extension/frontend/public/img/icon/toolbar/wiki.png")
	ImageResource wiki();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/wiki_link.png")
	ImageResource wikiLink();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/wiki_add.png")
	ImageResource wikiAdd();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/resultset_next.gif")
	ImageResource next();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/resultset_next_disabled.gif")
	ImageResource nextDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/resultset_previous.gif")
	ImageResource previous();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/resultset_previous_disabled.gif")
	ImageResource previousDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/search.png")
	ImageResource search();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/chart_organisation.png")
	ImageResource chartOrganisation();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/fast_action.png")
	ImageResource fastAction();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/fast_action_disabled.png")
	ImageResource fastActionDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/toolbar/meeting.png")
	ImageResource meeting();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/room.png")
	ImageResource room();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/room_in.png")
	ImageResource roomIn();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/room_close.png")
	ImageResource roomClose();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/room_add_file.png")
	ImageResource roomAddFile();

	@Source("com/openkm/extension/frontend/public/img/zoom_out.gif")
	ImageResource zoomOut();

	@Source("com/openkm/extension/frontend/public/img/zoom_in.gif")
	ImageResource zoomIn();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/user.png")
	ImageResource user();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/image_find.png")
	ImageResource findImage();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/edit_workflow.png")
	ImageResource workflowEdit();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/edit_workflow_disabled.png")
	ImageResource workflowEditDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/add_workflow.png")
	ImageResource workflowAdd();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/add_workflow_disabled.png")
	ImageResource workflowAddDisabled();
}
