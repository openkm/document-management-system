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

package com.openkm.frontend.client.panel.left;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ScrollPanel;

public class ExtendedScrollPanel extends ScrollPanel {
	
	/*
	 HOW IT RUNS EXTENDEDSCROLL DESCRIPTION
	 
	 [getAbsoluteLeft() , getAbsoluteTop()]
	 ^
	 |
	 x--------------------------x-> [getAbsoluteLeft()+getOffsetWidth() , getAbsoluteTop()]
	 |			^	timer    	|
	 |			|	fired		| (timer fired while is on this zone )
	 |			|				|							
	 |--------------------------x	<- MARGIN_AUTO_SCROLL_POS (Zone on auto scroll up while drag & drop 		
	 |							|
	 |							|
	 |							|
	 |							|
	 |							|							
	 |							|							
	 |							|
	 |							|
	 |							|
	 |							|
	 |--------------------------x	<- MARGIN_AUTO_SCROLL_POS (Zone on auto scroll downwhile drag & drop
	 |			^	timer		|							
	 |			|	fired		|   
	 |			|				|
	 x--------------------------x-> [getAbsoluteLeft()+getOffsetHeight() , getAbsoluteLeft() + getOffsetWidth()]
	  
	 ^
	 |
	 [getAbsoluteLeft() , getAbsoluteTop() + getOffsetHeight()]

	 */

	private final int MARGIN_AUTO_SCROLL_POS = 30; // Sets 30px from top widget and 30px from bottom to start autoscroll during drag  and drop
	private final int SCROLL_ACCELERATION = 20; // Acceleration factor to top / down scroll during drag and drop
	private final int TIMER_SPEED = 100;

	private Timer timer = null;

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.UIObject#getAbsoluteTop()
	 */
	public int getAbsoluteTop() {
		// Must be make a correction on getAbsoluteTop when the scroll is enabled the real absolute top is
		// super absoluteTop value + actual scroll position
		return super.getAbsoluteTop() + getVerticalScrollPosition();
	}

	/**
	 * Evaluate if x,y position is on scroll square widget
	 *
	 * @param x position
	 * @param y position
	 * @return
	 */
	private boolean isOverlap(int x, int y) {
		boolean xOverlap = getAbsoluteLeft() <= x && (getAbsoluteLeft() + getOffsetWidth()) >= x;
		boolean yOverlap = getAbsoluteTop() <= y && (getAbsoluteTop() + getOffsetHeight()) >= y;

		return (xOverlap && yOverlap);
	}

	/**
	 * ScrollDown
	 *
	 *  * Scrolls down with SCROLL_ACCELERATION factor
	 */
	private void ScrollDown() {
		this.setVerticalScrollPosition(getVerticalScrollPosition() + SCROLL_ACCELERATION);
	}

	/**
	 * ScrollUp
	 *
	 * Scrolls up with SCROLL_ACCELERATION factor
	 */
	private void ScrollUp() {
		this.setVerticalScrollPosition(getVerticalScrollPosition() - SCROLL_ACCELERATION);
	}

	/**
	 * isAutoScrollUp
	 *
	 * Determines is x,y position is on auto scroll up square
	 *
	 * @param x position
	 * @param y position
	 * @return
	 */
	private boolean isAutoScrollUp(int x, int y) {
		boolean xOverlap = getAbsoluteLeft() <= x && (getAbsoluteLeft() + getOffsetWidth()) >= x;
		boolean yOverlap = getAbsoluteTop() <= y && (getAbsoluteTop() + MARGIN_AUTO_SCROLL_POS) >= y;

		return (xOverlap && yOverlap);
	}

	/**
	 * isAutoScrollDown
	 *
	 * Determines is x,y position is on auto scroll down square
	 *
	 * @param x position
	 * @param y position
	 * @return
	 */
	private boolean isAutoScrollDown(int x, int y) {
		boolean xOverlap = getAbsoluteLeft() <= x && (getAbsoluteLeft() + getOffsetWidth()) >= x;
		boolean yOverlap = (getAbsoluteTop() + getOffsetHeight() - MARGIN_AUTO_SCROLL_POS) <= y &&
				(getAbsoluteTop() + getOffsetHeight()) >= y;

		return (xOverlap && yOverlap);
	}

	/**
	 * ScrollOnDragDrop
	 *
	 * @param x position
	 * @param y position
	 */
	public void ScrollOnDragDrop(int x, int y) {
		if (isOverlap(x, y)) {
			if (isAutoScrollUp(x, y)) {
				if (timer == null) {
					timer = new Timer() {
						public void run() {
							ScrollUp();
						}
					};
					timer.scheduleRepeating(TIMER_SPEED);
				}
			} else if (isAutoScrollDown(x, y)) {
				if (timer == null) {
					timer = new Timer() {
						public void run() {
							ScrollDown();
						}
					};
					timer.scheduleRepeating(TIMER_SPEED);
				}
			} else {
				destroyTimer();
			}
		} else {
			destroyTimer();
		}
	}

	/**
	 * destroyTimer
	 *
	 * Removes the scroll up / down timer
	 */
	public void destroyTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

}