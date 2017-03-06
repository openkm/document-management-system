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

import entagged.audioformats.AudioFile;
import entagged.audioformats.AudioFileIO;
import entagged.audioformats.Tag;
import entagged.audioformats.exceptions.CannotReadException;
import entagged.audioformats.mp3.util.id3frames.TextId3Frame;
import entagged.audioformats.ogg.util.OggTagField;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Text extractor for audio documents.
 * Use java metadata extraction library from 
 * http://entagged.sourceforge.net/
 */
public class AudioTextExtractor extends AbstractTextExtractor {

	/**
	 * Logger instance.
	 */
	private static final Logger log = LoggerFactory.getLogger(AudioTextExtractor.class);

	/**
	 * Creates a new <code>AudioTextExtractor</code> instance.
	 */
	public AudioTextExtractor() {
		super(new String[]{"audio/mpeg", "audio/x-ogg"});
	}

	//-------------------------------------------------------< TextExtractor >

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public String extractText(InputStream stream, String type, String encoding) throws IOException {
		File tmpFile = null;

		try {
			if (type.equals("audio/mpeg")) {
				tmpFile = File.createTempFile("okm", ".mp3");
			} else if (type.equals("audio/x-ogg")) {
				tmpFile = File.createTempFile("okm", ".ogg");
			}

			FileOutputStream fos = new FileOutputStream(tmpFile);
			IOUtils.copy(stream, fos);
			fos.close();

			StringBuffer sb = new StringBuffer();
			AudioFile af = AudioFileIO.read(tmpFile);
			Tag tag = af.getTag();

			for (Iterator<Object> it = tag.getFields(); it.hasNext(); ) {
				Object o = it.next();

				if (o instanceof TextId3Frame) {
					TextId3Frame tf = (TextId3Frame) o;
					sb.append("[ID3] ");
					sb.append(tf.getId());
					sb.append("=");
					sb.append(tf.getContent());
					sb.append("\n");
				} else if (o instanceof OggTagField) {
					OggTagField tf = (OggTagField) o;
					sb.append("[OGG] ");
					sb.append(tf.getId());
					sb.append("=");
					sb.append(tf.getContent());
					sb.append("\n");
				}
			}

			log.debug("TEXT: " + sb.toString());
			return sb.toString();
		} catch (CannotReadException e) {
			log.warn("Failed to extract tag information", e);
			return "";
		} finally {
			IOUtils.closeQuietly(stream);
			FileUtils.deleteQuietly(tmpFile);
		}
	}
}
