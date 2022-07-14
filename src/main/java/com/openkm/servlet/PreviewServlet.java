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
package com.openkm.servlet;

import com.openkm.api.OKMAuth;
import com.openkm.api.OKMDocument;
import com.openkm.bean.Document;
import com.openkm.core.*;
import com.openkm.dao.ConfigDAO;
import com.openkm.dao.bean.Profile;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.servlet.admin.BaseServlet;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.DocConverter;
import com.openkm.util.PathUtils;
import com.openkm.util.WebUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;

/**
 * In case of HTML5 Preview, Access-Control-Allow-Origin header is needed.
 * See http://www.html5rocks.com/en/tutorials/cors/
 *
 * @author sochoa
 */
@WebServlet("/Preview")
public class PreviewServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(PreviewServlet.class);
	public static final String DOWNLOAD_TYPE_PREVIEW = "preview";
	public static final String PREVIEWER_PDFJS = "PDF.js";

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		PreviewParams params = new PreviewParams(request, response);

		try {
			if (!params.swfUrl.isEmpty() || !params.pdfUrl.isEmpty()) {
				pdfPreview(params);
			} else if (!params.htmUrl.isEmpty()) {
				html(params);
			} else if (!params.codUrl.isEmpty()) {
				code(params);
			} else if (!params.mpgUrl.isEmpty()) {
				html5player(params);
			} else if (!params.docUrl.isEmpty()) {
				if (params.docUrl.endsWith(".swf") || params.contentType.equals(MimeTypeConfig.MIME_SWF)
						|| params.mimeType.equals(MimeTypeConfig.MIME_SWF)) {
					params.swfUrl = params.docUrl;
					pdfPreview(params);
				} else if (params.docUrl.endsWith(".pdf") || params.contentType.equals(MimeTypeConfig.MIME_PDF)
						|| params.mimeType.equals(MimeTypeConfig.MIME_PDF)) {
					params.pdfUrl = params.docUrl;
					pdfPreview(params);
				} else if (params.docUrl.endsWith(".mp3") || params.contentType.equals(MimeTypeConfig.MIME_MP3)
						|| params.mimeType.equals(MimeTypeConfig.MIME_MP3)) {
					params.mpgUrl = params.docUrl;
					html5player(params);
				} else if (params.docUrl.endsWith(".wav") || params.contentType.equals(MimeTypeConfig.MIME_WAV)
						|| params.mimeType.equals(MimeTypeConfig.MIME_WAV)) {
					params.mpgUrl = params.docUrl;
					html5player(params);
				} else if (params.docUrl.endsWith(".mp4") || params.contentType.equals(MimeTypeConfig.MIME_MP4)
						|| params.mimeType.equals(MimeTypeConfig.MIME_MP4)) {
					params.mpgUrl = params.docUrl;
					html5player(params);
				} else if (params.docUrl.endsWith(".swf") || params.contentType.equals(MimeTypeConfig.MIME_SWF)
						|| params.mimeType.equals(MimeTypeConfig.MIME_SWF)) {
					params.mpgUrl = params.docUrl;
					html5player(params);
				} else if (params.docUrl.endsWith(".html") || params.contentType.equals(MimeTypeConfig.MIME_HTML)
						|| params.mimeType.equals(MimeTypeConfig.MIME_HTML)) {
					params.htmUrl = params.docUrl;
					html(params);
				} else if (DocConverter.validSourceCode.contains(params.contentType) || DocConverter.validSourceCode.contains(params.mimeType)) {
					params.codUrl = params.docUrl;
					code(params);
				} else {
					generatePreviewNotAvailablePdf(request, response);
				}
			} else if (!params.uuid.isEmpty()) {
				Document doc = null;

				if (params.mimeType.isEmpty()) {
					doc = OKMDocument.getInstance().getProperties(null, params.uuid);
					params.mimeType = doc.getMimeType();
				}

				if (params.mimeType.equals(MimeTypeConfig.MIME_SWF)) {
					buildUrl(params, doc);
					pdfPreview(params);
				} else if (params.mimeType.equals(MimeTypeConfig.MIME_PDF)) {
					buildUrl(params, doc);
					pdfPreview(params);
				} else if (params.mimeType.equals(MimeTypeConfig.MIME_MP3)) {
					buildUrl(params, doc);
					html5player(params);
				} else if (params.mimeType.equals(MimeTypeConfig.MIME_WAV)) {
					buildUrl(params, doc);
					html5player(params);
				} else if (params.mimeType.equals(MimeTypeConfig.MIME_MP4)) {
					buildUrl(params, doc);
					html5player(params);
				} else if (params.mimeType.equals(MimeTypeConfig.MIME_SWF)) {
					html5player(params);
				} else if (params.mimeType.equals(MimeTypeConfig.MIME_HTML)) {
					// does not need buildURL it does automatically by internal logic
					html(params);
				} else if (DocConverter.validSourceCode.contains(params.mimeType)) {
					code(params);
				} else if (doc != null && doc.isConvertibleToPdf()) {
					buildUrl(params, doc);
					pdfPreview(params);
				} else {
					generatePreviewNotAvailablePdf(request, response);
				}
			} else {
				generatePreviewNotAvailablePdf(request, response);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	/**
	 * Generate preview not available content. Return a pdf with text instead to raise an error.
	 */
	private void generatePreviewNotAvailablePdf(HttpServletRequest request, HttpServletResponse response) {
		try {
			InputStream is = PreviewServlet.class.getResourceAsStream("frontend/preview_not_available.pdf");
			WebUtils.sendFile(request, response, "preview_not_available.pdf", MimeTypeConfig.MIME_PDF, true, is);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * buildUrl
	 */
	private void buildUrl(PreviewParams params, Document doc) throws DatabaseException, UnsupportedEncodingException {
		String publicUrl = ConfigDAO.getString("application.url", "");

		if (params.mimeType.equals(MimeTypeConfig.MIME_SWF)) {
			publicUrl = publicUrl.replace("/index", "/frontend/Download"); // using frontend download
			publicUrl += "?uuid=" + URLEncoder.encode(doc.getUuid(), "UTF-8") + "&downloadType=" + DOWNLOAD_TYPE_PREVIEW
					+ "&version=" + doc.getActualVersion().getName();
			params.swfUrl = publicUrl;
		} else if (params.mimeType.equals(MimeTypeConfig.MIME_PDF)) {
			publicUrl = publicUrl.replace("/index", "/frontend/Download"); // using frontend download
			publicUrl += "?uuid=" + URLEncoder.encode(doc.getUuid(), "UTF-8") + "&downloadType=" + DOWNLOAD_TYPE_PREVIEW
					+ "&version=" + doc.getActualVersion().getName();
			params.pdfUrl = publicUrl;
		} else if (params.mimeType.equals(MimeTypeConfig.MIME_MP3) || params.mimeType.equals(MimeTypeConfig.MIME_WAV)
				|| params.mimeType.equals(MimeTypeConfig.MIME_MP4)) {
			publicUrl = publicUrl.replace("/index", "/frontend/Download"); // using frontend download
			publicUrl += "?uuid=" + URLEncoder.encode(doc.getUuid(), "UTF-8") + "&downloadType=" + DOWNLOAD_TYPE_PREVIEW
					+ "&version=" + doc.getActualVersion().getName();
			params.mpgUrl = publicUrl;
		} else if (doc.isConvertibleToPdf()) {
			params.mimeType = MimeTypeConfig.MIME_PDF;
			publicUrl = publicUrl.replace("/index", "/frontend/Converter"); // using frontend download
			publicUrl += "?inline=true&toPdf=true&uuid=" + URLEncoder.encode(doc.getUuid(), "UTF-8") + "&downloadType="
					+ DOWNLOAD_TYPE_PREVIEW + "&version=" + doc.getActualVersion().getName();
			params.pdfUrl = publicUrl;
		}
	}

	/**
	 * Get PDF preview. Look into profile to get configured preview
	 */
	private void pdfPreview(PreviewParams params) throws IOException, PrincipalAdapterException {
		pdfjs(params);
	}

	/**
	 * Show PDF.js preview
	 */
	private void pdfjs(PreviewParams params) throws IOException {
		String contextPath = params.request.getContextPath();
		String url = contextPath + "/preview/pdfjs/index.jsp?pdfUrl=" + URLEncoder.encode(params.pdfUrl, "UTF-8");
		params.response.sendRedirect(url);
	}

	/**
	 * Show html preview
	 */
	private void html(PreviewParams params) throws ServletException, IOException {
		String version = WebUtils.getString(params.request, "version");
		InputStream fis = null;
		String content = "";

		try {
			if (!params.htmUrl.isEmpty()) {
				URL url = new URL(params.htmUrl);
				fis = url.openStream();
			} else if (!params.uuid.isEmpty()) {
				if (version.isEmpty()) {
					fis = OKMDocument.getInstance().getContent(null, params.uuid, false);
				} else {
					fis = OKMDocument.getInstance().getContentByVersion(null, params.uuid, version);
				}
			}

			if (fis != null) {
				StringWriter writer = new StringWriter();
				IOUtils.copy(fis, writer, "UTF-8");
				content = writer.getBuffer().toString();
				content = content.replaceAll("jsOpenPathByUuid", "parent.jsOpenPathByUuid");
				content = content.replaceAll("jsDownloadByUuid", "parent.jsDownloadByUuid");
				content = content.replaceAll("href=\"#\">", "href=\"javascript:void(0);\">"); // solve refreshing issue into iframe
			}

			ServletContext sc = getServletContext();
			sc.setAttribute("content", content);
			sc.getRequestDispatcher("/preview/html/index.jsp").forward(params.request, params.response);
		} catch (Exception e) {
			sendErrorRedirect(params.request, params.response, e);
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}

	/**
	 * Source code preview
	 */
	private void code(PreviewParams params) throws ServletException, IOException {
		String version = WebUtils.getString(params.request, "version");
		InputStream fis = null;
		String content = "";
		String markup = "";

		try {
			if (!params.codUrl.isEmpty()) {
				URL url = new URL(params.codUrl);
				fis = url.openStream();
			} else if (!params.uuid.isEmpty()) {
				if (version.isEmpty()) {
					fis = OKMDocument.getInstance().getContent(null, params.uuid, false);
				} else {
					fis = OKMDocument.getInstance().getContentByVersion(null, params.uuid, version);
				}
			}

			if (fis != null) {
				content = IOUtils.toString(fis);
			}

			if (params.mimeType.equals(MimeTypeConfig.MIME_HTML) || params.mimeType.equals(MimeTypeConfig.MIME_TEXT)) {
				markup = "no-highlight";
			} else {
				markup = "auto";
			}

			ServletContext sc = getServletContext();
			sc.setAttribute("markup", markup);
			sc.setAttribute("content", StringEscapeUtils.escapeHtml(content));
			sc.getRequestDispatcher("/preview/code/index.jsp").forward(params.request, params.response);
		} catch (Exception e) {
			sendErrorRedirect(params.request, params.response, e);
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}

	/**
	 * Show mp3, wav, mp4 preview
	 */
	private void html5player(PreviewParams params) throws ServletException, IOException {
		String width = WebUtils.getString(params.request, "width", "100%");
		String height = WebUtils.getString(params.request, "height", "100%");
		String mediaProvider = "";

		if (params.mimeType.equals(MimeTypeConfig.MIME_MP3) || params.mimeType.equals(MimeTypeConfig.MIME_WAV)) {
			mediaProvider = "sound";
		} else if (params.mimeType.equals(MimeTypeConfig.MIME_FLV) || params.mimeType.equals(MimeTypeConfig.MIME_MP4)) {
			mediaProvider = "video";
		} else if (params.mimeType.equals(MimeTypeConfig.MIME_SWF)) {
			mediaProvider = "";
		} else if (params.mpgUrl.endsWith(".mp3") || params.mpgUrl.endsWith(".wav")) {
			mediaProvider = "sound";
		} else if (params.mpgUrl.endsWith(".mp4")) {
			mediaProvider = "video";
		}

		ServletContext sc = getServletContext();
		sc.setAttribute("mediaUrl", params.mpgUrl);
		sc.setAttribute("mediaProvider", mediaProvider);
		sc.setAttribute("width", width);
		sc.setAttribute("height", height);
		sc.getRequestDispatcher("/preview/html5player/index.jsp").forward(params.request, params.response);
	}

	/**
	 * Inner class for parameters
	 */
	private static class PreviewParams {
		public HttpServletRequest request;
		public HttpServletResponse response;
		public boolean buildUrl = false;
		public String previewEngine;
		public String contentType;
		public String mimeType;
		public String docUrl;
		public String swfUrl;
		public String pdfUrl;
		public String htmUrl;
		public String codUrl;
		public String mpgUrl;
		public String jsUrl;
		public String uuid;

		public PreviewParams(HttpServletRequest request, HttpServletResponse response) {
			this.request = request;
			this.response = response;
			this.previewEngine = WebUtils.getString(request, "previewEngine");
			this.contentType = WebUtils.getHeader(request, "Content-Type");
			this.buildUrl = WebUtils.getBoolean(request, "buildUrl");
			this.mimeType = WebUtils.getString(request, "mimeType");
			this.docUrl = WebUtils.getString(request, "docUrl");
			this.swfUrl = WebUtils.getString(request, "swfUrl");
			this.pdfUrl = WebUtils.getString(request, "pdfUrl");
			this.htmUrl = WebUtils.getString(request, "htmUrl");
			this.codUrl = WebUtils.getString(request, "codUrl");
			this.mpgUrl = WebUtils.getString(request, "mpgUrl");
			this.jsUrl = WebUtils.getString(request, "jsUrl");
			this.uuid = WebUtils.getString(request, "uuid");
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			sb.append("previewEngine=").append(previewEngine);
			sb.append(", contentType=").append(contentType);
			sb.append(", mimeType=").append(mimeType);
			sb.append(", buildUrl=").append(buildUrl);
			sb.append(", docUrl=").append(docUrl);
			sb.append(", swfUrl=").append(swfUrl);
			sb.append(", pdfUrl=").append(pdfUrl);
			sb.append(", htmUrl=").append(htmUrl);
			sb.append(", codUrl=").append(codUrl);
			sb.append(", mpgUrl=").append(mpgUrl);
			sb.append(", uuid=").append(uuid);
			sb.append("}");
			return sb.toString();
		}
	}
}
