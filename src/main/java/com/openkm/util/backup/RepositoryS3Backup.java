/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
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

package com.openkm.util.backup;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.google.gson.Gson;
import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.core.*;
import com.openkm.module.DocumentModule;
import com.openkm.module.FolderModule;
import com.openkm.module.ModuleManager;
import com.openkm.util.FileUtils;
import com.openkm.util.impexp.ImpExpStats;
import com.openkm.util.impexp.InfoDecorator;
import com.openkm.util.impexp.metadata.DocumentMetadata;
import com.openkm.util.impexp.metadata.FolderMetadata;
import com.openkm.util.impexp.metadata.MetadataAdapter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Iterator;

public class RepositoryS3Backup {
	private static Logger log = LoggerFactory.getLogger(RepositoryS3Backup.class);
	private static volatile boolean running = false;

	private RepositoryS3Backup() {
	}

	public boolean isRunning() {
		return running;
	}

	/**
	 * Performs a recursive repository content export with metadata
	 */
	public static ImpExpStats backup(String token, String fldPath, String bucket, boolean metadata, Writer out,
	                                 InfoDecorator deco) throws PathNotFoundException, AccessDeniedException, RepositoryException,
			FileNotFoundException, ParseException, NoSuchGroupException, IOException, DatabaseException,
			GeneralException {
		log.debug("backup({}, {}, {}, {}, {}, {})", new Object[]{token, fldPath, bucket, metadata, out, deco});
		ImpExpStats stats = null;

		if (running) {
			throw new GeneralException("Backup in progress");
		} else {
			running = true;

			try {
				if (!Config.AMAZON_ACCESS_KEY.equals("") && !Config.AMAZON_SECRET_KEY.equals("")) {
					AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(Config.AMAZON_ACCESS_KEY,
							Config.AMAZON_SECRET_KEY));

					if (!s3.doesBucketExist(bucket)) {
						s3.createBucket(bucket, Region.EU_Ireland);
					}

					stats = backupHelper(token, fldPath, s3, bucket, metadata, out, deco);
					log.info("Backup finished!");
				} else {
					throw new GeneralException("Missing Amazon Web Service keys");
				}
			} finally {
				running = false;
			}
		}

		log.debug("exportDocuments: {}", stats);
		return stats;
	}

	/**
	 * Performs a recursive repository content export with metadata
	 */
	private static ImpExpStats backupHelper(String token, String fldPath, AmazonS3 s3, String bucket, boolean metadata,
	                                        Writer out, InfoDecorator deco) throws FileNotFoundException, PathNotFoundException, AccessDeniedException,
			ParseException, NoSuchGroupException, RepositoryException, IOException, DatabaseException {
		log.info("backup({}, {}, {}, {}, {}, {})", new Object[]{token, fldPath, bucket, metadata, out, deco});
		ImpExpStats stats = new ImpExpStats();
		DocumentModule dm = ModuleManager.getDocumentModule();
		FolderModule fm = ModuleManager.getFolderModule();
		MetadataAdapter ma = MetadataAdapter.getInstance(token);
		Gson gson = new Gson();

		for (Iterator<Document> it = dm.getChildren(token, fldPath).iterator(); it.hasNext(); ) {
			File tmpDoc = null;
			InputStream is = null;
			FileOutputStream fos = null;
			boolean upload = true;

			try {
				Document docChild = it.next();
				String path = docChild.getPath().substring(1);
				ObjectMetadata objMeta = new ObjectMetadata();

				if (Config.REPOSITORY_CONTENT_CHECKSUM) {
					if (exists(s3, bucket, path)) {
						objMeta = s3.getObjectMetadata(bucket, path);

						if (docChild.getActualVersion().getChecksum().equals(objMeta.getETag())) {
							upload = false;
						}
					}
				}

				if (upload) {
					tmpDoc = FileUtils.createTempFileFromMime(docChild.getMimeType());
					fos = new FileOutputStream(tmpDoc);
					is = dm.getContent(token, docChild.getPath(), false);
					IOUtils.copy(is, fos);
					PutObjectRequest request = new PutObjectRequest(bucket, path, tmpDoc);

					if (metadata) {
						// Metadata
						DocumentMetadata dmd = ma.getMetadata(docChild);
						String json = gson.toJson(dmd);
						objMeta.addUserMetadata("okm", json);
					}

					request.setMetadata(objMeta);
					s3.putObject(request);
					out.write(deco.print(docChild.getPath(), docChild.getActualVersion().getSize(), null));
					out.flush();
				} else {
					if (metadata) {
						// Metadata
						DocumentMetadata dmd = ma.getMetadata(docChild);
						String json = gson.toJson(dmd);
						objMeta.addUserMetadata("okm", json);

						// Update object metadata
						CopyObjectRequest copyObjReq = new CopyObjectRequest(bucket, path, bucket, path);
						copyObjReq.setNewObjectMetadata(objMeta);
						s3.copyObject(copyObjReq);
					}

					log.info("Don't need to upload document {}", docChild.getPath());
				}

				// Stats
				stats.setSize(stats.getSize() + docChild.getActualVersion().getSize());
				stats.setDocuments(stats.getDocuments() + 1);
			} finally {
				IOUtils.closeQuietly(is);
				IOUtils.closeQuietly(fos);
				FileUtils.deleteQuietly(tmpDoc);
			}
		}

		for (Iterator<Folder> it = fm.getChildren(token, fldPath).iterator(); it.hasNext(); ) {
			InputStream is = null;

			try {
				Folder fldChild = it.next();
				String path = fldChild.getPath().substring(1) + "/";
				is = new ByteArrayInputStream(new byte[0]);
				ObjectMetadata objMeta = new ObjectMetadata();
				objMeta.setContentLength(0);
				PutObjectRequest request = new PutObjectRequest(bucket, path, is, objMeta);

				// Metadata
				if (metadata) {
					FolderMetadata fmd = ma.getMetadata(fldChild);
					String json = gson.toJson(fmd);
					objMeta.addUserMetadata("okm", json);
				}

				request.setMetadata(objMeta);
				s3.putObject(request);

				ImpExpStats tmp = backupHelper(token, fldChild.getPath(), s3, bucket, metadata, out, deco);

				// Stats
				stats.setSize(stats.getSize() + tmp.getSize());
				stats.setDocuments(stats.getDocuments() + tmp.getDocuments());
				stats.setFolders(stats.getFolders() + tmp.getFolders() + 1);
				stats.setOk(stats.isOk() && tmp.isOk());
			} finally {
				IOUtils.closeQuietly(is);
			}
		}

		log.debug("backupHelper: {}", stats);
		return stats;
	}

	private static boolean exists(AmazonS3 s3, String bucket, String key) {
		ObjectListing list = s3.listObjects(bucket, key);
		return list.getObjectSummaries().size() > 0;
	}
}
