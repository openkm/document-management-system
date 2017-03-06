<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.core.Config" %>
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
  <title>Report</title>
</head>
<body>
  <c:set var="isAdmin"><%=request.isUserInRole(Config.DEFAULT_ADMIN_ROLE)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Report">Reports</a>
        </li>
        <li class="path">
          <c:choose>
            <c:when test="${action == 'create'}">Create report</c:when>
            <c:when test="${action == 'edit'}">Edit report</c:when>
            <c:when test="${action == 'delete'}">Delete report</c:when>
          </c:choose>
        </li>
      </ul>
      <br/>
      <form action="Report" method="post" enctype="multipart/form-data">
        <input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="rp_id" value="${rp.id}"/>
        <table class="form" width="425px">
          <tr>
            <td nowrap="nowrap">Name</td>
            <td><input class=":required :only_on_blur" name="rp_name" value="${rp.name}"/></td>
          </tr>
          <tr>
            <td>Active</td>
            <td>
              <c:choose>
                <c:when test="${rp.active}">
                  <input name="rp_active" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="rp_active" type="checkbox"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td>File</td>
            <td>
              <c:choose>
                <c:when test="${action == 'create'}">
                  <input class=":required :only_on_blur" type="file" name="file"/>
                </c:when>
                <c:otherwise>
                  <input type="file" name="file"/>
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