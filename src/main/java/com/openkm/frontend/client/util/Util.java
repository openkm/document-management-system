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

package com.openkm.frontend.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.constants.service.RPCService;
import com.openkm.frontend.client.service.OKMGeneralService;
import com.openkm.frontend.client.service.OKMGeneralServiceAsync;

import java.util.List;
import java.util.Map;

/**
 * Util
 *
 * @author jllort
 */
public class Util {
	private static final OKMGeneralServiceAsync generalService = (OKMGeneralServiceAsync) GWT.create(OKMGeneralService.class);

	/**
	 * Generates HTML for item with an attached icon.
	 *
	 * @param imageUrl the url of the icon image
	 * @param title    the title of the item
	 * @return the resultant HTML
	 */
	public static String imageItemHTML(String imageUrl, String title) {
		return "<span style='text-align:left; margin-right:4px;'><img align=\"absmidle\" style='margin-right:4px; white-space:nowrap;' src='"
				+ imageUrl.toLowerCase() + "'>" + title + "</span>";
	}

	/**
	 * Generates HTML for item with an attached icon.
	 *
	 * @param imageUrl the url of the icon image
	 * @param title    the title of the item
	 * @return the resultant HTML
	 */
	public static String imageItemHTML(String imageUrl, String title, String align) {
		return "<span style='text-align:left; margin-right:4px;'><img align=\"" + align
				+ "\" style='margin-right:4px; white-space:nowrap;' src='" + imageUrl.toLowerCase() + "'>" + title + "</span>";
	}

	/**
	 * Generates HTML for item with an attached icon.
	 *
	 * @param imageUrl the url of the icon image
	 * @return the resultant HTML
	 */
	public static String imageItemHTML(String imageUrl) {
		return "<img align=\"absmidle\" style='margin-right:4px' src='" + imageUrl.toLowerCase() + "'>";
	}

	/**
	 * Generates HTML image code with style.
	 *
	 * @param imageUrl the url of the icon image
	 * @param alt      image alt
	 * @param style    the style of the image
	 * @return the resultant HTML
	 */
	public static String imageHTML(String imageUrl, String alt, String style) {
		if (!style.equals("")) {
			return "<img align=\"absmidle\"" + style + " src='" + imageUrl.toLowerCase() + "'>";
		} else {
			return imageHTML(imageUrl, alt);
		}
	}

	/**
	 * Generates HTML image code with style.
	 *
	 * @param imageUrl the url of the icon image
	 * @param alt      the image alt
	 * @return the resultant HTML
	 */
	public static String imageHTML(String imageUrl, String alt) {
		return "<img border=\"0\" align=\"absmidle\" alt=\"" + alt + "\" title=\"" + alt + "\" src='" + imageUrl.toLowerCase() + "'>";
	}

	/**
	 * Generates HTML image code with style.
	 *
	 * @param imageUrl the url of the icon image
	 * @return the resultant HTML
	 */
	public static String imageHTML(String imageUrl) {
		return imageHTML(imageUrl, "");
	}

	/**
	 * Generate HTML icon for mime-type document
	 *
	 * @param mime The document mime-type
	 * @return the html image of mime-type file
	 */
	public static String mimeImageHTML(String mime) {
		return "<img align=\"absmidle\" style=\"margin-right:4px\" src=\"" + Main.CONTEXT + "/mime/" + mime + "\"'>";
	}

	/**
	 * Return the menu html value
	 *
	 * @param imageUrl The image url
	 * @param text     The text value
	 */
	public static String flagMenuHTML(String flag, String text) {
		return "<img style='margin-right:8px; margin-left:2px; vertical-align:middle;' " + "src=\"" + Main.CONTEXT + "/flag/" + flag
				+ "\"'>" + text;
	}

	/**
	 * Return the menu html value
	 *
	 * @param imageUrl The image url
	 * @param text     The text value
	 */
	public static String menuHTML(String imageUrl, String text) {
		return "<img style='margin-right:8px; margin-left:2px; vertical-align:middle;' src='" + imageUrl + "'>" + text;
	}

