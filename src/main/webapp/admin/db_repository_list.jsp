<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.core.Config" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" href="css/style.css" type="text/css" />
  <script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
  <title>Repository View</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:set var="managedTextExtraction"><%=Config.MANAGED_TEXT_EXTRACTION%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url value="DbRepositoryView" var="urlRefresh">
        <c:param name="uuid" value="${node.uuid}"/>
        <c:param name="action" value="list"/>
      </c:url>
      <ul id="breadcrumb">
        <li class="path">
          <a href="utilities.jsp">Utilities</a>
        </li>
        <li class="path">Repository view</li>
        <li class="action">
          <a href="${urlRefresh}">
            <img src="img/action/refresh.png" alt="Refresh" title="Refresh" style="vertical-align: middle;"/>
            Refresh
          </a>
        </li>
      </ul>
      <h2>Info</h2>
      <ul>
        <li><b>Path</b>: ${breadcrumb}</li>
        <li><b>Depth</b>: ${depth}</li>
        <li><b>Type</b>: ${fn:toUpperCase(nodeType)}</li>
        <c:if test="${depth > 0}">
          <li><b>Actions</b>:
            <c:url value="DbRepositoryView" var="urlRemoveCurrent">
              <c:param name="uuid" value="${node.uuid}"/>
              <c:param name="action" value="remove_current"/>
            </c:url>
            <c:url value="DbRepositoryView" var="urlRemoveContent">
              <c:param name="uuid" value="${node.uuid}"/>
              <c:param name="action" value="remove_content"/>
            </c:url>
            <c:if test="${depth > 1}">
              <a href="${urlRemoveCurrent}">Remove current</a>
            </c:if>
            <c:if test="${depth > 1 && nodeType == 'folder'}"> - </c:if>
            <c:if test="${nodeType == 'folder'}">
              <a href="${urlRemoveContent}">Remove contents</a>
            </c:if>
            <c:if test="${locked}">
              <c:url value="DbRepositoryView" var="urlUnlock">
                <c:param name="uuid" value="${node.uuid}"/>
                <c:param name="action" value="unlock"/>
              </c:url>
              - <a href="${urlUnlock}">Unlock</a>
            </c:if>
            <c:if test="${isDocumentContent && node.checkedOut}">
              <c:url value="DbRepositoryView" var="urlCheckin">
                <c:param name="uuid" value="${node.uuid}"/>
                <c:param name="action" value="checkin"/>
              </c:url>
              - <a href="${urlCheckin}">Update</a>
            </c:if>
            <c:if test="${nodeType == 'document'}">
              <c:url value="DbRepositoryView" var="urlForceTextExtraction">
                <c:param name="uuid" value="${node.uuid}"/>
                <c:param name="action" value="forceTextExtraction"/>
              </c:url>
              - <a href="${urlForceTextExtraction}">Force text extraction</a>
            </c:if>
            <!--
            <c:if test="${nodeType == 'folder'}">
              <c:choose>
                <c:when test="${contentInfo != null}">
                  <c:url value="DbRepositoryView" var="urlDeactivate">
                    <c:param name="path" value="${node.path}"/>
                    <c:param name="stats" value="0"/>
                  </c:url>
                  - <a href="${urlDeactivate}">Disable statistics</a>
                </c:when>
                <c:otherwise>
                  <c:url value="DbRepositoryView" var="urlActivate">
                    <c:param name="path" value="${node.path}"/>
                    <c:param name="stats" value="1"/>
                  </c:url>
                  - <a href="${urlActivate}">Enable statistics</a>
                </c:otherwise>
              </c:choose>
            </c:if>
            -->
          </li>
          <c:if test="${locked}"><li><b>Locked</b></li></c:if>
        </c:if>
      </ul>
      <c:if test="${contentInfo != null}">
        <h2>Statistics</h2>
        <ul>
          <li><b>Size</b>: <u:formatSize size="${contentInfo.size}"/></li>
          <li><b>Folders</b>: ${contentInfo.folders} </li>
          <li><b>Documents</b>: ${contentInfo.documents}</li>
          <li><b>Mails</b>: ${contentInfo.mails}</li>
        </ul>
      </c:if>
      <h2>Properties</h2>
      <table class="results" width="90%">
        <tr><th>Name</th><th>Value</th><th>Action</th></tr>
        <tr class="even"><td>UUID</td><td>${node.uuid}</td><td></td></tr>
        <tr class="odd"><td>Keywords</td><td>${node.keywords}</td><td></td></tr>
        <tr class="even"><td>Categories</td><td>${node.categories}</td><td></td></tr>
      </table>
      <h2>Metadata Groups</h2>
      <table class="results" width="90%">
        <tr><th>Group</th><th>Name</th><th>Field</th><th>Type</th><th>Value</th><th>Action</th></tr>
        <c:forEach var="property" items="${properties}" varStatus="row">
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${property.group}</td>
            <td>${property.name}</td>
            <td>${property.field}</td>
            <td>${property.type}</td>
            <td>${property.value}</td>
            <td align="center">
              <c:url value="DbRepositoryView" var="urlEdit">
                <c:param name="uuid" value="${node.uuid}"/>
                <c:param name="property" value="${property.name}"/>
                <c:param name="value" value="${property.value}"/>
                <c:param name="group" value="${property.group}"/>
                <c:param name="field" value="${property.field}"/>
                <c:param name="action" value="edit"/>
              </c:url>
              <a href="${urlEdit}"><img src="img/action/edit.png" title="Edit" alt="Edit"/></a>
            </td>
          </tr>
        </c:forEach>
      </table>
      <c:if test="${nodeType == 'folder' || nodeType == 'mail'}">
        <h2>Children</h2>
        <table class="results" width="90%">
          <tr><th>Type</th><th>Locked</th><th>CheckedOut</th><th>Name</th></tr>
          <c:forEach var="child" items="${children}" varStatus="row">
            <c:url value="DbRepositoryView" var="urlList">
              <c:param name="uuid" value="${child.uuid}"/>
            </c:url>
            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
              <td>${fn:toUpperCase(child.nodeType)}</td>
              <td align="center"><c:if test="${child.locked}"><img src="img/true.png"/></c:if></td>
              <td align="center"><c:if test="${child.checkedOut}"><img src="img/true.png"/></c:if></td>
              <td><a href="${urlList}">${child.name}</a></td>
            </tr>
          </c:forEach>
        </table>
      </c:if>
      <c:if test="${nodeType == 'document'}">
        <h2>History</h2>
        <table class="results" width="90%">
          <tr><th>Name</th><th>Author</th><th>Size</th><th>Created</th><th>Comment</th><th>Action</th></tr>
          <c:forEach var="version" items="${history}" varStatus="row">
            <c:url value="DbRepositoryView" var="urlDownloadVersion">
              <c:param name="action" value="downloadVersion"/>
              <c:param name="uuid" value="${node.uuid}"/>
              <c:param name="fileName" value="${node.name}"/>
              <c:param name="name" value="${version.name}"/>
              <c:param name="mimeType" value="${version.mimeType}"/>
            </c:url>
            <c:url value="DbRepositoryView" var="urlDiff">
              <c:param name="action" value="diff"/>
              <c:param name="uuid" value="${node.uuid}"/>
              <c:param name="mimeType" value="${version.mimeType}"/>
              <c:param name="oldUuid" value="${version.previous}"/>
              <c:param name="newName" value="${version.name}"/>
            </c:url>
            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
              <td width="50px">${version.name}</td>
              <td width="150px">${version.author}</td>
              <td width="60px"><u:formatSize size="${version.size}" /></td>
              <td width="120px"><u:formatDate calendar="${version.created}" /></td>
              <td>${version.comment}</td>
              <td width="120px" align="center">
                <a href="${urlDownloadVersion}">Download</a>
                <c:if test="${not empty version.previous}">
                  -
                  <a href="${urlDiff}">Diff</a>
                </c:if>
              </td>
            </tr>
          </c:forEach>
        </table>
        <h2>Text Extracted</h2>
        <table class="results" width="90%">
          <tr><th>${mimeType}</th></tr>
          <tr class="even">
            <c:choose>
              <c:when test="${textExtracted}"><td>${text}</td></c:when>
              <c:otherwise><td align="center">Still in text extraction queue</td></c:otherwise>
            </c:choose>
          </tr>
        </table>
      </c:if>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>