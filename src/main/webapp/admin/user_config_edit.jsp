<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="../css/chosen.css" />
  <link rel="stylesheet" type="text/css" href="css/admin-style.css" />
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="../js/vanadium-min.js"></script>
  <script type="text/javascript" src="../js/chosen.jquery.js"></script>
   <script type="text/javascript">
   	$(document).ready(function() {
   	 $('select#uc_profile').chosen({disable_search_threshold : 10 });
   	});
   </script>
  <title>User Config</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Auth">User list</a>
        </li>
        <li class="path">Edit user config</li>
      </ul>
      <br/>
      <form action="UserConfig">
        <input type="hidden" name="persist" value="${persist}"/>
        <input type="hidden" name="uc_user" value="${uc.user}"/>
        <table class="form" width="150px">
          <tr>
            <td>User</td>
            <td><input name="uc_user" value="${uc.user}" readonly/></td>
          </tr>
          <tr>
            <td nowrap="nowrap">Profile</td>
            <td>
              <select name="uc_profile" id="uc_profile" data-placeholder="Select profile" style="width: 100%">
                <c:forEach var="up" items="${profiles}">
                  <c:choose>
                    <c:when test="${up.id == uc.profile.id}">
                      <option value="${up.id}" selected="selected">${up.name}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${up.id}">${up.name}</option>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </select>
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