	/**
	 * Return the menu html value
	 *
	 * @param text The text value
	 */
	public static String menuHTMLWithouIcon(String text) {
		return "<span style='margin-left:24px; vertical-align:middle;'>" + text + "</span>";
	}

	/**
	 * Creates an HTML fragment that places an image & caption together, for use
	 * in a group header.
	 *
	 * @param imageUrl the url of the icon image to be used
	 * @param caption  the group caption
	 * @return the header HTML fragment
	 */
	public static String createHeaderHTML(String imageUrl, String caption) {
		return "<table align='left'><tr>" + "<td><img src='" + imageUrl + "'></td>"
				+ "<td style='vertical-align:middle'><b style='white-space:nowrap; cursor:default;'>" + caption + "</b></td>"
				+ "</tr></table>";
	}

	/**
	 * Creates an horizontal spacer
	 *
	 * @param width The desired width space
	 * @return an HTML element meaning the with
	 */
	public static HTML hSpace(String width) {
		HTML spacer = new HTML("");
		spacer.setWidth(width);
		return spacer;
	}

	/**
	 * Creates an vertical spacer
	 *
	 * @param height The desired height space
	 * @return an HTML element meaning the height
	 */
	public static HTML vSpace(String height) {
		HTML spacer = new HTML("");
		spacer.setHeight(height);
		return spacer;
	}

	/**
	 * Creates an square spacer
	 *
	 * @param width  The desired width space
	 * @param height The desired height space
	 * @return an HTML element meaning the with and height
	 */
	public static HTML space(String width, String height) {
		HTML spacer = new HTML("");
		spacer.setWidth(width);
		spacer.setHeight(height);
		return spacer;
	}

	/**
	 * Creates an HTML to opens a url with text on a new window
	 *
	 * @param text The text url description
	 * @param uri  The url to open
	 */
	public static String windowOpen(String text, String uri) {
		return "<span onclick=\"javascript:window.open('" + uri + "')\">" + text + "</span>";
	}

	/**
	 * Download file by UUID
	 */
	public static void downloadFileByUUID(String uuid, String params) {
		if (!params.equals("") && !params.endsWith("&")) {
			params += "&";
		}

		final Element downloadIframe = RootPanel.get("__download").getElement();
		String url = RPCService.DownloadServlet + "?" + params + "uuid=" + URL.encodeQueryString(uuid);
		DOM.setElementAttribute(downloadIframe, "src", url);
	}

	/**
	 * Download file by path
	 */
	@Deprecated
	public static void downloadFile(String path, String params) {
		if (!params.equals("") && !params.endsWith("&")) {
			params += "&";
		}

		final Element downloadIframe = RootPanel.get("__download").getElement();
		String url = RPCService.DownloadServlet + "?" + params + "path=" + URL.encodeQueryString(path);
		DOM.setElementAttribute(downloadIframe, "src", url);
	}

	/**
	 * downloadFilesByUUID
	 */
	public static void downloadFilesByUUID(List<String> uuidList, String params) {
		if (!params.equals("")) {
			params = "&" + params;
		}

		final Element downloadIframe = RootPanel.get("__download").getElement();
		String url = RPCService.DownloadServlet + "?export" + params;

		for (String uuid : uuidList) {
			url += "&uuidList=" + URL.encodeQueryString(uuid);
		}

		DOM.setElementAttribute(downloadIframe, "src", url);
	}

	/**
	 * Download files exported as zip
	 *
	 * @author danilo
	 */
	@Deprecated
	public static void downloadFiles(List<String> path, String params) {
		if (!params.equals("")) {
			params = "&" + params;
		}

		final Element downloadIframe = RootPanel.get("__download").getElement();
		String url = RPCService.DownloadServlet + "?export" + params;

		for (String p : path) {
			url += "&pathList=" + URL.encodeQueryString(p);
		}

		DOM.setElementAttribute(downloadIframe, "src", url);
	}

