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

package com.openkm.analysis;

import com.openkm.core.Config;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

/**
 * @author pavila
 *
 */
public class SearchDemo {
	private static final String DOC_FIELD = "content";
	private static final int NUM_HITS = 10;
	private static final String SEARCH_TERM = "专项信息*";

	private static String[] strings = {
			"专项信息管理.doc",
			"Lucene in Action",
			"Lucene for Dummies",
			"Managing Gigabytes",
			"The Art of Computer Science"
	};

	private static Analyzer[] analyzers = {
			new SimpleAnalyzer(Config.LUCENE_VERSION),
			new StandardAnalyzer(Config.LUCENE_VERSION),
			new CJKAnalyzer(Config.LUCENE_VERSION),
			new SmartChineseAnalyzer(Config.LUCENE_VERSION),
			new WhitespaceAnalyzer(Config.LUCENE_VERSION)
	};

	public static void main(String args[]) throws Exception {
		for (Analyzer analyzer : analyzers) {
			System.out.println("** Analyzer: " + analyzer.getClass().getName() + " **");
			Directory index = new RAMDirectory();

			for (String str : strings) {
				add(index, analyzer, str);
			}

			search(index, analyzer, SEARCH_TERM);
			System.out.println();
		}
	}

	/**
	 * Add documents
	 */
	private static void add(Directory index, Analyzer analyzer, String str) throws IOException, ParseException {
		IndexWriterConfig config = new IndexWriterConfig(Config.LUCENE_VERSION, analyzer);
		IndexWriter w = new IndexWriter(index, config);
		Document doc = new Document();
		doc.add(new Field(DOC_FIELD, str, Field.Store.YES, Field.Index.ANALYZED));
		w.addDocument(doc);
		w.close();
	}

	/**
	 * Search in documents
	 */
	private static void search(Directory index, Analyzer analyzer, String str) throws ParseException, CorruptIndexException,
			IOException {
		IndexReader reader = IndexReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(NUM_HITS, true);
		//Query q = new QueryParser(Config.LUCENE_VERSION, DOC_FIELD, analyzer).parse(str);
		Query q = new WildcardQuery(new Term(DOC_FIELD, str));
		System.out.println("Query: " + q);

		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		System.out.println("Found " + hits.length + " hits.");

		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println((i + 1) + ". " + d.get(DOC_FIELD));
		}

		searcher.close();
	}
}
