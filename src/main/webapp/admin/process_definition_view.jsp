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
  <title>Workflow Process Definition</title>
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
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url value="Workflow" var="urlProcessDefinitionView">
        <c:param name="action" value="processDefinitionView"/>
        <c:param name="pdid" value="${processDefinition.id}"/>
        <c:param name="statusFilter" value="${statusFilter}"/>
      </c:url>
      <ul id="breadcrumb">
        <li class="path">
          <a href="Workflow?action=processDefinitionList">Process definitions</a>
        </li>
        <li class="path">Process definition</li>
        <li class="action">
          <a href="${urlProcessDefinitionView}">
            <img src="img/action/refresh.png" alt="Refresh" title="Refresh" style="vertical-align: middle;"/>
            Refresh
          </a>
        </li>
      </ul>
      <br/>
      <table class="results" width="90%">
        <tr><th>Process ID</th><th>Name</th><th>Description</th><th>Version</th></tr>
        <tr class="even">
          <td>${processDefinition.id}</td><td>${processDefinition.name}</td>
          <td>${processDefinition.description}</td><td>${processDefinition.version}</td>
        </tr>
      </table>
      <table>
        <tr>
          <td><h2>Process Instances</h2></td>
          <td>
            <form id="filter" action="Workflow">
                <input type="hidden" name="action" value="processDefinitionView"/>
                <input type="hidden" name="pdid" value="${processDefinition.id}"/>
              - Status: <select name="statusFilter" onchange="document.getElementById('filter').submit()">
                <c:forEach var="statusFilterValue" items="${statusFilterValues}">
                  <c:choose>
                    <c:when test="${statusFilterValue.key eq statusFilter}">
                      <option value="${statusFilterValue.key}" selected="selected">${statusFilterValue.value}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${statusFilterValue.key}">${statusFilterValue.value}</option>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </select>
            </form>
          </td>
        </tr>
      </table>
      <table class="results" width="90%">
        <tr><th>Instance ID</th><th>Key</th><th>Status</th><th>Start Date</th><th>End Date</th><th width="100px">Actions</th></tr>
        <c:forEach var="pi" items="${processInstances}" varStatus="row">
          <c:url value="Workflow" var="urlProcessInstanceView">
            <c:param name="action" value="processInstanceView"/>
            <c:param name="piid" value="${pi.id}"/>
          </c:url>
          <c:url value="Workflow" var="urlProcessInstanceDelete">
            <c:param name="action" value="processInstanceDelete"/>
            <c:param name="pdid" value="${processDefinition.id}"/>
            <c:param name="piid" value="${pi.id}"/>
          </c:url>
          <c:url value="Workflow" var="urlProcessInstanceEnd">
            <c:param name="action" value="processInstanceEnd"/>
            <c:param name="pdid" value="${processDefinition.id}"/>
            <c:param name="piid" value="${pi.id}"/>
          </c:url>
          <c:url value="Workflow" var="urlProcessInstanceResume">
            <c:param name="action" value="processInstanceResume"/>
            <c:param name="pdid" value="${processDefinition.id}"/>
            <c:param name="piid" value="${pi.id}"/>
          </c:url>
          <c:url value="Workflow" var="urlProcessInstanceSuspend">
            <c:param name="action" value="processInstanceSuspend"/>
            <c:param name="pdid" value="${processDefinition.id}"/>
            <c:param name="piid" value="${pi.id}"/>
          </c:url>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${pi.id}</td>
            <td>${pi.key}</td>
            <td>
              <b>
                <c:choose>
                  <c:when test="${pi.end != null && pi.suspended}">Ended (suspended)</c:when>
                  <c:when test="${pi.end != null && !pi.suspended}">Ended</c:when>
                  <c:when test="${pi.end == null && pi.suspended}">Suspended</c:when>
                  <c:when test="${pi.end == null && !pi.suspended}">Running</c:when>
                </c:choose>
              </b>
            </td>
            <td><u:formatDate calendar="${pi.start}"/></td>
            <td><u:formatDate calendar="${pi.end}"/></td>
            <td>
              <a href="${urlProcessInstanceView}"><img src="img/action/examine.png" alt="Examine" title="Examine"/></a>
              &nbsp;
              <a class="confirm" href="${urlProcessInstanceDelete}"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a>
              <c:if test="${pi.end == null}">
                &nbsp;
                <a href="${urlProcessInstanceEnd}"><img src="img/action/end.png" alt="End" title="End"/></a>
              </c:if>
              &nbsp;
              <c:choose>
                <c:when test="${pi.suspended}">
                  <a href="${urlProcessInstanceResume}"><img src="img/action/resume.png" alt="Resume" title="Resume"/></a>
                </c:when>
                <c:otherwise>
                  <a href="${urlProcessInstanceSuspend}"><img src="img/action/suspend.png" alt="Suspend" title="Suspend"/></a>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
        </c:forEach>
      </table>
      <h2>Forms</h2>
      <table class="results" width="90%">
        <tr><th>Task</th><th>Form</th></tr>
        <c:forEach var="pdf" items="${processDefinitionForms}" varStatus="row">
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${pdf.key}</td>
            <td>
              <table class="results" width="100%">
                <tr><th>Label</th><th>Name</th><th>Width</th><th>Height</th><th>Field</th><th>Others</th></tr>
                <c:forEach var="fe" items="${pdf.value}" varStatus="row">
                  <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
                    <td>${fe.label}</td>
                    <td>${fe.name}</td>
                    <td>${fe.width}</td>
                    <td>${fe.height}</td>
                    <td>${fe.field}</td>
                    <td>${fe.others}</td>
                  </tr>
                </c:forEach>
              </table>
            </td>
          </tr>
        </c:forEach>
      </table>
      <h2>Process Image</h2>
      <c:url value="WorkflowGraph" var="urlWorkflowGraph">
        <c:param name="id" value="${processDefinition.id}"/>
      </c:url>
      <center><img src="${urlWorkflowGraph}"/></center>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>