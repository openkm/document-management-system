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

package com.openkm.form.suggestion;

import com.openkm.bean.form.Option;
import com.openkm.bean.form.Select;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.dao.NodeDocumentDAO;
import com.openkm.dao.NodeDocumentVersionDAO;
import com.openkm.dao.bean.NodeDocumentVersion;
import com.openkm.extractor.TextExtractorWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class DocumentContentContainsSuggestion implements Suggestion {
	private static Logger log = LoggerFactory.getLogger(DocumentContentContainsSuggestion.class);

	@Override
	public List<String> getSuggestions(String nodeUuid, String nodePath, Select sel) throws PathNotFoundException, SuggestionException,
			DatabaseException {
		log.debug("getSuggestions({}, {}, {})", new Object[]{nodeUuid, nodePath, sel});
		List<String> list = new ArrayList<String>();
		String textExtracted = null;

		try {
			if (NodeDocumentDAO.getInstance().isValid(nodeUuid)) {
				// Get text extraction
				if (NodeDocumentDAO.getInstance().isTextExtracted(nodeUuid)) {
					textExtracted = NodeDocumentDAO.getInstance().getExtractedText(nodeUuid);
				} else {
					// Force text extraction
					NodeDocumentVersion nDocVer = NodeDocumentVersionDAO.getInstance().findCurrentVersion(nodeUuid);
					TextExtractorWork tew = new TextExtractorWork();
					tew.setDocUuid(nodeUuid);
					tew.setDocPath(nodePath);
					tew.setDocVerUuid(nDocVer.getUuid());

					// Execute extractor
					textExtracted = NodeDocumentDAO.getInstance().textExtractorHelper(tew);
				}

				log.debug("Content length: {}", textExtracted.length());

				// Looking for metadata description values
				if (textExtracted != null && !textExtracted.isEmpty()) {
					textExtracted = textExtracted.toLowerCase();

					for (Option opt : sel.getOptions()) {
						if (textExtracted.contains(opt.getLabel().toLowerCase())) {
							if (!list.contains(opt.getValue())) {
								list.add(opt.getValue());
								break;
							}
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			throw new SuggestionException("FileNotFoundException: " + e.getMessage(), e);
		}

		return list;
	}
}
