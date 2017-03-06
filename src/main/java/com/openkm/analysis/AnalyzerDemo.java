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

package com.openkm.analysis;

import com.openkm.core.Config;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * @author pavila
 *
 */
public class AnalyzerDemo {
	private static Logger log = LoggerFactory.getLogger(AnalyzerDemo.class);
	private static String[] strings = {"专项信息管理"};

	private static Analyzer[] analyzers = {
			new SimpleAnalyzer(Config.LUCENE_VERSION),
			new StandardAnalyzer(Config.LUCENE_VERSION),
			new CJKAnalyzer(Config.LUCENE_VERSION),
			new SmartChineseAnalyzer(Config.LUCENE_VERSION),
			new WhitespaceAnalyzer(Config.LUCENE_VERSION)
	};

	public static void main(String args[]) throws Exception {
		for (String string : strings) {
			for (Analyzer analyzer : analyzers) {
				analyze(string, analyzer);
			}
		}
	}

	/**
	 * Analyze and display tokens
	 */
	private static void analyze(String string, Analyzer analyzer) throws IOException {
		StringBuffer buffer = new StringBuffer();
		TokenStream stream = analyzer.tokenStream("contents", new StringReader(string));
		CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
		buffer.append(analyzer.getClass().getName());
		buffer.append(" -> ");

		while (stream.incrementToken()) {
			buffer.append(" [");
			buffer.append(term.toString());
			buffer.append("]");
		}

		String output = buffer.toString();
		log.info(output);
	}
}
