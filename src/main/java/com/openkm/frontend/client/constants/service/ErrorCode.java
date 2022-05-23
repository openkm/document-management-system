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

package com.openkm.frontend.client.constants.service;

/**
 * Error code to determine more exactly by administrator the error cause
 * without needed to go to log file making general error codification for
 * application
 *
 * @author jllort
 */
public class ErrorCode {

	// Origin code error is XXX digits
	public static final String ORIGIN_OKMFolderService = "001";
	public static final String ORIGIN_OKMDocumentService = "002";
	public static final String ORIGIN_OKMRemoteService = "003";
	public static final String ORIGIN_OKMDownloadService = "004";
	public static final String ORIGIN_OKMUploadService = "005";
	public static final String ORIGIN_OKMHttpServlet = "006";
	public static final String ORIGIN_OKMAuthService = "007";
	public static final String ORIGIN_OKMSearchService = "008";
	public static final String ORIGIN_OKMPropertyGroupService = "009";
	public static final String ORIGIN_OKMNotifyService = "010";
	public static final String ORIGIN_OKMBookmarkService = "011";
	public static final String ORIGIN_OKMRepositoryService = "012";
	public static final String ORIGIN_OKMDashboardService = "013";
	public static final String ORIGIN_OKMWorkspaceService = "014";
	public static final String ORIGIN_OKMWorkflowService = "015";
	public static final String ORIGIN_OKMMailService = "016";
	public static final String ORIGIN_OKMPropertyService = "017";
	public static final String ORIGIN_OKMBrowser = "018";
	public static final String ORIGIN_OKMUserCopyService = "019";
	public static final String ORIGIN_OKMNoteService = "020";
	public static final String ORIGIN_OKMStaplingService = "021";
	public static final String ORIGIN_OKMGeneralService = "022";
	public static final String ORIGIN_OKMProposedSubscriptionService = "023";
	public static final String ORIGIN_OKMProposedQueryService = "024";
	public static final String ORIGIN_OKMMessageService = "025";
	public static final String ORIGIN_OKMStampService = "026";
	public static final String ORIGIN_OKMContactService = "027";
	public static final String ORIGIN_OKMActivityLogService = "028";
	public static final String ORIGIN_OKMWikiService = "029";
	public static final String ORIGIN_OKMZohoService = "030";
	public static final String ORIGIN_OKMForumService = "031";
	public static final String ORIGIN_OKMDatabaseMetadataService = "032";
	public static final String ORIGIN_OKMKeyValueService = "033";
	public static final String ORIGIN_OKMChatService = "034";
	public static final String ORIGIN_OKMCustomerService = "035";
	public static final String ORIGIN_OKMGoogleService = "036";
	public static final String ORIGIN_OKMMassiveService = "037";
	public static final String ORIGIN_OKMFastActionService = "038";
	public static final String ORIGIN_OKMOpenMeetingsService = "039";
	public static final String ORIGIN_OKMCSVExporterService = "042";
	public static final String ORIGIN_OKMDropboxService = "043";
	public static final String ORIGIN_OKMOmrService = "044";
	public static final String ORIGIN_OKMExtensionGeneralService = "051";

	// Cause code error is XXX digits
	public static final String CAUSE_Repository = "001";
	public static final String CAUSE_ItemNotFound = "002";
	public static final String CAUSE_ItemExists = "003";
	public static final String CAUSE_Lock = "004";
	public static final String CAUSE_UnLock = "005";
	public static final String CAUSE_General = "006";
	public static final String CAUSE_OKMGeneral = "007";
	public static final String CAUSE_GWTShellEnviroment = "008";
	public static final String CAUSE_AccessDenied = "009";
	public static final String CAUSE_UnsupportedMimeType = "010";
	public static final String CAUSE_FileSizeExceeded = "011";
	public static final String CAUSE_NoSuchGroup = "012";
	public static final String CAUSE_IO = "013";
	public static final String CAUSE_NoSuchProperty = "014";
	public static final String CAUSE_PathNotFound = "015";
	public static final String CAUSE_Version = "016";
	public static final String CAUSE_SessionLost = "017";
	public static final String CAUSE_FileNotFound = "018";
	public static final String CAUSE_Parse = "019";
	public static final String CAUSE_InvalidNodeTypeDef = "020";
	public static final String CAUSE_SQL = "021";
	public static final String CAUSE_Configuration = "022";
	public static final String CAUSE_QuotaExceed = "023";
	public static final String CAUSE_Database = "024";
	public static final String CAUSE_PrincipalAdapter = "025";
	public static final String CAUSE_Workflow = "026";
	public static final String CAUSE_DocumentNameMismatch = "027";
	public static final String CAUSE_NumberFormat = "028";
	public static final String CAUSE_Login = "029";
	public static final String CAUSE_Document = "030";
	public static final String CAUSE_Eval = "031";
	public static final String CAUSE_Virus = "032";
	public static final String CAUSE_Conversion = "033";
	public static final String CAUSE_Authentication = "034";
	public static final String CAUSE_Google = "035";
	public static final String CAUSE_UnsupportedEncoding = "036";
	public static final String CAUSE_MalformedURL = "037";
	public static final String CAUSE_Zoho = "038";
	public static final String CAUSE_DocumentTemplate = "039";
	public static final String CAUSE_Template = "040";
	public static final String CAUSE_Extension = "041";
	public static final String CAUSE_IllegalAccess = "042";
	public static final String CAUSE_InvocationTarget = "043";
	public static final String CAUSE_NoSuchMethod = "044";
	public static final String CAUSE_UserYetLogged = "045";
	public static final String CAUSE_Automation = "046";
	public static final String CAUSE_Service = "047";
	public static final String CAUSE_UserQuoteExceed = "048";
	public static final String CAUSE_OpenMeetings = "049";
	public static final String CAUSE_Messaging = "050";
	public static final String CAUSE_Dropbox = "051";
	public static final String CAUSE_Omr = "052";
	public static final String CAUSE_SystemReadOnlyMode = "053";
	public static final String CAUSE_DragAndDropError = "054";

	/**
	 * Gets the error
	 * <p>
	 * The final error code returned by application is ORIGIN_CODE_ERROR + CAUSE_CODE_ERROR
	 * example ERROR 001001 = Error causes on OKMFolderService and originated by Repository Exception
	 *
	 * @param origin The error origin
	 * @param cause  The error cause
	 * @return The error
	 */
	public static String get(String origin, String cause) {
		return "OKM-" + origin + cause;
	}
}
