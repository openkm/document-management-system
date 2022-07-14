/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017  Paco Avila & Josep Llort
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

package com.openkm.frontend.client.widget.properties;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTWorkspace;
import com.openkm.frontend.client.constants.service.RPCService;
import com.openkm.frontend.client.extension.widget.preview.PreviewExtension;
import com.openkm.frontend.client.util.Util;

/**
 * Notes
 *
 * @author jllort
 */
public class Preview extends Composite {
	public static final String DOWNLOAD_TYPE_PREVIEW = "preview";

	private VerticalPanel vPanel;
	private HTML pdf;
	private HTML swf;
	public EmbeddedPreview embeddedPreview;
	private int width = 0;
	private int height = 0;
	private boolean previewAvailable = false;
	private List<PreviewExtension> widgetPreviewExtensionList;
	private String pdfContainer = "pdfembededcontainer";
	private String flashContainer = "pdfviewercontainer";
	private String pdfID = "jsPdfViewer";

	/**
	 * Preview
	 */
	public Preview() {
		widgetPreviewExtensionList = new ArrayList<>();
		vPanel = new VerticalPanel();
		embeddedPreview = new EmbeddedPreview();
		pdf = new HTML("<div id=\"" + pdfContainer + "\"></div>\n");
		swf = new HTML("<div id=\"" + flashContainer + "\"></div>\n");
		initWidget(vPanel);
	}

	@Override
	public void setPixelSize(int width, int height) {
		super.setPixelSize(width, height);
		this.width = width;
		this.height = height;
		embeddedPreview.setPixelSize(width, height);
	}

	/**
	 * showEmbedPDF
	 *
	 * @param uuid Unique document ID to be previewed.
	 */
	public void showEmbedPDF(String uuid) {
		hideWidgetExtension();
		vPanel.clear();

		vPanel.add(pdf);
		vPanel.setCellHorizontalAlignment(pdf, HasAlignment.ALIGN_DEFAULT);
		vPanel.setCellVerticalAlignment(pdf, HasAlignment.ALIGN_MIDDLE);

		if (previewAvailable) {
			String url = RPCService.ConverterServlet + "?inline=true&toPdf=true&uuid=" + URL.encodeQueryString(uuid)
					+ "&downloadType=" + DOWNLOAD_TYPE_PREVIEW;
			pdf.setHTML("<div id=\"" + flashContainer + "\">" +
					"<object id=\"" + pdfID + "\" name=\"" + pdfID + "\" width=\"" + width + "\" height=\"" + height + "\" type=\"application/pdf\" data=\"" + url + "\"&#zoom=85&scrollbar=1&toolbar=1&navpanes=1&view=FitH\">" +
					"<p>Browser plugin support error, PDF can not be displayed</p>" +
					"</object>" +
					"</div>\n"); // needed for rewriting  purpose
		} else {
			pdf.setHTML("<div id=\"" + flashContainer + "\" align=\"center\"><br><br>" + Main.i18n("preview.unavailable") + "</div>\n");
		}
	}

