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

import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.writer.CSVWriter;
import com.googlecode.jcsv.writer.internal.CSVWriterBuilder;
import com.googlecode.jcsv.writer.internal.DefaultCSVEntryConverter;
import com.ibm.icu.util.Calendar;
import com.openkm.api.OKMAuth;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.MimeTypeConfig;
import com.openkm.dao.AuthDAO;
import com.openkm.dao.ProfileDAO;
import com.openkm.dao.bean.Profile;
import com.openkm.dao.bean.Role;
import com.openkm.dao.bean.User;
import com.openkm.frontend.client.OKMException;
import com.openkm.module.common.CommonAuthModule;
import com.openkm.principal.DatabasePrincipalAdapter;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.servlet.frontend.ChatServlet;
import com.openkm.util.SecureStore;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User servlet
 */
public class AuthServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(AuthServlet.class);
	private static boolean db = Config.PRINCIPAL_ADAPTER.equals(DatabasePrincipalAdapter.class.getCanonicalName());

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		String userId = request.getRemoteUser();
		updateSessionManager(request);

		if (isMultipleInstancesAdmin(request) || request.isUserInRole(Config.DEFAULT_ADMIN_ROLE)) {
			try {
				if (action.equals("userCreate")) {
					userCreate(userId, request, response);
				} else if (action.equals("roleCreate")) {
					roleCreate(userId, request, response);
				} else if (action.equals("userEdit")) {
					userEdit(userId, request, response);
				} else if (action.equals("roleEdit")) {
					roleEdit(userId, request, response);
				} else if (action.equals("userDelete")) {
					userDelete(userId, request, response);
				} else if (action.equals("roleDelete")) {
					roleDelete(userId, request, response);
				} else if (action.equals("userActive")) {
					userActive(userId, request, response);
					userList(userId, request, response);
				} else if (action.equals("roleActive")) {
					roleActive(userId, request, response);
					roleList(userId, request, response);
				} else if (action.equals("userChatDisconnect")) {
					userChatDisconnect(request, response);
					userList(userId, request, response);
				} else if (action.equals("validateUser")) {
					validateUser(request, response);
				} else if (action.equals("validateRole")) {
					validateRole(request, response);
				} else if (action.endsWith("Export")) {
                    export(request, response, action);
                } else if (action.equals("roleList")) {
                    roleList(userId, request, response);
                } else {
                    userList(userId, request, response);
                }
				
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				sendErrorRedirect(request, response, e);
			} catch (NoSuchAlgorithmException e) {
				log.error(e.getMessage(), e);
				sendErrorRedirect(request, response, e);
			} catch (PrincipalAdapterException e) {
				log.error(e.getMessage(), e);
				sendErrorRedirect(request, response, e);
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				sendErrorRedirect(request, response, e);
			}
		} else {
			// Activity log
			UserActivity.log(userId, "ADMIN_ACCESS_DENIED", request.getRequestURI(), null, request.getQueryString());

			AccessDeniedException ade = new AccessDeniedException("You should not access this resource");
			sendErrorRedirect(request, response, ade);
		}
	}
	
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.debug("doPost({}, {})", request, response);
        String action = WebUtils.getString(request, "action");
        String userId = request.getRemoteUser();
        updateSessionManager(request);

        if (isMultipleInstancesAdmin(request) || request.isUserInRole(Config.DEFAULT_ADMIN_ROLE)) {
            try {

                if (action.equals("userCreate")) {
                    userCreate(userId, request, response);
                } else if (action.equals("roleCreate")) {
                    roleCreate(userId, request, response);
                } else if (action.equals("userEdit")) {
                    userEdit(userId, request, response);
                } else if (action.equals("roleEdit")) {
                    roleEdit(userId, request, response);
                } else if (action.equals("userDelete")) {
                    userDelete(userId, request, response);
                } else if (action.equals("roleDelete")) {
                    roleDelete(userId, request, response);
                }

                // Go to list
                if (action.startsWith("user")) {
                    response.sendRedirect(request.getContextPath() + request.getServletPath() + "?action=userList");
                } else {
                    response.sendRedirect(request.getContextPath() + request.getServletPath() + "?action=roleList");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sendErrorRedirect(request, response, e);
            }
        } else {
            // Activity log
            UserActivity.log(request.getRemoteUser(), "ADMIN_ACCESS_DENIED", request.getRequestURI(), null,
                    request.getQueryString());

            AccessDeniedException ade = new AccessDeniedException("You should not access this resource");
            sendErrorRedirect(request, response, ade);
        }
    }

	/**
	 * Validate user name
	 */
	private void validateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, DatabaseException {
		String value = WebUtils.getString(request, "value");
		PrintWriter out = response.getWriter();
		response.setContentType("text/json");

		if (AuthDAO.findUserByPk(value) == null) {
			if (value.matches(Config.PRINCIPAL_IDENTIFIER_VALIDATION)) {
				out.print("{ \"success\": true }");
			} else {
				out.print("{ \"success\": false, \"message\": \"Invalid identifier.\" }");
			}
		} else {
			out.print("{ \"success\": false, \"message\": \"Id already taken.\" }");
		}

		out.flush();
		out.close();
	}

	/**
	 * Validate role name
	 */
	private void validateRole(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, DatabaseException {
		String value = WebUtils.getString(request, "value");
		PrintWriter out = response.getWriter();
		response.setContentType("text/json");

		if (AuthDAO.findRoleByPk(value) == null) {
			if (value.matches(Config.PRINCIPAL_IDENTIFIER_VALIDATION)) {
				out.print("{ \"success\": true }");
			} else {
				out.print("{ \"success\": false, \"message\": \"Invalid identifier.\" }");
			}
		} else {
			out.print("{ \"success\": false, \"message\": \"Id already taken.\" }");
		}

		out.flush();
		out.close();
	}
	
	/**
     * Export users and roles
     */
    private void export(HttpServletRequest request, HttpServletResponse response, String action) throws PrincipalAdapterException, DatabaseException, IOException {
        List<String[]> csvValues = new ArrayList<>();
        String fileName = "";

        if (action.equals("userListExport")) {
            fileName = userListExport(csvValues);
        } else if (action.equals("roleListExport")) {
            fileName = roleListExport(csvValues);
        }

        // Prepare file headers
        WebUtils.prepareSendFile(request, response, fileName, MimeTypeConfig.MIME_CSV, false);

        // CSVWriter
        CSVStrategy strategyFormat = new CSVStrategy(Config.CSV_FORMAT_DELIMITER.toCharArray()[0],
                Config.CSV_FORMAT_QUOTE_CHARACTER.toCharArray()[0], Config.CSV_FORMAT_COMMENT_INDICATOR.toCharArray()[0],
                Config.CSV_FORMAT_SKIP_HEADER, Config.CSV_FORMAT_IGNORE_EMPTY_LINES);
        Writer out = new OutputStreamWriter(response.getOutputStream());

        try {
            CSVWriter<String[]> csvWriter = new CSVWriterBuilder<String[]>(out).strategy(strategyFormat).entryConverter(new DefaultCSVEntryConverter()).build();
            csvWriter.writeAll(csvValues);
            csvWriter.flush();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

	/**
	 * New user
	 */
	private void userCreate(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException, AccessDeniedException {
		log.debug("userCreate({}, {}, {})", new Object[]{userId, request, response});

		if (Config.CLOUD_MODE && Config.CLOUD_MAX_USERS > 0) {
			// Subtract 2 because users "okmAdmin" and "admin" should not count
			int regUsers = AuthDAO.findAllUsers(false).size() - 2;

			if (regUsers >= Config.CLOUD_MAX_USERS) {
				String usr = request.getRemoteUser();
				UserActivity.log(usr, "ERROR_USERS_EXCEEDED", null, null, Long.toString(regUsers));
				throw new DatabaseException("Number of users exceeded: " + Long.toString(regUsers));
			}
		}

		if (WebUtils.getBoolean(request, "persist")) {
			String reqCsrft = WebUtils.getString(request, "csrft");
			String sesCsrft = (String) request.getSession().getAttribute("csrft");

			if (reqCsrft.equals(sesCsrft)) {
				String usrId = WebUtils.getString(request, "usr_id");

				if (AuthDAO.findUserByPk(usrId) == null) {
					if (usrId.matches(Config.PRINCIPAL_IDENTIFIER_VALIDATION)) {
						User usr = new User();
						usr.setId(usrId);
						usr.setName(WebUtils.getString(request, "usr_name"));
						usr.setPassword(WebUtils.getString(request, "usr_password"));
						usr.setEmail(WebUtils.getString(request, "usr_email"));
						usr.setActive(WebUtils.getBoolean(request, "usr_active"));
						List<String> usrRoles = WebUtils.getStringList(request, "usr_roles");

						for (String rolId : usrRoles) {
							usr.getRoles().add(AuthDAO.findRoleByPk(rolId));
						}

						AuthDAO.createUser(usr);

						// Activity log
						UserActivity.log(userId, "ADMIN_USER_CREATE", usr.getId(), null, usr.toString());
					} else {
						throw new DatabaseException("Invalid identifier");
					}
				} else {
					throw new DatabaseException("User name already taken");
				}
			} else {
				// Activity log
				UserActivity.log(request.getRemoteUser(), "ADMIN_SECURITY_RISK", request.getRemoteHost(), null, null);

				throw new AccessDeniedException("Security risk detected");
			}
		} else {
			String genCsrft = SecureStore.md5Encode(UUID.randomUUID().toString().getBytes());
			request.getSession().setAttribute("csrft", genCsrft);
			ServletContext sc = getServletContext();
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("csrft", genCsrft);
			sc.setAttribute("roles", AuthDAO.findAllRoles());
			sc.setAttribute("usr", null);
			sc.getRequestDispatcher("/admin/user_edit.jsp").forward(request, response);
		}

		log.debug("userCreate: void");
	}

	/**
	 * Edit user
	 */
	private void userEdit(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException, AccessDeniedException {
		log.debug("userEdit({}, {}, {})", new Object[]{userId, request, response});
		String usrId = WebUtils.getString(request, "usr_id");

		if (WebUtils.getBoolean(request, "persist")) {
			if (isMultipleInstancesAdmin(request) || !usrId.equals(Config.ADMIN_USER)) {
				String reqCsrft = WebUtils.getString(request, "csrft");
				String sesCsrft = (String) request.getSession().getAttribute("csrft");

				if (reqCsrft.equals(sesCsrft)) {
					String password = WebUtils.getString(request, "usr_password");
					User usr = new User();
					usr.setId(usrId);
					usr.setName(WebUtils.getString(request, "usr_name"));
					usr.setEmail(WebUtils.getString(request, "usr_email"));
					usr.setActive(WebUtils.getBoolean(request, "usr_active"));
					List<String> usrRoles = WebUtils.getStringList(request, "usr_roles");

					for (String rolId : usrRoles) {
						usr.getRoles().add(AuthDAO.findRoleByPk(rolId));
					}

					AuthDAO.updateUser(usr);

					if (!password.equals("")) {
						AuthDAO.updateUserPassword(usr.getId(), password);
					}

					// Activity log
					UserActivity.log(userId, "ADMIN_USER_EDIT", usr.getId(), null, usr.toString());
				} else {
					// Activity log
					UserActivity.log(request.getRemoteUser(), "ADMIN_SECURITY_RISK", request.getRemoteHost(), null, null);

					throw new AccessDeniedException("Security risk detected");
				}
			}
		} else {
			String genCsrft = SecureStore.md5Encode(UUID.randomUUID().toString().getBytes());
			request.getSession().setAttribute("csrft", genCsrft);
			ServletContext sc = getServletContext();
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("csrft", genCsrft);
			sc.setAttribute("roles", AuthDAO.findAllRoles());
			sc.setAttribute("usr", AuthDAO.findUserByPk(usrId));
			sc.getRequestDispatcher("/admin/user_edit.jsp").forward(request, response);
		}

		log.debug("userEdit: void");
	}

	/**
	 * Update user
	 */
	private void userDelete(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException, AccessDeniedException {
		log.debug("userDelete({}, {}, {})", new Object[]{userId, request, response});
		String usrId = WebUtils.getString(request, "usr_id");

		if (WebUtils.getBoolean(request, "persist")) {
			if (isMultipleInstancesAdmin(request) || !usrId.equals(Config.ADMIN_USER)) {
				String reqCsrft = WebUtils.getString(request, "csrft");
				String sesCsrft = (String) request.getSession().getAttribute("csrft");

				if (reqCsrft.equals(sesCsrft)) {
					AuthDAO.deleteUser(usrId);

					// Activity log
					UserActivity.log(userId, "ADMIN_USER_DELETE", usrId, null, null);
				} else {
					// Activity log
					UserActivity.log(request.getRemoteUser(), "ADMIN_SECURITY_RISK", request.getRemoteHost(), null, null);

					throw new AccessDeniedException("Security risk detected");
				}
			}
		} else {
			String genCsrft = SecureStore.md5Encode(UUID.randomUUID().toString().getBytes());
			request.getSession().setAttribute("csrft", genCsrft);
			ServletContext sc = getServletContext();
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("csrft", genCsrft);
			sc.setAttribute("roles", AuthDAO.findAllRoles());
			sc.setAttribute("usr", AuthDAO.findUserByPk(usrId));
			sc.getRequestDispatcher("/admin/user_edit.jsp").forward(request, response);
		}

		log.debug("userDelete: void");
	}

	/**
	 * Active user
	 */
	private void userActive(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException, AccessDeniedException {
		log.debug("userActive({}, {}, {})", new Object[]{userId, request, response});
		boolean active = WebUtils.getBoolean(request, "usr_active");
		String usrId = WebUtils.getString(request, "usr_id");

		if (isMultipleInstancesAdmin(request) || !usrId.equals(Config.ADMIN_USER)) {
			AuthDAO.activeUser(usrId, active);

			// Activity log
			UserActivity.log(userId, "ADMIN_USER_ACTIVE", usrId, null, Boolean.toString(active));
		}

		log.debug("userActive: void");
	}

	/**
	 * List users
	 */
	private void userList(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException, PrincipalAdapterException {
		log.debug("userList({}, {}, {})", new Object[]{userId, request, response});
		String roleFilter = WebUtils.getString(request, "roleFilter");
		ServletContext sc = getServletContext();
		sc.setAttribute("roleFilter", roleFilter);
		sc.setAttribute("chatUsers", ChatServlet.getChatManager().getLoggedUsers());

		if (roleFilter.equals("")) {
			if (db) {
				List<User> users = sortUserRoles(AuthDAO.findAllUsers(false));
				sc.setAttribute("users", toMapSetProfile(users));
				sc.setAttribute("roles", AuthDAO.findAllRoles());
			} else {
				List<User> users = str2user(OKMAuth.getInstance().getUsers(null));
				sc.setAttribute("users", toMapSetProfile(users));
				sc.setAttribute("roles", str2role(OKMAuth.getInstance().getRoles(null)));
			}
		} else {
			if (db) {
				List<User> users = sortUserRoles(AuthDAO.findUsersByRole(roleFilter, false));
				sc.setAttribute("users", toMapSetProfile(users));
				sc.setAttribute("roles", AuthDAO.findAllRoles());
			} else {
				List<User> users = str2user(OKMAuth.getInstance().getUsersByRole(null, roleFilter));
				sc.setAttribute("users", toMapSetProfile(users));
				sc.setAttribute("roles", str2role(OKMAuth.getInstance().getRoles(null)));
			}
		}

		String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
		sc.setAttribute("db", db);
		sc.setAttribute("date", date);
		sc.setAttribute("multInstAdmin", isMultipleInstancesAdmin(request));
		sc.getRequestDispatcher("/admin/user_list.jsp").forward(request, response);
		log.debug("userList: void");
	}

	/**
	 * User list export
	 */
	private String userListExport(List<String[]> csvValues) throws PrincipalAdapterException, DatabaseException {
		String[] columns = new String[]{"Id", "Name", "Mail", "Roles", "Active"};
		csvValues.add(columns);

		for (User usr : str2user(CommonAuthModule.getUsers())) {
			StringBuilder roles = new StringBuilder();

			for (Role rol : usr.getRoles()) {
				roles.append(rol.getId()).append(" ");
			}

			csvValues.add(new String[]{usr.getId(), usr.getName(), usr.getEmail(), roles.toString(),
					String.valueOf(usr.isActive())});
		}

		return "users-export.csv";
	}

	/**
	 * New role
	 */
	private void roleCreate(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException, AccessDeniedException {
		log.debug("roleCreate({}, {}, {})", new Object[]{userId, request, response});

		if (WebUtils.getBoolean(request, "persist")) {
			String reqCsrft = WebUtils.getString(request, "csrft");
			String sesCsrft = (String) request.getSession().getAttribute("csrft");

			if (reqCsrft.equals(sesCsrft)) {
				String rolId = WebUtils.getString(request, "rol_id");

				if (AuthDAO.findRoleByPk(rolId) == null) {
					if (rolId.matches(Config.PRINCIPAL_IDENTIFIER_VALIDATION)) {
						Role rol = new Role();
						rol.setId(rolId);
						rol.setActive(WebUtils.getBoolean(request, "rol_active"));
						AuthDAO.createRole(rol);

						// Activity log
						UserActivity.log(userId, "ADMIN_ROLE_CREATE", rol.getId(), null, rol.toString());
					} else {
						throw new DatabaseException("Invalid identifier");
					}
				} else {
					throw new DatabaseException("Role name already taken");
				}
			} else {
				// Activity log
				UserActivity.log(request.getRemoteUser(), "ADMIN_SECURITY_RISK", request.getRemoteHost(), null, null);

				throw new AccessDeniedException("Security risk detected");
			}
		} else {
			String genCsrft = SecureStore.md5Encode(UUID.randomUUID().toString().getBytes());
			request.getSession().setAttribute("csrft", genCsrft);
			ServletContext sc = getServletContext();
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("csrft", genCsrft);
			sc.setAttribute("rol", null);
			sc.getRequestDispatcher("/admin/role_edit.jsp").forward(request, response);
		}

		log.debug("roleCreate: void");
	}

	/**
	 * Edit role
	 */
	private void roleEdit(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException, AccessDeniedException {
		log.debug("roleEdit({}, {}, {})", new Object[]{userId, request, response});

		if (WebUtils.getBoolean(request, "persist")) {
			String reqCsrft = WebUtils.getString(request, "csrft");
			String sesCsrft = (String) request.getSession().getAttribute("csrft");

			if (reqCsrft.equals(sesCsrft)) {
				Role rol = new Role();
				rol.setId(WebUtils.getString(request, "rol_id"));
				rol.setActive(WebUtils.getBoolean(request, "rol_active"));
				AuthDAO.updateRole(rol);

				// Activity log
				UserActivity.log(userId, "ADMIN_ROLE_EDIT", rol.getId(), null, rol.toString());
			} else {
				// Activity log
				UserActivity.log(request.getRemoteUser(), "ADMIN_SECURITY_RISK", request.getRemoteHost(), null, null);

				throw new AccessDeniedException("Security risk detected");
			}
		} else {
			String genCsrft = SecureStore.md5Encode(UUID.randomUUID().toString().getBytes());
			request.getSession().setAttribute("csrft", genCsrft);
			ServletContext sc = getServletContext();
			String rolId = WebUtils.getString(request, "rol_id");
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("csrft", genCsrft);
			sc.setAttribute("rol", AuthDAO.findRoleByPk(rolId));
			sc.getRequestDispatcher("/admin/role_edit.jsp").forward(request, response);
		}

		log.debug("roleEdit: void");
	}

	/**
	 * Delete role
	 */
	private void roleDelete(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException, AccessDeniedException {
		log.debug("roleDelete({}, {}, {})", new Object[]{userId, request, response});

		if (WebUtils.getBoolean(request, "persist")) {
			String reqCsrft = WebUtils.getString(request, "csrft");
			String sesCsrft = (String) request.getSession().getAttribute("csrft");

			if (reqCsrft.equals(sesCsrft)) {
				String rolId = WebUtils.getString(request, "rol_id");
				AuthDAO.deleteRole(rolId);

				// Activity log
				UserActivity.log(userId, "ADMIN_ROLE_DELETE", rolId, null, null);
			} else {
				// Activity log
				UserActivity.log(request.getRemoteUser(), "ADMIN_SECURITY_RISK", request.getRemoteHost(), null, null);

				throw new AccessDeniedException("Security risk detected");
			}
		} else {
			String genCsrft = SecureStore.md5Encode(UUID.randomUUID().toString().getBytes());
			request.getSession().setAttribute("csrft", genCsrft);
			ServletContext sc = getServletContext();
			String rolId = WebUtils.getString(request, "rol_id");
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("csrft", genCsrft);
			sc.setAttribute("rol", AuthDAO.findRoleByPk(rolId));
			sc.getRequestDispatcher("/admin/role_edit.jsp").forward(request, response);
		}

		log.debug("roleDelete: void");
	}

	/**
	 * Active role
	 */
	private void roleActive(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException, AccessDeniedException {
		log.debug("roleActive({}, {}, {})", new Object[]{userId, request, response});
		String rolId = WebUtils.getString(request, "rol_id");
		boolean active = WebUtils.getBoolean(request, "rol_active");
		AuthDAO.activeRole(rolId, active);

		// Activity log
		UserActivity.log(userId, "ADMIN_ROLE_ACTIVE", rolId, null, Boolean.toString(active));
		log.debug("roleActive: void");
	}

	/**
	 * Diconnect user chat
	 */
	private void userChatDisconnect(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, DatabaseException, NoSuchAlgorithmException {
		log.debug("userChatDisconnect({}, {})", new Object[]{request, response});
		try {
			String userId = WebUtils.getString(request, "usr_id");
			ChatServlet.getChatManager().logout(userId);
		} catch (OKMException e) {
			throw new ServletException(e.getMessage());
		}
		log.debug("userChatDisconnect: void");
	}

	/**
	 * List roles
	 */
	private void roleList(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException, PrincipalAdapterException {
		log.debug("roleList({}, {}, {})", new Object[]{userId, request, response});
		ServletContext sc = getServletContext();

		if (db) {
			sc.setAttribute("roles", AuthDAO.findAllRoles());
		} else {
			sc.setAttribute("roles", str2role(OKMAuth.getInstance().getRoles(null)));
		}

		sc.setAttribute("db", db);
		sc.getRequestDispatcher("/admin/role_list.jsp").forward(request, response);
		log.debug("roleList: void");
	}

	/**
	 * Export role list
	 */
	private String roleListExport(List<String[]> csvValues) throws PrincipalAdapterException, DatabaseException {
		String[] columns = new String[]{"Id", "Active"};
		csvValues.add(columns);

		for (Role rol : str2role(CommonAuthModule.getRoles())) {
			csvValues.add(new String[]{rol.getId(), String.valueOf(rol.isActive())});
		}

		return "roles-export.csv";
	}

	/**
	 * Convenient conversion method
	 */
	private List<User> str2user(List<String> strList) throws PrincipalAdapterException {
		List<User> usrList = new ArrayList<User>();

		for (String usrId : strList) {
			List<String> roleList = OKMAuth.getInstance().getRolesByUser(null, usrId);
			User usr = new User();
			usr.setId(usrId);
			usr.setActive(true);
			usr.setName(OKMAuth.getInstance().getName(null, usrId));
			usr.setEmail(OKMAuth.getInstance().getMail(null, usrId));

			if (!roleList.isEmpty()) {
				Set<Role> roles = new TreeSet<Role>(new RoleComparator());

				for (String rolId : roleList) {
					Role rol = new Role();
					rol.setId(rolId);
					rol.setActive(true);
					roles.add(rol);
				}

				usr.setRoles(roles);
			}

			usrList.add(usr);
		}

		Collections.sort(usrList, new UserComparator());
		return usrList;
	}

	/**
	 * Convenient conversion method
	 */
	private List<Role> str2role(List<String> strList) {
		List<Role> roleList = new ArrayList<Role>();

		for (String id : strList) {
			Role rol = new Role();
			rol.setId(id);
			rol.setActive(true);
			roleList.add(rol);
		}

		Collections.sort(roleList, new RoleComparator());
		return roleList;
	}

	/**
	 * User comparator
	 */
	private class UserComparator implements Comparator<User> {
		@Override
		public int compare(User arg0, User arg1) {
			if (arg0 != null && arg1 != null) {
				return arg0.getId().compareTo(arg1.getId());
			} else {
				return 0;
			}
		}
	}

	/**
	 * Role comparator
	 */
	private class RoleComparator implements Comparator<Role> {
		@Override
		public int compare(Role arg0, Role arg1) {
			if (arg0 != null && arg1 != null) {
				return arg0.getId().compareTo(arg1.getId());
			} else {
				return 0;
			}
		}
	}

	/**
	 * Sort roles from user
	 */
	private List<User> sortUserRoles(List<User> users) {
		List<User> ret = new ArrayList<User>();

		for (User user : users) {
			Set<Role> sortedRoles = new TreeSet<Role>(new RoleComparator());
			sortedRoles.addAll(user.getRoles());
			user.setRoles(sortedRoles);
			ret.add(user);
		}

		return ret;
	}

	/**
	 * Convert to Map and set Profile
	 */
	private List<Map<String, Object>> toMapSetProfile(List<User> users) throws DatabaseException {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();

		for (User user : users) {
			Map<String, Object> usrMap = new HashMap<String, Object>();
			Profile prf = ProfileDAO.findByUser(user.getId());

			if (prf != null) {
				usrMap.put("profile", prf.getName());
			} else {
				usrMap.put("profile", "");
			}

			usrMap.put("id", user.getId());
			usrMap.put("name", user.getName());
			usrMap.put("email", user.getEmail());
			usrMap.put("active", user.isActive());
			usrMap.put("roles", user.getRoles());
			ret.add(usrMap);
		}

		return ret;
	}
}
