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

package com.openkm.frontend.client.widget.util;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.dashboard.keymap.TagCloud;

import java.util.Collection;

/**
 * WidgetUtil
 *
 * @author jllort
 */
public class WidgetUtil {

	/**
	 * Draws a tag cloud
	 */
	public static void drawTagCloud(final TagCloud keywordsCloud, Collection<String> keywords) {
		// Deletes all tag clouds keys
		keywordsCloud.clear();
		keywordsCloud.setMinFrequency(Main.get().mainPanel.dashboard.keyMapDashboard.getTotalMinFrequency());
		keywordsCloud.setMaxFrequency(Main.get().mainPanel.dashboard.keyMapDashboard.getTotalMaxFrequency());

		for (String keyword : keywords) {
			HTML tagKey = new HTML(keyword);
			tagKey.setStyleName("okm-cloudTags");
			Style linkStyle = tagKey.getElement().getStyle();
			int fontSize = keywordsCloud.getLabelSize(Main.get().mainPanel.dashboard.keyMapDashboard.getKeywordRate(keyword));
			linkStyle.setProperty("fontSize", fontSize + "pt");
			linkStyle.setProperty("color", keywordsCloud.getColor(fontSize));

			if (fontSize > 0) {
				linkStyle.setProperty("top", (keywordsCloud.getMaxFontSize() - fontSize) / 2 + "px");
			}

			keywordsCloud.add(tagKey);
		}
	}

	/**
	 * getAttachmentWidget
	 */
	public static Button getAttachmentWidget(final GWTDocument attach, final AttachmentHandler handler) {
		String html = Util.mimeImageHTML(attach.getMimeType()) + "<span>" + attach.getName() + " (" + Util.formatSize(attach.getActualVersion().getSize()) + ")</span>";
		final Button button = new Button();
		button.setHTML(html);
		button.setTitle(attach.getName());
		button.setStyleName("okm-Button-Mail");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Util.downloadFileByUUID(attach.getUuid(), "");
			}
		});
		button.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				handler.onAttachmentMouseDown(event, attach, button);
			}
		});
		return button;
	}

	/**
	 * getMailWidget
	 */
	public static Button getMailWidget(final String mail) {
		final Button button = new Button();
		final String mailTo = mail.contains("<") ? mail.substring(mail.indexOf("<") + 1, mail.indexOf(">")).trim() : mail.trim();
		String mailHTML = mail.trim().replace("<", "&lt;").replace(">", "&gt;");
		button.setHTML(mailHTML);
		button.setTitle(mail.trim());
		button.setStyleName("okm-Button-Mail");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("mailto:" + mailTo, "_blank", "");
			}
		});
		return button;
	}

	/**
	 * getSpace
	 */
	public static HTML getSpace() {
		HTML html = Util.hSpace("2px");
		html.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		return html;
	}
}