	/**
	 * showSystemEmbeddedPreview
	 */
	public void showSystemEmbeddedPreview(String url) {
		hideWidgetExtension();
		vPanel.clear();

		vPanel.add(embeddedPreview);
		vPanel.setCellHorizontalAlignment(embeddedPreview, HasAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(embeddedPreview, HasAlignment.ALIGN_MIDDLE);

		embeddedPreview.showEmbedded(url);
	}

	/**
	 * cleanPreview
	 */
	public void cleanPreview() {
		swf.setHTML("<div id=\"pdfviewercontainer\" ></div>\n");
		embeddedPreview.clear();
	}

	/**
	 * setPreviewExtension
	 */
	public void showPreviewExtension(PreviewExtension preview, String url, GWTDocument doc) {
		hideWidgetExtension();
		vPanel.clear();

		if (previewAvailable) {
			preview.createViewer(doc, url, width, height);
			vPanel.add(preview.getWidget());
		}
	}

	/**
	 * hideWidgetExtension
	 */
	private void hideWidgetExtension() {
		if (vPanel.getWidgetCount() > 4) {
			for (int i = 3; i < vPanel.getWidgetCount(); i++) {
				vPanel.getWidget(i).setVisible(false);
			}
		}
	}

	/**
	 * Sets the boolean value if previewing document is available
	 *
	 * @param doc Set preview availability status.
	 */
	public void setPreviewAvailable(GWTDocument doc) {
		if (doc.getMimeType().equals("video/x-flv") || doc.getMimeType().equals("video/mp4") || doc.getMimeType().equals("audio/mpeg")
				|| doc.getMimeType().equals("audio/x-wav") || doc.getMimeType().equals("application/pdf")
				|| doc.getMimeType().equals("application/postscript") || doc.getMimeType().equals("application/x-shockwave-flash")
				|| isHTMLPreviewAvailable(doc.getMimeType())
				|| isSyntaxHighlighterPreviewAvailable(doc.getMimeType())
				|| doc.isConvertibleToSwf() || doc.isConvertibleToPdf()) {
			previewAvailable = true;
		} else {
			boolean found = false;
			for (PreviewExtension preview : widgetPreviewExtensionList) {
				if (preview.isPreviewAvailable(doc.getMimeType())) {
					found = true;
					break;
				}
			}
			previewAvailable = found;
		}
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		if (!previewAvailable) {
			swf.setHTML("<div id=\"pdfviewercontainer\" align=\"center\"><br><br>" + Main.i18n("preview.unavailable")
					+ "</div>\n"); // needed for rewriting purpose
		}
	}

	/**
	 * addPreviewExtension
	 */
	public void addPreviewExtension(PreviewExtension extension) {
		widgetPreviewExtensionList.add(extension);
	}

	/**
	 * previewDocument
	 */
	public void previewDocument(boolean refreshing, GWTDocument doc) {
		Log.debug("PreviewDocument: " + doc.getPath());

		if (doc.getMimeType().equals("video/x-flv") || doc.getMimeType().equals("video/mp4") || doc.getMimeType().equals("audio/mpeg")
				|| doc.getMimeType().equals("audio/x-wav")) {
			Log.debug("Preview: Media Player");
			String url = RPCService.DownloadServlet + "?uuid=" + URL.encodeQueryString(doc.getUuid()) + "&downloadType="
					+ DOWNLOAD_TYPE_PREVIEW;
			showSystemEmbeddedPreview(EmbeddedPreview.MPG_URL + URL.encodeQueryString(url) + "&mimeType=" + doc.getMimeType()
					+ "&width=" + width + "&height=" + height);
		} else if (isHTMLPreviewAvailable(doc.getMimeType())) {
			Log.debug("Preview: HTML");
			if (!refreshing) {
				String url = "mimeType=" + doc.getMimeType() + "&uuid=" + doc.getUuid();
				showSystemEmbeddedPreview(url);
			}
		} else if (isSyntaxHighlighterPreviewAvailable(doc.getMimeType())) {
			Log.debug("Preview: Syntax Highlighter");
			if (!refreshing) {
				String url = "mimeType=" + doc.getMimeType() + "&uuid=" + doc.getUuid();
				showSystemEmbeddedPreview(url);
			}
		} else if (doc.getMimeType().equals("application/x-shockwave-flash")) {
			String url = RPCService.ConverterServlet + "?inline=true&toSwf=true&uuid=" + URL.encodeQueryString(doc.getUuid()) + "&downloadType="
					+ DOWNLOAD_TYPE_PREVIEW;
			showSystemEmbeddedPreview(EmbeddedPreview.SWF_URL + URL.encodeQueryString(url) + "&uuid=" + URL.encodeQueryString(doc.getUuid()));
		} else {
			if (refreshing) {
				if (Main.get().workspaceUserProperties.getWorkspace().isAcrobatPluginPreview()) {
					Util.resizeEmbededPDF("" + width, "" + height, pdfID);
				}
			} else {
				showEmbedPDF(doc.getUuid());
			}
		}
	}

	/**
	 * isHTMLPreviewAvailable
	 */
	public static boolean isHTMLPreviewAvailable(String mime) {
		return mime.equals("text/html");
	}

	/**
	 * isPreviewAvailable
	 */
	public static boolean isSyntaxHighlighterPreviewAvailable(String mime) {
		return mime.equals("text/x-java") || mime.equals("text/xml") || mime.equals("text/x-sql")
				|| mime.equals("text/x-scala") || mime.equals("text/x-python")
				|| mime.equals("application/x-php") || mime.equals("application/x-bsh")
				|| mime.equals("application/x-perl") || mime.equals("application/javascript")
				|| mime.equals("text/plain") || mime.equals("text/x-groovy") || mime.equals("text/x-diff")
				|| mime.equals("text/x-pascal") || mime.equals("text/css") || mime.equals("text/x-csharp")
				|| mime.equals("text/x-c++") || mime.equals("application/x-font-truetype")
				|| mime.equals("text/applescript") || mime.equals("application/x-shellscript");
	}
}
