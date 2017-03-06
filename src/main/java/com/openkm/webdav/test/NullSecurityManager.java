package com.openkm.webdav.test;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Request.Method;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.http11.auth.DigestResponse;

public class NullSecurityManager implements com.bradmcevoy.http.SecurityManager {
	String realm;

	public Object authenticate(String user, String password) {
		return user;
	}

	public Object authenticate(DigestResponse digestRequest) {
		return digestRequest.getUser();
	}

	public boolean authorise(Request request, Method method, Auth auth, Resource resource) {
		return true;
	}

	public String getRealm(String host) {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public boolean isDigestAllowed() {
		return true;
	}
}
