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

package com.openkm.frontend.client.widget;

import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * GroupBoxPanel
 *
 * @author jllort
 *
 */
public class GroupBoxPanel extends ComplexPanel implements InsertPanel {

	private Element legend;

	/**
	 * GroupBoxPanel
	 */
	public GroupBoxPanel() {
		Element fieldset = DOM.createFieldSet();
		this.legend = DOM.createLegend();
		DOM.appendChild(fieldset, legend);
		setElement(fieldset);
	}

	/**
	 * getCaption
	 *
	 * @return
	 */
	public String getCaption() {
		return DOM.getInnerText(this.legend);
	}

	/**
	 * setCaption
	 *
	 * @param caption
	 */
	public void setCaption(String caption) {
		DOM.setInnerText(this.legend, caption);
	}

	/**
	 * Adds a new child widget to the panel.
	 *
	 * @param w the widget to be added
	 */
	@Override
	public void add(Widget w) {
		add(w, getElement());
	}

	@Override
	public void clear() {
		// Remove all existing child nodes.
		Node child = getElement().getFirstChild();
		while (child != null) {
			getElement().removeChild(child);
			child = getElement().getFirstChild();
		}
	}

	/**
	 * Inserts a widget before the specified index.
	 *
	 * @param w the widget to be inserted
	 * @param beforeIndex the index before which it will be inserted
	 * @throws IndexOutOfBoundsException if <code>beforeIndex</code> is out of
	 *           range
	 */
	public void insert(Widget w, int beforeIndex) {
		insert(w, getElement(), beforeIndex, true);
	}
}  