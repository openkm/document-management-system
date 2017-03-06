/**
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

package com.openkm.frontend.client.widget.toolbar;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * ToolBarButton
 *
 * @author jllort
 *
 */
public class ToolBarButton extends HorizontalPanel implements HasAllMouseHandlers, HasClickHandlers {

	private Image image;

	public ToolBarButton(Image image, String title, ClickHandler handler) {
		super();
		this.image = image;
		this.image.setTitle(title);
		addClickHandler(handler); // Adding clickhandler to widget

		add(image);
		setCellHorizontalAlignment(image, HasAlignment.ALIGN_CENTER);
		setCellVerticalAlignment(image, HasAlignment.ALIGN_MIDDLE);
		setSize("24", "24");
		setCellHeight(image, "24");
		setCellWidth(image, "24");

		sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.UIObject#setTitle(java.lang.String)
	 */
	public void setTitle(String title) {
		image.setTitle(title);
	}

	/**
	 * setResource
	 *
	 * @param resource
	 */
	public void setResource(ImageResource resource) {
		image.setResource(resource);
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addHandler(handler, ClickEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return addDomHandler(handler, MouseMoveEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return addDomHandler(handler, MouseUpEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
		return addDomHandler(handler, MouseWheelEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}
}