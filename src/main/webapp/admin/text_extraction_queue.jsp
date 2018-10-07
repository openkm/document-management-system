<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="../css/dataTables-1.10.10/jquery.dataTables-1.10.10.min.css" />
  <link rel="stylesheet" href="css/style.css" type="text/css" />
  <script type="text/javascript" src="../js/utils.js"></script>
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="../js/jquery.dataTables-1.10.10.min.js"></script>
  <script type="text/javascript">
    $(document).ready(function () {
      $('#treat').dataTable({
        "bStateSave": true,
        "iDisplayLength": 10,
        "lengthMenu": [[10, 15, 20], [10, 15, 20]],
        "fnDrawCallback": function (oSettings) {
          dataTableAddRows(this, oSettings);
        }
      });
      $('#trick').dataTable({
        "bStateSave": true,
        "iDisplayLength": 10,
        "lengthMenu": [[10, 15, 20], [10, 15, 20]],
        "fnDrawCallback": function (oSettings) {
          dataTableAddRows(this, oSettings);
        }
      });
    });
  </script>
  <title>Text Extraction Queue</title>
<body>  
  <c:choose>
    <c:when test="${u:isAdmin()}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="TextExtractionQueue">Text extraction queue</a>
        </li>
        <li class="action">
          <a href="stats.jsp">
            <img src="img/action/generic.png" alt="Generic" title="Generic" style="vertical-align: middle;"/>
            Statistics
          </a>
        </li>
        <li class="action">
          <a href="TextExtractionQueue">
            <img src="img/action/refresh.png" alt="Refresh" title="Refresh" style="vertical-align: middle;"/>
            Refresh
          </a>
        </li>
      </ul>
      <br/>
      <div style="width:90%; margin-left:auto; margin-right:auto;">
	      <table id="trick" class="results">
	        <thead>
	          <tr class="header">
	            <td align="center" colspan="4">
                <b> Pending Extractions
                  <c:if test="${!inProgress}">
                      <span>
                        (Last execution:
                        <c:choose>
                          <c:when test="${lastExecution != null}">
                            <u:formatDate calendar="${lastExecution}"/>
                          </c:when>
                          <c:otherwise>
                            I don't know
                          </c:otherwise>
                        </c:choose>
                        )
                      </span>
                  </c:if></b>
	            </td>
	          </tr>
	          <tr><th>#</th><th>UUID</th><th>Path</th><th>Date</th></tr>
	        </thead>
	        <tbody>
	          <c:forEach var="work" items="${pendingWorks}" varStatus="row">
	            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
	              <td>${row.index + 1}</td>
	              <td nowrap="nowrap">${work.docUuid}</td>
	              <td>${work.docPath}</td>
	              <td nowrap="nowrap"><u:formatDate calendar="${work.date}"/></td>
	            </tr>
	          </c:forEach>
	          <c:if test="${pendingSize > 0}">
	            <tr class="fuzzy">
	              <td colspan="4" style="text-align: center; font-weight: bold;">
	                Total pending extractions: ${pendingSize}
	              </td>
	            </tr>
	          </c:if>
	        </tbody>
	      </table>    
	      <br/>        
	       <table id="treat" class="results">
	        <thead>
	          <tr class="header">
	            <td align="center" colspan="4">
                   <b> Extractions In Progress
                    <c:if test="${inProgress}">
                       <span>(Running)</span>
                    </c:if></b>
                 </td>
	          </tr>
	          <tr><th>#</th><th>UUID</th><th>Path</th><th>Date</th></tr>
	        </thead>
	        <tbody>
	          <c:forEach var="work" items="${inProgressWorks}" varStatus="row">
	            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
	              <td>${row.index + 1}</td>
	              <td nowrap="nowrap">${work.docUuid}</td>
	              <td>${work.docPath}</td>
	              <td nowrap="nowrap"><u:formatDate calendar="${work.date}"/></td>
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