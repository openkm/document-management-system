<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" href="css/style.css" type="text/css" />
  <title>Logged users</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Auth">User list</a>
        </li>
        <li class="path">Logged users</li>
        <li class="action">
          <a href="LoggedUsers">
          	<img src="img/action/refresh.png" alt="Refresh" title="Refresh" style="vertical-align: middle;"/>
          	Refresh
          </a>
        </li>
      </ul>
      <br/>
      <table class="results" width="80%">
        <tr><th>#</th><th>User</th><th>Session id</th><th>Remote IP</th><th>Remote host</th><th>Creation</th><th>Last access</th></tr>
        <c:forEach var="se" items="${sessions}" varStatus="row">
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td width="20px">${row.index + 1}</td><td>${se.user}</td><td>${se.id}</td><td>${se.ip}</td><td>${se.host}</td>
            <td><u:formatDate calendar="${se.creation}"/></td>
            <td><u:formatDate calendar="${se.lastAccess}"/></td>
          </tr>
        </c:forEach>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>