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
  		<c:choose>
        <c:when test="${action == 'create'}">
        </c:when>
        <c:when test="${action == 'edit'}">
          $("#ar_at").attr('disabled','disabled');
        </c:when>
        <c:when test="${action == 'delete'}">
          $("#ar_order").attr('readonly', true);
          $("#ar_name").attr('readonly', true);
          $("#ar_event").attr('disabled','disabled');
          $("#ar_at").attr('disabled','disabled');
          $("#ar_exclusive").attr('disabled','disabled');
          $("#ar_active").attr('disabled','disabled');
        </c:when>
      </c:choose> 
  	});
  </script>
<title>Automation rule</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Automation">Automation rules</a>
        </li>
        <li class="path">
          <c:choose>
            <c:when test="${action == 'create'}">Create automation rule</c:when>
            <c:when test="${action == 'edit'}">Edit automation rule</c:when>
            <c:when test="${action == 'delete'}">Delete automation rule</c:when>
          </c:choose>
        </li>
      </ul>
      <br/>
      <form action="Automation" id="form">
        <input type="hidden" name="action" id="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <input type="hidden" name="ar_id" value="${ar.id}"/>
        <table class="form" width="345px" align="center">
          <tr>
            <td nowrap="nowrap">Order</td>
            <td><input size="4" maxlength="4" class=":integer :required :only_on_blur" name="ar_order" value="${ar.order}" id="ar_order"/></td>
          </tr>
          <tr>
            <td nowrap="nowrap">Name</td>
            <td><input size="30" maxlength="255" class=":required :only_on_blur" name="ar_name" value="${ar.name}" id="ar_name" /></td>
          </tr>
          <tr>
            <td nowrap="nowrap">Event</td>
            <td>
              <select name="ar_event"  class=":required :min_length;1 :only_on_blur" id="ar_event">
              	<option value="">-</option>
              	<c:forEach var="event" items="${events}">
    			  <c:choose>
                    <c:when test="${event.key == ar.event}">
                      <option value="${event.key}" selected="selected">${event.value}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${event.key}">${event.value}</option>
                    </c:otherwise>
                  </c:choose>
				</c:forEach>
           	  </select>
            </td>
          </tr>
          <tr>
            <td nowrap="nowrap">At</td>
            <td>
      		  <select name="ar_at" class=":required :min_length;1 :only_on_blur" id="ar_at">
              	<option value="">-</option>
            	<c:forEach var="at" items="${ats}">
            	  <c:choose>
                    <c:when test="${at == ar.at}">
                      <option value="${at}" selected="selected">${at}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${at}">${at}</option>
                    </c:otherwise>
                  </c:choose>
	    		</c:forEach>
           	  </select>
            </td>
          </tr>
          <tr>
            <td>Exclusive</td>
            <td>
              <c:choose>
                <c:when test="${ar.exclusive}">
                  <input name="ar_exclusive" id="ar_exclusive" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="ar_exclusive" id="ar_exclusive" type="checkbox"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td>Active</td>
            <td>
              <c:choose>
                <c:when test="${ar.active}">
                  <input name="ar_active" id="ar_active" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="ar_active" id="ar_active" type="checkbox"/>
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