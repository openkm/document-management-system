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

package com.openkm.servlet.admin;

import com.openkm.bean.ContentInfo;
import com.openkm.bean.PropertyGroup;
import com.openkm.bean.Repository;
import com.openkm.bean.form.FormElement;
import com.openkm.bean.form.Input;
import com.openkm.bean.form.Select;
import com.openkm.core.*;
import com.openkm.core.Config;
import com.openkm.dao.*;
import com.openkm.dao.bean.*;
import com.openkm.extractor.RegisteredExtractors;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.*;
import com.openkm.util.DiffMatchPatch.Diff;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * RepositoryView servlet
 */
public class DbRepositoryViewServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(DbRepositoryViewServlet.class);

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String method = request.getMethod();

		if (checkMultipleInstancesAccess(request, response)) {
			if (method.equals(METHOD_GET)) {
				doGet(request, response);
			} else if (method.equals(METHOD_POST)) {
				doPost(request, response);
			}
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		String path = WebUtils.getString(request, "path");
		String uuid = WebUtils.getString(request, "uuid");
		updateSessionManager(request);

		try {
			if (uuid != null && !uuid.isEmpty()) {
				path = NodeBaseDAO.getInstance().getPathFromUuid(uuid);
			} else if (path != null && !path.isEmpty()) {
				uuid = NodeBaseDAO.getInstance().getUuidFromPath(path);
			}

			if (action.equals("unlock")) {
				unlock(uuid, path, request, response);
			} else if (action.equals("checkin")) {
				// checkin(session, path, request, response);
			} else if (action.equals("remove_content")) {
				removeContent(uuid, path, request, response);
			} else if (action.equals("remove_current")) {
				uuid = removeCurrent(uuid, path, request, response);
				path = NodeBaseDAO.getInstance().getPathFromUuid(uuid);
			} else if (action.equals("remove_pgroup")) {
				// removePropGrup(session, path, request, response);
			} else if (action.equals("edit")) {
				edit(uuid, path, request, response);
			} else if (action.equals("downloadVersion")) {
				downloadVersion(uuid, request, response);
			} else if (action.equals("diff")) {
				diff(uuid, request, response);
			} else if (action.equals("forceTextExtraction")) {
				forceTextExtraction(uuid, path, request, response);
			}

			if (!action.equals("edit") && !action.equals("downloadVersion") && !action.equals("diff")) {
				list(uuid, path, request, response);
			}
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doPost({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		String path = WebUtils.getString(request, "path");
		String uuid = WebUtils.getString(request, "uuid");
		updateSessionManager(request);

		try {
			if (uuid != null && !uuid.isEmpty()) {
				path = NodeBaseDAO.getInstance().getPathFromUuid(uuid);
			} else if (path != null && !path.isEmpty()) {
				uuid = NodeBaseDAO.getInstance().getUuidFromPath(path);
			}

			if ("editPersist".equals(action)) {
				editPersist(uuid, path, request, response);
				list(uuid, path, request, response);
			}
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	/**
	 * Edit node property.
	 */
	private void edit(String uuid, String path, HttpServletRequest request, HttpServletResponse response) throws PathNotFoundException,
			DatabaseException, ServletException, IOException {
		log.debug("edit({}, {})", new Object[]{request, response});
		String property = WebUtils.getString(request, "property");
		String group = WebUtils.getString(request, "group");
		String value = WebUtils.getString(request, "value");
		String field = WebUtils.getString(request, "field");

		ServletContext sc = getServletContext();
		sc.setAttribute("uuid", uuid);
		sc.setAttribute("path", path);
		sc.setAttribute("field", field);
		sc.setAttribute("property", property);
		sc.setAttribute("group", group);
		sc.setAttribute("value", value);
		sc.getRequestDispatcher("/admin/db_repository_edit.jsp").forward(request, response);
		log.debug("edit: void");
	}

	/**
	 * Save property
	 */
	private void editPersist(String uuid, String path, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, PathNotFoundException, RepositoryException, AccessDeniedException, DatabaseException {
		log.debug("editPersist({}, {})", new Object[]{request, response});
		String property = WebUtils.getString(request, "property");
		String group = WebUtils.getString(request, "group");
		String value = WebUtils.getString(request, "value");

		Map<String, String> properties = new HashMap<String, String>();
		properties.put(property, value);
		NodeBaseDAO.getInstance().setProperties(uuid, group, properties);

		// Activity log
		UserActivity.log(PrincipalUtils.getUser(), "ADMIN_REPOSITORY_EDIT", uuid, path, property + ", " + value);
		log.debug("editPersist: void");
	}

	private void list(String uuid, String path, HttpServletRequest request, HttpServletResponse response) throws RepositoryException,
			PathNotFoundException, DatabaseException, ServletException, IOException, ParseException {
		log.debug("list({}, {})", new Object[]{request, response});
		String stats = WebUtils.getString(request, "stats");
		ContentInfo ci = null;

		if (uuid == null || uuid.isEmpty()) {
			path = "/" + Repository.ROOT;
			uuid = NodeBaseDAO.getInstance().getUuidFromPath(path);
		}

		// Respository stats calculation
		if (!stats.equals("")) {
			if (stats.equals("0")) {
				request.getSession().removeAttribute("stats");
			} else {
				request.getSession().setAttribute("stats", true);
			}
		}
		
		/*
		 * if (request.getSession().getAttribute("stats") != null && node.isNodeType(Folder.TYPE)) {
		 * try {
		 * ci = OKMFolder.getInstance().getContentInfo(null, path);
		 * } catch (AccessDeniedException e) {
		 * log.warn(e.getMessage(), e);
		 * } catch (com.openkm.core.RepositoryException e) {
		 * log.warn(e.getMessage(), e);
		 * } catch (PathNotFoundException e) {
		 * log.warn(e.getMessage(), e);
		 * } catch (DatabaseException e) {
		 * log.warn(e.getMessage(), e);
		 * }
		 * }
		 */

		// Activity log
		UserActivity.log(PrincipalUtils.getUser(), "ADMIN_REPOSITORY_LIST", uuid, path, null);
		ServletContext sc = getServletContext();

		if (!Config.ROOT_NODE_UUID.equals(uuid)) {
			NodeBase node = NodeBaseDAO.getInstance().findByPk(uuid);
			sc.setAttribute("node", node);
			sc.setAttribute("nodeType", getNodeType(node));
			sc.setAttribute("properties", getProperties(uuid));
			sc.setAttribute("contentInfo", ci);
			sc.setAttribute("depth", PathUtils.getDepth(path));

			if (node instanceof NodeDocument) {
				sc.setAttribute("locked", ((NodeDocument) node).isLocked());
				sc.setAttribute("history", NodeDocumentVersionDAO.getInstance().findByParent(uuid));
				sc.setAttribute("text", ((NodeDocument) node).getText());
				sc.setAttribute("textExtracted", ((NodeDocument) node).isTextExtracted());
				sc.setAttribute("mimeType", ((NodeDocument) node).getMimeType());
			}
		} else {
			sc.setAttribute("depth", 0);
		}

		sc.setAttribute("breadcrumb", createBreadcrumb(path));
		sc.setAttribute("children", getChildren(uuid));
		sc.getRequestDispatcher("/admin/db_repository_list.jsp").forward(request, response);
		log.debug("list: void");
	}

	/**
	 * Remove current node.
	 */
	private String removeCurrent(String uuid, String path, HttpServletRequest request, HttpServletResponse response)
			throws DatabaseException, PathNotFoundException, AccessDeniedException, LockException, IOException {
		log.debug("removeCurrent({}, {})", uuid, path);
		String parentUuid = NodeBaseDAO.getInstance().getParentUuid(uuid);

		if (NodeFolderDAO.getInstance().isValid(uuid)) {
			NodeFolderDAO.getInstance().purge(uuid, true);
		} else if (NodeDocumentDAO.getInstance().isValid(uuid)) {
			NodeDocumentDAO.getInstance().purge(uuid);
		} else if (NodeMailDAO.getInstance().isValid(uuid)) {
			NodeMailDAO.getInstance().purge(uuid);
		}

		// Activity log
		UserActivity.log(PrincipalUtils.getUser(), "ADMIN_REPOSITORY_REMOVE_CURRENT", uuid, path, null);
		log.debug("removeCurrent: {}", path);
		return parentUuid;
	}

	/**
	 * Remove folder contents.
	 */
	private void removeContent(String uuid, String path, HttpServletRequest request, HttpServletResponse response)
			throws PathNotFoundException, DatabaseException, AccessDeniedException, LockException, IOException {
		log.debug("removeContent({}, {})", uuid, path);
		NodeFolderDAO.getInstance().purge(uuid, false);

		// Activity log
		UserActivity.log(PrincipalUtils.getUser(), "ADMIN_REPOSITORY_REMOVE_CONTENT", uuid, path, null);
		log.debug("removeContent: void");
	}

	/**
	 * Unlock document, or force it.
	 */
	private void unlock(String uuid, String path, HttpServletRequest request, HttpServletResponse response) throws PathNotFoundException,
			AccessDeniedException, DatabaseException, LockException {
		log.debug("unlock({}, {})", uuid, path);
		String user = PrincipalUtils.getUser();
		NodeDocumentDAO.getInstance().unlock(user, uuid, true);

		// Activity log
		UserActivity.log(PrincipalUtils.getUser(), "ADMIN_REPOSITORY_UNLOCK", uuid, path, null);
		log.debug("unlock: void");
	}

	/**
	 * Download version content.
	 */
	private void downloadVersion(String uuid, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException,
			PathNotFoundException, DatabaseException, IOException {
		log.debug("downloadVersion({})", uuid);
		String name = WebUtils.getString(request, "name");
		String fileName = WebUtils.getString(request, "fileName");
		String mimeType = WebUtils.getString(request, "mimeType");
		InputStream is = NodeDocumentVersionDAO.getInstance().getVersionContentByParent(uuid, name);

		try {
			WebUtils.sendFile(request, response, fileName, mimeType, false, is);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * Diff with previous version.
	 */
	private void diff(String uuid, HttpServletRequest request, HttpServletResponse response) throws DatabaseException,
			FileNotFoundException, PathNotFoundException, IOException {
		log.debug("diff({})", uuid);
		String oldUuid = WebUtils.getString(request, "oldUuid");
		String newName = WebUtils.getString(request, "newName");
		String mimeType = WebUtils.getString(request, "mimeType");
		NodeDocumentVersion oldVersion = NodeDocumentVersionDAO.getInstance().findByPk(oldUuid);
		String oldName = oldVersion.getName();
		InputStream oldIs = NodeDocumentVersionDAO.getInstance().getVersionContentByParent(uuid, oldName);
		InputStream newIs = NodeDocumentVersionDAO.getInstance().getVersionContentByParent(uuid, newName);
		String oldText = RegisteredExtractors.getText(mimeType, null, oldIs);
		String newText = RegisteredExtractors.getText(mimeType, null, newIs);
		IOUtils.closeQuietly(oldIs);
		IOUtils.closeQuietly(newIs);
		PrintWriter out = response.getWriter();

		// Calculate differences
		DiffMatchPatch dmp = new DiffMatchPatch();
		LinkedList<Diff> diffs = dmp.diffMain(oldText, newText);
		String diff = dmp.diffPrettyHtml(diffs).replace("&para;", "");

		// Send result
		response.setContentType(MimeTypeConfig.MIME_HTML);
		response.setCharacterEncoding("UTF-8");
		header(out, "Difference between versions", new String[][]{{"utilities.jsp", "Utilities"},
				{"DbRepositoryView?uuid=" + uuid, "Repository view"}});
		out.print(diff);
		footer(out);
	}

	/**
	 * Force text extraction.
	 */
	private void forceTextExtraction(String uuid, String path, HttpServletRequest request, HttpServletResponse response)
			throws DatabaseException, FileNotFoundException, PathNotFoundException, IOException {
		log.debug("forceTextExtraction({}, {})", uuid, path);
		NodeDocumentDAO.getInstance().resetPendingExtractionFlag(uuid);

		// Activity log
		UserActivity.log(PrincipalUtils.getUser(), "ADMIN_REPOSITORY_FORCE_TEXT_EXTRACTION", uuid, path, null);
		log.debug("forceTextExtraction: void");
	}

	/**
	 * Create bread crumb for easy navigation.
	 */
	private String createBreadcrumb(String path) throws UnsupportedEncodingException {
		int idx = path.lastIndexOf('/');
		if (idx > 0) {
			String name = path.substring(idx + 1);
			String parent = path.substring(0, idx);
			return createBreadcrumb(parent) + " / <a href=\"DbRepositoryView?path=" + URLEncoder.encode(path, "UTF-8") + "\">" + name
					+ "</a>";
		} else {
			if (!path.substring(1).equals("")) {
				return "<a href=\"DbRepositoryView?path=/\">ROOT</a> / <a href=\"DbRepositoryView?path=" + URLEncoder.encode(path, "UTF-8")
						+ "\">" + path.substring(1) + "</a>";
			} else {
				return "<a href=\"DbRepositoryView?path=/\">ROOT</a>";
			}
		}
	}

	/**
	 * Get node type.
	 */
	private String getNodeType(NodeBase node) {
		if (node instanceof NodeFolder) {
			return "folder";
		} else if (node instanceof NodeDocument) {
			return "document";
		} else if (node instanceof NodeMail) {
			return "mail";
		} else {
			return "unknown";
		}
	}

	/**
	 * Get children from node.
	 */
	private Collection<Map<String, Object>> getChildren(String uuid) throws RepositoryException, PathNotFoundException, DatabaseException {
		ArrayList<Map<String, Object>> al = new ArrayList<Map<String, Object>>();
		Map<String, Object> hm = new HashMap<String, Object>();

		for (NodeFolder nFld : NodeFolderDAO.getInstance().findByParent(uuid)) {
			hm = new HashMap<String, Object>();
			hm.put("name", nFld.getName());
			hm.put("uuid", nFld.getUuid());
			hm.put("nodeType", getNodeType(nFld));
			al.add(hm);
		}

		for (NodeMail nMail : NodeMailDAO.getInstance().findByParent(uuid)) {
			hm = new HashMap<String, Object>();
			hm.put("name", nMail.getName());
			hm.put("uuid", nMail.getUuid());
			hm.put("nodeType", getNodeType(nMail));
			al.add(hm);
		}

		for (NodeDocument nDoc : NodeDocumentDAO.getInstance().findByParent(uuid)) {
			hm = new HashMap<String, Object>();
			hm.put("name", nDoc.getName());
			hm.put("uuid", nDoc.getUuid());
			hm.put("nodeType", getNodeType(nDoc));
			hm.put("locked", nDoc.isLocked());
			hm.put("checkedOut", nDoc.isCheckedOut());
			// hm.put("isDocumentContent", child.isNodeType(Document.CONTENT_TYPE));
			al.add(hm);
		}

		Collections.sort(al, new ChildCmp());
		return al;
	}

	/**
	 * Make child node comparable.
	 */
	private class ChildCmp implements Comparator<Map<String, Object>> {
		@Override
		public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
			return ((String) arg0.get("name")).compareTo((String) arg1.get("name"));
		}
	}

	/**
	 * Get properties from node
	 */
	private Collection<HashMap<String, String>> getProperties(String uuid) throws RepositoryException, PathNotFoundException,
			DatabaseException, IOException, ParseException {
		Map<PropertyGroup, List<FormElement>> pgfs = FormUtils.parsePropertyGroupsForms(Config.PROPERTY_GROUPS_XML);
		ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();

		for (String grpName : NodeBaseDAO.getInstance().getPropertyGroups(uuid)) {
			List<FormElement> pgf = FormUtils.getPropertyGroupForms(pgfs, grpName);

			if (pgf != null) {
				Map<String, String> properties = NodeBaseDAO.getInstance().getProperties(uuid, grpName);

				for (FormElement fe : pgf) {
					String value = properties.get(fe.getName());

					if (value != null) {
						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put("group", grpName);
						hm.put("name", fe.getName());
						hm.put("field", fe.getClass().getSimpleName());
						hm.put("type", getPropertyType(fe));
						hm.put("value", value);
						al.add(hm);
					}
				}
			}
		}

		Collections.sort(al, new PropertyCmp());
		return al;
	}

	/**
	 * Get property type.
	 */
	private String getPropertyType(FormElement fe) {
		if (fe instanceof Input) {
			return ((Input) fe).getType();
		} else if (fe instanceof Select) {
			return ((Select) fe).getType();
		} else {
			return null;
		}
	}

	/**
	 * Make properties comparable
	 */
	private class PropertyCmp implements Comparator<HashMap<String, String>> {
		@Override
		public int compare(HashMap<String, String> arg0, HashMap<String, String> arg1) {
			return arg0.get("name").compareTo(arg1.get("name"));
		}
	}
}
