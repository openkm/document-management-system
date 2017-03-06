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
  <script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
  <script type="text/javascript" src="js/jquery.DOMWindow.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
		$dm = $('.ds').openDOMWindow({
			height:300, width:400,
			eventType:'click',
			overlayOpacity:'57',
			windowSource:'iframe', windowPadding:0
		});
	});
    
    function dialogClose() {
		$dm.closeDOMWindow();
    }
  </script>
  <title>Check Text Extraction</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="utilities.jsp">Utilities</a>
        </li>
        <li class="path">Check text extraction</li>
      </ul>
      <br/>
      <form action="CheckTextExtraction" method="post" enctype="multipart/form-data">
        <table class="form" align="center">
          <tr>
            <td>Document UUID</td>
            <td colspan="2"><input name="docUuid" size="40" value="${docUuid}"/></td>
          </tr>
          <tr>
            <td>Document path</td>
            <td><input name="repoPath" id="repoPath" size="50" value="${repoPath}"/></td>
            <td><a class="ds" href="../extension/DataBrowser?action=repo&sel=doc&dst=repoPath&path=<u:getParent path="${repoPath}"/>"><img src="img/action/browse_repo.png"/></a></td>
          </tr>
          <tr>
            <td>Upload document</td>
            <td colspan="2"><input name="file" type="file" size="40"/></td>
          </tr>
          <tr>
            <td colspan="3" align="right">
              <input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton"/>
              <input type="submit" value="Check" class="yesButton"/>
            </td>
          </tr>
        </table>
      </form>
      <br/>
      <c:if test="${not empty mimeType}">
        <div class="ok">
          <center>
            Time: <u:formatMiliSeconds time="${time}"/>
          </center>
        </div>
        <br/>
        <table class="results" width="80%">
          <tr><th width="50%">${mimeType}</th><th width="50%">${extractor}</th></tr>
          <c:choose>
            <c:when test="${empty error}">
              <tr class="even"><td colspan="2">${text}</td></tr>
            </c:when>
            <c:otherwise>
              <tr class="warn"><td colspan="2">${error}</td></tr>  
            </c:otherwise>
          </c:choose>
        </table>
      </c:if>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>