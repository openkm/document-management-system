<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" href="css/style.css" type="text/css" />
  <title>Check Email</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
       <li class="path">
          <a href="utilities.jsp">Utilities</a>
        </li>
        <li class="path">Check email</li>
      </ul>
      <br/>
      <form action="CheckEmail">
        <input type="hidden" name="action" value="send"/>
        <table class="form" width="250px">
          <tr><td>From</td><td><input type="text" name="from" size="25" value="${from}"/></td></tr>
          <tr><td>To</td><td><input type="text" name="to" size="25" value="${to}"/></td></tr>
          <tr><td>Subject</td><td><input type="text" name="subject" size="40" value="${subject}"/></td></tr>
          <tr><td colspan="2">Content</td></tr>
          <tr><td colspan="2"><textarea name="content" cols="60" rows="7">${content}</textarea></td></tr>
          <tr>
            <td colspan="2" align="right">
              <input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton"/>
              <input type="submit" value="Check" class="yesButton"/>
            </td>
          </tr>
        </table>
      </form>
      <br/>
      <c:choose>
        <c:when test="${not empty error}">
          <div class="warn" style="text-align: center;">${error}</div>
        </c:when>
        <c:when test="${not empty success}">
          <div class="ok" style="text-align: center;">${success}</div>
        </c:when>
      </c:choose>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>