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

package com.openkm.module.db.stuff;

import com.openkm.extractor.RegisteredExtractors;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.AbstractField;
import org.apache.lucene.document.Fieldable;
import org.hibernate.search.bridge.LuceneOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;

/**
 * @author pavila
 * @see http://community.jboss.org/wiki/HibernateSearchAndOfflineTextExtraction
 */
@SuppressWarnings("serial")
public class LazyField extends AbstractField implements Fieldable {
	private static Logger log = LoggerFactory.getLogger(LazyField.class);
	PersistentFile persistentFile;
	String content;

	public LazyField(String name, PersistentFile persistentFile, LuceneOptions luceneOptions) {
		super(name, luceneOptions.getStore(), luceneOptions.getIndex(), luceneOptions.getTermVector());
		lazy = true;
		this.persistentFile = persistentFile;
	}

	@Override
	public String stringValue() {
		if (content == null) {
			try {
				content = RegisteredExtractors.getText(persistentFile);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		log.info("stringValue: {}", content);
		return content;
	}

	@Override
	public Reader readerValue() {
		return null;
	}

	@Override
	public TokenStream tokenStreamValue() {
		return null;
	}
}
