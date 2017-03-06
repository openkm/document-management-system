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
  <title>Crontab</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="CronTab">Crontab</a>
        </li>
        <li class="path">
          <c:choose>
            <c:when test="${action == 'create'}">Create crontab</c:when>
            <c:when test="${action == 'edit'}">Edit crontab</c:when>
            <c:when test="${action == 'delete'}">Delete crontab</c:when>
          </c:choose>
        </li>
      </ul>
      <br/>
      <form action="CronTab" method="post" enctype="multipart/form-data">
        <input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="ct_id" value="${ct.id}"/>
        <table class="form" width="425px">
          <tr>
            <td nowrap="nowrap">Name</td>
            <td><input size="30" class=":required :only_on_blur" name="ct_name" value="${ct.name}"/></td>
          </tr>
          <tr>
            <td nowrap="nowrap">Mail</td>
            <td><input size="30" class=":email :only_on_blur" name="ct_mail" value="${ct.mail}"/></td>
          </tr>
          <tr>
            <td nowrap="nowrap">Expression</td>
            <td><input class=":required :only_on_blur" name="ct_expression" value="${ct.expression}"/></td>
          </tr>
          <tr>
            <td>Active</td>
            <td>
              <c:choose>
                <c:when test="${ct.active}">
                  <input name="ct_active" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="ct_active" type="checkbox"/>
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