	/**
	 * Download file
	 */
	public static void downloadFilePdf(String uuid) {
		final Element downloadIframe = RootPanel.get("__download").getElement();
		String url = RPCService.ConverterServlet + "?inline=false&toPdf=true&uuid=" + URL.encodeQueryString(uuid);
		DOM.setElementAttribute(downloadIframe, "src", url);
		Main.get().conversionStatus.getStatus();
	}

	/**
	 * executeReport
	 */
	public static void executeReport(long id, Map<String, String> params) {
		String parameters = "";

		if (!params.isEmpty()) {
			for (String key : params.keySet()) {
				parameters += "&" + key + "=" + params.get(key);
			}
		}

		final Element downloadIframe = RootPanel.get("__download").getElement();
		String url = RPCService.ReportServlet + "?" + "id=" + id + parameters;
		DOM.setElementAttribute(downloadIframe, "src", url);
	}

	/**
	 * print file
	 */
	public static void print(String uuid) {
		final Element printIframe = RootPanel.get("__print").getElement();
		String url = RPCService.ConverterServlet + "?inline=true&print=true&toPdf=true&uuid=" + URL.encodeQueryString(uuid);
		DOM.setElementAttribute(printIframe, "src", url);
	}

	/**
	 * Download CSV file
	 */
	public static void downloadCSVFile(String params) {
		final Element downloadIframe = RootPanel.get("__download").getElement();
		String url = RPCService.CSVExporterServlet + "?" + params;
		DOM.setElementAttribute(downloadIframe, "src", url);
	}

	/**
	 * markHTMLTextAsBold
	 */
	public static String getTextAsBoldHTML(String text, boolean mark) {
		if (mark) {
			return "<b>" + text + "</b>";
		} else {
			return text;
		}
	}

	/**
	 * Get parent item path from path.
	 *
	 * @param path The complete item path.
	 * @return The parent item path.
	 */
	public static String getParent(String path) {
		int lastSlash = path.lastIndexOf('/');
		String ret = (lastSlash > 0) ? path.substring(0, lastSlash) : "";
		return ret;
	}

	/**
	 * Get item name from path.
	 *
	 * @param path The complete item path.
	 * @return The name of the item.
	 */
	public static String getName(String path) {
		String ret = path.substring(path.lastIndexOf('/') + 1);
		return ret;
	}

	/**
	 * Encode path elements
	 */
	public static String encodePathElements(String path) {
		String[] eltos = path.split("\\/");
		String ret = "";

		for (int i = 1; i < eltos.length; i++) {
			ret = ret.concat("/").concat(URL.encodePathSegment(eltos[i]));
		}

		return ret;
	}

	/**
	 * Generate selectable widget text
	 */
	public static HTML createSelectable(String html) {
		HTML widget = new HTML(html);
		widget.addStyleName("okm-EnableSelect");
		return widget;
	}

	/**
	 * isRoot
	 */
	public static boolean isRoot(String fldPath) {
		boolean isRoot = false;

		if (Main.get().workspaceUserProperties.getWorkspace().isStackTaxonomy()) {
			isRoot = isRoot || Main.get().taxonomyRootFolder.getPath().equals(fldPath);
		}

		if (Main.get().workspaceUserProperties.getWorkspace().isStackCategoriesVisible()) {
			isRoot = isRoot || Main.get().categoriesRootFolder.getPath().equals(fldPath);
		}

		if (Main.get().workspaceUserProperties.getWorkspace().isStackThesaurusVisible()) {
			isRoot = isRoot || Main.get().thesaurusRootFolder.getPath().equals(fldPath);
		}

		if (Main.get().workspaceUserProperties.getWorkspace().isStackTemplatesVisible()) {
			isRoot = isRoot || Main.get().templatesRootFolder.getPath().equals(fldPath);
		}

		if (Main.get().workspaceUserProperties.getWorkspace().isStackPersonalVisible()) {
			isRoot = isRoot || Main.get().personalRootFolder.getPath().equals(fldPath);
		}

		if (Main.get().workspaceUserProperties.getWorkspace().isStackMailVisible()) {
			isRoot = isRoot || Main.get().mailRootFolder.getPath().equals(fldPath);
		}

		if (Main.get().workspaceUserProperties.getWorkspace().isStackTrashVisible()) {
			isRoot = isRoot || Main.get().trashRootFolder.getPath().equals(fldPath);
		}

		return isRoot;
	}

