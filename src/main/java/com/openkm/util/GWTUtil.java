/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
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

package com.openkm.util;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMAuth;
import com.openkm.api.OKMDocument;
import com.openkm.api.OKMFolder;
import com.openkm.api.OKMMail;
import com.openkm.api.OKMPropertyGroup;
import com.openkm.api.OKMRepository;
import com.openkm.bean.AppVersion;
import com.openkm.bean.DashboardDocumentResult;
import com.openkm.bean.DashboardFolderResult;
import com.openkm.bean.DashboardMailResult;
import com.openkm.bean.Document;
import com.openkm.bean.ExtendedAttributes;
import com.openkm.bean.Folder;
import com.openkm.bean.LockInfo;
import com.openkm.bean.Mail;
import com.openkm.bean.Note;
import com.openkm.bean.PropertyGroup;
import com.openkm.bean.QueryResult;
import com.openkm.bean.Version;
import com.openkm.bean.form.Button;
import com.openkm.bean.form.CheckBox;
import com.openkm.bean.form.Download;
import com.openkm.bean.form.FormElement;
import com.openkm.bean.form.Input;
import com.openkm.bean.form.Node;
import com.openkm.bean.form.Option;
import com.openkm.bean.form.Print;
import com.openkm.bean.form.Select;
import com.openkm.bean.form.Separator;
import com.openkm.bean.form.SuggestBox;
import com.openkm.bean.form.Text;
import com.openkm.bean.form.TextArea;
import com.openkm.bean.form.Upload;
import com.openkm.bean.form.Validator;
import com.openkm.bean.workflow.Comment;
import com.openkm.bean.workflow.ProcessDefinition;
import com.openkm.bean.workflow.ProcessInstance;
import com.openkm.bean.workflow.TaskInstance;
import com.openkm.bean.workflow.Token;
import com.openkm.bean.workflow.Transition;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.NoSuchGroupException;
import com.openkm.core.ParseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.KeyValueDAO;
import com.openkm.dao.bean.Activity;
import com.openkm.dao.bean.Bookmark;
import com.openkm.dao.bean.KeyValue;
import com.openkm.dao.bean.Language;
import com.openkm.dao.bean.MimeType;
import com.openkm.dao.bean.Omr;
import com.openkm.dao.bean.QueryParams;
import com.openkm.dao.bean.Report;
import com.openkm.dao.bean.UserConfig;
import com.openkm.extension.dao.bean.Forum;
import com.openkm.extension.dao.bean.ForumPost;
import com.openkm.extension.dao.bean.ForumTopic;
import com.openkm.extension.dao.bean.MessageReceived;
import com.openkm.extension.dao.bean.MessageSent;
import com.openkm.extension.dao.bean.ProposedQueryReceived;
import com.openkm.extension.dao.bean.ProposedQuerySent;
import com.openkm.extension.dao.bean.ProposedSubscriptionReceived;
import com.openkm.extension.dao.bean.ProposedSubscriptionSent;
import com.openkm.extension.dao.bean.Staple;
import com.openkm.extension.dao.bean.StapleGroup;
import com.openkm.extension.dao.bean.WikiPage;
import com.openkm.frontend.client.bean.GWTAppVersion;
import com.openkm.frontend.client.bean.GWTBookmark;
import com.openkm.frontend.client.bean.GWTComment;
import com.openkm.frontend.client.bean.GWTConfig;
import com.openkm.frontend.client.bean.GWTDashboardDocumentResult;
import com.openkm.frontend.client.bean.GWTDashboardFolderResult;
import com.openkm.frontend.client.bean.GWTDashboardMailResult;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTExtendedAttributes;
import com.openkm.frontend.client.bean.GWTFilebrowseExtraColumn;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTKeyValue;
import com.openkm.frontend.client.bean.GWTLanguage;
import com.openkm.frontend.client.bean.GWTLockInfo;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.bean.GWTMimeType;
import com.openkm.frontend.client.bean.GWTNote;
import com.openkm.frontend.client.bean.GWTOmr;
import com.openkm.frontend.client.bean.GWTProcessDefinition;
import com.openkm.frontend.client.bean.GWTProcessInstance;
import com.openkm.frontend.client.bean.GWTProcessInstanceLogEntry;
import com.openkm.frontend.client.bean.GWTPropertyGroup;
import com.openkm.frontend.client.bean.GWTPropertyParams;
import com.openkm.frontend.client.bean.GWTQueryParams;
import com.openkm.frontend.client.bean.GWTQueryResult;
import com.openkm.frontend.client.bean.GWTReport;
import com.openkm.frontend.client.bean.GWTTaskInstance;
import com.openkm.frontend.client.bean.GWTToken;
import com.openkm.frontend.client.bean.GWTTransition;
import com.openkm.frontend.client.bean.GWTUser;
import com.openkm.frontend.client.bean.GWTUserConfig;
import com.openkm.frontend.client.bean.GWTVersion;
import com.openkm.frontend.client.bean.GWTWorkflowComment;
import com.openkm.frontend.client.bean.GWTWorkspace;
import com.openkm.frontend.client.bean.extension.GWTActivity;
import com.openkm.frontend.client.bean.extension.GWTForum;
import com.openkm.frontend.client.bean.extension.GWTForumPost;
import com.openkm.frontend.client.bean.extension.GWTForumTopic;
import com.openkm.frontend.client.bean.extension.GWTMessageReceived;
import com.openkm.frontend.client.bean.extension.GWTProposedQueryReceived;
import com.openkm.frontend.client.bean.extension.GWTProposedQuerySent;
import com.openkm.frontend.client.bean.extension.GWTProposedSubscriptionReceived;
import com.openkm.frontend.client.bean.extension.GWTProposedSubscriptionSent;
import com.openkm.frontend.client.bean.extension.GWTStaple;
import com.openkm.frontend.client.bean.extension.GWTStapleGroup;
import com.openkm.frontend.client.bean.extension.GWTTextMessageSent;
import com.openkm.frontend.client.bean.extension.GWTWikiPage;
import com.openkm.frontend.client.bean.form.GWTButton;
import com.openkm.frontend.client.bean.form.GWTCheckBox;
import com.openkm.frontend.client.bean.form.GWTDownload;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.bean.form.GWTInput;
import com.openkm.frontend.client.bean.form.GWTNode;
import com.openkm.frontend.client.bean.form.GWTOption;
import com.openkm.frontend.client.bean.form.GWTPrint;
import com.openkm.frontend.client.bean.form.GWTSelect;
import com.openkm.frontend.client.bean.form.GWTSeparator;
import com.openkm.frontend.client.bean.form.GWTSuggestBox;
import com.openkm.frontend.client.bean.form.GWTText;
import com.openkm.frontend.client.bean.form.GWTTextArea;
import com.openkm.frontend.client.bean.form.GWTUpload;
import com.openkm.frontend.client.bean.form.GWTValidator;
import com.openkm.module.db.DbPropertyGroupModule;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.util.WorkflowUtils.ProcessInstanceLogEntry;

public class GWTUtil {
	private static Logger log = LoggerFactory.getLogger(GWTUtil.class);

	/**
	 * Copy the Document data to GWTDocument data.
	 *
	 * @param doc The original Document object.
	 * @param workspace
	 * @return A GWTDocument object with the data from the original Document.
	 */
	public static GWTDocument copy(Document doc, GWTWorkspace workspace) throws PrincipalAdapterException, IOException,
			ParseException, NoSuchGroupException, AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("copy({})", doc);
		GWTDocument gwtDocument = MappingUtils.getMapper().map(doc, GWTDocument.class);
		gwtDocument.setParentPath(GWTUtil.getParent(doc.getPath()));
		gwtDocument.setName(GWTUtil.getName(doc.getPath()));

		// User id & username
		GWTUser user = new GWTUser();
		user.setId(doc.getAuthor());
		user.setUsername(OKMAuth.getInstance().getName(null, doc.getAuthor()));
		gwtDocument.setUser(user);

		// Version user id & usersame
		if (gwtDocument.getActualVersion() != null) {
			GWTUser userVersion = new GWTUser();
			userVersion.setId(gwtDocument.getActualVersion().getAuthor());
			userVersion.setUsername(OKMAuth.getInstance().getName(null, gwtDocument.getActualVersion().getAuthor()));
			gwtDocument.getActualVersion().setUser(userVersion);
		}

		// Lockinfo user id & username
		if (gwtDocument.getLockInfo() != null) {
			GWTUser userLock = new GWTUser();
			userLock.setId(gwtDocument.getLockInfo().getOwner());
			userLock.setUsername(OKMAuth.getInstance().getName(null, gwtDocument.getLockInfo().getOwner()));
			gwtDocument.getLockInfo().setUser(userLock);
		}

		// Notes user id & username
		gwtDocument.getNotes().clear();
		for (Note note : doc.getNotes()) {
			gwtDocument.getNotes().add(copy(note));
			if (!note.getAuthor().equals(Config.SYSTEM_USER)) {
				gwtDocument.setHasNotes(true);
			}
		}

		// Subscriptors
		Set<GWTUser> subscriptors = new HashSet<GWTUser>();
		for (String userId : doc.getSubscriptors()) {
			GWTUser subscriptor = new GWTUser();
			subscriptor.setId(userId);
			subscriptor.setUsername(OKMAuth.getInstance().getName(null, userId));
			subscriptors.add(subscriptor);
		}
		gwtDocument.setSubscriptors(subscriptors);

