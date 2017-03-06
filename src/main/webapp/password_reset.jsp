<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="<%=request.getContextPath() %>/favicon.ico" />
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/desktop.css" type="text/css" />
  <title>OpenKM password reset</title>
</head>
<body onload="document.forms[0].elements[0].focus()">
  <div id="box">
    <div id="logo"></div>
    <% ServletContext sc = getServletContext(); %>
    <c:if test="${not empty resetFailed}">
      <div id="error">${resetFailed}</div>
      <% sc.removeAttribute("resetFailed"); %>
    </c:if>
    <c:if test="${not empty resetOk}">
      <div id="ok" style="height: 35px">Password correctly reset and mail sent to ${resetOk}</div>
      <% sc.removeAttribute("resetOk"); %>
    </c:if>
    <div id="text">
      <center><img src="<%=request.getContextPath() %>/img/lock.png"/></center>
      <p>Please insert the user name of your registered user. A password reminder will be sent to your email address.</p>
    </div>
    <div id="form">
      <form name="resetForm" method="post" action="PasswordReset">
        <label for="username">Registered user name</label>
        <input name="username" id="username" type="text" /><br/><br/>
        <input type="submit" name="submit" value="Reset Password" />
      </form>
      <br/><br/>
      <center><a href="login.jsp">Back to login</a></center>
    </div>
  </div>
</body>
</html>