<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn' %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <title>Language List</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Language">Language list</a>
        </li>
      </ul>
      <br/>
        <table class="results" width="60%">
          <tr>
            <th>Id</th><th>Flag</th><th>Name</th><th>Translations</th>
            <th width="100px">
	          <c:url value="Language" var="urlCreate">
              	<c:param name="action" value="create"/>
              </c:url>
              <a href="${urlCreate}"><img src="img/action/new.png" alt="New language" title="New language"/></a>
	      	</th>
          </tr>
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
              <td>
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
          <tr class="fuzzy">
            <td colspan="5" align="right">
              <form action="Language" method="post" enctype="multipart/form-data">
                <input type="hidden" name="action" value="import"/>
                <table>
                  <tr>
                    <td><input class=":required :only_on_blur" type="file" name="sql-file"/></td>
                    <td><input type="submit" value="Add new translation" class="addButton"/></td>
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