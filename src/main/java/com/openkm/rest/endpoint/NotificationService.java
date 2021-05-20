package com.openkm.rest.endpoint;

import com.openkm.module.ModuleManager;
import com.openkm.module.NotificationModule;
import com.openkm.rest.GenericException;
import io.swagger.annotations.Api;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Api(value = "notification-service")
@Path("/notification")
public class NotificationService {
	private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

	@POST
	@Path("/notify")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void notify(List<Attachment> atts) throws GenericException {
		log.debug("notify({})", atts);
		List<String> users = new ArrayList<>();
		List<String> mails = new ArrayList<>();
		String nodeId = "";
		String message = "";
		InputStream is = null;
		boolean attachment = false;

		try {
			for (Attachment att : atts) {
				if ("nodeId".equals(att.getContentDisposition().getParameter("name"))) {
					nodeId = att.getObject(String.class);
				} else if ("user".equals(att.getContentDisposition().getParameter("name"))) {
					String value = att.getObject(String.class);
					users.add(value);
				} else if ("mail".equals(att.getContentDisposition().getParameter("name"))) {
					String value = att.getObject(String.class);
					mails.add(value);
				} else if ("message".equals(att.getContentDisposition().getParameter("name"))) {
					is = att.getDataHandler().getInputStream();
					message = IOUtils.toString(is);
				} else if ("attachment".equals(att.getContentDisposition().getParameter("name"))) {
					attachment = Boolean.parseBoolean(att.getObject(String.class));
				}
			}

			NotificationModule nm = ModuleManager.getNotificationModule();
			nm.notify(null, nodeId, users, mails, message, attachment);
			log.debug("notify: void");
		} catch (Exception e) {
			throw new GenericException(e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
}
