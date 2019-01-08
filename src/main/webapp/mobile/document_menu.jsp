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
  <u:constantsMap className="com.openkm.bean.Permission" var="Permission"/>
  <u:constantsMap className="com.openkm.bean.Document" var="Document"/>
  <u:constantsMap className="com.openkm.core.Config" var="Config"/>
  <c:url value="/frontend/Download" var="urlDownload">
    <c:param name="uuid" value="${doc.uuid}"/>
  </c:url>
  <c:url value="/frontend/Converter" var="urlDownloadPdf">
    <c:param name="toPdf" value="true"/>
    <c:param name="uuid" value="${doc.uuid}"/>
  </c:url>
  <c:url value="Desktop" var="urlProperties">
    <c:param name="action" value="properties"/>
    <c:param name="uuid" value="${doc.uuid}"/>
    <c:param name="nodeType" value="${Document.TYPE}"/>
  </c:url>
  <c:url value="Desktop" var="urlConfirmDelete">
    <c:param name="action" value="confirmDelete"/>
    <c:param name="uuid" value="${doc.uuid}"/>
    <c:param name="parentUuid" value="${parentUuid}"/>
    <c:param name="nodeType" value="${Document.TYPE}"/>
  </c:url>
  <c:url value="Desktop" var="urlLock">
    <c:param name="action" value="lock"/>
    <c:param name="uuid" value="${doc.uuid}"/>
    <c:param name="parentUuid" value="${parentUuid}"/>
  </c:url>
  <c:url value="Desktop" var="urlUnlock">
    <c:param name="action" value="unlock"/>
    <c:param name="uuid" value="${doc.uuid}"/>
    <c:param name="parentUuid" value="${parentUuid}"/>
  </c:url>
  <div data-role="page" data-theme="${Config.MOBILE_THEME}">
    <div data-role="header" data-position="inline" data-theme="${Config.MOBILE_THEME}"> 
      <a href="#" data-iconpos="notext" data-icon="back" data-rel="back" class="ui-btn-left"></a>
	  <h1><u:getName path="${doc.path}"/></h1> 
    </div> 
    <div data-role="content" data-theme="${Config.MOBILE_THEME}">
      <div data-role="controlgroup">
	  	<a href="${urlDownload}" data-role="button" data-ajax="false" data-theme="${Config.MOBILE_THEME}"><u:message key="menu.download" module="${Translation.MODULE_MOBILE}"/></a>
	  	<c:if test="${doc.convertibleToPdf}">
	  	  <a href="${urlDownloadPdf}" data-role="button" data-ajax="false" data-theme="${Config.MOBILE_THEME}"><u:message key="menu.download.pdf" module="${Translation.MODULE_MOBILE}"/></a>
	  	</c:if>
	  	<a href="${urlProperties}" data-role="button" data-ajax="false" data-theme="${Config.MOBILE_THEME}"><u:message key="menu.properties" module="${Translation.MODULE_MOBILE}"/></a>
	  	<c:if test="${not doc.locked && u:bitwiseAnd(doc.permissions, Permission.DELETE) == Permission.DELETE}">
	  	  <a href="${urlConfirmDelete}" data-role="button" data-rel="dialog" data-transition="pop" data-theme="${Config.MOBILE_THEME}"><u:message key="menu.delete" module="${Translation.MODULE_MOBILE}"/></a>
	  	</c:if>
	  	<c:choose>
	  	  <c:when test="${doc.locked && doc.lockInfo.owner == pageContext.request.remoteUser}">
	  	    <a href="${urlUnlock}" data-role="button" data-ajax="false" data-theme="${Config.MOBILE_THEME}"><u:message key="menu.document.unlock" module="${Translation.MODULE_MOBILE}"/></a>
	  	  </c:when>
	  	  <c:otherwise>
	  	    <c:if test="${not doc.locked && u:bitwiseAnd(doc.permissions, Permission.WRITE) == Permission.WRITE}">
	  	      <a href="${urlLock}" data-role="button" data-ajax="false" data-theme="${Config.MOBILE_THEME}"><u:message key="menu.document.lock" module="${Translation.MODULE_MOBILE}"/></a>
	  	    </c:if>
	  	  </c:otherwise>
	  	</c:choose>
      </div>
    </div>
  </div>
</body>
</html>