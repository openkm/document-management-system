package com.openkm.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;

public class DummyFile {
	private static Logger log = LoggerFactory.getLogger(DummyFile.class);
	private static final String FILE = "prueba.txt";

	public static void main(String[] args) throws IOException {
		System.out.println("** CHARACTER ENCODING: " + (new OutputStreamWriter(new ByteArrayOutputStream())).getEncoding());
		System.out.println("** CHARACTER ENCODING: " + Charset.defaultCharset());
		System.out.println("** file.encoding: " + System.getProperty("file.encoding"));
		System.out.println("** sun.jnu.encoding: " + System.getProperty("sun.jnu.encoding"));
		write();
		read();
	}

	/**
	 *
	 */
	private static void write() throws FileNotFoundException, IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(FILE));
		bos.write("Esto es una coñó".getBytes());
		bos.close();
	}

	/**
	 *
	 */
	private static void read() throws FileNotFoundException, IOException {
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(FILE));
		byte[] buffer = new byte[24];

		while (bis.read(buffer) > 0) {
			log.info("** Contenido: " + new String(buffer));
		}

		bis.close();
	}
}
