package com.openkm.index;

import com.openkm.core.Config;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.SimpleFSDirectory;

import java.io.File;
import java.io.IOException;

public class Indexer {
	private static Analyzer analyzer = null;
	private static IndexWriter indexWriter = null;
	private static IndexSearcher indexSearcher = null;
	private static final String INDEX_PATH = "index";
	public static final int HITS_PER_PAGE = 32;

	/**
	 * Instance analyzer
	 */
	public static synchronized Analyzer getAnalyzer() {
		if (analyzer == null) {
			analyzer = new StandardAnalyzer(Config.LUCENE_VERSION);
		}

		return analyzer;
	}

	/**
	 * Obtain index writer
	 */
	public static synchronized IndexWriter getIndexWriter() throws CorruptIndexException,
			LockObtainFailedException, IOException {
		if (indexWriter == null) {
			IndexWriterConfig iwc = new IndexWriterConfig(Config.LUCENE_VERSION, getAnalyzer());
			FSDirectory fsd = new SimpleFSDirectory(new File(INDEX_PATH));
			indexWriter = new IndexWriter(fsd, iwc);
		}

		return indexWriter;
	}

	/**
	 * Close index writer
	 */
	public static synchronized void closeIndexWriter() throws CorruptIndexException, IOException {
		if (indexWriter != null) {
			indexWriter.close();
		}
	}

	/**
	 * Obtain index searcher
	 */
	public static synchronized IndexSearcher getIndexSearcher() throws CorruptIndexException, IOException {
		if (indexSearcher == null) {
			FSDirectory fsd = new SimpleFSDirectory(new File(INDEX_PATH));
			indexSearcher = new IndexSearcher(fsd);
		}

		return indexSearcher;
	}

	/**
	 * Close index searcher
	 */
	public static synchronized void closeIndexSearcher() throws CorruptIndexException, IOException {
		if (indexSearcher != null) {
			indexSearcher.close();
		}
	}
}
