<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ page import="com.openkm.util.FormatUtil"%>
<%@ page import="java.util.Map.Entry"%>
<%@ page import="java.util.SortedMap"%>
<%@ page import="java.util.TreeMap"%>
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
  <script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
  <script type="text/javascript" src="js/fixedTableHeader.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
    	TABLE.fixHeader('table');
	});
  </script>
  <title>Configuration</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="utilities.jsp">Utilities</a>
        </li>
        <li class="path">System properties</li>
      </ul>
      <br/>
      <table class="results" width="80%">
        <thead>
          <tr>
            <th>Key</th><th>Value</th>
          </tr>
        </thead>
        <tbody>
          <% int i=0; %>
          <% SortedMap<Object, Object> sortProps = new TreeMap<Object, Object>(System.getProperties()); %>
          <% for (Entry<Object, Object> entry : sortProps.entrySet()) { %>
            <tr class="<%=i++ % 2 == 0 ? "even" : "odd" %>">
              <td><b><%=entry.getKey()%></b></td>
              <td>
              <%
                if ("tomcat.util.scan.DefaultJarScanner.jarsToSkip".equals(entry.getKey())) {
                	out.println(FormatUtil.splitBySeparator(String.valueOf(entry.getValue())));
                } else if (String.valueOf(entry.getKey()).endsWith(".class.path")) {
                	out.println(FormatUtil.splitBySeparator(String.valueOf(entry.getValue())));
                } else {
                	out.println(String.valueOf(entry.getValue()));
                }
              %>
              </td>
            </tr>
          <% } %>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>