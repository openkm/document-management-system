package com.openkm.util;

import com.openkm.automation.AutomationException;
import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import com.openkm.module.ModuleManager;
import com.openkm.util.markov.Generator;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Calendar;
import java.util.InputMismatchException;

/**
 * Default values generate text files with about 39 pages.
 *
 * @author pavila
 */
public class Benchmark {
	private static Logger log = LoggerFactory.getLogger(Benchmark.class);
	private static final String SEED = Config.HOME_DIR + File.separator + "benchmark.txt";
	private static final int PARAGRAPH = 250;
	private static final int LINE_WIDTH = 80;
	private static final int TOTAL_CHARS = 500;
	private static final int MAX_DOCUMENTS = 12;
	private static final int MAX_FOLDERS = 4;
	private static final int MAX_DEPTH = 3;
	private Generator gen = null;
	private int maxDocuments = 0;
	private int maxFolders = 0;
	private int maxDepth = 0;
	private int totalFolders = 0;
	private int totalDocuments = 0;
	private long totalSize = 0;
	private int row = 0;

	/**
	 * Main method for testing purposes
	 */
	public static void main(String[] args) {
	}

	public Benchmark() throws IOException {
		this.maxDocuments = MAX_DOCUMENTS;
		this.maxFolders = MAX_FOLDERS;
		this.maxDepth = MAX_DEPTH;
		FileInputStream fis = new FileInputStream(SEED);
		gen = new Generator(fis);
		fis.close();
	}

	public Benchmark(int maxDocuments, int maxFolders, int maxDepth) throws IOException {
		this.maxDocuments = maxDocuments;
		this.maxFolders = maxFolders;
		this.maxDepth = maxDepth;
		FileInputStream fis = new FileInputStream(SEED);
		gen = new Generator(fis);
		fis.close();
	}

	public Benchmark(int maxDocuments, int maxFolders, int maxDepth, InputStream is) throws IOException {
		this.maxDocuments = maxDocuments;
		this.maxFolders = maxFolders;
		this.maxDepth = maxDepth;
		gen = new Generator(is);
	}

	public int getMaxDocuments() {
		return maxDocuments;
	}

	public int getMaxFolders() {
		return maxFolders;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public int getTotalFolders() {
		return totalFolders;
	}

	public int getTotalDocuments() {
		return totalDocuments;
	}

	public long getTotalSize() {
		return totalSize;
	}

	/**
	 * Calculates the number of folder created
	 */
	public int calculateFolders() {
		int nodesAtLevel = 1;
		int total = 0;

		for (int i = 1; i <= maxDepth; i++) {
			nodesAtLevel = nodesAtLevel * maxFolders;
			total += nodesAtLevel;
		}

		return total;
	}

	/**
	 * Calculates the number of document created
	 */
	public int calculateDocuments() {
		int nodesAtLevel = 1;
		int total = 0;

		for (int i = 1; i <= maxDepth; i++) {
			nodesAtLevel = nodesAtLevel * maxFolders;
			total += nodesAtLevel;
		}

		return total * maxDocuments;
	}

	/**
	 * Run system calibration
	 *
	 * @throws IOException
	 * @throws InputMismatchException
	 */
	public long runCalibration() throws InputMismatchException, IOException {
		final int ITERATIONS = 10;
		long total = 0;

		for (int i = 0; i < ITERATIONS; i++) {
			long calBegin = System.currentTimeMillis();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			gen.generateText(PARAGRAPH, LINE_WIDTH, TOTAL_CHARS, baos);
			baos.close();
			long calEnd = System.currentTimeMillis();
			total = calEnd - calBegin;
		}

		log.debug("Calibration: {} ms", total / ITERATIONS);
		return total / ITERATIONS;
	}

	/**
	 * Run OpenKM text document insertions (API)
	 */
	public void okmApiHighPopulate(String token, Folder root, PrintWriter out, PrintWriter res) throws IOException,
			InputMismatchException, ItemExistsException, PathNotFoundException, UserQuotaExceededException,
			AccessDeniedException, UnsupportedMimeTypeException, FileSizeExceededException, VirusDetectedException,
			RepositoryException, DatabaseException, ExtensionException, AutomationException {
		long begin = System.currentTimeMillis();
		okmApiHighPopulateHelper(token, root, out, res, gen, 0);
		long end = System.currentTimeMillis();
		String elapse = FormatUtil.formatSeconds(end - begin);
		log.debug("Total Time: {} - Folders: {}, Documents: {}", new Object[]{elapse, totalFolders, totalDocuments});
	}

	/**
	 * Helper
	 */
	private void okmApiHighPopulateHelper(String token, Folder root, PrintWriter out, PrintWriter res, Generator gen,
	                                      int depth) throws InputMismatchException, IOException, ItemExistsException, PathNotFoundException,
			UserQuotaExceededException, AccessDeniedException, UnsupportedMimeTypeException, FileSizeExceededException,
			VirusDetectedException, RepositoryException, DatabaseException, ExtensionException, AutomationException {
		log.debug("okmApiHighPopulateHelper({}, {}, {}, {})", new Object[]{token, root, gen, depth});

		if (depth < maxDepth) {
			for (int i = 0; i < maxFolders; i++) {
				long begin = System.currentTimeMillis();
				Folder fld = new Folder();
				fld.setPath(root.getPath() + "/" + System.currentTimeMillis());
				fld = ModuleManager.getFolderModule().create(token, fld);
				totalFolders++;
				log.debug("At depth {}, created folder {}", depth, fld.getPath());

				for (int j = 0; j < maxDocuments; j++) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					gen.generateText(PARAGRAPH, LINE_WIDTH, TOTAL_CHARS, baos);
					baos.close();
					totalSize += baos.size();

					// Repository insertion
					ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
					Document doc = new Document();
					doc.setMimeType("text/plain");
					doc.setPath(fld.getPath() + "/" + System.currentTimeMillis() + ".txt");
					ModuleManager.getDocumentModule().create(token, doc, bais);
					IOUtils.closeQuietly(bais);
					totalDocuments++;
				}

				long end = System.currentTimeMillis();
				String elapse = FormatUtil.formatSeconds(end - begin);
				log.debug("Partial Time: {} - Folders: {}, Documents: {}", new Object[]{elapse, totalFolders,
						totalDocuments});
				out.print("<tr class=\"" + (row++ % 2 == 0 ? "even" : "odd") + "\">");
				out.print("<td>" + FormatUtil.formatDate(Calendar.getInstance()) + "</td>");
				out.print("<td>" + elapse + "</td>");
				out.print("<td>" + (end - begin) + "</td>");
				out.print("<td>" + totalFolders + "</td>");
				out.print("<td>" + totalDocuments + "</td>");
				out.print("<td>" + FormatUtil.formatSize(totalSize) + "</td>");
				out.println("</tr>");
				out.flush();

				res.print("\"" + FormatUtil.formatDate(Calendar.getInstance()) + "\",");
				res.print("\"" + elapse + "\",");
				res.print("\"" + (end - begin) + "\",");
				res.print("\"" + totalFolders + "\",");
				res.print("\"" + totalDocuments + "\",");
				res.print("\"" + FormatUtil.formatSize(totalSize) + "\"\n");
				res.flush();

				// Go depth
				okmApiHighPopulateHelper(token, fld, out, res, gen, depth + 1);
			}
		} else {
			log.debug("Max depth reached: {}", depth);
		}
	}

