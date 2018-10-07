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
  <link rel="stylesheet" type="text/css" href="css/style.css" />
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
  <title>Reports</title>
</head>
<body>  
  <c:choose>
    <c:when test="${u:isAdmin()}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Report">Reports</a>
        </li>
      </ul>
      <br/>
	      <div style="width:70%; margin-left:auto; margin-right:auto;">
	      	<table id="results" class="results">
	      	<thead>
		      	<tr>
		          <th>Name</th><th>File Name</th><th>Active</th>
		          <th width="100px">
		            <c:url value="Report" var="urlCreate">
		              <c:param name="action" value="create"/>
		            </c:url>
		            <a href="${urlCreate}"><img src="img/action/new.png" alt="New report" title="New report"/></a>
		          </th>
		        </tr>
	      	</thead>
	      	<tbody>
		      	<c:forEach var="rp" items="${reports}" varStatus="row">
		          <c:url value="Report" var="urlEdit">
		            <c:param name="action" value="edit"/>
		            <c:param name="rp_id" value="${rp.id}"/>
		          </c:url>
		          <c:url value="Report" var="urlDelete">
		            <c:param name="action" value="delete"/>
		            <c:param name="rp_id" value="${rp.id}"/>
		          </c:url>
		          <c:url value="Report" var="urlParams">
		            <c:param name="action" value="paramList"/>
		            <c:param name="rp_id" value="${rp.id}"/>
		          </c:url>
		          <c:url value="Report" var="urlGetParams">
		            <c:param name="action" value="getParams"/>
		            <c:param name="rp_id" value="${rp.id}"/>
		          </c:url>
		          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
		            <td>${rp.name}</td><td>${rp.fileName}</td>
		            <td align="center">
		              <c:choose>
		                <c:when test="${rp.active}">
		                  <img src="img/true.png" alt="Active" title="Active"/>
		                </c:when>
		                <c:otherwise>
		                  <img src="img/false.png" alt="Inactive" title="Inactive"/>
		                </c:otherwise>
		              </c:choose>
		            </td>
		            <td align="center">
		              <a href="${urlEdit}"><img src="img/action/edit.png" alt="Edit" title="Edit"/></a>
		              &nbsp;
		              <a href="${urlDelete}"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a>
		              &nbsp;
		              <c:choose>
		                <c:when test="${rp.fileMime == 'application/x-report'}">
		                  <a href="${urlParams}"><img src="img/action/params.png" alt="Parameters" title="Parameters"/></a>
		                </c:when>
		                <c:otherwise>
		                  <img src="img/action/params_disabled.png" alt="Parameters" title="Parameters"/>
		                </c:otherwise>
		              </c:choose>
		              &nbsp;
		              <a href="${urlGetParams}"><img src="img/action/signal.png" alt="Execute" title="Execute"/></a>
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