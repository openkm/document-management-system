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
    	TABLE.fixHeader('#trick');
	});
  </script>
  <title>Statistics</title>
</head>
<body>
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
      <%-- <h2>Repository</h2> --%>
      <table align="center">
        <tr>
          <td><img src="StatsGraph?t=0"/></td>
          <td><img src="StatsGraph?t=1"/></td>
          <td><img src="StatsGraph?t=2"/></td>
        </tr>
      </table>
      
      <%-- <h2>System</h2> --%>
      <table align="center">
        <tr>
          <td><img src="StatsGraph?t=5"/></td>
          <td><img src="StatsGraph?t=3"/></td>
          <td><img src="StatsGraph?t=4"/></td>
        </tr>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>