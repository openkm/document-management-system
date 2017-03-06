<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <link rel="stylesheet" type="text/css" href="css/fixedTableHeader.css" />
  <script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
  <script type="text/javascript" src="js/fixedTableHeader.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
    	TABLE.fixHeader('table');
	});
  </script>
  <title>Role list</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Auth?action=roleList">Role list</a>
        </li>
        <li class="action">
          <a href="Auth">
            <img src="img/action/generic.png" alt="Generic" title="Generic" style="vertical-align: middle;"/>
            User list
          </a>
        </li>
      </ul>
      <br/>
      <table class="results" width="40%">
        <thead>
          <tr>
            <th>#</th><th>Id</th><th width="25px">Active</th>
            <th width="50px">
              <c:url value="Auth" var="urlCreate">
                <c:param name="action" value="roleCreate"/>
              </c:url>
              <c:url value="Auth" var="urlExport">
                <c:param name="action" value="roleListExport"/>
              </c:url>
              <c:if test="${db}">
                <a href="${urlCreate}"><img src="img/action/new.png" alt="New role" title="New role"/></a>
                &nbsp;
              </c:if>
              <a href="${urlExport}"><img src="img/action/export_csv.png" alt="CSV export" title="CSV export"/></a>
            </th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="role" items="${roles}" varStatus="row">
            <c:url value="Auth" var="urlEdit">
              <c:param name="action" value="roleEdit"/>
              <c:param name="rol_id" value="${role.id}"/>
            </c:url>
            <c:url value="Auth" var="urlDelete">
              <c:param name="action" value="roleDelete"/>
              <c:param name="rol_id" value="${role.id}"/>
            </c:url>
            <c:url value="Auth" var="urlActive">
              <c:param name="action" value="roleActive"/>
              <c:param name="rol_id" value="${role.id}"/>
              <c:param name="rol_active" value="${!role.active}"/>
            </c:url>
            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
              <td width="20px">${row.index + 1}</td><td>${role.id}</td>
              <td align="center">
                <c:choose>
                  <c:when test="${db}">
                    <c:choose>
                      <c:when test="${role.active}">
                        <a href="${urlActive}"><img src="img/true.png" alt="Active" title="Active"/></a>
                      </c:when>
                      <c:otherwise>
                        <a href="${urlActive}"><img src="img/false.png" alt="Inactive" title="Inactive"/></a>
                      </c:otherwise>
                    </c:choose>
                  </c:when>
                  <c:otherwise>
                    <img src="img/true.png" alt="Active" title="Active"/>
                  </c:otherwise>
                </c:choose>
              </td>
              <td align="center">
                <c:if test="${db}">
                  <a href="${urlEdit}"><img src="img/action/edit.png" alt="Edit" title="Edit"/></a>
                  &nbsp;
                  <a href="${urlDelete}"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a>
                </c:if>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>