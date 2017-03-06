<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.core.HttpSessionManager" %>
<%@ page errorPage="error.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
	HttpSessionManager.getInstance().add(request);
	com.openkm.api.OKMAuth.getInstance().login();
%>
<c:choose>
  <c:when test="${not empty param.uuid}">
    <c:redirect url="Desktop">
      <c:param name="action" value="directOpen"/>
      <c:param name="uuid" value="${param.uuid}"/>
	</c:redirect>
  </c:when>
  <c:when test="${not empty param.docPath}">
    <c:redirect url="Desktop">
      <c:param name="action" value="directOpen"/>
      <c:param name="docPath" value="${param.docPath}"/>
	</c:redirect>
  </c:when>
  <c:when test="${not empty param.fldPath}">
    <c:redirect url="Desktop">
      <c:param name="action" value="directOpen"/>
      <c:param name="fldPath" value="${param.fldPath}"/>
	</c:redirect>
  </c:when>
  <c:when test="${not empty param.mailPath}">
    <c:redirect url="Desktop">
      <c:param name="action" value="directOpen"/>
      <c:param name="mailPath" value="${param.mailPath}"/>
	</c:redirect>
  </c:when>
  <c:otherwise>
    <c:redirect url="home.jsp">
	</c:redirect>  
  </c:otherwise>
</c:choose>