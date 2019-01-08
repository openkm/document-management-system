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
  <u:constantsMap className="com.openkm.bean.Folder" var="Folder"/>
  <u:constantsMap className="com.openkm.core.Config" var="Config"/>
  <c:url value="Desktop" var="urlDelete">
    <c:param name="action" value="delete"/>
  	<c:param name="uuid" value="${fld.uuid}"/>
    <c:param name="parentUuid" value="${parentUuid}"/>
    <c:param name="nodeType" value="${Folder.TYPE}"/>
  </c:url>
  <c:url value="Desktop" var="urlBrowse">
    <c:param name="action" value="browse"/>
  	<c:param name="uuid" value="${parentUuid}"/>
  </c:url>
  <div data-role="page" data-theme="${Config.MOBILE_THEME}">
  	<div data-role="header" data-theme="${Config.MOBILE_THEME}"> 
      <c:choose>
        <c:when test="${action == 'confirmDelete'}">
          <h1><u:message key="menu.delete" module="${Translation.MODULE_MOBILE}"/></h1>
        </c:when>
      </c:choose>
    </div>
    <div data-role="content" data-theme="${Config.MOBILE_THEME}">
      <u:getName path="${fld.path}"/><br/>
      <c:choose>
        <c:when test="${action == 'confirmDelete'}">
          <h2><u:message key="confirm.delete.folder" module="${Translation.MODULE_MOBILE}"/></h2>
        </c:when>
      </c:choose>
      <fieldset class="ui-grid-a">
        <div class="ui-block-a"><a href="${urlBrowse}" data-role="button" data-theme="a"><u:message key="menu.no" module="${Translation.MODULE_MOBILE}"/></a></div>
        <div class="ui-block-b"><a href="${urlDelete}" data-role="button" data-ajax="false" data-theme="${Config.MOBILE_THEME}"><u:message key="menu.yes" module="${Translation.MODULE_MOBILE}"/></a></div>
      </fieldset>
	</div>
  </div>
</body>
</html>