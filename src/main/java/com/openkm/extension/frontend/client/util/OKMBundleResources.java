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

	public static final OKMBundleResources INSTANCE = GWT.create(OKMBundleResources.class);

	@Source("com/openkm/extension/frontend/public/img/icon/actions/download.gif")
	public ImageResource download();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/download_disabled.gif")
	public ImageResource downloadDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/download_pdf.gif")
	public ImageResource downloadPdf();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/download_pdf_disabled.gif")
	public ImageResource downloadPdfDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/stapling.gif")
	public ImageResource stapling();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/stapling_disabled.gif")
	public ImageResource staplingDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/stapling_stop.gif")
	public ImageResource staplingStop();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/stapling_stop_disabled.gif")
	public ImageResource staplingStopDisabled();

	@Source("com/openkm/extension/frontend/public/img/indicator.gif")
	public ImageResource indicator();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/propose_subscription.png")
	public ImageResource proposeSubscription();

	@Source("com/openkm/extension/frontend/public/img/icon/toolbar/messaging.png")
	public ImageResource messaging();

	@Source("com/openkm/extension/frontend/public/img/icon/general/yes.gif")
	public ImageResource yes();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/delete.png")
	public ImageResource delete();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/run.png")
	public ImageResource run();

	@Source("com/openkm/extension/frontend/public/img/icon/security/add.gif")
	public ImageResource add();

	@Source("com/openkm/extension/frontend/public/img/icon/security/remove.gif")
	public ImageResource remove();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/share_query.gif")
	public ImageResource shareQuery();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/message.png")
	public ImageResource messageSent();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/message_received.png")
	public ImageResource messageReceived();

	@Source("com/openkm/extension/frontend/public/img/icon/toolbar/stamp.png")
	public ImageResource stamp();

	@Source("com/openkm/extension/frontend/public/img/icon/toolbar/stamp_disabled.png")
	public ImageResource stampDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/digital_signature.png")
	public ImageResource digitalSignature();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/edit.png")
	public ImageResource edit();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/contact_add.png")
	public ImageResource addContact();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/pencil.png")
	public ImageResource pencil();

	@Source("com/openkm/extension/frontend/public/img/icon/toolbar/forum.png")
	public ImageResource forum();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_biggrin.gif")
	public ImageResource smileBigGrin();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_sad.gif")
	public ImageResource smileSad();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_smile.gif")
	public ImageResource smileSmile();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_eek.gif")
	public ImageResource smileEek();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_surprised.gif")
	public ImageResource smileSurprised();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_confused.gif")
	public ImageResource smileConfused();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_cool.gif")
	public ImageResource smileCool();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_lol.gif")
	public ImageResource smileLol();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_mad.gif")
	public ImageResource smileMad();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_razz.gif")
	public ImageResource smileRazz();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_redface.gif")
	public ImageResource smileRedface();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_cry.gif")
	public ImageResource smileCry();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_evil.gif")
	public ImageResource smileEvil();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_twisted.gif")
	public ImageResource smileTwisted();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_rolleyes.gif")
	public ImageResource smileRolleyes();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_wink.gif")
	public ImageResource smileWink();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_exclaim.gif")
	public ImageResource smileExclaim();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_idea.gif")
	public ImageResource smileIdea();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_question.gif")
	public ImageResource smileQuestion();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_arrow.gif")
	public ImageResource smileArrow();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_neutral.gif")
	public ImageResource smileNeutral();

	@Source("com/openkm/extension/frontend/public/img/icon/smilies/icon_mrgreen.gif")
	public ImageResource smileMrgreen();

	@Source("com/openkm/extension/frontend/public/img/icon/editor/justifyCenter.gif")
	public ImageResource justifyCenter();

	@Source("com/openkm/extension/frontend/public/img/icon/editor/justifyLeft.gif")
	public ImageResource justifyLeft();

	@Source("com/openkm/extension/frontend/public/img/icon/editor/justifyRight.gif")
	public ImageResource justifyRight();

	@Source("com/openkm/extension/frontend/public/img/icon/editor/bold.gif")
	public ImageResource bold();

	@Source("com/openkm/extension/frontend/public/img/icon/editor/italic.gif")
	public ImageResource italic();

	@Source("com/openkm/extension/frontend/public/img/icon/editor/underline.gif")
	public ImageResource underline();

	@Source("com/openkm/extension/frontend/public/img/icon/editor/createLink.gif")
	public ImageResource createLink();

	@Source("com/openkm/extension/frontend/public/img/icon/editor/insertImage.gif")
	public ImageResource insertImage();

	@Source("com/openkm/extension/frontend/public/img/icon/editor/strikeThrough.gif")
	public ImageResource strikeThrough();

	@Source("com/openkm/extension/frontend/public/img/icon/editor/justify.gif")
	public ImageResource justify();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/municipality.png")
	public ImageResource municipality();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/database_record.png")
	public ImageResource databaseRecord();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/new_record.png")
	public ImageResource newRecord();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/new_record_disabled.png")
	public ImageResource newRecordDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/new_entry.png")
	public ImageResource newEntry();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/new_entry_disabled.png")
	public ImageResource newEntryDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/contacts.png")
	public ImageResource contacts();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/contacts_disabled.png")
	public ImageResource contactsDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/document_find.png")
	public ImageResource findDocument();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/open_folder.gif")
	public ImageResource openFolder();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/folder_find.gif")
	public ImageResource findFolder();

	@Source("com/openkm/extension/frontend/public/img/icon/toolbar/wiki.png")
	public ImageResource wiki();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/wiki_link.png")
	public ImageResource wikiLink();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/wiki_add.png")
	public ImageResource wikiAdd();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/resultset_next.gif")
	public ImageResource next();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/resultset_next_disabled.gif")
	public ImageResource nextDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/resultset_previous.gif")
	public ImageResource previous();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/resultset_previous_disabled.gif")
	public ImageResource previousDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/search.png")
	public ImageResource search();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/chart_organisation.png")
	public ImageResource chartOrganisation();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/fast_action.png")
	public ImageResource fastAction();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/fast_action_disabled.png")
	public ImageResource fastActionDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/toolbar/meeting.png")
	public ImageResource meeting();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/room.png")
	public ImageResource room();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/room_in.png")
	public ImageResource roomIn();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/room_close.png")
	public ImageResource roomClose();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/room_add_file.png")
	public ImageResource roomAddFile();

	@Source("com/openkm/extension/frontend/public/img/zoom_out.gif")
	public ImageResource zoomOut();

	@Source("com/openkm/extension/frontend/public/img/zoom_in.gif")
	public ImageResource zoomIn();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/user.png")
	public ImageResource user();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/image_find.png")
	public ImageResource findImage();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/edit_workflow.png")
	public ImageResource workflowEdit();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/edit_workflow_disabled.png")
	public ImageResource workflowEditDisabled();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/add_workflow.png")
	public ImageResource workflowAdd();

	@Source("com/openkm/extension/frontend/public/img/icon/actions/add_workflow_disabled.png")
	public ImageResource workflowAddDisabled();
}
