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
        "iDisplayLength": 15,
        "lengthMenu": [[10, 15, 20], [10, 15, 20]],
        "fnDrawCallback": function (oSettings) {
          dataTableAddRows(this, oSettings);
        }
      });
    });
  </script>
  <title>Cache Stats</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url value="CacheStats" var="urlReload">
      </c:url>
      <c:url value="CacheStats" var="urlActivate">
        <c:param name="action" value="activate"/>
      </c:url>
      <c:url value="CacheStats" var="urlDeactivate">
        <c:param name="action" value="deactivate"/>
      </c:url>
      <c:url value="CacheStats" var="urlClear">
        <c:param name="action" value="clear"/>
      </c:url>
      <c:url value="CacheStats" var="urlResetAll">
        <c:param name="action" value="resetAll"/>
      </c:url>
      <ul id="breadcrumb">
        <li class="path">
          <a href="utilities.jsp">Utilities</a>
        </li>
        <li class="path">Cache stats</li>
        <li class="action">
          <a href="${urlResetAll}">
            <img src="img/action/delete.png" alt="Reset all" title="Reset all" style="vertical-align: middle;"/>
            Reset all
          </a>
        </li>
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
      <div style="width: 98%; margin-left: auto; margin-right: auto;">
        <table id="results" class="results">
          <thead>
            <tr>
              <th nowrap="nowrap">Cache name</th>
              <th nowrap="nowrap">Hits</th>
              <th nowrap="nowrap">Misses</th>
              <th nowrap="nowrap">Objects</th>
              <th nowrap="nowrap">Memory hits</th>
              <th nowrap="nowrap">Memory missed</th>
              <th nowrap="nowrap">Memory objects</th>
              <th nowrap="nowrap">Disc hits</th>
              <th nowrap="nowrap">Disc missed</th>
              <th nowrap="nowrap">Disc objects</th>
              <th nowrap="nowrap">Action</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="cst" items="${cacheStats}" varStatus="row">
              <c:url value="CacheStats" var="urlReset">
                <c:param name="action" value="reset" />
                <c:param name="name" value="${cst.cache}" />
              </c:url>
              <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
                <td>${cst.cache}</td>
                <td>${cst.cacheHits}</td>
                <td>${cst.cacheMisses}</td>
                <td>${cst.objectCount}</td>
                <td>${cst.inMemoryHits}</td>
                <td>${cst.inMemoryMisses}</td>
                <td>${cst.memoryStoreObjectCount}</td>
                <td>${cst.onDiskHits}</td>
                <td>${cst.onDiskMisses}</td>
                <td>${cst.diskStoreObjectCount}</td>
                <td align="center">
                  <a href="${urlReset}"> <img src="img/action/delete.png" alt="Reset" title="Reset" style="vertical-align: middle;" /></a>
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