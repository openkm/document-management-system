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
  <script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
  <title>List indexes</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url value="ListIndexes" var="urlActivate">
        <c:param name="id" value="${id}"/>
        <c:param name="showTerms" value="true"/>
      </c:url>
      <c:url value="ListIndexes" var="urlDeactivate">
        <c:param name="id" value="${id}"/>
        <c:param name="showTerms" value="false"/>
      </c:url>
      <ul id="breadcrumb">
        <li class="path">
          <a href="utilities.jsp">Utilities</a>
        </li>
        <li class="path">List indexes</li>
        <li class="action">
          <c:choose>
            <c:when test="${showTerms}">
              <a href="${urlDeactivate}">
                <img src="img/action/enabled.png" alt="Disable" title="Disable" style="vertical-align: middle;"/>
                Show terms
              </a>
            </c:when>
            <c:otherwise>
              <a href="${urlActivate}">
                <img src="img/action/disabled.png" alt="Enable" title="Enable" style="vertical-align: middle;"/>
                Show terms
              </a>
            </c:otherwise>
          </c:choose>
        </li>
        <li class="action">
          <a href="ListIndexes?action=search">
            <img src="img/action/examine.png" alt="Search" title="Search" style="vertical-align: middle;"/>
            Search indexes
          </a>
        </li>
      </ul>
      <br/>
      <table class="results" width="60%">
        <thead>
          <tr class="fuzzy">
            <td colspan="2" align="right">
              Max: ${max}
              &nbsp;
              <c:choose>
                <c:when test="${id > 0}">
                  <a href="ListIndexes?id=0&showTerms=${showTerms}"><img src="img/action/first.png"/></a>
                </c:when>
                <c:otherwise>
                  <img src="img/action/first_disabled.png"/>
                </c:otherwise>
              </c:choose>
              <c:choose>
                <c:when test="${prev}">
                  <a href="ListIndexes?id=${id - 1}&showTerms=${showTerms}"><img src="img/action/previous.png"/></a>
                </c:when>
                <c:otherwise>
                  <img src="img/action/previous_disabled.png"/>
                </c:otherwise>
              </c:choose>
              <c:choose>
                <c:when test="${next}">
                  <a href="ListIndexes?id=${id + 1}&showTerms=${showTerms}"><img src="img/action/next.png"/></a>
                </c:when>
                <c:otherwise>
                  <img src="img/action/next_disabled.png"/>
                </c:otherwise>
              </c:choose>
              <c:choose>
                <c:when test="${id < max}">
                  <a href="ListIndexes?id=${max}&showTerms=${showTerms}"><img src="img/action/last.png"/></a>
                </c:when>
                <c:otherwise>
                  <img src="img/action/last_disabled.png"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr><th>Field</th><th>Value</th></tr>
        </thead>
        <tbody>
          <tr class="even">
            <td><b>#</b></td><td>${id}</td>
          </tr>
          <c:forEach var="fld" items="${fields}" varStatus="row">
            <tr class="${row.index % 2 == 0 ? 'odd' : 'even'}">
              <td width="150px"><b>${fld.name}</b></td>
              <td><u:escapeHtml string="${fld.value}"/></td>
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