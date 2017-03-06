<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="bsh.Interpreter" %>
<%@ page import="com.openkm.core.Config" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ page import="com.openkm.util.FormatUtil" %>
<%@ page import="com.openkm.util.SecureStore" %>
<%@ page import="com.openkm.util.UserActivity" %>
<%@ page import="com.openkm.util.WebUtils" %>
<%@ page import="org.apache.commons.io.IOUtils" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.UUID" %>
<%@ page import="java.util.concurrent.TimeUnit" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="Shortcut icon" href="favicon.ico"/>
    <link rel="stylesheet" type="text/css" href="css/style.css"/>
    <link rel="stylesheet" type="text/css" href="js/codemirror/lib/codemirror.css"/>
    <link rel="stylesheet" type="text/css" href="js/codemirror/mode/clike/clike.css"/>
    <style type="text/css">
        .CodeMirror {
            width: 700px;
            height: 300px;
            background-color: #f8f6c2;
        }

        .activeline {
            background: #f0fcff !important;
        }
    </style>
    <script type="text/javascript" src="js/codemirror/lib/codemirror.js"></script>
    <script type="text/javascript" src="js/codemirror/mode/clike/clike.js"></script>
    <script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
    <script type="text/javascript" src="js/jquery.DOMWindow.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            cm = CodeMirror.fromTextArea(document.getElementById('script'), {
                lineNumbers: true,
                matchBrackets: true,
                indentUnit: 4,
                mode: "text/x-java",
                onCursorActivity: function () {
                    cm.setLineClass(hlLine, null);
                    hlLine = cm.setLineClass(cm.getCursor().line, "activeline");
                }
            });

            hlLine = cm.setLineClass(0, "activeline");
            var width = $(window).width() - 60;
            var height = $(window).height() - 230;
            $('.CodeMirror').css({"width": width});
            $('.CodeMirror').css({"height": height});

            $dm = $('.ds').openDOMWindow({
                height: 300, width: 400,
                eventType: 'click',
                overlayOpacity: '57',
                windowSource: 'iframe', windowPadding: 0
            });
        });

        function dialogClose() {
            $dm.closeDOMWindow();
        }

        function keepSessionAlive() {
            $.ajax({type: 'GET', url: '../SessionKeepAlive', cache: false, async: false});
        }

        window.setInterval('keepSessionAlive()', <%=TimeUnit.MINUTES.toMillis(Config.KEEP_SESSION_ALIVE_INTERVAL)%>);
  </script>
  <title>Scripting</title>
