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
  <script type="text/javascript">
    $(function() {
      $('li').bind('taphold', function(e) {
     	e.preventDefault();
        var id = $(this).attr('id');
        var action = $(this).attr('data-action');
        //alert('You ' + e.type + " - '" + id + "'" + " => " + action);
        $.mobile.changePage("Desktop?action="+action+"&uuid="+id, null, true, true);        
        e.stopImmediatePropagation();
        return false;
      });

      // Disable text selection
      body.style.webkitUserSelect = "none";
      body.style.MozUserSelect = "none";
    });
  </script>
</head>
<body>
  <u:constantsMap className="com.openkm.dao.bean.Translation" var="Translation"/>
  <u:constantsMap className="com.openkm.bean.Document" var="Document"/>
  <u:constantsMap className="com.openkm.core.Config" var="Config"/>
  <div data-role="page" data-theme="${Config.MOBILE_THEME}">
    <div data-role="header" data-position="inline" data-theme="${Config.MOBILE_THEME}"> 
      <!-- <a href="home.jsp" data-iconpos="notext" data-icon="home" class="ui-btn-left"></a> -->
      <a href="#" data-iconpos="notext" data-icon="back" data-rel="back" class="ui-btn-left"></a>
      <c:choose>
        <c:when test="${action == 'lastModified'}">
      	  <h1><u:message key="menu.dashboard.last.modified.documents" module="${Translation.MODULE_MOBILE}"/></h1>
        </c:when>
        <c:when test="${action == 'lastUploaded'}">
      	  <h1><u:message key="menu.dashboard.last.uploaded.documents" module="${Translation.MODULE_MOBILE}"/></h1>
        </c:when>
        <c:when test="${action == 'checkout'}">
      	  <h1><u:message key="menu.dashboard.checkout.documents" module="${Translation.MODULE_MOBILE}"/></h1>
        </c:when>
        <c:when test="${action == 'downloaded'}">
      	  <h1><u:message key="menu.dashboard.download" module="${Translation.MODULE_MOBILE}"/></h1>
        </c:when>
        <c:when test="${action == 'locked'}">
      	  <h1><u:message key="menu.dashboard.locked" module="${Translation.MODULE_MOBILE}"/></h1>
        </c:when>
        <c:when test="${action == 'generalLastWeekViewed'}">
      	  <h1><u:message key="dashboard.general.last.week.top.viewer" module="${Translation.MODULE_MOBILE}"/></h1>
        </c:when>
        <c:when test="${action == 'generalLastMonthViewed'}">
      	  <h1><u:message key="dashboard.general.last.month.top.viewer" module="${Translation.MODULE_MOBILE}"/></h1>
        </c:when>
        <c:when test="${action == 'generalLastWeekModified'}">
      	  <h1><u:message key="dashboard.general.last.week.top.modified" module="${Translation.MODULE_MOBILE}"/></h1>
        </c:when>
        <c:when test="${action == 'generalLastMonthModified'}">
      	  <h1><u:message key="dashboard.general.last.month.top.modified" module="${Translation.MODULE_MOBILE}"/></h1>
        </c:when>
        <c:when test="${action == 'generalLastUploaded'}">
      	  <h1><u:message key="dashboard.general.last.uploaded" module="${Translation.MODULE_MOBILE}"/></h1>
        </c:when>
        <c:when test="${action == 'generalLastModified'}">
      	  <h1><u:message key="dashboard.general.last.modified" module="${Translation.MODULE_MOBILE}"/></h1>
        </c:when>
      </c:choose>
    </div>
    <div data-role="content">
      <ul data-role="listview" data-theme="c">
        <!-- List documents -->
        <c:forEach var="dash" items="${dashboardDocs}">
          <li id="${dash.document.path}" data-action="docMenu">
            <c:url value="/frontend/Download" var="urlDownload">
              <c:if test="${dash.document.convertibleToPdf}">
                <c:param name="toPdf"/>
              </c:if>
              <c:param name="id" value="${dash.document.uuid}"/>
            </c:url>
            <c:url value="Desktop" var="urlProperties">
              <c:param name="action" value="propertiesFromDashBoard"/>
              <c:param name="uuid" value="${dash.document.uuid}"/>
              <c:param name="nodeType" value="${Document.TYPE}"/>
            </c:url>
            <c:url value="/mime/${dash.document.mimeType}" var="urlIcon"></c:url>
            <c:set var="size"><u:formatSize size="${dash.document.actualVersion.size}"/></c:set>
            <a href="${urlDownload}" data-ajax="false"><img src="${urlIcon}" class="ui-li-icon"/>${dash.document.name}</a>
            <span class="ui-li-count">${size}</span>
            <a href="${urlProperties}"></a>
          </li>
        </c:forEach>
      </ul>
    </div>
  </div>
</body>
</html>