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

package com.openkm.bean.nr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NodeResultSet implements Serializable {
	private static final long serialVersionUID = 1L;
	private long total;
	private List<NodeQueryResult> results = new ArrayList<NodeQueryResult>();

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<NodeQueryResult> getResults() {
		return results;
	}

	public void setResults(List<NodeQueryResult> results) {
		this.results = results;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("total=");
		sb.append(total);
		sb.append(", results=");
		sb.append(results);
		sb.append("}");
		return sb.toString();
	}
}
