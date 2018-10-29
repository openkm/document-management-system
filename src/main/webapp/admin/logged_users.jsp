<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="../css/dataTables-1.10.10/jquery.dataTables-1.10.10.min.css" />
  <link rel="stylesheet" type="text/css" href="css/admin-style.css"/>
  <script type="text/javascript" src="../js/utils.js"></script>
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="../js/jquery.dataTables-1.10.10.min.js"></script>
  <script type="text/javascript">
    $(document).ready(function () {
      $('#results').dataTable({
        "bStateSave": true,
        "iDisplayLength": 10,
        "lengthMenu": [[10, 15, 20], [10, 15, 20]],
        "fnDrawCallback": function (oSettings) {
          dataTableAddRows(this, oSettings);
        }
      });
    });
  </script>
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
      <div style="width: 80%; margin-left: auto; margin-right: auto;">
        <table id="results" class="results">
          <thead>
            <tr>
              <th width="20px">#</th>
              <th width="100px">User</th>
              <th width="225px">Session id</th>
              <th>Remote IP</th>
              <th>Remote host</th>
              <th width="125px">Creation</th>
              <th width="125px">Last access</th>
              <th width="25px"></th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="se" items="${sessions}" varStatus="row">
              <c:url value="ActivityLog" var="urlLog">
                <c:param name="user" value="${se.user}" />
                <c:param name="dbegin" value="${date}" />
                <c:param name="dend" value="${date}" />
              </c:url>
              <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
                <td width="20px">${row.index + 1}</td>
                <td width="100px">${se.user}</td>
                <td width="225px">${se.id}</td>
                <td>${se.ip}</td>
                <td>${se.host}</td>
                <td width="125px"><u:formatDate calendar="${se.creation}" /></td>
                <td width="125px"><u:formatDate calendar="${se.lastAccess}" /></td>
                <td width="25px" align="center">
                  <a href="${urlLog}"><img src="img/action/calendar.png" alt="Activity log" title="Activity log" /></a>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>