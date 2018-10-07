<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" href="css/style.css" type="text/css" />
  <title>Experimental</title>
</head>

<body>
  <c:choose>
    <c:when test="${u:isMultipleInstancesAdmin()}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="experimental.jsp">Experimental</a>
        </li>
      </ul>
      <br/>
      <c:url value="Language" var="urlAddTranslation">
        <c:param name="action" value="addTranslation" />
      </c:url>
      <center>
        <a href="Tail">
          <button class="utilButton">Tail</button>
        </a>
        <a href="config.jsp">
          <button class="utilButton">Configuration</button>
        </a>
        <a href="UnitTesting">
          <button class="utilButton">Unit testing</button>
        </a>
        <a href="${urlAddTranslation}">
          <button class="utilButton">Add translation term</button>
        </a>
      </center>
    </c:when>
    <c:otherwise>
      <div class="error">
        <h3>Only admin users allowed</h3>
      </div>
    </c:otherwise>
  </c:choose>
</body>
</html>