</head>
<body>
<%
	if (BaseServlet.isMultipleInstancesAdmin(request)) {
		String action = WebUtils.getString(request, "action");
		String script = WebUtils.getString(request, "script");
		String fsPath = WebUtils.getString(request, "fsPath");
		String reqCsrft = WebUtils.getString(request, "csrft");
		String sesCsrft = (String) session.getAttribute("csrft");
		StringBuffer scriptOutput = new StringBuffer();
		Object scriptResult = null;
		Exception scriptError = null;
		long begin = System.currentTimeMillis();
		
		if (action.equals("Load") && !fsPath.equals("")) {
			if (reqCsrft.equals(sesCsrft)) {
				File file = new File(fsPath);
				FileInputStream fis = new FileInputStream(file);
				script = IOUtils.toString(fis);
				IOUtils.closeQuietly(fis);
			} else {
				out.println("<div class=\"error\"><h3>Security risk detected</h3></div>");
				UserActivity.log(request.getRemoteUser(), "ADMIN_SECURITY_RISK", request.getRemoteHost(), null, script);
			}
		} else if (action.equals("Save") && !fsPath.equals("") && !script.equals("")) {
			if (reqCsrft.equals(sesCsrft)) {
				File file = new File(fsPath);
				FileOutputStream fos = new FileOutputStream(file);
				IOUtils.write(script, fos);
				IOUtils.closeQuietly(fos);
			} else {
				out.println("<div class=\"error\"><h3>Security risk detected</h3></div>");
				UserActivity.log(request.getRemoteUser(), "ADMIN_SECURITY_RISK", request.getRemoteHost(), null, script);
			}
		} else if (action.equals("Evaluate") && !script.equals("")) {
			if (reqCsrft.equals(sesCsrft)) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream pout = new PrintStream(baos);
				Interpreter bsh = new Interpreter(null, pout, pout, false);
				
				// set up interpreter
				bsh.set("bsh.httpServletRequest", request);
				bsh.set("bsh.httpServletResponse", response);
				
				try {
					scriptResult = bsh.eval(script);
				} catch (Exception e) {
					scriptError = e;
				}
				
				pout.flush();
				scriptOutput.append(baos.toString());
				
				// Activity log
				UserActivity.log(request.getRemoteUser(), "ADMIN_SCRIPTING", request.getRemoteHost(), null, script);
			} else {
				out.println("<div class=\"error\"><h3>Security risk detected</h3></div>");
				UserActivity.log(request.getRemoteUser(), "ADMIN_SECURITY_RISK", request.getRemoteHost(), null, script);
			}
		} else {
			script = "print(\"Hola, mundo!\");";		
		}
		
		String genCsrft = SecureStore.md5Encode(UUID.randomUUID().toString().getBytes());
		session.setAttribute("csrft", genCsrft);
		out.println("<ul id=\"breadcrumb\">");
		out.println("<li class=\"path\"><a href=\"scripting.jsp\">Scripting</a></li>");
		out.println("</ul>");
		out.println("<br/>");
		out.println("<form action=\"scripting.jsp\" method=\"post\">");
		out.println("<input type=\"hidden\" name=\"csrft\" value=\"" + genCsrft + "\">");
		out.println("<table class=\"form\" align=\"center\">");
		out.println("<tr><td colspan=\"4\"><textarea cols=\"80\" rows=\"25\" name=\"script\" id=\"script\">"+script+"</textarea></td></tr>");
		out.println("<tr>");
		out.println("<td align=\"left\" width=\"125px\">");
		out.println("<input type=\"text\" size=\"50\" name=\"fsPath\" id=\"fsPath\" value=\"" + fsPath + "\" >");
		out.println("</td>");
		out.println("<td align=\"left\">");
		out.println("<a class=\"ds\" href=\"../extension/DataBrowser?action=fs&dst=fsPath\"><img src=\"img/action/browse_fs.png\"/></a>");
		out.println("</td>");
		out.println("<td align=\"left\">");
		out.println("<input type=\"submit\" name=\"action\" value=\"Load\" class=\"loadButton\">");
		out.println("<input type=\"submit\" name=\"action\" value=\"Save\" class=\"saveButton\">");
		out.println("</td>");
		out.println("<td align=\"right\"><input type=\"submit\" name=\"action\" value=\"Evaluate\" class=\"executeButton\"></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<br/>");
		out.println("<div class=\"ok\">");
		out.println("<center>");
		out.println("Time: " + FormatUtil.formatMiliSeconds(System.currentTimeMillis() - begin));
		out.println("</center>");
		out.println("</div>");
		out.println("<br/>");
		out.println("<table class=\"results\" width=\"95%\">");
		out.println("<tr><th>Script error</th></tr><tr class=\"odd\"><td>"+(scriptError==null?"":scriptError)+"</td></tr>");
		out.println("<tr><th>Script result</th></tr><tr class=\"odd\"><td>"+(scriptResult==null?"":scriptResult)+"</td></tr>");
		out.println("<tr><th>Script output</th></tr><tr class=\"odd\"><td>"+scriptOutput+"</td></tr>");
		out.println("</table>");
		out.println("</form>");
	} else {
		out.println("<div class=\"error\"><h3>Only admin users allowed</h3></div>");
	}
%>
</body>
</html>