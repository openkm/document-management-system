package com.openkm.core;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class VirusDetection {
	private static Logger log = LoggerFactory.getLogger(VirusDetection.class);

	/**
	 * Check for viruses in file
	 */
	public static String detect(File tmpFile) {
		try {
			// Performs virus check
			log.debug("CMD: " + Config.SYSTEM_ANTIVIR + " " + tmpFile.getPath());
			ProcessBuilder pb = new ProcessBuilder(Config.SYSTEM_ANTIVIR, "--no-summary", tmpFile.getPath());
			Process process = pb.start();
			process.waitFor();
			String info = IOUtils.toString(process.getInputStream());
			process.destroy();

			// Check return code
			if (process.exitValue() == 1) {
				log.warn(info);
				info = info.substring(info.indexOf(':') + 1);
				return info;
			} else {
				return null;
			}
		} catch (InterruptedException e) {
			log.warn("Failed to check for viruses", e);
		} catch (IOException e) {
			log.warn("Failed to check for viruses", e);
		}

		return "Failed to check for viruses";
	}
}
