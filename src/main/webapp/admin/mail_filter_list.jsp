<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="../css/dataTables-1.10.10/jquery.dataTables-1.10.10.min.css" />
  <link rel="stylesheet" type="text/css" href="css/admin-style.css" />
  <script type="text/javascript" src="../js/utils.js"></script>
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="../js/jquery.dataTables-1.10.10.min.js"></script>
  <script type="text/javascript">
    $(document).ready(function () {
      $('#results').dataTable({
        "bStateSave": true,
        "iDisplayLength": 10,
        "lengthMenu": [[10, 15, 20], [10, 15, 20]],
        "fnDrawCallback": function (oSettings) {
          dataTableAddRows(this, oSettings);
        }
      });
    });
  </script>
  <title>Mail filters</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url value="MailAccount" var="urlMailAccountList">
        <c:param name="ma_user" value="${ma_user}"/>
      </c:url>
      <ul id="breadcrumb">
        <li class="path">
          <a href="Auth">User list</a>
        </li>
        <li class="path">
          <a href="${urlMailAccountList}">Mail accounts</a>
        </li>
        <li class="path">Mail filters</li>
      </ul>
      <br/>
      <div style="width: 70%; margin-left: auto; margin-right: auto;">
        <table id="results" class="results">
          <thead>
            <tr>
              <th>Folder</th>
              <th>Grouping</th>
              <th>Active</th>
              <th width="75px">
                <c:url value="MailAccount" var="urlCreate">
                  <c:param name="action" value="filterCreate" />
                  <c:param name="ma_user" value="${ma_user}" />
                  <c:param name="ma_id" value="${ma_id}" />
                </c:url> 
                <a href="${urlCreate}"><img src="img/action/new.png" alt="New filter" title="New filter" /></a>
              </th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="mf" items="${mailFilters}" varStatus="row">
              <c:url value="MailAccount" var="urlEdit">
                <c:param name="action" value="filterEdit" />
                <c:param name="ma_user" value="${ma_user}" />
                <c:param name="ma_id" value="${ma_id}" />
                <c:param name="mf_id" value="${mf.id}" />
              </c:url>
              <c:url value="MailAccount" var="urlDelete">
                <c:param name="action" value="filterDelete" />
                <c:param name="ma_user" value="${ma_user}" />
                <c:param name="ma_id" value="${ma_id}" />
                <c:param name="mf_id" value="${mf.id}" />
              </c:url>
              <c:url value="MailAccount" var="urlRule">
                <c:param name="action" value="ruleList" />
                <c:param name="ma_user" value="${ma_user}" />
                <c:param name="ma_id" value="${ma_id}" />
                <c:param name="mf_id" value="${mf.id}" />
              </c:url>
              <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
                <td>${mf.path}</td>
                <td align="center"><c:choose>
                    <c:when test="${mf.grouping}">
                      <img src="img/true.png" alt="Active" title="Active" />
                    </c:when>
                    <c:otherwise>
                      <img src="img/false.png" alt="Inactive" title="Inactive" />
                    </c:otherwise>
                  </c:choose></td>
                <td align="center"><c:choose>
                    <c:when test="${mf.active}">
                      <img src="img/true.png" alt="Active" title="Active" />
                    </c:when>
                    <c:otherwise>
                      <img src="img/false.png" alt="Inactive" title="Inactive" />
                    </c:otherwise>
                  </c:choose></td>
                <td>
                  <a href="${urlEdit}"><img src="img/action/edit.png" alt="Edit" title="Edit" /></a> 
                  &nbsp; 
                  <a href="${urlDelete}"><img src="img/action/delete.png" alt="Delete" title="Delete" /></a> 
                  &nbsp; 
                  <a href="${urlRule}"><img src="img/action/rule.png" alt="Rules" title="Rules" /></a>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>