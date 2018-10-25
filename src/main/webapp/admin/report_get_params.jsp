<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.core.Config" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="../css/jquery-ui-1.10.3/jquery-ui-1.10.3.css"/>
  <link rel="stylesheet" type="text/css" href="css/admin-style.css" />
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="../js/jquery-ui-1.10.3/jquery-ui-1.10.3.js"></script>
  <script type="text/javascript" src="../js/jquery-ui-1.10.3/i18n/jquery.ui.datepicker-en-GB.js"></script>
  <script type="text/javascript">
    $(document).ready(function () {
      $(".datepicker").datepicker({
        dateFormat: 'yy-mm-dd'
      });
    });
  </script>
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
        <table class="results-old" width="50%">
          <tr>
            <th>Class</th><th>Label</th><th>Name</th><th>Type</th><th>Value</th>
          </tr>
          <c:choose>
            <c:when test="${empty params}">
              <tr><td colspan="4" align="center">No parameters defined</td></tr>
            </c:when>
            <c:otherwise>
              <c:forEach var="rpp" items="${params}" varStatus="row">
                <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
                  <td>${rpp['class'].name}</td>
                  <td>${rpp.label}</td>
                  <td>${rpp.name}</td>
                  <td><c:catch var="exception">${rpp.type}</c:catch></td>
                  <td>
                  	<c:catch>
                  	  <c:choose>
                  		<c:when test="${rpp.type == 'date'}">
                  		  <input name="${rpp.name}" id="${rpp.name}" class="datepicker" value="" />
                  		</c:when>
                  	  </c:choose>
                  		<c:otherwise>
                  		 <input name="${rpp.name}" id="${rpp.name}" value="" />
                  		</c:otherwise>
                  	</c:catch>                  	
                  </td>
                </tr>
              </c:forEach>
            </c:otherwise>
          </c:choose>
          <tr class="fuzzy">
            <td colspan="5" align="right">
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