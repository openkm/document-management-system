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
  <link rel="stylesheet" type="text/css" href="css/admin-style.css" />
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
  <title>Message list</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <u:constantsMap className="com.openkm.frontend.client.bean.GWTUINotification" var="GWTUINotification"/>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url var="messageList" value="LoggedUsers">
      	<c:param name="action" value="messageList"></c:param>
      </c:url>
      <ul id="breadcrumb">
        <li class="path">
          <a href="Auth">User list</a>
        </li>
        <li class="path">Message queue</li>
        <li class="action">
          <a href="${messageList}">
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
              <th>Date</th>
              <th>Action</th>
              <th>Message</th>
              <th>Type</th>
              <th>Show</th>
              <th width="50px">
                <c:url var="messageCreate" value="LoggedUsers">
                  <c:param name="action" value="messageCreate"></c:param>
                </c:url> 
                <a href="${messageCreate}"><img src="img/action/new.png" alt="New message" title="New message" /></a>
              </th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="me" items="${messages}" varStatus="row">
              <c:url value="LoggedUsers" var="urlEdit">
                <c:param name="action" value="messageEdit" />
                <c:param name="me_id" value="${me.id}" />
              </c:url>
              <c:url value="LoggedUsers" var="urlDelete">
                <c:param name="action" value="messageDelete" />
                <c:param name="me_id" value="${me.id}" />
              </c:url>
              <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
                <td><u:formatDate date="${me.date}" /></td>
                <td>
                  <c:choose>
                    <c:when test="${me.action == GWTUINotification.ACTION_LOGOUT}">
		              Logout
		            </c:when>
                  </c:choose>
                </td>
                <td>${me.message}</td>
                <td>
                  <c:choose>
                    <c:when test="${me.type == GWTUINotification.TYPE_TEMPORAL}">
                      Temporal
                    </c:when>
                  </c:choose> 
                  <c:choose>
                    <c:when test="${me.type == GWTUINotification.TYPE_PERMANENT}">
                      Permanent
                    </c:when>
                  </c:choose>
                </td>
                <td align="center">
                  <c:choose>
                    <c:when test="${me.show}">
                      <img src="img/true.png" alt="Active" title="Active" />
                    </c:when>
                    <c:otherwise>
                      <img src="img/false.png" alt="Inactive" title="Inactive" />
                    </c:otherwise>
                  </c:choose>
                </td>
                <td align="center">
                  <a href="${urlEdit}"><img src="img/action/edit.png" alt="Edit" title="Edit" /></a>
                  &nbsp; 
                  <a href="${urlDelete}"><img src="img/action/delete.png" alt="Delete" title="Delete" /></a>
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