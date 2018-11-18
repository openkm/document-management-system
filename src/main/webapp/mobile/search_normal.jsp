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
  <script type="text/javascript">
    $(function() {
      $('li').bind('taphold', function(e) {
     	e.preventDefault();
        var id = $(this).attr('id');
        var action = $(this).attr('data-action');
        //alert('You ' + e.type + " - '" + id + "'" + " => " + action);
        $.mobile.changePage("Search?action="+action+"&uuid="+id, null, true, true);        
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
      <a href="home.jsp" data-iconpos="notext" data-icon="home" class="ui-btn-left"></a>
      <h1><u:message key="menu.search" module="${Translation.MODULE_MOBILE}"/></h1>
    </div>
    <div data-role="content" data-theme="${Config.MOBILE_THEME}">
      <form action="Search" method="get">
  	    <input type="hidden" name="action" value="searchNormal"/>
  	    <fieldset>
  	      <label for="content">Content</label>
  	      <input type="text" name="content" id="content" value="${content}"/>
  	      <label for="filename">Filename</label>
  	      <input type="text" name="filename" id="filename" value="${filename}"/>
  	      <label for="keywords">Keywords</label>
  	      <input type="text" name="keywords" id="keywords" value="${keywords}"/>
  	      <button type="submit" data-theme="${Config.MOBILE_THEME}">Search</button>
  	    </fieldset>
  	  </form>
  	</div>
  	<!-- List documents -->
  	<ul data-role="listview" data-theme="c">
      <c:forEach var="result" items="${resultSet.results}">
        <c:choose>
          <c:when test="${result.node != null}">
            <!-- document -->
            <c:set var="doc" value="${result.node}"></c:set>
            <li id="${doc.uuid}" data-action="searchMenu">
              <c:url value="/frontend/Download" var="urlDownload">
                <c:if test="${doc.convertibleToPdf}">
                  <c:param name="toPdf" value="true"/>
                </c:if>
                <c:param name="uuid" value="${doc.uuid}"/>
              </c:url>
              <c:url value="Desktop" var="urlProperties">
                <c:param name="action" value="propertiesFromSearch"/>
                <c:param name="content" value="${content}"/>
                <c:param name="filename" value="${filename}"/>
                <c:param name="keywords" value="${keywords}"/>
                <c:param name="offset" value="${offset}"/>
                <c:param name="limit" value="${limit}"/>
                <c:param name="uuid" value="${doc.uuid}"/>
                <c:param name="nodeType" value="${Document.TYPE}"/>
              </c:url>
              <c:url value="/mime/${doc.mimeType}" var="urlIcon"></c:url>
              <c:set var="size"><u:formatSize size="${doc.actualVersion.size}"/></c:set>
              <a href="${urlDownload}" data-ajax="false"><img src="${urlIcon}" class="ui-li-icon"/><u:getName path="${doc.path}"/></a>
              <span class="ui-li-count">${size}</span>
              <a href="${urlProperties}"></a>
            </li>
          </c:when>
        </c:choose>
      </c:forEach>
    </ul>
    <c:if test="${resultSet.total > 0}">
      <div data-role="navbar" data-iconpos="left">
        <ul>
          <li>
            <a href="#">
              <u:message key="search.results" module="${Translation.MODULE_MOBILE}"/>: ${resultSet.total}
              &nbsp;&nbsp; (${offset + 1}
              <u:message key="search.results.to" module="${Translation.MODULE_MOBILE}"/>
              <c:choose>
                <c:when test="${(offset + limit) > resultSet.total}">${resultSet.total}</c:when>
                <c:otherwise>${offset+limit}</c:otherwise>
              </c:choose>
              )
            </a>
          </li>
          <c:if test="${offset > 0}">
            <c:url value="Search" var="urlSearchPrevious">
              <c:param name="action" value="searchNormal"/>
              <c:param name="content" value="${content}"/>
              <c:param name="filename" value="${filename}"/>
              <c:param name="keywords" value="${keywords}"/>
              <c:param name="offset" value="${offset - limit}"/>
              <c:param name="limit" value="${limit}"/>
            </c:url>
            <li><a href="${urlSearchPrevious}" data-icon="arrow-l">&nbsp;</a></li>
          </c:if>
          <c:if test="${resultSet.total > (offset + limit)}">
            <c:url value="Search" var="urlSearchNext">
              <c:param name="action" value="searchNormal"/>
              <c:param name="content" value="${content}"/>
              <c:param name="filename" value="${filename}"/>
              <c:param name="keywords" value="${keywords}"/>
              <c:param name="offset" value="${offset + limit}"/>
              <c:param name="limit" value="${limit}"/>
            </c:url>
            <li><a href="${urlSearchNext}" data-icon="arrow-r">&nbsp;</a></li>
          </c:if>
        </ul>
      </div>
    </c:if>
  </div>
</body>
</html>