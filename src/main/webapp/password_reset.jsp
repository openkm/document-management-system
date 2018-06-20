<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<%@ page import="com.openkm.core.Config" %>
<!DOCTYPE html>
<head>
  <meta charset="utf-8">
  <meta name="author" content="OpenKM">
  <meta name="description" content="OpenKM is an EDRMS EDRMS, Document Management System and Record Management, easily to manage digital content, simplify your workload and yield high efficiency.">
  <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1.0">
  <link rel="Shortcut icon" href="<%=request.getContextPath() %>/logo/favicon"/>
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/bootstrap/bootstrap.min.css" type="text/css"/>
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/font-awesome/font-awesome.min.css" type="text/css"/>
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/login.css" type="text/css"/>
  <title>OpenKM password reset</title>
</head>
<body onload="document.forms[0].elements[0].focus()">
<div id="login-background" class="background-zen">
  <div id="col-xs-12" class="hidden-xs hidden-sm hidden-md" style="height:100%;">
    <div class="background-zen" style="height:100%;"></div>
  </div>
</div>
<u:constantsMap className="com.openkm.core.Config" var="Config"/>
<div id="login-container">
  <div class="login-title">
    <img id="login-image" class="img-responsive center-block" src="img/logo_login.gif">
  </div>
  <div class="block remove-margin" style="border-bottom-left-radius: 10px; border-bottom-right-radius: 10px;">
    <form name="resetForm" method="post" action="PasswordReset"
          class="form-horizontal form-bordered form-control-borderless" id="form-reset">
      <div class="form-group form-header text-center">
        <div class="col-xs-12">
          <%=Config.TEXT_BANNER %>
          <p>Please insert the user name of your registered user. A password reminder will be sent to your email address.</p>
        </div>
      </div>
      <% ServletContext sc = getServletContext(); %>
      <c:if test="${not empty resetFailed}">
        <div class="form-group form-error">
          <div id="col-xs-12">
            <p class="text-danger text-center">${resetFailed}</p>
          </div>
        </div>
        <% sc.removeAttribute("resetFailed"); %>
      </c:if>
      <c:if test="${not empty resetOk}">
        <div class="form-group form-error">
          <div id="col-xs-12">
            <p class="text-success text-center">Password correctly reset and mail sent to ${resetOk}</p>
          </div>
        </div>
        <% sc.removeAttribute("resetOk"); %>
      </c:if>
      <div class="form-group">
        <div class="col-xs-12">
          <div class="input-group">
            <span class="input-group-addon"><i class="fa fa-user"></i></span>
            <input name="userId" id="userId" type="text" class="form-control input-lg" placeholder="Registered user id"/>
          </div>
        </div>
      </div>
      <div class="form-group form-actions">
        <div class="col-xs-6">
          <a href="login.jsp" class="btn btn-sm btn-default">
            <i class="fa fa-arrow-left"></i> Back to login
          </a>
        </div>
        <div class="col-xs-4 pull-right">
          <button name="submit" type="submit" class="btn btn-sm btn-primary btn-block">
            <i class="fa fa-key"></i> Reset Password
          </button>
        </div>
      </div>
      <div class="form-group form-footer"
           style="border-bottom-left-radius: 10px !important; border-bottom-right-radius: 10px !important;">
        <div class="col-xs-12 text-center">
          <p>&copy; 2006-2018 OpenKM. All rights reserved.</p>
        </div>
      </div>
    </form>
  </div>
</div>
</body>
</html>