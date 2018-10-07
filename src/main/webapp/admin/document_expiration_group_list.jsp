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
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="../js/jquery.dataTables-1.10.10.min.js"></script>
  <script type="text/javascript" src="../js/utils.js"></script>
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
  <title>Document expiration group list</title>
</head>
<body>  
  <c:choose>
    <c:when test="${u:isAdmin()}">
      <c:url value="DocumentExpiration" var="urlSyncUsers">
        <c:param name="action" value="syncUsers"/>
      </c:url>
      <c:url value="DocumentExpiration" var="urlSyncRoles">
        <c:param name="action" value="syncRoles"/>
      </c:url>
      <c:url value="DocumentExpiration" var="urlClean">
        <c:param name="action" value="clean"/>
      </c:url>
      <ul id="breadcrumb">
        <li class="path">
          <a href="DocumentExpiration">Document expiration</a>
        </li>
        <li class="action">
          <a href="${urlClean}">
            <img src="img/action/generic.png" alt="Generic" title="Generic" style="vertical-align: middle;"/>
            Clean
          </a>
        </li>
        <li class="action">
          <a href="${urlSyncRoles}">
            <img src="img/action/generic.png" alt="Generic" title="Generic" style="vertical-align: middle;"/>
            Sync Roles
          </a>
        </li>
        <li class="action">
          <a href="${urlSyncUsers}">
            <img src="img/action/generic.png" alt="Generic" title="Generic" style="vertical-align: middle;"/>
            Sync Users
          </a>
        </li>
      </ul>
      <br/>
      <div style="width:20%; margin-left:auto; margin-right:auto;">
	      <table id="results" class="results">
	        <thead>
	          <tr>
	            <th>Group</th>
	            <c:url value="DocumentExpiration" var="urlCreate">
	              <c:param name="action" value="groupCreate"/>
	            </c:url>
	            <th width="50px"><a href="${urlCreate}"><img src="img/action/new.png" alt="New group" title="New group"/></a></th>
	          </tr>
	        </thead>
	        <tbody>
		        <c:forEach var="group" items="${groups}" varStatus="row">
		          <c:url value="DocumentExpiration" var="urlDelete">
		            <c:param name="action" value="groupDelete"/>
		            <c:param name="gru_name" value="${group}"/>
		          </c:url>
		          <c:url value="DocumentExpiration" var="urlEdit">
		            <c:param name="action" value="groupEdit"/>
		            <c:param name="gru_name" value="${group}"/>
		          </c:url>
		          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
		          	<td>${group}</td>
		          	<td align="center">
		          	  <a href="${urlEdit}"><img src="img/action/edit.png" alt="Edit" title="Edit"/></a>
		              &nbsp;
		          	  <a href="${urlDelete}"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a>
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