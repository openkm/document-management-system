/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2016 Paco Avila & Josep Llort
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

package com.openkm.extractor;

import net.xeoh.plugins.base.Plugin;

import java.io.IOException;
import java.io.InputStream;

public interface TextExtractor extends Plugin {

	/**
	 * Returns the MIME types supported by this extractor. The returned
	 * strings must be in lower case, and the returned array must not be empty.
	 * <p>
	 * The returned array must not be modified.
	 *
	 * @return supported MIME types, lower case
	 */
	String[] getContentTypes();

	/**
	 * Returns a reader for the text content of the given binary document.
	 * The content type and character encoding (if available and applicable)
	 * are given as arguments. The given content type is guaranteed to be
	 * one of the types reported by {@link #getContentTypes()} unless the
	 * implementation explicitly permits other content types.
	 * <p>
	 * The implementation can choose either to read and parse the given document immediately or to return a reader that
	 * does it incrementally. The only constraint is that the implementation must close the given stream latest when the
	 * returned reader is closed. The caller on the other hand is responsible for closing the returned reader.
	 * <p>
	 * The implementation should only throw an exception on transient errors, i.e. when it can expect to be able to
	 * successfully extract the text content of the same binary at another time. An effort should be made to recover
	 * from syntax errors and other similar problems.
	 * <p>
	 * This method should be thread-safe, i.e. it is possible that this method is invoked simultaneously by different
	 * threads to extract the text content of different documents. On the other hand the returned reader does not need
	 * to be thread-safe.
	 *
	 * @param stream binary document from which to extract text
	 * @param type MIME type of the given document, lower case
	 * @param encoding the character encoding of the binary data,
	 *        or <code>null</code> if not available
	 * @return reader for the extracted text content
	 * @throws IOException on transient errors
	 */
	String extractText(InputStream stream, String type, String encoding) throws IOException;
}
