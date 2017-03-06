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
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <link rel="stylesheet" type="text/css" href="css/fixedTableHeader.css" />
  <script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
  <script type="text/javascript" src="js/fixedTableHeader.js"></script>
  <title>Document expiration group list</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url value="DocumentExpiration" var="urlSyncUsers">
        <c:param name="action" value="syncUsers"/>
      </c:url>
      <c:url value="DocumentExpiration" var="urlSyncRoles">
        <c:param name="action" value="syncRoles"/>
      </c:url>
      <c:url value="DocumentExpiration" var="urlClean">
        <c:param name="action" value="clean"/>
      </c:url>
      <ul id="breadcrumb">
        <li class="path">
          <a href="DocumentExpiration">Document expiration</a>
        </li>
        <li class="action">
          <a href="${urlClean}">
            <img src="img/action/generic.png" alt="Generic" title="Generic" style="vertical-align: middle;"/>
            Clean
          </a>
        </li>
        <li class="action">
          <a href="${urlSyncRoles}">
            <img src="img/action/generic.png" alt="Generic" title="Generic" style="vertical-align: middle;"/>
            Sync Roles
          </a>
        </li>
        <li class="action">
          <a href="${urlSyncUsers}">
            <img src="img/action/generic.png" alt="Generic" title="Generic" style="vertical-align: middle;"/>
            Sync Users
          </a>
        </li>
      </ul>
      <br/>
      <table class="results" width="20%">
        <thead>
          <tr>
            <th>Group</th>
            <c:url value="DocumentExpiration" var="urlCreate">
              <c:param name="action" value="groupCreate"/>
            </c:url>
            <th width="50px"><a href="${urlCreate}"><img src="img/action/new.png" alt="New group" title="New group"/></a></th>
          </tr>
        </thead>
        <c:forEach var="group" items="${groups}" varStatus="row">
          <c:url value="DocumentExpiration" var="urlDelete">
            <c:param name="action" value="groupDelete"/>
            <c:param name="gru_name" value="${group}"/>
          </c:url>
          <c:url value="DocumentExpiration" var="urlEdit">
            <c:param name="action" value="groupEdit"/>
            <c:param name="gru_name" value="${group}"/>
          </c:url>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
          	<td>${group}</td>
          	<td align="center">
          	  <a href="${urlEdit}"><img src="img/action/edit.png" alt="Edit" title="Edit"/></a>
              &nbsp;
          	  <a href="${urlDelete}"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a>
          	</td>
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