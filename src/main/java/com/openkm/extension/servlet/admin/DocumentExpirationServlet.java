package com.openkm.extension.servlet.admin;

import com.openkm.api.OKMAuth;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.dao.AuthDAO;
import com.openkm.dao.HibernateUtil;
import com.openkm.dao.bean.DatabaseMetadataValue;
import com.openkm.dao.bean.Role;
import com.openkm.dao.bean.User;
import com.openkm.extension.dao.ExtensionDAO;
import com.openkm.principal.DatabasePrincipalAdapter;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.servlet.admin.BaseServlet;
import com.openkm.util.WebUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * DocumentExpirationServlet
 *
 * @author jllort
 */
public class DocumentExpirationServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static boolean db = Config.PRINCIPAL_ADAPTER.equals(DatabasePrincipalAdapter.class.getCanonicalName());
	private static Logger log = LoggerFactory.getLogger(DocumentExpirationServlet.class);
	private static final String UUID = "988fde2d-e456-4a59-ab89-d5ba158adfd5";

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		updateSessionManager(request);

		try {
			if (action.equals("groupEdit")) {
				edit(request, response);
			} else if (action.equals("groupDelete")) {
				delete(request, response);
			} else if (action.equals("groupCreate")) {
				create(request, response);
			} else if (action.equals("syncUsers")) {
				syncUsers(request, response);
			} else if (action.equals("syncRoles")) {
				syncRoles(request, response);
			} else if (action.equals("clean")) {
				clean(request, response);
			}

			if (action.equals("") || WebUtils.getBoolean(request, "persist") || action.startsWith("sync") || action.equals("clean")) {
				groupList(request, response);
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}
	}

	/**
	 * Group List
	 */
	@SuppressWarnings("unchecked")
	private void groupList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DatabaseException,
			PrincipalAdapterException {
		log.debug("groupList({}, {})", new Object[]{request, response});
		ServletContext sc = getServletContext();
		String qs = "select distinct (dmv.col00) from DatabaseMetadataValue dmv where dmv.table='group' order by dmv.col00";
		org.hibernate.Session dbSession = null;
		Transaction tx = null;

		try {
			dbSession = HibernateUtil.getSessionFactory().openSession();
			tx = dbSession.beginTransaction();
			Query q = dbSession.createQuery(qs);
			List<String> groups = q.list();
			sc.setAttribute("groups", groups);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(dbSession);
		}

		sc.getRequestDispatcher("/admin/document_expiration_group_list.jsp").forward(request, response);
		log.debug("groupList: void");
	}

	/**
	 * Edit
	 */
	@SuppressWarnings("unchecked")
	private void edit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DatabaseException,
			PrincipalAdapterException {
		log.debug("edit({}, {})", new Object[]{request, response});
		String group = WebUtils.getString(request, "gru_name");
		org.hibernate.Session dbSession = null;
		Transaction tx = null;

		try {
			dbSession = HibernateUtil.getSessionFactory().openSession();
			tx = dbSession.beginTransaction();

			if (WebUtils.getBoolean(request, "persist")) {
				String qs = "delete from DatabaseMetadataValue dmv where dmv.table='group' and dmv.col00=:group";
				Query q = dbSession.createQuery(qs);
				q.setParameter("group", group);
				q.executeUpdate();
				List<String> users = WebUtils.getStringList(request, "users");

				for (String user : users) {
					DatabaseMetadataValue dmv = new DatabaseMetadataValue();
					dmv.setTable("group");
					dmv.setCol00(group);
					dmv.setCol01(user);
					dbSession.save(dmv);
					// DatabaseMetadataDAO.createValue(dmv);
				}

				HibernateUtil.commit(tx);
			} else {
				ServletContext sc = getServletContext();
				String qs = "select dmv.col01 from DatabaseMetadataValue dmv where dmv.table='group' and dmv.col00=:group";
				Query q = dbSession.createQuery(qs);
				q.setParameter("group", group);
				List<String> users = q.list();
				HibernateUtil.commit(tx);
				List<String> availableUsers = new ArrayList<String>();

				if (db) {
					for (User user : AuthDAO.findAllUsers(false)) {
						availableUsers.add(user.getId());
					}
				} else {
					for (String user : OKMAuth.getInstance().getUsers(null)) {
						availableUsers.add(user);
					}
				}

				sc.setAttribute("action", WebUtils.getString(request, "action"));
				sc.setAttribute("group", group);
				sc.setAttribute("users", users);
				sc.setAttribute("availableUsers", availableUsers);
				sc.setAttribute("persist", true);
				sc.getRequestDispatcher("/admin/document_expiration_group_edit.jsp").forward(request, response);
			}
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(dbSession);
		}

		log.debug("edit: void");
	}

	/**
	 * Create
	 */
	private void create(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DatabaseException,
			PrincipalAdapterException {
		log.debug("create({}, {})", new Object[]{request, response});
		String group = WebUtils.getString(request, "gru_name");
		org.hibernate.Session dbSession = null;
		Transaction tx = null;

		try {
			dbSession = HibernateUtil.getSessionFactory().openSession();
			tx = dbSession.beginTransaction();

			if (WebUtils.getBoolean(request, "persist")) {
				String qs = "delete from DatabaseMetadataValue dmv where dmv.table='group' and dmv.col00=:group";
				Query q = dbSession.createQuery(qs);
				q.setParameter("group", group);
				q.executeUpdate();

				List<String> users = WebUtils.getStringList(request, "users");
				for (String user : users) {
					DatabaseMetadataValue dmv = new DatabaseMetadataValue();
					dmv.setTable("group");
					dmv.setCol00(group);
					dmv.setCol01(user);
					dbSession.save(dmv);
					// DatabaseMetadataDAO.createValue(dmv);
				}

				HibernateUtil.commit(tx);
			} else {
				ServletContext sc = getServletContext();
				List<String> availableUsers = new ArrayList<String>();

				if (db) {
					for (User user : AuthDAO.findAllUsers(false)) {
						availableUsers.add(user.getId());
					}
				} else {
					for (String user : OKMAuth.getInstance().getUsers(null)) {
						availableUsers.add(user);
					}
				}

				sc.setAttribute("action", WebUtils.getString(request, "action"));
				sc.setAttribute("group", group);
				sc.setAttribute("users", new ArrayList<String>());
				sc.setAttribute("availableUsers", availableUsers);
				sc.setAttribute("persist", true);
				sc.getRequestDispatcher("/admin/document_expiration_group_edit.jsp").forward(request, response);
			}
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(dbSession);
		}

		log.debug("create: void");
	}

	/**
	 * Delete
	 */
	@SuppressWarnings("unchecked")
	private void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DatabaseException,
			PrincipalAdapterException {
		log.debug("delete({}, {})", new Object[]{request, response});
		String group = WebUtils.getString(request, "gru_name");
		org.hibernate.Session dbSession = null;
		Transaction tx = null;

		try {
			dbSession = HibernateUtil.getSessionFactory().openSession();
			tx = dbSession.beginTransaction();

			if (WebUtils.getBoolean(request, "persist")) {
				String qs = "delete from DatabaseMetadataValue dmv where dmv.table='group' and dmv.col00=:group";
				Query q = dbSession.createQuery(qs);
				q.setParameter("group", group);
				q.executeUpdate();
				HibernateUtil.commit(tx);
			} else {
				ServletContext sc = getServletContext();
				String qs = "select dmv.col01 from DatabaseMetadataValue dmv where dmv.table='group' and dmv.col00=:group";
				Query q = dbSession.createQuery(qs);
				q.setParameter("group", group);
				List<String> users = q.list();
				HibernateUtil.commit(tx);
				List<String> availableUsers = new ArrayList<String>();

				if (db) {
					for (User user : AuthDAO.findAllUsers(false)) {
						availableUsers.add(user.getId());
					}
				} else {
					for (String user : OKMAuth.getInstance().getUsers(null)) {
						availableUsers.add(user);
					}
				}

				sc.setAttribute("action", WebUtils.getString(request, "action"));
				sc.setAttribute("group", group);
				sc.setAttribute("users", users);
				sc.setAttribute("availableUsers", availableUsers);
				sc.setAttribute("persist", true);
				sc.getRequestDispatcher("/admin/document_expiration_group_edit.jsp").forward(request, response);
			}
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(dbSession);
		}

		log.debug("delete: void");
	}

	/**
	 * syncUsers
	 */
	private void syncUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DatabaseException,
			PrincipalAdapterException {
		log.debug("syncUsers({}, {})", new Object[]{request, response});
		org.hibernate.Session dbSession = null;
		Transaction tx = null;

		try {
			dbSession = HibernateUtil.getSessionFactory().openSession();
			tx = dbSession.beginTransaction();
			List<String> availableUsers = new ArrayList<String>();

			if (db) {
				for (User user : AuthDAO.findAllUsers(false)) {
					availableUsers.add(user.getId());
				}
			} else {
				for (String user : OKMAuth.getInstance().getUsers(null)) {
					availableUsers.add(user);
				}
			}

			// Delete all users
			for (String user : availableUsers) {
				String qs = "delete from DatabaseMetadataValue dmv where dmv.table='group' and dmv.col00=:group";
				Query q = dbSession.createQuery(qs);
				q.setParameter("group", user);
				q.executeUpdate();
			}

			// Create all users
			for (String user : availableUsers) {
				DatabaseMetadataValue dmv = new DatabaseMetadataValue();
				dmv.setTable("group");
				dmv.setCol00(user);
				dmv.setCol01(user);
				dbSession.save(dmv);
				// DatabaseMetadataDAO.createValue(dmv);
			}
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(dbSession);
		}

		log.debug("syncUsers: void");
	}

	/**
	 * syncRoles
	 */
	private void syncRoles(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DatabaseException,
			PrincipalAdapterException {
		log.debug("syncRoles({}, {})", new Object[]{request, response});
		org.hibernate.Session dbSession = null;
		Transaction tx = null;

		try {
			dbSession = HibernateUtil.getSessionFactory().openSession();
			tx = dbSession.beginTransaction();
			List<String> availableRoles = new ArrayList<String>();

			if (db) {
				for (Role role : AuthDAO.findAllRoles()) {
					availableRoles.add(role.getId());
				}
			} else {
				for (String role : OKMAuth.getInstance().getRoles(null)) {
					availableRoles.add(role);
				}
			}

			// Delete all roles
			for (String role : availableRoles) {
				String qs = "delete from DatabaseMetadataValue dmv where dmv.table='group' and dmv.col00=:group";
				Query q = dbSession.createQuery(qs);
				q.setParameter("group", role);
				q.executeUpdate();

			}

			// Create all roles
			for (String role : availableRoles) {
				List<String> users = new ArrayList<String>();

				if (db) {
					for (User user : AuthDAO.findUsersByRole(role, false)) {
						users.add(user.getId());
					}
				} else {
					for (String user : OKMAuth.getInstance().getUsersByRole(null, role)) {
						users.add(user);
					}
				}

				for (String user : users) {
					DatabaseMetadataValue dmv = new DatabaseMetadataValue();
					dmv.setTable("group");
					dmv.setCol00(role);
					dmv.setCol01(user);
					dbSession.save(dmv);
					// DatabaseMetadataDAO.createValue(dmv);
				}

			}

			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(dbSession);
		}

		log.debug("syncRoles: void");
	}

	/**
	 * clean
	 */
	private void clean(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DatabaseException,
			PrincipalAdapterException {
		log.debug("clean({}, {})", new Object[]{request, response});
		org.hibernate.Session dbSession = null;
		Transaction tx = null;

		try {
			dbSession = HibernateUtil.getSessionFactory().openSession();
			tx = dbSession.beginTransaction();
			String qs = "delete from DatabaseMetadataValue dmv where dmv.table='group'";
			Query q = dbSession.createQuery(qs);
			q.executeUpdate();
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(dbSession);
		}

		log.debug("clean: void");
	}

	/**
	 * isDocumentExpiration
	 */
	public static boolean isDocumentExpiration() throws DatabaseException {
		return isRegistered(ExtensionDAO.findAllUuids());
	}

	/**
	 * isRegistered
	 */
	private static boolean isRegistered(List<String> uuidList) {
		return uuidList.contains(UUID);
	}
}