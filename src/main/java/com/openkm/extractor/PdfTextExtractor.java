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

import com.openkm.core.Config;
import com.openkm.util.*;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.pdfbox.util.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Text extractor for Portable Document Format (PDF).
 */
public class PdfTextExtractor extends AbstractTextExtractor {

	/**
	 * Logger instance.
	 */
	private static final Logger log = LoggerFactory.getLogger(PdfTextExtractor.class);

	/**
	 * Force loading of dependent class.
	 */
	static {
		PDFParser.class.getName();
	}

	/**
	 * Creates a new <code>PdfTextExtractor</code> instance.
	 */
	public PdfTextExtractor() {
		super(new String[]{"application/pdf"});
	}

	// -------------------------------------------------------< TextExtractor >

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	public String extractText(InputStream stream, String type, String encoding) throws IOException {
		try {
			PDFParser parser = new PDFParser(new BufferedInputStream(stream));

			try {
				parser.parse();
				PDDocument document = parser.getPDDocument();

				if (document.isEncrypted()) {
					try {
						document.decrypt("");
						document.setAllSecurityToBeRemoved(true);
					} catch (Exception e) {
						throw new IOException("Unable to extract text: document encrypted", e);
					}
				}

				CharArrayWriter writer = new CharArrayWriter();
				PDFTextStripper stripper = new PDFTextStripper();
				stripper.setLineSeparator("\n");
				stripper.writeText(document, writer);
				String st = writer.toString().trim();
				log.debug("TextStripped: '{}'", st);

				if (Config.SYSTEM_PDF_FORCE_OCR || st.length() <= 1) {
					log.warn("PDF does not contains text layer");

					// Extract images from PDF
					StringBuilder sb = new StringBuilder();

					if (!Config.SYSTEM_PDFIMAGES.isEmpty()) {
						File tmpPdf = FileUtils.createTempFile("pdf");
						File tmpDir = new File(EnvironmentDetector.getTempDir());
						String baseName = FileUtils.getFileName(tmpPdf.getName());
						document.save(tmpPdf);
						int pgNum = 1;

						try {
							for (PDPage page : (List<PDPage>) document.getDocumentCatalog().getAllPages()) {
								HashMap<String, Object> hm = new HashMap<String, Object>();
								hm.put("fileIn", tmpPdf.getPath());
								hm.put("firstPage", pgNum);
								hm.put("lastPage", pgNum++);
								hm.put("imageRoot", tmpDir + File.separator + baseName);
								String cmd = TemplateUtils.replace("SYSTEM_PDFIMAGES", Config.SYSTEM_PDFIMAGES, hm);
								ExecutionUtils.runCmd(cmd);

								for (File tmp : tmpDir.listFiles()) {
									if (tmp.getName().startsWith(baseName + "-")) {
										if (page.findRotation() > 0) {
											ImageUtils.rotate(tmp, tmp, page.findRotation());
										}

										try {
											String txt = doOcr(tmp);
											sb.append(txt).append(" ");
											log.debug("OCR Extracted: {}", txt);
										} finally {
											FileUtils.deleteQuietly(tmp);
										}
									}
								}
							}
						} finally {
							FileUtils.deleteQuietly(tmpPdf);
						}
					} else {
						for (PDPage page : (List<PDPage>) document.getDocumentCatalog().getAllPages()) {
							PDResources resources = page.getResources();
							Map<String, PDXObject> images = resources.getXObjects();

							if (images != null) {
								for (String key : images.keySet()) {
									PDXObjectImage image = (PDXObjectImage) images.get(key);
									String prefix = "img-" + key + "-";
									File pdfImg = null;

									try {
										pdfImg = File.createTempFile(prefix, ".png");
										log.debug("Writing image: {}", pdfImg.getPath());

										// Won't work until PDFBox 1.8.9
										ImageIO.write(image.getRGBImage(), "png", pdfImg);

										if (page.findRotation() > 0) {
											ImageUtils.rotate(pdfImg, pdfImg, page.findRotation());
										}

										// Do OCR
										String txt = doOcr(pdfImg);
										sb.append(txt).append(" ");
										log.debug("OCR Extracted: {}", txt);
									} finally {
										FileUtils.deleteQuietly(pdfImg);
									}
								}
							}
						}
					}

					return sb.toString();
				} else {
					return writer.toString();
				}
			} finally {
				try {
					PDDocument doc = parser.getPDDocument();
					if (doc != null) {
						doc.close();
					}
				} catch (IOException e) {
					// ignore
				}
			}
		} catch (Exception e) {
			// it may happen that PDFParser throws a runtime
			// exception when parsing certain pdf documents
			log.warn("Failed to extract PDF text content", e);
			throw new IOException(e.getMessage(), e);
		} finally {
			stream.close();
		}
	}

	/**
	 * Guess the active OCR engine and use it to extract text from image.
	 */
	private String doOcr(File pdfImg) throws Exception {
		String text = "";

		if (RegisteredExtractors.isRegistered(CuneiformTextExtractor.class.getCanonicalName())) {
			text = new CuneiformTextExtractor().doOcr(pdfImg);
		} else if (RegisteredExtractors.isRegistered(Tesseract3TextExtractor.class.getCanonicalName())) {
			text = new Tesseract3TextExtractor().doOcr(pdfImg);
		} else if (RegisteredExtractors.isRegistered(AbbyTextExtractor.class.getCanonicalName())) {
			text = new AbbyTextExtractor().doOcr(pdfImg);
		} else {
			log.warn("No OCR engine configured");
		}

		return text;
	}
}
