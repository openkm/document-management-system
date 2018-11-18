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
  <c:url value="Dashboard" var="urlLastModified">
    <c:param name="action" value="lastModified"/>
  </c:url>
  <c:url value="Dashboard" var="urlLastUploaded">
    <c:param name="action" value="lastUploaded"/>
  </c:url>
  <c:url value="Dashboard" var="urlCheckout">
    <c:param name="action" value="checkout"/>
  </c:url>
  <c:url value="Dashboard" var="urlDownloaded">
    <c:param name="action" value="downloaded"/>
  </c:url>
  <c:url value="Dashboard" var="urlLocked">
    <c:param name="action" value="locked"/>
  </c:url>
  <c:url value="Dashboard" var="urlGeneralLastWeekViewed">
    <c:param name="action" value="generalLastWeekViewed"/>
  </c:url>
  <c:url value="Dashboard" var="urlGeneralLastMonthViewed">
    <c:param name="action" value="generalLastMonthViewed"/>
  </c:url>
  <c:url value="Dashboard" var="urlGeneralLastWeekModified">
    <c:param name="action" value="generalLastWeekModified"/>
  </c:url>
  <c:url value="Dashboard" var="urlGeneralLastMonthModified">
    <c:param name="action" value="generalLastMonthModified"/>
  </c:url>
  <c:url value="Dashboard" var="urlGeneralLastUploaded">
    <c:param name="action" value="generalLastUploaded"/>
  </c:url>
  <c:url value="Dashboard" var="urlGeneralLastModified">
    <c:param name="action" value="generalLastModified"/>
  </c:url>
  <u:constantsMap className="com.openkm.dao.bean.Translation" var="Translation"/>
  <u:constantsMap className="com.openkm.core.Config" var="Config"/>
  <div data-role="page" data-theme="${Config.MOBILE_THEME}">
  	<div data-role="header" data-position="inline" data-theme="${Config.MOBILE_THEME}"> 
      <a href="home.jsp" data-iconpos="notext" data-icon="home" class="ui-btn-left"></a>
	  <h1><u:message key="menu.dashboard" module="${Translation.MODULE_MOBILE}"/></h1> 
    </div> 
    <div data-role="content" data-theme="${Config.MOBILE_THEME}">
      <div align="center"><h2><u:message key="dashboard.user" module="${Translation.MODULE_MOBILE}"/></h2></div>
      <div data-role="controlgroup">
	    <a href="${urlLastModified}" data-role="button" data-theme="${Config.MOBILE_THEME}"><u:message key="menu.dashboard.last.modified.documents" module="${Translation.MODULE_MOBILE}"/></a>
	    <a href="${urlLastUploaded}" data-role="button" data-theme="${Config.MOBILE_THEME}"><u:message key="menu.dashboard.last.uploaded.documents" module="${Translation.MODULE_MOBILE}"/></a>
	    <a href="${urlCheckout}" data-role="button" data-theme="${Config.MOBILE_THEME}"><u:message key="menu.dashboard.checkout.documents" module="${Translation.MODULE_MOBILE}"/></a>
	    <a href="${urlDownloaded}" data-role="button" data-theme="${Config.MOBILE_THEME}"><u:message key="menu.dashboard.download" module="${Translation.MODULE_MOBILE}"/></a>
	    <a href="${urlLocked}" data-role="button" data-theme="${Config.MOBILE_THEME}"><u:message key="menu.dashboard.locked" module="${Translation.MODULE_MOBILE}"/></a>
      </div>
      <div align="center"><h2><u:message key="dashboard.general" module="${Translation.MODULE_MOBILE}"/></h2></div>
      <div data-role="controlgroup">
	    <a href="${urlGeneralLastWeekViewed}" data-role="button" data-theme="${Config.MOBILE_THEME}"><u:message key="dashboard.general.last.week.top.viewer" module="${Translation.MODULE_MOBILE}"/></a>
	    <a href="${urlGeneralLastMonthViewed}" data-role="button" data-theme="${Config.MOBILE_THEME}"><u:message key="dashboard.general.last.month.top.viewer" module="${Translation.MODULE_MOBILE}"/></a>
	    <a href="${urlGeneralLastWeekModified}" data-role="button" data-theme="${Config.MOBILE_THEME}"><u:message key="dashboard.general.last.week.top.modified" module="${Translation.MODULE_MOBILE}"/></a>
	    <a href="${urlGeneralLastMonthModified}" data-role="button" data-theme="${Config.MOBILE_THEME}"><u:message key="dashboard.general.last.month.top.modified" module="${Translation.MODULE_MOBILE}"/></a>
	    <a href="${urlGeneralLastUploaded}" data-role="button" data-theme="${Config.MOBILE_THEME}"><u:message key="dashboard.general.last.uploaded" module="${Translation.MODULE_MOBILE}"/></a>
	    <a href="${urlGeneralLastModified}" data-role="button" data-theme="${Config.MOBILE_THEME}"><u:message key="dashboard.general.last.modified" module="${Translation.MODULE_MOBILE}"/></a>
      </div>
    </div>
  </div>
</body>
</html>