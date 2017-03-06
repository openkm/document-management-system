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
  <title>Workflow Process Instances View</title>
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
      <c:url value="Workflow" var="urlProcessInstanceView">
        <c:param name="action" value="processInstanceView"/>
        <c:param name="piid" value="${processInstance.id}"/>
      </c:url>
      <ul id="breadcrumb">
        <li class="path">
          <a href="Workflow?action=processDefinitionList">Process definitions</a>
        </li>
        <li class="path">
          <a href="${urlProcessDefinitionView}">Process definition</a>
        </li>
        <li class="path">Process instance</li>
        <li class="action">
          <a href="${urlProcessInstanceView}">
            <img src="img/action/refresh.png" alt="Refresh" title="Refresh" style="vertical-align: middle;"/>
            Refresh
          </a>
        </li>
      </ul>
      <br/>
      <table class="results" width="90%">
        <tr><th>Instance ID</th><th>Key</th><th>Process</th><th>Status</th><th>Start Date</th><th>End Date</th></tr>
        <tr class="even">
          <td>${processInstance.id}</td><td>${processInstance.key}</td>
          <td>
            <a href="${urlProcessDefinitionView}">
              ${processInstance.processDefinition.name} v${processInstance.processDefinition.version}
            </a>
          </td>
          <td>
            <b>
              <c:choose>
                <c:when test="${processInstance.end != null && processInstance.suspended}">Ended (suspended)</c:when>
                <c:when test="${processInstance.end != null && !processInstance.suspended}">Ended</c:when>
                <c:when test="${processInstance.end == null && processInstance.suspended}">Suspended</c:when>
                <c:when test="${processInstance.end == null && !processInstance.suspended}">Running</c:when>
              </c:choose>
            </b>
          </td>
          <td><u:formatDate calendar="${processInstance.start}"/></td>
          <td><u:formatDate calendar="${processInstance.end}"/></td>
        </tr>
      </table>
      <h2>Tasks Instances</h2>
      <table class="results" width="90%">
        <tr><th>ID</th><th>Name</th><th>Pooled Actors</th><th>Assigned To</th><th>Status</th><th>Start Date</th><th>End Date</th><th width="75px">Actions</th></tr>
        <c:forEach var="ti" items="${taskInstances}" varStatus="row">
          <c:url value="Workflow" var="urlTaskInstanceView">
            <c:param name="action" value="taskInstanceView"/>
            <c:param name="tiid" value="${ti.id}"/>
          </c:url>
          <c:url value="Workflow" var="urlTaskInstanceSuspend">
            <c:param name="action" value="taskInstanceSuspend"/>
            <c:param name="piid" value="${processInstance.id}"/>
            <c:param name="tiid" value="${ti.id}"/>
          </c:url>
          <c:url value="Workflow" var="urlTaskInstanceResume">
            <c:param name="action" value="taskInstanceResume"/>
            <c:param name="piid" value="${processInstance.id}"/>
            <c:param name="tiid" value="${ti.id}"/>
          </c:url>
          <c:url value="Workflow" var="urlTaskInstanceStart">
            <c:param name="action" value="taskInstanceStart"/>
            <c:param name="piid" value="${processInstance.id}"/>
            <c:param name="tiid" value="${ti.id}"/>
          </c:url>
          <c:url value="Workflow" var="urlTaskInstanceEnd">
            <c:param name="action" value="taskInstanceEnd"/>
            <c:param name="piid" value="${processInstance.id}"/>
            <c:param name="tiid" value="${ti.id}"/>
          </c:url>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${ti.id}</td><td>${ti.name}</td><td>${ti.pooledActors}</td>
            <td>
              <c:choose>
                <c:when test="${ti.end != null}">${ti.actorId}</c:when>
                <c:otherwise>
                  <form id="setActor" action="Workflow">
                    <input type="hidden" name="action" value="taskInstanceSetActor"/>
                    <input type="hidden" name="piid" value="${processInstance.id}"/>
                    <input type="hidden" name="tiid" value="${ti.id}"/>
                    <select name="actor" onchange="document.getElementById('setActor').submit()">
                      <option>-</option>
					  <c:forEach var="user" items="${users}">
					    <c:choose>
					      <c:when test="${user == ti.actorId}">
					        <option selected="selected">${user}</option>
					      </c:when>
					      <c:otherwise>
					        <option>${user}</option>
					      </c:otherwise>
					    </c:choose>
					  </c:forEach>
                    </select>
                  </form>
                </c:otherwise>
              </c:choose>
            </td>
            <td>
              <b>
                <c:choose>
                  <c:when test="${ti.end != null && ti.suspended}">Ended (suspended)</c:when>
                  <c:when test="${ti.end != null && !ti.suspended}">Ended</c:when>
                  <c:when test="${ti.end == null && ti.start == null && !ti.suspended}">Not Started</c:when>
                  <c:when test="${ti.end == null && ti.start == null && ti.suspended}">Not Started (suspended)</c:when>
                  <c:when test="${ti.end == null && ti.start != null && !ti.suspended}">Running</c:when>
                  <c:when test="${ti.end == null && ti.start != null && ti.suspended}">Suspended</c:when>
                </c:choose>
              </b>
            </td>
            <td><u:formatDate calendar="${ti.start}"/></td>
            <td><u:formatDate calendar="${ti.end}"/></td>
            <td>
              <a href="${urlTaskInstanceView}"><img src="img/action/examine.png" alt="Examine" title="Examine"/></a>
              <c:if test="${!ti.suspended && ti.end == null}">
                &nbsp;
                <a href="${urlTaskInstanceSuspend}"><img src="img/action/suspend.png" alt="Suspend" title="Suspend"/></a>
              </c:if>
              <c:if test="${ti.suspended && ti.end == null}">
                &nbsp;
                <a href="${urlTaskInstanceResume}"><img src="img/action/resume.png" alt="Resume" title="Resume"/></a>
              </c:if>
              <c:if test="${ti.start == null && ti.end == null}">
                &nbsp;
                <a href="${urlTaskInstanceStart}"><img src="img/action/start.png" alt="Start" title="Start"/></a>
              </c:if>
              <c:if test="${ti.start != null && ti.end == null && !ti.suspended}">
                &nbsp;
                <a href="${urlTaskInstanceEnd}"><img src="img/action/end.png" alt="End" title="End"/></a>
              </c:if>
            </td>
          </tr>
        </c:forEach>
      </table>
      <h2>Comments</h2>
      <table class="results" width="90%">
        <tr><th>Actor ID</th><th>Time</th><th>Comment</th></tr>
        <c:forEach var="cmt" items="${processInstance.rootToken.comments}" varStatus="row">
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${cmt.actorId}</td><td><u:formatDate calendar="${cmt.time}"/></td><td>${cmt.message}</td>
          </tr>
        </c:forEach>
      </table>
      <br/>
      <form action="Workflow">
        <input type="hidden" name="action" value="processInstanceAddComment"/>
        <input type="hidden" name="piid" value="${processInstance.id}"/>
        <input type="hidden" name="tid" value="${processInstance.rootToken.id}"/>
        <table class="form">
          <tr><td><textarea name="message" cols="50" rows="5"></textarea></td></tr>
          <tr><td align="right"><input type="submit" value="Add comment"/></td></tr>
        </table>
      </form>
      <h2>Tokens</h2>
      <table class="results" width="90%">
        <tr><th>Token ID</th><th>Parent</th><th>Node</th><th>Status</th><th>Start Date</th><th>End Date</th><th width="75px">Actions</th></tr>
        <c:forEach var="tk" items="${processInstance.allTokens}" varStatus="row">
          <c:url value="Workflow" var="urlTokenView">
            <c:param name="action" value="tokenView"/>
            <c:param name="tid" value="${tk.id}"/>
          </c:url>
          <c:url value="Workflow" var="urlTokenEnd">
            <c:param name="action" value="tokenEnd"/>
            <c:param name="pdid" value="${processInstance.processDefinition.id}"/>
            <c:param name="piid" value="${processInstance.id}"/>
            <c:param name="tid" value="${tk.id}"/>
          </c:url>
          <c:url value="Workflow" var="urlTokenSuspend">
            <c:param name="action" value="tokenSuspend"/>
            <c:param name="pdid" value="${processInstance.processDefinition.id}"/>
            <c:param name="piid" value="${processInstance.id}"/>
            <c:param name="tid" value="${tk.id}"/>
          </c:url>
          <c:url value="Workflow" var="urlTokenResume">
            <c:param name="action" value="tokenResume"/>
            <c:param name="pdid" value="${processInstance.processDefinition.id}"/>
            <c:param name="piid" value="${processInstance.id}"/>
            <c:param name="tid" value="${tk.id}"/>
          </c:url>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${tk.id}</td><td>${tk.parent}</td><td>${tk.node}</td>
            <td>
              <b>
                <c:choose>
                  <c:when test="${tk.end != null && tk.suspended}">Ended (suspended)</c:when>
                  <c:when test="${tk.end != null && !tk.suspended}">Ended</c:when>
                  <c:when test="${tk.end == null && tk.suspended}">Suspended</c:when>
                  <c:when test="${tk.end == null && !tk.suspended}">Running</c:when>
                </c:choose>
              </b>
            </td>
            <td><u:formatDate calendar="${tk.start}"/></td>
            <td><u:formatDate calendar="${tk.end}"/></td>
            <td>
              <a href="${urlTokenView}"><img src="img/action/examine.png" alt="Examine" title="Examine"/></a>
              <c:if test="${tk.end == null}">
                &nbsp;
                <a href="${urlTokenEnd}"><img src="img/action/end.png" alt="End" title="End"/></a>
              </c:if>
              <c:choose>
                <c:when test="${tk.suspended}">
                  &nbsp;
                  <a href="${urlTokenResume}"><img src="img/action/resume.png" alt="Resume" title="Resume"/></a>
                </c:when>
                <c:otherwise>
                  &nbsp;
                  <a href="${urlTokenSuspend}"><img src="img/action/suspend.png" alt="Suspend" title="Suspend"/></a>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
        </c:forEach>
      </table>
      <h2>Process Variables</h2>
      <table class="results" width="90%">
        <tr><th>Name</th><th>Value</th><th width="25px">Actions</th></tr>
        <c:forEach var="var" items="${variables}" varStatus="row">
          <c:url value="Workflow" var="urlProcessInstanceVariableDelete">
            <c:param name="action" value="processInstanceVariableDelete"/>
            <c:param name="piid" value="${processInstance.id}"/>
            <c:param name="name" value="${var.key}"/>
          </c:url>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${var.key}</td><td>${var.value}</td>
            <td>
              <a class="confirm" href="${urlProcessInstanceVariableDelete}"><img src="img/action/delete.png" alt="Remove" title="Remove"/></a>
            </td>
          </tr>
        </c:forEach>
      </table>
      <br/>
      <form action="Workflow">
        <input type="hidden" name="action" value="processInstanceVariableAdd"/>
        <input type="hidden" name="piid" value="${processInstance.id}"/>
        <table class="form">
          <tr>
            <td>Name <input type="text" name="name"/></td>
            <td>Value <input type="text" name="value"/></td>
          </tr>
          <tr><td colspan="2" align="right"><input type="submit" value="Add variable"/></td></tr>
        </table>
      </form>
      <h2>Process Image</h2>
      <c:url value="WorkflowGraph" var="urlWorkflowGraph">
        <c:param name="id" value="${processInstance.processDefinition.id}"/>
        <c:param name="node" value="${processInstance.rootToken.node}"/>
      </c:url>
      <center><img src="${urlWorkflowGraph}"/></center>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>