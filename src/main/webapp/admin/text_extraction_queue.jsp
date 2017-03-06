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
  <link rel="stylesheet" href="css/style.css" type="text/css" />
  <link rel="stylesheet" type="text/css" href="css/fixedTableHeader.css" />
  <script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
  <script type="text/javascript" src="js/fixedTableHeader.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
    	TABLE.fixHeader('#trick');
	});
  </script>
  <title>Text Extraction Queue</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="TextExtractionQueue">Text extraction queue</a>
        </li>
        <li class="action">
          <a href="stats.jsp">
            <img src="img/action/generic.png" alt="Generic" title="Generic" style="vertical-align: middle;"/>
            Statistics
          </a>
        </li>
        <li class="action">
          <a href="TextExtractionQueue">
            <img src="img/action/refresh.png" alt="Refresh" title="Refresh" style="vertical-align: middle;"/>
            Refresh
          </a>
        </li>
      </ul>
      <br/>
      <table id="treat" class="results" width="90%">
        <thead>
          <tr class="fuzzy">
            <td colspan="4" style="text-align: center; font-weight: bold; font-size: 14px">
              Extractions In Progress
              <c:if test="${inProgress}">
                <span style="text-align: center; font-weight: bold; font-size: 12px">
                  (Running)
                </span>
              </c:if>
            </td>
          </tr>
          <tr><th>#</th><th>UUID</th><th>Path</th><th>Date</th></tr>
        </thead>
        <tbody>
          <c:forEach var="work" items="${inProgressWorks}" varStatus="row">
            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
              <td>${row.index + 1}</td>
              <td nowrap="nowrap">${work.docUuid}</td>
              <td>${work.docPath}</td>
              <td nowrap="nowrap"><u:formatDate calendar="${work.date}"/></td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
      <br/>
      <table id="trick" class="results" width="90%">
        <thead>
          <tr class="fuzzy">
            <td colspan="4" style="text-align: center; font-weight: bold; font-size: 14px">
              Pending Extractions
              <c:if test="${!inProgress}">
                <span style="text-align: center; font-weight: bold; font-size: 12px">
                  (Last execution:
                  <c:choose>
                    <c:when test="${lastExecution != null}">
                      <u:formatDate calendar="${lastExecution}"/>
                    </c:when>
                    <c:otherwise>
                      I don't know
                    </c:otherwise>
                  </c:choose>
                  )
                </span>
              </c:if>
            </td>
          </tr>
          <tr><th>#</th><th>UUID</th><th>Path</th><th>Date</th></tr>
        </thead>
        <tbody>
          <c:forEach var="work" items="${pendingWorks}" varStatus="row">
            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
              <td>${row.index + 1}</td>
              <td nowrap="nowrap">${work.docUuid}</td>
              <td>${work.docPath}</td>
              <td nowrap="nowrap"><u:formatDate calendar="${work.date}"/></td>
            </tr>
          </c:forEach>
          <c:if test="${pendingSize > 0}">
            <tr class="fuzzy">
              <td colspan="4" style="text-align: center; font-weight: bold;">
                Total pending extractions: ${pendingSize}
              </td>
            </tr>
          </c:if>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>