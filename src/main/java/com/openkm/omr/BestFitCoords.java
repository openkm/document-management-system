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

package com.openkm.omr;

import net.sourceforge.jiu.data.Gray8Image;

/**
 * @author Aaditeshwar Seth
 */
public class BestFitCoords {

	int x, y;
	double approxCircleOuterX, approxCircleInnerX, aspectScale;
	Gray8Image template;
	double maxsim = -1;

	/**
	 * BestFitCoords
	 */
	public BestFitCoords(int x, int y, Gray8Image template, double approxCircleOuterX, double approxCircleInnerX, double aspectScale) {
		this.x = x;
		this.y = y;
		this.template = template;
		this.approxCircleOuterX = approxCircleOuterX;
		this.approxCircleInnerX = approxCircleInnerX;
		this.aspectScale = aspectScale;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Gray8Image getTemplate() {
		return template;
	}

	public double getApproxCircleOuterX() {
		return approxCircleOuterX;
	}

	public double getApproxCircleInnerX() {
		return approxCircleInnerX;
	}

	public double getAspectScale() {
		return aspectScale;
	}

	public double getSim() {
		return maxsim;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setApproxCircleOuterX(double approxCircleOuterX) {
		this.approxCircleOuterX = approxCircleOuterX;
	}

	public void setApproxCircleInnerX(double approxCircleInnerX) {
		this.approxCircleInnerX = approxCircleInnerX;
	}

	public void setAspectScale(double aspectscale) {
		this.aspectScale = aspectscale;
	}

	public void setTemplate(Gray8Image template) {
		this.template = template;
	}

	public void setSim(double maxsim) {
		this.maxsim = maxsim;
	}
}
