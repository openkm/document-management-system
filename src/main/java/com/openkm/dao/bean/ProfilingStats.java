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

package com.openkm.dao.bean;

import java.io.Serializable;

public class ProfilingStats implements Serializable {
	private static final long serialVersionUID = 1L;
	private String clazz;
	private String method;
	private Long maxTime;
	private Long minTime;
	private Long totalTime;
	private Long avgTime;
	private Long executionCount;

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Long getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(Long maxTime) {
		this.maxTime = maxTime;
	}

	public Long getMinTime() {
		return minTime;
	}

	public void setMinTime(Long minTime) {
		this.minTime = minTime;
	}

	public Long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(Long totalTime) {
		this.totalTime = totalTime;
	}

	public Long getAvgTime() {
		return avgTime;
	}

	public void setAvgTime(Long avgTime) {
		this.avgTime = avgTime;
	}

	public Long getExecutionCount() {
		return executionCount;
	}

	public void setExecutionCount(Long executionCount) {
		this.executionCount = executionCount;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("clazz=");
		sb.append(clazz);
		sb.append(", method=");
		sb.append(method);
		sb.append(", maxTime=");
		sb.append(maxTime);
		sb.append(", minTime=");
		sb.append(minTime);
		sb.append(", avgTime=");
		sb.append(avgTime);
		sb.append(", totalTime=");
		sb.append(totalTime);
		sb.append(", executionCount=");
		sb.append(executionCount);
		sb.append("}");
		return sb.toString();
	}
}