	/**
	 * Generate documents in the filesystem
	 */
	public void filesystemDocumentGenerate(File root) throws IOException, InputMismatchException, ItemExistsException,
			PathNotFoundException, UserQuotaExceededException, AccessDeniedException, UnsupportedMimeTypeException,
			FileSizeExceededException, VirusDetectedException, RepositoryException, DatabaseException,
			ExtensionException, AutomationException {
		long begin = System.currentTimeMillis();
		filesystemDocumentGenerateHelper(root, gen, 0, begin);
		long end = System.currentTimeMillis();
		String elapse = FormatUtil.formatSeconds(end - begin);
		log.debug("Total Time: {} - Folders: {}, Documents: {}", new Object[]{elapse, totalFolders, totalDocuments});
	}

	/**
	 * Helper
	 */
	private void filesystemDocumentGenerateHelper(File root, Generator gen, int depth, long begin) throws
			InputMismatchException, IOException, ItemExistsException, PathNotFoundException,
			UserQuotaExceededException, AccessDeniedException, UnsupportedMimeTypeException,
			FileSizeExceededException, VirusDetectedException, RepositoryException, DatabaseException,
			ExtensionException, AutomationException {
		log.debug("filesystemDocumentGenerateHelper({}, {}, {}, {})", new Object[]{root, gen, depth, begin});

		if (depth < maxDepth) {
			for (int i = 0; i < maxFolders; i++) {
				File fld = new File(root, Long.toString(System.currentTimeMillis()));
				fld.mkdirs();
				totalFolders++;
				log.debug("At depth {}, created folder {}", depth, fld.getPath());

				for (int j = 0; j < maxDocuments; j++) {
					File doc = new File(fld, System.currentTimeMillis() + ".txt");
					FileOutputStream fos = new FileOutputStream(doc);
					gen.generateText(PARAGRAPH, LINE_WIDTH, TOTAL_CHARS, fos);
					IOUtils.closeQuietly(fos);
					totalSize += doc.length();
					totalDocuments++;
				}

				long end = System.currentTimeMillis();
				String elapse = FormatUtil.formatSeconds(end - begin);
				log.info("Elapse Time: {} - Folders: {}, Documents: {}, Size: {}", new Object[]{elapse, totalFolders,
						totalDocuments, FormatUtil.formatSize(totalSize)});

				// Go depth
				filesystemDocumentGenerateHelper(fld, gen, depth + 1, begin);
			}
		} else {
			log.debug("Max depth reached: {}", depth);
		}
	}
}
