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

import com.google.gson.Gson;
import com.openkm.api.OKMRepository;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.AutomationDAO;
import com.openkm.dao.bean.AutomationAction;
import com.openkm.dao.bean.AutomationMetadata;
import com.openkm.dao.bean.AutomationRule;
import com.openkm.dao.bean.AutomationValidation;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Automation servlet
 */
public class AutomationServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(AutomationServlet.class);
	private static String ats[] = {AutomationRule.AT_PRE, AutomationRule.AT_POST};
	private static final Map<String, String> events = new LinkedHashMap<String, String>() {
		private static final long serialVersionUID = 1L;

		{
			put(AutomationRule.EVENT_DOCUMENT_CREATE, "Document creation");
			put(AutomationRule.EVENT_DOCUMENT_UPDATE, "Document update");
			put(AutomationRule.EVENT_DOCUMENT_DELETE, "Document delete");
			put(AutomationRule.EVENT_DOCUMENT_RENAME, "Document rename");
			put(AutomationRule.EVENT_DOCUMENT_MOVE, "Document move");
			put(AutomationRule.EVENT_FOLDER_CREATE, "Folder creation");
			put(AutomationRule.EVENT_MAIL_CREATE, "Mail creation");
			put(AutomationRule.EVENT_PROPERTY_GROUP_ADD, "Add property group");
			put(AutomationRule.EVENT_PROPERTY_GROUP_SET, "Set property group");
			put(AutomationRule.EVENT_PROPERTY_GROUP_REMOVE, "Remove property group");
			put(AutomationRule.EVENT_TEXT_EXTRACTOR, "Text extraction");
			put(AutomationRule.EVENT_CONVERSION_PDF, "Convert to PDF");
			put(AutomationRule.EVENT_CONVERSION_SWF, "Convert to SWF");
		}
	};

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		String userId = request.getRemoteUser();
		updateSessionManager(request);

		try {
			if (action.equals("ruleList")) {
				ruleList(userId, request, response);
			} else if (action.equals("definitionList")) {
				definitionList(userId, request, response);
			} else if (action.equals("getMetadata")) {
				getMetadata(userId, request, response);
			} else if (action.equals("create")) {
				create(userId, request, response);
			} else if (action.equals("edit")) {
				edit(userId, request, response);
			} else if (action.equals("delete")) {
				delete(userId, request, response);
			} else if (action.equals("loadMetadataForm")) {
				loadMetadataForm(userId, request, response);
			} else if (action.equals("createAction")) {
				createAction(userId, request, response);
			} else if (action.equals("deleteAction")) {
				deleteAction(userId, request, response);
			} else if (action.equals("editAction")) {
				editAction(userId, request, response);
			} else if (action.equals("createValidation")) {
				createValidation(userId, request, response);
			} else if (action.equals("deleteValidation")) {
				deleteValidation(userId, request, response);
			} else if (action.equals("editValidation")) {
				editValidation(userId, request, response);
			}

			if (action.equals("") || WebUtils.getBoolean(request, "persist")) {
				ruleList(userId, request, response);
			} else if (action.equals("createAction") || action.equals("createValidation") || action.equals("deleteAction")
					|| action.equals("editAction") || action.equals("deleteValidation") || action.equals("editValidation")) {
				definitionList(userId, request, response);
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	/**
	 * List rules
	 */
	private void ruleList(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, DatabaseException {
		log.debug("ruleList({}, {}, {})", new Object[]{userId, request, response});
		ServletContext sc = getServletContext();
		sc.setAttribute("automationRules", AutomationDAO.getInstance().findAll());
		sc.setAttribute("events", events);
		sc.getRequestDispatcher("/admin/automation_rule_list.jsp").forward(request, response);

		// Activity log
		UserActivity.log(userId, "ADMIN_AUTOMATION_LIST", null, null, null);
		log.debug("ruleList: void");
	}

	/**
	 * List rules
	 */
	private void definitionList(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException, AccessDeniedException, PathNotFoundException,
			RepositoryException {
		log.debug("definitionList({}, {}, {})", new Object[]{userId, request, response});
		ServletContext sc = getServletContext();
		long arId = WebUtils.getLong(request, "ar_id");
		AutomationRule aRule = AutomationDAO.getInstance().findByPk(arId);

		for (AutomationValidation av : aRule.getValidations()) {
			for (int i = 0; i < av.getParams().size(); i++) {
				av.getParams().set(i, convertToHumanValue(av.getParams().get(i), av.getType(), i));
			}
		}

		for (AutomationAction aa : aRule.getActions()) {
			for (int i = 0; i < aa.getParams().size(); i++) {
				aa.getParams().set(i, convertToHumanValue(aa.getParams().get(i), aa.getType(), i));
			}
		}

		sc.setAttribute("ar", aRule);
		sc.setAttribute("metadaActions", AutomationDAO.getInstance().findMetadataActionsByAt(aRule.getAt()));
		sc.setAttribute("metadaValidations", AutomationDAO.getInstance().findMetadataValidationsByAt(aRule.getAt()));
		sc.getRequestDispatcher("/admin/automation_definition_list.jsp").forward(request, response);

		// Activity log
		UserActivity.log(userId, "ADMIN_AUTOMATION_GET_DEFINITION_LIST", null, null, null);
		log.debug("definitionList: void");
	}

	/**
	 * getMetadataAction
	 */
	private void getMetadata(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, DatabaseException {
		long amId = WebUtils.getLong(request, "amId");
		Gson son = new Gson();
		AutomationMetadata am = AutomationDAO.getInstance().findMetadataByPk(amId);
		String json = son.toJson(am);
		PrintWriter writer = response.getWriter();
		writer.print(json);
		writer.flush();

		// Activity log
		UserActivity.log(userId, "ADMIN_AUTOMATION_GET_METADATA", Long.toString(amId), null, am.getName());
	}

	/**
	 * New automation
	 */
	private void create(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, DatabaseException {
		log.debug("create({}, {}, {})", new Object[]{userId, request, response});

		if (WebUtils.getBoolean(request, "persist")) {
			AutomationRule ar = new AutomationRule();
			ar.setName(WebUtils.getString(request, "ar_name"));
			ar.setOrder(WebUtils.getInt(request, "ar_order"));
			ar.setExclusive(WebUtils.getBoolean(request, "ar_exclusive"));
			ar.setActive(WebUtils.getBoolean(request, "ar_active"));
			ar.setAt(WebUtils.getString(request, "ar_at"));
			ar.setEvent(WebUtils.getString(request, "ar_event"));
			AutomationDAO.getInstance().create(ar);

			// Activity log
			UserActivity.log(userId, "ADMIN_AUTOMATION_CREATE", Long.toString(ar.getId()), null, ar.toString());
		} else {
			ServletContext sc = getServletContext();
			AutomationRule ar = new AutomationRule();
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("ar", ar);
			sc.setAttribute("ats", ats);
			sc.setAttribute("events", events);
			sc.getRequestDispatcher("/admin/automation_rule_edit.jsp").forward(request, response);
		}

		log.debug("create: void");
	}

	/**
	 * New metadata action
	 * @throws RepositoryException
	 * @throws PathNotFoundException
	 */
	private void createAction(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, DatabaseException, AccessDeniedException, PathNotFoundException, RepositoryException {
		long arId = WebUtils.getLong(request, "ar_id");
		AutomationAction aa = new AutomationAction();
		aa.setType(WebUtils.getLong(request, "am_id"));
		aa.setOrder(WebUtils.getInt(request, "am_order"));
		aa.setActive(WebUtils.getBoolean(request, "am_active"));
		List<String> params = new ArrayList<String>();
		String am_param00 = WebUtils.getString(request, "am_param00");
		String am_param01 = WebUtils.getString(request, "am_param01");

		if (!am_param00.equals("")) {
			am_param00 = convertToInternalValue(am_param00, aa.getType(), 0);
		}

		if (!am_param01.equals("")) {
			am_param01 = convertToInternalValue(am_param01, aa.getType(), 1);
		}

		params.add(am_param00);
		params.add(am_param01);
		aa.setParams(params);
		AutomationDAO.getInstance().createAction(aa);
		AutomationRule ar = AutomationDAO.getInstance().findByPk(arId);
		ar.getActions().add(aa);
		AutomationDAO.getInstance().update(ar);

		// Activity log
		UserActivity.log(userId, "ADMIN_AUTOMATION_ADD_ACTION", Long.toString(ar.getId()), null, ar.toString());
	}

	/**
	 * Delete action
	 */
	private void deleteAction(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, DatabaseException {
		long aaId = WebUtils.getLong(request, "aa_id");
		long arId = WebUtils.getLong(request, "ar_id");
		AutomationRule ar = AutomationDAO.getInstance().findByPk(arId);

		for (AutomationAction action : ar.getActions()) {
			if (action.getId() == aaId) {
				ar.getActions().remove(action);
				break;
			}
		}

		AutomationDAO.getInstance().update(ar);
		AutomationDAO.getInstance().deleteAction(aaId);

		// Activity log
		UserActivity.log(userId, "ADMIN_AUTOMATION_DELETE_ACTION", Long.toString(ar.getId()), null, ar.toString());
	}

	/**
	 * Edit action
	 * @throws RepositoryException
	 * @throws PathNotFoundException
	 */
	private void editAction(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, DatabaseException, AccessDeniedException, PathNotFoundException, RepositoryException {
		long aaId = WebUtils.getLong(request, "aa_id");
		AutomationAction aa = AutomationDAO.getInstance().findActionByPk(aaId);
		aa.setOrder(WebUtils.getInt(request, "am_order"));
		aa.setActive(WebUtils.getBoolean(request, "am_active"));
		List<String> params = new ArrayList<String>();
		String am_param00 = WebUtils.getString(request, "am_param00");
		String am_param01 = WebUtils.getString(request, "am_param01");

		if (!am_param00.equals("")) {
			am_param00 = convertToInternalValue(am_param00, aa.getType(), 0);
		}
		if (!am_param01.equals("")) {
			am_param01 = convertToInternalValue(am_param01, aa.getType(), 1);
		}
		params.add(am_param00);
		params.add(am_param01);
		aa.setParams(params);
		AutomationDAO.getInstance().updateAction(aa);

		// Activity log
		UserActivity.log(userId, "ADMIN_AUTOMATION_	EDIT_ACTION", Long.toString(aa.getId()), null, aa.toString());
	}

	/**
	 * Edit validation
	 */
	private void editValidation(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException, AccessDeniedException, PathNotFoundException, RepositoryException {
		long avId = WebUtils.getLong(request, "av_id");
		AutomationValidation av = AutomationDAO.getInstance().findValidationByPk(avId);
		av.setOrder(WebUtils.getInt(request, "am_order"));
		av.setActive(WebUtils.getBoolean(request, "am_active"));
		List<String> params = new ArrayList<String>();
		String am_param00 = WebUtils.getString(request, "am_param00");
		String am_param01 = WebUtils.getString(request, "am_param01");

		if (!am_param00.equals("")) {
			am_param00 = convertToInternalValue(am_param00, av.getType(), 0);
		}
		if (!am_param01.equals("")) {
			am_param01 = convertToInternalValue(am_param01, av.getType(), 1);
		}

		params.add(am_param00);
		params.add(am_param01);
		av.setParams(params);
		AutomationDAO.getInstance().updateValidation(av);

		// Activity log
		UserActivity.log(userId, "ADMIN_AUTOMATION_	EDIT_VALIDATION", Long.toString(av.getId()), null, av.toString());
	}

	/**
	 * New metadata validation
	 */
	private void createValidation(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException, AccessDeniedException, PathNotFoundException, RepositoryException {
		long arId = WebUtils.getLong(request, "ar_id");
		AutomationValidation av = new AutomationValidation();
		av.setType(WebUtils.getLong(request, "am_id"));
		av.setOrder(WebUtils.getInt(request, "am_order"));
		av.setActive(WebUtils.getBoolean(request, "am_active"));
		List<String> params = new ArrayList<String>();
		String am_param00 = WebUtils.getString(request, "am_param00");
		String am_param01 = WebUtils.getString(request, "am_param01");

		if (!am_param00.equals("")) {
			am_param00 = convertToInternalValue(am_param00, av.getType(), 0);
		}
		if (!am_param01.equals("")) {
			am_param01 = convertToInternalValue(am_param01, av.getType(), 1);
		}

		params.add(am_param00);
		params.add(am_param01);
		av.setParams(params);
		AutomationDAO.getInstance().createValidation(av);
		AutomationRule ar = AutomationDAO.getInstance().findByPk(arId);
		ar.getValidations().add(av);
		AutomationDAO.getInstance().update(ar);

		// Activity log
		UserActivity.log(userId, "ADMIN_AUTOMATION_ADD_VALIDATION", Long.toString(ar.getId()), null, ar.toString());
	}

	/**
	 * Delete validation
	 */
	private void deleteValidation(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException {
		long avId = WebUtils.getLong(request, "av_id");
		long arId = WebUtils.getLong(request, "ar_id");
		AutomationRule ar = AutomationDAO.getInstance().findByPk(arId);

		for (AutomationValidation validation : ar.getValidations()) {
			if (validation.getId() == avId) {
				ar.getValidations().remove(validation);
				break;
			}
		}

		AutomationDAO.getInstance().update(ar);
		AutomationDAO.getInstance().deleteValidation(avId);

		// Activity log
		UserActivity.log(userId, "ADMIN_AUTOMATION_DELETE_VALIDATION", Long.toString(ar.getId()), null, ar.toString());
	}

	/**
	 * Edit automation
	 */
	private void edit(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, DatabaseException {
		log.debug("edit({}, {}, {})", new Object[]{userId, request, response});

		if (WebUtils.getBoolean(request, "persist")) {
			long arId = WebUtils.getLong(request, "ar_id");
			AutomationRule ar = AutomationDAO.getInstance().findByPk(arId);
			ar.setName(WebUtils.getString(request, "ar_name"));
			ar.setOrder(WebUtils.getInt(request, "ar_order"));
			ar.setExclusive(WebUtils.getBoolean(request, "ar_exclusive"));
			ar.setActive(WebUtils.getBoolean(request, "ar_active"));
			ar.setEvent(WebUtils.getString(request, "ar_event"));
			AutomationDAO.getInstance().update(ar);

			// Activity log
			UserActivity.log(userId, "ADMIN_AUTOMATION_EDIT", Long.toString(ar.getId()), null, ar.toString());
		} else {
			ServletContext sc = getServletContext();
			long arId = WebUtils.getLong(request, "ar_id");
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("ar", AutomationDAO.getInstance().findByPk(arId));
			sc.setAttribute("ats", ats);
			sc.setAttribute("events", events);
			sc.getRequestDispatcher("/admin/automation_rule_edit.jsp").forward(request, response);
		}

		log.debug("edit: void");
	}

	/**
	 * Load Metadata form
	 */
	private void loadMetadataForm(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException, AccessDeniedException, PathNotFoundException, RepositoryException {
		ServletContext sc = getServletContext();
		String action = WebUtils.getString(request, "newAction");
		sc.setAttribute("action", action);
		sc.setAttribute("ar_id", WebUtils.getString(request, "ar_id"));
		long amId = WebUtils.getLong(request, "am_id");
		sc.setAttribute("am_id", amId);

		if (action.equals("createAction") || action.equals("createValidation")) {
			sc.setAttribute("am", AutomationDAO.getInstance().findMetadataByPk(amId));
			sc.setAttribute("aa_id", "");
			sc.setAttribute("av_id", "");
			sc.setAttribute("am_order", "0");
			sc.setAttribute("am_param00", "");
			sc.setAttribute("am_param01", "");
		} else if (action.equals("deleteAction") || action.equals("editAction")) {
			long aaId = WebUtils.getLong(request, "aa_id");
			sc.setAttribute("aa_id", aaId);
			sc.setAttribute("av_id", "");
			AutomationMetadata am = AutomationDAO.getInstance().findMetadataByPk(amId);
			AutomationAction aa = AutomationDAO.getInstance().findActionByPk(aaId);

			for (int i = 0; i < aa.getParams().size(); i++) {
				switch (i) {
					case 0:
						if (aa.getParams().get(0) != null && !aa.getParams().get(0).equals("")) {
							sc.setAttribute("am_param00", convertToHumanValue(aa.getParams().get(0), aa.getType(), 0));
						} else {
							sc.setAttribute("am_param00", "");
						}
						break;
					case 1:
						if (aa.getParams().get(1) != null && !aa.getParams().get(1).equals("")) {
							sc.setAttribute("am_param01", convertToHumanValue(aa.getParams().get(1), aa.getType(), 1));
						} else {
							sc.setAttribute("am_param01", "");
						}
						break;
				}
			}

			sc.setAttribute("am_order", String.valueOf(aa.getOrder()));
			am.setActive(aa.isActive());
			sc.setAttribute("am", am);
		} else if (action.equals("deleteValidation") || action.equals("editValidation")) {
			long avId = WebUtils.getLong(request, "av_id");
			sc.setAttribute("aa_id", "");
			sc.setAttribute("av_id", avId);
			AutomationMetadata am = AutomationDAO.getInstance().findMetadataByPk(amId);
			AutomationValidation av = AutomationDAO.getInstance().findValidationByPk(avId);

			for (int i = 0; i < av.getParams().size(); i++) {
				switch (i) {
					case 0:
						if (av.getParams().get(0) != null && !av.getParams().get(0).equals("")) {
							sc.setAttribute("am_param00", convertToHumanValue(av.getParams().get(0), av.getType(), 0));
						} else {
							sc.setAttribute("am_param00", "");
						}
						break;
					case 1:
						if (av.getParams().get(1) != null && !av.getParams().get(1).equals("")) {
							sc.setAttribute("am_param01", convertToHumanValue(av.getParams().get(1), av.getType(), 1));
						} else {
							sc.setAttribute("am_param01", "");
						}
						break;
				}
			}

			sc.setAttribute("am_order", String.valueOf(av.getOrder()));
			am.setActive(av.isActive());
			sc.setAttribute("am", am);
		}

		sc.getRequestDispatcher("/admin/automation_definition_form.jsp").forward(request, response);
	}

	/**
	 * Delete automation
	 */
	private void delete(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, DatabaseException {
		log.debug("delete({}, {}, {})", new Object[]{userId, request, response});

		if (WebUtils.getBoolean(request, "persist")) {
			long arId = WebUtils.getLong(request, "ar_id");
			AutomationDAO.getInstance().delete(arId);

			// Activity log
			UserActivity.log(userId, "ADMIN_AUTOMATION_DELETE", Long.toString(arId), null, null);
		} else {
			ServletContext sc = getServletContext();
			long arId = WebUtils.getLong(request, "ar_id");
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("ar", AutomationDAO.getInstance().findByPk(arId));
			sc.setAttribute("ats", ats);
			sc.setAttribute("events", events);
			sc.getRequestDispatcher("/admin/automation_rule_edit.jsp").forward(request, response);
		}

		log.debug("edit: void");
	}

	/**
	 * convertToInternalValue
	 */
	private String convertToInternalValue(String value, long amId, int param) throws DatabaseException, AccessDeniedException,
			PathNotFoundException, RepositoryException {
		AutomationMetadata am = AutomationDAO.getInstance().findMetadataByPk(amId);

		// Convert folder path to UUID
		if (value != null && !value.equals("")) {
			switch (param) {
				case 0:
					if (AutomationMetadata.SOURCE_FOLDER.equals(am.getSource00())) {
						value = OKMRepository.getInstance().getNodeUuid(null, value);
					}
				case 1:
					if (AutomationMetadata.SOURCE_FOLDER.equals(am.getSource01())) {
						value = OKMRepository.getInstance().getNodeUuid(null, value);
					}
			}
		}

		return value;
	}

	/**
	 * convertToHumanValue
	 */
	private String convertToHumanValue(String value, long amId, int param) throws DatabaseException, AccessDeniedException,
			PathNotFoundException, RepositoryException {
		AutomationMetadata am = AutomationDAO.getInstance().findMetadataByPk(amId);

		// Convert folder path to UUID
		if (value != null && !value.equals("")) {
			switch (param) {
				case 0:
					if (AutomationMetadata.SOURCE_FOLDER.equals(am.getSource00())) {
						value = OKMRepository.getInstance().getNodePath(null, value);
					}
				case 1:
					if (AutomationMetadata.SOURCE_FOLDER.equals(am.getSource01())) {
						value = OKMRepository.getInstance().getNodePath(null, value);
					}
			}
		}

		return value;
	}
}
