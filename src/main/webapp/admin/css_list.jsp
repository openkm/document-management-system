<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn'%>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="../css/dataTables-1.10.10/jquery.dataTables-1.10.10.min.css" />
  <link rel="stylesheet" type="text/css" href="css/admin-style.css" />
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="../js/jquery.dataTables-1.10.10.min.js"></script>
  <script type="text/javascript" src="../js/utils.js"></script>
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
  <title>CSS List</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="utilities.jsp">Utilities</a>
        </li>
        <li class="path">CSS list</li>
      </ul>
      <br/>
      <div style="width: 60%; margin-left: auto; margin-right: auto;">
        <table id="results" class="results">
          <thead>
            <tr>
              <th>#</th>
              <th>Name</th>
              <th>Context</th>
              <th>Active</th>
              <th width="75px">
                <c:url value="Css" var="urlCreate">
                  <c:param name="action" value="create" />
                </c:url> 
                <a href="${urlCreate}"><img src="img/action/new.png" alt="New css" title="New css" /></a>
              </th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="css" items="${cssList}" varStatus="row">
              <c:url value="Css" var="urlEdit">
                <c:param name="action" value="edit" />
                <c:param name="css_id" value="${css.id}" />
              </c:url>
              <c:url value="Css" var="urlDelete">
                <c:param name="action" value="delete" />
                <c:param name="css_id" value="${css.id}" />
              </c:url>
              <c:url value="Css" var="urlDownload">
                <c:param name="action" value="download" />
                <c:param name="css_id" value="${css.id}" />
              </c:url>
              <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
                <td>${row.index + 1}</td>
                <td>${css.name}</td>
                <td>${css.context}</td>
                <td align="center"><c:choose>
                    <c:when test="${css.active}">
                      <img src="img/true.png" alt="Active" title="Active" />
                    </c:when>
                    <c:otherwise>
                      <img src="img/false.png" alt="Inactive" title="Inactive" />
                    </c:otherwise>
                  </c:choose></td>
                <td align="center">
                  <a href="${urlEdit}"><img src="img/action/edit.png" alt="Edit" title="Edit" /></a>
                  &nbsp; 
                  <a href="${urlDelete}"><img src="img/action/delete.png" alt="Delete" title="Delete" /></a> 
                  &nbsp; 
                  <a href="${urlDownload}"><img src="img/action/download.png" alt="Download" title="Download" /></a>
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