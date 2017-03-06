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
  <title>Metadata Group</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="PropertyGroups">Metadata groups</a>
        </li>
        <li class="action">
          <a href="PropertyGroups?action=register">
            <img src="img/action/generic.png" alt="Generic" title="Generic" style="vertical-align: middle;"/>
            Register metadata groups
          </a>
        </li>
        <li class="action">
          <a href="PropertyGroups?action=edit">
            <img src="img/action/generic.png" alt="Generic" title="Generic" style="vertical-align: middle;"/>
            Edit metadata groups
          </a>
        </li>
      </ul>
      <br/>
        <c:if test="${empty pGroups}">
          <table class="results" width="80%">
            <tr><th colspan="2">Group label</th><th colspan="3">Group name</th><th colspan="1">Group info</th></tr>
            <tr><th>Label</th><th>Name</th><th>Width</th><th>Height</th><th>Field</th><th>Others</th></tr>
          </table>
        </c:if>
        <c:forEach var="pGroup" items="${pGroups}">
          <table class="results" width="80%">
            <tr><th colspan="2">Group label</th><th colspan="3">Group name</th><th colspan="1">Group info</th></tr>
            <tr class="fuzzy">
              <td colspan="2" align="center"><b>${pGroup.key.label}</b></td>
              <td colspan="3" align="center"><b>${pGroup.key.name}</b></td>
              <td colspan="1" align="center">
                <i>Visible</i>: ${pGroup.key.visible}<br/>
                <i>ReadOnly</i>: ${pGroup.key.readonly}
              </td>
            </tr>
            <tr><th>Label</th><th>Name</th><th>Width</th><th>Height</th><th>Field</th><th>Others</th></tr>
            <c:forEach var="pgForm" items="${pGroup.value}" varStatus="row">
              <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
                <td>${pgForm.label}</td>
                <td>${pgForm.name}</td>
                <td>${pgForm.width}</td>
                <td>${pgForm.height}</td>
                <td>${pgForm.field}</td>
                <td width="45%">${pgForm.others}</td>
              </tr>
            </c:forEach>
          </table>
          <br/>
        </c:forEach>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>