package com.openkm.rest.endpoint;

import com.openkm.dao.bean.Bookmark;
import com.openkm.module.BookmarkModule;
import com.openkm.module.ModuleManager;
import com.openkm.rest.GenericException;
import com.openkm.rest.util.BookmarkList;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Api(value = "bookmark-service")
@Path("/bookmark")
public class BookmarkService {
	private static final Logger log = LoggerFactory.getLogger(BookmarkService.class);

	@POST
	@Path("/create")
	// The "name" parameter comes in the POST request body.
	public Bookmark create(@QueryParam("nodeId") String nodeId, String name) throws GenericException {
		try {
			log.debug("create({}, {})", nodeId, name);
			BookmarkModule bm = ModuleManager.getBookmarkModule();
			Bookmark bookmark = bm.add(null, nodeId, name);
			log.debug("create: {}", bookmark);
			return bookmark;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/get")
	public Bookmark get(@QueryParam("bookmarkId") int bookmarkId) throws GenericException {
		try {
			log.debug("get({})", bookmarkId);
			BookmarkModule bm = ModuleManager.getBookmarkModule();
			Bookmark bookmark = bm.get(null, bookmarkId);
			log.debug("get: {}", bookmark);
			return bookmark;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@DELETE
	@Path("/delete")
	public void delete(@QueryParam("bookmarkId") int bookmarkId) throws GenericException {
		try {
			log.debug("delete({})", bookmarkId);
			BookmarkModule bm = ModuleManager.getBookmarkModule();
			bm.remove(null, bookmarkId);
			log.debug("delete: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}

	}

	@PUT
	@Path("/rename")
	// The "newName" parameter comes in the PUT request body.
	public Bookmark rename(@QueryParam("bookmarkId") int bookmarkId, String newName) throws GenericException {
		try {
			log.debug("rename({}, {})", bookmarkId, newName);
			BookmarkModule bm = ModuleManager.getBookmarkModule();
			Bookmark bookmark = bm.rename(null, bookmarkId, newName);
			log.debug("rename: {}", bookmark);
			return bookmark;
		} catch (Exception e) {
			throw new GenericException(e);
		}

	}

	@GET
	@Path("/getAll")
	public BookmarkList getAll() throws GenericException {
		try {
			log.debug("getAll()");
			BookmarkModule bm = ModuleManager.getBookmarkModule();
			BookmarkList bookmarkList = new BookmarkList();
			List<Bookmark> bookmarks = bm.getAll(null);
			bookmarkList.getList().addAll(bookmarks);
			log.debug("getAll: {}", bookmarks);
			return bookmarkList;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
}
