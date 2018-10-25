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
    $('#results').dataTable({
      "bStateSave": true,
      "iDisplayLength": 15,
      "lengthMenu": [[10, 15, 20], [10, 15, 20]],
      "fnDrawCallback": function (oSettings) {
        dataTableAddRows(this, oSettings);
      }
    });
  </script>
  <title>LogCat</title>
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
          <a href="LogCat">LogCat</a>
        </li>
        <li class="path">View</li>
      </ul>
      <br/>
      <form action="LogCat">
        <input type="hidden" name="action" value="view"/>
        <input type="hidden" name="file" value="${file}"/>
        <table class="form">
          <tr><td>File</td><td>${file}</td></tr>
          <tr><td>Begin</td><td><input type="text" name="begin" value="${begin}"/></td></tr>
          <tr><td>End</td><td><input type="text" name="end" value="${end}"/></td></tr>
          <tr><td>String</td><td><input type="text" name="str" value="${str}"/></td></tr>
          <tr><td colspan="2" align="right"><input type="submit" value="Send"/></td></tr>
        </table>
      </form>
      <br/>
      <div style="width: 95%; margin-left: auto; margin-right: auto;">
        <table id="results" class="results">
          <thead>            
            <tr>
              <th width="2%">Line</th>
              <th width="98%">Message</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="msg" items="${messages}" varStatus="row">
              <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
                <td>${msg.line}</td>
                <td>${msg.message}</td>
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