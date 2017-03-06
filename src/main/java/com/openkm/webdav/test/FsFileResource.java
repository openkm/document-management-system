package com.openkm.webdav.test;

import com.bradmcevoy.common.ContentTypeUtils;
import com.bradmcevoy.http.*;
import com.bradmcevoy.http.entity.PartialEntity;
import com.bradmcevoy.http.webdav.PropPatchHandler.Fields;
import com.bradmcevoy.io.ReadingException;
import com.bradmcevoy.io.WritingException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class FsFileResource extends FsResource implements CopyableResource, DeletableResource, GetableResource,
		MoveableResource, PropFindableResource, PropPatchableResource {

	private static final Logger log = LoggerFactory.getLogger(FsFileResource.class);

	/**
	 * @param host    - the requested host. E.g. www.mycompany.com
	 * @param factory
	 * @param file
	 */
	public FsFileResource(String host, FileSystemResourceFactory factory, File file) {
		super(host, factory, file);
	}

	public Long getContentLength() {
		return file.length();
	}

	public String getContentType(String preferredList) {
		String mime = ContentTypeUtils.findContentTypes(this.file);
		String s = ContentTypeUtils.findAcceptableContentType(mime, preferredList);
		if (log.isTraceEnabled()) {
			log.trace("getContentType: preferred: {} mime: {} selected: {}", new Object[]{preferredList, mime, s});
		}
		return s;
	}

	public String checkRedirect(Request arg0) {
		return null;
	}

	public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType)
			throws IOException {
		log.info("sendContent({}, {})", range, contentType);

		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			if (range != null) {
				log.debug("sendContent: ranged content: " + file.getAbsolutePath());
				PartialEntity.writeRange(in, range, out);
			} else {
				log.debug("sendContent: send whole file " + file.getAbsolutePath());
				IOUtils.copy(in, out);
			}
			out.flush();
		} catch (ReadingException e) {
			throw new IOException(e);
		} catch (WritingException e) {
			throw new IOException(e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * @{@inheritDoc
	 */
	public Long getMaxAgeSeconds(Auth auth) {
		return factory.maxAgeSeconds(this);
	}

	/**
	 * @{@inheritDoc
	 */
	@Override
	protected void doCopy(File dest) {
		log.info("doCopy({})", dest);

		try {
			FileUtils.copyFile(file, dest);
		} catch (IOException ex) {
			throw new RuntimeException("Failed doing copy to: " + dest.getAbsolutePath(), ex);
		}
	}

	@Deprecated
	public void setProperties(Fields fields) {
		// MIL-50
		// not implemented. Just to keep MS Office sweet
	}
}
