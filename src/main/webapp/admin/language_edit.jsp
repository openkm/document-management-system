<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script src="../js/vanadium-min.js" type="text/javascript"></script>
  <title>Language edit</title>
</head>
<body>  
  <c:choose>
    <c:when test="${u:isMultipleInstancesAdmin()}">
	 <ul id="breadcrumb">
	   <li class="path">
	     <a href="Language">Language list</a>
	   </li>
	   <li class="path">
	     <c:choose>
	   	   <c:when test="${action == 'create'}">Create language</c:when>
	   	   <c:when test="${action == 'edit'}">Edit language</c:when>
	   	   <c:when test="${action == 'delete'}">Delete language</c:when>
	      </c:choose>
	    </li>
	  </ul>
      <br/>
      <form action="Language" method="post" enctype="multipart/form-data">
        <input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <table class="form" width="372px">
          <tr>
            <td>Id</td>
            <td width="100%">
              <c:choose>
                <c:when test="${action != 'create'}">
                  <input size="5" class=":required :only_on_blur" name="lg_id" value="${lg.id}" readonly="readonly"/>
                </c:when>
                <c:otherwise>
                  <input class=":required :only_on_blur" name="lg_id" value=""/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td>Name</td>
            <td><input class=":required :only_on_blur" name="lg_name" value="${lg.name}"/></td>
          </tr>
          <tr>
            <td>Flag</td>
            <td>
              <c:choose>
                <c:when test="${action == 'create'}">
                  <input class=":required :only_on_blur" type="file" name="image"/>
                </c:when>
                <c:otherwise>
                  <c:url value="Language" var="urlFlag">
                    <c:param name="action" value="flag"/>
                    <c:param name="lg_id" value="${lg.id}"/>
                  </c:url>
                  <table cellpadding="0" cellspacing="0"><tr><td><img src="${urlFlag}"/>&nbsp;</td><td><input type="file" name="image"/></td></tr></table>
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