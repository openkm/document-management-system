<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.core.Config" %>
<%@ page import="com.openkm.dao.LanguageDAO" %>
<%@ page import="com.openkm.dao.bean.Language" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<!DOCTYPE html>
<head>
  <meta charset="utf-8">  
  <meta name="author" content="OpenKM">
  <meta content="OpenKM is an EDRMS EDRMS, Document Management System and Record Management, easily to manage digital content, simplify your workload and yield high efficiency." name="description">
  <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1.0">
  <link rel="Shortcut icon" href="<%=request.getContextPath() %>/logo/favicon" />
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/bootstrap/bootstrap.min.css?v=%{TIMESTAMP}%" type="text/css" />
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/font-awesome/font-awesome.min.css?v=%{TIMESTAMP}%" type="text/css" />
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/login.css?v=%{TIMESTAMP}%" type="text/css" />  
  <%
    Locale locale = request.getLocale();
    Cookie[] cookies = request.getCookies();
    String preset = null;
    
    if (cookies != null) {
      for (int i=0; i<cookies.length; i++) {
        if (cookies[i].getName().equals("lang")) {
          preset = cookies[i].getValue();
        }
      }
    }
    
    if (preset == null || preset.equals("")) {
      preset = locale.getLanguage() + "-" + locale.getCountry();
    }
  %>
  <title><%=Config.TEXT_TITLE%></title>
</head>
<body onload="document.forms[0].elements[0].focus()">
  <div class="openkm-version" style="padding: 5px 20px; color: #404040; font-wight:bold; background:#fed400; width:200px; position:fixed; top:0px; left:0px; z-index:1;">
    <strong>Community Version</strong>
  </div>
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
              <form name="loginform" method="post" action="j_spring_security_check" onsubmit="setCookie()" class="form-horizontal form-bordered form-control-borderless" id="form-login">
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
                                <% if (Config.SYSTEM_MAINTENANCE) { %>                        
                        <span class="input-group-addon"><i class="gi gi-user"></i></i></span>
                        <input name="j_username" id="j_username" type="hidden" value="<%=Config.SYSTEM_LOGIN_LOWERCASE?Config.ADMIN_USER.toLowerCase():Config.ADMIN_USER%>" class="form-control input-lg" placeholder="System under maintenance"/>
                    <% } else { %>
                      <span class="input-group-addon"><i class="fa fa-user"></i></i></span>                       
                        <input name="j_username" id="j_username" type="text" <%=Config.SYSTEM_LOGIN_LOWERCASE?"onchange=\"makeLowercase();\"":"" %> class="form-control input-lg" placeholder="User"/>
                    <% } %>
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
                      <!-- 
                        <div class="col-xs-3">                          
                            <label class="switch switch-primary" data-toggle="tooltip" title="Recordarme?">
                                <input type="checkbox" id="_spring_security_remember_me" name="_spring_security_remember_me" >
                                <span></span>
                            </label>                            
                        </div>
                        -->
                        <div class="col-xs-5">
                          <select name="j_language" id="j_language" class="form-control" style="border-bottom: 1px solid #eaedf1 !important;">
                    <%
                        List<Language> langs = LanguageDAO.findAll();
                        String whole = null;
                        String part = null;
          
                      // Match whole locale
                        for (Language lang : langs) {
                          String id = lang.getId();
            
                          if (preset.equalsIgnoreCase(id)) {
                              whole = id;
                          } else if (preset.substring(0, 2).equalsIgnoreCase(id.substring(0, 2))) {
                              part = id;
                          }
                        }
          
                        // Select selected
                        for (Language lang : langs) {
                          String id = lang.getId();
                          String selected = "";
            
                          if (whole != null && id.equalsIgnoreCase(whole)) {
                              selected = "selected";
                          } else if (whole == null && part != null && id.equalsIgnoreCase(part)) {
                              selected = "selected";
                          } else if (whole == null && part == null && Language.DEFAULT.equals(id)) {
                              selected = "selected";
                          }
            
                          out.print("<option "+selected+" value=\""+id+"\">"+lang.getName()+"</option>");
                        }
                  %>
                  </select>
                        </div>
                        <div class="col-xs-4 pull-right">
                            <button name="submit" type="submit" class="btn btn-sm btn-primary btn-block"><i class="fa fa-key"></i> Login</button>                            
                        </div>
                    </div>
                    <% if (Config.SYSTEM_DEMO) { %>
                      <div class="form-group low" style="background-color:white !important;">
                          <div class="col-xs-12 hidden-lg">
                              <jsp:include flush="true" page="login_demo_users.jsp"/>
                          </div>
                      </div>
                    <% } %>
                    <div class="form-group form-footer" style="border-bottom-left-radius: 10px !important; border-bottom-right-radius: 10px !important;">
                      <div class="col-xs-12 text-center">
                            <p>&copy; 2006-2017 OpenKM. All rights reserved.</p>
                        </div>                      
                    </div>
                </form>
            </div>
  </div>   
  
  <% if (Config.SYSTEM_DEMO) { %>
    <div class="demo_users high">
      <div class="col-xs-12 hidden-xs hidden-sm hidden-md">
          <jsp:include flush="true" page="login_demo_users.jsp"/>
      </div>
    </div>
  <% } %>
  
  <script type="text/javascript">
    function makeLowercase() {
      var username = document.getElementById('j_username'); 
      username.value = username.value.toLowerCase();
    }

    function setCookie() {
      var exdate = new Date();
      var value = document.getElementById('j_language').value;
      exdate.setDate(exdate.getDate() + 7);
      document.cookie = "lang=" + escape(value) + ";expires=" + exdate.toUTCString();
    }
  </script>
</body>
</html>