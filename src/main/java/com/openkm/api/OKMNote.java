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

package com.openkm.api;

import com.openkm.bean.Note;
import com.openkm.core.*;
import com.openkm.module.ModuleManager;
import com.openkm.module.NoteModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author pavila
 *
 */
public class OKMNote implements NoteModule {
	private static Logger log = LoggerFactory.getLogger(OKMNote.class);
	private static OKMNote instance = new OKMNote();

	private OKMNote() {
	}

	public static OKMNote getInstance() {
		return instance;
	}

	@Override
	public Note add(String token, String nodeId, String text) throws LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("add({}, {}, {})", new Object[]{token, nodeId, text});
		NoteModule nm = ModuleManager.getNoteModule();
		Note ret = nm.add(token, nodeId, text);
		log.debug("add: {}", ret);
		return ret;
	}

	@Override
	public Note get(String token, String noteId) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("get({}, {})", token, noteId);
		NoteModule nm = ModuleManager.getNoteModule();
		Note ret = nm.get(token, noteId);
		log.debug("get: {}", ret);
		return ret;
	}

	@Override
	public void delete(String token, String noteId) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("delete({}, {})", token, noteId);
		NoteModule nm = ModuleManager.getNoteModule();
		nm.delete(token, noteId);
		log.debug("delete: void");
	}

	@Override
	public String set(String token, String noteId, String text) throws LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("set({}, {}, {})", new Object[]{token, noteId, text});
		NoteModule nm = ModuleManager.getNoteModule();
		String ret = nm.set(token, noteId, text);
		log.debug("set: {}", ret);
		return ret;
	}

	@Override
	public List<Note> list(String token, String nodeId) throws AccessDeniedException, PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("list({}, {})", token, nodeId);
		NoteModule nm = ModuleManager.getNoteModule();
		List<Note> col = nm.list(token, nodeId);
		log.debug("list: {}", col);
		return col;
	}
}