		// Extra columns
		if (workspace != null) {
			if (workspace.getProfileFileBrowser().getColumn0() != null) {
				gwtDocument.setColumn0(getExtraColumn(doc.getPath(), workspace.getProfileFileBrowser().getColumn0()));
			}
			if (workspace.getProfileFileBrowser().getColumn1() != null) {
				gwtDocument.setColumn1(getExtraColumn(doc.getPath(), workspace.getProfileFileBrowser().getColumn1()));
			}
			if (workspace.getProfileFileBrowser().getColumn2() != null) {
				gwtDocument.setColumn2(getExtraColumn(doc.getPath(), workspace.getProfileFileBrowser().getColumn2()));
			}
			if (workspace.getProfileFileBrowser().getColumn3() != null) {
				gwtDocument.setColumn3(getExtraColumn(doc.getPath(), workspace.getProfileFileBrowser().getColumn3()));
			}
			if (workspace.getProfileFileBrowser().getColumn4() != null) {
				gwtDocument.setColumn4(getExtraColumn(doc.getPath(), workspace.getProfileFileBrowser().getColumn4()));
			}
			if (workspace.getProfileFileBrowser().getColumn5() != null) {
				gwtDocument.setColumn5(getExtraColumn(doc.getPath(), workspace.getProfileFileBrowser().getColumn5()));
			}
			if (workspace.getProfileFileBrowser().getColumn6() != null) {
				gwtDocument.setColumn6(getExtraColumn(doc.getPath(), workspace.getProfileFileBrowser().getColumn6()));
			}
			if (workspace.getProfileFileBrowser().getColumn7() != null) {
				gwtDocument.setColumn7(getExtraColumn(doc.getPath(), workspace.getProfileFileBrowser().getColumn7()));
			}
			if (workspace.getProfileFileBrowser().getColumn8() != null) {
				gwtDocument.setColumn8(getExtraColumn(doc.getPath(), workspace.getProfileFileBrowser().getColumn8()));
			}
			if (workspace.getProfileFileBrowser().getColumn9() != null) {
				gwtDocument.setColumn9(getExtraColumn(doc.getPath(), workspace.getProfileFileBrowser().getColumn9()));
			}
		}

