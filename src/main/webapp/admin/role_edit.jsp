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
  <script type="text/javascript">
    $(document).ready(function() {
    	$('form').bind('submit', function(event) {
        	var error = $('input[name="rol_id"] + span.vanadium-invalid');
    		
    		if (error == null || error.text() == '') {
        		return true;
        	} else {
        		return false;
            }
	   	});
	});
  </script>
  <title>Role edit</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Auth?action=roleList">Role list</a>
        </li>
        <li class="path">
          <c:choose>
            <c:when test="${action == 'roleCreate'}">Create role</c:when>
            <c:when test="${action == 'roleEdit'}">Edit role</c:when>
            <c:when test="${action == 'roleDelete'}">Delete role</c:when>
          </c:choose>
        </li>
      </ul>
      <br/>
      <form action="Auth">
        <input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <input type="hidden" name="csrft" value="${csrft}"/>
        <table class="form" width="300px">
          <tr>
            <td>Id</td>
            <td width="100%">
              <c:choose>
                <c:when test="${action != 'roleCreate'}">
                  <input class=":required :only_on_blur" name="rol_id" size="25" value="${rol.id}" readonly="readonly"/>
                </c:when>
                <c:otherwise>
                  <input class=":required :only_on_blur :ajax;Auth?action=validateRole" name="rol_id" value=""/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td>Active</td>
            <td>
              <c:choose>
                <c:when test="${rol.active}">
                  <input name="rol_active" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="rol_active" type="checkbox"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td colspan="2" align="right">
              <input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton"/>
              <c:choose>
                <c:when test="${action == 'roleCreate'}"><input type="submit" value="Create" class="yesButton"/></c:when>
                <c:when test="${action == 'roleEdit'}"><input type="submit" value="Edit" class="yesButton"/></c:when>
                <c:when test="${action == 'roleDelete'}"><input type="submit" value="Delete" class="yesButton"/></c:when>
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