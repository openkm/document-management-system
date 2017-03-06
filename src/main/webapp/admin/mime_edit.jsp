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
  <script src="../js/jquery-1.7.1.min.js" type="text/javascript"></script>
  <script src="../js/vanadium-min.js" type="text/javascript"></script>
  <title>Mime Type</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="MimeType">Mime types</a>
        </li>
        <li class="path">
          <c:choose>
            <c:when test="${action == 'create'}">Create mime type</c:when>
            <c:when test="${action == 'edit'}">Edit mime type</c:when>
            <c:when test="${action == 'delete'}">Delete mime type</c:when>
          </c:choose>
        </li>
      </ul>
      <br/>
      <form action="MimeType" method="post" enctype="multipart/form-data">
        <input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="mt_id" value="${mt.id}"/>
        <table class="form" width="425px">
          <tr>
            <td>Name</td>
            <td><input class=":required :only_on_blur" size="36" name="mt_name" value="${mt.name}"/></td>
          </tr>
          <tr>
            <td>Description</td>
            <td><input class=":required :only_on_blur" size="36" name="mt_description" value="${mt.description}"/></td>
          </tr>
          <tr>
            <td>Extensions</td>
            <td><input class=":required :only_on_blur" name="mt_extensions" value="${extensions}"/></td>
          </tr>
          <tr>
            <td>Image</td>
            <td>
              <c:choose>
                <c:when test="${action == 'create'}">
                  <input class=":required :only_on_blur" type="file" name="image"/>
                </c:when>
                <c:otherwise>
                  <c:url value="/mime/${mt.name}" var="urlIcon">
                  </c:url>
                  <table cellpadding="0" cellspacing="0"><tr><td><img src="${urlIcon}"/>&nbsp;</td><td><input type="file" name="image"/></td></tr></table>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td>Search</td>
            <td>
              <c:choose>
                <c:when test="${mt.search}">
                  <input name="mt_search" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="mt_search" type="checkbox"/>
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