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
  <link rel="stylesheet" type="text/css" href="css/fixedTableHeader.css" />
  <link rel="stylesheet" type="text/css" href="js/jscalendar/calendar-win2k-1.css" />
  <script type="text/javascript" src="js/jscalendar/calendar.js"></script>
  <script type="text/javascript" src="js/jscalendar/lang/calendar-en.js"></script>
  <script type="text/javascript" src="js/jscalendar/calendar-setup.js"></script>
  <script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
  <script type="text/javascript" src="js/fixedTableHeader.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
    	TABLE.fixHeader('table.results');
	});
  </script>
  <title>Activity Log</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="ActivityLog">Activity log</a>
        </li>
      </ul>
      <br/>
      <form action="ActivityLog">
        <table class="form" align="center">
          <tr>
            <td>
              From <input type="text" name="dbegin" id="dbegin" value="${dbeginFilter}" size="15" readonly="readonly"/>
              <img src="img/action/calendar.png" id="f_trigger_begin" style="vertical-align: middle;"/>
              To <input type="text" name="dend" id="dend" value="${dendFilter}" size="15" readonly="readonly"/>
              <img src="img/action/calendar.png" id="f_trigger_end" style="vertical-align: middle;"/>
              User <select name="user">
                <option value=""></option>
                <c:forEach var="user" items="${users}" varStatus="row">
                  <c:choose>
                    <c:when test="${user == userFilter}">
                      <option value="${user}" selected="selected">${user}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${user}">${user}</option>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </select>
              Action <select name="action">
                <option value=""></option>
                <c:forEach var="act" items="${actions}">
                  <c:choose>
                    <c:when test="${act == 'Auth' || act == 'Document' || act == 'Folder' || act == 'Mail' || act == 'Repository' || act == 'Admin' || act == 'Misc'}">
                      <optgroup label="${act}"/>
                    </c:when>
                    <c:otherwise>
                      <c:choose>
                        <c:when test="${act == actionFilter}">
                          <option value="${act}" selected="selected">${act}</option>
                        </c:when>
                        <c:otherwise>
                          <option value="${act}">${act}</option>
                        </c:otherwise>
                      </c:choose>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </select>
            </td>
          </tr>
          <tr><td align="right"><input type="submit" value="Search" class="searchButton"/></td></tr>
        </table>
      </form>
      <br/>
      <table class="results" width="95%">
        <thead>
          <tr><th>Date</th><th>User</th><th>Action</th><th>Item</th><th>Path</th><th>Parameters</th></tr>
        </thead>
        <tbody>
          <c:forEach var="act" items="${results}" varStatus="row">
            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
              <td nowrap="nowrap"><u:formatDate calendar="${act.date}"/></td>
              <td>${act.user}</td><td>${act.action}</td><td>${act.item}</td><td>${act.path}</td><td>${act.params}</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
  <script type="text/javascript">
    Calendar.setup({
      inputField : "dbegin",
      ifFormat   : "%Y-%m-%d",
      button     : "f_trigger_begin"
    });
    Calendar.setup({
      inputField : "dend",
      ifFormat   : "%Y-%m-%d",
      button     : "f_trigger_end"
    });
  </script>
</body>
</html>