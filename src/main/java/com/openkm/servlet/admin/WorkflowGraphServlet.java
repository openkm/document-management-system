package com.openkm.servlet.admin;

import com.openkm.api.OKMWorkflow;
import com.openkm.core.DatabaseException;
import com.openkm.core.WorkflowException;
import com.openkm.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Workflow graphic servlet
 */
public class WorkflowGraphServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(WorkflowGraphServlet.class);

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		request.setCharacterEncoding("UTF-8");
		long id = WebUtils.getLong(request, "id");
		String node = WebUtils.getString(request, "node");
		;
		ServletOutputStream sos = response.getOutputStream();
		updateSessionManager(request);

		try {
			// Get image
			byte[] data = OKMWorkflow.getInstance().getProcessDefinitionImage(null, id, node);

			if (data != null) {
				// Disable browser cache
				response.setHeader("Expires", "Sat, 6 May 1971 12:00:00 GMT");
				response.setHeader("Cache-Control", "max-age=0, must-revalidate");
				response.addHeader("Cache-Control", "post-check=0, pre-check=0");

				// Send data
				response.setContentType("image/jpeg");
				response.setContentLength(data.length);
				sos.write(data);
			} else {
				response.setContentType("text/plain");
				sos.write("Null process definition image".getBytes());
			}
		} catch (WorkflowException e) {
			log.error(e.getMessage(), e);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			sos.flush();
			sos.close();
		}
	}
}
