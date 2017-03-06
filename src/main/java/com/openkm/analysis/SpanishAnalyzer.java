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
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

public class SpanishAnalyzer extends Analyzer {

	/**
	 * An array containing some common Spanish words that are usually not  
	 * useful for searching. Imported from http://www.unine.ch/info/clef/.
	 * http://members.unine.ch/jacques.savoy/clef/spanishSmart.txt
	 */
	private static final String SPANISH_STOP_WORDS[] = {
			"él", "ésta", "éstas", "éste", "éstos", "última", "últimas", "último", "últimos", "a", "añadió",
			"aún", "actualmente", "adelante", "además", "afirmó", "agregó", "ahí", "ahora", "al", "algún",
			"algo", "alguna", "algunas", "alguno", "algunos", "alrededor", "ambos", "ante", "anterior", "antes",
			"apenas", "aproximadamente", "aquí", "así", "aseguró", "aunque", "ayer", "bajo", "bien", "buen",
			"buena", "buenas", "bueno", "buenos", "cómo", "cada", "casi", "cerca", "cierto", "cinco", "comentó",
			"como", "con", "conocer", "consideró", "considera", "contra", "cosas", "creo", "cual", "cuales",
			"cualquier", "cuando", "cuanto", "cuatro", "cuenta", "da", "dado", "dan", "dar", "de", "debe",
			"deben", "debido", "decir", "dejó", "del", "demás", "dentro", "desde", "después", "dice", "dicen",
			"dicho", "dieron", "diferente", "diferentes", "dijeron", "dijo", "dio", "donde", "dos", "durante",
			"e", "ejemplo", "el", "ella", "ellas", "ello", "ellos", "embargo", "en", "encuentra", "entonces",
			"entre", "era", "eran", "es", "esa", "esas", "ese", "eso", "esos", "está", "están", "esta", "estaba",
			"estaban", "estamos", "estar", "estará", "estas", "este", "esto", "estos", "estoy", "estuvo", "ex",
			"existe", "existen", "explicó", "expresó", "fin", "fue", "fuera", "fueron", "gran", "grandes", "ha",
			"había", "habían", "haber", "habrá", "hace", "hacen", "hacer", "hacerlo", "hacia", "haciendo",
			"han", "hasta", "hay", "haya", "he", "hecho", "hemos", "hicieron", "hizo", "hoy", "hubo", "igual",
			"incluso", "indicó", "informó", "junto", "la", "lado", "las", "le", "les", "llegó", "lleva",
			"llevar", "lo", "los", "luego", "lugar", "más", "manera", "manifestó", "mayor", "me", "mediante",
			"mejor", "mencionó", "menos", "mi", "mientras", "misma", "mismas", "mismo", "mismos", "momento",
			"mucha", "muchas", "mucho", "muchos", "muy", "nada", "nadie", "ni", "ningún", "ninguna", "ningunas",
			"ninguno", "ningunos", "no", "nos", "nosotras", "nosotros", "nuestra", "nuestras", "nuestro",
			"nuestros", "nueva", "nuevas", "nuevo", "nuevos", "nunca", "o", "ocho", "otra", "otras", "otro",
			"otros", "para", "parece", "parte", "partir", "pasada", "pasado", "pero", "pesar", "poca", "pocas",
			"poco", "pocos", "podemos", "podrá", "podrán", "podría", "podrían", "poner", "por", "porque",
			"posible", "próximo", "próximos", "primer", "primera", "primero", "primeros", "principalmente",
			"propia", "propias", "propio", "propios", "pudo", "pueda", "puede", "pueden", "pues", "qué", "que",
			"quedó", "queremos", "quién", "quien", "quienes", "quiere", "realizó", "realizado", "realizar",
			"respecto", "sí", "sólo", "se", "señaló", "sea", "sean", "según", "segunda", "segundo", "seis",
			"ser", "será", "serán", "sería", "si", "sido", "siempre", "siendo", "siete", "sigue", "siguiente",
			"sin", "sino", "sobre", "sola", "solamente", "solas", "solo", "solos", "son", "su", "sus", "tal",
			"también", "tampoco", "tan", "tanto", "tenía", "tendrá", "tendrán", "tenemos", "tener", "tenga",
			"tengo", "tenido", "tercera", "tiene", "tienen", "toda", "todas", "todavía", "todo", "todos",
			"total", "tras", "trata", "través", "tres", "tuvo", "un", "una", "unas", "uno", "unos", "usted",
			"va", "vamos", "van", "varias", "varios", "veces", "ver", "vez", "y", "ya", "yo"};

	/**
	 * Contains the stopwords used with the StopFilter.
	 */
	private Set<Object> stopTable = new HashSet<Object>();

	/**
	 * Contains words that should be indexed but not stemmed.
	 */
	@SuppressWarnings("unused")
	private Set<Object> exclTable = new HashSet<Object>();

	/**
	 * Builds an analyzer with the default stop words.
	 */
	public SpanishAnalyzer() {
		stopTable = StopFilter.makeStopSet(Config.LUCENE_VERSION, SPANISH_STOP_WORDS);
	}

	/** Builds an analyzer with the given stop words. */
	public SpanishAnalyzer(String[] stopWords) {
		stopTable = StopFilter.makeStopSet(Config.LUCENE_VERSION, stopWords);
	}

	/**
	 * Builds an analyzer with the given stop words from file.
	 * @throws IOException
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public SpanishAnalyzer(File stopWords) throws IOException {
		stopTable = new HashSet(WordlistLoader.getWordSet(stopWords));
	}

	/** Constructs a {@link StandardTokenizer} filtered by a {@link
	 * StandardFilter}, a {@link LowerCaseFilter}, a {@link StopFilter}
	 * and a {@link SpanishStemFilter}. */
	public final TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream result = new StandardTokenizer(Config.LUCENE_VERSION, reader);
		result = new StandardFilter(Config.LUCENE_VERSION, result);
		result = new LowerCaseFilter(Config.LUCENE_VERSION, result);
		result = new StopFilter(Config.LUCENE_VERSION, result, stopTable);
		result = new SpanishStemFilter(result);
		return result;
	}
}
