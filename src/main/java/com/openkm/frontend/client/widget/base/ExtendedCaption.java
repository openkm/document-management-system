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

package com.openkm.frontend.client.widget.base;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.DialogBox.Caption;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.base.handler.CaptionHandler;
import com.openkm.frontend.client.widget.base.hashandler.HasCaptionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * ExtendedCaption
 *
 * @author sochoa
 */
public class ExtendedCaption extends HorizontalPanel implements Caption, HasCaptionHandler {
	private HTML title = new HTML("");
	private HTML space = new HTML("");
	private Image minimize = new Image(OKMBundleResources.INSTANCE.buttonMinimize());
	private HTML space1 = Util.hSpace("2px");
	private Image maximize = new Image(OKMBundleResources.INSTANCE.buttonMaximize());
	private HTML space2 = Util.hSpace("2px");
	private Image close = new Image(OKMBundleResources.INSTANCE.buttonClose());
	private List<CaptionHandler> captionHandlers = new ArrayList<>();

	/**
	 * ExtendedCaption
	 */
	public ExtendedCaption(boolean isTitle, boolean isMinimize, boolean isMaximize, boolean isClose) {
		super();
		if (isTitle) {
			add(title);
			setCellHorizontalAlignment(title, HasAlignment.ALIGN_CENTER);
			setCellVerticalAlignment(title, HasAlignment.ALIGN_MIDDLE);
		} else {
			add(new HTML(""));
		}
		if (isMinimize) {
			add(minimize);
			minimize.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					for (CaptionHandler captionHandler : captionHandlers) {
						captionHandler.onMinimize();
					}
				}
			});
			setCellWidth(minimize, "20");
			minimize.setStyleName("okm-Hyperlink");
			setCellHorizontalAlignment(minimize, HasAlignment.ALIGN_CENTER);
			setCellVerticalAlignment(minimize, HasAlignment.ALIGN_MIDDLE);
			add(space1);
			setCellWidth(space1, "2");
		}
		if (isMaximize) {
			add(maximize);
			maximize.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					for (CaptionHandler captionHandler : captionHandlers) {
						captionHandler.onMinimize();
					}
				}
			});
			setCellWidth(maximize, "20");
			maximize.setStyleName("okm-Hyperlink");
			setCellHorizontalAlignment(maximize, HasAlignment.ALIGN_CENTER);
			setCellVerticalAlignment(maximize, HasAlignment.ALIGN_MIDDLE);
			add(space2);
			setCellWidth(space2, "2");
		}
		if (isClose) {
			add(close);
			close.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					for (CaptionHandler captionHandler : captionHandlers) {
						captionHandler.onClose();
					}
				}
			});
			setCellWidth(close, "20");
			close.setStyleName("okm-Hyperlink");
			setCellHorizontalAlignment(close, HasAlignment.ALIGN_CENTER);
			setCellVerticalAlignment(close, HasAlignment.ALIGN_MIDDLE);
		}
		setStyleName("caption");
		add(space);
		setCellWidth(space, "2");
		setWidth("100%");
	}

	/**
	 * showIcons
	 */
	public void showIcons(boolean isMinimize, boolean isMaximize, boolean isClose) {
		if (!isMinimize) {
			minimize.setVisible(false);
		}
		if (!isMaximize) {
			maximize.setVisible(false);
		}
		if (!isClose) {
			close.setVisible(false);
		}
	}

	@Override
	public void setHTML(SafeHtml html) {
		title.setHTML(html);
	}

	@Override
	public void setText(String text) {
		title.setHTML(text);
	}

	@Override
	public String getText() {
		return title.getText();
	}

	@Override
	public void setHTML(String html) {
		title.setHTML(html);
	}

	@Override
	public String getHTML() {
		return title.getHTML();
	}

	@Override
	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
		return null;
	}

	@Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return null;
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return null;
	}

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return null;
	}

	@Override
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return null;
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return null;
	}

	@Override
	public void addCaptionHandler(CaptionHandler captionHandler) {
		captionHandlers.add(captionHandler);
	}
}
