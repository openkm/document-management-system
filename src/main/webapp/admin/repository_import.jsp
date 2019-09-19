<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.openkm.core.Config" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ page import="com.openkm.util.FormatUtil" %>
<%@ page import="java.util.concurrent.TimeUnit" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/admin-style.css" />
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="js/jquery.DOMWindow.js"></script> 
  <script type="text/javascript">
    $(document).ready(function() {
		$dm = $('.ds').openDOMWindow({
			height:300, width:400,
			eventType:'click',
			overlayOpacity:'57',
			windowSource:'iframe', windowPadding:0
		});
	});
    
    function dialogClose() {
		$dm.closeDOMWindow();
    }
    
    function keepSessionAlive() {
    	$.ajax({ type:'GET', url:'../SessionKeepAlive', cache:false, async:false });
    }
    
	window.setInterval('keepSessionAlive()', <%=TimeUnit.MINUTES.toMillis(Config.KEEP_SESSION_ALIVE_INTERVAL)%>);
  </script>
  <title>Repository Import</title>
</head>
<body>
<c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
<c:choose>
  <c:when test="${isAdmin}">
    <ul id="breadcrumb">
      <li class="path"><a href="repository_import.jsp">Repository import</a></li>
    </ul>
    <br/>
    <form action="Repository" method="post" id="form">
      <input type="hidden" name="action" value="import"/>
      <table class="form" align="center">
        <tr>
          <td>Filesystem path</td>
          <td colspan="2">
            <input type="text" size="50" name="fsPath" id="fsPath" value="${fsPath}"/>
          </td>
          <td>
            <a class="ds" href="../extension/DataBrowser?action=fs&sel=fld&dst=fsPath&root=${Config.INSTANCE_CHROOT_PATH}&path=${fsPath}">
              <img src="img/action/browse_fs.png"/>
            </a>
          </td>
        </tr>
        <tr>
          <td>Repository path</td>
          <td colspan="2">
            <input type="text" size="50" name="repoPath" id="repoPath" value="${repoPath}"/>
          </td>
          <td>
            <a class="ds" href="../extension/DataBrowser?action=repo&sel=fld&dst=repoPath&path=${repoPath}"/>
              <img src="img/action/browse_repo.png"/>
            </a>
          </td>
        </tr>
        <tr>
          <td>Metadata</td>
          <td colspan="2">
            <c:choose>
              <c:when test="${metadata}">
                <input type="checkbox" name="metadata" id="metadata" checked="checked"/>
              </c:when>
              <c:otherwise>
                <input type="checkbox" name="metadata" id="metadata" />
              </c:otherwise>
            </c:choose>            
          </td>
        </tr>
        <tr>
          <td>History</td>
          <td colspan="2">
            <c:choose>
              <c:when test="${history}">
                <input type="checkbox" name="history" id="history" checked="checked"/>
              </c:when>
              <c:otherwise>
                <input type="checkbox" name="history" id="history" />
              </c:otherwise>
            </c:choose>            
          </td>
        </tr>
        <tr>
          <td>Restore UUIDs</td>
          <td width="23px">
            <c:choose>
              <c:when test="${uuid}">
                <input type="checkbox" name="uuid" id="uuid" checked="checked"/>
              </c:when>
              <c:otherwise>
                <input type="checkbox" name="uuid" id="uuid" />
              </c:otherwise>
            </c:choose>            
          </td>
          <td>
            <span style="color:red;">(use with caution)</span>
          </td>
        </tr>
        <tr>
          <td colspan="4" align="right">
            <input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton"/>
            <input type="submit" value="Import" class="yesButton">
          </td>
        </tr>
      </table>
    </form>
    <c:if test="${not empty cInfo}">
      <hr/>
      <b>Files & directories to import:</b> ${files}<br/>
    </c:if>
    <c:if test="${not empty fsPath}">
      <hr/>
      <div class="ok"> Filesystem ${fsPath} imported into ${repoPath} </div>
    </c:if>
    <c:if test="${not empty stats}">
      <br/>
      <b>Documents:</b> ${stats.getDocuments()} <br/>
      <b>Folders:</b> ${stats.getFolders()} <br/>
      <b>Mails:</b> ${stats.getMails()} <br/>
      <b>Size:</b> ${FormatUtil.formatSize(stats.getSize())} <br/>
      <b>Time:</b> ${FormatUtil.formatSeconds(end - begin)} <br/>
    </c:if>
  </c:when>
  <c:otherwise>
    <div class="error"><h3>Only admin users allowed</h3></div>
  </c:otherwise>
</c:choose>
</body>
</html>