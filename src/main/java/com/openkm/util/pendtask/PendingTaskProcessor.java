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

package com.openkm.util.pendtask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.openkm.core.DatabaseException;
import com.openkm.dao.HibernateUtil;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.bean.*;
import com.openkm.spring.PrincipalUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Misc repository utilities and helpers.
 */
public class PendingTaskProcessor {
	private static Logger log = LoggerFactory.getLogger(PendingTaskProcessor.class);
	private static final String BEGIN = "begin";
	private static final String DOCS = "docs";
	private static final String END = "end";
	private static Type statusObjType = new TypeToken<LinkedList<NodeStatus>>() {
	}.getType();
	private LinkedList<NodeStatus> status = new LinkedList<NodeStatus>();
	private PendingTask pt = null;

	public PendingTaskProcessor(PendingTask pt) {
		this.pt = pt;
	}

	public LinkedList<NodeStatus> getStatus() {
		return status;
	}

	/**
	 * Process repository tree in depth.
	 */
	public void processInDepth(ProcessInDepthTask task) throws DatabaseException {
		log.debug("processInDepth({})", task);
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();

			// Begin recursive stuff
			PendingTaskExecutor.addRunningTask(pt.getId());
			processInDepth(session, task);

			// Remove completed pending task
			log.info("Auth: {}, Task: {}", PrincipalUtils.getUser(), pt);
			PendingTaskExecutor.removeRunningTask(pt.getId());
			session.delete(pt);

			HibernateUtil.commit(session.getTransaction());
		} catch (HibernateException e) {
			HibernateUtil.rollback(session.getTransaction());
		} catch (InterruptedException e) {
			HibernateUtil.commit(session.getTransaction());
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Process repository tree in depth.
	 */
	private void processInDepth(Session session, ProcessInDepthTask task) throws DatabaseException, InterruptedException {
		if (pt.getNode() != null) {
			NodeFolder fld = (NodeFolder) session.get(NodeFolder.class, pt.getNode());

			if (fld != null) {
				deserializeStatus(session);

				if (status.isEmpty()) {
					status.add(new NodeStatus(fld.getUuid(), fld.getUuid(), BEGIN));
				}

				// Process start node
				NodeBase parentNode = NodeBaseDAO.getInstance().getParentNode(session, fld.getUuid());
				task.doTask(session, parentNode, fld);

				// Begin recursive stuff
				processInDepthHelper(session, fld, task);

				if (status.getLast().getNode().equals(fld.getUuid())) {
					status.getLast().setStatus(END);
				}

				pruneStatus(session, fld.getUuid());
				serializeStatus(session);
			} else {
				log.error("Can't execute ProcessInDepth from a null folder");
			}
		} else {
			log.error("Error PendingTask defined with null UUID");
		}
	}

	@SuppressWarnings("unchecked")
	private void processInDepthHelper(Session session, NodeBase parentNode, ProcessInDepthTask task) throws DatabaseException,
			InterruptedException {
		log.debug("processInDepthHelper({}, {}, {}", new Object[]{session, parentNode, task});

		if (!PendingTaskExecutor.running) {
			log.info("### ### ### Executor flag disabled ### ### ###");
			throw new InterruptedException("Executor flag disabled");
		}

		if (matchNodeStatus(parentNode.getUuid(), BEGIN)) {
			log.debug("** Process DOCS: {}, Status: {}", getPath(session, parentNode.getUuid()), getNodeStatus(parentNode.getUuid()));
			String qsDoc = "from NodeDocument nd where nd.parent=:parent order by nd.uuid";
			Query qDoc = session.createQuery(qsDoc);
			qDoc.setString("parent", parentNode.getUuid());
			List<NodeDocument> lstDoc = qDoc.list();

			// Security Check
			task.securityPruneNodeList(lstDoc);

			for (NodeDocument nd : lstDoc) {
				task.doTask(session, parentNode, nd);
			}

			String qsMail = "from NodeMail nm where nm.parent=:parent order by nm.uuid";
			Query qMail = session.createQuery(qsMail);
			qMail.setString("parent", parentNode.getUuid());
			List<NodeMail> lstMail = qMail.list();

			// Security Check
			task.securityPruneNodeList(lstMail);

			for (NodeMail nm : lstMail) {
				task.doTask(session, parentNode, nm);

				// Process in depth
				processInDepthHelper(session, nm, task);
			}

			// Update status
			setNodeStatus(parentNode.getUuid(), DOCS);
			serializeStatus(session);
			log.debug("Path: {}, Status: {}", getPath(session, status.getLast().getNode()), status.getLast().getStatus());
		} else {
			log.debug("** Bypass DOCS: {}, Status: {}", getPath(session, parentNode.getUuid()), getNodeStatus(parentNode.getUuid()));
		}

		log.debug("** Process ITER: {}, Status: {}", getPath(session, parentNode.getUuid()), getNodeStatus(parentNode.getUuid()));
		String qsFld = "from NodeFolder nf where nf.parent=:parent order by nf.uuid";
		Query qFld = session.createQuery(qsFld);
		qFld.setString("parent", parentNode.getUuid());
		List<NodeFolder> lstFld = qFld.list();

		// Security Check
		task.securityPruneNodeList(lstFld);

		for (NodeFolder nf : lstFld) {
			if (!matchNodeStatus(nf.getUuid(), END)) {
				if (getNodeStatus(nf.getUuid()) == null) {
					status.add(new NodeStatus(nf.getUuid(), parentNode.getUuid(), BEGIN));
				}

				task.doTask(session, parentNode, nf);

				// Process in depth
				processInDepthHelper(session, nf, task);

				// Folder process finished
				log.debug("ENDED - {}", getPath(session, nf.getUuid()));

				setNodeStatus(nf.getUuid(), END);
				serializeStatus(session);
			} else {
				log.debug("** Bypass ITER: {}, Status: {}", getPath(session, nf.getUuid()), getNodeStatus(nf.getUuid()));
			}
		}

		// Remove completed folder
		pruneStatus(session, parentNode.getUuid());
		serializeStatus(session);
	}

	/**
	 * Prune status list.
	 */
	private void pruneStatus(Session session, String uuid) {
		log.debug("pruneStatus({})", getPath(session, uuid));

		for (ListIterator<NodeStatus> it = status.listIterator(status.size()); it.hasPrevious(); ) {
			NodeStatus nd = it.previous();

			if (uuid.equals(nd.getParent()) && nd.getStatus().equals(END)) {
				log.debug("REMOVE: {}", getPath(session, nd.getNode()));
				it.remove();
			} else {
				break;
			}
		}
	}

	/**
	 * Match node status.
	 */
	private boolean matchNodeStatus(String uuid, String st) {
		for (NodeStatus ns : status) {
			if (ns.getNode().equals(uuid) && ns.getStatus().equals(st)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Set node status.
	 */
	private void setNodeStatus(String uuid, String st) {
		for (NodeStatus ns : status) {
			if (ns.getNode().equals(uuid)) {
				ns.setStatus(st);
				break;
			}
		}
	}

	/**
	 * Get node status.
	 */
	private String getNodeStatus(String uuid) {
		for (NodeStatus ns : status) {
			if (ns.getNode().equals(uuid)) {
				return ns.getStatus();
			}
		}

		return null;
	}

	/**
	 * Store status.
	 */
	private void serializeStatus(Session session) throws DatabaseException {
		log.debug("serializeStatus({})", status);

		try {
			pt.setStatus(new Gson().toJson(status, statusObjType));
			session.update(pt);

			// Commit transaction
			HibernateUtil.commit(session.getTransaction());
			session.beginTransaction();
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	/**
	 * Retrieve status.
	 */
	private void deserializeStatus(Session session) throws DatabaseException {
		log.debug("deserializeStatus({})", pt.getStatus());

		try {
			if (pt.getStatus() != null) {
				status = decodeStatus(pt.getStatus());
			}
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	/**
	 * Convert from String to Json.
	 */
	public static LinkedList<NodeStatus> decodeStatus(String status) {
		return new Gson().fromJson(status, statusObjType);
	}

	private String getPath(Session session, String uuid) {
		try {
			return NodeBaseDAO.getInstance().getPathFromUuid(session, uuid);
		} catch (Exception e) {
			return null;
		}
	}
}
