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
  <u:constantsMap className="com.openkm.core.Config" var="Config"/>
  <u:constantsMap className="com.openkm.frontend.client.bean.GWTPermission" var="GWTPermission"/>
  <div data-role="page" data-theme="${Config.MOBILE_THEME}">
    <div data-role="header" data-position="inline" data-theme="${Config.MOBILE_THEME}">
      <a href="home.jsp" data-iconpos="notext" data-icon="home" class="ui-btn-left"></a>
      <!-- <a href="#" data-iconpos="notext" data-icon="back" data-rel="back" class="ui-btn-left"></a> -->
	  <h1><u:message key="${context}" module="${Translation.MODULE_MOBILE}"/></h1>
	  <a href="context_menu.jsp" data-icon="gear" data-rel="dialog" class="ui-btn-right">
	    <u:message key="menu.title.context" module="${Translation.MODULE_MOBILE}"/>
	  </a>
    </div>
    <div data-role="content">
      <ul data-role="listview" data-theme="c">
        <!-- List categories -->
        <c:forEach var="cat" items="${catChildren}">
          <li id="${cat.uuid}" data-action="fldMenu">
            <c:url value="Desktop" var="urlBrowse">
              <c:param name="action" value="browse"/>
              <c:param name="uuid" value="${cat.uuid}"/>
            </c:url>
            <c:url value="Desktop" var="urlMenu">
              <c:param name="action" value="fldMenu"/>
              <c:param name="uuid" value="${cat.uuid}"/>
              <c:param name="parentUuid" value="${parentUuid}"/>
            </c:url>
            <c:choose>
              <c:when test="${u:bitwiseAnd(cat.permissions, GWTPermission.WRITE) == GWTPermission.WRITE}">
                <c:choose>
              	  <c:when test="${cat.hasChildren}"><c:set var="fldImg" value="menuitem_childs.gif"/></c:when>
              	  <c:otherwise><c:set var="fldImg" value="menuitem_empty.gif"/></c:otherwise>
            	</c:choose>
              </c:when>
              <c:otherwise>
                <c:choose>
              	  <c:when test="${cat.hasChildren}"><c:set var="fldImg" value="menuitem_childs_ro.gif"/></c:when>
              	  <c:otherwise><c:set var="fldImg" value="menuitem_empty_ro.gif"/></c:otherwise>
            	</c:choose>
              </c:otherwise>
            </c:choose>
            <a href="${urlBrowse}"><img src="../frontend/img/${fldImg}" class="ui-li-icon"/><u:getName path="${cat.path}"/></a>
            <a href="${urlMenu}"></a>
          </li>
        </c:forEach>
        
        <!-- List folders -->
        <c:forEach var="fld" items="${fldChildren}">
          <li id="${fld.uuid}" data-action="fldMenu">
            <c:url value="Desktop" var="urlBrowse">
              <c:param name="action" value="browse"/>
              <c:param name="uuid" value="${fld.uuid}"/>
            </c:url>
            <c:url value="Desktop" var="urlMenu">
              <c:param name="action" value="fldMenu"/>
              <c:param name="uuid" value="${fld.uuid}"/>
              <c:param name="parentUuid" value="${parentUuid}"/>
            </c:url>
            <c:choose>
              <c:when test="${u:bitwiseAnd(fld.permissions, GWTPermission.WRITE) == GWTPermission.WRITE}">
                <c:choose>
              	  <c:when test="${fld.hasChildren}"><c:set var="fldImg" value="menuitem_childs.gif"/></c:when>
              	  <c:otherwise><c:set var="fldImg" value="menuitem_empty.gif"/></c:otherwise>
            	</c:choose>
              </c:when>
              <c:otherwise>
                <c:choose>
              	  <c:when test="${fld.hasChildren}"><c:set var="fldImg" value="menuitem_childs_ro.gif"/></c:when>
              	  <c:otherwise><c:set var="fldImg" value="menuitem_empty_ro.gif"/></c:otherwise>
            	</c:choose>
              </c:otherwise>
            </c:choose>
            <c:choose>
              <c:when test="${context == 'context.title.categories'}">
                <a href="#"><img src="../frontend/img/${fldImg}" class="ui-li-icon"/><u:getName path="${fld.path}"/></a>
              </c:when>
              <c:otherwise>
                <a href="${urlBrowse}"><img src="../frontend/img/${fldImg}" class="ui-li-icon"/><u:getName path="${fld.path}"/></a>
              </c:otherwise>
            </c:choose>
            <a href="${urlMenu}"></a>
          </li>
        </c:forEach>
        
        <!-- List documents -->
        <c:forEach var="doc" items="${docChildren}">
          <li id="${doc.uuid}" data-action="docMenu">
            <c:url value="/frontend/Download" var="urlDownload">
              <c:if test="${doc.convertibleToPdf}">
                <c:param name="toPdf"/>
              </c:if>
              <c:param name="uuid" value="${doc.uuid}"/>
            </c:url>
            <c:url value="Desktop" var="urlMenu">
              <c:param name="action" value="docMenu"/>
              <c:param name="uuid" value="${doc.uuid}"/>
              <c:param name="parentUuid" value="${parentUuid}"/>
            </c:url>
            <c:url value="/mime/${doc.mimeType}" var="urlIcon"></c:url>
            <c:set var="size"><u:formatSize size="${doc.actualVersion.size}"/></c:set>
            <a href="${urlDownload}" data-ajax="false"><img src="${urlIcon}" class="ui-li-icon"/><u:getName path="${doc.path}"/></a>
            <span class="ui-li-count">${size}</span>
            <a href="${urlMenu}"></a>
          </li>
        </c:forEach>
      </ul>
    </div>
  </div>
</body>
</html>