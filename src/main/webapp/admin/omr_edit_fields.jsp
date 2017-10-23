<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.openkm.core.Config" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css?v=%{TIMESTAMP}%" />
  <script src="../js/jquery-1.8.3.min.js" type="text/javascript"></script>
  <script src="../js/vanadium-min.js" type="text/javascript"></script>
  <title>OMR Template</title>
</head>
<body> 
<c:set var="isAdmin"><%=request.isUserInRole(Config.DEFAULT_ADMIN_ROLE)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Omr">OMR template</a>
        </li>
        <li class="path">
          <c:choose>
            <c:when test="${action == 'editFields'}">Edit Fields File</c:when>
          </c:choose>
        </li>
      </ul>
      <br/>
  	<form action="Omr" method="post" enctype="multipart/form-data">
  	<input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="om_id" value="${om.id}"/>
        <table class="form" width="425px">
          
          <tr>
		  	<td valign="top">Fields</td>
		  	<td valign="top">
		  		<c:if test="${om.fieldsFileName!=null && om.fieldsFileName ne ''}">
			  		<c:url value="Omr" var="urlDownload">
		          		<c:param name="action" value="downloadFile"/>
		          		<c:param name="om_id" value="${om.id}"/>
		        	</c:url>
		        	<a href="${urlDownload}&type=2">${om.fieldsFileName}</a><br/>
          		</c:if>
		  		<input class=":required :only_on_blur" type="file" name="file"/>
		  	</td>
		  </tr>
          <tr>
            <td colspan="2" align="right">
              <div id="buttons">
              	<input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton"/>
              	<input type="submit" value="Upload" class="yesButton"/>
              </div>
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