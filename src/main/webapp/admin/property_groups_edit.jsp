<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="Shortcut icon" href="favicon.ico" />
<link rel="stylesheet" type="text/css" href="js/codemirror/lib/codemirror.css" />
<link rel="stylesheet" type="text/css" href="css/admin-style.css" />
<style type="text/css">
.CodeMirror {
	width: 700px;
	height: 300px;
	background-color: #f8f6c2;
}
</style>
<script type="text/javascript" src="js/codemirror/lib/codemirror.js"></script>
<script type="text/javascript" src="js/codemirror/mode/xml/xml.js"></script>
<script type="text/javascript" src="js/codemirror/addon/selection/active-line.js"></script>
<script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
<script type="text/javascript">
  $(document).ready(function() {
    var cm = CodeMirror.fromTextArea(document.getElementById('definition'), {
      lineNumbers : true,
      matchBrackets : true,
      styleActiveLine : true,
      mode : "application/xml",
      indentUnit : 4
    });

    var width = $(window).width() - 60;
    var height = $(window).height() - 130;
    cm.setSize(width, height);
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