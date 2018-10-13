<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.bean.Repository" %>
<%@ page import="com.openkm.core.Config" %>
<%@ page import="com.openkm.kea.tree.KEATree" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="java.util.Vector" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="../css/dataTables-1.10.10/jquery.dataTables-1.10.10.min.css" />
  <link rel="stylesheet" type="text/css" href="../css/jquery-ui-1.10.3/jquery-ui-1.10.3.css" />  
  <link rel="stylesheet" type="text/css" href="../css/chosen.css"/>
	<link rel="stylesheet" type="text/css" href="css/admin-style.css" />
  <script type="text/javascript" src="../js/utils.js"></script>
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="../js/jquery-ui-1.10.3/jquery-ui-1.10.3.js"></script>
  <script type="text/javascript" src="../js/jquery.dataTables-1.10.10.min.js"></script>
  <script type="text/javascript" src="../js/chosen.jquery.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
	  $('select#level').chosen({disable_search_threshold: 10});
	  $('#results').dataTable({
	    "bStateSave": true,
	    "iDisplayLength": 15,
	    "lengthMenu": [[10, 15, 20], [10, 15, 20]],
	    "fnDrawCallback": function (oSettings) {
	      dataTableAddRows(this, oSettings);
	    }
      });
    });
  </script>
  <title>Generate thesaurus</title>
<body>
<%
	if (BaseServlet.isMultipleInstancesAdmin(request)) {
		request.setCharacterEncoding("UTF-8");
		String strLevel = request.getParameter("level");
		
		out.println("<ul id=\"breadcrumb\">");
		out.println("  <li class=\"path\"><a href=\"generate_thesaurus.jsp\">Generate thesaurus</a></li>");
		out.println("</ul>");
		out.println("<br/>");
				
        out.println("<div style=\"width:90%; margin-left:auto; margin-right:auto;\">");
        out.println("<table id=\"results\" class=\"results\">");
        out.println("<tr class=\"header\">");
		out.println("<td align=\"right\" colspan=\"2\">");
		out.println("<form action=\"generate_thesaurus.jsp\">");				
		out.println("Show level <select name=\"level\" id=\"level\" style=\"width: 50px\" data-placeholder=\"&nbsp;\">");
		
		for (int i=1; i<6; i++) {
			out.println("<option value=\"" + i + "\" " + (String.valueOf(i).equals(strLevel) ? "selected" : "") + ">" + i + "</option>");
		}
		
		out.println("</select>");
		out.println("<input type=\"submit\" value=\"Generate\" class=\"executeButton\">");		
		out.println("</form>");
		out.println("</td>");
		out.println("</tr>");
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
		out.println("</div>");

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