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
  <c:url value="/frontend/Download" var="urlDownload">
    <c:param name="uuid" value="${doc.uuid}"/>
  </c:url>
  <c:url value="/frontend/Converter" var="urlDownloadPdf">
    <c:param name="toPdf" value="true"/>
    <c:param name="uuid" value="${doc.uuid}"/>
  </c:url>
  <u:constantsMap className="com.openkm.dao.bean.Translation" var="Translation"/>
  <u:constantsMap className="com.openkm.core.Config" var="Config"/>
  <div data-role="page" data-theme="${Config.MOBILE_THEME}">
    <c:choose>
      <c:when test="${action == 'directOpen'}">
        <div data-role="header" data-position="inline" data-theme="${Config.MOBILE_THEME}"> 
          <a href="home.jsp" data-iconpos="notext" data-icon="home" class="ui-btn-left"></a>
      	  <h1><u:getName path="${doc.path}"/></h1>
    	</div>
      </c:when>
      <c:when test="${action == 'propertiesFromSearch'}">
        <c:url value="Search" var="urlSearch">
          <c:param name="action" value="searchNormal"/>
          <c:param name="content" value="${content}"/>
          <c:param name="filename" value="${filename}"/>
          <c:param name="keywords" value="${keywords}"/>
          <c:param name="offset" value="${offset}"/>
          <c:param name="limit" value="${limit}"/>
        </c:url>
        <div data-role="header" data-position="inline" data-theme="${Config.MOBILE_THEME}"> 
          <a href="${urlSearch}" data-iconpos="notext" data-icon="back" class="ui-btn-left"></a>
      	  <h1><u:getName path="${doc.path}"/></h1>
    	</div>
      </c:when>
      <c:otherwise>
        <div data-role="header" data-position="inline" data-theme="${Config.MOBILE_THEME}"> 
          <a href="#" data-iconpos="notext" data-icon="back" data-rel="back" class="ui-btn-left"></a>
      	  <h1><u:getName path="${doc.path}"/></h1>
    	</div>
      </c:otherwise>
    </c:choose>
    <div data-role="content" data-theme="${Config.MOBILE_THEME}">
      <table style="width: 100%">
        <tr><td><b><u:message key="document.uuid" module="${Translation.MODULE_MOBILE}"/>:</b></td><td>${doc.uuid}</td></tr>
        <tr><td><b><u:message key="document.name" module="${Translation.MODULE_MOBILE}"/>:</b></td><td><u:getName path="${doc.path}"/></td></tr>
        <tr><td><b><u:message key="document.version" module="${Translation.MODULE_MOBILE}"/>:</b></td><td>${doc.actualVersion.name}</td></tr>
        <tr><td><b><u:message key="document.folder" module="${Translation.MODULE_MOBILE}"/>:</b></td><td><u:getParent path="${doc.path}"/></td></tr>
        <tr><td><b><u:message key="document.size" module="${Translation.MODULE_MOBILE}"/>:</b></td><td><u:formatSize size="${doc.actualVersion.size}"/></td></tr>
        <tr><td><b><u:message key="document.created" module="${Translation.MODULE_MOBILE}"/>:</b></td><td><u:formatDate calendar="${doc.created}"/> by ${doc.author}</td></tr>
        <tr><td><b><u:message key="document.modified" module="${Translation.MODULE_MOBILE}"/>:</b></td><td><u:formatDate calendar="${doc.actualVersion.created}"/> by ${doc.actualVersion.author}</td></tr>
        <tr><td><b><u:message key="document.mime" module="${Translation.MODULE_MOBILE}"/>:</b></td><td>${doc.mimeType}</td></tr>
        <tr>
          <td valign="top"><b><u:message key="document.keywords" module="${Translation.MODULE_MOBILE}"/>:</b></td>
          <td>
            <c:forEach var="keyword" items="${doc.keywords}" varStatus="loop">
              ${keyword}<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
          </td>
        </tr>
        <tr>
          <td valign="top"><b><u:message key="document.categories" module="${Translation.MODULE_MOBILE}"/>:</b></td>
          <td>
            <c:forEach var="category" items="${categories}" varStatus="loop">
              ${category}<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
          </td>
        </tr>
        <c:choose>
          <c:when test="${doc.checkedOut}"><c:set var="status" value="Checkout by ${doc.lockInfo.owner}"/></c:when>
          <c:when test="${doc.locked}"><c:set var="status" value="Locked by ${doc.lockInfo.owner}"/></c:when>
          <c:otherwise><c:set var="status" value="Available"/></c:otherwise>
        </c:choose>
        <tr><td><b><u:message key="document.status" module="${Translation.MODULE_MOBILE}"/>:</b></td><td>${status}</td></tr>
        <c:choose>
          <c:when test="${doc.subscribed}"><c:set var="subscribed" value="Yes"/></c:when>
          <c:otherwise><c:set var="subscribed" value="No"/></c:otherwise>
        </c:choose>
        <tr><td><b><u:message key="document.subscribed" module="${Translation.MODULE_MOBILE}"/>:</b></td><td>${subscribed}</td></tr>
        <tr>
          <td colspan="2">
            <a href="${urlDownload}" data-role="button" data-ajax="false" data-theme="${Config.MOBILE_THEME}">
              <u:message key="menu.download" module="${Translation.MODULE_MOBILE}"/>
            </a>
          </td>
        </tr>
        <c:if test="${doc.convertibleToPdf}">
          <tr>
            <td colspan="2">
              <a href="${urlDownloadPdf}" data-role="button" data-ajax="false" data-theme="${Config.MOBILE_THEME}">
                <u:message key="menu.download.pdf" module="${Translation.MODULE_MOBILE}"/>
              </a>
            </td>
          </tr>
        </c:if>
        <c:if test="${not empty doc.subscriptors}">
          <tr>
            <td colspan="2">
              <div data-role="collapsible" data-collapsed="true" data-content-theme="c">
                <h3><u:message key="document.subscribed.users" module="${Translation.MODULE_MOBILE}"/></h3>
                <ul data-role="listview">
                  <c:forEach var="sub" items="${doc.subscriptors}">
                    <li><span style="font-weight: normal">${sub}</span></li>
                  </c:forEach>
                </ul>
              </div>
            </td>
          </tr>
        </c:if>
        <c:forEach var="group" items="${groups}">
          <tr>
          	<td colspan="2">
              <div data-role="collapsible" data-collapsed="true" data-content-theme="c">
              	<h3>${group.label}</h3>
              	<ul data-role="listview">
              	  <c:forEach var="entry" items="${group.map}">
              	    <li>${entry.key}: <span style="font-weight: normal">${entry.value}</span></li>
              	  </c:forEach>
              	</ul>
              </div>
          	</td>
          </tr>
        </c:forEach>
        <c:if test="${not empty notes}">
          <tr>
            <td colspan="2">
              <div data-role="collapsible" data-collapsed="true" data-content-theme="c">
                <h3><u:message key="document.notes" module="${Translation.MODULE_MOBILE}"/></h3>
                <ul data-role="listview">
                  <c:forEach var="note" items="${notes}">
                    <li data-role="list-divider">
                      <div class="ui-grid-a">
                        <div class="ui-block-a">${note.author}</div>
                        <div class="ui-block-b"><u:formatDate calendar="${note.date}"/></div>
                      </div>
                    </li>
                    <li><span style="font-weight: normal">${note.text}</span></li>
                  </c:forEach>
                </ul>
              </div>
            </td>
          </tr>
        </c:if>
      </table>
    </div>
   </div>
</body>
</html>