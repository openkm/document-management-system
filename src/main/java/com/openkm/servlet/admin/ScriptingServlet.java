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

package com.openkm.servlet.admin;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.ParseException;
import bsh.TargetError;
import com.openkm.util.FormatUtil;
import com.openkm.util.SecureStore;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * Execute BeanShell
 * 
 * @author sochoa
 */
@WebServlet("/admin/Scripting")
public class ScriptingServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private static Logger log = LoggerFactory.getLogger(ScriptingServlet.class);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.debug("doGet({}, {})", request, response);
        updateSessionManager(request);

        try {
            String genCsrft = SecureStore.md5Encode(UUID.randomUUID().toString().getBytes());
            request.getSession().setAttribute("csrft", genCsrft);
            ServletContext sc = getServletContext();
            sc.setAttribute("script", null);
            sc.setAttribute("csrft", genCsrft);
            sc.setAttribute("scriptResult", null);
            sc.setAttribute("scriptError", null);
            sc.setAttribute("scriptOutput", null);
            sc.setAttribute("time", null);
            sc.getRequestDispatcher("/admin/scripting.jsp").forward(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendErrorRedirect(request, response, e);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.debug("doPost({}, {})", request, response);
        updateSessionManager(request);
        String action = WebUtils.getString(request, "action");
        String script = WebUtils.getString(request, "script");
        String fsPath = WebUtils.getString(request, "fsPath");
        String reqCsrft = WebUtils.getString(request, "csrft");
        String sesCsrft = (String) request.getSession().getAttribute("csrft");

        try {
            ByteArrayOutputStream scriptOutput = new ByteArrayOutputStream();
            Exception scriptError = null;
            Object scriptResult = null;
            long begin = System.currentTimeMillis();

            if (action.equals("Load") && !fsPath.isEmpty()) {
                if (reqCsrft.equals(sesCsrft)) {
                    File file = new File(fsPath);
                    FileInputStream fis = new FileInputStream(file);
                    script = IOUtils.toString(fis);
                    IOUtils.closeQuietly(fis);
                } else {
                    UserActivity.log(request.getRemoteUser(), "ADMIN_SECURITY_RISK", request.getRemoteHost(), null, script);
                    throw new ServletException("Security risk detected");
                }
            } else if (action.equals("Save") && !fsPath.isEmpty() && !script.isEmpty()) {
                if (reqCsrft.equals(sesCsrft)) {
                    // Trim filename
                    File fsFile = new File(fsPath.trim());
                    String fsName = fsFile.getName().trim();
                    String fsParent = fsFile.getParent().trim();
                    File file = new File(fsParent, fsName);

                    FileOutputStream fos = new FileOutputStream(file);
                    IOUtils.write(script, fos);
                    IOUtils.closeQuietly(fos);
                } else {
                    UserActivity.log(request.getRemoteUser(), "ADMIN_SECURITY_RISK", request.getRemoteHost(), null, script);
                    throw new ServletException("Security risk detected");
                }
            } else if (action.equals("Evaluate") && !script.isEmpty()) {
                if (reqCsrft.equals(sesCsrft)) {
                    PrintStream pout = new PrintStream(scriptOutput);
                    Interpreter bsh = new Interpreter(null, pout, pout, false);

                    // set up interpreter
                    bsh.set("bsh.httpServletRequest", request);
                    bsh.set("bsh.httpServletResponse", response);

                    try {
                        scriptResult = bsh.eval(script);
                    } catch (ParseException e) {
                        scriptError = e;
                    } catch (TargetError e) {
                        scriptError = e;
                    } catch (EvalError e) {
                        scriptError = e;
                    } catch (Exception e) {
                        scriptError = e;
                    }

                    pout.flush();

                    // Activity log
                    UserActivity.log(request.getRemoteUser(), "ADMIN_SCRIPTING", request.getRemoteHost(), null, script);
                } else {
                    UserActivity.log(request.getRemoteUser(), "ADMIN_SECURITY_RISK", request.getRemoteHost(), null, script);
                    throw new ServletException("Security risk detected");
                }
            }

            String genCsrft = SecureStore.md5Encode(UUID.randomUUID().toString().getBytes());
            request.getSession().setAttribute("csrft", genCsrft);
            ServletContext sc = getServletContext();
            sc.setAttribute("script", script);
            sc.setAttribute("csrft", genCsrft);
            sc.setAttribute("scriptResult", scriptResult);
            sc.setAttribute("scriptError", scriptError);
            sc.setAttribute("scriptOutput", scriptOutput.toString());
            sc.setAttribute("time", FormatUtil.formatMiliSeconds(System.currentTimeMillis() - begin));
            sc.getRequestDispatcher("/admin/scripting.jsp").forward(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendErrorRedirect(request, response, e);
        }
    }
}