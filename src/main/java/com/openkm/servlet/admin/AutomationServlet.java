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

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.openkm.api.OKMRepository;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.AutomationDAO;
import com.openkm.dao.OmrDAO;
import com.openkm.dao.bean.Automation;
import com.openkm.dao.bean.AutomationAction;
import com.openkm.dao.bean.AutomationRule;
import com.openkm.dao.bean.AutomationValidation;
import com.openkm.dao.bean.Omr;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;

/**
 * Automation servlet
 */
public class AutomationServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(AutomationServlet.class);
	private static String ats[] = { AutomationRule.AT_PRE, AutomationRule.AT_POST };

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		String action = WebUtils.getString(request, "action");
		updateSessionManager(request);

		try {

			if (action.equals("ruleList")) {
				ruleList(request, response);
			} else if (action.equals("definitionList") || action.equals("viewDefinition")) {
				definitionList(request, response);
			} else if (action.equals("getMetadata")) {
				getMetadata(request, response);
			} else if (action.equals("create")) {
				create(request, response);
			} else if (action.equals("edit")) {
				edit(request, response);
			} else if (action.equals("delete")) {
				delete(request, response);
			} else if (action.equals("loadMetadataForm")) {
				loadMetadataForm(request, response);
			} else if (action.equals("registeredList")) {
				registeredList(request, response, false);
			} else if (action.equals("reloadRegisteredList")) {
				registeredList(request, response, true);
			} else {
				ruleList(request, response);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doPost({}, {})", request, response);
		String action = WebUtils.getString(request, "action");
		updateSessionManager(request);

		try {
			if (action.equals("create")) {
				create(request, response);
			} else if (action.equals("edit")) {
				edit(request, response);
			} else if (action.equals("delete")) {
				delete(request, response);
			} else if (action.equals("createAction")) {
				createAction(request, response);
			} else if (action.equals("deleteAction")) {
				deleteAction(request, response);
			} else if (action.equals("editAction")) {
				editAction(request, response);
			} else if (action.equals("createValidation")) {
				createValidation(request, response);
			} else if (action.equals("deleteValidation")) {
				deleteValidation(request, response);
			} else if (action.equals("editValidation")) {
				editValidation(request, response);
			}

			// Go to list
			if (action.endsWith("Action") || action.endsWith("Validation")) {
				response.sendRedirect(request.getContextPath() + request.getServletPath()
						+ "?action=definitionList&ar_id=" + WebUtils.getLong(request, "ar_id"));
			} else {
				response.sendRedirect(request.getContextPath() + request.getServletPath());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	/**
	 * List rules
	 */
	private void ruleList(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException {
		log.debug("ruleList({}, {})", new Object[] { request, response });
		ServletContext sc = getServletContext();
		sc.setAttribute("automationRules", AutomationDAO.getInstance().findAll());
		sc.setAttribute("events", AutomationRule.EVENTS);
		sc.getRequestDispatcher("/admin/automation_rule_list.jsp").forward(request, response);

		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_AUTOMATION_LIST", null, null, null);
		log.debug("ruleList: void");
	}

	/**
	 * List rules
	 */
	private void definitionList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DatabaseException,
			AccessDeniedException, RepositoryException, IllegalArgumentException, SecurityException, URISyntaxException,
			ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		log.debug("definitionList({}, {})", new Object[] { request, response });
		ServletContext sc = getServletContext();

		try {
			long arId = WebUtils.getLong(request, "ar_id");
			AutomationRule aRule = AutomationDAO.getInstance().findByPk(arId);
			
			for (AutomationValidation av : aRule.getValidations()) {
				for (int i = 0; i < av.getParams().size(); i++) {
					av.getParams().set(i, convertToHumanValue(av.getParams().get(i), av.getClassName(), i));
				}
			}

			for (AutomationAction aa : aRule.getActions()) {
				for (int i = 0; i < aa.getParams().size(); i++) {
					aa.getParams().set(i, convertToHumanValue(aa.getParams().get(i), aa.getClassName(), i));
				}
			}

			sc.setAttribute("ar", aRule);
			sc.setAttribute("events", AutomationRule.EVENTS);
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("metadaActions", AutomationDAO.getInstance().findMetadataActionsByAt(aRule.getAt()));
			sc.setAttribute("metadaValidations", AutomationDAO.getInstance().findMetadataValidationsByAt(aRule.getAt()));
		} catch (PathNotFoundException e) {
			request.setAttribute("pathNotFound", Boolean.TRUE);
		}

		sc.getRequestDispatcher("/admin/automation_definition_list.jsp").forward(request, response);

		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_AUTOMATION_GET_DEFINITION_LIST", null, null, null);
		log.debug("definitionList: void");
	}

	/**
	 * registeredList
	 */
	private void registeredList(HttpServletRequest request, HttpServletResponse response, boolean reload) throws ServletException, IOException,
			DatabaseException, PathNotFoundException, RepositoryException, IllegalArgumentException, SecurityException, URISyntaxException,
			ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		log.debug("registeredList({}, {})", new Object[]{request, response});
		ServletContext sc = getServletContext();
		sc.setAttribute("actions", AutomationDAO.getInstance().findActions(reload));
		sc.setAttribute("validations", AutomationDAO.getInstance().findValidations(reload));
		sc.getRequestDispatcher("/admin/automation_registered_list.jsp").forward(request, response);
		log.debug("registeredList: void");
	}
	
	/**
	 * getMetadataAction
	 */
	private void getMetadata(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException, IllegalArgumentException, SecurityException,
			URISyntaxException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
			InstantiationException, IllegalAccessException {
		String className = WebUtils.getString(request, "am_className");
		Gson son = new Gson();
		Automation am = AutomationDAO.getInstance().findMetadataByPk(className);
		String json = son.toJson(am);
		PrintWriter writer = response.getWriter();
		writer.print(json);
		writer.flush();
	}

	/**
	 * New automation
	 */
	private void create(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DatabaseException {
		log.debug("create({}, {})", new Object[]{request, response});

		if (isPost(request)) {
			AutomationRule ar = new AutomationRule();
			ar.setName(WebUtils.getString(request, "ar_name"));
			ar.setOrder(WebUtils.getInt(request, "ar_order"));
			ar.setExclusive(WebUtils.getBoolean(request, "ar_exclusive"));
			ar.setActive(WebUtils.getBoolean(request, "ar_active"));
			ar.setAt(WebUtils.getString(request, "ar_at"));
			ar.setEvent(WebUtils.getString(request, "ar_event"));
			AutomationDAO.getInstance().create(ar);

			// Activity log
			UserActivity.log(request.getRemoteUser(), "ADMIN_AUTOMATION_CREATE", Long.toString(ar.getId()), null, ar.toString());
		} else {
			ServletContext sc = getServletContext();
			AutomationRule ar = new AutomationRule();
			sc.setAttribute("ar", ar);
			sc.setAttribute("ats", ats);
			sc.setAttribute("events", AutomationRule.EVENTS);
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.getRequestDispatcher("/admin/automation_rule_edit.jsp").forward(request, response);
		}

		log.debug("create: void");
	}


	/**
	 * New metadata action
	 */
	private void createAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DatabaseException,
			AccessDeniedException, PathNotFoundException, RepositoryException, IllegalArgumentException, SecurityException, URISyntaxException,
			ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		long arId = WebUtils.getLong(request, "ar_id");
		AutomationAction aa = new AutomationAction();
		aa.setClassName(WebUtils.getString(request, "am_className"));
		aa.setOrder(WebUtils.getInt(request, "am_order"));
		aa.setActive(WebUtils.getBoolean(request, "am_active"));
		List<String> params = new ArrayList<String>();
		String am_param00;
		String am_param01;
		String am_param02;

		Automation am = AutomationDAO.getInstance().findMetadataByPk(aa.getClassName());
		if (Automation.PARAM_TYPE_USER.equals(am.getType00()) || Automation.PARAM_TYPE_ROLE.equals(am.getType00())) {
			am_param00 = WebUtils.getStringComaSeparedValues(request, "am_param00");
		} else {
			am_param00 = WebUtils.getString(request, "am_param00");
		}
		if (Automation.PARAM_TYPE_USER.equals(am.getType01()) || Automation.PARAM_TYPE_ROLE.equals(am.getType01())) {
			am_param01 = WebUtils.getStringComaSeparedValues(request, "am_param01");
		} else {
			am_param01 = WebUtils.getString(request, "am_param01");
		}
		if (Automation.PARAM_TYPE_USER.equals(am.getType02()) || Automation.PARAM_TYPE_ROLE.equals(am.getType02())) {
			am_param02 = WebUtils.getStringComaSeparedValues(request, "am_param02");
		} else {
			am_param02 = WebUtils.getString(request, "am_param02");
		}

		if (!am_param00.equals("")) {
			am_param00 = convertToInternalValue(am_param00, aa.getClassName(), 0);
		}
		if (!am_param01.equals("")) {
			am_param01 = convertToInternalValue(am_param01, aa.getClassName(), 1);
		}
		if (!am_param02.equals("")) {
			am_param02 = convertToInternalValue(am_param02, aa.getClassName(), 2);
		}

		params.add(am_param00);
		params.add(am_param01);
		params.add(am_param02);
		aa.setParams(params);
		AutomationDAO.getInstance().createAction(aa);
		AutomationRule ar = AutomationDAO.getInstance().findByPk(arId);
		ar.getActions().add(aa);
		AutomationDAO.getInstance().update(ar);
		
		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_AUTOMATION_ADD_ACTION", Long.toString(ar.getId()), null, ar.toString());
	}

	/**
	 * Delete action
	 */
	private void deleteAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DatabaseException {
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
		UserActivity.log(request.getRemoteUser(), "ADMIN_AUTOMATION_DELETE_ACTION", Long.toString(ar.getId()), null, ar.toString());
	}

	/**
	 * Edit action
	 */
	private void editAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DatabaseException,
			AccessDeniedException, PathNotFoundException, RepositoryException, IllegalArgumentException, SecurityException, URISyntaxException,
			ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		long aaId = WebUtils.getLong(request, "aa_id");
		AutomationAction aa = AutomationDAO.getInstance().findActionByPk(aaId);
		aa.setOrder(WebUtils.getInt(request, "am_order"));
		aa.setActive(WebUtils.getBoolean(request, "am_active"));
		List<String> params = new ArrayList<String>();
		String am_param00;
		String am_param01;
		String am_param02;

		Automation am = AutomationDAO.getInstance().findMetadataByPk(aa.getClassName());
		if (Automation.PARAM_TYPE_USER.equals(am.getType00()) || Automation.PARAM_TYPE_ROLE.equals(am.getType00())) {
			am_param00 = WebUtils.getStringComaSeparedValues(request, "am_param00");
		} else {
			am_param00 = WebUtils.getString(request, "am_param00");
		}
		if (Automation.PARAM_TYPE_USER.equals(am.getType01()) || Automation.PARAM_TYPE_ROLE.equals(am.getType01())) {
			am_param01 = WebUtils.getStringComaSeparedValues(request, "am_param01");
		} else {
			am_param01 = WebUtils.getString(request, "am_param01");
		}
		if (Automation.PARAM_TYPE_USER.equals(am.getType02()) || Automation.PARAM_TYPE_ROLE.equals(am.getType02())) {
			am_param02 = WebUtils.getStringComaSeparedValues(request, "am_param02");
		} else {
			am_param02 = WebUtils.getString(request, "am_param02");
		}

		if (!am_param00.equals("")) {
			am_param00 = convertToInternalValue(am_param00, aa.getClassName(), 0);
		}
		if (!am_param01.equals("")) {
			am_param01 = convertToInternalValue(am_param01, aa.getClassName(), 1);
		}
		if (!am_param02.equals("")) {
			am_param02 = convertToInternalValue(am_param02, aa.getClassName(), 2);
		}

		params.add(am_param00);
		params.add(am_param01);
		params.add(am_param02);
		aa.setParams(params);
		AutomationDAO.getInstance().updateAction(aa);

		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_AUTOMATION_EDIT_ACTION", Long.toString(aa.getId()), null, aa.toString());
	}

	/**
	 * Edit validation
	 */
	private void editValidation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException,
			DatabaseException, AccessDeniedException, PathNotFoundException, RepositoryException, IllegalArgumentException, SecurityException,
			URISyntaxException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException,
			IllegalAccessException {
		long avId = WebUtils.getLong(request, "av_id");
		AutomationValidation av = AutomationDAO.getInstance().findValidationByPk(avId);
		av.setOrder(WebUtils.getInt(request, "am_order"));
		av.setActive(WebUtils.getBoolean(request, "am_active"));
		List<String> params = new ArrayList<String>();
		String am_param00 = WebUtils.getString(request, "am_param00");
		String am_param01 = WebUtils.getString(request, "am_param01");
		String am_param02 = WebUtils.getString(request, "am_param02");

		if (!am_param00.equals("")) {
			am_param00 = convertToInternalValue(am_param00, av.getClassName(), 0);
		}
		if (!am_param01.equals("")) {
			am_param01 = convertToInternalValue(am_param01, av.getClassName(), 1);
		}
		if (!am_param02.equals("")) {
			am_param02 = convertToInternalValue(am_param02, av.getClassName(), 2);
		}

		params.add(am_param00);
		params.add(am_param01);
		params.add(am_param02);
		av.setParams(params);
		AutomationDAO.getInstance().updateValidation(av);

		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_AUTOMATION_EDIT_VALIDATION", Long.toString(av.getId()), null, av.toString());
	}
	
	/**
	 * New metadata validation
	 */
	private void createValidation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException,
			DatabaseException, AccessDeniedException, PathNotFoundException, RepositoryException, IllegalArgumentException, SecurityException,
			URISyntaxException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException,
			IllegalAccessException {
		long arId = WebUtils.getLong(request, "ar_id");
		AutomationValidation av = new AutomationValidation();
		av.setClassName(WebUtils.getString(request, "am_className"));
		av.setOrder(WebUtils.getInt(request, "am_order"));
		av.setActive(WebUtils.getBoolean(request, "am_active"));
		List<String> params = new ArrayList<String>();
		String am_param00 = WebUtils.getString(request, "am_param00");
		String am_param01 = WebUtils.getString(request, "am_param01");
		String am_param02 = WebUtils.getString(request, "am_param02");

		if (!am_param00.equals("")) {
			am_param00 = convertToInternalValue(am_param00, av.getClassName(), 0);
		}
		if (!am_param01.equals("")) {
			am_param01 = convertToInternalValue(am_param01, av.getClassName(), 1);
		}
		if (!am_param02.equals("")) {
			am_param02 = convertToInternalValue(am_param02, av.getClassName(), 2);
		}

		params.add(am_param00);
		params.add(am_param01);
		params.add(am_param02);
		av.setParams(params);
		AutomationDAO.getInstance().createValidation(av);
		AutomationRule ar = AutomationDAO.getInstance().findByPk(arId);
		ar.getValidations().add(av);
		AutomationDAO.getInstance().update(ar);

		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_AUTOMATION_ADD_VALIDATION", Long.toString(ar.getId()), null, ar.toString());
	}

	/**
	 * Delete validation
	 */
	private void deleteValidation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException,
			DatabaseException {
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
		UserActivity.log(request.getRemoteUser(), "ADMIN_AUTOMATION_DELETE_VALIDATION", Long.toString(ar.getId()), null, ar.toString());
	}

	/**
	 * Edit automation
	 */
	private void edit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DatabaseException {
		log.debug("edit({}, {})", new Object[]{request, response});

		if (isPost(request)) {
			long arId = WebUtils.getLong(request, "ar_id");
			AutomationRule ar = AutomationDAO.getInstance().findByPk(arId);
			ar.setName(WebUtils.getString(request, "ar_name"));
			ar.setOrder(WebUtils.getInt(request, "ar_order"));
			ar.setExclusive(WebUtils.getBoolean(request, "ar_exclusive"));
			ar.setActive(WebUtils.getBoolean(request, "ar_active"));
			ar.setEvent(WebUtils.getString(request, "ar_event"));
			AutomationDAO.getInstance().update(ar);

			// Activity log
			UserActivity.log(request.getRemoteUser(), "ADMIN_AUTOMATION_EDIT", Long.toString(ar.getId()), null, ar.toString());
		} else {
			ServletContext sc = getServletContext();
			long arId = WebUtils.getLong(request, "ar_id");
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("ar", AutomationDAO.getInstance().findByPk(arId));
			sc.setAttribute("ats", ats);
			sc.setAttribute("events", AutomationRule.EVENTS);
			sc.getRequestDispatcher("/admin/automation_rule_edit.jsp").forward(request, response);
		}

		log.debug("edit: void");
	}

	/**
	 * Load Metadata form
	 */
	private void loadMetadataForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException,
			DatabaseException, AccessDeniedException, PathNotFoundException, RepositoryException, IllegalArgumentException, SecurityException,
			URISyntaxException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException,
			IllegalAccessException {
		ServletContext sc = getServletContext();
		String action = WebUtils.getString(request, "newAction");
		sc.setAttribute("action", action);
		sc.setAttribute("ar_id", WebUtils.getString(request, "ar_id"));
		String className = WebUtils.getString(request, "am_className");

		if (action.equals("createAction") || action.equals("createValidation")) {
			sc.setAttribute("am", AutomationDAO.getInstance().findMetadataByPk(className));
			sc.setAttribute("aa_id", "");
			sc.setAttribute("av_id", "");
			sc.setAttribute("am_order", "0");
			sc.setAttribute("am_param00", "");
			sc.setAttribute("am_param01", "");
			sc.setAttribute("am_param02", "");
		} else if (action.equals("deleteAction") || action.equals("editAction")) {
			long aaId = WebUtils.getLong(request, "aa_id");
			sc.setAttribute("aa_id", aaId);
			sc.setAttribute("av_id", "");
			Automation am = AutomationDAO.getInstance().findMetadataByPk(className);
			AutomationAction aa = AutomationDAO.getInstance().findActionByPk(aaId);

			// clean attributes
			sc.setAttribute("am_param00", "");
			sc.setAttribute("am_param01", "");
			sc.setAttribute("am_param02", "");

			for (int i = 0; i < aa.getParams().size(); i++) {
				switch (i) {
					case 0:
						if (aa.getParams().get(0) != null && !aa.getParams().get(0).equals("")) {
							sc.setAttribute("am_param00", convertToHumanValue(aa.getParams().get(0), aa.getClassName(), 0));
						} else {
							sc.setAttribute("am_param00", "");
						}
						break;
					case 1:
						if (aa.getParams().get(1) != null && !aa.getParams().get(1).equals("")) {
							sc.setAttribute("am_param01", convertToHumanValue(aa.getParams().get(1), aa.getClassName(), 1));
						} else {
							sc.setAttribute("am_param01", "");
						}
						break;
					case 2:
						if (aa.getParams().get(2) != null && !aa.getParams().get(2).equals("")) {
							sc.setAttribute("am_param02", convertToHumanValue(aa.getParams().get(2), aa.getClassName(), 2));
						} else {
							sc.setAttribute("am_param02", "");
						}
						break;
				}
			}

			sc.setAttribute("am_order", String.valueOf(aa.getOrder()));
			am.setActive(aa.getActive());
			sc.setAttribute("am", am);
		} else if (action.equals("deleteValidation") || action.equals("editValidation")) {
			long avId = WebUtils.getLong(request, "av_id");
			sc.setAttribute("aa_id", "");
			sc.setAttribute("av_id", avId);
			Automation am = AutomationDAO.getInstance().findMetadataByPk(className);
			AutomationValidation av = AutomationDAO.getInstance().findValidationByPk(avId);

			// clean attributes
			sc.setAttribute("am_param00", "");
			sc.setAttribute("am_param01", "");
			sc.setAttribute("am_param02", "");

			for (int i = 0; i < av.getParams().size(); i++) {
				switch (i) {
				case 0:
					if (av.getParams().get(0) != null && !av.getParams().get(0).equals("")) {
						sc.setAttribute("am_param00", convertToHumanValue(av.getParams().get(0), av.getClassName(), 0));
					} else {
						sc.setAttribute("am_param00", "");
					}
					break;
				case 1:
					if (av.getParams().get(1) != null && !av.getParams().get(1).equals("")) {
						sc.setAttribute("am_param01", convertToHumanValue(av.getParams().get(1), av.getClassName(), 1));
					} else {
						sc.setAttribute("am_param01", "");
					}
					break;
				case 2:
					if (av.getParams().get(2) != null && !av.getParams().get(2).equals("")) {
						sc.setAttribute("am_param02", convertToHumanValue(av.getParams().get(2), av.getClassName(), 2));
					} else {
						sc.setAttribute("am_param02", "");
					}
					break;
				}
			}

			sc.setAttribute("am_order", String.valueOf(av.getOrder()));
			am.setActive(av.getActive());
			sc.setAttribute("am", am);
		}

		sc.getRequestDispatcher("/admin/automation_definition_form.jsp").forward(request, response);
	}

	/**
	 * Delete automation
	 */
	private void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DatabaseException {
		log.debug("delete({}, {})", new Object[]{request, response});

		if (isPost(request)) {
			long arId = WebUtils.getLong(request, "ar_id");
			AutomationDAO.getInstance().delete(arId);

			// Activity log
			UserActivity.log(request.getRemoteUser(), "ADMIN_AUTOMATION_DELETE", Long.toString(arId), null, null);
		} else {
			ServletContext sc = getServletContext();
			long arId = WebUtils.getLong(request, "ar_id");
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("ar", AutomationDAO.getInstance().findByPk(arId));
			sc.setAttribute("ats", ats);
			sc.setAttribute("events", AutomationRule.EVENTS);
			sc.getRequestDispatcher("/admin/automation_rule_edit.jsp").forward(request, response);
		}

		log.debug("edit: void");
	}

	/**
	 * convertToInternalValue
	 */
	private String convertToInternalValue(String value, String className, int param)
			throws DatabaseException, AccessDeniedException, PathNotFoundException, RepositoryException,
			IllegalArgumentException, SecurityException, URISyntaxException, ClassNotFoundException,
			NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		Automation am = AutomationDAO.getInstance().findMetadataByPk(className);

		// Convert folder path to UUID
		if (value != null && !value.equals("")) {
			switch (param) {
			case 0:
				if (Automation.PARAM_SOURCE_FOLDER.equals(am.getSource00())) {
					value = OKMRepository.getInstance().getNodeUuid(null, value);
				}
			case 1:
				if (Automation.PARAM_SOURCE_FOLDER.equals(am.getSource01())) {
					value = OKMRepository.getInstance().getNodeUuid(null, value);
				}
			case 2:
				if (Automation.PARAM_SOURCE_FOLDER.equals(am.getSource02())) {
					value = OKMRepository.getInstance().getNodeUuid(null, value);
				}
			}
		}

		return value;
	}

	/**
	 * convertToHumanValue
	 */
	private String convertToHumanValue(String value, String className, int param) throws DatabaseException, AccessDeniedException,
			PathNotFoundException, RepositoryException, IllegalArgumentException, SecurityException, URISyntaxException, ClassNotFoundException,
			NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		Automation am = AutomationDAO.getInstance().findMetadataByPk(className);

		// Convert folder path to UUID
		if (value != null && !value.equals("")) {
			switch (param) {
				case 0:
					if (Automation.PARAM_SOURCE_FOLDER.equals(am.getSource00())) {
						value = OKMRepository.getInstance().getNodePath(null, value);
					} else if (Automation.PARAM_SOURCE_OMR.equals(am.getSource00())) {
						Omr omr = OmrDAO.getInstance().findByPk(Long.parseLong(value));
						value = omr.getName();
					}
				case 1:
					if (Automation.PARAM_SOURCE_FOLDER.equals(am.getSource01())) {
						value = OKMRepository.getInstance().getNodePath(null, value);
					} else if (Automation.PARAM_SOURCE_OMR.equals(am.getSource01())) {
						Omr omr = OmrDAO.getInstance().findByPk(Long.parseLong(value));
						value = omr.getName();
					}
				case 2:
					if (Automation.PARAM_SOURCE_FOLDER.equals(am.getSource02())) {
						value = OKMRepository.getInstance().getNodePath(null, value);
					} else if (Automation.PARAM_SOURCE_OMR.equals(am.getSource02())) {
						Omr omr = OmrDAO.getInstance().findByPk(Long.parseLong(value));
						value = omr.getName();
					}
			}
		}

		return value;
	}
}
