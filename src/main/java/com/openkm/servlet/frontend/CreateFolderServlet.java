package com.openkm.servlet.frontend;

import com.google.gson.Gson;
import com.openkm.api.OKMFolder;
import com.openkm.bean.FileUploadResponse;
import com.openkm.bean.Folder;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import com.openkm.frontend.client.constants.UploadConstants;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.util.PathUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * CreateFolderServlet
 */
@WebServlet("/frontend/CreateFolder")
public class CreateFolderServlet extends OKMHttpServlet {
	private static Logger log = LoggerFactory.getLogger(CreateFolderServlet.class);
	private static final long serialVersionUID = 1L;
	public static final int INSERT = 0;
	public static final int UPDATE = 1;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		log.debug("doPost({}, {})", request, response);
		InputStream is = null;
		String path = null;
		int action = 0;
		PrintWriter out = null;
		java.io.File tmp = null;
		boolean redirect = false;
		String redirectURL = "";
		updateSessionManager(request);

		// JSON Stuff
		Ref<FileUploadResponse> fuResponse = new Ref<>(new FileUploadResponse());

		try {
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			response.setContentType(MimeTypeConfig.MIME_TEXT);
			out = response.getWriter();
			log.debug("isMultipart: {}", isMultipart);

			// Create a factory for disk-based file items
			if (isMultipart) {
				FileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
				String contentLength = request.getHeader("Content-Length");
				FileUploadListener listener = new FileUploadListener(Long.parseLong(contentLength));

				// Saving listener to session
				request.getSession().setAttribute(UploadConstants.FILE_UPLOAD_STATUS, listener);
				upload.setHeaderEncoding("UTF-8");

				// upload servlet allows to set upload listener
				upload.setProgressListener(listener);
				List<FileItem> items = upload.parseRequest(request);

				// Parse the request and get all parameters and the uploaded file
				for (FileItem item : items) {
					if (item.isFormField()) {
						if (item.getFieldName().equals("path")) {
							path = item.getString("UTF-8");
							break;
						}
					}
				}

				if (path != null) {
					Folder fld = new Folder();
					path = PathUtils.toValidPathName(path);
					fld.setPath(path);
					OKMFolder.getInstance().create(null, fld);
				} else {
					throw new PathNotFoundException("Path not found in servlet parameters");
				}
			}
		} catch (AccessDeniedException e) {
			fuResponse.get().setError(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied));
			sendErrorResponse(out, action, fuResponse.get(), request, response, redirect, redirectURL);
		} catch (PathNotFoundException e) {
			fuResponse.get().setError(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound));
			sendErrorResponse(out, action, fuResponse.get(), request, response, redirect, redirectURL);
		} catch (ItemExistsException e) {
			fuResponse.get().setError(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_ItemExists));
			sendErrorResponse(out, action, fuResponse.get(), request, response, redirect, redirectURL);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			fuResponse.get().setError(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository), e.getMessage());
			sendErrorResponse(out, action, fuResponse.get(), request, response, redirect, redirectURL);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			fuResponse.get().setError(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database));
			sendErrorResponse(out, action, fuResponse.get(), request, response, redirect, redirectURL);
		} catch (ExtensionException e) {
			log.error(e.getMessage(), e);
			fuResponse.get().setError(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Extension));
			sendErrorResponse(out, action, fuResponse.get(), request, response, redirect, redirectURL);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			fuResponse.get().setError(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_IO));
			sendErrorResponse(out, action, fuResponse.get(), request, response, redirect, redirectURL);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fuResponse.get().setError(e.toString());
			sendErrorResponse(out, action, fuResponse.get(), request, response, redirect, redirectURL);
		} finally {
			IOUtils.closeQuietly(is);
			out.flush();
			IOUtils.closeQuietly(out);
			System.gc();
		}
	}

	/**
	 * sendErrorResponse
	 */
	private void sendErrorResponse(PrintWriter out, int action, FileUploadResponse fur, HttpServletRequest request,
			HttpServletResponse response, boolean redirect, String redirectURL) {
		if (redirect) {
			ServletContext sc = getServletContext();

			try {
				sc.getRequestDispatcher(redirectURL).forward(request, response);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
		} else {
			sendResponse(out, action, fur);
		}
	}

	/**
	 * Send response back to browser.
	 */
	private void sendResponse(PrintWriter out, int action, FileUploadResponse fur) {
		Gson gson = new Gson();
		String json = gson.toJson(fur);
		out.print(json);
		log.debug("Action: {}, JSON Response: {}", action, json);
	}
}