		log.debug("copy: {}", gwtDocument);
		return gwtDocument;
	}

	/**
	 * Copy the GWTDocument data to Document data.
	 *
	 * @param gWTDoc The original GWTDocument object.
	 * @return A Document object with the data form de original GWTDocument
	 */
	public static Document copy(GWTDocument gWTDoc) throws PrincipalAdapterException {
		log.debug("copy({})", gWTDoc);
		Document doc = new Document();
		Calendar cal = Calendar.getInstance();

		doc.setKeywords(gWTDoc.getKeywords());
		doc.setMimeType(gWTDoc.getMimeType());
		doc.setPath(gWTDoc.getPath());
		doc.setAuthor(gWTDoc.getAuthor());
		cal.setTime(gWTDoc.getCreated());
		doc.setCreated(cal);
		cal.setTime(gWTDoc.getLastModified());
		doc.setLastModified(cal);
		doc.setCheckedOut(gWTDoc.isCheckedOut());
		doc.setLocked(gWTDoc.isLocked());
		doc.setActualVersion(GWTUtil.copy(gWTDoc.getActualVersion()));
		doc.setPermissions(gWTDoc.getPermissions());
		doc.setSubscribed(gWTDoc.isSubscribed());
		Set<String> subscriptors = new HashSet<String>();
		for (GWTUser user : gWTDoc.getSubscriptors()) {
			subscriptors.add(user.getId());
		}
		doc.setSubscriptors(subscriptors);
		Set<Folder> categories = new HashSet<Folder>();

		for (Iterator<GWTFolder> it = gWTDoc.getCategories().iterator(); it.hasNext(); ) {
			categories.add(copy(it.next()));
		}

		doc.setCategories(categories);
		gWTDoc.setActualVersion(copy(doc.getActualVersion()));

		log.debug("copy: {}", gWTDoc);
		return doc;
	}

	/**
	 * Copy the Folder data to GWTFolder data.
	 *
	 * @param doc The original Folder object.
	 * @param workspace
	 * @return A GWTFolder object with the data from the original Document.
	 */
	public static GWTFolder copy(Folder fld, GWTWorkspace workspace) throws PrincipalAdapterException, IOException,
			ParseException, NoSuchGroupException, AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("copy({})", fld);
		GWTFolder gwtFolder = MappingUtils.getMapper().map(fld, GWTFolder.class);
		gwtFolder.setParentPath(GWTUtil.getParent(fld.getPath()));
		gwtFolder.setName(GWTUtil.getName(fld.getPath()));

		// User id & username
		GWTUser user = new GWTUser();
		user.setId(fld.getAuthor());
		user.setUsername(OKMAuth.getInstance().getName(null, fld.getAuthor()));
		gwtFolder.setUser(user);

		// Notes user id & username
		gwtFolder.getNotes().clear();
		for (Note note : fld.getNotes()) {
			gwtFolder.getNotes().add(copy(note));
			if (!note.getAuthor().equals(Config.SYSTEM_USER)) {
				gwtFolder.setHasNotes(true);
			}
		}

		// Subscriptors
		Set<GWTUser> subscriptors = new HashSet<GWTUser>();
		for (String userId : fld.getSubscriptors()) {
			GWTUser subscriptor = new GWTUser();
			subscriptor.setId(userId);
			subscriptor.setUsername(OKMAuth.getInstance().getName(null, userId));
			subscriptors.add(subscriptor);
		}
		gwtFolder.setSubscriptors(subscriptors);

		// Extra columns
		if (workspace != null) {
			if (workspace.getProfileFileBrowser().getColumn0() != null) {
				gwtFolder.setColumn0(getExtraColumn(fld.getPath(), workspace.getProfileFileBrowser().getColumn0()));
			}
			if (workspace.getProfileFileBrowser().getColumn1() != null) {
				gwtFolder.setColumn1(getExtraColumn(fld.getPath(), workspace.getProfileFileBrowser().getColumn1()));
			}
			if (workspace.getProfileFileBrowser().getColumn2() != null) {
				gwtFolder.setColumn2(getExtraColumn(fld.getPath(), workspace.getProfileFileBrowser().getColumn2()));
			}
			if (workspace.getProfileFileBrowser().getColumn3() != null) {
				gwtFolder.setColumn3(getExtraColumn(fld.getPath(), workspace.getProfileFileBrowser().getColumn3()));
			}
			if (workspace.getProfileFileBrowser().getColumn4() != null) {
				gwtFolder.setColumn4(getExtraColumn(fld.getPath(), workspace.getProfileFileBrowser().getColumn4()));
			}
			if (workspace.getProfileFileBrowser().getColumn5() != null) {
				gwtFolder.setColumn5(getExtraColumn(fld.getPath(), workspace.getProfileFileBrowser().getColumn5()));
			}
			if (workspace.getProfileFileBrowser().getColumn6() != null) {
				gwtFolder.setColumn6(getExtraColumn(fld.getPath(), workspace.getProfileFileBrowser().getColumn6()));
			}
			if (workspace.getProfileFileBrowser().getColumn7() != null) {
				gwtFolder.setColumn7(getExtraColumn(fld.getPath(), workspace.getProfileFileBrowser().getColumn7()));
			}
			if (workspace.getProfileFileBrowser().getColumn8() != null) {
				gwtFolder.setColumn8(getExtraColumn(fld.getPath(), workspace.getProfileFileBrowser().getColumn8()));
			}
			if (workspace.getProfileFileBrowser().getColumn9() != null) {
				gwtFolder.setColumn9(getExtraColumn(fld.getPath(), workspace.getProfileFileBrowser().getColumn9()));
			}
		}

		log.debug("copy: {}", gwtFolder);
		return gwtFolder;
	}

	/**
	 * Copy the GWTFolder data to Folder data.
	 *
	 * @param doc The original GWTFolder object.
	 * @return A Folder object with the data from the original Document.
	 */
	public static Folder copy(GWTFolder fld) {
		log.debug("copy({})", fld);
		Folder folder = new Folder();

		folder.setUuid(fld.getUuid());
		folder.setPath(fld.getPath());
		folder.setHasChildren(fld.isHasChildren());
		Calendar created = Calendar.getInstance();
		created.setTimeInMillis(fld.getCreated().getTime());
		folder.setCreated(created);
		folder.setPermissions(fld.getPermissions());
		folder.setAuthor(fld.getAuthor());
		folder.setSubscribed(fld.isSubscribed());

		// Subscriptors
		Set<String> subscriptors = new HashSet<String>();
		for (GWTUser user : fld.getSubscriptors()) {
			subscriptors.add(user.getId());
		}
		folder.setSubscriptors(subscriptors);

		log.debug("copy: {}", folder);
		return folder;
	}

	/**
	 * Copy the Version data to GWTVersion data.
	 *
	 * @param doc The original Version object.
	 * @return A GWTVersion object with the data from the original Document.
	 */
	public static GWTVersion copy(Version version) throws PrincipalAdapterException {
		log.debug("copy({})", version);
		GWTVersion gWTVersion = new GWTVersion();

		gWTVersion.setCreated(version.getCreated().getTime());
		gWTVersion.setName(version.getName());
		gWTVersion.setSize(version.getSize());
		gWTVersion.setAuthor(version.getAuthor());
		gWTVersion.setActual(version.isActual());
		gWTVersion.setComment(version.getComment());

		GWTUser user = new GWTUser();
		user.setId(version.getAuthor());
		user.setUsername(OKMAuth.getInstance().getName(null, version.getAuthor()));
		gWTVersion.setUser(user);

		log.debug("copy: {}", gWTVersion);
		return gWTVersion;
	}

	/**
	 * Copy the GWTVersion data to Version data object
	 *
	 * @param gWTVersion The original GWTVersion
	 * @return A Version object with the data from the original GWTVersion
	 */
	public static Version copy(GWTVersion gWTVersion) {
		log.debug("copy({})", gWTVersion);
		Version version = new Version();
		Calendar cal = Calendar.getInstance();

		version.setName(gWTVersion.getName());
		version.setSize(gWTVersion.getSize());
		version.setAuthor(gWTVersion.getAuthor());
		version.setActual(gWTVersion.isActual());
		cal.setTime(gWTVersion.getCreated());
		version.setCreated(cal);
		version.setComment(gWTVersion.getComment());

		log.debug("copy: {}", version);
		return version;
	}

	/**
	 * Copy the Lock data to GWTLock data.
	 *
	 * @param doc The original Version object.
	 * @return A GWTLock object with the data from the original Lock.
	 */
	public static GWTLockInfo copy(LockInfo lock) throws PrincipalAdapterException {
		log.debug("copy({})", lock);
		GWTLockInfo gwtLock = MappingUtils.getMapper().map(lock, GWTLockInfo.class);
		GWTUser user = new GWTUser();
		user.setId(gwtLock.getOwner());
		user.setUsername(OKMAuth.getInstance().getName(null, gwtLock.getOwner()));
		log.debug("copy: {}", gwtLock);
		return gwtLock;
	}

	/**
	 * Copy the Bookmark data to GWTBookmark data.
	 *
	 * @param bookmark The original Version object.
	 * @return A GWTBookmark object with the data from the original Bookmark.
	 */
	public static GWTBookmark copy(Bookmark bm, String path) {
		log.debug("copy({})", bm);
		GWTBookmark gwtBookmark = MappingUtils.getMapper().map(bm, GWTBookmark.class);
		gwtBookmark.setPath(path);
		log.debug("copy: {}", gwtBookmark);
		return gwtBookmark;
	}

	/**
	 * Get parent item path from path.
	 *
	 * @param path The complete item path.
	 * @return The parent item path.
	 */
	public static String getParent(String path) {
		log.debug("getParent({})", path);
		int lastSlash = path.lastIndexOf('/');
		String ret = (lastSlash > 0) ? path.substring(0, lastSlash) : "";
		log.debug("getParent: {}", ret);
		return ret;
	}

	/**
	 * Get item name from path.
	 *
	 * @param path The complete item path.
	 * @return The name of the item.
	 */
	public static String getName(String path) {
		log.debug("getName({})", path);
		String ret = path.substring(path.lastIndexOf('/') + 1);
		log.debug("getName: {}", ret);
		return ret;
	}

	/**
	 * Copy the gWTparams data to GWTQueryParams data object
	 *
	 * @param gWTParams The original GWTQueryParams
	 * @return The QueryParams object with the data from de original GWTQueryParams
	 */
	public static QueryParams copy(GWTQueryParams gWTParams) {
		QueryParams params = new QueryParams();

		params.setId(new Long(gWTParams.getId()));
		params.setQueryName(gWTParams.getQueryName());
		params.setContent(gWTParams.getContent());
		String keywords = gWTParams.getKeywords().trim();
		Set<String> tmpKwd = new HashSet<String>();

		if (!keywords.equals("")) {
			String kw[] = keywords.split(" ");
			for (int i = 0; i < kw.length; i++) {
				tmpKwd.add(kw[i]);
			}
		}

		params.setKeywords(tmpKwd);
		params.setMimeType(gWTParams.getMimeType());
		params.setName(gWTParams.getName());
		Map<String, String> properties = new HashMap<String, String>();

		for (Iterator<String> it = gWTParams.getProperties().keySet().iterator(); it.hasNext(); ) {
			String key = it.next();
			properties.put(key, gWTParams.getProperties().get(key).getValue());
		}

		params.setProperties(properties);
		params.setPath(gWTParams.getPath());
		String categories = gWTParams.getCategoryUuid().trim();
		Set<String> tmpCat = new HashSet<String>();

		if (!categories.equals("")) {
			tmpCat.add(categories);
		}

		params.setCategories(tmpCat);
		params.setAuthor(gWTParams.getAuthor());
		Calendar lastModifiedFrom = Calendar.getInstance();
		Calendar lastModifiedTo = Calendar.getInstance();

		if (gWTParams.getLastModifiedFrom() != null && gWTParams.getLastModifiedTo() != null) {
			lastModifiedFrom.setTime(gWTParams.getLastModifiedFrom());
			lastModifiedTo.setTime(gWTParams.getLastModifiedTo());
		} else {
			lastModifiedFrom = null;
			lastModifiedTo = null;
		}

		params.setLastModifiedFrom(lastModifiedFrom);
		params.setLastModifiedTo(lastModifiedTo);
		params.setDashboard(gWTParams.isDashboard());
		params.setDomain(gWTParams.getDomain());
		params.setMailSubject(gWTParams.getMailSubject());
		params.setMailTo(gWTParams.getMailTo());
		params.setMailFrom(gWTParams.getMailFrom());
		params.setOperator(gWTParams.getOperator());

		return params;
	}

	/**
	 * Copy the QueryResult data to GWTQueryResult
	 *
	 * @param queryResult The original QueryResult
	 * @return The GWTQueryResult object with data values from de origina QueryResult
	 */
	public static GWTQueryResult copy(QueryResult queryResult, GWTWorkspace workspace) throws PrincipalAdapterException, IOException,
			ParseException, NoSuchGroupException, AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		GWTQueryResult gwtQueryResult = new GWTQueryResult();

		if (queryResult.getNode() instanceof Document) {
			if (!queryResult.isAttachment()) {
				// Normal document
				gwtQueryResult.setDocument(copy((Document) queryResult.getNode(), workspace));
				gwtQueryResult.getDocument().setAttachment(false);
			} else {
				// Attachment
				gwtQueryResult.setAttachment(copy((Document) queryResult.getNode(), workspace));
				gwtQueryResult.getAttachment().setAttachment(true);
			}
		} else if (queryResult.getNode() instanceof Folder) {
			gwtQueryResult.setFolder(copy((Folder) queryResult.getNode(), workspace));
		} else if (queryResult.getNode() instanceof Mail) {
			gwtQueryResult.setMail(copy((Mail) queryResult.getNode(), workspace));
		}

		gwtQueryResult.setExcerpt(queryResult.getExcerpt());
		gwtQueryResult.setScore(queryResult.getScore());

		return gwtQueryResult;
	}

	/**
	 * Copy the QueryParams data to GWTQueryParams data object
	 *
	 * @param params The original QueryParams
	 * @return The GWTQueryParams object with the data from de original QueryParams
	 */
	public static GWTQueryParams copy(QueryParams params) throws RepositoryException, IOException, AccessDeniedException,
			PathNotFoundException, ParseException, DatabaseException, PrincipalAdapterException, NoSuchGroupException {
		GWTQueryParams gWTParams = new GWTQueryParams();

		gWTParams.setId(params.getId());
		gWTParams.setQueryName(params.getQueryName());
		gWTParams.setContent(params.getContent());
		String tmp = "";

		for (Iterator<String> itKwd = params.getKeywords().iterator(); itKwd.hasNext(); ) {
			tmp += itKwd.next() + " ";
		}

		gWTParams.setKeywords(tmp);
		gWTParams.setMimeType(params.getMimeType());
		gWTParams.setName(params.getName());
		gWTParams.setPath(params.getPath());
		gWTParams.setAuthor(params.getAuthor());
		gWTParams.setDashboard(params.isDashboard());
		gWTParams.setDomain(params.getDomain());
		gWTParams.setMailSubject(params.getMailSubject());
		gWTParams.setMailFrom(params.getMailFrom());
		gWTParams.setMailTo(params.getMailTo());
		gWTParams.setOperator(params.getOperator());
		Iterator<String> itCat = params.getCategories().iterator();

		if (itCat.hasNext()) {
			gWTParams.setCategoryUuid(itCat.next());
		}

		if (params.getCategories() != null && !params.getCategories().isEmpty()) {
			itCat = params.getCategories().iterator();
			if (itCat.hasNext()) {
				gWTParams.setCategoryPath(OKMRepository.getInstance().getNodePath(null, itCat.next()));
			}
		}

		if (params.getLastModifiedFrom() != null && params.getLastModifiedTo() != null) {
			gWTParams.setLastModifiedFrom(params.getLastModifiedFrom().getTime());
			gWTParams.setLastModifiedTo(params.getLastModifiedTo().getTime());
		}

		// Sets group name for each property param
		Map<String, GWTPropertyParams> finalProperties = new HashMap<String, GWTPropertyParams>();
		Map<String, String> properties = params.getProperties();
		Collection<String> colKeys = properties.keySet();

		for (Iterator<String> itKeys = colKeys.iterator(); itKeys.hasNext(); ) {
			String key = itKeys.next();
			boolean found = false;

			// Obtain all group names
			Collection<PropertyGroup> colGroups = OKMPropertyGroup.getInstance().getAllGroups(null);
			Iterator<PropertyGroup> itGroup = colGroups.iterator();
			while (itGroup.hasNext() && !found) {
				PropertyGroup group = itGroup.next();

				// Obtain all metadata values
				Collection<FormElement> metaData = OKMPropertyGroup.getInstance().getPropertyGroupForm(null, group.getName());
				for (Iterator<FormElement> it = metaData.iterator(); it.hasNext(); ) {
					FormElement formElement = it.next();
					if (formElement.getName().equals(key)) {
						found = true;
						GWTPropertyParams gWTPropertyParams = new GWTPropertyParams();
						gWTPropertyParams.setGrpName(group.getName());
						gWTPropertyParams.setGrpLabel(group.getLabel());
						gWTPropertyParams.setFormElement(GWTUtil.copy(formElement));
						gWTPropertyParams.setValue(properties.get(key));
						finalProperties.put(key, gWTPropertyParams);
						break;
					}
				}
			}
		}

		gWTParams.setProperties(finalProperties);
		return gWTParams;
	}

	/**
	 * Copy the DashboardDocumentResult data to GWTDashboardDocumentResult
	 *
	 * @param dsDocumentResult The original DashboardDocumentResult
	 * @return The GWTDashboardDocumentResult object with data values from the original
	 *         DashboardDocumentResult
	 */
	public static GWTDashboardDocumentResult copy(DashboardDocumentResult dsDocumentResult) throws PrincipalAdapterException,
			IOException, ParseException, NoSuchGroupException, AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		GWTDashboardDocumentResult gwtDashboardDocumentResult = new GWTDashboardDocumentResult();

		gwtDashboardDocumentResult.setDocument(copy(dsDocumentResult.getDocument(), null));
		gwtDashboardDocumentResult.setVisited(dsDocumentResult.isVisited());
		gwtDashboardDocumentResult.setDate(dsDocumentResult.getDate().getTime());

		return gwtDashboardDocumentResult;
	}

	/**
	 * Copy the DashboardFolderResult data to GWTDashboardFolderResult
	 *
	 * @param dsFolderResult The original DashboardFolderResult
	 * @return The GWTDashboardFolderResult object with data values from the original
	 *         DashboardFolderResult
	 */
	public static GWTDashboardFolderResult copy(DashboardFolderResult dsFolderResult) throws PrincipalAdapterException,
			IOException, ParseException, NoSuchGroupException, AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException {
		GWTDashboardFolderResult gwtDashboardFolderResult = new GWTDashboardFolderResult();

		gwtDashboardFolderResult.setFolder(copy(dsFolderResult.getFolder(), null));
		gwtDashboardFolderResult.setVisited(dsFolderResult.isVisited());
		gwtDashboardFolderResult.setDate(dsFolderResult.getDate().getTime());

		return gwtDashboardFolderResult;
	}

	/**
	 * Copy the DashboardMailResult data to GWTDashboardMailResult
	 *
	 * @param dsMailResult The original DashboardMailResult
	 * @return The GWTDashboardMailResult object with data values from the original
	 *         DashboardMailResult
	 */
	public static GWTDashboardMailResult copy(DashboardMailResult dsmailResult) throws PrincipalAdapterException, IOException,
			ParseException, NoSuchGroupException, AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		GWTDashboardMailResult gwtDashboardMailResult = new GWTDashboardMailResult();

		gwtDashboardMailResult.setMail(copy(dsmailResult.getMail(), null));
		gwtDashboardMailResult.setVisited(dsmailResult.isVisited());
		gwtDashboardMailResult.setDate(dsmailResult.getDate().getTime());

		return gwtDashboardMailResult;
	}

	/**
	 * Copy to ProcessDefinition data to GWTProcessDefinition
	 *
	 * @param ProcessDefinition the original data
	 * @return The GWTProcessDefinition object with data values from original ProcessDefinition
	 */
	public static GWTProcessDefinition copy(ProcessDefinition processDefinition) {
		GWTProcessDefinition gWTProcessDefinition = new GWTProcessDefinition();

		gWTProcessDefinition.setId(processDefinition.getId());
		gWTProcessDefinition.setName(processDefinition.getName());
		gWTProcessDefinition.setVersion(processDefinition.getVersion());
		gWTProcessDefinition.setDescription(processDefinition.getDescription());

		return gWTProcessDefinition;
	}

	/**
	 * Copy to TaskInstance data to GWTTaskInstance
	 *
	 * @param TaskInstance the original data
	 * @return The GWTTaskInstance object with data values from original TaskInstance
	 */
	public static GWTTaskInstance copy(TaskInstance taskInstance) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException, PrincipalAdapterException, IOException, ParseException, NoSuchGroupException {
		GWTTaskInstance gWTTaskInstance = new GWTTaskInstance();

		gWTTaskInstance.setActorId(taskInstance.getActorId());
		gWTTaskInstance.setCreate(taskInstance.getCreate().getTime());
		gWTTaskInstance.setId(taskInstance.getId());
		gWTTaskInstance.setName(taskInstance.getName());
		gWTTaskInstance.setProcessInstance(copy(taskInstance.getProcessInstance()));
		gWTTaskInstance.setDescription(taskInstance.getDescription());

		if (taskInstance.getDueDate() != null) {
			gWTTaskInstance.setDueDate(taskInstance.getDueDate().getTime());
		}

		if (taskInstance.getStart() != null) {
			gWTTaskInstance.setStart(taskInstance.getStart().getTime());
		}

		gWTTaskInstance.setComments(copyComments(taskInstance.getComments()));
		return gWTTaskInstance;
	}

	/**
	 * Copy to ProcessInstance data to GWTProcessInstance
	 *
	 * @param ProcessInstance the original data
	 * @return The GWTProcessInstance object with data values from original ProcessInstance
	 */
	public static GWTProcessInstance copy(ProcessInstance processInstance) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException, PrincipalAdapterException, IOException, ParseException, NoSuchGroupException {
		GWTProcessInstance gWTProcessInstance = new GWTProcessInstance();

		gWTProcessInstance.setEnded(processInstance.isEnded());
		gWTProcessInstance.setId(processInstance.getId());
		gWTProcessInstance.setProcessDefinition(copy(processInstance.getProcessDefinition()));
		gWTProcessInstance.setSuspended(processInstance.isSuspended());
		Map<String, Object> variables = new HashMap<String, Object>();

		for (Iterator<String> it = processInstance.getVariables().keySet().iterator(); it.hasNext(); ) {
			String key = it.next();
			Object obj = processInstance.getVariables().get(key);

			if (obj instanceof FormElement) {
				variables.put(key, copy((FormElement) obj));
			} else {
				variables.put(key, String.valueOf(obj));
			}
		}

		gWTProcessInstance.setVariables(variables);
		gWTProcessInstance.setVersion(processInstance.getVersion());
		gWTProcessInstance.setKey(processInstance.getKey());
		gWTProcessInstance.setRootToken(copy(processInstance.getRootToken()));

		if (processInstance.getStart() != null) {
			gWTProcessInstance.setStart(processInstance.getStart().getTime());
		}

		if (processInstance.getEnd() != null) {
			gWTProcessInstance.setEnd(processInstance.getEnd().getTime());
		}

		return gWTProcessInstance;
	}

	/**
	 * Copy to Token data to GWTToken
	 *
	 * @param Token the original data
	 * @return The GWTToken object with data values from original Token
	 */
	public static GWTToken copy(Token token) throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException,
			PrincipalAdapterException, IOException, ParseException, NoSuchGroupException {
		GWTToken gWTToken = new GWTToken();
		Collection<GWTTransition> availableTransitions = new ArrayList<GWTTransition>();

		for (Iterator<Transition> it = token.getAvailableTransitions().iterator(); it.hasNext(); ) {
			availableTransitions.add(copy(it.next()));
		}

		gWTToken.setAvailableTransitions(availableTransitions);
		Collection<GWTWorkflowComment> comments = new ArrayList<GWTWorkflowComment>();

		for (Iterator<Comment> it = token.getComments().iterator(); it.hasNext(); ) {
			comments.add(copy(it.next()));
		}

		gWTToken.setComments(comments);

		if (token.getEnd() != null) {
			gWTToken.setEnd(token.getEnd().getTime());
		}

		gWTToken.setId(token.getId());
		gWTToken.setName(token.getName());
		gWTToken.setNode(token.getNode());

		if (token.getParent() != null) {
			gWTToken.setParent(copy(token.getParent()));
		}

		if (token.getProcessInstance() != null) {
			gWTToken.setProcessInstance(copy(token.getProcessInstance()));
		}

		gWTToken.setStart(token.getStart().getTime());
		gWTToken.setSuspended(token.isSuspended());

		return gWTToken;
	}

	/**
	 * Copy to Token data to GWTTransition
	 *
	 * @param Transition the original data
	 * @return The GWTTransition object with data values from original Transition
	 */
	public static GWTTransition copy(Transition transition) {
		GWTTransition gWTTransition = new GWTTransition();
		gWTTransition.setFrom(transition.getFrom());
		gWTTransition.setId(transition.getId());
		gWTTransition.setName(transition.getName());
		gWTTransition.setTo(transition.getTo());

		return gWTTransition;
	}

	/**
	 * Copy to Comment data to GWTWorkFlowComment
	 *
	 * @param Transition the original data
	 * @return The GWTWorkFlowComment object with data values from original Comment
	 */
	public static GWTWorkflowComment copy(Comment comment) {
		GWTWorkflowComment gWTComment = new GWTWorkflowComment();
		gWTComment.setActorId(comment.getActorId());
		gWTComment.setMessage(comment.getMessage());
		gWTComment.setTime(comment.getTime().getTime());

		return gWTComment;
	}

	/**
	 * Copy to Validator data to GWTValidator
	 *
	 * @param Validator the original data
	 * @return The GWTValidator object with data values from original Validator
	 */
	public static List<GWTValidator> copyValidators(List<Validator> validators) {
		List<GWTValidator> gwtValidatorsList = new ArrayList<GWTValidator>();
		for (Validator validator : validators) {
			gwtValidatorsList.add(copy(validator));
		}
		return gwtValidatorsList;
	}

	/**
	 * copyNodes
	 */
	public static List<GWTNode> copyNodes(List<Node> nodes) {
		List<GWTNode> gwtNodesList = new ArrayList<GWTNode>();
		for (Node node : nodes) {
			gwtNodesList.add(copy(node));
		}
		return gwtNodesList;
	}

	/**
	 * Copy to FormElement data to GWTFormElemen
	 *
	 * @param FormElement the original data
	 * @return The GWTFormElement object with data values from original FormElement
	 */
	public static GWTFormElement copy(FormElement formElement) throws AccessDeniedException, PathNotFoundException, RepositoryException,
			DatabaseException, PrincipalAdapterException, IOException, ParseException, NoSuchGroupException {
		if (formElement instanceof Button) {
			GWTButton gWTButton = new GWTButton();
			gWTButton.setName(formElement.getName());
			gWTButton.setLabel(formElement.getLabel());
			gWTButton.setWidth(formElement.getWidth());
			gWTButton.setHeight(formElement.getHeight());
			Button button = (Button) formElement;
			gWTButton.setTransition(button.getTransition());
			gWTButton.setConfirmation(button.getConfirmation());
			gWTButton.setStyle(button.getStyle());
			gWTButton.setValidate(button.isValidate());
			return gWTButton;
		} else if (formElement instanceof Input) {
			GWTInput gWTInput = new GWTInput();
			gWTInput.setName(formElement.getName());
			gWTInput.setLabel(formElement.getLabel());
			gWTInput.setWidth(formElement.getWidth());
			gWTInput.setHeight(formElement.getHeight());
			Input input = (Input) formElement;
			gWTInput.setReadonly(input.isReadonly());
			gWTInput.setValue(input.getValue());

			if (input.getType().equals(Input.TYPE_DATE)) {
				if (!input.getValue().equals("")) {
					Calendar date = ISO8601.parseBasic(input.getValue());

					if (date != null) {
						gWTInput.setDate(date.getTime());
					} else {
						log.warn("Input '{}' value should be in ISO8601 format: {}", input.getName(), input.getValue());
					}
				}
			}

			if (input.getType().equals(Input.TYPE_FOLDER) && !gWTInput.getValue().equals("")) {
				try {
					gWTInput.setFolder(copy(OKMFolder.getInstance().getProperties(null, ((Input) formElement).getValue()), null));
				} catch (PathNotFoundException e) {
					log.warn("Folder not found: {}", e.getMessage(), e);
				}
			}

			gWTInput.setType(((Input) formElement).getType());
			gWTInput.setValidators(copyValidators(input.getValidators()));
			gWTInput.setData(input.getData());
			return gWTInput;
		} else if (formElement instanceof SuggestBox) {
			GWTSuggestBox gWTsuggestBox = new GWTSuggestBox();
			gWTsuggestBox.setName(formElement.getName());
			gWTsuggestBox.setLabel(formElement.getLabel());
			gWTsuggestBox.setWidth(formElement.getWidth());
			gWTsuggestBox.setHeight(formElement.getHeight());
			SuggestBox suggestBox = (SuggestBox) formElement;
			gWTsuggestBox.setReadonly(suggestBox.isReadonly());
			gWTsuggestBox.setValidators(copyValidators(suggestBox.getValidators()));
			gWTsuggestBox.setValue(suggestBox.getValue());
			gWTsuggestBox.setDialogTitle(suggestBox.getDialogTitle());
			gWTsuggestBox.setTable(suggestBox.getTable());
			gWTsuggestBox.setFilterQuery(suggestBox.getFilterQuery());
			gWTsuggestBox.setValueQuery(suggestBox.getValueQuery());
			gWTsuggestBox.setFilterMinLen(suggestBox.getFilterMinLen());
			gWTsuggestBox.setData(suggestBox.getData());

			if (!suggestBox.getValue().equals("")) {
				String formatedQuery = MessageFormat.format(suggestBox.getValueQuery().replaceAll("'", "\\\""),
						suggestBox.getValue()).replaceAll("\"", "'");
				List<KeyValue> keyValues = KeyValueDAO.getKeyValues(Arrays.asList(suggestBox.getTable()), formatedQuery);

				if (!keyValues.isEmpty()) {
					gWTsuggestBox.setText(keyValues.get(0).getValue());
				}
			}
			return gWTsuggestBox;
		} else if (formElement instanceof CheckBox) {
			GWTCheckBox gWTCheckbox = new GWTCheckBox();
			gWTCheckbox.setName(formElement.getName());
			gWTCheckbox.setLabel(formElement.getLabel());
			CheckBox checkbox = (CheckBox) formElement;
			gWTCheckbox.setValue(checkbox.getValue());
			gWTCheckbox.setReadonly(checkbox.isReadonly());
			gWTCheckbox.setValidators(copyValidators(checkbox.getValidators()));
			gWTCheckbox.setData(checkbox.getData());
			return gWTCheckbox;
		} else if (formElement instanceof Select) {
			GWTSelect gWTselect = new GWTSelect();
			gWTselect.setName(formElement.getName());
			gWTselect.setLabel(formElement.getLabel());
			gWTselect.setWidth(formElement.getWidth());
			gWTselect.setHeight(formElement.getHeight());
			Select select = (Select) formElement;
			gWTselect.setType(select.getType());
			gWTselect.setReadonly(select.isReadonly());

			List<GWTOption> options = new ArrayList<GWTOption>();
			for (Option option : select.getOptions()) {
				options.add(copy(option));
			}

			gWTselect.setOptions(options);
			gWTselect.setValidators(copyValidators(select.getValidators()));
			gWTselect.setData(select.getData());
			gWTselect.setOptionsData(select.getOptionsData());
			gWTselect.setSuggestion(select.getSuggestion());
			gWTselect.setClassName(select.getClassName());
			return gWTselect;
		} else if (formElement instanceof TextArea) {
			GWTTextArea gWTTextArea = new GWTTextArea();
			gWTTextArea.setName(formElement.getName());
			gWTTextArea.setLabel(formElement.getLabel());
			gWTTextArea.setWidth(formElement.getWidth());
			gWTTextArea.setHeight(formElement.getHeight());
			TextArea textArea = (TextArea) formElement;
			gWTTextArea.setValue(textArea.getValue());
			gWTTextArea.setReadonly(textArea.isReadonly());
			gWTTextArea.setValidators(copyValidators(textArea.getValidators()));
			gWTTextArea.setData(textArea.getData());
			return gWTTextArea;
		} else if (formElement instanceof Upload) {
			GWTUpload gWTUpload = new GWTUpload();
			gWTUpload.setName(formElement.getName());
			gWTUpload.setLabel(formElement.getLabel());
			gWTUpload.setWidth(formElement.getWidth());
			gWTUpload.setHeight(formElement.getHeight());
			Upload upload = (Upload) formElement;
			gWTUpload.setFolderPath(upload.getFolderPath());
			gWTUpload.setFolderUuid(upload.getFolderUuid());
			gWTUpload.setDocumentName(upload.getDocumentName());
			gWTUpload.setDocumentUuid(upload.getDocumentUuid());
			gWTUpload.setType(upload.getType());
			gWTUpload.setData(upload.getData());
			gWTUpload.setValidators(copyValidators(upload.getValidators()));
			return gWTUpload;
		} else if (formElement instanceof Text) {
			GWTText gWTtext = new GWTText();
			gWTtext.setName(formElement.getName());
			gWTtext.setLabel(formElement.getLabel());
			gWTtext.setHeight(formElement.getHeight());
			gWTtext.setWidth(formElement.getWidth());
			Text text = (Text) formElement;
			gWTtext.setData(text.getData());
			return gWTtext;
		} else if (formElement instanceof Separator) {
			GWTSeparator separator = new GWTSeparator();
			separator.setName(formElement.getName());
			separator.setLabel(formElement.getLabel());
			separator.setHeight(formElement.getHeight());
			separator.setWidth(formElement.getWidth());
			return separator;
		} else if (formElement instanceof Download) {
			GWTDownload gWTdownload = new GWTDownload();
			gWTdownload.setName(formElement.getName());
			gWTdownload.setLabel(formElement.getLabel());
			gWTdownload.setHeight(formElement.getHeight());
			gWTdownload.setWidth(formElement.getWidth());
			Download download = (Download) formElement;
			gWTdownload.setData(download.getData());
			gWTdownload.setValidators(copyValidators(download.getValidators()));
			gWTdownload.setNodes(copyNodes(download.getNodes()));
			return gWTdownload;
		} else if (formElement instanceof Print) {
			GWTPrint gWTprint = new GWTPrint();
			gWTprint.setName(formElement.getName());
			gWTprint.setLabel(formElement.getLabel());
			gWTprint.setHeight(formElement.getHeight());
			gWTprint.setWidth(formElement.getWidth());
			Print download = (Print) formElement;
			gWTprint.setData(download.getData());
			gWTprint.setValidators(copyValidators(download.getValidators()));
			gWTprint.setNodes(copyNodes(download.getNodes()));
			return gWTprint;
		} else {
			return new GWTFormElement();
		}
	}

	/**
	 * Copy to GWTFormElement data to FormElement
	 *
	 * @param GWTFormElement the original data
	 * @return The FormElement object with data values from original GWTFormElement
	 */
	public static FormElement copy(GWTFormElement formElement) {
		if (formElement instanceof GWTButton) {
			Button button = new Button();
			button.setName(formElement.getName());
			GWTButton gWTButton = ((GWTButton) formElement);
			button.setTransition(gWTButton.getTransition());
			button.setConfirmation(gWTButton.getConfirmation());
			button.setStyle(gWTButton.getStyle());
			button.setValidate(gWTButton.isValidate());
			return button;
		} else if (formElement instanceof GWTInput) {
			Input input = new Input();
			input.setName(formElement.getName());
			GWTInput gWTInput = ((GWTInput) formElement);
			input.setReadonly(gWTInput.isReadonly());

			if (gWTInput.getType().equals(GWTInput.TYPE_TEXT) || gWTInput.getType().equals(GWTInput.TYPE_LINK)
					|| gWTInput.getType().equals(GWTInput.TYPE_FOLDER)) {
				input.setValue(gWTInput.getValue());
			} else if (gWTInput.getType().equals(GWTInput.TYPE_DATE)) {
				if (gWTInput.getDate() != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(((GWTInput) formElement).getDate());
					input.setValue(ISO8601.formatBasic(cal));
				}
			}

			input.setType(gWTInput.getType());
			input.setData(gWTInput.getData());
			return input;
		} else if (formElement instanceof GWTSuggestBox) {
			SuggestBox suggestBox = new SuggestBox();
			suggestBox.setName(formElement.getName());
			GWTSuggestBox gWTSuggestBox = ((GWTSuggestBox) formElement);
			suggestBox.setReadonly(gWTSuggestBox.isReadonly());
			suggestBox.setValue(gWTSuggestBox.getValue());
			suggestBox.setFilterQuery(gWTSuggestBox.getFilterQuery());
			suggestBox.setValueQuery(gWTSuggestBox.getValueQuery());
			suggestBox.setFilterMinLen(gWTSuggestBox.getFilterMinLen());
			suggestBox.setData(gWTSuggestBox.getData());
			suggestBox.setTable(gWTSuggestBox.getTable());
			return suggestBox;
		} else if (formElement instanceof GWTCheckBox) {
			CheckBox checkbox = new CheckBox();
			checkbox.setLabel(formElement.getLabel());
			checkbox.setName(formElement.getName());
			GWTCheckBox gWTCheckBox = ((GWTCheckBox) formElement);
			checkbox.setValue(gWTCheckBox.getValue());
			checkbox.setReadonly(gWTCheckBox.isReadonly());
			checkbox.setData(gWTCheckBox.getData());
			return checkbox;
		} else if (formElement instanceof GWTSelect) {
			Select select = new Select();
			select.setName(formElement.getName());
			GWTSelect gWTSelect = ((GWTSelect) formElement);
			select.setType(gWTSelect.getType());
			select.setReadonly(gWTSelect.isReadonly());
			List<Option> options = new ArrayList<Option>();

			for (GWTOption option : gWTSelect.getOptions()) {
				options.add(copy(option));

				if (option.isSelected()) {
					if (Select.TYPE_SIMPLE.equals(select.getType())) {
						select.setValue(option.getValue());
					} else {
						if ("".equals(select.getValue())) {
							select.setValue(option.getValue());
						} else {
							select.setValue(select.getValue().concat(",").concat(option.getValue()));
						}
					}
				}
			}

			select.setOptions(options);
			select.setData(gWTSelect.getData());
			select.setOptionsData(gWTSelect.getOptionsData());
			return select;
		} else if (formElement instanceof GWTTextArea) {
			TextArea textArea = new TextArea();
			textArea.setName(formElement.getName());
			GWTTextArea gWTTextArea = ((GWTTextArea) formElement);
			textArea.setValue(gWTTextArea.getValue());
			textArea.setReadonly(gWTTextArea.isReadonly());
			textArea.setData(gWTTextArea.getData());
			return textArea;
		} else if (formElement instanceof GWTUpload) {
			Upload upload = new Upload();
			upload.setName(formElement.getName());
			GWTUpload gWTUpload = ((GWTUpload) formElement);
			upload.setDocumentName(gWTUpload.getDocumentName());
			upload.setDocumentUuid(gWTUpload.getDocumentUuid());
			upload.setFolderPath(gWTUpload.getFolderPath());
			upload.setFolderUuid(gWTUpload.getFolderUuid());
			upload.setType(gWTUpload.getType());
			upload.setData(gWTUpload.getData());
			return upload;
		} else if (formElement instanceof GWTText) {
			Text text = new Text();
			GWTText gWTText = (GWTText) formElement;
			text.setName(gWTText.getName());
			text.setLabel(gWTText.getLabel());
			text.setHeight(gWTText.getHeight());
			text.setWidth(gWTText.getWidth());
			text.setData(gWTText.getData());
			return text;
		} else if (formElement instanceof GWTSeparator) {
			Separator separator = new Separator();
			GWTSeparator gWTSeparator = (GWTSeparator) formElement;
			separator.setName(gWTSeparator.getName());
			separator.setLabel(gWTSeparator.getLabel());
			separator.setHeight(gWTSeparator.getHeight());
			separator.setWidth(gWTSeparator.getWidth());
			return separator;
		} else if (formElement instanceof GWTDownload) {
			Download download = new Download();
			GWTDownload gWTDownload = (GWTDownload) formElement;
			download.setName(gWTDownload.getName());
			download.setLabel(gWTDownload.getLabel());
			download.setHeight(gWTDownload.getHeight());
			download.setWidth(gWTDownload.getWidth());
			download.setData(gWTDownload.getData());
			List<Node> nodes = new ArrayList<Node>();

			for (GWTNode gWTNode : gWTDownload.getNodes()) {
				nodes.add(copy(gWTNode));
			}

			return download;
		} else if (formElement instanceof GWTPrint) {
			Print print = new Print();
			GWTPrint gWTprint = (GWTPrint) formElement;
			print.setName(gWTprint.getName());
			print.setLabel(gWTprint.getLabel());
			print.setHeight(gWTprint.getHeight());
			print.setWidth(gWTprint.getWidth());
			print.setData(gWTprint.getData());
			List<Node> nodes = new ArrayList<Node>();

			for (GWTNode gWTNode : gWTprint.getNodes()) {
				nodes.add(copy(gWTNode));
			}

			return print;
		} else {
			return new FormElement();
		}
	}

	/**
	 * getFormElementValue
	 */
	public static Object getFormElementValue(GWTFormElement formElement) throws DatabaseException {
		if (formElement instanceof GWTButton) {
			return ((GWTButton) formElement).getLabel();
		} else if (formElement instanceof GWTInput) {
			GWTInput input = (GWTInput) formElement;

			if (GWTInput.TYPE_DATE.equals(input.getType())) {
				return input.getDate();
			} else {
				return input.getValue();
			}
		} else if (formElement instanceof GWTSuggestBox) {
			GWTSuggestBox suggestBox = (GWTSuggestBox) formElement;

			// The ' character must be replaced to \" to be correctly parsed
			// and after it must change all " characters to '
			String formatedQuery = MessageFormat.format(suggestBox.getValueQuery().replaceAll("'", "\\\""),
					suggestBox.getValue()).replaceAll("\"", "'");
			List<KeyValue> keyValues = KeyValueDAO.getKeyValues(Arrays.asList(suggestBox.getTable()), formatedQuery);

			if (!keyValues.isEmpty()) {
				return keyValues.get(0).getValue();
			} else {
				return "";
			}
		} else if (formElement instanceof GWTCheckBox) {
			return ((GWTCheckBox) formElement).getValue() ? "true" : "false";
		} else if (formElement instanceof GWTSelect) {
			String value = "";

			for (Iterator<GWTOption> it = ((GWTSelect) formElement).getOptions().iterator(); it.hasNext(); ) {
				GWTOption option = it.next();

				if (option.isSelected()) {
					value += option.getLabel() + " ";
				}
			}

			return value;
		} else if (formElement instanceof GWTTextArea) {
			return ((GWTTextArea) formElement).getValue();
		} else if (formElement instanceof GWTUpload) {
			return ((GWTUpload) formElement).getLabel();
		} else if (formElement instanceof GWTText) {
			return ((GWTText) formElement).getLabel();
		} else if (formElement instanceof GWTSeparator) {
			return ((GWTSeparator) formElement).getLabel();
		} else if (formElement instanceof GWTDownload) {
			GWTDownload download = ((GWTDownload) formElement);
			String value = "";

			for (GWTNode node : download.getNodes()) {
				if (!value.equals("")) {
					value += ",";
				}

				if (!node.getUuid().equals("")) {
					value += node.getUuid();
				} else {
					value += node.getPath();
				}
			}

			return value;
		} else if (formElement instanceof GWTPrint) {
			GWTPrint print = ((GWTPrint) formElement);
			String value = "";

			for (GWTNode node : print.getNodes()) {
				if (!value.equals("")) {
					value += ",";
				}

				if (!node.getUuid().equals("")) {
					value += node.getUuid();
				} else {
					value += node.getPath();
				}
			}

			return value;
		}

		return "";
	}

	/**
	 * Copy to Validator data to GWTValidator
	 *
	 * @param Validator the original data
	 * @return The GWTValidator object with data values from original Validator
	 */
	public static GWTValidator copy(Validator validator) {
		GWTValidator gWTValidator = new GWTValidator();
		gWTValidator.setParameter(validator.getParameter());
		gWTValidator.setType(validator.getType());
		return gWTValidator;
	}

	/**
	 * Copy to Node data to GWTNode
	 *
	 * @param Node the original data
	 * @return The GWTNode object with data values from original Node
	 */
	public static GWTNode copy(Node node) {
		GWTNode gWTNode = new GWTNode();
		gWTNode.setLabel(node.getLabel());
		gWTNode.setPath(node.getPath());
		gWTNode.setUuid(node.getUuid());
		return gWTNode;
	}

	/**
	 * Copy to GWTNode data to Node
	 *
	 * @param GWTNode the original data
	 * @return The Node object with data values from original GWTNode
	 */
	public static Node copy(GWTNode gWTNode) {
		Node node = new Node();
		node.setLabel(gWTNode.getLabel());
		node.setPath(gWTNode.getPath());
		node.setUuid(gWTNode.getUuid());
		return node;
	}

	/**
	 * Copy to GWTOption data to Option
	 *
	 * @param GWTOption the original data
	 * @return The Option object with data values from original GWTOption
	 */
	public static Option copy(GWTOption gWTOption) {
		Option option = new Option();
		option.setLabel(gWTOption.getLabel());
		option.setValue(gWTOption.getValue());
		option.setSelected(gWTOption.isSelected());
		return option;
	}

	/**
	 * Copy to Option data to GWTOption
	 *
	 * @param Option the original data
	 * @return The GWTOption object with data values from original Option
	 */
	public static GWTOption copy(Option option) {
		GWTOption gWTOption = new GWTOption();
		gWTOption.setLabel(option.getLabel());
		gWTOption.setValue(option.getValue());
		gWTOption.setSelected(option.isSelected());
		return gWTOption;
	}

	/**
	 * Copy to Comment data to GWTComment
	 *
	 * @param Comment the original data
	 * @return The GWTTaskInstanceComment object with data values from original TaskInstanceComment
	 */
	public static List<GWTComment> copyComments(List<Comment> list) {
		List<GWTComment> al = new ArrayList<GWTComment>();
		GWTComment gWTComment;

		for (Iterator<Comment> it = list.iterator(); it.hasNext(); ) {
			Comment comment = it.next();
			gWTComment = new GWTComment();

			gWTComment.setActorId(comment.getActorId());
			gWTComment.setMessage(comment.getMessage());
			gWTComment.setTime(comment.getTime().getTime());
			al.add(gWTComment);
		}

		return al;
	}

	/**
	 * Copy Note data to GWTNote
	 *
	 * @param Note the original data
	 * @return The GWTNote object with data values from original Note
	 */
	public static List<GWTNote> copy(List<Note> commentList) throws PrincipalAdapterException {
		List<GWTNote> gWTCommentList = new ArrayList<GWTNote>();

		for (Iterator<Note> it = commentList.iterator(); it.hasNext(); ) {
			gWTCommentList.add(copy(it.next()));
		}

		return gWTCommentList;
	}

	/**
	 * Copy Note data to GWTNote
	 *
	 * @param Note the original data
	 * @return The GWTNote object with data values from original Note
	 */
	public static GWTNote copy(Note note) throws PrincipalAdapterException {
		GWTNote gWTNote = new GWTNote();
		gWTNote.setDate(note.getDate().getTime());
		gWTNote.setText(note.getText());
		gWTNote.setAuthor(note.getAuthor());
		GWTUser user = new GWTUser();
		user.setId(note.getAuthor());
		user.setUsername(OKMAuth.getInstance().getName(null, note.getAuthor()));
		gWTNote.setUser(user);
		gWTNote.setPath(note.getPath());
		return gWTNote;
	}

	/**
	 * Copy the Mail data to GWTMail data.
	 *
	 * @param mail The original Mail object.
	 * @param workspace
	 * @return A GWTMail object with the data from the original Mail.
	 */
	public static GWTMail copy(Mail mail, GWTWorkspace workspace) throws PrincipalAdapterException, IOException,
			ParseException, NoSuchGroupException, AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("copy({})", mail);
		GWTMail gwtMail = MappingUtils.getMapper().map(mail, GWTMail.class);
		gwtMail.setParentPath(GWTUtil.getParent(mail.getPath()));

		for (GWTDocument doc : gwtMail.getAttachments()) {
			doc.setParentPath(GWTUtil.getParent(doc.getPath()));
			doc.setName(GWTUtil.getName(doc.getPath()));
		}

		// Notes user id & username
		gwtMail.getNotes().clear();

		for (Note note : mail.getNotes()) {
			gwtMail.getNotes().add(copy(note));

			if (!note.getAuthor().equals(Config.SYSTEM_USER)) {
				gwtMail.setHasNotes(true);
			}
		}

		// Extra columns
		if (workspace != null) {
			if (workspace.getProfileFileBrowser().getColumn0() != null) {
				gwtMail.setColumn0(getExtraColumn(gwtMail.getPath(), workspace.getProfileFileBrowser().getColumn0()));
			}
			if (workspace.getProfileFileBrowser().getColumn1() != null) {
				gwtMail.setColumn1(getExtraColumn(gwtMail.getPath(), workspace.getProfileFileBrowser().getColumn1()));
			}
			if (workspace.getProfileFileBrowser().getColumn2() != null) {
				gwtMail.setColumn2(getExtraColumn(gwtMail.getPath(), workspace.getProfileFileBrowser().getColumn2()));
			}
			if (workspace.getProfileFileBrowser().getColumn3() != null) {
				gwtMail.setColumn3(getExtraColumn(gwtMail.getPath(), workspace.getProfileFileBrowser().getColumn3()));
			}
			if (workspace.getProfileFileBrowser().getColumn4() != null) {
				gwtMail.setColumn4(getExtraColumn(gwtMail.getPath(), workspace.getProfileFileBrowser().getColumn4()));
			}
			if (workspace.getProfileFileBrowser().getColumn5() != null) {
				gwtMail.setColumn5(getExtraColumn(gwtMail.getPath(), workspace.getProfileFileBrowser().getColumn5()));
			}
			if (workspace.getProfileFileBrowser().getColumn6() != null) {
				gwtMail.setColumn6(getExtraColumn(gwtMail.getPath(), workspace.getProfileFileBrowser().getColumn6()));
			}
			if (workspace.getProfileFileBrowser().getColumn7() != null) {
				gwtMail.setColumn7(getExtraColumn(gwtMail.getPath(), workspace.getProfileFileBrowser().getColumn7()));
			}
			if (workspace.getProfileFileBrowser().getColumn8() != null) {
				gwtMail.setColumn8(getExtraColumn(gwtMail.getPath(), workspace.getProfileFileBrowser().getColumn8()));
			}
			if (workspace.getProfileFileBrowser().getColumn9() != null) {
				gwtMail.setColumn9(getExtraColumn(gwtMail.getPath(), workspace.getProfileFileBrowser().getColumn9()));
			}
		}

		log.debug("copy: {}", gwtMail);
		return gwtMail;
	}

	/**
	 * Copy the PropertyGroup data to GWTPropertyGroup data.
	 *
	 * @param doc The original PropertyGroup object.
	 * @return A GWTPropertyGroup object with the data from the original PropertyGroup.
	 */
	public static GWTPropertyGroup copy(PropertyGroup property) {
		GWTPropertyGroup gWTPropertyGroup = new GWTPropertyGroup();

		gWTPropertyGroup.setLabel(property.getLabel());
		gWTPropertyGroup.setName(property.getName());
		gWTPropertyGroup.setVisible(property.isVisible());
		gWTPropertyGroup.setReadonly(property.isReadonly());

		return gWTPropertyGroup;
	}

	/**
	 * Copy the UserConfig data to GWTUserConfig data.
	 *
	 * @param doc The original UserConfig object.
	 * @return A GWTUserConfig object with the data from the original UserConfig.
	 */
	public static GWTUserConfig copy(UserConfig userConfig) {
		GWTUserConfig gwtUserConfig = MappingUtils.getMapper().map(userConfig, GWTUserConfig.class);
		return gwtUserConfig;
	}

	/**
	 * Copy the StaplingGroup data to GWTStapleGroup data.
	 *
	 * @param doc The original StaplingGroup object.
	 * @return A GWTStaplingGroup object with the data from the original StaplingGroup.
	 */
	public static GWTStapleGroup copy(StapleGroup sg) throws RepositoryException, DatabaseException, PathNotFoundException,
			PrincipalAdapterException, IOException, ParseException, NoSuchGroupException {
		GWTStapleGroup gsg = new GWTStapleGroup();
		gsg.setId(sg.getId());
		gsg.setUser(sg.getUser());

		for (Staple st : sg.getStaples()) {
			GWTStaple gst = new GWTStaple();
			gst.setId(st.getId());
			gst.setType(st.getType());

			try {
				// Getting document / folder / mail properties
				if (st.getType().equals(Staple.STAPLE_DOCUMENT)) {
					String path = OKMDocument.getInstance().getPath(null, st.getNode());
					gst.setDoc(copy(OKMDocument.getInstance().getProperties(null, path), null));
				} else if (st.getType().equals(Staple.STAPLE_FOLDER)) {
					String path = OKMFolder.getInstance().getPath(null, st.getNode());
					gst.setFolder(copy(OKMFolder.getInstance().getProperties(null, path), null));
				} else if (st.getType().equals(Staple.STAPLE_MAIL)) {
					String path = OKMMail.getInstance().getPath(null, st.getNode());
					gst.setMail(GWTUtil.copy(OKMMail.getInstance().getProperties(null, path), null));
				}

				gsg.getStaples().add(gst);
			} catch (AccessDeniedException e) {
				// Silent exception, some users have not accessing to some documents, folders or mails
			}
		}

		return gsg;
	}

	/**
	 * Copy Language to GWTLanguage
	 */
	public static GWTLanguage copy(Language language) {
		GWTLanguage gWTlang = new GWTLanguage();
		gWTlang.setId(language.getId());
		gWTlang.setName(language.getName());
		return gWTlang;
	}

	/**
	 * Copy GWTProposedSubscriptionReceived to GWTProposedSubscription
	 */
	public static GWTProposedSubscriptionReceived copy(ProposedSubscriptionReceived psr, String path) {
		GWTProposedSubscriptionReceived gwtPsr = MappingUtils.getMapper().map(psr, GWTProposedSubscriptionReceived.class);
		gwtPsr.setPath(path);
		return gwtPsr;
	}

	/**
	 * Copy ProposedSubscriptionSent to GWTProposedSubscriptionSent
	 */
	public static GWTProposedSubscriptionSent copy(ProposedSubscriptionSent pss, String path) {
		GWTProposedSubscriptionSent gwtPss = MappingUtils.getMapper().map(pss, GWTProposedSubscriptionSent.class);
		gwtPss.setPath(path);
		return gwtPss;
	}

	/**
	 * Copy ProposedQuery to GWTProposedQuery
	 */
	public static GWTProposedQueryReceived copy(ProposedQueryReceived pq, QueryParams params) throws RepositoryException,
			IOException, AccessDeniedException, PathNotFoundException, ParseException, DatabaseException, PrincipalAdapterException,
			NoSuchGroupException {
		GWTProposedQueryReceived gWTProposedQuery = new GWTProposedQueryReceived();
		gWTProposedQuery.setAccepted(pq.isAccepted());
		gWTProposedQuery.setComment(pq.getComment());
		gWTProposedQuery.setFrom(pq.getFrom());
		gWTProposedQuery.setTo(pq.getTo());
		gWTProposedQuery.setUser(pq.getUser());
		gWTProposedQuery.setId(pq.getId());
		gWTProposedQuery.setSentDate(pq.getSentDate().getTime());

		if (pq.getSeenDate() != null) {
			gWTProposedQuery.setSeenDate(pq.getSeenDate().getTime());
		}

		gWTProposedQuery.setParams(copy(params));
		return gWTProposedQuery;
	}

	/**
	 * Copy ProposedQuery to GWTProposedQuery
	 */
	public static GWTProposedQuerySent copy(ProposedQuerySent pq, QueryParams params) throws RepositoryException, IOException,
			AccessDeniedException, PathNotFoundException, ParseException, DatabaseException, PrincipalAdapterException, NoSuchGroupException {
		GWTProposedQuerySent gWTProposedQuery = new GWTProposedQuerySent();
		gWTProposedQuery.setComment(pq.getComment());
		gWTProposedQuery.setFrom(pq.getFrom());
		gWTProposedQuery.setTo(pq.getTo());
		gWTProposedQuery.setUser(pq.getUser());
		gWTProposedQuery.setId(pq.getId());
		gWTProposedQuery.setSentDate(pq.getSentDate().getTime());
		gWTProposedQuery.setParams(copy(params));
		return gWTProposedQuery;
	}

	/**
	 * Copy MessageSent to GWTMessageSent
	 */
	public static GWTTextMessageSent copy(MessageSent ms) {
		GWTTextMessageSent gWTMessageSent = new GWTTextMessageSent();
		gWTMessageSent.setContent(ms.getContent());
		gWTMessageSent.setFrom(ms.getFrom());
		gWTMessageSent.setUser(ms.getUser());
		gWTMessageSent.setId(ms.getId());
		gWTMessageSent.setSentDate(ms.getSentDate().getTime());
		gWTMessageSent.setSubject(ms.getSubject());
		gWTMessageSent.setTo(ms.getTo());

		return gWTMessageSent;
	}

	/**
	 * Copy MessageReceived to GWTMessageReceived
	 */
	public static GWTMessageReceived copy(MessageReceived mr) {
		GWTMessageReceived gWTMessageReceived = new GWTMessageReceived();
		gWTMessageReceived.setContent(mr.getContent());
		gWTMessageReceived.setFrom(mr.getFrom());
		gWTMessageReceived.setId(mr.getId());

		if (mr.getSeenDate() != null) {
			gWTMessageReceived.setSeenDate(mr.getSeenDate().getTime());
		}

		gWTMessageReceived.setSentDate(mr.getSentDate().getTime());
		gWTMessageReceived.setSubject(mr.getSubject());
		gWTMessageReceived.setTo(mr.getTo());
		gWTMessageReceived.setUser(mr.getUser());

		return gWTMessageReceived;
	}

	/**
	 * Copy Activity to GWTActivity
	 */
	public static GWTActivity copy(Activity activity) {
		GWTActivity gwtActivity = MappingUtils.getMapper().map(activity, GWTActivity.class);
		return gwtActivity;
	}

	/**
	 * Copy Forum to GWTForum
	 */
	public static GWTForum copy(Forum forum) {
		GWTForum f = new GWTForum();
		f.setActive(forum.isActive());
		f.setDate(forum.getDate().getTime());
		f.setDescription(forum.getDescription());
		f.setId(forum.getId());
		f.setLastPostDate(forum.getLastPostDate().getTime());
		f.setLastPostUser(forum.getLastPostUser());
		f.setName(forum.getName());
		f.setNumPosts(forum.getNumPosts());
		f.setNumTopics(forum.getNumTopics());

		for (ForumTopic topic : forum.getTopics()) {
			f.getTopics().add(copy(topic));
		}

		return f;
	}

	/**
	 * Copy ForumTopic to GWTForumTopic
	 */
	public static GWTForumTopic copy(ForumTopic topic) {
		GWTForumTopic ft = new GWTForumTopic();
		ft.setDate(topic.getDate().getTime());
		ft.setId(topic.getId());
		ft.setReplies(topic.getReplies());
		ft.setLastPostDate(topic.getLastPostDate().getTime());
		ft.setLastPostUser(topic.getLastPostUser());
		ft.setTitle(topic.getTitle());
		ft.setUser(topic.getUser());
		ft.setNode(topic.getNode());
		ft.setViews(topic.getViews());

		for (Iterator<ForumPost> it = topic.getPosts().iterator(); it.hasNext(); ) {
			ft.getPosts().add(copy(it.next()));
		}

		return ft;
	}

	/**
	 * Copy ForumPost to GWTForumPost
	 */
	public static GWTForumPost copy(ForumPost post) {
		GWTForumPost fp = new GWTForumPost();
		fp.setDate(post.getDate().getTime());
		fp.setId(post.getId());
		fp.setMessage(post.getMessage());
		fp.setSubject(post.getSubject());
		fp.setUser(post.getUser());
		return fp;
	}

	/**
	 * Copy GWTForum to Forum
	 */
	public static Forum copy(GWTForum forum) {
		Forum f = new Forum();
		f.setActive(forum.isActive());
		Calendar cal = Calendar.getInstance();
		cal.setTime(forum.getDate());
		f.setDate(cal);
		f.setDescription(forum.getDescription());
		f.setId(forum.getId());
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(forum.getLastPostDate());
		f.setLastPostDate(cal2);
		f.setLastPostUser(forum.getLastPostUser());
		f.setName(forum.getName());
		f.setNumPosts(forum.getNumPosts());
		f.setNumTopics(forum.getNumTopics());

		for (GWTForumTopic topic : forum.getTopics()) {
			f.getTopics().add(copy(topic));
		}

		return f;
	}

	/**
	 * Copy GWTForumTopic to ForumTopic
	 */
	public static ForumTopic copy(GWTForumTopic topic) {
		ForumTopic ft = new ForumTopic();
		Calendar date = Calendar.getInstance();
		date.setTime(topic.getDate());
		ft.setDate(date);
		ft.setId(topic.getId());
		ft.setReplies(topic.getReplies());
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(topic.getLastPostDate());
		ft.setLastPostDate(lastDate);
		ft.setLastPostUser(topic.getLastPostUser());
		ft.setTitle(topic.getTitle());
		ft.setUser(topic.getUser());
		ft.setNode(topic.getNode());
		ft.setViews(topic.getViews());

		for (Iterator<GWTForumPost> it = topic.getPosts().iterator(); it.hasNext(); ) {
			ft.getPosts().add(copy(it.next()));
		}

		return ft;
	}

	/**
	 * Copy ForumPost to GWTForumPost
	 */
	public static ForumPost copy(GWTForumPost gwtForumPost) {
		ForumPost forumPost = MappingUtils.getMapper().map(gwtForumPost, ForumPost.class);
		return forumPost;
	}

	/**
	 * Copy KeyValue to GWTKeyValue
	 */
	public static GWTKeyValue copy(KeyValue keyValue) {
		GWTKeyValue gwtKeyValue = MappingUtils.getMapper().map(keyValue, GWTKeyValue.class);
		return gwtKeyValue;
	}

	/**
	 * Copy Report to GWTReport
	 */
	public static GWTReport copy(Report report, List<FormElement> formElements) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException, PrincipalAdapterException, IOException, ParseException, NoSuchGroupException {
		GWTReport gWTReport = new GWTReport();
		gWTReport.setActive(report.isActive());
		gWTReport.setFileContent(report.getFileContent());
		gWTReport.setFileMime(report.getFileMime());
		gWTReport.setFileName(report.getFileName());
		gWTReport.setId(report.getId());
		gWTReport.setName(report.getName());
		List<GWTFormElement> gWTFormElemets = new ArrayList<GWTFormElement>();

		for (FormElement formElement : formElements) {
			gWTFormElemets.add(copy(formElement));
		}

		gWTReport.setFormElements(gWTFormElemets);
		return gWTReport;
	}

	/**
	 * Copy WikiPage to GWTWikiPage
	 */
	public static GWTWikiPage copy(WikiPage wikiPage) {
		GWTWikiPage gwtWikiPage = MappingUtils.getMapper().map(wikiPage, GWTWikiPage.class);
		return gwtWikiPage;
	}

	/**
	 * Copy GWTWikiPage to WikiPage
	 */
	public static WikiPage copy(GWTWikiPage gwtWikiPage) {
		WikiPage wikiPage = MappingUtils.getMapper().map(gwtWikiPage, WikiPage.class);
		return wikiPage;
	}

	/**
	 * Copy Omr to GWTOmr
	 */
	public static GWTOmr copy(Omr omr) {
		GWTOmr gWTOmr = new GWTOmr();
		gWTOmr.setId(omr.getId());
		gWTOmr.setName(omr.getName());
		return gWTOmr;
	}
	
	/**
	 * GWTProcessInstanceLogEntry
	 */
	public static GWTProcessInstanceLogEntry copy(ProcessInstanceLogEntry logEntry) {
		GWTProcessInstanceLogEntry gWTLogEntry = new GWTProcessInstanceLogEntry();
		gWTLogEntry.setDate(logEntry.getDate());
		gWTLogEntry.setInfo(logEntry.getInfo());
		gWTLogEntry.setProcessDefinitionId(logEntry.getProcessDefinitionId());
		gWTLogEntry.setProcessDefinitionName(logEntry.getProcessDefinitionName());
		gWTLogEntry.setProcessInstanceId(logEntry.getProcessInstanceId());
		gWTLogEntry.setToken(gWTLogEntry.getToken());
		gWTLogEntry.setType(logEntry.getType());
		return gWTLogEntry;
	}

	/**
	 * GWTAppVersion
	 */
	public static GWTAppVersion copy(AppVersion appVersion) {
		GWTAppVersion gwtAppVersion = MappingUtils.getMapper().map(appVersion, GWTAppVersion.class);
		return gwtAppVersion;
	}

	/**
	 * Copy GWTExtendedAttributes to ExtendedAttributes
	 */
	public static ExtendedAttributes copy(GWTExtendedAttributes attributes) {
		ExtendedAttributes extendedAttributes = new ExtendedAttributes();
		extendedAttributes.setCategories(attributes.isCategories());
		extendedAttributes.setKeywords(attributes.isKeywords());
		extendedAttributes.setNotes(attributes.isNotes());
		extendedAttributes.setPropertyGroups(attributes.isPropertyGroups());
		extendedAttributes.setWiki(attributes.isWiki());
		return extendedAttributes;
	}

	/**
	 * Copy MimeType to GWTMimeType
	 */
	public static GWTMimeType copy(MimeType mt) {
		GWTMimeType gWTmt = new GWTMimeType();
		gWTmt.setName(mt.getName());
		gWTmt.setDescription(mt.getDescription());
		return gWTmt;
	}

	/**
	 * Copy Config to GWTConfig
	 */
	public static GWTConfig copy(com.openkm.dao.bean.Config config) {
		GWTConfig gWTConfig = new GWTConfig();
		gWTConfig.setKey(config.getKey());
		gWTConfig.setType(config.getType());
		gWTConfig.setValue(config.getValue());
		return gWTConfig;
	}

	/**
	 * Obtain extra column FormElement which represents a PropertyGroup.
	 */
	public static GWTFormElement getExtraColumn(String path, GWTFilebrowseExtraColumn extraColumn) throws IOException,
			ParseException, NoSuchGroupException, AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException,
			PrincipalAdapterException {
		long begin = System.currentTimeMillis();

		FormElement formElement = new DbPropertyGroupModule().getProperty(null, path, extraColumn.getGroupName(),
				extraColumn.getFormElement().getName());
		GWTFormElement gWTformElement = GWTUtil.copy(formElement);

		SystemProfiling.log(path + ", " + extraColumn, System.currentTimeMillis() - begin);
		log.trace("getExtraColumn.Time: {}", System.currentTimeMillis() - begin);
		return gWTformElement;
	}

	/**
	 * getExtraColumn
	 */
	public static GWTFilebrowseExtraColumn getExtraColumn(String propertyGroup) throws IOException, ParseException,
			RepositoryException, DatabaseException, AccessDeniedException, PathNotFoundException, PrincipalAdapterException,
			NoSuchGroupException {
		GWTFilebrowseExtraColumn extraColumn = null;

		// Test if has possible property group
		if (propertyGroup != null && !propertyGroup.equals("") && propertyGroup.split("\\.").length >= 2) {
			String grpName = propertyGroup.split("\\.")[0];
			grpName = grpName.replaceFirst("okp", "okg");

			// Test if it's valid group
			boolean found = false;

			for (PropertyGroup pg : OKMPropertyGroup.getInstance().getAllGroups(null)) {
				if (pg.getName().equals(grpName)) {
					found = true;
					break;
				}
			}

			if (found) {
				for (FormElement formElement : OKMPropertyGroup.getInstance().getPropertyGroupForm(null, grpName)) {
					if (formElement.getName().equals(propertyGroup)) {
						extraColumn = new GWTFilebrowseExtraColumn();
						extraColumn.setGroupName(grpName);
						extraColumn.setFormElement(GWTUtil.copy(formElement));
						break;
					}
				}
			}
		}

		return extraColumn;
	}
}
