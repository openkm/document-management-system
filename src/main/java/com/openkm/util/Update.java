/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2015 Paco Avila & Josep Llort
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

package com.openkm.util;

import com.openkm.bean.AppVersion;
import com.openkm.bean.Repository;
import com.openkm.core.Config;
import com.openkm.core.RepositoryInfo;
import com.openkm.module.common.CommonAuthModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;

public class Update {
	private static Logger log = LoggerFactory.getLogger(Update.class);

	public static String checkVersion() {
		log.debug("checkVersion()");
		StringBuffer sb = new StringBuffer();

		try {
			URL url = new URL("http://update.openkm.com/");
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			AppVersion av = WarUtils.getAppVersion();
			// @formatter:off
			String content = "okm_uuid=" + URLEncoder.encode(Repository.getUuid(), "UTF-8") +
					"&okm_version=" + URLEncoder.encode(av.getVersion(), "UTF-8") +
					"&okm_build=" + URLEncoder.encode(av.getBuild(), "UTF-8") +
					"&os_name=" + URLEncoder.encode(System.getProperty("os.name"), "UTF-8") +
					"&os_version=" + URLEncoder.encode(System.getProperty("os.version"), "UTF-8") +
					"&java_vendor=" + URLEncoder.encode(System.getProperty("java.vm.vendor"), "UTF-8") +
					"&java_version=" + URLEncoder.encode(System.getProperty("java.version"), "UTF-8") +
					"&hbm_dialect=" + URLEncoder.encode(Config.HIBERNATE_DIALECT, "UTF-8") +
					"&doc_num=" + RepositoryInfo.getDocumentsByContext().getTotal() +
					"&fld_num=" + RepositoryInfo.getFoldersByContext().getTotal() +
					"&mail_num=" + RepositoryInfo.getMailsByContext().getTotal() +
					"&doc_size=" + RepositoryInfo.getDocumentsSizeByContext().getTotal() +
					"&user_num=" + CommonAuthModule.getPrincipalAdapter().getUsers().size();
			// @formatter:on

			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			urlConn.setRequestProperty("Content-Length", String.valueOf(content.length()));

			// Send POST output.
			DataOutputStream printOut = new DataOutputStream(urlConn.getOutputStream());
			printOut.writeBytes(content);
			printOut.flush();
			printOut.close();

			// Get response data.
			BufferedReader input = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			String line;

			while ((line = input.readLine()) != null) {
				sb.append(line);
			}

			input.close();
		} catch (UnsupportedEncodingException e) {
			log.error("UnsupportedEncodingException: " + e.getMessage());
		} catch (MalformedURLException e) {
			log.error("MalformedURLException: " + e.getMessage());
		} catch (UnknownHostException e) {
			log.error("UnknownHostException: " + e.getMessage());
		} catch (IOException e) {
			log.error("IOException: " + e.getMessage());
		} catch (Exception e) {
			log.error("Exception: " + e.getMessage());
		}

		log.info("checkVersion: {}", sb.toString());
		return sb.toString();
	}
}
