<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.core.HttpSessionManager" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" href="css/style.css" type="text/css" />
  <link rel="stylesheet" href="../css/menu.css" type="text/css" media="screen"/>
  <script src="../js/jquery-1.11.3.min.js" type="text/javascript"></script>
  <style type="text/css">
    html, body, div, iframe { margin: 0; padding: 0; width: 100%; }
    iframe { width: 100%; border: none; visibility: hidden; }
    div#menu { border-bottom: 1px solid #A5A596; background-color: #E5E5E1;}
  </style>
  <title>OpenKM Administration</title>
  <script type="text/javascript">
    $(document).ready(function() {
    	$('div#menu li').bind('click', function() {
    		$(this).addClass('selected');
    		var clicked = $(this);

    		// When another option is selected, remove the 'selected' class from others
    		$('div#menu li').each(function () {
    			if ($(this).children(":first").attr('href') != clicked.children(":first").attr('href')) {
    				$(this).removeClass('selected');
        		}
        	});
        });

        $('div#menu li').bind('mouseenter mouseleave', function() {
            $(this).toggleClass('over');
        });
    });
    
    if (window.attachEvent) {
      // Explorer
      trick = '4';
      window.attachEvent("onload", function() { setTimeout(loaded, 100); }, true);
    } else {
      trick = '0';
      window.addEventListener("load", function() { setTimeout(loaded, 100); }, true);
    }
    
    window.onresize = function() {
        loaded();
    };
    
    function loaded() {
      var frame = document.getElementById('frame');
      var menu = document.getElementById('menu');
      //var height = Math.max(
      //        Math.max(document.body.scrollHeight, document.documentElement.scrollHeight),
      //        Math.max(document.body.offsetHeight, document.documentElement.offsetHeight),
      //        Math.max(document.body.clientHeight, document.documentElement.clientHeight)
      //    );
      var height = document.documentElement.clientHeight;
      frame.style.height = (height-menu.offsetHeight-trick) + 'px';
      frame.style.visibility = 'visible';
    }
  </script>
</head>
<body>
<%
	HttpSessionManager.getInstance().add(request);
	com.openkm.api.OKMAuth.getInstance().login();
%>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
	  <div id="menu"><%@include file="menu.jsp" %></div>
	  <div><iframe id="frame" name="frame" src="home.jsp" frameborder="0"></iframe></div>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>