package com.openkm.test;

import com.openkm.core.Config;
import com.openkm.dao.HibernateUtil;
import com.openkm.dao.bean.NodeFolder;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {
	private static Logger log = LoggerFactory.getLogger(Test.class);

	/**
	 * Only for testing purposes
	 */
	public static void main(String[] args) throws Exception {
		log.info("Generate database schema & initial data");
		HibernateUtil.generateDatabase("org.hibernate.dialect.Oracle10gDialect");
		Configuration cfg = new Configuration();

		// Add annotated beans
		cfg.addAnnotatedClass(NodeFolder.class);

		// Configure Hibernate
		cfg.setProperty("hibernate.dialect", Config.HIBERNATE_DIALECT);
		cfg.setProperty("hibernate.hbm2ddl.auto", "create");

		SchemaExport se = new SchemaExport(cfg);
		se.setOutputFile("/home/pavila/export.sql");
		se.setDelimiter(";");
		se.setFormat(false);
		se.create(false, false);
	}
}
