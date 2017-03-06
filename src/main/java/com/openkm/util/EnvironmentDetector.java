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

package com.openkm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class EnvironmentDetector {
	private static Logger log = LoggerFactory.getLogger(EnvironmentDetector.class);

	private static final String JBOSS_PROPERTY = "jboss.home.dir";
	private static final String TOMCAT_PROPERTY = "catalina.home";
	private static final String CUSTOM_HOME_PROPERTY = "openkm.custom.home";

	private static final String OS_LINUX = "linux";
	private static final String OS_WINDOWS = "windows";
	private static final String OS_MAC = "mac os";

	/**
	 * Guess the application server home directory
	 */
	public static String getServerHomeDir() {
		// Try custom environment variable
		String dir = System.getProperty(CUSTOM_HOME_PROPERTY);

		if (dir != null) {
			log.debug("Using custom home: {}", dir);
			return dir;
		}

		// Try JBoss
		dir = System.getProperty(JBOSS_PROPERTY);

		if (dir != null) {
			log.debug("Using JBoss: {}", dir);
			return dir;
		}

		// Try Tomcat
		dir = System.getProperty(TOMCAT_PROPERTY);

		if (dir != null) {
			log.debug("Using Tomcat: {}", dir);
			return dir;
		}

		// Otherwise GWT hosted mode
		dir = System.getProperty("user.dir") + "/src/test/resources";
		log.debug("Using default dir: {}", dir);
		return dir;
	}

	/**
	 * Get server log directory
	 */
	public static String getServerLogDir() {
		// Try JBoss
		String dir = System.getProperty(JBOSS_PROPERTY);

		if (dir != null) {
			return dir + "/server/default/log";
		}

		// Try Tomcat
		dir = System.getProperty(TOMCAT_PROPERTY);

		if (dir != null) {
			return dir + "/logs";
		}

		return "";
	}

	/**
	 * Detect if running in JBoss 
	 */
	public static boolean isServerJBoss() {
		return System.getProperty(JBOSS_PROPERTY) != null;
	}

	/**
	 * Detect if running in Tomcat
	 */
	public static boolean isServerTomcat() {
		return !isServerJBoss() && System.getProperty(TOMCAT_PROPERTY) != null;
	}

	/**
	 * Guess JNDI base
	 */
	public static String getServerJndiBase() {
		if (isServerJBoss()) return "java:/";
		else if (isServerTomcat()) return "java:/comp/env/";
		else return "";
	}

	/**
	 * Guess the system wide temporary directory
	 */
	public static String getTempDir() {
		String dir = System.getProperty("java.io.tmpdir");

		if (dir != null) {
			return dir;
		} else {
			return "";
		}
	}

	/**
	 * Guess the system null device
	 */
	public static String getNullDevice() {
		String os = System.getProperty("os.name").toLowerCase();

		if (os.contains(OS_LINUX) || os.contains(OS_MAC)) {
			return "/dev/null";
		} else if (os.contains(OS_WINDOWS)) {
			return "NUL:";
		} else {
			return null;
		}
	}

	/**
	 * Execute application launcher
	 */
	public static void executeLauncher(String file) throws IOException {
		String os = System.getProperty("os.name").toLowerCase();

		if (os.contains(OS_LINUX)) {
			if (new File("/usr/bin/xdg-open").canExecute()) {
				Runtime.getRuntime().exec(new String[]{"/usr/bin/xdg-open", file});
			} else if (new File("/usr/bin/kde-open").canExecute()) {
				Runtime.getRuntime().exec(new String[]{"/usr/bin/kde-open", file});
			} else {
				throw new IOException("Linux flavour not supported");
			}
		} else if (os.contains(OS_MAC)) {
			Runtime.getRuntime().exec(new String[]{"open", file});
		} else if (os.contains(OS_WINDOWS)) {
			Runtime.getRuntime().exec("rundll32 SHELL32.DLL,ShellExec_RunDLL \"" + file + "\"");
		} else {
			throw new IOException("Environment not supported");
		}
	}

	/**
	 * Guess if running in Windows
	 */
	public static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.contains(OS_WINDOWS);
	}

	/**
	 * Guess if running in Linux
	 */
	public static boolean isLinux() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.contains(OS_LINUX);
	}

	/**
	 * Guess if running in Mac
	 */
	public static boolean isMac() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.contains(OS_MAC);
	}

	/**
	 * Test if is running in application server
	 */
	public static boolean inServer() {
		return isServerJBoss() || isServerTomcat();
	}

	/**
	 * Get user home
	 */
	public static String getUserHome() {
		return System.getProperty("user.home");
	}

	/**
	 * Guess OpenOffice / LibreOffice directory
	 */
	public static String detectOpenOfficePath() {
		if (isLinux()) {
			// Try LibreOffice
			File dir = new File("/usr/lib/libreoffice");

			if (dir.exists() && dir.isDirectory()) {
				log.info("Using LibreOffice from: " + dir);
				return dir.getAbsolutePath();
			}

			// Try LibreOffice (CentOS 64 bits)
			dir = new File("/usr/lib64/libreoffice");

			if (dir.exists() && dir.isDirectory()) {
				log.info("Using LibreOffice from: " + dir);
				return dir.getAbsolutePath();
			}

			// Try OpenOffice
			dir = new File("/usr/lib/openoffice");

			if (dir.exists() && dir.isDirectory()) {
				log.info("Using OpenOffice from: " + dir);
				return dir.getAbsolutePath();
			}

			// Try OpenOffice (CentOS 64 bits)
			dir = new File("/usr/lib64/openoffice");

			if (dir.exists() && dir.isDirectory()) {
				log.info("Using OpenOffice from: " + dir);
				return dir.getAbsolutePath();
			}

			// Otherwise none
			return "";
		} else {
			return "";
		}
	}

	/**
	 * Guess convert application
	 */
	public static String detectImagemagickConvert() {
		if (isLinux()) {
			File app = new File("/usr/bin/convert");

			if (app.exists() && app.isFile()) {
				return app.getAbsolutePath();
			} else {
				return "";
			}
		}
		if (isWindows()) {
			File app = new File(getServerHomeDir() + "\\bin\\convert.exe");

			if (app.exists() && app.isFile()) {
				return app.getAbsolutePath();
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	/**
	 * Guess pdfimages application
	 */
	public static String detectPdfImages() {
		final String params = "-j -f ${firstPage} -l ${lastPage} ${fileIn} ${imageRoot}";

		if (isLinux()) {
			File app = new File("/usr/bin/pdfimages");

			if (app.exists() && app.isFile()) {
				return app.getAbsolutePath() + " " + params;
			} else {
				app = new File(getServerHomeDir() + "/bin/pdfimages");

				if (app.exists() && app.isFile()) {
					return app.getAbsolutePath() + " " + params;
				} else {
					return "";
				}
			}
		}
		if (isWindows()) {
			File app = new File(getServerHomeDir() + "\\bin\\pdfimages.exe");

			if (app.exists() && app.isFile()) {
				return app.getAbsolutePath() + " " + params;
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	/**
	 * Guess pdf2swf application
	 */
	public static String detectSwftoolsPdf2Swf() {
		final String params = "-f -T 9 -t -s storeallcharacters ${fileIn} -o ${fileOut}";

		if (isLinux()) {
			File app = new File(getServerHomeDir() + "/bin/pdf2swf");

			if (app.exists() && app.isFile()) {
				return app.getAbsolutePath() + " " + params;
			} else {
				return "";
			}
		}
		if (isWindows()) {
			File app = new File(getServerHomeDir() + "\\bin\\pdf2swf.exe");

			if (app.exists() && app.isFile()) {
				return app.getAbsolutePath() + " " + params;
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	/**
	 * Guess gs application
	 */
	public static String detectGhostscript() {
		if (isLinux()) {
			File app = new File("/usr/bin/gs");

			if (app.exists() && app.isFile()) {
				return app.getAbsolutePath();
			} else {
				return "";
			}
		}
		if (isWindows()) {
			File app = new File(getServerHomeDir() + "\\bin\\gswin32c.exe");

			if (app.exists() && app.isFile()) {
				return app.getAbsolutePath();
			} else {
				return "";
			}
		} else {
			return "";
		}
	}
}
