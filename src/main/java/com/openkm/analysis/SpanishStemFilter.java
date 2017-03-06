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

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.tartarus.snowball.ext.SpanishStemmer;

import java.io.IOException;

public class SpanishStemFilter extends TokenFilter {
	@SuppressWarnings("unused")
	private SpanishStemmer stemmer;

	@SuppressWarnings("unused")
	private Token token = null;

	public SpanishStemFilter(TokenStream in) {
		super(in);
		stemmer = new SpanishStemmer();
	}

	/** Returns the next input Token, after being stemmed */
	/*
	public final Token next() throws IOException {
		if ((token = input.next()) == null) {
			return null;
		} else {
			stemmer.setCurrent(token.termText());
			stemmer.stem();
			String s = stemmer.getCurrent();
			if (!s.equals(token.termText())) {
				return new Token(s, token.startOffset(), token.endOffset(),
						token.type());
			}
			return token;
		}
	}
	*/

	/**
	 * Set a alternative/custom Stemmer for this filter.
	 */
	public void setStemmer(SpanishStemmer stemmer) {
		if (stemmer != null) {
			this.stemmer = stemmer;
		}
	}

	@Override
	public boolean incrementToken() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}
}
