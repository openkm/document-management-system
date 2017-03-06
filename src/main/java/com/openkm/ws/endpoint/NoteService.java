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

package com.openkm.ws.endpoint;

import com.openkm.bean.Note;
import com.openkm.core.*;
import com.openkm.module.ModuleManager;
import com.openkm.module.NoteModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(name = "OKMNote", serviceName = "OKMNote", targetNamespace = "http://ws.openkm.com")
public class NoteService {
	private static Logger log = LoggerFactory.getLogger(NoteService.class);

	@WebMethod
	public Note add(@WebParam(name = "token") String token, @WebParam(name = "nodePath") String nodePath,
	                @WebParam(name = "text") String text) throws LockException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("add({}, {}, {})", new Object[]{token, nodePath, text});
		NoteModule nm = ModuleManager.getNoteModule();
		Note ret = nm.add(token, nodePath, text);
		log.debug("addNote: {}", ret);
		return ret;
	}

	@WebMethod
	public Note get(@WebParam(name = "token") String token, @WebParam(name = "notePath") String notePath) throws LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("get({}, {})", token, notePath);
		NoteModule nm = ModuleManager.getNoteModule();
		Note ret = nm.get(token, notePath);
		log.debug("get: {}", ret);
		return ret;
	}

	@WebMethod
	public void delete(@WebParam(name = "token") String token, @WebParam(name = "notePath") String notePath)
			throws LockException, PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("remove({}, {})", token, notePath);
		NoteModule nm = ModuleManager.getNoteModule();
		nm.delete(token, notePath);
		log.debug("remove: void");
	}

	@WebMethod
	public void set(@WebParam(name = "token") String token, @WebParam(name = "notePath") String notePath,
	                @WebParam(name = "text") String text) throws LockException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("set({}, {}, {})", new Object[]{token, notePath, text});
		NoteModule nm = ModuleManager.getNoteModule();
		nm.set(token, notePath, text);
		log.debug("set: void");
	}

	@WebMethod
	public Note[] list(@WebParam(name = "token") String token, @WebParam(name = "nodePath") String nodePath)
			throws LockException, PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("list({}, {})", token, nodePath);
		NoteModule nm = ModuleManager.getNoteModule();
		List<Note> col = nm.list(token, nodePath);
		Note[] result = (Note[]) col.toArray(new Note[col.size()]);
		log.debug("list: {}", result);
		return result;
	}
}
