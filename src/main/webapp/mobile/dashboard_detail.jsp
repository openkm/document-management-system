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
  <c:choose>
      <c:when test="${action == 'lastModified'}">
        <div data-role="header" data-position="inline" data-theme="${Config.MOBILE_THEME}"> 
          <a href="home.jsp" data-iconpos="notext" data-icon="home" class="ui-btn-left"></a>
          <!-- <a href="#" data-iconpos="notext" data-icon="back" data-rel="back" class="ui-btn-left"></a> -->
      	  <h1>${doc.name}</h1>
    	</div>
      </c:when>
    </c:choose>
  </div>
</body>
</html>