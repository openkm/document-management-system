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
  <link rel="stylesheet" type="text/css" href="../js/cleditor/jquery.cleditor.css" />
  <script src="../js/jquery-1.7.1.min.js" type="text/javascript"></script>
  <script src="../js/vanadium-min.js" type="text/javascript"></script>
  <script src="../js/cleditor/jquery.cleditor.js" type="text/javascript"></script>
  <script type="text/javascript">
  	$(document).ready(function() {
  	  // http://premiumsoftware.net/cleditor/docs/GettingStarted.html
  	  $("#me_message").cleditor({
  	    width: 500,
	    height: 250,
	    controls: "bold italic underline strikethrough | font size style | " +
	      "color highlight removeformat | bullets numbering | " +
	      "rule link unlink | undo redo | source"
	  });
  	  <c:choose>
        <c:when test="${action == 'messageDelete'}">
          $("#me_action").attr('disabled', 'disabled');
          $("#me_type").attr('disabled', 'disabled');
          $("#me_show").attr('disabled', 'disabled');
          $("#me_message").attr('readonly', true);
        </c:when>
      </c:choose>
  	});
  </script>
  <title>Message edit</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <u:constantsMap className="com.openkm.frontend.client.bean.GWTUINotification" var="GWTUINotification"/>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url var="messageList" value="LoggedUsers">
      	<c:param name="action" value="messageList"></c:param>
      </c:url>
      <ul id="breadcrumb">
        <li class="path">
          <a href="Auth">User list</a>
        </li>
        <li class="path">
          <a href="${messageList}">Message queue</a>
        </li>
        <li class="path">
          <c:choose>
            <c:when test="${action == 'messageCreate'}">Create message</c:when>
            <c:when test="${action == 'messageEdit'}">Edit message</c:when>
            <c:when test="${action == 'messageDelete'}">Delete message</c:when>
          </c:choose>
        </li>
      </ul>
      <br/>
      <form action="LoggedUsers">
      	<input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <input type="hidden" name="me_id" value="${me.id}"/>
        <table class="form" width="372px">
        <tr>
            <td>Action</td>
            <td width="100%">
              <select name="me_action" id="me_action">
                <option value="">-</option>
                  <c:choose>
                    <c:when test="${me.action == GWTUINotification.ACTION_LOGOUT}">
                      <option value="${GWTUINotification.ACTION_LOGOUT}" selected="selected">Logout</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${GWTUINotification.ACTION_LOGOUT}">Logout</option>
                    </c:otherwise>
                  </c:choose>
              </select>
            </td>
        </tr>
        <tr>
            <td>Type</td>
            <td width="100%">
              <select name="me_type" id="me_type" class=":integer :required :only_on_blur">
                <option value="">-</option>
                  <c:choose>
                    <c:when test="${me.type == GWTUINotification.TYPE_TEMPORAL}">
                      <option value="${GWTUINotification.TYPE_TEMPORAL}" selected="selected">Temporal</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${GWTUINotification.TYPE_TEMPORAL}">Temporal</option>
                    </c:otherwise>
                  </c:choose>
              	  <c:choose>
                    <c:when test="${me.type == GWTUINotification.TYPE_PERMANENT}">
                      <option value="${GWTUINotification.TYPE_PERMANENT}" selected="selected">Permanent</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${GWTUINotification.TYPE_PERMANENT}">Permanent</option>
                    </c:otherwise>
                  </c:choose>
              </select>
            </td>
        </tr>
        <tr>
          <td>Show</td>
          <td>
            <c:choose>
              <c:when test="${me.show}">
                <input name="me_show" id="me_show" type="checkbox" checked="checked"/>
              </c:when>
              <c:otherwise>
                <input name="me_show" id="me_show" type="checkbox"/>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>	
          <td colspan="2">Message</td>
        </tr>
        <tr>	
          <td colspan="2">
            <textarea class=":required :only_on_blur" name="me_message" id="me_message" rows="10" cols="60">${me.message}</textarea>
          </td>
        </tr>
        <tr>
          <td colspan="2" align="right">
            <input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton"/>
            <c:choose>
                <c:when test="${action == 'messageCreate'}"><input type="submit" value="Create" class="yesButton"/></c:when>
                <c:when test="${action == 'messageEdit'}"><input type="submit" value="Edit" class="yesButton"/></c:when>
                <c:when test="${action == 'messageDelete'}"><input type="submit" value="Delete" class="yesButton"/></c:when>
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