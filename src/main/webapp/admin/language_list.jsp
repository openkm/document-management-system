<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn' %>
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
  <title>Language List</title>
</head>
<body>  
  <c:choose>
    <c:when test="${u:isMultipleInstancesAdmin()}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Language">Language list</a>
        </li>
      </ul>
      <br/>
      <div style="width:60%; margin-left:auto; margin-right:auto;">
	      <table id="results" class="results">
	      	<thead>
	          <tr>
	            <th>Id</th><th>Flag</th><th>Name</th><th>Translations</th>
	            <th width="100px">
		          <c:url value="Language" var="urlCreate">
	              	<c:param name="action" value="create"/>
	              </c:url>
	              <a href="${urlCreate}"><img src="img/action/new.png" alt="New language" title="New language"/></a>
		      	</th>
	          </tr>
	        </thead>
	        <tbody>	        
	          <c:forEach var="lang" items="${langs}" varStatus="row">
	            <c:url value="Language" var="urlFlag">
	            	<c:param name="action" value="flag"/>
	              	<c:param name="lg_id" value="${lang.id}"/>
	          	</c:url>
	            <c:url value="Language" var="urlEdit">
	              <c:param name="action" value="edit"/>
	              <c:param name="lg_id" value="${lang.id}"/>
	            </c:url>
	            <c:url value="Language" var="urlDelete">
	              <c:param name="action" value="delete"/>
	              <c:param name="lg_id" value="${lang.id}"/>
	            </c:url>
	            <c:url value="Language" var="urlTranslate">
	              <c:param name="action" value="translate"/>
	              <c:param name="lg_id" value="${lang.id}"/>
	            </c:url>
	            <c:url value="Language" var="urlExport">
	              <c:param name="action" value="export"/>
	              <c:param name="lg_id" value="${lang.id}"/>
	            </c:url>
	            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
	              <td align="center">${lang.id}</td>
	              <td align="center"><img src="${urlFlag}"/></td>
	              <td>${lang.name}</td>
	              <td>${fn:length(lang.translations)}
	                <c:if test="${max>fn:length(lang.translations)}">
	                  &nbsp;( Warning, translations needed )
	                </c:if>
	              </td>
	              <td width="100px" align="center">
	                <a href="${urlEdit}"><img src="img/action/edit.png" alt="Edit" title="Edit"/></a>
	                &nbsp;
	                <c:choose>
	                  <c:when test="${lang.id == 'en-GB'}">
	                    <img src="img/action/delete_disabled.png" alt="Delete" title="Delete"/>
	                  </c:when>
	                  <c:otherwise>
	                    <a href="${urlDelete}"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a>
	                  </c:otherwise>
	                </c:choose>
	                &nbsp;
	                <a href="${urlTranslate}"><img src="img/action/translate.png" alt="Edit translations" title="Edit translations"/></a>
	                &nbsp;
	                <a href="${urlExport}"><img src="img/action/export_sql.png" alt="SQL export" title="SQL export"/></a>
	              </td>
	            </tr>
	          </c:forEach>
	        </tbody>
	        <tfoot>
	          <tr>
	            <td align="right" colspan="5">
	              <form action="Language" method="post" enctype="multipart/form-data">
	                <input type="hidden" name="action" value="import"/>	                
                    <input class=":required :only_on_blur" type="file" name="sql-file"/>
                    <input type="submit" value="Add new translation" class="addButton"/>	                
	              </form>
	            </td>
	          </tr>
	        </tfoot>
	      </table>
      </div>        
      </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>