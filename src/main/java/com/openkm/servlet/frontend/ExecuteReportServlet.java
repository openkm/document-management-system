package com.openkm.servlet.frontend;

import bsh.EvalError;
import com.openkm.bean.form.FormElement;
import com.openkm.bean.form.Input;
import com.openkm.core.DatabaseException;
import com.openkm.core.ParseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.dao.ReportDAO;
import com.openkm.dao.UserConfigDAO;
import com.openkm.dao.bean.Profile;
import com.openkm.dao.bean.Report;
import com.openkm.dao.bean.UserConfig;
import com.openkm.util.ISO8601;
import com.openkm.util.ReportUtils;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Execute report for users
 *
 * @pavila
 */
public class ExecuteReportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ExecuteReportServlet.class);

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		int id = WebUtils.getInt(request, "id");
		int format = WebUtils.getInt(request, "format", ReportUtils.OUTPUT_PDF);
		ByteArrayOutputStream baos = null;
		ByteArrayInputStream bais = null;
		String user = request.getRemoteUser();

		try {
			UserConfig uc = UserConfigDAO.findByPk(request.getRemoteUser());
			Profile up = uc.getProfile();

			if (up.getPrfMisc().getReports().contains(new Long(id).longValue())) {
				Report rp = ReportDAO.findByPk(id);

				// Set file name
				String fileName = rp.getFileName().substring(0, rp.getFileName().indexOf('.')) + ReportUtils.FILE_EXTENSION[format];

				// Set default report parameters
				Map<String, Object> params = new HashMap<String, Object>();
				String host = com.openkm.core.Config.APPLICATION_URL;
				params.put("host", host.substring(0, host.lastIndexOf("/") + 1));

				for (FormElement fe : ReportUtils.getReportParameters(id)) {
					if (fe instanceof Input && ((Input) fe).getType().equals(Input.TYPE_DATE)) {
						params.put(fe.getName(), ISO8601.parseBasic(WebUtils.getString(request, fe.getName())).getTime());
					} else {
						params.put(fe.getName(), WebUtils.getString(request, fe.getName()));
					}
				}

				baos = ReportUtils.execute(rp, params, format);
				bais = new ByteArrayInputStream(baos.toByteArray());
				WebUtils.sendFile(request, response, fileName, ReportUtils.FILE_MIME[format], false, bais);

				// Activity log
				UserActivity.log(user, "EXECUTE_REPORT", Integer.toString(id), null, "OK");
			} else {
				// Activity log
				UserActivity.log(user, "EXECUTE_REPORT", Integer.toString(id), null, "FAILURE");
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} catch (JRException e) {
			log.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} catch (EvalError e) {
			log.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} finally {
			IOUtils.closeQuietly(bais);
			IOUtils.closeQuietly(baos);
		}
	}
}
