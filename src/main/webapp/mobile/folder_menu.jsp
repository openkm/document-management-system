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
  <u:constantsMap className="com.openkm.bean.Folder" var="Folder"/>
  <u:constantsMap className="com.openkm.core.Config" var="Config"/>
  <c:url value="Desktop" var="urlProperties">
    <c:param name="action" value="properties"/>
    <c:param name="uuid" value="${fld.uuid}"/>
    <c:param name="nodeType" value="${Folder.TYPE}"/>
  </c:url>
  <c:url value="Desktop" var="urlConfirmDelete">
    <c:param name="action" value="confirmDelete"/>
    <c:param name="uuid" value="${fld.uuid}"/>
    <c:param name="parentUuid" value="${parentUuid}"/>
    <c:param name="nodeType" value="${Folder.TYPE}"/>
  </c:url>
  <div data-role="page" data-theme="${Config.MOBILE_THEME}">
    <div data-role="header" data-position="inline" data-theme="${Config.MOBILE_THEME}"> 
      <a href="#" data-iconpos="notext" data-icon="back" data-rel="back" class="ui-btn-left"></a>
	  <h1><u:getName path="${fld.path}"/></h1>
    </div> 
    <div data-role="content" data-theme="${Config.MOBILE_THEME}">
      <div data-role="controlgroup">
        <a href="${urlProperties}" data-role="button" data-ajax="false" data-theme="${Config.MOBILE_THEME}"><u:message key="menu.properties" module="${Translation.MODULE_MOBILE}"/></a>
        <c:if test="${u:bitwiseAnd(fld.permissions, Permission.DELETE) == Permission.DELETE}">
	  	  <a href="${urlConfirmDelete}" data-role="button" data-rel="dialog" data-transition="pop" data-theme="${Config.MOBILE_THEME}"><u:message key="menu.delete" module="${Translation.MODULE_MOBILE}"/></a>
	  	</c:if>
      </div>
    </div>
  </div>
</body>
</html>