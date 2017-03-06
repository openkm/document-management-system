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
  <title>Automation rules</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Automation">Automation rules</a>
        </li>
      </ul>
      <br/>
      <table class="results" width="70%">
        <tr>
      	  <th>#</th><th>Order</th><th>Name</th><th>Event</th><th>At</th><th>Validations</th>
      	  <th>Actions</th><th>Exclusive</th><th>Active</th>
		  <th width="80px">
            <c:url value="Automation" var="urlCreate">
          	  <c:param name="action" value="create"/>
            </c:url>
            <a href="${urlCreate}"><img src="img/action/new.png" alt="New rule" title="New rule"/></a>
          </th>
          <c:forEach var="ar" items="${automationRules}" varStatus="row">
            <c:url value="Automation" var="urlEdit">
              <c:param name="action" value="edit"/>
              <c:param name="ar_id" value="${ar.id}"/>
            </c:url>
            <c:url value="Automation" var="urlDelete">
              <c:param name="action" value="delete"/>
              <c:param name="ar_id" value="${ar.id}"/>
            </c:url>
            <c:url value="Automation" var="urlDefinition">
              <c:param name="action" value="definitionList"/>
              <c:param name="ar_id" value="${ar.id}"/>
            </c:url>
            <c:url value="Automation" var="urlAction">
              <c:param name="action" value="actionList"/>
              <c:param name="ar_id" value="${ar.id}"/>
            </c:url>
            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
              <td width="20px">${row.index + 1}</td>
              <td>${ar.order}</td><td>${ar.name}</td>
              <td>${events.get(ar.event)}</td><td>${ar.at}</td>
              <td align="center">${ar.validations.size()}</td><td align="center">${ar.actions.size()}</td>
              <td align="center">
                <c:choose>
                  <c:when test="${ar.exclusive}">
                    <img src="img/true.png" alt="Active" title="Active"/>
                  </c:when>
                  <c:otherwise>
                    <img src="img/false.png" alt="Inactive" title="Inactive"/>
                  </c:otherwise>
                </c:choose>
              </td>
              <td align="center">
                <c:choose>
                  <c:when test="${ar.active}">
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
                <a href="${urlDefinition}"><img src="img/action/filter.png" alt="Definition" title="Definition"/></a>
              </td>
            </tr>
          </c:forEach>
	    </tr>
	  </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>