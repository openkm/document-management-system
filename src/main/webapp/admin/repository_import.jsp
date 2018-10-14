<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.bean.Repository" %>
<%@ page import="com.openkm.core.Config" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ page import="com.openkm.util.FileUtils" %>
<%@ page import="com.openkm.util.FormatUtil" %>
<%@ page import="com.openkm.util.WebUtils" %>
<%@ page import="com.openkm.util.impexp.HTMLInfoDecorator" %>
<%@ page import="com.openkm.util.impexp.ImpExpStats" %>
<%@ page import="com.openkm.util.impexp.RepositoryImporter" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.FileNotFoundException" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.concurrent.TimeUnit" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/admin-style.css" />
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="js/jquery.DOMWindow.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
		$dm = $('.ds').openDOMWindow({
			height:300, width:400,
			eventType:'click',
			overlayOpacity:'57',
			windowSource:'iframe', windowPadding:0
		});
	});
    
    function dialogClose() {
		$dm.closeDOMWindow();
    }
    
    function keepSessionAlive() {
    	$.ajax({ type:'GET', url:'../SessionKeepAlive', cache:false, async:false });
    }
    
	window.setInterval('keepSessionAlive()', <%=TimeUnit.MINUTES.toMillis(Config.KEEP_SESSION_ALIVE_INTERVAL)%>);
  </script>
  <title>Repository Import</title>
</head>
<body>
<%! private static Logger log = LoggerFactory.getLogger("repository_import.jsp"); %>
<%
	if (BaseServlet.isMultipleInstancesAdmin(request)) {
		request.setCharacterEncoding("UTF-8");
		String repoPath = WebUtils.getString(request, "repoPath", "/" + Repository.ROOT);
		String fsPath = WebUtils.getString(request, "fsPath");
		boolean metadata = WebUtils.getBoolean(request, "metadata");
		boolean history = WebUtils.getBoolean(request, "history");
		boolean uuid = WebUtils.getBoolean(request, "uuid");
		
		out.println("<ul id=\"breadcrumb\">");
		out.println("  <li  class=\"path\"><a href=\"repository_import.jsp\">Repository import</a></li>");
		out.println("</ul>");
		out.println("<br/>");
		out.println("<form action=\"repository_import.jsp\">");
		out.println("<table class=\"form\" align=\"center\">");
		out.println("<tr>");
		out.println("<td>Filesystem path</td>");
		out.println("<td colspan=\"2\"><input type=\"text\" size=\"50\" name=\"fsPath\" id=\"fsPath\" value=\"" + fsPath + "\"></td>");
		out.println("<td><a class=\"ds\" href=\"../extension/DataBrowser?action=fs&sel=fld&dst=fsPath&root=" + Config.INSTANCE_CHROOT_PATH + "&path=" + fsPath + "\"><img src=\"img/action/browse_fs.png\"/></a></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td>Repository path</td>");
		out.println("<td colspan=\"2\"><input type=\"text\" size=\"50\" name=\"repoPath\" id=\"repoPath\" value=\"" + repoPath + "\" ></td>");
		out.println("<td><a class=\"ds\" href=\"../extension/DataBrowser?action=repo&sel=fld&dst=repoPath&path=" + repoPath +"\"><img src=\"img/action/browse_repo.png\"/></a></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td>Metadata</td>");
		out.println("<td colspan=\"2\"><input type=\"checkbox\" name=\"metadata\" " + (metadata?"checked":"") + "/></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td>History</td>");
		out.println("<td colspan=\"2\"><input type=\"checkbox\" name=\"history\" " + (history?"checked":"") + "/></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td>Restore UUIDs</td>");
		out.println("<td width=\"23px\"><input type=\"checkbox\" name=\"uuid\" " + (uuid?"checked":"") + "/></td>");
		out.println("<td><span style=\"color: red;\">(use with caution)</span></td>");
		out.println("</tr>");
		out.println("<tr><td colspan=\"4\" align=\"right\">");
		out.println("<input type=\"button\" onclick=\"javascript:window.history.back()\" value=\"Cancel\" class=\"noButton\"/>");
		out.println("<input type=\"submit\" value=\"Import\" class=\"yesButton\">");
		out.println("</td></tr>");
		out.println("</table>");
		out.println("</form>");

		try {
			if (repoPath != null && !repoPath.equals("") && fsPath != null && !fsPath.equals("")) {
				out.println("<hr/>");
				
				if (fsPath.startsWith(Config.INSTANCE_CHROOT_PATH)) {
					File dir = new File(fsPath);
					int files = FileUtils.countImportFiles(dir);
					out.println("<b>Files & directories to import:</b> "+files+"<br/>");
					long begin = System.currentTimeMillis();
					ImpExpStats stats = RepositoryImporter.importDocuments(null, dir, repoPath, metadata, history, uuid, out, 
						 new HTMLInfoDecorator(files));
					long end = System.currentTimeMillis();
					out.println("<hr/>");
					out.println("<div class=\"ok\">Filesystem '"+new File(fsPath).getAbsolutePath()+"' imported into '"+repoPath+"'</div>");
					out.println("<br/>");
					out.println("<b>Documents:</b> "+stats.getDocuments()+"<br/>");
					out.println("<b>Folders:</b> "+stats.getFolders()+"<br/>");
					out.println("<b>Mails:</b> "+stats.getMails()+"<br/>");
					out.println("<b>Size:</b> "+FormatUtil.formatSize(stats.getSize())+"<br/>");
					out.println("<b>Time:</b> "+FormatUtil.formatSeconds(end - begin)+"<br/>");
				} else {
					out.println("<div class=\"error\">Path out of root: "+Config.INSTANCE_CHROOT_PATH+"<div>");
				}
			}
		} catch (FileNotFoundException e) {
			out.println("<div class=\"error\">File Not Found: "+e.getMessage()+"<div>");
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			out.println("<div class=\"error\">IO Error: "+e.getMessage()+"<div>");
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			out.println("<div class=\"error\">Error: "+e.getMessage()+"<div>");
			log.error(e.getMessage(), e);
		}
	} else {
		out.println("<div class=\"error\"><h3>Only admin users allowed</h3></div>");
	}
%>
</body>
</html>