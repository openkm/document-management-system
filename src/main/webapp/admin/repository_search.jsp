<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.bean.Repository" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" href="css/style.css" type="text/css" />
  <title>Repository Search</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
   <c:choose>
    <c:when test="${isAdmin}">
      <c:set var="root"><%=Repository.ROOT%></c:set>
      <h1>Repository Search</h1>
      <form action="RepositorySearch">
        <table class="form">
          <tr>
            <td><b>Predefined</b></td>
            <td>
              <c:url value="RepositorySearch" var="urlPreLocked">
                <c:param name="type" value="xpath"/>
                <c:param name="statement" value="/jcr:root/${root}//element(*,okm:document)[@jcr:lockOwner]/@jcr:lockOwner"/>
              </c:url>
              <c:url value="RepositorySearch" var="urlPreCreated">
                <c:param name="type" value="xpath"/>
                <c:param name="statement" value="/jcr:root/${root}//element(*, okm:document)[@okm:author='okmAdmin']/(@jcr:lockOwner|@jcr:created)"/>
              </c:url>
              <c:url value="RepositorySearch" var="urlPreProperty">
                <c:param name="type" value="xpath"/>
                <c:param name="statement" value="/jcr:root/${root}//element(*, okg:test)"/>
              </c:url>
              <c:url value="RepositorySearch" var="urlPreSubscription">
                <c:param name="type" value="xpath"/>
                <c:param name="statement" value="/jcr:root/${root}//element(*, mix:notification)/@okm:subscriptors"/>
              </c:url>
              <a href="${urlPreLocked}">Locked documents</a>
              -
              <a href="${urlPreCreated}">Documents created by 'okmAdmin'</a>
              -
              <a href="${urlPreProperty}">Documents with metadata group 'okg:test'</a>
              -
              <a href="${urlPreSubscription}">Documents subscribed</a>
            </td>
          </tr>
          <tr>
            <td><b>Query</b></td>
            <td><textarea cols="100" name="statement">${statement}</textarea></td>
          </tr>
          <tr>
            <td><b>Type</b></td>
            <td>
              <select name="type">
                <c:choose>
                  <c:when test="${type == 'xpath'}">
                    <option value="xpath" selected="selected">XPath</option>
                  </c:when>
                  <c:otherwise>
                    <option value="xpath">XPath</option>
                  </c:otherwise>
                </c:choose>
                <c:choose>
                  <c:when test="${type == 'sql'}">
                    <option value="sql" selected="selected">SQL</option>
                  </c:when>
                  <c:otherwise>
                    <option value="sql">SQL</option>
                  </c:otherwise>
                </c:choose>
              </select>
              <a target="_blank" href="http://people.apache.org/~mreutegg/jcr-query-translator/translator.html">JCR Query Translator</a>
            </td>
          </tr>
          <tr>
            <td colspan="2" align="right"><input type="submit" value="Send"/></td>
          </tr>
        </table>
      </form>
      <br/>
      <c:if test="${size != null }">
        <center><b>Count:</b> ${size}</center>
      </c:if>
      <table class="results" width="100%">
        <tr>
          <c:forEach var="col" items="${columns}">
            <th>${col}</th>
          </c:forEach>
        </tr>
        <c:forEach var="result" items="${results}" varStatus="row">
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <c:forEach var="tp" items="${result}">
              <td>${tp}</td>
            </c:forEach>
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