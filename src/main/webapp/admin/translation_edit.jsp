<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.openkm.servlet.admin.BaseServlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn'%>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="Shortcut icon" href="favicon.ico" />
<link rel="stylesheet" type="text/css" href="../css/dataTables-1.10.10/jquery.dataTables-1.10.10.min.css" />
<link rel="stylesheet" type="text/css" href="../css/chosen.css" />
<link rel="stylesheet" type="text/css" href="css/admin-style.css" />
<script type="text/javascript" src="../js/utils.js"></script>
<script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
<script type="text/javascript" src="../js/jquery.dataTables-1.10.10.min.js"></script>
<script type="text/javascript" src="../js/chosen.jquery.js"></script>
<script type="text/javascript">
  $(document).ready(function() {
    $('select#module').chosen({
      disable_search_threshold : 10,
      allow_single_deselect : true
    });

    $('#results').dataTable({
      "bStateSave" : true,
      "iDisplayLength" : 15,
      "lengthMenu" : [ [ 10, 15, 20 ], [ 10, 15, 20 ] ],
      "fnDrawCallback" : function(oSettings) {
        dataTableAddRows(this, oSettings);
      }
    });

    $('#submitForm').click(function() {
      $('#formModule').val($('#module option:selected').val());
      $('#formFilter').val($('#filter').val());
      $('#searchForm').submit();
      return false;
    });
  });
</script>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path"><a href="Language">Language list</a></li>
        <li class="path">Translation list</li>
      </ul>
      <br />
      <div style="width: 95%; margin-left: auto; margin-right: auto;">
        <form id="searchForm" action="Language" method="get">
          <input type="hidden" name="action" value="${action}" /> 
          <input type="hidden" name="lg_id" value="${lg_id}" /> 
          <input type="hidden" id="formModule" name="module" value="" /> 
          <input type="hidden" id="formFilter" name="filter" value="" />
        </form>
        <form action="Language" method="post">
          <input type="hidden" name="action" value="${action}" /> 
          <input type="hidden" name="persist" value="${persist}" /> 
          <input type="hidden" name="lg_id" value="${lg_id}" />
          <table id="results" class="results">
            <thead>
              <tr class="header">
                <td align="right" colspan="5">
                  <b>Module</b> 
                  <select name="module" id="module" style="width: 125px" data-placeholder="&nbsp;">
                    <c:forEach var="tr_module" items="${tr_modules}" varStatus="row">
                      <c:choose>
                        <c:when test="${tr_module == module}">
                          <option value="${tr_module}" selected="selected">${tr_module}</option>
                        </c:when>
                        <c:otherwise>
                          <option value="${tr_module}">${tr_module}</option>
                        </c:otherwise>
                      </c:choose>
                    </c:forEach>
                </select> 
                <b>Key</b> 
                <input type="text" id="filter" name="filter" value="${filter}" size="35" /> 
                <input id="submitForm" type="submit" value="Filter" class="searchButton" /></td>
              </tr>
              <tr>
                <th>#</th>
                <th>Module</th>
                <th>Key property</th>
                <th>${langBaseName}</th>
                <th>${langToTranslateName}</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="translation" items="${translationsBase}" varStatus="row">
                <c:set var="moduleKey" value="${translation.translationId.module}-${translation.translationId.key}" />
                <c:choose>
                  <c:when test="${empty translations[moduleKey]}">
                    <c:set var="rowClass">warn</c:set>
                  </c:when>
                  <c:otherwise>
                    <c:set var="rowClass">${row.index % 2 == 0 ? 'even' : 'odd'}</c:set>
                  </c:otherwise>
                </c:choose>
                <tr class="${rowClass}">
                  <td align="right">${row.index+1}&nbsp;&nbsp;</td>
                  <td width="10%">${translation.translationId.module}</td>
                  <td width="30%">${translation.translationId.key}</td>
                  <td width="30%">${translation.text}</td>
                  <td width="30%"><input size="60" name="${moduleKey}" value="${translations[moduleKey]}" /></td>
                </tr>
              </c:forEach>
            </tbody>
            <tfoot>
              <tr class="foot">
                <td align="right" colspan="5">
                  <input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton" /> 
                  <input type="submit" value="Edit" class="yesButton" />
                </td>
              </tr>
            </tfoot>
          </table>
        </form>
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