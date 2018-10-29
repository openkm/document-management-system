<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/admin-style.css" />
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="../js/vanadium-min.js"></script>
  <title>Twitter account</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url value="TwitterAccount" var="urlTwitterAccountList">
        <c:param name="ta_user" value="${ta.user}"/>
      </c:url>
      <ul id="breadcrumb">
        <li class="path">
          <a href="Auth">User list</a>
        </li>
        <li class="path">
          <a href="${urlTwitterAccountList}">Twitter accounts</a>
        </li>
        <li class="path">
          <c:choose>
            <c:when test="${action == 'create'}">Create twitter account</c:when>
            <c:when test="${action == 'edit'}">Edit twitter account</c:when>
            <c:when test="${action == 'delete'}">Delete twitter account</c:when>
          </c:choose>
        </li>
      </ul>
      <br/>
      <form action="TwitterAccount">
        <input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <input type="hidden" name="ta_id" value="${ta.id}"/>
        <input type="hidden" name="ta_user" value="${ta.user}"/>
        <table class="form" width="330px">
          <tr>
            <td nowrap="nowrap">Twitter user</td>
            <td><input class=":required :only_on_blur" name="ta_tuser" value="${ta.twitterUser}"/></td>
          </tr>
          <tr>
            <td>Active</td>
            <td>
              <c:choose>
                <c:when test="${ta.active}">
                  <input name="ta_active" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="ta_active" type="checkbox"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td colspan="2" align="right">
              <input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton"/>
              <c:choose>
                <c:when test="${action == 'create'}"><input type="submit" value="Create" class="yesButton"/></c:when>
                <c:when test="${action == 'edit'}"><input type="submit" value="Edit" class="yesButton"/></c:when>
                <c:when test="${action == 'delete'}"><input type="submit" value="Delete" class="yesButton"/></c:when>
              </c:choose>
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