	/**
	 * isSearchableKey
	 */
	public static boolean isSearchableKey(KeyUpEvent event) {
		return (!EventUtils.isNavigationKey(event.getNativeKeyCode()) && !EventUtils.isModifierKey(event.getNativeKeyCode())
				&& !EventUtils.isArrowKey(event.getNativeKeyCode()));
	}

	/**
	 * Show only mail name
	 */
	public static String showMailName(String mail) {
		if (mail.startsWith("\"")) {
			return mail.substring(1, mail.indexOf("\"", 1));
		} else {
			return mail.replaceFirst("<", "&lt;").replaceAll(">", "&gt;");
		}
	}

	/**
	 * Change on fly the actual css
	 *
	 * @param title The css name
	 */
	public static void changeCss(String title) {
		if (title.equals("bigfont")) {
			Main.get().mainPanel.desktop.navigator.setSkinExtrStackSize(1);
			Main.get().mainPanel.search.historySearch.setSkinExtrStackSize(1);
		} else {
			Main.get().mainPanel.desktop.navigator.setSkinExtrStackSize(0);
			Main.get().mainPanel.search.historySearch.setSkinExtrStackSize(0);
		}

		browserChangeCss(title);
		Main.get().mainPanel.stylesChanged();
	}

	/**
	 * Change on fly the actual css
	 *
	 * @param title The css name
	 */
	public static native void browserChangeCss(String title) /*-{
      new $wnd.changeCss(title);
    }-*/;

	/**
	 * printFile
	 */
	public static native void printFile() /*-{
      new $wnd.printFile();
    }-*/;

	/**
	 * Format file size in Bytes, KBytes or MBytes.
	 *
	 * @param size The file size in bytes.
	 * @return The formated file size.
	 */
	public static native String formatSize(double size) /*-{
      if (size / 1024 < 1) {
        str = size + " Bytes";
      } else if (size / 1048576 < 1) {
        str = (size / 1024).toFixed(1) + " KB";
      } else if (size / 1073741824 < 1) {
        str = (size / 1048576).toFixed(1) + " MB";
      } else if (size / 1099511627776 < 1) {
        str = (size / 1073741824).toFixed(1) + " GB";
      } else {
        str = "BIG";
      }

      return str;
    }-*/;

	/**
	 * Get browser language
	 *
	 * @return The language in ISO 639 format.
	 */
	public static native String getBrowserLanguage() /*-{
      var lang = navigator.language ? navigator.language : navigator.userLanguage;

      if (lang) {
        return lang;
      } else {
        return "en";
      }
    }-*/;

	/**
	 * returns 'opera', 'safari', 'ie6', 'ie7', 'ie8', 'ie9', 'gecko' or 'unknown'.
	 */
	public static native String getUserAgent() /*-{
      try {
        if (window.opera) return 'opera';
        var ua = navigator.userAgent.toLowerCase();
        if (ua.indexOf('chrome') != -1) return 'chrome';
        if (ua.indexOf('webkit') != -1) return 'safari';
        if (ua.indexOf('msie 6.0') != -1) return 'ie6';
        if (ua.indexOf('msie 7.0') != -1) return 'ie7';
        if (ua.indexOf('msie 8.0') != -1) return 'ie8';
        if (ua.indexOf('msie 9.0') != -1) return 'ie9';
        if (ua.indexOf('gecko') != -1) return 'gecko';
        if (ua.indexOf('opera') != -1) return 'opera';
        return 'unknown';
      } catch (e) {
        return 'unknown'
      }
    }-*/;

