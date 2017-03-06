<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.core.Config" %>
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
  <title>Execute Report</title>
</head>
<body>
  <c:set var="isAdmin"><%=request.isUserInRole(Config.DEFAULT_ADMIN_ROLE)%></c:set>
  <u:constantsMap className="com.openkm.util.ReportUtils" var="ReportUtils"/>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Report">Reports</a>
        </li>
        <li class="path">Execute report</li>
      </ul>
      <br/>
      <form action="Report">
        <input type="hidden" name="action" value="execute"/>
        <input type="hidden" name="rp_id" value="${rp_id}"/>
        <input type="hidden" name="format" id="format" value=""/>
        <table class="results" width="50%">
          <tr>
            <th>Label</th><th>Name</th><th>Type</th><th>Value</th>
          </tr>
          <c:choose>
            <c:when test="${empty params}">
              <tr><td colspan="4" align="center">No parameters defined</td></tr>
            </c:when>
            <c:otherwise>
              <c:forEach var="rpp" items="${params}" varStatus="row">
                <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
                  <td>${rpp.label}</td><td>${rpp.name}</td><td>${rpp.type}</td>
                  <td><input name="${rpp.name}" value="" /></td>
                </tr>
              </c:forEach>
            </c:otherwise>
          </c:choose>
          <tr class="fuzzy">
            <td colspan="4" align="right">
              <button onclick="$('#format').val('<c:out value="${ReportUtils.OUTPUT_PDF}"/>')"><img src="img/action/pdf.png" alt="Generate PDF" title="Generate PDF"/></button>
              &nbsp;
              <button onclick="$('#format').val('<c:out value="${ReportUtils.OUTPUT_RTF}"/>')"><img src="img/action/rtf.png" alt="Generate RTF" title="Generate RTF"/></button>
              &nbsp;
              <button onclick="$('#format').val('<c:out value="${ReportUtils.OUTPUT_CSV}"/>')"><img src="img/action/table.png" alt="Generate CSV" title="Generate CSV"/></button>
            </td>
          </tr>
        </table>
      </form>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>