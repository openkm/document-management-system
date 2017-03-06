package com.openkm.test;

import com.openkm.extractor.AbstractTextExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class DummyMyTextExtractor extends AbstractTextExtractor {

	/**
	 * Logger instance.
	 */
	private static final Logger log = LoggerFactory.getLogger(DummyMyTextExtractor.class);

	/**
	 * Creates a new <code>TextExtractor</code> instance.
	 */
	public DummyMyTextExtractor() {
		super(new String[]{"image/jpeg"});
	}

	// -------------------------------------------------------< TextExtractor >

	/**
	 * {@inheritDoc}
	 */
	public String extractText(InputStream stream, String type, String encoding) throws IOException {
		log.info("******************* EXTRACT");
		return "";
	}
}
