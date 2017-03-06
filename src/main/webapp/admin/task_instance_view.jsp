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
  <title>Workflow Task Instance View</title>
</head>
<body>
   <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url value="Workflow" var="urlProcessDefinitionView">
        <c:param name="action" value="processDefinitionView"/>
        <c:param name="pdid" value="${taskInstance.processInstance.processDefinition.id}"/>
        <c:param name="statusFilter" value="${statusFilter}"/>
      </c:url>
      <c:url value="Workflow" var="urlProcessInstanceView">
        <c:param name="action" value="processInstanceView"/>
        <c:param name="piid" value="${taskInstance.processInstance.id}"/>
      </c:url>
      <c:url value="Workflow" var="urlTaskInstanceView">
        <c:param name="action" value="taskInstanceView"/>
        <c:param name="tiid" value="${taskInstance.id}"/>
      </c:url>
      <ul id="breadcrumb">
        <li class="path">
          <a href="Workflow?action=processDefinitionList">Process definitions</a>
        </li>
        <li class="path">
          <a href="${urlProcessDefinitionView}">Process definition</a>
        </li>
        <li class="path">
          <a href="${urlProcessInstanceView}">Process instance</a>
        </li>
        <li class="path">Task instance</li>
        <li class="action">
          <a href="${urlTaskInstanceView}">
            <img src="img/action/refresh.png" alt="Refresh" title="Refresh" style="vertical-align: middle;"/>
            Refresh
          </a>
        </li>
      </ul>
      <br/>
      <c:url value="Workflow" var="urlTokenView">
        <c:param name="action" value="tokenView"/>
        <c:param name="tid" value="${taskInstance.token.id}"/>
      </c:url>
      <c:url value="Workflow" var="urlProcessInstanceView">
        <c:param name="action" value="processInstanceView"/>
        <c:param name="piid" value="${taskInstance.processInstance.id}"/>
      </c:url>
      <c:url value="Workflow" var="urlProcessDefinitionView">
        <c:param name="action" value="processDefinitionView"/>
        <c:param name="pdid" value="${taskInstance.processInstance.processDefinition.id}"/>
      </c:url>
      <table class="results" width="90%">
        <tr><th>Task ID</th><th>Name</th><th>Status</th><th>Assigned To</th><th>Token</th><th>Process Instance</th><th>Process</th><th>Creation Date</th></tr>
        <tr class="even">
          <td>${taskInstance.id}</td><td>${taskInstance.name}</td>
          <td>
            <b>
              <c:choose>
                <c:when test="${taksInstance.end != null && taksInstance.suspended}">Ended (suspended)</c:when>
                <c:when test="${taksInstance.end != null && !taksInstance.suspended}">Ended</c:when>
                <c:when test="${taksInstance.end == null && taskInstance.start == null && taksInstance.suspended}">Not started (suspended)</c:when>
                <c:when test="${taksInstance.end == null && taskInstance.start == null && !taksInstance.suspended}">Not started</c:when>
                <c:when test="${taksInstance.end == null && taskInstance.start != null && taksInstance.suspended}">Suspended</c:when>
                <c:when test="${taksInstance.end == null && taskInstance.start != null && !taksInstance.suspended}">Running</c:when>
              </c:choose>
            </b>
          </td>
          <td>${taskInstance.actorId}</td>
          <td><a href="${urlTokenView}">${taskInstance.token.id}</a></td>
          <td><a href="${urlProcessInstanceView}">${taskInstance.processInstance.id}</a></td>
          <td>
            <a href="${urlProcessDefinitionView}">
              ${taskInstance.processInstance.processDefinition.name} v${taskInstance.processInstance.processDefinition.version}
            </a>
          </td>
          <td><u:formatDate calendar="${taskInstance.create}"/></td>
        </tr>
      </table>
      <h2>Task Form</h2>
      <table class="results" width="90%">
        <tr><th>Label</th><th>Name</th><th>Width</th><th>Height</th><th>Field</th><th>Others</th></tr>
        <c:forEach var="fe" items="${taskInstanceForm}" varStatus="row">
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
      <h2>Comments</h2>
      <table class="results" width="90%">
        <tr><th>Actor ID</th><th>Time</th><th>Comment</th></tr>
        <c:forEach var="cmt" items="${taskInstance.comments}" varStatus="row">
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${cmt.actorId}</td><td><u:formatDate calendar="${cmt.time}"/></td><td>${cmt.message}</td>
          </tr>
        </c:forEach>
      </table>
      <br/>
      <form action="Workflow">
        <input type="hidden" name="action" value="taskInstanceAddComment"/>
        <input type="hidden" name="tiid" value="${taskInstance.id}"/>
        <table class="form">
          <tr><td><textarea name="message" cols="50" rows="5"></textarea></td></tr>
          <tr><td align="right"><input type="submit" value="Add comment"/></td></tr>
        </table>
      </form>
      <h2>Process Variables</h2>
      <table class="results" width="90%">
        <tr><th>Name</th><th>Value</th><th width="25px">Actions</th></tr>
        <c:forEach var="var" items="${variables}" varStatus="row">
          <c:url value="Workflow" var="urlTaskInstanceVariableDelete">
            <c:param name="action" value="taskInstanceVariableDelete"/>
            <c:param name="tiid" value="${taskInstance.id}"/>
            <c:param name="name" value="${var.key}"/>
          </c:url>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${var.key}</td><td>${var.value}</td>
            <td>
              <a href="${urlTaskInstanceVariableDelete}"><img src="img/action/delete.png" alt="Remove" title="Remove"/></a>
            </td>
          </tr>
        </c:forEach>
      </table>
      <br/>
      <form action="Workflow">
        <input type="hidden" name="action" value="taskInstanceVariableAdd"/>
        <input type="hidden" name="tiid" value="${taskInstance.id}"/>
        <table class="form">
          <tr>
            <td>Name <input type="text" name="name"/></td>
            <td>Value <input type="text" name="value"/></td>
          </tr>
          <tr><td colspan="2" align="right"><input type="submit" value="Add variable"/></td></tr>
        </table>
      </form>
      <h2>Transitions</h2>
      <table class="results" width="90%">
        <tr><th>ID</th><th>Name</th><th>Target Node</th><th width="25px">Actions</th></tr>
        <c:forEach var="tr" items="${taskInstance.availableTransitions}" varStatus="row">
          <c:url value="Workflow" var="urlTaskInstanceEnd">
            <c:param name="action" value="taskInstanceEnd"/>
            <c:param name="tiid" value="${taskInstance.id}"/>
            <c:param name="transition" value="${tr.name}"/>
          </c:url>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${tr.id}</td><td>${tr.name}</td><td>${tr.to}</td>
            <td>
              <c:if test="${!taskInstance.suspended}">
                <a href="${urlTaskInstanceEnd}"><img src="img/action/end.png" alt="End task" title="End task"/></a>
              </c:if>
            </td>
          </tr>
        </c:forEach>
      </table>
      <h2>Process Image</h2>
      <c:url value="WorkflowGraph" var="urlWorkflowGraph">
        <c:param name="id" value="${taskInstance.processInstance.processDefinition.id}"/>
        <c:param name="node" value="${taskInstance.token.node}"/>
      </c:url>
      <center><img src="${urlWorkflowGraph}"/></center>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>