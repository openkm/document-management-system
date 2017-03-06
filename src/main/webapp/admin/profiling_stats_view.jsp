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
      </c:url>
      <c:url value="ProfilingStats" var="urlActivate">
        <c:param name="action" value="activate"/>
      </c:url>
      <c:url value="ProfilingStats" var="urlDeactivate">
        <c:param name="action" value="deactivate"/>
      </c:url>
      <c:url value="ProfilingStats" var="urlClear">
        <c:param name="action" value="clear"/>
      </c:url>
      <ul id="breadcrumb">
        <li class="path">
          <a href="utilities.jsp">Utilities</a>
        </li>
        <li class="path">Profiling stats</li>
        <li class="action">
          <c:choose>
            <c:when test="${statsEnabled}">
              <a href="${urlDeactivate}">
                <img src="img/action/enabled.png" alt="Disable" title="Disable" style="vertical-align: middle;"/>
                Disable
              </a>
            </c:when>
            <c:otherwise>
              <a href="${urlActivate}">
                <img src="img/action/disabled.png" alt="Enable" title="Enable" style="vertical-align: middle;"/>
                Enable
              </a>
            </c:otherwise>
          </c:choose>
        </li>
        <li class="action">
          <a href="${urlClear}">
            <img src="img/action/clear.png" alt="Clear" title="Clear" style="vertical-align: middle;"/>
            Clear
          </a>
        </li>
        <li class="action">
          <a href="${urlRefresh}">
            <img src="img/action/refresh.png" alt="Refresh" title="Refresh" style="vertical-align: middle;"/>
            Refresh
          </a>
        </li>
      </ul>
      <br/>
      <table class="results" width="80%">
        <tr>
          <th nowrap="nowrap">Class</th>
          <th nowrap="nowrap">Method</th>
          <th nowrap="nowrap">Calls</th>
          <th nowrap="nowrap">Max dur.</th>
          <th nowrap="nowrap">Min dur.</th>
          <th nowrap="nowrap">Avg dur.</th>
          <th nowrap="nowrap">Total dur.</th>
          <th nowrap="nowrap">Action</th>
        </tr>
        <c:forEach var="st" items="${stats}" varStatus="row">
          <c:url value="ProfilingStats" var="urlList">
            <c:param name="action" value="list"/>
            <c:param name="clazz" value="${st.clazz}"/>
            <c:param name="method" value="${st.method}"/>
          </c:url>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${st.clazz}</td>
            <td>${st.method}</td>
            <td>${st.executionCount}</td>
            <td>${st.maxTime}</td>
            <td>${st.minTime}</td>
            <td>${st.avgTime}</td>
            <td>${st.totalTime}</td>
            <td align="center">
              <a href="${urlList}">
                <img src="img/action/table.png" alt="List" title="List" style="vertical-align: middle;"/>
              </a>
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