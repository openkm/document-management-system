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
  <title>Workflow Tokens</title>
</head>
<body>
   <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url value="Workflow" var="urlProcessDefinitionView">
        <c:param name="action" value="processDefinitionView"/>
        <c:param name="pdid" value="${token.processInstance.processDefinition.id}"/>
        <c:param name="statusFilter" value="${statusFilter}"/>
      </c:url>
      <c:url value="Workflow" var="urlProcessInstanceView">
        <c:param name="action" value="processInstanceView"/>
        <c:param name="piid" value="${token.processInstance.id}"/>
      </c:url>
      <c:url value="Workflow" var="urlTokenView">
        <c:param name="action" value="tokenView"/>
        <c:param name="tid" value="${token.id}"/>
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
        <li class="path">Token</li>
        <li class="action">
          <a href="${urlTokenView}">Refresh</a>
        </li>
      </ul>
      <br/>
      <table class="results" width="90%">
        <tr><th>Token ID</th><th>Current Node</th><th>Process Instance</th><th>Process Definition</th><th>Status</th><th>Start Date</th><th>End Date</th></tr>
        <tr class="even">
          <td>${token.id}</td><td>${token.node}</td>
          <td><a href="${urlProcessInstanceView}">${token.processInstance.id}</a></td>
          <td>
            <a href="${urlProcessDefinitionView}">
              ${token.processInstance.processDefinition.name} v${token.processInstance.processDefinition.version}
            </a>
          </td>
          <td>
            <b>
              <c:choose>
                <c:when test="${token.end != null && token.suspended}">Ended (suspended)</c:when>
                <c:when test="${token.end != null && !token.suspended}">Ended</c:when>
                <c:when test="${token.end == null && token.suspended}">Suspended</c:when>
                <c:when test="${token.end == null && !token.suspended}">Running</c:when>
              </c:choose>
            </b>
          </td>
          <td><u:formatDate calendar="${token.start}"/></td>
          <td><u:formatDate calendar="${token.end}"/></td>
        </tr>
      </table>
      <h2>Transitions</h2>
      <table class="results" width="90%">
        <tr><th>ID</th><th>Name</th><th>Target Node</th><th width="25px">Actions</th></tr>
        <c:forEach var="tr" items="${token.availableTransitions}" varStatus="row">
          <c:url value="Workflow" var="urlTokenSignal">
            <c:param name="action" value="tokenSignal"/>
            <c:param name="tid" value="${token.id}"/>
            <c:param name="transition" value="${tr.name}"/>
          </c:url>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${tr.id}</td><td>${tr.name}</td><td>${tr.to}</td>
            <td>
              <c:if test="${!token.suspended}">
                <a href="${urlTokenSignal}"><img src="img/action/signal.png" alt="Signal" title="Signal"/></a>
              </c:if>
            </td>
          </tr>
        </c:forEach>
      </table>
      <h2>Nodes</h2>
      <table class="results" width="90%">
        <tr><th>Name</th><th width="25px">Actions</th></tr>
        <c:forEach var="node" items="${token.processInstance.processDefinition.nodes}" varStatus="row">
          <c:url value="Workflow" var="urlTokenSetNode">
            <c:param name="action" value="tokenSetNode"/>
            <c:param name="tid" value="${token.id}"/>
            <c:param name="node" value="${node}"/>
          </c:url>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>
              <c:choose>
                <c:when test="${token.node == node}"><b>${node}</b></c:when>
                <c:otherwise>${node}</c:otherwise>
              </c:choose>
            </td>
            <td>
              <c:if test="${!token.suspended && token.node != node}">
                <a href="${urlTokenSetNode}"><img src="img/action/move.png" alt="Move to this node" title="Move to this node"/></a>
              </c:if>
            </td>
          </tr>
        </c:forEach>
      </table>
      <h2>Process Image</h2>
      <c:url value="WorkflowGraph" var="urlWorkflowGraph">
        <c:param name="id" value="${token.processInstance.processDefinition.id}"/>
        <c:param name="node" value="${token.node}"/>
      </c:url>
      <center><img src="${urlWorkflowGraph}"/></center>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>