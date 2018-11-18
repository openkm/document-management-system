<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page errorPage="error.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
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
  <u:constantsMap className="com.openkm.bean.Repository" var="Repository"/>
  <u:constantsMap className="com.openkm.core.Config" var="Config"/>
  <c:url value="Desktop" var="changeTaxonomy">
    <c:param name="action" value="browse"/>
  	<c:param name="path" value="/${Repository.ROOT}"/>
  </c:url>
  <c:url value="Desktop" var="changeCategories">
    <c:param name="action" value="browse"/>
  	<c:param name="path" value="/${Repository.CATEGORIES}"/>
  </c:url>
  <c:url value="Desktop" var="changeTemplates">
    <c:param name="action" value="browse"/>
  	<c:param name="path" value="/${Repository.TEMPLATES}"/>
  </c:url>
  <c:url value="Desktop" var="changePersonal">
    <c:param name="action" value="browse"/>
  	<c:param name="path" value="/${Repository.PERSONAL}/${pageContext.request.remoteUser}"/>
  </c:url>
  <div data-role="page" data-theme="${Config.MOBILE_THEME}">
    <div data-role="header" data-position="inline" data-theme="${Config.MOBILE_THEME}"> 
	  <h1><u:message key="menu.change.context" module="${Translation.MODULE_MOBILE}"/></h1> 
    </div>
    <div data-role="content" data-theme="${Config.MOBILE_THEME}">
      <div data-role="controlgroup">
        <a href="${changeTaxonomy}" data-role="button" data-theme="${Config.MOBILE_THEME}"><u:message key="context.title.taxonomy" module="${Translation.MODULE_MOBILE}"/></a>
        <a href="${changeCategories}" data-role="button" data-theme="${Config.MOBILE_THEME}"><u:message key="context.title.categories" module="${Translation.MODULE_MOBILE}"/></a>
        <a href="${changeTemplates}" data-role="button" data-theme="${Config.MOBILE_THEME}"><u:message key="context.title.templates" module="${Translation.MODULE_MOBILE}"/></a>
        <a href="${changePersonal}" data-role="button" data-theme="${Config.MOBILE_THEME}"><u:message key="context.title.personal" module="${Translation.MODULE_MOBILE}"/></a>
        <!-- <a href="#" data-role="button" data-rel="back" data-theme="${Config.MOBILE_THEME}"><u:message key="menu.back" module="${Translation.MODULE_MOBILE}"/></a> -->
      </div>
    </div> 
  </div>
</body>
</html>