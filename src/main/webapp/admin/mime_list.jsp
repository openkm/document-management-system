<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
  <title>Mime types</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="MimeType">Mime types</a>
        </li>
      </ul>
      <br/>
      <table class="results" width="80%">
        <thead>
          <tr>
            <th>Name</th><th>Description</th><th>Image</th><th>Extensions</th><th>Search</th>
            <th width="50px">
              <c:url value="MimeType" var="urlCreate">
                <c:param name="action" value="create"/>
              </c:url>
              <c:url value="MimeType" var="urlExport">
                <c:param name="action" value="export"/>
              </c:url>
              <a href="${urlCreate}"><img src="img/action/new.png" alt="New mime type" title="New mime type"/></a>
              <a href="${urlExport}"><img src="img/action/export_sql.png" alt="SQL export" title="SQL export"/></a>
            </th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="mt" items="${mimeTypes}" varStatus="row">
            <c:url value="/mime/${mt.name}" var="urlIcon">
            </c:url>
            <c:url value="MimeType" var="urlEdit">
              <c:param name="action" value="edit"/>
              <c:param name="mt_id" value="${mt.id}"/>
            </c:url>
            <c:url value="MimeType" var="urlDelete">
              <c:param name="action" value="delete"/>
              <c:param name="mt_id" value="${mt.id}"/>
            </c:url>
            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
              <td>${mt.name}</td>
              <td>${mt.description}</td>
              <td align="center"><img src="${urlIcon}"/></td>
              <td>${mt.extensions}</td>
              <td align="center">
                <c:choose>
                  <c:when test="${mt.search}">
                    <img src="img/true.png" alt="Search" title="Search"/>
                  </c:when>
                  <c:otherwise>
                    <img src="img/false.png" alt="No Search" title="No Search"/>
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
        </tbody>
        <tr class="fuzzy">
          <td colspan="6" align="right">
            <form action="MimeType" method="post" enctype="multipart/form-data">
              <input type="hidden" name="action" value="import"/>
              <table>
                <tr>
                  <td><input class=":required :only_on_blur" type="file" name="sql-file"/></td>
                  <td><input type="submit" value="Import mime types" class="addButton"/></td>
                </tr>
              </table>
            </form>
          </td>
        </tr>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>