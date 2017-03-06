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
  <script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
  <title>Search indexes</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="utilities.jsp">Utilities</a>
        </li>
        <li class="path">List indexes</li>
        <li class="action">
          <a href="ListIndexes?action=list">
            <img src="img/action/table.png" alt="List" title="List" style="vertical-align: middle;"/>
            List indexes
          </a>
        </li>
      </ul>
      <br/>
      <table class="results" width="60%">
        <thead>
          <tr class="fuzzy">
            <td colspan="5" align="right">
              <form action="ListIndexes">
                <input type="hidden" name="action" value="search"/>
                <input type="text" name="exp" value="${exp}" style="width: 80%"/>
                <input type="submit" value="Search" class="searchButton"/>
              </form>
            </td>
          </tr>
          <tr><th>Score</th><th>UUID</th><th>Name</th><th>Type</th><th>Action</th></tr>
        </thead>
        <tbody>
          <c:forEach var="res" items="${results}" varStatus="row">
            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
              <td>${res.score}</td>
              <td>${res.uuid}</td>
              <td>${res.name}</td>
              <td>${res.type}</td>
              <td align="center">
                <a href="ListIndexes?action=list&id=${res.docId}">
                  <img src="img/action/table.png" alt="List" title="List" style="vertical-align: middle;"/>
                </a>
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