<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.api.OKMRepository" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ page import="com.openkm.util.WarUtils" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<u:constantsMap className="com.openkm.core.Config" var="Config"/>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" href="css/style.css" type="text/css" />
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="../js/utils.js"></script>
  <script type="text/javascript">
    if (getUserAgent().startsWith('ie')) {
      addCss('css/ie.css');
    }
  </script>
  <title>Main</title>
</head>
<body>  
  <c:choose>
    <c:when test="${u:isAdmin()}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="home.jsp">OpenKM Administration</a>
        </li>
      </ul>
      <br/>
      <table width="234px" class="form" style="padding: 10px; margin-top: 25px">
        <tr><td><b>OpenKM - Knowledge Management</b></td></tr>
        <tr><td nowrap="nowrap">Version: <%=WarUtils.getAppVersion() %></td></tr>
        <tr><td>&nbsp;</td></tr>
        <tr><td>&copy; 2006-2018 OpenKM</td></tr>
        <tr><td>&nbsp;</td></tr>
        <tr><td><b>Support</b></td></tr>
        <tr><td><a target="_blank" href="http://forum.openkm.com/">http://forum.openkm.com</a></td></tr>
        <tr><td>&nbsp;</td></tr>
        <tr><td><b>Installation ID</b></td></tr>
        <tr><td nowrap="nowrap"><%=OKMRepository.getInstance().getRepositoryUuid(null)%></td></tr>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>