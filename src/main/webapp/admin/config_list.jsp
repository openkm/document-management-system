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
          <a href="Config">Configuration</a>
        </li>
      </ul>
      <br/>
      <table class="results" width="80%">
        <thead>
          <tr class="fuzzy">
            <td colspan="4" align="right">
              <form action="Config" method="get">
                <input type="hidden" name="action" value="list"/>
                <b>Property</b> <input type="text" name="filter" value="${filter}" size="35"/>
                <input type="submit" value="Filter" class="searchButton"/>
              </form>
            </td>
          </tr>
          <tr>
            <th>Property</th><th>Type</th><th>Value</th>
            <th width="50px">
              <c:url value="Config" var="urlCheck">
                <c:param name="action" value="check"/>
              </c:url>
              <c:url value="Config" var="urlExport">
                <c:param name="action" value="export"/>
              </c:url>
              <a href="${urlCheck}"><img src="img/action/check.png" alt="Check" title="Check"/></a>
              &nbsp;
              <a href="${urlExport}"><img src="img/action/export_sql.png" alt="SQL export" title="SQL export"/></a>
            </th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="cfg" items="${configs}" varStatus="row">
            <c:url value="Config" var="urlEdit">
              <c:param name="action" value="edit"/>
              <c:param name="filter" value="${filter}"/>
              <c:param name="cfg_key" value="${cfg.key}"/>
            </c:url>
            <c:url value="Config" var="urlDelete">
              <c:param name="action" value="delete"/>
              <c:param name="filter" value="${filter}"/>
              <c:param name="cfg_key" value="${cfg.key}"/>
            </c:url>
            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
              <td><b>${cfg.key}</b></td><td><i>${cfg.type}</i></td>
              <td>
                <c:choose>
                  <c:when test="${cfg.type == 'Boolean'}">
                    <c:choose>
                      <c:when test="${cfg.value == 'true'}">
                        <img src="img/true.png" alt="Active" title="Active"/>
                      </c:when>
                      <c:otherwise>
                        <img src="img/false.png" alt="Inactive" title="Inactive"/>
                      </c:otherwise>
                    </c:choose>
                  </c:when>
                  <c:when test="${cfg.type == 'File'}">
                    <c:url value="Config" var="urlView">
                      <c:param name="action" value="view"/>
                      <c:param name="cfg_key" value="${cfg.key}"/>
                    </c:url>
                    <img src="${urlView}"/>
                  </c:when>
                  <c:otherwise>
                    <u:escapeHtml string="${cfg.value}"/>
                  </c:otherwise>
                </c:choose>
              </td>
              <td align="center">
                <a href="${urlEdit}"><img src="img/action/edit.png" alt="Edit" title="Edit"/></a>
                &nbsp;
                <a href="${urlDelete}"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a>
              </td>
            </tr>
          </c:forEach>
          <tr class="fuzzy">
            <td colspan="4" align="right">
              <form action="Config" method="post" enctype="multipart/form-data">
                <input type="hidden" name="action" value="import"/>
                <table>
                  <tr>
                    <td><input class=":required :only_on_blur" type="file" name="sql-file"/></td>
                    <td><input type="submit" value="Import" class="addButton"/></td>
                  </tr>
                </table>
              </form>
            </td>
          </tr>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>