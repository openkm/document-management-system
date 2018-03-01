<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.core.Config" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0"/>
  <link rel="Shortcut icon" href="<%=request.getContextPath() %>/favicon.ico" />
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/bootstrap/bootstrap.min.css" type="text/css" />
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/font-awesome/font-awesome.min.css" type="text/css" />
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/login.css" type="text/css" />
  <title>OpenKM Mobile</title>
</head>
<body>
<div id="login-background"></div>
<u:constantsMap className="com.openkm.core.Config" var="Config"/>
  <div id="login-container">
    <div class="openkm-version" style="padding: 5px 20px; color: #404040; font-wight:bold; background:#fed400; width:200px; position:fixed; top:0px; left:0px; z-index:1;">
      <strong>Community Version</strong>
    </div>
            <div class="login-title">
                <img id="login-image" class="img-responsive center-block" src="img/logo_login.gif">
            </div>            
            <div class="block remove-margin" style="border-bottom-left-radius: 10px; border-bottom-right-radius: 10px;">              
              <form name="loginform" method="post" action="<%=request.getContextPath() %>/j_spring_security_check" class="form-horizontal form-bordered form-control-borderless" id="form-login">
                <div class="form-group form-header text-center">
                        <div class="col-xs-12">
                            <%=Config.TEXT_BANNER %>
                      <%=Config.TEXT_WELCOME %>
                        </div>
                    </div>
                <c:if test="${not empty param.error}">
                  <div class="form-group form-error">
                      <div id="col-xs-12">                                    
                    <p class="text-danger text-center">
                        Authentication error
                      <c:if test="${Config.USER_PASSWORD_RESET && Config.PRINCIPAL_ADAPTER == 'com.openkm.principal.DatabasePrincipalAdapter'}">
                          (<a href="password_reset.jsp">Forgot your password?</a>)
                      </c:if>
                    </p>
                    
                </div>  
              </div>   
            </c:if>           
                    <div class="form-group">
                        <div class="col-xs-12">
                            <div class="input-group">                                
                    <span class="input-group-addon"><i class="fa fa-user"></i></i></span>                       
                    <input name="j_username" id="j_username" type="text" <%=Config.SYSTEM_LOGIN_LOWERCASE?"onchange=\"makeLowercase();\"":"" %> class="form-control input-lg" placeholder="User"/>
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-xs-12">
                            <div class="input-group">
                                <span class="input-group-addon"><i class="fa fa-asterisk"></i></span>
                                <input type="password" id="j_password" name="j_password" class="form-control input-lg" placeholder="Password">
                            </div>
                        </div>
                    </div>
                    <div class="form-group form-actions">
                        <div class="col-xs-6">
                          <!-- 
                            <label class="switch switch-primary" data-toggle="tooltip" title="Recordarme?">
                                <input type="checkbox" id="_spring_security_remember_me" name="_spring_security_remember_me" >
                                <span></span>
                            </label>
                            -->
                        </div>                        
                        <div class="col-xs-12 text-right">
                            <button name="submit" type="submit" class="btn btn-sm btn-primary btn-block"><i class="fa fa-key"></i> Login</button>                            
                        </div>
                    </div>
                    <div class="form-group form-footer" style="border-bottom-left-radius: 10px !important; border-bottom-right-radius: 10px !important;">
                      <div class="col-xs-12 text-center">
                            <p>OpenKM Mobile</p>
                        </div>                      
                    </div>
                </form>
            </div>
  </div>   
  <script type="text/javascript">
    function makeLowercase() {
      var username = document.getElementById('j_username'); 
      username.value = username.value.toLowerCase();
    }
  </script>
</body>
</html>