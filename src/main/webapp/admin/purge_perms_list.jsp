<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css?v=%{TIMESTAMP}%" />
  <title>Rebuild Indexes</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="utilities.jsp">Utilities</a>
        </li>
        <li class="path">
          <a href="purge_perms.jsp">Purge permissions</a>
        </li>
        <li class="path">${type} list</li>
      </ul>
      <br/>
      <form action="PurgePermissions">
        <table class="results" align="center" width="175px">
          <tr><th>${type}</th><th>Action</th></tr>
          <c:forEach var="elto" items="${elements}" varStatus="row">
            <c:url value="PurgePermissions" var="urlDelete">
              <c:param name="action" value="purge${type}" />
              <c:param name="elto" value="${elto}" />
            </c:url>
            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
              <td>${elto}</td>
              <td align="center">
                <a href="${urlDelete}"><img src="img/action/delete.png" alt="Delete" title="Delete" /></a>
              </td>
            </tr>
          </c:forEach>
        </table>
      </form>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>
