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
  <script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
  <title>Repository Edit</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="utilities.jsp">Utilities</a>
        </li>
        <li class="path">
          <a href="DbRepositoryView?uuid=${uuid}">Repository view</a>
        </li>
        <li class="path">Repository edit</li>
      </ul>
      <br/>
      <form action="DbRepositoryView" method="post">
        <input type="hidden" name="action" value="editPersist"/>
        <input type="hidden" name="uuid" value="${uuid}"/>
        <input type="hidden" name="group" value="${group}"/>
        <input type="hidden" name="property" value="${property}"/>
        <table class="form" width="350px">
          <tr><td>Node</td><td><i>${path}</i></td></tr>
          <tr><td>Property</td><td><i>${property}</i></td></tr>
          <tr>
            <td>Value</td>
            <td>
              <c:choose>
                <c:when test="${field == 'Input'}">
                  <input size="64" type="text" name="value" value="${value}"/>
                </c:when>
                <c:otherwise>
                  <textarea cols="75" rows="15" name="value" id="value">${value}</textarea>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td colspan="2" align="right">
              <input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton"/>
              <input type="submit" value="Edit" class="yesButton"/>
            </td>
          </tr>
        </table>
      </form>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>