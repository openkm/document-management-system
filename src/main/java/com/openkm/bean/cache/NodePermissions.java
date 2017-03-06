package com.openkm.bean.cache;

import java.io.Serializable;
import java.util.Set;

public class NodePermissions implements Serializable {
	private static final long serialVersionUID = -895133213627179445L;
	private Set<String> usersRead;
	private Set<String> usersWrite;
	private Set<String> usersDelete;
	private Set<String> usersSecurity;
	private Set<String> rolesRead;
	private Set<String> rolesWrite;
	private Set<String> rolesDelete;
	private Set<String> rolesSecurity;

	public Set<String> getUsersRead() {
		return usersRead;
	}

	public void setUsersRead(Set<String> usersRead) {
		this.usersRead = usersRead;
	}

	public Set<String> getUsersWrite() {
		return usersWrite;
	}

	public void setUsersWrite(Set<String> usersWrite) {
		this.usersWrite = usersWrite;
	}

	public Set<String> getRolesRead() {
		return rolesRead;
	}

	public void setRolesRead(Set<String> rolesRead) {
		this.rolesRead = rolesRead;
	}

	public Set<String> getRolesWrite() {
		return rolesWrite;
	}

	public void setRolesWrite(Set<String> rolesWrite) {
		this.rolesWrite = rolesWrite;
	}

	public Set<String> getUsersDelete() {
		return usersDelete;
	}

	public void setUsersDelete(Set<String> usersDelete) {
		this.usersDelete = usersDelete;
	}

	public Set<String> getRolesDelete() {
		return rolesDelete;
	}

	public void setRolesDelete(Set<String> rolesDelete) {
		this.rolesDelete = rolesDelete;
	}

	public Set<String> getUsersSecurity() {
		return usersSecurity;
	}

	public void setUsersSecurity(Set<String> usersSecurity) {
		this.usersSecurity = usersSecurity;
	}

	public Set<String> getRolesSecurity() {
		return rolesSecurity;
	}

	public void setRolesSecurity(Set<String> rolesSecurity) {
		this.rolesSecurity = rolesSecurity;
	}
}
