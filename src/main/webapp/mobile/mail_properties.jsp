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
  <u:constantsMap className="com.openkm.core.Config" var="Config"/>
  <div data-role="page" data-theme="${Config.MOBILE_THEME}">
    <div data-role="header" data-position="inline" data-theme="${Config.MOBILE_THEME}"> 
      <a href="#" data-iconpos="notext" data-icon="back" data-rel="back" class="ui-btn-left"></a>
      <h1><u:getName path="${mail.path}"/></h1>
    </div>
    <div data-role="content">
      <table style="width: 100%">
        <tr><td><b><u:message key="folder.uuid" module="${Translation.MODULE_MOBILE}"/>:</b></td><td>${mail.uuid}</td></tr>
        <tr><td><b><u:message key="folder.name" module="${Translation.MODULE_MOBILE}"/>:</b></td><td><u:getName path="${mail.path}"/></td></tr>
        <tr><td><b><u:message key="folder.parent" module="${Translation.MODULE_MOBILE}"/>:</b></td><td><u:getParent path="${mail.path}"/></td></tr>
        <tr><td><b><u:message key="folder.created" module="${Translation.MODULE_MOBILE}"/>:</b></td><td><u:formatDate calendar="${mail.created}"/> by ${mail.author}</td></tr>
        <c:choose>
          <c:when test="${mail.subscribed}"><c:set var="subscribed" value="Yes"/></c:when>
          <c:otherwise><c:set var="subscribed" value="No"/></c:otherwise>
        </c:choose>
        <tr>
          <td valign="top"><b><u:message key="mail.keywords" module="${Translation.MODULE_MOBILE}"/>:</b></td>
          <td>
            <c:forEach var="keyword" items="${mail.keywords}" varStatus="loop">
              ${keyword}<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
          </td>
        </tr>
        <tr>
          <td valign="top"><b><u:message key="mail.categories" module="${Translation.MODULE_MOBILE}"/>:</b></td>
          <td>
            <c:forEach var="category" items="${categories}" varStatus="loop">
              ${category}<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
          </td>
        </tr>
        <tr><td><b><u:message key="folder.subscribed" module="${Translation.MODULE_MOBILE}"/>:</b></td><td>${subscribed}</td></tr>
        <c:if test="${not empty mail.subscriptors}">
          <tr>
            <td colspan="2">
              <div data-role="collapsible" data-collapsed="true" data-content-theme="c">
                <h3><u:message key="mail.subscribed.users" module="${Translation.MODULE_MOBILE}"/></h3>
                <ul data-role="listview">
                  <c:forEach var="sub" items="${mail.subscriptors}">
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