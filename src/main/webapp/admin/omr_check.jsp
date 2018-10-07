<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <script src="../js/jquery-1.8.3.min.js" type="text/javascript"></script>
  <script src="../js/vanadium-min.js" type="text/javascript"></script>
  <title>Omr Template</title>
</head>
<body> 
  <c:choose>
      <c:when test="${u:isAdmin()}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Omr">OMR template</a>
        </li>
        <li class="path">
          <c:choose>
            <c:when test="${action == 'check'}">Process Template Check</c:when>
          </c:choose>
        </li>
      </ul>
      <br/>
  	<form action="Omr" method="post" enctype="multipart/form-data">
  		<input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="om_id" value="${om.id}"/>
        <table class="form" width="425px">     
          <tr>
		  	<td valign="top">Upload Form</td>
		  	<td valign="top">
		  		<input class=":required :only_on_blur" type="file" name="file"/>
		  	</td>
		  </tr>
          <tr>
            <td colspan="2" align="right">
              <div id="buttons">
              	<input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton"/>
              	<input type="submit" value="Check" class="yesButton"/>
              </div>
            </td>
          </tr>
          <c:if test="${results != null}">
	        <tr>
	          <td colspan="2" align="center"><b>Results</b></td>
	        </tr>
          	<tr>
          	  <td colspan="2">
          	  <table class="results-old" border="0" cellspacing="4" width="100%">
          	  	<tr>
          	  		<th>Key</th>
          	  		<th>Value</th>
          	  	</tr>
          	  	<c:forEach var="result" items="${results}" varStatus="row">
          	  	  <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
          	  	  	<td>${result.key} </td>
          	  	  	<td>${result.value} </td>
          	  	  </tr>
	  			      </c:forEach>
          	  </table>
          	  </td>
          	</tr>
      	  </c:if>
        </table>
      </form>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>