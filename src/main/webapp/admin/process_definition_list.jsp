<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" href="css/style.css" type="text/css" />
  <script src="../js/jquery-1.7.1.min.js" type="text/javascript"></script>
  <title>Workflow Process Definition Browser</title>
  <script type="text/javascript">
    $(document).ready(function() {
    	$('a.confirm').click(function(e) {
    		e.preventDefault();
    		
    		if (confirm('Are you sure?')) {
    			window.location.href = $(this).attr('href');
    		}
    	});
    });
  </script>
</head>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <u:constantsMap className="com.openkm.core.Config" var="Config"/>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Workflow?action=processDefinitionList">Process definitions</a>
        </li>
        <li class="action">
          <a href="Workflow?action=processDefinitionList">
            <img src="img/action/refresh.png" alt="Refresh" title="Refresh" style="vertical-align: middle;"/>
            Refresh
          </a>
        </li>
      </ul>
      <br/>
      <c:choose>
        <c:when test="${Config.HIBERNATE_DIALECT == 'org.hibernate.dialect.HSQLDialect'}">
          <table border="0" cellpadding="5" cellspacing="3" align="center">
            <tr>
              <td style="background-color: #DE6611; font-weight: bold;">HSQL database is not compatible with workflow engine</td>
            </tr>
          </table>
        </c:when>
        <c:otherwise>
          <table class="results" width="90%">
            <tr><th>Process ID</th><th>Process Name</th><th>Version</th><th width="50px">Actions</th></tr>
            <c:forEach var="pd" items="${processDefinitions}" varStatus="row">
              <c:url value="Workflow" var="urlProcessDefinitionView">
                <c:param name="action" value="processDefinitionView"/>
                <c:param name="pdid" value="${pd.id}"/>
              </c:url>
              <c:url value="Workflow" var="urlProcessDefinitionDelete">
                <c:param name="action" value="processDefinitionDelete"/>
                <c:param name="pdid" value="${pd.id}"/>
              </c:url>
              <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
                <td>${pd.id}</td>
                <td>${pd.name}</td>
                <td>${pd.version}</td>
                <td>
                  <a href="${urlProcessDefinitionView}"><img src="img/action/examine.png" alt="Examine" title="Examine"/></a>
                  &nbsp;
                  <a class="confirm" href="${urlProcessDefinitionDelete}"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a>
                </td>
              </tr>
            </c:forEach>
            <tr class="fuzzy">
              <td colspan="5" align="right">
                <form action="RegisterWorkflow" method="post" enctype="multipart/form-data">
                  <table>
                    <tr>
                      <td><input class=":required :only_on_blur" type="file" name="definition"/></td>
                      <td><input type="submit" value="Register process definition" class="loadButton"/></td>
                    </tr>
                  </table>
                </form>
              </td>
            </tr>
          </table>
        </c:otherwise>
      </c:choose>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>