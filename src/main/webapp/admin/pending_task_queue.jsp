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
  <link rel="stylesheet" type="text/css" href="css/fixedTableHeader.css" />
  <script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
  <script type="text/javascript" src="js/fixedTableHeader.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
    	TABLE.fixHeader('#trick');
	});
  </script>
  <title>Text Extraction Queue</title>
</head>
<body>
  <u:constantsMap className="com.openkm.dao.bean.PendingTask" var="PendingTask"/>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="PendingTaskQueue">Pending task queue</a>
        </li>
        <li class="action">
          <a href="stats.jsp">
            <img src="img/action/generic.png" alt="Generic" title="Generic" style="vertical-align: middle;"/>
            Statistics
          </a>
        </li>
        <li class="action">
          <a href="PendingTaskQueue">
            <img src="img/action/refresh.png" alt="Refresh" title="Refresh" style="vertical-align: middle;"/>
            Refresh
          </a>
        </li>
      </ul>
      <br/>
      <table class="results" width="90%">
        <thead>
          <tr><th>#</th><th>Date</th><th>Running</th><th>Path</th><th>Params</th><th>Status</th></tr>
        </thead>
        <tbody>
          <c:forEach var="task" items="${pendingTasks}" varStatus="row">
            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
              <td>${row.index + 1}</td>
              <td nowrap="nowrap"><u:formatDate calendar="${task.created}"/></td>
              <td width="50px" align="center">
                <c:choose>
                  <c:when test="${task.running}">
                    <img src="img/true.png" alt="True" title="True"/>
                  </c:when>
                  <c:otherwise>
                    <img src="img/false.png" alt="False" title="False"/>
                  </c:otherwise>
                </c:choose>
              </td>
              <td>${task.nodePath}</td>
              <td valign="top">
                <c:choose>
                  <c:when test="${task.task == PendingTask.TASK_CHANGE_SECURITY}">
                    <table width="100%">
                      <tr><td>User:</td><td>${task.params.user}</td></tr>
                      <tr><td>Roles:</td><td>${task.params.roles}</td></tr>
                      <tr><td>GrantUsers:</td><td>${task.params.grantUsers}</td></tr>
                      <tr><td>RevokeUsers:</td><td>${task.params.revokeUsers}</td></tr>
                      <tr><td>GrantRoles:</td><td>${task.params.grantRoles}</td></tr>
                      <tr><td>RevokeRoles:</td><td>${task.params.revokeRoles}</td></tr>
                    </table>
                  </c:when>
                  <c:otherwise>${task.params}</c:otherwise>
                </c:choose>
              </td>
              <td valign="top">
                <table width="100%">
                  <c:forEach var="ndst" items="${task.status}">
                    <tr><td>${ndst.nodePath}</td><td>${ndst.status}</td></tr>
                  </c:forEach>
                </table>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>