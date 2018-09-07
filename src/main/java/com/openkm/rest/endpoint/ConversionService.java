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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.rest.endpoint;

import com.openkm.core.MimeTypeConfig;
import com.openkm.dao.MimeTypeDAO;
import com.openkm.dao.bean.MimeType;
import com.openkm.rest.GenericException;
import com.openkm.util.DocConverter;
import com.openkm.util.FileUtils;
import com.openkm.util.ImageUtils;
import io.swagger.annotations.Api;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.List;

@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Api(description="conversion-service", value="conversion-service")
@Path("/conversion")
public class ConversionService {
	private static Logger log = LoggerFactory.getLogger(ConversionService.class);

	@POST
	@Path("/doc2pdf")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MimeTypeConfig.MIME_PDF)
	// The "filename" parameter comes in the POST request body (encoded as XML or JSON).
	public Response doc2pdf(List<Attachment> atts) throws GenericException {
		log.debug("doc2pdf({})", atts);
		String filename = null;
		String mimeType = null;
		InputStream is = null;
		FileOutputStream fos = null;
		File input = null;
		File output = null;

		try {
			for (Attachment att : atts) {
				ContentDisposition contDisp = att.getContentDisposition();

				if ("content".equals(contDisp.getParameter("name"))) {
					filename = contDisp.getParameter("filename");
					mimeType = MimeTypeConfig.mimeTypes.getContentType(filename.toLowerCase());
					is = att.getDataHandler().getInputStream();
				}
			}

			if (!DocConverter.validOpenOffice.contains(mimeType)) {
				throw new NotImplementedException("Unsupported MIME type: " + mimeType);
			}

			input = FileUtils.createTempFileFromMime(mimeType);
			fos = new FileOutputStream(input);
			IOUtils.copy(is, fos);
			output = FileUtils.createTempFileFromMime(MimeTypeConfig.MIME_PDF);
			DocConverter.getInstance().doc2pdf(input, mimeType, output);
			final FileInputStream fis = new FileInputStream(output);

			StreamingOutput stream = new StreamingOutput() {
				@Override
				public void write(OutputStream os) throws IOException, WebApplicationException {
					IOUtils.copy(fis, os);
					IOUtils.closeQuietly(fis);
					IOUtils.closeQuietly(os);
				}
			};

			Response.ResponseBuilder response = Response.ok(stream);
			String convertedFile = FileUtils.getFileName(filename) + ".pdf";
			response.header("Content-Disposition", "attachment; filename=\"" + convertedFile + "\"");
			log.debug("doc2pdf: [BINARY]");
			return response.build();
		} catch (Exception e) {
			throw new GenericException(e);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(fos);
			FileUtils.deleteQuietly(input);
			FileUtils.deleteQuietly(output);
		}
	}

	@POST
	@Path("/imageConvert")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	// The "mimeType" parameter comes in the POST request body (encoded as XML or JSON).
	public Response imageConvert(List<Attachment> atts) throws GenericException {
		log.debug("imageConvert({})", atts);
		String filename = null;
		String srcMimeTypeIn = null;
		String dstMimeType = null;
		String params = null;
		InputStream is = null;
		FileOutputStream fos = null;
		File input = null;
		File output = null;

		try {
			for (Attachment att : atts) {
				ContentDisposition contDisp = att.getContentDisposition();

				if ("dstMimeType".equals(contDisp.getParameter("name"))) {
					dstMimeType = att.getObject(String.class);
				} else if ("params".equals(contDisp.getParameter("name"))) {
					params = att.getObject(String.class);
				} else if ("content".equals(contDisp.getParameter("name"))) {
					filename = contDisp.getParameter("filename");
					srcMimeTypeIn = MimeTypeConfig.mimeTypes.getContentType(filename.toLowerCase());
					is = att.getDataHandler().getInputStream();
				}
			}

			input = FileUtils.createTempFileFromMime(srcMimeTypeIn);
			fos = new FileOutputStream(input);
			IOUtils.copy(is, fos);
			output = FileUtils.createTempFileFromMime(dstMimeType);
			ImageUtils.ImageMagickConvert(input, output, params);
			final FileInputStream fis = new FileInputStream(output);

			StreamingOutput stream = new StreamingOutput() {
				@Override
				public void write(OutputStream os) throws IOException, WebApplicationException {
					IOUtils.copy(fis, os);
					IOUtils.closeQuietly(fis);
					IOUtils.closeQuietly(os);
				}
			};

			Response.ResponseBuilder response = Response.ok(stream);
			MimeType mt = MimeTypeDAO.findByName(dstMimeType);
			String convertedFile = FileUtils.getFileName(filename) + "." + mt.getExtensions().iterator().next();
			response.header("Content-Disposition", "attachment; filename=\"" + convertedFile + "\"");
			log.debug("imageConvert: [BINARY]");
			return response.build();
		} catch (Exception e) {
			throw new GenericException(e);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(fos);
			FileUtils.deleteQuietly(input);
			FileUtils.deleteQuietly(output);
		}
	}
}
