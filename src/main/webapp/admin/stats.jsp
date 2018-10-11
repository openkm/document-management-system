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
  <link rel="stylesheet" href="css/admin-style.css" type="text/css" />
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
      $('#scroll').height($(window).height() - 21);
    });
  </script>
  <title>Statistics</title>
</head>
<body>
  <u:constantsMap className="com.openkm.servlet.admin.StatsGraphServlet" var="StatsGraph"/>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="stats.jsp">Statistics</a>
        </li>
        <li class="action">
          <a href="TextExtractionQueue">
            <img src="img/action/generic.png" alt="Generic" title="Generic" style="vertical-align: middle;"/>
            Text extraction queue
          </a>
        </li>
        <li class="action">
          <a href="PendingTaskQueue">
            <img src="img/action/generic.png" alt="Generic" title="Generic" style="vertical-align: middle;"/>
            Pending task queue
          </a>
        </li>
        <li class="action">
          <a href="StatsGraph?action=refresh">
            <img src="img/action/refresh.png" alt="Refresh" title="Refresh" style="vertical-align: middle;"/>
            Refresh
          </a>
        </li>
      </ul>
      <br/>
      <div id="scroll" style="width: 100%; height: 100%; overflow: auto;">
          <br/>
	      <table align="center">
	        <tr>
	          <td><img src="StatsGraph?t=${StatsGraph.DOCUMENTS}"/></td>
              <td><img src="StatsGraph?t=${StatsGraph.DOCUMENTS_SIZE}"/></td>
	          <td><img src="StatsGraph?t=${StatsGraph.FOLDERS}"/></td>
	        </tr>
	      </table>
	      <table align="center">
	        <tr>
            <td><img src="StatsGraph?t=${StatsGraph.OS_MEMORY}"/></td>
            <td><img src="StatsGraph?t=${StatsGraph.JVM_MEMORY}"/></td>
            <td><img src="StatsGraph?t=${StatsGraph.DISK}"/></td>
	        </tr>
	      </table>
      </div>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>