	public static native void removeMediaPlayer() /*-{
      $wnd.swfobject.removeSWF("jsmediaplayer");
    }-*/;

	public static native void createMediaPlayer(String mediaUrl, String mediaProvider, String width, String height) /*-{
      $wnd.swfobject.embedSWF("../js/mediaplayer/player.swf", "mediaplayercontainer", width, height, "9.0.0", "../js/mediaplayer/expressinstall.swf", {
        file: mediaUrl,
        provider: mediaProvider,
        autostart: "true",
        width: width,
        height: height
      }, {allowscriptaccess: "always", allowfullscreen: "true"}, {id: "jsmediaplayer", name: "jsmediaplayer"});
    }-*/;

	public static native void resizeMediaPlayer(String width, String height) /*-{
      obj = $wnd.swfobject.getObjectById('jsmediaplayer');
      obj.width = width;
      obj.height = height;
    }-*/;

	public static native void createSwfViewer(String swfUrl, String width, String height) /*-{
      $wnd.swfobject.embedSWF(swfUrl, "swfviewercontainer", width, height, "9.0.0", "../js/mediaplayer/expressinstall.swf", {
        width: width,
        height: height
      }, {}, {id: "jswfviewer", name: "jswfviewer"});
    }-*/;

	public static native void resizeSwfViewer(String width, String height) /*-{
      obj = $wnd.swfobject.getObjectById('jswfviewer');
      obj.width = width;
      obj.height = height;
    }-*/;

	public static native void createPDFViewerFlexPaper(String pdfUrl, String width, String height) /*-{
      fpViewer = "../js/flexpaper/FlexPaperViewer.swf";
      pdfUrl = encodeURIComponent(pdfUrl);
      $wnd.swfobject.embedSWF(fpViewer, "pdfviewercontainer", width, height, "10.0.0", "playerProductInstall.swf",
        {
          SwfFile: pdfUrl,
          Scale: 0.6,
          ZoomTransition: "easeOut",
          ZoomTime: 0.5,
          ZoomInterval: 0.1,
          FitPageOnLoad: false,
          FitWidthOnLoad: true,
          FullScreenAsMaxWindow: false,
          ProgressiveLoading: true,
          ViewModeToolsVisible: true,
          ZoomToolsVisible: true,
          FullScreenVisible: true,
          NavToolsVisible: true,
          CursorToolsVisible: true,
          SearchToolsVisible: true,
          localeChain: "en_US"
        },
        {
          quality: "high",
          bgcolor: "#ffffff",
          allowscriptaccess: "sameDomain",
          allowfullscreen: "true"
        },
        {
          id: "FlexPaperViewer",
          name: "FlexPaperViewer"
        });
    }-*/;

	public static native void resizePDFViewerFlexPaper(String width, String height) /*-{
      obj = $wnd.swfobject.getObjectById('FlexPaperViewer');
      obj.width = width;
      obj.height = height;
    }-*/;

	public static native void resizeEmbededPDF(String width, String height, String pdfId) /*-{
      obj = $wnd.document.getElementById(pdfId);
      obj.width = width;
      obj.height = height;
    }-*/;

	public static native void copyToClipboard(String text) /*-{
      var elto = $doc.createElement('DIV');
      elto.textContent = text;
      $doc.body.appendChild(elto);

      if ($doc.selection) {
        var range = $doc.body.createTextRange();
        range.moveToElementText(elto);
        range.select();
      } else if ($wnd.getSelection) {
        var range = $doc.createRange();
        range.selectNode(elto);
        $wnd.getSelection().removeAllRanges();
        $wnd.getSelection().addRange(range);
      }

      $doc.execCommand('copy');
      elto.remove();
    }-*/;

	public static native String escape(String text) /*-{
      return escape(text);
    }-*/;

	public static native void consoleLog(String message) /*-{
      console.log(message);
    }-*/;
}