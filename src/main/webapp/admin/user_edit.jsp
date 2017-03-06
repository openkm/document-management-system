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
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <script src="../js/jquery-1.7.1.min.js" type="text/javascript"></script>
  <script src="../js/vanadium-min.js" type="text/javascript"></script>
  <script type="text/javascript">
    $(document).ready(function() {
    	$('form').bind('submit', function(event) {
        	var error = $('input[name="usr_id"] + span.vanadium-invalid');
    		
    		if (error == null || error.text() == '') {
        		return true;
        	} else {
        		return false;
            }
	   	});
	});
    
    $(window).load(function() {
        if ($.browser.webkit) {
            setTimeout(function() {
                $('input:-webkit-autofill').each(function() {
                    var name = $(this).attr('name');
                    $(this).after(this.outerHTML).remove();
                    $('input[name=' + name + ']').val('');
                });
            }, 100);
        }
    });
  </script>
  <title>User edit</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Auth">User list</a>
        </li>
        <li class="path">
          <c:choose>
            <c:when test="${action == 'userCreate'}">Create user</c:when>
            <c:when test="${action == 'userEdit'}">Edit user</c:when>
            <c:when test="${action == 'userDelete'}">Delete user</c:when>
          </c:choose>
        </li>
      </ul>
      <br/>
      <form action="Auth">
        <input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <input type="hidden" name="csrft" value="${csrft}"/>
        <table class="form" width="372px">
          <tr>
            <td>Id</td>
            <td width="100%">
              <c:choose>
                <c:when test="${action != 'userCreate'}">
                  <input class=":required :only_on_blur" name="usr_id" value="${usr.id}" readonly="readonly"/>
                </c:when>
                <c:otherwise>
                  <input class=":required :only_on_blur :ajax;Auth?action=validateUser" name="usr_id" value=""/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td>Password</td>
            <td>
              <c:choose>
                <c:when test="${action == 'userCreate'}">
                  <input class=":required :only_on_blur" type="password" name="usr_password" id="usr_password" value="" autocomplete="off"/>
                </c:when>
                <c:otherwise>
                  <input class="" type="password" name="usr_password" id="usr_password" value="" autocomplete="off"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td nowrap="nowrap">Confirm password</td>
            <td><input class=":same_as;usr_password :only_on_blur" type="password" value="" autocomplete="off"/></td>
          </tr>
          <tr>
            <td>Name</td>
            <td><input class=":required :only_on_blur" name="usr_name" size="25" value="${usr.name}"/></td>
          </tr>
          <tr>
            <td>Mail</td>
            <td><input class=":email :required :only_on_blur" name="usr_email" size="30" value="${usr.email}"/></td>
          </tr>
          <tr>
            <td>Active</td>
            <td>
              <c:choose>
                <c:when test="${usr.active}">
                  <input name="usr_active" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="usr_active" type="checkbox"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td>Roles</td>
            <td>
              <select multiple="multiple" name="usr_roles" size="10">
                <c:forEach var="role" items="${roles}">
                  <c:choose>
                    <c:when test="${u:contains(usr.roles, role)}">
                      <option value="${role.id}" selected="selected">${role.id}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${role.id}">${role.id}</option>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </select>
            </td>
          </tr>
          <tr>
            <td colspan="2" align="right">
              <input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton"/>
              <c:choose>
                <c:when test="${action == 'userCreate'}"><input type="submit" value="Create" class="yesButton"/></c:when>
                <c:when test="${action == 'userEdit'}"><input type="submit" value="Edit" class="yesButton"/></c:when>
                <c:when test="${action == 'userDelete'}"><input type="submit" value="Delete" class="yesButton"/></c:when>
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