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
  <title>Hibernate Stats</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url value="HibernateStats" var="urlRefresh">
      </c:url>
      <c:url value="HibernateStats" var="urlActivate">
        <c:param name="action" value="activate"/>
      </c:url>
      <c:url value="HibernateStats" var="urlDeactivate">
        <c:param name="action" value="deactivate"/>
      </c:url>
      <c:url value="HibernateStats" var="urlClear">
        <c:param name="action" value="clear"/>
      </c:url>
      <ul id="breadcrumb">
        <li class="path">
          <a href="utilities.jsp">Utilities</a>
        </li>
        <li class="path">Hibernate stats</li>
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
      <table class="results" width="30%">
        <tr><th>Element</th><th>Value</th></tr>
        <tr class="even"><td><b>Connects</b></td><td>${generalStats[0]}</td></tr>
        <tr class="odd"><td><b>Flushes</b></td><td>${generalStats[1]}</td></tr>
        <tr class="even"><td><b>Prepare statements</b></td><td>${generalStats[2]}</td></tr>
        <tr class="odd"><td><b>Close statements</b></td><td>${generalStats[3]}</td></tr>
        <tr class="even"><td><b>Session opens</b></td><td>${generalStats[5]}</td></tr>
        <tr class="odd"><td><b>Session closes</b></td><td>${generalStats[4]}</td></tr>
        <tr class="even"><td><b>Total Transactions</b></td><td>${generalStats[6]}</td></tr>
        <tr class="odd"><td><b>Successfull Transactions</b></td><td>${generalStats[7]}</td></tr>
        <tr class="even"><td><b>Optimistic failures</b></td><td>${generalStats[8]}</td></tr>
      </table>
      <h2>Query statistics</h2>
      <table class="results" width="98%">
        <tr>
          <th nowrap="nowrap">HQL Query</th>
          <!-- <th nowrap="nowrap">SQL Query</th> -->
          <th nowrap="nowrap">Calls</th>
          <th nowrap="nowrap">Total rowcount</th>
          <th nowrap="nowrap">Max dur.</th>
          <th nowrap="nowrap">Min dur.</th>
          <th nowrap="nowrap">Avg dur.</th>
          <th nowrap="nowrap">Total dur.</th>
          <th nowrap="nowrap">Cache hits</th>
          <th nowrap="nowrap">Cache miss</th>
          <th nowrap="nowrap">Cache put</th>
        </tr>
        <c:forEach var="qs" items="${queryStats}" varStatus="row">
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${qs.query}</td>
            <!-- <td>${qs.tquery}</td> -->
            <td>${qs.executionCount}</td>
            <td>${qs.executionRowCount}</td>
            <td>${qs.executionMaxTime}</td>
            <td>${qs.executionMinTime}</td>
            <td>${qs.executionAvgTime}</td>
            <td>${qs.executionTotalTime}</td>
            <td>${qs.cacheHitCount}</td>
            <td>${qs.cacheMissCount}</td>
            <td>${qs.cachePutCount}</td>
          </tr>
        </c:forEach>
      </table>
      <h2>Entity statistics</h2>
      <table class="results" width="98%">
        <tr>
          <th nowrap="nowrap">Entity</th>
          <th nowrap="nowrap">Loads</th>
          <th nowrap="nowrap">Fetches</th>
          <th nowrap="nowrap">Inserts</th>
          <th nowrap="nowrap">Updates</th>
          <th nowrap="nowrap">Deletes</th>
          <th nowrap="nowrap">Optimistic failures</th>
        </tr>
        <c:forEach var="es" items="${entityStats}" varStatus="row">
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${es.entity}</td>
            <td>${es.loadCount}</td>
            <td>${es.fetchCount}</td>
            <td>${es.insertCount}</td>
            <td>${es.updateCount}</td>
            <td>${es.deleteCount}</td>
            <td>${es.optimisticFailureCount}</td>
          </tr>
        </c:forEach>
      </table>
      <h2>Collection statistics</h2>
      <table class="results" width="98%">
        <tr>
          <th nowrap="nowrap">Role</th>
          <th nowrap="nowrap">Loads</th>
          <th nowrap="nowrap">Fetches</th>
          <th nowrap="nowrap">Updates</th>
          <th nowrap="nowrap">Recreate</th>
          <th nowrap="nowrap">Remove</th>
        </tr>
        <c:forEach var="cs" items="${collectionStats}" varStatus="row">
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${cs.collection}</td>
            <td>${cs.loadCount}</td>
            <td>${cs.fetchCount}</td>
            <td>${cs.updateCount}</td>
            <td>${cs.recreateCount}</td>
            <td>${cs.removeCount}</td>
          </tr>
        </c:forEach>
      </table>
      <h2>2nd level cache statistics</h2>
      <table class="results" width="98%">
        <tr>
          <th nowrap="nowrap">Region name</th>
          <th nowrap="nowrap">Puts</th>
          <th nowrap="nowrap">Hits</th>
          <th nowrap="nowrap">Misses</th>
          <th nowrap="nowrap">Elements in memory</th>
          <th nowrap="nowrap">Size in memory</th>
          <th nowrap="nowrap">Elements on disk</th>
        </tr>
        <c:forEach var="slcs" items="${secondLevelCacheStats}" varStatus="row">
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${slcs.cache}</td>
            <td>${slcs.putCount}</td>
            <td>${slcs.hitCount}</td>
            <td>${slcs.missCount}</td>
            <td>${slcs.elementCountInMemory}</td>
            <td><u:formatSize size="${slcs.sizeInMemory}"/></td>
            <td>${slcs.elementCountOnDisk}</td>
          </tr>
        </c:forEach>
        <tr class="fuzzy"><td colspan="5">&nbsp;</td><td><u:formatSize size="${totalSizeInMemory}"/></td><td></td></tr>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>