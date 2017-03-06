<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.bean.Repository" %>
<%@ page import="com.openkm.core.Config" %>
<%@ page import="com.openkm.kea.tree.KEATree" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="java.util.Vector" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="Shortcut icon" href="favicon.ico"/>
    <link rel="stylesheet" type="text/css" href="css/style.css"/>
    <title>Generate thesaurus</title>
</head>
<body>
<%
	if (BaseServlet.isMultipleInstancesAdmin(request)) {
		request.setCharacterEncoding("UTF-8");
		String strLevel = request.getParameter("level");
		
		out.println("<ul id=\"breadcrumb\">");
		out.println("  <li class=\"path\"><a href=\"generate_thesaurus.jsp\">Generate thesaurus</a></li>");
		out.println("</ul>");
		out.println("<br/>");
		out.println("<form action=\"generate_thesaurus.jsp\">");
		out.println("<table class=\"form\" width=\"125px\">");
		out.println("<tr><td>");
		out.println("Show level <select name=\"level\">");
		
		for (int i=1; i<6; i++) {
			out.println("<option value=\"" + i + "\" " + (String.valueOf(i).equals(strLevel) ? "selected" : "") + ">" + i + "</option>");
		}
		
		out.println("</select>");
		out.println("</td></tr>");
		out.println("<tr><td align=\"right\">");
		out.println("<input type=\"submit\" value=\"Generate\" class=\"executeButton\">");
		out.println("</td></tr>");
		out.println("</table>");
		out.println("</form>");
		out.println("<br/>");
		out.println("<table class=\"results\" width=\"90%\">");
		out.println("<tr><th>Parameter</th><th>Value</th></tr>");
		
		out.println("<tr class=\"even\">");
		out.println("<td><b>" + Config.PROPERTY_KEA_THESAURUS_SKOS_FILE + "</b></td>");
		out.println("<td>" + Config.KEA_THESAURUS_SKOS_FILE + "</td>");
		out.println("</tr>");
		
		out.println("<tr class=\"odd\">");
		out.println("<td><b>" + Config.PROPERTY_KEA_THESAURUS_OWL_FILE + "</b></td>");
		out.println("<td>" + Config.KEA_THESAURUS_OWL_FILE + "</td>");
		out.println("</tr>");
		
		out.println("<tr class=\"even\">");
		out.println("<td><b>" + Config.PROPERTY_KEA_THESAURUS_VOCABULARY_SERQL + "</b></td>");
		out.println("<td>" + StringEscapeUtils.escapeHtml(Config.KEA_THESAURUS_VOCABULARY_SERQL) + "</td>");
		out.println("</tr>");
		
		out.println("<tr class=\"odd\">");
		out.println("<td><b>" + Config.PROPERTY_KEA_THESAURUS_BASE_URL + "</b></td>");
		out.println("<td>" + Config.KEA_THESAURUS_BASE_URL + "</td>");
		out.println("</tr>");
		
		out.println("<tr class=\"even\">");
		out.println("<td><b>" + Config.PROPERTY_KEA_THESAURUS_TREE_ROOT + "</b></td>");
		out.println("<td>" + StringEscapeUtils.escapeHtml(Config.KEA_THESAURUS_TREE_ROOT) + "</td>");
		out.println("</tr>");
		
		out.println("<tr class=\"odd\">");
		out.println("<td><b>" + Config.PROPERTY_KEA_THESAURUS_TREE_CHILDS + "</b></td>");
		out.println("<td>" + StringEscapeUtils.escapeHtml(Config.KEA_THESAURUS_TREE_CHILDS) + "</td>");
		out.println("</tr>");
		
		out.println("</table>");

		try {
			if (!Config.KEA_THESAURUS_OWL_FILE.equals("")) {
				if (strLevel != null && !strLevel.equals("")) {
					out.println("<hr/>");
					int level = Integer.parseInt(strLevel);
					long begin = System.currentTimeMillis();
					KEATree.generateTree(null, level, "/" + Repository.THESAURUS, new Vector<String>(), out);
					long end = System.currentTimeMillis();
					out.println("<hr/>");
					out.println("<div class=\"ok\">Level '" + level + "'</div>");
				}
			} else {
				out.println("<hr/>");
				out.println("<div class=\"warn\">Warning: " + Config.PROPERTY_KEA_THESAURUS_OWL_FILE + " is empty</div>");
			}
		} catch (Exception e) {
			out.println("<div class=\"error\">" + e.getMessage() + "<div>");
		}
	} else {
		out.println("<div class=\"error\"><h3>Only admin users allowed</h3></div>");
	}
%>
</body>
</html>