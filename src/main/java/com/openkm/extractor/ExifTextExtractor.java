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

package com.openkm.extractor;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Text extractor for JPEG image documents.
 * Use java metadata extraction library from 
 * http://www.drewnoakes.com/code/exif/index.html
 */
public class ExifTextExtractor extends AbstractTextExtractor {

	/**
	 * Logger instance.
	 */
	private static final Logger log = LoggerFactory.getLogger(ExifTextExtractor.class);

	/**
	 * Creates a new <code>JpegTextExtractor</code> instance.
	 */
	public ExifTextExtractor() {
		super(new String[]{"image/jpeg"});
	}

	//-------------------------------------------------------< TextExtractor >

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public String extractText(InputStream stream, String type, String encoding) throws IOException {
		try {
			Metadata metadata = ImageMetadataReader.readMetadata(new BufferedInputStream(stream));
			Iterator<Directory> directories = metadata.getDirectoryIterator();
			StringBuffer sb = new StringBuffer();

			while (directories.hasNext()) {
				Directory directory = directories.next();
				Iterator<Tag> tags = directory.getTagIterator();

				while (tags.hasNext()) {
					Tag tag = tags.next();
					sb.append("[");
					sb.append(tag.getDirectoryName());
					sb.append("] ");
					sb.append(tag.getTagName());
					sb.append(" = ");
					sb.append(tag.getDescription());
					sb.append("\n");
				}
			}

			log.debug("TEXT: " + sb.toString());
			return sb.toString();
		} catch (ImageProcessingException e) {
			log.warn("Failed to extract EXIF information", e);
			return "";
		} catch (MetadataException e) {
			log.warn("Failed to extract EXIF information", e);
			return "";
		} finally {
			stream.close();
		}
	}
}
