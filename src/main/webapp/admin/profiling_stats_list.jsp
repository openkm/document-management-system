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
  <title>Profiling Stats</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url value="ProfilingStats" var="urlRefresh">
        <c:param name="action" value="list"/>
        <c:param name="clazz" value="${clazz}"/>
        <c:param name="method" value="${method}"/>
      </c:url>
      <ul id="breadcrumb">
        <li class="path">
          <a href="utilities.jsp">Utilities</a>
        </li>
        <li class="path">
          <a href="ProfilingStats">Profiling stats</a>
        </li>
        <li class="path">Profiling stats list</li>
        <li class="action">
          <a href="${urlRefresh}">
            <img src="img/action/refresh.png" alt="Refresh" title="Refresh" style="vertical-align: middle;"/>
            Refresh
          </a>
        </li>
      </ul>
      <br/>
      <table class="results" width="90%">
        <tr>
          <th nowrap="nowrap">Date</th>
          <th nowrap="nowrap">Class</th>
          <th nowrap="nowrap">Method</th>
          <th nowrap="nowrap">Time</th>
          <th nowrap="nowrap">User</th>
          <th>Params</th>
          <th>Trace</th>
        </tr>
        <c:forEach var="lst" items="${list}" varStatus="row">
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td nowrap="nowrap"><u:formatDate calendar="${lst.date}"/></td>
            <td nowrap="nowrap">${lst.clazz}</td>
            <td nowrap="nowrap">${lst.method}</td>
            <td nowrap="nowrap">${lst.time}</td>
            <td nowrap="nowrap">${lst.user}</td>
            <td width="25px">${lst.params}</td>
            <td width="250px">${lst.trace}</td>
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