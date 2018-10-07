<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" href="css/style.css" type="text/css" />
  <title>Utilities</title>
</head>
<body>  
  <u:constantsMap className="com.openkm.core.Config" var="Config"/>
  <c:choose>
    <c:when test="${u:isMultipleInstancesAdmin()}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="utilities.jsp">Utilities</a>
        </li>
      </ul>
      <br/>
      <center>
      	<a href="CacheStats"><button class="utilButton">Cache stats</button></a>
        <a href="CheckEmail"><button class="utilButton">Check email</button></a>
        <a href="CheckTextExtraction"><button class="utilButton">Check text extraction</button></a>
        <a href="HibernateStats"><button class="utilButton">Hibernate stats</button></a>
        <a href="ListIndexes"><button class="utilButton">List indexes</button></a>        
        <a href="LogCat"><button class="utilButton">LogCat</button></a>
        <a href="Plugin"><button class="utilButton">Plugins</button></a>
        <a href="ProfilingStats"><button class="utilButton">Profiling stats</button></a>
        <c:if test="${u:isMultipleInstancesAdmin()}">
        	<a href="PurgePermissions"><button class="utilButton">Purge permissions</button></a>
        </c:if>
        <a href="rebuild_indexes.jsp"><button class="utilButton">Rebuild indexes</button></a>
        <a href="repository_checker.jsp"><button class="utilButton">Repository checker</button></a>                                      
        <a href="system_properties.jsp"><button class="utilButton">System properties</button></a>
        <a href="Css"><button class="utilButton">CSS</button></a>
      </center>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>
