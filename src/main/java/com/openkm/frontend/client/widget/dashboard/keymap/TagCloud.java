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

package com.openkm.frontend.client.widget.dashboard.keymap;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openkm.frontend.client.bean.GWTKeyword;

import java.util.Iterator;
import java.util.List;

/**
 * TagCloud
 *
 * @author jllort
 *
 */
public class TagCloud extends Composite {

	private int minFrequency = 1;
	private int maxFrequency = 1;
	private static final int MIN_FONT_SIZE = 8;
	private static final int MAX_FONT_SIZE = 25;
	private FlowPanel flowPanel;

	/**
	 *
	 */
	public TagCloud() {
		flowPanel = new FlowPanel();
		initWidget(flowPanel);
	}

	/**
	 * Gets the label size
	 *
	 * @param frequency The frequency
	 *
	 * @return Size value
	 */
	public int getLabelSize(int frequency) {
		// Lineal interpolation
		//int multiplier = (MAX_FONT_SIZE-MIN_FONT_SIZE)/(maxFrequency-minFrequency);  
		//int fontSize = MIN_FONT_SIZE + ((maxFrequency-(maxFrequency-(frequency-minFrequency)))*multiplier);

		// Logarithmic interpolation
		double weight = (Math.log(frequency) - Math.log(minFrequency)) / (Math.log(maxFrequency) - Math.log(minFrequency));
		int fontSize = MIN_FONT_SIZE + (int) Math.round((MAX_FONT_SIZE - MIN_FONT_SIZE) * weight);

		return fontSize;
	}

	/**
	 * Sets the frequencies
	 *
	 * @param keywordsList Keyword list to calculate frequencies
	 */
	public void calculateFrequencies(List<GWTKeyword> keywordsList) {
		minFrequency = 1;
		maxFrequency = 1;

		for (Iterator<GWTKeyword> it = keywordsList.iterator(); it.hasNext(); ) {
			GWTKeyword keyword = it.next();
			if (minFrequency > keyword.getFrequency()) {
				minFrequency = keyword.getFrequency();
			}

			if (maxFrequency < keyword.getFrequency()) {
				maxFrequency = keyword.getFrequency();
			}
		}
	}

	/**
	 * Get some color dependig fontSize
	 *
	 * @param fontSize the font size
	 *
	 * @return Some color
	 */
	public String getColor(int fontSize) {
		String color = "#c3d9ff";

		if (fontSize > 20) {
			color = "#488bff";
		} else if (fontSize > 15) {
			color = "#6ca2ff";
		} else if (fontSize > 10) {
			color = "#8bb6ff";
		} else if (fontSize > 5) {
			color = "#a5c6ff";
		}

		return color;
	}

	/**
	 * Get the minimum font size
	 */
	public int getMinFontSize() {
		return MIN_FONT_SIZE;
	}

	/**
	 * Get the maximum font size
	 */
	public int getMaxFontSize() {
		return MAX_FONT_SIZE;
	}

	/**
	 * Removes all widgets
	 */
	public void clear() {
		flowPanel.clear();
	}

	/**
	 * Adds a widget
	 *
	 * @param widget The widget to be added
	 */
	public void add(Widget widget) {
		flowPanel.add(widget);
	}

	/**
	 * Gets the minimun frequency
	 *
	 * @return The frequency
	 */
	public int getMinFrequency() {
		return minFrequency;
	}

	/**
	 * Sets the minimun frequency
	 *
	 * @param minFrequency
	 */
	public void setMinFrequency(int minFrequency) {
		this.minFrequency = minFrequency;
	}

	/**
	 * Gets the maximun frequency
	 *
	 * @return The max frequency
	 */
	public int getMaxFrequency() {
		return maxFrequency;
	}

	/**
	 * Sets the max frequency
	 *
	 * @param maxFrequency The max frequency
	 */
	public void setMaxFrequency(int maxFrequency) {
		this.maxFrequency = maxFrequency;
	}
}
