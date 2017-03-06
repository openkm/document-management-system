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

package com.openkm.frontend.client.widget.dashboard;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;

/**
 * Image hovering on mouse over
 *
 * @author jllort
 *
 */
public class ImageHover extends Image implements HasClickHandlers, HasAllMouseHandlers {
	private String url = "";
	private String urlHover = "";

	public ImageHover(final String url, final String urlHover) {
		super(url);
		sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS);
		this.url = url;
		this.urlHover = urlHover;

		addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				setUrl(urlHover);
			}
		});

		addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				setUrl(url);
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler)
	 */
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addHandler(handler, ClickEvent.getType());
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasMouseDownHandlers#addMouseDownHandler(com.google.gwt.event.dom.client.MouseDownHandler)
	 */
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasMouseMoveHandlers#addMouseMoveHandler(com.google.gwt.event.dom.client.MouseMoveHandler)
	 */
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return addDomHandler(handler, MouseMoveEvent.getType());
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasMouseOutHandlers#addMouseOutHandler(com.google.gwt.event.dom.client.MouseOutHandler)
	 */
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasMouseOverHandlers#addMouseOverHandler(com.google.gwt.event.dom.client.MouseOverHandler)
	 */
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasMouseUpHandlers#addMouseUpHandler(com.google.gwt.event.dom.client.MouseUpHandler)
	 */
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return addDomHandler(handler, MouseUpEvent.getType());
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasMouseWheelHandlers#addMouseWheelHandler(com.google.gwt.event.dom.client.MouseWheelHandler)
	 */
	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
		return addDomHandler(handler, MouseWheelEvent.getType());
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.Widget#onBrowserEvent(com.google.gwt.user.client.Event)
	 */
	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
			case Event.ONMOUSEDOWN:
			case Event.ONMOUSEUP:
			case Event.ONMOUSEMOVE:
			case Event.ONMOUSEOVER:
				if (!super.getUrl().equals(urlHover)) {
					super.setUrl(urlHover);
				}
				break;
			case Event.ONMOUSEOUT: {
				if (!super.getUrl().equals(url)) {
					super.setUrl(url);
				}
				break;
			}

		}
		super.onBrowserEvent(event);
	}
}