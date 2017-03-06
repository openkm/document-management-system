<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
      TABLE.fixHeader('table');
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
        <li class="path">LogCat</li>
      </ul>
      <br/>
      <table class="results" width="80%">
        <thead>
          <tr class="fuzzy">
            <td colspan="2" align="right">
              <form action="LogCat" method="get">
                <input type="hidden" name="action" value="purge"/>
                <input type="submit" value="Purge" class="deleteButton"/>
              </form>
            </td>
          </tr>
          <tr><th>File</th><th>Action</th></tr>
        </thead>
        <tbody>
          <c:forEach var="log" items="${files}" varStatus="row">
            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
              <td>${log.path}</td>
              <td align="center">
                <a href="LogCat?action=download&file=${log.name}"><img src="img/action/compress.png" alt="Download" title="Download"/></a>
                &nbsp;
                <a href="LogCat?action=view&file=${log.name}"><img src="img/action/examine.png" alt="Examine" title="Examine"/></a>
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