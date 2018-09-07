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

package com.openkm.rest.endpoint;

import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.module.AuthModule;
import com.openkm.module.ModuleManager;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.rest.GenericException;
import com.openkm.rest.util.*;

import io.swagger.annotations.Api;

@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Api(description="auth-service", value="auth-service")
@Path("/auth")
public class AuthService {
	private static Logger log = LoggerFactory.getLogger(AuthService.class);

	@GET
	@Path("/login")
	public void login() throws GenericException {
		try {
			log.debug("login()");
			AuthModule am = ModuleManager.getAuthModule();
			am.login();
			log.debug("login: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getGrantedRoles")
	public GrantedRoleList getGrantedRoles(@QueryParam("nodeId") String nodeId) throws GenericException {
		try {
			log.debug("getGrantedRoles({})", nodeId);
			AuthModule am = ModuleManager.getAuthModule();
			GrantedRoleList grl = new GrantedRoleList();
			Map<String, Integer> hm = am.getGrantedRoles(null, nodeId);

			// Marshall HashMap
			for (Entry<String, Integer> entry : hm.entrySet()) {
				GrantedRole gr = new GrantedRole();
				gr.setRole(entry.getKey());
				gr.setPermissions(entry.getValue());
				grl.getList().add(gr);
			}

			log.debug("getGrantedRoles: {}", grl);
			return grl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getGrantedUsers")
	public GrantedUserList getGrantedUsers(@QueryParam("nodeId") String nodeId) throws GenericException {
		try {
			log.debug("getGrantedUsers({})", nodeId);
			AuthModule am = ModuleManager.getAuthModule();
			GrantedUserList gul = new GrantedUserList();
			Map<String, Integer> hm = am.getGrantedUsers(null, nodeId);

			// Marshall HashMap
			for (Entry<String, Integer> entry : hm.entrySet()) {
				GrantedUser gu = new GrantedUser();
				gu.setUser(entry.getKey());
				gu.setPermissions(entry.getValue());
				gul.getList().add(gu);
			}

			log.debug("getGrantedUsers: {}", gul);
			return gul;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getRoles")
	public RoleList getRoles() throws PrincipalAdapterException {
		try {
			log.debug("getRoles()");
			AuthModule am = ModuleManager.getAuthModule();
			RoleList rl = new RoleList();
			rl.getList().addAll(am.getRoles(null));
			log.debug("getRoles: {}", rl);
			return rl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getUsers")
	public UserList getUsers() throws PrincipalAdapterException {
		try {
			log.debug("getUsers()");
			AuthModule am = ModuleManager.getAuthModule();
			UserList ul = new UserList();
			ul.getList().addAll(am.getUsers(null));
			log.debug("getUsers: {]", ul);
			return ul;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/grantRole")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void grantRole(@FormParam("nodeId") String nodeId, @FormParam("role") String role, @FormParam("permissions") int permissions,
	                      @FormParam("recursive") boolean recursive) throws GenericException {
		try {
			log.debug("grantRole({}, {}, {}, {})", new Object[]{nodeId, role, permissions, recursive});
			AuthModule am = ModuleManager.getAuthModule();
			am.grantRole(null, nodeId, role, permissions, recursive);
			log.debug("grantRole: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/grantUser")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void grantUser(@FormParam("nodeId") String nodeId, @FormParam("user") String user, @FormParam("permissions") int permissions,
	                      @DefaultValue("false") @FormParam("recursive") boolean recursive) throws GenericException {
		try {
			log.debug("grantUser({}, {}, {}, {})", new Object[]{nodeId, user, permissions, recursive});
			AuthModule am = ModuleManager.getAuthModule();
			am.grantUser(null, nodeId, user, permissions, recursive);
			log.debug("grantUser: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/revokeRole")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void revokeRole(@FormParam("nodeId") String nodeId, @FormParam("role") String role, @FormParam("permissions") int permissions,
	                       @FormParam("recursive") boolean recursive) throws GenericException {
		try {
			log.debug("revokeRole({}, {}, {}, {})", new Object[]{nodeId, role, permissions, recursive});
			AuthModule am = ModuleManager.getAuthModule();
			am.revokeRole(null, nodeId, role, permissions, recursive);
			log.debug("revokeRole: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/revokeUser")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void revokeUser(@FormParam("nodeId") String nodeId, @FormParam("user") String user, @FormParam("permissions") int permissions,
	                       @FormParam("recursive") boolean recursive) throws GenericException {
		try {
			log.debug("revokeUser({}, {}, {}, {})", new Object[]{nodeId, user, permissions, recursive});
			AuthModule am = ModuleManager.getAuthModule();
			am.revokeUser(null, nodeId, user, permissions, recursive);
			log.debug("revokeUser: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getUsersByRole/{role}")
	public UserList getUsersByRole(@PathParam("role") String role) throws GenericException {
		try {
			log.debug("getUsersByRole({})", role);
			AuthModule am = ModuleManager.getAuthModule();
			UserList ul = new UserList();
			ul.getList().addAll(am.getUsersByRole(null, role));
			log.debug("getUsersByRole: {}", ul);
			return ul;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getRolesByUser/{user}")
	public RoleList getRolesByUser(@PathParam("user") String user) throws GenericException {
		try {
			log.debug("getRolesByUser({})", user);
			AuthModule am = ModuleManager.getAuthModule();
			RoleList rl = new RoleList();
			rl.getList().addAll(am.getRolesByUser(null, user));
			log.debug("getRolesByUser: {}", rl);
			return rl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getMail/{user}")
	public String getMail(@PathParam("user") String user) throws GenericException {
		try {
			log.debug("getMail({})", user);
			AuthModule am = ModuleManager.getAuthModule();
			String ret = am.getMail(null, user);
			log.debug("getMail: {}", ret);
			return ret;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getName/{user}")
	public String getName(@PathParam("user") String user) throws GenericException {
		try {
			log.debug("getName({})", user);
			AuthModule am = ModuleManager.getAuthModule();
			String ret = am.getName(null, user);
			log.debug("getName: {}", ret);
			return ret;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@POST
	@Path("/createUser")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void createUser(@FormParam("user") String user, @FormParam("password") String password, @FormParam("email") String email,
	                       @FormParam("name") String name, @FormParam("active") boolean active) throws GenericException {
		try {
			log.debug("createUser({}, {}, {}, {}, {})", new Object[]{user, password, email, name, active});
			AuthModule am = ModuleManager.getAuthModule();
			am.createUser(null, user, password, email, name, active);
			log.debug("createUser: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@DELETE
	@Path("/deleteUser")
	public void deleteUser(@QueryParam("user") String user) throws GenericException {
		try {
			log.debug("deleteUser({})", new Object[]{user});
			AuthModule am = ModuleManager.getAuthModule();
			am.deleteUser(null, user);
			log.debug("deleteUser: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/updateUser")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void updateUser(@FormParam("user") String user, @FormParam("password") String password, @FormParam("email") String email,
	                       @FormParam("name") String name, @FormParam("active") boolean active) throws GenericException {
		try {
			log.debug("updateUser({}, {}, {}, {}, {})", new Object[]{user, password, email, name, active});
			AuthModule am = ModuleManager.getAuthModule();
			am.updateUser(null, user, password, email, name, active);
			log.debug("updateUser: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@POST
	@Path("/createRole")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void createRole(@FormParam("role") String role, @FormParam("active") boolean active) throws GenericException {
		try {
			log.debug("createRole({}, {})", new Object[]{role, active});
			AuthModule am = ModuleManager.getAuthModule();
			am.createRole(null, role, active);
			log.debug("createRole: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@DELETE
	@Path("/deleteRole")
	public void deleteRole(@QueryParam("role") String role) throws GenericException {
		try {
			log.debug("deleteRole({})", new Object[]{role});
			AuthModule am = ModuleManager.getAuthModule();
			am.deleteRole(null, role);
			log.debug("deleteRole: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/updateRole")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void updateRole(@FormParam("role") String role, @FormParam("active") boolean active) throws GenericException {
		try {
			log.debug("updateRole({}, {})", new Object[]{role, active});
			AuthModule am = ModuleManager.getAuthModule();
			am.updateRole(null, role, active);
			log.debug("updateRole: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/assignRole")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void assignRole(@FormParam("user") String user, @FormParam("role") String role) throws GenericException {
		try {
			log.debug("assignRole({}, {})", new Object[]{user, role});
			AuthModule am = ModuleManager.getAuthModule();
			am.assignRole(null, user, role);
			log.debug("assignRole: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@PUT
	@Path("/removeRole")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void removeRole(@FormParam("user") String user, @FormParam("role") String role) throws GenericException {
		try {
			log.debug("removeRole({}, {})", new Object[]{user, role});
			AuthModule am = ModuleManager.getAuthModule();
			am.removeRole(null, user, role);
			log.debug("removeRole: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
}
