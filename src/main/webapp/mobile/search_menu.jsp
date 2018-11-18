<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page errorPage="error.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8" />
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
  <meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0"/>
  <title>OpenKM Mobile</title>
  <link rel="apple-touch-icon" href="img/condor.jpg" />
  <link rel="stylesheet" href="../css/jquery.mobile-1.2.1.min.css" />
  <!-- jQuery mobile requires jquery min 1.8.3 for running, it does not work with upper version -->
  <script src="../js/jquery-1.8.3.min.js"></script>
  <script src="../js/jquery.mobile-1.2.1.min.js"></script>
</head>
<body>
  <u:constantsMap className="com.openkm.dao.bean.Translation" var="Translation"/>
  <u:constantsMap className="com.openkm.core.Config" var="Config"/>
  <c:url value="Desktop" var="urlGoto">
    <c:param name="action" value="browse"/>
  	<c:param name="docPath" value="${doc.parent}"/>
  </c:url>
  <div data-role="page" data-theme="${Config.MOBILE_THEME}">
    <div data-role="header" data-position="inline" data-theme="${Config.MOBILE_THEME}"> 
	  <h1>${doc.name}</h1> 
    </div>
    <div data-role="content" data-theme="${Config.MOBILE_THEME}">
      <div data-role="controlgroup">
        <a href="${urlGoto}" data-role="button" data-theme="${Config.MOBILE_THEME}"><u:message key="search.goto" module="${Translation.MODULE_MOBILE}"/></a>
        <a href="#" data-role="button" data-rel="back" data-theme="${Config.MOBILE_THEME}"><u:message key="menu.back" module="${Translation.MODULE_MOBILE}"/></a>
      </div>
    </div> 
  </div>
</body>
</html>