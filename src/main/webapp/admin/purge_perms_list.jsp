<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
    $(document).ready(function() {
      $('#results').dataTable({
        "bStateSave": true,
        "iDisplayLength": 10,
        "lengthMenu": [[10, 15, 20], [10, 15, 20]],
        "fnDrawCallback": function (oSettings) {
          dataTableAddRows(this, oSettings);
        }
      });

      $('a.confirm').click(function(e) {
        e.preventDefault();

        if (confirm('Are you sure?')) {
          window.location.href = $(this).attr('href');
        }
      });
    });
  </script>
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
        <div style="width: 350px; margin-left: auto; margin-right: auto;">
          <table id="results" align="center" width="100%">
            <thead>
              <tr>
                <th>${type}</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="elto" items="${elements}" varStatus="row">
                <c:url value="PurgePermissions" var="urlDelete">
                  <c:param name="action" value="purge${type}" />
                  <c:param name="elto" value="${elto}" />
                </c:url>
                <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
                  <td>${elto}</td>
                  <td align="center">
                    <a href="${urlDelete}">
                      <img src="img/action/delete.png" alt="Delete"title="Delete" />
                    </a>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </form>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>
