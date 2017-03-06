package com.openkm.dao;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.cfg.Settings;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.connection.ConnectionProviderFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.util.ReflectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A commandline tool to update a database schema. May also be called from
 * inside an application.
 *
 * @author Christoph Sturm
 * @author Paco Avila
 * @see http://opensource.atlassian.com/projects/hibernate/browse/HHH-1186
 */
public class SchemaUpdate {
	private static Logger log = LoggerFactory.getLogger(SchemaUpdate.class);
	private ConnectionProvider connectionProvider;
	private Configuration configuration;
	private Dialect dialect;
	private List<Exception> exceptions;
	private String outputFile;

	public SchemaUpdate(Configuration cfg) throws HibernateException {
		this(cfg, cfg.getProperties());
	}

	public SchemaUpdate(Configuration cfg, Properties connectionProperties) throws HibernateException {
		this.configuration = cfg;
		dialect = Dialect.getDialect(connectionProperties);
		Properties props = new Properties();
		props.putAll(dialect.getDefaultProperties());
		props.putAll(connectionProperties);
		connectionProvider = ConnectionProviderFactory.newConnectionProvider(props);
		exceptions = new ArrayList<Exception>();
	}

	public SchemaUpdate(Configuration cfg, Settings settings) throws HibernateException {
		this.configuration = cfg;
		dialect = settings.getDialect();
		connectionProvider = settings.getConnectionProvider();
		exceptions = new ArrayList<Exception>();
	}

	public static void main(String[] args) {
		try {
			Configuration cfg = new Configuration();
			String outFile = null;

			boolean script = true;
			// If true then execute db updates, otherwise just generate and
			// display updates
			boolean doUpdate = true;
			String propFile = null;

			for (int i = 0; i < args.length; i++) {
				if (args[i].startsWith("--")) {
					if (args[i].equals("--quiet")) {
						script = false;
					} else if (args[i].startsWith("--properties=")) {
						propFile = args[i].substring(13);
					} else if (args[i].startsWith("--config=")) {
						cfg.configure(args[i].substring(9));
					} else if (args[i].startsWith("--text")) {
						doUpdate = false;
					} else if (args[i].startsWith("--naming=")) {
						cfg.setNamingStrategy((NamingStrategy) ReflectHelper.classForName(
								args[i].substring(9)).newInstance());
					} else if (args[i].startsWith("--output=")) {
						outFile = args[i].substring(9);
					}
				} else {
					cfg.addFile(args[i]);
				}

			}

			if (propFile != null) {
				Properties props = new Properties();
				props.putAll(cfg.getProperties());
				props.load(new FileInputStream(propFile));
				cfg.setProperties(props);
			}

			new SchemaUpdate(cfg).setOutputFile(outFile).execute(script, doUpdate);
		} catch (Exception e) {
			log.error("Error running schema update", e);
			e.printStackTrace();
		}
	}

	/**
	 * Set an output filename. The generated script will be written to this
	 * file.
	 */
	public SchemaUpdate setOutputFile(String filename) {
		outputFile = filename;
		return this;
	}

	/**
	 * Execute the schema updates
	 *
	 * @param script print all DDL to the console
	 */
	public void execute(boolean script, boolean doUpdate) {
		log.info("Running hbm2ddl schema update");
		Connection connection = null;
		Statement stmt = null;
		boolean autoCommitWasEnabled = true;
		Writer outputFileWriter = null;
		exceptions.clear();

		try {
			DatabaseMetadata meta;

			try {
				log.info("fetching database metadata");
				connection = connectionProvider.getConnection();

				if (!connection.getAutoCommit()) {
					connection.commit();
					connection.setAutoCommit(true);
					autoCommitWasEnabled = false;
				}

				meta = new DatabaseMetadata(connection, dialect);
				stmt = connection.createStatement();
			} catch (SQLException sqle) {
				exceptions.add(sqle);
				log.error("could not get database metadata", sqle);
				throw sqle;
			}

			log.info("updating schema");

			if (outputFile != null) {
				log.info("writing generated schema to file: " + outputFile);
				outputFileWriter = new FileWriter(outputFile);
			}

			String[] createSQL = configuration.generateSchemaUpdateScript(dialect, meta);

			for (int j = 0; j < createSQL.length; j++) {
				final String sql = createSQL[j];

				try {
					if (script) {
						log.info("writing generated schema to console: ");
						log.info(sql);
					}

					if (outputFile != null) {
						outputFileWriter.write(sql + "\n");
					}

					if (doUpdate) {
						log.debug(sql);
						stmt.executeUpdate(sql);
					}
				} catch (SQLException e) {
					exceptions.add(e);
					log.error("Unsuccessful: " + sql);
					log.error(e.getMessage());
				}
			}

			log.info("schema update complete");
		} catch (Exception e) {
			exceptions.add(e);
			log.error("could not complete schema update", e);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (!autoCommitWasEnabled)
					connection.setAutoCommit(false);
				if (connection != null)
					connection.close();
				if (connectionProvider != null)
					connectionProvider.close();
			} catch (Exception e) {
				exceptions.add(e);
				log.error("Error closing connection", e);
			}

			try {
				if (outputFileWriter != null) {
					outputFileWriter.close();
				}
			} catch (Exception e) {
				exceptions.add(e);
				log.error("Error closing connection", e);
			}
		}
	}

	/**
	 * Returns a List of all Exceptions which occured during the export.
	 *
	 * @return A List containig the Exceptions occured during the export
	 */
	public List<Exception> getExceptions() {
		return exceptions;
	}
}
