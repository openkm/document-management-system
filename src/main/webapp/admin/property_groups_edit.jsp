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
  <link rel="stylesheet" type="text/css" href="js/codemirror/lib/codemirror.css" />
  <link rel="stylesheet" type="text/css" href="js/codemirror/mode/xml/xml.css" />
  <style type="text/css">
    .CodeMirror { width: 600px; height: 300px; background-color: #f8f6c2; }
    .activeline { background: #f0fcff !important; }
  </style>
  <script type="text/javascript" src="js/codemirror/lib/codemirror.js"></script>
  <script type="text/javascript" src="js/codemirror/mode/xml/xml.js"></script>
  <script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
  <script type="text/javascript">
	$(document).ready(function() {
		cm = CodeMirror.fromTextArea(document.getElementById('definition'), {
			lineNumbers: true,
			matchBrackets: true,
			indentUnit: 4,
			mode: "application/xml",
			onCursorActivity: function() {
				cm.setLineClass(hlLine, null);
				hlLine = cm.setLineClass(cm.getCursor().line, "activeline");
			}
		});
      	
		hlLine = cm.setLineClass(0, "activeline");
		var width = $(window).width() - 60;
	    var height = $(window).height() - 130;
	    $('.CodeMirror').css({"width": width});
	    $('.CodeMirror').css({"height": height});
    });
  </script>
  <title>Metadata Group Edit</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="PropertyGroups">Metadata groups</a>
        </li>
        <li class="path">Edit</li>
      </ul>
      <br/>
      <form action="PropertyGroups" method="post">
        <input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <table class="form" align="center">
          <tr>
            <td>
              <textarea cols="80" rows="25" name="definition" id="definition">${definition}</textarea>
            </td>
          </tr>
          <tr>
            <td align="right">
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