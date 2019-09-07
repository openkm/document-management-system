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
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/bootstrap/bootstrap.min.css" type="text/css" />
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/font-awesome/font-awesome.min.css" type="text/css" />
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/login.css" type="text/css" />
  <script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.easy-ticker.min.js"></script>

  <% if (Config.RSS_NEWS) { %>
    <script type="text/javascript">
      $(document).ready(function () {
        // Always show sticker when rss is enabled
        var loaded = false;
        $('#stickerController').show();

        // Change div style when sticker is enabled
        $('#openkmNews').width('<%=Config.RSS_NEWS_BOX_WIDTH %>px');
        $('#feedContainer').width('<%=Config.RSS_NEWS_BOX_WIDTH %>px');
        $('#openkmVersion').addClass("vticket-border-right");

        $('#stop').on({
          'click': function () {
            var src = ($('#stopImg').attr('src') === 'img/stop.png')
              ? 'img/pause.png'
              : 'img/stop.png';
            $('#stopImg').attr('src', src);
          }
        });

        $('#eye').on({
          'click': function () {
            if ($('#eyeImg').attr('src') === 'img/eye.png') {
              localStorage.setItem('openkmNews', 'hide');
              $('#eyeImg').attr('src', 'img/eye-disabled.png');
              $('#feedContainer').hide();
              $('#stop').hide();
              $('#forward').hide();
              $('#backward').hide();
            } else {
              localStorage.setItem('openkmNews', 'show');
              $('#eyeImg').attr('src', 'img/eye.png');
              $('#feedContainer').show();
              $('#stop').show();
              $('#forward').show();
              $('#backward').show();
              loadRss();
            }
          }
        });

        if (localStorage.getItem('openkmNews') == 'hide') {
          $('#eyeImg').attr('src', 'img/eye-disabled.png');
          $('#feedContainer').hide();
          $('#stop').hide();
          $('#forward').hide();
          $('#backward').hide();
        } else {
          $('#feedContainer').show();
          loadRss();
        }

        function loadRss() {
          // Rss must be loaded only one time, because easyTicket can not be executed twice
          if (!loaded) {
            loaded = true;
            $.get("Rss", function (data) {
              // Clean sticker
              $('#feedContainer').empty();

              // Parse xml
              var html = '<ul>';
              $(data).find('item').each(function () {
                var $item = $(this);
                var title = $item.find('title').text();
                var link = $item.find('link').text();
                var description = $item.find('description').text();
                var pubDate = $item.find('pubDate').text();

                html += "<li>";
                html += "<h4>" + title + "</h4>";
                html += "<em>" + pubDate + "</em>";
                html += "<p>" + description + "</p>";
                html += "<div align=\"right\"><a href=\"" + link + "\" target=\"_blank\" style=\"cursor:hand !important;\">Read More</a></div>";
                html += "</li>";
              });
              html += '</ul>';

              // Set html
              $('#feedContainer').append(html);

              // Load sticker
              $('#feedContainer').easyTicker({
                direction: 'up',
                easing: 'swing',
                speed: 'slow',
                interval: 5000,
                height: 'auto',
                visible: <%=Config.RSS_NEWS_VISIBLE %>,
                mousePause: 1,
                controls: {
                  up: '#forward',
                  down: '#backward',
                  toggle: '#stop'
                }
              });
            });
          }
        }
      });
    </script>
  <% } %>
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
  <div id="openkmNews" class="openkm-news">
    <div id="openkmVersion" class="openkm-version">
      <strong>Community Version</strong>
      <div id="stickerController" class="openkm-sticker" style="display:none;">
        <a href="#" id="backward" style="cursor:hand !important;"><img src="img/backward.png" alt="Backward" title="Backward" /></a>
        <a href="#" id="stop" style="cursor:hand !important;"><img id="stopImg" src="img/stop.png" alt="Stop" title="Stop"/></a>
        <a href="#" id="forward" style="cursor:hand !important;"><img src="img/forward.png" alt="Forward" title="Forward" /></a>
        <a href="#" id="eye" style="cursor:hand !important;"><img id="eyeImg" src="img/eye.png" alt="Show / hide news" title="Show / hide news" /></a>
      </div>
    </div>
    <div style="display:none;" id="feedContainer" class="vticker"></div>
  </div>

  <div id="login-background" class="background-zen">
    <div id="col-xs-12" class="hidden-xs hidden-sm hidden-md" style="height:100%;">
      <div class="background-zen" style="height:100%;"></div>
    </div>
  </div>
  <u:constantsMap className="com.openkm.core.Config" var="Config"/>
  <div id="login-container">
    <div class="login-title">
      <img id="login-image" class="img-responsive center-block" src="logo/login">
    </div>
    <div class="block remove-margin" style="border-bottom-left-radius: 10px; border-bottom-right-radius: 10px;">
      <form name="loginform" method="post" action="j_spring_security_check" onsubmit="setCookie()"
            class="form-horizontal form-bordered form-control-borderless" id="form-login">
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
                <c:if
                    test="${Config.USER_PASSWORD_RESET && Config.PRINCIPAL_ADAPTER == 'com.openkm.principal.DatabasePrincipalAdapter'}">
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
              <span class="input-group-addon"><i class="gi gi-user"></i></span>
              <input name="j_username" id="j_username" type="hidden"
                     value="<%=Config.SYSTEM_LOGIN_LOWERCASE?Config.ADMIN_USER.toLowerCase():Config.ADMIN_USER%>"
                     class="form-control input-lg" placeholder="System under maintenance"/>
              <% } else { %>
              <span class="input-group-addon"><i class="fa fa-user"></i></span>
              <input name="j_username" id="j_username"
                     type="text" <%=Config.SYSTEM_LOGIN_LOWERCASE ? "onchange=\"makeLowercase();\"" : "" %>
                     class="form-control input-lg" placeholder="User"/>
              <% } %>
            </div>
          </div>
        </div>
        <div class="form-group">
          <div class="col-xs-12">
            <div class="input-group">
              <span class="input-group-addon"><i class="fa fa-asterisk"></i></span>
              <input type="password" id="j_password" name="j_password" class="form-control input-lg"
                     placeholder="Password">
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
            <select name="j_language" id="j_language" class="form-control"
                    style="border-bottom: 1px solid #eaedf1 !important;">
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

                  out.print("<option " + selected + " value=\"" + id + "\">" + lang.getName() + "</option>");
                }
              %>
            </select>
          </div>
          <div class="col-xs-4 pull-right">
            <button name="submit" type="submit" class="btn btn-sm btn-primary btn-block"><i class="fa fa-key"></i> Login
            </button>
          </div>
        </div>
        <% if (Config.SYSTEM_DEMO) { %>
        <div class="form-group low" style="background-color:white !important;">
          <div class="col-xs-12 hidden-lg">
            <jsp:include flush="true" page="login_demo_users.jsp"/>
          </div>
        </div>
        <% } %>
        <div class="form-group form-footer"
             style="border-bottom-left-radius: 10px !important; border-bottom-right-radius: 10px !important;">
          <div class="col-xs-12 text-center">
            <p>&copy; 2006-2018 OpenKM. All rights reserved.</p>
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
