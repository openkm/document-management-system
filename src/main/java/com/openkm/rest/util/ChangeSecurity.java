package com.openkm.rest.util;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "changeSecurity")
public class ChangeSecurity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nodeId;
    private GrantedUserList grantedUsersList = new GrantedUserList();
    private RevokedUserList revokedUsersList = new RevokedUserList();
    private GrantedRoleList grantedRolesList = new GrantedRoleList();
    private RevokedRoleList revokedRolesList = new RevokedRoleList();
    private boolean recursive;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public GrantedUserList getGrantedUsersList() {
        return grantedUsersList;
    }

    public void setGrantedUsersList(GrantedUserList grantedUsersList) {
        this.grantedUsersList = grantedUsersList;
    }

    public RevokedUserList getRevokedUsersList() {
        return revokedUsersList;
    }

    public void setRevokedUsersList(RevokedUserList revokedUsersList) {
        this.revokedUsersList = revokedUsersList;
    }

    public GrantedRoleList getGrantedRolesList() {
        return grantedRolesList;
    }

    public void setGrantedRolesList(GrantedRoleList grantedRolesList) {
        this.grantedRolesList = grantedRolesList;
    }

    public RevokedRoleList getRevokedRolesList() {
        return revokedRolesList;
    }

    public void setRevokedRolesList(RevokedRoleList revokedRolesList) {
        this.revokedRolesList = revokedRolesList;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("nodeId=").append(nodeId);
        sb.append(", grantedUsersList=").append(grantedUsersList);
        sb.append(", revokedUsersList=").append(revokedUsersList);
        sb.append(", grantedRolesList=").append(grantedRolesList);
        sb.append(", revokedRolesList=").append(revokedRolesList);
        sb.append(", recursive=").append(recursive);
        sb.append("}");
        return sb.toString();
    }
}