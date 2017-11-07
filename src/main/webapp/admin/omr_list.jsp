<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.openkm.core.Config" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css?v=%{TIMESTAMP}%" />
  <script type="text/javascript" src="../js/jquery-1.8.3.min.js"></script>
  <script type="text/javascript" src="../js/jquery.tablescroll.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
        $('#results').tableScroll({
            height: $(window).height() - 100
        });
	});
  </script>
  <title>OMR Template</title>
</head>
<body>
  <c:set var="isAdmin"><%=request.isUserInRole(Config.DEFAULT_ADMIN_ROLE)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Omr">OMR Template</a>
        </li>
      </ul>
      <br/>
      <table id="results" class="results" width="90%">
        <thead>
          <tr>
            <th>Id</th><th>Name</th><th>Template</th><th>Asc</th><th>Config</th><th>Fields</th><th>Active</th>
            <th width="100px">
              <c:url value="Omr" var="urlCreate">
                <c:param name="action" value="create"/>
              </c:url>
              <a href="${urlCreate}"><img src="img/action/new.png" alt="New template" title="New template"/></a>
            </th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="om" items="${omr}" varStatus="row">
            <c:url value="Omr" var="urlEdit">
              <c:param name="action" value="edit"/>
              <c:param name="om_id" value="${om.id}"/>
            </c:url>
            <c:url value="Omr" var="urlDelete">
              <c:param name="action" value="delete"/>
              <c:param name="om_id" value="${om.id}"/>
            </c:url>
            <c:url value="Omr" var="urlCheck">
              <c:param name="action" value="check"/>
              <c:param name="om_id" value="${om.id}"/>
            </c:url>
            <c:url value="Omr" var="urlDownload">
              <c:param name="action" value="downloadFile"/>
              <c:param name="om_id" value="${om.id}"/>
            </c:url>
            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
              <td>${om.id}</td> 
              <td>${om.name}</td> 
              <td><a href="${urlDownload}&type=1">${om.templateFileName}</a></td>
              <td><a href="${urlDownload}&type=2">${om.ascFileName}</a></td>
              <td><a href="${urlDownload}&type=3">${om.configFileName}</a></td>
              <td>
                <c:if test="${om.fieldsFileName != null && om.fieldsFileName ne ''}">
                  <a href="${urlDownload}&type=4">${om.fieldsFileName}</a>
            	</c:if>
              </td>
              <td align="center">
                <c:choose>
                  <c:when test="${om.active}">
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
                <c:if test="${om.fieldsFileName!=null && om.fieldsFileName ne ''}">
                  <a href="${urlCheck}"><img src="img/action/check.png" alt="Check" title="Check"/></a>
                </c:if>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>