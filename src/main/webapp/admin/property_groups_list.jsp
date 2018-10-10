<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.openkm.servlet.admin.BaseServlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="Shortcut icon" href="favicon.ico" />
<link rel="stylesheet" type="text/css" href="css/admin-style.css" />
<script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
<script type="text/javascript">
  $(document).ready(function() {
    $('#scroll').height($(window).height() - 21);
  });
</script>
<title>Metadata Group</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path"><a href="PropertyGroups">Metadata groups</a></li>
        <li class="action">
          <a href="PropertyGroups?action=register"> 
            <img src="img/action/generic.png" alt="Generic" title="Generic" style="vertical-align: middle;" /> Register metadata groups
          </a>
        </li>
        <li class="action">
          <a href="PropertyGroups?action=edit"> 
            <img src="img/action/generic.png" alt="Generic" title="Generic" style="vertical-align: middle;" /> Edit metadata groups
          </a>
        </li>
      </ul>
      <div id="scroll" style="width: 100%; height: 100%; overflow: auto;">
        <br />
        <c:if test="${empty pGroups}">
          <table class="results-old" width="80%">
            <tr>
              <th colspan="2">Group label</th>
              <th colspan="3">Group name</th>
              <th colspan="1">Group info</th>
            </tr>
            <tr>
              <th>Label</th>
              <th>Name</th>
              <th>Width</th>
              <th>Height</th>
              <th>Field</th>
              <th>Others</th>
            </tr>
          </table>
        </c:if>
        <c:forEach var="pGroup" items="${pGroups}">
          <table class="results-old" style="border-bottom: 0" width="80%">
            <thead>
              <tr>
                <th colspan="2">Group label</th>
                <th colspan="3">Group name</th>
                <th colspan="1">Group info</th>
              </tr>
            </thead>
            <tbody>
              <tr class="fuzzy">
                <td colspan="2" align="center"><b>${pGroup.key.label}</b></td>
                <td colspan="3" align="center"><b>${pGroup.key.name}</b></td>
                <td colspan="1" align="center"><i>Visible</i>: ${pGroup.key.visible}<br /> <i>ReadOnly</i>:
                  ${pGroup.key.readonly}</td>
              </tr>
            </tbody>
          </table>
          <table class="results-old id-results" style="border-top: 0" width="80%">
            <thead>
              <tr>
                <th>Label</th>
                <th>Name</th>
                <th>Width</th>
                <th>Height</th>
                <th>Element</th>
                <th>Others</th>
              </tr>
            </thead>
            <tbody>
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
            </tbody>
          </table>
          <br />
        </c:forEach>
      </div>
    </c:when>
    <c:otherwise>
      <div class="error">
        <h3>Only admin users allowed</h3>
      </div>
    </c:otherwise>
  </c:choose>
</body>
</html>