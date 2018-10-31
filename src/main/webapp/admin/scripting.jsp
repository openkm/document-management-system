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
  <link rel="stylesheet" type="text/css" href="css/admin-style.css" />
  <link rel="stylesheet" type="text/css" href="js/codemirror/lib/codemirror.css" />
  <style type="text/css">
    .CodeMirror { width: 700px; height: 300px; background-color: #f8f6c2; }
  </style>
  <script type="text/javascript" src="js/codemirror/lib/codemirror.js"></script>
  <script type="text/javascript" src="js/codemirror/mode/clike/clike.js"></script>
  <script type="text/javascript" src="js/codemirror/addon/selection/active-line.js"></script>
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="js/jquery.DOMWindow.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
      var cm = CodeMirror.fromTextArea(document.getElementById('script'), {
        lineNumbers: true,
        matchBrackets: true,
        styleActiveLine: true,
        mode: "text/x-java",
        indentUnit: 4
      });

      var width = $(window).width() - 60;
      var height = $(window).height() - 300;
      cm.setSize(width, height);

      dm = $('.ds').openDOMWindow({
        height:300, width:400,
        eventType:'click',
        overlayOpacity: '57',
        windowSource:'iframe', windowPadding:0
      });
    });

    function dialogClose() {
		  dm.closeDOMWindow();
    }
    
    function keepSessionAlive() {
    	$.ajax({ type:'GET', url:'../SessionKeepAlive', cache:false, async:false });
    }
    
	window.setInterval('keepSessionAlive()', <%=java.util.concurrent.TimeUnit.MINUTES.toMillis(com.openkm.core.Config.KEEP_SESSION_ALIVE_INTERVAL)%>);
  </script>
  <title>Scripting</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path"><a href="utilities.jsp">Utilities</a></li>
        <li class="path">Scripting</li>
      </ul>
      <br />
      <form action="Scripting" method="post">
        <input type="hidden" name="csrft" value="${csrft}" />
        <table class="form" align="center">
          <tr>
            <td colspan="4">
              <textarea cols="80" rows="25" name="script" id="script">${script}</textarea>
            </td>
          </tr>
          <tr>
            <td align="left" width="125px">
              <input type="text" size="50" name="fsPath" id="fsPath" value="${fsPath}" />
            </td>
            <td align="left" width="25px">
              <a class="ds" href="../extension/DataBrowser?action=fs&dst=fsPath"> 
              <img src="img/action/browse_fs.png" />
              </a>
            </td>
            <td>
              <input type="submit" name="action" value="Load" class="loadButton" /> 
              <input type="submit" name="action" value="Save" class="saveButton" />
            </td>
            <td align="right">
              <input type="submit" name="action" value="Evaluate" class="executeButton" />
            </td>
          </tr>
        </table>
        <br />
        <div class="ok" style="text-align: center">
          <c:if test="${!empty time}">Time: ${time}</c:if>
        </div>
        <br />
        <table class="results-old" width="95%">
          <tr><th>Script error</th></tr>
          <tr class="even"><td>${scriptError}</td></tr>
          <tr><th>Script result</th></tr>
          <tr class="even"><td>${scriptResult}</td></tr>
          <tr><th>Script output</th></tr>
          <tr class="even"><td>${scriptOutput}</td></tr>
        </table>
      </form>
    </c:when>
    <c:otherwise>
      <div class="error">
        <h3>Only admin users allowed</h3>
      </div>
    </c:otherwise>
  </c:choose>
</body>
</html>
