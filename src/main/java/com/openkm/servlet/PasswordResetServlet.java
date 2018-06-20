/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Danilo Tomasoni
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

package com.openkm.servlet;

import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.dao.AuthDAO;
import com.openkm.dao.bean.User;
import com.openkm.jaas.PrincipalUtils;
import com.openkm.module.db.stuff.DbSessionManager;
import com.openkm.spring.SecurityHolder;
import com.openkm.util.MailUtils;
import com.openkm.util.WebUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PasswordResetServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(PasswordResetServlet.class);
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		ServletContext sc = getServletContext();
		sc.removeAttribute("forgot");
		sc.removeAttribute("failed");
		response.sendRedirect("login.jsp");
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String userId = WebUtils.getString(request, "userId");
		ServletContext sc = getServletContext();
		User usr = null;

		if (Config.USER_PASSWORD_RESET) {
			try {
				usr = AuthDAO.findUserByPk(userId);
			} catch (DatabaseException e) {
				log.error(getServletName() + " User '" + userId + "' not found");
			}

			if (usr != null) {
				try {
					String newPass = resetPassword(userId);
					MailUtils.sendMessage(usr.getEmail(), usr.getEmail(), "OpenKM Password reset", "Your new password is: " + newPass
							+ "<br/>" + "To change it log in and then go to 'Tools' > 'Preferences' > 'User Configuration'.");
					sc.setAttribute("resetOk", usr.getEmail());
					response.sendRedirect("password_reset.jsp");
				} catch (MessagingException e) {
					log.error(e.getMessage(), e);
					sc.setAttribute("resetFailed", "Failed to send the new password by email");
					response.sendRedirect("password_reset.jsp");
				} catch (DatabaseException e) {
					log.error(e.getMessage(), e);
					sc.setAttribute("resetFailed", "Failed reset the user password");
					response.sendRedirect("password_reset.jsp");
				} catch (AccessDeniedException e) {
					log.error(e.getMessage(), e);
					sc.setAttribute("resetFailed", "Failed reset the user password");
					response.sendRedirect("password_reset.jsp");
				}
			} else {
				sc.setAttribute("resetFailed", "Invalid user name provided");
				sc.getRequestDispatcher("/password_reset.jsp").forward(request, response);
			}
		} else {
			sc.getRequestDispatcher("/login.jsp").forward(request, response);
		}
	}

	private String resetPassword(String userId) throws DatabaseException, AccessDeniedException {
		try {
			// Necessary to have privileges to change the password
			String sysToken = DbSessionManager.getInstance().getSystemToken();
			SecurityHolder.set(PrincipalUtils.getAuthenticationByToken(sysToken));

			String newPass = RandomStringUtils.randomAlphanumeric(12);
			AuthDAO.updateUserPassword(userId, newPass);

			return newPass;
		} finally {
			SecurityHolder.unset();
		}
	}
}