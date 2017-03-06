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

package com.openkm.util;

import com.openkm.api.OKMDocument;
import com.openkm.automation.AutomationException;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import de.svenjacobs.loremipsum.LoremIpsum;
import org.apache.commons.io.IOUtils;
import org.docx4j.TraversalUtil;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http://www.docx4java.org/trac/docx4j
 *
 * @author pavila
 */
public class MSOUtils {
	private static Logger log = LoggerFactory.getLogger(MSOUtils.class);

	/**
	 * Fill DOCX template
	 */
	public static void fillTemplate(InputStream input, HashMap<String, String> model, OutputStream output) throws FileNotFoundException,
			Docx4JException, JAXBException, IOException {
		log.info("fillTemplate({}, {}, {})", new Object[]{input, model, output});
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(input);
		MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

		// Do replace...
		documentPart.variableReplace(model);

		// Save it
		wordMLPackage.save(output);
		log.info("fillTemplate: void");
	}

	/**
	 * Fill document template.
	 *
	 * @param token   Authentication info.
	 * @param docId   The path that identifies an unique document or its UUID.
	 * @param model   A map with the template keys and values.
	 * @param dstPath The path of the resulting PDF document (with the name).
	 */
	public static void fillTemplate(String token, String docId, HashMap<String, String> model, String dstPath) throws LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException, IOException, Docx4JException,
			JAXBException, FileSizeExceededException, UserQuotaExceededException, VirusDetectedException, VersionException,
			ExtensionException, UnsupportedMimeTypeException, ItemExistsException, AutomationException {
		File docOut = null;
		InputStream docIs = null;
		OutputStream docOs = null;

		try {
			// Get document content
			com.openkm.bean.Document doc = OKMDocument.getInstance().getProperties(token, docId);
			docIs = OKMDocument.getInstance().getContent(token, docId, false);
			String mimeType = doc.getMimeType();

			// Convert to PDF
			docOut = FileUtils.createTempFileFromMime(mimeType);
			docOs = new FileOutputStream(docOut);
			MSOUtils.fillTemplate(docIs, model, docOs);

			// Upload to OpenKM
			try {
				docIs = new FileInputStream(docOut);
				OKMDocument.getInstance().createSimple(token, dstPath, docIs);
			} catch (ItemExistsException e) {
				IOUtils.closeQuietly(docIs);
				docIs = new FileInputStream(docOut);
				OKMDocument.getInstance().checkout(token, dstPath);
				OKMDocument.getInstance().checkin(token, dstPath, docIs, "Fill template");
			}
		} finally {
			IOUtils.closeQuietly(docIs);
			IOUtils.closeQuietly(docOs);
			FileUtils.deleteQuietly(docOut);
		}
	}

	/**
	 * Replace text
	 *
	 * See also http://www.docx4java.org/forums/docx-java-f6/best-approach-to-search-replace-in-a-template-merge-t1040.html
	 */
	public static void replaceText(InputStream input, HashMap<String, String> model, OutputStream output) throws Docx4JException,
			JAXBException, IOException {
		log.info("replaceText({}, {}, {})", new Object[]{input, model, output});
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(input);
		MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

		for (final Map.Entry<String, String> entry : model.entrySet()) {
			new TraversalUtil(documentPart, new TraversalUtil.CallbackImpl() {
				@Override
				public List<Object> apply(Object child) {
					if (child instanceof org.docx4j.wml.Text) {
						org.docx4j.wml.Text t = (org.docx4j.wml.Text) child;

						if (t.getValue().contains(entry.getKey())) {
							t.setValue(t.getValue().replaceAll(entry.getKey(), entry.getValue()));
						}
					}

					return null;
				}
			});
		}

		// Save it
		wordMLPackage.save(output);
		log.info("replaceText: void");
	}

	/**
	 * Replace text.
	 *
	 * @param token   Authentication info.
	 * @param docId   The path that identifies an unique document or its UUID.
	 * @param model   A map with the template keys and values.
	 * @param dstPath The path of the resulting PDF document (with the name).
	 */
	public static void replaceText(String token, String docId, HashMap<String, String> model, String dstPath) throws LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException, IOException, Docx4JException,
			JAXBException, FileSizeExceededException, UserQuotaExceededException, VirusDetectedException, VersionException,
			ExtensionException, UnsupportedMimeTypeException, ItemExistsException, AutomationException {
		File docOut = null;
		InputStream docIs = null;
		OutputStream docOs = null;

		try {
			// Get document content
			com.openkm.bean.Document doc = OKMDocument.getInstance().getProperties(token, docId);
			docIs = OKMDocument.getInstance().getContent(token, docId, false);
			String mimeType = doc.getMimeType();

			// Do action
			docOut = FileUtils.createTempFileFromMime(mimeType);
			docOs = new FileOutputStream(docOut);
			MSOUtils.replaceText(docIs, model, docOs);

			// Upload to OpenKM
			try {
				docIs = new FileInputStream(docOut);
				OKMDocument.getInstance().createSimple(token, dstPath, docIs);
			} catch (ItemExistsException e) {
				IOUtils.closeQuietly(docIs);
				docIs = new FileInputStream(docOut);
				OKMDocument.getInstance().checkout(token, dstPath);
				OKMDocument.getInstance().checkin(token, dstPath, docIs, "Replace text");
			}
		} finally {
			IOUtils.closeQuietly(docIs);
			IOUtils.closeQuietly(docOs);
			FileUtils.deleteQuietly(docOut);
		}
	}

	/**
	 * Generate sample docx
	 */
	public static void generateSample(int paragraphs, OutputStream os) throws Exception {
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
		MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
		LoremIpsum li = new LoremIpsum();

		for (int i = 0; i < paragraphs; i++) {
			mdp.addParagraphOfText(li.getParagraphs());
		}

		wordMLPackage.save(os);
	}
}
