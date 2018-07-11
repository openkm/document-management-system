<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page errorPage="general-error.jsp" %>
<%@ page import="com.openkm.core.Config" %>
<%@ page import="com.openkm.util.FormatUtil" %>
<%@ page import="com.openkm.util.WebUtils" %>
<%@ page import="java.net.URLEncoder" %>
<%
	request.setCharacterEncoding("UTF-8");
	String url = null;
	String docPath = WebUtils.getString(request, "docPath", null);
	String fldPath = WebUtils.getString(request, "fldPath", null);
	String uuid = WebUtils.getString(request, "uuid", null);
	
	if (FormatUtil.isMobile(request)) {
		url = Config.MOBILE_CONTEXT + "/index.jsp";
	} else {
		url = "frontend/index.jsp";
	}
	
	if (uuid != null) {
		url += "?uuid=" + uuid;
	} else if (docPath != null) {
		url += "?docPath=" + URLEncoder.encode(docPath, "UTF-8");
	} else if (fldPath != null) {
		url += "?fldPath=" + URLEncoder.encode(fldPath, "UTF-8");
	}
	
	if (!Config.DEFAULT_LANG.equals("")) {
		if (docPath != null || fldPath != null || uuid != null) {
			url += "&lang=" + Config.DEFAULT_LANG;
		} else {
			url += "?lang=" + Config.DEFAULT_LANG;
		}
	}
	
	// Go to party
	response.sendRedirect(url);
%>
