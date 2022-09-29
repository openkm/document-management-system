/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
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
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Text extractor for Portable Document Format (PDF).
 */
@PluginImplementation
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
		CharArrayWriter writer = null;
		PDDocument doc = null;

		try {
			PDFParser parser = new PDFParser(new RandomAccessBufferedFileInputStream(stream));

			try {
				parser.parse();
				doc = parser.getPDDocument();

				if (doc.isEncrypted()) {
					try {
						doc.setAllSecurityToBeRemoved(true);
					} catch (Exception e) {
						throw new IOException("Unable to extract text: document encrypted", e);
					}
				}

				writer = new CharArrayWriter();
				PDFTextStripper stripper = new PDFTextStripper();
				stripper.setLineSeparator("\n");
				stripper.writeText(doc, writer);
				String st = writer.toString().trim();
				log.debug("TextStripped: '{}'", st);

				if (Config.SYSTEM_PDF_FORCE_OCR || st.length() <= 1) {
					log.warn("PDF does not contains text layer");

					// When OCR engine is not configure should not expending time on the images extraction from the PDF file
					if (Config.SYSTEM_OCR.isEmpty()) {
						log.warn("No OCR engine configured");
						return "";
					} else {
						// Extract images from PDF
						StringBuilder sb = new StringBuilder();

						if (!Config.SYSTEM_PDFIMAGES.isEmpty()) {
							File tmpPdf = null;
							File tmpDir;

							try {
								tmpPdf = FileUtils.createTempFile("pdf");
								tmpDir = new File(EnvironmentDetector.getTempDir());
								String baseName = FileUtils.getFileName(tmpPdf.getName());
								doc.save(tmpPdf);
								int pgNum = 1;

								for (PDPage page : doc.getPages()) {
									log.debug("Page: {}", pgNum);
									HashMap<String, Object> hm = new HashMap<>();
									hm.put("fileIn", tmpPdf.getPath());
									hm.put("firstPage", pgNum);
									hm.put("lastPage", pgNum++);
									hm.put("imageRoot", tmpDir + File.separator + baseName);
									String cmd = TemplateUtils.replace("SYSTEM_PDFIMAGES", Config.SYSTEM_PDFIMAGES, hm);
									ExecutionUtils.runCmd(cmd);

									for (File tmp : tmpDir.listFiles()) {
										if (tmp.getName().startsWith(baseName + "-")) {
											try {
												if (page.getRotation() > 0) {
													ImageUtils.rotate(tmp, tmp, page.getRotation());
												}

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
							int pgNum = 1;

							for (PDPage page : doc.getPages()) {
								PDResources resources = page.getResources();
								log.debug("Page: {}", pgNum++);

								for (COSName cName : resources.getXObjectNames()) {
									PDXObject obj = resources.getXObject(cName);

									if (obj instanceof PDImageXObject) {
										PDImageXObject image = (PDImageXObject) obj;
										String prefix = "img-" + cName + "-";
										File pdfImg = null;

										try {
											pdfImg = File.createTempFile(prefix, ".jpg");
											log.debug("Writing image: {}", pdfImg.getPath());

											// Won't work until PDFBox 1.8.9
											ImageIO.write(image.getImage(), "jpg", pdfImg);

											if (page.getRotation() > 0) {
												ImageUtils.rotate(pdfImg, pdfImg, page.getRotation());
											}

											// Do OCR
											String txt = doOcr(pdfImg);
											sb.append(txt).append("\n");
											log.debug("OCR Extracted: {}", txt);
										} finally {
											FileUtils.deleteQuietly(pdfImg);
										}
									}
								}
							}
						}

						return sb.toString();
					}
				} else {
					return writer.toString();
				}
			} finally {
				try {
					if (doc != null) {
						doc.close();
					}
				} catch (IOException e) {
					// ignore
				}
			}
		} catch (Exception e) {
			// it may happen that PDFParser throws a runtime exception when parsing certain pdf documents
			log.warn("Failed to extract PDF text content", e);
			throw new IOException(e.getMessage(), e);
		} finally {
			if (writer != null) {
				writer.close();
			}

			IOUtils.closeQuietly(stream);
		}
	}

	/**
	 * Guess the active OCR engine and use it to extract text from image.
	 */
	private String doOcr(File pdfImg) throws Exception {
		String text = "";

		if (RegisteredExtractors.isRegistered(TesseractTextExtractor.class.getCanonicalName())) {
			text = new TesseractTextExtractor().doOcr(pdfImg);
		} else {
			log.warn("No OCR engine configured");
		}

		return text;
	}
}
