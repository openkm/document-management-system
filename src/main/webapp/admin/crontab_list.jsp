<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.openkm.servlet.admin.BaseServlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u"%>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="Shortcut icon" href="favicon.ico" />
<link rel="stylesheet" type="text/css" href="../css/dataTables-1.10.10/jquery.dataTables-1.10.10.min.css" />
<link rel="stylesheet" type="text/css" href="css/admin-style.css" />
<script type="text/javascript" src="../js/utils.js"></script>
<script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
<script type="text/javascript" src="../js/jquery.dataTables-1.10.10.min.js"></script>
<script type="text/javascript">
  $(document).ready(function() {
    $('#results').dataTable({
      "bStateSave" : true,
      "iDisplayLength" : 15,
      "lengthMenu" : [ [ 10, 15, 20 ], [ 10, 15, 20 ] ],
      "fnDrawCallback" : function(oSettings) {
        dataTableAddRows(this, oSettings);
      }
    });
  });
</script>
<title>Crontab</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path"><a href="CronTab">Crontab</a></li>
        <li class="action">
          <a href="CronTab"> 
            <img src="img/action/refresh.png" alt="Refresh" title="Refresh" style="vertical-align: middle;" /> Refresh
          </a>
        </li>
      </ul>
      <br />
      <div style="width: 90%; margin-left: auto; margin-right: auto;">
        <table id="results" class="results">
          <thead>
            <tr>
              <th>Name</th>
              <th>Expression</th>
              <th>Mime</th>
              <th>File Name</th>
              <th>Mail</th>
              <th>Last Begin</th>
              <th>Last End</th>
              <th width="50px">Active</th>
              <th width="100px">
                <c:url value="CronTab" var="urlCreate">
                  <c:param name="action" value="create" />
                </c:url> 
                <a href="${urlCreate}"><img src="img/action/new.png" alt="New crontab" title="New crontab" /></a>
              </th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="ct" items="${crontabs}" varStatus="row">
              <c:url value="CronTab" var="urlEdit">
                <c:param name="action" value="edit" />
                <c:param name="ct_id" value="${ct.id}" />
              </c:url>
              <c:url value="CronTab" var="urlDelete">
                <c:param name="action" value="delete" />
                <c:param name="ct_id" value="${ct.id}" />
              </c:url>
              <c:url value="CronTab" var="urlExecute">
                <c:param name="action" value="execute" />
                <c:param name="ct_id" value="${ct.id}" />
              </c:url>
              <c:url value="CronTab" var="urlDownload">
                <c:param name="action" value="download" />
                <c:param name="ct_id" value="${ct.id}" />
              </c:url>
              <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
                <td>${ct.name}</td>
                <td>${ct.expression}</td>
                <td>${ct.fileMime}</td>
                <td>${ct.fileName}</td>
                <td>${ct.mail}</td>
                <td><u:formatDate calendar="${ct.lastBegin}" /></td>
                <td><u:formatDate calendar="${ct.lastEnd}" /></td>
                <td width="50px" align="center">
                  <c:choose>
                    <c:when test="${ct.active}">
                      <img src="img/true.png" alt="Active" title="Active" />
                    </c:when>
                    <c:otherwise>
                      <img src="img/false.png" alt="Inactive" title="Inactive" />
                    </c:otherwise>
                  </c:choose>
                </td>
                <td width="100px" align="center">
                  <a href="${urlEdit}"><img src="img/action/edit.png" alt="Edit" title="Edit" /></a> &nbsp; 
                  <a href="${urlDelete}"><img src="img/action/delete.png" alt="Delete" title="Delete" /></a> &nbsp; 
                  <c:choose>
                    <c:when test="${(empty ct.lastBegin && empty ct.lastEnd) || not empty ct.lastEnd}">
                      <a href="${urlExecute}"><img src="img/action/signal.png" alt="Execute" title="Execute" /></a>
                    </c:when>
                    <c:otherwise>
                      <img src="img/action/signal_disabled.png" alt="Running" title="Running" />
                    </c:otherwise>
                  </c:choose> 
                  &nbsp; <a href="${urlDownload}"><img src="img/action/download.png" alt="Download" title="Download" /></a>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </c:when>
    <c:otherwise>
      <div class="error">
        <h3>Only admin users allowed</h3>
      </div>
    </c:otherwise>
  </c:choose>
</body>
</html>