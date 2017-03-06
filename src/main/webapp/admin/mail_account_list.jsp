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
  <title>Mail accounts</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Auth">User list</a>
        </li>
        <li class="path">Mail accounts</li>
      </ul>
      <br/>
      <table class="results" width="70%">
        <tr>
          <th>Mail protocol</th><th>Mail host</th><th>Mail user</th>
          <th>Mail folder</th><th>Active</th>
          <th width="100px">
            <c:url value="MailAccount" var="urlCreate">
              <c:param name="action" value="create"/>
              <c:param name="ma_user" value="${ma_user}"/>
            </c:url>
            <a href="${urlCreate}"><img src="img/action/new.png" alt="New account" title="New account"/></a>
          </th>
        </tr>
        <c:forEach var="ma" items="${mailAccounts}" varStatus="row">
          <c:url value="MailAccount" var="urlEdit">
            <c:param name="action" value="edit"/>
            <c:param name="ma_id" value="${ma.id}"/>
            <c:param name="ma_user" value="${ma_user}"/>
          </c:url>
          <c:url value="MailAccount" var="urlDelete">
            <c:param name="action" value="delete"/>
            <c:param name="ma_id" value="${ma.id}"/>
            <c:param name="ma_user" value="${ma_user}"/>
          </c:url>
          <c:url value="MailAccount" var="urlFilter">
            <c:param name="action" value="filterList"/>
            <c:param name="ma_id" value="${ma.id}"/>
            <c:param name="ma_user" value="${ma_user}"/>
          </c:url>
          <c:url value="MailAccount" var="urlServerList">
            <c:param name="action" value="serverList"/>
            <c:param name="ma_id" value="${ma.id}"/>
            <c:param name="ma_user" value="${ma_user}"/>
          </c:url>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${ma.mailProtocol}</td><td>${ma.mailHost}</td>
            <td>${ma.mailUser}</td><td>${ma.mailFolder}</td>
            <td align="center">
              <c:choose>
                <c:when test="${ma.active}">
                  <img src="img/true.png" alt="Active" title="Active"/>
                </c:when>
                <c:otherwise>
                  <img src="img/false.png" alt="Inactive" title="Inactive"/>
                </c:otherwise>
              </c:choose>
            </td>
            <td>
              <a href="${urlEdit}"><img src="img/action/edit.png" alt="Edit" title="Edit"/></a>
              &nbsp;
              <a href="${urlDelete}"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a>
              &nbsp;
              <a href="${urlFilter}"><img src="img/action/filter.png" alt="Filters" title="Filters"/></a>
              &nbsp;
              <a href="${urlServerList}"><img src="img/action/table.png" alt="Server List" title="Server List"/></a>
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