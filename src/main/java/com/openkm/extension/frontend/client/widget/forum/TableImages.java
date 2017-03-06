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

package com.openkm.extension.frontend.client.widget.forum;

import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollTableImages;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

/**
 * ForumManager
 *
 * @author jllort
 *
 */

public class TableImages implements ScrollTableImages {
	@Override
	public AbstractImagePrototype scrollTableAscending() {
		return new AbstractImagePrototype() {
			public void applyTo(Image image) {
				image.setUrl("img/sort_asc.gif");
			}

			public Image createImage() {
				return new Image("img/sort_asc.gif");
			}

			public String getHTML() {
				return "<img border=\"0\" src=\"img/sort_asc.gif\"/>";
			}
		};
	}

	@Override
	public AbstractImagePrototype scrollTableDescending() {
		return new AbstractImagePrototype() {
			public void applyTo(Image image) {
				image.setUrl("img/sort_desc.gif");
			}

			public Image createImage() {
				return new Image("img/sort_desc.gif");
			}

			public String getHTML() {
				return "<img border=\"0\" src=\"img/sort_desc.gif\"/>";
			}
		};
	}

	@Override
	public AbstractImagePrototype scrollTableFillWidth() {
		return new AbstractImagePrototype() {
			public void applyTo(Image image) {
				image.setUrl("img/fill_width.gif");
			}

			public Image createImage() {
				return new Image("img/fill_width.gif");
			}

			public String getHTML() {
				return "<img border=\"0\" src=\"img/fill_width.gif\"/>";
			}
		};
	}

	;
}