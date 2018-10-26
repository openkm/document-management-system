<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
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
        height : 300,
        width : 400,
        eventType : 'click',
        overlayOpacity : '57',
        windowSource : 'iframe',
        windowPadding : 0
      });
    });
  
    function dialogClose() {
      $dm.closeDOMWindow();
    }
  </script>
  <title>Repository Checker</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <u:constantsMap className="com.openkm.bean.Repository" var="Repository"/>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="utilities.jsp">Utilities</a>
        </li>
        <li class="path">Repository checker</li>
      </ul>
      <br/>
      <form action="RepositoryChecker">
        <table class="form" align="center">
          <tr>
            <td>Path</td>
            <td>
              <input name="repoPath" id="repoPath" size="50" value="/${Repository.ROOT}"/>
            </td>
            <td>
              <a class="ds" href="../extension/DataBrowser?action=repo&sel=fld&dst=repoPath&path=/${Repository.ROOT}">
                <img src="img/action/browse_repo.png"/>
              </a>
            </td>
          </tr>
          <tr><td>Fast</td><td colspan="2"><input name="fast" type="checkbox"/></td></tr>
          <tr><td>Versions</td><td colspan="2"><input name="versions" type="checkbox"/></td></tr>
          <tr><td>Checksum</td><td colspan="2"><input name="checksum" type="checkbox"/></td></tr>
          <tr>
            <td colspan="3" align="right">
              <input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton"/>
              <input type="submit" value="Check" class="yesButton"/>
            </td>
          </tr>
        </table>
      </form>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>