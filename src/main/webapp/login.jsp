<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.util.FormatUtil" %>
<% if (FormatUtil.isMobile(request)) { %>
<jsp:include page="login_mobile.jsp">
  <jsp:param name="error" value="${param.error}"/>
</jsp:include>
<% } else { %>
<jsp:include page="login_desktop.jsp">
  <jsp:param name="error" value="${param.error}"/>
</jsp:include>
<% } %>
