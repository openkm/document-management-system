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
  <title>User Profiles</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Profile">User profiles</a>
        </li>
      </ul>
      <br/>
      <table class="results" width="40%">
        <tr>
          <th>Name</th><th width="25px">Active</th>
          <th width="75px">
            <c:url value="Profile" var="urlCreate">
              <c:param name="action" value="create"/>
            </c:url>
            <a href="${urlCreate}"><img src="img/action/new.png" alt="New profile" title="New profile"/></a>
          </th>
        </tr>
        <c:forEach var="prf" items="${userProfiles}" varStatus="row">
          <c:url value="Profile" var="urlEdit">
            <c:param name="action" value="edit"/>
            <c:param name="prf_id" value="${prf.id}"/>
          </c:url>
          <c:url value="Profile" var="urlDelete">
            <c:param name="action" value="delete"/>
            <c:param name="prf_id" value="${prf.id}"/>
          </c:url>
          <c:url value="Profile" var="urlClone">
            <c:param name="action" value="clone"/>
            <c:param name="prf_id" value="${prf.id}"/>
          </c:url>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${prf.name}</td>
            <td align="center">
              <c:choose>
                <c:when test="${prf.active}">
                  <img src="img/true.png" alt="Active" title="Active"/>
                </c:when>
                <c:otherwise>
                  <img src="img/false.png" alt="Inactive" title="Inactive"/>
                </c:otherwise>
              </c:choose>
            </td>
            <td align="center">
              <a href="${urlEdit}"><img src="img/action/edit.png" alt="Edit" title="Edit"/></a>
              <c:if test="${prf.id != 1}">
                &nbsp;
                <a href="${urlDelete}"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a>
              </c:if>
              &nbsp;
              <a href="${urlClone}"><img src="img/action/clone.png" alt="Clone" title="Clone"/></a>
            </td>
          </tr>
        </c:forEach>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>